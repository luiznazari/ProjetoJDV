package huehue.br.logica;

import huehue.br.exception.JdvException;
import huehue.br.modelo.Caractere;
import huehue.br.modelo.Jogador;
import huehue.br.modelo.JogadorAleatorio;
import huehue.br.modelo.JogadorAutomato;
import huehue.br.modelo.JogadorRNA;
import huehue.br.tela.TelaTabuleiro;
import huehue.br.util.JdvUtils;
import lombok.Getter;

/**
 * Classe responsável pelo gerenciamento de jogadores e partidas do jogo da velha, assim como o
 * controle das lógicas envolvidas do jogo.
 * 
 * @author Luiz Felipe Nazari
 */
public class Tabuleiro {
	
	private Partida partida;
	
	private TelaTabuleiro tela;
	
	@Getter
	private Jogador jogadorUm;
	
	@Getter
	private Jogador jogadorDois;
	
	public Tabuleiro(TelaTabuleiro tela) {
		this.tela = tela;
		partida = new Partida();
		
		setJogadorUm(new JogadorRNA(Caractere.X));
//		setJogadorUm(new JogadorHumano(Caractere.X));
//		setJogadorUm(new JogadorAleatorio(Caractere.X));
		
//		setJogadorDois(new JogadorRNA(Caractere.O));
//		setJogadorDois(new JogadorHumano(Caractere.O));
		setJogadorDois(new JogadorAleatorio(Caractere.O));
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
		if (partida.getNumeroJogadas() > 4) {
			double valorVencedor = JdvUtils.Tabuleiro.computaVencedor(tela.getPosicoesTabuleiro());
			
			if (valorVencedor == jogadorUm.getCaractere().getValor())
				return jogadorUm;
			if (valorVencedor == jogadorDois.getCaractere().getValor())
				return jogadorDois;
		}
		
		return null;
	}
	
	public void setJogadorUm(Jogador jogadorUm) {
		if (jogadorDois != null && jogadorDois.getCaractere().equals(jogadorUm.getCaractere()))
			throw new JdvException(
					"Caractere "
						+ jogadorDois.getCaractere()
						+ " já está sendo utilizado po routro jogador!");
		
		this.jogadorUm = jogadorUm;
	}
	
	public void setJogadorDois(Jogador jogadorDois) {
		if (jogadorUm != null && jogadorUm.getCaractere().equals(jogadorDois.getCaractere()))
			throw new JdvException(
					"Caractere "
						+ jogadorDois.getCaractere()
						+ "já está sendo utilizado po routro jogador!");
		
		this.jogadorDois = jogadorDois;
	}
	
	public void novaPartida() {
		partida.resetaJogadas();
	}
	
}
