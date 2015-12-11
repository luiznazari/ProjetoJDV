package br.edu.unoesc.logica;

import java.util.ArrayList;
import java.util.List;

import br.edu.unoesc.modelo.Caractere;
import br.edu.unoesc.modelo.Jogador;
import br.edu.unoesc.util.JdvLog;
import br.edu.unoesc.util.JdvUtils;

import lombok.Getter;

/**
 * Classe responsável pelo controle das partidas, ordem dos jogadores e armazenar todas as jogadas
 * realizadas.
 * 
 * @author Luiz Felipe Nazari
 */
public class Partida {

	@Getter
	private Integer numeroPartidas = -1;

	@Getter
	private Integer numeroJogadas;

	@Getter
	private Jogada[] jogadas;

	@Getter
	private Jogador vencedor;

	// Indica se o vencedor foi o primeiro ou o segundo a começar a jogar.
	private Integer ordemJogadaVencedor;

	public Partida() {
		novaPartida();
	}

	public void novaJogada(Caractere caractere, double[] entradas, int posicaoEscolhida) {
		jogadas[numeroJogadas] = new Jogada(caractere, entradas, posicaoEscolhida);
		entradas[posicaoEscolhida] = caractere.getValor();
		numeroJogadas++;

		JdvLog.partida(caractere, entradas, posicaoEscolhida);
	}

	public void novaPartida() {
		numeroPartidas++;

		jogadas = new Jogada[9];
		numeroJogadas = 0;
	}

	public Jogador temVencedor(final double[] tabuleiro, Jogador um, Jogador dois) {
		if (getNumeroJogadas() <= 4)
			return null;

		return JdvUtils.Tabuleiro.computaVencedor(tabuleiro, um, dois);
	}

	public void encerrar(Jogador vencedor) {
		this.vencedor = vencedor;
		ordemJogadaVencedor = (numeroJogadas - 1) % 2;

		JdvLog.fimPartida(vencedor);
	}

	public boolean isJogadaPar() {
		return (numeroPartidas + numeroJogadas) % 2 == 0;
	}

	/**
	 * Recupera o histórico de jogadas realizadas pelo jogador vencedor ou todo o histórico da
	 * partida caso não haja um vencedor.
	 * 
	 * @return lista de jogadas.
	 */
	public List<Jogada> getJogadasVencedor() {
		return getJogadasJogador(ordemJogadaVencedor);
	}

	/**
	 * Recupera o histórico de jogadas realizadas pelo jogador que perdeu a partida ou todo o histórico da partida caso
	 * não haja um vencedor.
	 * 
	 * @return lista de jogadas.
	 */
	public List<Jogada> getJogadasPerdedor() {
		return getJogadasJogador(ordemJogadaVencedor == 0 ? 1 : 0);
	}

	private List<Jogada> getJogadasJogador(int ordem) {
		List<Jogada> jogadasVencedor = new ArrayList<>();

		if (vencedor == null) {
			for (int i = 0; i < numeroJogadas; i++)
				jogadasVencedor.add(jogadas[i]);

			return jogadasVencedor;
		}

		for (int i = ordem; i < 9; i += 2)
			if (jogadas[i] != null)
				jogadasVencedor.add(jogadas[i]);

		return jogadasVencedor;
	}

	@Getter
	public class Jogada {

		final double[] configuracao;

		final int posicaoEscolhida;

		final Caractere caractere;

		public Jogada(final Caractere caractere, final double[] configuracao, final int posicaoEscolhida) {
			this.configuracao = configuracao.clone();
			this.posicaoEscolhida = posicaoEscolhida;
			this.caractere = caractere;
		}

	}

}
