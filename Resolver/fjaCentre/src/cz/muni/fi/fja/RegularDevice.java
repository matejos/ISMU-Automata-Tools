package cz.muni.fi.fja;

public interface RegularDevice extends Device {

  int getTypeOfDevice();

  RegularDevice makeEFA();

  RegularDevice makeNFA();

  RegularDevice makeDFA();

  RegularDevice makeMinimalDFA();

  RegularDevice makeCanonicDFA();

  RegularDevice makeMinimalCanonicDFA();

  boolean containsEpsilon();

  boolean isTotal();

  boolean isCanonic();

  int controlCount();

  int alphabetCount();
  
  String toString2();
}
