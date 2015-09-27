package huehue.br.modelo;

import huehue.br.logica.Partida;
import huehue.br.modelo.rede.JdvRedeAbstrata;
import huehue.br.modelo.rede.MultilayerPerceptron2;
import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.util.JdvUtils;

/**
 * Classe representando um jogador com Inteligência Artificial utilizando uma
 * Rede Neural Artificial.
 * 
 * @author Luiz Felipe Nazari
 */
public class JogadorRNA extends JogadorAutomato {
	
	private JdvRedeAbstrata rede;
	
	// Conjuntos de dados conhecidos pela rede e utilizados no treinamento.
	private ConjuntosDados dados;
	
	public JogadorRNA(Caractere caractere) {
		super(caractere);
		rede = new MultilayerPerceptron2();
		dados = new ConjuntosDados(JdvUtils.Arquivo.carregarDados(rede));
	}
	
	@Override
	public int novaJogada(double[] entradas) {
		entradas = validaEntradasDaRede(entradas);
		int posicaoEscolhida = rede.processar(entradas);
		
		// Escolheu uma posição já ocupada.
		if (entradas[posicaoEscolhida] != Caractere.VAZIO.getValor()) {
			System.out
					.println("A Rede computou uma posição inválida. Escolhendo novo movimento...");
			posicaoEscolhida = super.escolhePosicao(entradas);
		}
		
		return posicaoEscolhida;
	}
	
	@Override
	public void notificarResultado(Partida partida) {
		if (partida.getVencedor() == this && this.getCaractere().equals(Caractere.X)) {
			partida.getJogadasVencedor().forEach(
					j -> dados.adicionarDadoES(j.getConfiguracao(),
							validaPosicaoEscolhida(j.getPosicaoEscolhida())));
		} else {
			partida.getJogadasVencedor().forEach(
					j -> dados.adicionarDadoES(validaEntradasDaRede(j.getConfiguracao()),
							validaPosicaoEscolhida(j.getPosicaoEscolhida())));
		}
		aprenderJogadas();
	}
	
	int i = 0;
	
	private void aprenderJogadas() {
		// FIXME Temporário
		JdvUtils.Arquivo.salvarDados(rede, dados.getMLDataSet());
		// ----------------
		
		rede.treinar(dados);
		
		// FIXME Temporário
//		MultilayerPerceptron.salvaNetwork(rede, "hue_2" + i++);
		// ----------------
	}
	
	/**
	 * Valida os valores das entradas a serem computadas pela Rede. Caso o {@link JogadorRNA} em
	 * questão possuir o {@link Caractere#O}, os valores das entradas precisarão ser ajustados,
	 * pois a rede é treinada para reconhecer o valor do {@link Caractere#X} como posição da Rede e
	 * o {@link Caractere#O} como o adversário.
	 * 
	 * @param entradas
	 * @return
	 */
	private double[] validaEntradasDaRede(double[] entradas) {
		if (getCaractere().equals(Caractere.O))
			for (int i = 0; i < entradas.length; i++)
				entradas[i] *= -1;
		
		return entradas;
	}
	
	private double[] validaPosicaoEscolhida(int posicao) {
		return rede.traduzirPosicaoTabuleiro(posicao).getData();
	}
}
