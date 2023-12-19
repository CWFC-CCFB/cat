/*
 * This file is part of the lerfob-forestools library.
 *
 * Copyright (C) 2012 Mathieu Fortin for LERFOB INRA/AgroParisTech, 
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
 * This class provides an exponential function to act as 
 * decay function in the calculation of end product lifetime.
 * @author Mathieu Fortin - July 2010
 */
public class CATExponentialDecayFunction implements CATDecayFunction {

	private double averageLifetimeYr;

	/**
	 * Constructor
	 */
	public CATExponentialDecayFunction() {}
	
	@Override
	public double getInfiniteIntegral() {
		return averageLifetimeYr;
	}


	@Override
	public void setAverageLifetimeYr(double averageLifetimeYr) {
		this.averageLifetimeYr = averageLifetimeYr;
	}

	@Override
	public double getValueAtTime(double timeYr) {
		return Math.exp(- timeYr / averageLifetimeYr);
	}
}


