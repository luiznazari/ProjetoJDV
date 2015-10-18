package huehue.br.modelo;

import huehue.br.logica.Partida;
import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.rede.modelo.JdvRedeAbstrata;
import huehue.br.rede.modelo.MultilayerPerceptron2;
import huehue.br.util.JdvUtils;
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
		
		rede = new MultilayerPerceptron2().inicializar();
		dados = new ConjuntosDados(JdvUtils.Arquivo.carregarDados(rede));
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
		partida.getJogadasVencedor().forEach(
				p -> dados.adicionarDadoES(
						rede.traduzirEntrada(super.validaEntradas(p.getConfiguracao())),
						rede.convertePosicaoTabuleiroEmSaida(p.getPosicaoEscolhida())));
		
		if (deveTreinar)
			aprenderJogadas();
	}
	
	private void aprenderJogadas() {
		
		rede.treinar(dados);
		
		// FIXME Temporário
		int v = JdvUtils.Arquivo.incrementaVersao();
		
//		if (v % 10 == 0) {
		JdvUtils.Arquivo.salvarRede(rede);
		JdvUtils.Arquivo.salvarDados(rede, dados.getConjuntos());
//		}
		// ----------------
	}
	
}
