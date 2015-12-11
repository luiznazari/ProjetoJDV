package br.edu.unoesc.modelo;

import br.edu.unoesc.logica.Partida;
import br.edu.unoesc.rede.dados.ConjuntosDados;
import br.edu.unoesc.rede.dados.NormalizadorDados;
import br.edu.unoesc.rede.modelo.JdvRedeAbstrata;
import br.edu.unoesc.rede.modelo.MultilayerPerceptron3;
import br.edu.unoesc.util.JdvUtils;

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
		boolean vitoriaOuEmpate;

		if (partida.getVencedor() != null) {
			vitoriaOuEmpate = partida.getVencedor() == this;

			NormalizadorDados normalizador = new NormalizadorDados(partida, this.rede);

			dados.adicionarDadoESTemporario(normalizador.criaParesJogadasVencedoras());
			dados.adicionarDadoESTemporario(normalizador.criaParJogadaBloqueio());

		} else {
			vitoriaOuEmpate = true;

			// TODO empate
		}

		if (deveTreinar)
			aprenderJogadas(vitoriaOuEmpate);
	}

	private void aprenderJogadas(boolean redeVenceu) {
		rede.treinar(dados, redeVenceu);

		// FIXME Temporários
		salvaEstado();
		// ----------------
	}

	public void salvaEstado() {
//		versaoArquivo++;
//		if (versaoArquivo % 100 == 0) {
//		JdvUtils.Arquivo.incrementaVersao();
		JdvUtils.Arquivo.salvarRede(rede);
		JdvUtils.Arquivo.salvarDados(rede, dados);
//		}
	}

}
