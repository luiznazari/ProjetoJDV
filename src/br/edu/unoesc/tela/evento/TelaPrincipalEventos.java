package br.edu.unoesc.tela.evento;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.encog.Encog;

/**
 * Eventos lançados pela tela principal do sistema.
 * 
 * @author Luiz Felipe Nazari
 */
public class TelaPrincipalEventos implements WindowListener {
	
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * {@inheritDoc}<br>
	 * <br>
	 * Encerra todas as threads iniciadas pelo {@link Encog}.
	 */
	@Override
	public void windowClosed(WindowEvent e) {
		Encog.getInstance().shutdown();
	}
	
	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}
	
}
