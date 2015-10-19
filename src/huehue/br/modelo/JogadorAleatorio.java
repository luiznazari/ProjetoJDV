package huehue.br.modelo;

import huehue.br.logica.Partida;
import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.rede.modelo.JdvRedeAbstrata;
import huehue.br.rede.modelo.MultilayerPerceptron3;
import huehue.br.util.JdvUtils;

import org.encog.ml.data.basic.BasicMLDataSet;

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
			dados = new ConjuntosDados(new BasicMLDataSet());
			dados.setSubstituirRepetidos(false);
		}
	}
	
	@Override
	public int novaJogada(double[] entradas) {
		return super.escolhePosicao(entradas);
	}
	
	@Override
	public void notificarResultado(Partida partida) {
		if (dados != null) {
			partida.getJogadasVencedor().forEach(
					p -> dados.adicionarDadoES(
							rede.traduzirEntrada(super.validaEntradas(p.getConfiguracao())),
							rede.convertePosicaoTabuleiroEmSaida(p.getPosicaoEscolhida())));
			
			JdvUtils.Arquivo.salvarDados(rede, dados.getConjuntos());
			JdvUtils.Arquivo.incrementaVersao();
		}
	}
	
}
