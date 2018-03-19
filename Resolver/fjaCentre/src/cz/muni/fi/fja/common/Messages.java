/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.fja.common;

import cz.muni.fi.fja.RegularDevice;

public class Messages {
  public static String enterModel() {
    return "Vstupní model";
  }

  public static String wrongParameter() {
    return "Nerozeznáno zadání úlohy.";
  }

  public static String unrecognizedModel() {
    return "Nerozeznán zadaný model.";
  }

  public static String wrongDevice(boolean who) {
    if (who)
      return "Model odpov&#283;di je chybn&#283; formalizovaný.";
    return "Model zadání je chybn&#283; formalizovaný.";
  }

  // **** EQUAL ****
  public static String equalModel() {
    return "Modely jsou jazykov&#283; ekvivalentní.";
  }

  public static String nonequalModel() {
    return "Modely nejsou jazykov&#283; ekvivalentní.";
  }

  public static String equalAlphabets() {
    return "Modely mají shodné abecedy.";
  }

  public static String nonequalAlphabets() {
    return "Modely nemají shodné abecedy.";
  }

  // **** TOTAL DFA ****
  public static String minimalMustBeTotal(boolean who) {
    if (who)
      return "DFA studenta není minimální, proto&#382;e neni totální.";
    return "DFA u&#269;itele není minimální, proto&#382;e není totální.";
  }

  public static String canonicMustBeTotal(boolean who) {
    if (who)
      return "DFA studenta neni kanonizovany, protoze neni totalni.";
    return "DFA ucitele neni kanonizovany, protoze neni totalni.";
  }

  public static String totalDFA(boolean who) {
    if (who)
      return "DFA studenta je totalni.";
    return "DFA ucitele je totalni.";
  }

  public static String nontotalDFA(boolean who) {
    if (who)
      return "DFA studenta neni totalni.";
    return "DFA ucitele neni totalni.";
  }

  // **** MINIMAL DFA ****
  public static String minimalDFA(boolean who) {
    if (who)
      return "DFA studenta je minimalni.";
    return "DFA ucitele je minimalni.";
  }

  public static String nonminimalDFA(boolean who) {
    if (who)
      return "DFA studenta neni minimalni.";
    return "DFA ucitele neni minimalni.";
  }

  // **** CANONIC DFA ****
  public static String canonicDFA(boolean who) {
    if (who)
      return "DFA studenta je kanonizovany.";
    return "DFA ucitele je kanonizovane.";
  }

  public static String noncanonicDFA(boolean who) {
    if (who)
      return "DFA studenta neni kanonizovany.";
    return "DFA ucitele neni kanonizovany.";
  }

  // **** EPSILON FREE NFA ****
  public static String epsilonFreeNFA(boolean who) {
    if (who)
      return "NFA studenta neobsahuje epsilon kroky.";
    return "NFA ucitele neobsahuje epsilon kroky.";
  }

  public static String nonepsilonFreeNFA(boolean who) {
    if (who)
      return "NFA studenta obrahuje epsilon kroky.";
    return "NFA ucitele obrahuje epsilon kroky.";
  }

  // **** MODEL ALL ****
  public static String recognizedModel(boolean who, RegularDevice d) {
    if (d.isError()) {
      if (who)
        return "Model studenta nebyl rozeznan.";
      return "Model ucitele nebyl rozeznan.";
    }
    String s = "";
    int i = d.getTypeOfDevice();
    if (i < 3) {
      s = "DFA";
    } else if (i < 5) {
      s = "EFA";
    } else if (i == 5) {
      s = "GRA";
    } else {
      s = "REG";
    }
    if (who)
      return "Model studenta byl rozeznan jako " + s + ".";
    return "Model ucitele byl rozeznan jako " + s + ".";
  }

}
