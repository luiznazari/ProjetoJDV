package huehue.br.logica;

import huehue.br.exception.JdvException;
import huehue.br.modelo.Caractere;
import huehue.br.modelo.Jogador;
import huehue.br.modelo.JogadorAutomato;
import huehue.br.modelo.JogadorHumano;
import huehue.br.modelo.JogadorRNA;
import huehue.br.rede.modelo.MultilayerPerceptron3;
import huehue.br.tela.TelaExibicao;
import huehue.br.tela.TelaTabuleiro;
import huehue.br.util.JdvLog;
import huehue.br.util.JdvUtils;
import lombok.Getter;

/**
 * Classe responsável pelo gerenciamento de jogadores e partidas do jogo da velha, assim como o
 * controle das lógicas envolvidas do jogo.
 * 
 * @author Luiz Felipe Nazari
 */
public class Tabuleiro {

	// Variável apenas com controle via código, indica se as partidas serão salvas em arquivos.
	boolean salvarPartidas = false;

	@Getter
	private Partida partida;

	private TelaTabuleiro tela;

	@Getter
	private Jogador jogadorUm;

	@Getter
	private Jogador jogadorDois;

	@Getter
	boolean temJogadorHumano;

	public Tabuleiro(TelaTabuleiro tela) {
		this.tela = tela;
		partida = new Partida();

		JdvUtils.Arquivo.versionamento(1);

		setJogadorUm(new JogadorRNA(Caractere.X, new MultilayerPerceptron3("delete_me"), true));
//		setJogadorUm(new JogadorRNA(Caractere.X, false));
//		setJogadorUm(new JogadorHumano(Caractere.X));
//		setJogadorUm(new JogadorAleatorio(Caractere.X));
//		setJogadorUm(new JogadorMiniMax(Caractere.X));

//		setJogadorDois(new JogadorRNA(Caractere.O, new MultilayerPerceptron3("delete_me"), true));
//		setJogadorDois(new JogadorRNA(Caractere.O, false));
		setJogadorDois(new JogadorHumano(Caractere.O));
//		setJogadorDois(new JogadorAleatorio(Caractere.O));
//		setJogadorDois(new JogadorMiniMax(Caractere.O));

		JdvUtils.Arquivo.incrementaVersao();

		temJogadorHumano = jogadorUm instanceof JogadorHumano || jogadorDois instanceof JogadorHumano;
	}

	public void novaJogada(Integer posicaoEscolhida) {
		double[] cfgTabuleiro = tela.getPosicoesTabuleiro();
		Caractere caractere = getJogadorDaVez().getCaractere();
		tela.setPosicao(posicaoEscolhida, caractere);

		// Jogada é incrementada e o jogador da vez é alterado.
		partida.novaJogada(caractere, cfgTabuleiro, posicaoEscolhida);

		Jogador vencedor = partida.temVencedor(cfgTabuleiro, jogadorUm, jogadorDois);
		if (vencedor != null || partida.getNumeroJogadas() >= 9) {
			fimPartida(vencedor);
			return;
		}

		encadeiaJogada();
	}

	public void encadeiaJogada() {
		Jogador jogador = getJogadorDaVez();

		if (jogador instanceof JogadorAutomato) {
			int posicaoEscolhida = jogador.novaJogada(tela.getPosicoesTabuleiro());
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

		if (salvarPartidas)
			mostraPartidas();

		partida.encerrar(vencedor);
		notificarJogadores();
		tela.fimPartida(mensagemFinal);
	}

	private void mostraPartidas() {
		new TelaExibicao(partida.getJogadas());
	}

	private void notificarJogadores() {
		if (jogadorUm instanceof JogadorAutomato)
			(( JogadorAutomato ) jogadorUm).notificarResultado(partida);

		if (jogadorDois instanceof JogadorAutomato)
			(( JogadorAutomato ) jogadorDois).notificarResultado(partida);
	}

	public Jogador getJogadorDaVez() {
		if (partida.isJogadaPar())
			return jogadorDois;
		return jogadorUm;
	}

	public void setJogadorUm(Jogador jogadorUm) {
		if (jogadorDois != null && jogadorDois.getCaractere().equals(jogadorUm.getCaractere()))
			throw new JdvException("Caractere " + jogadorDois.getCaractere()
					+ " já está sendo utilizado por outro jogador!");

		this.jogadorUm = jogadorUm;
	}

	public void setJogadorDois(Jogador jogadorDois) {
		if (jogadorUm != null && jogadorUm.getCaractere().equals(jogadorDois.getCaractere()))
			throw new JdvException("Caractere " + jogadorDois.getCaractere()
					+ " já está sendo utilizado por outro jogador!");

		this.jogadorDois = jogadorDois;
	}

	public void novaPartida() {
		partida.novaPartida();
		encadeiaJogada();
	}

	public String getPlacar() {
		return JdvLog.placar(getPartida().getNumeroPartidas() - 1, getJogadorUm(), getJogadorDois());
	}

}
