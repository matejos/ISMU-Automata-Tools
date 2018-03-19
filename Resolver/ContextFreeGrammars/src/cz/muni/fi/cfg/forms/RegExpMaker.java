package cz.muni.fi.cfg.forms;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author NICKT
 */
public class RegExpMaker {

    /**
     *
     * @param set set of terminal symbols ... E
     * @return E*
     */
    public String regexOnlyTerminals(Set<String> set) {
        if (set.isEmpty()) {
            return "";
        } else {
            StringBuilder outputString = new StringBuilder();
            for (String s : set) {
                outputString.append(s);
            }
            return "[" + outputString.toString() + "]*";
        }
    }

    public String regexPositiveIterateThese(Set<String> set) {
        if (set.isEmpty()) {
            return "";
        } else {
            StringBuilder outputString = new StringBuilder();
            outputString.append("(()");
            for (String s : set) {
                outputString.append("|").append(s);
            }
            outputString.append(")*");
            return outputString.toString();
        }
    }

    /**
     *
     * @param terminals set of terminal symbols ... E
     * @param ne set of nonterminal symbols, which are generating a word ... Ne
     * @return (Ne U E)*
     */
    public String regexNeUnionAlphabeth(Set<String> ne, Set<String> terminals) {
        StringBuilder outputString = new StringBuilder();
        Set<String> all = new HashSet<String>();
        all.addAll(ne);
        all.addAll(terminals);
        outputString.append("("); // puvodne [ (Daniel Pelisek)
        for (String s : all) {
            outputString.append("|").append(s);
        }
        outputString.append(")*"); // puvodne ] (Daniel Pelisek)
        return outputString.toString();
    }
    
    public String regexNonterminalInRule(String nonTerminal, Set<String> nonTerminals, Set<String> terminals) {
        return regexNeUnionAlphabeth(nonTerminals, terminals) + nonTerminal + regexNeUnionAlphabeth(nonTerminals, terminals);
    }
}
