/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

import generator.core.FormalLanguagesExampleGenerator;
import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.Form;
import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.RequirementsNotFulfilledException;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.TooManyRulesException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author drasto, bafco
 */
public class GeneratorManager
{

	private Set<SimpleCriteria> crit = new HashSet<SimpleCriteria>();
	private CriteriaEvaluator evaluator;
	private Set<Form> inputProp;
	private InputGrammarForm inputForm;
	private OutputGrammarForm outputForm;
	private int maxNsize;
	private int maxTerminalsCount;
	private int maxRuleLength;
	private int minRulesNumber;
	private Boolean sOnRight;
	private Boolean sToEpsilon;
	public static final GeneratorSettings DEFAULT_GENERATOR_SETTINGS = new GeneratorSettings(30, 35, 35, 30, 25, 25);
	public static final int STANDARD_MAX_N_SIZE = 26;
	public static final int STANDARD_TERMINALS_COUNT = 26;
	public static final int MINIMAL_TERMINALS_COUNT = 2;
	public static final int STANDARD_MAX_RULE_LENGTH = 4;

	private Set<String> allGeneratedGrammars = new HashSet<String>();

	public GeneratorManager(Set<SimpleCriteria> crit, AlgorithmType[] algorithmsToUse, InputGrammarForm inputForm,
		OutputGrammarForm outputForm, Integer maxRuleLength, Integer maxNonTermNumber, Integer minLinesNumber,
		Integer minRulesNumber, Integer maxTerminalsCount, Boolean sToEpsilon, Boolean sOnRight)
	{
		if (crit == null)
		{
			throw new NullPointerException("crit");
		}
		if (algorithmsToUse == null)
		{
			throw new NullPointerException("algorithmsToUse");
		}
		if (inputForm == null)
		{
			throw new NullPointerException("inputForm");
		}
		if (outputForm == null)
		{
			throw new NullPointerException("outputForm");
		}
		if (algorithmsToUse.length < 1)
		{
			throw new IllegalArgumentException(
				"algorithmToUse must be at least one element long - otherwise there is no algorithm to use");
		}

		this.inputProp = inputForm.getAttributes();
		this.inputForm = inputForm;
		this.outputForm = outputForm;

		if (maxRuleLength == null)
		{
			maxRuleLength = STANDARD_MAX_RULE_LENGTH;
		}

		if (maxNonTermNumber == null && minLinesNumber == null)
		{
			maxNsize = STANDARD_MAX_N_SIZE;
		}
		else if (maxNonTermNumber == null)
		{
			maxNsize = Math.max(minLinesNumber * 2, minLinesNumber + 10);
		}
		else
		{
			maxNsize = maxNonTermNumber;
		}

		if (maxTerminalsCount != null)
		{
			if (maxTerminalsCount < MINIMAL_TERMINALS_COUNT)
			{
				throw new IllegalArgumentException("max terminal number should be greater then or equal to "
					+ MINIMAL_TERMINALS_COUNT);
			}
		}
		else
		{
			maxTerminalsCount = STANDARD_TERMINALS_COUNT;
		}

		// setBooleans(sToEpsilon, sOnRight);

		this.crit = new HashSet<SimpleCriteria>();
		this.crit.addAll(crit);

		this.maxTerminalsCount = maxTerminalsCount;
		this.maxRuleLength = maxRuleLength;
		this.minRulesNumber = minRulesNumber;
		this.sToEpsilon = sToEpsilon;
		this.sOnRight = sOnRight;

		evaluator = new BasicCriteriaEvaluator(algorithmsToUse, this.crit, allGeneratedGrammars);

	}

	public List<GrammarOrderAndAlg> generateGrammar()
	{

		ContextFreeGrammar<ContextFreeRule> current = null;
		GrammarGenerator generator = null;
		Set<SimpleCriteria> lowerProblematic = new HashSet<SimpleCriteria>();

		while (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE)
		{

			boolean allOK = false;
			boolean maxOK = true;
			BooleanPaar booleans = getBooleans();
			generator = new BasicGenerator(inputForm, outputForm, maxNsize, maxTerminalsCount, maxRuleLength,
				booleans.isSToEpsilon(), booleans.isSOnRight(), DEFAULT_GENERATOR_SETTINGS);

			lowerProblematic.clear();

			while (!allOK && maxOK)
			{

				// in case that generation has been canceled
				if (!FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE)
				{
					Thread.currentThread().interrupt();
					maxOK = true;
					break;
				}

				try
				{
					generator.addRule();
					while ((generator.getCurrentVersion().getRules().size() < minRulesNumber))
					{
						generator.addRule();
					}
				}
				catch (TooManyRulesException ex)
				{
					maxOK = false;
					break;
				}

				current = generator.getCurrentVersion();
				try
				{
					allOK = evaluator.evaluate(current, inputProp, generator.getCurrentOrder());
				}
				catch (RequirementsNotFulfilledException ex)
				{
					Logger.getLogger(GeneratorManager.class.getName()).log(Level.SEVERE, null, ex);
					throw new RuntimeException(ex);
				}

				maxOK = evaluator.shouldTryToAddMoreRules();

				if (maxOK)
				{
					lowerProblematic.addAll(evaluator.getLowerNotFulfilled());
				}
			}

			if (maxOK)
			{
				break;
			}
			else
			{
				for (SimpleCriteria c : evaluator.getUpperNotFulfilled())
				{
					c.wasNotFulfilled();
				}
				for (SimpleCriteria c : lowerProblematic)
				{
					c.wasNotFulfilled();
				}
			}
		}

		if (generator == null)
		{
			return null;
		}

		allGeneratedGrammars.add(current.toString());
		return evaluator.getResult();
	}

	private static class BooleanPaar
	{
		private boolean sToEpsilon;
		private boolean sOnRight;

		public BooleanPaar(Boolean sToEpsilon, Boolean sOnRight)
		{
			if (sOnRight == null)
			{
				throw new NullPointerException("sOnRight");
			}
			if (sToEpsilon == null)
			{
				throw new NullPointerException("sToEpsilon");
			}
			this.sToEpsilon = sToEpsilon;
			this.sOnRight = sOnRight;
		}

		public boolean isSOnRight()
		{
			return sOnRight;
		}

		public boolean isSToEpsilon()
		{
			return sToEpsilon;
		}
	}

	private BooleanPaar getBooleans()
	{

		Boolean sToEpsilonLoc = this.sToEpsilon;
		Boolean sOnRightLoc = this.sOnRight;
		Random r = new Random();
		int option = 0;
		if (inputProp.contains(Form.NO_EPSILON))
		{
			if (sToEpsilonLoc == null && sOnRightLoc == null)
			{
				option = r.nextInt(2);
				switch (option)
				{
					case 0:
						sToEpsilonLoc = true;
						sOnRightLoc = false;
						break;
					case 1:
						sToEpsilonLoc = false;
						sOnRightLoc = true;
						break;
					// treba vobec tuto moznost?
					// case 2:
					// sToEpsilonLoc = false;
					// sOnRightLoc = false;
					// break;
					default:
						throw new IllegalStateException("no other options should happen");
				}
			}
			else if (sToEpsilonLoc == false)
			{
				// sOnRightLoc = r.nextBoolean();
				sOnRightLoc = true;
			}
			else if (sToEpsilonLoc == true)
			{
				sOnRightLoc = false;
			}
			else if (sOnRightLoc == true)
			{
				sToEpsilonLoc = false;
			}
			else if (sOnRightLoc == false)
			{
				sToEpsilonLoc = r.nextBoolean();
			}
			else
			{
				throw new IllegalStateException(
					"Unexpected situation: values of 2 booleans are in impossible combination");
			}
		}
		else
		{
			if (sOnRightLoc == null)
			{
				// sOnRightLoc = r.nextBoolean();
				sOnRightLoc = true;
			}
			if (sToEpsilonLoc == null)
			{
				sToEpsilonLoc = r.nextBoolean();
			}
		}

		return new BooleanPaar(sToEpsilonLoc, sOnRightLoc);
	}

}
