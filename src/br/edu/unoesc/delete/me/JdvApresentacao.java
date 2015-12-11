package br.edu.unoesc.delete.me;

import org.encog.Encog;

import br.edu.unoesc.modelo.Caractere;
import br.edu.unoesc.modelo.JogadorAleatorio;
import br.edu.unoesc.modelo.JogadorAutomato;
import br.edu.unoesc.modelo.JogadorMiniMax;
import br.edu.unoesc.modelo.JogadorRNA;
import br.edu.unoesc.rede.dados.ConjuntosDados;
import br.edu.unoesc.rede.modelo.JdvRedeAbstrata;
import br.edu.unoesc.rede.modelo.MultilayerPerceptron3;
import br.edu.unoesc.tela.TelaExibicao;
import br.edu.unoesc.util.JdvUtils.Arquivo;
import br.edu.unoesc.util.JdvUtils.Tabuleiro;

@SuppressWarnings("unused")
public class JdvApresentacao {

	private static final String nomeRede = "apresentacao";
	private static final String nomeRedeNova = "apresentacao_novo";
	private static final int versao = 501;

	public static void main(String[] args) {
		Arquivo.versionamento(versao);

		imprime_ES();
//		simulacaoAleatorio(100000);
//		simulacaoMinimax(1000);

		Encog.getInstance().shutdown();
	}

	public static void simulacaoAleatorio(int partidas) {
		JogadorRNA um = new JogadorRNA(Caractere.X, new MultilayerPerceptron3(nomeRedeNova), false);
		JogadorAutomato dois = new JogadorAleatorio(Caractere.O);

		Arquivo.incrementaVersao();
		Tabuleiro.comparaJogadores(um, dois, partidas);
	}

	public static void simulacaoMinimax(int partidas) {
		JogadorRNA um = new JogadorRNA(Caractere.X, new MultilayerPerceptron3(nomeRede), false);
		JogadorAutomato dois = new JogadorMiniMax(Caractere.O);

		Arquivo.incrementaVersao();
		Tabuleiro.comparaJogadores(um, dois, partidas);
	}

	public static void imprime_ES() {
		JdvRedeAbstrata rede = new MultilayerPerceptron3();

		ConjuntosDados dados1 = Arquivo.carregarDados(nomeRedeNova, rede.getNumeroEntradas(), rede.getNumeroSaidas());
		new TelaExibicao(dados1.getConjuntosMLSet(), rede);
	}
}