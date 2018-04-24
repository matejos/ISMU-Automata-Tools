
package generator.modules.reglang.regularExpression;

import generator.common.tools.CommonUtils;
import generator.core.FormalLanguagesExampleGenerator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RegularExpressionGenerator
{

	public static RegularExpressionNode generateRegularExpression(int minAlph, int maxAlph, int minUnion, int maxUnion,
		int minConcat, int maxConcat, int minIter, int maxIter, int minEmptySets, int maxEmptySets, int minEps,
		int maxEps, int alphabetType)
	{

		Random rand = new Random();
		int alphCount = CommonUtils.randInt(minAlph, maxAlph, rand);
		List<String> alphabet = CommonUtils.generateAlphabetList(alphCount, alphabetType);
		// = new ArrayList<>();
		// for (int i = 0; i < alphCount; i++)
		// {
		// alphabet.add(String.valueOf((char) ('a' + i)));
		// }

		List<String> unusedAlphChars = new LinkedList<>(alphabet);

		int concatCount = CommonUtils.randInt(minConcat, maxConcat, rand);
		int unionCount = CommonUtils.randInt(minUnion, maxUnion, rand);
		int iterCount = CommonUtils.randInt(minIter, maxIter, rand);

		int emptySetsCount = CommonUtils.randInt(minEmptySets, maxEmptySets, rand);
		int epsCount = CommonUtils.randInt(minEps, maxEps, rand);

		int numberOfLeaves = concatCount + unionCount;

		do
		{
			concatCount = CommonUtils.randInt(minConcat, maxConcat, rand);
			unionCount = CommonUtils.randInt(minUnion, maxUnion, rand);
			iterCount = CommonUtils.randInt(minIter, maxIter, rand);

			emptySetsCount = CommonUtils.randInt(minEmptySets, maxEmptySets, rand);
			epsCount = CommonUtils.randInt(minEps, maxEps, rand);

			numberOfLeaves = concatCount + unionCount;
		}
		while (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE && (numberOfLeaves - emptySetsCount - epsCount - alphCount < 0 || iterCount > numberOfLeaves));

		// itercount > numberofleaves TODO add validation

		// vygenerovat listy a pak je nahodne spojovat
		List<RegularExpressionNode> leaves = new ArrayList<>();
		for (int j = 0; j < numberOfLeaves; j++)
		{
			RegularExpressionNode leaf = null;
			if (emptySetsCount > 0)
			{
				leaf = new RegularExpressionNode(RegularExpressionNodeType.EMPTY_SET);
				emptySetsCount--;
			}
			else if (epsCount > 0)
			{
				leaf = new RegularExpressionNode(RegularExpressionNodeType.EPS);
				epsCount--;
			}
			else if (unusedAlphChars.size() > 0)
			{
				leaf = new RegularExpressionNode(unusedAlphChars.get(0));
				unusedAlphChars.remove(0);
			}
			else
			{
				int selectedAlphaChar = CommonUtils.randInt(0, alphabet.size() - 1, rand);
				leaf = new RegularExpressionNode(alphabet.get(selectedAlphaChar));
			}

			leaves.add(leaf);
		}

		List<RegularExpressionNode> nodes = new ArrayList<>(leaves);

		int iterDeadline = numberOfLeaves - iterCount;

		int iteration = 0;
		while (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE && (nodes.size() > 1))
		{
			// add random iteration
			if (iterCount > 0 && (iterDeadline - iteration <= 0 || rand.nextBoolean()))
			{

				RegularExpressionNode iterationNode = new RegularExpressionNode(RegularExpressionNodeType.ITERATION);

				int iterationNodeId = CommonUtils.randInt(0, nodes.size() - 1, rand);
				while (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE && (nodes.get(iterationNodeId).getNodeType().equals(RegularExpressionNodeType.ITERATION)))
				{
					iterationNodeId = CommonUtils.randInt(0, nodes.size() - 1, rand);
				}
				RegularExpressionNode nodeSelectedForIteration = nodes.get(iterationNodeId);
				iterationNode.setIterationChild(nodeSelectedForIteration);
				nodes.remove(iterationNodeId);
				nodes.add(iterationNode);
				iterCount--;
			}

			int leftNodeId = CommonUtils.randInt(0, nodes.size() - 1, rand);
			int rightNodeId = -1;
			do
			{
				rightNodeId = CommonUtils.randInt(0, nodes.size() - 1, rand);
			}
			while (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE && (leftNodeId == rightNodeId));
			RegularExpressionNode newNode = null;
			if (concatCount > 0 && unionCount > 0)
			{
				int selectedOp = CommonUtils.randInt(0, 1, rand);
				if (selectedOp == 0)
				{
					newNode = new RegularExpressionNode(RegularExpressionNodeType.UNION);
					unionCount--;
				}
				else
				{
					newNode = new RegularExpressionNode(RegularExpressionNodeType.CONCATENATION);
					concatCount--;
				}
			}
			else if (concatCount > 0)
			{
				newNode = new RegularExpressionNode(RegularExpressionNodeType.CONCATENATION);
				concatCount--;
			}
			else if (unionCount > 0)
			{
				newNode = new RegularExpressionNode(RegularExpressionNodeType.UNION);
				unionCount--;
			}

			newNode.setLeftChild(nodes.get(leftNodeId));
			newNode.setRightChild(nodes.get(rightNodeId));

			if (leftNodeId < rightNodeId)
			{
				nodes.remove(leftNodeId);
				nodes.remove(rightNodeId - 1);
			}
			else
			{
				nodes.remove(rightNodeId);
				nodes.remove(leftNodeId - 1);
			}
			nodes.add(newNode);

			iteration++;
		}

		return nodes.get(0);
	}

}
