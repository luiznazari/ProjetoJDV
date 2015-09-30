package huehue.br.rede.modelo;

import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.util.JdvUtils;
import lombok.Getter;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

@Getter
public class MultilayerPerceptron2 extends JdvRedeAbstrata {
	
	public MultilayerPerceptron2() {
		super(9, 9);
		margemDeErro = 0.07D; // 7%
		constanteDeAprendizagem = 0.1;
		momentum = 0.4;
	}
	
	@Override
	public String getEstruturaRede() {
		return "?:B->SIGMOID->5:B->SIGMOID->?";
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
		JdvRedeAbstrata rede = new MultilayerPerceptron2();
		ConjuntosDados dados = new ConjuntosDados(JdvUtils.Arquivo.carregarDados(rede));
		rede.treinar(dados);
		System.out.println(rede);
	}
	
}
