package huehue.br.logica;

import huehue.br.modelo.Jogador;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
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
	
	public void novaJogada(double[] entradas, int posicaoEscolhida) {
		jogadas[numeroJogadas] = new Jogada(entradas, posicaoEscolhida);
		numeroJogadas++;
	}
	
	public void encerrar(Jogador vencedor) {
		this.vencedor = vencedor;
		ordemJogadaVencedor = (numeroJogadas - 1) % 2;
	}
	
	public List<Jogada> getJogadasVencedor() {
		List<Jogada> jogadasVencedor = new ArrayList<>();
		
		if (vencedor == null)
			return jogadasVencedor;
		
		for (int i = ordemJogadaVencedor; i < 9; i += 2)
			if (jogadas[i] != null)
				jogadasVencedor.add(jogadas[i]);
		
		return jogadasVencedor;
	}
	
	public void novaPartida() {
		numeroPartidas++;
		
		jogadas = new Jogada[9];
		numeroJogadas = 0;
	}
	
	public boolean isPartidaPar() {
		return (numeroPartidas + numeroJogadas) % 2 == 0;
	}
	
	@Getter
	@AllArgsConstructor
	public class Jogada {
		
		double[] configuracao;
		
		int posicaoEscolhida;
		
	}
	
}
