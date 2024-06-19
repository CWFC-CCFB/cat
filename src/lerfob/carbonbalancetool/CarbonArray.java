/*
 * This file is part of the CAT library.
 *
 * Copyright (C) 2024 His Majesty the King in Right of Canada
 * Author: Mathieu Fortin, Canadian Forest Service 
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
package lerfob.carbonbalancetool;

/**
 * A class implementing a simple array that contains the carbon stocks.
 */
public class CarbonArray {

	private double[] calculatedCarbonArray;

	public CarbonArray() {}
	
	public CarbonArray(int size) {
		initializeCarbonArray(size);
	}
	
	public void initializeCarbonArray(int size) {
		calculatedCarbonArray = new double[size];
	}
	
	protected void setCarbonIntoArray(int indexDate, double d) {
		calculatedCarbonArray[indexDate] = d;
	}

	public double[] getCarbonArray() {
		return calculatedCarbonArray;
	}
}
