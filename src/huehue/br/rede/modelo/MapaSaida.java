package huehue.br.rede.modelo;

/**
 * Classe que representa o mapeamento de uma saída de uma {@link JdvRede}, armazenando os valores e
 * índices originais.
 * 
 * @author Luiz Felipe Nazari
 */
public class MapaSaida implements Comparable<MapaSaida> {
	
	public int index;
	
	public double valor;
	
	public MapaSaida(int index, double valor) {
		this.index = index;
		this.valor = valor;
	}
	
	public static MapaSaida padrao() {
		return new MapaSaida(0, 0);
	}
	
	@Override
	public String toString() {
//		return "[i=" + this.index + ",  v=" + this.valor + "]";
		return this.index + "|" + this.valor;
	}
	
	@Override
	public int compareTo(MapaSaida o) {
		if (this.valor == o.valor)
			return 0;
		return this.valor > o.valor ? 1 : -1;
	}
}