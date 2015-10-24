package huehue.br.modelo;

import huehue.br.logica.Partida;
import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.rede.dados.NormalizadorDados;
import huehue.br.rede.modelo.JdvRedeAbstrata;
import huehue.br.rede.modelo.MultilayerPerceptron3;
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
			NormalizadorDados normalizador = new NormalizadorDados(partida, this.rede);

			dados.adicionarDadoESTemporario(normalizador.criaParesJogadasVencedoras());
			dados.adicionarDadoESTemporario(normalizador.criaParJogadaBloqueio());

			if (deveTreinar)
				aprenderJogadas(partida.getVencedor() == this);

		} else {
			// TODO empate
		}
	}

	private void aprenderJogadas(boolean redeVenceu) {
		rede.treinar(dados, redeVenceu);

		// FIXME Temporário
//		versaoArquivo++;
//		if (versaoArquivo % 100 == 0) {
//		JdvUtils.Arquivo.incrementaVersao();
		JdvUtils.Arquivo.salvarRede(rede);
		JdvUtils.Arquivo.salvarDados(rede, dados);
//		}
		// ----------------
	}

}
