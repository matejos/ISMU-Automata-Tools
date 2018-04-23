package cz.muni.fi.fja.fa;

import cz.muni.fi.fja.RegularDevice;
import cz.muni.fi.fja.common.ModelError;

public class NFAFromDFA implements RegularDevice {
  private DFA dfa;

  public NFAFromDFA(RegularDevice d) {
    dfa = (DFA) d.makeDFA();
  }

  public int getTypeOfDevice() {
    return 3;
  }

  public RegularDevice makeEFA() {
    return dfa.makeEFA();
  }

  public RegularDevice makeNFA() {
    return dfa.makeNFA();
  }

  public RegularDevice makeDFA() {
    return dfa.makeDFA();
  }

  public RegularDevice makeMinimalDFA() {
    return dfa.makeMinimalDFA();
  }

  public RegularDevice makeCanonicDFA() {
    return dfa.makeCanonicDFA();
  }

  public RegularDevice makeMinimalCanonicDFA() {
    return dfa.makeMinimalCanonicDFA();
  }

  public boolean containsEpsilon() {
    return dfa.containsEpsilon();
  }

  public boolean isTotal() {
    return dfa.isTotal();
  }

  public boolean isCanonic() {
    return dfa.isCanonic();
  }

  public int controlCount() {
    return dfa.controlCount();
  }

  public int alphabetCount() {
    return dfa.alphabetCount();
  }

  public ModelError getError() {
    return dfa.getError();
  }

  public boolean isError() {
    return dfa.isError();
  }

  public String toStringInOneRow() {
    if (isError()) {
      return getError().toString();
    }
    String s = dfa.DFAToStringOrToStringNFA(true, true);
    StringBuffer sb = new StringBuffer(s.length() * 2);
    for (int i = 0, l = s.length(); i < l; i++) {
      if (s.charAt(i) == '<') {
        sb.append("&lt;");
      } else if (s.charAt(i) == '>') {
        sb.append("&gt;");
      } else {
        sb.append(s.charAt(i));
      }
    }
    return sb.toString();
  }

  public String toString() {
    if (isError()) {
      return getError().toString();
    }
    String s = dfa.deviceToString(false);
    StringBuffer sb = new StringBuffer(s.length() * 2);
    for (int i = 0, l = s.length(); i < l; i++) {
      if (s.charAt(i) == '<') {
        sb.append("&lt;");
      } else if (s.charAt(i) == '>') {
        sb.append("&gt;");
      } else {
        sb.append(s.charAt(i));
      }
    }
    return sb.toString();
  }
  public String toString2() {
    if (isError()) {
      return getError().toString();
    }
    String s = dfa.deviceToString2(false);
    StringBuffer sb = new StringBuffer(s.length() * 2);
    for (int i = 0, l = s.length(); i < l; i++) {
      if (s.charAt(i) == '<') {
        sb.append("&lt;");
      } else if (s.charAt(i) == '>') {
        sb.append("&gt;");
      } /*start*/else if (s.charAt(i) == '\u02C2') {
          sb.append('<');
      } else if (s.charAt(i) == '\u02C3') {
          sb.append('>');
      } /*end*/else {
        sb.append(s.charAt(i));
      }
    }
    return sb.toString();
  }
}
