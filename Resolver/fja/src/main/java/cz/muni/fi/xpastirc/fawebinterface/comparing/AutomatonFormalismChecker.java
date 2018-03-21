/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.xpastirc.fawebinterface.comparing;

import cz.muni.fi.RegularLanguage.Automaton.DeterministicFA;
import cz.muni.fi.RegularLanguage.Automaton.NondeterministicFA;
import cz.muni.fi.RegularLanguage.Exceptions.RegLanguageException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Tomas Pastircak - 324693@mail.muni.cz
 * @version 18.5.2011
 */
public class AutomatonFormalismChecker {
    private DeterministicFA automaton;
    private static StringBuilder feedback;
    private static int feedbackVal;

    public AutomatonFormalismChecker(DeterministicFA automaton){
    }
    public static boolean isInFormalism(String formalism, DeterministicFA automaton, int epscount){
        if (formalism.equals("MIN"))
        {
            feedback = new StringBuilder();
            feedbackVal = 0;
            boolean isTot = isTotal(automaton);
            boolean isMin = isMinimal(automaton);
            if(isTot) {feedback.append(" je totální,"); feedbackVal += 20;} else feedback.append(" není totální,");
            if(isMin) {feedback.append(" je minimální,"); feedbackVal += 20;}else feedback.append(" není minimální,");
            return (isMin && isTot);
        }
        if (formalism.equals("MIC"))
        {
            feedback = new StringBuilder();
            feedbackVal = 0;
            boolean isTot = isTotal(automaton);
            boolean isMin = isMinimal(automaton);
            if(isTot) {feedback.append(" je totální,"); feedbackVal += 15;} else feedback.append(" není totální,");
            if(isMin) {feedback.append(" je minimální,"); feedbackVal += 15;} else feedback.append(" není minimální,");
            if(isCanonised(automaton)) {feedback.append(" je kanonizovaný,"); feedbackVal += 15;} else feedback.append(" není kanonizovaný,");
            return (isCanonised(automaton) && isMin && isTot);
        }
        if (formalism.equals("TOT"))
        {
            feedback = new StringBuilder();
            feedbackVal = 0;
            boolean isTot = isTotal(automaton);
            if(isTot) {feedback.append(" je totální,");feedbackVal += 40;} else feedback.append(" není totální,");
            return (isTot);
        }
        if (formalism.equals("NFA")){
            feedback = new StringBuilder();
            feedbackVal = 0;
            if(epscount == 0){feedback.append(" je bez epsilon kroků,");feedbackVal += 30;} 
            else if(epscount == 1) {feedback.append(" obsahuje 1 epsilon krok,");feedbackVal += 15;}
            else {feedback.append(" stále obsahuje epsilon kroky,");feedbackVal += 0;};
            return (epscount == 0);
        }
        if (formalism.equals("DFA") || formalism.equals("ALL") || formalism.equals("EFA")
                || formalism.equals("GRA") || formalism.equals("REG"))
            return true;
        return false;
    }
    public static String getFeedback(){
        if(feedback == null) return "";
        return feedback.toString();
    }
    public static int getFeedbackVal(){
        return feedbackVal;
    }
    public static boolean isMinimalTotal(DeterministicFA automaton){
        if (!isTotal(automaton)) return false;
        int states = automaton.getStates().size();
        automaton.minimize();
        automaton.makeTotal();
        //System.out.println("minimaltotal " + states + "  " + automaton.getStates().size());
        return states==automaton.getStates().size();
    }
    public static boolean isMinimal(DeterministicFA automaton){
        if (!isTotal(automaton)) return false;
        int states = automaton.getStates().size();
        //automaton.removeIrelevantStates();
        automaton.minimize();
        automaton.makeTotal();
        System.out.println("minimal " + states + "  " + automaton.getStates().size());
        return states==automaton.getStates().size();
    }
    public static boolean isCanonised(DeterministicFA automaton){
        DeterministicFA aut;
        try {
            //System.out.println("canon ");
            aut = new DeterministicFA(automaton.toString());
        } catch (RegLanguageException ex) {
            aut = automaton;
        }
        aut.kanonize();
        return aut.isCanonizedFormOf(automaton);
    }
    public static boolean isTotal(DeterministicFA automaton){
        int states = automaton.getStates().size();
        automaton.makeTotal();
        //System.out.println("total " + states + "  " + automaton.getStates().size());
        return states == automaton.getStates().size();
    }
}
