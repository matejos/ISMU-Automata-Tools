/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

/**
 * @author drasto
 */
public class GeneratorSettings
{

	private int whenOneEpsilonProbability;
	private int whenOneTerminalProbability;
	private int whenOneNonTerminalProbability;
	private int[] probabilityOfLength;

	public GeneratorSettings(int whenOneEpsilonProbability, int whenOneTerminalProbability,
		int whenOneNonTerminalProbability, int... probabilityOfLength)
	{
		if (whenOneEpsilonProbability < 0)
		{
			throw new IllegalArgumentException("whenOneEpsylonProbability must be > 0");
		}
		if (whenOneTerminalProbability < 0)
		{
			throw new IllegalArgumentException("whenOneTerminalProbability must be > 0");
		}
		if (whenOneNonTerminalProbability < 0)
		{
			throw new IllegalArgumentException("whenOneNonTerminalProbability must be > 0");
		}
		this.whenOneEpsilonProbability = whenOneEpsilonProbability;
		this.whenOneTerminalProbability = whenOneTerminalProbability;
		this.whenOneNonTerminalProbability = whenOneNonTerminalProbability;
		if (probabilityOfLength == null)
		{
			throw new NullPointerException("probabilityOfLength");
		}
		for (int i : probabilityOfLength)
		{
			if (i < 0)
			{
				throw new IllegalArgumentException("probabilityOfLength cannot contain elements less then zero");
			}
		}
		this.probabilityOfLength = probabilityOfLength;
	}

	public int[] getProbabilityOfLength()
	{
		return probabilityOfLength;
	}

	public int getWhenOneEpsilonProbability()
	{
		return whenOneEpsilonProbability;
	}

	public int getWhenOneNonTerminalProbability()
	{
		return whenOneNonTerminalProbability;
	}

	public int getWhenOneTerminalProbability()
	{
		return whenOneTerminalProbability;
	}

}
