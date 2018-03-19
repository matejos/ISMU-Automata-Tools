package cz.muni.fi.fja.fa;

import java.util.HashSet;
import java.util.Set;

import cz.muni.fi.fja.common.Alphabet;
import cz.muni.fi.fja.common.Control;
import cz.muni.fi.fja.common.InStream;
import cz.muni.fi.fja.common.ModelError;
import cz.muni.fi.fja.common.ModelReaderAbstract;
import cz.muni.fi.fja.common.Rule;

public abstract class FAReaderAbstract extends ModelReaderAbstract {

  static char[] initSymbol = new char[] { 'i', 'n', 'i', 't' };

  protected Set<Control> finalControl;

  public FAReaderAbstract(InStream is) {
    super(is);
    finalControl = new HashSet<Control>();
    if (!isError()) {
      start();
    }
    this.is = null;
    finishedAdding();
  }

  public void start() {
    is.skipAllWhiteSpaces();
    while (!isError() && !is.isEOF()) {
      switch (is.getChar()) {
      case '(':
        getRule();
        break;
      case 'i':
        getInit();
        break;
      case 'F':
        getFinal();
        break;
      default:
        setError(ModelError.unexpectedSymbol(is.getChar()));
      }
    }
    return;
  }

  protected boolean getInit() {
    if (is.getExpectedSymbol(initSymbol) && is.getExpectedSymbol('=')
        && is.getControl()) {
      setStartSymbol(is.getLastSymbol());
      return true;
    } else {
      getErrorFromIs();
    }
    return false;
  }

  protected boolean getRule() {
    if (is.getExpectedSymbol('(') && is.getControl()) {
      Rule r = getControl(is.getLastSymbol()).getRule();
      if (startSymbol == null) {
        startSymbol = r.getControl();
      }
      if (is.getExpectedSymbol(',') && is.getAlphabet()) {
        Alphabet a = getAlphabet(is.getLastSymbol());
        if (is.getExpectedSymbol(')') && is.getExpectedSymbol('=')) {
          if (getRightSideRule(r, a)) {
            return true;
          }
        }
      }
    }
    getErrorFromIs();
    return false;
  }

  protected boolean getFinal() {
    if (is.getExpectedSymbol('F') && is.getExpectedSymbol('=')
        && is.getExpectedSymbol('{') && !is.isEOF()) {
      if (is.getChar() == '}') {
        is.getExpectedSymbol('}');
        return true;
      } else {
        while (is.getControl() && !is.isEOF()) {
          finalControl.add(getControl(is.getLastSymbol()));
          if (is.getChar() == ',') {
            is.getExpectedSymbol(',');
          } else {
            if (is.getExpectedSymbol('}')) {
              return true;
            } else {
              break;
            }
          }
        }
        if (is.isEOF()) {
          is.getExpectedSymbol('}');
        }
      }

    }
    getErrorFromIs();
    return false;
  }

  protected abstract boolean getRightSideRule(Rule r, Alphabet a);

  public ModelError finishedAdding() {
    return super.finishedAdding();
  }

  public Control[] getAllFinal() {
    if (isError()) {
      return new Control[0];
    }
    return finalControl.toArray(new Control[0]);
  }

  protected Control createControl(String n) {
    return new Control(n);
  }

  protected Alphabet createAlphabet(String n) {
    return new Alphabet(n);
  }

  public String toString() {
    if (isError())
      return getError().toString();

    String s = "";
    if (startSymbol != null)
      s += "init=" + startSymbol.toFAString() + "\n";
    s += super.toString();
    s += "F={";
    boolean first = true;
    for (Control c : finalControl) {
      if (first) {
        first = false;
      } else {
        s += ",";
      }
      s += c.toFAString();
    }
    s += "}\n";
    return s;
  }

}
