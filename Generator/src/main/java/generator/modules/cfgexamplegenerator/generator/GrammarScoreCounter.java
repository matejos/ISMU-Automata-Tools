/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

/**
 * @author drasto
 */
// nepouzita trieda
public class GrammarScoreCounter
{

	public double getScore(Iterable<? extends Criteria> criter)
	{
		if (criter == null)
		{
			throw new NullPointerException("criter");
		}

		double result = 0;

		int count = 0;
		double sum = 0;
		for (Criteria c : criter)
		{
			count++;
			sum = sum + c.getValue() / c.getLowerBound();
		}
		double avg = sum / count;
		double revavg = 1 / avg;

		double prediction;
		Criteria worst = null;
		double worstValue = -1;
		boolean upper = true;
		double recent;
		for (Criteria c : criter)
		{
			prediction = (revavg * c.getValue());
			if (prediction > c.getUpperBound())
			{
				recent = ((prediction - c.getUpperBound()) / c.getUpperBound()) * 100;
				recent = recent * recent;
				c.iterExededMax();
				if (recent > worstValue)
				{
					worst = c;
					worstValue = recent;
					upper = true;
				}
				result = result + recent;
			}
			if (prediction < c.getLowerBound())
			{
				recent = ((c.getLowerBound() - prediction) / c.getLowerBound()) * 100;
				recent = recent * recent;
				c.iterExededMin();
				if (recent > worstValue)
				{
					worst = c;
					worstValue = recent;
					upper = false;
				}
				result = result + recent;
			}
			if (c.getValue() > c.getUpperBound())
			{
				c.boundCrossed();
				c.iterBoundCrossedAtMax();
			}
		}

		if (worst != null)
		{
			if (upper)
			{
				worst.iterExceededMaxAsHighest();
			}
			else
			{
				worst.iterExceededMinAsLowest();
			}
		}

		return result;
	}
}
