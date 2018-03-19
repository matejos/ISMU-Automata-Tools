/*
 * RuleDFA.java
 *
 * Created on 30. srpen 2007, 16:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cz.muni.fi.fja.fa;

import java.util.Map;

import cz.muni.fi.fja.common.Alphabet;
import cz.muni.fi.fja.common.Control;
import cz.muni.fi.fja.common.Rule;
import cz.muni.fi.fja.common.RuleAlphabetControlAbstract;
import cz.muni.fi.fja.common.Symbol;

/**
 * 
 * @author Bronek
 */
public class RuleDFA extends RuleAlphabetControlAbstract implements Rule {

  /** Creates a new instance of RuleDFA */
  public RuleDFA(Control c) {
    super(c);
  }

  public String toString() {
    if (right.isEmpty()) {
      return "";
    }
    String s = "";
    for (Map.Entry<Alphabet, Control> r : right.entrySet()) {
      s += "(" + left.toFAString() + "," + ((Symbol) r.getKey()).toFAString()
          + ")=" + ((Symbol) r.getValue()).toFAString() + " ";
    }
    return s;
  }

}
