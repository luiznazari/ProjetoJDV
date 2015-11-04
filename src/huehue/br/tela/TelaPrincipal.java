package huehue.br.tela;

import huehue.br.tela.evento.TelaPrincipalEventos;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import lombok.Getter;

public class TelaPrincipal extends JFrame {
	private static final long serialVersionUID = 1709587359998885314L;

	// --------------------

	private JPanel pnlPrincipal;

	// --------------------

	@Getter
	private TelaTabuleiro telaTabuleiro;

	public TelaPrincipal() {
		setTitle("Jogo da Velha");
		setContentPane(constroiPainel());
		setJMenuBar(new TelaPrincipalMenu(this).constroiMenu());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 400);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		addWindowListener(new TelaPrincipalEventos());

		telaTabuleiro.novaPartida();
	}

	private JPanel constroiPainel() {
		pnlPrincipal = new JPanel(new BorderLayout());
		telaTabuleiro = new TelaTabuleiro();

		pnlPrincipal.add(telaTabuleiro.getPainel(), BorderLayout.CENTER);

		return pnlPrincipal;
	}

	public static void main(String[] args) {
		new TelaPrincipal();
	}

}
