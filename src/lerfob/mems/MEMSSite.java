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
package lerfob.mems;

import repicea.stats.estimators.mcmc.MetropolisHastingsAlgorithm;
import repicea.util.REpiceaTranslator;
import repicea.util.REpiceaTranslator.TextableEnum;

public class MEMSSite {
	
    public static enum SiteType implements TextableEnum {
        MontmorencyBL0_95CO0_10("Montmorency Forest", "For\u00E8t Montmorency"),
        Hereford("Hereford Forest", "For\u00E8t de Hereford");

        SiteType(String englishText, String frenchText) {
            setText(englishText, frenchText);
        }
        @Override
        public void setText(String englishText, String frenchText) {
            REpiceaTranslator.setString(this, englishText, frenchText);
        }
    }
    
    final MetropolisHastingsAlgorithm mha;
    final SoilCarbonPredictorInput inputs;
    
    MEMSSite(MetropolisHastingsAlgorithm mha, SoilCarbonPredictorInput inputs) {
    	this.mha = mha;
    	this.inputs = inputs;
    }

    public SoilCarbonPredictorInput getInputs() {
        return inputs;
    }

    public MetropolisHastingsAlgorithm getMetropolisHastingsAlgorithm() {
        return mha;
    }
    
}
