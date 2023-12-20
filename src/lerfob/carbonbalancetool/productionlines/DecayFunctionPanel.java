package lerfob.carbonbalancetool.productionlines;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import lerfob.carbonbalancetool.productionlines.DecayFunction.DecayFunctionType;
import lerfob.carbonbalancetool.productionlines.DecayFunction.LifetimeMode;
import repicea.gui.CommonGuiUtility;
import repicea.gui.REpiceaPanel;
import repicea.gui.UIControlManager;
import repicea.gui.components.NumberFormatFieldFactory;
import repicea.gui.components.NumberFormatFieldFactory.JFormattedNumericField;
import repicea.gui.components.NumberFormatFieldFactory.Range;
import repicea.gui.components.NumberFormatFieldFactory.Type;
import repicea.util.REpiceaTranslator;
import repicea.util.REpiceaTranslator.TextableEnum;

/**
 * The User Interface of the DecayFunction class.
 * @author Mathieu Fortin - December 2023
 */
@SuppressWarnings("serial")
public class DecayFunctionPanel extends REpiceaPanel {

	public static enum MessageID implements TextableEnum {
		LifeTimeLabel("Lifetime", "Dur\u00E9e de vie")
		;
		
		MessageID(String englishText, String frenchText) {
			setText(englishText, frenchText);
		}
		
		@Override
		public void setText(String englishText, String frenchText) {
			REpiceaTranslator.setString(this, englishText, frenchText);
		}
		
		@Override
		public String toString() {
			return REpiceaTranslator.getString(this);
		}
	}

	protected JComboBox<LifetimeMode> lifetimeModeList;
	protected JComboBox<DecayFunctionType> decayFunctionList;
	
	protected JFormattedNumericField lifetimeTextField;
	
	private DecayFunction caller;
	
	protected DecayFunctionPanel(DecayFunction caller) {
		super();
		setCaller(caller);
		initializeFields();
		createUI();
	}

	protected DecayFunction getCaller() {return caller;}
	protected void setCaller(DecayFunction caller) {this.caller = caller;}
	
	protected void initializeFields() {
		lifetimeTextField = NumberFormatFieldFactory.createNumberFormatField(Type.Double, Range.Positive, false);
		lifetimeTextField.setText(((Double) getCaller().getLifetime()).toString());
		lifetimeTextField.setPreferredSize(new Dimension(100, lifetimeTextField.getFontMetrics(lifetimeTextField.getFont()).getHeight()));
		
		lifetimeModeList = new JComboBox<LifetimeMode>(LifetimeMode.values());
		lifetimeModeList.setSelectedItem(getCaller().lifetimeMode);
		
		decayFunctionList = new JComboBox<DecayFunctionType>(DecayFunctionType.values());
		decayFunctionList.setSelectedItem(getCaller().functionType);
	}
	
	protected void createUI() {
		setLayout(new BorderLayout());
//		setBorder(UIControlManager.getTitledBorder(MessageID.LifeTimeLabel.toString()));
		JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		subPanel.add(Box.createHorizontalStrut(5));
		subPanel.add(UIControlManager.getLabel(MessageID.LifeTimeLabel));
		subPanel.add(decayFunctionList);
		subPanel.add(lifetimeModeList);
		subPanel.add(Box.createHorizontalStrut(5));
		add(subPanel, BorderLayout.WEST);
		JPanel centerPanel = new JPanel(new BorderLayout());
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.add(lifetimeTextField, BorderLayout.CENTER);
		add(Box.createHorizontalStrut(5), BorderLayout.EAST);

		
		
////		innerPanel.add(Box.createHorizontalStrut(5));
//		innerPanel.add(decayFunctionList);
//		innerPanel.add(lifetimeModeList);
//		innerPanel.add(lifetimeTextField);
////		innerPanel.add(Box.createHorizontalStrut(5));
//		add(innerPanel, BorderLayout.CENTER);
	}

	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		decayFunctionList.setEnabled(b);
		lifetimeModeList.setEnabled(b);
		lifetimeTextField.setEnabled(b);
	}

	@Override
	public void listenTo() {
		lifetimeTextField.addNumberFieldListener(getCaller());
		lifetimeModeList.addItemListener(getCaller());
		decayFunctionList.addItemListener(getCaller());
	}

	@Override
	public void doNotListenToAnymore() {
		lifetimeTextField.removeNumberFieldListener(getCaller());
		lifetimeModeList.removeItemListener(getCaller());
		decayFunctionList.removeItemListener(getCaller());
	}

	/*
	 * Useless for this class (non-Javadoc)
	 * @see repicea.gui.Refreshable#refreshInterface()
	 */
	@Override
	public void refreshInterface() {}



}
