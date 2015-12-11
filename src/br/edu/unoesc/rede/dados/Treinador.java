package br.edu.unoesc.rede.dados;

/**
 * @author Luiz Felipe Nazari
 */
public interface Treinador {

	public static final double MAX_ERRO = 0.10; // 10%

	public static final double MIN_ERRO = 0.03; // 3%

	public static final double MAX_CONST_MOMENTUM = 0.9;

	public static final double MIN_CONST_MOMENTUM = 0.1;

	public static final double MAX_CONST_APRENDIZAGEM = 0.09;

	public static final double MIN_CONST_APRENDIZAGEM = 0.02;

	public static final int MAX_ITERACOES = 30;

	public static final int MAX_CICLOS_TREINAMENTO = 5;

	public boolean isTreinou();

	public void treinar();

}
