package huehue.br.rede.dados;

import huehue.br.encog.modelo.JdvMLDataPair;
import huehue.br.modelo.JogadorRNA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;

/**
 * Classe responsável por armazenar todos os conjuntos de dados utilizados no treinamento da rede
 * neuronal de um {@link JogadorRNA}, assim como aqueles temporários.
 * 
 * @author Luiz Felipe Nazari
 */
public class ConjuntosDados {
	
	@Setter
	private boolean substituirRepetidos = false;
	
	@Getter
	private List<JdvMLDataPair> conjuntosES = new ArrayList<JdvMLDataPair>();
	
	private List<JdvMLDataPair> conjuntosESTemporarios = new ArrayList<JdvMLDataPair>();
	
	public ConjuntosDados() {}
	
	public ConjuntosDados(List<JdvMLDataPair> conjuntos) {
		this.conjuntosES = conjuntos;
	}
	
	public ConjuntosDados(BasicMLDataSet set) {
		this(set.getData().stream().map(par -> new JdvMLDataPair(par)).collect(Collectors.toList()));
	}
	
	public void adicionarDadoESTemporario(double[] entradas, double[] saidas) {
		JdvMLDataPair par = new JdvMLDataPair(entradas, saidas);
		
		if (substituirRepetidos)
			substituiDadoRepetido(conjuntosES, par);
		else
			conjuntosES.add(par);
	}
	
	public void adicionarDadoES(MLData entrada, MLData saida) {
		JdvMLDataPair par = new JdvMLDataPair(entrada, saida);
		
		if (substituirRepetidos)
			substituiDadoRepetido(conjuntosESTemporarios, par);
		else
			conjuntosESTemporarios.add(par);
	}
	
	private void substituiDadoRepetido(List<JdvMLDataPair> lista, JdvMLDataPair dado) {
		Iterator<JdvMLDataPair> iterator = lista.iterator();
		
		int i = 0;
		while (iterator.hasNext()) {
			if (iterator.next().isEntradasIguais(dado))
				lista.set(i, dado);
			i++;
		}
		
		lista.add(dado);
	}
	
	public void descartarTemporarios() {
		conjuntosESTemporarios.clear();
	}
	
	public void armazenarTemporarios() {
		conjuntosES.addAll(conjuntosESTemporarios);
	}
	
	public MLDataSet getMLDataSet() {
		return new BasicMLDataSet(conjuntosES.stream()
				.map(par -> new BasicMLDataPair(par.getInput(), par.getIdeal()))
				.collect(Collectors.toList()));
	}
	
	public List<MLDataPair> getDadosEmbaralhados() {
		List<MLDataPair> conjuntosEmbaralhados = new ArrayList<>(conjuntosES);
		Collections.shuffle(conjuntosEmbaralhados);
		return conjuntosEmbaralhados;
	}
}
