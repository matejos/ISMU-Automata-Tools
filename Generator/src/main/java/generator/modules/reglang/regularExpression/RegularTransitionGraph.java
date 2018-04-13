
package generator.modules.reglang.regularExpression;

import generator.modules.reglang.automaton.Automaton;
import generator.modules.reglang.automaton.NoSuchStateException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class RegularTransitionGraph
{
	Set<RegularTransitionGraphEdge> graphEdges = new HashSet<>();
	private int stateOrdinal = 1;
	private RegularTransitionGraphEdge baseEdge;

	private Automaton automaton;
	private String finalState; // new final state of automaton
	private String oldStartState; // old start state of automaton
	private String newStartState;

	public RegularTransitionGraph(Automaton a)
	{

		automaton = new Automaton(a);

		oldStartState = automaton.getStartState();
		setNewStartState();
		setNewFinalState();

		automaton.removeUnreachableStates();

		removeBlackHolesFromAutomaton();

		// now transform automaton to reggraph
		for (String stateFrom : automaton.getTransitions().keySet())
		{

			Map<String, Set<String>> automatonStateFromMapping = automaton.getTransitions().get(stateFrom);
			for (String alphChar : automatonStateFromMapping.keySet())
			{
				// since the input is DFA, only one transition per char, hence the iterator
				RegularExpressionNode node = null;
				if (alphChar.equals("epsilon"))
				{
					node = new RegularExpressionNode(RegularExpressionNodeType.EPS);
				}
				else
				{
					node = new RegularExpressionNode(alphChar);
				}
				String stateTo = automatonStateFromMapping.get(alphChar).iterator().next();
				if (!automaton.getStates().contains(stateTo))
				{
					continue;
				}
				graphEdges.add(new RegularTransitionGraphEdge(node, stateTo, stateFrom));

			}
		}
		convertToRegularExpressionString();
	}

	public RegularTransitionGraph(RegularExpressionNode regularExpression)
	{
		baseEdge = new RegularTransitionGraphEdge(regularExpression, "B", "A");
	}

	public void expandRegularGraphFromBaseEdge()
	{
		expandEdge(baseEdge);
	}

	private void expandEdge(RegularTransitionGraphEdge edge)
	{
		RegularTransitionGraphEdge leftEdge;
		RegularTransitionGraphEdge rightEdge;
		RegularTransitionGraphEdge iterationEdge;
		String newState;

		switch (edge.getEdgeValue().getNodeType())
		{
			case CONCATENATION:
				newState = getNextState();
				leftEdge = new RegularTransitionGraphEdge(edge.getEdgeValue().getLeftChild(), newState,
					edge.getStateFrom());
				rightEdge = new RegularTransitionGraphEdge(edge.getEdgeValue().getRightChild(), edge.getStateTo(),
					newState);
				graphEdges.add(leftEdge);
				graphEdges.add(rightEdge);
				expandEdge(leftEdge);
				expandEdge(rightEdge);
				graphEdges.remove(edge);
				break;
			case UNION:
				leftEdge = new RegularTransitionGraphEdge(edge.getEdgeValue().getLeftChild(), edge.getStateTo(),
					edge.getStateFrom());
				rightEdge = new RegularTransitionGraphEdge(edge.getEdgeValue().getRightChild(), edge.getStateTo(),
					edge.getStateFrom());
				graphEdges.add(leftEdge);
				graphEdges.add(rightEdge);
				expandEdge(leftEdge);
				expandEdge(rightEdge);
				graphEdges.remove(edge);
				break;
			case ITERATION:
				newState = getNextState();
				leftEdge = new RegularTransitionGraphEdge(new RegularExpressionNode(RegularExpressionNodeType.EPS),
					newState, edge.getStateFrom());
				rightEdge = new RegularTransitionGraphEdge(new RegularExpressionNode(RegularExpressionNodeType.EPS),
					edge.getStateTo(), newState);
				iterationEdge = new RegularTransitionGraphEdge(edge.getEdgeValue().getIterationChild(), newState,
					newState);
				graphEdges.add(leftEdge);
				graphEdges.add(rightEdge);
				graphEdges.add(iterationEdge);
				expandEdge(iterationEdge);
				graphEdges.remove(edge);
				break;
			case CHAR:
			case EMPTY_SET:
			case EPS:
				break;
		}
	}

	public Automaton transformGraphToAutomaton()
	{
		Automaton a = new Automaton("A");
		for (int i = 0; i <= stateOrdinal; i++)
		{
			a.addState(String.valueOf((char) ('A' + i)));
		}
		try
		{
			a.addToFinalStates("B");
			a.setIsNFA(true);
			cleanupEmptySets();

			Iterator<RegularTransitionGraphEdge> edgeIt = graphEdges.iterator();
			while (edgeIt.hasNext())
			{
				RegularTransitionGraphEdge edgeToProcess = edgeIt.next();
				if (edgeToProcess.getEdgeValue().getNodeType().equals(RegularExpressionNodeType.EPS))
				{
					a.addTransition(edgeToProcess.getStateFrom(), "epsilon", edgeToProcess.getStateTo());
				}
				else
				{
					a.addTransition(edgeToProcess.getStateFrom(), edgeToProcess.getEdgeValue().getAlphChar(),
						edgeToProcess.getStateTo());
				}

			}
			edgeIt.remove();
		}
		catch (NoSuchStateException e)
		{
			e.printStackTrace();
		}

		return a;
	}

	private void cleanupEmptySets()
	{
		Iterator<RegularTransitionGraphEdge> edgeIt = graphEdges.iterator();
		while (edgeIt.hasNext())
		{
			if (edgeIt.next().getEdgeValue().getNodeType().equals(RegularExpressionNodeType.EMPTY_SET))
			{
				edgeIt.remove();
			}
		}
	}

	private String getNextState()
	{
		stateOrdinal++;
		return String.valueOf((char) ('A' + stateOrdinal));
	}

	public RegularExpressionNode convertToRegularExpressionString()
	{
		processStates();

		RegularExpressionNode resultNode = graphEdges.iterator().next().getEdgeValue();
		return resultNode;
	}

	private void processStates()
	{
		Set<String> statesToBeProcessed = new HashSet<>();
		statesToBeProcessed.addAll(automaton.getStates());
		statesToBeProcessed.remove(finalState);
		statesToBeProcessed.remove(newStartState);

		try
		{
			for (String state : statesToBeProcessed)
			{
				// before processing each state, check unions
				updateUnions();
				processState(state);
			}
			updateUnions();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void updateUnions()
	{
		Map<String, Set<RegularTransitionGraphEdge>> edges = new HashMap<>();
		for (RegularTransitionGraphEdge edge : graphEdges)
		{
			String edgesKey = edge.getStateFrom() + "," + edge.getStateTo();
			if (!edges.containsKey(edgesKey))
			{
				edges.put(edgesKey, new HashSet<RegularTransitionGraphEdge>());
			}
			edges.get(edgesKey).add(edge);
		}

		Iterator<Entry<String, Set<RegularTransitionGraphEdge>>> edgesIt = edges.entrySet().iterator();
		while (edgesIt.hasNext())
		{
			Entry<String, Set<RegularTransitionGraphEdge>> edgeEntry = edgesIt.next();
			if (edgeEntry.getValue().size() <= 1)
			{
				edgesIt.remove();
			}
		}

		for (String statePair : edges.keySet())
		{
			String stateFrom = null;
			String stateTo = null;
			RegularExpressionNode left = null;
			RegularExpressionNode right = null;
			for (RegularTransitionGraphEdge edgeToProcess : edges.get(statePair))
			{
				stateFrom = edgeToProcess.getStateFrom();
				stateTo = edgeToProcess.getStateTo();
				graphEdges.remove(edgeToProcess);
				if (left == null)
				{
					left = edgeToProcess.getEdgeValue();
					continue;
				}
				right = edgeToProcess.getEdgeValue();
				RegularExpressionNode unionEdge = new RegularExpressionNode(RegularExpressionNodeType.UNION);
				unionEdge.setLeftChild(left);
				unionEdge.setRightChild(right);
				left = new RegularExpressionNode(unionEdge);

			}
			graphEdges.add(new RegularTransitionGraphEdge(left, stateTo, stateFrom));
		}
	}

	private void processState(String state)
	{
		Set<RegularTransitionGraphEdge> edgesTo = new HashSet<>();
		Set<RegularTransitionGraphEdge> edgesFrom = new HashSet<>();
		RegularTransitionGraphEdge iterationEdge = null;

		Iterator<RegularTransitionGraphEdge> edgeIterator = graphEdges.iterator();
		while (edgeIterator.hasNext())
		{
			RegularTransitionGraphEdge edge = edgeIterator.next();
			if (edge.getStateFrom().contains(state) && edge.getStateTo().contains(state))
			{
				edgeIterator.remove();
				iterationEdge = edge;
				RegularExpressionNode iterationNode = new RegularExpressionNode(RegularExpressionNodeType.ITERATION);
				iterationNode.setIterationChild(iterationEdge.getEdgeValue());
				iterationEdge.setEdgeValue(iterationNode);

				continue;
			}
			if (edge.getStateFrom().contains(state))
			{
				edgesFrom.add(edge);
			}
			else if (edge.getStateTo().contains(state))
			{
				edgesTo.add(edge);
			}
		}

		for (RegularTransitionGraphEdge edgeTo : edgesTo)
		{
			for (RegularTransitionGraphEdge edgeFrom : edgesFrom)
			{
				if (iterationEdge != null)
				{
					RegularExpressionNode concatNode1 = new RegularExpressionNode(
						RegularExpressionNodeType.CONCATENATION);
					RegularExpressionNode concatNode2 = new RegularExpressionNode(
						RegularExpressionNodeType.CONCATENATION);
					concatNode1.setLeftChild(edgeTo.getEdgeValue());
					concatNode1.setRightChild(iterationEdge.getEdgeValue());
					concatNode2.setLeftChild(concatNode1);
					concatNode2.setRightChild(edgeFrom.getEdgeValue());
					graphEdges.add(new RegularTransitionGraphEdge(concatNode2, edgeFrom.getStateTo(), edgeTo
						.getStateFrom()));
				}
				else
				{
					RegularExpressionNode concatNode = new RegularExpressionNode(
						RegularExpressionNodeType.CONCATENATION);
					concatNode.setLeftChild(edgeTo.getEdgeValue());
					concatNode.setRightChild(edgeFrom.getEdgeValue());
					graphEdges.add(new RegularTransitionGraphEdge(concatNode, edgeFrom.getStateTo(), edgeTo
						.getStateFrom()));
				}
			}
		}

		graphEdges.removeAll(edgesFrom);
		graphEdges.removeAll(edgesTo);

	}

	private void removeBlackHolesFromAutomaton()
	{
		Set<String> statesReachingFinalState = new HashSet<String>();
		statesReachingFinalState.add(finalState);

		boolean newStateFound = false;
		do
		{
			newStateFound = false;
			Set<String> statesReachingFinalStateCopy = new HashSet<>(statesReachingFinalState);

			for (String toState : statesReachingFinalState)
			{
				Set<String> statesReachingState = automaton.getStatesHavingTransitionToState(toState);
				if (!statesReachingFinalState.containsAll(statesReachingState))
				{
					statesReachingFinalStateCopy.addAll(statesReachingState);
					newStateFound = true;
				}

			}
			statesReachingFinalState = statesReachingFinalStateCopy;
		}
		while (newStateFound);

		Set<String> statesToRemove = new HashSet<String>();

		for (String state : automaton.getTransitions().keySet())
		{
			if (!statesReachingFinalState.contains(state))
			{
				statesToRemove.add(state);
			}
		}

		try
		{
			statesReachingFinalState.removeAll(statesToRemove);
			Iterator<Entry<String, Map<String, Set<String>>>> transitionsMapIt = automaton.getTransitions().entrySet()
				.iterator();
			while (transitionsMapIt.hasNext())
			{
				if (!statesReachingFinalState.contains(transitionsMapIt.next().getKey()))
				{
					transitionsMapIt.remove();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * sets new start state and adds new the transition from new beginning state to old start state under epsilon
	 */
	private void setNewStartState()
	{
		newStartState = this.automaton.generateUniqueState("x");
		this.automaton.addState(newStartState);
		try
		{
			this.automaton.setStartState(newStartState);
			this.automaton.addEpsilon(newStartState, this.oldStartState);
		}
		catch (NoSuchStateException e)
		{
			throw new Error("RegularGraph.setNewStartState(): " + e.getMessage());
		}
	}

	/**
	 * sets the new final state and adds transitions from old final states to new final state under epsilon
	 */
	private void setNewFinalState()
	{
		// new final state is generated
		this.finalState = this.automaton.generateUniqueState("y");
		// new final state is added to the automaton
		this.automaton.addState(this.finalState);
		try
		{
			// transitions from the old final states to new final states under
			// epsilon are added and old final states are removed from the final
			// states
			for (String oldFinalState : new HashSet<String>(this.automaton.getFinalStates()))
			{
				this.automaton.addEpsilon(oldFinalState, this.finalState);
				this.automaton.removeFinalState(oldFinalState);
			}
			this.automaton.addToFinalStates(finalState);
		}
		catch (NoSuchStateException e)
		{
			throw new Error("RegularGraph.setNewFinalState(): " + e.getMessage());
		}
	}

}
