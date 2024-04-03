/*
 * This file is part of the lerfob-forestools library.
 *
 * Copyright (C) 2020-2024 His Majesty the King in right of Canada
 * Author: Mathieu Fortin, Canadian Forest Service 
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

import lerfob.carbonbalancetool.productionlines.CarbonUnit.BiomassType;

interface BiomassTypeProvider {

	/**
	 * Provide the type of biomass.<p>
	 * @return a BiomassType enum
	 * @see BiomassType
	 */
	public BiomassType getBiomassType();

}
