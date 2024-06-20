/*
 * This file is part of the lerfob-forestools library.
 *
 * Copyright (C) 2010-2012 Mathieu Fortin for LERFOB INRA/AgroParisTech, 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed with the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * Please see the license at http://www.gnu.org/copyleft/lesser.html.
 */
package lerfob.carbonbalancetool.productionlines;


import lerfob.carbonbalancetool.CATCompartmentManager;
import repicea.simulation.processsystem.AmountMap;
import repicea.simulation.processsystem.ProcessUnit;


/**
 * A LeftInForestCarbonUnit object results from
 * @author M.Fortin - March 2024
 */
public class LeftInForestCarbonUnit extends CarbonUnit {

	/**
	 * Official constructor for this class.
	 * @param dateIndex the date index
	 * @param carbonUnitFeature an EndUseWoodProductCarbonUnitFeature instance
	 * @param amountMap an AmountMap instance
	 * @param originalCarbonUnit the original CarbonUnit instance from which this EndUseWoodProductCarbonUnit instance is created
	 */
	protected LeftInForestCarbonUnit(int dateIndex,
                                     CarbonUnitFeature carbonUnitFeature,
                                     AmountMap<Element> amountMap,
                                     CarbonUnit originalCarbonUnit) {
		super(dateIndex, carbonUnitFeature, amountMap, originalCarbonUnit);
		addStatus(CarbonUnitStatus.DeadWood);
	}

	/**
	 * This method returns the volume of the product as it was created.
	 * @return a double
	 */
	public double getProcessedVolumeAtCreationDate() {return getAmountMap().get(Element.Volume);}
	
	/**
	 * This method returns the dry biomass of the product as it was created.
	 * @return a double
	 */
	public double getBiomassMgAtCreationDate() {return getAmountMap().get(Element.Biomass);}
	
	/**
	 * This method actualizes the EndProduct instance on a basis that is specified through the time scale parameter. Landfill products are retrieved 
	 * through a static collection in the manager.
	 * @param compartmentManager a CATCompartmentManager instance
	 */
	@Override
	protected void actualizeCarbon(CATCompartmentManager compartmentManager) {
		super.actualizeCarbon(compartmentManager);
		if (compartmentManager.isMEMSEnabled()) {
			double[] releasedCarbonArray = getReleasedCarbonArray();

			if (getWoodyDebrisType() != null) {
				boolean addToHumus = getWoodyDebrisType() == WoodyDebrisProcessor.WoodyDebrisProcessorID.FineWoodyDebris || getWoodyDebrisType() == WoodyDebrisProcessor.WoodyDebrisProcessorID.CommercialWoodyDebris;

				for (int i = getIndexInTimeScale(); i < getTimeTable().size(); i++) {
					compartmentManager.getMEMS().addCarbonToMEMSInput(i, releasedCarbonArray[i], addToHumus);
				}
			}
		}
	}
	
	@Override
	protected void addProcessUnit(ProcessUnit<Element> carbonUnit) {
		super.addProcessUnit(carbonUnit);
	}
}