package huehue.br.modelo;

import java.security.InvalidParameterException;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum Caractere {
	
	X("X", 1),
	O("O", -1),
	VAZIO("", 0);
	
	private int valor;
	
	@Setter
	private String chave;
	
	Caractere(String chave, int valor) {
		this.valor = valor;
		this.chave = chave;
	}
	
	public static Caractere get(String chave) {
		for (Caractere c : values()) {
			if (c.getChave().equals(chave))
				return c;
		}
		
		throw new InvalidParameterException("Caractere [" + chave + "] não conhecido!");
	}
}
