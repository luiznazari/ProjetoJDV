package huehue.br.modelo;

import huehue.br.exception.VeiaException;
import huehue.br.tela.TelaTabuleiro;
import huehue.br.util.VeiaUtil;
import lombok.Getter;

public class Tabuleiro {
	
	private Partida partida;
	
	private TelaTabuleiro tela;
	
	@Getter
	private Jogador jogadorUm;
	
	@Getter
	private Jogador jogadorDois;
	
	public Tabuleiro(TelaTabuleiro tela) {
		this.tela = tela;
		novaPartida();
		
//		setJogadorUm(new JogadorRNA(Caractere.X));
		setJogadorUm(new JogadorHumano(Caractere.X));
//		setJogadorUm(new JogadorAleatorio(Caractere.X));
		
		setJogadorDois(new JogadorRNA(Caractere.O));
//		setJogadorDois(new JogadorHumano(Caractere.O));
//		setJogadorDois(new JogadorAleatorio(Caractere.O));
	}
	
	public void novaJogada(Integer posicaoEscolhida) {
		double[] cfgTabuleiroAntiga = tela.getPosicoesTabuleiro();
		tela.setPosicao(posicaoEscolhida, getJogadorDaVez().getCaractere());
		
		// Jogada é incrementada e o jogador da vez é alterado.
		partida.novaJogada(cfgTabuleiroAntiga, posicaoEscolhida);
		
		Jogador vencedor = computaJogada();
		if (vencedor != null || partida.getNumeroJogadas() >= 9) {
			fimPartida(vencedor);
			return;
		}
		
		encadeiaJogada(getJogadorDaVez());
	}
	
	public void encadeiaJogada(Jogador jogador) {
		if (jogador instanceof JogadorAutomato) {
			int posicaoEscolhida = jogador.novaJogada(tela.getPosicoesTabuleiro());
			System.out.println("Posição escolida: " + posicaoEscolhida);
			
			novaJogada(posicaoEscolhida);
		} else {
			System.out.println("Esperando movimento do jogador...");
		}
	}
	
	public void fimPartida(Jogador vencedor) {
		String mensagemFinal;
		
		if (vencedor != null) {
			vencedor.pontuar();
			mensagemFinal = "O jogador " + vencedor.getCaractere().getChave() + " venceu!"
			        + "\nPontuação atual: " + vencedor.getPontuacao();
		} else {
			mensagemFinal = "Empate !";
		}
		
		partida.encerrar(vencedor);
		notificarJogadores();
		tela.fimPartida(mensagemFinal);
	}
	
	private void notificarJogadores() {
		if (jogadorUm instanceof JogadorAutomato)
			(( JogadorAutomato ) jogadorUm).notificarResultado(partida);
		
		if (jogadorDois instanceof JogadorAutomato)
			(( JogadorAutomato ) jogadorDois).notificarResultado(partida);
	}
	
	public Jogador getJogadorDaVez() {
		if (partida.isPartidaPar())
			return jogadorDois;
		return jogadorUm;
	}
	
	private Jogador computaJogada() {
		if (partida.getNumeroJogadas() > 5) {
			double valorVencedor = VeiaUtil.Tabuleiro.computaVencedor(tela.getPosicoesTabuleiro());
			
			if (valorVencedor == jogadorUm.getCaractere().getValor())
				return jogadorUm;
			if (valorVencedor == jogadorDois.getCaractere().getValor())
				return jogadorDois;
		}
		
		return null;
	}
	
	public void setJogadorUm(Jogador jogadorUm) {
		if (jogadorDois != null && jogadorDois.getCaractere().equals(jogadorUm.getCaractere()))
			throw new VeiaException(
			        "Caractere " + jogadorDois.getCaractere() + " já está sendo utilizado por outro jogador!");
		
		this.jogadorUm = jogadorUm;
	}
	
	public void setJogadorDois(Jogador jogadorDois) {
		if (jogadorUm != null && jogadorUm.getCaractere().equals(jogadorDois.getCaractere()))
			throw new VeiaException(
			        "Caractere " + jogadorDois.getCaractere() + " já está sendo utilizado por outro jogador!");
		
		this.jogadorDois = jogadorDois;
	}
	
	public void novaPartida() {
		partida = new Partida();
	}
	
}
