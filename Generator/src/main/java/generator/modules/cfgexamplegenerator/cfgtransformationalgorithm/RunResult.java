
package generator.modules.cfgexamplegenerator.cfgtransformationalgorithm;

import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.NonTerminal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "rawtypes" })
public class RunResult<T extends ContextFreeGrammar>
{

	private int difficulty;
	private Map<Metric, Integer> metrics = new HashMap<Metric, Integer>();
	private List<NonTerminal> newOrder;
	private T transformedGrammar;

	RunResult(T transformedGrammar, int diff, List<NonTerminal> newOrder)
	{
		if (transformedGrammar == null)
		{
			throw new NullPointerException("transformedGrammar");
		}
		this.transformedGrammar = transformedGrammar;
		if (diff < 0)
		{
			throw new IllegalArgumentException("difficulty cannot be less then 0");
		}
		this.difficulty = diff;
		if (newOrder == null)
		{
			throw new NullPointerException("newOrder");
		}
		this.newOrder = newOrder;
	}

	public int getCost()
	{
		return difficulty;
	}

	void setMetric(Metric metric, Integer value)
	{
		if (metric == null)
		{
			throw new NullPointerException("metric");
		}
		if (value == null)
		{
			throw new NullPointerException("value");
		}
		metrics.put(metric, value);
	}

	public int getMetric(Metric metric)
	{
		if (metric == null)
		{
			throw new NullPointerException("metric");
		}
		if (!metrics.containsKey(metric))
		{
			throw new IllegalArgumentException("Value of metric " + metric.name() + "wasn\'t set.");
		}
		return metrics.get(metric);
	}

	public List<NonTerminal> getNewOrder()
	{
		if (newOrder == null)
		{
			throw new NullPointerException("newOrder not set");
		}
		return newOrder;
	}

	public T getTransformedGrammar()
	{
		if (transformedGrammar == null)
		{
			throw new NullPointerException("transformed grammar not set");
		}
		return transformedGrammar;
	}

}
