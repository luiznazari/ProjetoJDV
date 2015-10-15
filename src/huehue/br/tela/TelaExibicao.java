package huehue.br.tela;

import huehue.br.rede.modelo.JdvRedeAbstrata;
import huehue.br.rede.modelo.MultilayerPerceptron2;
import huehue.br.util.JdvUtils;

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
	
	JdvRedeAbstrata rede;
	
	public TelaExibicao(MLDataSet dados) {
		this(dados, new MultilayerPerceptron2());
	}
	
	public TelaExibicao(MLDataSet dados, JdvRedeAbstrata rede) {
		this.rede = rede;
		this.dados = dados;
		
		setTitle("Conjuntos de treinamentos");
		setContentPane(constroiPainel());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 400);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private Container constroiPainel() {
		JPanel principal = new JPanel();
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
		
		double[] entrada = rede.converteEntradaEmTabuleiro(par.getInput()).clone();
		int posicao = rede.traduzirSaida(par.getIdeal());
		entrada[posicao] = 2;
		
		for (int i = 0; i < 9; i++) {
			jlbs[i] = constroiCelulaTabuleiro(i);
		}
		
		return tab;
	}
	
	private JLabel constroiCelulaTabuleiro(int index) {
		JLabel celula = new JLabel(JdvUtils.Log.converteValorCaractere(index));
		
		celula.setName("" + index);
		celula.setHorizontalAlignment(JLabel.CENTER);
		celula.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
		celula.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		
		if (index > 1)
			celula.setForeground(Color.RED);
		
		return celula;
	}
	
}
