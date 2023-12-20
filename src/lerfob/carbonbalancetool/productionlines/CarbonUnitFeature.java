/*
 * This file is part of the lerfob-forestools library.
 *
 * Copyright (C) 2010-2012 Mathieu Fortin for LERFOB INRA/AgroParisTech, 
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

import java.io.Serializable;

import lerfob.carbonbalancetool.productionlines.DecayFunction.DecayFunctionType;
import lerfob.carbonbalancetool.productionlines.DecayFunction.LifetimeMode;
import repicea.gui.REpiceaUIObject;
import repicea.serial.SerializerChangeMonitor;

/**
 * The CarbonUnitFeature class defines some characteristics of carbon units contained in a wood piece. <p>
 * It contains the lifetime, use class, and others characteristics. 
 * @author Mathieu Fortin - 2012
 */
@SuppressWarnings("serial")
public class CarbonUnitFeature implements Serializable, REpiceaUIObject {

	static {
		SerializerChangeMonitor.registerClassNameChange("lerfob.carbonbalancetool.productionlines.CarbonUnitFeature$LifetimeMode",
				"lerfob.carbonbalancetool.productionlines.DecayFunction$LifetimeMode");
	}

//	private static final double HALFLIFE_TO_MEANLIFETIME_CONSTANT = 1d / Math.log(2d);


	/**
	 * IMPORTANT: This field can be either the average lifetime or the half-life. The conversion is handled 
	 * by the getAverageLifetime() method.
	 * @deprecated the average life time and the half life are now members of the DecayFunction class
	 * @see {@link  lerfob.carbonbalancetool.productionlines.DecayFunction}
	 */
	@Deprecated
	protected double averageLifetime;
	/**
	 * @deprecated the lifetime model is now a member of the DecayFunction class
	 * @see {@link  lerfob.carbonbalancetool.productionlines.DecayFunction}
	 */
	@Deprecated
	private LifetimeMode lifetimeMode;

	private DecayFunction decayFunction;
	
	private AbstractProductionLineProcessor processor;
	
	private transient CarbonUnitFeaturePanel userInterfacePanel;


	/**
	 * Constructor for GUI mode.
	 * @param processor an AbstractProductionLineProcessor instance, which hosts this CarbonUnitFeature instance.
	 */
	protected CarbonUnitFeature(AbstractProductionLineProcessor processor) {
		setProcessor(processor);
//		lifetimeMode = LifetimeMode.HALFLIFE; // default value 
		decayFunction = new DecayFunction(this, LifetimeMode.HALFLIFE, DecayFunctionType.Exponential, 0d);	// default IPCC setup (exponential and half life), the half life is set later on.
	}
	
	@Deprecated
	private LifetimeMode getLifetimeMode() {
		if (lifetimeMode == null) {
			lifetimeMode = LifetimeMode.AVERAGE;	// for former implementation
		}
		return lifetimeMode;
	}

	protected DecayFunction getDecayFunction() {
		if (decayFunction == null) {
			decayFunction = new DecayFunction(this, getLifetimeMode(), DecayFunctionType.Exponential, averageLifetime);
		} 
		return decayFunction;
	}
	
//	/*
//	 * Accessors
//	 */
//	protected double getAverageLifetime(MonteCarloSimulationCompliantObject subject) {
//		double meanLifetime; 
//		if (getLifetimeMode() == LifetimeMode.AVERAGE) {
//			meanLifetime = averageLifetime;
//		} else {
//			meanLifetime = averageLifetime * HALFLIFE_TO_MEANLIFETIME_CONSTANT; // TODO fix that the average lifetime should be calculated using the 
//		}
//		if (subject != null) {
//			return meanLifetime * CATSensitivityAnalysisSettings.getInstance().getModifier(VariabilitySource.Lifetime, subject, toString());
//		} else {
//			return meanLifetime;
//		}
//	}
	
	@Deprecated
	protected void setAverageLifetime(double d) {averageLifetime = d;}
	
	protected CarbonUnitFeaturePanel getUserInterfacePanel() {return userInterfacePanel;}
	protected void setUserInterfacePanel(CarbonUnitFeaturePanel panel) {this.userInterfacePanel = panel;}
	
	protected AbstractProductionLineProcessor getProcessor() {return processor;}
	protected void setProcessor(AbstractProductionLineProcessor processor) {this.processor = processor;}
	
	

	@Override
	public CarbonUnitFeaturePanel getUI() {
		if (getUserInterfacePanel() == null) {
			setUserInterfacePanel(new CarbonUnitFeaturePanel(this));
		}
		return getUserInterfacePanel();
	}

	@Override 
	public boolean isVisible() {
		return getUserInterfacePanel() != null && getUserInterfacePanel().isVisible();
	}
	
//	@Override
//	public boolean equals(Object obj) {
//		if (!(obj instanceof CarbonUnitFeature)) {
//			return false;
//		} else {
//			CarbonUnitFeature cuf = (CarbonUnitFeature) obj;
//			if (cuf.averageLifetime != averageLifetime) {
//				return false;
//			}
//		}
//		return true;
//	}

//	@Override
//	public void numberChanged(NumberFieldEvent e) {
//		if (e.getSource().equals(getUI().averageLifetimeTextField)) {
//			double value = Double.parseDouble(getUI().averageLifetimeTextField.getText());
//			if (value != averageLifetime) {
//				((AbstractProcessorButton) getProcessor().getUI()).setChanged(true);
//				setAverageLifetime(value);
//			}
//		} 
//	}
	
	@Override
	public String toString() {
//		return getProcessor().getName() + "_" + averageLifetime;
		return getProcessor().getName();
	}

//	@SuppressWarnings("rawtypes")
//	@Override
//	public void itemStateChanged(ItemEvent evt) {
//		if (evt.getSource() instanceof JComboBox) {
//			Object obj = ((JComboBox) evt.getSource()).getSelectedItem();
//			if (obj instanceof LifetimeMode) {
//				LifetimeMode newLifetimeMode = (LifetimeMode) obj;
//				if (newLifetimeMode != lifetimeMode) {
//					((AbstractProcessorButton) getProcessor().getUI()).setChanged(true);
//					lifetimeMode = newLifetimeMode;
//					System.out.println("Lifetime mode switch to " + lifetimeMode);
//				}
//			}
//		}	
//	}

}
