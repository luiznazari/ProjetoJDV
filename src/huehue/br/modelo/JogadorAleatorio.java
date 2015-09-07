package huehue.br.modelo;

/**
 * Representa um jogador que utiliza um algorótimo para determinar posições aleatórias.
 * 
 * @author Luiz Felipe Nazari
 */
public class JogadorAleatorio extends JogadorAutomato {
	
	public JogadorAleatorio(Caractere caractere) {
		super(caractere);
	}
	
	@Override
	public int novaJogada(double[] entradas) {
		return escolhePosicao(entradas);
	}
	
}
