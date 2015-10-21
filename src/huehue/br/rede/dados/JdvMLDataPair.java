package huehue.br.rede.dados;

import java.util.Arrays;

import lombok.Getter;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;

/**
 * Extensão da classe {@link BasicMLDataPair} para suportar o método <code>equals</code>.
 * 
 * @author Luiz Felipe Nazari
 */
@Getter
public class JdvMLDataPair {
	
	public static final int ENTRADAS_ADICIONAIS = 2;
	
	private int pontos;
	
	// Frequência do par em casos com vitória.
	private int frequencia;
	
	private MLDataPair par;
	
	public JdvMLDataPair(MLDataPair par, int frequencia, int pontos) {
		this.par = par;
		this.frequencia = frequencia;
		this.pontos = pontos;
	}
	
	public JdvMLDataPair(MLData theInput, MLData theIdeal, int frequencia, int pontos) {
		this(new BasicMLDataPair(theInput, theIdeal), frequencia, pontos);
	}
	
	public JdvMLDataPair(double[] entradas, double[] saidas, int frequencia, int pontos) {
		this(new BasicMLData(entradas), new BasicMLData(saidas), frequencia, pontos);
	}
	
	public void incrementaFrequencia() {
		this.frequencia++;
	}
	
	public boolean isEntradasIguais(MLDataPair mlDataPair) {
		return isDadosIguais(par.getInput(), mlDataPair.getInput());
	}
	
	public boolean isSaidasIguais(MLDataPair mlDataPair) {
		return par.isSupervised() && isDadosIguais(par.getIdeal(), mlDataPair.getIdeal());
	}
	
	private boolean isDadosIguais(MLData dado1, MLData dado2) {
		int size = dado1.size();
		if (size != dado2.size())
			return false;
		
		for (int i = 0; i < size; i++)
			if ((dado1.getData(i) > 0 && dado2.getData(i) <= 0)
				|| (dado1.getData(i) < 0 && dado2.getData(i) >= 0)
				|| (dado1.getData(i) == 0 && dado2.getData(i) != 0))
				return false;
		
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MLDataPair))
			return false;
		if (obj == this)
			return true;
		
		MLDataPair mlDataPair = ( MLDataPair ) obj;
		
		return this.isEntradasIguais(mlDataPair) && isSaidasIguais(mlDataPair);
	}
	
	public static JdvMLDataPair criaAPartirDeArquivo(MLDataPair par) {
		double[] entradas = par.getInputArray();
		int len = entradas.length;
		int frequencia = ( int ) entradas[len - 2];
		int pontos = ( int ) entradas[len - 1];
		entradas = Arrays.copyOfRange(entradas, 0, len - ENTRADAS_ADICIONAIS);
		
		return new JdvMLDataPair(entradas, par.getIdealArray(), frequencia, pontos);
	}
	
	public MLDataPair getParaSalvar() {
		int len = this.par.getInputArray().length;
		double[] entradas = Arrays.copyOf(par.getInputArray(), len + ENTRADAS_ADICIONAIS);
		entradas[len] = frequencia;
		entradas[len + 1] = pontos;
		
		return new BasicMLDataPair(new BasicMLData(entradas), this.par.getIdeal());
	}
	
}
