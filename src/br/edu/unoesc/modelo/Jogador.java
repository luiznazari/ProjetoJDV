package br.edu.unoesc.modelo;

import lombok.Getter;

/**
 * Classe abstrata que define as ações de um jogador visíveis às telas.
 * 
 * @author Luiz Felipe Nazari
 */
public abstract class Jogador {
	
	@Getter
	private Integer pontuacao = 0;
	
	private Caractere caractere;
	
	public Jogador(Caractere caractere) {
		this.caractere = caractere;
	}
	
	/**
	 * Retorna o caractere que será utilizado para demarcar as
	 * posições escolhidas pelo jogador durante as partidas.
	 * 
	 * @return o caractere utilizado pelo jogador.
	 */
	public Caractere getCaractere() {
		return this.caractere;
	}
	
	/**
	 * Adiciona um ponto ao contador do jogador.
	 */
	public void pontuar() {
		pontuacao++;
	}
	
	/**
	 * Realiza uma nova jogada.
	 * 
	 * @param entradas
	 *        configuração atual do tabuleiro.
	 * @return posição escolhida pelo jogador.
	 */
	abstract public int novaJogada(double[] entradas);
	
}
