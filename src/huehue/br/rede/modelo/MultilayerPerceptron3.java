package huehue.br.rede.modelo;

import huehue.br.modelo.Caractere;
import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.util.JdvUtils;

import org.encog.ml.data.MLData;

public class MultilayerPerceptron3 extends MultilayerPerceptron2 {
	
	public MultilayerPerceptron3() {
		super(18, 9);
		momentum = 0.4;
		margemDeErro = 0.07D;
		constanteDeAprendizagem = 0.05;
	}
	
	@Override
	public MLData traduzirEntrada(double[] entradas) {
		double[] entradasXO = new double[18];
		
		for (int i = 0; i < 9; i++) {
			if (entradas[i] == Caractere.X.getValor())
				entradasXO[i] = 1;
			else if (entradas[i] == Caractere.O.getValor())
				entradasXO[i + 9] = 1;
		}
		
		return super.traduzirEntrada(entradasXO);
	}
	
	@Override
	public double[] converteEntradaEmTabuleiro(MLData entrada) {
		double[] entradas = entrada.getData();
		double[] tabuleiro = new double[9];
		
		for (int i = 0; i < 9; i++) {
			if (entradas[i] == 1)
				tabuleiro[i] = Caractere.X.getValor();
			else if (entradas[i + 9] == 1)
				tabuleiro[i] = Caractere.O.getValor();
		}
		
		return tabuleiro;
	}
	
	public static void main(String[] args) {
		JdvUtils.Arquivo.versionamento(455);
		JdvRedeAbstrata rede = new MultilayerPerceptron3();
		ConjuntosDados dados = new ConjuntosDados(JdvUtils.Arquivo.carregarDados(rede));
		
		rede.treinar(dados);
		
		JdvUtils.Arquivo.incrementaVersao();
		
		JdvUtils.Arquivo.salvarRede(rede);
		JdvUtils.Arquivo.salvarDados(rede, dados.getMLDataSet());
	}
	
}
