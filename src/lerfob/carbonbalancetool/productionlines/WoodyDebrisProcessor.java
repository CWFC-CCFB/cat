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
package lerfob.carbonbalancetool.productionlines;

import java.awt.Container;

import repicea.simulation.processsystem.ProcessorButton;
import repicea.simulation.processsystem.SystemPanel;
import repicea.util.REpiceaTranslator;
import repicea.util.REpiceaTranslator.TextableEnum;

/**
 * A special class of Processor for woody debris.<p>
 * 
 * It inherits from the LeftHandSideProcessor class as these
 * processors are entry points into the flux configuration.
 * 
 * @author Mathieu Fortin - 2015
 */
@SuppressWarnings("serial")
public class WoodyDebrisProcessor extends LeftHandSideProcessor {

	/**
	 * An enum that distinguish three types of woody debris.<p>
	 * 
	 * The values of the enum are:
	 * <ul>
	 * <li> <b>CoarseWoodyDebris</b>: stumps and roots
	 * <li> <b>CommercialWoodyDebris</b>: commercial-sized part of the trees
	 * <li> <b>FineWoodyDebris</b>: branches below the commercial threshold
	 * </ul>
	 */
	public static enum WoodyDebrisProcessorID implements TextableEnum {
		CoarseWoodyDebris("Coarse Woody Debris", "Gros d\u00E9bris ligneux"),
		CommercialWoodyDebris("Commercial-sized Woody Debris", "D\u00E9bris ligneux commerciaux"),
		FineWoodyDebris("Fine Woody Debris", "Petit d\u00E9bris ligneux");

		
		WoodyDebrisProcessorID(String englishText, String frenchText) {
			setText(englishText, frenchText);
		}
		
		@Override
		public void setText(String englishText, String frenchText) {
			REpiceaTranslator.setString(this, englishText, frenchText);
		}

		@Override
		public String toString() {return REpiceaTranslator.getString(this);}
		
	}
	
	
	/**
	 * The WoodyDebrisProcessorButton class is the GUI implementation for 
	 * WoodyDebrisProcessor. It has a specific icon for better identification in the GUI.
	 * @author Mathieu Fortin - May 2014
	 */
	public static class WoodyDebrisProcessorButton extends LeftHandSideProcessorButton {

		/**
		 * Constructor.
		 * @param panel	a SystemPanel instance
		 * @param processor the WoodyDebrisProcessor that owns this button
		 */
		protected WoodyDebrisProcessorButton(SystemPanel panel, WoodyDebrisProcessor processor) {
			super(panel, processor);
		}

	}
	
	protected final WoodyDebrisProcessorID wdpID;
	
	protected WoodyDebrisProcessor(WoodyDebrisProcessorID wdpID) {
		this.wdpID = wdpID;
	}
	
	
	@Override
	public String getName() {return wdpID.toString();}
	
	@Override
	public ProcessorButton getUI(Container container) {
		if (guiInterface == null) {
			guiInterface = new WoodyDebrisProcessorButton((SystemPanel) container, this);
		}
		return guiInterface;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WoodyDebrisProcessor) {
			WoodyDebrisProcessor wdp = (WoodyDebrisProcessor) obj;
			if (wdp.wdpID.equals(wdpID)) {
				return true;
			}
		}
		return false;
	}
	
}
