/*
 * This file is part of the CAT library.
 *
 * Copyright (C) 2024 His Majesty the King in Right of Canada
 * Authors: Mathieu Fortin, Canadian Wood Fibre Centre
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
package lerfob.carbonbalancetool.memsconnectors;

import lerfob.carbonbalancetool.CATCompatibleStand;
import lerfob.carbonbalancetool.memsconnectors.MEMSSite.SiteType;

/**
 * Ensures the stand instance is compatible with MEMS.
 */
public interface MEMSCompatibleStand extends CATCompatibleStand {

	/**
	 * Provide the site type of this stand.<p>
	 * The method can return null. In such a case, MEMS will be disabled. 
	 * @return a SiteType enum
	 */
	public SiteType getSiteType();
	
	/**
	 * Provide the mean annual temperature for a particular year.<p>
	 * A stand instance should be able to provide the mean annual 
	 * temperature for the interval that goes from the previous stand
	 * date + 1 to the date of this stand.
	 * @param year an integer
	 * @return the mean annual temperature (C)
	 */
	public double getMeanAnnualTemperatureCForThisYear(int year);
	
	/**
	 * Provide the range between the min mean temperature and the 
	 * max mean temperature.<p>
	 * A stand instance should be able to provide the annual 
	 * temperature range for the interval that goes from the previous stand
	 * date + 1 to the date of this stand.
	 * @param year an integer
	 * @return the range of temperature (C)
	 */
	public double getAnnualTemperatureRangeForThisYear(int year);
	
}
