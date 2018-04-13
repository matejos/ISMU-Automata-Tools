/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.Form;
import java.util.Collections;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author drasto, bafco
 */
public enum InputGrammarForm implements GrammarForm
{

	GENERAL(new Form[] {}), NO_USELESS(new Form[] { Form.NO_NONGENERATING, Form.NO_UNREACHABLE }), WITHOUT_EPSILON(
		new Form[] { Form.NO_EPSILON }),
	// NO_EPSILON_NO_USELESS(new Form[]{Form.NO_NONGENERATING, Form.NO_UNREACHABLE, Form.NO_EPSILON}),

	PROPER(new Form[] { Form.NO_NONGENERATING, Form.NO_UNREACHABLE, Form.NO_EPSILON, Form.NOT_CYCLIC }),
	PROPER_NO_SIMPLE(new Form[] { Form.NO_NONGENERATING, Form.NO_UNREACHABLE, Form.NO_EPSILON, Form.NO_SIMPLE_RULES }),
	PROPER_NO_LEFT_REC(
		new Form[] { Form.NO_NONGENERATING, Form.NO_UNREACHABLE, Form.NO_EPSILON, Form.NO_LEFT_RECURSION }),

	NO_EPSILON_NO_SIMPLE(new Form[] { Form.NO_SIMPLE_RULES, Form.NO_EPSILON }), NOT_CYCLIC_NO_EPSILON(new Form[] {
		Form.NOT_CYCLIC, Form.NO_EPSILON }), READY_FOR_GNF_ALG(new Form[] { Form.NO_LEFT_RECURSION, Form.NO_EPSILON });

	private String visibleName;
	private Set<Form> atributes = new HashSet<Form>();

	private InputGrammarForm(Form[] atributes)
	{
		ResourceBundle bundle = ResourceBundle.getBundle("InputGrammarForm");
		this.visibleName = bundle.getString(this.name());
		for (Form f : atributes)
		{
			this.atributes.add(f);
		}
	}

	@Override
	public String toString()
	{
		return visibleName;
	}

	public void setVisibleName(String visibleName)
	{
		this.visibleName = " " + visibleName;
	}

	public Set<Form> getAttributes()
	{
		return Collections.unmodifiableSet(atributes);
	}
}
