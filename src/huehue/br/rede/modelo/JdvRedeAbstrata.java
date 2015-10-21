package huehue.br.rede.modelo;

import huehue.br.exception.JdvException;
import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.util.JdvUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.util.simple.EncogUtility;

@Getter
public abstract class JdvRedeAbstrata implements JdvRede {
	
//	private static final String REDE_FEEDFORWARD_TANH = "?:B->TANH->5:B->TANH->?";
	
	private static final String REDE_FEEDFORWARD_SIGMOID = "?:B->SIGMOID->5:B->SIGMOID->?";
	
//	private static final String REDE_FEEDFORWARD_SEM_BIAS = "?->SIGMOID->5->SIGMOID->?";
	
	protected Double margemDeErro = 0.1D; // 10.0%
	
	protected Double constanteDeAprendizagem = 0.1;
	
	protected Double momentum = 0.4;
	
	protected Integer numeroEntradas;
	
	protected Integer numeroSaidas;
	
	protected String tipoTreinamento = MLTrainFactory.TYPE_BACKPROP;
	
	protected String tipoRede = MLMethodFactory.TYPE_FEEDFORWARD;
	
	@Setter
	protected BasicNetwork rede;
	
	protected double[] entradasTmp;
	
	public JdvRedeAbstrata(Integer numeroEntradas, Integer numeroSaidas) {
		this.numeroEntradas = numeroEntradas;
		this.numeroSaidas = numeroSaidas;
		resetaEntradasTemporarias();
	}
	
	public final JdvRedeAbstrata inicializar() {
		this.rede = JdvUtils.Arquivo.carregarRede(this);
		return this;
	}
	
	@Override
	public String getEstruturaRede() {
		return REDE_FEEDFORWARD_SIGMOID;
	}
	
	/**
	 * Traduz o conjunto de posições que formam o tabuleiro ao conjunto de dados reconhecidos pela
	 * mesma (i.e. normaliza os valores e adapta os números correspondendo ao número
	 * {@link JdvRedeAbstrata#numeroEntradas}.<br>
	 * É o processo inverso do método {@link JdvRedeAbstrata#converteEntradaEmTabuleiro(MLData)}<br>
	 * Por padrão, retorna um conjunto de dados representando as entradas recebidas sem tratamentos.
	 * 
	 * @param entradas
	 *        recebidas pela rede.
	 * @return conjunto de dados representando a entrada da rede.
	 */
	public MLData traduzirEntrada(double[] entradas) {
		return new BasicMLData(entradas);
	}
	
	/**
	 * Traduz o valor da saída resultante do processamento da RNA.<br>
	 * É o processo inverso do método {@link JdvRedeAbstrata#convertePosicaoTabuleiroEmSaida(int)}.
	 * 
	 * @param d
	 *        a saída resultante do processamento da RNA.
	 * @return valor da saída traduzido, onde: 0 <= valor <= 8.
	 */
	abstract public int traduzirSaida(MLData saida);
	
	/**
	 * Converte uma posição do tabuleiro em um conjuntos de dados reconhecidos pela RNA com o
	 * tamanho {@link JdvRede#getNumeroSaidas()}, representando o conjunto de saídas utilizados no
	 * treinamento da rede.<br>
	 * É o processo inverso do método {@link JdvRedeAbstrata#traduzirSaida(MLData)}.
	 * 
	 * @param posicao
	 *        a posição do tabuleiro.
	 * @return conjunto de dados representando a posicão.
	 */
	abstract public MLData convertePosicaoTabuleiroEmSaida(int posicao);
	
	/**
	 * Converte um conjunto de entradas reconhecidas pela rede, com o tamanho
	 * {@link JdvRede#getNumeroEntradas()}, para o conjunto de posições que formam o tabuleiro
	 * respectivo à entrada. <b>Sempre</b> retorna um array de nove posições.<br>
	 * É o processo inverso do método {@link JdvRedeAbstrata#traduzirEntrada(double[])}.
	 * 
	 * @param entrada
	 *        o conjunto de entrada da rede.
	 * @return posições do tabuleiro representando o conjuntos de entrada da rede.
	 */
	public double[] converteEntradaEmTabuleiro(MLData entrada) {
		return entrada.getData().clone();
	}
	
	/**
	 * Computa a saída da rede às entradas especificadas, normalizando as entradas àquelas
	 * reconhecidas pela rede e a saída à posição do tabuleiro.<br>
	 * A normalização de conjuntos de entradas e saídas pode variar dependendo da implementação da
	 * rede.
	 * 
	 * @param entradas
	 *        array de entradas representando o estado do tabuleiro.
	 * @return posicão escolhida pela rede.
	 * @see JdvRedeAbstrata#traduzirEntrada(double[])
	 * @see JdvRedeAbstrata#traduzirSaida(MLData)
	 */
	public int processar(double[] entradas) {
		MLData dadosEntrada = traduzirEntrada(entradas);
		MLData dadosSaida = processar(dadosEntrada);
		
		return traduzirSaida(dadosSaida);
	}
	
	/**
	 * Computa a saída da rede à entrada especificada.
	 * 
	 * @param conjunto
	 *        de entradas da rede.
	 * @return conjunto de saídas da rede.
	 */
	private final MLData processar(MLData entrada) {
		entradasTmp = converteEntradaEmTabuleiro(entrada);
		return rede.compute(entrada);
	}
	
	public final void treinar(final ConjuntosDados dados) {
		this.treinar(dados, 0);
	}
	
	public final void treinar(final ConjuntosDados dados, int tamanhoBlocos) {
		dados.embaralhar();
		
		List<MLDataPair> pares = dados.getConjuntos();
		int len = pares.size();
		if (len <= 0)
			throw new JdvException("Não é possível realizar o treinamento com um conjunto vazio!");
		
		if (tamanhoBlocos <= 0)
			tamanhoBlocos = len;
		
		int iteracoes = len / tamanhoBlocos;
		
		if (len % tamanhoBlocos != 0)
			iteracoes += 1;
		
		LocalDateTime inicio = LocalDateTime.now();
		for (int i = 0; i < iteracoes; i++) {
			int lenIteracao = (i < iteracoes - 1 ? tamanhoBlocos : len % tamanhoBlocos) + 1;
			MLDataSet set = new BasicMLDataSet(pares.subList(i * tamanhoBlocos, i * tamanhoBlocos + lenIteracao));
			
			MLTrain train = new Backpropagation(getRede(), set, constanteDeAprendizagem, momentum);
			
			EncogUtility.trainToError(train, getMargemDeErro());
		}
		
		System.out.println("Treinamento finalizado. Tempo total: " + Duration.between(inicio, LocalDateTime.now()));
	}
	
	public void testar(final MLDataSet conjuntoDados) {
		int tamanho = conjuntoDados.size();
		int sucesso = 0;
		int falha = 0;
		
		for (final MLDataPair par : conjuntoDados) {
			final MLData saida = processar(par.getInput());
			int posicao = this.traduzirSaida(saida);
			int esperado = this.traduzirSaida(par.getIdeal());
			
			if (posicao == esperado)
				sucesso++;
			else
				falha++;
		}
		
		JdvUtils.Log.resultado(tamanho, sucesso, falha);
	}
	
	public void testar(final MLDataPair par) {
		final MLData saida = processar(par.getInput());
		JdvUtils.Log.resultado(par, saida, this);
	}
	
	private void resetaEntradasTemporarias() {
		this.entradasTmp = new double[9];
	}
	
	@Override
	public BasicNetwork getRede() {
		if (this.rede == null)
			throw new JdvException("A rede neuronal ainda não foi inicializada! Experimente o método " + this.getNome() + ".inicializar().");
		
		return this.rede;
	}
	
}
