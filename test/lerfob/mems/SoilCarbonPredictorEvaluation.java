/*
 * This file is part of the mems library.
 *
 * Copyright (C) 2022-24 His Majesty the King in Right of Canada
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

import org.junit.Assert;
import org.junit.Test;

import repicea.stats.data.DataSet;
import repicea.util.ObjectUtility;

/**
 * A class to evaluate MEMS against ground truth.
 * @author Mathieu Fortin - January 2024 
 */
public class SoilCarbonPredictorEvaluation {

	@Test
	public void testAgainstFOMChronosequence() throws Exception {
		String thisPath = ObjectUtility.getPackagePath(SoilCarbonPredictorEvaluation.class);
		String FOMFilename = thisPath + "ChronosequenceFOMFormatted.csv";
		DataSet ds = new DataSet(FOMFilename, true); // true: autoload the file
		Assert.assertEquals("Testing number of observations", 403, ds.getNumberOfObservations());
		Assert.assertEquals("Testing nb of fields", 15, ds.getFieldNames().size());
	}
	

}
