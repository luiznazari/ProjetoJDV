package huehue.br.rede.modelo;

import huehue.br.modelo.Caractere;
import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.util.JdvUtils;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

@Getter
public class MultilayerPerceptron2 extends JdvRedeAbstrata {
	
	private double[] entradasTmp;
	
	public MultilayerPerceptron2(Integer numeroEntradas, Integer numeroSaidas) {
		super(numeroEntradas, numeroSaidas);
		resetaEntradasTemporarias();
	}
	
	public MultilayerPerceptron2() {
		this(9, 9);
		momentum = 0.4;
		margemDeErro = 0.07D;
		constanteDeAprendizagem = 0.05;
	}
	
	@Override
	public String getEstruturaRede() {
		return "?:B->SIGMOID->5:B->SIGMOID->?";
	}
	
	@Override
	public MLData traduzirEntrada(double[] entradas) {
		entradasTmp = entradas;
		return super.traduzirEntrada(entradas);
	}
	
	/**
	 * {@inheritDoc}<br>
	 * <br>
	 * Será escolhida a saída com o maior valor cuja posição no tabuleiro esteja vazia (baseado nos
	 * sinais de entrada recebidos). Têm como referência de comparação o {@link Caractere#VAZIO}.
	 */
	@Override
	public int traduzirSaida(MLData saida) {
		List<MapaSaida> listaSaida = new ArrayList<>();
		
		for (int i = 0; i < getNumeroSaidas(); i++)
			listaSaida.add(new MapaSaida(i, saida.getData(i)));
		
		int index = listaSaida.stream().sorted((ms1, ms2) -> ms2.compareTo(ms1))
				.filter(ms -> entradasTmp[ms.index] == Caractere.VAZIO.getValor())
				.findFirst().orElse(MapaSaida.padrao()).index;
		
		resetaEntradasTemporarias();
		return index;
	}
	
	@Override
	public MLData convertePosicaoTabuleiroEmSaida(int posicao) {
		double[] dd = new double[9];
		
		dd[posicao] = Caractere.X.getValor();
		
		return new BasicMLData(dd);
	}
	
	private void resetaEntradasTemporarias() {
		entradasTmp = new double[9];
	}
	
	private static class MapaSaida implements Comparable<MapaSaida> {
		public int index;
		
		public double valor;
		
		public MapaSaida(int index, double valor) {
			this.index = index;
			this.valor = valor;
		}
		
		public static MapaSaida padrao() {
			return new MapaSaida(0, 0);
		}
		
		@Override
		public int compareTo(MapaSaida o) {
			return this.valor > o.valor ? 1 : -1;
		}
	}
	
	public static void main(String[] args) {
		JdvUtils.Arquivo.versionamento(455);
		JdvRedeAbstrata rede = new MultilayerPerceptron2();
		ConjuntosDados dados = new ConjuntosDados(JdvUtils.Arquivo.carregarDados(rede));
		
		rede.treinar(dados);
		
		JdvUtils.Arquivo.incrementaVersao();
		
		JdvUtils.Arquivo.salvarRede(rede);
		JdvUtils.Arquivo.salvarDados(rede, dados.getMLDataSet());
	}
}
