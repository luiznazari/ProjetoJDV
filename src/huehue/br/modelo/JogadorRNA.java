package huehue.br.modelo;

import huehue.br.logica.Partida;
import huehue.br.logica.Partida.Jogada;
import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.rede.dados.JdvMLDataPair;
import huehue.br.rede.modelo.JdvRedeAbstrata;
import huehue.br.rede.modelo.MultilayerPerceptron3;
import huehue.br.util.JdvUtils;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe representando um jogador com Inteligência Artificial utilizando uma
 * Rede Neural Artificial.
 * 
 * @author Luiz Felipe Nazari
 */
public class JogadorRNA extends JogadorAutomato {
	
	private int versaoArquivo = 0;
	
	@Getter
	@Setter
	private boolean deveTreinar = false;
	
	private JdvRedeAbstrata rede;
	
	// Conjuntos de dados conhecidos pela rede e utilizados no treinamento.
	private ConjuntosDados dados;
	
	public JogadorRNA(Caractere caractere) {
		this(caractere, false);
	}
	
	public JogadorRNA(Caractere caractere, boolean deveTreinar) {
		this(caractere, new MultilayerPerceptron3(), deveTreinar);
	}
	
	public JogadorRNA(Caractere caractere, JdvRedeAbstrata rede, boolean deveTreinar) {
		super(caractere);
		this.deveTreinar = deveTreinar;
		
		this.rede = rede.inicializar();
		this.dados = JdvUtils.Arquivo.carregarDados(rede);
	}
	
	@Override
	public int novaJogada(double[] entradas) {
		entradas = super.validaEntradas(entradas);
		int posicaoEscolhida = rede.processar(entradas);
		
		// Escolheu uma posição já ocupada.
		if (entradas[posicaoEscolhida] != Caractere.VAZIO.getValor()) {
			System.err.println("A Rede computou uma posição inválida [" + posicaoEscolhida + "]."
					+ " Escolhendo novo movimento...");
			posicaoEscolhida = super.escolhePosicao(entradas);
		}
		
		return posicaoEscolhida;
	}
	
	@Override
	public void notificarResultado(Partida partida) {
		if (partida.getVencedor() != null) {
			List<Jogada> jogadas = partida.getJogadasVencedor();
			jogadas.forEach(
					j -> dados.adicionarDadoESTemporario(
							criaParJogada(j, jogadas.size(), partida.getVencedor().getCaractere() == Caractere.X)));
			
		} else {
			// TODO empate
		}
		
		dados.armazenarTemporarios();
		if (deveTreinar)
			aprenderJogadas();
	}
	
	private void aprenderJogadas() {
		
		rede.treinar(dados);
		
		// FIXME Temporário
		versaoArquivo++;
		if (versaoArquivo % 100 == 0) {
			JdvUtils.Arquivo.salvarRede(rede);
			JdvUtils.Arquivo.salvarDados(rede, dados);
		}
		// ----------------
	}
	
	private JdvMLDataPair criaParJogada(Jogada jogada, int passos, boolean jogadorX) {
		double[] tabuleiro = super.validaEntradas(jogada.getConfiguracao(), jogadorX);
		
		tabuleiro[jogada.getPosicaoEscolhida()] = Caractere.X.getValor();
		int[] indicesX = JdvUtils.Tabuleiro.computaIndicesVencedor(tabuleiro);
		tabuleiro[jogada.getPosicaoEscolhida()] = Caractere.VAZIO.getValor();
		
		int pontos = 1;
		double delta = 1;
		
		switch (passos) {
			case 3: { // Ótimo
				pontos = 3;
				delta = 0.9999;
				break;
			}
			case 4: { // Bom
				pontos = 2;
				delta = 0.8888;
				break;
			}
			case 5: { // Razoável
				pontos = 1;
				delta = 0.7777;
				break;
			}
		}
		
		for (int i = 0; i < tabuleiro.length; i++)
			tabuleiro[i] *= delta;
		
		for (int i = 0; i < indicesX.length; i++)
			if (indicesX[i] != jogada.getPosicaoEscolhida())
				tabuleiro[indicesX[i]] = 0.9999;
		
		JdvMLDataPair par = new JdvMLDataPair(rede.traduzirEntrada(tabuleiro),
				rede.convertePosicaoTabuleiroEmSaida(jogada.getPosicaoEscolhida()), 1, pontos);
		return par;
	}
	
	public static void main(String[] args) {
		
	}
	
}
