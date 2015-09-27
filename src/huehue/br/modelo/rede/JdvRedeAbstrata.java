package huehue.br.modelo.rede;

import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.util.JdvUtils;

import java.time.Duration;
import java.time.LocalDateTime;

import lombok.Getter;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.util.simple.EncogUtility;

@Getter
public abstract class JdvRedeAbstrata implements JdvRede {
	
	@SuppressWarnings("unused")
	private static final String REDE_FEEDFORWARD_TANH = "?:B->TANH->18:B->TANH->?";
	
//	private static final String REDE_FEEDFORWARD_TANH = "?:B->TANH->" + NEURONIOS_CAMADA_OCULTA + ":B->TANH->" + NEURONIOS_CAMADA_OCULTA + ":B->TANH->" + NEURONIOS_CAMADA_OCULTA + ":B->TANH->?";
	
//	private static final String REDE_FEEDFORWARD_SIGMOID = "?:B->SIGMOID->" + NEURONIOS_CAMADA_OCULTA + ":B->SIGMOID->?";
	
//	private static final String REDE_FEEDFORWARD_SIGMOID = "?:B->SIGMOID->" + NEURONIOS_CAMADA_OCULTA + ":B->SIGMOID->" + NEURONIOS_CAMADA_OCULTA + ":B->SIGMOID->" + NEURONIOS_CAMADA_OCULTA + ":B->SIGMOID->?";
	
	private static final String REDE_FEEDFORWARD_SIGMOID = "?:B->SIGMOID->18:B->SIGMOID->?";
	
//	private static final String REDE_FEEDFORWARD_SEM_BIAS = "?->SIGMOID->" + NEURONIOS_CAMADA_OCULTA + "->SIGMOID->?";
	
	protected Double margemDeErro = 0.001; // 0.1%
	
	protected Integer numeroEntradas;
	
	protected Integer numeroSaidas;
	
	protected String tipoTreinamento = MLTrainFactory.TYPE_BACKPROP;
	
	protected String tipoRede = MLMethodFactory.TYPE_FEEDFORWARD;
	
	protected BasicNetwork rede;
	
	public JdvRedeAbstrata(Integer numeroEntradas, Integer numeroSaidas) {
		this.numeroEntradas = numeroEntradas;
		this.numeroSaidas = numeroSaidas;
		this.rede = JdvUtils.Arquivo.carregarRede(this);
	}
	
	@Override
	public String getEstruturaRede() {
		return REDE_FEEDFORWARD_SIGMOID;
	}
	
	/**
	 * Traduz o valor da saída resultante do processamento da RNA. É o processo inverso do método
	 * {@link JdvRedeAbstrata#traduzirPosicaoTabuleiro(int)}.
	 * 
	 * @param d
	 *            a saída resultante do processamento da RNA.
	 * @return valor da saída traduzido, onde: 0 <= valor <= 8.
	 */
	abstract public int traduzirSaida(MLData saida);
	
	/**
	 * Traduz o valor da saída resultante do processamento da RNA. É o processo inverso do método
	 * {@link JdvRedeAbstrata#traduzirSaida(MLData)}.
	 * 
	 * @param posica
	 *            a posição do tabuleiro.
	 * @return conjunto de dados representando a posicão.
	 */
	abstract public MLData traduzirPosicaoTabuleiro(int posicao);
	
	/**
	 * @param entradas
	 *            array de entradas representando o estado do tabuleiro.
	 * @return posicão escolhida pela rede;
	 */
	public int processar(double[] entradas) {
		MLData dadosEntrada = new BasicMLData(entradas);
		MLData dadosSaida = rede.compute(dadosEntrada);
		
		return traduzirSaida(dadosSaida);
	}
	
	public void treinar(ConjuntosDados dados) {
		MLTrainFactory trainFactory = new MLTrainFactory();
		MLTrain train = trainFactory.create(rede, dados.getMLDataSet(), getTipoTreinamento(), "");
		
		LocalDateTime inicio = LocalDateTime.now();
		System.out.println("Iniciando treinamento.");
		
		EncogUtility.trainToError(train, getMargemDeErro());
		
		System.out.println("Treinamento finalizado. Tempo total: " + Duration.between(inicio,
				LocalDateTime.now()));
	}
	
	// TODO
	public void processarNoConsole(final MLDataSet training) {
		for (final MLDataPair pair : training) {
			processarNoConsole(pair);
		}
	}
	
	// TODO
	public void processarNoConsole(final MLDataPair pair) {
		final MLData output = getRede().compute(pair.getInput());
		System.out.println("Input="
			+ EncogUtility.formatNeuralData(pair.getInput())
			+ ", Actual=" + EncogUtility.formatNeuralData(output)
			+ ", Ideal=" + EncogUtility.formatNeuralData(pair.getIdeal())
			+ ", IdealJDV=" + traduzirSaida(output));
	}
	
}
