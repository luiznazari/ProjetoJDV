package huehue.br.rede.dados;

import huehue.br.modelo.JogadorRNA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
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
	private List<JdvMLDataPair> conjuntos = new ArrayList<JdvMLDataPair>();
	
	@Getter
	private List<JdvMLDataPair> conjuntosTemporarios = new ArrayList<JdvMLDataPair>();
	
//	public ConjuntosDados(BasicMLDataSet conjuntos) {
//		this.conjuntos = conjuntos;
//	}
	
	public void armazenarTemporarios() {
		conjuntosTemporarios.forEach(par -> adicionarDadoES(conjuntos, par));
		descartarTemporarios();
	}
	
	public void descartarTemporarios() {
		conjuntosTemporarios.clear();
	}
	
	public void embaralhar() {
		Collections.shuffle(conjuntos);
	}
	
	/* Adição de conjuntos */
	
	public void adicionarDadoESTemporario(JdvMLDataPair par) {
		adicionarDadoES(conjuntosTemporarios, par);
	}
	
	public void adicionarDadoESTemporario(MLData entrada, MLData saida) {
		adicionarDadoESTemporario(new JdvMLDataPair(entrada, saida));
	}
	
	public void adicionarDadoES(MLData entrada, MLData saida) {
		adicionarDadoES(conjuntos, new JdvMLDataPair(entrada, saida));
	}
	
	private void adicionarDadoES(List<JdvMLDataPair> lista, JdvMLDataPair par) {
		int indiceES = getIndiceDadoES(lista.iterator(), par);
		
		if (indiceES == -1)
			lista.add(par);
		else if (substituirRepetidos)
			lista.set(indiceES, par);
	}
	
	private int getIndiceDadoES(Iterator<JdvMLDataPair> iterator, JdvMLDataPair par) {
		int i = 0;
		while (iterator.hasNext()) {
			if (par.isEntradasIguais(iterator.next()))
				return i;
			i++;
		}
		
		return -1;
	}
	
	/* ------------------------ */
	
	public static ConjuntosDados criaConjuntosAPartirDeArquivo(BasicMLDataSet set) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public MLDataSet getConjuntosParaSalvar() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static int getEntradasAdicionaisCSV() {
		return JdvMLDataPair.ENTRADAS_ADICIONAIS;
	}
	
}
