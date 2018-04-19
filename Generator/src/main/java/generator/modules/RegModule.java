
package generator.modules;

import generator.common.GenericModulePane;
import generator.common.tools.CommonUtils;
import generator.common.tools.CriteriaConstraintsChecker;
import generator.common.tools.SpinnersSupport;
import generator.common.tools.ThreadPair;
import generator.core.FormalLanguagesExampleGenerator;
import generator.modules.cfgexamplegenerator.ColorController;
import generator.modules.reglang.T0Generator;
import generator.modules.reglang.T1Generator;
import generator.modules.reglang.T2Generator;
import generator.modules.reglang.T3Generator;
import javafx.util.Pair;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.event.ChangeEvent;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class RegModule extends GenericModulePane
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ResourceBundle resourceBundle = ResourceBundle.getBundle("regularLanguagesModule", new Locale("en", "US"));
	ResourceBundle nameBundle = ResourceBundle.getBundle("CriteriaChecker", new Locale("en"));
	private SpinnersSupport spinnersSup;
	private ColorController colorContr;
	private CriteriaConstraintsChecker criteriaChecker;
	private JLabel messagesLabel = new JLabel();
	private int activePanelNumber = 0;
	private int activeType2SubpanelNumber = 0;
	private int activeType3SubpanelNumber = 0;
	public static final String SPINNER_DISABLED_BY_RADIO = "SPINNER_DISABLED_BY_RADIO";
	private FormalLanguagesExampleGenerator generatorCore;
	private boolean validationOK;

	public RegModule()
	{
	    exclusionsList = new ArrayList<String>();
	    for (String s : exclusions)
	        exclusionsList.add(s);

		settingsPane.setName("settingsPane");
		settingsPane.setLayout(new GridBagLayout());

		setupSpinners();


		setupTransformationTypeLayout();
		setupType0Layout();
		setupType1Layout();
		setupType2Layout();
		setupType3Layout();
		addEndPageFiller();

		deactivateAllGridbagPanels();
		activateTransformationPanel(0);

		disPopUpMenu.setName("disPopUpMenu"); // NOI18N
		disMenuItem.setName("disMenuItem"); // NOI18N
		disPopUpMenu.add(disMenuItem);

		messagesLabel.setVerticalAlignment(SwingConstants.TOP);
		messagesLabel.setName("messagesLabel"); // NOI18N

		updateStrings();

		settingsPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


	}

	public void generate(int numberOfExamplesToGenerate)
	{
		validationOK = true;
		generatorCore = FormalLanguagesExampleGenerator.getCoreInstance();

		int numberOfExercises = numberOfExamplesToGenerate;

		switch (activePanelNumber)
		{
			case 0:
				generateType0(numberOfExercises);
				break;
			case 1:
				generateType1(numberOfExercises);
				break;
			case 2:
				switch (activeType2SubpanelNumber)
				{
					case 0:
						generateType2Subtype0(numberOfExercises);
						break;
					case 1:
						generateType2Subtype1(numberOfExercises);
						break;

					default:
						break;
				}
				break;
			case 3:
				switch (activeType3SubpanelNumber)
				{
					case 0:

						generateType3Subtype0(numberOfExercises);
						break;
					case 1:
						generateType3Subtype1(numberOfExercises);
						break;

					default:
						break;
				}
				break;
			default:
		}
		validationOK = true;
	}

	private void checkSpinnerErrors()
    {
        for (Pair<String, String> p : criteriaChecker.getErrorMessages()){
            wrongParamsInterruptGenerating(p.getValue());
        }
    }

	private void generateType0(int numberOfExercises)
	{

		int operation = this.type0OperationJComboBox.getSelectedIndex();
		int alphabet = this.type0Alphabet1JComboBox.getSelectedIndex();
		int sizeOfAlphabetMin = 0;
		int sizeOfAlphabetMax = 0;
		// attributes of the first automaton
		int a1NumberOfStatesMin = 0;
		int a1NumberOfStatesMax = 0;
		int a1NumberOfFinalStatesMin = 0;
		int a1NumberOfFinalStatesMax = 0;
		int a1NumberOfTransitionsMin = 0;
		int a1NumberOfTransitionsMax = 0;
		boolean a1TotalTransitionFunction = this.type0Automaton1TotalTransitionFunctionJCheckBox.isSelected();
		boolean a1Complement = this.type0Automaton1CoJRadioButton.isSelected();
		int a1States = this.type0Automaton1StatesJComboBox.getSelectedIndex();

		int selectedStates1 = 0;
		switch (a1States)
		{
			case 0:
				selectedStates1 = 1;
				break;
			case 1:
				selectedStates1 = 2;
				break;
			case 2:
				selectedStates1 = 4;
				break;
			case 3:
				selectedStates1 = 5;
				break;
			case 4:
				selectedStates1 = 6;
				break;
		}
		// attributes of the second automaton
		int a2NumberOfStatesMin = 0;
		int a2NumberOfStatesMax = 0;
		int a2NumberOfFinalStatesMin = 0;
		int a2NumberOfFinalStatesMax = 0;
		int a2NumberOfTransitionsMin = 0;
		int a2NumberOfTransitionsMax = 0;
		boolean a2TotalTransitionFunction = this.type0Automaton2TotalTransitionFunctionJCheckBox.isSelected();
		boolean a2Complement = this.type0Automaton2CoJRadioButton.isSelected();
		int a2States = this.type0Automaton2StatesJComboBox.getSelectedIndex();
		int selectedStates2 = 0;
		switch (a2States)
		{
			case 0:
				selectedStates2 = 1;
				break;
			case 1:
				selectedStates2 = 2;
				break;
			case 2:
				selectedStates2 = 4;
				break;
			case 3:
				selectedStates2 = 5;
				break;
			case 4:
				selectedStates2 = 6;
				break;
		}
		// result, the optional parameters are -1, if tey are not set
		int outputStatesMin = -1;
		int outputStatesMax = -1;
		int outputTransitionsMin = -1;
		int outputTransitionsMax = -1;
		boolean outputMinOneFinalState = this.type0OutputAutomatonMinOneFinalStateJCheckBox.isSelected();
		// try
		// {
		a1NumberOfStatesMin = (Integer) type0Automaton1StatesMinJSpinner.getValue();
		a1NumberOfStatesMax = (Integer) type0Automaton1StatesMaxJSpinner.getValue();
		a1NumberOfFinalStatesMin = (Integer) type0Automaton1FinalStatesMinJSpinner.getValue();
		a1NumberOfFinalStatesMax = (Integer) type0Automaton1FinalStatesMaxJSpinner.getValue();
		a1NumberOfTransitionsMin = (Integer) type0Automaton1TransitionsMinJSpinner.getValue();
		a1NumberOfTransitionsMax = (Integer) type0Automaton1TransitionsMaxJSpinner.getValue();
		sizeOfAlphabetMin = (Integer) type0Automaton1AlphabetMinJSpinner.getValue();
		sizeOfAlphabetMax = (Integer) type0Automaton1AlphabetMaxJSpinner.getValue();
		a2NumberOfStatesMin = (Integer) type0Automaton2StatesMinJSpinner.getValue();
		a2NumberOfStatesMax = (Integer) type0Automaton2StatesMaxJSpinner.getValue();
		a2NumberOfFinalStatesMin = (Integer) type0Automaton2FinalStatesMinJSpinner.getValue();
		a2NumberOfFinalStatesMax = (Integer) type0Automaton2FinalStatesMaxJSpinner.getValue();
		a2NumberOfTransitionsMin = (Integer) type0Automaton2TransitionsMinJSpinner.getValue();
		a2NumberOfTransitionsMax = (Integer) type0Automaton2TransitionsMaxJSpinner.getValue();

        checkSpinnerErrors();

		if (spinnersSup.isEnabled(type0OutputAutomatonStatesMinJSpinner)
			&& spinnersSup.isEnabled(type0OutputAutomatonStatesMaxJSpinner))
		{
			outputStatesMin = (Integer) type0OutputAutomatonStatesMinJSpinner.getValue();
			outputStatesMax = (Integer) type0OutputAutomatonStatesMaxJSpinner.getValue();
			/*if (outputStatesMin < 1)
			{
				generatorCore.generatingStopped();
				wrongParamsInterruptGenerating("The output automaton panel:\n" + "number of states min < 1.");

			}*/
		}

		if (spinnersSup.isEnabled(type0OutputAutomatonTransitionsMinJSpinner)
			&& spinnersSup.isEnabled(type0OutputAutomatonTransitionsMaxJSpinner))
		{
			outputTransitionsMin = (Integer) type0OutputAutomatonTransitionsMinJSpinner.getValue();
			outputTransitionsMax = (Integer) type0OutputAutomatonTransitionsMaxJSpinner.getValue();
			/*if (outputTransitionsMin < 0)
			{
				generatorCore.generatingStopped();
				wrongParamsInterruptGenerating("The output automaton panel:\n" + "number of transitions min < 0.");

			}*/
		}

		// check the input for the automaton 1
        /*
		if (a1NumberOfStatesMin < 1 || a1NumberOfStatesMax < 1)
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The pane automaton A panel:\n" + "Number of states min, max must be >= 1.");

		}

		if ((a1NumberOfFinalStatesMin < 0) || (a1NumberOfFinalStatesMax < 0) || (a1NumberOfTransitionsMin < 0)
			|| (a1NumberOfTransitionsMax < 0) || (sizeOfAlphabetMin < 0) || (sizeOfAlphabetMax < 0))
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The pane automaton A panel:\n"
				+ "The fields \nhave to be filled with number >= 0.");

		}
		*/
		/*
		if (a1NumberOfStatesMin > a1NumberOfStatesMax)
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The automaton A panel:\n" + "number of states min > number of states max.");

		}
		if (a1NumberOfFinalStatesMin > a1NumberOfFinalStatesMax)
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The automaton A panel:\n"
				+ "number of final states min > number of final states max.");

		}
		if (a1NumberOfTransitionsMin > a1NumberOfTransitionsMax)
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The automaton A panel:\n"
				+ "number of transitions min > number of transitions max.");

		}
		if (sizeOfAlphabetMin > sizeOfAlphabetMax)
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("Automaton A panel:\n" + "size of alphabet min > size of alphabet max.");

		}

		if (a1NumberOfFinalStatesMin > a1NumberOfStatesMax)
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The automaton A panel:\n"
				+ "number of final states min > number of states max.");

		}

		if (sizeOfAlphabetMin > a1NumberOfTransitionsMax)
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The automaton A panel:\n"
				+ "size of alphabet min > number of transitions max.");

		}
		*/
		if (a1NumberOfTransitionsMin > a1NumberOfStatesMax * a1NumberOfStatesMax * sizeOfAlphabetMax)
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The automaton A panel:\n" + "number of transitions min > "
				+ "((number of states max)^2 * (size of alphabet max)).");

		}
		if (a1TotalTransitionFunction && (a1NumberOfStatesMin * sizeOfAlphabetMax < a1NumberOfTransitionsMax))
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The automaton A panel:\n" + "total transition function && "
				+ "(number of states min * size of alphabet max) < " + "number of transitions max).");

		}
		// check the input for automaton 2
        /*
		if (a2NumberOfStatesMin < 1 || a2NumberOfStatesMax < 1)
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The pane automaton B panel:\n" + "Number of states min, max must be >= 1.");

		}
		if ((a2NumberOfFinalStatesMin < 0) || (a2NumberOfFinalStatesMax < 0) || (a2NumberOfTransitionsMin < 0)
			|| (a2NumberOfTransitionsMax < 0))
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The pane automaton B panel:\n"
				+ "The fields \nhave to be filled with number >= 0.");

		}
		*/
		/*
		if (a2NumberOfStatesMin > a2NumberOfStatesMax)
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The automaton B panel:\n" + "number of states min > number of states max.");

		}
		if (a2NumberOfFinalStatesMin > a2NumberOfFinalStatesMax)
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The automaton B panel:\n"
				+ "number of final states min > number of final states max.");

		}
		if (a2NumberOfTransitionsMin > a2NumberOfTransitionsMax)
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The automaton B panel:\n"
				+ "number of transitions min > number of transitions max.");

		}

		if (sizeOfAlphabetMin > a2NumberOfTransitionsMax)
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The automaton B panel:\n"
				+ "size of alphabet min > number of transitions max.");

		}
		*/
		if (a2NumberOfTransitionsMin > a2NumberOfStatesMax * a2NumberOfStatesMax * sizeOfAlphabetMax)
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The automaton B panel:\n" + "number of transitions min > "
				+ "((number of states max)^2 * (size of alphabet max)).");

		}
		if (a2TotalTransitionFunction && (a2NumberOfStatesMin * sizeOfAlphabetMin > a2NumberOfTransitionsMax))
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The automaton B panel:\n" + "total transition function && "
				+ "(number of states min * size of alphabet max) < " + "number of transitions max).");

		}
		// check output attributes
        /*
		if (outputStatesMin > outputStatesMax)
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The output automaton panel:\n"
				+ "number of states min > number of states max.");

		}
		if (outputTransitionsMin > outputTransitionsMax)
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The output automaton panel:\n"
				+ "number of transitions min > number of transitions max.");

		}
		*/
		if (outputStatesMin > (a1NumberOfStatesMin + (a1TotalTransitionFunction ? 0 : 1))
			* (a2NumberOfStatesMin + (a2TotalTransitionFunction ? 0 : 1)))
		{
			generatorCore.generatingStopped();
			wrongParamsInterruptGenerating("The output automaton panel:\n"
				+ "output states min > (a1NumberOfStatesMin + "
				+ "(a1TotalTransitionFunction?0:1)) * (a2NumberOfStatesMin + " + "(a2TotalTransitionFunction?0:1))");

		}

		if (!validationOK)
		{
			return;
		}

		List<ThreadPair> threads = CommonUtils.getThreadsForGivenExampleNumber(numberOfExercises);

		for (int i = 0; i < threads.size(); i++)
		{

			new T0Generator(threads.get(i).getNumberOfExamplesPerThread(), operation, alphabet, sizeOfAlphabetMin,
				sizeOfAlphabetMax, a1NumberOfStatesMin, a1NumberOfStatesMax, a1NumberOfFinalStatesMin,
				a1NumberOfFinalStatesMax, a1NumberOfTransitionsMin, a1NumberOfTransitionsMax, selectedStates1,
				a1TotalTransitionFunction, a1Complement, a2NumberOfStatesMin, a2NumberOfStatesMax,
				a2NumberOfFinalStatesMin, a2NumberOfFinalStatesMax, a2NumberOfTransitionsMin, a2NumberOfTransitionsMax,
				selectedStates2, a2TotalTransitionFunction, a2Complement, outputStatesMin, outputStatesMax,
				outputTransitionsMin, outputTransitionsMax, outputMinOneFinalState, false, 0).execute();
		}
		// T0Generator t0 = new T0Generator(numberOfExercises, operation, alphabet, sizeOfAlphabetMin,
		// sizeOfAlphabetMax,
		// a1NumberOfStatesMin, a1NumberOfStatesMax, a1NumberOfFinalStatesMin, a1NumberOfFinalStatesMax,
		// a1NumberOfTransitionsMin, a1NumberOfTransitionsMax, a1States, a1TotalTransitionFunction, a1Complement,
		// a2NumberOfStatesMin, a2NumberOfStatesMax, a2NumberOfFinalStatesMin, a2NumberOfFinalStatesMax,
		// a2NumberOfTransitionsMin, a2NumberOfTransitionsMax, a2States, a2TotalTransitionFunction, a2Complement,
		// outputStatesMin, outputStatesMax, outputTransitionsMin, outputTransitionsMax, outputMinOneFinalState)
		// ;

		// t0.execute();
	}

	private void wrongParamsInterruptGenerating(String warningMessage)
	{
		validationOK = false;
		generatorCore.generatingStopped();
		this.showWarningDialog(warningMessage);
	}

	private void generateType1(int numberOfExercises)
	{
		int operationFrom = this.type1OperationFromJComboBox.getSelectedIndex();
		int operationTo = this.type1OperationFromJComboBox.getItemCount()
			- this.type1OperationToJComboBox.getItemCount() + this.type1OperationToJComboBox.getSelectedIndex();
		int alphabet = this.type1InputAutomatonAlphabetJComboBox.getSelectedIndex();

		int states = this.type1InputAutomatonStatesJComboBox.getSelectedIndex();

		int selectedStates = 0;
		switch (states)
		{
			case 0:
				selectedStates = 1;
				break;
			case 1:
				selectedStates = 2;
				break;
			case 2:
				selectedStates = 4;
				break;
			case 3:
				selectedStates = 5;
				break;
			case 4:
				selectedStates = 6;
				break;
		}

		boolean firstStateNotInitial = this.type1InputAutomatonFirstStateJCheckBox.isSelected();
		boolean totalTransitionFunction = this.type1InputAutomatonTotalTransitionFunctionJCheckBox.isSelected();
		boolean stepsWriteOut = this.type1MinimalizationStepsJCheckBox.isSelected();
		boolean isomorphism = this.type1IsomorphismJCheckBox.isSelected();
		int isoPercent = (Integer) this.type1IsoPercentJSpinner.getValue();
		int numberOfStatesMin = 1;
		int numberOfStatesMax = 10;
		int numberOfFinalStatesMin = 1;
		int numberOfFinalStatesMax = 10;
		int numberOfTransitionsMin = 1;
		int numberOfTransitionsMax = 10;
		int numberOfEpsTransitionsMin = 2;
		int numberOfEpsTransitionsMax = 5;
		int sizeOfAlphabetMin = 1;
		int sizeOfAlphabetMax = 10;
		// optional parameters
		int numberOfStepsMin = -1; // is -1, if is not set
		int numberOfStepsMax = -1; // is -1, if is not set
		int numberOfUnreachableStatesMin = 0;
		int numberOfUnreachableStatesMax = 0;
		int resultNumberOfStatesMin = -1; // is -1, if is not set
		int resultNumberOfStatesMax = -1; // is -1, if is not set
		int resultNumberOfTransitionsMin = -1; // is -1, if is not set
		int resultNumberOfTransitionsMax = -1; // is -1, if is not set
		// try
		// {
		numberOfStatesMin = (Integer) type1InputAutomatonStatesMinJSpinner.getValue();
		numberOfStatesMax = (Integer) type1InputAutomatonStatesMaxJSpinner.getValue();
		numberOfFinalStatesMin = (Integer) type1InputAutomatonFinalStatesMinJSpinner.getValue();
		numberOfFinalStatesMax = (Integer) type1InputAutomatonFinalStatesMaxJSpinner.getValue();
		numberOfTransitionsMin = (Integer) type1InputAutomatonTransitionsMinJSpinner.getValue();
		numberOfTransitionsMax = (Integer) type1InputAutomatonTransitionsMaxJSpinner.getValue();
		numberOfEpsTransitionsMin = (Integer) type1InputAutomatonEpsTransitionsMinJSpinner.getValue();
		numberOfEpsTransitionsMax = (Integer) type1InputAutomatonEpsTransitionsMaxJSpinner.getValue();

		if (operationFrom == 3)
		{
			numberOfStatesMin++;
			numberOfStatesMax++;
		}

		// for eps
		// if (operationFrom == 0)
		// {
		// numberOfTransitionsMin = numberOfTransitionsMin + 5;
		// numberOfTransitionsMax = numberOfTransitionsMax + 5;
		// }
		sizeOfAlphabetMin = (Integer) type1InputAutomatonAlphabetMinJSpinner.getValue();
		sizeOfAlphabetMax = (Integer) type1InputAutomatonAlphabetMaxJSpinner.getValue();

		checkSpinnerErrors();

		if (spinnersSup.isEnabled(type1InputAutomatonUnreachableStatesMinJSpinner)
			&& spinnersSup.isEnabled(type1InputAutomatonUnreachableStatesMaxJSpinner))
		{
			numberOfUnreachableStatesMin = (Integer) type1InputAutomatonUnreachableStatesMinJSpinner.getValue();
			numberOfUnreachableStatesMax = (Integer) type1InputAutomatonUnreachableStatesMaxJSpinner.getValue();
			/*if ((numberOfUnreachableStatesMin < 0) || (numberOfUnreachableStatesMax < 0))
			{
				wrongParamsInterruptGenerating("The input automaton panel:\n"
					+ "The fields number of unreachable states min, "
					+ "max \n do not have to be filled or have to be filled " + "with number >= 0.");

			}*/
		}

		if (spinnersSup.isEnabled(type1MinimalizationStepsMinJSpinner)
			&& spinnersSup.isEnabled(type1MinimalizationStepsMaxJSpinner)
			&& type1MinimalizationStepsMinJSpinner.isEnabled() && type1MinimalizationStepsMaxJSpinner.isEnabled())
		{
			numberOfStepsMin = (Integer) type1MinimalizationStepsMinJSpinner.getValue();
			numberOfStepsMax = (Integer) type1MinimalizationStepsMaxJSpinner.getValue();
			/*
			if (numberOfStepsMin <= 0 || numberOfStepsMax <= 0)
			{
				wrongParamsInterruptGenerating("The minimization panel:\n"
					+ "The fields (number of steps min, max) do not " + "have to be filled\n"
					+ "or must be filled with number > 0.");

			}
			*/
		}

		if (spinnersSup.isEnabled(type1OutputAutomatonStatesMinJSpinner)
			&& spinnersSup.isEnabled(type1OutputAutomatonStatesMaxJSpinner)
			&& type1OutputAutomatonStatesMinJSpinner.isEnabled() && type1OutputAutomatonStatesMaxJSpinner.isEnabled())
		{
			resultNumberOfStatesMin = (Integer) type1OutputAutomatonStatesMinJSpinner.getValue();
			resultNumberOfStatesMax = (Integer) type1OutputAutomatonStatesMaxJSpinner.getValue();
			/*if (resultNumberOfStatesMin < 0)
			{
				wrongParamsInterruptGenerating("The output automaton panel:\n" + "number of states min < 1.");

			}*/
		}

		if (spinnersSup.isEnabled(type1OutputAutomatonTransitionsMinJSpinner)
			&& spinnersSup.isEnabled(type1OutputAutomatonTransitionsMaxJSpinner))
		{
			resultNumberOfTransitionsMin = (Integer) type1OutputAutomatonTransitionsMinJSpinner.getValue();
			resultNumberOfTransitionsMax = (Integer) type1OutputAutomatonTransitionsMaxJSpinner.getValue();

			/*if (resultNumberOfTransitionsMin < 0)
			{
				wrongParamsInterruptGenerating("The output automaton panel:\n" + "number of transitions min < 0.");

			}*/
		}

		// // check the input
		// // attributes of the input automatom panel
		/*
		if ((numberOfStatesMin < 1) || (numberOfStatesMax < 1))
		{

			wrongParamsInterruptGenerating("The input automaton panel:\n"
				+ "the number of states min, max must be filled with number > 1.");

		}

		if ((numberOfFinalStatesMin < 0) || (numberOfFinalStatesMax < 0) || (numberOfTransitionsMin < 0)
			|| (numberOfTransitionsMax < 0) || (sizeOfAlphabetMin < 0) || (sizeOfAlphabetMax < 0))
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n"
				+ "The fields have to be filled with number >= 0.\n"
				+ "The fields unreachable states min, max do not have\n"
				+ "to be filled or must be filled with number >= 0.");

		}
		*/
		// if (totalTransitionFunction && (numberOfStatesMin * sizeOfAlphabetMin > numberOfTransitionsMax))
		// {
		// wrongParamsInterruptGenerating("The input automaton panel:\n" + "total transition function && "
		// + "(number of states min * size of alphabet max > " + "number of transitions max).");
		//
		// }
		/*
		if (numberOfStatesMin > numberOfStatesMax)
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n"
				+ "number of states min > number of states max.");

		}
		if (numberOfFinalStatesMin > numberOfFinalStatesMax)
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n"
				+ "number of final states min > number of final states max.");

		}
		if (numberOfTransitionsMin > numberOfTransitionsMax)
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n"
				+ "number of transitions min > number of transitions max.");

		}
		if (sizeOfAlphabetMin > sizeOfAlphabetMax)
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n"
				+ "size of alphabet min > size of alphabet max.");

		}
		if (numberOfUnreachableStatesMin > numberOfUnreachableStatesMax)
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n" + "nnumber of unreachable states min > "
				+ "number of unreachable states max.");

		}

		if (numberOfFinalStatesMin > numberOfStatesMax)
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n"
				+ "number of final states min > number of states max.");

		}

		if (sizeOfAlphabetMin > numberOfTransitionsMax)
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n"
				+ "size of alphabet min > number of transitions max.");

		}
		*/
		if (operationFrom == 0)
		{
			if (numberOfTransitionsMin > numberOfStatesMax * numberOfStatesMax * (sizeOfAlphabetMax + 1))
			{
				wrongParamsInterruptGenerating("The input automaton panel:\n" + "number of transitions min > "
					+ "(number of states max)^2 * (size of alphabet max + 1).");

			}
		}
		else
		{
			if (numberOfTransitionsMin > numberOfStatesMax * numberOfStatesMax * sizeOfAlphabetMax)
			{
				wrongParamsInterruptGenerating("The input automaton panel:\n" + "number of transitions min > "
					+ "(number of states max)^2 * (size of alphabet max).");

			}
		}
		if (firstStateNotInitial && numberOfStatesMax == 1)
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n"
				+ "first state not initial && (number of states max = 1).");
		}
		// minimization
        /*
		if (numberOfStepsMin > numberOfStepsMax)
		{
			wrongParamsInterruptGenerating("The minimization panel:\n" + "number of steps min > number of steps max.");

		}
		*/
		// output automaton
        /*
		if (resultNumberOfStatesMin > resultNumberOfStatesMax)
		{
			wrongParamsInterruptGenerating("The output automaton panel:\n"
				+ "number of states min > number of states max.");

		}
		if (resultNumberOfTransitionsMin > resultNumberOfTransitionsMax)
		{
			wrongParamsInterruptGenerating("The output automaton panel:\n"
				+ "number of transitions min > number of transitions max.");

		}
		*/
		/*
		if (isoPercent > 100) {
			wrongParamsInterruptGenerating("The correct algorithm test panel:\n"
				+ "partial points > 100.");
		}
		*/
		if (!validationOK)
		{
			return;
		}
		List<ThreadPair> threads = CommonUtils.getThreadsForGivenExampleNumber(numberOfExercises);

		for (int i = 0; i < threads.size(); i++)
		{
			new T1Generator(threads.get(i).getNumberOfExamplesPerThread(), operationFrom, operationTo,
				numberOfStatesMin, numberOfStatesMax, numberOfFinalStatesMin, numberOfFinalStatesMax,
				numberOfTransitionsMin, numberOfTransitionsMax, sizeOfAlphabetMin, sizeOfAlphabetMax, alphabet,
				selectedStates, stepsWriteOut, totalTransitionFunction, firstStateNotInitial, numberOfStepsMin,
				numberOfStepsMax, numberOfUnreachableStatesMin, numberOfUnreachableStatesMax, resultNumberOfStatesMin,
				resultNumberOfStatesMax, resultNumberOfTransitionsMin, resultNumberOfTransitionsMax,
				numberOfEpsTransitionsMin, numberOfEpsTransitionsMax, isomorphism, isoPercent).execute();
		}
	}

	/**
	 * RG->NFA
	 * 
	 * @param numberOfExercises
	 */
	private void generateType2Subtype0(int numberOfExercises)
	{

		int terminals = 0;
		int variables = 0;
		int numberOfVariablesMin = 0;
		int numberOfVariablesMax = 0;
		int numberOfTerminalsMin = 0;
		int numberOfTerminalsMax = 0;
		int numberOfRulesMin = 0;
		int numberOfRulesMax = 0;
		int numberOfLoopsMin = 0;
		int numberOfLoopsMax = 4;
		boolean epsilon = this.type2InputGrammarEpsilonJCheckBox.isSelected();
        boolean isomorphism = this.type2IsomorphismJCheckBox.isSelected();
        int isoPercent = (Integer) this.type2IsoPercentJSpinner.getValue();
		// optional attributes
		int resultNumberOfStatesMin = -1; // -1, if is not set
		int resultNumberOfStatesMax = -1; // -1, if is not set
		int resultNumberOfTransitionsMin = -1; // -1, if is not set
		int resultNumberOfTransitionsMax = -1; // -1, if is not set
		int resultSizeOfAlphabetMin = -1; // -1, if is not set
		int resultSizeOfAlphabetMax = -1; // -1, if is not set
		int resultNumberOfUnreachableStatesMin = -1; // -1, if is not set
		int resultNumberOfUnreachableStatesMax = -1; // -1, if is not set
		switch (this.type2InputGrammarTerminalsJComboBox.getSelectedIndex())
		{
			case 0:
				terminals = 0;
				break;
			case 1:
				terminals = 2;
				break;
			case 2:
				terminals = 3;
				break;
			case 3:
				terminals = 5;
				break;
			case 4:
				terminals = 6;
				break;
			case 5:
				terminals = 8;
				break;
			case 6:
				terminals = 9;
				break;
		}
		switch (this.type2InputGrammarVariablesJComboBox.getSelectedIndex())
		{
			case 0:
				variables = 1;
				break;
			case 1:
				variables = 4;
				break;
			case 2:
				variables = 5;
				break;
			case 3:
				variables = 6;
				break;
			case 4:
				variables = 7;
				break;
		}
		numberOfVariablesMin = (Integer) type2InputGrammarVariablesMinJSpinner.getValue();
		numberOfVariablesMax = (Integer) type2InputGrammarVariablesMaxJSpinner.getValue();
		numberOfRulesMin = (Integer) type2InputGrammarRulesMinJSpinner.getValue();
		numberOfRulesMax = (Integer) type2InputGrammarRulesMaxJSpinner.getValue();
		numberOfTerminalsMin = (Integer) type2InputGrammarTerminalsMinJSpinner.getValue();
		numberOfTerminalsMax = (Integer) type2InputGrammarTerminalsMaxJSpinner.getValue();
		numberOfLoopsMin = (Integer) type2InputGrammarLoopsMinJSpinner.getValue();
		numberOfLoopsMax = (Integer) type2InputGrammarLoopsMaxJSpinner.getValue();

		checkSpinnerErrors();

		// check the input
		// if (resultNumberOfStatesMin > resultNumberOfStatesMax)
		// {
		// wrongParamsInterruptGenerating("The output automaton panel:\n"
		// + "number of states min > number of states max.");
		//
		// }
		// if (resultNumberOfTransitionsMin > resultNumberOfTransitionsMax)
		// {
		// wrongParamsInterruptGenerating("The output automaton panel:\n"
		// + "number of transitions min > number of transitions max.");
		//
		// }
		// if (resultSizeOfAlphabetMin > resultSizeOfAlphabetMax)
		// {
		// wrongParamsInterruptGenerating("The output automaton panel:\n"
		// + "size of alphabet min > size of alphabet max.");
		//
		// }
		// if (resultSizeOfAlphabetMin > -1 && resultNumberOfTransitionsMax > -1)
		// if (resultSizeOfAlphabetMin > resultNumberOfTransitionsMax)
		// {
		// wrongParamsInterruptGenerating("The output automaton panel:\n"
		// + "size of alphabet min > number of transitions max.");
		//
		// }
        /*
		if ((numberOfVariablesMin < 1) || (numberOfVariablesMax < 1))
		{
			wrongParamsInterruptGenerating("The input grammar panel:\n"
				+ "The fields number of non-terminals min, max have to be " + "filled with number >= 1.");

		}
		if ((numberOfRulesMin < 0) || (numberOfRulesMax < 0) || (numberOfTerminalsMin < 0)
			|| (numberOfTerminalsMax < 0))
		{
			wrongParamsInterruptGenerating("The input grammar panel:\n"
				+ "The fields have to be filled with number >= 0.");

		}

		if (numberOfTerminalsMin > numberOfTerminalsMax)
		{
			wrongParamsInterruptGenerating("The input grammar panel:\n"
				+ "number of terminals min > number of terminals max.");

		}
		if (numberOfVariablesMin > numberOfVariablesMax)
		{
			wrongParamsInterruptGenerating("The input grammar panel:\n"
				+ "number of non-terminals min > number of non-terminals max.");

		}
		if (numberOfRulesMin > numberOfRulesMax)
		{
			wrongParamsInterruptGenerating("The input grammar panel:\n" + "number of rules min > number of rules max.");

		}
		// if (resultNumberOfUnreachableStatesMin > resultNumberOfUnreachableStatesMax)
		// {
		// wrongParamsInterruptGenerating("The output automaton panel:\n"
		// + "number of unreachable states min > number of unreachable states max.");
		//
		// }
		if (isoPercent > 100) {
			wrongParamsInterruptGenerating("The correct algorithm test panel:\n"
					+ "partial points > 100.");
		}
		*/
		if (!validationOK)
		{
			return;
		}
		List<ThreadPair> threads = CommonUtils.getThreadsForGivenExampleNumber(numberOfExercises);

		for (int i = 0; i < threads.size(); i++)
		{
			new T2Generator(threads.get(i).getNumberOfExamplesPerThread(), numberOfVariablesMin, numberOfVariablesMax,
				numberOfRulesMin, numberOfRulesMax, numberOfTerminalsMin, numberOfTerminalsMax, terminals, variables,
				epsilon, resultNumberOfStatesMin, resultNumberOfStatesMax, resultNumberOfTransitionsMin,
				resultNumberOfTransitionsMax, resultSizeOfAlphabetMin, resultSizeOfAlphabetMax,
				resultNumberOfUnreachableStatesMin, resultNumberOfUnreachableStatesMax, numberOfLoopsMin,
				numberOfLoopsMax, isomorphism, isoPercent).execute();
		}

	}
	/**
	 * NFA->RG
	 * 
	 * @param numberOfExercises
	 */
	private void generateType2Subtype1(int numberOfExercises)
	{
		int numberOfStatesMin = 0;
		int numberOfStatesMax = 0;
		int numberOfFinalStatesMin = 0;
		int numberOfFinalStatesMax = 0;
		int numberOfTransitionsMin = 0;
		int numberOfTransitionsMax = 0;
		int sizeOfAlphabetMin = 0;
		int sizeOfAlphabetMax = 0;
		// optional atributes
		int numberOfUnreachableStatesMin = 0;
		int numberOfUnreachableStatesMax = 0;
		int resultNumberOfVariablesMin = -1; // -1, if is not set
		int resultNumberOfVariablesMax = -1; // -1, if is not set
		int resultNumberOfTerminalsMin = -1; // -1, if is not set
		int resultNumberOfTerminalsMax = -1; // -1, if is not set
		int resultNumberOfRulesMin = -1; // -1, if is not set
		int resultNumberOfRulesMax = -1; // -1, if is not set
		int alphabet = this.type2InputAutomatonAlphabetJComboBox.getSelectedIndex();
		int states = this.type2InputAutomatonStatesJComboBox.getSelectedIndex();
		boolean firstStateFinal = type2InputAutomatonInitialStateFinal.isSelected();

		int selectedStates2 = 0;
		switch (states)
		{
			case 0:
				selectedStates2 = 1;
				break;
			case 1:
				selectedStates2 = 2;
				break;
			case 2:
				selectedStates2 = 4;
				break;
			case 3:
				selectedStates2 = 5;
				break;
			case 4:
				selectedStates2 = 6;
				break;
		}
		boolean epsilon = this.type2OutputGRammarEpsilonJCheckBox.isSelected();

		numberOfStatesMin = (Integer) type2InputAutomatonStatesMinJSpinner.getValue();
		numberOfStatesMax = (Integer) type2InputAutomatonStatesMaxJSpinner.getValue();
		numberOfFinalStatesMin = (Integer) type2InputAutomatonFinalStatesMinJSpinner.getValue();
		numberOfFinalStatesMax = (Integer) type2InputAutomatonFinalStatesMaxJSpinner.getValue();
		numberOfTransitionsMin = (Integer) type2InputAutomatonTransitionsMinJSpinner.getValue();
		numberOfTransitionsMax = (Integer) type2InputAutomatonTransitionsMaxJSpinner.getValue();
		sizeOfAlphabetMin = (Integer) type2InputAutomatonAlphabetMinJSpinner.getValue();
		sizeOfAlphabetMax = (Integer) type2InputAutomatonAlphabetMaxJSpinner.getValue();

		checkSpinnerErrors();

		if (spinnersSup.isEnabled(type2InputAutomatonUnreachableStatesMinJSpinner)
			&& spinnersSup.isEnabled(type2InputAutomatonUnreachableStatesMaxJSpinner))
		{
			numberOfUnreachableStatesMin = (Integer) type2InputAutomatonUnreachableStatesMinJSpinner.getValue();
			numberOfUnreachableStatesMax = (Integer) type2InputAutomatonUnreachableStatesMaxJSpinner.getValue();
			if (numberOfUnreachableStatesMin < 0)
			{
				wrongParamsInterruptGenerating("The pane Input automaton:\n"
					+ "The fields number of unreachable states min, max\n"
					+ "do not have to be filled or have to be filled " + "with number >= 0.");

			}
		}
		else
		{
			numberOfUnreachableStatesMax = numberOfStatesMax - 1;
			// if unreachable states min, max is not set ->
			// unreachable states min = 0
			// unreachable states max = (states max - 1)
		}

		// check the input
		// automaton
        /*
		if ((numberOfStatesMin < 1) || (numberOfStatesMax < 1))
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n"
				+ "the number of states min, max must be filled with number > 1.");

		}

		if ((numberOfFinalStatesMin < 0) || (numberOfFinalStatesMax < 0) || (numberOfTransitionsMin < 0)
			|| (numberOfTransitionsMax < 0) || (sizeOfAlphabetMin < 0) || (sizeOfAlphabetMax < 0))
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n"
				+ "The fields (except number of unreachable states min, " + "max)\nhave to be filled with number >= 0.");

		}

		if (numberOfStatesMin > numberOfStatesMax)
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n"
				+ "number of states min > number of states max.");

		}
		if (numberOfFinalStatesMin > numberOfFinalStatesMax)
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n"
				+ "number of final states min > number of final states max.");

		}
		if (numberOfTransitionsMin > numberOfTransitionsMax)
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n"
				+ "number of transitions min > number of transitions max.");

		}
		if (sizeOfAlphabetMin > sizeOfAlphabetMax)
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n"
				+ "size of alphabet min > size of alphabet max.");

		}
		if (numberOfUnreachableStatesMin > numberOfUnreachableStatesMax)
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n" + "number of unreachable states min > "
				+ "number of unreachable states max.");

		}

		if (numberOfFinalStatesMin > numberOfStatesMax)
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n"
				+ "number of final states min > number of states max.");

		}
		if (sizeOfAlphabetMin > numberOfTransitionsMax)
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n"
				+ "size of alphabet min > number of transitions max.");

		}
*/
		if (!validationOK)
		{
			return;
		}
		List<ThreadPair> threads = CommonUtils.getThreadsForGivenExampleNumber(numberOfExercises);

		for (int i = 0; i < threads.size(); i++)
		{
			new T2Generator(threads.get(i).getNumberOfExamplesPerThread(), numberOfStatesMin, numberOfStatesMax,
				numberOfFinalStatesMin, numberOfFinalStatesMax, numberOfTransitionsMin, numberOfTransitionsMax,
				sizeOfAlphabetMin, sizeOfAlphabetMax, alphabet, selectedStates2, epsilon, numberOfUnreachableStatesMin,
				numberOfUnreachableStatesMax, resultNumberOfVariablesMin, resultNumberOfVariablesMax,
				resultNumberOfRulesMin, resultNumberOfRulesMax, resultNumberOfTerminalsMin, resultNumberOfTerminalsMax,
				firstStateFinal, false, 0).execute();
		}

	}

	/**
	 * DFA to RE
	 * 
	 * @param numberOfExercises
	 */
	private void generateType3Subtype0(int numberOfExercises)
	{
		int sizeOfAlphabetMin = 0;
		int sizeOfAlphabetMax = 0;
		int numberOfStatesMin = 0;
		int numberOfStatesMax = 0;
		int numberOfFinalStatesMin = 0;
		int numberOfFinalStatesMax = 0;
		int numberOfTransitionsMin = 0;
		int numberOfTransitionsMax = 0;
		int alphabet = this.type3InputAutomatonAlphabetJComboBox.getSelectedIndex();
		int states = this.type3InputAutomatonStatesJComboBox.getSelectedIndex();

		int selectedStates2 = 0;
		switch (states)
		{
			case 0:
				selectedStates2 = 1;
				break;
			case 1:
				selectedStates2 = 2;
				break;
			case 2:
				selectedStates2 = 4;
				break;
			case 3:
				selectedStates2 = 5;
				break;
			case 4:
				selectedStates2 = 6;
				break;
		}

		// the output automaton regular expression panel
		int lengthMin = -1;// the parameter is not set
		int lengthMax = -1;// the parameter is not set
		int resultSizeOfAlphabetMin = -1;
		int resultSizeOfAlphabetMax = -1;

		int unionMin = (Integer) type3OutputREUnionMinJSpinner.getValue();
		int unionMax = (Integer) type3OutputREUnionMaxJSpinner.getValue();
		int concatMin = (Integer) type3OutputREConcatenationMinJSpinner.getValue();
		int concatMax = (Integer) type3OutputREConcatenationMaxJSpinner.getValue();
		int iterMin = (Integer) type3OutputREIterationMinJSpinner.getValue();
		int iterMax = (Integer) type3OutputREIterationMaxJSpinner.getValue();

		boolean hasEps = type3OutputREHasEpsJCheckBox.isSelected();

		numberOfStatesMin = (Integer) type3InputAutomatonStatesMinJSpinner.getValue();
		numberOfStatesMax = (Integer) type3InputAutomatonStatesMaxJSpinner.getValue();
		numberOfFinalStatesMin = (Integer) type3InputAutomatonFinalStatesMinJSpinner.getValue();
		numberOfFinalStatesMax = (Integer) type3InputAutomatonFinalStatesMaxJSpinner.getValue();
		numberOfTransitionsMin = (Integer) type3InputAutomatonTransitionsMinJSpinner.getValue();
		numberOfTransitionsMax = (Integer) type3InputAutomatonTransitionsMaxJSpinner.getValue();
		sizeOfAlphabetMin = (Integer) type3InputAutomatonAlphabetMinJSpinner.getValue();
		sizeOfAlphabetMax = (Integer) type3InputAutomatonAlphabetMaxJSpinner.getValue();

		int numberOfUnreachableMin = (Integer) type3InputAutomatonUnreachableStatesMinJSpinner.getValue();
		int numberOfUnreachableMax = (Integer) type3InputAutomatonUnreachableStatesMaxJSpinner.getValue();

		checkSpinnerErrors();

		// check the input
        /*
		if ((numberOfStatesMin < 0) || (numberOfStatesMax < 0) || (numberOfFinalStatesMin < 0)
			|| (numberOfFinalStatesMax < 0) || (numberOfTransitionsMin < 0) || (numberOfTransitionsMax < 0)
			|| (sizeOfAlphabetMin < 0) || (sizeOfAlphabetMax < 0))
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n"
				+ "the fields have to be filled with number >= 0.");

		}
*/
		if (numberOfTransitionsMin > numberOfStatesMax * numberOfStatesMax * sizeOfAlphabetMax)
		{
			wrongParamsInterruptGenerating("The input automaton panel:\n" + "number of transitions min > "
				+ "(number of states max)^2 * (size of alphabet max).");

		}

		if (!validationOK)
		{
			return;
		}
		List<ThreadPair> threads = CommonUtils.getThreadsForGivenExampleNumber(numberOfExercises);

		for (int i = 0; i < threads.size(); i++)
		{
			new T3Generator(threads.get(i).getNumberOfExamplesPerThread(), alphabet, selectedStates2,
				sizeOfAlphabetMin, sizeOfAlphabetMax, numberOfStatesMin, numberOfStatesMax, numberOfUnreachableMin,
				numberOfUnreachableMax, numberOfFinalStatesMin, numberOfFinalStatesMax, numberOfTransitionsMin,
				numberOfTransitionsMax, resultSizeOfAlphabetMin, resultSizeOfAlphabetMax, lengthMin, lengthMax,
				unionMin, unionMax, concatMin, concatMax, iterMin, iterMax, hasEps).execute();
		}

	}

	/**
	 * RE -> NFA
	 * 
	 * @param numberOfExercises
	 */
	private void generateType3Subtype1(int numberOfExercises)
	{

		int sizeOfAlphabetMin = 0;
		int sizeOfAlphabetMax = 0;
		int alphabet = this.type3InputReAlphabetJComboBox.getSelectedIndex();
		boolean isomorphism = this.type3IsomorphismJCheckBox.isSelected();
        int isoPercent = (Integer) this.type3IsoPercentJSpinner.getValue();

		int unionMin = (Integer) type3inputREUnionMinJSpinner.getValue();
		int unionMax = (Integer) type3inputREUnionMaxJSpinner.getValue();
		int concatMin = (Integer) type3inputREConcatenationMinJSpinner.getValue();
		int concatMax = (Integer) type3inputREConcatenationMaxJSpinner.getValue();
		int iterMin = (Integer) type3inputREIterationMinJSpinner.getValue();
		int iterMax = (Integer) type3inputREIterationMaxJSpinner.getValue();
		int epsMin = (Integer) type3inputREEpsMinJSpinner.getValue();
		int epsMax = (Integer) type3inputREEpsMaxJSpinner.getValue();
		int emptySetMin = (Integer) type3inputREEmptySetMinJSpinner.getValue();
		int emptySetMax = (Integer) type3inputREEmptySetMaxJSpinner.getValue();
		// input regular expression requierements
		sizeOfAlphabetMin = (Integer) type3InputREAlphabetMinJSpinner.getValue();
		sizeOfAlphabetMax = (Integer) type3InputREAlphabetMaxJSpinner.getValue();

		checkSpinnerErrors();

		// // check input
        /*
		if (sizeOfAlphabetMin < 0 || sizeOfAlphabetMax < 0)
		{
			wrongParamsInterruptGenerating("The input regular expression panel:\n"
				+ "the fields have to be filled with number >= 0.");
		}

		if (sizeOfAlphabetMin > sizeOfAlphabetMax)
		{
			wrongParamsInterruptGenerating("The input regular expression panel:\n"
				+ "size of alphabet min > size of alphabet max.");

		}
		if (isoPercent > 100) {
			wrongParamsInterruptGenerating("The correct algorithm test panel:\n"
					+ "partial points > 100.");
		}
		*/

		if (!validationOK)
		{
			return;
		}
		// TODO mohlo by se informativne prepocitavat jako vystupni parametry
		// System.out.println("states " + (2 + iterMin + concatMin) + "-" + (2+iterMax + concatMax));
		// System.out.println("transitions " + (1+2*iterMin+concatMin+unionMin)+"-"+(1+2*iterMax+concatMax+unionMax) );

		List<ThreadPair> threads = CommonUtils.getThreadsForGivenExampleNumber(numberOfExercises);

		for (int i = 0; i < threads.size(); i++)
		{
			new T3Generator(threads.get(i).getNumberOfExamplesPerThread(), sizeOfAlphabetMin, sizeOfAlphabetMax,
				alphabet, unionMin, unionMax, concatMin, concatMax, iterMin, iterMax, epsMin, epsMax, emptySetMin,
				emptySetMax, isomorphism, isoPercent).execute();
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
		if (transformationOrdinalNumber < 0 || transformationOrdinalNumber > 3)
			throw new IllegalArgumentException("Nonexistent transformation type");
		switch (transformationOrdinalNumber)
		{
			case 0:
				activePanelNumber = 0;
				type0OperationJPanel.setVisible(true);
				type0Automaton1JPanel.setVisible(true);
                /*for (Component comp : type0Automaton1JPanel.getComponents()){
                    if (comp instanceof JSpinner) {
                        Object val = ((JSpinner)comp).getValue();
                        ((JSpinner)comp).setValue(0);
                        ((JSpinner)comp).setValue(val);
                    }
                }*/
				type0Automaton2JPanel.setVisible(true);
				type0OutputAutomatonJPanel.setVisible(true);
				break;
			case 1:
				activePanelNumber = 1;
				type1OperationJPanel.setVisible(true);
				type1InputAutomatonJPanel.setVisible(true);
				type1OutputAutomatonJPanel.setVisible(true);
				type1MinimalizationJPanel.setVisible(false);
                type1IsomorphismJPanel.setVisible(true);
				int toIndex = type1OperationToJComboBox.getSelectedIndex();
				type1OperationFromJComboBox.setSelectedIndex(type1OperationFromJComboBox.getSelectedIndex());
                type1OperationToJComboBox.setSelectedIndex(toIndex);
				break;
			case 2:
				activePanelNumber = 2;
				activeType2SubpanelNumber = 0;
				type2OperationJPanel.setVisible(true);
				type2InputGrammarJPanel.setVisible(true);
                if (type2OperationNFAToGrammarJButton.isSelected())
                    type2OperationNFAToGrammarJButton.doClick();
                else
                    type2OperationGrammarToNFAJButton.doClick();
				break;
			case 3:
				activePanelNumber = 3;
				type3OperationJPanel.setVisible(true);
				type3InputAutomatonJPanel.setVisible(true);
				type3OutputREJPanel.setVisible(true);
                if (type3OperationDFAToREJButton.isSelected())
                    type3OperationDFAToREJButton.doClick();
                else
                    type3OperationREToDFAJButton.doClick();
		}
	}

	private void deactivateAllGridbagPanels()
	{
		type0OperationJPanel.setVisible(false);
		type0Automaton1JPanel.setVisible(false);
		type0Automaton2JPanel.setVisible(false);
		type0OutputAutomatonJPanel.setVisible(false);

		type1OperationJPanel.setVisible(false);
		type1InputAutomatonJPanel.setVisible(false);
		type1OutputAutomatonJPanel.setVisible(false);
		type1MinimalizationJPanel.setVisible(false);
        type1IsomorphismJPanel.setVisible(false);

		type2OperationJPanel.setVisible(false);
		type2InputGrammarJPanel.setVisible(false);
		type2OutputAutomatonJPanel.setVisible(false);
		type2InputAutomatonJPanel.setVisible(false);
		type2OutputGrammarJPanel.setVisible(false);
        type2IsomorphismJPanel.setVisible(false);

		type3OperationJPanel.setVisible(false);
		type3InputAutomatonJPanel.setVisible(false);
		type3OutputREJPanel.setVisible(false);
		type3InputREJPanel.setVisible(false);
		type3OutputAutomatonJPanel.setVisible(false);
        type3IsomorphismJPanel.setVisible(false);
	}

	private void changeGridbagPanelAllComponentsStatus(JPanel pane, boolean enabled)
	{
		pane.setEnabled(enabled);
		for (Component c : pane.getComponents())
		{
			c.setEnabled(enabled);
		}
	}

	private void setupSpinners()
	{
		colorContr = new ColorController(type0Automaton1AlphabetMaxJSpinner);
		spinnersSup = new SpinnersSupport(disPopUpMenu, disMenuItem, colorContr);
		criteriaChecker = new CriteriaConstraintsChecker(messagesLabel, new Action[] {}, spinnersSup, colorContr);
		for (JSpinner[] s : errorSpinnerPairs)
		{
			criteriaChecker.addErrConstraint(s[0], s[1]);
		}

		for (JSpinner spinner : allSpinners)
		{
			spinnersSup.registerSpinner(spinner);
			NumberEditor e = (NumberEditor) spinner.getEditor();
			e.getTextField().setHorizontalAlignment(JTextField.CENTER);
			spinner.setName("Default name - has to be set!");
			spinner.putClientProperty(SPINNER_DISABLED_BY_RADIO, !spinner.isEnabled());
		}

		type0Automaton1AlphabetMinJSpinner.setName("type0Automaton1AlphabetMinJSpinner.name");
		type0Automaton1StatesMinJSpinner.setName("type0Automaton1StatesMinJSpinner.name");
		type0Automaton1FinalStatesMinJSpinner.setName("type0Automaton1FinalStatesMinJSpinner.name");
		type0Automaton1TransitionsMinJSpinner.setName("type0Automaton1TransitionsMinJSpinner.name");
		type0Automaton1AlphabetMaxJSpinner.setName("type0Automaton1AlphabetMaxJSpinner.name");
		type0Automaton1StatesMaxJSpinner.setName("type0Automaton1StatesMaxJSpinner.name");
		type0Automaton1FinalStatesMaxJSpinner.setName("type0Automaton1FinalStatesMaxJSpinner.name");
		type0Automaton1TransitionsMaxJSpinner.setName("type0Automaton1TransitionsMaxJSpinner.name");
		type0Automaton2AlphabetMinJSpinner.setName("type0Automaton2AlphabetMinJSpinner.name");
		type0Automaton2StatesMinJSpinner.setName("type0Automaton2StatesMinJSpinner.name");
		type0Automaton2FinalStatesMinJSpinner.setName("type0Automaton2FinalStatesMinJSpinner.name");
		type0Automaton2TransitionsMinJSpinner.setName("type0Automaton2TransitionsMinJSpinner.name");
		type0Automaton2AlphabetMaxJSpinner.setName("type0Automaton2AlphabetMaxJSpinner.name");
		type0Automaton2StatesMaxJSpinner.setName("type0Automaton2StatesMaxJSpinner.name");
		type0Automaton2FinalStatesMaxJSpinner.setName("type0Automaton2FinalStatesMaxJSpinner.name");
		type0Automaton2TransitionsMaxJSpinner.setName("type0Automaton2TransitionsMaxJSpinner.name");
		type0OutputAutomatonStatesMinJSpinner.setName("type0OutputAutomatonStatesMinJSpinner.name");
		type0OutputAutomatonStatesMaxJSpinner.setName("type0OutputAutomatonStatesMaxJSpinner.name");
		type0OutputAutomatonTransitionsMinJSpinner.setName("type0OutputAutomatonTransitionsMinJSpinner.name");
		type0OutputAutomatonTransitionsMaxJSpinner.setName("type0OutputAutomatonTransitionsMaxJSpinner.name");
		type1InputAutomatonStatesMinJSpinner.setName("type1InputAutomatonStatesMinJSpinner.name");
		type1InputAutomatonFinalStatesMinJSpinner.setName("type1InputAutomatonFinalStatesMinJSpinner.name");
		type1InputAutomatonTransitionsMinJSpinner.setName("type1InputAutomatonTransitionsMinJSpinner.name");
		type1InputAutomatonEpsTransitionsMinJSpinner.setName("type1InputAutomatonEpsTransitionsMinJSpinner.name");
		type1InputAutomatonAlphabetMinJSpinner.setName("type1InputAutomatonAlphabetMinJSpinner.name");
		type1InputAutomatonStatesMaxJSpinner.setName("type1InputAutomatonStatesMaxJSpinner.name");
		type1InputAutomatonFinalStatesMaxJSpinner.setName("type1InputAutomatonFinalStatesMaxJSpinner.name");
		type1InputAutomatonTransitionsMaxJSpinner.setName("type1InputAutomatonTransitionsMaxJSpinner.name");
		type1InputAutomatonEpsTransitionsMaxJSpinner.setName("type1InputAutomatonEpsTransitionsMaxJSpinner.name");
		type1InputAutomatonAlphabetMaxJSpinner.setName("type1InputAutomatonAlphabetMaxJSpinner.name");
		type1InputAutomatonUnreachableStatesMinJSpinner.setName("type1InputAutomatonUnreachableStatesMinJSpinner.name");
		type1InputAutomatonUnreachableStatesMaxJSpinner.setName("type1InputAutomatonUnreachableStatesMaxJSpinner.name");
		type1OutputAutomatonStatesMinJSpinner.setName("type1OutputAutomatonStatesMinJSpinner.name");
		type1OutputAutomatonStatesMaxJSpinner.setName("type1OutputAutomatonStatesMaxJSpinner.name");
		type1OutputAutomatonTransitionsMinJSpinner.setName("type1OutputAutomatonTransitionsMinJSpinner.name");
		type1OutputAutomatonTransitionsMaxJSpinner.setName("type1OutputAutomatonTransitionsMaxJSpinner.name");
		type1MinimalizationStepsMinJSpinner.setName("type1MinimalizationStepsMinJSpinner.name");
		type1MinimalizationStepsMaxJSpinner.setName("type1MinimalizationStepsMaxJSpinner.name");
        type1IsoPercentJSpinner.setName("type1IsoPercentJSpinner.name");
        type2IsoPercentJSpinner.setName("type2IsoPercentJSpinner.name");
        type3IsoPercentJSpinner.setName("type3IsoPercentJSpinner.name");
		type2InputGrammarVariablesMinJSpinner.setName("type2InputGrammarVariablesMinJSpinner.name");
		type2InputGrammarRulesMinJSpinner.setName("type2InputGrammarRulesMinJSpinner.name");
		type2InputGrammarVariablesMaxJSpinner.setName("type2InputGrammarVariablesMaxJSpinner.name");
		type2InputGrammarRulesMaxJSpinner.setName("type2InputGrammarRulesMaxJSpinner.name");
		type2InputGrammarTerminalsMinJSpinner.setName("type2InputGrammarTerminalsMinJSpinner.name");
		type2InputGrammarTerminalsMaxJSpinner.setName("type2InputGrammarTerminalsMaxJSpinner.name");
		type2OutputAutomatonAlphabetMinJSpinner.setName("type2OutputAutomatonAlphabetMinJSpinner.name");
		type2OutputAutomatonAlphabetMaxJSpinner.setName("type2OutputAutomatonAlphabetMaxJSpinner.name");
		type2OutputAutomatonStatesMinJSpinner.setName("type2OutputAutomatonStatesMinJSpinner.name");
		type2OutputAutomatonTransitionsMinJSpinner.setName("type2OutputAutomatonTransitionsMinJSpinner.name");
		type2OutputAutomatonStatesMaxJSpinner.setName("type2OutputAutomatonStatesMaxJSpinner.name");
		type2OutputAutomatonTransitionsMaxJSpinner.setName("type2OutputAutomatonTransitionsMaxJSpinner.name");
		type2OutputAutomatonUnreachableStatesMinJSpinner
			.setName("type2OutputAutomatonUnreachableStatesMinJSpinner.name");
		type2OutputAutomatonUnreachableStatesMaxJSpinner
			.setName("type2OutputAutomatonUnreachableStatesMaxJSpinner.name");
		type2InputAutomatonAlphabetMinJSpinner.setName("type2InputAutomatonAlphabetMinJSpinner.name");
		type2InputAutomatonStatesMinJSpinner.setName("type2InputAutomatonStatesMinJSpinner.name");
		type2InputAutomatonFinalStatesMinJSpinner.setName("type2InputAutomatonFinalStatesMinJSpinner.name");
		type2InputAutomatonTransitionsMinJSpinner.setName("type2InputAutomatonTransitionsMinJSpinner.name");
		type2InputAutomatonAlphabetMaxJSpinner.setName("type2InputAutomatonAlphabetMaxJSpinner.name");
		type2InputAutomatonStatesMaxJSpinner.setName("type2InputAutomatonStatesMaxJSpinner.name");
		type2InputAutomatonFinalStatesMaxJSpinner.setName("type2InputAutomatonFinalStatesMaxJSpinner.name");
		type2InputAutomatonTransitionsMaxJSpinner.setName("type2InputAutomatonTransitionsMaxJSpinner.name");
		type2InputAutomatonUnreachableStatesMinJSpinner.setName("type2InputAutomatonUnreachableStatesMinJSpinner.name");
		type2InputAutomatonUnreachableStatesMaxJSpinner.setName("type2InputAutomatonUnreachableStatesMaxJSpinner.name");
		type2OutputGrammarVariablesMinJSpinner.setName("type2OutputGrammarVariablesMinJSpinner.name");
		type2OutputGrammarTerminalsMinJSpinner.setName("type2OutputGrammarTerminalsMinJSpinner.name");
		type2OutputGrammarRulesMinJSpinner.setName("type2OutputGrammarRulesMinJSpinner.name");
		type2OutputGrammarVariablesMaxJSpinner.setName("type2OutputGrammarVariablesMaxJSpinner.name");
		type2OutputGrammarTerminalsMaxJSpinner.setName("type2OutputGrammarTerminalsMaxJSpinner.name");
		type2OutputGrammarRulesMaxJSpinner.setName("type2OutputGrammarRulesMaxJSpinner.name");
		type3InputAutomatonStatesMinJSpinner.setName("type3InputAutomatonStatesMinJSpinner.name");
		type3InputAutomatonStatesMaxJSpinner.setName("type3InputAutomatonStatesMaxJSpinner.name");
		type3InputAutomatonUnreachableStatesMinJSpinner.setName("type3InputAutomatonUnreachableStatesMinJSpinner.name");
		type3InputAutomatonUnreachableStatesMaxJSpinner.setName("type3InputAutomatonUnreachableStatesMaxJSpinner.name");
		type3InputAutomatonFinalStatesMinJSpinner.setName("type3InputAutomatonFinalStatesMinJSpinner.name");
		type3InputAutomatonFinalStatesMaxJSpinner.setName("type3InputAutomatonFinalStatesMaxJSpinner.name");
		type3InputAutomatonTransitionsMinJSpinner.setName("type3InputAutomatonTransitionsMinJSpinner.name");
		type3InputAutomatonTransitionsMaxJSpinner.setName("type3InputAutomatonTransitionsMaxJSpinner.name");
		type3InputAutomatonAlphabetMinJSpinner.setName("type3InputAutomatonAlphabetMinJSpinner.name");
		type3InputAutomatonAlphabetMaxJSpinner.setName("type3InputAutomatonAlphabetMaxJSpinner.name");
		type3InputREAlphabetMinJSpinner.setName("type3InputREAlphabetMinJSpinner.name");
		type3InputREAlphabetMaxJSpinner.setName("type3InputREAlphabetMaxJSpinner.name");
		type3InputRELengthMinJSpinner.setName("type3InputRELengthMinJSpinner.name");
		type3InputRELengthMaxJSpinner.setName("type3InputRELengthMaxJSpinner.name");
		type3OutputAutomatonStatesMinJSpinner.setName("type3OutputAutomatonStatesMinJSpinner.name");
		type3OutputAutomatonTransitionsMinJSpinner.setName("type3OutputAutomatonTransitionsMinJSpinner.name");
		type3OutputAutomatonStatesMaxJSpinner.setName("type3OutputAutomatonStatesMaxJSpinner.name");
		type3OutputAutomatonTransitionsMaxJSpinner.setName("type3OutputAutomatonTransitionsMaxJSpinner.name");
		type3OutputREAlphabetMinJSpinner.setName("type3OutputREAlphabetMinJSpinner.name");
		type3OutputREAlphabetMaxJSpinner.setName("type3OutputREAlphabetMaxJSpinner.name");
		type3OutputRELengthMinJSpinner.setName("type3OutputRELengthMinJSpinner.name");
		type3OutputRELengthMaxJSpinner.setName("type3OutputRELengthMaxJSpinner.name");
		type2InputGrammarLoopsMaxJSpinner.setName("type2InputGrammarLoopsMaxJSpinner.name");
		type2InputGrammarLoopsMinJSpinner.setName("type2InputGrammarLoopsMinJSpinner.name");
		type3inputREUnionMinJSpinner.setName("type3inputREUnionMinJSpinner.name");
		type3inputREConcatenationMinJSpinner.setName("type3inputREConcatenationMinJSpinner.name");
		type3inputREIterationMinJSpinner.setName("type3inputREIterationMinJSpinner.name");
		type3inputREEpsMinJSpinner.setName("type3inputREEpsMinJSpinner.name");
		type3inputREEmptySetMinJSpinner.setName("type3inputREEmptySetMinJSpinner.name");
		type3inputREUnionMaxJSpinner.setName("type3inputREUnionMaxJSpinner.name");
		type3inputREConcatenationMaxJSpinner.setName("type3inputREConcatenationMaxJSpinner.name");
		type3inputREIterationMaxJSpinner.setName("type3inputREIterationMaxJSpinner.name");
		type3inputREEpsMaxJSpinner.setName("type3inputREEpsMaxJSpinner.name");
		type3inputREEmptySetMaxJSpinner.setName("type3inputREEmptySetMaxJSpinner.name");
		type3OutputREUnionMinJSpinner.setName("type3OutputREUnionMinJSpinner.name");
		type3OutputREConcatenationMinJSpinner.setName("type3OutputREConcatenationMinJSpinner.name");
		type3OutputREIterationMinJSpinner.setName("type3OutputREIterationMinJSpinner.name");
		type3OutputREUnionMaxJSpinner.setName("type3OutputREUnionMaxJSpinner.name");
		type3OutputREConcatenationMaxJSpinner.setName("type3OutputREConcatenationMaxJSpinner.name");
		type3OutputREIterationMaxJSpinner.setName("type3OutputREIterationMaxJSpinner.name");

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

		transformationTypePane.setName("transformationType");

		typeJComboBox.setName("typeJComboBox"); // NOI18N
		setStandardJComponentSize(typeJComboBox, 400, 25);
		typeJComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				typeJComboBoxActionPerformed(evt);
			}
		});
		transformationTypePane.add(typeJComboBox);
		setGridBagPosition(1, transformationTypePane);
	}

	private void setupType0Layout()
	{
		setupType0OperationLayout();
		setupType0Automaton1Layout();
		setupType0Automaton2Layout();
		setupType0OutputAutomatonLayout();

		setGridBagPosition(2, type0OperationJPanel);
		setGridBagPosition(3, type0Automaton1JPanel);
		setGridBagPosition(4, type0Automaton2JPanel);
		setGridBagPosition(5, type0OutputAutomatonJPanel);
	}

	private void setupType1Layout()
	{
		setupType1OperationLayout();
		setupType1InputAutomatonLayout();
		setupType1OutputAutomatonLayout();
		setupType1MinimalizationLayout();
        setupType1IsomorphismTestLayout();

		setGridBagPosition(2, type1OperationJPanel);
		setGridBagPosition(3, type1InputAutomatonJPanel);
		setGridBagPosition(4, type1OutputAutomatonJPanel);
		setGridBagPosition(5, type1MinimalizationJPanel);
        setGridBagPosition(6, type1IsomorphismJPanel);
		type1MinimalizationJPanel.setVisible(false);
	}

	private void setupType2Layout()
	{
		setupType2OperationLayout();
		setupType2InputGrammarLayout();
		// setupType2OutputAutomatonLayout();
		setupType2InputAutomatonLayout();
		// setupType2OutputGrammarLayout();
        setupType2IsomorphismTestLayout();

		setGridBagPosition(2, type2OperationJPanel);
		setGridBagPosition(3, type2InputGrammarJPanel);
		// setGridBagPosition(4, type2OutputAutomatonJPanel);
		setGridBagPosition(3, type2InputAutomatonJPanel);
		// setGridBagPosition(4, type2OutputGrammarJPanel);
        setGridBagPosition(4, type2IsomorphismJPanel);
	}

	private void setupType3Layout()
	{
		setupType3OperationLayout();
		setupType3InputAutomatonLayout();
		setupType3OutputRELayout();
		setupType3InputRELayout();
		// setupType3OutputAutomatonLayout();
        setupType3IsomorphismTestLayout();

		setGridBagPosition(2, type3OperationJPanel);
		setGridBagPosition(3, type3InputAutomatonJPanel);
		// for output RE IMHO yes
		setGridBagPosition(4, type3OutputREJPanel);
		setGridBagPosition(3, type3InputREJPanel);
		// calculated
		// setGridBagPosition(4, type3OutputAutomatonJPanel);
        setGridBagPosition(4, type3IsomorphismJPanel);
	}

	private void setupType0OperationLayout()
	{

		type0Automaton1buttonGroup.add(type0Automaton1JRadioButton);

		type0Automaton1JRadioButton.setSelected(true);

		type0OperationJPanel.add(type0Automaton1JRadioButton);

		type0Automaton1buttonGroup.add(type0Automaton1CoJRadioButton);

		type0OperationJPanel.add(type0Automaton1CoJRadioButton);

		setStandardJComponentSize(type0OperationJComboBox, 150, 25);
		type0OperationJPanel.add(type0OperationJComboBox);

		type0Automaton2buttonGroup.add(type0Automaton2JRadioButton);

		type0Automaton2JRadioButton.setSelected(true);

		type0OperationJPanel.add(type0Automaton2JRadioButton);

		type0Automaton2buttonGroup.add(type0Automaton2CoJRadioButton);

		type0OperationJPanel.add(type0Automaton2CoJRadioButton);
	}

	private void setupType0Automaton1Layout()
	{

		setStandardJComponentSize(type0Automaton1MinJLabel, 40, 14);
		// TODO
//		System.out.println(type0Automaton1AlphabetJLabel.getFont());;
		type0Automaton1MinJLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		type0Automaton1MinJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		setStandardJComponentSize(type0Automaton1MaxJLabel, 40, 14);
		type0Automaton1MaxJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// Alphabet min and max setting
		type0Automaton1AlphabetMinJSpinner.setValue(2);
		setStandardJComponentSize(type0Automaton1AlphabetMinJSpinner, 80, 25);

		type0Automaton1AlphabetMaxJSpinner.setValue(3);
		setStandardJComponentSize(type0Automaton1AlphabetMaxJSpinner, 80, 25);

		// States min and max setting
		type0Automaton1StatesMinJSpinner.setValue(3);
		setStandardJComponentSize(type0Automaton1StatesMinJSpinner, 80, 25);

		type0Automaton1StatesMaxJSpinner.setValue(5);
		setStandardJComponentSize(type0Automaton1StatesMaxJSpinner, 80, 25);

		// FinalStates min and max setting
		type0Automaton1FinalStatesMinJSpinner.setValue(1);
		setStandardJComponentSize(type0Automaton1FinalStatesMinJSpinner, 80, 25);

		type0Automaton1FinalStatesMaxJSpinner.setValue(2);
		setStandardJComponentSize(type0Automaton1FinalStatesMaxJSpinner, 80, 25);

		// Transitions min and max setting
		type0Automaton1TransitionsMinJSpinner.setValue(2);
		setStandardJComponentSize(type0Automaton1TransitionsMinJSpinner, 80, 25);

		type0Automaton1TransitionsMaxJSpinner.setValue(6);
		setStandardJComponentSize(type0Automaton1TransitionsMaxJSpinner, 80, 25);

		type0Alphabet1JComboBox.setModel(new DefaultComboBoxModel(new String[] { " a, b, c...", " A, B, C...",
			" 1, 2, 3...", " x, y, z...", " X, Y, Z...", " t0, t1, t2...", " p0, p1, p2...", " I, II, III, IV...",
			" i, ii, iii, iv...", " 1, 10, 11, 100..." }));
		setStandardJComponentSize(type0Alphabet1JComboBox, 148, 25);

		type0Automaton1StatesJComboBox.setModel(new DefaultComboBoxModel(new String[] { " A, B, C...", " 1, 2, 3...",
			" X, Y, Z...", " q0, q1, q2...", " s0, s1, s2..." }));
		type0Automaton1StatesJComboBox.setSelectedIndex(4);
		setStandardJComponentSize(type0Automaton1StatesJComboBox, 148, 25);

		setStandardJComponentSize(type0Automaton1TotalTransitionFunctionJCheckBox, 148, 25);

		criteriaChecker.addErrorConstraint(type0Automaton1AlphabetMinJSpinner, 0, true, null, true);
        criteriaChecker.addErrorConstraint(type0Automaton1StatesMinJSpinner, 1, true, null, true);
        criteriaChecker.addErrorConstraint(type0Automaton1FinalStatesMinJSpinner, 0, true, null, true);
        criteriaChecker.addErrorConstraint(type0Automaton1TransitionsMinJSpinner, 0, true, null, true);

		type0Automaton1TotalTransitionFunctionJCheckBox.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (type0Automaton1TotalTransitionFunctionJCheckBox.isSelected())
				{
					type0Automaton1TransitionsMinJSpinner.setEnabled(false);
					type0Automaton1TransitionsMaxJSpinner.setEnabled(false);
				}
				else
				{
					type0Automaton1TransitionsMinJSpinner.setEnabled(true);
					type0Automaton1TransitionsMaxJSpinner.setEnabled(true);
				}

			}
		});

		GroupLayout type0Automaton1Layout = new GroupLayout(type0Automaton1JPanel);
		type0Automaton1JPanel.setLayout(type0Automaton1Layout);
		type0Automaton1Layout
			.setHorizontalGroup(type0Automaton1Layout.createParallelGroup(Alignment.LEADING)
				.addGroup(
					Alignment.TRAILING,
					type0Automaton1Layout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
							type0Automaton1Layout
								.createParallelGroup(Alignment.TRAILING)
								.addComponent(type0Automaton1AlphabetJLabel, GroupLayout.DEFAULT_SIZE, 100,
									Short.MAX_VALUE)
								.addComponent(type0Automaton1StatesJLabel, GroupLayout.DEFAULT_SIZE, 100,
									Short.MAX_VALUE)
								.addComponent(type0Automaton1FinalStatesJLabel, GroupLayout.DEFAULT_SIZE, 100,
									Short.MAX_VALUE)
								.addComponent(type0Automaton1TransitionsJLabel, GroupLayout.DEFAULT_SIZE, 100,
									Short.MAX_VALUE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
							type0Automaton1Layout
								.createParallelGroup(Alignment.LEADING)
								.addGroup(
									type0Automaton1Layout
										.createSequentialGroup()
										.addComponent(type0Automaton1MinJLabel, GroupLayout.PREFERRED_SIZE, 65,
											GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(type0Automaton1MaxJLabel, GroupLayout.PREFERRED_SIZE, 65,
											GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type0Automaton1Layout
										.createSequentialGroup()
										.addComponent(type0Automaton1AlphabetMinJSpinner, GroupLayout.PREFERRED_SIZE,
											65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type0Automaton1AlphabetMaxJSpinner, GroupLayout.PREFERRED_SIZE,
											65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type0Automaton1TotalTransitionFunctionJCheckBox,
											GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
											GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type0Automaton1Layout
										.createSequentialGroup()
										.addComponent(type0Automaton1StatesMinJSpinner, GroupLayout.PREFERRED_SIZE, 65,
											GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type0Automaton1StatesMaxJSpinner, GroupLayout.PREFERRED_SIZE, 65,
											GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type0Alphabet1JComboBox, GroupLayout.PREFERRED_SIZE,
											GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type0Automaton1Layout
										.createSequentialGroup()
										.addComponent(type0Automaton1FinalStatesMinJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type0Automaton1FinalStatesMaxJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type0Automaton1StatesJComboBox, GroupLayout.PREFERRED_SIZE,
											GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type0Automaton1Layout
										.createSequentialGroup()
										.addComponent(type0Automaton1TransitionsMinJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type0Automaton1TransitionsMaxJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)))
						.addContainerGap()));
		type0Automaton1Layout.setVerticalGroup(type0Automaton1Layout.createParallelGroup(Alignment.LEADING).addGroup(
			type0Automaton1Layout
				.createSequentialGroup()
				.addGroup(
					type0Automaton1Layout
						.createParallelGroup(Alignment.BASELINE)
						.addComponent(type0Automaton1MinJLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Automaton1MaxJLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					type0Automaton1Layout
						.createParallelGroup(Alignment.BASELINE)
						.addComponent(type0Automaton1AlphabetJLabel, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Automaton1AlphabetMinJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Automaton1AlphabetMaxJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Alphabet1JComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					type0Automaton1Layout
						.createParallelGroup(Alignment.BASELINE)
						.addComponent(type0Automaton1StatesJLabel, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Automaton1StatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Automaton1StatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Automaton1StatesJComboBox, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					type0Automaton1Layout
						.createParallelGroup(Alignment.BASELINE)
						.addComponent(type0Automaton1FinalStatesJLabel, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Automaton1FinalStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Automaton1FinalStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Automaton1TotalTransitionFunctionJCheckBox, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					type0Automaton1Layout
						.createParallelGroup(Alignment.BASELINE)
						.addComponent(type0Automaton1TransitionsJLabel, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Automaton1TransitionsMinJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Automaton1TransitionsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
	}

	private void setupType0Automaton2Layout()
	{

		setStandardJComponentSize(type0Automaton2MinJLabel, 40, 14);
		type0Automaton2MinJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		setStandardJComponentSize(type0Automaton2MaxJLabel, 40, 14);
		type0Automaton2MaxJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// Alphabet min and max setting

		type0Automaton2StatesMinJSpinner.setValue(2);
		setStandardJComponentSize(type0Automaton2StatesMinJSpinner, 80, 25);

		type0Automaton2StatesMaxJSpinner.setValue(4);
		setStandardJComponentSize(type0Automaton2StatesMaxJSpinner, 80, 25);

		// States min and max setting
		type0Automaton2FinalStatesMinJSpinner.setValue(1);
		setStandardJComponentSize(type0Automaton2FinalStatesMinJSpinner, 80, 25);

		type0Automaton2FinalStatesMaxJSpinner.setValue(2);
		setStandardJComponentSize(type0Automaton2FinalStatesMaxJSpinner, 80, 25);

		// Transitions min and max setting
		type0Automaton2TransitionsMinJSpinner.setValue(2);
		setStandardJComponentSize(type0Automaton2TransitionsMinJSpinner, 80, 25);

		type0Automaton2TransitionsMaxJSpinner.setValue(6);
		setStandardJComponentSize(type0Automaton2TransitionsMaxJSpinner, 80, 25);
		//
		// type0Alphabet2JComboBox.setModel(new DefaultComboBoxModel(new String[] { " a, b, c...", " A, B, C...",
		// " 1, 2, 3...", " x, y, z...", " X, Y, Z...", " t0, t1, t2...", " p0, p1, p2...", " I, II, III, IV...",
		// " i, ii, iii, iv...", " 1, 10, 11, 100..." }));
		// // type0Automaton2JPanel.add(type0AlphabetJComboBox);
		// setStandardJComponentSize(type0Alphabet2JComboBox, 148, 25);

		type0Automaton2StatesJComboBox.setModel(new DefaultComboBoxModel(new String[] { " A, B, C...", " 1, 2, 3...",
			" X, Y, Z...", " q0, q1, q2...", " s0, s1, s2..." }));
		type0Automaton2StatesJComboBox.setSelectedIndex(4);
		// type0Automaton2JPanel.add(type0Automaton2StatesJComboBox);
		setStandardJComponentSize(type0Automaton2StatesJComboBox, 148, 25);

        criteriaChecker.addErrorConstraint(type0Automaton2AlphabetMinJSpinner, 0, true, null, true);
        criteriaChecker.addErrorConstraint(type0Automaton2StatesMinJSpinner, 1, true, null, true);
        criteriaChecker.addErrorConstraint(type0Automaton2FinalStatesMinJSpinner, 0, true, null, true);
        criteriaChecker.addErrorConstraint(type0Automaton2TransitionsMinJSpinner, 0, true, null, true);

		type0Automaton2TotalTransitionFunctionJCheckBox.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (type0Automaton2TotalTransitionFunctionJCheckBox.isSelected())
				{
					type0Automaton2TransitionsMinJSpinner.setEnabled(false);
					type0Automaton2TransitionsMaxJSpinner.setEnabled(false);
				}
				else
				{
					type0Automaton2TransitionsMinJSpinner.setEnabled(true);
					type0Automaton2TransitionsMaxJSpinner.setEnabled(true);
				}

			}
		});

		// type0Automaton2JPanel.add(type0Automaton2TotalTransitionFunctionJCheckBox);

		GroupLayout type0Automaton2Layout = new GroupLayout(type0Automaton2JPanel);
		type0Automaton2JPanel.setLayout(type0Automaton2Layout);
		type0Automaton2Layout.setHorizontalGroup(type0Automaton2Layout.createParallelGroup(Alignment.LEADING)
			.addGroup(
				Alignment.TRAILING,
				type0Automaton2Layout
					.createSequentialGroup()
					.addContainerGap()
					.addGroup(
						type0Automaton2Layout
							.createParallelGroup(Alignment.LEADING)
							.addComponent(type0Automaton2StatesJLabel, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
							.addComponent(type0Automaton2FinalStatesJLabel, GroupLayout.DEFAULT_SIZE, 119,
								Short.MAX_VALUE)
							.addComponent(type0Automaton2TransitionsJLabel, GroupLayout.DEFAULT_SIZE, 119,
								Short.MAX_VALUE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(
						type0Automaton2Layout
							.createParallelGroup(Alignment.LEADING, false)
							.addGroup(
								type0Automaton2Layout
									.createSequentialGroup()
									.addComponent(type0Automaton2MinJLabel, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(type0Automaton2MaxJLabel, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE))
							.addGroup(
								type0Automaton2Layout
									.createSequentialGroup()
									.addComponent(type0Automaton2StatesMinJSpinner, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(type0Automaton2StatesMaxJSpinner, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(type0Automaton2StatesJComboBox, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGroup(
								type0Automaton2Layout
									.createSequentialGroup()
									.addComponent(type0Automaton2FinalStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
										65, GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(type0Automaton2FinalStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
										65, GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(type0Automaton2TotalTransitionFunctionJCheckBox,
										GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
							.addGroup(
								type0Automaton2Layout
									.createSequentialGroup()
									.addComponent(type0Automaton2TransitionsMinJSpinner, GroupLayout.PREFERRED_SIZE,
										65, GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(type0Automaton2TransitionsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
										65, GroupLayout.PREFERRED_SIZE))).addContainerGap()));
		type0Automaton2Layout.setVerticalGroup(type0Automaton2Layout.createParallelGroup(Alignment.LEADING).addGroup(
			type0Automaton2Layout
				.createSequentialGroup()
				.addGroup(
					type0Automaton2Layout
						.createParallelGroup(Alignment.LEADING)
						.addComponent(type0Automaton2MinJLabel)
						.addGroup(
							type0Automaton2Layout
								.createSequentialGroup()
								.addGap(1, 1, 1)
								.addComponent(type0Automaton2MaxJLabel, GroupLayout.PREFERRED_SIZE,
									GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
				.addGap(6, 6, 6)
				.addGroup(
					type0Automaton2Layout
						.createParallelGroup(Alignment.CENTER)
						.addComponent(type0Automaton2StatesJLabel, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Automaton2StatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Automaton2StatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Automaton2StatesJComboBox, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					type0Automaton2Layout
						.createParallelGroup(Alignment.CENTER)
						.addComponent(type0Automaton2FinalStatesJLabel, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Automaton2FinalStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Automaton2FinalStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Automaton2TotalTransitionFunctionJCheckBox, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					type0Automaton2Layout
						.createParallelGroup(Alignment.CENTER)
						.addComponent(type0Automaton2TransitionsJLabel, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Automaton2TransitionsMinJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type0Automaton2TransitionsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				// .addGap(6, 6, 6)
				// .addGroup(
				// type0Automaton2Layout
				// .createParallelGroup(Alignment.CENTER)
				// .addComponent(type0Automaton2TotalTransitionFunctionJCheckBox,
				// GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
				// GroupLayout.PREFERRED_SIZE)
				// .addComponent(type0Alphabet2JComboBox, GroupLayout.PREFERRED_SIZE,
				// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				// .addComponent(type0Automaton2StatesJComboBox, GroupLayout.PREFERRED_SIZE,
				// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

	}

	private void setupType0OutputAutomatonLayout()
	{

		setStandardJComponentSize(type0OutputAutomatonMinJLabel, 40, 14);
		type0OutputAutomatonMinJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		setStandardJComponentSize(type0OutputAutomatonMaxJLabel, 40, 14);
		type0OutputAutomatonMaxJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// States min and max setting
		type0OutputAutomatonStatesMinJSpinner.setValue(2);

		setStandardJComponentSize(type0OutputAutomatonStatesMinJSpinner, 80, 25);

		type0OutputAutomatonStatesMaxJSpinner.setValue(5);
		setStandardJComponentSize(type0OutputAutomatonStatesMaxJSpinner, 80, 25);

		// Transitions min and max setting
		type0OutputAutomatonTransitionsMinJSpinner.setValue(2);
		setStandardJComponentSize(type0OutputAutomatonTransitionsMinJSpinner, 80, 25);

		type0OutputAutomatonTransitionsMaxJSpinner.setValue(6);
		setStandardJComponentSize(type0OutputAutomatonTransitionsMaxJSpinner, 80, 25);

		spinnersSup.defaultDisabled(type0OutputAutomatonStatesMinJSpinner);
		spinnersSup.defaultDisabled(type0OutputAutomatonStatesMaxJSpinner);
		spinnersSup.defaultDisabled(type0OutputAutomatonTransitionsMinJSpinner);
		spinnersSup.defaultDisabled(type0OutputAutomatonTransitionsMaxJSpinner);

		setStandardJComponentSize(type0OutputAutomatonMinOneFinalStateJCheckBox, 148, 25);

		criteriaChecker.addErrorConstraint(type0OutputAutomatonStatesMinJSpinner, 1, true, null);

		GroupLayout type0OutputAutomatonLayout = new GroupLayout(type0OutputAutomatonJPanel);
		type0OutputAutomatonJPanel.setLayout(type0OutputAutomatonLayout);
		type0OutputAutomatonLayout.setHorizontalGroup(type0OutputAutomatonLayout.createParallelGroup(Alignment.LEADING)
			.addGroup(
				Alignment.TRAILING,
				type0OutputAutomatonLayout
					.createSequentialGroup()
					.addContainerGap()
					.addGroup(
						type0OutputAutomatonLayout
							.createParallelGroup(Alignment.LEADING)
							.addComponent(type0OutputAutomatonStatesJLabel, GroupLayout.DEFAULT_SIZE, 100,
								Short.MAX_VALUE)
							.addComponent(type0OutputAutomatonTransitionsJLabel, GroupLayout.DEFAULT_SIZE, 100,
								Short.MAX_VALUE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(
						type0OutputAutomatonLayout
							.createParallelGroup(Alignment.LEADING, false)
							.addGroup(
								type0OutputAutomatonLayout
									.createSequentialGroup()
									.addComponent(type0OutputAutomatonMinJLabel, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(type0OutputAutomatonMaxJLabel, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE))
							.addGroup(
								type0OutputAutomatonLayout
									.createSequentialGroup()
									.addComponent(type0OutputAutomatonStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
										65, GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(type0OutputAutomatonStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
										65, GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(type0OutputAutomatonMinOneFinalStateJCheckBox,
										GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE))

							.addGroup(
								type0OutputAutomatonLayout
									.createSequentialGroup()
									.addComponent(type0OutputAutomatonTransitionsMinJSpinner,
										GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(type0OutputAutomatonTransitionsMaxJSpinner,
										GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE))).addGap(9, 9, 9)
					.addContainerGap()));
		type0OutputAutomatonLayout.setVerticalGroup(type0OutputAutomatonLayout.createParallelGroup(Alignment.LEADING)
			.addGroup(
				type0OutputAutomatonLayout
					.createSequentialGroup()
					.addGroup(
						type0OutputAutomatonLayout
							.createParallelGroup(Alignment.LEADING)
							.addComponent(type0OutputAutomatonMinJLabel)
							.addGroup(
								type0OutputAutomatonLayout
									.createSequentialGroup()
									.addGap(1, 1, 1)
									.addComponent(type0OutputAutomatonMaxJLabel, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(6, 6, 6)
					.addGroup(
						type0OutputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type0OutputAutomatonStatesJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type0OutputAutomatonStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type0OutputAutomatonStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type0OutputAutomatonMinOneFinalStateJCheckBox, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						type0OutputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type0OutputAutomatonTransitionsJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type0OutputAutomatonTransitionsMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type0OutputAutomatonTransitionsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
	}

	private void setupType1OperationLayout()
	{

		// setStandardJComponentSize(type1OperationFromJLabel,"Lucida Sans", 0, 12);

		setStandardJComponentSize(type1OperationFromJLabel, 90, 25);

		type1OperationFromJComboBox.setModel(new DefaultComboBoxModel(new String[] { "NFA with ε-transitions", "NFA",
			"DFA", "minimal DFA" }));
		type1OperationFromJComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				type1OperationFromJComboBoxActionPerformed(evt);
			}
		});
		setStandardJComponentSize(type1OperationFromJComboBox, 200, 25);

		setStandardJComponentSize(type1OperationToJLabel, 90, 25);

		type1OperationToJComboBox.setModel(new DefaultComboBoxModel(new String[] { "NFA", "DFA", "minimal DFA",
			"canonical DFA" }));
		setStandardJComponentSize(type1OperationToJComboBox, 200, 20);
		type1OperationToJComboBox.setName("Operations: to");
		type1OperationToJComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				type1OperationToJComboBoxActionPerformed(evt);
			}
		});

		setStandardJComponentSize(type1OperationToJComboBox, 200, 25);

		GroupLayout type1OperationLayout = new GroupLayout(type1OperationJPanel);
		type1OperationJPanel.setLayout(type1OperationLayout);
		type1OperationLayout.setHorizontalGroup(type1OperationLayout.createParallelGroup(Alignment.LEADING).addGroup(
			Alignment.TRAILING,
			type1OperationLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					type1OperationLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(type1OperationFromJLabel, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
						.addComponent(type1OperationToJLabel, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
				// .addGap(65, 65, 65)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					type1OperationLayout
						.createParallelGroup(Alignment.LEADING, false)
						.addComponent(type1OperationFromJComboBox, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(6, 6, 6)
						.addComponent(type1OperationToJComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)
                        ).addContainerGap()));
		type1OperationLayout.setVerticalGroup(type1OperationLayout.createParallelGroup(Alignment.LEADING).addGroup(
			type1OperationLayout
				.createSequentialGroup()
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(
					type1OperationLayout
						.createParallelGroup(Alignment.CENTER)
						.addComponent(type1OperationFromJLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)
						.addComponent(type1OperationFromJComboBox, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					type1OperationLayout
						.createParallelGroup(Alignment.CENTER)
						.addComponent(type1OperationToJLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)
						.addComponent(type1OperationToJComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		// type0Automaton2JPanel.setVisible(false);

	}

	private void setupType1InputAutomatonLayout()
	{

		setStandardJComponentSize(type1InputAutomatonMinJLabel, 40, 14);
		type1InputAutomatonMinJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		setStandardJComponentSize(type1InputAutomatonMaxJLabel, 40, 14);
		type1InputAutomatonMaxJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// Minimal values
		type1InputAutomatonStatesMinJSpinner.setValue(3);
		setStandardJComponentSize(type1InputAutomatonStatesMinJSpinner, 148, 25);

		type1InputAutomatonFinalStatesMinJSpinner.setValue(1);
		setStandardJComponentSize(type1InputAutomatonFinalStatesMinJSpinner, 148, 25);

		type1InputAutomatonTransitionsMinJSpinner.setValue(5);
		setStandardJComponentSize(type1InputAutomatonTransitionsMinJSpinner, 148, 25);

		type1InputAutomatonEpsTransitionsMinJSpinner.setValue(2);
		setStandardJComponentSize(type1InputAutomatonEpsTransitionsMinJSpinner, 148, 25);

		type1InputAutomatonAlphabetMinJSpinner.setValue(3);
		setStandardJComponentSize(type1InputAutomatonAlphabetMinJSpinner, 148, 25);

		setStandardJComponentSize(type1InputAutomatonUnreachableStatesMinJSpinner, 148, 25);

		// Maximal values
		type1InputAutomatonStatesMaxJSpinner.setValue(5);
		setStandardJComponentSize(type1InputAutomatonStatesMaxJSpinner, 148, 25);

		type1InputAutomatonFinalStatesMaxJSpinner.setValue(3);
		setStandardJComponentSize(type1InputAutomatonFinalStatesMaxJSpinner, 148, 25);

		type1InputAutomatonTransitionsMaxJSpinner.setValue(8);
		setStandardJComponentSize(type1InputAutomatonTransitionsMaxJSpinner, 148, 25);

		type1InputAutomatonEpsTransitionsMaxJSpinner.setValue(6);
		setStandardJComponentSize(type1InputAutomatonEpsTransitionsMaxJSpinner, 148, 25);

		type1InputAutomatonAlphabetMaxJSpinner.setValue(3);
		setStandardJComponentSize(type1InputAutomatonAlphabetMaxJSpinner, 148, 25);

		spinnersSup.defaultDisabled(type1InputAutomatonUnreachableStatesMaxJSpinner);
		setStandardJComponentSize(type1InputAutomatonUnreachableStatesMaxJSpinner, 148, 25);
		spinnersSup.defaultDisabled(type1InputAutomatonUnreachableStatesMinJSpinner);
		setStandardJComponentSize(type1InputAutomatonUnreachableStatesMinJSpinner, 148, 25);

		// Comboboxes alphabet, states
		type1InputAutomatonAlphabetJComboBox.setModel(new DefaultComboBoxModel(new String[] { " a, b, c...",
			" A, B, C...", " 1, 2, 3...", " x, y, z...", " X, Y, Z..." }));
		setStandardJComponentSize(type1InputAutomatonAlphabetJComboBox, 148, 25);

		type1InputAutomatonStatesJComboBox.setModel(new DefaultComboBoxModel(new String[] { " A, B, C...",
			" 1, 2, 3...", " X, Y, Z...", " q0, q1, q2...", " s0 , s1 , s2..." }));
		type1InputAutomatonStatesJComboBox.setSelectedIndex(4);
		setStandardJComponentSize(type1InputAutomatonStatesJComboBox, 148, 25);

		setStandardJComponentSize(type1InputAutomatonFirstStateJCheckBox, 148, 25);

		setStandardJComponentSize(type1InputAutomatonTotalTransitionFunctionJCheckBox, 148, 25);

        criteriaChecker.addErrorConstraint(type1InputAutomatonStatesMinJSpinner, 1, true, null, true);
        criteriaChecker.addErrorConstraint(type1InputAutomatonFinalStatesMinJSpinner, 0, true, null, true);
        criteriaChecker.addErrorConstraint(type1InputAutomatonAlphabetMinJSpinner, 0, true, null, true);
        criteriaChecker.addErrorConstraint(type1InputAutomatonTransitionsMinJSpinner, 0, true, null, true);
        criteriaChecker.addErrorConstraint(type1InputAutomatonEpsTransitionsMinJSpinner, 1, true, null, true);

		type1InputAutomatonTotalTransitionFunctionJCheckBox.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (type1InputAutomatonTotalTransitionFunctionJCheckBox.isSelected())
				{
					type1InputAutomatonTransitionsMaxJSpinner.setEnabled(false);
					type1InputAutomatonTransitionsMinJSpinner.setEnabled(false);
					type1InputAutomatonStatesMinJSpinner.setValue((Integer) type1InputAutomatonStatesMinJSpinner
						.getValue() + 1);
					type1InputAutomatonStatesMaxJSpinner.setValue((Integer) type1InputAutomatonStatesMaxJSpinner
						.getValue() + 1);
				}
				else
				{
					type1InputAutomatonTransitionsMaxJSpinner.setEnabled(true);
					type1InputAutomatonTransitionsMinJSpinner.setEnabled(true);
					type1InputAutomatonStatesMinJSpinner.setValue((Integer) type1InputAutomatonStatesMinJSpinner
						.getValue() - 1);
					type1InputAutomatonStatesMaxJSpinner.setValue((Integer) type1InputAutomatonStatesMaxJSpinner
						.getValue() - 1);
				}

			}
		});

		GroupLayout type1InputAutomatonLayout = new GroupLayout(type1InputAutomatonJPanel);
		type1InputAutomatonJPanel.setLayout(type1InputAutomatonLayout);
		type1InputAutomatonLayout
			.setHorizontalGroup(type1InputAutomatonLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(
					Alignment.TRAILING,
					type1InputAutomatonLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
							type1InputAutomatonLayout
								.createParallelGroup(Alignment.LEADING)
								.addComponent(type1InputAutomatonAlphabetJLabel, GroupLayout.DEFAULT_SIZE, 119,
									Short.MAX_VALUE)
								.addComponent(type1InputAutomatonStatesJLabel, GroupLayout.DEFAULT_SIZE, 119,
									Short.MAX_VALUE)
								.addComponent(type1InputAutomatonUnreachableStatesJLabel, GroupLayout.DEFAULT_SIZE,
									119, Short.MAX_VALUE)
								.addComponent(type1InputAutomatonFinalStatesJLabel, GroupLayout.DEFAULT_SIZE, 119,
									Short.MAX_VALUE)
								.addComponent(type1InputAutomatonTransitionsJLabel, GroupLayout.DEFAULT_SIZE, 119,
									Short.MAX_VALUE)
								.addComponent(type1InputAutomatonEpsTransitionsJLabel, GroupLayout.DEFAULT_SIZE, 119,
									Short.MAX_VALUE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
							type1InputAutomatonLayout
								.createParallelGroup(Alignment.LEADING, false)
								.addGroup(
									type1InputAutomatonLayout
										.createSequentialGroup()
										.addComponent(type1InputAutomatonMinJLabel, GroupLayout.PREFERRED_SIZE, 65,
											GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(type1InputAutomatonMaxJLabel, GroupLayout.PREFERRED_SIZE, 65,
											GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type1InputAutomatonLayout
										.createSequentialGroup()
										.addComponent(type1InputAutomatonAlphabetMinJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type1InputAutomatonAlphabetMaxJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type1InputAutomatonAlphabetJComboBox, GroupLayout.PREFERRED_SIZE,
											GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type1InputAutomatonLayout
										.createSequentialGroup()
										.addComponent(type1InputAutomatonStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
											65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type1InputAutomatonStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
											65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type1InputAutomatonStatesJComboBox, GroupLayout.PREFERRED_SIZE,
											GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type1InputAutomatonLayout
										.createSequentialGroup()
										.addComponent(type1InputAutomatonUnreachableStatesMinJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type1InputAutomatonUnreachableStatesMaxJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type1InputAutomatonFirstStateJCheckBox,
											GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
											GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type1InputAutomatonLayout
										.createSequentialGroup()
										.addComponent(type1InputAutomatonFinalStatesMinJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type1InputAutomatonFinalStatesMaxJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type1InputAutomatonTotalTransitionFunctionJCheckBox,
											GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
											GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type1InputAutomatonLayout
										.createSequentialGroup()
										.addComponent(type1InputAutomatonTransitionsMinJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type1InputAutomatonTransitionsMaxJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type1InputAutomatonLayout
										.createSequentialGroup()
										.addComponent(type1InputAutomatonEpsTransitionsMinJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type1InputAutomatonEpsTransitionsMaxJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)))
						.addContainerGap()));
		type1InputAutomatonLayout.setVerticalGroup(type1InputAutomatonLayout.createParallelGroup(Alignment.LEADING)
			.addGroup(
				type1InputAutomatonLayout
					.createSequentialGroup()
					.addGroup(
						type1InputAutomatonLayout
							.createParallelGroup(Alignment.LEADING)
							.addComponent(type1InputAutomatonMinJLabel)
							.addGroup(
								type1InputAutomatonLayout
									.createSequentialGroup()
									.addGap(1, 1, 1)
									.addComponent(type1InputAutomatonMaxJLabel, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(6, 6, 6)
					.addGroup(
						type1InputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type1InputAutomatonAlphabetJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1InputAutomatonAlphabetMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1InputAutomatonAlphabetMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1InputAutomatonAlphabetJComboBox, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						type1InputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type1InputAutomatonStatesJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1InputAutomatonStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1InputAutomatonStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1InputAutomatonStatesJComboBox, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						type1InputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type1InputAutomatonUnreachableStatesJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1InputAutomatonUnreachableStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1InputAutomatonUnreachableStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1InputAutomatonFirstStateJCheckBox, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						type1InputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type1InputAutomatonFinalStatesJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1InputAutomatonFinalStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1InputAutomatonFinalStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1InputAutomatonTotalTransitionFunctionJCheckBox,
								GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						type1InputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type1InputAutomatonTransitionsJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1InputAutomatonTransitionsMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1InputAutomatonTransitionsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						type1InputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type1InputAutomatonEpsTransitionsJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1InputAutomatonEpsTransitionsMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1InputAutomatonEpsTransitionsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

	}

	private void setupType1OutputAutomatonLayout()
	{

		setStandardJComponentSize(type1OutputAutomatonMinJLabel, 40, 14);
		type1OutputAutomatonMinJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		setStandardJComponentSize(type1OutputAutomatonMaxJLabel, 40, 14);
		type1OutputAutomatonMaxJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// Minimal values
		spinnersSup.defaultDisabled(type1OutputAutomatonStatesMinJSpinner);
		setStandardJComponentSize(type1OutputAutomatonStatesMinJSpinner, 148, 25);
		type1OutputAutomatonStatesMinJSpinner.setValue(2);

		spinnersSup.defaultDisabled(type1OutputAutomatonTransitionsMinJSpinner);
		setStandardJComponentSize(type1OutputAutomatonTransitionsMinJSpinner, 148, 25);

		// Maximal values
		spinnersSup.defaultDisabled(type1OutputAutomatonStatesMaxJSpinner);
		setStandardJComponentSize(type1OutputAutomatonStatesMaxJSpinner, 148, 25);
		type1OutputAutomatonStatesMaxJSpinner.setValue(5);

		spinnersSup.defaultDisabled(type1OutputAutomatonTransitionsMaxJSpinner);
		setStandardJComponentSize(type1OutputAutomatonTransitionsMaxJSpinner, 148, 25);

		criteriaChecker.addErrorConstraint(type1OutputAutomatonStatesMinJSpinner, 1, true, null);

		GroupLayout type1OutputAutomatonLayout = new GroupLayout(type1OutputAutomatonJPanel);
		type1OutputAutomatonJPanel.setLayout(type1OutputAutomatonLayout);
		type1OutputAutomatonLayout.setHorizontalGroup(type1OutputAutomatonLayout.createParallelGroup(Alignment.LEADING)
			.addGroup(
				Alignment.TRAILING,
				type1OutputAutomatonLayout
					.createSequentialGroup()
					.addContainerGap()
					.addGroup(
						type1OutputAutomatonLayout
							.createParallelGroup(Alignment.LEADING)
							.addComponent(type1OutputAutomatonStatesJLabel, GroupLayout.DEFAULT_SIZE, 100,
								Short.MAX_VALUE)
							.addComponent(type1OutputAutomatonTransitionsJLabel, GroupLayout.DEFAULT_SIZE, 100,
								Short.MAX_VALUE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(
						type1OutputAutomatonLayout
							.createParallelGroup(Alignment.LEADING, false)
							.addGroup(
								type1OutputAutomatonLayout
									.createSequentialGroup()
									.addComponent(type1OutputAutomatonMinJLabel, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(type1OutputAutomatonMaxJLabel, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE))
							.addGroup(
								type1OutputAutomatonLayout
									.createSequentialGroup()
									.addComponent(type1OutputAutomatonStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
										65, GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(type1OutputAutomatonStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
										65, GroupLayout.PREFERRED_SIZE))
							.addGroup(
								type1OutputAutomatonLayout
									.createSequentialGroup()
									.addComponent(type1OutputAutomatonTransitionsMinJSpinner,
										GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(type1OutputAutomatonTransitionsMaxJSpinner,
										GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)))
					.addGap(164, 164, 164).addContainerGap()));
		type1OutputAutomatonLayout.setVerticalGroup(type1OutputAutomatonLayout.createParallelGroup(Alignment.LEADING)
			.addGroup(
				type1OutputAutomatonLayout
					.createSequentialGroup()
					.addGroup(
						type1OutputAutomatonLayout
							.createParallelGroup(Alignment.LEADING)
							.addComponent(type1OutputAutomatonMinJLabel)
							.addGroup(
								type1OutputAutomatonLayout
									.createSequentialGroup()
									.addGap(1, 1, 1)
									.addComponent(type1OutputAutomatonMaxJLabel, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(6, 6, 6)
					.addGroup(
						type1OutputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type1OutputAutomatonStatesJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1OutputAutomatonStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1OutputAutomatonStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						type1OutputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type1OutputAutomatonTransitionsJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1OutputAutomatonTransitionsMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1OutputAutomatonTransitionsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		type1OutputAutomatonStatesJLabel.setEnabled(false);
		type1OutputAutomatonStatesMinJSpinner.setEnabled(false);
		type1OutputAutomatonStatesMaxJSpinner.setEnabled(false);
	}

	private void setupType1MinimalizationLayout()
	{

		setStandardJComponentSize(type1MinimalizationMinJLabel, 40, 14);
		type1MinimalizationMinJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		setStandardJComponentSize(type1MinimalizationMaxJLabel, 40, 14);
		type1MinimalizationMaxJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// Minimal values
		spinnersSup.defaultDisabled(type1MinimalizationStepsMinJSpinner);
		setStandardJComponentSize(type1MinimalizationStepsMinJSpinner, 148, 25);

		// Maximal values
		spinnersSup.defaultDisabled(type1MinimalizationStepsMaxJSpinner);
		setStandardJComponentSize(type1MinimalizationStepsMaxJSpinner, 148, 25);

		setStandardJComponentSize(type1MinimalizationStepsJCheckBox, 148, 25);

        criteriaChecker.addErrorConstraint(type1MinimalizationStepsMinJSpinner, 1, true, null);

		GroupLayout type1MinimalizationLayout = new GroupLayout(type1MinimalizationJPanel);
		type1MinimalizationJPanel.setLayout(type1MinimalizationLayout);
		type1MinimalizationLayout.setHorizontalGroup(type1MinimalizationLayout.createParallelGroup(Alignment.LEADING)
			.addGroup(
				Alignment.TRAILING,
				type1MinimalizationLayout
					.createSequentialGroup()
					.addContainerGap()
					.addGroup(
						type1MinimalizationLayout.createParallelGroup(Alignment.LEADING).addComponent(
							type1MinimalizationStepsJLabel, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(
						type1MinimalizationLayout
							.createParallelGroup(Alignment.LEADING, false)
							.addGroup(
								type1MinimalizationLayout
									.createSequentialGroup()
									.addComponent(type1MinimalizationMinJLabel, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(type1MinimalizationMaxJLabel, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE))
							.addGroup(
								type1MinimalizationLayout
									.createSequentialGroup()
									.addComponent(type1MinimalizationStepsMinJSpinner, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(type1MinimalizationStepsMaxJSpinner, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(type1MinimalizationStepsJCheckBox, GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))).addGap(9, 9, 9)
					.addContainerGap()));
		type1MinimalizationLayout.setVerticalGroup(type1MinimalizationLayout.createParallelGroup(Alignment.LEADING)
			.addGroup(
				type1MinimalizationLayout
					.createSequentialGroup()
					.addGroup(
						type1MinimalizationLayout
							.createParallelGroup(Alignment.LEADING)
							.addComponent(type1MinimalizationMinJLabel)
							.addGroup(
								type1MinimalizationLayout
									.createSequentialGroup()
									.addGap(1, 1, 1)
									.addComponent(type1MinimalizationMaxJLabel, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(6, 6, 6)
					.addGroup(
						type1MinimalizationLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type1MinimalizationStepsJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1MinimalizationStepsMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1MinimalizationStepsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type1MinimalizationStepsJCheckBox, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

	}

    private void setupType1IsomorphismTestLayout()
    {
        // Setup layout
        setStandardJComponentSize(type1IsoPercentJLabel, 269, 25);
        setStandardJComponentSize(type1IsoPercentJSpinner, 148, 25);
		criteriaChecker.addErrorConstraint(type1IsoPercentJSpinner, 100, false, null);
        GroupLayout type1IsomorphismLayout = new GroupLayout(type1IsomorphismJPanel);
        type1IsomorphismJPanel.setLayout(type1IsomorphismLayout);

        type1IsomorphismLayout.setHorizontalGroup(type1IsomorphismLayout.createParallelGroup(Alignment.LEADING).addGroup(
                Alignment.TRAILING,
                type1IsomorphismLayout
                        .createParallelGroup()
                        .addGroup(
                                type1IsomorphismLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(type1IsomorphismJCheckBox, GroupLayout.DEFAULT_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                        )
                        .addGroup(
                                type1IsomorphismLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(type1IsomorphismLayout.createSequentialGroup()
                                        .addComponent(type1IsoPercentJLabel, GroupLayout.DEFAULT_SIZE, 125, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(type1IsoPercentJSpinner, GroupLayout.PREFERRED_SIZE, 65,
                                                GroupLayout.PREFERRED_SIZE))
                        )));
        type1IsomorphismLayout.setVerticalGroup(type1IsomorphismLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(
                        type1IsomorphismLayout
                                .createSequentialGroup()
                                .addContainerGap()
                                .addComponent(type1IsomorphismJCheckBox, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addGroup(
                                        type1IsomorphismLayout
                                                .createParallelGroup(Alignment.LEADING)
                                                .addComponent(type1IsoPercentJLabel, GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(type1IsoPercentJSpinner, GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

    }

	private void setupType2OperationLayout()
	{

		type2OperationButtonGroup.add(type2OperationGrammarToNFAJButton);
		type2OperationButtonGroup.add(type2OperationNFAToGrammarJButton);
		// type2OperationButtonGroup.setSelected((ButtonModel) type2OperationGrammarToNFAJButton,true);
		type2OperationGrammarToNFAJButton.setSelected(true);

		type2OperationJPanel.setLayout(new BoxLayout(type2OperationJPanel, BoxLayout.Y_AXIS));
		type2OperationGrammarToNFAJButton.setMargin(new Insets(5, 10, 5, 0));
		type2OperationNFAToGrammarJButton.setMargin(new Insets(0, 10, 5, 0));
		type2OperationJPanel.add(type2OperationGrammarToNFAJButton);
		type2OperationJPanel.add(type2OperationNFAToGrammarJButton);

		type2OperationNFAToGrammarJButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				type2OperationJRadioButtonActionPerformed(e);
			}
		});

		type2OperationGrammarToNFAJButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				type2OperationJRadioButtonActionPerformed(e);
			}
		});
	}

	private void setupType2InputGrammarLayout()
	{

		setStandardJComponentSize(type2InputGrammarMinJLabel, 40, 14);
		type2InputGrammarMinJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		setStandardJComponentSize(type2InputGrammarMaxJLabel, 40, 14);
		type2InputGrammarMaxJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// Minimal values

		type2InputGrammarLoopsMinJSpinner.setValue(2);
		setStandardJComponentSize(type2InputGrammarLoopsMinJSpinner, 148, 25);

		type2InputGrammarTerminalsMinJSpinner.setValue(3);
		setStandardJComponentSize(type2InputGrammarTerminalsMinJSpinner, 148, 25);

		type2InputGrammarVariablesMinJSpinner.setValue(3);
		setStandardJComponentSize(type2InputGrammarVariablesMinJSpinner, 148, 25);

		type2InputGrammarRulesMinJSpinner.setValue(5);
		setStandardJComponentSize(type2InputGrammarRulesMinJSpinner, 148, 25);

		// Maximal values

		type2InputGrammarLoopsMaxJSpinner.setValue(5);
		setStandardJComponentSize(type2InputGrammarLoopsMaxJSpinner, 148, 25);

		type2InputGrammarTerminalsMaxJSpinner.setValue(3);
		setStandardJComponentSize(type2InputGrammarTerminalsMaxJSpinner, 148, 25);

		type2InputGrammarVariablesMaxJSpinner.setValue(4);
		setStandardJComponentSize(type2InputGrammarVariablesMaxJSpinner, 148, 25);

		type2InputGrammarRulesMaxJSpinner.setValue(10);
		setStandardJComponentSize(type2InputGrammarRulesMaxJSpinner, 148, 25);

		// Comboboxes and additional options

		setStandardJComponentSize(type2InputGrammarEpsilonJCheckBox, 148, 25);

		type2InputGrammarTerminalsJComboBox
			.setModel(new DefaultComboBoxModel(new String[] { " a, b, c...", " 1, 2, 3...", " x, y, z...",
				" t0, t1, t2", " p0, p1, p2...", " i, ii, iii, iv...", " 1, 10, 11, 100.." }));
		type2InputGrammarTerminalsJComboBox.setSelectedIndex(2);
		setStandardJComponentSize(type2InputGrammarTerminalsJComboBox, 148, 25);

		type2InputGrammarVariablesJComboBox.setModel(new DefaultComboBoxModel(new String[] { " A, B, C...",
			" X, Y, Z...", " q0, q1, q2...", " s0, s1, s2...", " I, II, III, IV..." }));
		setStandardJComponentSize(type2InputGrammarVariablesJComboBox, 148, 25);

        criteriaChecker.addErrorConstraint(type2InputGrammarVariablesMinJSpinner, 1, true, null, true);
        criteriaChecker.addErrorConstraint(type2InputGrammarRulesMinJSpinner, 1, true, null, true);
        criteriaChecker.addErrorConstraint(type2InputGrammarTerminalsMinJSpinner, 1, true, null, true);
        criteriaChecker.addErrorConstraint(type2InputGrammarLoopsMinJSpinner, 1, true, null, true);

		// Setup layout

		GroupLayout type2InputGrammarLayout = new GroupLayout(type2InputGrammarJPanel);
		type2InputGrammarJPanel.setLayout(type2InputGrammarLayout);
		type2InputGrammarLayout
			.setHorizontalGroup(type2InputGrammarLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(
					Alignment.TRAILING,
					type2InputGrammarLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
							type2InputGrammarLayout
								.createParallelGroup(Alignment.LEADING)
								.addComponent(type2InputGrammarTerminalsJLabel, GroupLayout.DEFAULT_SIZE, 119,
									Short.MAX_VALUE)
								.addComponent(type2InputGrammarNonTerminalsJLabel, GroupLayout.DEFAULT_SIZE, 119,
									Short.MAX_VALUE)
								.addComponent(type2InputGrammarRulesJLabel, GroupLayout.DEFAULT_SIZE, 119,
									Short.MAX_VALUE)
								.addComponent(type2InputGrammarLoopsJLabel, GroupLayout.DEFAULT_SIZE, 119,
									Short.MAX_VALUE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
							type2InputGrammarLayout
								.createParallelGroup(Alignment.LEADING, false)
								.addGroup(
									type2InputGrammarLayout
										.createSequentialGroup()
										.addComponent(type2InputGrammarMinJLabel, GroupLayout.PREFERRED_SIZE, 65,
											GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(type2InputGrammarMaxJLabel, GroupLayout.PREFERRED_SIZE, 65,
											GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type2InputGrammarLayout
										.createSequentialGroup()
										.addComponent(type2InputGrammarTerminalsMinJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type2InputGrammarTerminalsMaxJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type2InputGrammarTerminalsJComboBox, GroupLayout.PREFERRED_SIZE,
											GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type2InputGrammarLayout
										.createSequentialGroup()
										.addComponent(type2InputGrammarVariablesMinJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type2InputGrammarVariablesMaxJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type2InputGrammarVariablesJComboBox, GroupLayout.PREFERRED_SIZE,
											GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type2InputGrammarLayout
										.createSequentialGroup()
										.addComponent(type2InputGrammarRulesMinJSpinner, GroupLayout.PREFERRED_SIZE,
											65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type2InputGrammarRulesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
											65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type2InputGrammarEpsilonJCheckBox, GroupLayout.PREFERRED_SIZE,
											GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type2InputGrammarLayout
										.createSequentialGroup()
										.addComponent(type2InputGrammarLoopsMinJSpinner, GroupLayout.PREFERRED_SIZE,
											65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type2InputGrammarLoopsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
											65, GroupLayout.PREFERRED_SIZE))).addContainerGap()));
		type2InputGrammarLayout.setVerticalGroup(type2InputGrammarLayout.createParallelGroup(Alignment.LEADING)
			.addGroup(
				type2InputGrammarLayout
					.createSequentialGroup()
					.addGroup(
						type2InputGrammarLayout
							.createParallelGroup(Alignment.LEADING)
							.addComponent(type2InputGrammarMinJLabel)
							.addGroup(
								type2InputGrammarLayout
									.createSequentialGroup()
									.addGap(1, 1, 1)
									.addComponent(type2InputGrammarMaxJLabel, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(6, 6, 6)
					.addGroup(
						type2InputGrammarLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type2InputGrammarTerminalsJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputGrammarTerminalsMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputGrammarTerminalsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputGrammarTerminalsJComboBox, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						type2InputGrammarLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type2InputGrammarNonTerminalsJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputGrammarVariablesMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputGrammarVariablesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputGrammarVariablesJComboBox, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						type2InputGrammarLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type2InputGrammarRulesJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputGrammarRulesMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputGrammarRulesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputGrammarEpsilonJCheckBox, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						type2InputGrammarLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type2InputGrammarLoopsJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputGrammarLoopsMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputGrammarLoopsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))

					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

	}

    private void setupType2IsomorphismTestLayout()
    {
        // Setup layout
        setStandardJComponentSize(type2IsoPercentJLabel, 269, 25);
        setStandardJComponentSize(type2IsoPercentJSpinner, 148, 25);
		criteriaChecker.addErrorConstraint(type2IsoPercentJSpinner, 100, false, null);
        GroupLayout type2IsomorphismLayout = new GroupLayout(type2IsomorphismJPanel);
        type2IsomorphismJPanel.setLayout(type2IsomorphismLayout);

        type2IsomorphismLayout.setHorizontalGroup(type2IsomorphismLayout.createParallelGroup(Alignment.LEADING).addGroup(
                Alignment.TRAILING,
                type2IsomorphismLayout
                        .createParallelGroup()
                        .addGroup(
                                type2IsomorphismLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(type2IsomorphismJCheckBox, GroupLayout.DEFAULT_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                        )
                        .addGroup(
                                type2IsomorphismLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(type2IsomorphismLayout.createSequentialGroup()
                                                .addComponent(type2IsoPercentJLabel, GroupLayout.DEFAULT_SIZE, 125, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(type2IsoPercentJSpinner, GroupLayout.PREFERRED_SIZE, 65,
                                                        GroupLayout.PREFERRED_SIZE))
                        )));
        type2IsomorphismLayout.setVerticalGroup(type2IsomorphismLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(
                        type2IsomorphismLayout
                                .createSequentialGroup()
                                .addContainerGap()
                                .addComponent(type2IsomorphismJCheckBox, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addGroup(
                                        type2IsomorphismLayout
                                                .createParallelGroup(Alignment.LEADING)
                                                .addComponent(type2IsoPercentJLabel, GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(type2IsoPercentJSpinner, GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    }

	// private void setupType2OutputAutomatonLayout()
	// {
	//
	// setStandardJComponentSize(type2OutputAutomatonMinJLabel, 40, 14);
	// type2OutputAutomatonMinJLabel.setHorizontalAlignment(SwingConstants.CENTER);
	//
	// setStandardJComponentSize(type2OutputAutomatonMaxJLabel, 40, 14);
	// type2OutputAutomatonMaxJLabel.setHorizontalAlignment(SwingConstants.CENTER);
	//
	// setStandardJComponentSize(type2OutputAutomatonAlphabetMinJSpinner, 148, 25);
	// setStandardJComponentSize(type2OutputAutomatonAlphabetMaxJSpinner, 148, 25);
	// setStandardJComponentSize(type2OutputAutomatonStatesMinJSpinner, 148, 25);
	// setStandardJComponentSize(type2OutputAutomatonStatesMaxJSpinner, 148, 25);
	// setStandardJComponentSize(type2OutputAutomatonTransitionsMinJSpinner, 148, 25);
	// setStandardJComponentSize(type2OutputAutomatonTransitionsMaxJSpinner, 148, 25);
	// setStandardJComponentSize(type2OutputAutomatonUnreachableStatesMinJSpinner, 148, 25);
	// setStandardJComponentSize(type2OutputAutomatonUnreachableStatesMaxJSpinner, 148, 25);
	//
	// spinnersSup.defaultDisabled(type2OutputAutomatonAlphabetMinJSpinner);
	// spinnersSup.defaultDisabled(type2OutputAutomatonAlphabetMaxJSpinner);
	// spinnersSup.defaultDisabled(type2OutputAutomatonStatesMinJSpinner);
	// spinnersSup.defaultDisabled(type2OutputAutomatonStatesMaxJSpinner);
	// spinnersSup.defaultDisabled(type2OutputAutomatonTransitionsMinJSpinner);
	// spinnersSup.defaultDisabled(type2OutputAutomatonTransitionsMaxJSpinner);
	// spinnersSup.defaultDisabled(type2OutputAutomatonUnreachableStatesMinJSpinner);
	// spinnersSup.defaultDisabled(type2OutputAutomatonUnreachableStatesMaxJSpinner);
	//
	// GroupLayout type2OutputAutomatonLayout = new GroupLayout(type2OutputAutomatonJPanel);
	// type2OutputAutomatonJPanel.setLayout(type2OutputAutomatonLayout);
	// type2OutputAutomatonLayout.setHorizontalGroup(type2OutputAutomatonLayout.createParallelGroup(Alignment.LEADING)
	// .addGroup(
	// Alignment.TRAILING,
	// type2OutputAutomatonLayout
	// .createSequentialGroup()
	// .addContainerGap()
	// .addGroup(
	// type2OutputAutomatonLayout
	// .createParallelGroup(Alignment.LEADING)
	// .addComponent(type2OutputAutomatonAlphabetJLabel, GroupLayout.DEFAULT_SIZE, 100,
	// Short.MAX_VALUE)
	// .addComponent(type2OutputAutomatonStatesJLabel, GroupLayout.DEFAULT_SIZE, 100,
	// Short.MAX_VALUE)
	// .addComponent(type2OutputAutomatonTransitionsJLabel, GroupLayout.DEFAULT_SIZE, 100,
	// Short.MAX_VALUE)
	// .addComponent(type2OutputAutomatonUnreachableStatesJLabel, GroupLayout.DEFAULT_SIZE, 100,
	// Short.MAX_VALUE))
	// .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	// .addGroup(
	// type2OutputAutomatonLayout
	// .createParallelGroup(Alignment.LEADING, false)
	// .addGroup(
	// type2OutputAutomatonLayout
	// .createSequentialGroup()
	// .addComponent(type2OutputAutomatonMinJLabel, GroupLayout.PREFERRED_SIZE, 65,
	// GroupLayout.PREFERRED_SIZE)
	// .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	// .addComponent(type2OutputAutomatonMaxJLabel, GroupLayout.PREFERRED_SIZE, 65,
	// GroupLayout.PREFERRED_SIZE))
	// .addGroup(
	// type2OutputAutomatonLayout
	// .createSequentialGroup()
	// .addComponent(type2OutputAutomatonAlphabetMinJSpinner, GroupLayout.PREFERRED_SIZE,
	// 65, GroupLayout.PREFERRED_SIZE)
	// .addGap(6, 6, 6)
	// .addComponent(type2OutputAutomatonAlphabetMaxJSpinner, GroupLayout.PREFERRED_SIZE,
	// 65, GroupLayout.PREFERRED_SIZE))
	// .addGroup(
	// type2OutputAutomatonLayout
	// .createSequentialGroup()
	// .addComponent(type2OutputAutomatonStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
	// 65, GroupLayout.PREFERRED_SIZE)
	// .addGap(6, 6, 6)
	// .addComponent(type2OutputAutomatonStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
	// 65, GroupLayout.PREFERRED_SIZE))
	// .addGroup(
	// type2OutputAutomatonLayout
	// .createSequentialGroup()
	// .addComponent(type2OutputAutomatonTransitionsMinJSpinner,
	// GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
	// .addGap(6, 6, 6)
	// .addComponent(type2OutputAutomatonTransitionsMaxJSpinner,
	// GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE))
	// .addGroup(
	// type2OutputAutomatonLayout
	// .createSequentialGroup()
	// .addComponent(type2OutputAutomatonUnreachableStatesMinJSpinner,
	// GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
	// .addGap(6, 6, 6)
	// .addComponent(type2OutputAutomatonUnreachableStatesMaxJSpinner,
	// GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)))
	// .addGap(164, 164, 164).addContainerGap()));
	// type2OutputAutomatonLayout.setVerticalGroup(type2OutputAutomatonLayout.createParallelGroup(Alignment.LEADING)
	// .addGroup(
	// type2OutputAutomatonLayout
	// .createSequentialGroup()
	// .addGroup(
	// type2OutputAutomatonLayout
	// .createParallelGroup(Alignment.LEADING)
	// .addComponent(type2OutputAutomatonMinJLabel)
	// .addGroup(
	// type2OutputAutomatonLayout
	// .createSequentialGroup()
	// .addGap(1, 1, 1)
	// .addComponent(type2OutputAutomatonMaxJLabel, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
	// .addGap(6, 6, 6)
	// .addGroup(
	// type2OutputAutomatonLayout
	// .createParallelGroup(Alignment.CENTER)
	// .addComponent(type2OutputAutomatonAlphabetJLabel, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	// .addComponent(type2OutputAutomatonAlphabetMinJSpinner, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	// .addComponent(type2OutputAutomatonAlphabetMaxJSpinner, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
	// .addGap(6, 6, 6)
	// .addGroup(
	// type2OutputAutomatonLayout
	// .createParallelGroup(Alignment.CENTER)
	// .addComponent(type2OutputAutomatonStatesJLabel, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	// .addComponent(type2OutputAutomatonStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	// .addComponent(type2OutputAutomatonStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
	// .addGap(6, 6, 6)
	// .addGroup(
	// type2OutputAutomatonLayout
	// .createParallelGroup(Alignment.CENTER)
	// .addComponent(type2OutputAutomatonTransitionsJLabel, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	// .addComponent(type2OutputAutomatonTransitionsMinJSpinner, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	// .addComponent(type2OutputAutomatonTransitionsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
	// .addGap(6, 6, 6)
	// .addGroup(
	// type2OutputAutomatonLayout
	// .createParallelGroup(Alignment.CENTER)
	// .addComponent(type2OutputAutomatonUnreachableStatesJLabel, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	// .addComponent(type2OutputAutomatonUnreachableStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	// .addComponent(type2OutputAutomatonUnreachableStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
	// .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
	// }

	private void setupType2InputAutomatonLayout()
	{
		setStandardJComponentSize(type2InputAutomatonMinJLabel, 40, 14);
		type2InputAutomatonMinJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		setStandardJComponentSize(type2InputAutomatonMaxJLabel, 40, 14);
		type2InputAutomatonMaxJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		type2InputAutomatonAlphabetMinJSpinner.setValue(2);
		setStandardJComponentSize(type2InputAutomatonAlphabetMinJSpinner, 148, 25);
		type2InputAutomatonAlphabetMaxJSpinner.setValue(3);
		setStandardJComponentSize(type2InputAutomatonAlphabetMaxJSpinner, 148, 25);

		type2InputAutomatonStatesMinJSpinner.setValue(3);
		setStandardJComponentSize(type2InputAutomatonStatesMinJSpinner, 148, 25);
		type2InputAutomatonStatesMaxJSpinner.setValue(4);
		setStandardJComponentSize(type2InputAutomatonStatesMaxJSpinner, 148, 25);

		type2InputAutomatonFinalStatesMinJSpinner.setValue(1);
		setStandardJComponentSize(type2InputAutomatonFinalStatesMinJSpinner, 148, 25);
		type2InputAutomatonFinalStatesMaxJSpinner.setValue(2);
		setStandardJComponentSize(type2InputAutomatonFinalStatesMaxJSpinner, 148, 25);

		type2InputAutomatonTransitionsMinJSpinner.setValue(4);
		setStandardJComponentSize(type2InputAutomatonTransitionsMinJSpinner, 148, 25);
		type2InputAutomatonTransitionsMaxJSpinner.setValue(8);
		setStandardJComponentSize(type2InputAutomatonTransitionsMaxJSpinner, 148, 25);

		spinnersSup.defaultDisabled(type2InputAutomatonUnreachableStatesMinJSpinner);
		setStandardJComponentSize(type2InputAutomatonUnreachableStatesMinJSpinner, 148, 25);
		spinnersSup.defaultDisabled(type2InputAutomatonUnreachableStatesMaxJSpinner);
		setStandardJComponentSize(type2InputAutomatonUnreachableStatesMaxJSpinner, 148, 25);

		type2InputAutomatonAlphabetJComboBox.setModel(new DefaultComboBoxModel(new String[] { " a, b, c...",
			" A, B, C...", " 1, 2, 3...", " x, y, z ...", " X, Y, Z ...", " t0, t1, t2...", " p0, p1, p2..." }));
		setStandardJComponentSize(type2InputAutomatonAlphabetJComboBox, 148, 25);

		type2InputAutomatonStatesJComboBox.setModel(new DefaultComboBoxModel(new String[] { " A, B, C...",
			" 1, 2, 3...", " X, Y, Z ...", " q0, q1, q2...", " s0, s1, s2..." }));
		type2InputAutomatonStatesJComboBox.setSelectedIndex(4);
		setStandardJComponentSize(type2InputAutomatonStatesJComboBox, 148, 25);

		setStandardJComponentSize(type2InputAutomatonInitialStateFinal, 148, 25);

        criteriaChecker.addErrorConstraint(type2InputAutomatonStatesMinJSpinner, 1, true, null, true);
        criteriaChecker.addErrorConstraint(type2InputAutomatonAlphabetMinJSpinner, 0, true, null, true);
        criteriaChecker.addErrorConstraint(type2InputAutomatonFinalStatesMinJSpinner, 0, true, null, true);
        criteriaChecker.addErrorConstraint(type2InputAutomatonTransitionsMinJSpinner, 0, true, null, true);

		GroupLayout type2InputAutomatonLayout = new GroupLayout(type2InputAutomatonJPanel);
		type2InputAutomatonJPanel.setLayout(type2InputAutomatonLayout);
		type2InputAutomatonLayout.setHorizontalGroup(type2InputAutomatonLayout.createParallelGroup(Alignment.LEADING)
			.addGroup(
				Alignment.TRAILING,
				type2InputAutomatonLayout
					.createSequentialGroup()
					.addContainerGap()
					.addGroup(
						type2InputAutomatonLayout
							.createParallelGroup(Alignment.LEADING)
							.addComponent(type2InputAutomatonAlphabetJLabel, GroupLayout.DEFAULT_SIZE, 119,
								Short.MAX_VALUE)
							.addComponent(type2InputAutomatonStatesJLabel, GroupLayout.DEFAULT_SIZE, 119,
								Short.MAX_VALUE)
							.addComponent(type2InputAutomatonFinalStatesJLabel, GroupLayout.DEFAULT_SIZE, 119,
								Short.MAX_VALUE)
							.addComponent(type2InputAutomatonTransitionsJLabel, GroupLayout.DEFAULT_SIZE, 119,
								Short.MAX_VALUE)
					// .addComponent(type2InputAutomatonUnreachableStatesJLabel, GroupLayout.DEFAULT_SIZE,
					// 119, Short.MAX_VALUE)
					)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(
						type2InputAutomatonLayout
							.createParallelGroup(Alignment.LEADING, false)
							.addGroup(
								type2InputAutomatonLayout
									.createSequentialGroup()
									.addComponent(type2InputAutomatonMinJLabel, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(type2InputAutomatonMaxJLabel, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE))
							.addGroup(
								type2InputAutomatonLayout
									.createSequentialGroup()
									.addComponent(type2InputAutomatonAlphabetMinJSpinner, GroupLayout.PREFERRED_SIZE,
										65, GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(type2InputAutomatonAlphabetMaxJSpinner, GroupLayout.PREFERRED_SIZE,
										65, GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(type2InputAutomatonAlphabetJComboBox, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGroup(
								type2InputAutomatonLayout
									.createSequentialGroup()
									.addComponent(type2InputAutomatonStatesMinJSpinner, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(type2InputAutomatonStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE, 65,
										GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(type2InputAutomatonStatesJComboBox, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGroup(
								type2InputAutomatonLayout
									.createSequentialGroup()
									.addComponent(type2InputAutomatonFinalStatesMinJSpinner,
										GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(type2InputAutomatonFinalStatesMaxJSpinner,
										GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(type2InputAutomatonInitialStateFinal, GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGroup(
								type2InputAutomatonLayout
									.createSequentialGroup()
									.addComponent(type2InputAutomatonTransitionsMinJSpinner,
										GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(type2InputAutomatonTransitionsMaxJSpinner,
										GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE))
					// .addGroup(
					// type2InputAutomatonLayout
					// .createSequentialGroup()
					// .addComponent(type2InputAutomatonUnreachableStatesMinJSpinner,
					// GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
					// .addGap(6, 6, 6)
					// .addComponent(type2InputAutomatonUnreachableStatesMaxJSpinner,
					// GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE))
					).addContainerGap()));
		type2InputAutomatonLayout.setVerticalGroup(type2InputAutomatonLayout.createParallelGroup(Alignment.LEADING)
			.addGroup(
				type2InputAutomatonLayout
					.createSequentialGroup()
					.addGroup(
						type2InputAutomatonLayout
							.createParallelGroup(Alignment.LEADING)
							.addComponent(type2InputAutomatonMinJLabel)
							.addGroup(
								type2InputAutomatonLayout
									.createSequentialGroup()
									.addGap(1, 1, 1)
									.addComponent(type2InputAutomatonMaxJLabel, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(6, 6, 6)
					.addGroup(
						type2InputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type2InputAutomatonAlphabetJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputAutomatonAlphabetMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputAutomatonAlphabetMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputAutomatonAlphabetJComboBox, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						type2InputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type2InputAutomatonStatesJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputAutomatonStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputAutomatonStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputAutomatonStatesJComboBox, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						type2InputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type2InputAutomatonFinalStatesJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputAutomatonFinalStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputAutomatonFinalStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputAutomatonInitialStateFinal, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						type2InputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type2InputAutomatonTransitionsJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputAutomatonTransitionsMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type2InputAutomatonTransitionsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					// .addGap(6, 6, 6)
					// .addGroup(
					// type2InputAutomatonLayout
					// .createParallelGroup(Alignment.CENTER)
					// .addComponent(type2InputAutomatonUnreachableStatesJLabel, GroupLayout.PREFERRED_SIZE,
					// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					// .addComponent(type2InputAutomatonUnreachableStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
					// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					// .addComponent(type2InputAutomatonUnreachableStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
					// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
	}

	// private void setupType2OutputGrammarLayout()
	// {
	//
	// setStandardJComponentSize(type2OutputGrammarMinJLabel, 40, 14);
	// type2OutputGrammarMinJLabel.setHorizontalAlignment(SwingConstants.CENTER);
	//
	// setStandardJComponentSize(type2OutputGrammarMaxJLabel, 40, 14);
	// type2OutputGrammarMaxJLabel.setHorizontalAlignment(SwingConstants.CENTER);
	//
	// // Minimal values
	// setStandardJComponentSize(type2OutputGrammarTerminalsMinJSpinner, 148, 25);
	// setStandardJComponentSize(type2OutputGrammarVariablesMinJSpinner, 148, 25);
	// setStandardJComponentSize(type2OutputGrammarRulesMinJSpinner, 148, 25);
	//
	// // Maximal values
	// setStandardJComponentSize(type2OutputGrammarTerminalsMaxJSpinner, 148, 25);
	// setStandardJComponentSize(type2OutputGrammarVariablesMaxJSpinner, 148, 25);
	// setStandardJComponentSize(type2OutputGrammarRulesMaxJSpinner, 148, 25);
	//
	// spinnersSup.defaultDisabled(type2OutputGrammarTerminalsMinJSpinner);
	// spinnersSup.defaultDisabled(type2OutputGrammarTerminalsMaxJSpinner);
	// spinnersSup.defaultDisabled(type2OutputGrammarVariablesMinJSpinner);
	// spinnersSup.defaultDisabled(type2OutputGrammarVariablesMaxJSpinner);
	// spinnersSup.defaultDisabled(type2OutputGrammarRulesMinJSpinner);
	// spinnersSup.defaultDisabled(type2OutputGrammarRulesMaxJSpinner);
	//
	// // Comboboxes and additional options
	//
	// setStandardJComponentSize(type2OutputGRammarEpsilonJCheckBox, 148, 25);
	//
	// // Setup layout
	//
	// GroupLayout type2OutputGrammarLayout = new GroupLayout(type2OutputGrammarJPanel);
	// type2OutputGrammarJPanel.setLayout(type2OutputGrammarLayout);
	// type2OutputGrammarLayout
	// .setHorizontalGroup(type2OutputGrammarLayout.createParallelGroup(Alignment.LEADING)
	// .addGroup(
	// Alignment.TRAILING,
	// type2OutputGrammarLayout
	// .createSequentialGroup()
	// .addContainerGap()
	// .addGroup(
	// type2OutputGrammarLayout
	// .createParallelGroup(Alignment.LEADING)
	// .addComponent(type2OutputGrammarTerminalsJLabel, GroupLayout.DEFAULT_SIZE, 100,
	// Short.MAX_VALUE)
	// .addComponent(type2OutputGrammarVariablesJLabel, GroupLayout.DEFAULT_SIZE, 100,
	// Short.MAX_VALUE)
	// .addComponent(type2OutputGrammarRulesJLabel, GroupLayout.DEFAULT_SIZE, 100,
	// Short.MAX_VALUE))
	// .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	// .addGroup(
	// type2OutputGrammarLayout
	// .createParallelGroup(Alignment.LEADING, false)
	// .addGroup(
	// type2OutputGrammarLayout
	// .createSequentialGroup()
	// .addComponent(type2OutputGrammarMinJLabel, GroupLayout.PREFERRED_SIZE, 65,
	// GroupLayout.PREFERRED_SIZE)
	// .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	// .addComponent(type2OutputGrammarMaxJLabel, GroupLayout.PREFERRED_SIZE, 65,
	// GroupLayout.PREFERRED_SIZE))
	// .addGroup(
	// type2OutputGrammarLayout
	// .createSequentialGroup()
	// .addComponent(type2OutputGrammarTerminalsMinJSpinner,
	// GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
	// .addGap(6, 6, 6)
	// .addComponent(type2OutputGrammarTerminalsMaxJSpinner,
	// GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE))
	// .addGroup(
	// type2OutputGrammarLayout
	// .createSequentialGroup()
	// .addComponent(type2OutputGrammarVariablesMinJSpinner,
	// GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
	// .addGap(6, 6, 6)
	// .addComponent(type2OutputGrammarVariablesMaxJSpinner,
	// GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE))
	// .addGroup(
	// type2OutputGrammarLayout
	// .createSequentialGroup()
	// .addComponent(type2OutputGrammarRulesMinJSpinner, GroupLayout.PREFERRED_SIZE,
	// 65, GroupLayout.PREFERRED_SIZE)
	// .addGap(6, 6, 6)
	// .addComponent(type2OutputGrammarRulesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
	// 65, GroupLayout.PREFERRED_SIZE)
	// .addGap(6, 6, 6)
	// .addComponent(type2OutputGRammarEpsilonJCheckBox, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))).addContainerGap()));
	// type2OutputGrammarLayout.setVerticalGroup(type2OutputGrammarLayout.createParallelGroup(Alignment.LEADING)
	// .addGroup(
	// type2OutputGrammarLayout
	// .createSequentialGroup()
	// .addGroup(
	// type2OutputGrammarLayout
	// .createParallelGroup(Alignment.LEADING)
	// .addComponent(type2OutputGrammarMinJLabel)
	// .addGroup(
	// type2OutputGrammarLayout
	// .createSequentialGroup()
	// .addGap(1, 1, 1)
	// .addComponent(type2OutputGrammarMaxJLabel, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
	// .addGap(6, 6, 6)
	// .addGroup(
	// type2OutputGrammarLayout
	// .createParallelGroup(Alignment.CENTER)
	// .addComponent(type2OutputGrammarTerminalsJLabel, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	// .addComponent(type2OutputGrammarTerminalsMinJSpinner, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	// .addComponent(type2OutputGrammarTerminalsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
	// .addGap(6, 6, 6)
	// .addGroup(
	// type2OutputGrammarLayout
	// .createParallelGroup(Alignment.CENTER)
	// .addComponent(type2OutputGrammarVariablesJLabel, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	// .addComponent(type2OutputGrammarVariablesMinJSpinner, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	// .addComponent(type2OutputGrammarVariablesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
	// .addGap(6, 6, 6)
	// .addGroup(
	// type2OutputGrammarLayout
	// .createParallelGroup(Alignment.CENTER)
	// .addComponent(type2OutputGrammarRulesJLabel, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	// .addComponent(type2OutputGrammarRulesMinJSpinner, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	// .addComponent(type2OutputGrammarRulesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	// .addComponent(type2OutputGRammarEpsilonJCheckBox, GroupLayout.PREFERRED_SIZE,
	// GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
	// .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
	// }

	private void setupType3OperationLayout()
	{

		type3OperationButtonGroup.add(type3OperationDFAToREJButton);
		type3OperationButtonGroup.add(type3OperationREToDFAJButton);
		type3OperationDFAToREJButton.setSelected(true);

		type3OperationJPanel.setLayout(new BoxLayout(type3OperationJPanel, BoxLayout.Y_AXIS));
		type3OperationDFAToREJButton.setMargin(new Insets(5, 10, 5, 0));
		type3OperationREToDFAJButton.setMargin(new Insets(0, 10, 5, 0));
		type3OperationJPanel.add(type3OperationDFAToREJButton);
		type3OperationJPanel.add(type3OperationREToDFAJButton);

		type3OperationDFAToREJButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				type3OperationJRadioButtonActionPerformed(e);
			}
		});

		type3OperationREToDFAJButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				type3OperationJRadioButtonActionPerformed(e);
			}
		});
	}

	private void setupType3InputAutomatonLayout()
	{

		setStandardJComponentSize(type3InputAutomatonMinJLabel, 40, 14);
		type3InputAutomatonMinJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		setStandardJComponentSize(type3InputAutomatonMaxJLabel, 40, 14);
		type3InputAutomatonMaxJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		type3InputAutomatonAlphabetMinJSpinner.setValue(2);
		type3InputAutomatonAlphabetMaxJSpinner.setValue(3);
		setStandardJComponentSize(type3InputAutomatonAlphabetMinJSpinner, 148, 25);
		setStandardJComponentSize(type3InputAutomatonAlphabetMaxJSpinner, 148, 25);

		type3InputAutomatonStatesMinJSpinner.setValue(3);
		type3InputAutomatonStatesMaxJSpinner.setValue(4);
		setStandardJComponentSize(type3InputAutomatonStatesMinJSpinner, 148, 25);
		setStandardJComponentSize(type3InputAutomatonStatesMaxJSpinner, 148, 25);

		type3InputAutomatonUnreachableStatesMinJSpinner.setValue(0);
		type3InputAutomatonUnreachableStatesMaxJSpinner.setValue(2);
		setStandardJComponentSize(type3InputAutomatonUnreachableStatesMinJSpinner, 148, 25);
		setStandardJComponentSize(type3InputAutomatonUnreachableStatesMaxJSpinner, 148, 25);

		type3InputAutomatonFinalStatesMinJSpinner.setValue(1);
		type3InputAutomatonFinalStatesMaxJSpinner.setValue(2);
		setStandardJComponentSize(type3InputAutomatonFinalStatesMinJSpinner, 148, 25);
		setStandardJComponentSize(type3InputAutomatonFinalStatesMaxJSpinner, 148, 25);

		type3InputAutomatonTransitionsMinJSpinner.setValue(3);
		type3InputAutomatonTransitionsMaxJSpinner.setValue(6);
		setStandardJComponentSize(type3InputAutomatonTransitionsMinJSpinner, 148, 25);
		setStandardJComponentSize(type3InputAutomatonTransitionsMaxJSpinner, 148, 25);

		type3InputAutomatonAlphabetJComboBox.setModel(new DefaultComboBoxModel(new String[] { " a, b, c...",
			" A, B, C...", " 1, 2, 3...", " x, y, z ...", " X, Y, Z ..." }));
		setStandardJComponentSize(type3InputAutomatonAlphabetJComboBox, 148, 25);

		type3InputAutomatonStatesJComboBox.setModel(new DefaultComboBoxModel(new String[] { " A, B, C...",
			" 1, 2, 3...", " X, Y, Z ...", " q0, q1, q2...", " s0, s1, s2..." }));
		type3InputAutomatonStatesJComboBox.setSelectedIndex(4);
		setStandardJComponentSize(type3InputAutomatonStatesJComboBox, 148, 25);

        criteriaChecker.addErrorConstraint(type3InputAutomatonAlphabetMinJSpinner, 1, true, null, true);
        criteriaChecker.addErrorConstraint(type3InputAutomatonTransitionsMinJSpinner, 1, true, null, true);
        criteriaChecker.addErrorConstraint(type3InputAutomatonFinalStatesMinJSpinner, 1, true, null, true);
        criteriaChecker.addErrorConstraint(type3InputAutomatonStatesMinJSpinner, 1, true, null, true);
        criteriaChecker.addErrorConstraint(type3InputAutomatonUnreachableStatesMinJSpinner, 0, true, null, true);

		GroupLayout type3InputAutomatonLayout = new GroupLayout(type3InputAutomatonJPanel);
		type3InputAutomatonJPanel.setLayout(type3InputAutomatonLayout);
		type3InputAutomatonLayout
			.setHorizontalGroup(type3InputAutomatonLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(
					Alignment.TRAILING,
					type3InputAutomatonLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
							type3InputAutomatonLayout
								.createParallelGroup(Alignment.LEADING)
								.addComponent(type3InputAutomatonAlphabetJLabel, GroupLayout.DEFAULT_SIZE, 119,
									Short.MAX_VALUE)
								.addComponent(type3InputAutomatonStatesJLabel, GroupLayout.DEFAULT_SIZE, 119,
									Short.MAX_VALUE)
								.addComponent(type3InputAutomatonUnreachableStatesJLabel, GroupLayout.DEFAULT_SIZE,
									119, Short.MAX_VALUE)
								.addComponent(type3InputAutomatonFinalStatesJLabel, GroupLayout.DEFAULT_SIZE, 119,
									Short.MAX_VALUE)
								.addComponent(type3InputAutomatonTransitionsJLabel, GroupLayout.DEFAULT_SIZE, 119,
									Short.MAX_VALUE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
							type3InputAutomatonLayout
								.createParallelGroup(Alignment.LEADING, false)
								.addGroup(
									type3InputAutomatonLayout
										.createSequentialGroup()
										.addComponent(type3InputAutomatonMinJLabel, GroupLayout.PREFERRED_SIZE, 65,
											GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(type3InputAutomatonMaxJLabel, GroupLayout.PREFERRED_SIZE, 65,
											GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type3InputAutomatonLayout
										.createSequentialGroup()
										.addComponent(type3InputAutomatonAlphabetMinJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type3InputAutomatonAlphabetMaxJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type3InputAutomatonAlphabetJComboBox, GroupLayout.PREFERRED_SIZE,
											GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type3InputAutomatonLayout
										.createSequentialGroup()
										.addComponent(type3InputAutomatonStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
											65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type3InputAutomatonStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
											65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type3InputAutomatonStatesJComboBox, GroupLayout.PREFERRED_SIZE,
											GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type3InputAutomatonLayout
										.createSequentialGroup()
										.addComponent(type3InputAutomatonUnreachableStatesMinJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type3InputAutomatonUnreachableStatesMaxJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type3InputAutomatonLayout
										.createSequentialGroup()
										.addComponent(type3InputAutomatonFinalStatesMinJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type3InputAutomatonFinalStatesMaxJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type3InputAutomatonLayout
										.createSequentialGroup()
										.addComponent(type3InputAutomatonTransitionsMinJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type3InputAutomatonTransitionsMaxJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)))
						.addContainerGap()));
		type3InputAutomatonLayout.setVerticalGroup(type3InputAutomatonLayout.createParallelGroup(Alignment.LEADING)
			.addGroup(
				type3InputAutomatonLayout
					.createSequentialGroup()
					.addGroup(
						type3InputAutomatonLayout
							.createParallelGroup(Alignment.LEADING)
							.addComponent(type3InputAutomatonMinJLabel)
							.addGroup(
								type3InputAutomatonLayout
									.createSequentialGroup()
									.addGap(1, 1, 1)
									.addComponent(type3InputAutomatonMaxJLabel, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(6, 6, 6)
					.addGroup(
						type3InputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type3InputAutomatonAlphabetJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type3InputAutomatonAlphabetMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type3InputAutomatonAlphabetMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type3InputAutomatonAlphabetJComboBox, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						type3InputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type3InputAutomatonStatesJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type3InputAutomatonStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type3InputAutomatonStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type3InputAutomatonStatesJComboBox, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						type3InputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type3InputAutomatonUnreachableStatesJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type3InputAutomatonUnreachableStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type3InputAutomatonUnreachableStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						type3InputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type3InputAutomatonFinalStatesJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type3InputAutomatonFinalStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type3InputAutomatonFinalStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						type3InputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type3InputAutomatonTransitionsJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type3InputAutomatonTransitionsMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type3InputAutomatonTransitionsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
	}

	private void setupType3OutputRELayout()
	{

		setStandardJComponentSize(type3OutputREMinJLabel, 40, 14);
		type3OutputREMinJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		setStandardJComponentSize(type3OutputREMaxJLabel, 40, 14);
		type3OutputREMaxJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// Minimal values
		type3OutputREAlphabetMinJSpinner.setValue(2);
		type3OutputREUnionMinJSpinner.setValue(2);
		type3OutputREConcatenationMinJSpinner.setValue(2);
		type3OutputREIterationMinJSpinner.setValue(1);
		setStandardJComponentSize(type3OutputREAlphabetMinJSpinner, 148, 25);
		setStandardJComponentSize(type3OutputREUnionMinJSpinner, 148, 25);
		setStandardJComponentSize(type3OutputREConcatenationMinJSpinner, 148, 25);
		setStandardJComponentSize(type3OutputREIterationMinJSpinner, 148, 25);

		// Maximal values
		type3OutputREAlphabetMaxJSpinner.setValue(3);
		type3OutputREUnionMaxJSpinner.setValue(3);
		type3OutputREConcatenationMaxJSpinner.setValue(3);
		type3OutputREIterationMaxJSpinner.setValue(2);
		setStandardJComponentSize(type3OutputREAlphabetMaxJSpinner, 148, 25);
		setStandardJComponentSize(type3OutputREUnionMaxJSpinner, 148, 25);
		setStandardJComponentSize(type3OutputREConcatenationMaxJSpinner, 148, 25);
		setStandardJComponentSize(type3OutputREIterationMaxJSpinner, 148, 25);

		setStandardJComponentSize(type3OutputREHasEpsJCheckBox, 148, 25);

		GroupLayout type3OutputRELayout = new GroupLayout(type3OutputREJPanel);
		type3OutputREJPanel.setLayout(type3OutputRELayout);
		type3OutputRELayout.setHorizontalGroup(type3OutputRELayout.createParallelGroup(Alignment.LEADING).addGroup(
			Alignment.TRAILING,
			type3OutputRELayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					type3OutputRELayout.createParallelGroup(Alignment.LEADING)
						.addComponent(type3OutputREUnionJLabel, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
						.addComponent(type3OutputREConcatenationJLabel, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
						.addComponent(type3OutputREIterationJLabel, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					type3OutputRELayout
						.createParallelGroup(Alignment.LEADING, false)
						.addGroup(
							type3OutputRELayout
								.createSequentialGroup()
								.addComponent(type3OutputREMinJLabel, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(type3OutputREMaxJLabel, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE))

						.addGroup(
							type3OutputRELayout
								.createSequentialGroup()
								.addComponent(type3OutputREUnionMinJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(type3OutputREUnionMaxJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(type3OutputREHasEpsJCheckBox, GroupLayout.PREFERRED_SIZE,
									GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(
							type3OutputRELayout
								.createSequentialGroup()
								.addComponent(type3OutputREConcatenationMinJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(type3OutputREConcatenationMaxJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE))
						.addGroup(
							type3OutputRELayout
								.createSequentialGroup()
								.addComponent(type3OutputREIterationMinJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(type3OutputREIterationMaxJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE))).addGap(9, 9, 9).addContainerGap()));
		type3OutputRELayout.setVerticalGroup(type3OutputRELayout.createParallelGroup(Alignment.LEADING).addGroup(
			type3OutputRELayout
				.createSequentialGroup()
				.addGroup(
					type3OutputRELayout
						.createParallelGroup(Alignment.LEADING)
						.addComponent(type3OutputREMinJLabel)
						.addGroup(
							type3OutputRELayout
								.createSequentialGroup()
								.addGap(1, 1, 1)
								.addComponent(type3OutputREMaxJLabel, GroupLayout.PREFERRED_SIZE,
									GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))

				.addGap(6, 6, 6)
				.addGroup(
					type3OutputRELayout
						.createParallelGroup(Alignment.CENTER)
						.addComponent(type3OutputREUnionJLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)
						.addComponent(type3OutputREUnionMinJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type3OutputREUnionMaxJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type3OutputREHasEpsJCheckBox, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					type3OutputRELayout
						.createParallelGroup(Alignment.CENTER)
						.addComponent(type3OutputREConcatenationJLabel, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type3OutputREConcatenationMinJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type3OutputREConcatenationMaxJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					type3OutputRELayout
						.createParallelGroup(Alignment.CENTER)
						.addComponent(type3OutputREIterationJLabel, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type3OutputREIterationMinJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type3OutputREIterationMaxJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))

				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

	}

	private void setupType3InputRELayout()
	{

		setStandardJComponentSize(type3InputREMinJLabel, 40, 14);
		type3InputREMinJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		setStandardJComponentSize(type3InputREMaxJLabel, 40, 14);
		type3InputREMaxJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		type3InputReAlphabetJComboBox.setModel(new DefaultComboBoxModel(new String[] { " a, b, c...", " A, B, C...",
			" 1, 2, 3...", " x, y, z...", " X, Y, Z..." }));
		setStandardJComponentSize(type3InputReAlphabetJComboBox, 148, 25);

		criteriaChecker.addErrorConstraint(type3InputREAlphabetMinJSpinner, 1, true, null, true);

		// Minimal values
		type3InputREAlphabetMinJSpinner.setValue(2);
		type3inputREUnionMinJSpinner.setValue(2);
		type3inputREConcatenationMinJSpinner.setValue(2);
		type3inputREIterationMinJSpinner.setValue(1);
		type3inputREEpsMinJSpinner.setValue(0);
		type3inputREEmptySetMinJSpinner.setValue(0);
		setStandardJComponentSize(type3InputREAlphabetMinJSpinner, 148, 25);
		setStandardJComponentSize(type3inputREUnionMinJSpinner, 148, 25);
		setStandardJComponentSize(type3inputREConcatenationMinJSpinner, 148, 25);
		setStandardJComponentSize(type3inputREIterationMinJSpinner, 148, 25);
		setStandardJComponentSize(type3inputREEpsMinJSpinner, 148, 25);
		setStandardJComponentSize(type3inputREEmptySetMinJSpinner, 148, 25);

		// Maximal values
		type3InputREAlphabetMaxJSpinner.setValue(3);
		type3inputREUnionMaxJSpinner.setValue(3);
		type3inputREConcatenationMaxJSpinner.setValue(3);
		type3inputREIterationMaxJSpinner.setValue(2);
		type3inputREEpsMaxJSpinner.setValue(2);
		type3inputREEmptySetMaxJSpinner.setValue(2);
		setStandardJComponentSize(type3InputREAlphabetMaxJSpinner, 148, 25);
		setStandardJComponentSize(type3inputREUnionMaxJSpinner, 148, 25);
		setStandardJComponentSize(type3inputREConcatenationMaxJSpinner, 148, 25);
		setStandardJComponentSize(type3inputREIterationMaxJSpinner, 148, 25);
		setStandardJComponentSize(type3inputREEpsMaxJSpinner, 148, 25);
		setStandardJComponentSize(type3inputREEmptySetMaxJSpinner, 148, 25);

		GroupLayout type3InputRELayout = new GroupLayout(type3InputREJPanel);
		type3InputREJPanel.setLayout(type3InputRELayout);
		type3InputRELayout.setHorizontalGroup(type3InputRELayout.createParallelGroup(Alignment.LEADING).addGroup(
			Alignment.TRAILING,
			type3InputRELayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					type3InputRELayout.createParallelGroup(Alignment.LEADING)
						.addComponent(type3InputREAlphabetJLabel, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
						.addComponent(type3InputREUnionJLabel, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
						.addComponent(type3InputREConcatenationJLabel, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
						.addComponent(type3InputREIterationJLabel, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
						.addComponent(type3InputREEpsJLabel, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
						.addComponent(type3InputREEmptySetJLabel, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					type3InputRELayout
						.createParallelGroup(Alignment.LEADING, false)
						.addGroup(
							type3InputRELayout
								.createSequentialGroup()
								.addComponent(type3InputREMinJLabel, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(type3InputREMaxJLabel, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE))
						.addGroup(
							type3InputRELayout
								.createSequentialGroup()
								.addComponent(type3InputREAlphabetMinJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(type3InputREAlphabetMaxJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(type3InputReAlphabetJComboBox, GroupLayout.PREFERRED_SIZE,
									GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(
							type3InputRELayout
								.createSequentialGroup()
								.addComponent(type3inputREUnionMinJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(type3inputREUnionMaxJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE))
						.addGroup(
							type3InputRELayout
								.createSequentialGroup()
								.addComponent(type3inputREConcatenationMinJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(type3inputREConcatenationMaxJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE))
						.addGroup(
							type3InputRELayout
								.createSequentialGroup()
								.addComponent(type3inputREIterationMinJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(type3inputREIterationMaxJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE))
						.addGroup(
							type3InputRELayout
								.createSequentialGroup()
								.addComponent(type3inputREEpsMinJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(type3inputREEpsMaxJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE))
						.addGroup(
							type3InputRELayout
								.createSequentialGroup()
								.addComponent(type3inputREEmptySetMinJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(type3inputREEmptySetMaxJSpinner, GroupLayout.PREFERRED_SIZE, 65,
									GroupLayout.PREFERRED_SIZE))).addContainerGap()));
		type3InputRELayout.setVerticalGroup(type3InputRELayout.createParallelGroup(Alignment.LEADING).addGroup(
			type3InputRELayout
				.createSequentialGroup()
				.addGroup(
					type3InputRELayout
						.createParallelGroup(Alignment.LEADING)
						.addComponent(type3InputREMinJLabel)
						.addGroup(
							type3InputRELayout
								.createSequentialGroup()
								.addGap(1, 1, 1)
								.addComponent(type3InputREMaxJLabel, GroupLayout.PREFERRED_SIZE,
									GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
				.addGap(6, 6, 6)
				.addGroup(
					type3InputRELayout
						.createParallelGroup(Alignment.CENTER)
						.addComponent(type3InputREAlphabetJLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)
						.addComponent(type3InputREAlphabetMinJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type3InputREAlphabetMaxJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type3InputReAlphabetJComboBox, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					type3InputRELayout
						.createParallelGroup(Alignment.CENTER)
						.addComponent(type3InputREUnionJLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)
						.addComponent(type3inputREUnionMinJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type3inputREUnionMaxJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					type3InputRELayout
						.createParallelGroup(Alignment.CENTER)
						.addComponent(type3InputREConcatenationJLabel, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type3inputREConcatenationMinJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type3inputREConcatenationMaxJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					type3InputRELayout
						.createParallelGroup(Alignment.CENTER)
						.addComponent(type3InputREIterationJLabel, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type3inputREIterationMinJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type3inputREIterationMaxJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					type3InputRELayout
						.createParallelGroup(Alignment.CENTER)
						.addComponent(type3InputREEpsJLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)
						.addComponent(type3inputREEpsMinJSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)
						.addComponent(type3inputREEpsMaxJSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					type3InputRELayout
						.createParallelGroup(Alignment.CENTER)
						.addComponent(type3InputREEmptySetJLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
							GroupLayout.PREFERRED_SIZE)
						.addComponent(type3inputREEmptySetMinJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(type3inputREEmptySetMaxJSpinner, GroupLayout.PREFERRED_SIZE,
							GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

	}

	@SuppressWarnings("unused")
	private void setupType3OutputAutomatonLayout()
	{

		setStandardJComponentSize(type3OutputAutomatonMinJLabel, 40, 14);
		type3OutputAutomatonMinJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		setStandardJComponentSize(type3OutputAutomatonMaxJLabel, 40, 14);
		type3OutputAutomatonMaxJLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// States min and max setting
		type3OutputAutomatonStatesMinJSpinner.setValue(2);
		setStandardJComponentSize(type3OutputAutomatonStatesMinJSpinner, 80, 25);

		type3OutputAutomatonStatesMaxJSpinner.setValue(6);
		setStandardJComponentSize(type3OutputAutomatonStatesMaxJSpinner, 80, 25);

		// Transitions min and max setting
		type3OutputAutomatonTransitionsMinJSpinner.setValue(2);
		setStandardJComponentSize(type3OutputAutomatonTransitionsMinJSpinner, 80, 25);

		type3OutputAutomatonTransitionsMaxJSpinner.setValue(10);
		setStandardJComponentSize(type3OutputAutomatonTransitionsMaxJSpinner, 80, 25);

		type3OutputAutomatonStatesJComboBox.setModel(new DefaultComboBoxModel(new String[] { " A, B, C...",
			" 1, 2, 3...", " X, Y, Z...", " q0, q1, q2...", " s0, s1, s2....", " I, II, III, IV...",
			" i, ii, iii, iv...", " 1, 10, 11, 100..." }));
		type3OutputAutomatonStatesJComboBox.setSelectedIndex(4);
		setStandardJComponentSize(type3OutputAutomatonStatesJComboBox, 148, 25);

		GroupLayout type3OutputAutomatonLayout = new GroupLayout(type3OutputAutomatonJPanel);
		type3OutputAutomatonJPanel.setLayout(type3OutputAutomatonLayout);
		type3OutputAutomatonLayout
			.setHorizontalGroup(type3OutputAutomatonLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(
					Alignment.TRAILING,
					type3OutputAutomatonLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
							type3OutputAutomatonLayout
								.createParallelGroup(Alignment.LEADING)
								.addComponent(type3OutputAutomatonStatesJLabel, GroupLayout.DEFAULT_SIZE, 119,
									Short.MAX_VALUE)
								.addComponent(type3OutputAutomatonTransitionsJLabel, GroupLayout.DEFAULT_SIZE, 119,
									Short.MAX_VALUE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
							type3OutputAutomatonLayout
								.createParallelGroup(Alignment.LEADING, false)
								.addGroup(
									type3OutputAutomatonLayout
										.createSequentialGroup()
										.addComponent(type3OutputAutomatonMinJLabel, GroupLayout.PREFERRED_SIZE, 65,
											GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(type3OutputAutomatonMaxJLabel, GroupLayout.PREFERRED_SIZE, 65,
											GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type3OutputAutomatonLayout
										.createSequentialGroup()
										.addComponent(type3OutputAutomatonStatesMinJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type3OutputAutomatonStatesMaxJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type3OutputAutomatonStatesJComboBox, GroupLayout.PREFERRED_SIZE,
											GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(
									type3OutputAutomatonLayout
										.createSequentialGroup()
										.addComponent(type3OutputAutomatonTransitionsMinJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
										.addGap(6, 6, 6)
										.addComponent(type3OutputAutomatonTransitionsMaxJSpinner,
											GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)))
						.addContainerGap()));
		type3OutputAutomatonLayout.setVerticalGroup(type3OutputAutomatonLayout.createParallelGroup(Alignment.LEADING)
			.addGroup(
				type3OutputAutomatonLayout
					.createSequentialGroup()
					.addGroup(
						type3OutputAutomatonLayout
							.createParallelGroup(Alignment.LEADING)
							.addComponent(type3OutputAutomatonMinJLabel)
							.addGroup(
								type3OutputAutomatonLayout
									.createSequentialGroup()
									.addGap(1, 1, 1)
									.addComponent(type3OutputAutomatonMaxJLabel, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(6, 6, 6)
					.addGroup(
						type3OutputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type3OutputAutomatonStatesJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type3OutputAutomatonStatesMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type3OutputAutomatonStatesMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type3OutputAutomatonStatesJComboBox, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6, 6, 6)
					.addGroup(
						type3OutputAutomatonLayout
							.createParallelGroup(Alignment.CENTER)
							.addComponent(type3OutputAutomatonTransitionsJLabel, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type3OutputAutomatonTransitionsMinJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(type3OutputAutomatonTransitionsMaxJSpinner, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
	}

    private void setupType3IsomorphismTestLayout()
    {
        // Setup layout
        setStandardJComponentSize(type3IsoPercentJLabel, 269, 25);
        setStandardJComponentSize(type3IsoPercentJSpinner, 148, 25);
		criteriaChecker.addErrorConstraint(type3IsoPercentJSpinner, 100, false, null);
        GroupLayout type3IsomorphismLayout = new GroupLayout(type3IsomorphismJPanel);
        type3IsomorphismJPanel.setLayout(type3IsomorphismLayout);

        type3IsomorphismLayout.setHorizontalGroup(type3IsomorphismLayout.createParallelGroup(Alignment.LEADING).addGroup(
                Alignment.TRAILING,
                type3IsomorphismLayout
                        .createParallelGroup()
                        .addGroup(
                                type3IsomorphismLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(type3IsomorphismJCheckBox, GroupLayout.DEFAULT_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                        )
                        .addGroup(
                                type3IsomorphismLayout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(type3IsomorphismLayout.createSequentialGroup()
                                                .addComponent(type3IsoPercentJLabel, GroupLayout.DEFAULT_SIZE, 125, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(type3IsoPercentJSpinner, GroupLayout.PREFERRED_SIZE, 65,
                                                        GroupLayout.PREFERRED_SIZE))
                        )));
        type3IsomorphismLayout.setVerticalGroup(type3IsomorphismLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(
                        type3IsomorphismLayout
                                .createSequentialGroup()
                                .addContainerGap()
                                .addComponent(type3IsomorphismJCheckBox, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addGroup(
                                        type3IsomorphismLayout
                                                .createParallelGroup(Alignment.LEADING)
                                                .addComponent(type3IsoPercentJLabel, GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(type3IsoPercentJSpinner, GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    }

	/**
	 * type of exercises is choosen
	 * 
	 * @param evt
	 *            the type of exercises is choosen in the combobox
	 */
	private void typeJComboBoxActionPerformed(java.awt.event.ActionEvent evt)
	{
		if (evt.getSource() != null)
		{ // switch panel
			int i = this.typeJComboBox.getSelectedIndex();
			deactivateAllGridbagPanels();
			activateTransformationPanel(i);
		}
	}

	/**
	 * operation from in panel 1 is choosen
	 * 
	 * @param evt
	 *            the operation is selected in combobox with the sign "from"
	 */

	private void type1OperationFromJComboBoxActionPerformed(java.awt.event.ActionEvent evt)
	{
		if (evt.getSource() != null)
		{
			// index of selected item
			int i = this.type1OperationFromJComboBox.getSelectedIndex();
			// change operation To combobox
			this.type1OperationToJComboBox.removeAllItems();
			for (int j = i; j < this.type1OperationFromJComboBox.getItemCount() - 1; j++)
			{
				this.type1OperationToJComboBox.addItem(this.type1OperationFromJComboBox.getItemAt(j + 1));
			}
			this.type1OperationToJComboBox.addItem(" " + resourceBundle.getString("canonicalDFA"));
			// disable/enable minimization
			// disable/ enable total transition function
			boolean enabled = (i == 2 ? true : false);
			// changeGridbagPanelAllComponentsStatus(type1MinimalizationJPanel, enabled);
			type1MinimalizationJPanel.setVisible(enabled);
			type1InputAutomatonTotalTransitionFunctionJCheckBox.setVisible(enabled);

			// disable / enable unreachable states
			enabled = (i == 3 ? false : true);

			setType1UnreachableStatesEnabled(enabled);
			type1InputAutomatonFirstStateJCheckBox.setVisible(enabled);

			type1InputAutomatonEpsTransitionsMinJSpinner.setVisible(false);
			type1InputAutomatonEpsTransitionsMaxJSpinner.setVisible(false);
			type1InputAutomatonEpsTransitionsJLabel.setVisible(false);
			type1InputAutomatonTransitionsMinJSpinner.setEnabled(true);
			type1InputAutomatonTransitionsMaxJSpinner.setEnabled(true);
			// enable / disable resulting automaton states
			switch (i)
			{
				case 0:
					this.setType1ResultingAutomatonStatesEnabled(false);
					this.setType1ResultingAutomatonTransitionsEnabled(true);
					type1InputAutomatonEpsTransitionsMinJSpinner.setVisible(true);
					type1InputAutomatonEpsTransitionsMaxJSpinner.setVisible(true);
					type1InputAutomatonEpsTransitionsJLabel.setVisible(true);
					break;
				case 1:
					break;
				case 2:
					type1InputAutomatonTotalTransitionFunctionJCheckBox.setSelected(false);
					break;
				case 3:
					type1InputAutomatonTotalTransitionFunctionJCheckBox.setSelected(false);
					this.setType1ResultingAutomatonEnabled(false);
					break;
				default:
					this.setType1ResultingAutomatonEnabled(true);
					break;
			}
		}
	}

	/**
	 * operation to in panel 1 is choosen
	 * 
	 * @param evt
	 *            the operation is selected in the combobox with the sign "to"
	 */
	private void type1OperationToJComboBoxActionPerformed(java.awt.event.ActionEvent evt)
	{
		if (evt.getSource() != null)
		{
			// index of selected item
			int i = this.type1OperationFromJComboBox.getItemCount() - this.type1OperationToJComboBox.getItemCount()
				+ this.type1OperationToJComboBox.getSelectedIndex();
			// enable / disable advanced minimization options
			boolean enabled = (i > 1 ? true : false);
			// changeGridbagPanelAllComponentsStatus(type1MinimalizationJPanel, enabled);
			type1MinimalizationJPanel.setVisible(enabled);
			int j = this.type1OperationFromJComboBox.getSelectedIndex();
			// enable / disable resulting automaton options
			switch (i)
			{
				case 0:
					this.setType1ResultingAutomatonStatesEnabled(false);
					this.setType1ResultingAutomatonTransitionsEnabled(true);
					break;
				default:
					this.setType1ResultingAutomatonEnabled(true);
					break;
			}
			enabled = (j == 2 ? true : false);
			// enable / disable total transition function
			type1InputAutomatonTotalTransitionFunctionJCheckBox.setVisible(enabled);
			// enable / disable unreachable states options
			enabled = (j == 3 ? false : true);
			this.setType1UnreachableStatesEnabled(enabled);
		}
	}

	/**
	 * Enables or disables this component, depending on the value of the parameter b. An enabled component can respond
	 * to user input Components are enabled initially by default.
	 * 
	 * @param enabled
	 *            If true, this component is enabled; otherwise this component is disabled
	 */
	private void setType1UnreachableStatesEnabled(boolean enabled)
	{
		if (enabled == false)
		{

			this.type1InputAutomatonUnreachableStatesMinJSpinner.setEnabled(false);
			this.type1InputAutomatonUnreachableStatesMaxJSpinner.setEnabled(false);
		}
		type1InputAutomatonUnreachableStatesJLabel.setEnabled(enabled);
		type1InputAutomatonUnreachableStatesMinJSpinner.setEnabled(enabled);
		type1InputAutomatonUnreachableStatesMaxJSpinner.setEnabled(enabled);
	}

	/**
	 * Enables or disables this component, depending on the value of the parameter b. An enabled component can respond
	 * to user input Components are disabled initially by default.
	 * 
	 * @param enabled
	 *            If true, this component is enabled; otherwise this component is disabled
	 */
	private void setType1ResultingAutomatonEnabled(boolean enabled)
	{
		if (enabled == false)
		{
			this.type1OutputAutomatonStatesMinJSpinner.setEnabled(enabled);
			this.type1OutputAutomatonStatesMaxJSpinner.setEnabled(enabled);
			this.type1OutputAutomatonTransitionsJLabel.setEnabled(enabled);
			this.type1OutputAutomatonTransitionsMinJSpinner.setEnabled(enabled);
			this.type1OutputAutomatonTransitionsMaxJSpinner.setEnabled(enabled);
			this.type1InputAutomatonTransitionsMinJSpinner.setEnabled(false);
			this.type1InputAutomatonTransitionsMaxJSpinner.setEnabled(false);
		}
		changeGridbagPanelAllComponentsStatus(type1OutputAutomatonJPanel, enabled);
	}

	/**
	 * Enables or disables this component, depending on the value of the parameter b. An enabled component can respond
	 * to user input Components are disabled initially by default.
	 * 
	 * @param enabled
	 *            If true, this component is enabled; otherwise this component is disabled
	 */
	private void setType1ResultingAutomatonStatesEnabled(boolean enabled)
	{
		// if (enabled == false)
		// {
		// this.type1OutputAutomatonStatesMinJSpinner.setValue(0);
		// this.type1OutputAutomatonStatesMaxJSpinner.setValue(0);
		// }
		this.type1OutputAutomatonStatesJLabel.setEnabled(enabled);
		this.type1OutputAutomatonStatesMinJSpinner.setEnabled(enabled);
		this.type1OutputAutomatonStatesMaxJSpinner.setEnabled(enabled);
	}

	/**
	 * Enables or disables this component, depending on the value of the parameter b. An enabled component can respond
	 * to user input Components are enabled initially by default.
	 * 
	 * @param enabled
	 *            If true, this component is enabled; otherwise this component is disabled
	 */
	private void setType1ResultingAutomatonTransitionsEnabled(boolean enabled)
	{
		// if (enabled == false)
		// {
		// this.type1OutputAutomatonTransitionsMinJSpinner.setValue(0);
		// this.type1OutputAutomatonTransitionsMaxJSpinner.setValue(0);
		// }

		this.type1OutputAutomatonTransitionsJLabel.setEnabled(enabled);
		this.type1OutputAutomatonTransitionsMinJSpinner.setEnabled(enabled);
		this.type1OutputAutomatonTransitionsMaxJSpinner.setEnabled(enabled);
	}

	/**
	 * the item in combo box operation of type 2 was selected
	 * 
	 * @param evt
	 *            the item in combo box operation of type 2 was selected
	 */
	private void type2OperationJRadioButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		if (evt.getSource() != null)
		{
			this.setType2Panel();
		}
	}

	/**
	 * the item in combo box operation of type 3 was selected
	 * 
	 * @param evt
	 *            the item in combo box operation of type 3 was selected
	 */
	private void type3OperationJRadioButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		if (evt.getSource() != null)
		{
			this.setType3Panel();
		}
	}

	/**
	 * sets the panel for exercises of type 2
	 */
	private void setType2Panel()
	{
		if (type2OperationNFAToGrammarJButton.isSelected())
		{
			activeType2SubpanelNumber = 1;
			this.type2InputAutomatonJPanel.setVisible(true);
			this.type2InputGrammarJPanel.setVisible(false);
            this.type2IsomorphismJPanel.setVisible(false);
			// this.type2OutputAutomatonJPanel.setVisible(false);
			// this.type2OutputGrammarJPanel.setVisible(true);
		}
		else
		{
			activeType2SubpanelNumber = 0;
			this.type2InputAutomatonJPanel.setVisible(false);
			this.type2InputGrammarJPanel.setVisible(true);
			this.type2IsomorphismJPanel.setVisible(true);
			// this.type2OutputAutomatonJPanel.setVisible(true);
			// this.type2OutputGrammarJPanel.setVisible(false);
		}
	}

	/**
	 * sets the panel for exercises of type 3
	 */
	private void setType3Panel()
	{
		if (type3OperationDFAToREJButton.isSelected())
		{
			activeType3SubpanelNumber = 0;
			this.type3InputAutomatonJPanel.setVisible(true);
			this.type3OutputREJPanel.setVisible(true);
			this.type3InputREJPanel.setVisible(false);
			this.type3OutputAutomatonJPanel.setVisible(false);
            this.type3IsomorphismJPanel.setVisible(false);
		}
		else
		{
			activeType3SubpanelNumber = 1;
			this.type3InputAutomatonJPanel.setVisible(false);
			this.type3OutputREJPanel.setVisible(false);
			this.type3InputREJPanel.setVisible(true);
			this.type3OutputAutomatonJPanel.setVisible(true);
            this.type3IsomorphismJPanel.setVisible(true);
		}
	}

	private JLabel type2InputGrammarLoopsJLabel = new JLabel();
	private JSpinner type2InputGrammarLoopsMinJSpinner = new JSpinner();
	private JSpinner type2InputGrammarLoopsMaxJSpinner = new JSpinner();
	private ButtonGroup type0Automaton1buttonGroup = new ButtonGroup();
	private ButtonGroup type0Automaton2buttonGroup = new ButtonGroup();
	private JComboBox typeJComboBox = new JComboBox();
	private JPanel transformationTypePane = new JPanel();
	private JLabel type0Automaton1StatesJLabel = new JLabel();
	private JLabel type0Automaton1FinalStatesJLabel = new JLabel();
	private JLabel type0Automaton1TransitionsJLabel = new JLabel();
	private JLabel type0Automaton1AlphabetJLabel = new JLabel();
	private JLabel type0Automaton1MinJLabel = new JLabel();
	private JSpinner type0Automaton1AlphabetMinJSpinner = new JSpinner();
	private JSpinner type0Automaton1StatesMinJSpinner = new JSpinner();
	private JSpinner type0Automaton1FinalStatesMinJSpinner = new JSpinner();
	private JSpinner type0Automaton1TransitionsMinJSpinner = new JSpinner();
	private JLabel type0Automaton1MaxJLabel = new JLabel();
	private JSpinner type0Automaton1AlphabetMaxJSpinner = new JSpinner();
	private JSpinner type0Automaton1StatesMaxJSpinner = new JSpinner();
	private JSpinner type0Automaton1FinalStatesMaxJSpinner = new JSpinner();
	private JSpinner type0Automaton1TransitionsMaxJSpinner = new JSpinner();
	private JComboBox type0Alphabet1JComboBox = new JComboBox();
	private JComboBox type0Automaton1StatesJComboBox = new JComboBox();
	private JCheckBox type0Automaton1TotalTransitionFunctionJCheckBox = new JCheckBox();
	private JLabel type0Automaton2StatesJLabel = new JLabel();
	private JLabel type0Automaton2FinalStatesJLabel = new JLabel();
	private JLabel type0Automaton2TransitionsJLabel = new JLabel();
	private JLabel type0Automaton2MinJLabel = new JLabel();
	private JSpinner type0Automaton2AlphabetMinJSpinner = new JSpinner();
	private JSpinner type0Automaton2StatesMinJSpinner = new JSpinner();
	private JSpinner type0Automaton2FinalStatesMinJSpinner = new JSpinner();
	private JSpinner type0Automaton2TransitionsMinJSpinner = new JSpinner();
	private JLabel type0Automaton2MaxJLabel = new JLabel();
	private JLabel type0OutputAutomatonMinJLabel = new JLabel();
	private JLabel type0OutputAutomatonMaxJLabel = new JLabel();
	private JSpinner type0Automaton2AlphabetMaxJSpinner = new JSpinner();
	private JSpinner type0Automaton2StatesMaxJSpinner = new JSpinner();
	private JSpinner type0Automaton2FinalStatesMaxJSpinner = new JSpinner();
	private JSpinner type0Automaton2TransitionsMaxJSpinner = new JSpinner();
	private JComboBox type0Automaton2StatesJComboBox = new JComboBox();
	private JCheckBox type0Automaton2TotalTransitionFunctionJCheckBox = new JCheckBox();
	private JPanel type0Automaton2JPanel = new JPanel();
	private JPanel type0OutputAutomatonJPanel = new JPanel();
	private JPanel type0OperationJPanel = new JPanel();
	private JRadioButton type0Automaton1JRadioButton = new JRadioButton();
	private JRadioButton type0Automaton1CoJRadioButton = new JRadioButton();
	private JRadioButton type0Automaton2JRadioButton = new JRadioButton();
	private JRadioButton type0Automaton2CoJRadioButton = new JRadioButton();
	private JComboBox type0OperationJComboBox = new JComboBox();
	private JPanel type0Automaton1JPanel = new JPanel();
	private JLabel type0OutputAutomatonStatesJLabel = new JLabel();
	private JSpinner type0OutputAutomatonStatesMinJSpinner = new JSpinner();
	private JSpinner type0OutputAutomatonStatesMaxJSpinner = new JSpinner();
	private JCheckBox type0OutputAutomatonMinOneFinalStateJCheckBox = new JCheckBox();
	private JLabel type0OutputAutomatonTransitionsJLabel = new JLabel();
	private JSpinner type0OutputAutomatonTransitionsMinJSpinner = new JSpinner();
	private JSpinner type0OutputAutomatonTransitionsMaxJSpinner = new JSpinner();
	private JPanel type1InputAutomatonJPanel = new JPanel();
	private JLabel type1InputAutomatonStatesJLabel = new JLabel();
	private JLabel type1InputAutomatonFinalStatesJLabel = new JLabel();
	private JLabel type1InputAutomatonTransitionsJLabel = new JLabel();
	private JLabel type1InputAutomatonEpsTransitionsJLabel = new JLabel();
	private JLabel type1InputAutomatonAlphabetJLabel = new JLabel();
	private JLabel type1InputAutomatonMinJLabel = new JLabel();
	private JSpinner type1InputAutomatonStatesMinJSpinner = new JSpinner();
	private JSpinner type1InputAutomatonFinalStatesMinJSpinner = new JSpinner();
	private JSpinner type1InputAutomatonTransitionsMinJSpinner = new JSpinner();
	private JSpinner type1InputAutomatonEpsTransitionsMinJSpinner = new JSpinner();
	private JSpinner type1InputAutomatonAlphabetMinJSpinner = new JSpinner();
	private JLabel type1InputAutomatonMaxJLabel = new JLabel();
	private JSpinner type1InputAutomatonStatesMaxJSpinner = new JSpinner();
	private JSpinner type1InputAutomatonFinalStatesMaxJSpinner = new JSpinner();
	private JSpinner type1InputAutomatonTransitionsMaxJSpinner = new JSpinner();
	private JSpinner type1InputAutomatonEpsTransitionsMaxJSpinner = new JSpinner();
	private JSpinner type1InputAutomatonAlphabetMaxJSpinner = new JSpinner();
	private JComboBox type1InputAutomatonAlphabetJComboBox = new JComboBox();
	private JComboBox type1InputAutomatonStatesJComboBox = new JComboBox();
	private JCheckBox type1InputAutomatonFirstStateJCheckBox = new JCheckBox();
	private JCheckBox type1InputAutomatonTotalTransitionFunctionJCheckBox = new JCheckBox();
	private JLabel type1InputAutomatonUnreachableStatesJLabel = new JLabel();
	private JSpinner type1InputAutomatonUnreachableStatesMinJSpinner = new JSpinner();
	private JSpinner type1InputAutomatonUnreachableStatesMaxJSpinner = new JSpinner();
	private JPanel type1OutputAutomatonJPanel = new JPanel();
	private JLabel type1OutputAutomatonMinJLabel = new JLabel();
	private JLabel type1OutputAutomatonMaxJLabel = new JLabel();
	private JLabel type1OutputAutomatonStatesJLabel = new JLabel();
	private JSpinner type1OutputAutomatonStatesMinJSpinner = new JSpinner();
	private JSpinner type1OutputAutomatonStatesMaxJSpinner = new JSpinner();
	private JLabel type1OutputAutomatonTransitionsJLabel = new JLabel();
	private JSpinner type1OutputAutomatonTransitionsMinJSpinner = new JSpinner();
	private JSpinner type1OutputAutomatonTransitionsMaxJSpinner = new JSpinner();
	private JPanel type1MinimalizationJPanel = new JPanel();
	private JCheckBox type2InputAutomatonInitialStateFinal = new JCheckBox();
	private JCheckBox type1MinimalizationStepsJCheckBox = new JCheckBox();
	private JLabel type1MinimalizationStepsJLabel = new JLabel();
	private JSpinner type1MinimalizationStepsMinJSpinner = new JSpinner();
	private JLabel type1MinimalizationMinJLabel = new JLabel();
	private JLabel type1MinimalizationMaxJLabel = new JLabel();
	private JSpinner type1MinimalizationStepsMaxJSpinner = new JSpinner();
	private JPanel type1OperationJPanel = new JPanel();
	private JLabel type1OperationFromJLabel = new JLabel();
	private JComboBox type1OperationFromJComboBox = new JComboBox();
	private JLabel type1OperationToJLabel = new JLabel();
	private JComboBox type1OperationToJComboBox = new JComboBox();
    private JPanel type1IsomorphismJPanel = new JPanel();
    private JCheckBox type1IsomorphismJCheckBox = new JCheckBox();
    private JSpinner type1IsoPercentJSpinner = new JSpinner();
    private JLabel type1IsoPercentJLabel = new JLabel();
	private JPanel type2OperationJPanel = new JPanel();
	private ButtonGroup type2OperationButtonGroup = new ButtonGroup();
	private ButtonGroup type3OperationButtonGroup = new ButtonGroup();
	private JRadioButton type2OperationGrammarToNFAJButton = new JRadioButton();
	private JRadioButton type2OperationNFAToGrammarJButton = new JRadioButton();
	private JPanel type2InputGrammarJPanel = new JPanel();
	private JLabel type2InputGrammarNonTerminalsJLabel = new JLabel();
	private JLabel type2InputGrammarRulesJLabel = new JLabel();
	private JLabel type2InputGrammarMinJLabel = new JLabel();
	private JSpinner type2InputGrammarVariablesMinJSpinner = new JSpinner();
	private JSpinner type2InputGrammarRulesMinJSpinner = new JSpinner();
	private JLabel type2InputGrammarMaxJLabel = new JLabel();
	private JSpinner type2InputGrammarVariablesMaxJSpinner = new JSpinner();
	private JSpinner type2InputGrammarRulesMaxJSpinner = new JSpinner();
	private JLabel type2InputGrammarTerminalsJLabel = new JLabel();
	private JSpinner type2InputGrammarTerminalsMinJSpinner = new JSpinner();
	private JSpinner type2InputGrammarTerminalsMaxJSpinner = new JSpinner();
	private JCheckBox type2InputGrammarEpsilonJCheckBox = new JCheckBox();
	private JComboBox type2InputGrammarTerminalsJComboBox = new JComboBox();
	private JComboBox type2InputGrammarVariablesJComboBox = new JComboBox();
    private JPanel type2IsomorphismJPanel = new JPanel();
    private JCheckBox type2IsomorphismJCheckBox = new JCheckBox();
    private JSpinner type2IsoPercentJSpinner = new JSpinner();
    private JLabel type2IsoPercentJLabel = new JLabel();
	private JPanel type2OutputAutomatonJPanel = new JPanel();
	private JSpinner type2OutputAutomatonAlphabetMinJSpinner = new JSpinner();
	private JSpinner type2OutputAutomatonAlphabetMaxJSpinner = new JSpinner();
	private JSpinner type2OutputAutomatonStatesMinJSpinner = new JSpinner();
	private JSpinner type2OutputAutomatonTransitionsMinJSpinner = new JSpinner();
	private JSpinner type2OutputAutomatonStatesMaxJSpinner = new JSpinner();
	private JSpinner type2OutputAutomatonTransitionsMaxJSpinner = new JSpinner();
	private JSpinner type2OutputAutomatonUnreachableStatesMinJSpinner = new JSpinner();
	private JSpinner type2OutputAutomatonUnreachableStatesMaxJSpinner = new JSpinner();
	private JPanel type2InputAutomatonJPanel = new JPanel();
	private JLabel type2InputAutomatonAlphabetJLabel = new JLabel();
	private JLabel type2InputAutomatonStatesJLabel = new JLabel();
	private JLabel type2InputAutomatonFinalStatesJLabel = new JLabel();
	private JLabel type2InputAutomatonTransitionsJLabel = new JLabel();
	private JLabel type2InputAutomatonMinJLabel = new JLabel();
	private JSpinner type2InputAutomatonAlphabetMinJSpinner = new JSpinner();
	private JSpinner type2InputAutomatonStatesMinJSpinner = new JSpinner();
	private JSpinner type2InputAutomatonFinalStatesMinJSpinner = new JSpinner();
	private JSpinner type2InputAutomatonTransitionsMinJSpinner = new JSpinner();
	private JLabel type2InputAutomatonMaxJLabel = new JLabel();
	private JSpinner type2InputAutomatonAlphabetMaxJSpinner = new JSpinner();
	private JSpinner type2InputAutomatonStatesMaxJSpinner = new JSpinner();
	private JSpinner type2InputAutomatonFinalStatesMaxJSpinner = new JSpinner();
	private JSpinner type2InputAutomatonTransitionsMaxJSpinner = new JSpinner();
	private JComboBox type2InputAutomatonAlphabetJComboBox = new JComboBox();
	private JComboBox type2InputAutomatonStatesJComboBox = new JComboBox();
	private JLabel type2InputAutomatonUnreachableStatesJLabel = new JLabel();
	private JSpinner type2InputAutomatonUnreachableStatesMinJSpinner = new JSpinner();
	private JSpinner type2InputAutomatonUnreachableStatesMaxJSpinner = new JSpinner();
	private JPanel type2OutputGrammarJPanel = new JPanel();
	private JSpinner type2OutputGrammarVariablesMinJSpinner = new JSpinner();
	private JSpinner type2OutputGrammarTerminalsMinJSpinner = new JSpinner();
	private JSpinner type2OutputGrammarRulesMinJSpinner = new JSpinner();
	private JSpinner type2OutputGrammarVariablesMaxJSpinner = new JSpinner();
	private JSpinner type2OutputGrammarTerminalsMaxJSpinner = new JSpinner();
	private JSpinner type2OutputGrammarRulesMaxJSpinner = new JSpinner();
	private JCheckBox type2OutputGRammarEpsilonJCheckBox = new JCheckBox();
	private JPanel type3OperationJPanel = new JPanel();
	private JRadioButton type3OperationDFAToREJButton = new JRadioButton();
	private JRadioButton type3OperationREToDFAJButton = new JRadioButton();
	private JPanel type3InputAutomatonJPanel = new JPanel();
	private JLabel type3InputAutomatonStatesJLabel = new JLabel();
	private JLabel type3InputAutomatonUnreachableStatesJLabel = new JLabel();
	private JLabel type3InputAutomatonMinJLabel = new JLabel();
	private JLabel type3InputAutomatonMaxJLabel = new JLabel();
	private JLabel type3InputAutomatonFinalStatesJLabel = new JLabel();
	private JLabel type3InputAutomatonTransitionsJLabel = new JLabel();
	private JLabel type3InputAutomatonAlphabetJLabel = new JLabel();
	private JSpinner type3InputAutomatonStatesMinJSpinner = new JSpinner();
	private JSpinner type3InputAutomatonStatesMaxJSpinner = new JSpinner();
	private JSpinner type3InputAutomatonUnreachableStatesMinJSpinner = new JSpinner();
	private JSpinner type3InputAutomatonUnreachableStatesMaxJSpinner = new JSpinner();
	private JSpinner type3InputAutomatonFinalStatesMinJSpinner = new JSpinner();
	private JSpinner type3InputAutomatonFinalStatesMaxJSpinner = new JSpinner();
	private JSpinner type3InputAutomatonTransitionsMinJSpinner = new JSpinner();
	private JSpinner type3InputAutomatonTransitionsMaxJSpinner = new JSpinner();
	private JSpinner type3InputAutomatonAlphabetMinJSpinner = new JSpinner();
	private JSpinner type3InputAutomatonAlphabetMaxJSpinner = new JSpinner();
	private JComboBox type3InputAutomatonAlphabetJComboBox = new JComboBox();
	private JComboBox type3InputAutomatonStatesJComboBox = new JComboBox();
	private JPanel type3InputREJPanel = new JPanel();
	private JLabel type3InputREAlphabetJLabel = new JLabel();
	private JLabel type3InputREMinJLabel = new JLabel();
	private JLabel type3InputREMaxJLabel = new JLabel();
	private JSpinner type3InputREAlphabetMinJSpinner = new JSpinner();
	private JSpinner type3InputREAlphabetMaxJSpinner = new JSpinner();
	private JComboBox type3InputReAlphabetJComboBox = new JComboBox();
	private JLabel type3InputRELengthJLabel = new JLabel();
	private JSpinner type3InputRELengthMinJSpinner = new JSpinner();
	private JSpinner type3InputRELengthMaxJSpinner = new JSpinner();
	private JPanel type3OutputAutomatonJPanel = new JPanel();
	private JLabel type3OutputAutomatonStatesJLabel = new JLabel();
	private JLabel type3OutputAutomatonMinJLabel = new JLabel();
	private JLabel type3OutputAutomatonMaxJLabel = new JLabel();
	private JLabel type3OutputAutomatonTransitionsJLabel = new JLabel();
	private JSpinner type3OutputAutomatonStatesMinJSpinner = new JSpinner();
	private JSpinner type3OutputAutomatonTransitionsMinJSpinner = new JSpinner();
	private JSpinner type3OutputAutomatonStatesMaxJSpinner = new JSpinner();
	private JSpinner type3OutputAutomatonTransitionsMaxJSpinner = new JSpinner();
	private JComboBox type3OutputAutomatonStatesJComboBox = new JComboBox();
	private JPanel type3OutputREJPanel = new JPanel();
	private JLabel type3OutputREAlphabetJLabel = new JLabel();
	private JLabel type3OutputREMinJLabel = new JLabel();
	private JSpinner type3OutputREAlphabetMinJSpinner = new JSpinner();
	private JLabel type3OutputREMaxJLabel = new JLabel();
	private JSpinner type3OutputREAlphabetMaxJSpinner = new JSpinner();
	private JLabel type3OutputRELengthJLabel = new JLabel();
	private JSpinner type3OutputRELengthMinJSpinner = new JSpinner();
	private JSpinner type3OutputRELengthMaxJSpinner = new JSpinner();
	private JLabel type3InputREUnionJLabel = new JLabel();
	private JLabel type3InputREConcatenationJLabel = new JLabel();
	private JLabel type3InputREIterationJLabel = new JLabel();
	private JLabel type3InputREEpsJLabel = new JLabel();
	private JLabel type3InputREEmptySetJLabel = new JLabel();
    private JPanel type3IsomorphismJPanel = new JPanel();
    private JCheckBox type3IsomorphismJCheckBox = new JCheckBox();
    private JSpinner type3IsoPercentJSpinner = new JSpinner();
    private JLabel type3IsoPercentJLabel = new JLabel();

	private JSpinner type3inputREUnionMinJSpinner = new JSpinner();
	private JSpinner type3inputREConcatenationMinJSpinner = new JSpinner();
	private JSpinner type3inputREIterationMinJSpinner = new JSpinner();
	private JSpinner type3inputREEpsMinJSpinner = new JSpinner();
	private JSpinner type3inputREEmptySetMinJSpinner = new JSpinner();

	private JSpinner type3inputREUnionMaxJSpinner = new JSpinner();
	private JSpinner type3inputREConcatenationMaxJSpinner = new JSpinner();
	private JSpinner type3inputREIterationMaxJSpinner = new JSpinner();
	private JSpinner type3inputREEpsMaxJSpinner = new JSpinner();
	private JSpinner type3inputREEmptySetMaxJSpinner = new JSpinner();

	private JLabel type3OutputREUnionJLabel = new JLabel();
	private JLabel type3OutputREConcatenationJLabel = new JLabel();
	private JLabel type3OutputREIterationJLabel = new JLabel();

	private JSpinner type3OutputREUnionMinJSpinner = new JSpinner();
	private JSpinner type3OutputREConcatenationMinJSpinner = new JSpinner();
	private JSpinner type3OutputREIterationMinJSpinner = new JSpinner();

	private JSpinner type3OutputREUnionMaxJSpinner = new JSpinner();
	private JSpinner type3OutputREConcatenationMaxJSpinner = new JSpinner();
	private JSpinner type3OutputREIterationMaxJSpinner = new JSpinner();
	JCheckBox type3OutputREHasEpsJCheckBox = new JCheckBox();

	private GridBagConstraints gridBagConstraints;
	private JPanel settingsPane = new JPanel();

	private JPopupMenu disPopUpMenu = new JPopupMenu();
	private JMenuItem disMenuItem = new JMenuItem();

	private JSpinner[] allSpinners = { type0Automaton1AlphabetMinJSpinner, type0Automaton1StatesMinJSpinner,
		type0Automaton1FinalStatesMinJSpinner, type0Automaton1TransitionsMinJSpinner,
		type0Automaton1AlphabetMaxJSpinner, type0Automaton1StatesMaxJSpinner, type0Automaton1FinalStatesMaxJSpinner,
		type0Automaton1TransitionsMaxJSpinner, type0Automaton2AlphabetMinJSpinner, type0Automaton2StatesMinJSpinner,
		type0Automaton2FinalStatesMinJSpinner, type0Automaton2TransitionsMinJSpinner,
		type0Automaton2AlphabetMaxJSpinner, type0Automaton2StatesMaxJSpinner, type0Automaton2FinalStatesMaxJSpinner,
		type0Automaton2TransitionsMaxJSpinner, type0OutputAutomatonStatesMinJSpinner,
		type0OutputAutomatonStatesMaxJSpinner, type0OutputAutomatonTransitionsMinJSpinner,
		type0OutputAutomatonTransitionsMaxJSpinner, type1InputAutomatonStatesMinJSpinner,
		type1InputAutomatonFinalStatesMinJSpinner, type1InputAutomatonTransitionsMinJSpinner,
		type1InputAutomatonAlphabetMinJSpinner, type1InputAutomatonStatesMaxJSpinner,
		type1InputAutomatonFinalStatesMaxJSpinner, type1InputAutomatonTransitionsMaxJSpinner,
		type1InputAutomatonEpsTransitionsMinJSpinner, type1InputAutomatonEpsTransitionsMaxJSpinner,
		type1InputAutomatonAlphabetMaxJSpinner, type1InputAutomatonUnreachableStatesMinJSpinner,
		type1InputAutomatonUnreachableStatesMaxJSpinner, type1OutputAutomatonStatesMinJSpinner,
		type1OutputAutomatonStatesMaxJSpinner, type1OutputAutomatonTransitionsMinJSpinner,
		type1OutputAutomatonTransitionsMaxJSpinner, type1MinimalizationStepsMinJSpinner,
		type1MinimalizationStepsMaxJSpinner,
            type1IsoPercentJSpinner, type2IsoPercentJSpinner, type3IsoPercentJSpinner,
            type2InputGrammarVariablesMinJSpinner, type2InputGrammarRulesMinJSpinner,
		type2InputGrammarVariablesMaxJSpinner, type2InputGrammarRulesMaxJSpinner,
		type2InputGrammarTerminalsMinJSpinner, type2InputGrammarTerminalsMaxJSpinner,
		type2InputAutomatonAlphabetMinJSpinner, type2InputAutomatonStatesMinJSpinner,
		type2InputAutomatonFinalStatesMinJSpinner, type2InputAutomatonTransitionsMinJSpinner,
		type2InputAutomatonAlphabetMaxJSpinner, type2InputAutomatonStatesMaxJSpinner,
		type2InputAutomatonFinalStatesMaxJSpinner, type2InputAutomatonTransitionsMaxJSpinner,
		type2InputAutomatonUnreachableStatesMinJSpinner, type2InputAutomatonUnreachableStatesMaxJSpinner,
		type3InputAutomatonStatesMinJSpinner, type3InputAutomatonStatesMaxJSpinner,
		type3InputAutomatonUnreachableStatesMinJSpinner, type3InputAutomatonUnreachableStatesMaxJSpinner,
		type3InputAutomatonFinalStatesMinJSpinner, type3InputAutomatonFinalStatesMaxJSpinner,
		type3InputAutomatonTransitionsMinJSpinner, type3InputAutomatonTransitionsMaxJSpinner,
		type3InputAutomatonAlphabetMinJSpinner, type3InputAutomatonAlphabetMaxJSpinner,
		type3InputREAlphabetMinJSpinner, type3InputREAlphabetMaxJSpinner, type3InputRELengthMinJSpinner,
		type3InputRELengthMaxJSpinner, type3OutputAutomatonStatesMinJSpinner,
		type3OutputAutomatonTransitionsMinJSpinner, type3OutputAutomatonStatesMaxJSpinner,
		type3OutputAutomatonTransitionsMaxJSpinner, type3OutputREAlphabetMinJSpinner, type3OutputREAlphabetMaxJSpinner,
		type3OutputRELengthMinJSpinner, type3OutputRELengthMaxJSpinner, type2InputGrammarLoopsMaxJSpinner,
		type2InputGrammarLoopsMinJSpinner, type3inputREUnionMinJSpinner, type3inputREConcatenationMinJSpinner,
		type3inputREIterationMinJSpinner, type3inputREEpsMinJSpinner, type3inputREEmptySetMinJSpinner,
		type3inputREUnionMaxJSpinner, type3inputREConcatenationMaxJSpinner, type3inputREIterationMaxJSpinner,
		type3inputREEpsMaxJSpinner, type3inputREEmptySetMaxJSpinner, type3OutputREUnionMinJSpinner,
		type3OutputREConcatenationMinJSpinner, type3OutputREIterationMinJSpinner, type3OutputREUnionMaxJSpinner,
		type3OutputREConcatenationMaxJSpinner, type3OutputREIterationMaxJSpinner, };

	private JSpinner[][] errorSpinnerPairs = {
        { type0Automaton1AlphabetMinJSpinner, type0Automaton1AlphabetMaxJSpinner },
        { type0Automaton1FinalStatesMinJSpinner, type0Automaton1FinalStatesMaxJSpinner },
        { type0Automaton1TransitionsMinJSpinner, type0Automaton1TransitionsMaxJSpinner },
        { type0Automaton1StatesMinJSpinner, type0Automaton1StatesMaxJSpinner },
        { type0Automaton2AlphabetMinJSpinner, type0Automaton2AlphabetMaxJSpinner },
        { type0Automaton2StatesMinJSpinner, type0Automaton2StatesMaxJSpinner },
        { type0Automaton2FinalStatesMinJSpinner, type0Automaton2FinalStatesMaxJSpinner },
        { type0Automaton2TransitionsMinJSpinner, type0Automaton2TransitionsMaxJSpinner },
        { type0OutputAutomatonStatesMinJSpinner, type0OutputAutomatonStatesMaxJSpinner },
        { type0OutputAutomatonTransitionsMinJSpinner, type0OutputAutomatonTransitionsMaxJSpinner },
        { type0Automaton1AlphabetMinJSpinner, type0Automaton1TransitionsMaxJSpinner },
        { type0Automaton1AlphabetMinJSpinner, type0Automaton2TransitionsMaxJSpinner },
        { type1InputAutomatonStatesMinJSpinner, type1InputAutomatonStatesMaxJSpinner },
        { type1InputAutomatonFinalStatesMinJSpinner, type1InputAutomatonFinalStatesMaxJSpinner },
        { type1InputAutomatonTransitionsMinJSpinner, type1InputAutomatonTransitionsMaxJSpinner },
        { type1InputAutomatonAlphabetMinJSpinner, type1InputAutomatonAlphabetMaxJSpinner },
        { type1InputAutomatonAlphabetMinJSpinner, type1InputAutomatonTransitionsMaxJSpinner },
        { type1InputAutomatonUnreachableStatesMinJSpinner, type1InputAutomatonUnreachableStatesMaxJSpinner },
        { type1InputAutomatonEpsTransitionsMinJSpinner, type1InputAutomatonEpsTransitionsMaxJSpinner },
        { type1OutputAutomatonStatesMinJSpinner, type1OutputAutomatonStatesMaxJSpinner },
        { type1OutputAutomatonTransitionsMinJSpinner, type1OutputAutomatonTransitionsMaxJSpinner },
        { type1MinimalizationStepsMinJSpinner, type1MinimalizationStepsMaxJSpinner },
        { type0Automaton1FinalStatesMaxJSpinner, type0Automaton1StatesMinJSpinner },
        { type0Automaton2FinalStatesMaxJSpinner, type0Automaton2StatesMinJSpinner },
        { type3InputAutomatonFinalStatesMaxJSpinner, type3InputAutomatonStatesMinJSpinner },
        { type1InputAutomatonFinalStatesMaxJSpinner, type1InputAutomatonStatesMinJSpinner },
        { type2InputGrammarVariablesMinJSpinner, type2InputGrammarVariablesMaxJSpinner },
        { type2InputGrammarRulesMinJSpinner, type2InputGrammarRulesMaxJSpinner },
        { type2InputGrammarTerminalsMinJSpinner, type2InputGrammarTerminalsMaxJSpinner },
        { type2InputAutomatonAlphabetMinJSpinner, type2InputAutomatonAlphabetMaxJSpinner },
        { type2InputAutomatonStatesMinJSpinner, type2InputAutomatonStatesMaxJSpinner },
        { type2InputAutomatonFinalStatesMinJSpinner, type2InputAutomatonFinalStatesMaxJSpinner },
        { type2InputAutomatonTransitionsMinJSpinner, type2InputAutomatonTransitionsMaxJSpinner },
        { type2InputAutomatonUnreachableStatesMinJSpinner, type2InputAutomatonUnreachableStatesMaxJSpinner },
        { type2InputAutomatonFinalStatesMinJSpinner, type2InputAutomatonStatesMaxJSpinner },
        { type2InputAutomatonAlphabetMinJSpinner, type2InputAutomatonTransitionsMaxJSpinner },
        { type3InputAutomatonStatesMinJSpinner, type3InputAutomatonStatesMaxJSpinner },
        { type3InputAutomatonUnreachableStatesMinJSpinner, type3InputAutomatonUnreachableStatesMaxJSpinner },
        { type3InputAutomatonFinalStatesMinJSpinner, type3InputAutomatonFinalStatesMaxJSpinner },
        { type3InputAutomatonTransitionsMinJSpinner, type3InputAutomatonTransitionsMaxJSpinner },
        { type3InputAutomatonAlphabetMinJSpinner, type3InputAutomatonAlphabetMaxJSpinner },
        { type3InputREAlphabetMinJSpinner, type3InputREAlphabetMaxJSpinner },
        { type3OutputRELengthMinJSpinner, type3OutputRELengthMaxJSpinner },
        { type3InputRELengthMinJSpinner, type3InputRELengthMaxJSpinner },
        { type3OutputAutomatonStatesMinJSpinner, type3OutputAutomatonStatesMaxJSpinner },
        { type3OutputAutomatonTransitionsMinJSpinner, type3OutputAutomatonTransitionsMaxJSpinner },
        { type3OutputREAlphabetMinJSpinner, type3OutputREAlphabetMaxJSpinner },
        { type2InputGrammarLoopsMinJSpinner, type2InputGrammarLoopsMaxJSpinner },
        { type3inputREUnionMinJSpinner, type3inputREUnionMaxJSpinner },
        { type3inputREConcatenationMinJSpinner, type3inputREConcatenationMaxJSpinner },
        { type3inputREIterationMinJSpinner, type3inputREIterationMaxJSpinner },
        { type3inputREEpsMinJSpinner, type3inputREEpsMaxJSpinner },
        { type3inputREEmptySetMinJSpinner, type3inputREEmptySetMaxJSpinner },
        { type3OutputREUnionMinJSpinner, type3OutputREUnionMaxJSpinner },
        { type3OutputREConcatenationMinJSpinner, type3OutputREConcatenationMaxJSpinner },
        { type3OutputREIterationMinJSpinner, type3OutputREIterationMaxJSpinner }, };

	private String[] exclusions = {
            "typeJComboBox"
    };

	private List<String> exclusionsList;

	@Override
	public void afterInit()
	{
		this.setViewportView(settingsPane);
	}

	@Override
	public void onLocaleChanged(Locale newLocale)
	{
		resourceBundle = ResourceBundle.getBundle("regularLanguagesModule", newLocale);
		nameBundle = ResourceBundle.getBundle("CriteriaChecker", newLocale);
		updateStrings();
	}

	private void updateStrings()
	{
		String alphabet = resourceBundle.getString("Alphabet");
		String states = resourceBundle.getString("States");
		String statesWithoutUnreach = resourceBundle.getString("StatesWithoutUnreach");
		String transitions = resourceBundle.getString("Transitions");
		String minimal = resourceBundle.getString("Minimal");
		String maximal = resourceBundle.getString("Maximal");
		String finalStates = resourceBundle.getString("FinalStates");
		type0Automaton1JRadioButton.setText("L(A)");
		type0Automaton1CoJRadioButton.setText("co-L(A)");
		type0Automaton2JRadioButton.setText("L(B)");
		type0Automaton2CoJRadioButton.setText("co-L(B)");
		type0Automaton1AlphabetJLabel.setText(alphabet);
		type0Automaton1StatesJLabel.setText(states);
		type0Automaton1FinalStatesJLabel.setText(finalStates);
		type0Automaton1TransitionsJLabel.setText(transitions);
		type0Automaton1MinJLabel.setText(minimal);
		type0Automaton1MaxJLabel.setText(maximal);
		type0Automaton1TotalTransitionFunctionJCheckBox.setText(resourceBundle.getString("TotalTransitionFunction"));
		// type0Automaton2AlphabetJLabel.setText(alphabet);
		type0Automaton2FinalStatesJLabel.setText(finalStates);
		type0Automaton2StatesJLabel.setText(states);
		type0Automaton2TransitionsJLabel.setText(transitions);
		type0Automaton2MinJLabel.setText(minimal);
		type0Automaton2MaxJLabel.setText(maximal);
		type0Automaton2TotalTransitionFunctionJCheckBox.setText(resourceBundle.getString("TotalTransitionFunction"));
		type0OutputAutomatonStatesJLabel.setText(states);
		type0OutputAutomatonTransitionsJLabel.setText(transitions);
		type0OutputAutomatonMinJLabel.setText(minimal);
		type0OutputAutomatonMaxJLabel.setText(maximal);
		type0OutputAutomatonMinOneFinalStateJCheckBox.setText(resourceBundle.getString("MinOneFinalState"));
		type1OperationFromJLabel.setText(resourceBundle.getString("InputAutomatonType"));
		type1OperationToJLabel.setText(resourceBundle.getString("OutputAutomatonType"));
		type1InputAutomatonStatesJLabel.setText(statesWithoutUnreach);
		type1InputAutomatonFinalStatesJLabel.setText(finalStates);
		type1InputAutomatonTransitionsJLabel.setText(resourceBundle.getString("TransitionsNoEps"));
		type1InputAutomatonAlphabetJLabel.setText(alphabet);
		type1InputAutomatonUnreachableStatesJLabel.setText(resourceBundle.getString("UnreachableStates"));
		type1InputAutomatonMinJLabel.setText(minimal);
		type1InputAutomatonMaxJLabel.setText(maximal);
		type1InputAutomatonFirstStateJCheckBox.setText(resourceBundle.getString("FirstStateNotInitial"));
		type1InputAutomatonTotalTransitionFunctionJCheckBox
			.setText(resourceBundle.getString("TotalTransitionFunction"));
		type1OutputAutomatonStatesJLabel.setText(states);
		type1OutputAutomatonTransitionsJLabel.setText(transitions);
		type1OutputAutomatonMinJLabel.setText(minimal);
		type1OutputAutomatonMaxJLabel.setText(maximal);
		type1MinimalizationStepsJLabel.setText(resourceBundle.getString("Steps"));
		type1MinimalizationMinJLabel.setText(minimal);
		type1MinimalizationMaxJLabel.setText(maximal);
		type1MinimalizationStepsJCheckBox.setText(resourceBundle.getString("WriteOut"));
        type1IsomorphismJCheckBox.setText(resourceBundle.getString("IsomorphismTest"));
        type1IsoPercentJLabel.setText(resourceBundle.getString("IsoPercent"));
		type2OperationGrammarToNFAJButton.setText(resourceBundle.getString("REGNFATransformation"));// "regular grammar -> NFA");
		type2OperationNFAToGrammarJButton.setText(resourceBundle.getString("NFAREGTransformation"));// "NFA  -> regular grammar");
		type2InputGrammarTerminalsJLabel.setText(resourceBundle.getString("Terminals"));
		type2InputGrammarNonTerminalsJLabel.setText(resourceBundle.getString("NonTerminals"));
		type2InputGrammarRulesJLabel.setText(resourceBundle.getString("Rules"));
		type2InputGrammarMinJLabel.setText(minimal);
		type2InputGrammarMaxJLabel.setText(maximal);
		type2InputGrammarEpsilonJCheckBox.setText(resourceBundle.getString("EpsilonRule"));
        type2IsomorphismJCheckBox.setText(resourceBundle.getString("IsomorphismTest"));
        type2IsoPercentJLabel.setText(resourceBundle.getString("IsoPercent"));
		type2InputAutomatonAlphabetJLabel.setText(alphabet);
		type2InputAutomatonStatesJLabel.setText(statesWithoutUnreach);
		type2InputAutomatonFinalStatesJLabel.setText(finalStates);
		type2InputAutomatonTransitionsJLabel.setText(transitions);
		type2InputAutomatonUnreachableStatesJLabel.setText(resourceBundle.getString("UnreachableStates"));
		type2InputAutomatonMinJLabel.setText(minimal);
		type2InputAutomatonMaxJLabel.setText(maximal);

		type3InputAutomatonUnreachableStatesJLabel.setText(resourceBundle.getString("UnreachableStates"));

		type3OperationDFAToREJButton.setText(resourceBundle.getString("DFARETransformation"));// "DFA -> RE");
		type3OperationREToDFAJButton.setText(resourceBundle.getString("REDFATransformation"));// "RE  -> DFA");
		type3InputAutomatonAlphabetJLabel.setText(alphabet);
		type3InputAutomatonStatesJLabel.setText(statesWithoutUnreach);
		type3InputAutomatonTransitionsJLabel.setText(transitions);
		type3InputAutomatonFinalStatesJLabel.setText(finalStates);
		type3InputAutomatonMinJLabel.setText(minimal);
		type3InputAutomatonMaxJLabel.setText(maximal);
		type3OutputREAlphabetJLabel.setText(alphabet);
		type3OutputRELengthJLabel.setText(resourceBundle.getString("Length"));
		type3OutputREMinJLabel.setText(minimal);
		type3OutputREMaxJLabel.setText(maximal);
		type3InputREAlphabetJLabel.setText(alphabet);
		type3InputRELengthJLabel.setText(resourceBundle.getString("Length"));
		type3InputREMinJLabel.setText(minimal);
		type3InputREMaxJLabel.setText(maximal);
		type3OutputAutomatonStatesJLabel.setText(states);
		type3OutputAutomatonTransitionsJLabel.setText(transitions);
		type3OutputAutomatonMinJLabel.setText(minimal);
		type3OutputAutomatonMaxJLabel.setText(maximal);
		type2InputGrammarLoopsJLabel.setText(resourceBundle.getString("NonterminalLoops"));
		type2InputAutomatonInitialStateFinal.setText(resourceBundle.getString("InitialStateFinal"));
		type3InputREUnionJLabel.setText(resourceBundle.getString("Union"));
		type3InputREConcatenationJLabel.setText(resourceBundle.getString("Concatenation"));
		type3InputREIterationJLabel.setText(resourceBundle.getString("Iteration"));
		type3InputREEpsJLabel.setText(resourceBundle.getString("Epsilon"));
		type3InputREEmptySetJLabel.setText(resourceBundle.getString("EmptySets"));
		type1InputAutomatonEpsTransitionsJLabel.setText(resourceBundle.getString("epsTransitions"));

		type3OutputREUnionJLabel.setText(resourceBundle.getString("Union"));
		type3OutputREConcatenationJLabel.setText(resourceBundle.getString("Concatenation"));
		type3OutputREIterationJLabel.setText(resourceBundle.getString("Iteration"));
		type3OutputREHasEpsJCheckBox.setText(resourceBundle.getString("HasEpsilon"));
        type3IsomorphismJCheckBox.setText(resourceBundle.getString("IsomorphismTest"));
        type3IsoPercentJLabel.setText(resourceBundle.getString("IsoPercent"));

		// Titled borders
		transformationTypePane.setBorder(BorderFactory.createTitledBorder(resourceBundle
			.getString("TransformationType")));
		type0OperationJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("Operation")));
		type0Automaton1JPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("Automaton") + " A"));
		type0Automaton2JPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("Automaton") + " B"));
		type0OutputAutomatonJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle
			.getString("OutputAutomaton")));
		type1OperationJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("Operation")));
		type1InputAutomatonJPanel
			.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("InputAutomaton")));
		type1OutputAutomatonJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle
			.getString("OutputAutomaton")));
		type1MinimalizationJPanel
			.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("Minimalization")));
        type1IsomorphismJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("CorrectAlgorithmTest")));
		type2OperationJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("Operation")));
		type2InputGrammarJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("InputGrammar")));
		type2OutputAutomatonJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle
			.getString("OutputAutomaton")));
		type2InputAutomatonJPanel
			.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("InputAutomaton")));
		type2OutputGrammarJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("OutputGrammar")));
        type2IsomorphismJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("CorrectAlgorithmTest")));
		type3OperationJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("Operation")));
		type3InputAutomatonJPanel
			.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("InputAutomaton")));
		type3OutputREJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("OutputRegex")));
		type3InputREJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("InputRegex")));
		type3OutputAutomatonJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle
			.getString("OutputAutomaton")));
        type3IsomorphismJPanel.setBorder(BorderFactory.createTitledBorder(resourceBundle.getString("CorrectAlgorithmTest")));

		// because model is changed, previous state has to be preserved
		int type0selectedOperation = type0OperationJComboBox.getSelectedIndex();
		type0OperationJComboBox.setModel(new DefaultComboBoxModel(new String[] {
			" " + resourceBundle.getString("Union"), " " + resourceBundle.getString("Intersection"),
			" " + resourceBundle.getString("Difference") }));
		if (type0selectedOperation != -1)
			type0OperationJComboBox.setSelectedIndex(type0selectedOperation);

		int selectedTransformation = typeJComboBox.getSelectedIndex();
		typeJComboBox.setModel(new DefaultComboBoxModel(new String[] { " " + resourceBundle.getString("SynchParComp"),
			" " + resourceBundle.getString("VariousFATransformation"),
			" " + resourceBundle.getString("AutomataGrammarEquiv"),
			" " + resourceBundle.getString("RegexAutomataEquiv") }));
		if (selectedTransformation != -1)
			typeJComboBox.setSelectedIndex(selectedTransformation);

		int type1selectedFrom = type1OperationFromJComboBox.getSelectedIndex();
		type1OperationFromJComboBox.setModel(new DefaultComboBoxModel(new String[] {
			" " + resourceBundle.getString("NFAeps"), " " + resourceBundle.getString("NFA"),
			" " + resourceBundle.getString("DFA"), " " + resourceBundle.getString("minimalDFA") }));
		if (type1selectedFrom != -1)
			type1OperationFromJComboBox.setSelectedIndex(type1selectedFrom);

		int type1selectedTo = type1OperationToJComboBox.getSelectedIndex();
		type1OperationToJComboBox.setModel(new DefaultComboBoxModel(new String[] {
			" " + resourceBundle.getString("NFA"), " " + resourceBundle.getString("DFA"),
			" " + resourceBundle.getString("minimalDFA"), " " + resourceBundle.getString("canonicalDFA") }));
		if (type1selectedTo != -1)
			type1OperationToJComboBox.setSelectedIndex(type1selectedTo);

		for (JSpinner s : allSpinners)
		{
			s.setToolTipText("<html><p style=\"font:10px Tahoma\">" +nameBundle.getString(s.getName())+" </html>");
		}
	}

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

	/**
	 * shows the warning dialog pane and resets the stop and generate button
	 * 
	 * @param message
	 *            the cause of the proposition
	 */
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

		for (Field field : getClass().getDeclaredFields()) {
		    if (exclusionsList.indexOf(field.getName()) == -1) {
                if (field.getType().isAssignableFrom(JCheckBox.class)) {
                    try {
                        JCheckBox cb = (JCheckBox) field.get(this);
                        settings.put(field.getName(), cb.isSelected() ? 1 : 0);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else if (field.getType().isAssignableFrom(JRadioButton.class)) {
                    try {
                        JRadioButton rb = (JRadioButton) field.get(this);
                        settings.put(field.getName(), rb.isSelected() ? 1 : 0);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else if (field.getType().isAssignableFrom(JComboBox.class)) {
                    try {
                        JComboBox cb = (JComboBox) field.get(this);
                        settings.put(field.getName(), cb.getSelectedIndex());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

		return settings;
	}
	@Override
	public void loadSettings(Map<String, Integer> settings)
	{
	    System.out.println("loading");
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

        for (Field field : getClass().getDeclaredFields()) {
            if (exclusionsList.indexOf(field.getName()) == -1) {
                if (settings.get(field.getName()) != null) {
                    if (field.getType().isAssignableFrom(JCheckBox.class)) {
                        try {
                            JCheckBox cb = (JCheckBox) field.get(this);
                            cb.setSelected(settings.get(field.getName()) == 1);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else if (field.getType().isAssignableFrom(JRadioButton.class)) {
                        try {
                            JRadioButton cb = (JRadioButton) field.get(this);
                            cb.setSelected(settings.get(field.getName()) == 1);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else if (field.getType().isAssignableFrom(JComboBox.class)) {
                        try {
                            JComboBox cb = (JComboBox) field.get(this);
                            cb.setSelectedIndex(settings.get(field.getName()));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        activateTransformationPanel(activePanelNumber);
	}
}
