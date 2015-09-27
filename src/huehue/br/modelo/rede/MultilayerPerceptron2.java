package huehue.br.modelo.rede;

import huehue.br.util.JdvUtils;
import lombok.Getter;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

@Getter
public class MultilayerPerceptron2 extends JdvRedeAbstrata {
	
	public MultilayerPerceptron2() {
		super(9, 9);
		margemDeErro = 0.0001D; // 0.01%
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
