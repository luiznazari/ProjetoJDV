package huehue.br.encog.modelo;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

/**
 * Extensão da classe {@link BasicMLData} para suportar o método <code>equals</code>.
 * 
 * @author Luiz Felipe Nazari
 */
public class JdvMLData extends BasicMLData {
	
	private static final long serialVersionUID = 3974395302901861769L;
	
	public JdvMLData(double[] d) {
		super(d);
	}
	
	public JdvMLData(double d) {
		this(new double[] {
			d
		});
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MLData))
			return false;
		if (obj == this)
			return true;
		
		MLData mlData = ( MLData ) obj;
		
		int size = this.size();
		if (mlData.size() != size)
			return false;
		
		for (int i = 0; i < size; i++)
			if (mlData.getData(i) != this.getData(i))
				return false;
		
		return true;
	}
	
}
