/*
 * This file is part of the mems library.
 *
 * Copyright (C) 2022-23 His Majesty the King in Right of Canada
 * Authors: Jean-Francois Lavoie and Mathieu Fortin, Canadian Wood Fibre Centre
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
package lerfob.mems;

import static lerfob.mems.SoilCarbonPredictorEquation.Eq01_getDailyChangeC1;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq02_getDailyChangeC2;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq03_getDailyChangeC3;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq04_getDailyChangeC4;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq05_getDailyChangeC5;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq06_getDailyChangeC6;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq07_getDailyChangeC7;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq08_getDailyChangeC8;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq09_getDailyChangeC9;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq10_getDailyChangeC10;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq11_getDailyChangeC11;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq12_getDailyInputInC1;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq13_getDailyInputInC2;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq14_getDailyInputInC3;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq15_calculate;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq16_getLCI;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq17_getDailyCarbonStockTransferFromC1ToC4;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq18_getDailyCarbonStockTransferFromC2ToC4;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq19_getModifier;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq20_getModifier;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq21_getLeachingLA1;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq22_getLeachingLA4;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq23_getDailyCarbonStockTransferFromC4ToC5;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq24_getDailyCarbonStockTransferFromC2ToC5;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq25_getDailyCarbonStockTransferFromC3ToC5;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq26_getDailyCarbonStockTransferFromC2ToC10;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq27_getDailyCarbonStockTransferFromC3ToC10;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq28_getDailyCarbonStockTransferFromC1ToC6;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq29_getDailyCarbonStockTransferFromC2ToC6;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq30_getDailyCarbonStockTransferFromC3ToC6;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq31_getDailyCarbonStockTransferFromC4ToC6;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq32_getDailyCarbonStockTransferFromC5ToC8;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq33_getDailyCarbonStockTransferFromC6ToC8;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq34_getDailyCarbonStockTransferFromC10ToC8;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq35_getBindingAffinityL_k_lm;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq36_getMaximumSorptionCapacityQ_max;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq37_getSorption;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq38_getDailyCarbonStockTransferFromC1ToC7;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq39_getDailyCarbonStockTransferFromC2ToC7;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq40_getDailyCarbonStockTransferFromC3ToC7;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq41_getDailyCarbonStockTransferFromC4ToC7;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq42_getDailyCarbonStockTransferFromC5ToC7;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq43_getDailyCarbonStockTransferFromC8ToC7;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq44_getDailyCarbonStockTransferFromC9ToC7;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq45_getDailyCarbonStockTransferFromC10ToC7;
import static lerfob.mems.SoilCarbonPredictorEquation.Eq46_getK3Estimate;
import static lerfob.mems.SoilCarbonPredictorEquation.Weibull_getTemperatureModifier;

import java.security.InvalidParameterException;

import repicea.math.Matrix;
import repicea.simulation.REpiceaPredictor;

/**
 * As described here, MEMS v1.0 currently only simulates a surface organic horizon
 * and a single mineral soil layer and does not yet differentiate
 * between above- and belowground litter input chemistry
 * to avoid requiring additional input parameters on root litter
 * chemistry.
 * 
 * @author Jean-Francois Lavoie and Mathieu Fortin - February 2023
 * 
 * @see <a href="https://doi.org/10.5194/bg-16-1225-2019">Robertson et al. 2019. Unifying soil organic matter formation 
 * and persistence frameworks: the MEMS model. Biogeosciences 16, 1225-1248.</a>  
 */
@SuppressWarnings("serial")
public class SoilCarbonPredictor extends REpiceaPredictor  {
	public enum MCParam
	{
		parmB1(0.6, 0.4, 0.7),
		parmB2(0.5, 0.3, 0.6),
		parmB3(0.33, 0.028, 0.79),
		LIT_frg(0.0006, 1e-5, 2e-3),
		POM_split(0.3, 0.07, 0.83),
		DOC_frg(0.8, 0.2, 0.99),
		DOC_lch(0.00438, 1e-5, 0.02),
		parmK1(0.37, 0.16, 0.7),
		parmK2(0.009, 0.0011, 0.02),
		parmK3(0.0002, 2e-5, 1e-3),
		parmK4(0.57, 0.11, 0.97),
		parmK5(0.0005, 6e-5, 1e-3),
		parmK9(2.2E-5, 1e-5, 4e-5),
		parmK10(2.96E-4, 1e-4, 1e-3),
		la_2(0.19, 0.022, 0.42),
		la_3(0.038, 0.014, 0.05),
		sigma2Litter(400.0, 0.0, 10000.0),
		sigma2Soil(400.0, 0.0, 10000.0);

		MCParam(double initValue, double rangeMin, double rangeMax) {
			if (initValue < rangeMin || initValue > rangeMax)
				throw new InvalidParameterException();
			this.initValue = initValue;
			this.rangeMin = rangeMin;
			this.rangeMax = rangeMax;
		}
		double getInitialValue() {
			return initValue;
		}

		double getRangeMin() {
			return rangeMin;
		}

		double getRangeMax() {
			return rangeMax;
		}

		private final double initValue;
		private final double rangeMin;
		private final double rangeMax;
	};

	// All the parameters down to N_mid were taken from Table 2 in Robertson et al. 2019
	
	/**
	 * Maximum growth efficiency of microbial use of water-soluble litter carbon (C1).<p>
	 * Default value is 0.6 g C microbial biomass C per g decayed (0.4-0.7).<p>
	 * Source: Sinsabaugh et al. (2013) 
	 */
	double parmB1 = 0.6;
	
	/**
	 * Maximum growth efficiency of microbial use of acid-soluble structural litter carbon (C2).<p>
	 * Default value is 0.5 g microbial biomass C per g decayed (0.3-0.6).<p>
	 * Source: Sinsabaugh et al. (2013) 
	 */
	double parmB2 = 0.5;
	
	/**
	 * Heavy, coarse particulate organic matter (C5) generation from microbial biomass carbon (C4) decay.<p>
	 * Default value is 0.33 g microbial products C per g decayed C (0.028-0.79).<p>
	 * Source: Campbell et al. (2016) 
	 */
	double parmB3 = 0.33;

	/**
	 * Carbon in structural litter inputs (C2 and C3 transported to soil
	 * particulate organic matter (C5 and C10) each time step).<p>
	 * Default value is 0.006 g C per g C decayed (1x10-5 - 2x10-3).<p>
	 * Source: MEMS v1.0
	 */
	double LIT_frg = 0.0006;	// here the value has been adjusted to 6e-4 instead of 6e-3 so that it is comprised in the specified range (typo?)

	/**
	 * Fraction of fragmented litter inputs that form heavy particulate organic matter (C5).<p>
	 * Default value is 0.30 (0.07-0.83).<p>
	 * Sources: Poeplau and Don (2013); Soong et al. (2016)
	 */
	double POM_split = .3;

	/**
	 * Carbon in litter layer DOM (C6) transported to soil DOM (C8) at each time step.<p>
	 * Default value is 0.8 g C-DOM per g C-DOM (0.2-0.99).<p>
	 * Source: MEMS v1.0
	 */
	double DOC_frg = 0.8;

	/**
	 * Maximum specific rate of leaching to represent vertical transport of carbon in DOM through the soil profile.<p>
	 * Default value is 0.00438 g C per day (1x10-5 - 0.02).<p>
	 * Source: Trumbore et al. (1992) 
	 */
	double DOC_lch = 0.00438;
	
	/**
	 * Maximum amount of carbon leached from decayed acid-soluble litter carbon (C2) to litter layer DOM (C6).<p>
	 * Default value is 0.15 g DOM-C per g decayed C.<p>
	 * Source: Campbell et al. (2016) 
	 */
	final double E_Hmax = 0.15;
	
	/**
	 * Minimum amount of carbon leached from decayed acid-soluble litter carbon (C2) to litter layer DOM (C6).<p>
	 * Default value is 0.005 g DOM-C per g decayed C.<p>
	 * Source: Campbell et al. (2016) 
	 */
	final double E_Hmin = 0.005;

	/**
	 * Maximum amount of carbon leached from decayed water-soluble litter carbon (C1) to litter layer DOM (C6).<p> // this seems to be a maximum proportion (and not a maximum amount)
	 * Default value is 0.15 g DOM-C per g decayed C.<p>
	 * Source: Campbell et al. (2016)
	 */
	final double E_smax = 0.15;
	
	/**
	 * Minimum amount of carbon leached from decayed water-soluble litter carbon (C1) to litter layer DOM (C6).<p> // this seems to be a minimum proportion (and not a maximum amount)
	 * Default value is 0.005 g DOM-C per g decayed C.<p>
	 * Source: Campbell et al. (2016)
	 */
	final double E_smin = 0.005;
	
	/**
	 * Maximum decay rate of water-soluble litter carbon (C1). <p>
	 * Default value is 0.37/day (0.16-0.70) <p>
	 * Source: Campbell et al. 2016
	 */
	double parmK1 = 0.37;

	/**
	 * Maximum decay rate of acid-soluble litter carbon (C2).<p>
	 * Default value is 0.009/day (0.0011-0.0200).<p>
	 * Source: Campbell et al. (2016)
	 */
	double parmK2 = 0.009;

	/**
	 * Maximum decay rate of acid-insoluble litter carbon (C3).<p>
	 * Default value is 0.0002/day (2x10-5 - 1x10-3).<p>
	 * Source: Moorhead et al. (2013) 
	 */
	double parmK3 = 0.0002;

	/**
	 * Maximum decay rate of microbial biomass carbon (C4).<p>
	 * Default value is 0.57/day (0.11-0.97).<p>
	 * Source: Campbell et al. (2016)
	 */
	double parmK4 = 0.57;

	/**
	 * Maximum decay rate of heavy, coarse particulate soil organic matter (C5).<p>
	 * Default value is 0.0005/day (6x10-5 - 1x10-3).<p>
	 * Sources: Campbell et al. (2016); Del Galdo et al. (2003) 
	 */
	double parmK5 = 0.0005;

	/**
	 * Maximum decay rate if soil DOM (C8).<p>
	 * Default value is 0.00144/day.<p>
	 * Source: Kalbitz et al. (2005) 
	 */
	final double parmK8 = 0.00144;

	/**
	 * Maximum decay rate of mineral-associated soil organic matter (C9).<p>
	 * Default value is 2.2x10-5/day (1x10-5 - 4x10-5).<p>
	 * Source: Del Galdo et al. (2003) 
	 */
	double parmK9 = 2.2E-5;

	/**
	 * Maximum decay rate of light particulate soil organic matter (C10).<p>
	 * Default value is 2.96x10-5/day (4x10-3 - 1x10-4).<p>
	 * Source: Del Galdo et al. (2003) 
	 */
	double parmK10 = 2.96E-5;
	
	/**
	 * Carbon leached from decayed microbial biomass carbon (C4).<p>
	 * Default value is 0.19 g DOM-C per g decayed C (0.022-0.42).<p>
	 * Source: Campbell et al. (2016)
	 */
	double la_2 = 0.19;

	/**
	 * Carbon leached from acid-insoluble litter carbon and heavy, coarse particulate organic matter carbon (C3 and C5).<p>
	 * Default value is 0.038 g C-DOM par g decayed C (0.014-0.050).<p>
	 * Sources: Campbell et al. (2016); Soong et al. (2015)
	 */
	double la_3 = 0.038; 
	
	/**
	 * Maximum lignocellulosic index that influences DOM generation from litter decay.<p>
	 * Default value is 0.51.<p> 
	 * Sources: Campbell et al. (2016); Soong et al. (2015) 
	 */
	final double LCI_max = 0.51;
		
	/**
	 * Maximum N concentration that influences rates (above this, there is no limit) of
	 * DOM generation and microbial carbon assimilation.<p>
	 * Default value is 3%. Units are in percent.<p>
	 * Source: Sinsabaugh et al. (2013) 
	 */
	final double N_max = 3; // Units are in percent in the [0-100] range
	
	/**
	 * Mid-point of logistic function that describes N limitation.<p>
	 * Default value is 1.75%. Units are in percent.<p>
	 * Sources: Campbell et al. (2016); Soong et al. (2015) 
	 */
	final double N_mid = 1.75; // Units are in percent in the [0-100] range
		
	/**
	 * Binding affinity for carbon in soil DOM (C8) sorption to mineral surfaces (C9) of soil layer L.<p>
	 * Default value is 0.25 g C per day.<p>
	 * Sources: Mayes et al. (2012); Abramoff et al. (2017)
	 */
	final double L_k_lm = 0.25;

	
	// THIS ONE IS PROVIDED IN CAMPBELL ET AL. 2016
	
	/**
	 * The cold-water extractable fraction of the hot-water extractable litter input.<p>
	 * It is the fraction of the water-soluble litter that bypasses the microbial 
	 * process and that is leached from the litter into the DOM pool.<p> 
	 * Default value is 0.15 (0.09-0.21).
	 * Source: Table 5 in Campbell et al. (2016)
	 */
	final double f_DOC = 0.15;

	/**
	 * Optimal temperature at which decay rates are the highest in Celsius.
	 */
	final double Topt = 35.0;

	/**
	 * Difference between optimal temperature and   at which decay rates are the highest in Celsius.
	 */
	final double Tlag = 7.0;

	/**
	 * Shape of the exceeding temperature limit for the temperature modifier
	 */
	final double Tshp = 3.0;

	/**
	 * Rate at which the decay rate rises for a 10 Celsius degree rise of soil temperature
	 */
	final double Tq10 = 3.0;

	/**
	 * Maximal estimated decay rate reference temperature.
	 */
	final double Tref = 13.5;

	/**
	 * Temperature modifier to be used for pool transfers
	 */
	double Tmod = 1.0;

	/**
	 * Temperature modifier coefficient 1 (see Mems2_Eq26_getTemperatureModifier)
	 */
	final double coeff_t1 = 18.4;

	/**
	 * Temperature modifier coefficient 2 (see Mems2_Eq26_getTemperatureModifier)
	 */
	final double coeff_t2 = 0.21;
	double sigma2Litter = 400.0;
	double sigma2Soil = 400.0;

	long[] elapsedTimes = new long[10];
	
	
	/**
	 * Constructor.
	 * @param isVariabilityEnabled a boolean true to enable the stochastic mode
	 */
	public SoilCarbonPredictor(boolean isVariabilityEnabled) {
		super(isVariabilityEnabled, isVariabilityEnabled, isVariabilityEnabled);
		init();
	}

	public void setParms(Matrix parms) {
		// vector parms is expected to contain parameters corresponding to enum MCParam ordinals
		this.parmB1 = parms.getValueAt(MCParam.parmB1.ordinal(), 0);
		this.parmB2 = parms.getValueAt(MCParam.parmB2.ordinal(), 0);
		this.parmB3 = parms.getValueAt(MCParam.parmB3.ordinal(), 0);
		this.LIT_frg = parms.getValueAt(MCParam.LIT_frg.ordinal(), 0);
		this.POM_split = parms.getValueAt(MCParam.POM_split.ordinal(), 0);
		this.DOC_frg = parms.getValueAt(MCParam.DOC_frg.ordinal(), 0);
		this.DOC_lch = parms.getValueAt(MCParam.DOC_lch.ordinal(), 0);
		this.parmK1 = parms.getValueAt(MCParam.parmK1.ordinal(), 0);
		this.parmK2 = parms.getValueAt(MCParam.parmK2.ordinal(), 0);
		this.parmK3 = parms.getValueAt(MCParam.parmK3.ordinal(), 0);
		this.parmK4 = parms.getValueAt(MCParam.parmK4.ordinal(), 0);
		this.parmK5 = parms.getValueAt(MCParam.parmK5.ordinal(), 0);
		this.parmK9 = parms.getValueAt(MCParam.parmK9.ordinal(), 0);
		this.parmK10 = parms.getValueAt(MCParam.parmK10.ordinal(), 0);
		this.la_2 = parms.getValueAt(MCParam.la_2.ordinal(), 0);
		this.la_3 = parms.getValueAt(MCParam.la_3.ordinal(), 0);
		this.sigma2Litter = parms.getValueAt(MCParam.sigma2Litter.ordinal(), 0);
		this.sigma2Soil = parms.getValueAt(MCParam.sigma2Soil.ordinal(), 0);
	}

	@Override
	protected void init() {}

	/**
	 * Predict the daily changes in the different compartments.
	 * @param compartments a SoilCarbonPredictorCompartments instance that contains the initial carbon stocks
	 * @param inputs a SoilCarbonPredictorInput instance 
	 */
	protected void predictDailyCStockChanges(SoilCarbonPredictorCompartments compartments, SoilCarbonPredictorInput inputs) {
		predictDailyCStockChanges(compartments, inputs, 1.0);
	}
	
	/**
	 * Predict the changes in the different compartments.
	 * @param compartments a SoilCarbonPredictorCompartments instance that contains the initial carbon stocks
	 * @param inputs a SoilCarbonPredictorInput instance
	 * @param Tmod the temperature modifier to be applied (1.0 means no change)
	 */
	protected void predictDailyCStockChanges(SoilCarbonPredictorCompartments compartments, SoilCarbonPredictorInput inputs, double Tmod) {
		long initTime = System.currentTimeMillis();

		this.Tmod = Tmod;

//		SoilCarbonPredictorCompartments pred = new SoilCarbonPredictorCompartments(0d);

		double LCI_lit = Eq16_getLCI(compartments);
		this.parmK3 = Eq46_getK3Estimate(this, LCI_lit);
		
		// local variables are computed here and passed on to the equations to avoid recomputing them multiple times (ex : uk)
		// Eq01
		double C1_i_in = Eq12_getDailyInputInC1(this, inputs);
		double uk = Eq20_getModifier(this, compartments, inputs.landType.N_lit, LCI_lit);
		compartments.deltaC1 = Eq01_getDailyChangeC1(this, compartments, C1_i_in, uk);

		// Eq02
		double C2_i_in = Eq13_getDailyInputInC2(inputs.CT_i, inputs.landType.f_sol, inputs.landType.f_lig);
		// double uk = Eq20_getModifier(this, compartments, N_lit, LCI_lit);
		compartments.deltaC2 = Eq02_getDailyChangeC2(this, compartments, C2_i_in, uk);

		// Eq03
		double C3_i_in = Eq14_getDailyInputInC3(inputs.CT_i, inputs.landType.f_lig);
		compartments.deltaC3 = Eq03_getDailyChangeC3(this, compartments, C3_i_in);
		// Eq04
		double uB = Eq19_getModifier(this, compartments, inputs.landType.N_lit);
		double la_4 = Eq22_getLeachingLA4(this, compartments, inputs.landType.N_lit);
		//double uk = Eq20_getModifier(this, compartments, N_lit);
		double C4_C1_ass = Eq17_getDailyCarbonStockTransferFromC1ToC4(this, compartments, uB, la_4, uk);
		//double uB = Eq19_getModifier(this, compartments, N_lit);
		double la_1 = Eq21_getLeachingLA1(this, compartments, inputs.landType.N_lit);
		//double uk = Eq20_getModifier(this, compartments, N_lit);
		double C4_C2_ass = Eq18_getDailyCarbonStockTransferFromC2ToC4(this, compartments, uB, la_1, uk);
		compartments.deltaC4 = Eq04_getDailyChangeC4(this, compartments, C4_C1_ass, C4_C2_ass);

		// Eq05
		double C5_C4_gen = Eq23_getDailyCarbonStockTransferFromC4ToC5(this, compartments);
		double C5_C2_frg = Eq24_getDailyCarbonStockTransferFromC2ToC5(this, compartments);
		double C5_C3_frg = Eq25_getDailyCarbonStockTransferFromC3ToC5(this, compartments);
		compartments.deltaC5 = Eq05_getDailyChangeC5(this, compartments, C5_C4_gen, C5_C2_frg, C5_C3_frg);

		// Eq06
		double C6_i_in = Eq15_calculate(this, inputs.CT_i, inputs.landType.f_sol);
		//double la_4 = Eq22_getLeachingLA4(this, compartments, N_lit);
		//double uk = Eq20_getModifier(this, compartments, N_lit);
		double C6_C1_in = Eq28_getDailyCarbonStockTransferFromC1ToC6(this, compartments, la_4, uk);
		//double la_1 = Eq21_getLeachingLA1(this, compartments, N_lit);
		//double uk = Eq20_getModifier(this, compartments, N_lit);
		double C6_C2_in = Eq29_getDailyCarbonStockTransferFromC2ToC6(this, compartments, la_1, uk);
		double C6_C3_in = Eq30_getDailyCarbonStockTransferFromC3ToC6(this, compartments);
		double C6_C4_in = Eq31_getDailyCarbonStockTransferFromC4ToC6(this, compartments);
		double C8_C6_in = Eq33_getDailyCarbonStockTransferFromC6ToC8(this, compartments);
		compartments.deltaC6 = Eq06_getDailyChangeC6(this, compartments, C6_i_in, C6_C1_in, C6_C2_in, C6_C3_in, C6_C4_in, C8_C6_in);

		elapsedTimes[0] += (System.currentTimeMillis() - initTime);
		
		initTime = System.currentTimeMillis();
		
		// Eq07
		//double uB = Eq19_getModifier(this, compartments, N_lit);
		//double la_4 = Eq22_getLeachingLA4(this, compartments, N_lit);
		//double uk = Eq20_getModifier(this, compartments, N_lit);
		double C1_C02 = Eq38_getDailyCarbonStockTransferFromC1ToC7(this, compartments, uB, la_4, uk);
		//double uB = Eq19_getModifier(this, compartments, N_lit);
		//double la_1 = Eq21_getLeachingLA1(this, compartments, N_lit);
		//double uk = Eq20_getModifier(this, compartments, N_lit);
		double C2_C02 = Eq39_getDailyCarbonStockTransferFromC2ToC7(this, compartments, uB, la_1, uk);
		double C3_C02 = Eq40_getDailyCarbonStockTransferFromC3ToC7(this, compartments);
		double C4_C02 = Eq41_getDailyCarbonStockTransferFromC4ToC7(this, compartments);
		double C5_C02 = Eq42_getDailyCarbonStockTransferFromC5ToC7(this, compartments);
		double C8_C02 = Eq43_getDailyCarbonStockTransferFromC8ToC7(this, compartments);
		double C9_C02 = Eq44_getDailyCarbonStockTransferFromC9ToC7(this, compartments);
		double C10_C02 = Eq45_getDailyCarbonStockTransferFromC10ToC7(this, compartments);
		compartments.deltaC7 = Eq07_getDailyChangeC7(this, compartments, C1_C02, C2_C02, C3_C02, C4_C02, C5_C02, C8_C02, C9_C02, C10_C02);

		// Eq08
		double C8_C5_in = Eq32_getDailyCarbonStockTransferFromC5ToC8(this, compartments);
		//double C8_C6_in = Eq33_getDailyCarbonStockTransferFromC6ToC8(this, compartments);
		double C8_C10_in = Eq34_getDailyCarbonStockTransferFromC10ToC8(this, compartments);

		double K_lm = Eq35_getBindingAffinityL_k_lm(inputs.soil_pH);
		double Q_max = Eq36_getMaximumSorptionCapacityQ_max(inputs.bulkDensity, inputs.sandProportion, inputs.rockProportion);
		double sorption = Eq37_getSorption(compartments, K_lm, Q_max);
		compartments.deltaC8 = Eq08_getDailyChangeC8(this, compartments, C8_C5_in, C8_C6_in, C8_C10_in, sorption);

		// Eq09
		//double sorption = Eq37_getSorption(compartments, soil_pH, bulkDensity, sandProportion, rockProportion);
		compartments.deltaC9 = Eq09_getDailyChangeC9(this, compartments, sorption);

		// Eq10
		double C10_C2_frg = Eq26_getDailyCarbonStockTransferFromC2ToC10(this, compartments);
		double C10_C3_frg = Eq27_getDailyCarbonStockTransferFromC3ToC10(this, compartments);
		compartments.deltaC10 = Eq10_getDailyChangeC10(this, compartments, C10_C2_frg, C10_C3_frg);

		// Eq11
		compartments.deltaC11 = Eq11_getDailyChangeC11(this, compartments);

		elapsedTimes[1] += (System.currentTimeMillis() - initTime);

		
//		return pred;
	}

	/**
	 * Predict C stocks in the different compartments after one day.<p>
	 * Calls {@link SoilCarbonPredictor#predictDailyCStockChanges(SoilCarbonPredictorCompartments, SoilCarbonPredictorInput, double)} and
	 * then {@link SoilCarbonPredictorCompartments#updateStocks()}.
	 * @param compartments a SoilCarbonPredictorCompartments instance
	 * @param inputs a SoilCarbonPredictorInput instance
	 * @param Tmod the temperature modifier to be applied (1.0 means no change)
	 */
	public void predictDailyCStocks(SoilCarbonPredictorCompartments compartments, SoilCarbonPredictorInput inputs, double Tmod) {
		predictDailyCStockChanges(compartments, inputs, Tmod);
		compartments.updateStocks();
	}
	
	/**
	 * Predict the stocks on an annual basis. <p>
	 * If the dailySoilTemperatureC is null, the method relies on the mean temperature and range to
	 * estimate the daily temperature.
	 * @param compartments a SoilCarbonPredictorCompartments instance that contains the initial carbon stocks
	 * @param inputs a SoilCarbonPredictorInput instance
	 * @param dailySoilTemperatureC an array of 365 or 366 slots containing the daily soil temperature.
	 */
	public void predictAnnualCStocks(SoilCarbonPredictorCompartments compartments, SoilCarbonPredictorInput inputs) {
		for (int day = 0; day < compartments.dailySoilTemperature.length; day++) {
			double TmodLocal = 0.0;
			long initTime = System.currentTimeMillis();
			TmodLocal = Weibull_getTemperatureModifier(this, compartments.dailySoilTemperature[day]);
			elapsedTimes[2] += (System.currentTimeMillis() - initTime);

			initTime = System.currentTimeMillis();
			if (TmodLocal > 0.0) {
				predictDailyCStocks(compartments, inputs, TmodLocal);
			}
			elapsedTimes[3] += (System.currentTimeMillis() - initTime);
		}
	}

}
