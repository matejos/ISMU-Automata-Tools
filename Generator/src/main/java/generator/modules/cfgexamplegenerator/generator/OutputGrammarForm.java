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
public enum OutputGrammarForm implements GrammarForm
{

	NO_NONGENERATING(new Form[] { Form.NO_NONGENERATING }),
	NO_UNREACHABLE(new Form[] { Form.NO_UNREACHABLE }),
	NO_USELESS(new Form[] { Form.NO_NONGENERATING, Form.NO_UNREACHABLE }),
	NO_EPSILON(new Form[] { Form.NO_EPSILON }),
	// NO_EPSILON_NO_USELESS(new Form[]{Form.NO_NONGENERATING, Form.NO_UNREACHABLE, Form.NO_EPSILON}),

	NO_SIMPLE(new Form[] { Form.NO_EPSILON, Form.NO_SIMPLE_RULES, Form.NOT_CYCLIC }),
	PROPER(new Form[] { Form.NO_NONGENERATING, Form.NO_UNREACHABLE, Form.NO_EPSILON, Form.NO_SIMPLE_RULES,
		Form.NOT_CYCLIC }),
	CNF(new Form[] { Form.NO_EPSILON, Form.NO_SIMPLE_RULES, Form.CNF }),
	NO_LEFT_RECURSION(new Form[] { Form.NO_LEFT_RECURSION, Form.NO_EPSILON, Form.NOT_CYCLIC }),
	GNF(new Form[] { Form.NO_EPSILON, Form.GNF }),

	RED_NO_LEFT_RECURSION(new Form[] { Form.NO_NONGENERATING, Form.NO_UNREACHABLE, Form.NO_EPSILON,
		Form.NO_LEFT_RECURSION, Form.NOT_CYCLIC }),
	RED_CNF(new Form[] { Form.NO_NONGENERATING, Form.NO_UNREACHABLE, Form.NO_EPSILON, Form.NO_SIMPLE_RULES, Form.CNF }),
	RED_GNF(new Form[] { Form.NO_NONGENERATING, Form.NO_UNREACHABLE, Form.NO_EPSILON, Form.GNF });

	private String visibleName;
	private Set<Form> atributes = new HashSet<Form>();

	private OutputGrammarForm(Form[] atributes)
	{

		ResourceBundle bundle = ResourceBundle.getBundle("OutputGrammarForm");
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
