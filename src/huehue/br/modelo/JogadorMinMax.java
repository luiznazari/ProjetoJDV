package huehue.br.modelo;

import huehue.br.rede.modelo.JdvRedeAbstrata;
import huehue.br.rede.modelo.MultilayerPerceptron;
import huehue.br.util.JdvUtils;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;

public class JogadorMinMax extends JogadorAutomato {
	
	public JogadorMinMax(Caractere caractere) {
		super(caractere);
	}
	
	@Override
	public int novaJogada(double[] entradas) {
		TabuleiroMinMax tabuleiro = new TabuleiroMinMax(entradas, getCaractere() == Caractere.X);
		tabuleiro = MiniMaxShortVersion(8, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, tabuleiro);
		
		_tabuleiro = new TabuleiroMinMax(entradas, getCaractere() == Caractere.X);
		MiniMax(8, true, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, _tabuleiro);
		
		return tabuleiro.posicao;
	}
	
	private static TabuleiroMinMax MiniMaxShortVersion(int depth, int alpha, int beta,
			TabuleiroMinMax tabuleiro) {
		if (depth == 0 || tabuleiro.isFimDeJogo()) {
			// TabuleiroMinMax.pontosRecursivos = tabuleiro.pontos;
			
			// Quando for a vez do Caractere.O, é preciso encontrar a pontuação mínima.
			if (!tabuleiro.x)
				tabuleiro.pontos *= -1;
			
			return tabuleiro;
		}
		
		for (TabuleiroMinMax t : tabuleiro.filhos()) {
			t = MiniMaxShortVersion(depth - 1, -beta, -alpha, t);
			t.pontos = -t.pontos;
			
			if (alpha < t.pontos) {
				alpha = t.pontos;
				
				tabuleiro = t;
				
				if (alpha >= beta)
					break;
			}
		}
		
//		TabuleiroMinMax.pontosRecursivos = alpha;
		tabuleiro.pontos = alpha;
		return tabuleiro;
	}
	
	private static TabuleiroMinMax MiniMax(int depth, int alpha, int beta,
			TabuleiroMinMax tabuleiro) {
		boolean needMax = tabuleiro.x;
		
		if (depth == 0 || tabuleiro.isFimDeJogo()) {
			// TabuleiroMinMax.pontosRecursivos = tabuleiro.pontos;
			return tabuleiro;
		}
		
		for (TabuleiroMinMax t : tabuleiro.filhos()) {
			t = MiniMax(depth - 1, alpha, beta, t);
			
			if (!needMax) {
				if (beta > t.pontos) {
					beta = t.pontos;
					
					tabuleiro = t;
					
					if (alpha >= beta)
						break;
				}
			} else {
				if (alpha < t.pontos) {
					alpha = t.pontos;
					
					tabuleiro = t;
					
					if (alpha >= beta)
						break;
				}
			}
		}
		
		tabuleiro.pontos = needMax ? alpha : beta;
		// TabuleiroMinMax.pontosRecursivos = tabuleiro.pontos;
		return tabuleiro;
	}
	
	@Deprecated
	static TabuleiroMinMax _tabuleiro;
	
	@Deprecated
	public static int MiniMax(int depth, boolean needMax, int alpha, int beta,
			TabuleiroMinMax tabuleiro) {
		
		boolean primeiro = tabuleiro == _tabuleiro;
		
//	    System.Diagnostics.Debug.Assert(m_TurnForPlayerX == needMax);
		
		if (depth == 0 || tabuleiro.isFimDeJogo()) {
			TabuleiroMinMax.pontosRecursivos = tabuleiro.pontos;
			return tabuleiro.pontos;
		}
		
		for (TabuleiroMinMax t : tabuleiro.filhos()) {
			int score = MiniMax(depth - 1, !needMax, alpha, beta, t);
			
			if (!needMax) {
				if (beta > score) {
					beta = score;
					
					tabuleiro = t;
					
					if (alpha >= beta) {
						break;
					}
				}
			} else {
				if (alpha < score) {
					alpha = score;
					
					tabuleiro = t;
					
					if (alpha >= beta) {
						break;
					}
				}
			}
		}
		
		if (primeiro)
			_tabuleiro = tabuleiro;
		
		TabuleiroMinMax.pontosRecursivos = needMax ? alpha : beta;
		return TabuleiroMinMax.pontosRecursivos;
	}
	
	public static void main(String[] args) {
		JdvRedeAbstrata rede = new MultilayerPerceptron();
		MLDataPair par = new BasicMLDataPair(new BasicMLData(new double[] {
			0, 0, 0,
			0, 0, 0,
			0, 0, 0
		}), new BasicMLData(new double[] {
			0.2
		}));
		
		TabuleiroMinMax tabuleiro = new TabuleiroMinMax(par.getInputArray());
		tabuleiro = MiniMaxShortVersion(8, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, tabuleiro);
		JdvUtils.Log.resultado(par, rede.convertePosicaoTabuleiroEmSaida(tabuleiro.posicao), rede);
		
		tabuleiro = new TabuleiroMinMax(par.getInputArray());
		tabuleiro = MiniMax(8, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, tabuleiro);
		JdvUtils.Log.resultado(par, rede.convertePosicaoTabuleiroEmSaida(tabuleiro.posicao), rede);
	}
	
	private static class TabuleiroMinMax {
		
		// Utilizado no algoritmo MiniMax.
		private static int pontosRecursivos;
		
		// Valor da Heurística MiniMax.
		// Quando +1 indica que é um movimento Max, que maximiza as chances de ganhar.
		// Quando -1 indica que é um movimento Min, que maximiza as chances do oponente ganhar.
		private int pontos;
		
		private int posicao;
		
		private boolean x = true;
		
		private double[] tabuleiro;
		
		public TabuleiroMinMax(double[] tabuleiro) {
			this.tabuleiro = tabuleiro;
			
			this.pontos = ( int ) JdvUtils.Tabuleiro.computaVencedor(this.tabuleiro);
		}
		
		public boolean isFimDeJogo() {
			return this.pontos != 0 || JdvUtils.Tabuleiro.isCompleto(this.tabuleiro);
		}
		
		public TabuleiroMinMax(double[] tabuleiro, boolean x) {
			this(tabuleiro);
			this.x = x;
		}
		
		public double[] get() {
			return tabuleiro;
		}
		
		private void marcaPosicao(int posicao, int caractere) {
			this.posicao = posicao;
			this.tabuleiro[posicao] = caractere;
		}
		
		private TabuleiroMinMax filho(int posicao) {
			TabuleiroMinMax tabuleiroFilho = new TabuleiroMinMax(this.get().clone(), !x);
			int caractere = x ? Caractere.X.getValor() : Caractere.O.getValor();
			
			tabuleiroFilho.marcaPosicao(posicao, caractere);
			
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
			sb.append("] Computado: ").append(this.posicao);
			
			return sb.toString().replaceAll(",\\s]", "]");
		}
		
	}
}
