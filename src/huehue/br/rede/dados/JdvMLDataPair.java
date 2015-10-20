package huehue.br.rede.dados;

import lombok.Getter;
import lombok.Setter;

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
@Setter
public class JdvMLDataPair extends BasicMLDataPair {
	private static final long serialVersionUID = -6061475918446115955L;
	
	public static final int ENTRADAS_ADICIONAIS = 2;
	
	private int pontos;
	
	// Frequência do par em casos com vitória.
	private int frequencia;
	
	private MLDataPair par;
	
	public JdvMLDataPair(MLDataPair par) {
		this(par.getInput(), par.getIdeal());
	}
	
	public JdvMLDataPair(MLData theInput, MLData theIdeal) {
		super(theInput, theIdeal);
	}
	
	public JdvMLDataPair(double[] entradas, double[] saidas) {
		this(new BasicMLData(entradas), new BasicMLData(saidas));
	}
	
	public boolean isEntradasIguais(MLDataPair mlDataPair) {
		return isDadosIguais(this.getInput(), mlDataPair.getInput());
	}
	
	public boolean isSaidasIguais(MLDataPair mlDataPair) {
		return this.isSupervised() && isDadosIguais(this.getIdeal(), mlDataPair.getIdeal());
	}
	
	private boolean isDadosIguais(MLData dado1, MLData dado2) {
		int size = dado1.size();
		if (size != dado2.size())
			return false;
		
		for (int i = 0; i < size; i++)
			if (dado1.getData(i) != dado2.getData(i))
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
	
}
