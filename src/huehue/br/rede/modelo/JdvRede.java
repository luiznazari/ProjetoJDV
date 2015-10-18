package huehue.br.rede.modelo;

import huehue.br.util.JdvUtils;

import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;

/**
 * Interface que padroniza as redes neuronais utilizadas para resolver o Jogo da Velha.<br>
 * 
 * <pre>
 * <b>Padronização dos dados conhecidos pelas redes:</b>
 * <b>Mapeamento dos valores:</b>
 *  1 = Jogador X
 * -1 = Jogador O
 *  0 = posição vazia
 * 
 * <b>Mapeamento do tabuleiro:</b>
 *  0 | 1 | 2
 * ---+---+---
 *  3 | 4 | 5
 * ---+---+---
 *  6 | 7 | 8
 * </pre>
 * 
 * @author Luiz Felipe Nazari
 */
public interface JdvRede {
	
	public Integer getNumeroEntradas();
	
	public Integer getNumeroSaidas();
	
	public Double getMargemDeErro();
	
	public String getTipoRede();
	
	public String getEstruturaRede();
	
	public String getTipoTreinamento();
	
	public BasicNetwork getRede();
	
	default String getNome() {
		return this.getClass().getSimpleName();
	}
	
	default BasicNetwork construirRede() {
		MLMethodFactory methodFactory = new MLMethodFactory();
		return ( BasicNetwork ) methodFactory.create(getTipoRede(), getEstruturaRede(),
				getNumeroEntradas(), getNumeroSaidas());
	}
	
	default BasicNetwork carregarRede() {
		return JdvUtils.Arquivo.carregarRede(this);
	}
	
	@Deprecated
	default BasicNetwork _construirRede() {
		BasicNetwork rede = new BasicNetwork();
		
		Layer entrada = new BasicLayer(new ActivationLinear(), true, getNumeroEntradas());
		Layer oculta1 = new BasicLayer(new ActivationSigmoid(), true, 5);
		Layer saida = new BasicLayer(new ActivationSigmoid(), true, getNumeroSaidas());
		
		rede.addLayer(entrada);
		rede.addLayer(oculta1);
		rede.addLayer(saida);
		
		rede.getStructure().finalizeStructure();
		rede.reset();
		return rede;
	}
	
}
