/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.Form;
import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.RequirementsNotFulfilledException;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.NonTerminal;
import java.util.List;
import java.util.Set;

/**
 * @author drasto
 */
public interface CriteriaEvaluator
{

	public boolean evaluate(ContextFreeGrammar<ContextFreeRule> grammarToEvaluate, Set<Form> grammarAtributes,
		List<NonTerminal> nonTerminalsOrder) throws RequirementsNotFulfilledException;
	public List<GrammarOrderAndAlg> getResult();
	public Set<SimpleCriteria> getLowerNotFulfilled();
	public Set<SimpleCriteria> getUpperNotFulfilled();

	public boolean shouldTryToAddMoreRules();

}
