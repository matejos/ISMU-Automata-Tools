/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.xpastirc.parsers;

import cz.muni.fi.RegularLanguage.Exceptions.RegLanguageException;
import cz.muni.fi.xpastirc.fja.parsers.AutomatonDefinition;
import cz.muni.fi.xpastirc.fja.parsers.Grammar;
import org.antlr.runtime.RecognitionException;
/**
 * @author Tomas Pastircak - 324693@mail.muni.cz
 * @version 10.5.2011
 */
public class GrammarParser {
    private String grammar;
    public GrammarParser(String grammar){
        this.grammar=grammar;
    }

    public AutomatonDefinition match(boolean requireNormal) throws RegLanguageException{
        AutomatonDefinition toReturn;
        try {
            toReturn = Grammar.match(grammar);
        } catch (RecognitionException ex) {
            throw new RegLanguageException("Chyba v zadání gramatiky: " + ex.getMessage());
        }
        if (!toReturn.getNormal() && requireNormal)
            throw new RegLanguageException("Gramatika není normovaná.");
        return toReturn;
    }

}
