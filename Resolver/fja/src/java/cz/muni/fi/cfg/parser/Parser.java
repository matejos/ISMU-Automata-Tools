/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.cfg.parser;

import cz.muni.fi.cfg.grammar.ContextFreeGrammar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author NICKT
 */
public class Parser {

    public static List<String> orderingOfNonTerminals(String input) {
                        
        input = input.replaceAll("\\r\\n", ",");
        input = input.replaceAll("\\n", ",");
        input = input.replaceAll("\\r", ",");
        input = input.replaceAll("[,]+", ",");
        input = input.replaceAll(" ", "");
        
        List<String> order = new ArrayList<String>();
        Set<String> allSymbols = new HashSet<String>();
        
        if (input.equalsIgnoreCase("NAG")) {
            return order;
        }
        
        String[] commaSplit = input.split(",");
        for (int i = 0; i < commaSplit.length; i++) {
            String nonTerminal = commaSplit[i].split("->")[0];
            if (!allSymbols.contains(nonTerminal)) {
                order.add(nonTerminal);
                allSymbols.add(nonTerminal);
            }
        }
        return order;
    }

    public static ContextFreeGrammar parse(String input) throws ParserException {
        
        if (input.equalsIgnoreCase("NAG")) {
            return new ContextFreeGrammar(new HashSet<String>(), new HashMap<String, Set<String>>(), "");
        }
            
        String initialNonTerminal = null;
        Set<String> nonTerminals = new HashSet<String>();
        Set<String> terminals = new HashSet<String>();
        Map<String, Set<List<String>>> rulesArray = new HashMap<String, Set<List<String>>>();
        Map<String, Set<String>> rules = new HashMap<String, Set<String>>();
        input = input.replaceAll("\\r\\n", ",");
        input = input.replaceAll("\\n", ",");
        input = input.replaceAll("\\r", ",");
        input = input.replaceAll("[,]+", ",");
        input = input.replaceAll(" ", "");
        input = input.replaceAll("\\e", "\\\\e");
        if (input.isEmpty()) {
            throw new ParserException("Nebyla zadána žádná gramatika.");
        }
        while (input.startsWith(",")) {
            input = input.replaceFirst(",", "");
        }
        String[] arrayRules = input.split(",");

        boolean wasType0 = false;
        boolean wasType1 = false;

        for (int i = 0; i < arrayRules.length; i++) {
            String actualRule = arrayRules[i].trim();
            if (actualRule.split("->").length != 2) {
                throw new ParserException("Chyba syntaxe: Nebyla uvedena šipka (->), popř. chybí pravá strana pravidla, nebo chybí čárka mezi pravidly.");
            } else if (actualRule.split("->").length == 2 && actualRule.split("->")[0].equals("")) {
                throw new ParserException("Chyba syntaxe: Chybí levá strana pravidla.");
            }
            String leftSide = actualRule.split("->")[0];
            if (leftSide.contains("|")) {
                throw new ParserException("Levá strana obsahuje zakázaný symbol.");
            }
            String rightSide = actualRule.split("->")[1];
            if ((leftSide.replaceAll("\\\\e", "").length() == 1 && Character.isUpperCase(leftSide.charAt(0))) || (leftSide.split("<").length == 2) || (leftSide.contains("\'") && leftSide.split("'").length == 1)) {
                leftSide = leftSide.replaceAll("\\\\e", "");
                if (i == 0) {
                    initialNonTerminal = leftSide;
                }
                nonTerminals.add(leftSide);
                Set<String> helpSet = new HashSet<String>();
                Set<List<String>> helpListSet = new HashSet<List<String>>();
                String[] rightSideRule = rightSide.split("[|]");
                for (int j = 0; j < rightSideRule.length; j++) {
                    if (rightSideRule[j].equals("\\\\e")) {
                        helpSet.add("");
                        List<String> helpList = new ArrayList<String>();
                        helpList.add("");
                        helpListSet.add(helpList);
                    } else {
                        rightSideRule[j] = rightSideRule[j].replaceAll("\\\\e", "");
                        List<String> list = new ArrayList<String>();
                        StringBuilder symbol = new StringBuilder();
                        for (int k = 0; k < rightSideRule[j].length(); k++) {
                            Character ch = rightSideRule[j].charAt(k);
                            Character next = null;
                            if (k + 1 < rightSideRule[j].length()) {
                                next = rightSideRule[j].charAt(k + 1);
                            }
                            Character bracket = Character.valueOf('<');
                            Character apostrof = Character.valueOf('\'');

                            symbol.append(ch);
                            /*         if (next != null) {
                            if (next.equals(bracket) || (next.equals(apostrof) && !ch.equals(apostrof))) {
                            symbol.delete(0, symbol.length());
                            }
                            } */
                            if (ch.equals(apostrof) && (next == null || !next.equals(apostrof)) && symbol.length() > 0) { // pokud je tvaru A'
                                list.add(symbol.toString());
                                nonTerminals.add(symbol.toString());
                                symbol.delete(0, symbol.length());
                            }
                            if ((next == null || !next.equals(apostrof)) && !symbol.toString().startsWith("<")) { //pokud je neterminál
                                if (Character.isUpperCase(ch)) {
                                    list.add(ch.toString());
                                    nonTerminals.add(ch.toString());
                                    symbol.delete(0, symbol.length()); //??? ano ???
                                } else if (!ch.equals(apostrof) && (next == null || !next.equals(apostrof))) { //pokud je terminál
                                    list.add(ch.toString());
                                    terminals.add(ch.toString());
                                    symbol.delete(0, symbol.length()); //??? ano ???
                                }
                            }

                            if (symbol.toString().startsWith("<") && symbol.toString().endsWith(">")) { //pokud je neterminál tvaru <něco>
                                list.add(symbol.toString());
                                nonTerminals.add(symbol.toString());
                                symbol.delete(0, symbol.length());
                            }
                            if ((symbol.toString().startsWith("<") && k == rightSideRule[j].length() - 1 && !symbol.toString().endsWith(">")) || (!symbol.toString().startsWith("<") && k == rightSideRule[j].length() - 1 && symbol.toString().endsWith(">"))) {
                                throw new ParserException("Chyba syntaxe: Neterminál v závorkách není správně uzavřen.");
                            }

                        }
                        helpSet.add(rightSideRule[j]);
                        helpListSet.add(list);
                    }
                }
                if (helpSet.isEmpty()) {
                    throw new ParserException("Chyba syntaxe: Chybí pravá strana pravidla.");
                }
                if (rules.containsKey(leftSide)) {
                    helpSet.addAll(rules.get(leftSide));
                }
                rules.put(leftSide, helpSet);
                if (rulesArray.containsKey(leftSide)) {
                    helpListSet.addAll(rulesArray.get(leftSide));
                }
                rulesArray.put(leftSide, helpListSet);
            } else if (leftSide.length() == 1 && !Character.isUpperCase(leftSide.charAt(0))) {
                throw new ParserException("Chyba syntaxe: Levá strana pravidla musí obsahovat alespoň jeden neterminál.");
            } else {//pokud nebyla bezkontextová

                boolean hasAtleastOneNonTerminal = false;
                List<String> leftHelpList = new ArrayList<String>();
                leftSide = leftSide.replaceAll("\\\\e", "");
                StringBuilder symbol = new StringBuilder();
                for (int k = 0; k < leftSide.length(); k++) {
                    Character ch = leftSide.charAt(k);
                    Character next = null;
                    if (k + 1 < leftSide.length()) {
                        next = leftSide.charAt(k + 1);
                    }
                    Character bracket = Character.valueOf('<');
                    Character apostrof = Character.valueOf('\'');

                    symbol.append(ch);
                    if (ch.equals(apostrof) && (next == null || !next.equals(apostrof)) && symbol.length() > 0) { // pokud je tvaru A'
                        leftHelpList.add(symbol.toString());
                        hasAtleastOneNonTerminal = true;
                        symbol.delete(0, symbol.length());
                    }
                    if ((next == null || !next.equals(apostrof)) && !symbol.toString().startsWith("<")) { //pokud je neterminál
                        if (Character.isUpperCase(ch)) {
                            hasAtleastOneNonTerminal = true;
                            leftHelpList.add(ch.toString());
                            symbol.delete(0, symbol.length()); //??? ano ???
                        } else if (!ch.equals(apostrof) && (next == null || !next.equals(apostrof))) {                                        //pokud je terminál
                            leftHelpList.add(ch.toString());
                            symbol.delete(0, symbol.length()); //??? ano ???
                        }
                    }
                    if (symbol.toString().startsWith("<") && symbol.toString().endsWith(">")) { //pokud je neterminál tvaru <něco>
                        leftHelpList.add(symbol.toString());
                        hasAtleastOneNonTerminal = true;
                        symbol.delete(0, symbol.length());
                    }
                    if ((symbol.toString().startsWith("<") && k == leftSide.length() - 1 && !symbol.toString().endsWith(">")) || (!symbol.toString().startsWith("<") && k == leftSide.length() - 1 && symbol.toString().endsWith(">"))) {
                        throw new ParserException("Chyba syntaxe: Neterminál v závorkách není správně uzavřen.");
                    }
                }
                if (!hasAtleastOneNonTerminal) {
                    throw new ParserException("Chyba syntaxe: Levá strana pravidla musí obsahovat alespoň jeden neterminál.");
                }

                if (leftHelpList.isEmpty()) {
                    throw new ParserException("Chyba syntaxe: Chybí levá strana pravidla.");
                }
                //levou stranu pravidla máme zpracovanou


                String[] rightSideRule = rightSide.split("[|]");
                for (int j = 0; j < rightSideRule.length; j++) {
                    List<String> rightHelpList = new ArrayList<String>();
                    if (rightSideRule[j].equals("\\\\e")) {
                        rightHelpList.add("");
                    } else {
                        rightSideRule[j].replaceAll("\\\\e", "");
                        StringBuilder symbol2 = new StringBuilder();
                        for (int k = 0; k < rightSideRule[j].length(); k++) {
                            Character ch = rightSideRule[j].charAt(k);
                            Character next = null;
                            if (k + 1 < rightSideRule[j].length()) {
                                next = rightSideRule[j].charAt(k + 1);
                            }
                            Character bracket = Character.valueOf('<');
                            Character apostrof = Character.valueOf('\'');

                            symbol2.append(ch);
                            if (ch.equals(apostrof) && (next == null || !next.equals(apostrof)) && symbol2.length() > 0) { // pokud je tvaru A'
                                rightHelpList.add(symbol2.toString());
                                symbol2.delete(0, symbol2.length());
                            }
                            if ((next == null || !next.equals(apostrof)) && !symbol2.toString().startsWith("<")) { //pokud je neterminál
                                if (Character.isUpperCase(ch)) {
                                    rightHelpList.add(ch.toString());
                                    symbol2.delete(0, symbol.length()); //??? ano ???
                                } else {                                        //pokud je terminál
                                    rightHelpList.add(ch.toString());
                                    symbol2.delete(0, symbol.length()); //??? ano ???
                                }
                            }
                            if (symbol2.toString().startsWith("<") && symbol2.toString().endsWith(">")) { //pokud je neterminál tvaru <něco>
                                rightHelpList.add(symbol2.toString());
                                symbol2.delete(0, symbol2.length());
                            }
                            if ((symbol2.toString().startsWith("<") && k == rightSideRule[j].length() - 1 && !symbol2.toString().endsWith(">")) || (!symbol2.toString().startsWith("<") && k == rightSideRule[j].length() - 1 && symbol2.toString().endsWith(">"))) {
                                throw new ParserException("Chyba syntaxe: Neterminál v závorkách není správně uzavřen.");
                            }
                        }
                    }
                    if (rightHelpList.isEmpty()) {
                        throw new ParserException("Chyba syntaxe: Chybí pravá strana pravidla.");
                    }
                    //porovnáme velikosti pravidel
                    if (leftHelpList.size() <= rightHelpList.size()) {
                        wasType1 = true;
                    } else {
                        wasType0 = true;
                    }
                }
            }
        }
        if (wasType0) {
            throw new ParserException("Gramatika byla typu 0.");
        }
        if (wasType1) {
            throw new ParserException("Gramatika byla typu 1.");
        }
        return new ContextFreeGrammar(nonTerminals, terminals, rules, rulesArray, initialNonTerminal);
    }

    public static String[] parseISString(String isString) throws ParserException {
        String transformationType = null;
        String teacherData = null;
        if (isString.length() >= 8 && isString.charAt(3) == '-' &&
                isString.charAt(7) == ':') {
            transformationType = isString.substring(4, 7);
            teacherData = isString.substring(8);
        } else {
            throw new ParserException("Neplatný řetězec ISu");
        }
        String[] returnArray = {transformationType, teacherData};
        return returnArray;
    }
}
