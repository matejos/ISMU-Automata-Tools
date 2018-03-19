/*
 * ControlSymbol.java
 *
 * Created on 29. srpen 2007, 20:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cz.muni.fi.fja.common;

/**
 * 
 * @author Bronek
 */
public class Control extends Symbol {
  private static final Control NULL_CONTROL = new Control("");
  private Rule rule;
  private boolean isFinal;

  /** Creates a new instance of ControlSymbol */
  public Control(String n) {
    super(n, false);
  }

  public Control(int i) {
    super(i, false);
  }

  public Rule getRule() {
    return rule;
  }

  public Rule setRule(Rule r) {
    return rule = r;
  }

  public String toGrammarString() {
    if (isNullControl()) {
      return "";
    }
    return super.toGrammarString();
  }

  public static Control getNullControl() {
    return NULL_CONTROL;
  }

  public boolean isNullControl() {
    return this == NULL_CONTROL;
  }

  public boolean isFinal() {
    return isFinal;
  }

  public void setFinal() {
    isFinal = true;
  }

}
