package cz.muni.fi.fja;

import cz.muni.fi.fja.common.ModelError;

public class ErrorDevice implements RegularDevice {
  private static ModelError error;
  private static ErrorDevice errorDevice;

  private ErrorDevice() {
    error = ModelError.unrecognizedModel();
  }

  public static RegularDevice getInstance() {
    if (errorDevice == null) {
      errorDevice = new ErrorDevice();
    }
    return errorDevice;
  }

  public boolean equals(Object o) {
    return false;
  }

  public String toString() {
    return error.toString();
  }

  public String toString2() {
      return toString();
  }
  
  public String toStringInOneRow() {
    return error.toString();
  }

  public int getTypeOfDevice() {
    return -1;
  }

  public RegularDevice makeEFA() {
    return this;
  }

  public RegularDevice makeNFA() {
    return this;
  }

  public RegularDevice makeDFA() {
    return this;
  }

  public RegularDevice makeMinimalDFA() {
    return this;
  }

  public RegularDevice makeCanonicDFA() {
    return this;
  }

  public RegularDevice makeMinimalCanonicDFA() {
    return this;
  }

  public boolean containsEpsilon() {
    return false;
  }

  public boolean isTotal() {
    return false;
  }

  public boolean isCanonic() {
    return false;
  }

  public int controlCount() {
    return 0;
  }

  public int alphabetCount() {
    return 0;
  }

  public ModelError getError() {
    return error;
  }

  public boolean isError() {
    return true;
  }

}
