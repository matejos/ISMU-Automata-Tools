/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.cfgtransformationalgorithm;

import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.NonTerminal;
import java.util.List;

/**
 * @author drasto
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class NullAlgorithm extends AbstractCFGTransformationAlgorithm
{

	private static final Form[] req = {};
	private static final Form[] sure = {};
	public static final NullAlgorithm INSTANCE = new NullAlgorithm();

	private NullAlgorithm()
	{
		super(req, sure);
	}

	public static NullAlgorithm getInstance()
	{
		return INSTANCE;
	}

	@Override
	protected RunResult processAlgorithm(ContextFreeGrammar<ContextFreeRule> gramar, List<NonTerminal> order)
	{
		RunResult result = new RunResult(gramar, 0, order);
		result.setMetric(Metric.CYCLE_COUNT, 0);
		result.setMetric(Metric.NON_TERMINALS_ADDED, 0);
		result.setMetric(Metric.NON_TERMINALS_DELETED, 0);
		result.setMetric(Metric.RULES_ADDED, 0);
		result.setMetric(Metric.RULES_DELETED, 0);
		List newOrder = new SetList();
		for (NonTerminal n : order)
		{
			newOrder.add(n);
		}
		return result;
	}

}
