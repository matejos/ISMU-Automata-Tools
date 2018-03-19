package cz.muni.fi.fja;

import cz.muni.fi.fja.common.ModelError;

;

public abstract class DeviceAbstract implements RegularDevice {
  private ModelError error;
  protected int controlCount; // number of Controls
  protected int alphabetCount; // number of Alphabets
  protected int typeOfDevice;

  /*
   * 0 - CAN 1 - MIN 2 - DFA 3 - EFR 4 - NFA 5 - GRA 6 - REG
   */

  public DeviceAbstract(int typeOfDevice) {
    this.typeOfDevice = typeOfDevice;
  }

  public int getTypeOfDevice() {
    return typeOfDevice;
  }

  public RegularDevice makeEFA() {
    return this;
  }

  public RegularDevice makeNFA() {
    if (getTypeOfDevice() <= 3) {
      return this;
    }

    return makeEFA().makeNFA();
  }

  public RegularDevice makeDFA() {
    if (getTypeOfDevice() <= 2) {
      return this;
    }
    return makeNFA().makeDFA();
  }

  public RegularDevice makeMinimalDFA() {
    if (getTypeOfDevice() == 1) {
      return this;
    }
    return makeDFA().makeMinimalDFA();
  }

  public RegularDevice makeCanonicDFA() {
    if (getTypeOfDevice() == 0) {
      return this;
    }
    return makeMinimalDFA().makeCanonicDFA();
  }

  public RegularDevice makeMinimalCanonicDFA() {
    if (getTypeOfDevice() == 1) {
      return makeCanonicDFA();
    }
    return makeMinimalDFA().makeCanonicDFA();
  }

  public boolean containsEpsilon() {
    if (getTypeOfDevice() <= 3) {
      return false;
    }
    return true;
  }

  // need implement it!!! and change DFA!!!
  public boolean isTotal() {
    return false;
  }

  // need implement it!!! and change DFA!!!

  public boolean isCanonic() {
    if (getTypeOfDevice() == 0) {
      return true;
    }
    return false;
  }

  public int controlCount() {
    return controlCount;
  }

  public int alphabetCount() {
    return alphabetCount;
  }

  public ModelError getError() {
    return error;
  }

  protected void setError(ModelError error) {
    this.error = error;
  }

  public boolean isError() {
    return error != null;
  }

  protected abstract String deviceToString(boolean inOneRow);
  protected abstract String deviceToString2(boolean inOneRow);

  public String toStringInOneRow() {
    if (isError()) {
      return getError().toString();
    }
    String s = deviceToString(true);
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
    String s = deviceToString(false);
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
    String s = deviceToString2(false);
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
  public boolean equals(Object o) {
    if (!(o instanceof RegularDevice)) {
      return false;
    }

    RegularDevice d = (RegularDevice) o;
    return this.makeDFA().equals(d.makeDFA());
  }
}
