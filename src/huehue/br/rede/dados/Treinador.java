package huehue.br.rede.dados;

import huehue.br.rede.modelo.JdvRedeAbstrata;
import huehue.br.util.JdvLog;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.back.Backpropagation;

/**
 * Treinador responsável por gerenciar as iterações de treinamento da rede.
 * TODO Refatorar métodos.
 * 
 * @author Luiz Felipe Nazari
 */
public class Treinador {

	private static final double MAX_CONST_MOMENTUM = 0.9;

	private static final double MAX_CONST_APRENDIZAGEM = 0.8;

	private static final double MIN_CONST_APRENDIZAGEM = 0.1;

	private static final int MAX_ITERACOES = 50;

	private static final int MAX_CICLOS_TREINAMENTO = 5;

	private ConjuntosDados dados;

	private final List<JdvMLDataPair> conjuntosTreinamento;

	private JdvRedeAbstrata rede;

	@Getter
	private boolean treinou;

	private Double erro;

	@Getter
	private Double momentum;

	@Getter
	private Double constanteAprendizagem;

	public Treinador(JdvRedeAbstrata rede, ConjuntosDados dados, Boolean vitoriaOuEmpate) {
		this.rede = rede;
		this.dados = dados;
		this.conjuntosTreinamento = dados.getConjuntosParaTreinamento();

		this.erro = rede.getMargemDeErro();
		this.momentum = rede.getMomentum();
		this.constanteAprendizagem = rede.getConstanteDeAprendizagem();

		if (dados.getConjuntos().size() != conjuntosTreinamento.size())
			this.erro -= 0.01;

		if (vitoriaOuEmpate != null) {
			if (vitoriaOuEmpate.booleanValue())
				this.constanteAprendizagem -= this.constanteAprendizagem * 0.05; // -5%
			else
				this.constanteAprendizagem += this.constanteAprendizagem * 0.05; // +5%

			if (this.constanteAprendizagem > MAX_CONST_APRENDIZAGEM)
				this.constanteAprendizagem = MAX_CONST_APRENDIZAGEM;
			else if (this.constanteAprendizagem < MIN_CONST_APRENDIZAGEM)
				this.constanteAprendizagem = MIN_CONST_APRENDIZAGEM;
		}
	}

	public void treinar() {
		System.out.println("[Treinador] Iniciando treinamento.");
		LocalDateTime inicio = LocalDateTime.now();

		tentarTreinamento(( BasicNetwork ) rede.getRede().clone());

		if (treinou) {
			dados.setConjuntos(conjuntosTreinamento);

			System.out.println("[Treinador] Treinamento finalizado. Tempo total: "
					+ Duration.between(inicio, LocalDateTime.now()));
		} else {
			dados.descartarTemporarios();

			System.err.println("[Treinador] Não foi possível treinar com os novos conjuntos. Tempo total: "
					+ Duration.between(inicio, LocalDateTime.now()));
		}
	}

	/*
	 * Calcular o erro:
	 * rede.getRede().calculateError(data)
	 */

	private void tentarTreinamento(BasicNetwork network) {
		MLDataSet conjunto = new BasicMLDataSet(conjuntosTreinamento.stream().map(JdvMLDataPair::getPar)
				.collect(Collectors.toList()));

		this.treinou = false;

		for (int i = 1; i <= MAX_CICLOS_TREINAMENTO; i++) {
			MLTrain treino = new Backpropagation(network, conjunto, this.constanteAprendizagem, this.momentum);

			int epoca = 1;
			do {
				treino.iteration();

				JdvLog.iteracaoTreinamento(epoca, treino.getError(), erro);

				epoca++;
			} while ((treino.getError() > erro) && MAX_ITERACOES >= epoca);

			treino.finishTraining();

			if (treino.getError() <= erro) {
				this.treinou = true;
				this.rede.atualizarRede(( BasicNetwork ) treino.getMethod(), constanteAprendizagem, momentum);
				return;

			} else {
				this.momentum += momentum * 0.1;

				if (this.momentum > MAX_CONST_MOMENTUM)
					this.momentum = MAX_CONST_MOMENTUM;

				System.out.println("[Treinador] Ciclo #" + i + " de treinamento não obteve êxito.");
			}
		}
	}
}
