/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.Form;
import java.util.Set;

/**
 * @author drasto
 */
public interface GrammarForm
{

	public Set<Form> getAttributes();
	@Override
	public String toString();
}
