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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cedarsoftware.util.io.JsonReader;

import lerfob.carbonbalancetool.productionlines.AbstractProcessor;
import lerfob.carbonbalancetool.productionlines.ProductionLineProcessor;
import repicea.simulation.processsystem.Processor;

/**
 * The AffiliereJSONReader class reads a JSON file from AFFILIERE and 
 * converts it into a flux configuration file for CAT.
 * @author Mathieu Fortin - October 2023
 */
public class AffiliereJSONImportReader {

	private static class FutureLink {
		final Processor fatherProcessor;
		final Processor childProcessor;
		final double value;
		
		FutureLink(Processor fatherProcessor, Processor childProcessor, double value) {
			this.fatherProcessor = fatherProcessor;
			this.childProcessor = childProcessor;
			this.value = value;
		}
	}
		
	protected static int OFFSET = 150;
	
	protected final LinkedHashMap<?,?> mappedJSON;
	protected final Map<String, Processor> processors;
	protected final List<ProductionLineProcessor> endProductProcessors;
	
	/**
	 * Constructor.
	 * @param file the File instance to be read.
	 * @throws FileNotFoundException if the file cannot be found.
	 */
	public AffiliereJSONImportReader(File file) throws FileNotFoundException {
		this(new FileInputStream(file));
	}

	/**
	 * Constructor.
	 * @param url the url of the file to be read.
	 * @throws IOException if the url cannot produce an input stream.
	 */
	public AffiliereJSONImportReader(URL url) throws IOException {
		this(url.openStream());
	}
	
	@SuppressWarnings("unchecked")
	private AffiliereJSONImportReader(InputStream is) {
		JsonReader reader = new JsonReader(is);
		mappedJSON = (LinkedHashMap<?,?>) reader.readObject();
		reader.close();
		LinkedHashMap<?,?> processorJSONMap = (LinkedHashMap<?,?>) mappedJSON.get("nodes");
		LinkedHashMap<?,?> linkJSONMap = (LinkedHashMap<?,?>) mappedJSON.get("links");
		processors = new HashMap<String, Processor>();
		endProductProcessors = new ArrayList<ProductionLineProcessor>();
		for (Object o : processorJSONMap.values()) {
			LinkedHashMap<String, Object> oMap = (LinkedHashMap<String, Object>) o;
			String id = oMap.get("idNode").toString();
			Processor p = AbstractProcessor.createProcessor(oMap);
			processors.put(id, p);
		}

		Map<Processor, List<FutureLink>> linkMap = new HashMap<Processor, List<FutureLink>>();
		
		for (String fatherProcessorName : processors.keySet()) {
			for (Object linkValue : linkJSONMap.values()) {
				LinkedHashMap<?,?> linkProperties = (LinkedHashMap<?,?>)  linkValue;
				if (linkProperties.containsKey("idSource") && linkProperties.get("idSource").equals(fatherProcessorName)) {
					Processor fatherProcessor = processors.get(fatherProcessorName);
					if (linkProperties.containsKey("idTarget")) {
						String childProcessorName = (String) linkProperties.get("idTarget");
						Processor childProcessor = processors.get(childProcessorName);
						if (childProcessor != null) {
							LinkedHashMap<?, ?> valueMap = (LinkedHashMap<?,?>) linkProperties.get("value"); 
							double value;
							if (valueMap.containsKey("proportion")) {
								value = ((Number) valueMap.get("proportion")).doubleValue();
							} else {
								value = ((Number) valueMap.get("value")).doubleValue();
							}
							if (!linkMap.containsKey(fatherProcessor)) {
								linkMap.put(fatherProcessor, new ArrayList<FutureLink>());
							}
							linkMap.get(fatherProcessor).add(new FutureLink(fatherProcessor, childProcessor, value));
						}
					}
				}
			}
		}
		
		for (List<FutureLink> futureLinks : linkMap.values()) {
			double sumValues = 0d;
			for (FutureLink fLink : futureLinks) {
				sumValues += fLink.value;
			}
			
			for (FutureLink fLink : futureLinks) {
				fLink.fatherProcessor.addSubProcessor(fLink.childProcessor);
				fLink.fatherProcessor.getSubProcessorIntakes().put(fLink.childProcessor, fLink.value / sumValues * 100);
			}
		}
	}

	/**
	 * Provide a map of the processors read by this AffiliereJSONReader instance.<p>
	 * Map keys are the names of the processors whereas the values are the ProductionLineProcessors
	 * instances.
	 * @return a Map
	 */
	public Map<String, Processor> getProcessors() {
		return processors;
	}
}
