package huehue.br.util;

import huehue.br.modelo.Caractere;
import huehue.br.modelo.JogadorAutomato;
import huehue.br.modelo.JogadorMiniMax;
import huehue.br.modelo.JogadorRNA;
import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.rede.modelo.JdvRedeAbstrata;
import huehue.br.rede.modelo.MultilayerPerceptron;
import huehue.br.rede.modelo.MultilayerPerceptron2;
import huehue.br.rede.modelo.MultilayerPerceptron3;
import huehue.br.tela.TelaExibicao;
import huehue.br.util.JdvUtils.Arquivo;
import huehue.br.util.JdvUtils.Tabuleiro;

import org.encog.Encog;

public class JdvMain {

	public static void main(String[] args) {
//		imprime_ES();
		simulaJogadas();
//		JdvUtils.RNA.converteArquivosDeDadosEntreRedes(new MultilayerPerceptron2(), new MultilayerPerceptron3());
	}

	// @formatter:off
	public static void simulaJogadas() {
		Arquivo.versionamento(0);

//		JogadorAutomato um = new JogadorMiniMax(Caractere.X);
//		JogadorAutomato um = new JogadorAleatorio(Caractere.X);
//		JogadorRNA um = new JogadorRNA(Caractere.X, false);
		JogadorRNA um = new JogadorRNA(Caractere.X, new MultilayerPerceptron3("delete_me"), false);

		JogadorAutomato dois = new JogadorMiniMax(Caractere.O);
//		JogadorAutomato dois = new JogadorAleatorio(Caractere.O);
//		JogadorRNA dois = new JogadorRNA(Caractere.O, false);
//		JogadorRNA dois = new JogadorRNA(Caractere.O, new MulilayerPerceptron3("O"), true);
		
//		Arquivo.incrementaVersao();
		Tabuleiro.comparaJogadores(um, dois, 1000);
		
		Encog.getInstance().shutdown();
	}
	// @formatter:on

	public static void imprime_ES() {
		JdvRedeAbstrata rede = new MultilayerPerceptron3();
		Arquivo.versionamento(1);

		String a = "delete_me";

		ConjuntosDados dados1 = Arquivo.carregarDados(a, rede.getNumeroEntradas(), rede.getNumeroSaidas());
		new TelaExibicao(dados1.getConjuntosMLSet(), rede);

//		ConjuntosDados dados2 = Arquivo.carregarDados(a + "2", rede.getNumeroEntradas(), rede.getNumeroSaidas());
//		new TelaExibicao(dados2.getConjuntosSet(), rede);
//		
//		RNA.converteArquivosDeDadosEntreRedes(new MultilayerPerceptron2(), new MultilayerPerceptron3());
	}

	public static void delete_me() {
		double[] tabuleiro = new double[] {
			1, 0, 1, 0, 0, 0, -1, 0, -1, 0
		};

		int saida = 4;

		JdvRedeAbstrata rede;

		System.out.println("\nMultilayerPerceptron");
		rede = new MultilayerPerceptron();
		System.out.println(rede.traduzirEntrada(tabuleiro));
		System.out.println(rede.convertePosicaoTabuleiroEmSaida(saida));

		System.out.println("\nMultilayerPerceptron2");
		rede = new MultilayerPerceptron2();
		System.out.println(rede.traduzirEntrada(tabuleiro));
		System.out.println(rede.convertePosicaoTabuleiroEmSaida(saida));

		System.out.println("\nMultilayerPerceptron3");
		rede = new MultilayerPerceptron3();
		System.out.println(rede.traduzirEntrada(tabuleiro));
		System.out.println(rede.convertePosicaoTabuleiroEmSaida(saida));
	}
}
