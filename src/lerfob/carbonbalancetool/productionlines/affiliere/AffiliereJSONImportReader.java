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
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cedarsoftware.util.io.JsonReader;

import lerfob.carbonbalancetool.productionlines.AbstractProcessor;
import repicea.simulation.processsystem.Processor;

/**
 * The AffiliereJSONReader class reads a JSON file from AFFILIERE and 
 * converts it into a flux configuration file for CAT.
 * @author Mathieu Fortin - October 2023
 */
public class AffiliereJSONImportReader {

	private static final List<String> NodeTypesToBeDiscarded = new ArrayList<String>();
	static {
		NodeTypesToBeDiscarded.add("echange");		// we do not want import / export processors MF2024-03-01
	}
	
	public enum AFFiliereUnit {
		VolumeM3("1000m3"), 
		DryBiomassMg("1000t");
	
		final String suffix;
		
		AFFiliereUnit(String suffix) {
			this.suffix = suffix;
		}
	}

	public enum AFFiliereStudy {
		AFFiliere("AF Fili\u00E8res"),
		Carbone4("Carbone 4"),
		BACCFIRE("BACCFIRE"),
		MFAB("MFAB");
		
		final String prefix;
		
		AFFiliereStudy(String prefix) {
			this.prefix = prefix;
		}
	}
	
	private static class FutureLink {
		final Processor fatherProcessor;
		final Processor childProcessor;
		final double value;
		final boolean isPercent;
		
		FutureLink(Processor fatherProcessor, Processor childProcessor, double value, boolean isPercent) {
			this.fatherProcessor = fatherProcessor;
			this.childProcessor = childProcessor;
			this.value = value;
			this.isPercent = isPercent;
		}
	}
		
	protected static int OFFSET = 150;
	
	protected final LinkedHashMap<?,?> mappedJSON;
	protected final Map<String, Processor> processors;
	protected final List<Processor> endProductProcessors;
	protected final List<Processor> ioProcessors;
	protected final AFFiliereStudy study;
	protected final AFFiliereUnit unit;
	
	/**
	 * Constructor.
	 * @param file the File instance to be read.
	 * @throws FileNotFoundException if the file cannot be found.
	 */
	public AffiliereJSONImportReader(File file, AFFiliereStudy study, AFFiliereUnit unit) throws FileNotFoundException {
		this(new FileInputStream(file), study, unit);
	}

//	/**
//	 * Constructor.
//	 * @param url the url of the file to be read.
//	 * @throws IOException if the url cannot produce an input stream.
//	 */
//	public AffiliereJSONImportReader(URL url, AFFiliereStudy study, AFFiliereUnit unit) throws IOException {
//		this(url.openStream(), study, unit);
//	}

//	/**
//	 * Constructor.
//	 * @param file the File instance to be read.
//	 * @throws FileNotFoundException if the file cannot be found.
//	 */
//	public AffiliereJSONImportReader(File file) throws FileNotFoundException {
//		this(new FileInputStream(file), null, null);
//	}

	/**
	 * Constructor.
	 * @param url the url of the file to be read.
	 * @throws IOException if the url cannot produce an input stream.
	 */
	public AffiliereJSONImportReader(URL url) throws IOException {
		this(url.openStream(), null, null);
	}

//	private String formatTagMap(LinkedHashMap<String,Object> oMap) {
//		StringBuilder sb = new StringBuilder();
//		for (String k : oMap.keySet()) {
//			sb.append(k);
//			sb.append("=");
//			sb.append(((Object[]) oMap.get(k))[0].toString());
//			sb.append(",");
//		}
//		return "{" + sb.toString() + "}";
//	}
	
	private void addToListIfRelevant(Processor p, LinkedHashMap<String, Object> oMap, String key, String expression, List<Processor> outputList) {
		if (oMap.containsKey(key) ) {
			Object o = oMap.get(key);
			String str = ((Object[]) o)[0].toString();
			if (str.startsWith(expression)) {
				outputList.add(p);
			}
		}
	}
	
	private static boolean containsUndesiredNodeTypeTag(Object[] tags) {
		for (Object o : tags) {
			if (NodeTypesToBeDiscarded.contains(o.toString()))
				return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private AffiliereJSONImportReader(InputStream is, AFFiliereStudy study, AFFiliereUnit unit) {
		this.study = study == null ?
				AFFiliereStudy.AFFiliere :
					study;
		this.unit = unit == null ?
				AFFiliereUnit.DryBiomassMg :
					unit;
		JsonReader reader = new JsonReader(is);
		mappedJSON = (LinkedHashMap<?,?>) reader.readObject();
		reader.close();
		LinkedHashMap<?,?> processorJSONMap = (LinkedHashMap<?,?>) mappedJSON.get(AffiliereJSONFormat.NODES_PROPERTY);
		LinkedHashMap<?,?> linkJSONMap = (LinkedHashMap<?,?>) mappedJSON.get(AffiliereJSONFormat.LINKS_PROPERTY);
		processors = new HashMap<String, Processor>();
		endProductProcessors = new ArrayList<Processor>();
		ioProcessors = new ArrayList<Processor>();
		for (Object o : processorJSONMap.values()) {
			LinkedHashMap<String, Object> oMap = (LinkedHashMap<String, Object>) o;
			LinkedHashMap<String, Object> tagMap = (LinkedHashMap<String, Object>) oMap.get(AffiliereJSONFormat.NODE_TAGS_PROPERTY);
			Object[] nodeTypeTags = (Object[]) tagMap.get(AffiliereJSONFormat.NODE_TAGS_NODETYPE_PROPERTY);
			if (!containsUndesiredNodeTypeTag(nodeTypeTags)) {
				String id = oMap.get(AffiliereJSONFormat.NODE_IDNODE_PROPERTY).toString();
				Processor p = AbstractProcessor.createProcessor(oMap);
				processors.put(id, p);
				addToListIfRelevant(p, tagMap, "Catégorie noeud", "Produit fini", endProductProcessors);
			}
		}
		
		for (Processor p : endProductProcessors) {
			System.out.println(p.getName());
		}

		Map<Processor, List<FutureLink>> linkMap = new HashMap<Processor, List<FutureLink>>();
		
		String studyUnitAttribute = this.study.prefix + " " + this.unit.suffix;
		
		for (String fatherProcessorName : processors.keySet()) {
			for (Object linkValue : linkJSONMap.values()) {
				LinkedHashMap<?,?> linkProperties = (LinkedHashMap<?,?>)  linkValue;
				if (linkProperties.containsKey(AffiliereJSONFormat.LINK_IDSOURCE_PROPERTY) && 
						linkProperties.get(AffiliereJSONFormat.LINK_IDSOURCE_PROPERTY).equals(fatherProcessorName)) {
					Processor fatherProcessor = processors.get(fatherProcessorName);
//					boolean isEndProductProcessor = endProductProcessors.contains(fatherProcessor);
					if (linkProperties.containsKey(AffiliereJSONFormat.LINK_IDTARGET_PROPERTY)) {
						String childProcessorName = (String) linkProperties.get(AffiliereJSONFormat.LINK_IDTARGET_PROPERTY);
						Processor childProcessor = processors.get(childProcessorName);
						if (childProcessor != null) {
							LinkedHashMap<?, ?> valueMap = (LinkedHashMap<?,?>) linkProperties.get(AffiliereJSONFormat.LINK_VALUE_PROPERTY); 
							boolean isPercent = false;
							double value;
							if (valueMap.containsKey(AffiliereJSONFormat.LINK_VALUE_PERCENT_PROPERTY)) {
								value = ((Number) valueMap.get(AffiliereJSONFormat.LINK_VALUE_PERCENT_PROPERTY)).doubleValue();
								isPercent = true;
							} else {
								if (valueMap.containsKey(studyUnitAttribute)) {
									valueMap = (LinkedHashMap<?,?>) valueMap.get(studyUnitAttribute);
								}
								value = ((Number) valueMap.get("value")).doubleValue();
							}
							if (!linkMap.containsKey(fatherProcessor)) {
								linkMap.put(fatherProcessor, new ArrayList<FutureLink>());
							}
							linkMap.get(fatherProcessor).add(new FutureLink(fatherProcessor, childProcessor, value, isPercent));
						}
					}
				}
			}
		}
		
		for (List<FutureLink> futureLinks : linkMap.values()) {
			double sumValues = 0d;
			Boolean lastIsPercent = null;
			for (FutureLink fLink : futureLinks) {
				if (lastIsPercent == null) {
					lastIsPercent = fLink.isPercent;
				} else {
					if (!lastIsPercent.equals(fLink.isPercent)) { // check if the isPercent member is consistent across the links
						throw new InvalidParameterException("It seems the values of some links are mix of percentage and absolute values!");
					}
				}
				sumValues += fLink.value;
			}
			
			if (!lastIsPercent) {
				for (FutureLink fLink : futureLinks) {
					fLink.fatherProcessor.addSubProcessor(fLink.childProcessor);
					fLink.fatherProcessor.getSubProcessorIntakes().put(fLink.childProcessor, fLink.value / sumValues * 100);
				}
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
