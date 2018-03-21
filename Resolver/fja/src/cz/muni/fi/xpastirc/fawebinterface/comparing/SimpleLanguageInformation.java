/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.xpastirc.fawebinterface.comparing;

import cz.muni.fi.RegularLanguage.Automaton.DeterministicFA;
import cz.muni.fi.RegularLanguage.Automaton.NondeterministicFA;
import cz.muni.fi.RegularLanguage.Automaton.State;
import cz.muni.fi.RegularLanguage.Automaton.StateSet;
import cz.muni.fi.RegularLanguage.Exceptions.RegLanguageException;
import cz.muni.fi.xpastirc.parsers.GrammarParser;
import cz.muni.fi.xpastirc.fja.parsers.RE;
import cz.muni.fi.xpastirc.fja.parsers.DFA;
import cz.muni.fi.xpastirc.fja.parsers.Grammar;
import cz.muni.fi.xpastirc.fja.parsers.NFA;
import cz.muni.fi.xpastirc.parsers.DefinitionToAutomaton;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import org.antlr.runtime.RecognitionException;

/**
 * @author Tomas Pastircak - 324693@mail.muni.cz
 * @version 17.4.2011
 */
public class SimpleLanguageInformation implements LanguageInformation{

    public static LanguageInformation getLanguageInformation(String formalism, String input) throws RegLanguageException{
      try{
        if (formalism.equals("DFA") || formalism.equals ("MIN") || formalism.equals("MIC")
         || formalism.equals("TOT")){
            DeterministicFA fa = new DefinitionToAutomaton(DFA.match(input)).toDFA();
            if (!AutomatonFormalismChecker.isInFormalism(formalism, fa, 0))
                   throw new RegLanguageException("Automat nesplňuje zadaný formalismus");
            return new SimpleLanguageInformation(fa.makeNondeterministicAutomata());
          }
        if (formalism.equals("NFA"))
            return new SimpleLanguageInformation(new DefinitionToAutomaton(NFA.match(input)).toEFA().makeNondeterministicAutomata());
        if (formalism.equals("EFA"))
            return new SimpleLanguageInformation(new DefinitionToAutomaton(NFA.match(input)).toEFA().makeNondeterministicAutomata());
        if (formalism.equals("GRA"))
            return new SimpleLanguageInformation(new DefinitionToAutomaton(new GrammarParser(input).match(true)).toEFA().makeNondeterministicAutomata());
        if (formalism.equals("REG"))
            return new SimpleLanguageInformation(new DefinitionToAutomaton(RE.match(input)).toEFA().makeNondeterministicAutomata());
      } catch (RecognitionException e){
          throw new RegLanguageException("Chyba parsování: "+e.getMessage());
      }
       //zkusit zjistit co je zac vstup - bud zadano ALL nebo nezadano
        try {
            return new SimpleLanguageInformation(new DefinitionToAutomaton(NFA.match(input)).toEFA().makeNondeterministicAutomata());
        } catch (Exception ex) {
        }
        try {
            return new SimpleLanguageInformation(new DefinitionToAutomaton(DFA.match(input)).toDFA().makeNondeterministicAutomata());
        } catch (Exception ex) {
        }
        try {
            return new SimpleLanguageInformation(new DefinitionToAutomaton(new GrammarParser(input).match(true)).toEFA().makeNondeterministicAutomata());
        } catch (Exception ex) {
        }
        try {
            return new SimpleLanguageInformation(new DefinitionToAutomaton(RE.match(input)).toEFA().makeNondeterministicAutomata());
        } catch (Exception ex) {
            throw new RegLanguageException("Špatný vstup");
        }
    }

    private NondeterministicFA nfa;

    public SimpleLanguageInformation(NondeterministicFA nfa) {
        this.nfa=nfa;
    }

    public String getCharacteristics() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    private class NFAEquality{
        private class Pair{
            public Pair(StateSet value1, StateSet value2)
                {this.value1=value1;this.value2=value2;}
            public StateSet value1;
            public StateSet value2;
        }
        private List<List<StateSet>> sets;
        private Stack<Pair> stack;

        private NFAEquality(){
            sets = new ArrayList<List<StateSet>>();
            stack = new Stack<Pair>();
        }

        private List<StateSet> make(StateSet state){
            List toReturn = new ArrayList<StateSet>();
            toReturn.add(state);
            return toReturn;
        }

        private List<StateSet> find(StateSet state){
            for (List<StateSet> set : sets){
                if (set.contains(state)) return set;
            }
            List<StateSet> toReturn = make(state);
            sets.add(toReturn);
            return toReturn;
        }

        private List<StateSet> union(List<StateSet> state1,List<StateSet> state2){
            List<StateSet> toReturn = new ArrayList<StateSet>();
            toReturn.addAll(state1);
            toReturn.addAll(state2);
            sets.remove(state1);
            sets.remove(state2);
            sets.add(toReturn);
            return toReturn;
        }

        private boolean eps(StateSet set){
            return set.containsFinal();
        }
        private StateSet delta(NondeterministicFA automaton,StateSet state, Character a){
            StateSet reachable = new StateSet();
            for (State s : state.getStates())
                reachable.addStates(automaton.getTransitions().reacheableStates(s, a));
            return reachable;
        }
        public boolean getEquality(NondeterministicFA a2){
            nfa.removeIrelevantStates();
            a2.removeIrelevantStates();
            HashSet<String> names1 = new HashSet(nfa.getStates());
            HashSet<String> names2 = new HashSet(a2.getStates());
            for (String name : names1){
                nfa.renameState(name, name + "_1");
            }
            for (String name : names2){
                a2.renameState(name, name + "_2");
            }
            StateSet s1 = new StateSet(nfa.getStartingState());
            StateSet s2 = new StateSet(a2.getStartingState());
            union(make(s1),make(s2));
            stack.push(new Pair(s1,s2));
            while(!stack.isEmpty()){
                Pair p = stack.pop();
                if (eps(p.value1)!= eps(p.value2))
                    return false;
                for (Character a : nfa.getAlphabet()){
                    StateSet d1 = delta(nfa, p.value1,a);
                    StateSet d2 = delta(a2, p.value2,a);
                    List<StateSet> px = find(d1);
                    List<StateSet> qx = find(d2);
                    if ((px != null && qx == null) || (px == null && qx != null))
                        return false;
                    if (px != null && qx != null && !px.equals(qx)){
                        union(px,qx);
                        stack.push(new Pair(d1,d2));
                    }
                }
            }
            return true;
        }
    }

    public boolean isFinal() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int isEmpty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<String> getWords() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isEqual(LanguageInformation other) {
        NFAEquality eq = new NFAEquality();
        return eq.getEquality(other.toNFA());
    }

    public int includes(LanguageInformation other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public LanguageInformation aNotB(LanguageInformation other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public LanguageInformation BNotA(LanguageInformation other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public LanguageInformation intersection(LanguageInformation other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public LanguageInformation union(LanguageInformation other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public LanguageInformation complement() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isInLanguage(String word) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public DeterministicFA toDFA() {
        return nfa.makeDeterministick();
    }

    public NondeterministicFA toNFA() {
        return nfa;
    }

    public void printInformation(PrintWriter out, boolean verbose, int pos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public int getEpscount(){
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
