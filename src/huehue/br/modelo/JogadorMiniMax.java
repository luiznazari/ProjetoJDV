package huehue.br.modelo;

import huehue.br.util.ElementoMiniMax;
import huehue.br.util.JdvUtils;
import huehue.br.util.LogicaMiniMax;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe representando um jogador com Inteligência Artificial utilizando o algoritmo MiniMax.
 * 
 * @author Luiz Felipe Nazari
 * @see LogicaMiniMax
 */
public class JogadorMiniMax extends JogadorAutomato {

	public JogadorMiniMax(Caractere caractere) {
		super(caractere);
	}

	@Override
	public int novaJogada(double[] entradas) {
		TabuleiroMiniMax tabuleiro = new TabuleiroMiniMax(super.validaEntradas(entradas));

		if (JdvUtils.Tabuleiro.computaEspacosVazios(entradas) == 9)
			return primeiraRodada();

		int[] movimentos = new int[tabuleiro.nivel];
		int movimentosIndex = 0;
		int maiorValor = -2;

		for (TabuleiroMiniMax t : tabuleiro.getFilhos()) {
			int val = LogicaMiniMax.MiniMax(t);

			if (val > maiorValor) {
				maiorValor = val;
				movimentosIndex = 0;
				movimentos[movimentosIndex++] = t.posicao;
			} else if (val == maiorValor) {
				movimentos[movimentosIndex++] = t.posicao;
			}
		}

		return movimentos[( int ) (Math.random() * --movimentosIndex)];
	}

	private int primeiraRodada() {
		int[] movimentos = new int[] { 0, 2, 4, 6, 8 };
		return movimentos[( int ) (Math.random() * movimentos.length)];
	}

	/**
	 * Implementação de um elemento MiniMax representando os estados do tabuleiro do jogo da velha.
	 * 
	 * @author Luiz Felipe Nazari
	 */
	private class TabuleiroMiniMax implements ElementoMiniMax {

		private int nivel;

		// Valor da Heurística MiniMax.
		// Quando +1 indica que é um movimento Max, que maximiza as chances de ganhar.
		// Quando -1 indica que é um movimento Min, que maximiza as chances do oponente ganhar.
		// Quando 0 indica que é um movimento que leva ao empate.
		private int pontos;

		private int posicao;

		private boolean x = true;

		private double[] tabuleiro;

		public TabuleiroMiniMax(double[] tabuleiro) {
			this(tabuleiro, true, 0);
		}

		private TabuleiroMiniMax(double[] tabuleiro, boolean x, int posicao) {
			this.x = x;
			this.posicao = posicao;
			this.tabuleiro = tabuleiro;
			this.pontos = JdvUtils.Tabuleiro.computaVencedor(this.tabuleiro);
			this.nivel = JdvUtils.Tabuleiro.computaEspacosVazios(this.tabuleiro);
		}

		private TabuleiroMiniMax constroiFilho(int posicao) {
			int caractere = this.x ? Caractere.X.getValor() : Caractere.O.getValor();
			double[] novoTabuleiro = this.tabuleiro.clone();
			novoTabuleiro[posicao] = caractere;

			return new TabuleiroMiniMax(novoTabuleiro, !this.x, posicao);
		}

		@Override
		public int getNivel() {
			return this.nivel;
		}

		@Override
		public boolean isMax() {
			return this.x;
		}

		@Override
		public int getValorHeuristica() {
			return this.pontos;
		}

		@Override
		public List<ElementoMiniMax> getElementosFilhos() {
			List<ElementoMiniMax> filhos = new ArrayList<>();

			for (int i = 0; i < this.tabuleiro.length; i++)
				if (this.tabuleiro[i] == Caractere.VAZIO.getValor())
					filhos.add(constroiFilho(i));

			return filhos;
		}

		public List<TabuleiroMiniMax> getFilhos() {
			return getElementosFilhos().stream().map(el -> ( TabuleiroMiniMax ) el).collect(Collectors.toList());
		}

		@Override
		public boolean isFimRecursao() {
			return this.nivel == 0 || this.pontos != 0;
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
