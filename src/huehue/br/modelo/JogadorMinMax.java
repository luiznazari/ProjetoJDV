package huehue.br.modelo;

import huehue.br.util.JdvUtils;
import huehue.br.util.LogicaMiniMax;

import java.util.ArrayList;
import java.util.List;

public class JogadorMinMax extends JogadorAutomato {
	
	public JogadorMinMax(Caractere caractere) {
		super(caractere);
	}
	
	@Override
	public int novaJogada(double[] entradas) {
		TabuleiroMinMax tabuleiro = new TabuleiroMinMax(entradas, getCaractere() == Caractere.X);
		List<Integer> movimentos = new ArrayList<>();
		int v = -2;
		
		for (TabuleiroMinMax t : tabuleiro.filhos()) {
			int val = LogicaMiniMax.MiniMaxReduzido(t);
			
			if (val > v) {
				v = val;
				movimentos.clear();
				movimentos.add(t.posicao);
			} else if (val == v) {
				movimentos.add(t.posicao);
			}
			
		}
		
		int escolhido = ( int ) (Math.random() * movimentos.size());
		return movimentos.get(escolhido);
	}
	
	public static class TabuleiroMinMax {
		// Valor da Heurística MiniMax.
		// Quando +1 indica que é um movimento Max, que maximiza as chances de ganhar.
		// Quando -1 indica que é um movimento Min, que maximiza as chances do oponente ganhar.
		// Quando 0 indica que é um movimento que leva ao empate.
		public int pontos;
		
		public int posicao;
		
		public boolean x = true;
		
		private double[] tabuleiro;
		
		public TabuleiroMinMax(double[] tabuleiro, boolean x) {
			this(tabuleiro, x, 0);
		}
		
		public TabuleiroMinMax(double[] tabuleiro, boolean x, int posicao) {
			this.x = x;
			this.posicao = posicao;
			this.tabuleiro = tabuleiro;
			this.pontos = ( int ) JdvUtils.Tabuleiro.computaVencedor(this.tabuleiro);
		}
		
		public boolean isFimDeJogo() {
			return this.pontos != 0 || JdvUtils.Tabuleiro.isCompleto(this.tabuleiro);
		}
		
		public double[] get() {
			return tabuleiro;
		}
		
		private TabuleiroMinMax filho(int posicao) {
			int caractere = x ? Caractere.X.getValor() : Caractere.O.getValor();
			double[] novo = this.get().clone();
			novo[posicao] = caractere;
			
			TabuleiroMinMax tabuleiroFilho = new TabuleiroMinMax(novo, !x, posicao);
			return tabuleiroFilho;
		}
		
		public List<TabuleiroMinMax> filhos() {
			List<TabuleiroMinMax> filhos = new ArrayList<>();
			
			for (int i = 0; i < this.tabuleiro.length; i++)
				if (this.tabuleiro[i] == Caractere.VAZIO.getValor())
					filhos.add(filho(i));
			
			return filhos;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			sb.append("[");
			for (double d : this.tabuleiro)
				sb.append(d).append(", ");
			sb.append("] Posição: ").append(this.posicao);
			
			return sb.toString().replaceAll(",\\s]", "]");
		}
		
	}
}
