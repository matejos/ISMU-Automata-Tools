package cz.muni.fi.fja.common;

import java.util.Map;
import java.util.HashMap;

/**
 * 
 * @author Bronek
 */
public abstract class ModelReaderAbstract implements ModelReader {
  protected Map<String, Control> controls;
  protected Map<String, Alphabet> alphabets;
  protected int controlCount = 0;
  protected int alphabetCount = 0;
  protected InStream is;

  protected Control startSymbol = null;

  private ModelError error = null;

  /** Creates a new instance of ModelAbstract */
  public ModelReaderAbstract(InStream is) {
    controls = new HashMap<String, Control>();
    alphabets = new HashMap<String, Alphabet>();

    error = is.getError();
    this.is = is;
  }

  public abstract void start();

  public Rule[] getAllControl() {
    Rule[] ruleArray;
    if (error == null) {
      ruleArray = new Rule[controlCount];
      ruleArray[0] = startSymbol.getRule();
      int startInt = startSymbol.getInt();
      for (Control c : controls.values()) {
        if (startInt < c.getInt()) {
          ruleArray[c.getInt()] = c.getRule();
        } else if (startSymbol != c) {
          ruleArray[c.getInt() + 1] = c.getRule();
        }
      }
    } else {
      ruleArray = new Rule[0];
    }
    return ruleArray;
  }

  public Alphabet[] getAllAlphabet() {
    Alphabet[] alphaArray;
    if (!isError()) {
      alphaArray = new Alphabet[alphabetCount];
      for (Alphabet a : alphabets.values()) {
        alphaArray[a.getInt()] = a;
      }
    } else {
      alphaArray = new Alphabet[0];
    }
    return alphaArray;
  }

  public Control[] getAllFinal() {
    return null;
  }

  public boolean isError() {
    return error != null;
  }

  public ModelError getError() {
    return error;
  }

  protected ModelError setError(String s) {
    return setError(ModelError.incorrectEnterDataError(is.getLine(), is
        .getPositionInLine(), s));
  }

  protected ModelError setError(ModelError e) {
    if (!isError()) {
      error = e;
    }
    return error;
  }

  protected void getErrorFromIs() {
    if (!isError())
      error = is.getError();
  }

  public ModelError finishedAdding() {
    if (error != null)
      return error;

    if (startSymbol == null) {
      return setError(ModelError.startSymbolNotExist());
    }

    return null;
  }

  public void setStartSymbol(String n) {
    startSymbol = getControl(n);
  }

  /*
   * Return Symbol with String n. If Symbol doesn't exist will be created
   */
  protected abstract Control createControl(String n);

  protected abstract Alphabet createAlphabet(String n);

  protected abstract Rule createRule(Control c);

  protected Control getControl(String n) {
    assert n.length() != 0;
    Control c = controls.get(n);
    if (c == null) {
      c = createControl(n);
      c.setInt(controlCount);
      controlCount++;
      controls.put(n, c);
      createRule(c);
    }
    return c;
  }

  protected Alphabet getAlphabet(String n) {
    Alphabet a = alphabets.get(n);
    if (a == null) {
      if (n.length() == 0) {
        a = Alphabet.getEpsilon();
      } else {
        a = createAlphabet(n);
      }
      a.setInt(alphabetCount);
      alphabetCount++;
      alphabets.put(n, a);
    }
    return a;
  }

  public String toString() {
    if (error != null)
      return error.getMessage();

    String st = "";
    if (startSymbol != null)
      st = startSymbol.getRule() + "\n";
    for (Control c : controls.values()) {
      if (c != startSymbol) {
        st += c.getRule() + "\n";
      }
    }
    return st;
  }

}
