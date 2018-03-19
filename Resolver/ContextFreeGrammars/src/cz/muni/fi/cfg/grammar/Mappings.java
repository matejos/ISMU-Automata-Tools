/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.cfg.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author NICKT
 */
public class Mappings {

    private String name;
    private ContextFreeGrammar cfg;
    private Map<String, String> sureMappings;
    private Map<String, Integer> ownRules = new HashMap<String, Integer>();
    private Map<String, Map<Set<String>, Integer>> appereanceRules = new HashMap<String, Map<Set<String>, Integer>>();
    private boolean hasQuestionMarkInOwnRules = false;

    public Mappings(String name, ContextFreeGrammar cfg, Map<String, String> sureMappings) {
        this.name = name;
        this.cfg = cfg;
        this.sureMappings = sureMappings;

        //výroba mapovani
        //1)vlastni pravidla neterminalu
        if (cfg.getRulesArray().isEmpty()) {
            cfg.mapToArrayMap();
        }
        if (cfg.getRulesArray().containsKey(name)) {
            for (List<String> listRules : cfg.getRulesArray().get(name)) {
                String updatedRule = "";
                for (String symbol : listRules) {
                    if (cfg.getTerminals().contains(symbol) || symbol.equals("")) {
                        updatedRule += symbol;
                    } else if (sureMappings.containsKey(symbol)) {
                        updatedRule += sureMappings.get(symbol);
                    } else if (symbol.equals(name)) {
                        updatedRule += "#";
                    } else {
                        updatedRule += "?";
                        hasQuestionMarkInOwnRules = true;
                    }
                }
                if (ownRules.containsKey(updatedRule)) {
                    ownRules.put(updatedRule,ownRules.get(updatedRule) + 1);
                } else {
                    ownRules.put(updatedRule, 1);
                }
            }
        }
        //2)pravidla ve kterych se vyskytuje
        for (Map.Entry<String, Set<List<String>>> entry : cfg.getRulesArray().entrySet()) {
            if (!entry.getKey().equals(name)) {
                Set<String> setRules = new HashSet<String>();
                for (List<String> rule : entry.getValue()) {
                    if (rule.contains(name)) {
                        String updatedRule = "";
                        for (String symbol : rule) {
                            if (cfg.getTerminals().contains(symbol) || symbol.equals("")) {
                                updatedRule += symbol;
                            } else if (sureMappings.containsKey(symbol)) {
                                updatedRule += sureMappings.get(symbol);
                            } else if (symbol.equals(name)) {
                                updatedRule += "#";
                            } else if (symbol.equals(entry.getKey())) {
                                updatedRule += "!";
                            } else {
                                updatedRule += "?";
                            }
                        }
                        setRules.add(updatedRule);
                    }
                }
                if (!setRules.isEmpty()) {
                    if (sureMappings.containsKey(entry.getKey())) {
                        if (appereanceRules.containsKey(sureMappings.get(entry.getKey()))) {
                            Map<Set<String>, Integer> value = new HashMap<Set<String>, Integer>();
                            value.putAll(appereanceRules.get(sureMappings.get(entry.getKey())));
                            if (value.containsKey(setRules)) {
                                value.put(setRules, value.get(setRules) + 1);
                                appereanceRules.put(sureMappings.get(entry.getKey()), value);
                            } else {
                                value.put(setRules, 1);
                                appereanceRules.put(sureMappings.get(entry.getKey()), value);
                            }
                        } else {
                            Map<Set<String>, Integer> value = new HashMap<Set<String>, Integer>();
                            value.put(setRules, 1);
                            appereanceRules.put(sureMappings.get(entry.getKey()), value);
                        }
                    } else {
                        if (appereanceRules.containsKey("!")) {
                            Map<Set<String>, Integer> value = new HashMap<Set<String>, Integer>();
                            value.putAll(appereanceRules.get("!"));
                            if (value.containsKey(setRules)) {
                                value.put(setRules, value.get(setRules) + 1);
                                appereanceRules.put("!", value);
                            } else {
                                value.put(setRules, 1);
                                appereanceRules.put("!", value);
                            }
                        } else {
                            Map<Set<String>, Integer> value = new HashMap<Set<String>, Integer>();
                            value.put(setRules, 1);
                            appereanceRules.put("!", value);
                        }
                    }
                } 
            }
        }
    }

    public String getName() {
        return name;
    }

    public Map<String, Integer> getOwnRules() {
        return ownRules;
    }

    public Map<String, Map<Set<String>, Integer>> getAppereanceRules() {
        return appereanceRules;
    }

    public boolean hasQuestionMarkInOwnRules() {
        return hasQuestionMarkInOwnRules;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Mappings other = (Mappings) obj;
        if (this.ownRules != other.ownRules && (this.ownRules == null || !this.ownRules.equals(other.ownRules))) {
            return false;
        }
        if (this.appereanceRules != other.appereanceRules && (this.appereanceRules == null || !this.appereanceRules.equals(other.appereanceRules))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.ownRules != null ? this.ownRules.hashCode() : 0);
        hash = 29 * hash + (this.appereanceRules != null ? this.appereanceRules.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return ownRules.toString() + " + " + appereanceRules.toString();
    }
}