package huehue.br.rede.dados;

import huehue.br.modelo.JogadorRNA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
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
	private BasicMLDataSet conjuntos;
	
	@Getter
	private List<MLDataPair> conjuntosTemporarios = new ArrayList<MLDataPair>();
	
	public ConjuntosDados(BasicMLDataSet conjuntos) {
		this.conjuntos = conjuntos;
	}
	
	public void armazenarTemporarios() {
		conjuntosTemporarios.forEach(par -> adicionarDadoES(conjuntos.getData(), par));
		descartarTemporarios();
	}
	
	public void descartarTemporarios() {
		conjuntosTemporarios.clear();
	}
	
	public void embaralhar() {
		Collections.shuffle(conjuntos.getData());
	}
	
	/* Adição de conjuntos */
	
	public void adicionarDadoESTemporario(MLData entrada, MLData saida) {
		adicionarDadoES(conjuntosTemporarios, new BasicMLDataPair(entrada, saida));
	}
	
	public void adicionarDadoES(MLData entrada, MLData saida) {
		adicionarDadoES(conjuntos.getData(), new BasicMLDataPair(entrada, saida));
	}
	
	private void adicionarDadoES(List<MLDataPair> lista, MLDataPair par) {
		int indiceES = getIndiceDadoES(lista.iterator(), par);
		
		if (indiceES == -1)
			lista.add(par);
		else if (substituirRepetidos)
			lista.set(indiceES, par);
	}
	
	private int getIndiceDadoES(Iterator<MLDataPair> iterator, MLDataPair dado) {
		int i = 0;
		while (iterator.hasNext()) {
			if (isParComEntradasIguais(iterator.next(), dado))
				return i;
			i++;
		}
		
		return -1;
	}
	
	private boolean isParComEntradasIguais(MLDataPair par1, MLDataPair par2) {
		return isDadosIguais(par1.getInput(), par2.getInput());
	}
	
	private boolean isDadosIguais(MLData dado1, MLData dado2) {
		int size = dado1.size();
		if (size != dado2.size())
			return false;
		
		for (int i = 0; i < size; i++)
			if (dado1.getData(i) != dado2.getData(i))
				return false;
		
		return true;
	}
	
	/* ------------------------ */
	
}
