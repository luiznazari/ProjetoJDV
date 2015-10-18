package huehue.br.logica;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import huehue.br.logica.Partida.Jogada;
import huehue.br.modelo.Caractere;
import huehue.br.modelo.Jogador;
import huehue.br.modelo.JogadorHumano;
import huehue.br.util.JdvUtils;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PartidaTest {
	
	private Partida partida;
	
	private Jogador um;
	
	private Jogador dois;
	
	private double[][] tab1_1;
	
	private double[][] tab1_2;
	
	private double[][] tab2_1;
	
	private double[][] tab2_2;
	
	private double[][] tab3_1;
	
	private double[][] tab3_2;
	
	private double[][] tab4_1;
	
	private double[] tab_final;
	
	// -- Testes
	
	@Test
	public void deveIntercalarJogadas() {
		assertTrue(partida.isJogadaPar());
		assertEquals(0, partida.getNumeroJogadas().intValue());
		assertEquals(0, partida.getNumeroPartidas().intValue());
		
		partida.novaJogada(um.getCaractere(), tab1_1[0], ( int ) tab1_1[1][0]);
		
		assertFalse(partida.isJogadaPar());
		assertEquals(1, partida.getNumeroJogadas().intValue());
		
		partida.novaJogada(dois.getCaractere(), tab1_2[0], ( int ) tab1_2[1][0]);
		
		assertTrue(partida.isJogadaPar());
		assertEquals(2, partida.getNumeroJogadas().intValue());
		
		double[] tab_final = new double[] { 1, 0, 0, 0, -1, 0, 0, 0, 0 };
		
		assertNull(partida.temVencedor(tab_final, um, dois));
		assertEquals(2, partida.getJogadasVencedor().size());
	}
	
	@Test
	public void deveComputarVencedor() {
		fazJogadas();
		
		assertEquals(7, partida.getNumeroJogadas().intValue());
		assertEquals(0, partida.getNumeroPartidas().intValue());
		
		assertEquals(um, partida.temVencedor(tab_final, um, dois));
	}
	
	@Test
	public void deveEncerrarPartida() {
		fazJogadas();
		
		assertEquals(7, partida.getNumeroJogadas().intValue());
		assertEquals(0, partida.getNumeroPartidas().intValue());
		assertEquals(um, partida.temVencedor(tab_final, um, dois));
		assertNull(partida.getVencedor());
		
		partida.encerrar(um);
		assertNotNull(partida.getVencedor());
		assertEquals(9, partida.getJogadas().length);
		assertNotNull(partida.getJogadas()[6]); // 7
		assertNull(partida.getJogadas()[7]); // 8
		assertNull(partida.getJogadas()[8]); // 9
		
		partida.novaPartida();
		
		assertEquals(1, partida.getNumeroPartidas().intValue());
	}
	
	@Test
	public void deveRetornarJogadasVencedor() {
		fazJogadas();
		partida.encerrar(um);
		
		List<Jogada> jogadas = partida.getJogadasVencedor();
		
		assertEquals(4, jogadas.size());
		
		for (Jogada j : jogadas) {
			double[] tab = j.getConfiguracao().clone();
			assertEquals(Caractere.VAZIO.getValor(), tab[j.getPosicaoEscolhida()], 0);
		}
		
		assertArrayEquals(tab1_1[0], jogadas.get(0).getConfiguracao(), 0);
		assertEquals(( int ) tab1_1[1][0], jogadas.get(0).getPosicaoEscolhida());
		
		assertArrayEquals(tab2_1[0], jogadas.get(1).getConfiguracao(), 0);
		assertEquals(( int ) tab2_1[1][0], jogadas.get(1).getPosicaoEscolhida());
		
		assertArrayEquals(tab3_1[0], jogadas.get(2).getConfiguracao(), 0);
		assertEquals(( int ) tab3_1[1][0], jogadas.get(2).getPosicaoEscolhida());
		
		assertArrayEquals(tab4_1[0], jogadas.get(3).getConfiguracao(), 0);
		assertEquals(( int ) tab4_1[1][0], jogadas.get(3).getPosicaoEscolhida());
	}
	
	// -- Inicializadores
	
	public void fazJogadas() {
		partida.novaJogada(um.getCaractere(), tab1_1[0].clone(), ( int ) tab1_1[1][0]);
		partida.novaJogada(dois.getCaractere(), tab1_2[0].clone(), ( int ) tab1_2[1][0]);
		
		partida.novaJogada(um.getCaractere(), tab2_1[0].clone(), ( int ) tab2_1[1][0]);
		partida.novaJogada(dois.getCaractere(), tab2_2[0].clone(), ( int ) tab2_2[1][0]);
		
		partida.novaJogada(um.getCaractere(), tab3_1[0].clone(), ( int ) tab3_1[1][0]);
		partida.novaJogada(dois.getCaractere(), tab3_2[0].clone(), ( int ) tab3_2[1][0]);
		
		partida.novaJogada(um.getCaractere(), tab4_1[0].clone(), ( int ) tab4_1[1][0]);
	}
	
	@Before
	public void init() {
		JdvUtils.Log.ativo = false;
		
		um = new JogadorHumano(Caractere.X);
		dois = new JogadorHumano(Caractere.O);
		
		tab1_1 = new double[][] {
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0 }
		};
		
		tab1_2 = new double[][] {
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 4 }
		};
		
		tab2_1 = new double[][] {
			{ 1, 0, 0, 0, -1, 0, 0, 0, 0 },
			{ 8 }
		};
		
		tab2_2 = new double[][] {
			{ 1, 0, 0, 0, -1, 0, 0, 0, 1 },
			{ 2 }
		};
		
		tab3_1 = new double[][] {
			{ 1, 0, -1, 0, -1, 0, 0, 0, 1 },
			{ 6 }
		};
		
		tab3_2 = new double[][] {
			{ 1, 0, -1, 0, -1, 0, 1, 0, 1 },
			{ 1 }
		};
		
		tab4_1 = new double[][] {
			{ 1, -1, -1, 0, -1, 0, 1, 0, 1 },
			{ 7 }
		};
		
		tab_final = new double[] { 1, -1, -1, 0, -1, 0, 1, 1, 1 };
		// Jogador 1 venceu.
		
		partida = new Partida();
	}
}
