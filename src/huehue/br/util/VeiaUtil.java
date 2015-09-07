package huehue.br.util;

import huehue.br.MultilayerPerceptron;
import huehue.br.exception.VeiaException;
import huehue.br.modelo.Caractere;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;

/**
 * Classe de utilitários para auxiliar na manipulação de dados e lógicas referentes ao Jogo da
 * Velha.
 * 
 * @author Luiz Felipe Nazari
 */
public class VeiaUtil {
	
	/**
	 * Métodos úteis para auxiliar no controle das lógicas do jogo.
	 */
	public static class Tabuleiro {
		
		/**
		 * Avalia se houve um vencedor em dado momento no jogo. Caso retornar zero, referente ao
		 * {@link Caractere#VAZIO}, não houve vencedor.
		 * 
		 * @param t
		 *            a array de valores correspondentes ao tabuleiro.
		 * @return o valor correspondente ao {@link Caractere} vencedor.
		 */
		// @formatter:off
		public static double computaVencedor(double[] t) {
			int vazio = Caractere.VAZIO.getValor();

			/**
			 * Índices do tabuleiro para comparar posições vencedoras do jogo da velha.
			 * Esta matriz possui 4 arrays, cada array corresponde há array.length conbinações vencedoras.
			 * Cada array vencedora do tabuleiro é dada pelos índices: array[x], array[x] + y e array[x] + y * 2],
			 * onde y é o índice da array na matriz + 1.
			 * 
			 * Exemplo: array = m[1]
			 * 			[array[0], array[0] + (1 + 1), array[0] + (1 + 1) * 2]
			 *			[2, 	   2 + 2,			   2 + 4]
			 *			[2,		   4,				   6] -> índices do array do tabuleiro, formando a combinação vencedora.
			 */
			int[][] m = { {0, 3, 6}, {2}, {0, 1, 2}, {0} };
			
			for (int i = 1; i <= m.length; i++) {
				int[] n = m[i - 1];
				for (int j = 0; j < n.length; j++)
					if (t[n[j]] != vazio && t[n[j]] == t[n[j] + i] && t[n[j]] == t[n[j] + i * 2])
						return t[n[j]];
			}
			
			return vazio;
		}
		// @formatter:on
		
	}
	
	/**
	 * Métodos úteis para auxiliar a manipulação de Redes Neuronais.
	 */
	public static class RNA {
		
		/**
		 * Traduz o valor da saída resultante do processamento da RNA.
		 * 
		 * @param saida
		 *            a saída resultante do processamento da RNA.
		 * @return valor da saída traduzido, onde: 0 <= valor <= 8.
		 * @throws VeiaException
		 *             caso o número de saídas for diferente do
		 *             {@link MultilayerPerceptron#NEURONIOS_CAMADA_SAIDA}.
		 * @see {@link VeiaUtil.RNA#traduzSaida(double)}
		 */
		public static int traduzSaida(MLData saida) {
			int numSaidas = saida == null ? 0 : saida.getData().length;
			
			if (numSaidas != MultilayerPerceptron.NEURONIOS_CAMADA_SAIDA)
				throw new VeiaException(
				        "MLData resultante do processamento da rede não possui o número esperado de saídas! Esperado: " + MultilayerPerceptron.NEURONIOS_CAMADA_SAIDA + ". Obtido: " + numSaidas);
			
			return traduzSaida(saida.getData(0)) - 1;
		}
		
		/**
		 * Traduz o valor da saída resultante do processamento da RNA.
		 * 
		 * <pre>
		 * Exemplos:
		 * 
		 * 0.04325	-> 0.0	-> 1
		 * 0.9831	-> 1.0	-> 9
		 * 0.9287	-> 0.9	-> 9
		 * 0.296	-> 0.3	-> 3
		 * 0.4328	-> 0.4	-> 4
		 * 0.5007	-> 0.5	-> 5
		 * 0.5984	-> 0.6	-> 6
		 * </pre>
		 * 
		 * @param d
		 *            a saída resultante do processamento da RNA.
		 * @return valor da saída traduzido, onde: 1 <= valor <= 9.
		 */
		public static int traduzSaida(double saida) {
			double d = valorAproximado(saida);
			
			if (d >= 1)
				return 9;
			if (d <= 0)
				return 1;
			return ( int ) (d * 10);
		}
		
		/**
		 * Arredonda o valor especificado. O arredondamento sempre é realizado para cima.
		 * 
		 * @param d
		 *            o valor.
		 * @return valor arredondado, onde: 0 <= valor <= 1.
		 */
		public static double valorAproximado(double d) {
			BigDecimal b = new BigDecimal(d);
			b = b.setScale(1, RoundingMode.HALF_UP);
			return b.doubleValue();
		}
		
	}
	
	/**
	 * Métodos úteis para auxiliar a manipulação de arquivos contendo dados sobre estruturas das
	 * Redes Neuronais e conjunto de dados de entrada e saída.
	 */
	public static class Arquivo {
		
		public static final CSVFormat FORMATO = CSVFormat.EG_FORMAT;
		
		public static final String DIR_RECURSOS = "resources/";
		
		public static final String DIR_CONJUNTO_DADOS_ES = DIR_RECURSOS + "ConjuntosES.eg";
		
		/**
		 * Carrega o arquivo contendo os conjuntos de entradas e saídas.
		 * 
		 * @return
		 */
		private static File carregaArquivoES() throws VeiaException {
			File arquivo = new File(DIR_CONJUNTO_DADOS_ES);
			return arquivo;
		}
		
		public static void salvarDados(List<MLDataPair> conjuntosES) {
			if (conjuntosES == null || conjuntosES.size() == 0)
				throw new VeiaException("Erro ao salvar. Conjunto de dados nulo ou vazio!");
			
			EncogUtility.saveCSV(carregaArquivoES(), FORMATO, new BasicMLDataSet(conjuntosES));
		}
		
		public static MLDataSet carregarDados() {
			MLDataSet set = EncogUtility.loadCSV2Memory(DIR_CONJUNTO_DADOS_ES,
			    MultilayerPerceptron.NEURONIOS_CAMADA_ENTRADA,
			    MultilayerPerceptron.NEURONIOS_CAMADA_SAIDA, false, FORMATO, false);
			
			return set;
		}
		
	}
	
}
