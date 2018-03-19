/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.xpastirc.parsers;

import cz.muni.fi.RegularLanguage.Automaton.DeterministicFA;
import cz.muni.fi.RegularLanguage.Automaton.ENondeterministicFA;
import cz.muni.fi.RegularLanguage.Automaton.NondeterministicFA;
import cz.muni.fi.RegularLanguage.Exceptions.RegLanguageException;
import cz.muni.fi.xpastirc.fja.parsers.AutomatonDefinition;
import cz.muni.fi.xpastirc.fja.parsers.Transition;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Tomas Pastircak - 324693@mail.muni.cz
 * @version 17.5.2011
 */
public class DefinitionToAutomaton {
    private AutomatonDefinition definition;
    private TreeSet<String> states;
    private int epsc;
    public DefinitionToAutomaton(AutomatonDefinition definition){
        this.definition=definition;
        states = new TreeSet<String>();
    }
    public int getEpsc(){
        return epsc;
    }
    public DeterministicFA toDFA() throws RegLanguageException{
            states();
            removeAllExtended();
            Set<String> fin = definition.getFinalStates();
            TreeMap<String,Character> alphabet = getAlphabet();
            //if (alphabet.isEmpty()) alphabet.put("\u03B5", '\u03B5');
            DeterministicFA toReturn = new DeterministicFA(new TreeSet(alphabet.values()));
            for (String state: states)
                toReturn.addState(state,fin.contains(state));
            toReturn.setStarting(definition.getStartingState());
            for (Transition transition : definition.getTransitions())
            {
                if (alphabet.get(transition.getEdge())==null
                        && transition.getFrom().equals(transition.getTo()) && toReturn.getStartingState().toString().equals(transition.getFrom())
                        && toReturn.getStartingState().isAccepting()){
                            return toReturn;
                };
                if (!toReturn.addTransition(transition.getFrom(), transition.getTo(), (alphabet.get(transition.getEdge()))))
                    throw new RegLanguageException("Zadaný automat není deterministický: Nemohu přidat přechod z "
                            + transition.getFrom() + " do "+ transition.getTo() + " s hranou "
                            + transition.getEdge() + ". ");
            }
            return toReturn;
        }
    public ENondeterministicFA toEFA() throws RegLanguageException{
            states();
            removeAllExtended();
            Set<String> fin = definition.getFinalStates();
            TreeMap<String,Character> alphabet = getAlphabet();
            //alphabet.put("\u03B5", '\u03B5');
            ENondeterministicFA toReturn = new ENondeterministicFA(new TreeSet(alphabet.values()));
            for (String state: states)
                toReturn.addState(state,fin.contains(state));
            //prazdny jazyk
            if (states.isEmpty() || definition.getStartingState()==null){
                toReturn.addState("A",false);
                toReturn.setStarting("A");
                return toReturn;
            }

            toReturn.setStarting(definition.getStartingState());
            epsc = 0;
            for (Transition transition : definition.getTransitions()){
                if (!toReturn.addTransition(transition.getFrom(), transition.getTo(), 
                    alphabet.get(transition.getEdge())==null?'\u03B5':alphabet.get(transition.getEdge()))
                        && (!transition.getFrom().equals(transition.getTo())))
                    throw new RegLanguageException("Zadaný automat není EFA: Nemohu přidat přechod z "
                            + transition.getFrom() + " do "+ transition.getTo() + " s hranou "
                            + transition.getEdge() + ". ");
                if(alphabet.get(transition.getEdge())==null) epsc += 1;
            }
            return toReturn;

    }
    public NondeterministicFA toNFA() throws RegLanguageException{
            states();
            removeAllExtended();
            Set<String> fin = definition.getFinalStates();
            TreeMap<String,Character> alphabet = getAlphabet();
            NondeterministicFA toReturn = new NondeterministicFA(new TreeSet(alphabet.values()));
            for (String state: states)
                toReturn.addState(state,fin.contains(state));
            //prazdny jazyk
            if (states.isEmpty() || definition.getStartingState()==null){
                toReturn.addState("A",false);
                toReturn.setStarting("A");
                return toReturn;
            }

            toReturn.setStarting(definition.getStartingState());

            for (Transition transition : definition.getTransitions()){
                if (!toReturn.addTransition(transition.getFrom(), transition.getTo(), 
                    alphabet.get(transition.getEdge())==null?'\u03B5':alphabet.get(transition.getEdge()))
                        && (!transition.getFrom().equals(transition.getTo())))
                    throw new RegLanguageException("Zadaný automat není NFA: Nemohu přidat přechod z "
                            + transition.getFrom() + " do "+ transition.getTo() + " s hranou "
                            + transition.getEdge()+ ". ");
            }
            return toReturn;

    }
    private TreeMap<String,Character> getAlphabet() throws RegLanguageException{
        TreeMap<String,Character> toReturn = new TreeMap<String,Character>();
        for (Transition transition : definition.getTransitions()){
            String e = transition.getEdge();
            if (e.length() == 1)
                toReturn.put(e,e.charAt(0));
            else if(e.equals("\\e"))
                transition.setEdge("");
            else if(e.matches("\\."))
                toReturn.put(e,e.charAt(1));
            else if (e.length() > 2)
                throw new RegLanguageException("Zlý symbol abecedy:" + e + ". ");
        }
        return toReturn;
    }
    private TreeSet<String> states(){
        for (Transition transition : definition.getTransitions()){
            states.add(transition.getFrom());
            states.add(transition.getTo());
        }
        return states;
    }
    private void removeAllExtended(){
        HashSet<Transition> extended = new HashSet<Transition>();
        for (Transition transition : definition.getTransitions()){

            if (transition.getEdge().matches("\".*\""))
                extended.add(transition);
            else if(transition.getEdge().length() > 2 ||
                (transition.getEdge().length() == 2 && !transition.getEdge().startsWith("\\")))
                extended.add(transition);
        }
        for (Transition transition : extended){
            removeExtended(transition);
        }
    }
    private void removeExtended(Transition transition){
        String toSplit = transition.getEdge();
        if (toSplit.matches("\".*\""))
            toSplit = toSplit.substring(1, toSplit.length()-1);
        String next = readCharacter(toSplit);
        if (next.equals(toSplit)) return;
        String nextState = addTransition(transition.getFrom(), transition.getFrom() + "."+next, next);
        transition.setFrom(nextState);
        transition.setEdge(eatCharacter(toSplit));
        removeExtended(transition);
    }
    private String addTransition(String from, String to, String edge){
        while (states.contains(to))
            to=to+"'";
        states.add(to);
        definition.addTransition(new Transition(from, to, edge));
        return to;
    }
    private String readCharacter(String from){
        if (from.startsWith("\\"))
            return from.substring(0,2);
        else
            return from.substring(0,1);
    }
    private String eatCharacter(String from){
        if (from.startsWith("\\"))
            return from.substring(2);
        else
            return from.substring(1);
    }
}
