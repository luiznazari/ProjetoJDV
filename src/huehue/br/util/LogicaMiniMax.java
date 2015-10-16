package huehue.br.util;

/**
 * Classe utilitária contendo implementações do algoritmo MiniMax.<br>
 * 
 * @author Luiz Felipe Nazari
 * @see ElementoMiniMax
 */
public class LogicaMiniMax {
	
	private static final int MINIMAX_ALPHA = Integer.MIN_VALUE + 1;
	
	private static final int MINIMAX_BETA = Integer.MAX_VALUE - 1;
	
	/**
	 * Implementação reduzida do algoritmo MiniMax.<br>
	 * Produz a mesma saída que {@link LogicaMiniMax#MiniMax(ElementoMiniMax)}.
	 * 
	 * @param tabuleiro
	 *            o tabuleiro a ser analisado.
	 * @return o valor de heurística do tabuleiro.
	 */
	public static int MiniMaxReduzido(ElementoMiniMax elemento) {
		return -MiniMaxReduzido(elemento.getNivel(), MINIMAX_ALPHA, MINIMAX_BETA, elemento);
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
	 * @param nivel
	 *            o nível de profundidade da análise do MiniMax.
	 * @param alpha
	 * @param beta
	 * @param elemento
	 *            o elemento a ser analisado.
	 * @return o valor de heurística do elemento.
	 */
	private static int MiniMaxReduzido(int nivel, int alpha, int beta, ElementoMiniMax elemento) {
		if (nivel <= 0 || elemento.isFimRecursao())
			return elemento.isMax() ? elemento.getValorHeuristica() : -elemento.getValorHeuristica();
		
		for (ElementoMiniMax el : elemento.getElementosFilhos()) {
			int valorHeuristica = -MiniMaxReduzido(nivel - 1, -beta, -alpha, el);
			
			if (alpha < valorHeuristica) {
				alpha = valorHeuristica;
				
				if (alpha >= beta)
					break;
			}
		}
		
		return alpha;
	}
	
	/**
	 * Implementação do algoritmo MiniMax.<br>
	 * Produz a mesma saída que {@link LogicaMiniMax#MiniMaxReduzido(ElementoMiniMax)}.
	 * 
	 * @param elemento
	 *            o elemento a ser analisado.
	 * @return o valor de heurística do elemento.
	 */
	public static int MiniMax(ElementoMiniMax elemento) {
		return MiniMax(elemento.getNivel(), MINIMAX_ALPHA, MINIMAX_BETA, elemento);
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
	 * @param nivel
	 *            o nível de profundidade da análise do MiniMax.
	 * @param alpha
	 * @param beta
	 * @param elemento
	 *            o elemento a ser analisado.
	 * @return o valor de heurística do elemento.
	 */
	private static int MiniMax(int nivel, int alpha, int beta, ElementoMiniMax elemento) {
		
		if (nivel <= 0 || elemento.isFimRecursao())
			return elemento.getValorHeuristica();
		
		for (ElementoMiniMax el : elemento.getElementosFilhos()) {
			int valorHeuristica = MiniMax(nivel - 1, alpha, beta, el);
			
			if (elemento.isMax()) {
				if (alpha < valorHeuristica) {
					alpha = valorHeuristica;
					
					if (alpha >= beta)
						break;
				}
			} else {
				if (beta > valorHeuristica) {
					beta = valorHeuristica;
					
					if (alpha >= beta)
						break;
				}
			}
		}
		
		return elemento.isMax() ? alpha : beta;
	}
	
}
