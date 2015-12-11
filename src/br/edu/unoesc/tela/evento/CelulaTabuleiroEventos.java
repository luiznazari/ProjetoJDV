package br.edu.unoesc.tela.evento;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import br.edu.unoesc.tela.TelaTabuleiro;

/**
 * Eventos relacionados a cada célula do tabuleiro da {@link TelaTabuleiro}.
 * 
 * @author Luiz Felipe Nazari
 */
public class CelulaTabuleiroEventos implements MouseListener {

	private List<TabuleiroListener> listeners = new ArrayList<TabuleiroListener>();

	private void addListener(TabuleiroListener toAdd) {
		listeners.add(toAdd);
	}

	public CelulaTabuleiroEventos(TelaTabuleiro tabuleiro) {
		addListener(tabuleiro);
	}

	private void notificaListeners(JLabel jLabel) {
		listeners.forEach(l -> l.novaJogadaClique(jLabel));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		notificaListeners(getSource(e));
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		getSource(e).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	private JLabel getSource(MouseEvent e) {
		return ( JLabel ) e.getSource();
	}

}
