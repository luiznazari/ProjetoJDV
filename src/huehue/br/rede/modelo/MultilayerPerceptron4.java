package huehue.br.rede.modelo;

import huehue.br.exception.JdvException;
import huehue.br.modelo.Caractere;
import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.util.JdvUtils;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.encog.Encog;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;

@Getter
public class MultilayerPerceptron4 extends JdvRedeAbstrata {
	
	public MultilayerPerceptron4(Integer numeroEntradas, Integer numeroSaidas, String nome) {
		super(numeroEntradas, numeroSaidas, nome);
	}
	
	public MultilayerPerceptron4() {
		this(null);
	}
	
	public MultilayerPerceptron4(String nome) {
		this(9, 9, nome);
		
		momentum = 0.4;
		margemDeErro = 0.40;
		constanteDeAprendizagem = 0.05;
	}
	
	@Override
	public String getEstruturaRede() {
		return "?:B->SIGMOID->18:B->SIGMOID->?";
	}
	
	@Override
	public MLData convertePosicaoTabuleiroEmSaida(int posicao) {
		throw new JdvException("Método não suportado!");
	}
	
	@Override
	public MLData convertePosicaoTabuleiroEmSaida(double[] entradas, int posicao) {
		entradas = entradas.clone();
		entradas[posicao] = Caractere.X.getValor();
		
		// FIXME feito sem otimizações, apenas para testes.
		return new BasicMLData(entradas);
	}
	
	@Override
	public int traduzirSaida(MLData saida) {
		List<MapaSaida> listaSaida = new ArrayList<>();
		
		int len = saida.size();
		for (int i = 0; i < len; i++)
			if (entradasTmp[i] == Caractere.VAZIO.getValor())
				listaSaida.add(new MapaSaida(i, saida.getData(i)));
		
		return listaSaida.stream().sorted((ms1, ms2) -> ms2.compareTo(ms1))
				.findFirst().get().index;
	}
	
	public static void main(String[] args) {
		JdvUtils.Arquivo.versionamento(99);
		JdvRedeAbstrata rede = new MultilayerPerceptron4("treinamento4").inicializar();
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
