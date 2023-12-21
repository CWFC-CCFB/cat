/*
 * This file is part of the CAT library.
 *
 * Copyright (C) 2023 His Majesty the King in Right of Canada
 * Author: Mathieu Fortin, Canadian Forest Service
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
package lerfob.carbonbalancetool.productionlines;

import org.junit.Assert;
import org.junit.Test;

import lerfob.carbonbalancetool.productionlines.DecayFunction.DecayFunctionType;
import lerfob.carbonbalancetool.productionlines.DecayFunction.LifetimeMode;

/**
 * Test different DecayFunction settings.
 * @author Mathieu Fortin - December 2023
 */
public class DecayFunctionTest {

	@Test
	public void testExponentialFunction() {
		DecayFunction df = new DecayFunction(null, LifetimeMode.AVERAGE, DecayFunctionType.Exponential, 10d);
		Assert.assertEquals("Testing half life", 10d * Math.log(2d), df.halfLifeYr, 1E-8);
		df.setHalfLifeYr(10);
		Assert.assertEquals("Testing half life", 10d / Math.log(2d), df.averageLifetimeYr, 1E-8);
	}
	
	@Test
	public void testWeibullFunction() {
		DecayFunction df = new DecayFunction(null, LifetimeMode.AVERAGE, DecayFunctionType.Exponential, 10d);
		df.functionType = DecayFunctionType.Weibull;
		df.setAverageLifetimeYr(df.averageLifetimeYr);
		Assert.assertEquals("Testing Weibull lambda", 10.89124421058335, df.weibullLambda, 1E-8);
		Assert.assertEquals("Testing average lifetime", 10, df.averageLifetimeYr, 1E-8);
		df.functionType = DecayFunctionType.Exponential;
		df.setHalfLifeYr(10);
		Assert.assertEquals("Testing average lifetime", 10d / Math.log(2d), df.averageLifetimeYr, 1E-8);
	}
	
	
}
