/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.cfg.grammar;

import java.util.Map.Entry;
import java.util.*;

/**
 *
 * @author NICKT
 */
public class ContextFreeGrammar {

    private Set<String> nonTerminals;
    private Set<String> terminals;
    private Map<String, Set<String>> rules;
    private Map<String, Set<List<String>>> rulesArray = new HashMap<String, Set<List<String>>>();
    private String initialNonTerminal;

    public ContextFreeGrammar(Set<String> nonTerminals, Set<String> terminals, Map<String, Set<String>> rules, Map<String, Set<List<String>>> rulesArray, String initialNonTerminal) {
        this.nonTerminals = nonTerminals;
        this.terminals = terminals;
        this.rules = rules;
        this.rulesArray = rulesArray;
        this.initialNonTerminal = initialNonTerminal;
    }

    public ContextFreeGrammar(Set<String> terminals, Map<String, Set<String>> rules, String initialNonTerminal) {
        this.terminals = terminals;
        this.rules = rules;
        this.initialNonTerminal = initialNonTerminal;
        this.nonTerminals = rules.keySet();
    }

    public ContextFreeGrammar(Set<String> nonTerminals, Set<String> terminals, Map<String, Set<String>> rules, String initialNonTerminal) {
        this.nonTerminals = nonTerminals;
        this.terminals = terminals;
        this.rules = rules;
        this.initialNonTerminal = initialNonTerminal;
    }

    public Set<String> getNonTerminals() {
        return Collections.unmodifiableSet(nonTerminals);
    }

    public Set<String> getTerminals() {
        return Collections.unmodifiableSet(terminals);
    }

    public Map<String, Set<String>> getRules() {
        return Collections.unmodifiableMap(rules);
    }

    public String getInitialNonTerminal() {
        return initialNonTerminal;
    }

    public Map<String, Set<List<String>>> getRulesArray() {
        return Collections.unmodifiableMap(rulesArray);
    }

    @Override
    public String toString() {
        StringBuilder outputString = new StringBuilder();
        outputString.append(initialNonTerminal).append(" -> ");  //vypíšeme všechna pravidla iniciálního neterminálu
        //Map<String, Set<String>> backupRules = new TreeMap<String, Set<String>>();
        //backupRules.putAll(rules);
        Map<String, Set<String>> backupRules = new TreeMap<String, Set<String>>(rules);
        if (backupRules.get(initialNonTerminal) == null)
            return "NAG";
        Set<String> rulesSet = new TreeSet<String>();
        rulesSet.addAll(backupRules.remove(initialNonTerminal));
        Iterator<String> initialRules = rulesSet.iterator();
        while (initialRules.hasNext()) {
            String rule = initialRules.next();
            if (rule.equals("")) { //když se pravidlo rovná prázdnému řetězci, přepiš na \e
                outputString.append("\\e");
            } else {
                outputString.append(rule);
            }
            if (initialRules.hasNext()) {
                outputString.append(" | ");
            } else if (!backupRules.isEmpty()) {
                outputString.append(",").append(System.getProperty("line.separator"));
            }
        }

        Iterator<Map.Entry<String, Set<String>>> it = backupRules.entrySet().iterator();
        while (it.hasNext()) {  //PRO KAŽDOU POLOŽKU MAPY
            Map.Entry<String, Set<String>> entry = it.next();
            outputString.append(entry.getKey()).append(" -> ");
            Set<String> otherRulesSet = new TreeSet<String>();
            otherRulesSet.addAll(entry.getValue());
            Iterator<String> otherRules = otherRulesSet.iterator();
            while (otherRules.hasNext()) { //PRO KAŽDÉ PRAVIDLO
                String rule = otherRules.next();
                if (rule.equals("")) {  //když se pravidlo rovná prázdnému řetězci, přepiš na \e
                    outputString.append("\\e");
                } else {
                    outputString.append(rule);
                }
                if (otherRules.hasNext()) {
                    outputString.append(" | ");
                } else if (it.hasNext()) {
                    outputString.append(",").append(System.getProperty("line.separator"));
                }
            }
        }

        return outputString.toString();
    }

    public void mapToArrayMap() {
        for (String nonTerminal : nonTerminals) {
            Set<List<String>> newRules = new HashSet<List<String>>();
            if (rules.containsKey(nonTerminal)) {
                for (String rule : rules.get(nonTerminal)) {
                    List<String> array = new ArrayList<String>();
                    if (rule.equals("")) { //pokud je pravidlo epsilon, nemusíme nic zišťovat
                        array.add("");
                    } else {
                        while (!rule.equals("")) { //dokud nemáme přečteny všechny symboly pravidla
                            boolean wasEqualWithSymbol = false;
                            for (String symbol : nonTerminals) { //procházíme každé pravidlo po symbolech a rozděláváme je do pole
                                if (rule.startsWith(symbol) && !rule.substring(symbol.length()).startsWith("'")) {
                                    array.add(symbol);
                                    rule = rule.replaceFirst(symbol, "");
                                    wasEqualWithSymbol = true;
                                    break;
                                }
                            }
                            if (!wasEqualWithSymbol) {
                                String symbol = rule.substring(0, 1);
                                array.add(symbol);
                                rule = rule.replaceFirst(symbol, "");
                            }
                        }
                    }
                    newRules.add(array);
                }
                this.rulesArray.put(nonTerminal, newRules);
            }
        }
    }

    public ContextFreeGrammar remapNonTerminals(ContextFreeGrammar cfg, Map<String, String> mappings) {
        Map<String, Set<String>> newRules = new HashMap<String, Set<String>>();
        if (cfg.getRulesArray().isEmpty()) { //pokud eště nemáme ještě mapu inicializovanou, doděláme
            cfg.mapToArrayMap();
        }
        Iterator<Entry<String, Set<List<String>>>> it = cfg.getRulesArray().entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Set<List<String>>> entry = it.next();
            Set<String> helpRules = new HashSet<String>();
            for (List<String> rule : entry.getValue()) {
                StringBuilder outputString = new StringBuilder();
                for (String symbol : rule) {
                    if (mappings.containsKey(symbol)) {
                        outputString.append(mappings.get(symbol));
                    } else {
                        outputString.append(symbol);
                    }
                }
                helpRules.add(outputString.toString());
            }
            newRules.put(mappings.get(entry.getKey()), helpRules);
        }
        return new ContextFreeGrammar(cfg.getTerminals(), newRules, mappings.get(cfg.getInitialNonTerminal()));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContextFreeGrammar other = (ContextFreeGrammar) obj;
        if (this.terminals != other.terminals && (this.terminals == null || !this.terminals.equals(other.terminals))) {
            return false;
        }

        if (this.initialNonTerminal == null || other.initialNonTerminal == null || this.nonTerminals == null || other.nonTerminals == null || this.rules == null || other.rules == null) {
            return false;
        }

        //pokud vypadají identicky
        if (this.rules.equals(other.rules) && this.nonTerminals.equals(other.nonTerminals) && this.initialNonTerminal.equals(other.initialNonTerminal)) {
            return true;
        } else if (this.nonTerminals.size() != other.nonTerminals.size()) {
            //jsou stejné i přes jiná jména neterminálů?
            return false;
        } else if (this.rules.get(this.initialNonTerminal).size() != other.rules.get(other.initialNonTerminal).size()) {
            return false;
        } else {
            //maji stejne pocty pravidel?
            Map<Integer, Integer> thisRulesSizes = new HashMap<Integer, Integer>();
            Map<Integer, Integer> otherRulesSizes = new HashMap<Integer, Integer>();
            for (Map.Entry<String, Set<String>> entry : this.rules.entrySet()) {
                int i = entry.getValue().size();
                if (thisRulesSizes.containsKey(i)) {
                    thisRulesSizes.put(i, thisRulesSizes.get(i) + 1);
                } else {
                    thisRulesSizes.put(i, 1);
                }
            }
            for (Map.Entry<String, Set<String>> entry : other.rules.entrySet()) {
                int i = entry.getValue().size();
                if (otherRulesSizes.containsKey(i)) {
                    otherRulesSizes.put(i, otherRulesSizes.get(i) + 1);
                } else {
                    otherRulesSizes.put(i, 1);
                }
            }
            if (!thisRulesSizes.equals(otherRulesSizes)) {
                return false;
            }
            //vlastni porovnavani
            Map<String, String> thisToOther = new HashMap<String, String>();
            Map<String, String> otherToThis = new HashMap<String, String>();
            thisToOther.put(this.initialNonTerminal, other.initialNonTerminal); //predpokladame, ze se inicialni pravidla rovnaji
            otherToThis.put(other.initialNonTerminal, other.initialNonTerminal);
            return core(this, other, thisToOther, otherToThis);
        }
    }

    private boolean core(ContextFreeGrammar thisCFG, ContextFreeGrammar otherCFG, Map<String, String> thisToOther, Map<String, String> otherToThis) {
        boolean iterate = true;
        while (iterate) {
            iterate = false;
            Map<Mappings, Integer> thisMappings = new HashMap<Mappings, Integer>();
            Map<Mappings, Integer> otherMappings = new HashMap<Mappings, Integer>();
            List<Mappings> listThisMappings = new ArrayList<Mappings>();
            List<Mappings> listOtherMappings = new ArrayList<Mappings>();
            for (String thisNonterminal : thisCFG.nonTerminals) {
//                    if (thisNonterminal.equals(this.initialNonTerminal) || !thisToOther.containsKey(thisNonterminal)) {
                Mappings mapping = new Mappings(thisNonterminal, thisCFG, thisToOther);
                if (thisMappings.containsKey(mapping)) {
                    thisMappings.put(mapping, thisMappings.get(mapping) + 1);
                } else {
                    thisMappings.put(mapping, 1);
                }
                listThisMappings.add(mapping);
//                    }
            }
            for (String otherNonterminal : otherCFG.nonTerminals) {
//                    if (otherNonterminal.equals(other.initialNonTerminal) || !otherToThis.containsKey(otherNonterminal)) {
                Mappings mapping = new Mappings(otherNonterminal, otherCFG, otherToThis);
                if (otherMappings.containsKey(mapping)) {
                    otherMappings.put(mapping, otherMappings.get(mapping) + 1);
                } else {
                    otherMappings.put(mapping, 1);
                }
                listOtherMappings.add(mapping);
//                    }
            }
            if (!thisMappings.equals(otherMappings)) {
                return false;
            }
            if (thisToOther.keySet().equals(thisCFG.nonTerminals)) {
                return true;
            } else { //když byla provedena změna substitucí
                for (Map.Entry<Mappings, Integer> entry : thisMappings.entrySet()) {
                    if (entry.getValue().equals(1) && !thisToOther.containsKey(entry.getKey().getName())) {
                        for (Mappings equivalent : otherMappings.keySet()) {
                            if (equivalent.equals(entry.getKey())) {
                                thisToOther.put(entry.getKey().getName(), equivalent.getName());
                                otherToThis.put(equivalent.getName(), equivalent.getName());
                                break;
                            }
                        }
                        iterate = true;
                    } //pokud obsahuje zamenitelne neterminaly
                    if (entry.getValue().intValue() > 1 && !entry.getKey().hasQuestionMarkInOwnRules()) {
                        for (Mappings thisMapping : listThisMappings) {
                            if (thisMapping.equals(entry.getKey()) && !thisToOther.containsKey(thisMapping.getName())) {
                                for (Mappings otherMapping : listOtherMappings) {
                                    if (otherMapping.equals(entry.getKey()) && !otherToThis.containsKey(otherMapping.getName())) {
                                        thisToOther.put(thisMapping.getName(), otherMapping.getName());
                                        otherToThis.put(otherMapping.getName(), otherMapping.getName());
                                        iterate = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //neodhalili jsme všechny jisté substituce
            if (!iterate) {
                for (Map.Entry<Mappings, Integer> entry : thisMappings.entrySet()) {
                    if (entry.getValue() > 1) {
                        List<String> thisSubstitutions = new ArrayList<String>();
                        List<String> otherSubstitutions = new ArrayList<String>();
                        for (Mappings thisMapping : listThisMappings) {
                            if (thisMapping.equals(entry.getKey())) {
                                thisSubstitutions.add(thisMapping.getName());
                            }
                        }
                        for (Mappings otherMapping : listOtherMappings) {
                            if (otherMapping.equals(entry.getKey())) {
                                otherSubstitutions.add(otherMapping.getName());
                            }
                        }
                        for (String thisSubstitute : thisSubstitutions) {
                            for (String otherSubstitute : otherSubstitutions) {
                                Map<String, String> newThisToOther = new HashMap<String, String>();
                                Map<String, String> newOtherToThis = new HashMap<String, String>();
                                newThisToOther.putAll(thisToOther);
                                newThisToOther.put(thisSubstitute, otherSubstitute);
                                newOtherToThis.putAll(otherToThis);
                                newOtherToThis.put(otherSubstitute, otherSubstitute);
                                if (core(thisCFG, otherCFG, newThisToOther, newOtherToThis)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash =
                97 * hash + (this.nonTerminals != null ? this.nonTerminals.hashCode() : 0);
        hash =
                97 * hash + (this.terminals != null ? this.terminals.hashCode() : 0);
        hash =
                97 * hash + (this.rules != null ? this.rules.hashCode() : 0);
        hash =
                97 * hash + (this.initialNonTerminal != null ? this.initialNonTerminal.hashCode() : 0);
        return hash;
    }
}

