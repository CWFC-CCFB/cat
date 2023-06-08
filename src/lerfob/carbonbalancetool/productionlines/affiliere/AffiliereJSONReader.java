/*
 * This file is part of the CAT library.
 *
 * Copyright (C) 2023 His Majesty the King in Right of Canada
 * Author: Mathieu Fortin, Canadian Forest Service, Canadian Wood Fibre Centre
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
package lerfob.carbonbalancetool.productionlines.affiliere;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonReader;

import lerfob.carbonbalancetool.productionlines.ProductionLineProcessor;

public class AffiliereJSONReader {

	public static int OFFSET = 150;
	
	protected final JsonObject<?,?> mappedJSON;
	protected final Map<String, ProductionLineProcessor> processors;
	
	/**
	 * Constructor.
	 * @param filename the JSON file to be read.
	 * @throws FileNotFoundException if the file cannot be found.
	 */
	public AffiliereJSONReader(String filename) throws FileNotFoundException {
		// TODO check if extension is json. Otherwise throw an InvalidParameterException here
		FileInputStream fis = new FileInputStream(filename);
		JsonReader reader = new JsonReader(fis);
		mappedJSON = (JsonObject<?,?>) reader.readObject();
		reader.close();
		JsonObject<?,?> processorJSONMap = (JsonObject<?,?>) mappedJSON.get("nodes");
		JsonObject<?,?> linkJSONMap = (JsonObject<?,?>) mappedJSON.get("links");
		processors = new HashMap<String, ProductionLineProcessor>();
		for (Object o : processorJSONMap.values()) {
			String id = (String) ((JsonObject<?,?>) o).get("idNode");
			String name = (String) ((JsonObject<?,?>) o).get("name");
			int x = ((Number) ((JsonObject<?,?>) o).get("x")).intValue() + OFFSET;
			int y = ((Number) ((JsonObject<?,?>) o).get("y")).intValue();
			ProductionLineProcessor p = ProductionLineProcessor.createProductionLineProcessor(name, x, y);
			processors.put(id, p);
		}

		for (Object o : processorJSONMap.values()) {
			Object[] linkArray = (Object[]) ((JsonObject<?,?>) o).get("outputLinksId");
			if (linkArray.length > 0) {
				List<Double> values = new ArrayList<Double>();
				for (Object linkName : linkArray) {
					JsonObject<?,?> linkProperties = (JsonObject<?,?>) linkJSONMap.get(linkName.toString());
					double value = ((Number) ((JsonObject<?,?>) linkProperties.get("value")).get("value")).doubleValue();
					values.add(value);
				}

				List<Integer> subProcessorIntakes = calculateIntakes(values);
				
				for (int i = 0; i < linkArray.length; i++) {
					Object linkName = linkArray[i];
					JsonObject<?,?> linkProperties = (JsonObject<?,?>) linkJSONMap.get(linkName.toString());
					ProductionLineProcessor fatherProcessor = processors.get((String) linkProperties.get("idSource"));
					ProductionLineProcessor childProcessor = processors.get((String) linkProperties.get("idTarget"));
					fatherProcessor.addSubProcessor(childProcessor);
					fatherProcessor.getSubProcessorIntakes().put(childProcessor, subProcessorIntakes.get(i));
				}
			}
		}
	}
	

	/*
	 * Convert the absolute masses into proportions
	 */
	private static List<Integer> calculateIntakes(List<Double> values) {
		double sum = 0d;
		for (double v : values) {
			sum += v;
		}
		List<Integer> intakes = new ArrayList<Integer>();
		for (double v : values) {
			long roundedValue = Math.round(v / sum * 100);
			intakes.add(((Number) roundedValue).intValue());
		}
//		int sumIntakes = 0;
//		for (int i : intakes) {
//			sumIntakes += i;
//		}
		return intakes;
	}
	
	
}
