package huehue.br.rede.dados;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import huehue.br.logica.Partida;
import huehue.br.modelo.Caractere;
import huehue.br.modelo.Jogador;
import huehue.br.modelo.JogadorAleatorio;
import huehue.br.modelo.JogadorAutomato;
import huehue.br.rede.modelo.JdvRedeAbstrata;
import huehue.br.rede.modelo.MultilayerPerceptron3;
import huehue.br.util.JdvLog;

import java.util.List;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.junit.Before;
import org.junit.Test;

public class NormalizadorDadosTest {

	private JogadorAutomato um;

	private Jogador dois;

	private Partida partida;

	private JdvRedeAbstrata rede;

	@Before
	public void init() {
		JdvLog.ativo = false;

		rede = new MultilayerPerceptron3();

		um = new JogadorAleatorio(Caractere.X);
		dois = new JogadorAleatorio(Caractere.O);

		partida = new Partida();

		double[][] jogada1_O = new double[][] {
			{ 0, 0, 0,
				0, 0, 0,
				0, 0, 0 },
			{ 0 }
		};

		double[][] jogada1_X = new double[][] {
			{ -1, 0, 0,
				0, 0, 0,
				0, 0, 0 },
			{ 3 }
		};

		double[][] jogada2_O = new double[][] {
			{ -1, 0, 0,
				1, 0, 0,
				0, 0, 0 },
			{ 4 }
		};

		double[][] jogada2_X = new double[][] {
			{ -1, 0, 0,
				1, -1, 0,
				0, 0, 0 },
			{ 5 }
		};

		double[][] jogada3_O = new double[][] {
			{ -1, 0, 0,
				1, -1, 1,
				0, 0, 0 },
			{ 8 }
		};

		/**
		 * <pre>
		 *  O |   |   
		 * -----------
		 *  X | O | X 
		 * -----------
		 *    |   | O
		 * </pre>
		 */
		// Jogador O ganhou.

		partida.encerrar(null); // Jogador O começará.

		partida.novaJogada(dois.getCaractere(), jogada1_O[0], ( int ) jogada1_O[1][0]);

		partida.novaJogada(um.getCaractere(), jogada1_X[0], ( int ) jogada1_X[1][0]);

		partida.novaJogada(dois.getCaractere(), jogada2_O[0], ( int ) jogada2_O[1][0]);

		partida.novaJogada(um.getCaractere(), jogada2_X[0], ( int ) jogada2_X[1][0]);

		partida.novaJogada(dois.getCaractere(), jogada3_O[0], ( int ) jogada3_O[1][0]);

		partida.encerrar(dois);
	}

	@Test
	public void deveRetornarPartidasDaVitoria() {
		NormalizadorDados normalizador = new NormalizadorDados(partida, rede);

		List<JdvMLDataPair> paresVitoria = normalizador.criaParesJogadasVencedoras();

		assertEquals(3, paresVitoria.size());
		paresVitoria.forEach(p -> assertEquals(3, p.getPontos()));

		MLData par2Entradas = new BasicMLData(new double[] {
			/* X */0.9999, 0, 0, 0, 0, 0, 0, 0, 0,
			/* O */0, 0, 0, -0.9999, 0, 0, 0, 0, 0
		});

		MLData par2Saidas = new BasicMLData(new double[] {
			/* 8 */0, 0, 0, 0, 1, 0, 0, 0, 0
		});

		MLDataPair par2Teste = new BasicMLDataPair(par2Entradas, par2Saidas);

		assertTrue(paresVitoria.get(1).isEntradasIguais(par2Teste));
		assertTrue(paresVitoria.get(1).isSaidasIguais(par2Teste));

		assertArrayEquals(par2Teste.getInputArray(), paresVitoria.get(1).getPar().getInputArray(), 0);
		assertArrayEquals(par2Teste.getIdealArray(), paresVitoria.get(1).getPar().getIdealArray(), 0);
	}

	@Test
	public void deveRetornarPartidaComBloqueio() {
		NormalizadorDados normalizador = new NormalizadorDados(partida, rede);

		JdvMLDataPair parBloqueio = normalizador.criaParJogadaBloqueio();

		MLData parEntradas = new BasicMLData(new double[] {
			/* X */0, 0, 0, 0.9999, 0, 0, 0, 0, 0,
			/* O */-0.9999, 0, 0, 0, -0.9999, 0, 0, 0, 0
		});

		MLData parSaidas = new BasicMLData(new double[] {
			/* 8 */0, 0, 0, 0, 0, 0, 0, 0, 1
		});

		MLDataPair parTeste = new BasicMLDataPair(parEntradas, parSaidas);

		assertEquals(4, parBloqueio.getPontos());

		assertTrue(parBloqueio.isEntradasIguais(parTeste));
		assertTrue(parBloqueio.isSaidasIguais(parTeste));

		assertArrayEquals(parTeste.getInputArray(), parBloqueio.getPar().getInputArray(), 0);
		assertArrayEquals(parTeste.getIdealArray(), parBloqueio.getPar().getIdealArray(), 0);
	}
}
