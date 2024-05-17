package lerfob.carbonbalancetool.memsconnectors;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;

import lerfob.carbonbalancetool.memsconnectors.MEMSSite.SiteName;
import repicea.gui.REpiceaShowableUIWithParent;

public class MEMSSiteParametersWrapper implements REpiceaShowableUIWithParent {

	
	protected final SiteName name;
	
	protected MEMSSiteParametersWrapper(SiteName name) {
		this.name = name;
	}

	/**
	 * Provide the name of this wrapper.
	 * @return a SiteName enum
	 */
	public SiteName getName() {return name;}
	
	@Override
	public Component getUI(Container parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void showUI(Window parent) {
		// TODO Auto-generated method stub
		
	}

}
