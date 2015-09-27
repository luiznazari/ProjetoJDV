package huehue.br.modelo.rede;

import huehue.br.util.JdvUtils;
import lombok.Getter;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

@Getter
public class MultilayerPerceptron extends JdvRedeAbstrata {
	
	public MultilayerPerceptron() {
		super(9, 1);
	}
	
	/**
	 * Traduz o valor da saída resultante do processamento da RNA.
	 * 
	 * <pre>
	 * Exemplos:
	 * 
	 * 0.0001	-> 0.0	-> 0
	 * 0.0981	-> 0.1	-> 1
	 * 0.196	-> 0.2	-> 2
	 * 0.3328	-> 0.3	-> 3
	 * 0.4007	-> 0.4	-> 4
	 * 0.7865	-> 0.8	-> 8
	 * 0.9999	-> 1.0	-> 8
	 * </pre>
	 * 
	 * @param d
	 *            a saída resultante do processamento da RNA.
	 * @return valor da saída traduzido, onde: 0 <= valor <= 8.
	 */
	@Override
	public int traduzirSaida(MLData saida) {
		double d = JdvUtils.RNA.valorAproximado(saida.getData(0));
		
		if (d >= 1)
			return 9;
		if (d <= 0)
			return 1;
		return (( int ) (d * 10)) - 1;
	}
	
	@Override
	public MLData traduzirPosicaoTabuleiro(int posicao) {
		return new BasicMLData(new double[] {
			posicao / 10
		});
	}
}
