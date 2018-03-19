/*
 * Alphabet.java
 *
 * Created on 29. srpen 2007, 18:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cz.muni.fi.fja.common;

/**
 * 
 * @author Bronek
 */
public class Alphabet extends Symbol {

  private static final Alphabet EPSILON = new Alphabet("");
  private static final Alphabet EMPTY_SET = new Alphabet(EMPTY_SET_STRING);

  /** Creates a new instance of Alphabet */
  public Alphabet(String n) {
    super(n, true);
  }

  public static Alphabet getEpsilon() {
    return EPSILON;
  }

  public static Alphabet getEmptySet() {
    return EMPTY_SET;
  }

  public boolean isEpsilon() {
    return this == EPSILON;
  }

  public boolean isEmptySet() {
    return this == EMPTY_SET;
  }

  public String toRegString() {
    if (isEmptySet()) {
      return EMPTY_SET_STRING;
    }
    return super.toRegString();
  }

  public boolean equalString(Alphabet a) {
    return toString().equals(a.toString());
  }
}
