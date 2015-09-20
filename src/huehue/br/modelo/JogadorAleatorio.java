package huehue.br.modelo;

/**
 * Representa um jogador que utiliza um algoritimo para determinar posições aleatórias.
 * 
 * @author Luiz Felipe Nazari
 */
public class JogadorAleatorio extends JogadorAutomato {
	
	public JogadorAleatorio(Caractere caractere) {
		super(caractere);
	}
	
	@Override
	public int novaJogada(double[] entradas) {
		return super.escolhePosicao(entradas);
	}
	
}
