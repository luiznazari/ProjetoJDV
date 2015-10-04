package huehue.br.util;

import huehue.br.modelo.Caractere;
import huehue.br.rede.modelo.JdvRede;
import huehue.br.rede.modelo.JdvRedeAbstrata;
import huehue.br.rede.modelo.MapaSaida;
import huehue.br.rede.modelo.MultilayerPerceptron;
import huehue.br.rede.modelo.MultilayerPerceptron2;
import huehue.br.rede.modelo.MultilayerPerceptron3;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
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
		 * {@link Caractere#VAZIO} , não houve vencedor.
		 * 
		 * @param t
		 *            a array de valores correspondentes ao tabuleiro.
		 * @return o valor correspondente ao {@link Caractere} vencedor.
		 */
		// @formatter:off
		public static double computaVencedor(double[] t) {
			int vazio = Caractere.VAZIO.getValor();
			
			/**
			 * Índices do tabuleiro para comparar posições vencedoras do jogo da velha.
			 * Esta matriz possui 4 arrays, para cada array corresponde, há array.length conbinações vencedoras.
			 * Cada array vencedora do tabuleiro é dada pelos índices: array[x], array[x] + y e array[x] + y * 2],
			 * onde y é o índice da array na matriz + 1.
			 * 
			 * Exemplo: array = m[1]
			 * 			[array[0], array[0] + (1 + 1), array[0] + (1 + 1) * 2]
			 *			[2, 	   2 + 2,			   2 + 4]
			 *			[2,		   4,				   6] -> Índices do array do tabuleiro, formando a combinação vencedora.
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
		 * Converte um arquivo de dados (conjuntos de entradas e saídas) de uma rede à outra.
		 * 
		 * @param redeBase
		 * @param redeObjetivo
		 * @param dirRecursos
		 *            diretório onde do arquivo base.
		 */
		public static void converteArquivosDeDadosEntreRedes(JdvRedeAbstrata redeBase,
				JdvRedeAbstrata redeObjetivo, String dirRecursos) {
			Arquivo.recursos(dirRecursos);
			converteArquivosDeDadosEntreRedes(redeBase, redeObjetivo);
		}
		
		/**
		 * Converte um arquivo de dados (conjuntos de entradas e saídas) de uma rede à outra.
		 * 
		 * @param redeBase
		 * @param redeObjetivo
		 */
		public static void converteArquivosDeDadosEntreRedes(JdvRedeAbstrata redeBase,
				JdvRedeAbstrata redeObjetivo) {
			Arquivo.versionamento(false);
			
			MLDataSet dadosRedeBase = Arquivo.carregarDados(redeBase);
			MLDataSet dadosRedeObjetivo = new BasicMLDataSet();
			
			for (MLDataPair par : dadosRedeBase) {
				double[] entradasBase = redeBase.converteEntradaEmTabuleiro(par.getInput());
				MLData entradasObjetivo = redeObjetivo.traduzirEntrada(entradasBase);
				
				int saidaBase = redeBase.traduzirSaida(par.getIdeal());
				MLData saidaObjetivo = redeObjetivo.convertePosicaoTabuleiroEmSaida(saidaBase);
				
				dadosRedeObjetivo.add(entradasObjetivo, saidaObjetivo);
			}
			
			Arquivo.salvarDados(redeObjetivo, dadosRedeObjetivo);
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
		
		/**
		 * Cria um mapeamento de saídas da rede, ordenando a lista de saídas do maior ao menor
		 * valor, mantendo os índices dos valores no vetor de saída original.
		 * 
		 * @param saídas
		 *            da rede.
		 * @return lista das saídas ordenadas e mapeadas.
		 */
		public static List<MapaSaida> toSaidasMapeadas(double[] saidas) {
			List<MapaSaida> listaSaida = new ArrayList<>();
			
			int len = saidas.length;
			for (int i = 0; i < len; i++)
				listaSaida.add(new MapaSaida(i, saidas[i]));
			
			return listaSaida.stream().sorted((ms1, ms2) -> ms2.compareTo(ms1)).collect(Collectors.toList());
		}
	}
	
	/**
	 * Métodos úteis para auxiliar a manipulação de arquivos, salvar e carregar dados dos conjuntos
	 * de entradas e saídas e redes neuronais.
	 */
	public static class Arquivo {
		
		private static final CSVFormat FORMATO = CSVFormat.EG_FORMAT;
		
		private static String DIR_RECURSOS = "resources/testes/";
		
		private static boolean versionamento = false;
		
		private static int versao = 0;
		
		public static void recursos(String caminho) {
			Arquivo.DIR_RECURSOS = caminho;
		}
		
		public static void incrementaVersao() {
			Arquivo.versao += 1;
		}
		
		public static void versionamento(int versao) {
			Arquivo.versao = versao;
			versionamento(true);
		}
		
		public static void versionamento(boolean versionamento) {
			Arquivo.versionamento = versionamento;
		}
		
		public static String getNomeArquivoRede(String nomeArquivo) {
			if (versionamento)
				return DIR_RECURSOS + versao + "_" + nomeArquivo + ".eg";
			return DIR_RECURSOS + nomeArquivo + ".eg";
		}
		
		public static String getNomeArquivoDados(String nomeArquivo) {
			if (versionamento)
				return DIR_RECURSOS + versao + "_" + nomeArquivo + "_ES.eg";
			return DIR_RECURSOS + nomeArquivo + "_ES.eg";
		}
		
		public static void salvarDados(JdvRede rede, MLDataSet set) {
			salvarDados(rede.getNome(), set);
		}
		
		public static void salvarDados(String nomeArquivo, MLDataSet set) {
			EncogUtility.saveCSV(new File(getNomeArquivoDados(nomeArquivo)), FORMATO, set);
		}
		
		public static BasicMLDataSet carregarDados(JdvRede rede) {
			return carregarDados(rede.getNome(), rede.getNumeroEntradas(), rede.getNumeroSaidas());
		}
		
		public static BasicMLDataSet carregarDados(String nomeArquivo, int entradas, int saidas) {
			String caminho = getNomeArquivoDados(nomeArquivo);
			
			try {
				return ( BasicMLDataSet ) EncogUtility.loadCSV2Memory(caminho, entradas, saidas, false, FORMATO, false);
				
			} catch (Exception e) {
				
				System.out.println("Arquivo de entrada e saída \"" + caminho + "\" não encontrado! Criado conjunto vazio.");
				return new BasicMLDataSet();
			}
		}
		
		public static void salvarRede(JdvRede rede) {
			EncogDirectoryPersistence.saveObject(new File(getNomeArquivoRede(rede.getNome())),
					rede.getRede());
		}
		
		public static BasicNetwork carregarRede(JdvRede rede) {
			BasicNetwork net = carregarRede(rede.getNome());
			
			if (net == null)
				return rede.construirRede();
			
			return net;
		}
		
		public static BasicNetwork carregarRede(String nomeArquivo) {
			String caminho = getNomeArquivoRede(nomeArquivo);
			
			try {
				FileInputStream stream = new FileInputStream(new File(caminho));
				return ( BasicNetwork ) EncogDirectoryPersistence.loadObject(stream);
			} catch (Exception e) {
				System.out.println("Arquivo de rede \"" + caminho + "\" não encontrado! Criada uma nova rede.");
				return null;
			}
		}
		
	}
	
	public static class Log {
		
		private static String converteValorCaractere(int valor) {
			switch (valor) {
				case 1:
					return Caractere.X.getChave();
				case -1:
					return Caractere.O.getChave();
				case 0:
					return Caractere.VAZIO.getChave();
				default:
					return "#";
			}
		}
		
		public static <T> String preencheValor(T valor, int casas) {
			return String.format("%1$" + casas + "s", valor);
		}
		
		@SafeVarargs
		private static StringBuilder imprimeLinhaTabuleiro(StringBuilder sb, double... valores) {
			for (int i = 0; i < valores.length; i++) {
				sb.append(" ");
				sb.append(converteValorCaractere(( int ) valores[i]));
				sb.append(" ");
				
				if (i != valores.length - 1)
					sb.append("|");
			}
			
			return sb;
		}
		
		private static StringBuilder imprimeDivisaoTabuleiro(StringBuilder sb) {
			return sb.append("---+---+---");
		}
		
		private static StringBuilder imprimeSaidaRede(StringBuilder sb, double[] saidas) {
			List<MapaSaida> listaSaida = JdvUtils.RNA.toSaidasMapeadas(saidas);
			
			for (int i = 0; i < saidas.length; i++) {
				sb.append(listaSaida.get(i));
				
				if (i != saidas.length - 1)
					sb.append(", ");
			}
			
			return sb;
		}
		
		public static void resultado(MLDataPair par, MLData saida, JdvRedeAbstrata rede) {
			double[] entrada = rede.converteEntradaEmTabuleiro(par.getInput());
			int ideal = rede.traduzirSaida(par.getIdeal());
			entrada[ideal] = 2;
			
			StringBuilder sb = new StringBuilder().append("\n");
			
			imprimeLinhaTabuleiro(sb, entrada[0], entrada[1], entrada[2]).append("\t");
			sb.append("Saídas: ").append("\n");
			
			imprimeDivisaoTabuleiro(sb).append("\t");
			imprimeSaidaRede(sb, saida.getData()).append("\n");
			
			imprimeLinhaTabuleiro(sb, entrada[3], entrada[4], entrada[5]).append("\t");
			sb.append("Posição: ").append(rede.traduzirSaida(saida)).append("\n");
			
			imprimeDivisaoTabuleiro(sb).append("\t");
			sb.append("Esperado: ").append(ideal).append("\n");
			
			imprimeLinhaTabuleiro(sb, entrada[6], entrada[7], entrada[8]).append("\n\n");
			
			System.out.print(sb.toString());
		}
		
		public static void resultado(int tamanho, int sucesso, int falha) {
			StringBuilder sb = new StringBuilder();
			sb.append("\n--\n");
			sb.append("Teste finalizado.").append("\n");
			sb.append("Total de conjuntos: ").append(tamanho).append("\n");
			
			sb.append("Sucesso:").append(Log.preencheValor(sucesso, 3)).append(" -> ");
			sb.append(RNA.valorAproximado(( double ) sucesso * 100 / tamanho)).append(" %\n");
			
			sb.append("Falhas: ").append(Log.preencheValor(falha, 3)).append(" -> ");
			sb.append(RNA.valorAproximado(( double ) falha * 100 / tamanho)).append(" %\n");
			
			System.out.println(sb.toString());
		}
	}
	
	public static void main(String[] args) {
//		String c = "A";
//		
//		String caminho = Arquivo.DIR_RECURSOS + c + "1";
//		MLDataSet set = EncogUtility.loadCSV2Memory(caminho, 9, 9, false, Arquivo.FORMATO, false);
//		
//		new TelaExibicao(set);
//		
//		caminho = Arquivo.DIR_RECURSOS + c + "2";
//		set = EncogUtility.loadCSV2Memory(caminho, 9, 9, false, Arquivo.FORMATO, false);
//		
//		new TelaExibicao(set);
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
