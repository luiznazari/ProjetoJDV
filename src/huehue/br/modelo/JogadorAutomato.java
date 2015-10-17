package huehue.br.modelo;

import huehue.br.logica.Partida;

/**
 * Classe representando um jogador autômato, as ações são determinadas por inteligências
 * artificiais.
 * 
 * @author Luiz Felipe Nazari
 */
public abstract class JogadorAutomato extends Jogador {
	
	public JogadorAutomato(Caractere caractere) {
		super(caractere);
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
	
}
