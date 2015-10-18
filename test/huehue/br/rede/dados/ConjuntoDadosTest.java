package huehue.br.rede.dados;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.junit.Before;
import org.junit.Test;

public class ConjuntoDadosTest {
	
	private BasicMLData[] par1;
	
	private BasicMLData[] par2;
	
	private BasicMLData[] par3;
	
	private ConjuntosDados conjuntosDados;
	
	@Before
	public void init() {
		
		// -- 1
		double[] entradas1 = new double[] {
			0, 1, 2, 3
		};
		double[] saidas1 = new double[] {
			0, -1, -2, -3
		};
		par1 = criaDataArray(entradas1, saidas1);
		
		// -- 2
		
		double[] saidas2 = new double[] {
			-3, -4, -5, -6
		};
		par2 = criaDataArray(entradas1.clone(), saidas2);
		
		// -- 3
		
		double[] entradas3 = new double[] {
			6, 7, 8
		};
		double[] saidas3 = new double[] {
			6, 7, 8
		};
		par3 = criaDataArray(entradas3, saidas3);
		
		// -- ConjuntoDados
		
		// @formatter:off
		double[][] entradasSet = {
				{11, 22, 33, 44},
				entradas3.clone(),
				{55, 66, 77},
		};
		double[][] saidasSet = {
				{44, 33, 22, 11},
				saidas3.clone(),
				{77, 66, 55},
		};
		// @formatter:on
		BasicMLDataSet set = new BasicMLDataSet(entradasSet, saidasSet);
		conjuntosDados = new ConjuntosDados(set);
		
	}
	
	public BasicMLData[] criaDataArray(double[] entradas, double[] saidas) {
		return new BasicMLData[] {
			new BasicMLData(entradas), new BasicMLData(saidas)
		};
	}
	
	@Test
	public void naoDeveAdicionarRepetidos() {
		assertEquals(3, conjuntosDados.getConjuntos().size());
		
		conjuntosDados.setSubstituirRepetidos(false);
		conjuntosDados.adicionarDadoES(par1[0], par1[1]); // Adiciona o par1
		conjuntosDados.adicionarDadoES(par2[0], par2[1]); // Não adiciona par2
		conjuntosDados.adicionarDadoES(par3[0], par3[1]); // Não adiciona par3
		
		assertEquals(4, conjuntosDados.getConjuntos().size());
		
		// Mesmas entradas do par 1. Saídas do par 1.
		assertArrayEquals(par1[0].getData(), conjuntosDados.getConjuntos().get(3).getInputArray(), 0);
		assertArrayEquals(par1[1].getData(), conjuntosDados.getConjuntos().get(3).getIdealArray(), 0);
		
		// Mesmas entradas do elemento 2 original.
		assertArrayEquals(par3[0].getData(), conjuntosDados.getConjuntos().get(1).getInputArray(), 0);
		assertArrayEquals(par3[1].getData(), conjuntosDados.getConjuntos().get(1).getIdealArray(), 0);
	}
	
	@Test
	public void deveSubstituirRepetidos() {
		assertEquals(3, conjuntosDados.getConjuntos().size());
		
		conjuntosDados.setSubstituirRepetidos(true);
		conjuntosDados.adicionarDadoES(par1[0], par1[1]); // Adiciona o par1
		
		assertArrayEquals(par1[0].getData(), conjuntosDados.getConjuntos().get(3).getInputArray(), 0);
		assertArrayEquals(par1[1].getData(), conjuntosDados.getConjuntos().get(3).getIdealArray(), 0);
		
		conjuntosDados.adicionarDadoES(par2[0], par2[1]); // Substitui o par1 pelo par2
		
		assertArrayEquals(par2[0].getData(), conjuntosDados.getConjuntos().get(3).getInputArray(), 0);
		assertArrayEquals(par2[1].getData(), conjuntosDados.getConjuntos().get(3).getIdealArray(), 0);
		
		conjuntosDados.adicionarDadoES(par3[0], par3[1]); // Não adiciona par3 pois já existe
		
		// Mesmas entradas do elemento 2 original.
		assertArrayEquals(par3[0].getData(), conjuntosDados.getConjuntos().get(1).getInputArray(), 0);
		assertArrayEquals(par3[1].getData(), conjuntosDados.getConjuntos().get(1).getIdealArray(), 0);
		
		assertEquals(4, conjuntosDados.getConjuntos().size());
	}
	
	@Test
	public void deveAdicionarTemporariosSubstituindoRepetidos() {
		assertEquals(3, conjuntosDados.getConjuntos().size());
		assertEquals(0, conjuntosDados.getConjuntosTemporarios().size());
		
		conjuntosDados.setSubstituirRepetidos(true);
		conjuntosDados.adicionarDadoESTemporario(par1[0], par1[1]); // Adiciona o par1
		conjuntosDados.adicionarDadoESTemporario(par2[0], par2[1]); // Substitui o par1 pelo 2
		conjuntosDados.adicionarDadoESTemporario(par3[0], par3[1]); // Adiciona o par3
		
		assertEquals(3, conjuntosDados.getConjuntos().size());
		assertEquals(2, conjuntosDados.getConjuntosTemporarios().size());
		
		assertArrayEquals(par2[0].getData(), conjuntosDados.getConjuntosTemporarios().get(0).getInputArray(), 0);
		assertArrayEquals(par2[1].getData(), conjuntosDados.getConjuntosTemporarios().get(0).getIdealArray(), 0);
		
		assertArrayEquals(par3[0].getData(), conjuntosDados.getConjuntosTemporarios().get(1).getInputArray(), 0);
		assertArrayEquals(par3[1].getData(), conjuntosDados.getConjuntosTemporarios().get(1).getIdealArray(), 0);
		
		conjuntosDados.descartarTemporarios();
		assertEquals(0, conjuntosDados.getConjuntosTemporarios().size());
	}
	
	@Test
	public void deveIncluirTemporariosNosConjuntos() {
		assertEquals(3, conjuntosDados.getConjuntos().size());
		assertEquals(0, conjuntosDados.getConjuntosTemporarios().size());
		
		conjuntosDados.setSubstituirRepetidos(true);
		conjuntosDados.adicionarDadoESTemporario(par1[0], par1[1]);
		conjuntosDados.adicionarDadoESTemporario(par2[0], par2[1]);
		conjuntosDados.adicionarDadoESTemporario(par3[0], par3[1]);
		
		assertEquals(3, conjuntosDados.getConjuntos().size());
		assertEquals(2, conjuntosDados.getConjuntosTemporarios().size());
		
		// Adiciona o par1
		// Substitui o par1 pelo par2
		// Não adiciona o par 3
		conjuntosDados.armazenarTemporarios();
		
		assertEquals(4, conjuntosDados.getConjuntos().size());
		assertEquals(0, conjuntosDados.getConjuntosTemporarios().size());
	}
	
	@Test
	public void deveEmbaralharConjuntosDeDados() {
		assertEquals(3, conjuntosDados.getConjuntos().size());
		
		conjuntosDados.embaralhar();
		
		assertEquals(3, conjuntosDados.getConjuntos().size());
	}
}
