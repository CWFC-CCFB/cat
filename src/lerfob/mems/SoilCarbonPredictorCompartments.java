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

import repicea.simulation.HierarchicalLevel;
import repicea.simulation.MonteCarloSimulationCompliantObject;

import java.security.InvalidParameterException;

/**
 * The SoilCarbonPredictorCompartments class contains the carbon stocks of 11 compartments.<p>
 * 
 * These compartments are:
 * <ul>
 * <li>C1: Water-soluble litter (litter)
 * <li>C2: Acid-soluble litter (litter)
 * <li>C3: Acid-insoluble litter (litter)
 * <li>C4: Microbial biomass (litter)
 * <li>C5: Heavy-particulate organic matter (soil)
 * <li>C6: Organic layer DOM (litter)
 * <li>C7: Atmospheric CO2 (atmosphere)
 * <li>C8: Mineral soil DOM (soil)
 * <li>C9: Mineral-associated organic matter (soil)
 * <li>C10: Light-particulate organic matter (soil)
 * <li>C11: Leached DOM (runoff)
 * </ul>
 * where DOM stands for Dead Organic Matter.
 * @author Jean-Francois Lavoie and Mathieu Fortin - February 2023
 */
public class SoilCarbonPredictorCompartments implements MonteCarloSimulationCompliantObject, Cloneable  {

    double MAT;                         // Mean Annual Temperature
    double Trange;                      // The difference between the maximum daily soil temperature and the minimum daily soil temperature in Celsius
	
	public double C1;     // Water soluble litter
    public double C2;     // Acid-soluble litter
    public double C3;     // Acid-insoluble litter
    public double C4;     // Microbial biomass
    public double C5;     // Coarse, heavy POM
    public double C6;     // Litter layer DOM
    public double C7;     // Emitted CO2
    public double C8;     // Soil layer DOM
    public double C9;     // Mineral-associated OM
    public double C10;    // Light POM
    public double C11;   // Leached DOM
    private int realizationID;

    double deltaC1;
    double deltaC2;
    double deltaC3;
    double deltaC4;
    double deltaC5;
    double deltaC6;
    double deltaC7;
    double deltaC8;
    double deltaC9;
    double deltaC10;
    double deltaC11;
    
    
    /**
     * Constructor 1.<p>
     * Initialize the different carbon compartments through an array. The
     * array is expected to have a length of 11. The stocks are initialized
     * in compartments C1 to C11 by iterating through the array.
     * @param initialStock a 11-slot array that contains the initial carbon stocks
     * @param MAT the Mean Annual Temperature
     * @param Trange the annual temperature range
     */
    public SoilCarbonPredictorCompartments(double[] initialStock, double MAT, double Trange) {
        if (initialStock.length != 11)
            throw new InvalidParameterException();

        C1 = initialStock[0];
        C2 = initialStock[1];
        C3 = initialStock[2];
        C4 = initialStock[3];
        C5 = initialStock[4];
        C6 = initialStock[5];
        C7 = initialStock[6];
        C8 = initialStock[7];
        C9 = initialStock[8];
        C10 = initialStock[9];
        C11 = initialStock[10];
        this.MAT = MAT;
        this.Trange = Trange;
    }

    /**
     * Constructor 2.<p>
     * Initialize the different carbon compartments to the same initial value.
     * @param initialValue the initial amount of carbon in each compartment
     * @param MAT the Mean Annual Temperature
     * @param Trange the annual temperature range
     */
    public SoilCarbonPredictorCompartments(double initialValue, double MAT, double Trange) {
        C1 = initialValue;
        C2 = initialValue;
        C3 = initialValue;
        C4 = initialValue;
        C5 = initialValue;
        C6 = initialValue;
        C7 = initialValue;
        C8 = initialValue;
        C9 = initialValue;
        C10 = initialValue;
        C11 = initialValue;
        this.MAT = MAT;
        this.Trange = Trange;
    }


    /**
     * Calculate the sum of the carbon stocks in the different compartments.
     * @return the sum of carbon
     */
    public double getSum() {
        return C1+C2+C3+C4+C5+C6+C7+C8+C9+C10+C11;
    }

    public Double[] getBins() {
        Double[] result = new Double[11];

        result[0] = C1;
        result[1] = C2;
        result[2] = C3;
        result[3] = C4;
        result[4] = C5;
        result[5] = C6;
        result[6] = C7;
        result[7] = C8;
        result[8] = C9;
        result[9] = C10;
        result[10] = C11;

        return result;
    }

    /**
     * Returns the sum of litter bins
     * @return the sum of the litter bins in grams of carbon per squared meter
     */
    public double getLitterBinsgCm2() {
        return C1 + C2 + C3 + C4 + C6;
    }

    /**
     * Returns the sum of soil bins
     * @return the sum of the soil bins in grams of carbon per squared meter
     */
    public double getSoilBinsgCm2() {
        return C5 + C9 + C10;
    }

    /**
     * Add the carbon stocks from another instance.
     * @param o a SoilCarbonPredictorCompartments instance
     */
    public void add(SoilCarbonPredictorCompartments o) {
        C1 += o.C1;
        C2 += o.C2;
        C3 += o.C3;
        C4 += o.C4;
        C5 += o.C5;
        C6 += o.C6;
        C7 += o.C7;
        C8 += o.C8;
        C9 += o.C9;
        C10 += o.C10;
        C11 += o.C11;
    }

    @Override
    public String getSubjectId() {
        return null;
    }

    @Override
    public HierarchicalLevel getHierarchicalLevel() {
        return null;
    }

    @Override
    public int getMonteCarloRealizationId() {
        return realizationID;
    }

    protected void updateStocks() {
    	C1 += deltaC1;
    	C2 += deltaC2;
    	C3 += deltaC3;
    	C4 += deltaC4;
    	C5 += deltaC5;
    	C6 += deltaC6;
    	C7 += deltaC7;
    	C8 += deltaC8;
    	C9 += deltaC9;
    	C10 += deltaC10;
    	C11 += deltaC11;
    }
    
    @Override
    public SoilCarbonPredictorCompartments clone() {
        try {
            return (SoilCarbonPredictorCompartments)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Could not clone SoilCarbonPredictorCompartments instance");
        }
    }
    
    protected double getSumOfDailyChange() {
       	return deltaC1 + deltaC2 + deltaC3 + deltaC4 + deltaC5 + deltaC6 + deltaC7 + deltaC8 + deltaC9 + deltaC10 + deltaC11;
    }
    
}
