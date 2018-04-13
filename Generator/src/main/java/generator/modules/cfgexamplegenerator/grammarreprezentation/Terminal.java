/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.grammarreprezentation;

import java.util.regex.Pattern;

/**
 * @author Rastislav Mirek at Masaryk University, Brno, Czech Republic
 * @mail rmirek@mail.muni.cz
 * @version Expression version is undefined on line 14, column 15 in Templates/Classes/Interface.java.
 * @copyright Rastislav Mirek all rights reserved
 */
public interface Terminal extends Symbol
{

	public static final String ALLOWED_LETTERS = "a-z";
	public static final String CORRECT_NAME_REGEX = "([" + ALLOWED_LETTERS + "])";
	public static final Pattern CORRECT_NAME_PATTERN = Pattern.compile(CORRECT_NAME_REGEX);
}
