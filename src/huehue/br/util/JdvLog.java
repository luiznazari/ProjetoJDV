package huehue.br.util;

import huehue.br.modelo.Caractere;
import huehue.br.modelo.Jogador;
import huehue.br.rede.modelo.JdvRedeAbstrata;
import huehue.br.rede.modelo.MapaSaida;
import huehue.br.util.JdvUtils.RNA;

import java.util.List;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;

/**
 * Classe utilitária para auxiliar na representação visual de estados e resultados
 * do sistema e do tabuleiro do Jogo da Velha.
 * 
 * @author Luiz Felipe Nazari
 */
public class JdvLog {

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

		sb.append("Sucesso: ").append(preencheValor(sucesso, 3)).append(" -> ");
		sb.append(RNA.valorAproximado(( double ) sucesso * 100 / tamanho)).append(" %\n");

		sb.append("Falhas:  ").append(preencheValor(falha, 3)).append(" -> ");
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
			tString += preencheValor(( int ) t[i], 2) + (i != len - 1 ? ", " : "]");

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
