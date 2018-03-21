/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.cfg.conversions;

import cz.muni.fi.cfg.forms.Analyser;
import cz.muni.fi.cfg.forms.TransformationException;
import cz.muni.fi.cfg.forms.Transformations;
import cz.muni.fi.cfg.grammar.ContextFreeGrammar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author NICKT
 */
public class CFGConvertor {

    public static String writeOrdering(List<String> order) {
        StringBuilder outputString = new StringBuilder();
        for (int i = 0; i < order.size(); i++) {
            if (i != order.size() - 1) {
                outputString.append(order.get(i)).append(" < ");
            } else {
                outputString.append(order.get(i));
            }
        }
        return outputString.toString();
    }

    public static Map<String, String> convert(ContextFreeGrammar cfg, TransformationTypes type, List<String> ordering, Modes mode) {
        Transformations transform = new Transformations();
        Map<String, String> outputMap = new LinkedHashMap<String, String>();
        String outputCFG = null;
        String title = null;
        switch (mode) {
            case normal:
                switch (type) {
                    case NE1:
                        title = "Odstraněny nenormované symboly:";
                        try {
                            outputCFG = transform.removeUnusefullSymbols(cfg).toString();
                        } catch (TransformationException ex) {
                            outputCFG = "NAG";
                        }
                        break;
                    case NE2:
                        title = "Odstraněny nedosažitelné symboly:";
                        outputCFG = transform.removeUnreachableSymbols(cfg).toString();
                        break;
                    case RED:
                        title = "Gramatika byla zredukována:";
                        try {
                            outputCFG = transform.makeReducedCFG(cfg).toString();
                        } catch (TransformationException ex) {
                            outputCFG = "NAG";
                        }
                        break;
                    case EPS:
                        title = "Odstraněny epsilon kroky:";
                        outputCFG = transform.removeEps(cfg).toString();
                        break;
                    case SRF:
                        title = "Odstraněna jednoduchá pravidla:";
                        outputCFG = transform.removeSimpleRules(cfg).toString();
                        break;
                    case PRO:
                        title = "Gramatika byla převedena na vlastní CFG:";
                        try {
                            outputCFG = transform.makeProperCFG(cfg).toString();
                        } catch (TransformationException ex) {
                            outputCFG = "NAG";
                        }
                        break;
                    case CNF:
                        title = "Gramatika byla převedena do CNF:";
                        try {
                            outputCFG = transform.transformToCNF(cfg).toString();
                        } catch (TransformationException ex) {
                            outputCFG = "NAG";
                        }
                        break;
                    case RLR:
                        try {
                            Map<ContextFreeGrammar, List[]> returnMap = transform.removeLeftRecursion(cfg, ordering);
                            for (Map.Entry<ContextFreeGrammar, List[]> entry : returnMap.entrySet()) {
                                outputCFG = entry.getKey().toString();
                                title = "Odstraněna levá rekurze (výchozí uspořádání neterminálů: " + writeOrdering(entry.getValue()[0]) + "):";
                            }
                        } catch (TransformationException ex) {
                            outputCFG = "NAG";
                        }
                        break;
                    case GNF:
                        title = "Převedeno do GNF:";
                        try {
                            outputCFG = transform.transformToGNF(cfg, ordering).toString();
                        } catch (TransformationException ex) {
                            outputCFG = "NAG";
                        }
                        break;
                    case ESA:
                        title = "Převedeno na PDA akceptující prázdným zásobníkem:";
                        outputCFG = transform.toEmptyStackPDA(cfg).toString();
                        break;
                    case FSA:
                        title = "Převedeno na rozšířený PDA akceptující koncovým stavem:";
                        outputCFG = transform.toFinalStatePDA(cfg).toString();
                        break;
                    case ANA:
                        outputMap.putAll(new Analyser().analyse(cfg));
                        break;
                }
                if (!type.equals(TransformationTypes.ANA))
                    outputMap.put(title, outputCFG);
                break;
            case verbose:
                switch (type) {
                    case NE1:
                        outputMap.putAll(convert(cfg, type, ordering, Modes.normal));
                        break;
                    case NE2:
                        outputMap.putAll(convert(cfg, type, ordering, Modes.normal));
                        break;
                    case RED:
                        outputMap.putAll(convert(cfg, TransformationTypes.NE1, ordering, Modes.normal));
                        outputMap.putAll(convert(cfg, type, ordering, Modes.normal));
                        break;
                    case EPS:
                        outputMap.putAll(convert(cfg, type, ordering, Modes.normal));
                        break;
                    case SRF:
                        outputMap.putAll(convert(cfg, TransformationTypes.EPS, ordering, Modes.normal));
                        outputMap.putAll(convert(cfg, type, ordering, Modes.normal));
                        break;
                    case PRO:
                        outputMap.putAll(convert(cfg, TransformationTypes.EPS, ordering, Modes.normal));
                        outputMap.putAll(convert(cfg, TransformationTypes.SRF, ordering, Modes.normal));
                        outputMap.putAll(convert(cfg, type, ordering, Modes.normal));
                        break;
                    case CNF:
                        outputMap.putAll(convert(cfg, TransformationTypes.PRO, ordering, Modes.normal));
                        outputMap.putAll(convert(cfg, type, ordering, Modes.normal));
                        break;
                    case RLR:
                        outputMap.putAll(convert(cfg, TransformationTypes.PRO, ordering, Modes.normal));
                        outputMap.putAll(convert(cfg, type, ordering, Modes.normal));
                        break;
                    case GNF:
                        outputMap.putAll(convert(cfg, TransformationTypes.RLR, ordering, Modes.normal));
                        outputMap.putAll(convert(cfg, type, ordering, Modes.normal));
                        break;
                    case ESA:
                        outputMap.putAll(convert(cfg, type, ordering, Modes.normal));
                        break;
                    case FSA:
                        outputMap.putAll(convert(cfg, type, ordering, Modes.normal));
                        break;
                    case ANA:
                        outputMap.putAll(convert(cfg, type, ordering, Modes.normal));
                        break;
                }
                break;
        }
        return outputMap;
    }
}
