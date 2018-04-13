/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.Form;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author drasto
 */
// nepouzita trieda
public class CriteriaValueFiller
{

	private AlgorithmWithCriteria[] transformations;
	private Set<Form> inputAttributes = new HashSet<Form>();

	public CriteriaValueFiller(AlgorithmWithCriteria[] transformations, Set<Form> inputAttributes)
	{
		if (transformations == null)
		{
			throw new NullPointerException("transformations");
		}
		if (inputAttributes == null)
		{
			throw new NullPointerException("inputAtributes");
		}
		if (transformations.length <= 0)
		{
			throw new IllegalArgumentException("there is no algorithm to apply");
		}

		this.transformations = new AlgorithmWithCriteria[transformations.length];
		int i = 0;
		for (AlgorithmWithCriteria a : transformations)
		{
			this.transformations[i] = a;
			i++;
		}
		this.inputAttributes.addAll(inputAttributes);
	}

	public CriteriaValueFiller(Collection<AlgorithmWithCriteria> transformations, Set<Form> inputAttributes)
	{
		if (transformations == null)
		{
			throw new NullPointerException("transformations");
		}
		if (inputAttributes == null)
		{
			throw new NullPointerException("inputAtributes");
		}
		if (transformations.size() <= 0)
		{
			throw new IllegalArgumentException("there is no algorithm to apply");
		}

		this.transformations = new AlgorithmWithCriteria[transformations.size()];
		int i = 0;

		for (AlgorithmWithCriteria a : transformations)
		{
			this.transformations[i] = a;
			i++;
		}
		this.inputAttributes.addAll(inputAttributes);
	}

	// public AlgorithmWithCriteria[] fillIn(ContextFreeGramar<ContextFreeRule> gram, Collection<NonTerminal> order)
	// throws RequimentsNotFullFilledException{
	// if(gram == null){
	// throw new NullPointerException("gram");
	// }
	// if(order == null){
	// throw new NullPointerException("order");
	// }
	// ContextFreeGramar<ContextFreeRule> curGram = gram;
	// Set<Form> curForms = inputAttributes;
	// Collection<NonTerminal> curOrder = order;
	// RunResult<? extends ContextFreeGramar> res = null;
	// for(AlgorithmWithCriteria al: transformations){
	// res = al.getAlTyepe().getAlgorithm().process(curGram, curOrder, curForms);
	// for(Criteria c: al.getCriteria()){
	// switch(c.getType()){
	// case COST_OF_ALGORITHM : c.setValue(res.getCost());
	// case CYCLES_COUNT : c.setValue(res.getMetric(Metric.CYCLE_COUNT));
	// case RULES_COUNT : c.setValue(res.getTransformedGramar().getRules().size());
	// case TERMINALS_COUNT : c.setValue(res.getTransformedGramar().getTerminals().size());
	// default: throw new InternalError("Criteria has unexpected type. This type is: " + c.getType() );
	// }
	// }
	// curForms.addAll(al.getAlTyepe().getAlgorithm().sureResultAttributes());
	// curOrder = res.getNewOrder();
	// curGram = res.getTransformedGramar();
	// }
	// return transformations.clone();
	// }

	// public List<Criteria> fillInCrit(ContextFreeGramar<ContextFreeRule> grama, Collection<NonTerminal> order) throws
	// RequimentsNotFullFilledException{
	// AlgorithmWithCriteria[] res = fillIn(grama, order);
	// List<Criteria> result = new SetList<Criteria>();
	// for(AlgorithmWithCriteria al: res){
	// result.addAll(al.getCriteria());
	// }
	// return result;
	// }

}
