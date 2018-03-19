package cz.muni.fi.fja.fa;

import cz.muni.fi.fja.common.Alphabet;
import cz.muni.fi.fja.common.Control;
import cz.muni.fi.fja.common.InStream;
import cz.muni.fi.fja.common.Rule;

public class DFAReader extends FAReaderAbstract {
  public DFAReader(InStream is) {
    super(is);
  }

  protected boolean getRightSideRule(Rule r, Alphabet a) {
    if (is.getControl()) {
      r.add(a, getControl(is.getLastSymbol()));
      return true;
    }
    return false;
  }

  protected Rule createRule(Control c) {
    return new RuleDFA(c);
  }

}
