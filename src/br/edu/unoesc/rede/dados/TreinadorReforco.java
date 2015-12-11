package br.edu.unoesc.rede.dados;

import java.time.Duration;
import java.time.LocalTime;
import java.util.stream.Collectors;

import lombok.Getter;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.back.Backpropagation;

import br.edu.unoesc.rede.modelo.JdvRedeAbstrata;
import br.edu.unoesc.util.JdvLog;
import br.edu.unoesc.util.JdvUtils;

/**
 * Treinador responsável por gerenciar as iterações de treinamento da rede.
 * TODO Refatorar métodos.
 * 
 * @author Luiz Felipe Nazari
 */
public class TreinadorReforco implements Treinador {

	private static final double MIN_ERRO_REFORCO = MIN_ERRO + 0.01;

	private ConjuntosDados dados;

	private JdvRedeAbstrata rede;

	MLDataSet conjunto;

	@Getter
	private boolean treinou;

	private boolean conseguiuTreinarComCiclos;

	private Double erro;

	private Double momentum;

	private Double constanteAprendizagem;

	public TreinadorReforco(JdvRedeAbstrata rede, ConjuntosDados dados, Boolean vitoriaOuEmpate) {
		this.rede = rede;
		this.dados = dados;

		this.conjunto = new BasicMLDataSet(dados.getConjuntosTemporarios().stream()
				.map(JdvMLDataPair::getPar).collect(Collectors.toList()));

		double erroAtual = rede.getRede().calculateError(conjunto);
		this.momentum = 0.4;
		this.erro = JdvUtils.RNA.correcaoDeValor(erroAtual, 0.01, false, MAX_ERRO, MIN_ERRO);

		this.constanteAprendizagem = JdvUtils.RNA.correcaoDeValor(erro * 0.85, 0.0, true,
				MAX_CONST_APRENDIZAGEM, MIN_CONST_APRENDIZAGEM);
	}

	@Override
	public void treinar() {
		if (dados.getConjuntosTemporarios().size() <= 0) {
			System.out.println("[Treinador] Não é possível treinar com conjuntos vazios!");
			return;
		}

		System.out.println("[Treinador] Iniciando treinamento.");
		LocalTime inicio = LocalTime.now();

		tentarTreinamento(( BasicNetwork ) rede.getRede().clone());

		dados.armazenarTemporarios();

		if (treinou || conseguiuTreinarComCiclos) {
			System.out.println("[Treinador] Treinamento finalizado. Tempo total: "
					+ Duration.between(inicio, LocalTime.now()));
		} else {
			System.err.println("[Treinador] Não foi possível treinar com os novos conjuntos. Tempo total: "
					+ Duration.between(inicio, LocalTime.now()));
		}
	}

	private void tentarTreinamento(BasicNetwork network) {
		MLTrain treinoCiclo = null;
		int max_iteracoes = ( int ) ((this.erro - MIN_ERRO_REFORCO) * 100);

		if (max_iteracoes <= 0)
			max_iteracoes = 1;

		for (int i = 0; i <= max_iteracoes; i++) {
			treinoCiclo = executaCiclosTreinamento(network, erro);

			// Caso o erro do ciclo for menor do que o mínimo de erro esperado.
			if (treinoCiclo.getError() < MIN_ERRO_REFORCO) {
				treinou = true;
				break;
			}

			// Caso o ciclo não obteve sucesso a tentativa o treinamento é finalizada.
			if (!conseguiuTreinarComCiclos) {
				treinou = false;
				break;
			}

			// Tenta aprimorar o treinamento.

			// Diminui o erro.
			erro = JdvUtils.RNA.correcaoDeValor(erro, 0.01, false, MAX_ERRO, MIN_ERRO_REFORCO);

			// Diminui constante de aprendizagem.
			constanteAprendizagem = JdvUtils.RNA.correcaoDeValor(constanteAprendizagem,
					constanteAprendizagem * 0.05, false, MAX_CONST_APRENDIZAGEM, MIN_CONST_APRENDIZAGEM);
		}

		rede.atualizarRede(( BasicNetwork ) treinoCiclo.getMethod(), constanteAprendizagem, momentum, erro);
	}

	private MLTrain executaCiclosTreinamento(BasicNetwork network, double erroCiclo) {
		MLTrain treino = null;

		for (int i = 1; i < MAX_CICLOS_TREINAMENTO; i++) {
			treino = new Backpropagation(network, conjunto, constanteAprendizagem, momentum);

			int epoca = 1;
			do {
				treino.iteration();

				JdvLog.iteracaoTreinamento(epoca, treino.getError(), erroCiclo);

				epoca++;
			} while ((treino.getError() > erroCiclo) && MAX_ITERACOES >= epoca);

			treino.finishTraining();

			if (treino.getError() <= erroCiclo) {
				conseguiuTreinarComCiclos = true;
				return treino;
			}

			// Aumenta erro.
			erroCiclo = JdvUtils.RNA.correcaoDeValor(erroCiclo, 0.0025, true,
					MAX_ERRO, MIN_ERRO_REFORCO);

			// Aumenta momento.
			momentum = JdvUtils.RNA.correcaoDeValor(momentum, 0.05, true,
					MAX_CONST_MOMENTUM, MIN_CONST_MOMENTUM);
		}

		return treino;
	}

}
