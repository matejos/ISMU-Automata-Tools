/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.reglang.regularExpression;

import generator.modules.reglang.automaton.Automaton;

/**
 * The class converts DFA to regular expression; regular expression to the NFA with epsilon-transitions
 * 
 * @author JUH
 */
public class RegularExpressionConverter
{

	/**
	 * Constructor is private, that make impossible to create the instances
	 */
	private RegularExpressionConverter()
	{
	}

	public static RegularExpressionNode convertAutomatonToRegularExpression(Automaton automaton)
	{
		RegularTransitionGraph rg = new RegularTransitionGraph(automaton);
		return rg.convertToRegularExpressionString();
	}

	public static Automaton convertRegularExpressionToAutomaton(RegularExpressionNode regularExpression)
	{
		RegularTransitionGraph graph = new RegularTransitionGraph(regularExpression);
		graph.expandRegularGraphFromBaseEdge();
		Automaton a = graph.transformGraphToAutomaton();

		return a;
	}

}
