package huehue.br.util;

import huehue.br.modelo.JogadorMinMax;
import huehue.br.modelo.JogadorMinMax.TabuleiroMinMax;

/**
 * Classe utilitária contendo implementações do algoritmo MiniMax.<br>
 * TODO Criar uma interface para utilização da classe auxiliar {@link JogadorMinMax.TabuleiroMinMax}
 * .
 * 
 * @author Luiz Felipe Nazari
 */
public class LogicaMiniMax {
	
	private static final int MINIMAX_ALPHA = Integer.MIN_VALUE + 1;
	
	private static final int MINIMAX_BETA = Integer.MAX_VALUE - 1;
	
	/**
	 * Implementação reduzida do algoritmo MiniMax.<br>
	 * Produz a mesma saída que {@link LogicaMiniMax#MiniMax(TabuleiroMinMax)}.
	 * 
	 * @param tabuleiro
	 *            o tabuleiro a ser analisado.
	 * @return o valor de heurística do tabuleiro.
	 */
	public static int MiniMaxReduzido(TabuleiroMinMax tabuleiro) {
		return -MiniMaxReduzido(8, MINIMAX_ALPHA, MINIMAX_BETA, tabuleiro);
	}
	
	/**
	 * Implementação reduzida do algoritmo MiniMax.<br>
	 * O parâmetro <code>level</code> indica o nível de profundidade da análise do algoritmo,
	 * determina quantos níveis irão ser analisados na árvore de possibilidades. Considerando em
	 * termos de I.A., representa o nível de "eficiência" do algorítimo, isto é, o nível de
	 * dificuldade em derrotar o computador.<br>
	 * <br>
	 * Esta implementação é uma adaptação do algoritmo elaborado por <a href=
	 * "http://www.codeproject.com/Articles/43622/Solve-Tic-Tac-Toe-with-the-MiniMax-algorithm">Dong
	 * Xiang</a>.
	 * 
	 * @param level
	 *            o nível de profundidade da análise do MiniMax.
	 * @param alpha
	 * @param beta
	 * @param tabuleiro
	 *            o tabuleiro a ser analisado.
	 * @return o valor de heurística do tabuleiro.
	 */
	private static int MiniMaxReduzido(int level, int alpha, int beta, TabuleiroMinMax tabuleiro) {
		if (level == 0 || tabuleiro.isFimDeJogo())
			return tabuleiro.x ? tabuleiro.pontos : -tabuleiro.pontos;
		
		for (TabuleiroMinMax t : tabuleiro.filhos()) {
			int score = -MiniMaxReduzido(level - 1, -beta, -alpha, t);
			
			if (alpha < score) {
				alpha = score;
				
				if (alpha >= beta)
					break;
			}
		}
		
		return alpha;
	}
	
	/**
	 * Implementação do algoritmo MiniMax.<br>
	 * Produz a mesma saída que {@link LogicaMiniMax#MiniMaxReduzido(TabuleiroMinMax)}.
	 * 
	 * @param tabuleiro
	 *            o tabuleiro a ser analisado.
	 * @return o valor de heurística do tabuleiro.
	 */
	public static int MiniMax(TabuleiroMinMax tabuleiro) {
		return MiniMax(8, MINIMAX_ALPHA, MINIMAX_BETA, tabuleiro);
	}
	
	/**
	 * Implementação do algoritmo MiniMax.<br>
	 * O parâmetro <code>level</code> indica o nível de profundidade da análise do algoritmo,
	 * determina quantos níveis irão ser analisados na árvore de possibilidades. Considerando em
	 * termos de I.A., representa o nível de "eficiência" do algorítimo, isto é, o nível de
	 * dificuldade em derrotar o computador.<br>
	 * <br>
	 * Esta implementação é uma adaptação do algoritmo elaborado por <a href=
	 * "http://www.codeproject.com/Articles/43622/Solve-Tic-Tac-Toe-with-the-MiniMax-algorithm">Dong
	 * Xiang</a>.
	 * 
	 * @param level
	 *            o nível de profundidade da análise do MiniMax.
	 * @param alpha
	 * @param beta
	 * @param tabuleiro
	 *            o tabuleiro a ser analisado.
	 * @return o valor de heurística do tabuleiro.
	 */
	private static int MiniMax(int level, int alpha, int beta, TabuleiroMinMax tabuleiro) {
		
		if (level == 0 || tabuleiro.isFimDeJogo())
			return tabuleiro.pontos;
		
		for (TabuleiroMinMax t : tabuleiro.filhos()) {
			int score = MiniMax(level - 1, alpha, beta, t);
			
			if (!tabuleiro.x) {
				if (beta > score) {
					beta = score;
					
					if (alpha >= beta)
						break;
				}
			} else {
				if (alpha < score) {
					alpha = score;
					
					if (alpha >= beta)
						break;
				}
			}
		}
		
		return tabuleiro.x ? alpha : beta;
	}
	
}
