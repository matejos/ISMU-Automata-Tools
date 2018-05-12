package cz.muni.fi.xpastirc.fawebinterface.comparing;

import cz.muni.fi.RegularLanguage.Automaton.DeterministicFA;
import cz.muni.fi.RegularLanguage.Automaton.NondeterministicFA;
import cz.muni.fi.RegularLanguage.Automaton.State;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Matej on 7.4.2018.
 */
public class AutomatonIsomorphismChecker {
    public static boolean areIsomorphic(LanguageInformation li1, LanguageInformation li2) {
        NondeterministicFA nfa1 = li1.toNFA();
        NondeterministicFA nfa2 = li2.toNFA();

        if (nfa1.getStates().size() != nfa2.getStates().size())
            return false;
        if (nfa1.getTransitions().size() != nfa2.getTransitions().size())
            return false;

        DeterministicFA dfa1 = li1.toDFA();
        DeterministicFA dfa2 = li2.toDFA();

        return checkIsomorphism(dfa1, dfa2);
    }

    private static boolean checkIsomorphism(DeterministicFA a1, DeterministicFA a2) {
        if (a1.getStates().size() != a2.getStates().size())
            return false;
        if (a1.getTransitions().size() != a2.getTransitions().size())
            return false;
        HashMap<State, State> f = new HashMap<State, State>();
        for (State s : a1.getAllStates().values()) {
            f.put(s, null);
        }
        f.put(a1.getStartingState(), a2.getStartingState());

        HashSet<State> discovered = new HashSet<State>();
        Stack<State> stack = new Stack<State>();
        stack.push(a1.getStartingState());
        while (!stack.empty()) {
            State s = stack.pop();
            if (!discovered.contains(s)) {
                discovered.add(s);
                for (Character x : a1.getAlphabet()) {
                    State t1 = a1.getTransitions().getResultFor(s, x);
                    State t2 = a2.getTransitions().getResultFor(f.get(s), x);
                    if (t1 != null) {
                        if (t2 == null)
                            return false;
                        if (f.get(t1) != null && f.get(t1) != t2)
                            return false;
                        f.put(t1, t2);
                        stack.push(t1);
                    }
                    else {
                        if (t2 != null)
                            return false;
                    }
                }
            }
        }
        return true;
    }
}
