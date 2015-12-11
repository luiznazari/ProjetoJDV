package br.edu.unoesc.tela.evento;

import java.util.EventListener;

import javax.swing.JLabel;

/**
 * Eventos lançados pelas iterações durante uma partida do jogo da velha.
 * 
 * @author Luiz Felipe Nazari
 */
public interface TabuleiroListener extends EventListener {
	
	public void novaJogadaClique(JLabel jLabel);
	
}
