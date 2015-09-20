package huehue.br;

import huehue.br.util.JdvUtils;

import java.io.File;

import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.MLResettable;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.RequiredImprovementStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.manhattan.ManhattanPropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.simple.EncogUtility;

/**
 * Função de ativação Sigmoid resulta em valores entre 0 e 1.
 * Função de ativação Tanh resulta em valores entre -1 e 1.
 * 
 * @author Luiz
 */
public class MultilayerPerceptron {
	
	public static final Integer NEURONIOS_CAMADA_ENTRADA = 9;
	
	public static final Integer NEURONIOS_CAMADA_OCULTA = 9;
	
	public static final Integer NEURONIOS_CAMADA_SAIDA = 1;
	
	public static final Double MARGEM_DE_ERRO = 0.001D; // 0.1%
	
	public static final String treinamento = MLTrainFactory.TYPE_BACKPROP;
	
//	private static final String REDE_FEEDFORWARD_TANH = "?:B->TANH->" + NEURONIOS_CAMADA_OCULTA + ":B->TANH->?";
	
	private static final String REDE_FEEDFORWARD_TANH = "?:B->TANH->" + NEURONIOS_CAMADA_OCULTA + ":B->TANH->" + NEURONIOS_CAMADA_OCULTA + ":B->TANH->" + NEURONIOS_CAMADA_OCULTA + ":B->TANH->?";
	
//	private static final String REDE_FEEDFORWARD_SIGMOID = "?:B->SIGMOID->" + NEURONIOS_CAMADA_OCULTA + ":B->SIGMOID->?";
	
//	private static final String REDE_FEEDFORWARD_SIGMOID = "?:B->SIGMOID->" + NEURONIOS_CAMADA_OCULTA + ":B->SIGMOID->" + NEURONIOS_CAMADA_OCULTA + ":B->SIGMOID->" + NEURONIOS_CAMADA_OCULTA + ":B->SIGMOID->?";
	
	private static final String REDE_FEEDFORWARD_SIGMOID = "?:B->SIGMOID->"
	        + 18 + ":B->SIGMOID->?";
	
	@SuppressWarnings("unused")
//	private static final String REDE_FEEDFORWARD_SEM_BIAS = "?->SIGMOID->" + NEURONIOS_CAMADA_OCULTA + "->SIGMOID->?";
	/**
	 * Entradas dos casos de testes do tabuleiro do jogo da velha.
	 * 
	 * <pre>
	 * 1  = Jogador X
	 * -1 = Jogador O
	 * 0  = posição vazia
	 * </pre>
	 */
	private static double TEST_INPUT[][] = {
	    {
	        0, 0, 0,
	        0, -1, 0,
	        0, 0, 0
	    }, {
	        0, 0, 0,
	        0, -1, 0,
	        1, 0, -1
	    }, {
	        1, 0, 0,
	        0, -1, 0,
	        1, 0, -1
	    }, {
	        1, 0, 0,
	        -1, -1, 0,
	        1, 0, -1
	    }, {
	        1, 0, 1,
	        0, -1, 0,
	        0, -1, 0
	    }, { // ---
	        0, 0, 0,
	        0, 0, 0,
	        0, 0, 0
	    }
	    , {
	        1, 0, 0,
	        0, 0, 0,
	        0, -1, 0
	    }
	    , {
	        1, 0, 0,
	        -1, 0, 1,
	        0, -1, 0
	    }
	    , {
	        1, 0, 1,
	        -1, 0, 1,
	        -1, -1, 0
	    }
	    , {
	        1, -1, 1,
	        -1, 1, 1,
	        -1, -1, 0
	    }
	};
	
	/**
	 * Saídas dos casos de testes do tabuleiro do jogo da velha representados por valores menores do
	 * que zero para
	 * contemplar a abrangência do resultado da função de ativação utilizada na camada de saída da
	 * Rede Neuronal. <br>
	 * A saída corresponde à posição do tabuleiro, começando do 1.
	 * 
	 * <pre>
	 *  1 | 2 | 3
	 * ---+---+---
	 *  4 | 5 | 6
	 * ---+---+---
	 *  7 | 8 | 9
	 * </pre>
	 */
	private static double TEST_IDEAL[][] = {
	    {
		    0.5
	    }, {
		    0.1
	    }, {
		    0.4
	    }, {
		    0.6
	    }, {
		    0.2
	    }, { // --
		    0.8
	    }, {
		    0.4
	    }, {
		    0.7
	    }, {
		    0.2
	    }, {
		    0.9
	    }
	};
	
	public static BasicNetwork getNetwotk() {
		return ( BasicNetwork ) EncogDirectoryPersistence.loadObject(new File(
		        JdvUtils.Arquivo.DIR_RECURSOS + "hue_14"));
//		MLMethodFactory methodFactory = new MLMethodFactory();
//		return ( BasicNetwork ) methodFactory.create(MLMethodFactory.TYPE_FEEDFORWARD,
//		    REDE_FEEDFORWARD_SIGMOID,
//		    NEURONIOS_CAMADA_ENTRADA, NEURONIOS_CAMADA_SAIDA);
	}
	
	public static void salvaNetwork(Object object) {
		salvaNetwork(object, "hue2");
	}
	
	public static void salvaNetwork(Object object, String nomeArquivo) {
		EncogDirectoryPersistence.saveObject(new File(JdvUtils.Arquivo.DIR_RECURSOS + nomeArquivo),
		    object);
	}
	
	public static void main(String[] args) {
//		process(MLMethodFactory.TYPE_FEEDFORWARD, XORFactory.METHOD_FEEDFORWARD_A, MLTrainFactory.TYPE_BACKPROP, "", 1);
		
		// first, create the machine learning method
		MLMethodFactory methodFactory = new MLMethodFactory();
		MLMethod method = methodFactory.create(MLMethodFactory.TYPE_FEEDFORWARD,
		    REDE_FEEDFORWARD_SIGMOID,
		    NEURONIOS_CAMADA_ENTRADA, NEURONIOS_CAMADA_SAIDA);
		
//		MLMethod method = ( MLMethod ) EncogDirectoryPersistence.loadObject(new File(
//		        VeiaUtil.Arquivo.DIR_RECURSOS + "hue"));
//		
//		 second, create the data set
//		MLDataSet dataSet = new BasicMLDataSet(TEST_INPUT, TEST_IDEAL);
//		MLDataSet dataSet = new DadosFelizes().carregarDoArquivo().getMLDataSet();
		MLDataSet dataSet = EncogUtility.loadCSV2Memory(
		    JdvUtils.Arquivo.DIR_RECURSOS + "20_ConjuntosES.eg",
		    MultilayerPerceptron.NEURONIOS_CAMADA_ENTRADA,
		    MultilayerPerceptron.NEURONIOS_CAMADA_SAIDA, false, JdvUtils.Arquivo.FORMATO, false);
		
		// third, create the trainer
		MLTrainFactory trainFactory = new MLTrainFactory();
		MLTrain train = trainFactory.create(method, dataSet, MLTrainFactory.TYPE_BACKPROP, "");
		// reset if improve is less than 1% over 5 cycles
		if (method instanceof MLResettable && !(train instanceof ManhattanPropagation)) {
			train.addStrategy(new RequiredImprovementStrategy(500));
		}
		
		// fourth, train and evaluate.
		EncogUtility.trainToError(train, MARGEM_DE_ERRO);
		method = train.getMethod();
		
//		salvaNetwork(method);
//		EncogUtility.saveCSV(new File("hue"), CSVFormat.EG_FORMAT, dataSet);
//		EncogDirectoryPersistence.saveObject(new File("hue"), method);
		
		MLDataSet dataSet2 = new BasicMLDataSet(new double[][] {
			{
			    1, 0, 1,
			    0, -1, 0,
			    0, -1, 0
			}
		}, new double[][] {
			{
				0.2
			}
		});
		
		evaluate(( MLRegression ) method, dataSet.get(( int ) (Math.random() * dataSet.size())));
		
		// finally, write out what we did
//		System.out.println("Machine Learning Type: " + methodName);
//		System.out.println("Machine Learning Architecture: " + methodArchitecture);
//		
//		System.out.println("Training Method: " + trainerName);
//		System.out.println("Training Args: " + trainerArgs);
		
	}
	
	public static void evaluate(final MLRegression network, final MLDataSet training) {
		for (final MLDataPair pair : training) {
			evaluate(network, pair);
		}
	}
	
	public static void evaluate(final MLRegression network, final MLDataPair pair) {
		final MLData output = network.compute(pair.getInput());
		System.out.println("Input="
		        + EncogUtility.formatNeuralData(pair.getInput())
		        + ", Actual=" + EncogUtility.formatNeuralData(output)
		        + ", Ideal="
		        + EncogUtility.formatNeuralData(pair.getIdeal())
		        + ", IdealHue="
		        + JdvUtils.RNA.traduzSaida(Double.valueOf(EncogUtility.formatNeuralData(output)
		                .replace(",", "."))));
	}
	
}
