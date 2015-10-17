package huehue.br.encog.modelo;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLDataPair;

/**
 * Extensão da classe {@link BasicMLDataPair} para suportar o método <code>equals</code>.
 * 
 * @author Luiz Felipe Nazari
 */
public class JdvMLDataPair extends BasicMLDataPair {
	
	private static final long serialVersionUID = 2284272303510690354L;
	
	public JdvMLDataPair(MLDataPair par) {
		this(par.getInput(), par.getIdeal());
	}
	
	public JdvMLDataPair(MLData theInput, MLData theIdeal) {
		super(theInput, theIdeal);
	}
	
	public JdvMLDataPair(double[] entradas, double[] saidas) {
		this(new JdvMLData(entradas), new JdvMLData(saidas));
	}
	
	public boolean isEntradasIguais(MLDataPair mlDataPair) {
		return this.getInput().equals(mlDataPair.getInput());
	}
	
	public boolean isSaidasIguais(MLDataPair mlDataPair) {
		return this.isSupervised() && this.getIdeal().equals(mlDataPair.getIdeal());
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
}
