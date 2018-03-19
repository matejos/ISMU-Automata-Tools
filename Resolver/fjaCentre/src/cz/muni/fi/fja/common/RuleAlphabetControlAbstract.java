/*
 * RuleAlphabetControl.java
 *
 * Created on 30. srpen 2007, 16:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cz.muni.fi.fja.common;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public abstract class RuleAlphabetControlAbstract extends RuleAbstract {
  protected Map<Alphabet, Control> right;

  public RuleAlphabetControlAbstract(Control c) {
    super(c);
    right = new HashMap<Alphabet, Control>();
  }

  protected boolean addRule(Alphabet a, Control c) {
    return right.put(a, c) == null;
  }

  public Set<Alphabet> getAlphabet() {
    return right.keySet();
  }

  public Control[] getControl(Alphabet a) {
    return new Control[] { right.get(a) };
  }

  protected void setTestedAbstract() {
    for (Alphabet a : right.keySet())
      a.setInt(1);
    for (Control c : right.values())
      c.getRule().setTested();
  }

}
