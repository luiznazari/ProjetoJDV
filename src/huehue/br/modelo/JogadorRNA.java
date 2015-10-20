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
		super(caractere);
		this.deveTreinar = deveTreinar;
		
		rede = new MultilayerPerceptron3().inicializar();
		dados = JdvUtils.Arquivo.carregarDados(rede);
		dados.setSubstituirRepetidos(true);
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
					j -> dados.adicionarDadoESTemporario(criaParJogada(j, jogadas.size())));
			
		} else {
			// TODO empate
		}
		
//		if (deveTreinar)
//			aprenderJogadas();
	}
	
	private void aprenderJogadas() {
		
		rede.treinar(dados);
		
		// FIXME Temporário
		int v = JdvUtils.Arquivo.incrementaVersao();
		
		if (v % 10 == 0) {
			JdvUtils.Arquivo.salvarRede(rede);
			JdvUtils.Arquivo.salvarDados(rede, dados);
		}
		// ----------------
	}
	
	private JdvMLDataPair criaParJogada(Jogada jogada, int passos) {
		double[] tabuleiro = super.validaEntradas(jogada.getConfiguracao());
		int[] indicesX = JdvUtils.Tabuleiro.computaIndicesVencedor(jogada.getConfiguracao());
		int pontos = 1;
		double delta = 1;
		
		switch (passos) {
			case 3: { // Ótimas
				pontos = 3;
				delta = 0.9;
				break;
			}
			case 4: { // Boas
				pontos = 2;
				delta = 0.6;
				break;
			}
			case 5: { // Razoáveis
				pontos = 1;
				delta = 0.4;
				break;
			}
		}
		
		for (int i = 0; i < tabuleiro.length; i++)
			tabuleiro[i] *= delta;
		
		for (int i = 0; i < indicesX.length; i++)
			if (indicesX[i] != jogada.getPosicaoEscolhida())
				tabuleiro[indicesX[i]] = 0.9;
		
		JdvMLDataPair par = new JdvMLDataPair(rede.traduzirEntrada(tabuleiro),
				rede.convertePosicaoTabuleiroEmSaida(jogada.getPosicaoEscolhida()));
		par.setPontos(pontos);
		return par;
	}
	
	public static void main(String[] args) {
		
	}
	
}
