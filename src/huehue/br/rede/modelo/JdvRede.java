package huehue.br.rede.modelo;

import huehue.br.util.JdvUtils;

import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;

/**
 * Função de ativação Sigmoid resulta em valores entre 0 e 1.
 * Função de ativação Tanh resulta em valores entre -1 e 1.
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
	default BasicNetwork construirRede2() {
		BasicNetwork rede = new BasicNetwork();
		
		Layer entrada = new BasicLayer(new ActivationLinear(), true, getNumeroEntradas());
		Layer oculta1 = new BasicLayer(new ActivationSigmoid(), true, 81);
		Layer oculta2 = new BasicLayer(new ActivationSigmoid(), true, 54);
		Layer saida = new BasicLayer(new ActivationSigmoid(), true, getNumeroSaidas());
		
		rede.addLayer(entrada);
		rede.addLayer(oculta1);
		rede.addLayer(oculta2);
		rede.addLayer(saida);
		
		rede.getStructure().finalizeStructure();
		rede.reset();
		return rede;
	}
	
}
