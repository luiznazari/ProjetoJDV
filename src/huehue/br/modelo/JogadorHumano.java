package huehue.br.modelo;

import huehue.br.exception.JdvException;

/**
 * Classe representando um jogador humano, requer interação via entrada de dados
 * pelo jogador.
 * 
 * @author Luiz Felipe Nazari
 */
public final class JogadorHumano extends Jogador {
	
	public JogadorHumano(Caractere caractere) {
		super(caractere);
	}
	
	@Override
	public final int novaJogada(double[] entradas) {
		throw new JdvException(
		        "Método não suportado! A entrada da posição ecolhida depende da ação do usuário.");
	}
	
}
