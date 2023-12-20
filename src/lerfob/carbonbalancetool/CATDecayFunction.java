/*
 * This file is part of the CAT library.
 *
 * Copyright (C) 2012 Mathieu Fortin for LERFOB INRA/AgroParisTech
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
 * The decay function interface is designated for lifetime decreasing functions. This interface serves 
 * to actualize the EndProduct instance throughout their useful lifetime. 
 * @author Mathieu Fortin - October 2010
 */
public interface CATDecayFunction {

	/**
	 * Enum associated to the implementation of CATDecayFunction.
	 * @author Mathieu Fortin - December 2023
	 */
	public static enum DecayFunctionType {
		Exponential,
		Weibull;
	}
	
	/**
	 * This method returns the value of the infinite integral of the decay function.
	 * @return the value of the infinite integral (double)
	 */
	public abstract double getInfiniteIntegral();

	public abstract void setAverageLifetimeYr(double averageLifetimeYr);
	
	public abstract double getValueAtTime(double timeYr);
	
}
