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

import lerfob.carbonbalancetool.CATCompatibleTree;
import repicea.simulation.covariateproviders.treelevel.BasalAreaM2Provider;

/**
 * Ensure that the tree instances are compatible with MEMS.
 * @author Mathieu Fortin - April 2024
 */
public interface MEMSCompatibleTree extends BasalAreaM2Provider, CATCompatibleTree {

	/**
	 * Ratio to convert foliage biomass to carbon.<p>
	 * 
	 * This ratio comes from Table 2 under "Europe All" in Neumann et al. 2018.
	 * 
	 * @see <a href=https://doi.org/10.1029/2017GB005825> Neumann, M., L. Ukonmaanaho, J. Johnson, 
	 * S. Benham, L. Vesterdal, R. Novotny, A. Verstraeten, L. Lundin, A. Thimonier,
	 * P. Michopoulos, and H. Hasenauer. 2018. Quantifying Carbon and Nutrient Input From 
	 * Litterfall in European Forests Using Field Observations. Global Biogeochemical Cycles 32:
	 * 784-798.</a> 
	 */
	public static double CARBON_TO_FOLIAGE_BIOMASS_RATIO = 0.517;  
	
	/**
	 * Provide the annual fine root detritus.<p>
	 * @param the detritus production (Mg of C)
	 */
	public double getAnnualFineRootDetritusCarbonProductionMgYr();
	
	/**
	 * Provide the annual foliage detritus.<p>
	 * @param the detritus production (Mg of C)
	 */
	public double getAnnualFoliarDetritusCarbonProductionMgYr(); 
	
	/**
	 * Provide the annual branch detritus.<p>
	 * @return the detritus production (Mg of C)
d	 */
	public double getAnnualBranchDetritusCarbonProductionMgYr();

}
