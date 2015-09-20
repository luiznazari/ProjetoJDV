package huehue.br.modelo;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Classe responsável pelo controle das partidas, ordem dos jogadores e armazenar todas as jogadas
 * realizadas.
 * 
 * @author Luiz Felipe Nazari
 */
public class Partida {
	
	@Getter
	private Integer numeroJogadas;
	
	private Integer primeiroAJogar;
	
	@Getter
	private Jogada[] jogadas;
	
	@Getter
	private Jogador vencedor;
	
	// Indica se o vencedor foi o primeiro ou o segundo a começar a jogar.
	private Integer ordemJogadaVencedor;
	
	public Partida() {
		primeiroAJogar = 1;
		
		resetaJogadas();
	}
	
	public void novaJogada(double[] entradas, int posicaoEscolhida) {
		jogadas[numeroJogadas] = new Jogada(entradas, posicaoEscolhida);
		System.out.println(jogadas[numeroJogadas]);
		
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
	
	public void resetaJogadas() {
		jogadas = new Jogada[9];
		numeroJogadas = 0;
		primeiroAJogar++;
	}
	
	public boolean isPartidaPar() {
		return (primeiroAJogar + numeroJogadas) % 2 == 0;
	}
	
	@Getter
	@AllArgsConstructor
	@ToString(of = {
	    "configuracao", "posicaoEscolhida"
	}, callSuper = false)
	public class Jogada {
		
		double[] configuracao;
		
		int posicaoEscolhida;
		
	}
	
}
