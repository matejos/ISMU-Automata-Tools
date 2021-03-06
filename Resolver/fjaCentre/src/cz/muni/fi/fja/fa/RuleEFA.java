package cz.muni.fi.fja.fa;

import java.util.Map;
import java.util.Set;

import cz.muni.fi.fja.common.Alphabet;
import cz.muni.fi.fja.common.Control;
import cz.muni.fi.fja.common.RuleAlphabetSetControlAbstract;
import cz.muni.fi.fja.common.Symbol;

public class RuleEFA extends RuleAlphabetSetControlAbstract {

  public RuleEFA(Control c) {
    super(c);
  }

  public String toString() {
    if (right.isEmpty()) {
      return "";
    }
    String s = "";
    for (Map.Entry<Alphabet, Set<Control>> r : right.entrySet()) {
      s += "(" + left.toFAString() + "," + ((Symbol) r.getKey()).toFAString()
          + ")={";
      boolean first = true;
      for (Control c : ((Set<Control>) r.getValue())) {
        if (first) {
          first = false;
        } else {
          s += ",";
        }
        s += c.toFAString();
      }
      s += "} ";
    }
    return s;
  }
}
