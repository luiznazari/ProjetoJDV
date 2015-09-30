package huehue.br.tela;

import huehue.br.logica.Tabuleiro;
import huehue.br.modelo.Caractere;
import huehue.br.tela.evento.CelulaTabuleiroEventos;
import huehue.br.tela.evento.TabuleiroListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import lombok.Getter;

public class TelaTabuleiro implements TabuleiroListener {
	
	private JPanel pnlTabuleiro;
	
	private JLabel[] jlbCelulas;
	
	@Getter
	private Tabuleiro tabuleiro;
	
	public TelaTabuleiro() {
		tabuleiro = new Tabuleiro(this);
	}
	
	public void novaPartida() {
		tabuleiro.novaPartida();
		
		for (int i = 0; i < 9; i++)
			setPosicao(i, Caractere.VAZIO);
		
		tabuleiro.encadeiaJogada(tabuleiro.getJogadorDaVez());
	}
	
	public void fimPartida(String mensagem) {
		if (tabuleiro.isTemJogadorHumano())
			JOptionPane.showMessageDialog(null, mensagem);
		novaPartida();
	}
	
	@Override
	public void novaJogadaClique(JLabel jLabel) {
		if (jLabel.getText().equals(Caractere.VAZIO.getChave())) {
			Integer posicaoClicada = Integer.valueOf(jLabel.getName());
			
			tabuleiro.novaJogada(posicaoClicada);
		}
	}
	
	public void setPosicao(int pos, Caractere caractere) {
		jlbCelulas[pos].setText(caractere.getChave());
	}
	
	/**
	 * Busca a configuração das posições do tabuleiro atual.
	 * 
	 * @return array contendo os valores das posições do tabuleiro atual.
	 */
	public double[] getPosicoesTabuleiro() {
		double[] posicoesAtuais = new double[9];
		
		int i = 0;
		for (JLabel jbl : jlbCelulas) {
			posicoesAtuais[i] = convertePosicaoParaDouble(jbl.getText());
			i++;
		}
		
		return posicoesAtuais;
	}
	
	/**
	 * Converte a {@link String} em um {@link Caractere} e retorna seu respectivo valor.
	 * 
	 * @param s
	 * @return valor do caractere representando a String.
	 */
	private int convertePosicaoParaDouble(String s) {
		return Caractere.get(s).getValor();
	}
	
	public JPanel getPainel() {
		if (pnlTabuleiro == null) {
			pnlTabuleiro = new JPanel(new GridLayout(3, 3));
			
			jlbCelulas = new JLabel[9];
			for (int i = 0; i < 9; i++) {
				jlbCelulas[i] = constroiCelulaTabuleiro(i);
				pnlTabuleiro.add(jlbCelulas[i]);
			}
		}
		
		return pnlTabuleiro;
	}
	
	private JLabel constroiCelulaTabuleiro(int index) {
		JLabel celula = new JLabel(Caractere.VAZIO.getChave());
		
		celula.setName("" + index);
		celula.setSize(40, 40);
		celula.setMinimumSize(new Dimension(40, 40));
		celula.setHorizontalAlignment(JLabel.CENTER);
		celula.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 100));
		celula.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		
		celula.addMouseListener(new CelulaTabuleiroEventos(TelaTabuleiro.this));
		
		return celula;
	}
	
}
