package huehue.br.rede.dados;

import huehue.br.modelo.JogadorRNA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;

/**
 * Classe responsável por armazenar todos os conjuntos de dados utilizados no treinamento da rede
 * neuronal de um {@link JogadorRNA}, assim como aqueles temporários.
 * 
 * @author Luiz Felipe Nazari
 */
@NoArgsConstructor
public class ConjuntosDados {
	
	private List<JdvMLDataPair> conjuntos = new ArrayList<JdvMLDataPair>();
	
	@Getter
	private List<JdvMLDataPair> conjuntosTemporarios = new ArrayList<JdvMLDataPair>();
	
	public ConjuntosDados(List<JdvMLDataPair> conjuntos) {
		this.conjuntos = conjuntos;
	}
	
	public void armazenarTemporarios() {
		conjuntosTemporarios.forEach(par -> adicionarDadoES(par));
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
	
	public void adicionarDadoES(JdvMLDataPair par) {
		adicionarDadoES(conjuntos, par);
	}
	
	private void adicionarDadoES(List<JdvMLDataPair> lista, JdvMLDataPair par) {
		Iterator<JdvMLDataPair> iterator = lista.iterator();
		JdvMLDataPair parAtual = null;
		
		int index = 0;
		while (iterator.hasNext()) {
			parAtual = iterator.next();
			
			if (par.isEntradasIguais(parAtual.getPar())) {
				
				// Quando as entradas já existirem na lista e
				// o par possuir uma pontuação menor, subistitui.
				if (par.getPontos() > parAtual.getPontos())
					lista.set(index, par);
				else if (par.getPontos() == parAtual.getPontos())
					parAtual.incrementaFrequencia();
				
				return;
			}
			
			index++;
		}
		
		// Quando o par não existir, adiciona.
		lista.add(par);
	}
	
	/* ------------------------ */
	
	public List<MLDataPair> getConjuntos() {
		return this.conjuntos.stream().map(JdvMLDataPair::getPar).collect(Collectors.toList());
	}
	
	public MLDataSet getConjuntosSet() {
		return new BasicMLDataSet(this.getConjuntos());
	}
	
	public static ConjuntosDados criaConjuntosAPartirDeArquivo(BasicMLDataSet set) {
		List<JdvMLDataPair> pares = set.getData().stream()
				.map(d -> JdvMLDataPair.criaAPartirDeArquivo(d)).collect(Collectors.toList());
		
		return new ConjuntosDados(pares);
	}
	
	public MLDataSet getConjuntosParaSalvarEmArquivo() {
		MLDataSet set = new BasicMLDataSet();
		
		this.conjuntos.forEach(c -> set.add(c.getParaSalvar()));
		
		return set;
	}
	
	public static int getEntradasAdicionaisCSV() {
		return JdvMLDataPair.ENTRADAS_ADICIONAIS;
	}
	
}
