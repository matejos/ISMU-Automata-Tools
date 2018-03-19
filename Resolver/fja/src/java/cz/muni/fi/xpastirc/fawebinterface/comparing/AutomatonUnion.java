/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.xpastirc.fawebinterface.comparing;

import cz.muni.fi.RegularLanguage.Automaton.DeterministicFA;
import cz.muni.fi.RegularLanguage.Automaton.NondeterministicFA;
import cz.muni.fi.RegularLanguage.Automaton.State;
import cz.muni.fi.RegularLanguage.Automaton.Transitions.DeterministicTransition;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author Tomas Pastircak - 324693@mail.muni.cz
 * @version 10.5.2011
 */
public class AutomatonUnion {
    DeterministicFA a1,a2;
    public AutomatonUnion(DeterministicFA a1, DeterministicFA a2){
        this.a1=a1;
        this.a2=a2;
    }

    public NondeterministicFA AutomatonUnion(){
        NondeterministicFA toReturn = new NondeterministicFA(getAlphabet());
        //takto budou jmena unikatni
        for (String state : a1.getStates()){
            toReturn.addState(state+"_1");
        }
        for (String state : a2.getStates()){
            toReturn.addState(state+"_2");
        }
        toReturn.setStarting(a1.getStartingState().getName()+"_1");
        HashMap<State,HashSet<DeterministicTransition>> transitions = a1.getTransitions().getTransitions();
        for (Map.Entry<State,HashSet<DeterministicTransition>> s : transitions.entrySet()){
            for (DeterministicTransition t : s.getValue())
                toReturn.addTransition(t.getFrom().getName()+"_1", t.getResult().getName()+"_1", t.getEdge());
        }
        HashMap<State,HashSet<DeterministicTransition>> transitions2 = a2.getTransitions().getTransitions();
        for (Map.Entry<State,HashSet<DeterministicTransition>> s : transitions2.entrySet()){
            for (DeterministicTransition t : s.getValue())
                if (t.getFrom().equals(a2.getStartingState()))
                    toReturn.addTransition(toReturn.getStartingState().getName(), t.getResult().getName()+"_2", t.getEdge());
                else
                    toReturn.addTransition(t.getFrom().getName()+"_2", t.getResult().getName()+"_2", t.getEdge());
        }
        for (State state : a1.getAllStates().values())
            if (state.isAccepting())
                toReturn.setAccepting(state.getName()+"_1", true);
        for (State state : a2.getAllStates().values())
            if (state.isAccepting()){
                if (state.equals(a2.getStartingState()))
                    toReturn.setAccepting(toReturn.getStartingState().getName(), true);
                else
                    toReturn.setAccepting(state.getName()+"_2", true);
            }
        return toReturn;
    }

    private TreeSet<Character> getAlphabet(){
        TreeSet<Character> al1 = a1.getAlphabet();
        TreeSet<Character> al2 = a2.getAlphabet();
        al1.addAll(al2);
        return al1;
    }


}
