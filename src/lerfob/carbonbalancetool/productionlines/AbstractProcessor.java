/*
 * This file is part of the lerfob-forestools library.
 *
 * Copyright (C) 2010-2015 Mathieu Fortin for LERFOB AgroParisTech/INRA, 
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

import java.awt.Point;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import lerfob.carbonbalancetool.productionlines.CarbonUnit.Element;
import repicea.simulation.processsystem.AmountMap;
import repicea.simulation.processsystem.ProcessUnit;
import repicea.simulation.processsystem.Processor;

@SuppressWarnings("serial")
public abstract class AbstractProcessor extends Processor {

	protected double functionUnitBiomass; // in Mg
	protected double emissionsByFunctionalUnit; // in Mg
	
	protected AbstractProcessor() {}
	
	/**
	 * Add emissions to the ProcessUnit before sending them to the super method. <p>
	 * The emissions are added only if the processor has subprocessors (meaning it is not
	 * and end use product). Otherwise the emissions will be accounted for through the 
	 * CarbonUnitFeature instance.
	 * @param inputUnits a List of ProcessUnit instances sent to this Processor instance
	 * @see Processor#doProcess(List)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Collection<ProcessUnit> doProcess(List<ProcessUnit> inputUnits) {
		if (hasSubProcessors()) {
			for (ProcessUnit processUnit : inputUnits) {
				AbstractProcessor.updateProcessEmissions(processUnit.getAmountMap(), functionUnitBiomass, emissionsByFunctionalUnit);
			}
		}
		return super.doProcess(inputUnits);
	}

	
	protected static void updateProcessEmissions(AmountMap<CarbonUnit.Element> amountMap, double functionalUnitBiomassMg, double emissionsMgCO2ByFunctionalUnit) {
		Double biomassMg = (Double) amountMap.get(Element.Biomass);
		if (biomassMg != null && functionalUnitBiomassMg > 0) {
			double fonctionalUnits = biomassMg / functionalUnitBiomassMg;
			double emissions = fonctionalUnits * emissionsMgCO2ByFunctionalUnit;
			amountMap.add(Element.EmissionsCO2Eq, emissions);
		}
	}
	
	/**
	 * Create a Processor instance from a LinkedHashMap instance.<p>
	 * 
	 * @param oMap a LinkedHashMap<String, Object> instance
	 * @return a Processor instance
	 */
	public static Processor createProcessor(LinkedHashMap<String, Object> oMap) {
//		if (oMap.containsKey("class")) { // previously exported from CAT
//			String clazz = oMap.get("class").toString();
//			
//			if (clazz.equals("WoodyDebrisProcessor")) {
//				return new WoodyDebrisProcessor(oMap);
//			} else if (clazz.equals("LogCategoryProcessor")) {
//				return new LogCategoryProcessor(oMap);
//			} else if (clazz.equals("LeftInForestProcessor")) {
//				return new LeftInForestProcessor(oMap);
//			} else if (clazz.equals("LandfillProcessor")) {
//				return new LandfillProcessor(oMap);
//			} else {
//				return new ProductionLineProcessor(oMap);
//			}
//		} else { // likely imported from AFFiliere
			ProductionLineProcessor p = new ProductionLineProcessor();
			String name = (String) oMap.get("name");
			int x = ((Number) oMap.get("x")).intValue(); // + OFFSET;
			int y = ((Number) oMap.get("y")).intValue();
			p.setName(name);
			p.setOriginalLocation(new Point(x,y));
			return p;
//		}
	}

//	/*
//	 * For extended visibility.
//	 */
//	@Override
//	protected Point getOriginalLocation() {
//		return super.getOriginalLocation();
//	}

}
