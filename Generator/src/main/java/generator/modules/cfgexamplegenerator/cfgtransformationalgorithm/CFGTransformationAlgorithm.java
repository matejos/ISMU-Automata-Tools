/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.cfgtransformationalgorithm;

import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.NonTerminal;
import generator.modules.cfgexamplegenerator.grammarreprezentation.TooManyRulesException;
import java.util.List;
import java.util.Set;

/**
 * @author Rastislav Mirek at Masaryk University, Brno, Czech RepubliC
 * @mail rmirek@mail.muni.cz
 * @version Expression version is undefined on line 14, column 15 in Templates/Classes/Interface.java.
 * @copyright Rastislav Mirek all rights reserved
 */
public interface CFGTransformationAlgorithm<T extends ContextFreeGrammar<? extends ContextFreeRule>>
{

	public RunResult<? extends T> process(T original, List<NonTerminal> order, Set<Form> attributes)
		throws RequirementsNotFulfilledException, TooManyRulesException;

	public boolean isCorrectForm(Set<Form> attributes, T gram);
	public Set<Form> lastCheckMissingRequirements();
	public Set<Form> getRequirements();
	public Set<Form> sureResultAttributes();

}
