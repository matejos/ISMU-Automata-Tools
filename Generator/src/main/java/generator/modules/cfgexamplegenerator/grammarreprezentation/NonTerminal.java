/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.grammarreprezentation;

import java.util.regex.Pattern;

/**
 * @author Rastislav Mirek, Matus Abaffy at Masaryk University, Brno, Czech Republic
 * @mail rmirek@mail.muni.cz\
 * @version Expression version i s undefined on line 14, column 15 in Templates/Classes/Class.java.
 * @copyright Rastislav Mirek, Matus Abaffy all rights reserved
 */
public interface NonTerminal extends Symbol
{

	public static final String ALLOWED_CAPITAL_LETTERS = "A-Z";
	// public static final String CORRECT_NAME_REGEX = "((<([" + ALLOWED_CAPITAL_LETTERS + "]'*)([" +
	// ALLOWED_CAPITAL_LETTERS + "]'*)+>'*)|([" +
	// ALLOWED_CAPITAL_LETTERS + "]'*))";
	public static final String LOWERCASE_LETTERS = "a-z";
	public static final String CORRECT_NAME_REGEX = "((<(([" + ALLOWED_CAPITAL_LETTERS + "]'*)|([" + LOWERCASE_LETTERS
		+ "]'+))(([" + ALLOWED_CAPITAL_LETTERS + "]'*)|([" + LOWERCASE_LETTERS + "]'+))+>'*)|(["
		+ ALLOWED_CAPITAL_LETTERS + "]'*)|([" + LOWERCASE_LETTERS + "]'+))";

	public static final Pattern CORRECT_NAME_PATTERN = Pattern.compile(CORRECT_NAME_REGEX);

}
