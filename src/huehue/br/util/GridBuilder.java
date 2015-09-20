package huehue.br.util;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Classe utilizada para auxiliar na manipulação de componentes com {@link GridBagLayout}.
 * 
 * @author Alessandro Beleboni Belini
 * @author Luiz Felipe Nazari
 * @version 1.2.1
 */
public class GridBuilder extends GridBagConstraints {
	private static final long serialVersionUID = -3673408843629080010L;
	
	public static GridBuilder novo() {
		return new GridBuilder().withSize(1, 1).withWeight(1, 1).withAnchor(GridBuilder.WEST);
	}
	
	public GridBuilder withLocation(int gridy, int gridx) {
		this.gridy = gridy;
		this.gridx = gridx;
		return this;
	}
	
	public GridBuilder withAnchor(int anchor) {
		this.anchor = anchor;
		return this;
	}
	
	public GridBuilder withFill(int fill) {
		this.fill = fill;
		return this;
	}
	
	public GridBuilder withSize(int gridwidth, int gridheight) {
		this.gridwidth = gridwidth;
		this.gridheight = gridheight;
		return this;
	}
	
	public GridBuilder withWeight(double weightx, double weighty) {
		this.weightx = weightx;
		this.weighty = weighty;
		return this;
	}
	
	public GridBuilder withMargins(int distance) {
		this.insets = new Insets(distance, distance, distance, distance);
		return this;
	}
	
	public GridBuilder withMargins(int top, int left, int bottom, int right) {
		this.insets = new Insets(top, left, bottom, right);
		return this;
	}
	
	public GridBuilder withMargins(int vertical, int horizontal) {
		this.insets = new Insets(vertical, horizontal, vertical, horizontal);
		return this;
	}
	
	public GridBuilder withPaddings(int ipadx, int ipady) {
		this.ipadx = ipadx;
		this.ipady = ipady;
		return this;
	}
}
