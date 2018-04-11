package cz.muni.fi.xpastirc.fawebinterface.comparing;

import cz.muni.fi.RegularLanguage.Automaton.DeterministicFA;
import cz.muni.fi.RegularLanguage.Automaton.State;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Matej on 7.4.2018.
 */
public class AutomatonIsomorphismChecker {
    public static boolean areIsomorphic(DeterministicFA a1, DeterministicFA a2) {
        if (a1.getStates().size() != a2.getStates().size())
            return false;

        return checkIsomorphism(a1, a2) && checkIsomorphism(a2, a1);
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
