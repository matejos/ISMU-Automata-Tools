/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.NonTerminal;
import generator.modules.cfgexamplegenerator.grammarreprezentation.TooManyRulesException;
import java.util.List;

/**
 * @author drasto
 */
public interface GrammarGenerator
{

	public ContextFreeGrammar<ContextFreeRule> getCurrentVersion();
	public void addRule() throws TooManyRulesException;
	public boolean removeRule();
	public int removeRules(int number);
	public int undo(int howManySteps);
	public boolean undoLast();
	public List<NonTerminal> getCurrentOrder();

}
