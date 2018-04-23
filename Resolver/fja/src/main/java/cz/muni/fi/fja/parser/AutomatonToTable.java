/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.fja.parser;

import cz.muni.fi.RegularLanguage.Automaton.DeterministicFA;
import cz.muni.fi.RegularLanguage.Automaton.State;
import cz.muni.fi.RegularLanguage.Exceptions.RegLanguageException;
import cz.muni.fi.xpastirc.fja.parsers.AutomatonDefinition;
import cz.muni.fi.xpastirc.fja.parsers.DFA;
import cz.muni.fi.xpastirc.fja.servlet.HTMLEscaper;
import cz.muni.fi.xpastirc.parsers.DefinitionToAutomaton;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.runtime.RecognitionException;


/**
 *
 * @author Adrian Elgyutt
 */
public class AutomatonToTable {
    
    private final String automaton;
    
    public AutomatonToTable(String automaton){
        this.automaton = automaton;
    }
    
    private String transform(){
        StringBuilder sb = new StringBuilder();
        try {
            AutomatonDefinition d = DFA.match(automaton);
            DeterministicFA fa = new DefinitionToAutomaton(d).toDFA();
            sb.append("<table class='table table-bordered table-min no-margin-bottom'><thead><tr><th></th>");
            for(Character c:fa.getAlphabet()){
                sb.append("<th>").append(HTMLEscaper.escapeHTML(c.toString())).append("</td>");
            }
            sb.append("</tr></thead>");
            TreeMap<String, State> sor = new TreeMap<String, State>(fa.getAllStates());
            for(Map.Entry<String,State> e:sor.entrySet()){
                sb.append("<tr><td class=\"text-right\">");
                if(fa.getStartingState().equals(e.getValue()) && e.getValue().isAccepting()) sb.append("<span>&#8596;</span>&nbsp;");
                else if (e.getValue().isAccepting()) sb.append("<span>&#8592;</span>&nbsp;");
                else if (fa.getStartingState().equals(e.getValue())) sb.append("<span>&#8594;</span>&nbsp;");
                sb.append(HTMLEscaper.escapeHTML(e.getKey())).append("</td>");
                for(Character c:fa.getAlphabet()){
                    sb.append("<td>");
                    try{
                        sb.append(HTMLEscaper.escapeHTML(fa.getResult(e.getValue(), c).toString()));
                    }
                    catch(NullPointerException ex){
                        sb.append("");
                    }
                    sb.append("</td>");
                }
                sb.append("</tr>");
            }
            sb.append("</table>");
        } catch (Exception ex) {
            return "Nemohu převést automat do tabulky.";
        }
        return sb.toString();
    }
    
    @Override
    public String toString(){
        return transform();
    }
}
