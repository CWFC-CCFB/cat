/*
 * This file is part of the lerfob-forestools library.
 *
 * Copyright (C) 2010-2016 Mathieu Fortin AgroParisTech/INRA UMR LERFoB, 
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
package lerfob.carbonbalancetool.sensitivityanalysis;

import repicea.math.Matrix;
import repicea.math.SymmetricMatrix;
import repicea.simulation.MonteCarloSimulationCompliantObject;
import repicea.simulation.SensitivityAnalysisParameter;
import repicea.stats.estimates.Estimate;

@SuppressWarnings({ "serial" })
public abstract class CATSensitivityAnalysisParameter<E extends Estimate<Matrix, SymmetricMatrix, ?>> extends SensitivityAnalysisParameter<E> {

	private double multiplier;

	protected CATSensitivityAnalysisParameter(boolean isParametersVariabilityEnabled) {
		super(isParametersVariabilityEnabled);
	}
	
	protected void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}
	
	protected double getMultiplier() {return multiplier;}
	
	protected double getParameterValueForThisSubject(MonteCarloSimulationCompliantObject subject) {
		if (isParametersVariabilityEnabled) {
			return getParametersForThisRealization(subject).scalarMultiply(getMultiplier()).scalarAdd(1d).getValueAt(0, 0);
		} else {
			return 1d;
		}
	}

	protected void setParametersVariabilityEnabled(boolean enabled) {
		this.isParametersVariabilityEnabled = enabled;
	}

}
