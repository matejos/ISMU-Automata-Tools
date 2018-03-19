/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.xpastirc.fawebinterface.comparing;

import cz.muni.fi.RegularLanguage.Automaton.DeterministicFA;
import cz.muni.fi.RegularLanguage.Automaton.NondeterministicFA;
import cz.muni.fi.RegularLanguage.Automaton.State;
import cz.muni.fi.RegularLanguage.Automaton.Transitions.SymbolTransition;
import cz.muni.fi.RegularLanguage.Exceptions.RegLanguageException;
import cz.muni.fi.fja.parser.AutomatonToTable;
import cz.muni.fi.xpastirc.fja.parsers.AutomatonDefinition;
import cz.muni.fi.xpastirc.fja.parsers.DFA;
import cz.muni.fi.xpastirc.fja.parsers.Grammar;
import cz.muni.fi.xpastirc.fja.parsers.NFA;
import cz.muni.fi.xpastirc.fja.parsers.RE;
import cz.muni.fi.xpastirc.fja.servlet.HTMLEscaper;
import cz.muni.fi.xpastirc.parsers.DefinitionToAutomaton;
import cz.muni.fi.xpastirc.parsers.GrammarParser;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.runtime.RecognitionException;

/**
 * Class that compares two deterministic
 *
 * @author fafner
 */
public class ComplexLanguageInformation implements LanguageInformation {

    public DeterministicFA automaton;

    private List<String> words;
    private boolean isFinal;
    private boolean finalityIsSet = false;
    private int isEmpty;
    private boolean emptinessIsSet = false;
    private String requestedFormalism;
    private DeterministicFA copyAut;
    private static int epscount;
    
    
    public ComplexLanguageInformation(DeterministicFA automaton){
        this.automaton = automaton;
        try {
            copyAut = new DeterministicFA(automaton.toString());
            copyAut.removeIrelevantStates();
        } catch (RegLanguageException ex) {
            copyAut = automaton;
        }
    }
    
    public ComplexLanguageInformation(ComplexLanguageInformation c){
        this.automaton = c.automaton;
        this.words = c.words;
        this.isFinal = c.isFinal;
        this.finalityIsSet = c.finalityIsSet;
        this.isEmpty = c.isEmpty;
        this.emptinessIsSet = c.emptinessIsSet;
        this.requestedFormalism = c.requestedFormalism;
        this.copyAut = c.copyAut;
    }

    public static ComplexLanguageInformation getLanguageInformation(String formalism, String input) throws RegLanguageException, ENFAException{
      try{
        if (formalism.equals("DFA")){
            AutomatonDefinition d = DFA.match(input);
            DeterministicFA fa = new DefinitionToAutomaton(d).toDFA();
            return new ComplexLanguageInformation(fa);
        }
        if (formalism.equals ("MIN")){
            AutomatonDefinition d = DFA.match(input);
            DeterministicFA fa = new DefinitionToAutomaton(d).toDFA();
            return new ComplexLanguageInformation(fa);        
        }
        if (formalism.equals("MIC")){
            AutomatonDefinition d = DFA.match(input);
            DeterministicFA fa = new DefinitionToAutomaton(d).toDFA();
            return new ComplexLanguageInformation(fa);        
        }
        if (formalism.equals("TOT")){
            AutomatonDefinition d = DFA.match(input);
            DeterministicFA fa = new DefinitionToAutomaton(d).toDFA();
            return new ComplexLanguageInformation(fa);
        }
        try{
        if (formalism.equals("NFA")){
            return new ComplexLanguageInformation(new DefinitionToAutomaton(NFA.match(input)).toNFA().makeDeterministick());
        }}
        catch(Exception e){
            throw new ENFAException(e);
        }
        if (formalism.equals("EFA")){
            DefinitionToAutomaton nefa = new DefinitionToAutomaton(NFA.match(input));
            nefa.toEFA().makeDeterministick();
            epscount = nefa.getEpsc();
            return new ComplexLanguageInformation(new DefinitionToAutomaton(NFA.match(input)).toEFA().makeDeterministick());
        }
        if (formalism.equals("GRA"))
            return new ComplexLanguageInformation(new DefinitionToAutomaton(new GrammarParser(input).match(true)).toEFA().makeDeterministick());
        if (formalism.equals("REG"))
            return new ComplexLanguageInformation(new DefinitionToAutomaton(RE.match(input)).toEFA().makeDeterministick());
      } catch (ENFAException e){
          throw new ENFAException(e);
      } catch (RecognitionException e){
          throw new RegLanguageException("Chyba parsování. ");//: " + e.getClass().getName() + ":" + e.getMessage());
      } catch (Exception e){
          throw new RegLanguageException("Chyba parsování. ");// + e.getMessage());
      }

       //zkusit zjistit co je zac vstup - bud zadano ALL nebo nezadano
       //priority -- automaty, gramatika, reg. vyraz, chyba
        try {
            return new ComplexLanguageInformation(new DefinitionToAutomaton(NFA.match(input)).toEFA().makeDeterministick());
        } catch (Exception ex) {
        }
        try {
            return new ComplexLanguageInformation(new DefinitionToAutomaton(DFA.match(input)).toDFA());
        } catch (Exception ex) {
        }
        try {
            return new ComplexLanguageInformation(new DefinitionToAutomaton(new GrammarParser(input).match(true)).toEFA().makeDeterministick());
        } catch (Exception ex) {
        }
        try {
            return new ComplexLanguageInformation(new DefinitionToAutomaton(RE.match(input)).toEFA().makeDeterministick());
        } catch (Exception ex) {
            throw new RegLanguageException("Špatný vstup!");
        }
    }
    
    @Override
    public int getEpscount(){
        return epscount;
    }
    @Override
    public int isEmpty() {
        if (!emptinessIsSet){
            isEmpty= (automaton.isEmptyLanguage()?1:
                automaton.makeComplemet().isEmptyLanguage()?-1:0);
        }
        return isEmpty;
    }

    @Override
    public boolean isFinal() {
        if (!finalityIsSet){
            isFinal = getFinality();
            finalityIsSet=true;
        }
        return isFinal;
    }

    private boolean getFinality(){
            ArrayList<State> list = new ArrayList<State>();
            list.add(copyAut.getStartingState());
            return isFinalNode(copyAut.getStartingState(), list);
    }
    
    /**
     * Je mozno pouzit pouze zjednoduseny algoritmus pro detekci cyklu, z automatu
     * jsme drive odstranili vsechny stavy, ze kterych se neda dosahnout konecny stav.
     */
    private boolean isFinalNode(State state, List<State> reachedStates){
        for (State nextState : copyAut.getTransitions().reachableStates(state)){
            if (reachedStates.contains(nextState))
                    return false;
            else{
                reachedStates.add(nextState);
                if (!isFinalNode(nextState, new ArrayList<State>(reachedStates)))
                    return false;
            }
        }
        return true;
    }

    @Override
    public List<String> getWords() {
        if (words == null && (isEmpty())!=1)
            words = getNewWords();
        return words;
    }

    private List<String> getNewWords(){
        ArrayList<State> reachedStates = new ArrayList<State>();
        ArrayList<State> finalStates = new ArrayList<State>();
        ArrayList<String> toReturn = new ArrayList<String>();
        HashMap<String,State> reachedNow = new HashMap<String,State>();
        HashMap<String,State> reachedPrevious = new HashMap<String,State>();
        reachedNow.put("",copyAut.getStartingState());
        while(!reachedNow.isEmpty()){
            //reachedNow obsahuje slova po n krocich
            //pro konecne automaty vsechna slova,
            //pro nekonecne jedno nejkratsi slovo na stav.
            for (Entry<String,State> entry : reachedNow.entrySet()){
                    if (entry.getValue().isAccepting())
                        toReturn.add(entry.getKey());
            }
            //toReturn obsahuje vsechna slova prijata po n krocich
            if (!isFinal() && (!toReturn.isEmpty()))
                    return toReturn;
            if (!isFinal())
                reachedStates.addAll(reachedNow.values());
            //reachedStates obsahuje vsechny dosazene stavy
            reachedPrevious.clear();
            reachedPrevious.putAll(reachedNow);
            reachedNow.clear();
            for (Entry<String,State> state : reachedPrevious.entrySet()){
                 for (SymbolTransition transition : copyAut.getTransitions().transitionsFrom(state.getValue())){
                     if (!reachedStates.contains(transition.getResult()))
                        reachedNow.put(state.getKey() + transition.getEdge(),transition.getResult());
                }
            }
            //konci iterace, jsme ve stavech po dalsim kroku
            //v konecnych automatech ve vsech stavech
            //v nekonecnych ve vsech nove dosazenych stavech
        }
        return toReturn;
    }

    @Override
    public boolean isEqual(LanguageInformation other) {
        return new SimpleLanguageInformation(toNFA()).isEqual(new SimpleLanguageInformation(other.toNFA()));    
    }

    @Override
    public int includes(LanguageInformation other) {
        return (aNotB(other).isEmpty()==1?1:
               (BNotA(other).isEmpty()==1?-1:0));
    }

    @Override
    public LanguageInformation aNotB(LanguageInformation other) {
        return intersection(other.complement());
    }

    @Override
    public LanguageInformation BNotA(LanguageInformation other) {
        return complement().intersection(other);
    }

    @Override
    public LanguageInformation intersection(LanguageInformation other) {
        return new ComplexLanguageInformation(new AutomatonIntersection(automaton, other.toDFA()).intersection());
    }

    @Override
    public DeterministicFA toDFA() {
        return automaton;
    }

    @Override
    public NondeterministicFA toNFA(){
        return automaton.makeNondeterministicAutomata();
    }

    @Override
    public LanguageInformation complement() {
        return new ComplexLanguageInformation(automaton.makeComplemet());
    }

    @Override
    public LanguageInformation union(LanguageInformation other) {
        //return new ComplexLanguageInformation(new AutomatonIntersection(toDFA().makeComplemet(),other.toDFA().makeComplemet()).intersection());
        return new ComplexLanguageInformation(new AutomatonUnion(toDFA(),other.toDFA()).AutomatonUnion().makeDeterministick());
    }

    @Override
    public void printInformation(PrintWriter out, boolean verbose, int pos){
                out.println("<td>" + getCharacteristics() +"</td><td>");
            if (isEmpty()!=1){
                int i=0;
                for (String word : getWords()){
                    out.print((i==0?"<b>":",<b>") + (word.equals("")?"\u025b":word) + "</b>" );
                    i++;
                }
            }
            out.println("</td>");
            out.println("<td class=\"automaton\"><input type='button' class=\"button\" id='hideshow"+pos+"' value=\"Zobraz/skry automat\"><div id='aut"+pos+"' style=\"display: none;\">");
            if(verbose) out.println(new AutomatonToTable(automaton.toString()).toString());
            else out.println(HTMLEscaper.escapeHTML(automaton.toString()));
            out.println("</div></td>");
            
    }

    @Override
    public String getCharacteristics(){
        return (isEmpty()==1?"prázdný":isEmpty()==-1?"úplný":(isFinal()?"konečný":"nekonečný"));
    }

}
