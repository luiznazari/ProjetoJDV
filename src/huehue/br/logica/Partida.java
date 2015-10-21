package huehue.br.logica;

import huehue.br.modelo.Caractere;
import huehue.br.modelo.Jogador;
import huehue.br.util.JdvUtils;

import java.util.ArrayList;
import java.util.List;

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
		jogadas[numeroJogadas] = new Jogada(entradas, posicaoEscolhida);
		entradas[posicaoEscolhida] = caractere.getValor();
		numeroJogadas++;
		
		JdvUtils.Log.partida(caractere, entradas, posicaoEscolhida);
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
		
		JdvUtils.Log.fimPartida(vencedor);
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
		List<Jogada> jogadasVencedor = new ArrayList<>();
		
		if (vencedor == null) {
			for (int i = 0; i < numeroJogadas; i++)
				jogadasVencedor.add(jogadas[i]);
			
			return jogadasVencedor;
		}
		
		for (int i = ordemJogadaVencedor; i < 9; i += 2)
			if (jogadas[i] != null)
				jogadasVencedor.add(jogadas[i]);
		
		return jogadasVencedor;
	}
	
	@Getter
	public class Jogada {
		
		final double[] configuracao;
		
		final int posicaoEscolhida;
		
		public Jogada(final double[] configuracao, final int posicaoEscolhida) {
			this.configuracao = configuracao.clone();
			this.posicaoEscolhida = posicaoEscolhida;
		}
		
	}
	
}
