package huehue.br.rede.modelo;

import huehue.br.exception.JdvException;
import huehue.br.modelo.Caractere;
import huehue.br.rede.dados.ConjuntosDados;
import huehue.br.rede.dados.TreinadorPadrao;
import huehue.br.util.JdvLog;
import huehue.br.util.JdvUtils;
import lombok.Getter;
import lombok.Setter;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.neural.networks.BasicNetwork;

@Getter
public abstract class JdvRedeAbstrata implements JdvRede {

	private static final String PROP_ERRO = "erro";

	private static final String PROP_MOMENTUM = "momentum";

	private static final String PROP_CONST_APRENDIZAGEM = "constante_aprendizagem";

	@Setter
	protected Double margemDeErro = 0.1D; // 10.0%

	@Setter
	protected Double momentum = 0.4;

	@Setter
	protected Double constanteDeAprendizagem = 0.8;

	protected Integer numeroEntradas;

	protected Integer numeroSaidas;

	protected String tipoTreinamento = MLTrainFactory.TYPE_BACKPROP;

	protected String tipoRede = MLMethodFactory.TYPE_FEEDFORWARD;

	@Setter
	protected String nome;

	@Setter
	protected BasicNetwork rede;

	protected double[] entradasTmp;

	public JdvRedeAbstrata(Integer numeroEntradas, Integer numeroSaidas, String nome) {
		this.numeroEntradas = numeroEntradas;
		this.numeroSaidas = numeroSaidas;
		this.nome = nome;
		resetaEntradasTemporarias();
	}

	public final JdvRedeAbstrata inicializar() {
		this.rede = JdvUtils.Arquivo.carregarRede(this);

		try {
			this.constanteDeAprendizagem = rede.getPropertyDouble(PROP_CONST_APRENDIZAGEM);
			this.margemDeErro = rede.getPropertyDouble(PROP_ERRO);
			this.momentum = rede.getPropertyDouble(PROP_MOMENTUM);
		} catch (NullPointerException e) {}

		return this;
	}

	public final void atualizarRede(BasicNetwork novaRede, double constanteAprendizagem, double momentum, double erro) {
		this.margemDeErro = erro;
		this.momentum = momentum;
		this.constanteDeAprendizagem = constanteAprendizagem;

		this.rede = novaRede;
		this.rede.setProperty(PROP_ERRO, erro);
		this.rede.setProperty(PROP_MOMENTUM, momentum);
		this.rede.setProperty(PROP_CONST_APRENDIZAGEM, constanteAprendizagem);
	}

	@Override
	public String getEstruturaRede() {
		return "?:B->SIGMOID->18:B->SIGMOID->?";
	}

	@Override
	public String getNome() {
		if (this.nome != null)
			return this.nome;
		return JdvRede.super.getNome();
	}

	/**
	 * Traduz o conjunto de posições que formam o tabuleiro ao conjunto de dados reconhecidos pela
	 * mesma (i.e. normaliza os valores e adapta os números correspondendo ao número
	 * {@link JdvRedeAbstrata#numeroEntradas}.<br>
	 * É o processo inverso do método {@link JdvRedeAbstrata#converteEntradaEmTabuleiro(MLData)}<br>
	 * Por padrão, retorna um conjunto de dados representando as entradas recebidas sem tratamentos.
	 * 
	 * @param entradas
	 *            recebidas pela rede.
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
	 *            a saída resultante do processamento da RNA.
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
	 *            a posição do tabuleiro.
	 * @return conjunto de dados representando a posicão.
	 */
	abstract public MLData convertePosicaoTabuleiroEmSaida(int posicao);

	/**
	 * Converte as entradas e posição escolhida em um conjuntos de dados reconhecidos pela RNA com o
	 * tamanho {@link JdvRede#getNumeroSaidas()}, representando o conjunto de saídas utilizados no
	 * treinamento da rede.<br>
	 * É o processo inverso do método {@link JdvRedeAbstrata#traduzirSaida(MLData)}.
	 * 
	 * @param entradas
	 *            as entradas do tabuleiro.
	 * @param posicao
	 *            a posição do tabuleiro.
	 * @return conjunto de dados representando a posicão.
	 */
	public MLData convertePosicaoTabuleiroEmSaida(double[] entradas, int posicao) {
		return convertePosicaoTabuleiroEmSaida(posicao);
	}

	/**
	 * Converte um conjunto de entradas reconhecidas pela rede, com o tamanho {@link JdvRede#getNumeroEntradas()}, para
	 * o conjunto de posições que formam o tabuleiro
	 * respectivo à entrada. <b>Sempre</b> retorna um array de nove posições.<br>
	 * É o processo inverso do método {@link JdvRedeAbstrata#traduzirEntrada(double[])}.
	 * 
	 * @param entrada
	 *            o conjunto de entrada da rede.
	 * @return posições do tabuleiro representando o conjuntos de entrada da rede.
	 */
	public double[] converteEntradaEmTabuleiro(MLData entrada) {
		double[] entradas = entrada.getData();
		double[] tabuleiro = new double[9];

		for (int i = 0; i < 9; i++) {
			if (entradas[i] > 0)
				tabuleiro[i] = Caractere.X.getValor();
			else if (entradas[i] < 0)
				tabuleiro[i] = Caractere.O.getValor();
		}

		return tabuleiro;
	}

	/**
	 * Converte um conjunto de entradas reconhecidas pela rede, com o tamanho {@link JdvRede#getNumeroEntradas()}, para
	 * o conjunto de entradas com tamanho 9, mantendo a normalização.<br>
	 * Utilizado para conversão de entrada entre redes diferentes.
	 * 
	 * @param entrada
	 *            o conjunto de entrada da rede.
	 * @return entrada normalizada com 9 posições.
	 */
	public double[] converteEntradaEmTabuleiroNormalizado(MLData entrada) {
		return entrada.getData();
	}

	/**
	 * Computa a saída da rede às entradas especificadas, normalizando as entradas àquelas
	 * reconhecidas pela rede e a saída à posição do tabuleiro.<br>
	 * A normalização de conjuntos de entradas e saídas pode variar dependendo da implementação da
	 * rede.
	 * 
	 * @param entradas
	 *            array de entradas representando o estado do tabuleiro.
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
	 *            de entradas da rede.
	 * @return conjunto de saídas da rede.
	 */
	private final MLData processar(MLData entrada) {
		entradasTmp = converteEntradaEmTabuleiro(entrada);
		return rede.compute(entrada);
	}

	public final void treinar(final ConjuntosDados dados) {
		this.treinar(dados, null);
	}

	public final void treinar(final ConjuntosDados dados, Boolean vitoriaOuEmpate) {
		new TreinadorPadrao(this, dados, vitoriaOuEmpate).treinar();
//		new TreinadorReforco(this, dados, vitoriaOuEmpate).treinar();
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

		JdvLog.resultado(tamanho, sucesso, falha);
	}

	public void testar(final MLDataPair par) {
		final MLData saida = processar(par.getInput());
		JdvLog.resultado(par, saida, this);
	}

	private void resetaEntradasTemporarias() {
		this.entradasTmp = new double[9];
	}

	@Override
	public BasicNetwork getRede() {
		if (this.rede == null)
			throw new JdvException("A rede neuronal ainda não foi inicializada! Experimente o método " + this.getNome()
					+ ".inicializar().");

		return this.rede;
	}

}
