package huehue.br.rede.modelo;

import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.util.JdvUtils;
import lombok.Getter;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

@Getter
public class MultilayerPerceptron3 extends JdvRedeAbstrata {
	
	public MultilayerPerceptron3() {
		super(18, 9);
		margemDeErro = 0.0001D; // 0.01%
	}
	
	@Override
	public String getEstruturaRede() {
		return "?:B->SIGMOID->81:B->SIGMOID->54:B->SIGMOID->?";
	}
	
	@Override
	public MLData traduzirEntrada(double[] entradas) {
		double[] entradasXO = new double[18];
		
		for (int i = 0; i < 9; i++) {
			if (entradas[i] == 1)
				entradasXO[i] = entradas[i];
			else if (entradas[i] == -1)
				entradasXO[i + 9] = entradas[i];
		}
		
		return super.traduzirEntrada(entradasXO);
	}
	
	@Override
	public int traduzirSaida(MLData saida) {
		int index = 0;
		double maior = 0;
		
		int i = 0;
		for (double d : saida.getData()) {
			if (d > maior) {
				maior = d;
				index = i;
			}
			i++;
		}
		
		return index;
	}
	
	@Override
	public MLData traduzirPosicaoTabuleiro(int posicao) {
		return JdvUtils.RNA.converteDadosUmParaNove(new BasicMLData(new double[] {
			posicao
		}));
	}
	
	public static void main(String[] args) {
		JdvRedeAbstrata rede = new MultilayerPerceptron3();
		ConjuntosDados dados = new ConjuntosDados(JdvUtils.Arquivo.carregarDados(rede));
		rede.treinar(dados);
	}
	
}
