package huehue.br.tela;

import huehue.br.modelo.Caractere;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;

public class TelaExibicao extends JFrame {
	private static final long serialVersionUID = 4724495342867849916L;
	
	List<JPanel> pnlTab;
	
	MLDataSet dados;
	
	public TelaExibicao(MLDataSet dados) {
		this.dados = dados;
		
		setTitle("Jogo da Velha");
		setContentPane(constroiPainel());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 400);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private Container constroiPainel() {
		JPanel principal = new JPanel();
//		principal.setLayout(new GridLayout(dados.size(), 1));
		principal.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		for (MLDataPair par : dados) {
			principal.add(constroiPainelTabuleiro(par));
		}
		
		return principal;
	}
	
	private JPanel constroiPainelTabuleiro(MLDataPair par) {
		JPanel tab = new JPanel(new GridLayout(3, 3));
		tab.setSize(40, 40);
		
		JLabel[] jlbs = new JLabel[9];
		
		int i = 0;
		for (double d : par.getInput().getData()) {
			jlbs[i] = constroiCelulaTabuleiro(( int ) d);
			i++;
		}
		
		i = 0;
		for (double d : par.getIdeal().getData()) {
			if ((( int ) d) == 1) {
				jlbs[i].setForeground(Color.RED);
				jlbs[i].setText("#");
			}
			tab.add(jlbs[i]);
			i++;
		}
		
		return tab;
	}
	
	private JLabel constroiCelulaTabuleiro(int index) {
		JLabel celula = new JLabel(getCaractere(index));
		
		celula.setName("" + index);
		celula.setHorizontalAlignment(JLabel.CENTER);
		celula.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
		celula.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		
		return celula;
	}
	
	private String getCaractere(int d) {
		switch (d) {
			case 1:
				return Caractere.X.getChave();
			case -1:
				return Caractere.O.getChave();
			default:
				return " ";
		}
	}
	
}
