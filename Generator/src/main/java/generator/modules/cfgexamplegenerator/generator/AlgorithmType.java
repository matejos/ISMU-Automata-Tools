/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package generator.modules.cfgexamplegenerator.generator;

import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.CFGTransformationAlgorithm;
import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.CNFCreationAlg;
import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.EpsilonRulesElimAlg;
import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.GNFCreationAlg;
import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.LeftRecursionElimAlg;
import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.NongeneratingSymbolsElimAlg;
import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.NullAlgorithm;
import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.SimpleRulesElimAlg;
import generator.modules.cfgexamplegenerator.cfgtransformationalgorithm.UnreachableSymbolsElimAlg;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeGrammar;
import generator.modules.cfgexamplegenerator.grammarreprezentation.ContextFreeRule;
import generator.modules.cfgexamplegenerator.grammarreprezentation.Symbol;

/**
 * @author drasto, bafco
 */
public enum AlgorithmType
{

	INPUT(NullAlgorithm.getInstance(), "Input"),

	EPSILON_ELIM_ALG(EpsilonRulesElimAlg.getInstance(), Symbol.EPSILON_LETTER + " elimination"),
	SIMPLE_RULES_ELIM_ALG(SimpleRulesElimAlg.getInstance(), "unit productions elimination"),

	NONGENERATING_ELIM_ALG(NongeneratingSymbolsElimAlg.getInstance(), "nongenerating symbols elimination"),
	UNREACHABLE_ELIM_ALG(UnreachableSymbolsElimAlg.getInstance(), "unreachable symbols elimination"),

	LRECURSION_ELIM_ALG(LeftRecursionElimAlg.getInstance(), "L-recursion elimination"),
	CNF_CREATION_ALG(CNFCreationAlg.getInstance(), "CNF creation"),
	GNF_CREATION_ALG(GNFCreationAlg.getInstance(), "GNF creation");

	private String name;
	private CFGTransformationAlgorithm<ContextFreeGrammar<ContextFreeRule>> algorithmForCreation;

	private AlgorithmType(CFGTransformationAlgorithm<ContextFreeGrammar<ContextFreeRule>> algorithm, String name)
	{
		this.algorithmForCreation = algorithm;
		this.name = name;
	}

	public CFGTransformationAlgorithm<ContextFreeGrammar<ContextFreeRule>> getAlgorithm()
	{
		return algorithmForCreation;
	}

	@Override
	public String toString()
	{
		return name;
	}

}
