package huehue.br.modelo;

import huehue.br.logica.Partida;

/**
 * Classe representando um jogador autômato, as ações são determinadas por inteligências
 * artificiais.
 * 
 * @author Luiz Felipe Nazari
 */
public abstract class JogadorAutomato extends Jogador {
	
	// Por padrão, a I.A. interpreta o X como sua posição (+1) e o O como adversário (-1).
	protected boolean jogadorX;
	
	public JogadorAutomato(Caractere caractere) {
		super(caractere);
		jogadorX = getCaractere() == Caractere.X;
	}
	
	@Override
	public abstract int novaJogada(double[] entradas);
	
	/**
	 * Notifica o resultado da partida ao jogador. <br>
	 * A implementação do método depende da necessidade da I.A. utilizada em cada jogador.<br>
	 * Por padrão, nada é realizado.
	 * 
	 * @param partida
	 *        a partida contendo o histórico de jogadas realizadas e o jogador vencedor.
	 */
	public void notificarResultado(Partida partida) {
		// Nada a fazer.
	}
	
	/**
	 * Escolhe uma posição aleatória na configuração do tabuleiro atual, apenas posições não
	 * ocupadas serão escolhidas.
	 * 
	 * @param cfgTabuleiro
	 *        configuração do tabuleiro atual.
	 * @return o índice da posição escolhida.
	 */
	protected int escolhePosicao(double[] cfgTabuleiro) {
		int posicoesValidasLength = 0;
		int[] posicoesValidas = new int[cfgTabuleiro.length];
		
		for (int i = 0; i < cfgTabuleiro.length; i++)
			if (cfgTabuleiro[i] == Caractere.VAZIO.getValor())
				posicoesValidas[posicoesValidasLength++] = i;
		
		return posicoesValidas[( int ) (Math.random() * --posicoesValidasLength)];
	}
	
	/**
	 * Valida os valores das entradas a serem computadas pela I.A..
	 * 
	 * @param entradas
	 *        o tabuleiro.
	 * @return o tabuleiro validado.
	 * @see JogadorAutomato#validaEntradas(double[], boolean)
	 */
	protected double[] validaEntradas(double[] entradas) {
		return validaEntradas(entradas, this.jogadorX);
	}
	
	/**
	 * Valida os valores das entradas a serem computadas pela I.A.. Caso o {@link JogadorAutomato}
	 * em questão possuir o {@link Caractere#O}, os valores das entradas precisarão ser ajustados,
	 * pois a rede é treinada para reconhecer o valor do {@link Caractere#X} como posição da Rede e
	 * o {@link Caractere#O} como o adversário.
	 * 
	 * @param entradas
	 *        o tabuleiro.
	 * @param jogadorX
	 *        informa se são entradas do jogador X.
	 * @return o tabuleiro validado.
	 */
	protected double[] validaEntradas(double[] entradas, boolean jogadorX) {
		entradas = entradas.clone();
		
		if (!jogadorX)
			for (int i = 0; i < entradas.length; i++)
				if (entradas[i] != Caractere.VAZIO.getValor())
					entradas[i] *= -1;
		
		return entradas;
	}
	
}
