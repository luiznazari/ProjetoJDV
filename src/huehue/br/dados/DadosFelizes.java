package huehue.br.dados;

import huehue.br.exception.VeiaException;
import huehue.br.util.VeiaUtil;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;

public class DadosFelizes {
	
	private List<MLDataPair> conjuntosES = new ArrayList<MLDataPair>();
	
	private List<MLDataPair> conjuntosESTemporarios = new ArrayList<MLDataPair>();
	
	public DadosFelizes() {}
	
	public DadosFelizes(List<MLDataPair> conjuntos) {
		this.conjuntosES = conjuntos;
	}
	
	public void salvarEmArquivo() {
		if (conjuntosES == null || conjuntosES.size() == 0)
			throw new VeiaException("Erro ao salvar. Conjunto de dados nulo ou vazio!");
		
		VeiaUtil.Arquivo.salvarDados(conjuntosES);
	}
	
	public DadosFelizes carregarDoArquivo() {
		BasicMLDataSet set = new BasicMLDataSet(VeiaUtil.Arquivo.carregarDados());
		conjuntosES = set.getData();
		
		return this;
	}
	
	public void adicionarDadoESTemporario(double[] entradas, double saida) {
		conjuntosESTemporarios.add(new BasicMLDataPair(new BasicMLData(entradas), new BasicMLData(
		        new double[] {
			        saida
		        })));
	}
	
	public void adicionarDadoES(double[] entradas, double saida) {
		adicionarDadoNaoRepetido(new BasicMLDataPair(new BasicMLData(entradas), new BasicMLData(
		        new double[] {
			        saida
		        })));
	}
	
	private void adicionarDadoNaoRepetido(MLDataPair dado) {
		int len = conjuntosES.size();
		
		for (int i = 0; i < len; i++) {
			if (isMLDataPairEquals(conjuntosES.get(i), dado)) {
				conjuntosES.set(i, dado);
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
