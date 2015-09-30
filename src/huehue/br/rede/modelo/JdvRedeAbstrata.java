package huehue.br.rede.modelo;

import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.util.JdvUtils;

import java.time.Duration;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.util.simple.EncogUtility;

@Getter
public abstract class JdvRedeAbstrata implements JdvRede {
	
//	private static final String REDE_FEEDFORWARD_TANH = "?:B->TANH->5:B->TANH->?";
	
	private static final String REDE_FEEDFORWARD_SIGMOID = "?:B->SIGMOID->5:B->SIGMOID->?";
	
//	private static final String REDE_FEEDFORWARD_SEM_BIAS = "?->SIGMOID->5->SIGMOID->?";
	
	protected Double margemDeErro = 0.1D; // 10.0%
	
	protected Integer numeroEntradas;
	
	protected Integer numeroSaidas;
	
	protected String tipoTreinamento = MLTrainFactory.TYPE_BACKPROP;
	
	protected String tipoRede = MLMethodFactory.TYPE_FEEDFORWARD;
	
	@Setter
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
	 * Traduz o valor de entrada da rede ao conjunto de dados reconhecidos pela mesma (i.e.
	 * normaliza os valores e adapta os números correspondendo ao número de neurônios na camada de
	 * entrada).<br>
	 * Por padrão, retorna um conjunto de dados representando as entradas recebidas sem tratamentos.
	 * 
	 * @param entradas
	 *            recebidas pela rede.
	 * @return conjunto de dados representando a entrada da rede.
	 */
	public MLData traduzirEntrada(double[] entradas) {
		return new BasicMLData(entradas);
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
	 * Traduz uma posição do tabuleiro em um conjuntos de dados reconhecidos pela RNA. É o processo
	 * inverso do método {@link JdvRedeAbstrata#traduzirSaida(MLData)}.
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
		MLData dadosEntrada = traduzirEntrada(entradas);
		MLData dadosSaida = rede.compute(dadosEntrada);
		
		return traduzirSaida(dadosSaida);
	}
	
	public void treinar(ConjuntosDados dados) {
		// TODO verificar forma de melhorar a chamada do método 'embaralhar'.
		MLDataSet setDados = new BasicMLDataSet(dados.embaralhar());
		
		MLTrain train = new Backpropagation(getRede(), setDados, 0.1, 0.4);
		
		LocalDateTime inicio = LocalDateTime.now();
		
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
