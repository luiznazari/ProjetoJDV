package huehue.br.rede.dados;

import huehue.br.rede.modelo.JdvRedeAbstrata;
import huehue.br.util.JdvLog;
import huehue.br.util.JdvUtils;

import java.time.Duration;
import java.time.LocalTime;
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
public class TreinadorPadrao implements Treinador {

	private ConjuntosDados dados;

	private final List<JdvMLDataPair> conjuntosTreinamento;

	private JdvRedeAbstrata rede;

	@Getter
	private boolean treinou;

	private Double erro;

	private Double momentum;

	private Double constanteAprendizagem;

	public TreinadorPadrao(JdvRedeAbstrata rede, ConjuntosDados dados, Boolean vitoriaOuEmpate) {
		this.rede = rede;
		this.dados = dados;
		this.conjuntosTreinamento = dados.getConjuntosParaTreinamento();

		this.erro = rede.getMargemDeErro();
		this.momentum = rede.getMomentum();
		this.constanteAprendizagem = rede.getConstanteDeAprendizagem();

		balanceiaParametros(vitoriaOuEmpate);
	}

	@Override
	public void treinar() {
		if (conjuntosTreinamento.size() <= 0) {
			System.out.println("[Treinador] Não é possível treinar com conjuntos vazios!");
			return;
		}

		System.out.println("[Treinador] Iniciando treinamento.");
		LocalTime inicio = LocalTime.now();

		tentarTreinamento(( BasicNetwork ) rede.getRede().clone());

		if (treinou) {
			dados.setConjuntos(conjuntosTreinamento);

			System.out.println("[Treinador] Treinamento finalizado. Tempo total: "
					+ Duration.between(inicio, LocalTime.now()));
		} else {
			dados.descartarTemporarios();

			System.err.println("[Treinador] Não foi possível treinar com os novos conjuntos. Tempo total: "
					+ Duration.between(inicio, LocalTime.now()));
		}
	}

	/*
	 * Calcular o erro:
	 * rede.getRede().calculateError(data)
	 */

	private void tentarTreinamento(BasicNetwork network) {
		MLDataSet conjunto = new BasicMLDataSet(conjuntosTreinamento.stream().map(JdvMLDataPair::getPar)
				.collect(Collectors.toList()));

		this.momentum = 0.4;
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
				this.rede.atualizarRede(( BasicNetwork ) treino.getMethod(), constanteAprendizagem, momentum, erro);
				return;

			} else {
				this.erro = JdvUtils.RNA.correcaoDeValor(this.erro, 0.0025, true, MAX_ERRO, MIN_ERRO);
				this.momentum = JdvUtils.RNA.correcaoDeValor(this.momentum, 0.05, true,
						MAX_CONST_MOMENTUM, MIN_CONST_MOMENTUM);

				System.out.println("[Treinador] Ciclo #" + i + " de treinamento não obteve êxito.");
			}
		}
	}

	private void balanceiaParametros(Boolean vitoriaOuEmpate) {
		boolean temNovosConjuntos = dados.getConjuntos().size() != conjuntosTreinamento.size();
		this.erro = JdvUtils.RNA.correcaoDeValor(this.erro, 0.0025, temNovosConjuntos, MAX_ERRO, MIN_ERRO);

		if (vitoriaOuEmpate != null) {
			this.constanteAprendizagem = JdvUtils.RNA.correcaoDeValor(this.constanteAprendizagem,
					this.constanteAprendizagem * 0.05, !vitoriaOuEmpate.booleanValue(),
					MAX_CONST_APRENDIZAGEM, MIN_CONST_APRENDIZAGEM);
		}
	}

}
