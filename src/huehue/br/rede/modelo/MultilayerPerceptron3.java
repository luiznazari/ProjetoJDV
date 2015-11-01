package huehue.br.rede.modelo;

import huehue.br.modelo.Caractere;
import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.util.JdvUtils;

import org.encog.Encog;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;

public class MultilayerPerceptron3 extends MultilayerPerceptron2 {

	public MultilayerPerceptron3() {
		this(null);
	}

	public MultilayerPerceptron3(String nome) {
		super(18, 9, nome);
		momentum = 0.4;
		margemDeErro = 0.08;
		constanteDeAprendizagem = 0.1;
	}

	@Override
	public String getEstruturaRede() {
		return "?:B->SIGMOID->9:B->SIGMOID->9:B->SIGMOID->?";
	}

	@Override
	public MLData traduzirEntrada(double[] entradas) {
		double[] entradasXO = new double[18];

		for (int i = 0; i < 9; i++) {
			if (entradas[i] > 0)
				entradasXO[i] = entradas[i];
			else if (entradas[i] < 0)
				entradasXO[i + 9] = entradas[i];
		}

		return super.traduzirEntrada(entradasXO);
	}

	@Override
	public double[] converteEntradaEmTabuleiro(MLData entrada) {
		double[] entradas = entrada.getData();
		double[] tabuleiro = new double[9];

		for (int i = 0; i < 9; i++) {
			if (entradas[i] > 0)
				tabuleiro[i] = Caractere.X.getValor();
			else if (entradas[i + 9] < 0)
				tabuleiro[i] = Caractere.O.getValor();
		}

		return tabuleiro;
	}

	public static void main(String[] args) {
		JdvUtils.Arquivo.versionamento(1);
		JdvRedeAbstrata rede = new MultilayerPerceptron3("treinamentoMM3").inicializar();
		ConjuntosDados dados = JdvUtils.Arquivo.carregarDados(rede);
		MLDataSet setDados = dados.getConjuntosMLSet();

//		rede.treinar(dados);
//
//		JdvUtils.Arquivo.incrementaVersao();
//		JdvUtils.Arquivo.salvarRede(rede);
//		JdvUtils.Arquivo.salvarDados(rede, dados);

//		rede.testar(setDados.get(( int ) (Math.random() * setDados.size())));
		rede.testar(setDados);

		Encog.getInstance().shutdown();
	}
}
