package huehue.br.rede.modelo;

import huehue.br.exception.JdvException;
import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.util.JdvUtils;
import lombok.Getter;

import org.encog.Encog;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;

@Getter
public class MultilayerPerceptron4 extends MultilayerPerceptron2 {
	
	public MultilayerPerceptron4(Integer numeroEntradas, Integer numeroSaidas, String nome) {
		super(numeroEntradas, numeroSaidas, nome);
	}
	
	public MultilayerPerceptron4() {
		this(null);
	}
	
	public MultilayerPerceptron4(String nome) {
		this(9, 9, nome);
		
		momentum = 0.4;
		margemDeErro = 0.04;
		constanteDeAprendizagem = 0.05;
	}
	
	@Override
	public MLData convertePosicaoTabuleiroEmSaida(int posicao) {
		throw new JdvException("Método não suportado para esta classe!");
	}
	
	public static void main(String[] args) {
		JdvUtils.Arquivo.versionamento(0);
		JdvRedeAbstrata rede = new MultilayerPerceptron4("treinamento").inicializar();
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
