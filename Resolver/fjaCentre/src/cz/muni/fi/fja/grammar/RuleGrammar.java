/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.fja.grammar;

import java.util.Map;
import java.util.Set;

import cz.muni.fi.fja.common.Alphabet;
import cz.muni.fi.fja.common.Control;
import cz.muni.fi.fja.common.RuleAlphabetSetControlAbstract;

public class RuleGrammar extends RuleAlphabetSetControlAbstract {

  public RuleGrammar(Control c) {
    super(c);
  }

  public String toString() {
    String s = left.toGrammarString() + " -> ";
    // if (right.isEmpty())
    // return "";

    boolean first = true;
    for (Map.Entry<Alphabet, Set<Control>> r : right.entrySet()) {
      Alphabet a = (Alphabet) r.getKey();
      for (Control c : ((Set<Control>) r.getValue())) {
        if (first) {
          first = false;
        } else {
          s += " | ";
        }
        s += a.toGrammarString() + c.toGrammarString();
      }
    }
    return s;
  }

}
