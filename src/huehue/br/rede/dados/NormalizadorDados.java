package huehue.br.rede.dados;

import huehue.br.logica.Partida;
import huehue.br.logica.Partida.Jogada;
import huehue.br.modelo.Caractere;
import huehue.br.modelo.JogadorAutomato;
import huehue.br.rede.modelo.JdvRedeAbstrata;
import huehue.br.util.JdvUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe responsável pela normalização dos casos de treinamento.
 * TODO Refatorar métodos.
 * 
 * @author Luiz Felipe Nazari
 */
public class NormalizadorDados {

	private JdvRedeAbstrata rede;

	private List<Jogada> jogadasV;

	private int lenJogadasV;

	private List<Jogada> jogadasP;

	private int lenJogadasP;

	private boolean jogadorVencedorX;

	public NormalizadorDados(Partida partida, JdvRedeAbstrata rede) {
		this.rede = rede;

		this.jogadasV = partida.getJogadasVencedor();
		this.lenJogadasV = jogadasV.size();

		this.jogadasP = partida.getJogadasPerdedor();
		this.lenJogadasP = jogadasP.size();

		this.jogadorVencedorX = partida.getVencedor().getCaractere() == Caractere.X;
	}

	public List<JdvMLDataPair> criaParesJogadasVencedoras() {
		return jogadasV.stream().map(j -> criaParJogadaVencedora(j)).collect(Collectors.toList());
	}

	private JdvMLDataPair criaParJogadaVencedora(Jogada jogadaV) {
		double[] tabuleiro = validaEntradas(jogadaV.getConfiguracao(), jogadorVencedorX);

		tabuleiro[jogadaV.getPosicaoEscolhida()] = Caractere.X.getValor();
		int[] indicesX = JdvUtils.Tabuleiro.computaIndicesVencedor(tabuleiro);
		tabuleiro[jogadaV.getPosicaoEscolhida()] = Caractere.VAZIO.getValor();

		int pontos = 1;
		double delta = 1;

		switch (lenJogadasV) {
			case 3: { // Ótimo
				pontos = 3;
				delta = 0.9999;
				break;
			}
			case 4: { // Bom
				pontos = 2;
				delta = 0.8888;
				break;
			}
			case 5: { // Razoável
				pontos = 1;
				delta = 0.7777;
				break;
			}
		}

		for (int i = 0; i < tabuleiro.length; i++)
			tabuleiro[i] *= delta;

		for (int i = 0; i < indicesX.length; i++)
			if (indicesX[i] != jogadaV.getPosicaoEscolhida())
				tabuleiro[indicesX[i]] = 0.9999;

		JdvMLDataPair par = new JdvMLDataPair(rede.traduzirEntrada(tabuleiro),
				rede.convertePosicaoTabuleiroEmSaida(jogadaV.getPosicaoEscolhida()), 1, pontos);
		return par;
	}

	public JdvMLDataPair criaParJogadaBloqueio() {
		Jogada jogadaV = jogadasV.get(lenJogadasV - 1);
		Jogada jogadaP = jogadasP.get(lenJogadasP - 1);

		double[] tabuleiro = validaEntradas(jogadaP.getConfiguracao(), !jogadorVencedorX);

		tabuleiro[jogadaV.getPosicaoEscolhida()] = Caractere.X.getValor();
		int[] indicesX = JdvUtils.Tabuleiro.computaIndicesVencedor(tabuleiro);
		tabuleiro[jogadaV.getPosicaoEscolhida()] = Caractere.VAZIO.getValor();

		// Bloquear tem mais importância do que realizar uma jogada comum.
		int pontos = 4;
		double delta = 1;

		switch (lenJogadasP) {
			case 2: { // Ótimo
				delta = 0.9999;
				break;
			}
			case 3: { // Bom
				delta = 0.8888;
				break;
			}
			case 4: { // Razoável
				delta = 0.7777;
				break;
			}
		}

		for (int i = 0; i < tabuleiro.length; i++)
			tabuleiro[i] *= delta;

		for (int i = 0; i < indicesX.length; i++)
			if (indicesX[i] != jogadaV.getPosicaoEscolhida())
				tabuleiro[indicesX[i]] = 0.9999;

		JdvMLDataPair par = new JdvMLDataPair(rede.traduzirEntrada(tabuleiro),
				rede.convertePosicaoTabuleiroEmSaida(jogadaV.getPosicaoEscolhida()), 1, pontos);
		return par;
	}

	/**
	 * Valida os valores das entradas a serem computadas pelas Inteligências Artificiais.<br>
	 * Caso o {@link JogadorAutomato} possuir o {@link Caractere#O}, os valores das entradas precisarão ser ajustados,
	 * pois a rede é treinada para reconhecer o valor do {@link Caractere#X} como posição da Rede e o
	 * {@link Caractere#O} como o adversário.
	 * 
	 * @param entradas
	 *            as entradas do tabuleiro.
	 * @param jogadorX
	 *            informa se são entradas do jogador X.
	 * @return as entradas validadas do tabuleiro.
	 */
	public static double[] validaEntradas(double[] entradas, boolean jogadorX) {
		entradas = entradas.clone();

		if (!jogadorX)
			for (int i = 0; i < entradas.length; i++)
				if (entradas[i] != Caractere.VAZIO.getValor())
					entradas[i] *= -1;

		return entradas;
	}

}