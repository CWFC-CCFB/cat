/*
 * This file is part of the lerfob-forestools library.
 *
 * Copyright (C) 2010-2013 Mathieu Fortin AgroParisTech/INRA UMR LERFoB, 
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
package lerfob.carbonbalancetool;

import java.util.LinkedHashMap;
import java.util.List;

@SuppressWarnings("serial")
class CATIntermediateBiomassCarbonMap extends LinkedHashMap<CATCompatibleStand, Double> {

	final CATTimeTable timeTable;
	final CarbonArray carbonArray;
	
	CATIntermediateBiomassCarbonMap(CATTimeTable timeTable, CarbonArray carbonArray) {
		this.timeTable = timeTable;
		this.carbonArray = carbonArray;
	}
	
	
	void interpolateIfNeeded() {
		List<List<CATCompatibleStand>> segments = timeTable.getSegments();
		for (List<CATCompatibleStand> segment : segments) {
			CATCompatibleStand previousStand = null;
			for (CATCompatibleStand s : segment) {
				int currentIndex = timeTable.getIndexOfThisStandOnTheTimeTable(s);
				double currentValue = this.get(s);
				if (previousStand != null) {
					int previousIndex = timeTable.getIndexOfThisStandOnTheTimeTable(previousStand);
					int previousDateYr = timeTable.getDateYrAtThisIndex(previousIndex);
					double previousValue = this.get(previousStand);
					int currentDateYr = timeTable.getDateYrAtThisIndex(currentIndex);
					double slope = (currentValue - previousValue) / (currentDateYr - previousDateYr);
					for (int i = previousIndex + 1; i < currentIndex; i++) {
						double interpolatedValue = previousValue + (timeTable.getDateYrAtThisIndex(i) - previousDateYr) * slope;
						carbonArray.setCarbonIntoArray(i, interpolatedValue);
					}
				}
				previousStand = s;
				carbonArray.setCarbonIntoArray(currentIndex, currentValue);
			}
		}
	}
	
	
	
	
}
