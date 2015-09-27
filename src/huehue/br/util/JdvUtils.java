package huehue.br.util;

import huehue.br.modelo.Caractere;
import huehue.br.rede.modelo.JdvRede;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;

/**
 * Classe de utilitários para auxiliar na manipulação de dados e lógicas referentes ao Jogo da
 * Velha.
 * 
 * @author Luiz Felipe Nazari
 */
public class JdvUtils {
	
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
			 * Esta matriz possui 4 arrays, para cada array corresponde há array.length conbinações vencedoras.
			 * Cada array vencedora do tabuleiro é dada pelos índices: array[x], array[x] + y e array[x] + y * 2],
			 * onde y é o índice da array na matriz + 1.
			 * 
			 * Exemplo: array = m[1]
			 * 			[array[0], array[0] + (1 + 1), array[0] + (1 + 1) * 2]
			 *			[2, 	   2 + 2,			   2 + 4]
			 *			[2,		   4,				   6] -> Índices do array do tabuleiro, formando a combinação vencedora.
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
		 * Converte um arquivo de dados (conjuntos de entradas e saídas) de uma rede à outra.<br>
		 * // TODO Aprimorar método, como está atualmente converte apenas de um tipo de rede com uma
		 * saída para outra de nove saídas.
		 * 
		 * @param redeBase
		 * @param redeObjetivo
		 */
		public static void converteArquivosDeDadosEntreRedes(JdvRede redeBase, JdvRede redeObjetivo) {
			MLDataSet set1 = Arquivo.carregarDados(redeBase);
			MLDataSet set2 = new BasicMLDataSet();
			
			for (MLDataPair pair : set1)
				set2.add(pair.getInput(), converteDadosUmParaNove(pair.getIdeal()));
			
			Arquivo.salvarDados(redeObjetivo, set2);
		}
		
		/**
		 * Converte os dados de saída de uma {@link JdvRede} com uma saída para outra de nove
		 * saídas.
		 * 
		 * @param data
		 *            dados de saída do arquivo original.
		 * @return dados de saída convertidos.
		 */
		public static MLData converteDadosUmParaNove(MLData data) {
			double d = data.getData(0);
			double[] dd = new double[9];
			
			dd[( int ) d] = 1;
			
			return new BasicMLData(dd);
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
		
		private static final CSVFormat FORMATO = CSVFormat.EG_FORMAT;
		
		private static final String DIR_RECURSOS = "resources/";
		
		public static String getNomeArquivoRede(String nomeArquivo) {
			return DIR_RECURSOS + nomeArquivo;
		}
		
		public static String getNomeArquivoDados(String nomeArquivo) {
			return DIR_RECURSOS + nomeArquivo + "_ES.eg";
		}
		
		public static void salvarDados(JdvRede rede, MLDataSet set) {
			EncogUtility.saveCSV(new File(getNomeArquivoDados(rede.getNome())), FORMATO, set);
		}
		
		public static BasicMLDataSet carregarDados(JdvRede rede) {
			String caminho = getNomeArquivoDados(rede.getNome());
			
			try {
				return ( BasicMLDataSet ) EncogUtility.loadCSV2Memory(caminho,
						rede.getNumeroEntradas(), rede.getNumeroSaidas(), false, FORMATO, false);
				
			} catch (Exception e) {
				
				System.out.println("Arquivo de entrada e saída \""
					+ caminho + "\" não encontrado! Criado conjunto vazio.");
				return new BasicMLDataSet();
			}
		}
		
		public static void salvarRede(JdvRede rede) {
			EncogDirectoryPersistence.saveObject(new File(getNomeArquivoRede(rede.getNome())), rede);
		}
		
		public static BasicNetwork carregarRede(JdvRede rede) {
			String caminho = getNomeArquivoRede(rede.getNome());
			
			try {
				return ( BasicNetwork ) EncogDirectoryPersistence.loadObject(new File(caminho));
				
			} catch (Exception e) {
				
				System.out.println("Arquivo de rede \""
					+ caminho + "\" não encontrado! Criada uma nova rede.");
				return rede.construirRede();
			}
			
		}
	}
	
}
