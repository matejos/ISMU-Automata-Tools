package cz.muni.fi.fja.fa;

import cz.muni.fi.fja.common.Alphabet;
import cz.muni.fi.fja.common.Control;
import cz.muni.fi.fja.common.InStream;
import cz.muni.fi.fja.common.Rule;

/**
 * 
 * @author Bronek
 */
public class EFAReader extends FAReaderAbstract {

  public EFAReader(InStream is) {
    super(is);
  }

  protected Rule createRule(Control c) {
    return new RuleEFA(c);
  }

  protected boolean getRightSideRule(Rule r, Alphabet a) {
    if (is.getExpectedSymbol('{')) {
      // System.out.print("FINAL SYMBOL = {");
      while (is.getControl() && !is.isEOF()) {
        r.add(a, getControl(is.getLastSymbol()));
        // System.out.println(is.getLastSymbol());
        if (is.getChar() == ',') {
          is.getExpectedSymbol(',');
          // System.out.print(',');
        } else {
          if (is.getExpectedSymbol('}')) {
            return true;
            // System.out.println(')');
          } else {
            break;
          }
        }
      }
      if (is.isEOF()) {
        is.getExpectedSymbol('}');
      }
    }
    return false;
  }

}
