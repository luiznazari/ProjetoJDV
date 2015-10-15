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
		return 0;
	}
	
	static TabuleiroMinMax _tabuleiro;
	
	public static int
			MiniMaxShortVersion(int depth, int alpha, int beta, TabuleiroMinMax tabuleiro) {
		boolean primeiro = tabuleiro == _tabuleiro;
		
		if (depth == 0 || tabuleiro.isFimDeJogo()) {
			// When it is turn for PlayO, we need to find the minimum score.
			_tabuleiro.pontosRecursivos = tabuleiro.pontos;
			return tabuleiro.x ? tabuleiro.pontos : -tabuleiro.pontos;
		}
		
		for (TabuleiroMinMax t : tabuleiro.filhos()) {
			int score = -MiniMaxShortVersion(depth - 1, -beta, -alpha, t);
			
			if (alpha < score) {
				alpha = score;
				
				tabuleiro = t;
				
				if (alpha >= beta) {
					break;
				}
			}
		}
		
		if (primeiro)
			_tabuleiro = tabuleiro;
		
		_tabuleiro.pontosRecursivos = alpha;
		return alpha;
	}
	
	public static int MiniMax(int depth, boolean needMax, int alpha, int beta,
			TabuleiroMinMax tabuleiro) {
		
		boolean primeiro = tabuleiro == _tabuleiro;
		
//	    System.Diagnostics.Debug.Assert(m_TurnForPlayerX == needMax);
		
		if (depth == 0 || tabuleiro.isFimDeJogo()) {
			_tabuleiro.pontosRecursivos = tabuleiro.pontos;
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
		
		_tabuleiro.pontosRecursivos = needMax ? alpha : beta;
		return _tabuleiro.pontosRecursivos;
	}
	
	public static void main(String[] args) {
		JdvRedeAbstrata rede = new MultilayerPerceptron();
		MLDataPair par = new BasicMLDataPair(new BasicMLData(new double[] {
			1, 0, -1, 0, 0, 0, -1, 0, 1
		}), new BasicMLData(new double[] {
			0.4
		}));
		
		_tabuleiro = new TabuleiroMinMax(par.getInputArray());
		MiniMaxShortVersion(8, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, _tabuleiro);
		System.out.println(_tabuleiro);
		
		_tabuleiro = new TabuleiroMinMax(par.getInputArray());
		MiniMax(8, _tabuleiro.x, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1, _tabuleiro);
		System.out.println(_tabuleiro);
		
		JdvUtils.Log.resultado(par, rede.convertePosicaoTabuleiroEmSaida(1), rede);
	}
	
	private static class TabuleiroMinMax {
		
		int pontos;
		
		int pontosRecursivos;
		
		boolean x = true;
		
		double[] tabuleiro;
		
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
		
		private double[] filho(int posicao) {
			double[] tabuleiroFilho = this.get().clone();
			tabuleiroFilho[posicao] = x ? 1 : -1;
			
			return tabuleiroFilho;
		}
		
		public List<TabuleiroMinMax> filhos() {
			List<TabuleiroMinMax> filhos = new ArrayList<>();
			
			for (int i = 0; i < this.tabuleiro.length; i++)
				if (this.tabuleiro[i] == Caractere.VAZIO.getValor())
					filhos.add(new TabuleiroMinMax(filho(i), !x));
			
			return filhos;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			sb.append("[");
			for (double d : tabuleiro)
				sb.append(d).append(", ");
			sb.append("]");
			
			return sb.toString().replaceAll(",\\s]", "]");
		}
		
	}
}
