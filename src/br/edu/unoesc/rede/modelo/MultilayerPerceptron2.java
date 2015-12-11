package br.edu.unoesc.rede.modelo;

import java.util.List;

import lombok.Getter;

import org.encog.Encog;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;

import br.edu.unoesc.modelo.Caractere;
import br.edu.unoesc.rede.dados.ConjuntosDados;
import br.edu.unoesc.util.JdvLog;
import br.edu.unoesc.util.JdvUtils;

@Getter
public class MultilayerPerceptron2 extends JdvRedeAbstrata {

	public MultilayerPerceptron2(Integer numeroEntradas, Integer numeroSaidas, String nome) {
		super(numeroEntradas, numeroSaidas, nome);
	}

	public MultilayerPerceptron2() {
		this(null);
	}

	public MultilayerPerceptron2(String nome) {
		this(9, 9, nome);

		momentum = 0.4;
		margemDeErro = 0.06;
		constanteDeAprendizagem = 0.1;
	}

	@Override
	public String getEstruturaRede() {
		return "?:B->TANH->18:B->TANH->9:B->TANH->?";
	}

	/**
	 * {@inheritDoc}<br>
	 * <br>
	 * Será escolhida a saída com o maior valor cuja posição no tabuleiro esteja vazia (baseado nos
	 * sinais de entrada recebidos). Têm como referência de comparação o {@link Caractere#VAZIO}.
	 */
	@Override
	public int traduzirSaida(MLData saida) {
		List<MapaSaida> listaSaida = JdvUtils.RNA.toSaidasMapeadas(saida.getData());

		return buscaPrimeiroIndiceValido(listaSaida);
	}

	@Override
	public MLData convertePosicaoTabuleiroEmSaida(int posicao) {
		double[] dd = new double[9];

		dd[posicao] = Caractere.X.getValor();

		return new BasicMLData(dd);
	}

	private int buscaPrimeiroIndiceValido(List<MapaSaida> listaSaida) {
		MapaSaida ms = listaSaida.get(0);

		if (entradasTmp[ms.index] == Caractere.VAZIO.getValor())
			return ms.index;

		JdvLog.erroRespostaInvalida(ms, entradasTmp);
		listaSaida.remove(0);
		return buscaPrimeiroIndiceValido(listaSaida);
	}

	public static void main(String[] args) {
		JdvUtils.Arquivo.versionamento(1);
		JdvRedeAbstrata rede = new MultilayerPerceptron2("treinamentoMM2").inicializar();
		ConjuntosDados dados = JdvUtils.Arquivo.carregarDados(rede);
		MLDataSet setDados = dados.getConjuntosMLSet();
		rede.treinar(dados);

		JdvUtils.Arquivo.incrementaVersao();
		JdvUtils.Arquivo.salvarRede(rede);
		JdvUtils.Arquivo.salvarDados(rede, dados);

//		rede.testar(setDados.get(( int ) (Math.random() * setDados.size())));
		rede.testar(setDados);

		Encog.getInstance().shutdown();
	}
}
