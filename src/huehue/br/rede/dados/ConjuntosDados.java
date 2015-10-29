package huehue.br.rede.dados;

import huehue.br.modelo.JogadorRNA;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;

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

	@Getter
	private List<JdvMLDataPair> conjuntos = new ArrayList<JdvMLDataPair>();

	@Getter
	private List<JdvMLDataPair> conjuntosTemporarios = new ArrayList<JdvMLDataPair>();

	@Getter
	private List<JdvMLDataPair> conjuntosNaoTreinados = new ArrayList<JdvMLDataPair>();

	public ConjuntosDados(List<JdvMLDataPair> conjuntos) {
		this.conjuntos = conjuntos;
	}

	public void setConjuntos(List<JdvMLDataPair> novosConjuntos) {
		conjuntos = novosConjuntos;
		conjuntosTemporarios.clear();
	}

	@Deprecated
	public void armazenarTemporarios() {
		conjuntosTemporarios.forEach(par -> adicionarDadoES(par));
		conjuntosTemporarios.clear();
	}

	public void descartarTemporarios() {
		conjuntosTemporarios.forEach(par -> {
			int index;
			if ((index = conjuntosNaoTreinados.indexOf(par)) != -1) {
				conjuntosNaoTreinados.get(index).incrementaFrequencia();
			} else {
				conjuntosNaoTreinados.add(par);
			}
		});
		conjuntosTemporarios.clear();
	}

	/* Adição de conjuntos */

	public void adicionarDadoESTemporario(List<JdvMLDataPair> pares) {
		pares.forEach(par -> adicionarDadoESTemporario(par));
	}

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

	public MLDataSet getConjuntosMLSet() {
		return new BasicMLDataSet(conjuntos.stream().map(JdvMLDataPair::getPar).collect(Collectors.toList()));
	}

	public List<JdvMLDataPair> getConjuntosParaTreinamento() {
//		List<JdvMLDataPair> conjuntosTreinamento = new ArrayList<>(conjuntos);
//		conjuntosTemporarios.forEach(par -> adicionarDadoES(conjuntosTreinamento, par));
//
//		Collections.shuffle(conjuntosTreinamento);
//		return conjuntosTreinamento;
		return conjuntosTemporarios;
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

	public MLDataSet getConjuntosTemporariosParaSalvarEmArquivo() {
		MLDataSet set = new BasicMLDataSet();
		this.conjuntosTemporarios.forEach(c -> set.add(c.getParaSalvar()));

		return set;
	}

	public static int getEntradasAdicionaisCSV() {
		return JdvMLDataPair.ENTRADAS_ADICIONAIS;
	}

}
