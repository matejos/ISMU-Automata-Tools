/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

/**
 * @author drasto, bafco
 */
public enum CriteriaType
{

	NONTERMINALS_COUNT("Number of lines", "Number of lines in grammar"),
	RULES_COUNT("Number of rules", "Number of rules in grammar"),
	TERMINALS_COUNT("Number of terminals", "Number of terminals in grammar"),
	COST_OF_ALGORITHM("Difficulty of algorithm", "Difficulty of algorithm"),
	CYCLES_COUNT("Number of bottom most cycles the algorithm have to run",
		"Number of bottom most cycles the algorithm have to run"),
	UNREACHABLE_SYMBOLS("Grammar shouldn't contain unreachable symbols",
		"Grammar shouldn't contain unreachable symbols"),
	NONGENERATING_SYMBOLS("Grammar should not contain nongenerating symbols",
		"Grammar should not contain nongenerating symbols"),
	MORE_RULES_THEN_ALLOWED("Grammar has too many rules", "Grammar has too many rules"),
	NOT_SAME("Same grammar was already generated", "Same grammar was already generated"),
	USELESS_NO_PUNISH("No difference if algorithms were applied in wrong order",
		"No difference if algorithms were applied in wrong order");

	// private String visibleName;
	// private String notFulfilledMessage;

	private CriteriaType(String visibleName, String notFulfilledMessage)
	{
		// this.notFulfilledMessage = notFulfilledMessage;
		// this.visibleName = visibleName;
	}
}
