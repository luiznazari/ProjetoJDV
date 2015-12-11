package br.edu.unoesc.util;

import java.util.List;

/**
 * Interface para padronizar os elementos que podem ser analisados pelo algoritmo MiniMax. Possui os
 * métodos básicos requeridos pelo algoritmo.
 * 
 * @author Luiz Felipe Nazari
 */
public interface ElementoMiniMax {
	
	/**
	 * Determina o nível máximo que será explorado na árvore de possibilidades analisada pelo
	 * algoritmo MiniMax. <br>
	 * Também pode ser definido como a <b>profundidade</b> do algoritmo.<br>
	 * <br>
	 * Para máxima efiência, o nível máximo deverá contemplar todos os níveis da árvore de
	 * possibilidade a partir do estado atual do elemento.<br>
	 * <br>
	 * Considerando em conceitos de I.A., pode representar o nível de dificuldade em derrotar o
	 * computador.<br>
	 * 
	 * @return o nível máximo para análise.
	 */
	int getNivel();
	
	/**
	 * Determina se o elemento representa a busca pelo valor máximo (Max). A negação deste método
	 * determina a busca pelo valor mínimo (Mini).
	 * 
	 * @return <code>true</code> se for um elemento Max, <code>false</code> se for Mini.
	 */
	boolean isMax();
	
	/**
	 * Determina o valor de heurística do elemento utilizado pelo algoritmo MiniMax.
	 * 
	 * @return o valor de heurística do elemento.
	 */
	int getValorHeuristica();
	
	/**
	 * Constroi uma lista contendo todos os elementos nós filhos do elemento atual.<br>
	 * Geralmente os nós filhos representam elementos Mini, ou seja, o método
	 * {@link ElementoMiniMax#isMax()} deve retornar <code>false</code>.
	 * 
	 * @return a lista de elementos filhos.
	 */
	List<ElementoMiniMax> getElementosFilhos();
	
	/**
	 * Determina se a análise chegou a um fim. Este método marca um ponto de término ou corte na
	 * recursão do algoritmo MiniMax e indica que o valor de heurística do elemento pode ser
	 * retornado.
	 * 
	 * @return <code>true</code> caso a recursão chegou ao fim, <code>false</code> caso contrário.
	 */
	default boolean isFimRecursao() {
		return false;
	}
}
