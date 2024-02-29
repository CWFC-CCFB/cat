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
import java.util.List;

import lerfob.carbonbalancetool.productionlines.DecayFunction.DecayFunctionType;
import lerfob.carbonbalancetool.productionlines.DecayFunction.LifetimeMode;
import repicea.gui.REpiceaUIObject;
import repicea.serial.SerializerChangeMonitor;
import repicea.simulation.processsystem.ProcessorListTable.MemberHandler;
import repicea.simulation.processsystem.ProcessorListTable.MemberInformation;
import repicea.simulation.processsystem.ResourceReleasable;

/**
 * The CarbonUnitFeature class defines some characteristics of carbon units contained in a wood piece. <p>
 * It contains the lifetime, use class, and others characteristics. 
 * @author Mathieu Fortin - 2012
 */
@SuppressWarnings("serial")
public class CarbonUnitFeature implements Serializable, REpiceaUIObject, MemberHandler, ResourceReleasable {

	static {
		SerializerChangeMonitor.registerClassNameChange("lerfob.carbonbalancetool.productionlines.CarbonUnitFeature$LifetimeMode",
				"lerfob.carbonbalancetool.productionlines.DecayFunction$LifetimeMode");
	}


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
	
	private final AbstractProductionLineProcessor processor;
	
	private transient CarbonUnitFeaturePanel userInterfacePanel;


	/**
	 * Constructor for GUI mode.
	 * @param processor an AbstractProductionLineProcessor instance, which hosts this CarbonUnitFeature instance.
	 */
	protected CarbonUnitFeature(AbstractProductionLineProcessor processor) {
		this.processor = processor;
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
	

	@Deprecated
	protected void setAverageLifetime(double d) {averageLifetime = d;}
	
	protected CarbonUnitFeaturePanel getUserInterfacePanel() {return userInterfacePanel;}
	protected void setUserInterfacePanel(CarbonUnitFeaturePanel panel) {this.userInterfacePanel = panel;}
	
	protected AbstractProductionLineProcessor getProcessor() {return processor;}
	
	

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
	
	
	@Override
	public String toString() {
		return getProcessor().getName();
	}

	@Override
	public List<MemberInformation> getInformationsOnMembers() {
		return getDecayFunction().getInformationsOnMembers();
	}

	@Override
	public void processChangeToMember(Enum<?> label, Object value) {
		getDecayFunction().processChangeToMember(label, value);
	}

	@Override
	public void releaseResources() {
		if (userInterfacePanel != null) {
			userInterfacePanel.doNotListenToAnymore();
			userInterfacePanel = null;
		}
		getDecayFunction().releaseResources();
	}


}
