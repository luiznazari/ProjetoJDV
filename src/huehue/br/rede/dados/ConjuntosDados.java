package huehue.br.rede.dados;

import huehue.br.modelo.JogadorRNA;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;

/**
 * Classe responsável por armazenar todos os conjuntos de dados utilizados no treinamento da rede
 * neuronal de um {@link JogadorRNA}, assim como aqueles temporários.
 * 
 * @author Luiz Felipe Nazari
 */
public class ConjuntosDados {
	
	private List<MLDataPair> conjuntosES = new ArrayList<MLDataPair>();
	
	private List<MLDataPair> conjuntosESTemporarios = new ArrayList<MLDataPair>();
	
	public ConjuntosDados() {}
	
	public ConjuntosDados(List<MLDataPair> conjuntos) {
		this.conjuntosES = conjuntos;
	}
	
	public ConjuntosDados(BasicMLDataSet set) {
		this(set.getData());
	}
	
	public void adicionarDadoESTemporario(double[] entradas, double saida) {
		conjuntosESTemporarios.add(new BasicMLDataPair(new BasicMLData(entradas), new BasicMLData(
				new double[] {
					saida
				})));
	}
	
	public void adicionarDadoES(MLData entrada, MLData saida) {
		adicionarDadoNaoRepetido(new BasicMLDataPair(entrada, saida));
	}
	
	private void adicionarDadoNaoRepetido(MLDataPair dado) {
		int len = conjuntosES.size();
		
		for (int i = 0; i < len; i++) {
			if (isMLDataPairEquals(conjuntosES.get(i), dado)) {
//				conjuntosES.set(i, dado);
				return;
			}
		}
		
		conjuntosES.add(dado);
	}
	
	private boolean isMLDataPairEquals(MLDataPair p1, MLDataPair p2) {
		double[] in1 = p1.getInputArray();
		double[] in2 = p2.getInputArray();
		
		for (int i = 0; i < in1.length; i++)
			if (in1[i] != in2[i])
				return false;
		
		return true;
	}
	
	public void descartarTemporarios() {
		conjuntosESTemporarios.clear();
	}
	
	public void armazenarTemporarios() {
		conjuntosESTemporarios.forEach(d -> adicionarDadoNaoRepetido(d));
		descartarTemporarios();
	}
	
	public MLDataSet getMLDataSet() {
		return new BasicMLDataSet(conjuntosES);
	}
}
