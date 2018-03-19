/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.cfg.conversions;

import cz.muni.fi.cfg.forms.Analyser;
import cz.muni.fi.cfg.forms.TransformationException;
import cz.muni.fi.cfg.forms.Transformations;
import cz.muni.fi.cfg.grammar.ContextFreeGrammar;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author NICKT
 */
public class CFGComparator {
    private static int feedbackVal;
    
    public int getFeedbackVal(){
        return feedbackVal;
    }
    /**
     *
     * @param studentCFG
     * @param teacherCFG
     * @param ordering
     * @param type
     * @param mode
     * @return If the mode is simple returned array has 1 value for true, 3 values for false. If the mode is normal, it returns array with 4 values. Title, studentCFG, teacherCFG and answer.
     */
    public static String[] compare(ContextFreeGrammar studentCFG, ContextFreeGrammar teacherCFG, List<String> ordering, TransformationTypes type, Modes mode) {
        String[] outputArray = null;
        Analyser form = new Analyser();
        Transformations transform = new Transformations();
        ContextFreeGrammar teacherTransformed = null;
        StringBuilder feedback = new StringBuilder();
        feedbackVal = 0;
        switch (type) {
            case NE1:
                if (!form.languageIsNotEmpty(teacherCFG)) {
                    if (!form.languageIsNotEmpty(studentCFG) && studentCFG.getInitialNonTerminal().equals("")) {
                        switch (mode) {
                            case simple:
                                String[] yes = {"true"};
                                outputArray = yes;
                                break;
                            case normal:
                                String[] answer = {"Porovnáno:", "NAG", "NAG", "Gramatiky jsou stejné a obě bez nenormovaných symbolů."};
                                outputArray = answer;
                                break;
                        }
                    } else {
                        switch (mode) {
                            case simple:
                                String[] no = {"false", "Studentova gramatika generuje neprázdný jazyk, narozdíl od gramatiky učitele. Mělo tedy být zadáno NAG (Not A Grammar).", "" +feedbackVal+ "%"};
                                outputArray = no;
                                break;
                            case normal:
                                String[] answer = {"Neporovnáno:", studentCFG.toString(), "NAG", "Studentova gramatika generuje neprázdný jazyk, narozdíl od gramatiky učitele. Mělo tedy být zadáno NAG (Not A Grammar)."};
                                outputArray = answer;
                                break;
                        }
                    }
                } else {
                    if (!form.languageIsNotEmpty(studentCFG) && studentCFG.getInitialNonTerminal().equals("")) {
                        switch (mode) {
                            case simple:
                                String[] no = {"false","Studentova gramatika generuje prázdný jazyk, narozdíl od gramatiky učitele.","" +feedbackVal+ "%"};
                                outputArray = no;
                                break;
                            case normal:
                                try {
                                    teacherTransformed = transform.removeUnusefullSymbols(teacherCFG);
                                } catch (TransformationException ex) {
                                    Logger.getLogger(CFGComparator.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Studentova gramatika generuje prázdný jazyk, narozdíl od gramatiky učitele."};
                                outputArray = answer;
                                break;
                        }
                    } else {
                        if (form.hasUnproductiveSymbols(studentCFG, feedback)) {
                            feedbackVal += 10;
                            switch (mode) {
                                case simple:
                                        String[] no = {"false","Studentova gramatika stále obsahuje nenormované symboly."+ feedback.toString(),"" +feedbackVal + "%"};
                                        outputArray = no;
                                    break;
                                case normal:
                                    try {
                                        teacherTransformed = transform.removeUnusefullSymbols(teacherCFG);
                                    } catch (TransformationException ex) {
                                        Logger.getLogger(CFGComparator.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Studentova gramatika stále obsahuje nenormované symboly."};
                                    outputArray = answer;
                                    break;
                            }
                        } else {
                            try {
                                teacherTransformed = transform.removeUnusefullSymbols(teacherCFG);
                            } catch (TransformationException ex) {
                                Logger.getLogger(CFGComparator.class.getName()).log(Level.SEVERE, null, ex);
                            }
                                if (cfgFinaltyEquality(teacherCFG, studentCFG)){
                                    feedbackVal += 10;
                                    feedback.append("Konečnost jazyků generovaných gramatikami je stejná. ");
                                } else {
                                    feedback.append("Konečnost jazyků generovaných gramatikami není stejná. ");
                                }
                            if((cfgContainsAllWords(studentCFG, form.generateWords(teacherCFG, 5))).size()!=0){
                                    feedback.append("Slova z jazyka generovaného gramatikou zadání, které negeneruje gramatika řešení " + cfgContainsAllWords(studentCFG, form.generateWords(teacherCFG, 5)));
                                }
                            if((cfgContainsAllWords(teacherCFG, form.generateWords(studentCFG, 5))).size()!=0){
                                    feedback.append("Slova z jazyka generovaného gramatikou řešení, které negeneruje gramatika zadání " + cfgContainsAllWords(teacherCFG, form.generateWords(studentCFG, 5)));
                            }
                            if (!studentCFG.equals(teacherTransformed)) {
                                switch (mode) {
                                    case simple:
                                        String[] no = {"false","Studentova gramatika je bez nenormovaných symbolů, ale není izomorfní s řešením."+ feedback.toString(),"" +feedbackVal+ "%"};
                                        outputArray = no;
                                        break;
                                    case normal:
                                        try {
                                            teacherTransformed = transform.removeUnusefullSymbols(teacherCFG);
                                        } catch (TransformationException ex) {
                                            Logger.getLogger(CFGComparator.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Obě gramatiky jsou bez nenormovaných symbolů, ale nejsou izomorfní."};
                                        outputArray = answer;
                                        break;
                                }
                            } else {
                                switch (mode) {
                                    case simple:
                                        String[] yes = {"true"};
                                        outputArray = yes;
                                        break;
                                    case normal:
                                        try {
                                            teacherTransformed = transform.removeUnusefullSymbols(teacherCFG);
                                        } catch (TransformationException ex) {
                                            Logger.getLogger(CFGComparator.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                        String[] answer = {"Porovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Obě gramatiky jsou bez nenormovaných symbolů a jsou stejné."};
                                        outputArray = answer;
                                        break;
                                }
                            }
                        }
                    }
                }
                break;
            case NE2:
                if (form.isNAG(studentCFG) && (form.isNAG(teacherCFG) || !form.languageIsNotEmpty(teacherCFG))) {
                    switch (mode) {
                        case simple:
                            String[] yes = {"true"};
                            outputArray = yes;
                            break;
                        case normal:
                            String[] answer = {"Porovnáno:", "NAG", "NAG", "Obě gramatiky jsou bez nedosažitelných symbolů a jsou stejné."};
                            outputArray = answer;
                            break;
                    }
                } else {
                    teacherTransformed = transform.removeUnreachableSymbols(teacherCFG);
                    if (form.hasInaccessibleSymbols(studentCFG, feedback)) {
                        switch (mode) {
                            case simple:
                                String[] no = {"false","Studentova gramatika stále obsahuje nedosažitelné symboly. " + feedback.toString(),"" +feedbackVal+ "%"};
                                outputArray = no;
                                break;
                            case normal:
                                String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Studentova gramatika stále obsahuje nedosažitelné symboly."};
                                outputArray = answer;
                                break;
                        }
                    } else {
                        if (cfgFinaltyEquality(teacherCFG, studentCFG)){
                                    feedbackVal += 10;
                                    feedback.append("Konečnost jazyků generovaných gramatikami je stejná. ");
                                } else {
                                    feedback.append("Konečnost jazyků generovaných gramatikami není stejná. ");
                                }
                        if((cfgContainsAllWords(studentCFG, form.generateWords(teacherCFG, 5))).size()!=0){
                                    feedback.append("Slova z jazyka generovaného gramatikou zadání, které negeneruje gramatika řešení " + cfgContainsAllWords(studentCFG, form.generateWords(teacherCFG, 5)));
                                }
                            if((cfgContainsAllWords(teacherCFG, form.generateWords(studentCFG, 5))).size()!=0){
                                    feedback.append("Slova z jazyka generovaného gramatikou řešení, které negeneruje gramatika zadání " + cfgContainsAllWords(teacherCFG, form.generateWords(studentCFG, 5)));
                            }
                        if (!studentCFG.equals(teacherTransformed)) {
                            switch (mode) {
                                case simple:
                                    String[] no = {"false","Studentova gramatika je bez nedosažitelných symbolů, ale není izomorfní s řešením."+ feedback.toString(),"" +feedbackVal+ "%"};
                                    outputArray = no;
                                    break;
                                case normal:
                                    String[] answer = {"Neporovnáno:", form.isNAG(studentCFG) ? "NAG" : studentCFG.toString(), form.isNAG(teacherCFG) ? "NAG" : teacherTransformed.toString(), "Obě gramatiky jsou bez nedosažitelných symbolů, ale nejsou izomorfní."};
                                    outputArray = answer;
                                    break;
                            }
                        } else {
                            switch (mode) {
                                case simple:
                                    String[] yes = {"true"};
                                    outputArray = yes;
                                    break;
                                case normal:
                                    String[] answer = {"Porovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Obě gramatiky jsou bez nedosažitelných symbolů a jsou stejné."};
                                    outputArray = answer;
                                    break;
                            }
                        }
                    }
                }
                break;
            case RED:
                if (form.isNAG(studentCFG) && (form.isNAG(teacherCFG) || !form.languageIsNotEmpty(teacherCFG))) {
                    switch (mode) {
                        case simple:
                            String[] yes = {"true"};
                            outputArray = yes;
                            break;
                        case normal:
                            String[] answer = {"Porovnáno:", "NAG", "NAG", "Obě gramatiky jsou redukované a jsou stejné."};
                            outputArray = answer;
                            break;
                    }
                } else {
                    if (form.hasUnproductiveSymbols(studentCFG, feedback)) {
                        switch (mode) {
                            case simple:
                                String[] no = {"false","Studentova gramatika stále obsahuje nenormované symboly."+ feedback.toString(), "" +feedbackVal+ "%"};
                                outputArray = no;
                                break;
                            case normal:
                                if (!form.languageIsNotEmpty(teacherCFG)) {
                                    String[] answer = {"Neporovnáno:", studentCFG.toString(), "NAG", "Studentova gramatika stále obsahuje nenormované symboly."};
                                    outputArray = answer;
                                } else {
                                    try {
                                        teacherCFG = transform.makeReducedCFG(teacherCFG);
                                    } catch (TransformationException ex) {
                                        Logger.getLogger(CFGComparator.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Studentova gramatika stále obsahuje nenormované symboly."};
                                    outputArray = answer;
                                }
                                break;
                        }
                    } else if (form.hasInaccessibleSymbols(studentCFG, feedback)) {
                        switch (mode) {
                            case simple:
                                feedbackVal += 10;
                                String[] no = {"false","Studentova gramatika je bez nenormovaných symbolů, ale stále obsahuje nedosažitelné symboly."+ feedback.toString(), "" +feedbackVal+ "%"};
                                outputArray = no;
                                break;
                            case normal:
                                if (!form.languageIsNotEmpty(teacherCFG)) {
                                    String[] answer = {"Neporovnáno:", studentCFG.toString(), "NAG", "Studentova gramatika stále obsahuje nedosažitelné symboly."};
                                    outputArray = answer;
                                } else {
                                    try {
                                        teacherTransformed = transform.makeReducedCFG(teacherCFG);
                                    } catch (TransformationException ex) {
                                        Logger.getLogger(CFGComparator.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Studentova gramatika stále obsahuje nedosažitelné symboly."};
                                    outputArray = answer;
                                }
                                break;
                        }
                    } else {
                        if (!form.languageIsNotEmpty(teacherCFG)) {
                            teacherTransformed = new ContextFreeGrammar(new HashSet<String>(), new HashMap<String, Set<String>>(), "");
                        } else {
                            try {
                                teacherTransformed = transform.makeReducedCFG(teacherCFG);
                            } catch (TransformationException ex) {
                                Logger.getLogger(CFGComparator.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if (cfgFinaltyEquality(teacherCFG, studentCFG)){
                                    feedbackVal += 10;
                                    feedback.append("Konečnost jazyků generovaných gramatikami je stejná. ");
                                } else {
                                    feedback.append("Konečnost jazyků generovaných gramatikami není stejná. ");
                                }
                        if((cfgContainsAllWords(studentCFG, form.generateWords(teacherCFG, 5))).size()!=0){
                                    feedback.append("Slova z jazyka generovaného gramatikou zadání, které negeneruje gramatika řešení " + cfgContainsAllWords(studentCFG, form.generateWords(teacherCFG, 5)));
                                }
                            if((cfgContainsAllWords(teacherCFG, form.generateWords(studentCFG, 5))).size()!=0){
                                    feedback.append("Slova z jazyka generovaného gramatikou řešení, které negeneruje gramatika zadání " + cfgContainsAllWords(teacherCFG, form.generateWords(studentCFG, 5)));
                            }
                        if (!studentCFG.equals(teacherTransformed)) {
                            switch (mode) {
                                case simple:
                                    feedbackVal += 10;
                                    String[] no = {"false","Studentova gramatika je redukovaná, ale není izomorfní s řešením."+ feedback.toString(), "" +feedbackVal+ "%"};
                                    outputArray = no;
                                    break;
                                case normal:
                                    String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Obě gramatiky jsou redukované, ale nejsou izomorfní."};
                                    outputArray = answer;
                                    break;
                            }
                        } else {
                            switch (mode) {
                                case simple:
                                    String[] yes = {"true"};
                                    outputArray = yes;
                                    break;
                                case normal:
                                    String[] answer = {"Porovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Obě gramatiky jsou redukované a jsou stejné."};
                                    outputArray = answer;
                                    break;
                            }
                        }
                    }
                }
                break;
            case EPS:
                if (form.isNAG(studentCFG) && form.isNAG(teacherCFG)) {
                    switch (mode) {
                        case simple:
                            String[] yes = {"true"};
                            outputArray = yes;
                            break;
                        case normal:
                            String[] answer = {"Porovnáno:", "NAG", "NAG", "Obě gramatiky jsou stejné."};
                            outputArray = answer;
                            break;
                    }
                } else if (form.isNAG(studentCFG) || form.isNAG(teacherCFG)) {
                    switch (mode) {
                        case simple:
                            String[] no = {"false","Nesmyslné zadání.", "" +feedbackVal+ "%"};
                            outputArray = no;
                            break;
                        case normal:
                            String[] answer = {"Neporovnáno:", form.isNAG(studentCFG) ? "NAG" : studentCFG.toString(), form.isNAG(teacherCFG) ? "NAG" : teacherCFG.toString(), "Nesmyslné zadání."};
                            outputArray = answer;
                            break;
                    }
                } else {
                    if (form.hasEpsilonRules(teacherCFG)) {
                        teacherTransformed = transform.removeEps(teacherCFG);
                    } else {
                        teacherTransformed = teacherCFG;
                    }
                if (form.hasEpsilonRules(studentCFG, feedback)) {
                    switch (mode) {
                        case simple:
                            String[] no = {"false","Studentova gramatika stále obsahuje epsilon kroky."+ feedback.toString(),"" +feedbackVal+ "%"};
                            outputArray = no;
                            break;
                        case normal:
                            String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Studentova gramatika stále obsahuje epsilon kroky."};
                            outputArray = answer;
                            break;
                    }
                } else {
                    if (cfgFinaltyEquality(teacherCFG, studentCFG)){
                                    feedbackVal += 10;
                                    feedback.append("Konečnost jazyků generovaných gramatikami je stejná. ");
                                } else {
                                    feedback.append("Konečnost jazyků generovaných gramatikami není stejná. ");
                                }
                    if((cfgContainsAllWords(studentCFG, form.generateWords(teacherCFG, 5))).size()!=0){
                                    feedback.append("Slova z jazyka generovaného gramatikou zadání, které negeneruje gramatika řešení " + cfgContainsAllWords(studentCFG, form.generateWords(teacherCFG, 5)));
                                }
                            if((cfgContainsAllWords(teacherCFG, form.generateWords(studentCFG, 5))).size()!=0){
                                    feedback.append("Slova z jazyka generovaného gramatikou řešení, které negeneruje gramatika zadání " + cfgContainsAllWords(teacherCFG, form.generateWords(studentCFG, 5)));
                            }
                    if (!studentCFG.equals(teacherTransformed)) {
                        switch (mode) {
                            case simple:
                                String[] no = {"false","Obě gramatiky jsou bez epsilon kroků, ale nejsou izomorfní."+ feedback.toString(), "" +feedbackVal+ "%"};
                                outputArray = no;
                                break;
                            case normal:
                                String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Obě gramatiky jsou bez epsilon kroků, ale nejsou izomorfní."};
                                outputArray = answer;
                                break;
                        }
                    } else {
                        switch (mode) {
                            case simple:
                                String[] yes = {"true"};
                                outputArray = yes;
                                break;
                            case normal:
                                String[] answer = {"Porovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Obě gramatiky jsou bez epsilon kroků a jsou stejné."};
                                outputArray = answer;
                                break;
                        }
                    }
                }
                }
                break;
            case SRF:
                if (form.isNAG(studentCFG) && form.isNAG(teacherCFG)) {
                    switch (mode) {
                        case simple:
                            String[] yes = {"true"};
                            outputArray = yes;
                            break;
                        case normal:
                            String[] answer = {"Porovnáno:", "NAG", "NAG", "Obě gramatiky jsou stejné."};
                            outputArray = answer;
                            break;
                    }
                } else if (form.isNAG(studentCFG) || form.isNAG(teacherCFG)) {
                    switch (mode) {
                        case simple:
                            String[] no = {"false","Nesmyslné zadání.","" +feedbackVal+ "%"};
                            outputArray = no;
                            break;
                        case normal:
                            String[] answer = {"Neporovnáno:", form.isNAG(studentCFG) ? "NAG" : studentCFG.toString(), form.isNAG(teacherCFG) ? "NAG" : teacherCFG.toString(), "Nesmyslné zadání."};
                            outputArray = answer;
                            break;
                    }
                } else {
//                if ((form.hasEpsilonRules(teacherCFG) && form.hasSimpleRules(teacherCFG)) ||
//                        (form.hasEpsilonRules(teacherCFG) && !form.hasSimpleRules(teacherCFG)) ||
//                        (!form.hasEpsilonRules(teacherCFG) && form.hasSimpleRules(teacherCFG))) {
                    teacherTransformed = transform.removeSimpleRules(teacherCFG);
//                } else {
//                    teacherTransformed = teacherCFG;
//                }
//                if (form.hasEpsilonRules(studentCFG)) {
//                    switch (mode) {
//                        case simple:
//                            String[] no = {"false"};
//                            outputArray = no;
//                            break;
//                        case normal:
//                            String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Studentova gramatika stále obsahuje epsilon kroky."};
//                            outputArray = answer;
//                            break;
//                    }
//                } else
                    if (form.hasSimpleRules(studentCFG, feedback)) {
                        switch (mode) {
                            case simple:
                                String[] no = {"false","Studentova gramatika stále obsahuje jednoduchá pravidla."+ feedback.toString(),"" +feedbackVal+ "%"};
                                outputArray = no;
                                break;
                            case normal:
                                String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Studentova gramatika stále obsahuje jednoduchá pravidla."};
                                outputArray = answer;
                                break;
                        }
                        
                    } else if (cfgFinaltyEquality(teacherCFG, studentCFG)){
                                    feedbackVal += 10;
                                    feedback.append("Konečnost jazyků generovaných gramatikami je stejná. ");
                                } else {
                                    feedback.append("Konečnost jazyků generovaných gramatikami není stejná. ");
                                }
                    if((cfgContainsAllWords(studentCFG, form.generateWords(teacherCFG, 5))).size()!=0){
                                    feedback.append("Slova z jazyka generovaného gramatikou zadání, které negeneruje gramatika řešení " + cfgContainsAllWords(studentCFG, form.generateWords(teacherCFG, 5)));
                                }
                            if((cfgContainsAllWords(teacherCFG, form.generateWords(studentCFG, 5))).size()!=0){
                                    feedback.append("Slova z jazyka generovaného gramatikou řešení, které negeneruje gramatika zadání " + cfgContainsAllWords(teacherCFG, form.generateWords(studentCFG, 5)));
                            }
                    if (!studentCFG.equals(teacherTransformed)) {
                        switch (mode) {
                            case simple:
                                String[] no = {"false","Studentova gramatika je bez jednoduchých pravidel, ale není izomorfní s řešením."+ feedback.toString(),"" +feedbackVal+ "%"};
                                outputArray = no;
                                break;
                            case normal:
                                String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Obě gramatiky jsou bez jednoduchých pravidel, ale nejsou izomorfní."};
                                outputArray = answer;
                                break;
                        }
                    } else {
                        switch (mode) {
                            case simple:
                                String[] yes = {"true"};
                                outputArray = yes;
                                break;
                            case normal:
                                String[] answer = {"Porovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Obě gramatiky jsou bez jednoduchých pravidel a jsou stejné."};
                                outputArray = answer;
                                break;
                        }
                    }
                }
                break;
            case PRO:
                if (form.isNAG(studentCFG) && (form.isNAG(teacherCFG) || !form.languageIsNotEmpty(teacherCFG))) {
                    switch (mode) {
                        case simple:
                            String[] yes = {"true"};
                            outputArray = yes;
                            break;
                        case normal:
                            String[] answer = {"Porovnáno:", "NAG", "NAG", "Obě gramatiky jsou stejné."};
                            outputArray = answer;
                            break;
                    }
                } else {
                    if (!form.languageIsNotEmpty(teacherCFG)) {
                        if (!form.languageIsNotEmpty(studentCFG) && studentCFG.getInitialNonTerminal().equals("")) {
                            switch (mode) {
                                case simple:
                                    String[] yes = {"true"};
                                    outputArray = yes;
                                    break;
                                case normal:
                                    String[] answer = {"Porovnáno:", studentCFG.toString(), "NAG", "Gramatiky jsou stejné a vlastní."};
                                    outputArray = answer;
                                    break;
                            }
                        } else {
                            switch (mode) {
                                case simple:
                                    String[] no = {"false","Studentova gramatika generuje neprázdný jazyk, narozdíl od gramatiky zadání. Mělo tedy být zadáno NAG (Not A Grammar).", "" +feedbackVal+ "%"};
                                    outputArray = no;
                                    break;
                                case normal:
                                    String[] answer = {"Neporovnáno:", studentCFG.toString(), "NAG", "Studentova gramatika generuje neprázdný jazyk, narozdíl od gramatiky učitele. Mělo tedy být zadáno NAG (Not A Grammar)."};
                                    outputArray = answer;
                                    break;
                            }
                        }
                    } else {
                        if (!form.languageIsNotEmpty(studentCFG) && studentCFG.getInitialNonTerminal().equals("")) {
                            switch (mode) {
                                case simple:
                                    String[] no = {"false","Studentova gramatika generuje prázdný jazyk, narozdíl od gramatiky zadání.","" +feedbackVal+ "%"};
                                    outputArray = no;
                                    break;
                                case normal:
                                    try {
                                        teacherTransformed = transform.removeUnusefullSymbols(teacherCFG);
                                    } catch (TransformationException ex) {
                                        Logger.getLogger(CFGComparator.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Studentova gramatika generuje prázdný jazyk, narozdíl od gramatiky učitele."};
                                    outputArray = answer;
                                    break;
                            }
                        } else { //obě generují neprázdný jazyk
//                        if ((form.hasEpsilonRules(teacherCFG) && form.hasSimpleRules(teacherCFG)) ||
//                                (form.hasEpsilonRules(teacherCFG) && !form.hasSimpleRules(teacherCFG)) ||
//                                (!form.hasEpsilonRules(teacherCFG) && form.hasSimpleRules(teacherCFG))) {
                            try {
                                teacherTransformed = transform.makeProperCFG(teacherCFG);
                            } catch (TransformationException ex) {
                                Logger.getLogger(CFGComparator.class.getName()).log(Level.SEVERE, null, ex);
                            }
//                        } else {
//                            try {
//                                teacherTransformed = transform.makeReducedCFG(teacherCFG);
//                            } catch (TransformationException ex) {
//                                Logger.getLogger(CFGComparator.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                        }

//                        if (form.hasEpsilonRules(studentCFG)) {
//                            switch (mode) {
//                                case simple:
//                                    String[] no = {"false"};
//                                    outputArray = no;
//                                    break;
//                                case normal:
//                                    String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Studentova gramatika stále obsahuje epsilon kroky."};
//                                    outputArray = answer;
//                                    break;
//                            }
//                        } else
                            if (form.hasSimpleRules(studentCFG, feedback)) {
                                switch (mode) {
                                    case simple:
                                        String[] no = {"false","Studentova gramatika stále obsahuje jednoduchá pravidla."+ feedback.toString(),"" +feedbackVal+ "%"};
                                        outputArray = no;
                                        break;
                                    case normal:
                                        String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Studentova gramatika stále obsahuje jednoduchá pravidla."};
                                        outputArray = answer;
                                        break;
                                }
                            } else if (!form.isReduced(studentCFG, feedback)) {
                                switch (mode) {
                                    case simple:
                                        String[] no = {"false", "Studentova gramatika stále obsahuje nepoužitelné symboly."+ feedback.toString(),"" +feedbackVal+ "%"};
                                        outputArray = no;
                                        break;
                                    case normal:
                                        String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Studentova gramatika stále obsahuje nepoužitelné symboly."};
                                        outputArray = answer;
                                        break;
                                }
                            } else if (cfgFinaltyEquality(teacherCFG, studentCFG)){
                                    feedbackVal += 10;
                                    feedback.append("Konečnost jazyků generovaných gramatikami je stejná. ");
                                } else if(!cfgFinaltyEquality(teacherCFG, studentCFG)){
                                    feedback.append("Konečnost jazyků generovaných gramatikami není stejná. ");
                                }
                                if (!studentCFG.equals(teacherTransformed)) {
                                switch (mode) {
                                    case simple:
                                        String[] no = {"false","Obě gramatiky jsou vlastní, ale nejsou izomorfní."+ feedback.toString(),"" +feedbackVal+ "%"};
                                        outputArray = no;
                                        break;
                                    case normal:
                                        String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Obě gramatiky jsou vlastní, ale nejsou izomorfní."};
                                        outputArray = answer;
                                        break;
                                }
                            } else {
                                switch (mode) {
                                    case simple:
                                        String[] yes = {"true"};
                                        outputArray = yes;
                                        break;
                                    case normal:
                                        String[] answer = {"Porovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Obě gramatiky jsou vlastní a jsou stejné."};
                                        outputArray = answer;
                                        break;
                                }
                            }
                        }
                    }
                }
                break;
            case CNF:
                if (form.isNAG(studentCFG) || form.isNAG(teacherCFG)) {
                    switch (mode) {
                        case simple:
                            String[] no = {"false","Nesmyslné zadání.","" +feedbackVal+ "%"};
                            outputArray = no;
                            break;
                        case normal:
                            String[] answer = {"Neporovnáno:", form.isNAG(studentCFG) ? "NAG" : studentCFG.toString(), form.isNAG(teacherCFG) ? "NAG" : teacherCFG.toString(), "Nesmyslné zadání."};
                            outputArray = answer;
                            break;
                    }
                } else {
                    if (!form.languageIsNotEmpty(teacherCFG)) {
                        try {
                            teacherTransformed = transform.transformToCNF(teacherCFG);
                        } catch (TransformationException ex) {
                            Logger.getLogger(CFGComparator.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (form.languageIsNotEmpty(studentCFG)) {
                            switch (mode) {
                                case simple:
                                    String[] no = {"false","Gramatiky nejsou izomorfní, protože studentova gramatika generuje neprázdný jazyk narozdíl od gramatiky učitele, generující jazyk prázdný.","" +feedbackVal+ "%"};
                                    outputArray = no;
                                    break;
                                case normal:
                                    String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Gramatiky nejsou izomorfní, protože studentova gramatika generuje neprázdný jazyk narozdíl od gramatiky učitele, generující jazyk prázdný."};
                                    outputArray = answer;
                                    break;
                            }
                        } else if (!studentCFG.equals(teacherTransformed)) {
                            switch (mode) {
                                case simple:
                                    String[] no = {"false","Gramatiky nejsou izomorfní. Pokud jste zadali gramatiku v CNF generující neprázdný jazyk, zadejte příště S->SS, jinak dojde k chybnému vyhodnocení.","" +feedbackVal+ "%"};
                                    outputArray = no;
                                    break;
                                case normal:
                                    String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Gramatiky nejsou izomorfní. Pokud jste zadali gramatiku v CNF generující neprázdný jazyk, zadejte příště S->SS, jinak dojde k chybnému vyhodnocení."};
                                    outputArray = answer;
                                    break;
                            }
                        } else {
                            switch (mode) {
                                case simple:
                                    String[] yes = {"true"};
                                    outputArray = yes;
                                    break;
                                case normal:
                                    String[] answer = {"Porovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Obě gramatiky jsou v CNF a jsou stejné."};
                                    outputArray = answer;
                                    break;
                            }
                        }
                    } else if (!form.languageIsNotEmpty(studentCFG)) { //učitelova generuje neprázdný jazyk
                        switch (mode) {
                            case simple:
                                String[] no = {"false","Studentova gramatika generuje prázdný jazyk, narozdíl od gramatiky učitele generující jazyk neprázdný.","" +feedbackVal+ "%"};
                                outputArray = no;
                                break;
                            case normal:
                                String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherCFG.toString(), "Studentova gramatika generuje prázdný jazyk, narozdíl od gramatiky učitele generující jazyk neprázdný."};
                                outputArray = answer;
                                break;
                        }
                    } else { //ani jedna negeneruje prázdný jazyk
                        if (form.isInCNF(teacherCFG)) {
                            teacherTransformed = teacherCFG;
                        } else {
                            try {
                                teacherTransformed = transform.transformToCNF(teacherCFG);
                            } catch (TransformationException ex) {
                                Logger.getLogger(CFGComparator.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

//                    if (form.hasEpsilonRules(studentCFG)) {
//                        switch (mode) {
//                            case simple:
//                                String[] no = {"false"};
//                                outputArray = no;
//                                break;
//                            case normal:
//                                String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Studentova gramatika stále obsahuje epsilon kroky."};
//                                outputArray = answer;
//                                break;
//                        }
//                    } else
                        if (form.hasSimpleRules(studentCFG, feedback)) {
                            switch (mode) {
                                case simple:
                                    String[] no = {"false","Studentova gramatika stále obsahuje jednoduchá pravidla."+ feedback.toString(),"" +feedbackVal+ "%"};
                                    outputArray = no;
                                    break;
                                case normal:
                                    String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Studentova gramatika stále obsahuje jednoduchá pravidla."};
                                    outputArray = answer;
                                    break;
                            }
                        } else if (!form.isReduced(studentCFG, feedback)) {
                            switch (mode) {
                                case simple:
                                    String[] no = {"false","Studentova gramatika stále obsahuje nepoužitelné symboly."+ feedback.toString(),"" +feedbackVal+ "%"};
                                    outputArray = no;
                                    break;
                                case normal:
                                    String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Studentova gramatika stále obsahuje nepoužitelné symboly."};
                                    outputArray = answer;
                                    break;
                            }
                        } else if (!form.isInCNF(studentCFG, feedback)) {
                            switch (mode) {
                                case simple:
                                    String[] no = {"false","Studentova gramatika není v CNF."+ feedback.toString(),"" +feedbackVal+ "%"};
                                    outputArray = no;
                                    break;
                                case normal:
                                    String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Studentova gramatika není v CNF."};
                                    outputArray = answer;
                                    break;
                            }
                        } else if (cfgFinaltyEquality(teacherCFG, studentCFG)){
                                    feedbackVal += 10;
                                    feedback.append("Konečnost jazyků generovaných gramatikami je stejná. ");
                                } else if(cfgFinaltyEquality(teacherCFG, studentCFG)){
                                    feedback.append("Konečnost jazyků generovaných gramatikami není stejná. ");
                                }
                        if((cfgContainsAllWords(studentCFG, form.generateWords(teacherCFG, 5))).size()!=0){
                                    feedback.append("Slova z jazyka generovaného gramatikou zadání, které negeneruje gramatika řešení " + cfgContainsAllWords(studentCFG, form.generateWords(teacherCFG, 5)));
                                }
                            if((cfgContainsAllWords(teacherCFG, form.generateWords(studentCFG, 5))).size()!=0){
                                    feedback.append("Slova z jazyka generovaného gramatikou řešení, které negeneruje gramatika zadání " + cfgContainsAllWords(teacherCFG, form.generateWords(studentCFG, 5)));
                            }
                        if (!studentCFG.equals(teacherTransformed)) {
                            switch (mode) {
                                case simple:
                                    String[] no = {"false","Gramatiky nejsou izomorfní."+ feedback.toString(),"" +feedbackVal+ "%"};
                                    outputArray = no;
                                    break;
                                case normal:
                                    String[] answer = {"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Gramatiky nejsou izomorfní."};
                                    outputArray = answer;
                                    break;
                            }
                        } else {
                            switch (mode) {
                                case simple:
                                    String[] yes = {"true"};
                                    outputArray = yes;
                                    break;
                                case normal:
                                    String[] answer = {"Porovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Obě gramatiky jsou v CNF a jsou stejné."};
                                    outputArray = answer;
                                    break;
                            }
                        }
                    }
                }
                break;
            // Left recursion compare
            case RLR:
                if ((form.isNAG(studentCFG) || !form.languageIsNotEmpty(studentCFG)) && (form.isNAG(teacherCFG) || !form.languageIsNotEmpty(teacherCFG))) {
                    if (mode.equals(Modes.simple)) {
                        outputArray = new String[]{"true"};
                    } else if (mode.equals(Modes.normal)) {
                        outputArray = new String[]{"Porovnáno:", "NAG", "NAG", "Obě gramatiky jsou stejné."};
                    }
                } else if (form.isLeftRecursive(studentCFG)) {
                    if (mode.equals(Modes.simple)) {
                        outputArray = new String[]{"false","Studentova gramatika má levou rekursi."+ feedback.toString(),"" +feedbackVal+ "%"};
                    } else if (mode.equals(Modes.normal)) {
                        outputArray = new String[]{"Neporovnáno:", studentCFG.toString(), teacherCFG.toString(), "Studentova gramatika má levou rekursi."};
                    }
                } else {
                    boolean equal = false;
                    Map<ContextFreeGrammar, List[]> returnMap = null;
                    for (List<String> studentOrdering : form.getOrdering(studentCFG, teacherCFG.getNonTerminals())) {
                        try {
                            returnMap = transform.removeLeftRecursion(teacherCFG, studentOrdering);
                            for (ContextFreeGrammar teacher : returnMap.keySet()) {
                                teacherTransformed = teacher;
                            }
                            if (studentCFG.equals(teacherTransformed)) {
                                equal = true;
                                break;
                            }
                        } catch (TransformationException ex) {
                            continue;
                        }
                    }
                    if (equal) {
                        if (mode.equals(Modes.simple)) {
                            outputArray = new String[]{"true"};
                        } else if (mode.equals(Modes.normal)) {
                            outputArray = new String[]{"Porovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Gramatiky jsou izomorfní."};
                        }
                    } else {
                        if((cfgContainsAllWords(studentCFG, form.generateWords(teacherCFG, 5))).size()!=0){
                                    feedback.append("Slova z jazyka generovaného gramatikou zadání, které negeneruje gramatika řešení " + cfgContainsAllWords(studentCFG, form.generateWords(teacherCFG, 5)));
                                }
                            if((cfgContainsAllWords(teacherCFG, form.generateWords(studentCFG, 5))).size()!=0){
                                    feedback.append("Slova z jazyka generovaného gramatikou řešení, které negeneruje gramatika zadání " + cfgContainsAllWords(teacherCFG, form.generateWords(studentCFG, 5)));
                            }
                        if (cfgFinaltyEquality(teacherCFG, studentCFG)){
                                    feedbackVal += 10;
                                    feedback.append("Konečnost jazyků generovaných gramatikami je stejná. ");
                                } else {
                                    feedback.append("Konečnost jazyků generovaných gramatikami není stejná. ");
                                }
                        if (mode.equals(Modes.simple)) {
                            outputArray = new String[]{"false","Gramatiky nejsou izomorfní."+ feedback.toString(),"" +feedbackVal+ "%"};
                        } else if (mode.equals(Modes.normal)) {
                            outputArray = new String[]{"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Gramatiky nejsou izomorfní."};
                        }
                    }
                }
                break;
// Greibach form compare
// @author Daniel Pelisek
            case GNF:
                // one of CFGs is wrong - return false
                if (form.isNAG(studentCFG) || form.isNAG(teacherCFG)) {
                    if (mode.equals(Modes.simple)) {
                        outputArray = new String[]{"false","Nesmyslné zadání.","" +feedbackVal+ "%"};
                    } else if (mode.equals(Modes.normal)) {
                        outputArray = new String[]{"Neporovnáno:", form.isNAG(studentCFG) ? "NAG" : studentCFG.toString(), form.isNAG(teacherCFG) ? "NAG" : teacherCFG.toString(), "Nesmyslné zadání."};
                    }
                    // student CFG is not in GNF - false
                } else if (!form.isInGNF(studentCFG, feedback)) {
                    if (mode.equals(Modes.simple)) {
                        outputArray = new String[]{"false","Studentova gramatika není v GNF."+ feedback.toString(),"" +feedbackVal+ "%"};
                    } else if (mode.equals(Modes.normal)) {
                        outputArray = new String[]{"Neporovnáno:", studentCFG.toString(), teacherCFG.toString(), "Studentova gramatika není v GNF."};
                    }
                    // transform teacher CFG to GNF
                } else {
                    try {
                        teacherTransformed = transform.transformToGNF(teacherCFG, ordering);
                    } catch (TransformationException ex) {
                        teacherTransformed = teacherCFG;
                    }
                    if (studentCFG.equals(teacherTransformed)) {
                        if (mode.equals(Modes.simple)) {
                            outputArray = new String[]{"true"};
                        } else if (mode.equals(Modes.normal)) {
                            outputArray = new String[]{"Porovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Gramatiky jsou izomorfní."};
                        }
                    } else {
                        if((cfgContainsAllWords(studentCFG, form.generateWords(teacherCFG, 5))).size()!=0){
                                    feedback.append("Slova z jazyka generovaného gramatikou zadání, které negeneruje gramatika řešení " + cfgContainsAllWords(studentCFG, form.generateWords(teacherCFG, 5)));
                                }
                            if((cfgContainsAllWords(teacherCFG, form.generateWords(studentCFG, 5))).size()!=0){
                                    feedback.append("Slova z jazyka generovaného gramatikou řešení, které negeneruje gramatika zadání " + cfgContainsAllWords(teacherCFG, form.generateWords(studentCFG, 5)));
                            }
                        if (cfgFinaltyEquality(teacherCFG, studentCFG)){
                                    feedbackVal += 10;
                                    feedback.append("Konečnost jazyků generovaných gramatikami je stejná. ");
                                } else {
                                    feedback.append("Konečnost jazyků generovaných gramatikami není stejná. ");
                                }
                        if (mode.equals(Modes.simple)) {
                            outputArray = new String[]{"false","Gramatiky nejsou izomorfní."+ feedback.toString(),"" +feedbackVal+ "%"};
                        } else if (mode.equals(Modes.normal)) {
                            outputArray = new String[]{"Neporovnáno:", studentCFG.toString(), teacherTransformed.toString(), "Gramatiky nejsou izomorfní."};
                        }
                    }
                }
                break;
        }

        return outputArray;
    }
    /**
     * @author Adrian Elgyutt
     * @returns true if are both finite/infinite false otherwise
     */
    private static boolean cfgFinaltyEquality(ContextFreeGrammar g1, ContextFreeGrammar g2){
        Analyser a = new Analyser();
        return a.languageIsFinite(g1) == a.languageIsFinite(g2);
    }
    /**
     * @author Adrian Elgyutt
     * @returns null if cfg cointains all words, otherwise return set of words it doesnt contain
     */
    private static Set<String> cfgContainsAllWords(ContextFreeGrammar g1, Set<String> s){
        Set<String> toReturn = new HashSet<String>();
        Transformations transform = new Transformations();
        Analyser a = new Analyser();
        for(String str:s){
            boolean isIn;
            try {
                List<List<Set<String>>> t = a.cyk(transform.transformToCNF(g1), str);
                if(t.isEmpty()) return toReturn;
                if(!"\\e".equals(str))isIn = t.get(0).get(str.length() - 1).contains(transform.transformToCNF(g1).getInitialNonTerminal());
                else isIn = t.get(0).get(0).contains(transform.transformToCNF(g1).getInitialNonTerminal());;
            } catch (Exception ex) {
                isIn = false;
            }
            if (!isIn) toReturn.add(str);
        }
        return toReturn;
    }
}
