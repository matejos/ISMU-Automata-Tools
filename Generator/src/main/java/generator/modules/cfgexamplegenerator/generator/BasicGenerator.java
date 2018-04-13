/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.Form;
import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.SetList;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.NonTerminal;
import generator.modules.cfgexamplegenerator.grammarreprezentation.SimpleContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.Symbol;
import generator.modules.cfgexamplegenerator.grammarreprezentation.Terminal;
import generator.modules.cfgexamplegenerator.grammarreprezentation.TooManyRulesException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

/**
 * @author Rastislav Mirek, Matus Abaffy at Masaryk University, Brno, Czech Republic
 * @mail rmirek@mail.muni.cz\
 * @version Expression version i s undefined on line 14, column 15 in Templates/Classes/Class.java.
 * @copyright Rastislav Mirek, Matus Abaffy all rights reserved
 */
@SuppressWarnings("unchecked")
public class BasicGenerator implements GrammarGenerator
{

	public static final String startName = "S";
	public static final int randomRefreshTime = 1000;
	private Set<Form> inputProp = new HashSet<Form>();
	// private InputGrammarForm inputForm;
	// private OutputGrammarForm outputForm;
	// private List<AlgorithmType> algsToRunThrough;
	private int maxRuleLength;
	// private int maxNsize;
	private NonTerminal[] nonTerminals;
	private Set<Terminal> terminals = new HashSet<Terminal>();
	private List<NonTerminal> allowedLeft = new SetList<NonTerminal>(); // týka sa Form.NO_UNREACHABLE - ak sa vyžaduje,
																		// sú tam iba dosažiteľné neterminály, inak
																		// všetky
	private List<Symbol> allowedRight = new ArrayList<Symbol>();
	private List<Symbol> allowedOnlyOneRight = new ArrayList<Symbol>();
	private Set<ContextFreeRule> terminationRules = new HashSet<ContextFreeRule>();

	@SuppressWarnings("rawtypes")
	private List<NonTerminal> leftOrder = new ArrayList();
	@SuppressWarnings("rawtypes")
	private List<Symbol> terminators = new SetList();
	private OneRuleGen gen;
	private NonTerminal startSymbol;
	private Stack<Step> steps = new Stack<Step>();
	private List<Symbol> terminatedSymbols = new ArrayList<Symbol>();// terminaly a normovane neterminaly, ktore mozu
																		// byt pouzite na normovanie dalsich;
	// epsilon sem nikdy nepatri; v pripade NO_EPS a existencie pravidla 'S -> eps' medzi ne S nepatri
	// private GeneratorSettings settings;
	private ContextFreeGrammar<ContextFreeRule> grammar = new SimpleContextFreeGrammar<ContextFreeRule>();
	private static Random random = new Random();
	private static long randomGetTime = System.nanoTime();

	@SuppressWarnings("unused")
	private BasicGenerator(final Set<Form> inputProp, final int maxRuleLength, final int maxNsize,
		final NonTerminal[] nonTerminals, final Set<Terminal> terminals, final OneRuleGen gen,
		final NonTerminal startSymbol, ContextFreeGrammar<ContextFreeRule> grammar, List<NonTerminal> allowedLeft,
		List<Symbol> allowedRight, List<Symbol> allowedOnlyOneRight, Set<ContextFreeRule> terminationRules,
		List<NonTerminal> leftOrder, List<Symbol> terminators, GeneratorSettings settings)
	{
		this.inputProp = inputProp;
		this.maxRuleLength = maxRuleLength;
		// this.maxNsize = maxNsize;
		this.nonTerminals = nonTerminals;
		this.terminals = terminals;
		this.gen = gen;
		this.startSymbol = startSymbol;
		this.grammar = grammar.clone();
		this.allowedLeft.addAll(allowedLeft);
		this.allowedRight = allowedRight;
		this.allowedOnlyOneRight = allowedOnlyOneRight;
		this.terminationRules.addAll(terminationRules);
		this.leftOrder = leftOrder;
		this.terminators = terminators;
		// this.settings = settings;
	}

	protected class Step
	{

		private Set<NonTerminal> allowedLeftNew;
		private Set<ContextFreeRule> terminationRulesNew;
		private Set<ContextFreeRule> terminationRuleDeleted;
		private Set<ContextFreeRule> grammarAdded;
		private Set<ContextFreeRule> grammarDeleted;

		protected Step(Set<NonTerminal> allowedLeftNew, Set<ContextFreeRule> terminationRulesNew,
			Set<ContextFreeRule> terminationRuleDeleted, Set<ContextFreeRule> grammarAdded,
			Set<ContextFreeRule> grammarDeleted)
		{
			this.allowedLeftNew = allowedLeftNew;
			this.terminationRulesNew = terminationRulesNew;
			this.terminationRuleDeleted = terminationRuleDeleted;
			this.grammarAdded = grammarAdded;
			this.grammarDeleted = grammarDeleted;
		}

		public Set<NonTerminal> getAllowedLeftNew()
		{
			return allowedLeftNew;
		}

		public Set<ContextFreeRule> getGrammarAdded()
		{
			return grammarAdded;
		}

		public Set<ContextFreeRule> getGrammarDeleted()
		{
			return grammarDeleted;
		}

		public Set<ContextFreeRule> getTerminationRuleDeleted()
		{
			return terminationRuleDeleted;
		}

		public Set<ContextFreeRule> getTerminationRulesNew()
		{
			return terminationRulesNew;
		}
	}

	// jediny sposob, ako pri inputProp.contains(NO_EPSILON) povolit (zaroven vynutit) pravidlo 'S -> eps', je cez
	// konstruktor ->
	// -> sToEpsilon nastavit na TRUE a sOnRight na FALSE
	public BasicGenerator(InputGrammarForm inputForm, OutputGrammarForm outputForm, int maxNsize, int terminalsCount,
		int maxRuleLength, boolean sToEpsilon, boolean sOnRight, GeneratorSettings settings)
	{
		if (inputForm == null)
		{
			throw new NullPointerException("inputForm");
		}
		if (outputForm == null)
		{
			throw new NullPointerException("outputForm");
		}
		Set<Form> inputProp = inputForm.getAttributes();
		if (terminalsCount < 1)
		{
			throw new IllegalArgumentException("terminalsCount must be greater then zero");
		}
		if (maxRuleLength < 1)
		{
			throw new IllegalArgumentException("maxRuleLength must be greater then zero");
		}
		if (maxNsize <= 0)
		{
			throw new IllegalArgumentException("maxNsize must be greater then zero");
		}
		if (sToEpsilon && sOnRight && inputProp.contains(Form.NO_EPSILON))
		{
			throw new InternalError(
				"Grammar should be without epsilon rules and still there is S-epsilon rule and S could be on right side of some "
					+ "rule");
		}
		if (settings == null)
		{
			settings = new GeneratorSettings(0, 0, 0, 10, 10, 10);
		}
		else
		{
			// this.settings = settings;
		}
		if ((inputProp.contains(Form.NO_LEFT_RECURSION) || inputProp.contains(Form.NOT_CYCLIC))// &&
																								// !inputProp.contains(Form.NO_SIMPLE_RULES)
			&& (!inputProp.contains(Form.NO_EPSILON) // || !inputProp.contains(Form.NO_UNREACHABLE) ||
														// !inputProp.contains(Form.NO_NONGENERATING)
			))
		{
			// throw new
			// InternalError("no left recursion however simple rules allowed and one of {epsilon rules allowed, unreachable symbols allowed,"
			// +
			// "not-normalized symbols allowed} is not correct form of grammar.");
			throw new InternalError("Epsilon-productions allowed and one of {no left recursion, not cyclic} required"
				+ "is not correct form of grammar.");
		}

		// this.inputForm = inputForm;
		// this.outputForm = outputForm;
		// algsToRunThrough = Arrays.asList(AlgorithmHierarchii.between(inputForm, outputForm));
		// this.maxNsize = maxNsize;
		this.maxRuleLength = maxRuleLength;
		nonTerminals = new NonTerminal[maxNsize];
		ContextFreeGrammar<ContextFreeRule> helpGram = new SimpleContextFreeGrammar<ContextFreeRule>();
		nonTerminals[0] = SymbolManager.getNewN(helpGram, startName);
		startSymbol = nonTerminals[0];
		grammar.setStartSymbol(startSymbol);
		helpGram.setStartSymbol(startSymbol);

		for (int i = 1; i < maxNsize; i++)
		{
			nonTerminals[i] = SymbolManager.getNewN(helpGram);
			helpGram.addNonTerminal(nonTerminals[i]);
		}

		terminals.addAll(SymbolManager.getTerminalSet(terminalsCount));
		allowedRight.addAll(terminals);
		for (NonTerminal n : nonTerminals)
		{
			allowedRight.add(n);
		}
		if (!sOnRight)
		{
			allowedRight.remove(startSymbol);
		}
		allowedOnlyOneRight.addAll(terminals);

		this.inputProp.addAll(inputProp);
		if (inputProp.contains(Form.NOT_CYCLIC) || inputProp.contains(Form.NO_LEFT_RECURSION))
		{
			setOrder();
			this.inputProp.add(Form.NO_EPSILON);
		}
		else
		{
			leftOrder = null;
		}

		if (inputProp.contains(Form.NO_UNREACHABLE))
		{
			allowedLeft.add(startSymbol);
		}
		else
		{
			for (NonTerminal n : nonTerminals)
			{
				allowedLeft.add(n);
			}
		}
		if (!inputProp.contains(Form.NO_SIMPLE_RULES))
		{
			for (NonTerminal n : nonTerminals)
			{
				allowedOnlyOneRight.add(n);
			}
			if (!sOnRight)
			{
				allowedOnlyOneRight.remove(startSymbol);
			}
		}
		if (!inputProp.contains(Form.NO_EPSILON))
		{
			allowedOnlyOneRight.add(Symbol.EPSILON);
		}

		gen = new OneRuleGen(inputProp.contains(Form.NO_SIMPLE_RULES), allowedOnlyOneRight, settings);

		terminators.addAll(terminals);
		if (!inputProp.contains(Form.NO_EPSILON))
		{
			terminators.add(Symbol.EPSILON);
		}

		// jedine miesto, kde moze vzniknut pravidlo 'S -> epsilon' v pripade, ze inputProp.contains(NO_EPSILON)
		if (sToEpsilon)
		{
			List<NonTerminal> left = new ArrayList<NonTerminal>(1);
			List<Symbol> right = new ArrayList<Symbol>();
			left.add(startSymbol);
			right.add(Symbol.EPSILON);
			ContextFreeRule r = gen.makeRule(1, left, right);
			try
			{
				grammar.addRuleAutoAddSymbols(r);
			}
			catch (TooManyRulesException ex)
			{
				throw new RuntimeException(
					"this is first rule added to grammar - max number of rules should not be exceeded", ex);
			}
			terminatedSymbols.addAll(terminals);
			if (sOnRight)
			{
				terminatedSymbols.add(startSymbol);
			}
		}
		else
		{
			// zarucit, ze gramatika nebude generovat prazdny jazyk
			try
			{
				if (!inputProp.contains(Form.NO_UNREACHABLE))
				{
					// NO_UNREACHABLE sa vyzaduje prave vtedy, ked sa vyzaduje aj NO_NONGENERATING
					// ak sa vyzaduje NO_UNREACHABLE, tak urcite aj NO_NONGENERATING,
					// a teda "je jedno", na aky najmensi pocet krokov sa vie prepisat koren gramatiky na terminalny
					// retazec
					// najprv sa terminuju nahodne neterminaly, aby sa S nemuselo terminovat v 1 kroku
					List<NonTerminal> helpList = new ArrayList<NonTerminal>(nonTerminals.length - 1);
					List<NonTerminal> nonTsToTerminate = new ArrayList<NonTerminal>(nonTerminals.length - 1);
					for (int i = 1; i < nonTerminals.length; i++)
					{
						helpList.add(nonTerminals[i]);
					}
					while (!helpList.isEmpty())
					{
						nonTsToTerminate.add(helpList.remove(getRandom(helpList.size())));
					}
					int max = nonTerminals.length * nonTerminals.length;
					int chance = max;// (initially) add up to 2*nonTerminals.length - 1 to increase probability od
										// terminating nonterminals
					int reducer = 2 * nonTerminals.length - 1;
					for (NonTerminal n : nonTsToTerminate)
					{
						chance -= reducer;
						reducer -= 2;
						if (chance > random.nextInt(max))
						{
							terminateNonTerminal(n);
						}
					}
				}
				// ContextFreeRule r = terminateNonTerminal(startSymbol);
				if (!sOnRight)
				{
					terminatedSymbols.remove(startSymbol);
					// aby sa S nemohlo pouzit na terminovanie ostatnych neterminalov
				}
			}
			catch (TooManyRulesException ex)
			{
				throw new RuntimeException(
					"this is one of first rules added to grammar - max number of rules should not be exceeded", ex);
			}
		}
	}

	protected ContextFreeRule terminateNonTerminal(NonTerminal n) throws TooManyRulesException
	{
		if (n == null)
		{
			throw new NullPointerException("nonTerminal");
		}
		List<NonTerminal> left = new ArrayList<NonTerminal>();
		left.add(n);
		int length = computeRuleLength();
		ContextFreeRule r;

		if (terminatedSymbols.isEmpty())
		{ // terminatedSymbols.isEmpty() ... keď sa generuje úplne prvé pravidlo
			terminatedSymbols.addAll(terminals);
			if (length == 1)
			{
				r = gen.makeRule(1, left, terminators);
			}
			else
			{
				r = gen.makeRule(length, left, new ArrayList<Symbol>(terminals));
			}
		}
		else
		{
			// pre pripad, ked S nemoze byt na pravej strane (NO_EPSILON a zaroven existuje pravidlo 'S -> eps')
			// - osetrene inak - v pripade, ze sa vyzaduje NO_EPS a existuje 'S -> eps' (jedine mozne miesto, kde moze
			// vzniknut za NO_EPS, je v konstruktore),
			// tak S sa po uvodnom normovani vymaze z terminatedSymbols
			// List<Symbol> allowedTerminatedSymbols = new ArrayList<Symbol>(terminatedSymbols);
			// if (!allowedRight.contains(startSymbol)) {
			// allowedTerminatedSymbols.remove(startSymbol);
			// }
			List<Symbol> firstRight = new ArrayList<Symbol>(); // terminaly a normovane neterminaly v poradi (order) za
																// "left"
			if (inputProp.contains(Form.NO_LEFT_RECURSION) || (inputProp.contains(Form.NOT_CYCLIC) && length == 1))
			{
				firstRight.addAll(terminals);
				for (Symbol s : leftOrder.subList(leftOrder.indexOf(n) + 1, leftOrder.size()))
				{
					if (terminatedSymbols.contains(s))
					{
						firstRight.add(s);
					}
				}
			}
			else
			{
				firstRight.addAll(terminatedSymbols);
			}

			if (length == 1)
			{
				List<Symbol> terminated = new ArrayList<Symbol>(terminatedSymbols);
				terminated.add(Symbol.EPSILON);
				if (!n.equals(startSymbol))
				{
					firstRight.add(Symbol.EPSILON);
				}
				List<Symbol> right = new ArrayList<Symbol>();
				for (Symbol s : terminated)
				{
					if (allowedOnlyOneRight.contains(s))
					{
						if (firstRight.contains(s)
							|| (!inputProp.contains(Form.NO_LEFT_RECURSION) && !inputProp.contains(Form.NOT_CYCLIC)))
						{
							right.add(s); // symbol povoleny, ak moze byt sam na pravej strane pravidla
							// a v pripade NO_L_R/NOT_CYC musi byt terminal/epsilon/normovany neterminal v poradi za
							// tymto
						}
					}
				}
				r = gen.makeRule(1, left, right);
			}
			else
			{
				if (inputProp.contains(Form.NO_LEFT_RECURSION))
				{
					r = gen.makeRule(length, left, firstRight, terminatedSymbols);
				}
				else
				{
					r = gen.makeRule(length, left, terminatedSymbols);
				}
			}
		}

		grammar.addRuleAutoAddSymbols(r);
		terminationRules.add(r);
		terminatedSymbols.add(n);
		return r;
	}

	protected void setOrder()
	{
		List<NonTerminal> helpList = new ArrayList<NonTerminal>(nonTerminals.length);
		Stack<NonTerminal> sorted = new Stack<NonTerminal>();
		for (NonTerminal n : nonTerminals)
		{
			helpList.add(n);
		}
		while (!helpList.isEmpty())
		{
			sorted.push(helpList.remove(getRandom(helpList.size())));
		}
		while (!sorted.isEmpty())
		{
			leftOrder.add(sorted.pop());
		}
	}

	public ContextFreeGrammar<ContextFreeRule> getCurrentVersion()
	{
		return grammar.clone();
	}

	protected int getRandom(int max)
	{
		if (max < 0)
		{
			throw new IllegalArgumentException("max must be positive");
		}
		if (System.nanoTime() > randomGetTime + randomRefreshTime)
		{
			random = new Random();
		}
		return random.nextInt(max);
	}

	protected double getRandomDouble()
	{
		if (System.nanoTime() > randomGetTime + randomRefreshTime)
		{
			random = new Random();
		}
		return random.nextDouble();
	}

	protected float getRandomFloat()
	{
		if (System.nanoTime() > randomGetTime + randomRefreshTime)
		{
			random = new Random();
		}
		return random.nextFloat();
	}

	protected int computeRuleLength()
	{
		int percent = getRandom(100);
		int result = 2;
		int minForFirstThree = 50;
		int forFirstThree;
		if (maxRuleLength <= 3)
		{
			if (percent < 25)
			{
				result = 1;
			}
			else if (percent < 65)
			{
				result = 2;
			}
			else
			{
				result = 3;
			}
		}
		else
		{
			forFirstThree = Math.max(minForFirstThree, 100 - 100 * (maxRuleLength - 3) / (maxRuleLength + 2));
			if (percent < forFirstThree)
			{
				if (percent < forFirstThree / 4)
				{
					result = 1;
				}
				else if (percent < forFirstThree * 2 / 3)
				{
					result = 2;
				}
				else
				{
					result = 3;
				}
			}
			else
			{
				result = getRandom(maxRuleLength - 3) + 4;
			}
		}

		return Math.min(result, maxRuleLength);
	}

	public void addRule() throws TooManyRulesException
	{
		int length = computeRuleLength();
		ContextFreeRule result;
		if (length < 1)
		{
			throw new RuntimeException("rule length less then 1");
		}

		if (length == 1)
		{
			if (inputProp.contains(Form.NO_LEFT_RECURSION) || inputProp.contains(Form.NOT_CYCLIC))
			{
				NonTerminal left = allowedLeft.get(getRandom(allowedLeft.size()));
				List<Symbol> firstRight = new ArrayList<Symbol>();
				firstRight.addAll(terminators);
				for (NonTerminal n : leftOrder.subList(leftOrder.indexOf(left) + 1, leftOrder.size()))
				{
					if (allowedOnlyOneRight.contains(n))
					{
						firstRight.add(n);
					}
				}
				result = gen.makeRule(1, left, firstRight);
			}
			else
			{
				result = gen.makeRule(1, allowedLeft, allowedOnlyOneRight);
				// OK, lebo ak nesmie byt L-rek. ani jednoduche pravidla, tak allowedOnlyOneRight neobsahuje
				// neterminaly, takze L-rek. vzniknut nemoze
			}
		}
		else
		{
			if (inputProp.contains(Form.NO_LEFT_RECURSION))
			{
				NonTerminal left = allowedLeft.get(getRandom(allowedLeft.size()));
				List<Symbol> firstRight = new ArrayList<Symbol>();
				firstRight.addAll(terminals);
				for (NonTerminal n : leftOrder.subList(leftOrder.indexOf(left) + 1, leftOrder.size()))
				{
					if (allowedRight.contains(n))
					{
						firstRight.add(n);
					}
				}
				result = gen.makeRule(length, left, firstRight, allowedRight);
			}
			else
			{
				result = gen.makeRule(length, allowedLeft, allowedRight);
			}
		}

		grammar.addRuleAutoAddSymbols(result);

		/*
		 * if result grammar should be without unreachable symbols then add nonterminals which were used to reachable
		 * set if also grammar should be terminated then also terminate used nonterminals
		 */
		Set<ContextFreeRule> terminatingRulesNew = new HashSet<ContextFreeRule>();
		Set<NonTerminal> allowedLeftNew = new HashSet<NonTerminal>();
		boolean isTerminatingRule = true;
		for (Symbol s : result.getSymbolsOnRightSide())
		{
			if (s.isNonTerminal())
			{
				if (inputProp.contains(Form.NO_UNREACHABLE))
				{
					if (allowedLeft.add((NonTerminal) s))
					{
						allowedLeftNew.add((NonTerminal) s);
					}
				}
				if (inputProp.contains(Form.NO_NONGENERATING))
				{
					if (!s.equals(startSymbol) && !terminatedSymbols.contains(s))
					{
						terminatingRulesNew.add(terminateNonTerminal((NonTerminal) s));
					}
				}
				else
				{
					if (!terminatedSymbols.contains(s))
					{
						isTerminatingRule = false;
					}
				}
			}
		}
		// if inputProp.contains(Form.NO_NONGENERATING) then terminatedSymbols.contains(S) already
		// unless !sOnRight (therefore S is not added here to terminatedSymbols)
		if (isTerminatingRule && !result.getSymbolOnLeftSide().equals(startSymbol))
		{
			terminatedSymbols.add(result.getSymbolOnLeftSide());
		}

		Set<ContextFreeRule> deletedRules = new HashSet<ContextFreeRule>();

		/*
		 * store all rules that has been added to or removed from grammar from start of this method to new step and push
		 * it to steps stack also stores new termination rules and new allowed symbols
		 */
		Set<ContextFreeRule> grammarAdded = new HashSet<ContextFreeRule>();
		grammarAdded.addAll(terminatingRulesNew);
		grammarAdded.add(result);
		steps.push(new Step(allowedLeftNew, terminatingRulesNew, deletedRules, grammarAdded, deletedRules));
	}

	public boolean removeRule()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int removeRules(int number)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int undo(int howManySteps)
	{
		if (howManySteps < 0)
		{
			throw new IllegalArgumentException("howManySteps must be positive");
		}
		int result = 0; // number of steps taken back
		Step s;
		while (!steps.empty() && result < howManySteps)
		{
			s = steps.pop();

			allowedLeft.removeAll(s.getAllowedLeftNew());
			terminationRules.removeAll(s.getTerminationRulesNew());
			terminationRules.addAll(s.getTerminationRuleDeleted());
			try
			{
				grammar.addRulesAutoAddSymbols(s.getGrammarDeleted());
			}
			catch (TooManyRulesException ex)
			{
				throw new RuntimeException(
					"Rules max should not be exceeded by taking steps back because then they were not the steps "
						+ "originally made");
			}
			grammar.deleteRulesAutoDeleteSymbols(s.getGrammarAdded());

			result++;
		}
		return result;
	}

	public boolean undoLast()
	{
		return undo(1) > 0;
	}

	public List<NonTerminal> getCurrentOrder()
	{
		List<NonTerminal> result = new ArrayList<NonTerminal>();

		for (NonTerminal n : nonTerminals)
		{
			if (grammar.nonTerminalExists(n) && !grammar.allRulesWithSymbolOnLeft(n).isEmpty())
			{
				result.add(n);
			}
		}
		return result;
	}
}
