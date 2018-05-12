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
    public static boolean areIsomorphic(LanguageInformation l1, LanguageInformation l2) {
        NondeterministicFA nfa1 = l1.toNFA();
        NondeterministicFA nfa2 = l2.toNFA();

        if (nfa1.getStates().size() != nfa2.getStates().size())
            return false;

        if (nfa1.getTransitions().size() != nfa2.getTransitions().size())
            return false;

        DeterministicFA dfa1 = l1.toDFA();
        DeterministicFA dfa2 = l2.toDFA();

        return checkIsomorphism(dfa1, dfa2);
    }

    private static boolean checkIsomorphism(DeterministicFA a1, DeterministicFA a2) {
        HashMap<State, State> permutation = new HashMap<State, State>();
        for (State s : a1.getAllStates().values()) {
            permutation.put(s, null);
        }
        permutation.put(a1.getStartingState(), a2.getStartingState());

        HashSet<State> discovered = new HashSet<State>();
        Stack<State> stack = new Stack<State>();
        stack.push(a1.getStartingState());
        while (!stack.empty()) {
            State s = stack.pop();
            if (!discovered.contains(s)) {
                discovered.add(s);
                if (a1.getTransitions().getTransitionsFrom(s).size() != a2.getTransitions().getTransitionsFrom(permutation.get(s)).size())
                    return false;
                for (Character x : a1.getAlphabet()) {
                    State s2 = a1.getTransitions().getResultFor(s, x);
                    State s2b = a2.getTransitions().getResultFor(permutation.get(s), x);
                    if (s2 != null) {
                        if (s2b == null)
                            return false;
                        if (permutation.get(s2) != null && permutation.get(s2) != s2b)
                            return false;
                        permutation.put(s2, s2b);
                        stack.push(s2);
                    }
                    else {
                        if (s2b != null)
                            return false;
                    }
                }
            }
        }
        return true;
    }
}
