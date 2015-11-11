package huehue.br.tela;

import huehue.br.logica.Partida.Jogada;
import huehue.br.rede.modelo.JdvRedeAbstrata;
import huehue.br.rede.modelo.MultilayerPerceptron2;
import huehue.br.util.JdvLog;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;

/**
 * Utilizado para mostrar tabuleiros em janelas.
 * Não, essa classe <b>não</b> está otimizada. ;)
 * 
 * @author Luiz Felipe Nazari
 */
public class TelaExibicao extends JFrame {
	private static final long serialVersionUID = 4724495342867849916L;

	List<JPanel> pnlTab;

	MLDataSet dados;

	JdvRedeAbstrata rede;

	List<Jogada> jogadas;

	public TelaExibicao(MLDataSet dados) {
		this(dados, new MultilayerPerceptron2());
	}

	public TelaExibicao(MLDataSet dados, JdvRedeAbstrata rede) {
		this(dados, rede, null);
	}

	public TelaExibicao(Jogada[] jogadas) {
		this(null, null, jogadas);
	}

	private TelaExibicao(MLDataSet dados, JdvRedeAbstrata rede, Jogada[] jogadas) {
		this.rede = rede;
		this.dados = dados;
		if (jogadas != null) {
			this.jogadas = new ArrayList<>();
			for (Jogada j : jogadas) {
				if (j == null)
					break;
				this.jogadas.add(j);
			}
		}

		setTitle("Conjuntos de treinamentos");
		setContentPane(constroiPainel());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 400);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private Container constroiPainel() {
		JPanel principal = new JPanel(new FlowLayout(FlowLayout.CENTER));
		principal.setBackground(Color.WHITE);

		int componentes = 0;

		if (dados != null) {
			componentes = dados.size();
			for (MLDataPair par : dados)
				principal.add(constroiPainelTabuleiro(par));

		} else {
			componentes = jogadas.size();
			for (Jogada j : jogadas)
				principal.add(constroiPainelTabuleiro(j));
		}

		principal.setPreferredSize(new Dimension(360, componentes / 5 * 40));

		JScrollPane jsp = new JScrollPane(principal);
		jsp.setAutoscrolls(true);

		return jsp;
	}

	private JPanel constroiPainelTabuleiro(MLDataPair par) {
		JPanel tab = new JPanel(new GridLayout(3, 3));
		tab.setSize(40, 40);

		double[] entrada = rede.converteEntradaEmTabuleiro(par.getInput()).clone();
		int posicao = rede.traduzirSaida(par.getIdeal());
		entrada[posicao] = 2;

		for (int i = 0; i < 9; i++)
			tab.add(constroiCelulaTabuleiro(( int ) entrada[i], entrada[i] == 2));

		return tab;
	}

	private JPanel constroiPainelTabuleiro(Jogada j) {
		JPanel tab = new JPanel(new GridLayout(3, 3));
		tab.setSize(40, 40);

		double[] entrada = j.getConfiguracao().clone();
		entrada[j.getPosicaoEscolhida()] = j.getCaractere().getValor();

		for (int i = 0; i < 9; i++)
			tab.add(constroiCelulaTabuleiro(( int ) entrada[i], i == j.getPosicaoEscolhida()));

		return tab;
	}

	private JLabel constroiCelulaTabuleiro(int caractere, boolean posicaoEscolhida) {
		JLabel celula = new JLabel(" " + JdvLog.converteValorCaractere(caractere) + " ");

		celula.setHorizontalAlignment(JLabel.CENTER);
		celula.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
		celula.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		if (posicaoEscolhida)
			celula.setForeground(Color.RED);

		return celula;
	}

}
