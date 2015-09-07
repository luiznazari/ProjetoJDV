package huehue.br.exception;

import java.time.LocalTime;

import lombok.Getter;

/**
 * Erros genéricos lançados pelas lógicas e telas referentes às classes do Jogo da Velha.
 * 
 * @author Luiz Felipe Nazari
 */
public class VeiaException extends RuntimeException {
	private static final long serialVersionUID = 5325084277409840830L;
	
	private static final String cabecalho = "[VEIA ERROR] " + LocalTime.now() + " ";
	
	@Getter
	private String mensagem;
	
	public VeiaException() {}
	
	public VeiaException(String mensagem) {
		this.mensagem = mensagem;
		System.err.println(cabecalho + mensagem);
	}
	
	public VeiaException(String mensagem, Throwable t) {
		this(mensagem);
		t.printStackTrace();
	}
	
}
