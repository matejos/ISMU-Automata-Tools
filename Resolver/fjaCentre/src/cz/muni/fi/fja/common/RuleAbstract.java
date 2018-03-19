/*
 * RuleAbstract.java
 *
 * Created on 30. srpen 2007, 16:18
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cz.muni.fi.fja.common;

import java.util.Set;
import java.util.HashSet;

/**
 * 
 * @author Bronek
 */
public abstract class RuleAbstract implements Rule {
  protected static final Set<Control> nullSet = new HashSet<Control>();
  private Set<Control> childs;

  protected Control left;

  private boolean epsilon = false;
  private boolean alphabet = false;
  private boolean control = false;

  public RuleAbstract(Control c) {
    left = c;
    left.setRule(this);
    childs = new HashSet<Control>();
  }

  public abstract Set<Alphabet> getAlphabet();

  protected Set<Control> getControlChilds() {
    return childs;
  }

  public Control getControl() {
    return left;
  }

  protected abstract void setTestedAbstract();

  public void setTested() {
    if (left.getInt() >= 0) {
      return;
    }
    left.setInt(0);
    setTestedAbstract();
  }

  public boolean isTested() {
    return left.getInt() >= 0;
  }

  protected abstract boolean addRule(Alphabet a, Control c);

  public boolean add(Alphabet a) {
    if (a.isEpsilon()) {
      epsilon = true;
    }
    alphabet = true;
    return addRule(a, Control.getNullControl());
  }

  public boolean add(Alphabet a, Control c) {
    control = true;
    childs.add(c);
    return addRule(a, c);
  }

  public boolean add(Alphabet a, Rule r) {
    return addRule(a, r.getControl());
  }

  public boolean containsEpsilon() {
    return epsilon;
  }

  public boolean containsAlphabet() {
    return alphabet;
  }

  public boolean containsControl() {
    return control;
  }

  public Rule convertToNFA(Control c) {
    return this;
  }

  public boolean equals(Object o) {
    if (!(o instanceof Rule)) {
      return false;
    }
    if (o == this) {
      return true;
    }
    RuleAbstract r = (RuleAbstract) o;
    return left == r.left;
  }

  public int hashCode() {
    return left.hashCode();
  }

  public String toString() {
    return left + " -> " + childs;
  }

}
