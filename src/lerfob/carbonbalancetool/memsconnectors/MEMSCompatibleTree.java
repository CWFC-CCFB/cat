/*
 * This file is part of the CAT library.
 *
 * Copyright (C) 2024 His Majesty the King in Right of Canada
 * Authors: Jean-Francois Lavoie and Mathieu Fortin, Canadian Wood Fibre Centre
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

import repicea.simulation.covariateproviders.treelevel.BasalAreaM2Provider;

/**
 * Ensure that the tree instances are compatible with MEMS.
 * @author Mathieu Fortin - April 2024
 */
public interface MEMSCompatibleTree extends BasalAreaM2Provider {

	/**
	 * Provide the annual production in fine root biomass.<p>
	 * 
	 * The default implementation is based on Finer et al. (2011).
	 * @return the annual production in dry biomass (Mg/yr)
	 * @see <a href=https://doi.org/10.1016/j.foreco.2011.08.042> Finer, L., M. Ohashi, K. Noguchi, and 
	 * Y. Hirano. 2011. Fine root production and turnover in forest ecosystems in relation to stand and 
	 * environmental characteristics. Forest Ecology and Management 262(11): 2008-2023</a>
	 */
	public default double getAnnualFineRootBiomassProductionMgYr() {
		return (1.55 * Math.log(getStemBasalAreaM2()) + 9.408) * .001;
	}
	
	/**
	 * Provide the annual production in foliage biomass.<p>
	 * 
	 * The default implementation comes from Lavigne et al. (2005).
	 * @return the annual production in dry biomass (Mg/yr)
	 * @see <a href=https://doi.org/10.1139/X06-284> Lavigne, M.B., R.J. Foster,
	 * G. Goodine, P.Y. Bernier, and C.-H. Ung. 2005. Estimating branch production in trembling aspen,
	 * Douglas fir, jack pine, black spruce, and balsam fir. Canadian Journal of 
	 * Research 37: 1024-1033</a>
	 */
	public default double getAnnualFoliarBiomassProductionMgYr() {
		return 0.15 * Math.pow(getAnnualCrossSectionalAreaGrowthCm2(), 1.18) * 0.001;
	}

	/**
	 * Provide the annual production in branch biomass.<p>
	 * 
	 * The default implementation assumes the production is equal to half 
	 * that of foliar production as per Lavigne et al. (2005).
	 * @return the annual production in dry biomass (Mg/yr)
	 * @see <a href=https://doi.org/10.1139/X06-284> Lavigne, M.B., R.J. Foster,
	 * G. Goodine, P.Y. Bernier, and C.-H. Ung. 2005. Estimating branch production in trembling aspen,
	 * Douglas fir, jack pine, black spruce, and balsam fir. Canadian Journal of 
	 * Research 37: 1024-1033</a>
	 */
	public default double getAnnualBranchBiomassProductionMgYr() {
		return getAnnualFoliarBiomassProductionMgYr() * .5;
	}

	/**
	 * Provide the annual increment of the cross-sectional area.
	 * @return the increment (cm2/yr)
	 */
	public double getAnnualCrossSectionalAreaGrowthCm2();

//	/**
//	 * Provide the annual increment in aboveground biomass.
//	 * @return the increment in (Mg/yr)
//	 */
//	public double getAnnualAboveGroundBiomassIncrementMgYr();
//
//	/**
//	 * Provide the annual increment in below biomass.
//	 * @return the increment in (Mg/yr)
//	 */
//	public double getAnnualBelowGroundBiomassIncrementMgYr();

}
