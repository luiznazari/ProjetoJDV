package br.edu.unoesc.rede.dados;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.junit.Assert;
import org.junit.Test;

import br.edu.unoesc.rede.dados.JdvMLDataPair;

public class JdvMLDataPairTest {
	
	@Test
	public void deveIndicarDadosComEntradasIguais() {
		MLData data1 = new BasicMLData(new double[] {
			0, 0.9, 0.6, 0, 0, -0.4
		});
		JdvMLDataPair par1 = new JdvMLDataPair(data1, null, 0, 0);
		
		MLData data2 = new BasicMLData(new double[] {
			0, 0.4, 0.6, 0, 0, -0.9
		});
		JdvMLDataPair par2 = new JdvMLDataPair(data2, null, 0, 0);
		
		Assert.assertTrue(par1.isEntradasIguais(par2.getPar()));
	}
	
	@Test
	public void deveIndicarDadosComEntradasIguais_2() {
		MLData data1 = new BasicMLData(new double[] {
			0.1, 0.2, 0.3, 0.4
		});
		JdvMLDataPair par1 = new JdvMLDataPair(data1, null, 0, 0);
		
		MLData data2 = new BasicMLData(new double[] {
			0.5, 0.6, 0.7, 0.8
		});
		JdvMLDataPair par2 = new JdvMLDataPair(data2, null, 0, 0);
		
		Assert.assertTrue(par1.isEntradasIguais(par2.getPar()));
		
		data1 = new BasicMLData(new double[] {
			-0.1, -0.2, -0.3, -0.4
		});
		par1 = new JdvMLDataPair(data1, null, 0, 0);
		
		data2 = new BasicMLData(new double[] {
			-0.5, -0.6, -0.7, -0.8
		});
		par2 = new JdvMLDataPair(data2, null, 0, 0);
		
		Assert.assertTrue(par1.isEntradasIguais(par2.getPar()));
		
		data1 = new BasicMLData(new double[] {
			0, 0, 0, 0
		});
		par1 = new JdvMLDataPair(data1, null, 0, 0);
		
		data2 = new BasicMLData(new double[] {
			0, 0, 0, 0
		});
		par2 = new JdvMLDataPair(data2, null, 0, 0);
		
		Assert.assertTrue(par1.isEntradasIguais(par2.getPar()));
	}
	
	@Test
	public void deveIndicarDadosComEntradasDiferentes() {
		MLData data1 = new BasicMLData(new double[] {
			0, 0.9, 0.6, 0, 0, -0.4
		});
		JdvMLDataPair par1 = new JdvMLDataPair(data1, null, 0, 0);
		
		MLData data2 = new BasicMLData(new double[] {
			0, 0.4, -0.6, 0, 0, -0.9
		});
		JdvMLDataPair par2 = new JdvMLDataPair(data2, null, 0, 0);
		
		Assert.assertFalse(par1.isEntradasIguais(par2.getPar()));
	}
	
	@Test
	public void naoDeveDarErroQuandoTamanhosSaoDiferentes_2() {
		MLData data1 = new BasicMLData(new double[] {
			0, 0, 0.6, 0, 0, -0.4
		});
		JdvMLDataPair par1 = new JdvMLDataPair(data1, null, 0, 0);
		
		MLData data2 = new BasicMLData(new double[] {
			0, 0, 0, 0, 0, -0.9
		});
		JdvMLDataPair par2 = new JdvMLDataPair(data2, null, 0, 0);
		
		Assert.assertFalse(par1.isEntradasIguais(par2.getPar()));
		
		data1 = new BasicMLData(new double[] {
			0, 0, 0, 0
		});
		par1 = new JdvMLDataPair(data1, null, 0, 0);
		
		data2 = new BasicMLData(new double[] {
			0, 0, 0, 0.1
		});
		par2 = new JdvMLDataPair(data2, null, 0, 0);
		
		Assert.assertFalse(par1.isEntradasIguais(par2.getPar()));
	}
	
	@Test
	public void naoDeveDarErroQuandoTamanhosSaoDiferentes() {
		MLData data1 = new BasicMLData(new double[] {
			0, 0.9
		});
		JdvMLDataPair par1 = new JdvMLDataPair(data1, null, 0, 0);
		
		MLData data2 = new BasicMLData(new double[] {
			0
		});
		JdvMLDataPair par2 = new JdvMLDataPair(data2, null, 0, 0);
		
		Assert.assertFalse(par1.isEntradasIguais(par2.getPar()));
	}
	
}
