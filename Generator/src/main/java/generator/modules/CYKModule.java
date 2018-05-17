
package generator.modules;

import generator.common.GenericModulePane;
import generator.common.tools.CommonUtils;
import generator.common.tools.CriteriaConstraintsChecker;
import generator.common.tools.SpinnersSupport;
import generator.common.tools.ThreadPair;
import generator.core.FormalLanguagesExampleGenerator;
import generator.modules.cfgexamplegenerator.ColorController;
import generator.modules.cyk.CYKGeneralGenerator;
import generator.modules.cyk.CYKGeneratorGrammarToWord;
import generator.modules.cyk.CYKHelper;
import generator.modules.cyk.Grammar;
import generator.modules.cyk.GrammarManager;
import generator.modules.cyk.GrammarManagerImpl;
import generator.common.Pair;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

public class CYKModule extends GenericModulePane
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ResourceBundle resourceBundle = ResourceBundle.getBundle("cykModule", new Locale("en", "US"));
	ResourceBundle nameBundle = ResourceBundle.getBundle("CriteriaChecker", new Locale("en"));
	private SpinnersSupport spinnersSup;
	private ColorController colorContr;
	private CriteriaConstraintsChecker criteriaChecker;
	private JLabel messagesLabel = new JLabel();
	private int activePanelNumber = 0;
	public static final String SPINNER_DISABLED_BY_RADIO = "SPINNER_DISABLED_BY_RADIO";

	@SuppressWarnings("unused")
	private boolean validationOK;
	private static Font TEXT_AREA_FONT = new Font("Monospaced", Font.PLAIN, 12);
	private static String LINE_SEPARATOR = System.getProperty("line.separator");
	private boolean grammarsValidated = false;
	private boolean wordsValidated = false;
	private FormalLanguagesExampleGenerator generatorCore;

	public CYKModule()
	{
		settingsPane.setName("settingsPane");
		settingsPane.setLayout(new GridBagLayout());

		setupSpinners();

		setupTransformationTypeLayout();
		setupGrammarToWordLayout();
		setupWordToGrammarLayout();
		setupGeneralCYKLayout();
		setupDerivabilityLayout();
		setGridBagPosition(2, derivabilityJPanel);

		disPopUpMenu.setName("disPopUpMenu"); // NOI18N
		disMenuItem.setName("disMenuItem"); // NOI18N
		disPopUpMenu.add(disMenuItem);

		addEndPageFiller();

		deactivateAllGridbagPanels();
		activateTransformationPanel(2);

		updateStrings();

		settingsPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	private void setupGeneralCYKLayout()
	{
		setupGrammarParametersLayout();
		setupWordParametersLayout();
		setupCYKTableParametersLayout();

		setGridBagPosition(3, grammarParametersJPanel);
		setGridBagPosition(4, wordParametersJPanel);
		setGridBagPosition(5, cykTableParametersJPanel);

	}

	private void setupWordParametersLayout()
	{
		wordParamLengthJLabel.setText("Length");

		setStandardJComponentSize(wordParamMinJLabel, 40, 14);
		wordParamMinJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		setStandardJComponentSize(wordParamMaxJLabel, 40, 14);
		wordParamMaxJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// Minimal values
		wordParamLengthMinJSpinner.setValue(4);
		setStandardJComponentSize(wordParamLengthMinJSpinner, 80, 25);

		// Maximal values
		wordParamLengthMaxJSpinner.setValue(7);
		setStandardJComponentSize(wordParamLengthMaxJSpinner, 80, 25);

		GroupLayout wordParametersLayout = new GroupLayout(wordParametersJPanel);
		wordParametersJPanel.setLayout(wordParametersLayout);
		wordParametersLayout.setHorizontalGroup(wordParametersLayout.createParallelGroup(Alignment.LEADING).addGroup(
			Alignment.TRAILING,
			wordParametersLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					wordParametersLayout.createParallelGroup(Alignment.LEADING).addComponent(wordParamLengthJLabel,
						GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					wordParametersLayout
						.createParallelGroup(Alignment.LEADING, false)
						.addGroup(
							wordParametersLayout
								.createSequentialGroup()
								.addComponent(wordParamMinJLabel, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(wordParamMaxJLabel, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE))
						.addGroup(
							wordParametersLayout
								.createSequentialGroup()
								.addComponent(wordParamLengthMinJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(wordParamLengthMaxJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE))).addGap(172).addContainerGap()));
		wordParametersLayout.setVerticalGroup(wordParametersLayout.createParallelGroup(Alignment.LEADING).addGroup(
			wordParametersLayout
				.createSequentialGroup()
				.addGroup(
					wordParametersLayout
						.createParallelGroup(Alignment.LEADING)
						.addComponent(wordParamMinJLabel)
						.addGroup(
							wordParametersLayout
								.createSequentialGroup()
								.addGap(1, 1, 1)
								.addComponent(wordParamMaxJLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
									GroupLayout.PREFERRED_SIZE)))
				.addGap(6, 6, 6)
				.addGroup(
					wordParametersLayout
						.createParallelGroup(Alignment.CENTER)
						.addComponent(wordParamLengthJLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)
						.addComponent(wordParamLengthMinJSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)
						.addComponent(wordParamLengthMaxJSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

	}

	private void setupGrammarParametersLayout()
	{
		grammarParamTerminalsJLabel.setText("Terminals");
		grammarParamNonterminalsJLabel.setText("Nonterminals");

		grammarParamTerminalsJComboBox.setModel(new DefaultComboBoxModel<>(
			new String[] { " a, b, c...", " 0, 1, 2..." }));

		setStandardJComponentSize(grammarParamMinJLabel, 40, 14);
		grammarParamMinJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		setStandardJComponentSize(grammarParamMaxJLabel, 40, 14);
		grammarParamMaxJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// Minimal values
		grammarParamNonterminalsMinJSpinner.setValue(3);
		grammarParamTerminalsMinJSpinner.setValue(2);
		setStandardJComponentSize(grammarParamNonterminalsMinJSpinner, 80, 25);
		setStandardJComponentSize(grammarParamTerminalsMinJSpinner, 80, 25);

		// Maximal values
		grammarParamNonterminalsMaxJSpinner.setValue(5);
		grammarParamTerminalsMaxJSpinner.setValue(4);
		setStandardJComponentSize(grammarParamNonterminalsMaxJSpinner, 80, 25);
		setStandardJComponentSize(grammarParamTerminalsMaxJSpinner, 80, 25);

		setStandardJComponentSize(grammarParamTerminalsJComboBox, 156, 25);

		GroupLayout grammarParametersLayout = new GroupLayout(grammarParametersJPanel);
		grammarParametersJPanel.setLayout(grammarParametersLayout);
		grammarParametersLayout.setHorizontalGroup(grammarParametersLayout.createParallelGroup(Alignment.LEADING)
			.addGroup(
				Alignment.TRAILING,
				grammarParametersLayout
					.createSequentialGroup()
					.addContainerGap()
					.addGroup(
						grammarParametersLayout
							.createParallelGroup(Alignment.TRAILING)
							.addComponent(grammarParamTerminalsJLabel, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
							.addComponent(grammarParamNonterminalsJLabel, GroupLayout.DEFAULT_SIZE, 100,
								Short.MAX_VALUE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(
						grammarParametersLayout
							.createParallelGroup(Alignment.LEADING)
							.addGroup(
								grammarParametersLayout
									.createSequentialGroup()
									.addComponent(grammarParamMinJLabel, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(grammarParamMaxJLabel, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE))
							.addGroup(
								grammarParametersLayout
									.createSequentialGroup()
									.addComponent(grammarParamTerminalsMinJSpinner, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(grammarParamTerminalsMaxJSpinner, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(grammarParamTerminalsJComboBox, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGroup(
								grammarParametersLayout
									.createSequentialGroup()
									.addComponent(grammarParamNonterminalsMinJSpinner, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(grammarParamNonterminalsMaxJSpinner, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE))).addContainerGap()));
		grammarParametersLayout.setVerticalGroup(grammarParametersLayout.createParallelGroup(Alignment.LEADING)
			.addGroup(
				grammarParametersLayout
					.createSequentialGroup()
					.addGroup(
						grammarParametersLayout
							.createParallelGroup(Alignment.LEADING)
							.addComponent(grammarParamMinJLabel)
							.addGroup(
								grammarParametersLayout
									.createSequentialGroup()
									.addGap(1, 1, 1)
									.addComponent(grammarParamMaxJLabel, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(6, 6, 6)
					.addGroup(
						grammarParametersLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(grammarParamTerminalsJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(grammarParamTerminalsMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(grammarParamTerminalsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(grammarParamTerminalsJComboBox, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						grammarParametersLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(grammarParamNonterminalsJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(grammarParamNonterminalsMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(grammarParamNonterminalsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

	}

	private void setupCYKTableParametersLayout()
	{
		cykTableParamDifferentSetsJLabel.setText("Alphabet");
		cykTableParamEmptySetsJLabel.setText("Length");

		setStandardJComponentSize(cykTableParamMinJLabel, 40, 14);
		cykTableParamMinJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		setStandardJComponentSize(cykTableParamMaxJLabel, 40, 14);
		cykTableParamMaxJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// Minimal values
		cykTableParamEmptySetsMinJSpinner.setValue(3);
		cykTableParamDifferentSetsMinJSpinner.setValue(3);
		setStandardJComponentSize(cykTableParamEmptySetsMinJSpinner, 148, 25);
		setStandardJComponentSize(cykTableParamDifferentSetsMinJSpinner, 148, 25);

		// Maximal values
		cykTableParamEmptySetsMaxJSpinner.setValue(9);
		cykTableParamDifferentSetsMaxJSpinner.setValue(7);
		setStandardJComponentSize(cykTableParamEmptySetsMaxJSpinner, 148, 25);
		setStandardJComponentSize(cykTableParamDifferentSetsMaxJSpinner, 148, 25);

		GroupLayout cykTableParametersLayout = new GroupLayout(cykTableParametersJPanel);
		cykTableParametersJPanel.setLayout(cykTableParametersLayout);
		cykTableParametersLayout
			.setHorizontalGroup(cykTableParametersLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(
					Alignment.TRAILING,
					cykTableParametersLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
							cykTableParametersLayout
								.createParallelGroup(Alignment.LEADING)
								.addComponent(cykTableParamDifferentSetsJLabel, GroupLayout.DEFAULT_SIZE, 100,
									Short.MAX_VALUE)
								.addComponent(cykTableParamEmptySetsJLabel, GroupLayout.DEFAULT_SIZE, 100,
									Short.MAX_VALUE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
							cykTableParametersLayout
								.createParallelGroup(Alignment.LEADING, false)
								.addGroup(
									cykTableParametersLayout
										.createSequentialGroup()
										.addComponent(cykTableParamMinJLabel, GroupLayout.PREFERRED_SIZE, 65,
											GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(cykTableParamMaxJLabel, GroupLayout.PREFERRED_SIZE, 65,
											GroupLayout.PREFERRED_SIZE))
								.addGroup(
									cykTableParametersLayout
										.createSequentialGroup()
										.addComponent(cykTableParamDifferentSetsMinJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(cykTableParamDifferentSetsMaxJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE))
								.addGroup(
									cykTableParametersLayout
										.createSequentialGroup()
										.addComponent(cykTableParamEmptySetsMinJSpinner, GroupLayout.PREFERRED_SIZE,
											65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(cykTableParamEmptySetsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
											65, GroupLayout.PREFERRED_SIZE))).addGap(172).addContainerGap()));
		cykTableParametersLayout.setVerticalGroup(cykTableParametersLayout.createParallelGroup(Alignment.LEADING)
			.addGroup(
				cykTableParametersLayout
					.createSequentialGroup()
					.addGroup(
						cykTableParametersLayout
							.createParallelGroup(Alignment.LEADING)
							.addComponent(cykTableParamMinJLabel)
							.addGroup(
								cykTableParametersLayout
									.createSequentialGroup()
									.addGap(1, 1, 1)
									.addComponent(cykTableParamMaxJLabel, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(6, 6, 6)
					.addGroup(
						cykTableParametersLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(cykTableParamDifferentSetsJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(cykTableParamDifferentSetsMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(cykTableParamDifferentSetsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						cykTableParametersLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(cykTableParamEmptySetsJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(cykTableParamEmptySetsMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(cykTableParamEmptySetsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

	}

	private class DerivabilityCheckListener implements ActionListener
	{

		public void actionPerformed(ActionEvent e)
		{
			if (!derivableCheck.isSelected() && !underivableCheck.isSelected())
			{
				derivablePercentLabel.setText("0 %");
				underivablePercentLabel.setText("0 %");
				derivabilityPercentageScrollbar.setEnabled(false);
				derivableScrollBarLabel.setEnabled(false);
				underivableScrollBarLabel.setEnabled(false);
			}

			if (derivableCheck.isSelected() && !underivableCheck.isSelected())
			{
				derivablePercentLabel.setText("100 %");
				underivablePercentLabel.setText("0 %");
				derivabilityPercentageScrollbar.setEnabled(false);
				derivableScrollBarLabel.setEnabled(false);
				underivableScrollBarLabel.setEnabled(false);
			}

			if (!derivableCheck.isSelected() && underivableCheck.isSelected())
			{
				derivablePercentLabel.setText("0 %");
				underivablePercentLabel.setText("100 %");
				derivabilityPercentageScrollbar.setEnabled(false);
				derivableScrollBarLabel.setEnabled(false);
				underivableScrollBarLabel.setEnabled(false);
			}

			if (derivableCheck.isSelected() && underivableCheck.isSelected())
			{
				int scrollBarValue = derivabilityPercentageScrollbar.getValue();
				derivablePercentLabel.setText(scrollBarValue + " %");
				underivablePercentLabel.setText((100 - scrollBarValue) + " %");
				derivabilityPercentageScrollbar.setEnabled(true);
				derivableScrollBarLabel.setEnabled(true);
				underivableScrollBarLabel.setEnabled(true);

			}

		}

	}

	private void setupDerivabilityLayout()
	{

		enabledDerivabalityLabel.setHorizontalAlignment(SwingConstants.CENTER);
		derivabilityRatioLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		derivableCheck.setHorizontalAlignment(SwingConstants.CENTER);
		underivableCheck.setHorizontalAlignment(SwingConstants.CENTER);
		underivablePercentLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		derivablePercentLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		derivableCheck.setSelected(true);
		derivablePercentLabel.setText("100 %");
		underivablePercentLabel.setText("0 %");
		derivabilityPercentageScrollbar.setEnabled(false);
		derivabilityPercentageScrollbar.setValue(50);
		derivableScrollBarLabel.setEnabled(false);
		underivableScrollBarLabel.setEnabled(false);
		// derivableCheck.setText("Derivable");
		// underivableCheck.setText("Underivable");
		setStandardJComponentSize(derivabilityPercentageScrollbar, 200, 20);

		ActionListener derivabilityListener = new DerivabilityCheckListener();
		derivableCheck.addActionListener(derivabilityListener);
		underivableCheck.addActionListener(derivabilityListener);

		derivabilityPercentageScrollbar.addAdjustmentListener(new AdjustmentListener()
		{

			public void adjustmentValueChanged(AdjustmentEvent e)
			{
				int scrollBarValue = derivabilityPercentageScrollbar.getValue();
				derivablePercentLabel.setText(scrollBarValue + " %");
				underivablePercentLabel.setText((100 - scrollBarValue) + " %");
			}
		});

		GroupLayout derivabilityLayout = new GroupLayout(derivabilityJPanel);
		derivabilityJPanel.setLayout(derivabilityLayout);
		derivabilityLayout.setHorizontalGroup(derivabilityLayout
			.createParallelGroup(Alignment.LEADING)
			.addGroup(
				Alignment.TRAILING,
				derivabilityLayout
					.createSequentialGroup()
					.addContainerGap()
					.addGroup(
						derivabilityLayout.createParallelGroup(Alignment.LEADING)
							.addComponent(derivableLabel, GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
							.addComponent(underivableLabel, GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(
						derivabilityLayout
							.createParallelGroup(Alignment.LEADING, false)
							.addGroup(
								derivabilityLayout
									.createSequentialGroup()
									.addComponent(enabledDerivabalityLabel, GroupLayout.PREFERRED_SIZE, 55,
										GroupLayout.PREFERRED_SIZE)
									.addGap(5)
									.addComponent(derivabilityRatioLabel, GroupLayout.PREFERRED_SIZE, 55,
										GroupLayout.PREFERRED_SIZE))
							.addGroup(
								derivabilityLayout
									.createSequentialGroup()
									.addComponent(derivableCheck, GroupLayout.PREFERRED_SIZE, 55,
										GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(derivablePercentLabel, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE))
							.addGroup(
								derivabilityLayout
									.createSequentialGroup()
									.addComponent(underivableCheck, GroupLayout.PREFERRED_SIZE, 55,
										GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(underivablePercentLabel, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE))).addGap(120).addContainerGap())
			.addGroup(
				derivabilityLayout.createSequentialGroup().addContainerGap().addComponent(underivableScrollBarLabel)
					.addPreferredGap(ComponentPlacement.RELATED).addComponent(derivabilityPercentageScrollbar)
					.addPreferredGap(ComponentPlacement.RELATED).addComponent(derivableScrollBarLabel)
					.addContainerGap()));
		derivabilityLayout.setVerticalGroup(derivabilityLayout.createParallelGroup(Alignment.LEADING).addGroup(
			derivabilityLayout
				.createSequentialGroup()
				.addGroup(
					derivabilityLayout
						.createSequentialGroup()
						.addGroup(
							derivabilityLayout
								.createParallelGroup(Alignment.LEADING)
								.addComponent(enabledDerivabalityLabel)
								.addGroup(
									derivabilityLayout
										.createSequentialGroup()
										.addGap(1, 1, 1)
										.addComponent(derivabilityRatioLabel, GroupLayout.PREFERRED_SIZE,
											GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addGap(6, 6, 6)
						.addGroup(
							derivabilityLayout
								.createParallelGroup(Alignment.CENTER)
								.addComponent(derivableLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
									GroupLayout.PREFERRED_SIZE)
								.addComponent(derivableCheck, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
									GroupLayout.PREFERRED_SIZE)
								.addComponent(derivablePercentLabel, GroupLayout.PREFERRED_SIZE,
									GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGap(6, 6, 6)
						.addGroup(
							derivabilityLayout
								.createParallelGroup(Alignment.CENTER)
								.addComponent(underivableLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
									GroupLayout.PREFERRED_SIZE)
								.addComponent(underivableCheck, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
									GroupLayout.PREFERRED_SIZE)
								.addComponent(underivablePercentLabel, GroupLayout.PREFERRED_SIZE,
									GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
				.addGap(7)
				.addGroup(
					derivabilityLayout.createParallelGroup(Alignment.CENTER).addComponent(underivableScrollBarLabel)
						.addComponent(derivabilityPercentageScrollbar).addComponent(derivableScrollBarLabel))
				.addContainerGap()));

	}

	private void checkSpinnerErrors()
	{
		for (Pair<String, String> p : criteriaChecker.getErrorMessages()){
			wrongParamsInterruptGenerating(p.getValue());
		}
	}

	private void wrongParamsInterruptGenerating(String warningMessage)
	{
		validationOK = false;
		generatorCore.generatingStopped();
		this.showWarningDialog(warningMessage);
	}

	public void generate(int numberOfExamplesToGenerate)
	{
		validationOK = true;
		generatorCore = FormalLanguagesExampleGenerator.getCoreInstance();
		checkSpinnerErrors();
		if (!validationOK)
		{
			return;
		}

		int numberOfExercises = numberOfExamplesToGenerate;
		switch (activePanelNumber)
		{
			case 0:
				// unused
				generateType0(numberOfExercises);
				break;
			case 1:
				// from words
				generateType1(numberOfExercises);
				break;
			case 2:
				// general
				generateType2(numberOfExercises);
				break;
			default:
		}
		validationOK = true;
	}

	private void generateType2(int numberOfExercises)
	{
		boolean derivable = derivableJCheckbox.isSelected();

		int minTerminals = 1;
		if (spinnersSup.isEnabled(grammarParamTerminalsMinJSpinner))
			minTerminals = (int) grammarParamTerminalsMinJSpinner.getValue();

		int maxTerminals = 5;
		if (spinnersSup.isEnabled(grammarParamTerminalsMaxJSpinner))
			maxTerminals = (int) grammarParamTerminalsMaxJSpinner.getValue();

		int minNonterminals = 1;
		if (spinnersSup.isEnabled(grammarParamNonterminalsMinJSpinner))
			minNonterminals = (int) grammarParamNonterminalsMinJSpinner.getValue();

		int maxNonterminals = 6;
		if (spinnersSup.isEnabled(grammarParamNonterminalsMaxJSpinner))
			maxNonterminals = (int) grammarParamNonterminalsMaxJSpinner.getValue();

		int minWordLength = 4;
		if (spinnersSup.isEnabled(wordParamLengthMinJSpinner))
			minWordLength = (int) wordParamLengthMinJSpinner.getValue();

		int maxWordLength = 10;
		if (spinnersSup.isEnabled(wordParamLengthMaxJSpinner))
			maxWordLength = (int) wordParamLengthMaxJSpinner.getValue();

		int minEmptySets = 0;
		if (spinnersSup.isEnabled(cykTableParamEmptySetsMinJSpinner))
			minEmptySets = (int) cykTableParamEmptySetsMinJSpinner.getValue();

		int maxEmptySets = 100;
		if (spinnersSup.isEnabled(cykTableParamEmptySetsMaxJSpinner))
			maxEmptySets = (int) cykTableParamEmptySetsMaxJSpinner.getValue();

		int minDifferentSets = 0;
		if (spinnersSup.isEnabled(cykTableParamDifferentSetsMinJSpinner))
			minDifferentSets = (int) cykTableParamDifferentSetsMinJSpinner.getValue();

		int maxDifferentSets = 100;
		if (spinnersSup.isEnabled(cykTableParamDifferentSetsMaxJSpinner))
			maxDifferentSets = (int) cykTableParamDifferentSetsMaxJSpinner.getValue();

		int selectedAlphabet = grammarParamTerminalsJComboBox.getSelectedIndex();

		List<ThreadPair> threads = CommonUtils.getThreadsForGivenExampleNumber(numberOfExercises);

		for (int i = 0; i < threads.size(); i++)
		{
			new CYKGeneralGenerator(threads.get(i).getNumberOfExamplesPerThread(), minTerminals, maxTerminals,
				minNonterminals, maxNonterminals, minWordLength, maxWordLength, minEmptySets, maxEmptySets,
				minDifferentSets, maxDifferentSets, selectedAlphabet, derivable).execute();
		}
	}

	private void generateType0(int numberOfExercises)
	{
		loadGrammars(numberOfExercises);
	}

	private void loadGrammars(int numberOfExercises)
	{
		int minTerminals = 1;
		if (spinnersSup.isEnabled(grammarParamTerminalsMinJSpinner))
			minTerminals = (int) grammarParamTerminalsMinJSpinner.getValue();

		int maxTerminals = 5;
		if (spinnersSup.isEnabled(grammarParamTerminalsMaxJSpinner))
			maxTerminals = (int) grammarParamTerminalsMaxJSpinner.getValue();

		int minNonterminals = 1;
		if (spinnersSup.isEnabled(wtgNumberOfNonTerminalsMinJSpinner))
			minNonterminals = (int) wtgNumberOfNonTerminalsMinJSpinner.getValue();

		int maxNonterminals = 6;
		if (spinnersSup.isEnabled(wtgNumberOfNonTerminalsMaxJSpinner))
			maxNonterminals = (int) wtgNumberOfNonTerminalsMaxJSpinner.getValue();

		int minWordLength = 4;
		if (spinnersSup.isEnabled(wordParamLengthMinJSpinner))
			minWordLength = (int) wordParamLengthMinJSpinner.getValue();

		int maxWordLength = 10;
		if (spinnersSup.isEnabled(wordParamLengthMaxJSpinner))
			maxWordLength = (int) wordParamLengthMaxJSpinner.getValue();

		int minEmptySets = 0;
		if (spinnersSup.isEnabled(cykTableParamEmptySetsMinJSpinner))
			minEmptySets = (int) cykTableParamEmptySetsMinJSpinner.getValue();

		int maxEmptySets = 100;
		if (spinnersSup.isEnabled(cykTableParamEmptySetsMaxJSpinner))
			maxEmptySets = (int) cykTableParamEmptySetsMaxJSpinner.getValue();

		int minDifferentSets = 0;
		if (spinnersSup.isEnabled(cykTableParamDifferentSetsMinJSpinner))
			minDifferentSets = (int) cykTableParamDifferentSetsMinJSpinner.getValue();

		int maxDifferentSets = 100;
		if (spinnersSup.isEnabled(cykTableParamDifferentSetsMaxJSpinner))
			maxDifferentSets = (int) cykTableParamDifferentSetsMaxJSpinner.getValue();

		int selectedAlphabet = grammarParamTerminalsJComboBox.getSelectedIndex();

		// getgrammar text
		String[] grammarLines = grammarArea.getText().split("\\r?\\n");
		String initialNonTerminal = "";
		String nonTerminal;
		// String rule;
		GrammarManager grammarManager = new GrammarManagerImpl();
		Grammar grammar = grammarManager.create("");
		for (String grammarLine : grammarLines)
		{
			if (grammarLine.startsWith("---"))
			{
				// reset and continue;
				initialNonTerminal = "";
				continue;
			}
			String[] lineRules = grammarLine.split("->");
			nonTerminal = lineRules[0].trim();
			if (initialNonTerminal.equals(""))
			{
				initialNonTerminal = nonTerminal;
				grammar = grammarManager.create(initialNonTerminal);
			}
			String[] rightHandRules = lineRules[1].split("\\|");
			for (String rightHandRule : rightHandRules)
			{
				grammarManager.addCFGRule(grammar, nonTerminal, rightHandRule.trim());
			}
		}
		// CYKAlgorithm.executeAlgorithm("ddsfas", grammar);

		List<String> words = new ArrayList<String>(CYKHelper.generateWordsFromGrammarInCNF(grammar,
			(int) gtwWordLengthMinJSpinner.getValue(), (int) gtwWordLengthMaxJSpinner.getValue()));

		new CYKGeneratorGrammarToWord(numberOfExercises, minTerminals, maxTerminals, minNonterminals, maxNonterminals,
			minWordLength, maxWordLength, minEmptySets, maxEmptySets, minDifferentSets, maxDifferentSets,
			selectedAlphabet, words, grammar).execute();

	}

	/**
	 * words to grammar
	 * 
	 * @param numberOfExercises
	 */
	private void generateType1(int numberOfExercises)
	{
		boolean derivable = derivableJCheckbox.isSelected();

		int minTerminals = 1;
		if (spinnersSup.isEnabled(grammarParamTerminalsMinJSpinner))
			minTerminals = (int) grammarParamTerminalsMinJSpinner.getValue();

		int maxTerminals = 5;
		if (spinnersSup.isEnabled(grammarParamTerminalsMaxJSpinner))
			maxTerminals = (int) grammarParamTerminalsMaxJSpinner.getValue();

		int minNonterminals = 1;
		if (spinnersSup.isEnabled(wtgNumberOfNonTerminalsMinJSpinner))
			minNonterminals = (int) wtgNumberOfNonTerminalsMinJSpinner.getValue();

		int maxNonterminals = 6;
		if (spinnersSup.isEnabled(wtgNumberOfNonTerminalsMaxJSpinner))
			maxNonterminals = (int) wtgNumberOfNonTerminalsMaxJSpinner.getValue();

		int minWordLength = 4;
		if (spinnersSup.isEnabled(wordParamLengthMinJSpinner))
			minWordLength = (int) wordParamLengthMinJSpinner.getValue();

		int maxWordLength = 10;
		if (spinnersSup.isEnabled(wordParamLengthMaxJSpinner))
			maxWordLength = (int) wordParamLengthMaxJSpinner.getValue();

		int minEmptySets = 0;
		if (spinnersSup.isEnabled(cykTableParamEmptySetsMinJSpinner))
			minEmptySets = (int) cykTableParamEmptySetsMinJSpinner.getValue();

		int maxEmptySets = 100;
		if (spinnersSup.isEnabled(cykTableParamEmptySetsMaxJSpinner))
			maxEmptySets = (int) cykTableParamEmptySetsMaxJSpinner.getValue();

		int minDifferentSets = 0;
		if (spinnersSup.isEnabled(cykTableParamDifferentSetsMinJSpinner))
			minDifferentSets = (int) cykTableParamDifferentSetsMinJSpinner.getValue();

		int maxDifferentSets = 100;
		if (spinnersSup.isEnabled(cykTableParamDifferentSetsMaxJSpinner))
			maxDifferentSets = (int) cykTableParamDifferentSetsMaxJSpinner.getValue();

		int selectedAlphabet = grammarParamTerminalsJComboBox.getSelectedIndex();

		String[] wordLines = wordArea.getText().split("\\r?\\n");
		List<String> words = Arrays.asList(wordLines);

		List<ThreadPair> threads = CommonUtils.getThreadsForGivenExampleNumber(numberOfExercises);

		for (int i = 0; i < threads.size(); i++)
		{

			new CYKGeneralGenerator(threads.get(i).getNumberOfExamplesPerThread(), minTerminals, maxTerminals,
				minNonterminals, maxNonterminals, minWordLength, maxWordLength, minEmptySets, maxEmptySets,
				minDifferentSets, maxDifferentSets, selectedAlphabet, words, derivable).execute();
		}
	}

	private void addEndPageFiller()
	{
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.anchor = GridBagConstraints.SOUTH;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		settingsPane.add(new JPanel(), gridBagConstraints);
	}

	private void activateTransformationPanel(int transformationOrdinalNumber)
	{

		derivabilityJPanel.setVisible(false);
		switch (transformationOrdinalNumber)
		{
			case 0:
				activePanelNumber = 0;
				grammarToWordOutputParametersJPanel.setVisible(true);
				grammarToWordInputGrammarJPanel.setVisible(true);
				cykTableParametersJPanel.setVisible(true);
				break;
			case 1:
				activePanelNumber = 1;
				wordToGrammarInputWordJPanel.setVisible(true);
				wordToGrammarOutputParametersJPanel.setVisible(true);
				cykTableParametersJPanel.setVisible(true);
				break;
			case 2:
				activePanelNumber = 2;
				wordParametersJPanel.setVisible(true);
				grammarParametersJPanel.setVisible(true);
				cykTableParametersJPanel.setVisible(true);
				break;

		}
	}

	private void deactivateAllGridbagPanels()
	{
		grammarToWordOutputParametersJPanel.setVisible(false);
		grammarToWordInputGrammarJPanel.setVisible(false);

		wordToGrammarInputWordJPanel.setVisible(false);
		wordToGrammarOutputParametersJPanel.setVisible(false);

		wordParametersJPanel.setVisible(false);
		grammarParametersJPanel.setVisible(false);
		cykTableParametersJPanel.setVisible(false);
	}

	private void setupSpinners()
	{
		colorContr = new ColorController(type0Automaton1AlphabetMaxJSpinner);
		spinnersSup = new SpinnersSupport(disPopUpMenu, disMenuItem, colorContr);
		criteriaChecker = new CriteriaConstraintsChecker(messagesLabel, new Action[] {},

		spinnersSup, colorContr);
		for (JSpinner[] s : errorSpinnerPairs)
		{
			criteriaChecker.addErrConstraint(s[0], s[1]);
		}
		for (JSpinner spinner : allSpinners)
		{
			spinnersSup.registerSpinner(spinner);
			NumberEditor e = (NumberEditor) spinner.getEditor();
			e.getTextField().setHorizontalAlignment(JTextField.CENTER);
			spinner.setName("tmp");
			spinner.putClientProperty(SPINNER_DISABLED_BY_RADIO, !spinner.isEnabled());
		}

		gtwWordLengthMinJSpinner.setName("gtwWordLengthMinJSpinner.name");
		gtwWordLengthMaxJSpinner.setName("gtwWordLengthMaxJSpinner.name");
		wtgNumberOfNonTerminalsMinJSpinner.setName("wtgNumberOfNonTerminalsMinJSpinner.name");
		wtgNumberOfNonTerminalsMaxJSpinner.setName("wtgNumberOfNonTerminalsMaxJSpinner.name");
		wtgNumberOfRulesMinJSpinner.setName("wtgNumberOfRulesMinJSpinner.name");
		wtgNumberOfRulesMaxJSpinner.setName("wtgNumberOfRulesMaxJSpinner.name");
		grammarParamTerminalsMinJSpinner.setName("grammarParamTerminalsMinJSpinner.name");
		grammarParamTerminalsMaxJSpinner.setName("grammarParamTerminalsMaxJSpinner.name");
		grammarParamNonterminalsMinJSpinner.setName("grammarParamNonterminalsMinJSpinner.name");
		grammarParamNonterminalsMaxJSpinner.setName("grammarParamNonterminalsMaxJSpinner.name");
		wordParamLengthMaxJSpinner.setName("wordParamLengthMaxJSpinner.name");
		wordParamLengthMinJSpinner.setName("wordParamLengthMinJSpinner.name");
		cykTableParamDifferentSetsMinJSpinner.setName("cykTableParamDifferentSetsMinJSpinner.name");
		cykTableParamDifferentSetsMaxJSpinner.setName("cykTableParamDifferentSetsMaxJSpinner.name");
		cykTableParamEmptySetsMinJSpinner.setName("cykTableParamEmptySetsMinJSpinner.name");
		cykTableParamEmptySetsMaxJSpinner.setName("cykTableParamEmptySetsMaxJSpinner.name");
	}

	private void setGridBagPosition(int ordinal, JPanel pane)
	{
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = ordinal;
		gridBagConstraints.weighty = 0;
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(0, 0, 0, 0);
		settingsPane.add(pane, gridBagConstraints);
	}

	private void setStandardJComponentSize(JComponent component, int width, int height)
	{
		component.setMaximumSize(new Dimension(width, height));
		component.setMinimumSize(new Dimension(width, height));
		component.setPreferredSize(new Dimension(width, height));
	}

	private void setupTransformationTypeLayout()
	{

		operationButtonGroup.add(operationGrammarToWordJButton);
		operationButtonGroup.add(operationWordToGrammarJButton);
		operationButtonGroup.add(operationGeneralCYKJButton);
		// type2OperationButtonGroup.setSelected((ButtonModel) type2OperationGrammarToNFAJButton,true);
		operationGeneralCYKJButton.setSelected(true);

		operationJPanel.setLayout(new BoxLayout(operationJPanel, BoxLayout.Y_AXIS));
		operationGrammarToWordJButton.setMargin(new Insets(0, 10, 5, 0));
		operationWordToGrammarJButton.setMargin(new Insets(0, 10, 5, 0));
		operationGeneralCYKJButton.setMargin(new Insets(5, 10, 5, 0));
		operationJPanel.add(operationGeneralCYKJButton);
		// Temporarily removed (not necessary, probably would be unused
		// operationJPanel.add(operationGrammarToWordJButton);
		operationJPanel.add(operationWordToGrammarJButton);

		derivableJCheckbox.setSelected(true);
		derivableJCheckbox.setMargin(new Insets(0, 10, 5, 0));
		operationJPanel.add(derivableJCheckbox);

		operationWordToGrammarJButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				operationJRadioButtonActionPerformed(e);
			}
		});

		operationGrammarToWordJButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				operationJRadioButtonActionPerformed(e);
			}
		});

		operationGeneralCYKJButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				operationJRadioButtonActionPerformed(e);
			}
		});

		setGridBagPosition(1, operationJPanel);
	}

	private void setupGrammarToWordLayout()
	{
		setupGrammarToWordInputGrammarLayout();
		setupGrammarToWordOutputParametersLayout();

		setGridBagPosition(3, grammarToWordInputGrammarJPanel);
		setGridBagPosition(4, grammarToWordOutputParametersJPanel);
	}

	private void setupWordToGrammarLayout()
	{
		setupWordToGrammarInputWordLayout();
		setupWordToGrammarOutputParametersLayout();

		setGridBagPosition(3, wordToGrammarInputWordJPanel);
		setGridBagPosition(4, wordToGrammarOutputParametersJPanel);
	}

	@SuppressWarnings({})
	private void setupGrammarToWordInputGrammarLayout()
	{

		grammarArea.setFont(TEXT_AREA_FONT);
		setStandardJComponentSize(grammarAreaPane, 300, 150);
		grammarAreaPane.setViewportView(grammarArea);

		// setStandardJComponentSize(type0Automaton1StatesJComboBox, 148, 25);

		loadGrammarButtonWrapperPane.setLayout(new GridLayout());
		setStandardJComponentSize(loadGrammarButtonWrapperPane, 300, 25);
		loadGrammarButtonWrapperPane.add(loadGrammarButton);

		loadGrammarButton.addActionListener(new ActionListener()
		{

			@SuppressWarnings("resource")
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileFilter()
				{

					@Override
					public String getDescription()
					{

						return "*.txt";
					}

					@Override
					public boolean accept(File f)
					{
						if (!f.getName().endsWith(".txt") && !f.isDirectory())
							return false;
						return true;
					}
				});
				int returnVal = fileChooser.showOpenDialog(FormalLanguagesExampleGenerator.getCoreInstance());
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					File grammarFile = fileChooser.getSelectedFile();
					try
					{
						FileReader fr = new FileReader(grammarFile);

						BufferedReader br = new BufferedReader(fr);
						StringBuilder grammarStringBuilder = new StringBuilder();
						while (br.ready())
						{
							grammarStringBuilder.append(br.readLine() + LINE_SEPARATOR);
						}

						grammarArea.setText(grammarStringBuilder.toString());
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}

			}
		});

		DocumentListener grammarInputValidatorListener = new DocumentListener()
		{

			public void insertUpdate(DocumentEvent e)
			{
				f();
			}
			public void removeUpdate(DocumentEvent e)
			{
				f();
			}
			void f()
			{
				boolean validated = CommonUtils.GRAMMAR_INPUT_FORM_PATTERN.matcher(grammarArea.getText()).matches();
				if (validated != grammarsValidated)
				{
					grammarsValidated = validated;
					grammarArea.setBackground(validated ? Color.WHITE : Color.YELLOW);
				}
			}
			public void changedUpdate(DocumentEvent e)
			{

			}
		};

		grammarArea.getDocument().addDocumentListener(grammarInputValidatorListener);

		GroupLayout grammarToWordInputGrammarLayout = new GroupLayout(grammarToWordInputGrammarJPanel);
		grammarToWordInputGrammarJPanel.setLayout(grammarToWordInputGrammarLayout);
		grammarToWordInputGrammarLayout.setHorizontalGroup(grammarToWordInputGrammarLayout.createParallelGroup(
			Alignment.LEADING).addGroup(
			Alignment.TRAILING,
			grammarToWordInputGrammarLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					grammarToWordInputGrammarLayout.createParallelGroup(Alignment.TRAILING).addComponent(
						grammarToWordGrammarJLabel, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					grammarToWordInputGrammarLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
							grammarToWordInputGrammarLayout.createSequentialGroup().addComponent(grammarAreaPane,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(
							grammarToWordInputGrammarLayout.createSequentialGroup().addComponent(
								loadGrammarButtonWrapperPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))).addContainerGap()));
		grammarToWordInputGrammarLayout.setVerticalGroup(grammarToWordInputGrammarLayout.createParallelGroup(
			Alignment.LEADING).addGroup(
			grammarToWordInputGrammarLayout
				.createSequentialGroup()
				.addGroup(
					grammarToWordInputGrammarLayout
						.createParallelGroup(Alignment.CENTER)
						.addComponent(grammarToWordGrammarJLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)
						.addComponent(grammarAreaPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					grammarToWordInputGrammarLayout.createParallelGroup(Alignment.BASELINE).addComponent(
						loadGrammarButtonWrapperPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE)).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
	}

	@SuppressWarnings({})
	private void setupGrammarToWordOutputParametersLayout()
	{

		setStandardJComponentSize(grammarToWordMinJLabel, 40, 14);
		grammarToWordMinJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		setStandardJComponentSize(grammarToWordMaxJLabel, 40, 14);
		grammarToWordMaxJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// Alphabet min and max setting

		gtwWordLengthMinJSpinner.setValue(1);
		setStandardJComponentSize(gtwWordLengthMinJSpinner, 80, 25);

		gtwWordLengthMaxJSpinner.setValue(5);
		setStandardJComponentSize(gtwWordLengthMaxJSpinner, 80, 25);

		// type0Automaton2JPanel.add(type0Automaton2TotalTransitionFunctionJCheckBox);

		GroupLayout grammarToWordOutputParametersLayout = new GroupLayout(grammarToWordOutputParametersJPanel);
		grammarToWordOutputParametersJPanel.setLayout(grammarToWordOutputParametersLayout);
		grammarToWordOutputParametersLayout.setHorizontalGroup(grammarToWordOutputParametersLayout.createParallelGroup(
			Alignment.LEADING).addGroup(
			Alignment.TRAILING,
			grammarToWordOutputParametersLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					grammarToWordOutputParametersLayout.createParallelGroup(Alignment.LEADING).addComponent(
						wordLengthLabel, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					grammarToWordOutputParametersLayout
						.createParallelGroup(Alignment.LEADING, false)
						.addGroup(
							grammarToWordOutputParametersLayout
								.createSequentialGroup()
								.addComponent(grammarToWordMinJLabel, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(grammarToWordMaxJLabel, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE))
						.addGroup(
							grammarToWordOutputParametersLayout
								.createSequentialGroup()
								.addComponent(gtwWordLengthMinJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(gtwWordLengthMaxJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE))).addGap(172).addContainerGap()));
		grammarToWordOutputParametersLayout.setVerticalGroup(grammarToWordOutputParametersLayout.createParallelGroup(
			Alignment.LEADING).addGroup(
			grammarToWordOutputParametersLayout
				.createSequentialGroup()
				.addGroup(
					grammarToWordOutputParametersLayout
						.createParallelGroup(Alignment.LEADING)
						.addComponent(grammarToWordMinJLabel)
						.addGroup(
							grammarToWordOutputParametersLayout
								.createSequentialGroup()
								.addGap(1, 1, 1)
								.addComponent(grammarToWordMaxJLabel, GroupLayout.PREFERRED_SIZE,
									GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
				.addGap(6, 6, 6)
				.addGroup(
					grammarToWordOutputParametersLayout
						.createParallelGroup(Alignment.CENTER)
						.addComponent(wordLengthLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)
						.addComponent(gtwWordLengthMinJSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)
						.addComponent(gtwWordLengthMaxJSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

	}

	private void setupWordToGrammarInputWordLayout()
	{

		wordArea.setFont(TEXT_AREA_FONT);
		setStandardJComponentSize(wordAreaPane, 300, 150);
		wordAreaPane.setViewportView(wordArea);

		loadWordButtonWrapperPane.setLayout(new GridLayout());
		setStandardJComponentSize(loadWordButtonWrapperPane, 300, 25);
		loadWordButtonWrapperPane.add(loadWordButton);

		loadWordButton.addActionListener(new ActionListener()
		{

			@SuppressWarnings("resource")
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileFilter()
				{

					@Override
					public String getDescription()
					{

						return "*.txt";
					}

					@Override
					public boolean accept(File f)
					{
						if (!f.getName().endsWith(".txt") && !f.isDirectory())
							return false;
						return true;
					}
				});
				int returnVal = fileChooser.showOpenDialog(FormalLanguagesExampleGenerator.getCoreInstance());
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					File wordFile = fileChooser.getSelectedFile();
					try
					{
						FileReader fr = new FileReader(wordFile);
						BufferedReader br = new BufferedReader(fr);
						StringBuilder wordStringBuilder = new StringBuilder();
						while (br.ready())
						{
							wordStringBuilder.append(br.readLine() + LINE_SEPARATOR);
						}
						wordArea.setText(wordStringBuilder.toString());
					}
					catch (FileNotFoundException e1)
					{
						e1.printStackTrace();
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}

			}
		});

		DocumentListener wordInputValidatorListener = new DocumentListener()
		{
			public void changedUpdate(DocumentEvent e)
			{
			}
			public void insertUpdate(DocumentEvent e)
			{
				f();
			}
			public void removeUpdate(DocumentEvent e)
			{
				f();
			}
			void f()
			{
				// TODO zadefinovat podminky a pripadne updatovat statusmessage label
				boolean validated = CommonUtils.WORD_INPUT_FORM_PATTERN.matcher(wordArea.getText()).matches();
				if (validated != wordsValidated)
				{
					wordsValidated = validated;
					wordArea.setBackground(validated ? Color.WHITE : Color.YELLOW);
				}
			}
		};

		wordArea.getDocument().addDocumentListener(wordInputValidatorListener);

		GroupLayout wordToGrammarInputWordLayout = new GroupLayout(wordToGrammarInputWordJPanel);
		wordToGrammarInputWordJPanel.setLayout(wordToGrammarInputWordLayout);
		wordToGrammarInputWordLayout.setHorizontalGroup(wordToGrammarInputWordLayout.createParallelGroup(
			Alignment.LEADING).addGroup(
			Alignment.TRAILING,
			wordToGrammarInputWordLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					wordToGrammarInputWordLayout.createParallelGroup(Alignment.TRAILING).addComponent(
						wordToGrammarWordJLabel, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					wordToGrammarInputWordLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
							wordToGrammarInputWordLayout.createSequentialGroup().addComponent(wordAreaPane,
								GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(
							wordToGrammarInputWordLayout.createSequentialGroup().addComponent(
								loadWordButtonWrapperPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))).addContainerGap()));
		wordToGrammarInputWordLayout.setVerticalGroup(wordToGrammarInputWordLayout.createParallelGroup(
			Alignment.LEADING).addGroup(
			wordToGrammarInputWordLayout
				.createSequentialGroup()
				.addGroup(
					wordToGrammarInputWordLayout
						.createParallelGroup(Alignment.CENTER)
						.addComponent(wordToGrammarWordJLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)
						.addComponent(wordAreaPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					wordToGrammarInputWordLayout.createParallelGroup(Alignment.BASELINE).addComponent(
						loadWordButtonWrapperPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE)).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

	}

	private void setupWordToGrammarOutputParametersLayout()
	{

		setStandardJComponentSize(wtgMinJLabel, 40, 14);
		wtgMinJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		setStandardJComponentSize(wtgMaxJLabel, 40, 14);
		wtgMaxJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// Minimal values
		wtgNumberOfNonTerminalsMinJSpinner.setValue(4);
		// wtgNumberOfRulesMinJSpinner.setValue(6);
		setStandardJComponentSize(wtgNumberOfNonTerminalsMinJSpinner, 80, 25);
		// setStandardJComponentSize(wtgNumberOfRulesMinJSpinner, 148, 25);

		// Maximal values
		wtgNumberOfNonTerminalsMaxJSpinner.setValue(6);
		// wtgNumberOfRulesMaxJSpinner.setValue(15);
		setStandardJComponentSize(wtgNumberOfNonTerminalsMaxJSpinner, 80, 25);
		// setStandardJComponentSize(wtgNumberOfRulesMaxJSpinner, 148, 25);

		GroupLayout wordToGrammarOutputParametersLayout = new GroupLayout(wordToGrammarOutputParametersJPanel);
		wordToGrammarOutputParametersJPanel.setLayout(wordToGrammarOutputParametersLayout);
		wordToGrammarOutputParametersLayout.setHorizontalGroup(wordToGrammarOutputParametersLayout.createParallelGroup(
			Alignment.LEADING)
			.addGroup(
				Alignment.TRAILING,
				wordToGrammarOutputParametersLayout
					.createSequentialGroup()
					.addContainerGap()
					.addGroup(
						wordToGrammarOutputParametersLayout.createParallelGroup(Alignment.LEADING).addComponent(
							wtgNonTerminalsJLabel, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
					// .addComponent(wtgRulesJLabel, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
					)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(
						wordToGrammarOutputParametersLayout
							.createParallelGroup(Alignment.LEADING, false)
							.addGroup(
								wordToGrammarOutputParametersLayout
									.createSequentialGroup()
									.addComponent(wtgMinJLabel, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(wtgMaxJLabel, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE))
							.addGroup(
								wordToGrammarOutputParametersLayout
									.createSequentialGroup()
									.addComponent(wtgNumberOfNonTerminalsMinJSpinner, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(wtgNumberOfNonTerminalsMaxJSpinner, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE))
					// .addGroup(
					// wordToGrammarOutputParametersLayout
					// .createSequentialGroup()
					// .addComponent(wtgNumberOfRulesMinJSpinner, GroupLayout.PREFERRED_SIZE, 65,
					// GroupLayout.PREFERRED_SIZE)
					// .addGap(6, 6, 6)
					// .addComponent(wtgNumberOfRulesMaxJSpinner, GroupLayout.PREFERRED_SIZE, 65,
					// GroupLayout.PREFERRED_SIZE))
					).addGap(172).addContainerGap()));
		wordToGrammarOutputParametersLayout.setVerticalGroup(wordToGrammarOutputParametersLayout.createParallelGroup(
			Alignment.LEADING).addGroup(
			wordToGrammarOutputParametersLayout
				.createSequentialGroup()
				.addGroup(
					wordToGrammarOutputParametersLayout
						.createParallelGroup(Alignment.LEADING)
						.addComponent(wtgMinJLabel)
						.addGroup(
							wordToGrammarOutputParametersLayout
								.createSequentialGroup()
								.addGap(1, 1, 1)
								.addComponent(wtgMaxJLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
									GroupLayout.PREFERRED_SIZE)))
				.addGap(6, 6, 6)
				.addGroup(
					wordToGrammarOutputParametersLayout
						.createParallelGroup(Alignment.CENTER)
						.addComponent(wtgNonTerminalsJLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)
						.addComponent(wtgNumberOfNonTerminalsMinJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(wtgNumberOfNonTerminalsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				// .addGap(6, 6, 6)
				// .addGroup(
				// wordToGrammarOutputParametersLayout
				// .createParallelGroup(Alignment.CENTER)
				// .addComponent(wtgRulesJLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
				// GroupLayout.PREFERRED_SIZE)
				// .addComponent(wtgNumberOfRulesMinJSpinner, GroupLayout.PREFERRED_SIZE,
				// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				// .addComponent(wtgNumberOfRulesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
				// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

	}

	/**
	 * the item in combo box operation of type 2 was selected
	 * 
	 * @param evt
	 *            the item in combo box operation of type 2 was selected
	 */
	private void operationJRadioButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		if (evt.getSource() != null)
		{
			this.setOperationPanel();
		}
	}

	/**
	 * sets the panel for exercises of type 2
	 */
	private void setOperationPanel()
	{
		if (operationWordToGrammarJButton.isSelected())
		{
			activePanelNumber = 1;
			this.wordToGrammarInputWordJPanel.setVisible(true);
			this.grammarToWordInputGrammarJPanel.setVisible(false);
			this.grammarToWordOutputParametersJPanel.setVisible(false);
			this.wordToGrammarOutputParametersJPanel.setVisible(true);
			this.wordParametersJPanel.setVisible(false);
			this.grammarParametersJPanel.setVisible(false);
			this.cykTableParametersJPanel.setVisible(true);
		}
		else if (operationGrammarToWordJButton.isSelected())
		{
			activePanelNumber = 0;
			this.wordToGrammarInputWordJPanel.setVisible(false);
			this.grammarToWordInputGrammarJPanel.setVisible(true);
			this.grammarToWordOutputParametersJPanel.setVisible(true);
			this.wordToGrammarOutputParametersJPanel.setVisible(false);
			this.wordParametersJPanel.setVisible(false);
			this.grammarParametersJPanel.setVisible(false);
			this.cykTableParametersJPanel.setVisible(true);
		}
		else
		{
			activePanelNumber = 2;
			this.wordToGrammarInputWordJPanel.setVisible(false);
			this.grammarToWordInputGrammarJPanel.setVisible(false);
			this.grammarToWordOutputParametersJPanel.setVisible(false);
			this.wordToGrammarOutputParametersJPanel.setVisible(false);
			this.wordParametersJPanel.setVisible(true);
			this.grammarParametersJPanel.setVisible(true);
			this.cykTableParametersJPanel.setVisible(true);
		}
	}

	private JRadioButton operationGeneralCYKJButton = new JRadioButton();
	private JPanel wordParametersJPanel = new JPanel();
	private JPanel grammarParametersJPanel = new JPanel();
	private JPanel cykTableParametersJPanel = new JPanel();
	private JLabel wordToGrammarWordJLabel = new JLabel();
	private JTextArea wordArea = new JTextArea();
	private JScrollPane wordAreaPane = new JScrollPane();
	private JButton loadWordButton = new JButton();
	private JPanel loadWordButtonWrapperPane = new JPanel();
	private JLabel wordLengthLabel = new JLabel();
	private JLabel derivableScrollBarLabel = new JLabel();
	private JLabel underivableScrollBarLabel = new JLabel();
	private JScrollBar derivabilityPercentageScrollbar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 10, 0, 110);
	private JCheckBox derivableCheck = new JCheckBox();
	private JCheckBox underivableCheck = new JCheckBox();
	private JLabel derivableLabel = new JLabel();
	private JLabel underivableLabel = new JLabel();
	private JLabel derivabilityRatioLabel = new JLabel();
	private JLabel derivablePercentLabel = new JLabel();
	private JLabel underivablePercentLabel = new JLabel();
	private JLabel enabledDerivabalityLabel = new JLabel();
	private JPanel derivabilityJPanel = new JPanel();
	private JPanel loadGrammarButtonWrapperPane = new JPanel();
	private JButton loadGrammarButton = new JButton();
	private JScrollPane grammarAreaPane = new JScrollPane();
	private JTextArea grammarArea = new JTextArea();
	private JPanel transformationTypePane = new JPanel();
	private JLabel grammarToWordGrammarJLabel = new JLabel();
	private JSpinner type0Automaton1AlphabetMaxJSpinner = new JSpinner();
	private JLabel grammarToWordMinJLabel = new JLabel();
	private JSpinner gtwWordLengthMinJSpinner = new JSpinner();
	private JLabel grammarToWordMaxJLabel = new JLabel();
	private JSpinner gtwWordLengthMaxJSpinner = new JSpinner();
	private JPanel grammarToWordOutputParametersJPanel = new JPanel();
	private JPanel grammarToWordInputGrammarJPanel = new JPanel();
	private JPanel wordToGrammarInputWordJPanel = new JPanel();
	private JPanel wordToGrammarOutputParametersJPanel = new JPanel();
	private JLabel wtgMinJLabel = new JLabel();
	private JLabel wtgMaxJLabel = new JLabel();
	private JLabel wtgNonTerminalsJLabel = new JLabel();
	private JSpinner wtgNumberOfNonTerminalsMinJSpinner = new JSpinner();
	private JSpinner wtgNumberOfNonTerminalsMaxJSpinner = new JSpinner();
	private JLabel wtgRulesJLabel = new JLabel();
	private JSpinner wtgNumberOfRulesMinJSpinner = new JSpinner();
	private JSpinner wtgNumberOfRulesMaxJSpinner = new JSpinner();
	private JPanel operationJPanel = new JPanel();
	private ButtonGroup operationButtonGroup = new ButtonGroup();
	private JRadioButton operationGrammarToWordJButton = new JRadioButton();
	private JRadioButton operationWordToGrammarJButton = new JRadioButton();

	private JLabel wordParamMinJLabel = new JLabel();
	private JLabel wordParamMaxJLabel = new JLabel();
	private JSpinner wordParamLengthMinJSpinner = new JSpinner();
	private JSpinner wordParamLengthMaxJSpinner = new JSpinner();
	private JLabel wordParamLengthJLabel = new JLabel();
	private JLabel grammarParamMinJLabel = new JLabel();
	private JLabel grammarParamMaxJLabel = new JLabel();
	private JSpinner grammarParamNonterminalsMinJSpinner = new JSpinner();
	private JSpinner grammarParamNonterminalsMaxJSpinner = new JSpinner();
	private JSpinner grammarParamTerminalsMinJSpinner = new JSpinner();
	private JSpinner grammarParamTerminalsMaxJSpinner = new JSpinner();
	private JComboBox<String> grammarParamTerminalsJComboBox = new JComboBox<String>();
	private JLabel grammarParamNonterminalsJLabel = new JLabel();
	private JLabel grammarParamTerminalsJLabel = new JLabel();
	private JLabel cykTableParamMinJLabel = new JLabel();
	private JLabel cykTableParamMaxJLabel = new JLabel();
	private JSpinner cykTableParamEmptySetsMinJSpinner = new JSpinner();
	private JSpinner cykTableParamEmptySetsMaxJSpinner = new JSpinner();
	private JSpinner cykTableParamDifferentSetsMinJSpinner = new JSpinner();
	private JSpinner cykTableParamDifferentSetsMaxJSpinner = new JSpinner();
	private JLabel cykTableParamEmptySetsJLabel = new JLabel();
	private JLabel cykTableParamDifferentSetsJLabel = new JLabel();
	private JCheckBox derivableJCheckbox = new JCheckBox();

	private GridBagConstraints gridBagConstraints;
	private JPanel settingsPane = new JPanel();

	private JPopupMenu disPopUpMenu = new JPopupMenu();
	private JMenuItem disMenuItem = new JMenuItem();

	private JSpinner[] allSpinners = { gtwWordLengthMinJSpinner, gtwWordLengthMaxJSpinner,
		wtgNumberOfNonTerminalsMinJSpinner, wtgNumberOfNonTerminalsMaxJSpinner, wtgNumberOfRulesMinJSpinner,
		wtgNumberOfRulesMaxJSpinner, grammarParamTerminalsMinJSpinner, grammarParamTerminalsMaxJSpinner,
		grammarParamNonterminalsMinJSpinner, grammarParamNonterminalsMaxJSpinner, wordParamLengthMaxJSpinner,
		wordParamLengthMinJSpinner, cykTableParamDifferentSetsMinJSpinner, cykTableParamDifferentSetsMaxJSpinner,
		cykTableParamEmptySetsMinJSpinner, cykTableParamEmptySetsMaxJSpinner };

	private JSpinner[][] errorSpinnerPairs = { { gtwWordLengthMinJSpinner, gtwWordLengthMaxJSpinner },
		{ wtgNumberOfNonTerminalsMinJSpinner, wtgNumberOfNonTerminalsMaxJSpinner },
		{ grammarParamTerminalsMinJSpinner, grammarParamTerminalsMaxJSpinner },
		{ grammarParamNonterminalsMinJSpinner, grammarParamNonterminalsMaxJSpinner },
		{ wordParamLengthMinJSpinner, wordParamLengthMaxJSpinner },
		{ cykTableParamDifferentSetsMinJSpinner, cykTableParamDifferentSetsMaxJSpinner },
		{ cykTableParamEmptySetsMinJSpinner, cykTableParamEmptySetsMaxJSpinner },
		{ wtgNumberOfRulesMinJSpinner, wtgNumberOfRulesMaxJSpinner },
		{ grammarParamTerminalsMinJSpinner, wordParamLengthMaxJSpinner },
		{ grammarParamTerminalsMinJSpinner, wordParamLengthMinJSpinner },
		{ grammarParamTerminalsMaxJSpinner, wordParamLengthMinJSpinner } };

	// opravit - pre input GENERAL nepovolit REDUCED grammars

	@Override
	public void afterInit()
	{
		this.setViewportView(settingsPane);

	}

	@Override
	public void onLocaleChanged(Locale newLocale)
	{
		resourceBundle = ResourceBundle.getBundle("cykModule", newLocale);
		nameBundle = ResourceBundle.getBundle("CriteriaChecker", newLocale);
		updateStrings();
	}

	private void updateStrings()
	{
		String minimal = resourceBundle.getString("Minimal");
		String maximal = resourceBundle.getString("Maximal");
		grammarToWordGrammarJLabel.setText(resourceBundle.getString("grammarOrGrammars"));
		grammarToWordMinJLabel.setText(minimal);
		grammarToWordMaxJLabel.setText(maximal);
		wtgNonTerminalsJLabel.setText(resourceBundle.getString("nonTerminalsLabel"));
		wtgRulesJLabel.setText(resourceBundle.getString("rulesLabel"));
		wtgMinJLabel.setText(minimal);
		wtgMaxJLabel.setText(maximal);
		operationGrammarToWordJButton.setText(resourceBundle.getString("operationGrammarToWord"));// "regular grammar -> NFA");
		operationWordToGrammarJButton.setText(resourceBundle.getString("operationWordToGrammar"));// "NFA  -> regular grammar");
		loadWordButton.setText(resourceBundle.getString("buttonLoadWordText"));
		wordToGrammarWordJLabel.setText(resourceBundle.getString("wordsLabel"));
		wordLengthLabel.setText(resourceBundle.getString("wordLengthLabel"));
		loadGrammarButton.setText(resourceBundle.getString("buttonLoadGrammarText"));
		derivableScrollBarLabel.setText(resourceBundle.getString("derivableLabelText"));
		underivableScrollBarLabel.setText(resourceBundle.getString("nonderivableLabelText"));
		enabledDerivabalityLabel.setText(resourceBundle.getString("enabledLabelText"));
		derivableLabel.setText(resourceBundle.getString("derivableLabelText"));
		underivableLabel.setText(resourceBundle.getString("nonderivableLabelText"));
		derivabilityRatioLabel.setText(resourceBundle.getString("ratioLabelText"));
		grammarParamNonterminalsJLabel.setText(resourceBundle.getString("nonTerminalsText"));
		grammarParamTerminalsJLabel.setText(resourceBundle.getString("terminalsText"));
		wordParamLengthJLabel.setText(resourceBundle.getString("lengthText"));
		cykTableParamEmptySetsJLabel.setText(resourceBundle.getString("emptySetsText"));
		cykTableParamDifferentSetsJLabel.setText(resourceBundle.getString("differentSetsText"));
		derivableJCheckbox.setText(resourceBundle.getString("derivableWords"));

		grammarParamMinJLabel.setText(minimal);
		grammarParamMaxJLabel.setText(maximal);
		wordParamMinJLabel.setText(minimal);
		wordParamMaxJLabel.setText(maximal);
		cykTableParamMinJLabel.setText(minimal);
		cykTableParamMaxJLabel.setText(maximal);

		// Titled borders
		transformationTypePane.setBorder(BorderFactory.createTitledBorder(resourceBundle
			.getString("TransformationType")));
		grammarToWordOutputParametersJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle
			.getString("Operation")));
		grammarToWordInputGrammarJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle
			.getString("InputGrammar")));
		grammarToWordOutputParametersJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle
			.getString("OutputWords")));
		wordToGrammarInputWordJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("InputWord")));
		wordToGrammarOutputParametersJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle
			.getString("OutputGrammars")));
		wordParametersJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("WordParams")));
		grammarParametersJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("GrammarParams")));
		cykTableParametersJPanel
			.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("CYKTableParams")));
		operationJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("Operation")));
		derivabilityJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("Derivability")));

		operationGeneralCYKJButton.setText(resourceBundle.getString("generalCYK.text"));

		for (JSpinner s : allSpinners)
		{
			s.setToolTipText("<html><p style=\"font:10px Tahoma\">" +nameBundle.getString(s.getName())+ " </html>");
		}
	}
	//
	// /**
	// * shows the the error dialog pane, if the error occures
	// *
	// * @param message
	// * the cause of the error
	// */
	// private void showErrorDialog(String message)
	// {
	// JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	// }
	//
	// /**
	// * shows the warning dialog pane and resets the stop and generate button
	// *
	// * @param message
	// * the cause of the proposition
	// */
	private void showWarningDialog(String message)
	{
		JOptionPane.showMessageDialog(this, message, resourceBundle.getString("Error"), JOptionPane.WARNING_MESSAGE);
	}

	@Override
	public Map<String, Integer> saveSettings()
	{
		Map<String, Integer> settings = new HashMap<String, Integer>();
		// -1 is disabled
		for (JSpinner spinner : allSpinners)
		{
			settings.put(spinner.getName().replace(".name", ""), !spinnersSup.isEnabled(spinner) ? -1
				: (Integer) spinner.getValue());
		}

		return settings;
	}
	@Override
	public void loadSettings(Map<String, Integer> settings)
	{
		for (JSpinner spinner : allSpinners)
		{
			String spinnerDeclarationName = spinner.getName().replace(".name", "");
			if (settings.get(spinnerDeclarationName) == -1)
			{
				spinnersSup.disable(spinner);
			}
			else
			{
				spinner.setValue(settings.get(spinnerDeclarationName));
			}
		}
	}
}
