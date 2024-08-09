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
import lerfob.mems.MEMSSite.SiteType;

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
	 * Provide the mean daily temperature for a particular year.<p>
	 * A stand instance should be able to provide these temperatures
	 * for the interval that goes from the previous stand
	 * date + 1 to the date of this stand.
	 * @param year an integer
	 * @return an array of double. The array is expected to have 365 or 366 slots.
	 */
	public double[] getMeanDailyTemperatureCForThisYear(int year);

	/**
	 * Inform on the nature of the temperature.<p>
	 * The default implementation returns true.
	 * @return a boolean true if this is the air temperature. Otherwise,
	 * if is assumed to be the soil temperature.
	 */
	public default boolean isTemperatureFromAir() {
		return true;
	}

}
