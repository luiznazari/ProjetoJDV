package huehue.br.rede.modelo;

import huehue.br.util.JdvUtils;

import org.encog.ml.factory.MLMethodFactory;
import org.encog.neural.networks.BasicNetwork;

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
	
}
