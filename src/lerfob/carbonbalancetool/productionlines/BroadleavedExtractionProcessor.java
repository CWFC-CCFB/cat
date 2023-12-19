/*
 * This file is part of the CAT library.
 *
 * Copyright (C) 2023 His Majesty the King in Right of Canada
 * Author: Mathieu Fortin for Canadian Forest Service
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
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

import java.util.ArrayList;
import java.util.List;

import lerfob.carbonbalancetool.CATSettings.CATSpecies;
import repicea.simulation.covariateproviders.treelevel.SpeciesTypeProvider.SpeciesType;
import repicea.simulation.processsystem.ProcessUnit;
import repicea.util.REpiceaTranslator;
import repicea.util.REpiceaTranslator.TextableEnum;

/**
 * A special processor for extracting wood pieces from broadleaved species.
 * @author Mathieu Fortin - December 2023
 */
@SuppressWarnings("serial")
public class BroadleavedExtractionProcessor extends AbstractExtractionProcessor {

	private enum MessageID implements TextableEnum {

		ExtractBroadleaved("Extract broadleaved species", "Extraire les esp\u00E8ces de feuillus");

		MessageID(String englishText, String frenchText) {
			setText(englishText, frenchText);
		}
		
		@Override
		public void setText(String englishText, String frenchText) {
			REpiceaTranslator.setString(this, englishText, frenchText);
		}

		@Override
		public String toString() {return REpiceaTranslator.getString(this);}
	}
	
	
	
	protected BroadleavedExtractionProcessor() {
		super();
		setName(MessageID.ExtractBroadleaved.toString());		// default name
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected List<ProcessUnit> extract(List<ProcessUnit> processUnits) {
		List<ProcessUnit> extractedUnits = new ArrayList<ProcessUnit>();
		List<ProcessUnit> copyList = new ArrayList<ProcessUnit>();
		copyList.addAll(processUnits);
		for (ProcessUnit p : copyList) {
			if (p instanceof CarbonUnit) {
				CATSpecies species = ((CarbonUnit) p).getSpecies();
				if (species.getSpeciesType() == SpeciesType.BroadleavedSpecies) {
					extractedUnits.add(p);
					processUnits.remove(p);
				}
			}
		}
		return extractedUnits;
	}

}
