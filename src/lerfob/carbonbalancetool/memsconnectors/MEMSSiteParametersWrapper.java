package lerfob.carbonbalancetool.memsconnectors;

import lerfob.carbonbalancetool.memsconnectors.MEMSSite.SiteType;

public class MEMSSiteParametersWrapper {

	
	protected final SiteType name;
	
	protected MEMSSiteParametersWrapper(SiteType name) {
		this.name = name;
	}

	/**
	 * Provide the name of this wrapper.
	 * @return a SiteName enum
	 */
	public SiteType getName() {return name;}

}
