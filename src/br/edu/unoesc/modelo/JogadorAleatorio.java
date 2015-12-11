package br.edu.unoesc.modelo;

import br.edu.unoesc.logica.Partida;
import br.edu.unoesc.rede.dados.ConjuntosDados;
import br.edu.unoesc.rede.modelo.JdvRedeAbstrata;
import br.edu.unoesc.rede.modelo.MultilayerPerceptron3;

/**
 * Representa um jogador que utiliza um algoritimo para determinar posições aleatórias.
 * 
 * @author Luiz Felipe Nazari
 */
public class JogadorAleatorio extends JogadorAutomato {
	
	private ConjuntosDados dados;
	
	private JdvRedeAbstrata rede;
	
	public JogadorAleatorio(Caractere caractere) {
		super(caractere);
	}
	
	public JogadorAleatorio(Caractere caractere, boolean armazenaCasos) {
		super(caractere);
		
		if (armazenaCasos) {
			rede = new MultilayerPerceptron3();
			dados = new ConjuntosDados();
		}
	}
	
	@Override
	public int novaJogada(double[] entradas) {
		return super.escolhePosicao(entradas);
	}
	
	@Override
	public void notificarResultado(Partida partida) {
//		if (dados != null) {
//			partida.getJogadasVencedor().forEach(
//					p -> dados.adicionarDadoES(
//							rede.traduzirEntrada(super.validaEntradas(p.getConfiguracao())),
//							rede.convertePosicaoTabuleiroEmSaida(p.getPosicaoEscolhida())));
//			
//			JdvUtils.Arquivo.salvarDados(rede, dados);
//			JdvUtils.Arquivo.incrementaVersao();
//		}
	}
	
}
