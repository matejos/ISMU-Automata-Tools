/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator;

import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.LeftRecursionElimAlg;
import generator.modules.cfgexamplegenerator.generator.AlgorithmType;
import generator.modules.cfgexamplegenerator.generator.GrammarOrderAndAlg;
import generator.modules.cfgexamplegenerator.generator.InputGrammarForm;
import generator.modules.cfgexamplegenerator.generator.OutputGrammarForm;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.NonTerminal;
import generator.modules.cfgexamplegenerator.grammarreprezentation.Terminal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextArea;

/**
 * @author drasto, bafco
 */
public class Publisher
{

	private InputGrammarForm input;
	private OutputGrammarForm output;
	private long exampleNumber = 0;
	private JTextArea area;
	private String px;
	private String setstart;
	private String assignment;
	private String afterAssignment;
	private String step;
	private String result;
	private String between;
	private String setend;
	private boolean somethingWritten = false;
	private ResourceBundle res = ResourceBundle.getBundle("CFGPublisher", new Locale("en"));

	public Publisher(JTextArea area, String languagePrefix, String typeOfOutputPrefix, InputGrammarForm input,
		OutputGrammarForm output)
	{
		if (area == null)
		{
			throw new NullPointerException("pane");
		}
		if (input == null)
		{
			throw new NullPointerException("input");
		}
		if (output == null)
		{
			throw new NullPointerException("output");
		}
		this.input = input;
		this.output = output;

		this.area = area;
		px = languagePrefix;

		// res = Application.getInstance(CFGExampleGeneratorApp.class).getContext().getResourceMap(Publisher.class);
		setstart = res.getString(typeOfOutputPrefix + "setstart.printout");
		controlDeletion(setstart, typeOfOutputPrefix + "setstart.printout");
		assignment = res.getString(typeOfOutputPrefix + "assignment.printout");
		controlDeletion(assignment, typeOfOutputPrefix + "assignment.printout");
		step = res.getString(typeOfOutputPrefix + "step.printout");
		controlDeletion(step, typeOfOutputPrefix + "step.printout");
		result = res.getString(typeOfOutputPrefix + "result.printout");
		controlDeletion(result, typeOfOutputPrefix + "result.printout");
		afterAssignment = res.getString(typeOfOutputPrefix + "afterassign.printout");
		controlDeletion(afterAssignment, typeOfOutputPrefix + "afterassign.printout");
		between = res.getString(typeOfOutputPrefix + "between.printout");
		controlDeletion(between, typeOfOutputPrefix + "between.printout");
		setend = res.getString(typeOfOutputPrefix + "setend.printout");
		controlDeletion(setend, typeOfOutputPrefix + "setend.printout");
	}

	private void controlDeletion(String setstart, String string)
	{
		if (setstart == null)
		{
			throw new IllegalArgumentException(
				"The key "
					+ string
					+ " cannot be found in Publisher properities file.\n Please"
					+ " make sure it is placed in \n cz\\muni\\fi\\examexamplegeneneratorforcfg\\resources\\Publisher.properities file");
		}
	}

	private void exept(String wholeCommand, String command) throws ParseException
	{
		throw new ParseException(
			"Unknown function, program variable, named variable(in some of 2 language versions) or "
				+ "not correct string format.\n"
				+ "For syntax rules refer to help contained in appropriate preferences file. \nThe wrong command was: "
				+ command + ". \nWhole command was: " + wholeCommand, wholeCommand.indexOf(command));
	}

	private String publishNonTerminals(ContextFreeGrammar<? extends ContextFreeRule> grammar)
	{
		if (grammar == null)
		{
			throw new NullPointerException("grammar");
		}

		List<NonTerminal> nonTs = new ArrayList<NonTerminal>();
		for (NonTerminal n : grammar.getNonTerminals())
		{
			if (!grammar.allRulesWithSymbol(n).isEmpty())
			{
				nonTs.add(n);
			}
		}
		return nonTerminalsToString(nonTs);
	}

	private String nonTerminalsToString(List<NonTerminal> nonTs)
	{
		String result = "";
		for (NonTerminal n : nonTs)
		{
			result = result + n + ",";
		}

		if (result.length() <= 0)
		{
			// throw new Exception("Grammar has to have at least 1 nonTerminal");
			return "";
		}

		return result.substring(0, result.length() - 1);
	}

	// mozno pridat order do parametrov
	private String publishTerminals(ContextFreeGrammar<? extends ContextFreeRule> grammar)
	{
		if (grammar == null)
		{
			throw new NullPointerException("grammar");
		}

		String result = "";
		for (Terminal n : grammar.getTerminals())
		{
			if (!grammar.allRulesWithSymbol(n).isEmpty())
			{
				result = result + n + ",";
			}
		}

		// when set is empty return empty string - may be improved to return symbol for empty set (include in script)
		if (result.length() <= 0)
		{
			return "";
		}

		return result.substring(0, result.length() - 1);
	}

	private String publishStart(ContextFreeGrammar<? extends ContextFreeRule> grammar)
	{
		if (grammar == null)
		{
			throw new NullPointerException("grammar");
		}
		return grammar.getStartSymbol().toString();
	}

	private String publishRules(ContextFreeGrammar<? extends ContextFreeRule> grammar, List<NonTerminal> order,
		int fromLeft)
	{
		if (grammar == null)
		{
			throw new NullPointerException("grammar");
		}
		if (order == null)
		{
			throw new NullPointerException("order");
		}
		String prefix = getPrefix(fromLeft);
		StringBuilder toWrite = new StringBuilder();
		Set<NonTerminal> controlSet = new HashSet<NonTerminal>();
		controlSet.addAll(grammar.getNonTerminals());
		// int i = 0;
		for (NonTerminal n : order)
		{
			// if(!gramar.nonTerminalExists(n) || gramar.allRulesWithSymbolOnLeft(n).isEmpty()){
			// throw new IllegalStateException("order contains nonterminal " + n + " which is not present in grammar " +
			// grammar + " or it has no rules");
			// }
			controlSet.remove(n);

			boolean first = true;
			String line = "";
			for (ContextFreeRule r : grammar.allRulesWithSymbolOnLeft(n))
			{
				if (first)
				{
					first = false;
					line = line + r.toString();
				}
				else
				{
					line = line + ContextFreeRule.RIGHT_SIDE_SEPARATOR + r.rightSideToString();
				}
			}

			if (!grammar.allRulesWithSymbolOnLeft(n).isEmpty())
			{
				toWrite.append(prefix + line + "\n");
			}
			// i++;
		}
		toWrite.deleteCharAt(toWrite.lastIndexOf("\n"));
		for (NonTerminal n : controlSet)
		{
			if (!grammar.allRulesWithSymbolOnLeft(n).isEmpty())
			{
				throw new IllegalStateException("there is a nonterminal " + n + " in grammar " + grammar
					+ " which is not in order and that nonterminal is on left side" + "of some rule");
			}
		}

		return toWrite.toString();
	}

	public JTextArea getTextArea()
	{
		return area;
	}

	public String publish(List<GrammarOrderAndAlg> generatorResult, boolean publishSubresults, boolean showInput)
		throws ParseException
	{

		String toPublish = "";
		// if (exampleNumber <= 0)
		// {
		// // try{
		// toPublish = toPublish + interpretCommand(setstart, setstart, -1, showInput, null);
		// // }
		// // catch(ArrayIndexOutOfBoundsException ex){
		// // throw new ParseException("It is not allowed to use algorithm_step variable in setstart.printout", -1);
		// // }
		// // catch(NullPointerException ex){
		// // throw new
		// // ParseException("It is not allowed to use commands print_assignment, print_step or print_result in " +
		// // "setstart.printout", -1);
		// // }
		// }
		// else
		// {
		toPublish = toPublish + interpretCommand(between, between, 0, showInput, generatorResult);
		// }
		exampleNumber++;

		toPublish = toPublish + interpretCommand(assignment, assignment, 0, showInput, generatorResult);
		toPublish = toPublish + interpretCommand(afterAssignment, afterAssignment, 0, showInput, generatorResult);

		if (publishSubresults)
		{
			for (int i = 1; i < generatorResult.size() - 1; i++)
			{
				toPublish = toPublish + interpretCommand(step, step, i, showInput, generatorResult);
			}
		}

		toPublish = toPublish
			+ interpretCommand(result, result, generatorResult.size() - 1, showInput, generatorResult);

		somethingWritten = true;
		return toPublish.replaceAll("`", "");
	}

	public String closeSet() throws ParseException
	{
		if (!somethingWritten)
		{
			return "";
		}
		String toPublish = "";
		try
		{
			toPublish = interpretCommand(setend, setend, -1, false, null);
		}
		catch (ArrayIndexOutOfBoundsException ex)
		{
			throw new ParseException("It is not allowed to use algorithm_step variable in setend.printout", -1);
		}
		catch (NullPointerException ex)
		{
			throw new ParseException(
				"It is not allowed to use commands print_assignment, print_step or print_result in "
					+ "setend.printout", -1);
		}
		return toPublish.replaceAll("`", "");
	}

	private String interpretCommand(String wholeCommand, String command, int subresultNumber, boolean showInput,
		List<GrammarOrderAndAlg> generatorResult) throws ParseException
	{
		String toReturn = "";
		command = command.replaceAll("`", "");

		Matcher mat;
		String[] func = split(command, '+');
		if (func.length > 1)
		{
			for (String f : func)
			{
				toReturn = toReturn + interpretCommand(wholeCommand, f, subresultNumber, showInput, generatorResult);
			}
			return toReturn;
		}
		else
		{
			command = command.trim();
			if (command.equals(""))
			{
				return "";
			}
			else if (command.matches("ln *\\(.*\\)"))
			{
				String inParenthesis = Pattern.compile("ln *\\((.*)\\)").matcher(command).replaceFirst("$1");
				return interpretCommand(wholeCommand, inParenthesis, subresultNumber, showInput, generatorResult)
					+ "\n";
			}
			else if (command.matches("ln"))
			{
				return "\n";
			}
			else if (command.matches("sp +\\d+"))
			{
				String number = Pattern.compile("sp +(\\d+) *(\\(\\))?").matcher(command).replaceFirst("$1");
				return getPrefix(Integer.parseInt(number));
			}
			else if (command.matches("rp .+"))
			{
				// vyraz za rp musi byt tvaru "firstArg" "secArg" (thirdArg)
				// @return vyhodnoteny podvyraz (thirdArg), kde kazdy vyskyt firstArg sa nahradi secArg-om
				mat = Pattern.compile("\"").matcher(command);
				if (!mat.find())
				{
					exept(wholeCommand, command);
				}
				int start = mat.end();
				if (!mat.find())
				{
					exept(wholeCommand, command);
				}
				int end = mat.start();
				String firstArg = command.substring(start, end);
				if (!mat.find())
				{
					exept(wholeCommand, command);
				}
				start = mat.end();
				if (!mat.find())
				{
					exept(wholeCommand, command);
				}
				end = mat.start();
				String secArg = command.substring(start, end);
				String inParen = command.substring(end + 1, command.length());
				mat = Pattern.compile(" *\\((.*)\\)").matcher(inParen);
				if (!mat.matches())
				{
					exept(wholeCommand, command);
				}

				String thirdArg = mat.group(1);
				String afterInterpretation = interpretCommand(wholeCommand, thirdArg, subresultNumber, showInput,
					generatorResult);
				return afterInterpretation.replaceAll(firstArg, secArg);
			}
			else if (command.matches("div +[cl] +[^\\(\\)\\+\"]+ +\\d+ +\\d+ *\\(.*\\)"))
			{
				// parametry pre divline: 0. center, 1. výplň, 2. length, 3. dĺžka prefixu, 4. text (ak nie je 4., tak
				// "")
				mat = Pattern.compile("div +([cl]) +([^\\(\\)\\+\"]+) +(\\d+) +(\\d+) *\\((.*)\\)").matcher(command);
				mat.matches();
				String[] arguments = new String[mat.groupCount()];
				for (int i = 1; i <= mat.groupCount(); i++)
				{
					arguments[i - 1] = mat.group(i);
				}
				return divLine(
					getPrefix(Integer.parseInt(arguments[3])),
					arguments.length > 4 ? interpretCommand(wholeCommand, arguments[4], subresultNumber, showInput,
						generatorResult) : "", arguments[1], Integer.parseInt(arguments[2]),
					arguments[0].equals("c") ? true : false);
			}
			else if (command.matches("print +[a-zA-Z]+ +[PNTS]{1}( +\\d+)?"))
			{
				// 1. sa tyka gramatiky (vstupna/medzi/vystupna), 2. zrejme, 3. dlzka prefixu
				String gra = Pattern.compile("print +([a-zA-Z]+) +([PNTS]{1})( +\\d+)? *(\\(\\))?").matcher(command)
					.replaceFirst("$1");
				String set = Pattern.compile("print +([a-zA-Z]+) +([PNTS]{1})( +\\d+)? *(\\(\\))?").matcher(command)
					.replaceFirst("$2");
				String num = Pattern.compile("print +([a-zA-Z]+) +([PNTS]{1})( +\\d+)? *(\\(\\))?").matcher(command)
					.replaceFirst("$3");
				int gramNum = 0;
				int leftSpace;
				if (gra.equals("as"))
				{
					gramNum = 0;
				}
				else if (gra.equals("st"))
				{
					gramNum = subresultNumber;
				}
				else if (gra.equals("re"))
				{
					gramNum = generatorResult.size() - 1;
				}
				else
				{
					exept(wholeCommand, command);
				}
				if (num == null || num.equals(""))
				{
					leftSpace = 0;
				}
				else
				{
					leftSpace = Integer.parseInt(num.trim());
				}

				if (set.equals("P"))
				{
					return publishRules(generatorResult.get(gramNum).getGrammar(), generatorResult.get(gramNum)
						.getOrder(), leftSpace);
				}
				else if (set.equals("N"))
				{
					return getPrefix(leftSpace) + publishNonTerminals(generatorResult.get(gramNum).getGrammar());
				}
				else if (set.equals("T"))
				{
					return getPrefix(leftSpace) + publishTerminals(generatorResult.get(gramNum).getGrammar());
				}
				else
				{
					return getPrefix(leftSpace) + publishStart(generatorResult.get(gramNum).getGrammar());
				}
			}
			else if (command.matches("gram +in"))
			{
				if (showInput)
				{
					return res.getString(px + "grammar." + input.name());
				}
				else
				{
					return res.getString(px + "grammar.CFG");
				}
			}
			else if (command.matches("gram +out"))
			{
				return res.getString(px + "grammar." + output.name());
			}
			else if (command.matches("alg_st"))
			{
				return res.getString(px + "algorithm." + generatorResult.get(subresultNumber).getAlg().name());
			}
			else if (command.matches("gram +in +u"))
			{
				// makes first letter of grammar's name capital
				if (showInput)
				{
					return res.getString(px + "grammar." + input.name()).substring(0, 2).toUpperCase()
						+ res.getString(px + "grammar." + input.name()).substring(2);
				}
				else
				{
					return res.getString(px + "grammar.CFG").substring(0, 2).toUpperCase()
						+ res.getString(px + "grammar.CFG").substring(2);
				}
			}
			else if (command.matches("gram +out +u"))
			{
				return res.getString(px + "grammar." + output.name()).substring(0, 2).toUpperCase()
					+ res.getString(px + "grammar." + output.name()).substring(2);
			}
			else if (command.matches("alg_st +u"))
			{
				return res.getString(px + "algorithm." + generatorResult.get(subresultNumber).getAlg().name())
					.substring(0, 2).toUpperCase()
					+ res.getString(px + "algorithm." + generatorResult.get(subresultNumber).getAlg().name())
						.substring(2);
			}
			else if (command.matches("gram +outjs( +u)?"))
			{
				return res.getString("js." + output.name());
			}
			else if (command.matches("number"))
			{
				return ((Long) exampleNumber).toString();
			}
			else if (command.matches("ordp"))
			{
				if (generatorResult.get(subresultNumber).getAlg().equals(AlgorithmType.LRECURSION_ELIM_ALG))
				{
					return "("
						+ res.getString(px + "order")
						+ getOrderToString(generatorResult.get(subresultNumber - 1).getGrammar(),
							generatorResult.get(subresultNumber - 1).getOrder()) + ".)\n";
				}
				else
					return "";
			}
			else if (command.matches("ordl"))
			{
				if (generatorResult.get(subresultNumber).getAlg().equals(AlgorithmType.LRECURSION_ELIM_ALG))
				{
					return "\n("
						+ res.getString(px + "order")
						+ "$"
						+ getOrderToString(generatorResult.get(subresultNumber - 1).getGrammar(),
							generatorResult.get(subresultNumber - 1).getOrder()) + "$.)\\linebreak \\linebreak";
				}
				else
					return "";
			}
			else if (res.containsKey(px + command))
			{
				return res.getString(px + command);
			}

			else if (command.matches("\".*\""))
			{
				return command.substring(1, command.length() - 1);
			}
			else
			{
				exept(wholeCommand, command);
				return null;
			}

		}
	}

	// private String replaceBackslash(String s)
	// {
	// String toret = "";
	// for (char c : s.toCharArray())
	// {
	// if (c == '\\')
	// {
	// toret = toret + c + c;
	// }
	// else
	// {
	// toret = toret + c;
	// }
	// }
	// return toret;
	// }

	// "what" rozdeli na casti v miestach, kde je "around", pricom sa nerataju symboly medzi "..." a medzi (...)
	// hadze ParseExc. pri zlom ozatvorkovani
	private String[] split(String what, char around) throws ParseException
	{
		int leftParenthesis = 0;
		int lastFound = 0;
		boolean ignore = false;
		List<String> found = new ArrayList<String>();
		for (int i = 0; i < what.length(); i++)
		{
			if (what.charAt(i) == '"')
			{
				ignore = !ignore;
			}
			if (!ignore && what.charAt(i) == '(')
			{
				leftParenthesis++;
			}
			else if (!ignore && what.charAt(i) == ')')
			{
				leftParenthesis--;
				if (leftParenthesis < 0)
				{
					throw new ParseException("Unmatched right paranthese found at index " + i, i);
				}
			}
			else if (!ignore && what.charAt(i) == around)
			{
				if (leftParenthesis == 0)
				{
					found.add(what.substring(lastFound, i));
					lastFound = i + 1;
				}
			}
		}
		if (leftParenthesis != 0)
		{
			throw new ParseException("Unmatched left paranthese found in script. ", what.length());
		}
		found.add(what.substring(lastFound, what.length()));

		String[] toReturn = new String[found.size()];
		int i = 0;
		for (String s : found)
		{
			toReturn[i] = s;
			i++;
		}
		return toReturn;
	}

	private String getPrefix(int fromLeft)
	{
		String prefix = "";
		for (int i = 0; i < fromLeft; i++)
		{
			prefix = prefix + " ";
		}
		return prefix;
	}

	private String divLine(String prefix, String text, String div, int length, boolean center)
	{
		text = text.replaceAll("`", "");
		if (center)
		{
			return writeDivMiddle(prefix, text, length, div);
		}
		return writeDivider(prefix + text, length, div);
	}

	// prefix ...use use use...
	private String writeDivider(String prefix, int length, String use)
	{
		String line = "";
		for (int i = 0; i < length - prefix.length(); i++)
		{
			line = line + use;
		}
		return prefix + line;
	}

	// prefix ...use use use... text ...use use use...
	private String writeDivMiddle(String prefix, String text, int length, String use)
	{

		int beginning = (length - text.length() - prefix.length()) / 2;
		String line = prefix;
		for (int i = 1; i <= beginning; i++)
		{
			line = line + use;
		}
		line = line + text;
		for (int i = line.length(); i < length; i++)
		{
			line = line + use;
		}
		return line;
	}

	private String getOrderToString(ContextFreeGrammar<ContextFreeRule> grammar, List<NonTerminal> ord)
	{
		NonTerminal[] order = new NonTerminal[ord.size()];
		order = ord.toArray(order);
		List<NonTerminal> sortedOrder = LeftRecursionElimAlg.getSortedOrder(grammar, order);

		for (NonTerminal n : grammar.getNonTerminals())
		{
			if (!sortedOrder.contains(n))
			{
				sortedOrder.add(n);
			}
		}

		return nonTerminalsToString(sortedOrder);
	}
}
