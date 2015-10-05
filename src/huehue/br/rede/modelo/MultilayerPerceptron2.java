package huehue.br.rede.modelo;

import huehue.br.modelo.Caractere;
import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.util.JdvUtils;

import java.util.List;

import lombok.Getter;

import org.encog.Encog;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;

@Getter
public class MultilayerPerceptron2 extends JdvRedeAbstrata {
	
	public MultilayerPerceptron2(Integer numeroEntradas, Integer numeroSaidas) {
		super(numeroEntradas, numeroSaidas);
	}
	
	public MultilayerPerceptron2() {
		this(9, 9);
		momentum = 0.4;
		margemDeErro = 0.05D;
		constanteDeAprendizagem = 0.1;
	}
	
	@Override
	public String getEstruturaRede() {
		return "?:B->SIGMOID->5:B->SIGMOID->?";
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
		
		System.err.println("Computou um resultado inválido! " + ms);
		listaSaida.remove(0);
		return buscaPrimeiroIndiceValido(listaSaida);
	}
	
	public static void main(String[] args) {
		JdvUtils.Arquivo.versionamento(0);
		JdvRedeAbstrata rede = new MultilayerPerceptron2().inicializar();
		ConjuntosDados dados = new ConjuntosDados(JdvUtils.Arquivo.carregarDados(rede));
		MLDataSet setDados = dados.getMLDataSet();
		
		rede.treinar(dados);
		
//		JdvUtils.Arquivo.incrementaVersao();
//		JdvUtils.Arquivo.salvarRede(rede);
//		JdvUtils.Arquivo.salvarDados(rede, setDados);
		
//		rede.testar(setDados.get(( int ) (Math.random() * setDados.size())));
		rede.testar(setDados);
		
//		rede.testar(new BasicMLDataPair(new BasicMLData(new double[] {
//			-1, 0, -1, -1, 1, 1, 1, -1, 1
//		}), new BasicMLData(new double[] {
//			0, 1, 0, 0, 0, 0, 0, 0, 0
//		})));
		
		Encog.getInstance().shutdown();
	}
}
