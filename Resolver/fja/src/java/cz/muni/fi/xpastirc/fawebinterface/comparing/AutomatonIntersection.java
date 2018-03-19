/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.xpastirc.fawebinterface.comparing;

import cz.muni.fi.RegularLanguage.Automaton.DeterministicFA;
import cz.muni.fi.RegularLanguage.Exceptions.RegLanguageException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

/**
 * @author Tomas Pastircak - 324693@mail.muni.cz
 * @version 21.5.2011
 */
public class AutomatonIntersection {
    private DeterministicFA a1;
    private DeterministicFA a2;
    
    private class Pair{
        public String s1;
        public String s2;
        public Pair(){};
        public Pair(String s1, String s2){
            this.s1=s1;
            this.s2=s2;
        }        
        public boolean isFinal(){
            return a1.getAllStates().get(s1).isAccepting() &&
                   a2.getAllStates().get(s2).isAccepting();
        }
        public String getName(){
            return s1+"."+s2;
        }
    }
    public AutomatonIntersection(DeterministicFA a1, DeterministicFA a2){
        this.a1=a1;
        this.a2=a2;
    }
    private Pair delta(Pair from, Character edge) throws RegLanguageException{
        Pair toReturn = new Pair();
        try{
            toReturn.s1= a1.getResult(a1.getAllStates().get(from.s1), edge).getName();
            toReturn.s2= a2.getResult(a2.getAllStates().get(from.s2), edge).getName();
        } catch (NullPointerException e){
            throw new RegLanguageException("No transition exists");
        }
        return toReturn;

    }
    public DeterministicFA intersection(){
        DeterministicFA toReturn = new DeterministicFA();
     // renameStates();
        Stack<Pair> s = new Stack<Pair>();
        for (Character c :a1.getAlphabet())
            toReturn.addSymbol(c);
        for (Character c :a2.getAlphabet())
            toReturn.addSymbol(c);
        Pair starting =new Pair(a1.getStartingState().getName(),a2.getStartingState().getName());
        s.push(starting);
        toReturn.myAddState(starting.getName());
        toReturn.setStarting(starting.getName());
        while (!s.isEmpty()){
            Pair p = s.pop();
              for (Character a : toReturn.getAlphabet()){
                    try {
                        Pair next = delta(p,a);
                        if (toReturn.myAddState(next.getName())){
                                s.push(next);
                        }
                        toReturn.addTransition(p.getName(), next.getName(), a);
                    } catch (RegLanguageException e){
                            //není některý z přechodů pod a, nic nedělat
                    }
              }
              toReturn.setAccepting(p.getName(),p.isFinal());
        }
        return toReturn;
    }

 /*   private void renameStates(){
        a1.removeIrelevantStates();
        a2.removeIrelevantStates();
        Collection<String> st1 = new HashSet<String>(a1.getStates());
        Collection<String> st2 = new HashSet<String>(a2.getStates());
        for (String name: st1)
            a1.renameState(name, name +"_1");
        for (String name: st2)
            a1.renameState(name, name +"_2");
    }*/
}
