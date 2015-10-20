package huehue.br.util;

import huehue.br.exception.JdvException;
import huehue.br.logica.Partida;
import huehue.br.modelo.Caractere;
import huehue.br.modelo.Jogador;
import huehue.br.modelo.JogadorAutomato;
import huehue.br.modelo.JogadorMiniMax;
import huehue.br.modelo.JogadorRNA;
import huehue.br.rede.dados.ConjuntosDados;
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

import org.encog.Encog;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.persist.PersistError;
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
		 * Avalia se há um vencedor em dado momento no jogo e retorna os índices do tabuleiro que formaram a combinação
		 * vencedora.
		 * 
		 * @param t
		 *            a array de valores correspondentes ao tabuleiro.
		 * @return o array contendo os índices que formam a combinação vencedora.
		 */
		// @formatter:off
		public static int[] computaIndicesVencedor(double[] t) {
			int vazio = Caractere.VAZIO.getValor();
			
			/*
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
						return new int[] { n[j], n[j] + i, n[j] + i * 2 };
			}
			
			return new int[] { vazio };
		}
		// @formatter:on
		
		/**
		 * Avalia se há um vencedor em dado momento no jogo. Caso retornar zero, referente ao {@link Caractere#VAZIO},
		 * não há vencedor.
		 * 
		 * @param t
		 *            a array de valores correspondentes ao tabuleiro.
		 * @return o valor correspondente ao {@link Caractere} vencedor.
		 */
		public static int computaVencedor(double[] t) {
			return ( int ) t[computaIndicesVencedor(t)[0]];
			
		}
		
		/**
		 * Avalia se houve um vencedor em dado momento no jogo.
		 * 
		 * @param t
		 *            a array de valores correspondentes ao tabuleiro.
		 * @param jogadores
		 *            os jogadores a serem analisados.
		 * @return o jogador vencedor ou null caso não haja um vencedor.
		 */
		public static Jogador computaVencedor(double[] t, Jogador... jogadores) {
			int vencedor = computaVencedor(t);
			
			for (Jogador j : jogadores)
				if (vencedor == j.getCaractere().getValor())
					return j;
			
			return null;
		}
		
		/**
		 * @param t
		 *            o tabuleiro.
		 * @return quantidade de posições vazias do tabuleiro.
		 */
		public static int computaEspacosVazios(double[] t) {
			int numeroVazios = 0;
			
			for (int i = 0; i < t.length; i++)
				if (t[i] == Caractere.VAZIO.getValor())
					numeroVazios++;
			
			return numeroVazios;
		}
		
		/**
		 * Avalia se o tabuleiro está completo, isto é, com todas as posições ocupadas.
		 * 
		 * @param t
		 *            o tabuleiro
		 * @return <code>true</code> caso esteja completo, <code>false</code> caso contrário.
		 */
		public static boolean isCompleto(double[] t) {
			return computaEspacosVazios(t) == 0;
		}
		
		public static void comparaJogadores(JogadorAutomato um, JogadorAutomato dois,
				int numeroPartidas) {
			if (um.getCaractere() == dois.getCaractere())
				throw new JdvException("Os jogadores precisam ter caracteres diferentes!");
			
			Log.ativo = false;
			
			for (int i = 0; i < numeroPartidas; i++) {
				Partida partida = new Partida();
				double[] t = new double[9];
				Jogador vencedor;
				
				while ((vencedor = computaVencedor(t, um, dois)) == null && !isCompleto(t)) {
					Jogador vez;
					
					if (partida.isJogadaPar())
						vez = dois;
					else
						vez = um;
					
					partida.novaJogada(vez.getCaractere(), t, vez.novaJogada(t));
				}
				
				if (vencedor != null)
					vencedor.pontuar();
				partida.encerrar(vencedor);
				
				um.notificarResultado(partida);
				dois.notificarResultado(partida);
			}
			
			Log.ativo = true;
			Log.placar(numeroPartidas, um, dois);
		}
		
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
			
			ConjuntosDados dados = Arquivo.carregarDados("bak_" + redeBase.getNome(),
					redeBase.getNumeroEntradas(), redeBase.getNumeroSaidas());
			MLDataSet dadosRedeObjetivo = new BasicMLDataSet();
			
			for (MLDataPair par : dados.getConjuntos()) {
				double[] entradasBase = redeBase.converteEntradaEmTabuleiro(par.getInput());
				MLData entradasObjetivo = redeObjetivo.traduzirEntrada(entradasBase);
				
				int saidaBase = redeBase.traduzirSaida(par.getIdeal());
				MLData saidaObjetivo = redeObjetivo.convertePosicaoTabuleiroEmSaida(saidaBase);
				
				dadosRedeObjetivo.add(entradasObjetivo, saidaObjetivo);
			}
			
			Arquivo.salvarDados("bak_" + redeObjetivo.getNome(), dadosRedeObjetivo);
		}
		
		/**
		 * Arredonda o valor especificado. O arredondamento sempre é realizado para cima.
		 * 
		 * @param d
		 *            o valor.
		 * @return valor arredondado.
		 */
		public static double valorAproximado(double d) {
			if (Double.isNaN(d))
				d = 0.0;
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
			
			return listaSaida.stream().sorted((ms1, ms2) -> ms2.compareTo(ms1))
					.collect(Collectors.toList());
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
		
		public static int incrementaVersao() {
			return ++Arquivo.versao;
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
				return DIR_RECURSOS + nomeArquivo + "/" + versao + ".eg";
			return DIR_RECURSOS + nomeArquivo + ".eg";
		}
		
		public static String getNomeArquivoDados(String nomeArquivo) {
			if (versionamento)
				return DIR_RECURSOS + nomeArquivo + "/" + versao + "_ES.eg";
			return DIR_RECURSOS + nomeArquivo + "_ES.eg";
		}
		
		public static void salvarDados(JdvRede rede, ConjuntosDados dados) {
			salvarDados(rede.getNome(), dados.getConjuntosParaSalvar());
		}
		
		private static void salvarDados(String nomeArquivo, MLDataSet set) {
			File arquivoDados = null;
			
			try {
				arquivoDados = new File(getNomeArquivoDados(nomeArquivo));
				EncogUtility.saveCSV(arquivoDados, FORMATO, set);
				
			} catch (PersistError e) {
				if (arquivoDados.getParentFile().mkdir())
					salvarDados(nomeArquivo, set);
				else
					System.err.println("Falha ao criar diretório do arquivo! Tente criá-lo manualmente: "
							+ arquivoDados.getParent());
			}
		}
		
		public static ConjuntosDados carregarDados(JdvRede rede) {
			return carregarDados(rede.getNome(), rede.getNumeroEntradas(), rede.getNumeroSaidas());
		}
		
		public static ConjuntosDados carregarDados(String nomeArquivo, int entradas, int saidas) {
			String caminho = getNomeArquivoDados(nomeArquivo);
			entradas += ConjuntosDados.getEntradasAdicionaisCSV();
			
			try {
				BasicMLDataSet set = ( BasicMLDataSet ) EncogUtility.loadCSV2Memory(caminho, entradas, saidas,
						false, FORMATO, false);
				return ConjuntosDados.criaConjuntosAPartirDeArquivo(set);
				
			} catch (Exception e) {
				
				System.out.println("Arquivo de entrada e saída \"" + caminho
						+ "\" não encontrado! Criado conjunto vazio.");
				return new ConjuntosDados();
			}
		}
		
		public static void salvarRede(JdvRede rede) {
			File arquivoRede = null;
			
			try {
				arquivoRede = new File(getNomeArquivoRede(rede.getNome()));
				EncogDirectoryPersistence.saveObject(arquivoRede, rede.getRede());
				
			} catch (PersistError e) {
				if (arquivoRede.getParentFile().mkdir())
					salvarRede(rede);
				else
					System.err.println("Falha ao criar diretório do arquivo! Tente criá-lo manualmente: "
							+ arquivoRede.getParent());
			}
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
				System.out.println("Arquivo de rede \"" + caminho
						+ "\" não encontrado! Criada uma nova rede.");
				return null;
			}
		}
		
	}
	
	public static class Log {
		
		// Indica se os logs devem aparecer no console.
		public static boolean ativo = true;
		
		public static String converteValorCaractere(int valor) {
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
		
		private static String logConsole(StringBuilder sb) {
			String log = sb.toString();
			System.out.println(log);
			return log;
		}
		
		public static String resultado(MLDataPair par, MLData saida, JdvRedeAbstrata rede) {
			if (!ativo)
				return "";
			
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
			
			return logConsole(sb);
		}
		
		public static String resultado(int tamanho, int sucesso, int falha) {
			if (!ativo)
				return "";
			
			StringBuilder sb = new StringBuilder();
			sb.append("\n--\n");
			sb.append("Teste finalizado.").append("\n");
			sb.append("Total de conjuntos: ").append(tamanho).append("\n");
			
			sb.append("Sucesso: ").append(Log.preencheValor(sucesso, 3)).append(" -> ");
			sb.append(RNA.valorAproximado(( double ) sucesso * 100 / tamanho)).append(" %\n");
			
			sb.append("Falhas:  ").append(Log.preencheValor(falha, 3)).append(" -> ");
			sb.append(RNA.valorAproximado(( double ) falha * 100 / tamanho)).append(" %\n");
			
			return logConsole(sb);
		}
		
		public static String partida(Caractere caractere, double[] entradas, int posicaoEscolhida) {
			if (!ativo)
				return "";
			
			double[] tabuleiro = entradas.clone();
			
			StringBuilder sb = new StringBuilder();
			sb.append("Jogador ").append(caractere.getChave());
			sb.append(" [Configuração=").append(tabuleiro(tabuleiro));
			sb.append(" Posição=").append(posicaoEscolhida).append("]");
			
			return logConsole(sb);
		}
		
		public static String tabuleiro(double[] t) {
			String tString = "[";
			
			int len = t.length;
			for (int i = 0; i < len; i++)
				tString += preencheValor(( int ) t[i], 2)
						+ (i != len - 1 ? ", " : "]");
			
			return tString;
		}
		
		public static String placar(final int partidas, final Jogador um, final Jogador dois) {
			if (!ativo)
				return "";
			
			int empates = partidas - um.getPontuacao() - dois.getPontuacao();
			
			StringBuilder sb = new StringBuilder();
			int pontuacaoUm = um.getPontuacao();
			int pontuacaoDois = dois.getPontuacao();
			
			String nomeUm = um.getClass().getSimpleName();
			String nomeDois = dois.getClass().getSimpleName();
			int padding = nomeUm.length() > nomeDois.length() ? nomeUm.length() : nomeDois.length();
			
			sb.append("\n--\n");
			sb.append("Resultado das partidas.").append("\n");
			
			sb.append("Total de partidas: ").append(partidas).append("\n");
			
			sb.append(preencheValor(nomeUm, padding)).append(" ").append(um.getCaractere().getChave())
					.append(" Pontuação:\t").append(pontuacaoUm).append(" -> ")
					.append(RNA.valorAproximado(( double ) pontuacaoUm * 100 / partidas))
					.append(" %\n");
			
			sb.append(preencheValor(nomeDois, padding)).append(" ").append(dois.getCaractere().getChave())
					.append(" Pontuação:\t").append(pontuacaoDois).append(" -> ")
					.append(RNA.valorAproximado(( double ) pontuacaoDois * 100 / partidas))
					.append(" %\n");
			
			sb.append("\t").append(preencheValor("Empates:\t", padding + 4)).append(empates).append(" -> ")
					.append(RNA.valorAproximado(( double ) empates * 100 / partidas))
					.append(" %\n");
			
			return logConsole(sb);
		}
		
		public static String fimPartida(Jogador vencedor) {
			if (!ativo)
				return "";
			
			StringBuilder sb = new StringBuilder();
			
			sb.append("Fim de partida. ");
			
			if (vencedor != null)
				sb.append("Vencedor: ").append(vencedor.getCaractere().getChave()).append("!");
			else
				sb.append("Empate!");
			
			return logConsole(sb);
		}
	}
	
	public static void main(String[] args) {
//		String c = "A";
//		
//		String caminho = Arquivo.DIR_RECURSOS + c + "1";
//		MLDataSet set = EncogUtility.loadCSV2Memory(caminho, 9, 9, false, Arquivo.FORMATO,
//				false);
//		
//		new TelaExibicao(set);
//		
//		caminho = Arquivo.DIR_RECURSOS + c + "2";
//		set = EncogUtility.loadCSV2Memory(caminho, 9, 9, false, Arquivo.FORMATO, false);
//		
//		new TelaExibicao(set);
//		RNA.converteArquivosDeDadosEntreRedes(new MultilayerPerceptron2(), new MultilayerPerceptron3());
		
		JdvUtils.Arquivo.versionamento(1967);
		
//		JogadorAutomato um = new JogadorAleatorio(Caractere.X);
		
		JogadorAutomato um = new JogadorRNA(Caractere.X, false);
		JogadorAutomato dois = new JogadorMiniMax(Caractere.O);
		
//		JogadorAutomato um = new JogadorRNA(Caractere.O, false);
//		JogadorAutomato dois = new JogadorMiniMax(Caractere.X);
		Tabuleiro.comparaJogadores(um, dois, 100);
		
		Encog.getInstance().shutdown();
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
