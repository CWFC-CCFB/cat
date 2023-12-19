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
package lerfob.carbonbalancetool;

/**
 * An implementation of decay function relying on the Weibull distribution.
 * @author Mathieu Fortin - December 2023
 */
public class CATWeibullDecayFunction implements CATDecayFunction {

	private double averageLifetimeYr;
	private double exponent = 5d;
	
	public CATWeibullDecayFunction() {}
	
	@Override
	public double getInfiniteIntegral() {return averageLifetimeYr;}

	@Override
	public void setAverageLifetimeYr(double averageLifetimeYr) {
		this.averageLifetimeYr = averageLifetimeYr;
	}

	@Override
	public double getValueAtTime(double timeYr) {
		return Math.exp(-Math.pow(timeYr / averageLifetimeYr, exponent));
	}

}
