package huehue.br.delete.me;

import huehue.br.modelo.Caractere;
import huehue.br.modelo.JogadorAleatorio;
import huehue.br.modelo.JogadorAutomato;
import huehue.br.modelo.JogadorMiniMax;
import huehue.br.modelo.JogadorRNA;
import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.rede.modelo.JdvRedeAbstrata;
import huehue.br.rede.modelo.MultilayerPerceptron3;
import huehue.br.tela.TelaExibicao;
import huehue.br.util.JdvUtils.Arquivo;
import huehue.br.util.JdvUtils.Tabuleiro;

import org.encog.Encog;

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
		JogadorRNA um = new JogadorRNA(Caractere.X, new MultilayerPerceptron3(nomeRede), false);
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