package huehue.br.rede.modelo;

import huehue.br.util.JdvUtils;
import lombok.Getter;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

@Getter
public class MultilayerPerceptron3 extends JdvRedeAbstrata {
	
	public MultilayerPerceptron3() {
		super(18, 9);
	}
	
	@Override
	public MLData traduzirEntrada(double[] entradas) {
		double[] entradasXO = new double[18];
		
		for (int i = 0; i < 9; i++) {
			if (entradas[i] == 1)
				entradasXO[i] = 1;
			else if (entradas[i] == -1)
				entradasXO[i + 9] = 1;
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
	
}
