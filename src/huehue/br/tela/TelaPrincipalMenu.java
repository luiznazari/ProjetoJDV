package huehue.br.tela;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

public class TelaPrincipalMenu {
	
	// --------------------
	
	private JMenuBar jmbMenuBar;
	
	private JMenu jmnArquivo;
	
	private JMenu jmnJogadorUm;
	
	private JMenuItem jmiJ1TrocarCaractere;
	
	private JMenu jmnJogadorDois;
	
	private JMenuItem jmiJ2TrocarCaractere;
	
	private JMenuItem jmiSair;
	
	private JMenu jmnTabuleiro;
	
	private JMenuItem jmiResetar;
	
	private JMenu jmnAjuda;
	
	private JMenuItem jmiComandos;
	
	// --------------------
	
	private TelaPrincipal principal;
	
	public TelaPrincipalMenu(TelaPrincipal telaPrincipal) {
		this.principal = telaPrincipal;
	}
	
	public JMenuBar constroiMenu() {
		jmbMenuBar = new JMenuBar();
		
		jmbMenuBar.add(constroiMenuArquivo());
		jmbMenuBar.add(constroiMenuTabuleiro());
		jmbMenuBar.add(constroiMenuAjuda());
		
		return jmbMenuBar;
	}
	
	private JMenu constroiMenuArquivo() {
		jmnArquivo = new JMenu("Arquivo");
		
		jmnJogadorUm = new JMenu("Jogador 1");
		jmiJ1TrocarCaractere = new JMenuItem("Trocar caractere");
		jmnJogadorUm.add(jmiJ1TrocarCaractere);
		jmnArquivo.add(jmnJogadorUm);
		
		jmnJogadorDois = new JMenu("Jogador 2");
		jmiJ2TrocarCaractere = new JMenuItem("Trocar caractere");
		jmnJogadorDois.add(jmiJ2TrocarCaractere);
		jmnArquivo.add(jmnJogadorDois);
		
		jmnArquivo.add(new JSeparator());
		jmiSair = new JMenuItem("Sair");
		jmnArquivo.add(jmiSair);
		
		jmiSair.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				principal.dispose();
			}
		});
		
		return jmnArquivo;
	}
	
	private JMenu constroiMenuTabuleiro() {
		jmnTabuleiro = new JMenu("Tabuleiro");
		
		jmiResetar = new JMenuItem("Resetar");
		jmnTabuleiro.add(jmiResetar);
		
		jmiResetar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				principal.getTelaTabuleiro().novaPartida();
			}
		});
		
		return jmnTabuleiro;
	}
	
	private JMenu constroiMenuAjuda() {
		jmnAjuda = new JMenu("Ajuda");
		jmbMenuBar.add(jmnAjuda);
		
		jmiComandos = new JMenuItem("Comandos");
		jmnAjuda.add(jmiComandos);
		
		jmiComandos.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
				    "Nenhuma ajuda disponível no momento.", "Comandos",
				    JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		return jmnAjuda;
	}
	
}
