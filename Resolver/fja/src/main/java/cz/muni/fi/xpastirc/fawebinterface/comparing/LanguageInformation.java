/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.xpastirc.fawebinterface.comparing;

import cz.muni.fi.RegularLanguage.Automaton.DeterministicFA;
import cz.muni.fi.RegularLanguage.Automaton.NondeterministicFA;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 * @author fafner
 */
public interface LanguageInformation {
    boolean isFinal();
    /**
     * Returns information about the emptiness of the language
     * @return 1 if this language is empty
     *        -1 if this language is universal
     *         0 otherwise
     */
    int isEmpty();
    List<String> getWords();
    boolean isEqual (LanguageInformation other);
    /**
     * Tests language inclusion
     *
     * @param other
     * @return 1 if this language includes other language,
     *        -1 if the other language includes this language
     *         0 otherwise
     */
    int includes(LanguageInformation other);
    LanguageInformation aNotB(LanguageInformation other);
    LanguageInformation BNotA(LanguageInformation other);
    LanguageInformation intersection(LanguageInformation other);
    LanguageInformation union(LanguageInformation other);
    LanguageInformation complement();
    DeterministicFA toDFA();
    NondeterministicFA toNFA();
    void printInformation(PrintWriter out, boolean verbose, int pos);
    String getCharacteristics();
    int getEpscount();
}