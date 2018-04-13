/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.cfgtransformationalgorithm;

import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.NonTerminal;
import java.util.List;

/**
 * @author Rastislav Mirek, Matus Abaffy at Masaryk University, Brno, Czech Republic
 * @mail rmirek@mail.muni.cz\
 * @version Expression version i s undefined on line 14, column 15 in Templates/Classes/Class.java.
 * @copyright Rastislav Mirek, Matus Abaffy all rights reserved
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
public class UselessSymbolsElimination extends AbstractCFGTransformationAlgorithm
{

	private static final Form[] req = {};
	private static final Form[] sure = { Form.NO_UNREACHABLE, Form.NO_NONGENERATING };

	private UselessSymbolsElimination()
	{
		super(req, sure);
	}

	@Override
	protected RunResult processAlgorithm(ContextFreeGrammar<ContextFreeRule> grammar, List<NonTerminal> order)
	{
		nullTest(grammar);
		RunResult result = NongeneratingSymbolsElimAlg.INSTANCE.processAlgorithm(grammar, order);
		RunResult result2 = UnreachableSymbolsElimAlg.INSTANCE.processAlgorithm(grammar, result.getNewOrder());

		// RunResult allRes = new RunResult(grammar, result.getCost() + result2.getCost(), result2.getNewOrder());
		result.setMetric(Metric.NON_TERMINALS_DELETED,
			result.getMetric(Metric.NON_TERMINALS_DELETED) + result2.getMetric(Metric.NON_TERMINALS_DELETED));
		result.setMetric(Metric.CYCLE_COUNT,
			Math.max(result.getMetric(Metric.CYCLE_COUNT), result2.getMetric(Metric.CYCLE_COUNT)));
		result.setMetric(Metric.DIFF, Math.max(result.getMetric(Metric.DIFF), result2.getMetric(Metric.DIFF)));// mozno
																												// dat
																												// "+"
																												// miesto
																												// max?
		return result;
	}

}
