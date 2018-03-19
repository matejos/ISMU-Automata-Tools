package cz.muni.fi.cfg.forms;

import cz.muni.fi.cfg.grammar.ContextFreeGrammar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @author      Daniel Pelisek <dpelisek@gmail.com>
 * @version     1.1                 
 * @since       2011-03-2
 */
public class Analyser {
        
    /**
     * analyzes given context-free grammar
     * 
     * TODO: dopsat co vsechno udela
     * 
     * @param cfg given context-free grammar
     * @return    log of analyze
     */
    public Map<String, String> analyse(ContextFreeGrammar cfg) {
        
        StringBuilder log = new StringBuilder();
        Map<String, String> mapLog = new HashMap<String, String>();
        
        // generated language
        if (!languageIsNotEmpty(cfg)) {
            mapLog.put("Gramatika generuje prázdný jazyk.", "");
        } else {
            String finiteness = (languageIsFinite(cfg))?"neprázdný konečný":"nekonečný";
            Set<String> words = generateWords(cfg, 10);
            mapLog.put("Gramatika generuje " + finiteness + " jazyk.", "Příklad slov: " + words.toString().replaceAll("[\\[\\]]", "") + ".\n");
        }
        
        boolean writeIfHasSimpleRules, writeIfIsLeftRecursive, writeIfHasEpsilonRules;
        writeIfHasSimpleRules = writeIfIsLeftRecursive = writeIfHasEpsilonRules = true;
        
        // normal forms
        if (hasEpsilonRules(cfg, log.delete(0, log.length()))) {
            mapLog.put("Gramatika má ε-pravidla", log.toString());
            if (isReduced(cfg, log.delete(0, log.length()))) {
                mapLog.put("Gramatika je redukovaná.", "");
            } else {
                mapLog.put("Gramatika není redukovaná.", log.toString());
            }
        } else {
            if (isReduced(cfg, log.delete(0, log.length()))) {
                if (isProper(cfg, log.delete(0, log.length()))) {
                    mapLog.put("Gramatika je vlastní.", "");
                    writeIfHasEpsilonRules = false;
                } else {
                    mapLog.put("Gramatika není vlastní.", log.toString());
                    mapLog.put("Gramatika je redukovaná.", "");
                }
            } else {
                mapLog.put("Gramatika není redukovaná.", log.toString());
            }
            if (isInCNF(cfg, log.delete(0, log.length()))) {
                mapLog.put("Gramatika je v Chomského normální formě.", "");
                writeIfHasEpsilonRules = writeIfHasSimpleRules = false;
            } else {
                mapLog.put("Gramatika není Chomského normální formě.", log.toString());
            }
            if (isInGNF(cfg, log.delete(0, log.length()))) {
                mapLog.put("Gramatika je v Greibachové normální formě.", "");
                writeIfHasEpsilonRules =  writeIfHasSimpleRules = writeIfIsLeftRecursive = false;
            } else {
                mapLog.put("Gramatika není Greibachové normální formě.", log.toString());
            }
            if (writeIfHasEpsilonRules) {
                mapLog.put("Gramatika je bez ε-pravidel", "");
            }
        }
        if (writeIfHasSimpleRules) {
            if (hasSimpleRules(cfg, log.delete(0, log.length()))) {
                mapLog.put("Gramatika má jednoduchá pravidla.", log.toString());
            } else {
                mapLog.put("Gramatika je bez jednoduchých pravidel.", "");
            }
        }
        if (writeIfIsLeftRecursive) {
            if (isLeftRecursive(cfg)) {
                mapLog.put("Gramatika má levou rekurzi.", "");
            } else {
                mapLog.put("Gramatika je bez levé rekurze.", "");
            }
        }
        
        // self embedding
        if (isSelfEmbedding(cfg)) {
            mapLog.put("Gramatika má vlastnost sebevložení.", "");
        } else {
            mapLog.put("Gramatika nemá vlastnost sebevložení.", "");
        }

        return mapLog;
    }
    
    /**
     * finds all productive non-terminals
     * 
     * @param cfg given context-free grammar
     * @return    set of strings representing set of found non-terminals
     */
    public Set<String> getProductiveSymbols(ContextFreeGrammar cfg) {
        
        Set<String> productiveSymbols = new HashSet<String>();
            
        for (int i = 0; i <= productiveSymbols.size(); i++) {
            for (String n : cfg.getRules().keySet()) {
                for (String r : cfg.getRules().get(n)) {
                    if (r.matches(new RegExpMaker().regexNeUnionAlphabeth(productiveSymbols, cfg.getTerminals())))
                        productiveSymbols.add(n);
                }
            }
        }
        return Collections.unmodifiableSet(productiveSymbols);
    }
    
    /**
     * determines whether given grammar generates empty language or not
     * 
     * @param cfg given context-free grammar  
     * @return    true if given grammar doesn't generate empty language, false otherwise
     */
    public boolean languageIsNotEmpty(ContextFreeGrammar cfg) {
        return getProductiveSymbols(cfg).contains(cfg.getInitialNonTerminal());
    }
    
    /**
     * determines whether given grammar generates finite language or not
     * 
     * transform given grammar into Chomsky normal form without useless symbols
     * and then check whether it contains any recursion. If not, language is finite.
     * 
     * @param cfg given context-free grammar  
     * @return    true if given grammar generates finite language, false otherwise
     */
    public boolean languageIsFinite(ContextFreeGrammar originalCfg) {
        
        if (!languageIsNotEmpty(originalCfg))
            return true;
        
        // transformation to reduced CNF withouth epsilon production
        Transformations transformer = new Transformations();
        ContextFreeGrammar cfg;
        try {
            cfg = transformer.transformToCNF(originalCfg);
        } catch (TransformationException ex) {
            return false;
        }
        
        // check if there is any recursion
        for (String nonterminal : cfg.getRules().keySet()) {
            
            List<String> visited = new ArrayList<String>();
            Set<String> unvisited = new HashSet<String>(cfg.getNonTerminals());
            
            visited.add(nonterminal);
            unvisited.remove(nonterminal);
            
            for (int i = 0; i < visited.size(); i++) {
                for (List<String> rule : cfg.getRulesArray().get(visited.get(i))) {
                    if (rule.contains(nonterminal))
                        return false;
                    for (String r : rule) {
                        if(unvisited.remove(r))
                            visited.add(r);
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * Generates a random symbols from generated language.
     * 
     * Converts given context-free grammar into Chomsky normal form without useless
     * symbols and epsilon productions.
     * Sets "priority" of each non-terminal symbol - priority means how many productions
     * is needed to rewrite non-terminal to terminal symbols only.
     * Generates random string of terminal and non-terminal symbols with length
     * up to 2^5.
     * Generated string rewrite to word consist only from terminal symbols
     * 
     * @param originalCfg given context-free grammar
     * @param count       maximal count of generated words
     * @return            set of random words from generated language
     */
    public Set<String> generateWords(ContextFreeGrammar originalCfg, int count) {
        
        Set<String> words = new HashSet<String>();
        
        if (!languageIsNotEmpty(originalCfg)) {
            return words;
        }

        // transformation to reduced cfg in CNF withouth epsilon productions
        ContextFreeGrammar cfg;
        try {
            cfg = new Transformations().transformToCNF(originalCfg);
        } catch (TransformationException ex) {
            return words;
        }
        
        // set priorities of non-terminals
        Map<String, Integer> priorities = new HashMap<String, Integer>();
        for (int i = 0; priorities.size() < cfg.getNonTerminals().size(); i++) {
            for (String nonTerminal : cfg.getNonTerminals()) {
                if (priorities.containsKey(nonTerminal))
                    continue;
                int priority = cfg.getNonTerminals().size();
                for (List<String> rule : cfg.getRulesArray().get(nonTerminal)) {
                    if (rule.size() <= 1) {
                        priority = 1;
                        break;
                    } else {
                        if (priorities.containsKey(rule.get(0)) && priorities.containsKey(rule.get(1))) {
                            int p = Math.max(priorities.get(rule.get(0)), priorities.get(rule.get(1)));
                            priority = Math.min(p + 1, priority);
                        }
                    }
                }
                if (priority == i+1) {
                    priorities.put(nonTerminal, priority);}
            }
        }
        
        for (int c = 0; c < count; c++) {

            // generate random array list of symbols
            List<String> symbols = new ArrayList<String>();
            symbols.add(cfg.getInitialNonTerminal());
            int iterates = new Random().nextInt(10);
            for (int i = 0; i < iterates; i++) {
                for (int j = 0; j < symbols.size(); j++) {
                    if (cfg.getTerminals().contains(symbols.get(j)) || symbols.get(j).isEmpty())
                        continue;
                    Set<List<String>> rules = new HashSet<List<String>>(cfg.getRulesArray().get(symbols.get(j)));
                    List<String> rule = (List<String>) rules.toArray()[new Random().nextInt(rules.size())];
                    symbols.remove(j--);
                    for (int k = 0; k < rule.size(); k++) {
                        symbols.add(++j, rule.get(k));
                    }
                }
            }

            // rewrite symbols to terminal symbols only
            StringBuilder word = new StringBuilder();
            while (!symbols.isEmpty()) {
                if (cfg.getNonTerminals().contains(symbols.get(0))) {
                    int priority = cfg.getNonTerminals().size();
                    List<String> rule = new ArrayList<String>();
                    for (List<String> r : cfg.getRulesArray().get(symbols.get(0))) {
                        if (r.size() <= 1) {
                            rule = r;
                            break;
                        } else if (Math.max(priorities.get(r.get(0)), priorities.get(r.get(1))) <= priority) {
                            int newPriority = Math.max(priorities.get(r.get(0)), priorities.get(r.get(1)));
                            if (newPriority <= priority) {
                                priority = newPriority;
                                rule = r;
                            }
                        }
                    }
                    symbols.remove(0);
                    for (int k = 0; k < rule.size(); k++) {
                        symbols.add(k, rule.get(k));
                    }
                } else {
                    word.append(symbols.remove(0));
                }
            }
            if (word.toString().isEmpty()) {
                word.append("\\e");
            }
            words.add(word.toString());
        }
        
        return words;
    }
    
    /**
     * determines whether given grammar doesn't have any rule
     * 
     * @param cfg given context-free grammar  
     * @return    true if given grammar doesn't have any rule, false otherwise
     */
    public boolean isNAG(ContextFreeGrammar cfg) {
        return cfg.getRules().isEmpty();
    }
    
    /**
     * determines whether given grammar has got any unproductive symbols
     * 
     * @param cfg given context-free grammar  
     * @param log optional parameter for logging
     * @return    true if given grammar has unproductive symbols, false otherwise
     */
    public boolean hasUnproductiveSymbols(ContextFreeGrammar cfg, StringBuilder... log) {
        
        Set<String> unproductiveSymbols = new HashSet<String>(cfg.getNonTerminals());
        unproductiveSymbols.removeAll(this.getProductiveSymbols(cfg));
        
        // logging
        if (log.length > 0 && !unproductiveSymbols.isEmpty()) {
            if (unproductiveSymbols.size() == 1)
                log[0].append("Neterminál ").append(unproductiveSymbols.toString().replaceAll("[\\[\\]]", "")).append(" je nenormovaný.\n");
            else 
                log[0].append("Neterminály ").append(unproductiveSymbols.toString().replaceAll("[\\[\\]]", "")).append(" jsou nenormované.\n");
        }

        return !unproductiveSymbols.isEmpty();
    }

    /**
     * determines whether given grammar has got any inaccessible symbols
     * 
     * @param cfg given context-free grammar
     * @param log optional parameter for logging
     * @return    true if given grammar has inaccessible symbols, false otherwise
     */
    public boolean hasInaccessibleSymbols(ContextFreeGrammar cfg, StringBuilder... log) {
        
        List<String> visited = new ArrayList<String>();
        Set<String> unvisited = new HashSet<String>(cfg.getRules().keySet());
        
        visited.add(cfg.getInitialNonTerminal());
        unvisited.remove(cfg.getInitialNonTerminal());

        for (int i = 0; i < visited.size(); i++) {
            for (List<String> rule : cfg.getRulesArray().get(visited.get(i))) {
                for (String r : rule) {
                    if (unvisited.remove(r))
                        visited.add(r);
                }
            }
        }
        
        // logging
        if (log.length > 0 && unvisited.size() == 1) {
            log[0].append("Neterminál ").append(unvisited.toString().replaceAll("[\\[\\]]", "")).append(" je nedosažitelný.\n");
        } else if (log.length > 0 && unvisited.size() > 1) {
            log[0].append("Neterminály ").append(unvisited.toString().replaceAll("[\\[\\]]", "")).append(" jsou nedosažitelné.\n");
        }
        
        return !unvisited.isEmpty();
    }

    /**
     * determines whether given grammar is reduced or not
     * 
     * @param cfg given context-free grammar 
     * @param log optional parameter for logging 
     * @return    true if given grammar is reduced, false otherwise
     */    
    public boolean isReduced(ContextFreeGrammar cfg, StringBuilder... log) {        
        return (!hasUnproductiveSymbols(cfg, log) & !hasInaccessibleSymbols(cfg, log));
    }

    /**
     * finds all non-terminal symbols which can generate empty symbols (N ->* /e)
     * 
     * @param cfg given context-free grammar  
     * @return    set of strings representing non-terminal symbols
     */
    public Set<String> buildNeps(ContextFreeGrammar cfg) {
        
        Set<String> nEps = new HashSet<String>();
            
        for (int i = 0; i <= nEps.size(); i++) {
            for (String n : cfg.getRules().keySet()) {
                for (String r : cfg.getRules().get(n)) {
                    if (r.matches(new RegExpMaker().regexPositiveIterateThese(nEps)))
                        nEps.add(n);
                }
            }
        }
        
        return Collections.unmodifiableSet(nEps);
    }

    /**
     * determines whether given grammar has initial symbol on right-hand side of any rule
     * 
     * @param cfg given context-free grammar  
     * @param log optional parameter for logging 
     * @return    true if initial symbol is on right-hand side of any rule, false otherwise
     */
    public boolean hasInitialSymbolOnRightSide(ContextFreeGrammar cfg, StringBuilder... log) {
        for (String nonterminal : cfg.getRules().keySet()) {
            for (List<String> rule : cfg.getRulesArray().get(nonterminal)) {
                if (rule.contains(cfg.getInitialNonTerminal())) {
                    if (log.length > 0) {
                        log[0].append("Počáteční neterminál ").append(cfg.getInitialNonTerminal());
                        log[0].append(" je na pravé straně pravidla ").append(nonterminal);
                        log[0].append(" -> ").append(rule.toString().replaceAll("[\\[\\], ]", ""));
                    }
                    return true;
                }
            }
        }
        return false;
    }
   
    /**
     * determines whether any non-terminal symbol of given grammar (except initial
     * symbol) generates empty symbols
     * 
     * @param cfg given context-free grammar  
     * @param log optional parameter for logging 
     * @return    true if any symbol (except initial symbol) generates empty symbols, false otherwise
     */
    public boolean hasEpsilonRules(ContextFreeGrammar cfg, StringBuilder... log) {
        
        Set<String> neps = new HashSet<String>(buildNeps(cfg));
        
        if (neps.remove(cfg.getInitialNonTerminal()) && hasInitialSymbolOnRightSide(cfg, log)) {
            if (log.length > 0)
                log[0].append(" a zároveň platí ").append(cfg.getInitialNonTerminal()).append(" ->* ε.\n");
            return true;
        }
        
        // logging
        if (log.length > 0 && neps.size() == 1) {
            log[0].append("Neterminál ").append(neps.toString().replaceAll("[\\[\\]]", "")).append(" lze přepsat na ε.\n");
        } else if (log.length > 0 && neps.size() > 1) {
            log[0].append("Neterminály ").append(neps.toString().replaceAll("[\\[\\]]", "")).append(" lze přepsat na ε.\n");
        }
        
        return (!neps.isEmpty());
    }

    /**
     * determines whether any rule of given grammar is simple
     * 
     * @param cfg given context-free grammar
     * @param log optional parameter for logging   
     * @return    true if any rule of given grammar is simple, false otherwise
     */
    public boolean hasSimpleRules(ContextFreeGrammar cfg, StringBuilder... log) {
        
        for (String n : cfg.getRules().keySet()) {
            for (String r : cfg.getRules().get(n)) {
                if (cfg.getNonTerminals().contains(r)) {
                    if (log.length > 0)
                        log[0].append("Pravidlo ").append(n).append(" -> ").append(r).append(" je jednoduché.\n");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines whether the given grammar is proper on not
     * 
     * Grammar is proper if it is reduced, has not epsilon rules and is not cyclic.
     * 
     * @param cfg given context-free grammar  
     * @param log optional parameter for logging  
     * @return    true if given grammar is proper, false otherwise
     */
    public boolean isProper(ContextFreeGrammar cfg, StringBuilder... log) {
        
        if (hasEpsilonRules(cfg) || !isReduced(cfg))
            return false;
        
        for (String n : cfg.getRules().keySet()) {
            
            List<String> simpleRules = new ArrayList<String>();
            Set<String> unattendedSymbols = new HashSet<String>(cfg.getNonTerminals());
            
            simpleRules.add(n);
            unattendedSymbols.remove(n);
            
            for (int i = 0; i < simpleRules.size(); i++) {
                for (String r : cfg.getRules().get(simpleRules.get(i))) {
                    if (n.equals(r)) {
                        if (log.length > 0)
                            log[0].append("Platí ").append(n).append(" =>+ ").append(r).append(".\n");
                        return false;
                    }
                    if (unattendedSymbols.remove(r))
                        simpleRules.add(r);
                }
            }
        }
        
        return true;
    }

    /**
     * determines whether the given grammar is in Chomsky normal form
     * 
     * @param cfg given context-free grammar  
     * @param log optional parameter for logging  
     * @return    true if given grammar is in CNF, false otherwise
     */
    public boolean isInCNF(ContextFreeGrammar cfg, StringBuilder... log) {

        if (this.hasEpsilonRules(cfg))
            return false;
        
        for (String n : cfg.getRules().keySet()) {
            for (String r : cfg.getRules().get(n)) {
                
                if (n.equals(cfg.getInitialNonTerminal()) && r.equals(""))
                    continue;
                if (cfg.getTerminals().contains(r))
                    continue;
                String pattern = cfg.getNonTerminals().toString().replace(", ", "|").replace("[", "(").replace("]", ")");
                if (!r.matches(pattern + "{2}")) { 
                    if (log.length > 0)
                        log[0].append("Pravidlo ").append(n).append(" -> ").append(r).append(" neodpovídá CNF.\n");
                    return false;
                }
            }
        }     
        
        return true;
    }

    /**
     * get list of possible orderings of non-terminal symbols from grammar without left recursion
     * 
     * result will be reduced to only non-terminals from second parameter, if it isn't null
     * 
     * @param cfg                  given context-free grammar 
     * @param originalNonTerminals non-terminal symbols to reduction
     * @return                     sorted lists of non-terminals or null if given grammar has left recursion
     */
    public Set<List<String>> getOrdering(ContextFreeGrammar cfg, Set<String> originalNonTerminals) {
        
        Set<String> neps = this.buildNeps(cfg);
        Map<String, Set<String>> relates = new HashMap<String, Set<String>>();
        List<List<String>> orderings = new ArrayList<List<String>>();
        orderings.add(new ArrayList<String>());
        orderings.get(0).add(cfg.getInitialNonTerminal());
        
        // relates < A ∈ non-terminals , [X ∈ non-terminals | X -> A...  ∈ rules]>
        for (String nonTerminal : cfg.getNonTerminals()) {
            relates.put(nonTerminal, new HashSet<String>());
            for (String n : cfg.getRulesArray().keySet()) {
                for (List<String> rule : cfg.getRulesArray().get(n)) {
                    for (String symbol : rule) {
                        if (symbol.equals(nonTerminal))
                            relates.get(nonTerminal).add(n);
                        if (!neps.contains(symbol))
                            break;
                    }
                }
            }
        }
        
        if (!relates.get(cfg.getInitialNonTerminal()).isEmpty())
            return null;
                
        while (!orderings.isEmpty()) {
            List<String> ordering = orderings.get(0);
            if(ordering.size()==cfg.getNonTerminals().size())
                break;
            for (String nonTerminal : relates.keySet()) {
                if (!ordering.contains(nonTerminal) && ordering.containsAll(relates.get(nonTerminal))) {
                    List<String> newOrdering = new ArrayList<String>(ordering);
                    newOrdering.add(nonTerminal);
                    orderings.add(newOrdering);
                }
            }
            orderings.remove(0);
        }
        
        if (originalNonTerminals != null) {
            for (List<String> ordering : orderings) {
                ordering.retainAll(originalNonTerminals);
            }
        }
        
        return (orderings.isEmpty()) ? null : new HashSet<List<String>>(orderings);
    }
    
    /**
     * determines whether the given grammar is without left recursion
     * 
     * @param cfg given context-free grammar 
     * @param log optional parameter for logging  
     * @return    true if given grammar is with left recursion, false otherwise
     */
    public boolean isLeftRecursive(ContextFreeGrammar cfg) {
        return (getOrdering(cfg, null) == null);
    }

    /**
     * determines whether the given grammar is in Greibach normal form
     * 
     * @param cfg given context-free grammar  
     * @param log optional parameter for logging  
     * @return    true if given grammar is in GNF, false otherwise
     */
    public boolean isInGNF(ContextFreeGrammar cfg, StringBuilder... log) {
        
        if (this.hasEpsilonRules(cfg))
            return false;
        
        for (String n : cfg.getRules().keySet()) {
            for (List<String> r : cfg.getRulesArray().get(n)) {
                if (n.equals(cfg.getInitialNonTerminal()) && r.isEmpty())
                    continue;
                List<String> rule = new ArrayList<String>(r);
                if (!cfg.getTerminals().contains(rule.remove(0)) || !cfg.getNonTerminals().containsAll(rule)) {
                    if (log.length > 0) {
                        log[0].append("Pravidlo ").append(n).append(" -> ");
                        log[0].append(r.toString().replaceAll("[\\[\\], ]", "")).append(" neodpovídá GNF.\n");
                    }
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * determines whether the given grammar is self-embedding
     * 
     * @param cfg given context-free grammar  
     * @return    true if given grammar is self-embedding, false otherwise
     */
    public boolean isSelfEmbedding(ContextFreeGrammar originalCfg) {
        
        ContextFreeGrammar cfg = new Transformations().removeSimpleRules(originalCfg);
        Set<String> productiveSymbols = this.getProductiveSymbols(cfg);
        
        for (String nonterminal : cfg.getRules().keySet()) {
            
            List<EmbeddedNonterminal> visited = new ArrayList<EmbeddedNonterminal>();
            visited.add(new EmbeddedNonterminal(nonterminal, false, false));
            
            for (int i = 0; i < visited.size(); i++) {
                EmbeddedNonterminal wanted = visited.get(i);
                for (String n : cfg.getRulesArray().keySet()) {
                    if (visited.contains(new EmbeddedNonterminal(n, true, true)))
                        continue;
                    for (List<String> r : cfg.getRulesArray().get(n)) {
                        if (!r.contains(wanted.toString()))
                            continue;
                        boolean left, right;
                        left = right = false;
                        for (int j = 0; j < r.size(); j++) {
                            if (r.get(j).equals(wanted.toString())) {
                                if ((left || right) && !productiveSymbols.contains(wanted.toString())) {
                                    left = right = false;
                                    break;
                                }
                                if (j < r.size()-1)
                                    right = true;
                                if (j > 0)
                                    left = true;
                            } else if (!productiveSymbols.contains(r.get(j)) && !cfg.getTerminals().contains(r.get(j))) {
                                left = right = false;
                                 break;
                            }
                        }
                        if (left || right) {
                            if (n.equals(nonterminal) && (left || wanted.left()) && (right || wanted.right()))
                                return true;
                            boolean founded = false;
                            for (int k = 0; k < visited.size(); k++) {
                                if (visited.get(k).toString().equals(n)) {
                                    founded = true;
                                    if (visited.get(k).update(left, right))
                                        i = k-1;
                                    break;
                                }
                            }
                            if (!founded)
                                visited.add(new EmbeddedNonterminal(n, left || wanted.left(), right || wanted.right()));
                        }
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Cocke-Younger-Kasami algorithm finds out whether the word is generated
     * by given context free grammar or not
     * 
     * @param cfg  given context-free grammar
     * @param word given word
     * @return     List of Lists of sets representing table T
     */
    public List<List<Set<String>>> cyk(ContextFreeGrammar originalCfg, String word) {
        
        List<List<Set<String>>> t = new ArrayList<List<Set<String>>>();
        
        ContextFreeGrammar cfg;
        try {
            cfg = new Transformations().transformToCNF(originalCfg);
        } catch (TransformationException ex) {
            return t;
        }
        
        if (word == null || word.isEmpty())
            return t;
        
        if (word.equals("\\e") && cfg.getRules().get(cfg.getInitialNonTerminal()).contains("")) {
            t.add(new ArrayList<Set<String>>());
            t.get(0).add(new HashSet<String>());
            t.get(0).get(0).add(cfg.getInitialNonTerminal());
            return t;
        }
        
        int n = word.length();
        
        for (int i = 0; i < n; i++) {
            t.add(new ArrayList<Set<String>>());
            t.get(i).add(new HashSet<String>());
            for (String nonterminal : cfg.getRules().keySet()) {
                for (String rule : cfg.getRules().get(nonterminal)) {
                    if (rule.equals(word.substring(i, i+1)))
                        t.get(i).get(0).add(nonterminal);
                }
            }
        }
        
        for (int j = 1; j < n; j++) {
            for (int i = 0; i < n - j; i++) {
                t.get(i).add(new HashSet<String>());
                for (int k = 0; k < j; k++) {
                    for (String nonterminal : cfg.getRulesArray().keySet()) {
                        for (List<String> rule : cfg.getRulesArray().get(nonterminal)) {
                            if (t.get(i).get(k).contains(rule.get(0)) && t.get(i+k+1).get(j-k-1).contains(rule.get(1)))
                                t.get(i).get(j).add(nonterminal);
                        }
                    }
                }
            }
        }
        
        return t;
    }

}    

