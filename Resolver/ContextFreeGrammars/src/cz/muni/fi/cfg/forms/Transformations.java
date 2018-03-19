/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.cfg.forms;

import cz.muni.fi.cfg.grammar.ContextFreeGrammar;
import cz.muni.fi.pda.automaton.PushDownAutomaton;
import java.util.*;

/**
 *
 * @author NICKT, Daniel Pelisek
 */
public class Transformations {

    public ContextFreeGrammar removeUnusefullSymbols(ContextFreeGrammar cfg) throws TransformationException {
        Analyser form = new Analyser();
        if (!form.languageIsNotEmpty(cfg)) {
            throw new TransformationException("NAG");
        }
        Set<String> newTerminals = new HashSet<String>();
        Set<String> n = form.getProductiveSymbols(cfg); //předem musí být ověřeno, že je jazyk neprázdný
        Map<String, Set<String>> newRules = new HashMap<String, Set<String>>();
        for (String s : n) { //ke každému neterminálu z n přidáme patřičná pravidla, taková, že neobsahují symbol z původní N\Ne
            Set<String> newRule = new HashSet<String>();
            for (String r : cfg.getRules().get(s)) { //vybíráme jen patřičná pravidla
                RegExpMaker regexp = new RegExpMaker();
                if (r.matches(regexp.regexNeUnionAlphabeth(n, cfg.getTerminals()))) {
                    newRule.add(r);
                    for (String terminal : cfg.getTerminals()) {
                        if (r.contains(terminal)) {
                            newTerminals.add(terminal);
                        }
                    }
                }
            }
            newRules.put(s, newRule);
        }
        return new ContextFreeGrammar(newTerminals, newRules, cfg.getInitialNonTerminal());
    }

    public ContextFreeGrammar removeUnreachableSymbols(ContextFreeGrammar cfg) {
        Set<String> visited = new HashSet<String>();
        Set<String> toVisit = new HashSet<String>();
        Set<String> nonTerminalsWithouthRules = new HashSet<String>();
        toVisit.add(cfg.getInitialNonTerminal());
        boolean addedToToVisit;
        do {
            addedToToVisit = false;
            toVisit.removeAll(visited);
            Set<String> reachable = new HashSet<String>(); //pomocná množina, totožná s toVisit
            reachable.addAll(toVisit);
            for (String nonTerminal : reachable) {
                visited.add(nonTerminal);
                if (cfg.getRules().containsKey(nonTerminal)) {
                    for (String rule : cfg.getRules().get(nonTerminal)) {
                        Set<String> notProcessedNonTerminals = new HashSet<String>();
                        notProcessedNonTerminals.addAll(cfg.getNonTerminals());
                        notProcessedNonTerminals.removeAll(visited);
                        for (String character : notProcessedNonTerminals) {
                            if (rule.contains(character)) {       //tohle dost možná nemusí fungovat!!
                                if (!cfg.getRules().containsKey(character)) {
                                    nonTerminalsWithouthRules.add(character);
                                } else {
                                    addedToToVisit = true;
                                    toVisit.add(character);
                                }
                            }
                        }
                    }
                }
            }
        } while (addedToToVisit);
        Map<String, Set<String>> newRules = new HashMap<String, Set<String>>(); //vytvoříme nové pravidla
        for (String nonTerminal : visited) {
            newRules.put(nonTerminal, cfg.getRules().get(nonTerminal));
        }
        Set<String> newNonTerminals = new HashSet<String>();
        newNonTerminals.addAll(newRules.keySet());
        newNonTerminals.addAll(nonTerminalsWithouthRules);
        Set<String> newTerminals = new HashSet<String>();
        for (Map.Entry<String, Set<String>> entry : newRules.entrySet()) {
            for (String rule : entry.getValue()) {
                for (String terminal : cfg.getTerminals()) {
                    if (rule.contains(terminal)) {
                        newTerminals.add(terminal);
                    }
                }
            }
        }
        return new ContextFreeGrammar(newNonTerminals, newTerminals, newRules, cfg.getInitialNonTerminal());
    }

    public ContextFreeGrammar makeReducedCFG(ContextFreeGrammar cfg) throws TransformationException {
        return removeUnreachableSymbols(removeUnusefullSymbols(cfg));
    }

    public static String replaceLast(String string, String toReplace, String replacement) {  //nahrazení posledního symbolu
        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos)
                    + replacement
                    + string.substring(pos + toReplace.length(), string.length());
        } else {
            return string;
        }
    }

    public ContextFreeGrammar removeEps(ContextFreeGrammar cfg) {
        //pokud jde o gramatiku S-> eps                             
        if (cfg.getRules().get(cfg.getInitialNonTerminal()).size() == 1 && cfg.getRules().get(cfg.getInitialNonTerminal()).contains("") && cfg.getNonTerminals().size() == 1) {
            Set<String> InitRules = new HashSet<String>();
            InitRules.add("");
            Map<String, Set<String>> rules = new HashMap<String, Set<String>>();
            rules.put(cfg.getInitialNonTerminal() + "'", InitRules);
            return new ContextFreeGrammar(cfg.getNonTerminals(), cfg.getTerminals(), rules, cfg.getInitialNonTerminal() + "'");
        }

        Analyser form = new Analyser();
        Set<String> nEps = form.buildNeps(cfg); //vytvoříme množinu Nepsilon a otestujeme, zda má gramatika epsilon pravidla
//        if ((nEps.contains(cfg.getInitialNonTerminal()) && form.hasInitialSymbolOnRightSide(cfg)) || !nEps.isEmpty()) {
        Analyser f = new Analyser();
        if (f.hasEpsilonRules(cfg)) {
            Set<String> generatingOnlyEps = new HashSet<String>();
            for (String nonTerminal : nEps) { //zjištění neterminálů, které generují jen epsilon
                int numberOfRules = cfg.getRules().get(nonTerminal).size();
//                for (String rule : cfg.getRules().get(nonTerminal)) {
//                    RegExpMaker regexp = new RegExpMaker();
//                    if (rule.matches(regexp.regexPositiveIterateThese(nEps))) {
//                        numberOfRules--;
//                    }
//                }
//                if (numberOfRules == 0) {
//                    generatingOnlyEps.add(nonTerminal);
//                }
                if (numberOfRules == 1 && cfg.getRules().get(nonTerminal).contains("")) {
                    generatingOnlyEps.add(nonTerminal);
                }
            }

            Map<String, Set<String>> newRules = new HashMap<String, Set<String>>();
            Set<String> nMinusGeneratingOnlyEps = new HashSet<String>();
            nMinusGeneratingOnlyEps.addAll(cfg.getRules().keySet());
            nMinusGeneratingOnlyEps.removeAll(generatingOnlyEps);
            for (String nonTerminal : nMinusGeneratingOnlyEps) { //pro každý neterminál N \ generatingOnlyEps
                Set<String> rightSideRules = new HashSet<String>();
                rightSideRules.addAll(cfg.getRules().get(nonTerminal));
                boolean rulesChanged;

                Set<String> rightSideRulesCopy = new HashSet<String>();
                rightSideRulesCopy.addAll(rightSideRules);
                for (String rule : rightSideRulesCopy) {  //odmažeme odkazy na pravidla generující jen eps
                    for (String s : generatingOnlyEps) {
                        rightSideRules.remove(rule); //dané pravidlo odstraníme a nahradíme novým
                        rightSideRules.add(rule.replaceAll(s, ""));
                        String newRule = rule;
                        boolean konec = true;
                        while (konec) {
                            konec = rightSideRules.add(newRule.replaceFirst(s, ""));
                        }
                        newRule = rule;
                        konec = true;
                        while (konec) {
                            konec = rightSideRules.add(replaceLast(newRule, s, ""));
                        }
                    }
                }

                do {
                    rulesChanged = false;
                    rightSideRulesCopy.addAll(rightSideRules);  //kdyby něco, přidat clear
                    for (String rule : rightSideRulesCopy) {
//                        Set<String> nEpsMinusGeneratingOnlyEps = new HashSet<String>();
//                        nEpsMinusGeneratingOnlyEps.addAll(nEps);
//                        nEpsMinusGeneratingOnlyEps.removeAll(generatingOnlyEps);
//                        for (String s : nEpsMinusGeneratingOnlyEps) {
                        rightSideRules.add(rule);
                        for (String s : nEps) {
                            if (rule.contains(s)) {
                                String[] splitRule = rule.split(s);
                                if (splitRule.length == 0) {
                                    if (rule.length() > 1) {
                                        String jenNeterminaly = s;
                                        while (jenNeterminaly.length() <= rule.length()) {
                                            rulesChanged = rightSideRules.add(jenNeterminaly);
                                            jenNeterminaly = jenNeterminaly + s;
                                        }
                                    }
                                } else if (splitRule.length == 1) {
                                    rulesChanged = rightSideRules.add(splitRule[0]);
                                    String newRule = rule;
                                    boolean konec = true;
                                    while (konec) {
                                        konec = rightSideRules.add(newRule.replaceFirst(s, ""));
                                    }
                                    newRule = rule;
                                    konec = true;
                                    while (konec) {
                                        konec = rightSideRules.add(replaceLast(newRule, s, ""));
                                    }
                                } else if (splitRule.length > 1) {
                                    for (String druhyZEps : nEps) {
                                        rightSideRules.add(rule.replaceAll(s, "").replaceAll(druhyZEps, ""));
                                    }
                                    rulesChanged = rightSideRules.add(rule.replaceAll(s, ""));
//                                    if (rule.endsWith(s)) {
//                                        rulesChanged = rightSideRules.add(rule.replaceAll(s, "") + s);
//                                    }
                                    String newRule = rule;
                                    boolean konec = true;
                                    while (konec) {
                                        konec = rightSideRules.add(newRule.replaceFirst(s, ""));
                                    }
                                    newRule = rule;
                                    konec = true;
                                    while (konec) {
                                        konec = rightSideRules.add(replaceLast(newRule, s, ""));
                                    }
                                    if (splitRule[0].equals("")) {
                                        for (int i = 1; i < splitRule.length; i++) {
                                            StringBuilder outputString = new StringBuilder();
                                            for (int j = 0; j < splitRule.length; j++) {
                                                if (i == 1 && i == j) {
                                                    outputString.append(s);
                                                }
                                                outputString.append(splitRule[j]);
                                                if (i == j && j != splitRule.length - 1) {
                                                    outputString.append(s);
                                                }
                                            }
                                            rulesChanged = rightSideRules.add(outputString.toString());
                                        }
                                    } else {
                                        for (int i = 0; i < splitRule.length; i++) {
                                            StringBuilder outputString = new StringBuilder();
                                            for (int j = 0; j < splitRule.length; j++) {
                                                outputString.append(splitRule[j]);
                                                if (i == j && j != splitRule.length - 1) {
                                                    outputString.append(s);
                                                }
                                            }
                                            rulesChanged = rightSideRules.add(outputString.toString());
                                        }
                                    }
                                }
                            }
                        }
                    }
                } while (rulesChanged);
                rightSideRules.remove(""); //odstraníme epsilon pravidlo, pokud je
                newRules.put(nonTerminal, rightSideRules);
            }

            if (nEps.contains(cfg.getInitialNonTerminal())) { //pokud je iniciální neterminál z NEps
                Set<String> initialRules = new HashSet<String>();
                initialRules.add(cfg.getInitialNonTerminal());
                initialRules.add("");
                String newInitialSymbol = "S";
                do { // ošetření, pokud už S' v gramatice je
                    newInitialSymbol += "'";
                } while (newRules.keySet().contains(newInitialSymbol));
                newRules.put(newInitialSymbol, initialRules);
                Set<String> newNonterminals = new HashSet<String>();
                newNonterminals.addAll(cfg.getNonTerminals());
                newNonterminals.addAll(newRules.keySet());
                newNonterminals.removeAll(generatingOnlyEps);
                newNonterminals.add(cfg.getInitialNonTerminal());//pro zichr
                ContextFreeGrammar newCfg = new ContextFreeGrammar(newNonterminals, cfg.getTerminals(), newRules, newInitialSymbol);
                newCfg.mapToArrayMap();
                return newCfg;
            }
            Set<String> newNonterminals = new HashSet<String>();
            newNonterminals.addAll(cfg.getNonTerminals());
            newNonterminals.addAll(newRules.keySet());
            newNonterminals.removeAll(generatingOnlyEps);
            if (!generatingOnlyEps.isEmpty()) {
                for (String nonTerminalToCheck : generatingOnlyEps) {
                    for (Map.Entry<String, Set<String>> entry : newRules.entrySet()) {
                        for (String rule : entry.getValue()) {
                            RegExpMaker r = new RegExpMaker();
                            Set<String> heplSet = new HashSet<String>();
                            heplSet.addAll(cfg.getNonTerminals());
                            heplSet.addAll(newRules.keySet());
                            if (rule.matches(r.regexNonterminalInRule(nonTerminalToCheck, heplSet, cfg.getTerminals()))) {
                                newNonterminals.add(nonTerminalToCheck);
                            }
                        }
                    }
                }
            }
            newNonterminals.add(cfg.getInitialNonTerminal());//pro zichr
            ContextFreeGrammar newCfg = new ContextFreeGrammar(newNonterminals, cfg.getTerminals(), newRules, cfg.getInitialNonTerminal());
            newCfg.mapToArrayMap();
            return newCfg;
        } else {
            return cfg;
        }
    }

    public ContextFreeGrammar removeSimpleRules(ContextFreeGrammar cfg) {
        cfg = removeEps(cfg); //pokud má cfg eps pravidla, odstraníme je

        Map<String, Set<String>> noSimpleRules = new HashMap<String, Set<String>>();
        Map<String, Set<String>> onlySimpleRules = new HashMap<String, Set<String>>(); //vytvoření pomocných množin obsahujících veškerá jednoduchá pravidla daného neterminálu
        boolean hasSimpleRules = false;
        for (String nonTerminal : cfg.getNonTerminals()) {
            Set<String> rulesWithouthSimpleRules = new HashSet<String>();
            Set<String> helpSet = new HashSet<String>();
            helpSet.add(nonTerminal);
            Set<String> rulesSet = cfg.getRules().get(nonTerminal);
            if (rulesSet == null) { //pokud se jedná o nenormovaný neterminál
                continue;
            }
            for (String rule : rulesSet) {
                boolean wasSimple = false;
                for (String allNonterminals : cfg.getNonTerminals()) {
                    if (rule.equals(allNonterminals)) { //možná jsem to neopravil dobře
                        helpSet.add(rule);
                        hasSimpleRules = true;
                        wasSimple = true;
                    }
                }
                if (!wasSimple) {
                    rulesWithouthSimpleRules.add(rule);
                }
            }
            onlySimpleRules.put(nonTerminal, helpSet);
            noSimpleRules.put(nonTerminal, rulesWithouthSimpleRules);
        }

        if (hasSimpleRules) { //pokud obsahuje jednoduchá pravidla
            Set<String> iterator = new HashSet<String>();
            iterator.addAll(onlySimpleRules.keySet());
            for (String nonTerminal : iterator) { //pro každý neterminál který obsahuje jednoduché pravidlo
                Set<String> visitedNonTerminals = new HashSet<String>();
                Set<String> allSimpleRules = new HashSet<String>();
                allSimpleRules.addAll(onlySimpleRules.get(nonTerminal));
                boolean rulesChanged = true;
                while (rulesChanged) {
                    rulesChanged = false;
                    Set<String> helpSet = new HashSet<String>();
                    helpSet.addAll(allSimpleRules);
                    helpSet.removeAll(visitedNonTerminals);
                    for (String simpleRule : helpSet) {
                        visitedNonTerminals.add(simpleRule);
                        Set<String> anotherRules = new HashSet<String>();
                        Set<String> r = onlySimpleRules.get(simpleRule);
                        if (r == null) {//pokud se jedná o nenormovaný neterminál
                            continue;
                        }
                        anotherRules.addAll(onlySimpleRules.get(simpleRule));
                        if (allSimpleRules.addAll(anotherRules)) {
                            rulesChanged = true;
                            break;

                        }
                    }
                }
                onlySimpleRules.put(nonTerminal, allSimpleRules);
            }

            Map<String, Set<String>> newRules = new HashMap<String, Set<String>>(); //dotvoříme nová pravidla
            for (String nonTerminal : cfg.getRules().keySet()) {
                Set<String> helpSet = new HashSet<String>();
                for (String nonTerminalToAdd : onlySimpleRules.get(nonTerminal)) {
                    Set<String> r = noSimpleRules.get(nonTerminalToAdd);
                    if (r == null) { //pokud mame nenormovany neterminal
                        continue;
                    }
                    helpSet.addAll(r);
                }
                if (!helpSet.isEmpty()) {
                    newRules.put(nonTerminal, helpSet);
                }
            }
            Set<String> newNonTerminals = new HashSet<String>();
            newNonTerminals.addAll(newRules.keySet());

            Set<String> nonTerminalsToCheck = new HashSet<String>();
            nonTerminalsToCheck.addAll(cfg.getNonTerminals());
            nonTerminalsToCheck.removeAll(newRules.keySet());
            if (!nonTerminalsToCheck.isEmpty()) {
                for (String nonTerminalToCheck : nonTerminalsToCheck) {
                    for (Map.Entry<String, Set<String>> entry : newRules.entrySet()) {
                        for (String rule : entry.getValue()) {
                            RegExpMaker r = new RegExpMaker();
                            if (rule.matches(r.regexNonterminalInRule(nonTerminalToCheck, cfg.getNonTerminals(), cfg.getTerminals()))) {
                                newNonTerminals.add(nonTerminalToCheck);
                            }
                        }
                    }
                }
            }
            ContextFreeGrammar newCfg = new ContextFreeGrammar(newNonTerminals, cfg.getTerminals(), newRules, cfg.getInitialNonTerminal()); //OPRAVENO na toto se optat, jinak může z gramatiky S-> \e | A, A->a vzniknout S-> \e | a, A->a
            newCfg.mapToArrayMap();
            return newCfg;
        } else {
            return cfg;
        }

    }

    public ContextFreeGrammar makeProperCFG(ContextFreeGrammar cfg) throws TransformationException {  //vlastní cfg
        return makeReducedCFG(removeSimpleRules(cfg)); //odstranění jednoduchých pravidel už ošéfovává odstranění eps, tak je zbytečné to znova volat
    }

    public ContextFreeGrammar transformToCNF(ContextFreeGrammar cfg) throws TransformationException {
        Analyser form = new Analyser();
        if (!form.languageIsNotEmpty(cfg)) { //pokud je jazyk prázdný S->SS
            Map<String, Set<String>> rules = new HashMap<String, Set<String>>();
            Set<String> sRules = new HashSet<String>();
            sRules.add("SS");
            rules.put("S", sRules);
            return new ContextFreeGrammar(new HashSet<String>(), rules, "S");
        } else {
            if (!form.isProper(cfg) || form.hasSimpleRules(cfg)) {
                cfg = makeProperCFG(cfg);
            }
            if (cfg.getRulesArray().isEmpty()) { //pokud nemáme vytvořenou pomocnou mapu, doděláme ji
                cfg.mapToArrayMap();
            }

            Set<String> newN = new HashSet<String>();
            newN.addAll(cfg.getRules().keySet());
            Map<String, Set<String>> newRules = new HashMap<String, Set<String>>();
            Map<String, Set<List<String>>> newArrayRules = new HashMap<String, Set<List<String>>>();
            newArrayRules.putAll(cfg.getRulesArray());
            Set<String> visitedN = new HashSet<String>();
            boolean addedToN = true;
            while (addedToN) {
                addedToN = false;
                Set<String> helpN = new HashSet<String>();
                helpN.addAll(newN);
                helpN.removeAll(visitedN);
                for (String nonTerminal : helpN) {
                    visitedN.add(nonTerminal);
                    Set<List<String>> helpArraySet = new HashSet<List<String>>();
                    Set<String> helpSet = new HashSet<String>();
                    Map<String, Set<List<String>>> helpRules = new HashMap<String, Set<List<String>>>();
                    helpRules.putAll(newArrayRules);
                    for (List<String> rule : helpRules.get(nonTerminal)) {
                        if (rule.size() < 3) {
                            if (rule.size() == 2) {
                                StringBuilder oldRule = new StringBuilder();
                                List<String> oldArrayRule = new ArrayList<String>();
                                Iterator<String> it = rule.iterator();
                                while (it.hasNext()) {
                                    String s = it.next();
                                    if (cfg.getTerminals().contains(s)) {
                                        String original = s;
                                        s += "'";
                                        while (cfg.getNonTerminals().contains(s)) {
                                            s += "'";
                                        }
                                        visitedN.add(s); //optimalizace
                                        List<String> l = new ArrayList<String>();
                                        l.add(original);
                                        Set<List<String>> se = new HashSet<List<String>>();
                                        se.add(l);
                                        newArrayRules.put(s, se);
                                        Set<String> newSet = new HashSet<String>();
                                        newSet.add(original);
                                        newRules.put(s, newSet);
                                        newN.add(s);
                                        addedToN = true;
                                    }

                                    oldRule.append(s);
                                    oldArrayRule.add(s);
                                }
                                helpArraySet.add(oldArrayRule);
                                helpSet.add(oldRule.toString());
                            } else {
                                helpArraySet.add(rule);
                                StringBuilder unchangedRule = new StringBuilder();
                                Iterator<String> it = rule.iterator();
                                while (it.hasNext()) {
                                    unchangedRule.append(it.next());
                                }

                                helpSet.add(unchangedRule.toString());
                            }

                        } else {
                            List<String> helpList = new ArrayList<String>();
                            String helpString = "";
                            StringBuilder newRule = new StringBuilder();
                            StringBuilder newNonTerminal = new StringBuilder();
                            Iterator<String> it = rule.iterator();
                            String firstSymbol = it.next();
                            if (cfg.getTerminals().contains(firstSymbol)) {
                                String s = firstSymbol;
                                firstSymbol += "'";
                                while (cfg.getNonTerminals().contains(firstSymbol)) {
                                    firstSymbol += "'";
                                }
                                visitedN.add(firstSymbol); //optimalizace
                                List<String> l = new ArrayList<String>();
                                l.add(s);
                                Set<List<String>> se = new HashSet<List<String>>();
                                se.add(l);
                                newArrayRules.put(firstSymbol, se);
                                Set<String> newSet = new HashSet<String>();
                                newSet.add(s);
                                newRules.put(firstSymbol, newSet);
                                newN.add(firstSymbol);
                            }

                            newRule.append(firstSymbol).append("<");
                            newNonTerminal.append("<");
                            while (it.hasNext()) {
                                String s = it.next();
                                newRule.append(s);
                                newNonTerminal.append(s);
                                helpList.add(s);
                                helpString += s;
                            }

                            newNonTerminal.append(">");
                            newRule.append(">");
                            newN.add(newNonTerminal.toString());
                            Set<List<String>> newArraySet = new HashSet<List<String>>();
                            newArraySet.add(helpList);
                            newArrayRules.put(newNonTerminal.toString(), newArraySet);
                            Set<String> newSet = new HashSet<String>();
                            newSet.add(helpString);
                            newRules.put(newNonTerminal.toString(), newSet);
                            List<String> newList = new ArrayList<String>();
                            newList.add(firstSymbol);
                            newList.add(newNonTerminal.toString());
                            helpArraySet.add(newList);
                            helpSet.add(newRule.toString());
                            visitedN.remove(newNonTerminal.toString());
                            addedToN = true;
                        }

                    }
                    newArrayRules.put(nonTerminal, helpArraySet);
                    newRules.put(nonTerminal, helpSet);
                }

            }
            return new ContextFreeGrammar(newN, cfg.getTerminals(), newRules, newArrayRules, cfg.getInitialNonTerminal());
        }
    }

    /*
     * removes direct left recursion from rules of one non-terminal symbol
     * 
     * @author Daniel Pelisek
     * 
     * @param rules                all rules of given non-terminal symbol
     * @param processedNonTerminal given non-terminal symbol
     * @param nonterminals         all non-termninal symbols in grammar
     * @return                     all rules of given non-terminal A symbol
     *                             and its successor A' if it was created
     */
    public Map<String, Set<List<String>>> removeDirectLeftRecursion(Set<List<String>> rules, String processedNonTerminal, Set<String> nonTerminals) {

        Set<List<String>> hadNotPrefix = new HashSet<List<String>>();
        Set<List<String>> hadPrefix = new HashSet<List<String>>();

        for (List<String> rule : rules) {
            if (!rule.isEmpty() && rule.get(0).equals(processedNonTerminal)) {
                hadPrefix.add(rule.subList(1, rule.size()));
            } else {
                hadNotPrefix.add(rule);
            }
        }

        Map<String, Set<List<String>>> newRules = new HashMap<String, Set<List<String>>>();
        newRules.put(processedNonTerminal, new HashSet<List<String>>(hadNotPrefix));

        if (!hadPrefix.isEmpty()) {

            String newNonTerminal = processedNonTerminal + "'";
            while (nonTerminals.contains(newNonTerminal)) {
                newNonTerminal += "'";
            }

            newRules.put(newNonTerminal, new HashSet<List<String>>(hadPrefix));

            for (List<String> rule : hadNotPrefix) {
                List<String> newRule = new ArrayList<String>(rule);
                newRule.add(newNonTerminal);
                newRules.get(processedNonTerminal).add(newRule);
            }
            for (List<String> rule : hadPrefix) {
                List<String> newRule = new ArrayList<String>(rule);
                newRule.add(newNonTerminal);
                newRules.get(newNonTerminal).add(newRule);
            }
        }
        return newRules;
    }

    /*
     * transform given CFG to CFG without left recursion
     * 
     * @author Daniel Pelisek
     * 
     * @param cfg      given context-free grammar
     * @param ordering default ordering of nonterminal
     * @throw          Transformation exception if transformation to proper CFG throws exception
     * @return         CFG without left recursion with list contains its default ordering
     *                 and new ordering of neterminals (map is becaouse request of pair)
     */
    public Map<ContextFreeGrammar, List[]> removeLeftRecursion(ContextFreeGrammar cfg, List<String> ordering) throws TransformationException {

        if (!new Analyser().isProper(cfg)) {
            cfg = makeProperCFG(cfg);
            ordering.retainAll(cfg.getNonTerminals());
            if (ordering.isEmpty() || !cfg.getInitialNonTerminal().equals(ordering.get(0))) {
                ordering.add(0, cfg.getInitialNonTerminal());
            }
        }

        if (cfg.getRulesArray().isEmpty()) {
            cfg.mapToArrayMap();
        }

        Map<String, Set<List<String>>> newRulesArray = new HashMap<String, Set<List<String>>>(cfg.getRulesArray());
        List<String> outputOrdering = new ArrayList<String>(ordering);

        for (String orderedNonTerminal : ordering) {
            for (String nextSymbol : ordering) {
                if (orderedNonTerminal.equals(nextSymbol)) {
                    break;
                }
                Set<List<String>> newRulesSet = new HashSet<List<String>>();
                for (List<String> rule : newRulesArray.get(orderedNonTerminal)) {
                    if (!rule.isEmpty() && rule.get(0).equals(nextSymbol)) {
                        for (List<String> ruleOfNextSymbol : newRulesArray.get(nextSymbol)) {
                            List<String> newRule = new ArrayList<String>(ruleOfNextSymbol);
                            newRule.addAll(rule.subList(1, rule.size()));
                            newRulesSet.add(newRule);
                        }
                    } else {
                        newRulesSet.add(rule);
                    }
                }
                newRulesArray.put(orderedNonTerminal, newRulesSet);
            }

            Map<String, Set<List<String>>> withouthDirectLeftRecursion = removeDirectLeftRecursion(newRulesArray.get(orderedNonTerminal), orderedNonTerminal, cfg.getNonTerminals());

            if (withouthDirectLeftRecursion.size() == 2) {
                newRulesArray.putAll(withouthDirectLeftRecursion);
                for (String newSymbol : withouthDirectLeftRecursion.keySet()) {
                    if (!newSymbol.equals(orderedNonTerminal)) {
                        outputOrdering.add(0, newSymbol);
                    }
                }
            }
        }

        Map<String, Set<String>> newRules = new HashMap<String, Set<String>>();
        for (String nonTerminal : newRulesArray.keySet()) {
            newRules.put(nonTerminal, new HashSet<String>());
            for (List<String> rule : newRulesArray.get(nonTerminal)) {
                newRules.get(nonTerminal).add(rule.toString().replaceAll("[\\[\\], ]", ""));
            }
        }

        Map<ContextFreeGrammar, List[]> returnMap = new HashMap<ContextFreeGrammar, List[]>();
        List[] outputArray = {ordering, outputOrdering};
        returnMap.put(new ContextFreeGrammar(cfg.getTerminals(), newRules, cfg.getInitialNonTerminal()), outputArray);
        return returnMap;
    }

    /*
     * transform given CFG to Greinbach normal form
     * 
     * @author Daniel Pelisek
     * 
     * @param cfg      given context-free grammar
     * @param ordering default ordering of nonterminal
     * @throw          Transformation exception if transformation to proper CFG throws exception
     * @return         CFG in GNF
     */
    public ContextFreeGrammar transformToGNF(ContextFreeGrammar cfg, List<String> ordering) throws TransformationException {

        // if language is empty
        if (!new Analyser().languageIsNotEmpty(cfg)) {
            Map<String, Set<String>> rules = new HashMap<String, Set<String>>(1);
            rules.put("S", new HashSet<String>(1));
            rules.get("S").add("aS");
            Set<String> terminals = new HashSet<String>(1);
            terminals.add("a");
            return new ContextFreeGrammar(terminals, rules, "S");
        }

        // get cfg withouth left recursion
        Map<ContextFreeGrammar, List[]> returnMap = removeLeftRecursion(cfg, ordering);
        for (Map.Entry<ContextFreeGrammar, List[]> entry : returnMap.entrySet()) {
            cfg = entry.getKey();
            ordering = entry.getValue()[1];
        }
        
        if (cfg.getRulesArray().isEmpty()) {
            cfg.mapToArrayMap();
        }
        
        // remove nonterminal prefix
        Map<String, Set<List<String>>> newRulesArray = new HashMap<String, Set<List<String>>>(ordering.size());
        for (int i = ordering.size() - 1; i >= 0; i--) {
            String n = ordering.get(i);
            newRulesArray.put(n, new HashSet<List<String>>());
            for (List<String> rule : cfg.getRulesArray().get(n)) {
                if (!rule.isEmpty() && cfg.getNonTerminals().contains(rule.get(0))) {
                    String n2 = rule.get(0);
                    for (List<String> rule2 : newRulesArray.get(n2)) {
                        List<String> newRule = new ArrayList<String>(rule2);
                        newRule.addAll(rule.subList(1, rule.size()));
                        newRulesArray.get(n).add(newRule);
                    }
                } else {
                    newRulesArray.get(n).add(rule);
                }
            }
        }
        
        // terminal substitution
        Map<String, Set<String>> newRules = new HashMap<String, Set<String>>(ordering.size());
        for (String n : newRulesArray.keySet()) {
            newRules.put(n, new HashSet<String>());
            for (List<String> rule : newRulesArray.get(n)) {
                StringBuilder newRule = new StringBuilder();
                for (String symbol : rule) {
                    if (symbol.equals(rule.get(0)) || cfg.getNonTerminals().contains(symbol)) { // compare equivalence of object! not of string
                        newRule.append(symbol);
                    } else {
                        String newN = symbol + "\'";
                        while (cfg.getRules().containsKey(newN) && !(cfg.getRules().get(newN).contains(symbol) && cfg.getRules().get(newN).size() == 1)) {
                            newN = newN + "\'";
                        }
                        newRule.append(newN);
                        newRules.put(newN, new HashSet<String>());
                        newRules.get(newN).add(symbol);
                    }
                }
                newRules.get(n).add(newRule.toString());
            }
        }

        return new ContextFreeGrammar(cfg.getTerminals(), newRules, cfg.getInitialNonTerminal());
    }

    /**
     * convert to pushdown automata accepting by empty stack 
     * 
     * @author Daniel Pelisek
     * 
     * @param cfg given context-free grammar  
     * @return    push-down automaton
     */
    public PushDownAutomaton toEmptyStackPDA(ContextFreeGrammar cfg) {

        Map<String, String> relation = new HashMap<String, String>();

        for (String terminal : cfg.getTerminals()) {
            relation.put("(q, " + terminal + ", " + terminal + ")", "{(q, \\e)}");
        }

        for (String nonTerminal : cfg.getRules().keySet()) {
            StringBuilder output = new StringBuilder("{");
            for (String rule : cfg.getRules().get(nonTerminal)) {
                output.append("(q, ").append((rule.isEmpty()) ? "\\e" : rule).append("), ");
            }
            output.delete(output.length() - 2, output.length());
            output.append("}");
            relation.put("(q, \\e, " + nonTerminal + ")", output.toString());
        }

        StringBuilder text = new StringBuilder();
        text.append("({q}, {").append(cfg.getTerminals().toString().replaceAll("[\\[\\]]", "")).append("}, {");
        text.append(cfg.getTerminals().toString().concat(", " + cfg.getNonTerminals().toString()).replaceAll("[\\[\\]]", ""));
        text.append("}, \\d, q, ").append(cfg.getInitialNonTerminal()).append(", {})\n");
        for (String leftSide : relation.keySet()) {
            String rightSide = relation.get(leftSide).toString().replace("[", "{").replace("]", "}");
            text.append(leftSide).append(" = ").append(rightSide).append("\n");
        }

        return new PushDownAutomaton(text.toString());
    }

    /**
     * convert to pushdown automata accepting by finalState
     * 
     * @author Daniel Pelisek
     * 
     * @param cfg given context-free grammar  
     * @return    push-down automaton
     */
    public PushDownAutomaton toFinalStatePDA(ContextFreeGrammar cfg) {

        Map<String, Set<String>> relation = new HashMap<String, Set<String>>();

        for (String terminal : cfg.getTerminals()) {
            Set<String> s = new HashSet<String>(Arrays.asList("(q, " + terminal + ")"));
            relation.put("(q, " + terminal + ", \\e)", s);
        }

        for (String nonTerminal : cfg.getRules().keySet()) {
            for (String rule : cfg.getRules().get(nonTerminal)) {
                String input = "(q, \\e, " + ((rule.isEmpty()) ? "\\e" : rule) + ")";
                if (!relation.containsKey(input)) {
                    relation.put(input, new HashSet<String>());
                }
                relation.get(input).add("(q, " + nonTerminal + ")");
            }
        }

        relation.put("(q, \\e, \\t" + cfg.getInitialNonTerminal() + ")", new HashSet<String>());
        relation.get("(q, \\e, \\t" + cfg.getInitialNonTerminal() + ")").add("(r, \\e)");

        StringBuilder text = new StringBuilder();
        text.append("({q, r}, {").append(cfg.getTerminals().toString().replaceAll("[\\[\\]]", "")).append("}, {");
        text.append(cfg.getTerminals().toString().concat(", " + cfg.getNonTerminals().toString()).replaceAll("[\\[\\]]", ""));
        text.append(", \\t}, \\d, q, \\t, {r})\n");
        for (String leftSide : relation.keySet()) {
            String rightSide = relation.get(leftSide).toString().replace("[", "{").replace("]", "}");
            text.append(leftSide).append(" = ").append(rightSide).append("\n");
        }

        return new PushDownAutomaton(text.toString());
    }
}
