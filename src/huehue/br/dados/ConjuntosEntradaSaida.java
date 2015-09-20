package huehue.br.dados;

import org.encog.ml.data.basic.BasicMLDataSet;

public class ConjuntosEntradaSaida {
	
	/**
	 * Entradas dos casos de testes do tabuleiro do jogo da velha.
	 * 
	 * <pre>
	 * 1  = Jogador X
	 * -1 = Jogador O
	 * 0  = posição vazia
	 * </pre>
	 */
	public static double[][] ENTRADAS = {
	    {
	        0, 0, 0,
	        0, 0, 0,
	        0, 0, 0
	    }, {
	        1, 1, 0,
	        0, 0, 0,
	        0, 0, 0
	    }, {
	        1, 0, 0,
	        0, 1, 0,
	        0, 0, 0
	    }, {
	        1, 0, 0,
	        1, 0, 0,
	        0, 0, 0
	    }, {
	        0, 1, 0,
	        0, 1, 0,
	        0, 0, 0
	    }, {
	        0, 0, 0,
	        0, 1, 0,
	        0, 1, 0
	    }, {
	        0, 0, 0,
	        1, 1, 0,
	        0, 0, 0
	    }, {
	        0, 0, 0,
	        0, 1, 1,
	        0, 0, 0
	    }, {
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
	    }
	};
	
	/**
	 * Saídas dos casos de testes do tabuleiro do jogo da velha representados por valores menores do
	 * que zero para contemplar a abrangência do resultado da função de ativação utilizada na camada
	 * de saída da Rede Neuronal. <br>
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
	public static double SAIDAS[][] = {
	    {
		    0.5
	    }, {
		    0.3
	    }, {
		    0.9
	    }, {
		    0.7
	    }, {
		    0.8
	    }, {
		    0.2
	    }, {
		    0.6
	    }, {
		    0.4
	    }, {
		    0.1
	    }, {
		    0.1
	    }, {
		    0.4
	    }, {
		    0.6
	    }, {
		    0.2
	    }
	};
	
	public BasicMLDataSet constroiDataSet() {
		return new BasicMLDataSet(ENTRADAS, SAIDAS);
	}
}
