package huehue.br.modelo;

import huehue.br.logica.Partida;
import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.rede.modelo.JdvRedeAbstrata;
import huehue.br.rede.modelo.MultilayerPerceptron3;
import huehue.br.util.JdvUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe representando um jogador com Inteligência Artificial utilizando uma
 * Rede Neural Artificial.
 * 
 * @author Luiz Felipe Nazari
 */
public class JogadorRNA extends JogadorAutomato {
	
	@Getter
	@Setter
	private boolean deveTreinar = false;
	
	private JdvRedeAbstrata rede;
	
	// Conjuntos de dados conhecidos pela rede e utilizados no treinamento.
	private ConjuntosDados dados;
	
	public JogadorRNA(Caractere caractere) {
		this(caractere, false);
	}
	
	public JogadorRNA(Caractere caractere, boolean deveTreinar) {
		super(caractere);
		this.deveTreinar = deveTreinar;
		
		JdvUtils.Arquivo.versionamento(10);
		
		rede = new MultilayerPerceptron3().inicializar();
		dados = new ConjuntosDados(JdvUtils.Arquivo.carregarDados(rede));
	}
	
	@Override
	public int novaJogada(double[] entradas) {
		entradas = validaEntradasParaRede(entradas);
		int posicaoEscolhida = rede.processar(entradas);
		
		// Escolheu uma posição já ocupada.
		if (entradas[posicaoEscolhida] != Caractere.VAZIO.getValor()) {
			System.err.println("A Rede computou uma posição inválida. Escolhendo novo movimento...");
			posicaoEscolhida = super.escolhePosicao(entradas);
		}
		
		return posicaoEscolhida;
	}
	
	@Override
	public void notificarResultado(Partida partida) {
		partida.getJogadasVencedor().forEach(
				p -> dados.adicionarDadoES(
						rede.traduzirEntrada(validaEntradasParaRede(p.getConfiguracao())),
						rede.convertePosicaoTabuleiroEmSaida(p.getPosicaoEscolhida())));
		
		if (deveTreinar)
			aprenderJogadas();
	}
	
	private void aprenderJogadas() {
		
		rede.treinar(dados);
		
		// FIXME Temporário
		int v = JdvUtils.Arquivo.incrementaVersao();
		
		if (v % 10 == 0) {
			JdvUtils.Arquivo.salvarRede(rede);
			JdvUtils.Arquivo.salvarDados(rede, dados.getMLDataSet());
		}
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
	private double[] validaEntradasParaRede(double[] entradas) {
		if (getCaractere().equals(Caractere.O))
			for (int i = 0; i < entradas.length; i++)
				entradas[i] *= -1;
		
		return entradas;
	}
}
