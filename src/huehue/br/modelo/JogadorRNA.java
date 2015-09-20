package huehue.br.modelo;

import huehue.br.MultilayerPerceptron;
import huehue.br.dados.ConjuntosTreinamento;
import huehue.br.util.JdvUtils;

import org.encog.Encog;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;

/**
 * Classe representando um jogador com Inteligência Artificial utilizando uma
 * Rede Neural Artificial.
 * 
 * @author Luiz Felipe Nazari
 */
public class JogadorRNA extends JogadorAutomato {
	
	private BasicNetwork rede;
	
	// Conjuntos de dados conhecidos pela rede e utilizados no treinamento.
	private ConjuntosTreinamento dados;
	
	public JogadorRNA(Caractere caractere) {
		super(caractere);
		dados = new ConjuntosTreinamento();
		dados.carregarDoArquivo();
		rede = MultilayerPerceptron.getNetwotk();
	}
	
	@Override
	public int novaJogada(double[] entradas) {
		entradas = validaEntradasDaRede(entradas);
		MLData saida = rede.compute(converteArrayEmMlData(entradas));
		int posicaoEscolhida = JdvUtils.RNA.traduzSaida(saida);
		
		// Escolheu uma posição já ocupada.
		if (entradas[posicaoEscolhida] != Caractere.VAZIO.getValor()) {
			System.out
			        .println("A Rede computou uma posição inválida. Escolhendo novo movimento...");
			posicaoEscolhida = super.escolhePosicao(entradas);
		}
		
		return posicaoEscolhida;
	}
	
	@Override
	public void notificarResultado(Partida partida) {
		if (partida.getVencedor() == this && this.getCaractere().equals(Caractere.X)) {
			partida.getJogadasVencedor().forEach(
			    j -> dados.adicionarDadoES(j.getConfiguracao(), validaResultadoDaRede(j
			            .getPosicaoEscolhida())));
		} else {
			partida.getJogadasVencedor().forEach(
			    j -> dados.adicionarDadoES(validaEntradasDaRede(j.getConfiguracao()),
			        validaResultadoDaRede(j
			                .getPosicaoEscolhida())));
		}
		aprenderJogadas();
	}
	
	int i = 0;
	
	private void aprenderJogadas() {
//		rede = MultilayerPerceptron.getNetwotk(); // TEMP
//		
//		MLTrainFactory trainFactory = new MLTrainFactory();
//		MLTrain train = trainFactory.create(rede, dados.getMLDataSet(),
//		    MultilayerPerceptron.treinamento, "");
//		
//		// FIXME Temporário
//		dados.salvarEmArquivo();
//		// ----------------
//		
//		LocalDateTime inicio = LocalDateTime.now();
//		System.out.println("Iniciando treinamento.");
//		EncogUtility.trainToError(train, MultilayerPerceptron.MARGEM_DE_ERRO);
//		System.out.println("Treinamento finalizado. Tempo total: " + Duration.between(inicio,
//		    LocalDateTime.now()));
//		
//		// FIXME Temporário
//		MultilayerPerceptron.salvaNetwork(rede, "hue_" + i++);
//		// ----------------
	}
	
	/**
	 * Busca a configuração das posições do tabuleiro atual e constroi os dados
	 * a serem utilizados pelo Framework {@link Encog}.
	 * 
	 * @param entradas
	 *            array de entradas representando o estado do tabuleiro.
	 * @return os dados do tabuleiro atual.
	 */
	private MLData converteArrayEmMlData(double[] entradas) {
		return new BasicMLData(entradas);
	}
	
	/**
	 * Valida os valores das entradas a serem computadas pela Rede. Caso o {@link JogadorRNA} em
	 * questão possuir o {@link Caractere#O}, os valores das entradas precisarão ser ajustados,
	 * pois a rede é treinada para reconhecer o valor do {@link Caractere#X} como posição da Rede e
	 * o {@link Caractere#O} como o adversário.
	 * 
	 * @param entradas
	 * @return
	 */
	private double[] validaEntradasDaRede(double[] entradas) {
		if (getCaractere().equals(Caractere.O))
			for (int i = 0; i < entradas.length; i++)
				entradas[i] *= -1;
		
		return entradas;
	}
	
	private double validaResultadoDaRede(double resultado) {
		return (resultado + 1) / 10;
	}
}
