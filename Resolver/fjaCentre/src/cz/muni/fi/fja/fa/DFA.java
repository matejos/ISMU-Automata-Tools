package cz.muni.fi.fja.fa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.muni.fi.fja.DeviceAbstract;
import cz.muni.fi.fja.common.Alphabet;
import cz.muni.fi.fja.common.Control;
import cz.muni.fi.fja.common.InStream;
import cz.muni.fi.fja.common.ModelError;
import cz.muni.fi.fja.common.Rule;
import cz.muni.fi.fja.common.Symbol;

public class DFA extends DeviceAbstract {
  private int[][] tableRule;
  private int[] finalControl; // must be sorted
  private Alphabet[] alphabets;
  private Control[] controls;
  private int blackHole = -1; // if is BlackHole -1 - DFA is total

  DFA(int[][] rules, Alphabet[] alphas, Control[] contros, int[] finals,
      int type) {
    super(type);
    tableRule = rules;
    alphabets = alphas;
    finalControl = finals;
    controls = contros;

    alphabetCount = alphabets.length;
    controlCount = tableRule.length;
    blackHole = -1;
  }

  DFA(ModelError error) {
    super(-1);
    setError(error);
  }

  public DFA(InStream is) {
    super(2);
    DFAReader dfa = new DFAReader(is);
    setError(dfa.getError());
    if (!isError()) {
      alphabets = dfa.getAllAlphabet();
      // Arrays.sort(alphabets);
      alphabetCount = alphabets.length;

      Rule[] rules = dfa.getAllControl();
      controlCount = rules.length + 1;
      tableRule = new int[controlCount][alphabetCount];
      blackHole = controlCount - 1;
      for (int i = 0; i < alphabetCount; i++) {
        alphabets[i].setInt(i);
        tableRule[blackHole][i] = blackHole;
      }
      for (int i = 0; i < blackHole; i++)
        rules[i].getControl().setInt(i);
      int checkForBlackHole = -1;
      for (int i = 0; i < blackHole; i++) {
        Rule r = rules[i];
        Arrays.fill(tableRule[i], blackHole);
        for (Alphabet a : r.getAlphabet())
          tableRule[i][a.getInt()] = r.getControl(a)[0].getInt();
        if (checkForBlackHole < 0) {
          for (int j = 0; j < alphabetCount; j++) {
            if (tableRule[i][j] == blackHole) {
              checkForBlackHole = blackHole;
            }
          }
        }
      }
      blackHole = checkForBlackHole;
      if (blackHole < 0) {
        controlCount--;
      }
      controls = new Control[controlCount];
      for (int i = 0; i < controlCount; i++) {
        if (i != blackHole) {
          controls[i] = rules[i].getControl();
        } else {
          controls[i] = new Control("blackHole");
        }
      }

      Control[] finals = dfa.getAllFinal();
      finalControl = new int[finals.length];
      int h = 0;
      for (Control c : finals) {
        finalControl[h] = c.getInt();
        h++;
      }
      Arrays.sort(finalControl);
    }
  }

  public boolean isTotal() {
    if (isError()) {
      return false;
    }
    return blackHole < 0;
  }

  public DFA makeMinimalDFA() {
    return removeUnavailableState().createMinimalDFA();
  }

  private DFA createMinimalDFA() {
    if (isError())
      return new DFA(getError());

    if (finalControl.length == 0)
      return createEmptyFA();
    if (finalControl.length == controlCount) {
      DFA minimal = createEmptyFA();
      minimal.finalControl = new int[] { 0 };
      return minimal;
    }

    List<int[]> minDFA = new ArrayList<int[]>();
    int[][] temporaryRules = new int[controlCount][alphabetCount];
    int[] convertStateToClass = new int[controlCount];
    Map<OldRuleToNew, OldRuleToNew> addedClasses = new HashMap<OldRuleToNew, OldRuleToNew>();

    OldRuleToNew finalClass = null;
    OldRuleToNew notFinalClass = null;
    int pomFinal = 1;
    int pomNotFinal = 0;
    if (finalControl[0] == 0) {
      pomFinal = 0;
      pomNotFinal = 1;
    }
    for (int i = 0, j = 0, pom = finalControl.length; i < controlCount; i++) {
      convertStateToClass[i] = pomNotFinal;
      if (j < pom) {
        if (finalControl[j] == i) {
          if (finalClass == null)
            finalClass = new OldRuleToNew(pomFinal, pomFinal, temporaryRules[i]);
          convertStateToClass[i] = pomFinal;
          j++;
        } else if (notFinalClass == null) {
          notFinalClass = new OldRuleToNew(pomNotFinal, pomNotFinal,
              temporaryRules[i]);
        }
      } else if (notFinalClass == null) {
        notFinalClass = new OldRuleToNew(pomNotFinal, pomNotFinal,
            temporaryRules[i]);
      }
    }
    addedClasses.put(finalClass, finalClass);
    addedClasses.put(notFinalClass, notFinalClass);
    if (pomFinal == 0)
      minDFA.add(finalClass.getRule());
    minDFA.add(notFinalClass.getRule());
    if (pomFinal == 1)
      minDFA.add(finalClass.getRule());

    while (!addedClasses.isEmpty()) {
      addedClasses.clear();
      updateRules(temporaryRules, convertStateToClass);
      for (int i = 0; i < controlCount; i++) {
        int actualClass = convertStateToClass[i];
        if (!Arrays.equals(temporaryRules[i], minDFA.get(actualClass))) {
          OldRuleToNew actualRule = new OldRuleToNew(actualClass,
              minDFA.size(), temporaryRules[i]);
          if (addedClasses.containsKey(actualRule)) {
            convertStateToClass[i] = addedClasses.get(actualRule).getNewClass();
          } else {
            addedClasses.put(actualRule, actualRule);
            convertStateToClass[i] = minDFA.size();
            minDFA.add(temporaryRules[i]);
          }
        }
      }
    }

    Set<Integer> finalSet = new HashSet<Integer>();
    for (int i = 0; i < finalControl.length; i++) {
      finalSet.add(new Integer(convertStateToClass[finalControl[i]]));
    }
    Integer[] pom = finalSet.toArray(new Integer[0]);
    int[] finalClasses = new int[pom.length];
    for (int i = 0; i < pom.length; i++) {
      finalClasses[i] = pom[i].intValue();
    }
    Arrays.sort(finalClasses);
    temporaryRules = new int[minDFA.size()][];
    for (int i = 0, l = minDFA.size(); i < l; i++) {
      temporaryRules[i] = minDFA.get(i);
    }
    DFA minimal = new DFA(temporaryRules, alphabets, null, finalClasses, 1);

    return minimal;
  }

  private DFA removeUnavailableState() {
    if (isError())
      return new DFA(getError());

    int[] listOfAvailableStates = getListOfAvailabeStates();
    int[] markAvailableStates = new int[controlCount];
    Arrays.fill(markAvailableStates, -1);
    int newControlCount = 0;
    while (newControlCount < controlCount
        && listOfAvailableStates[newControlCount] >= 0) {
      markAvailableStates[listOfAvailableStates[newControlCount]] = newControlCount;
      newControlCount++;
    }
    // markAvailableStates = all available states > -1
    // listOfAvailableStates = list of available states

    int[][] temporaryTable = new int[newControlCount][alphabetCount];
    for (int i = 0; i < newControlCount; i++) {
      for (int j = 0; j < alphabetCount; j++) {
        temporaryTable[i][j] = markAvailableStates[tableRule[listOfAvailableStates[i]][j]];
      }
    }
    int f = 0; // count available final states
    for (int i = 0; i < finalControl.length; i++) {
      if (markAvailableStates[finalControl[i]] >= 0) {
        f++;
      }
    }
    int[] finals = new int[f]; // create new array with available final states
    for (int i = 0, j = 0; i < finalControl.length; i++) {
      if (markAvailableStates[finalControl[i]] >= 0) {
        finals[j] = markAvailableStates[finalControl[i]];
        j++;
      }
    }
    DFA availables = new DFA(temporaryTable, alphabets, null, finals, 2);
    return availables;
  }

  public boolean isCanonic() {
    if (isError()) {
      return false;
    }

    if (controls == null || controls.length == 0) {
      renameControls();
    }

    if (getListOfAvailabeStates()[controlCount - 1] == -1) {
      return false;
    }

    int controlsWithoutBlackHole = blackHole >= 0 ? controlCount - 1
        : controlCount;

    // alphabetSorted obsahuje indexy znaku z alphabets setridene
    int[] alphabetSorted = sortSymbols(alphabets, alphabetCount);
    int[] controlSortedToTable = sortSymbols(controls, controlsWithoutBlackHole);
    int[] controlTableToSorted = new int[controlsWithoutBlackHole];
    for (int i = 0; i < controlsWithoutBlackHole; i++) {
      controlTableToSorted[controlSortedToTable[i]] = i;
    }
    if (controlSortedToTable[0] != 0) {
      return false; // inicialni stav musi byt prvni v poradi
    }
    for (int i = 0; i < controlsWithoutBlackHole; i++) {
      if (!controls[controlSortedToTable[i]].toString().equals(
          getNameControl(i))) {
        return false; // stavy musi byt pojmenovany A,B... nebo 0,1...
      }
    }

    // aktualni stav
    int newState = 1;
    for (int i = 0; i < controlsWithoutBlackHole; i++) {
      // if (newState < controlCount)
      // System.out.println("newState=" + newState + "=" +
      // controls[controlSortedToTable[newState]]);
      // if (i == newState) {
      // newState++;
      // }
      int actualState = controlSortedToTable[i];
      // printInfo(controlSortedToTable, actualState);
      // if (newState < controlCount)
      // System.out.println("newState=" + newState + "=" +
      // controls[controlSortedToTable[newState]]);
      for (int j = 0; j < alphabetCount; j++) {
        int testedState = tableRule[actualState][alphabetSorted[j]];
        if (testedState != blackHole) {
          if (controlTableToSorted[testedState] >= newState) {
            if (controlTableToSorted[testedState] > newState) {
              return false;
            }
            newState++;
          }
        }
      }
    }

    return true;
  }

  /*
   * private void printInfo(int[] sorted, int c) { System.out.print("Stav : " +
   * getInfo(sorted[c]) + " - "); System.out.print("Prechody:"); for (int i = 0;
   * i < alphabetCount; i++) { System.out.print(" " + alphabets[i] + "->" +
   * getInfo(tableRule[sorted[c]][i])); } System.out.println(); } private String
   * getInfo(int control) { return controls[control].toString(); }
   */

  private int[] sortSymbols(Symbol[] symbols, int length) {
    int[] sorted = new int[length];
    Symbol[] pomSymbol = new Symbol[length];
    for (int i = 0; i < length; i++) {
      pomSymbol[i] = symbols[i];
    }
    Arrays.sort(pomSymbol);
    for (int i = 0; i < length; i++) {
      Symbol s = pomSymbol[i];
      for (int j = 0; j < length; j++) {
        if (symbols[j].equals(s)) {
          sorted[i] = j;
          break;
        }
      }
    }
    return sorted;
  }

  private int[] getListOfAvailabeStates() {
    int[] listOfAvailableStates = new int[controlCount];
    boolean[] markAvailableStates = new boolean[controlCount];
    Arrays.fill(listOfAvailableStates, -1);
    // Arrays.fill(markAvailableStates, false);
    listOfAvailableStates[0] = 0;
    markAvailableStates[0] = true;
    int newControlCount = 1;
    for (int i = 0; i < newControlCount; i++) {
      int actualState = listOfAvailableStates[i];
      for (int j = 0; j < alphabetCount; j++) {
        int testedState = tableRule[actualState][j];
        if (!markAvailableStates[testedState]) {
          // jestlize tento stav nebyl oznacen, tak jej oznac a pridej do listu!
          markAvailableStates[testedState] = true;
          listOfAvailableStates[newControlCount] = testedState;
          newControlCount++;
        }
      }
    }
    return listOfAvailableStates;
  }

  public DFA makeCanonicDFA() {
    if (isError())
      return new DFA(getError());

    for (int i = 0, l = alphabets.length; i < l; i++) {
      alphabets[i].setInt(i);
    }
    Arrays.sort(alphabets);

    int[][] temporaryTable = new int[controlCount][alphabetCount];
    for (int i = 0; i < controlCount; i++) {
      for (int j = 0; j < alphabetCount; j++) {
        temporaryTable[i][j] = tableRule[i][alphabets[j].getInt()];
      }
    }

    int[][] canonTable = new int[controlCount][alphabetCount];
    int[] canonFinal = new int[finalControl.length];
    int[] convertOldToNew = new int[controlCount];
    int[] convertNewToOld = new int[controlCount];

    int countOfNewStates = 1;
    for (int i = 0; i < controlCount; i++) {
      int[] actualOldState = temporaryTable[convertNewToOld[i]];
      for (int j = 0; j < alphabetCount; j++) {
        if (convertOldToNew[actualOldState[j]] == 0 && actualOldState[j] != 0) {
          convertOldToNew[actualOldState[j]] = countOfNewStates;
          ;
          convertNewToOld[countOfNewStates] = actualOldState[j];
          countOfNewStates++;
        }
        canonTable[i][j] = convertOldToNew[actualOldState[j]];
      }
    }

    for (int i = 0, l = canonFinal.length; i < l; i++) {
      canonFinal[i] = convertOldToNew[finalControl[i]];
    }
    Arrays.sort(canonFinal);
    DFA canonDFA = new DFA(canonTable, alphabets, null, canonFinal, 0);
    return canonDFA;
  }

  private void updateRules(int[][] rules, int[] convertArray) {
    for (int i = 0; i < controlCount; i++) {
      for (int j = 0; j < alphabetCount; j++) {
        rules[i][j] = convertArray[tableRule[i][j]];
      }
    }
  }

  private DFA createEmptyFA() {
    int[][] rules = new int[1][alphabetCount];
    for (int i = 0; i < alphabetCount; i++)
      rules[0][i] = 0;
    DFA emptyFA = new DFA(rules, alphabets, null, new int[0], 1);
    return emptyFA;
  }

  public boolean equals(Object o) {
    if (!(o instanceof DFA))
      return false;

    DFA dfa = ((DFA) o).makeMinimalDFA().makeCanonicDFA();

    if (isError() || dfa.isError())
      return false;

    // System.out.println("jo porovnava");
    // System.out.println("teacher" + this.toString());
    // System.out.println("student" + dfa.toString());

    return this.makeMinimalDFA().makeCanonicDFA().semanticEqual(dfa);
  }

  private boolean semanticEqual(DFA dfa) {
    if (controlCount != dfa.controlCount || alphabetCount != dfa.alphabetCount
        || !Arrays.equals(finalControl, dfa.finalControl))
      return false;

    for (int i = 0; i < alphabetCount; i++)
      if (!alphabets[i].toString().equals(dfa.alphabets[i].toString()))
        return false;

    for (int i = 0; i < controlCount; i++)
      if (!Arrays.equals(tableRule[i], dfa.tableRule[i]))
        return false;

    return true;

  }

  /*
   * private boolean algorithmicEqual(DFA dfa) { QuickEqual qe = new
   * QuickEqual(this, dfa); return qe.result(); }
   */

  protected String DFAToStringOrToStringNFA2(boolean inOneRow, boolean nfa) {
      StringBuffer sb;
    if (inOneRow){
    sb = new StringBuffer(13 + (11 * alphabetCount + 1)
        * controlCount + 3 * finalControl.length);
    if (controls == null || controls.length == 0) {
      renameControls();
    }
    if (!inOneRow) {
      sb.append("init=" + controls[0].toFAString() + "\n");
    }
    for (int i = 0; i < controlCount && i != blackHole; i++) {
      boolean added = false;
      for (int j = 0; j < alphabetCount; j++) {
        if (tableRule[i][j] != blackHole) {
          sb.append("(" + controls[i].toFAString() + ","
              + alphabets[j].toFAString() + ")=");
          if (nfa) {
            sb.append("{" + controls[tableRule[i][j]].toFAString() + "}");
          } else {
            sb.append(controls[tableRule[i][j]].toFAString());
          }
          if (!inOneRow) {
            sb.append(" ");
          }
          added = true;
        }
      }
      if (added && !inOneRow) {
        sb.append("\n");
      }
    }
    if (inOneRow) {
      sb.append(" ");
    }
    sb.append("F={");
    boolean first = true;
    for (int i = 0; i < finalControl.length; i++) {
      if (first) {
        first = false;
      } else {
        sb.append(",");
      }
      sb.append(controls[finalControl[i]].toFAString());
    }
    sb.append("}");
    if (!inOneRow) {
      sb.append("\n");
    }
    }else{
    sb = new StringBuffer(13+(20*(alphabetCount+1)+1)*(controlCount+1)+40+3*finalControl.length);
    if (controls == null || controls.length == 0) {
      renameControls();
    }
    //start
    sb.append('\u02C2' + "center" + '\u02C3');
    sb.append('\u02C2' + "table  class=\"automatonTable\" " + '\u02C3');
    sb.append('\u02C2' + "tr" + '\u02C3');
    sb.append('\u02C2' + "td" + '\u02C3' + '\u02C2' + "/td" + '\u02C3');
    for(int i = 0; i<alphabetCount; i++){
        sb.append('\u02C2' + "td" + '\u02C3' + alphabets[i].toFAString() + '\u02C2' + "/td" + '\u02C3');
    }
    sb.append('\u02C2' + "/tr" + '\u02C3');
    for (int i = 0; i < controlCount && i != blackHole; i++) {
      sb.append('\u02C2' + "tr" + '\u02C3');
      sb.append('\u02C2' + "td style=\"text-align:right\"" + '\u02C3');
      if(Arrays.binarySearch(finalControl, i) >= 0) sb.append("<-");
      if(i==0)sb.append("->");
      sb.append(controls[i].toFAString() + '\u02C2' + "/td" + '\u02C3');
      for (int j = 0; j < alphabetCount; j++) {
        sb.append('\u02C2' + "td" + '\u02C3');
        if (tableRule[i][j] != blackHole) {
          if (nfa) {
            sb.append("{" + controls[tableRule[i][j]].toFAString() + "}");
          } else {
            sb.append(controls[tableRule[i][j]].toFAString());
          }
        }
        sb.append('\u02C2' + "/td" + '\u02C3');
      }
      sb.append('\u02C2' + "/tr" + '\u02C3');
    }
    sb.append('\u02C2' + "/table" + '\u02C3');
    sb.append('\u02C2' + "/center" + '\u02C3');
    //end
    }

    return sb.toString();
  }

  protected String deviceToString2(boolean inOneRow) {
    if(inOneRow) return DFAToStringOrToStringNFA2(inOneRow, false);
    StringBuffer sb = new StringBuffer(13+(20*(alphabetCount+1)+1)*(controlCount+1)+40+3*finalControl.length);
    if (controls == null || controls.length == 0) {
      renameControls();
    }
    //start
    sb.append('\u02C2' + "center" + '\u02C3');
    sb.append('\u02C2' + "table class=\"automatonTable\" " + '\u02C3');
    sb.append('\u02C2' + "tr" + '\u02C3');
    sb.append('\u02C2' + "td" + '\u02C3' + '\u02C2' + "/td" + '\u02C3');
    for(int i = 0; i<alphabetCount; i++){
        sb.append('\u02C2' + "td" + '\u02C3' + alphabets[i].toFAString() + '\u02C2' + "/td" + '\u02C3');
    }
    sb.append('\u02C2' + "/tr" + '\u02C3');
    for (int i=0; i<controlCount && i!=blackHole; i++) {
      sb.append('\u02C2' + "tr" + '\u02C3');
      sb.append('\u02C2' + "td style=\"text-align:right\"" + '\u02C3');
      if(Arrays.binarySearch(finalControl, i) >= 0) sb.append("<-");
      if(i==0)sb.append("->");
      sb.append(controls[i].toFAString() + '\u02C2' + "/td" + '\u02C3');
      for (int j = 0; j < alphabetCount; j++) {
        sb.append('\u02C2' + "td" + '\u02C3');
        if (tableRule[i][j] != blackHole) {
            sb.append(controls[tableRule[i][j]].toFAString());
          }
        sb.append('\u02C2' + "/td" + '\u02C3');
      }
        sb.append('\u02C2' + "/tr" + '\u02C3');
    }
    sb.append('\u02C2' + "/table" + '\u02C3');
    sb.append('\u02C2' + "/center" + '\u02C3');
    //end
    if (!inOneRow) {
      sb.append("\n");
    }
    return sb.toString();
  }
  protected String DFAToStringOrToStringNFA(boolean inOneRow, boolean nfa) {
    StringBuffer sb = new StringBuffer(13 + (11 * alphabetCount + 1)
        * controlCount + 3 * finalControl.length);
    if (controls == null || controls.length == 0) {
      renameControls();
    }
    if (!inOneRow) {
      sb.append("init=" + controls[0].toFAString() + "\n");
    }
    for (int i = 0; i < controlCount && i != blackHole; i++) {
      boolean added = false;
      for (int j = 0; j < alphabetCount; j++) {
        if (tableRule[i][j] != blackHole) {
          sb.append("(" + controls[i].toFAString() + ","
              + alphabets[j].toFAString() + ")=");
          if (nfa) {
            sb.append("{" + controls[tableRule[i][j]].toFAString() + "}");
          } else {
            sb.append(controls[tableRule[i][j]].toFAString());
          }
          if (!inOneRow) {
            sb.append(" ");
          }
          added = true;
        }
      }
      if (added && !inOneRow) {
        sb.append("\n");
      }
    }
    if (inOneRow) {
      sb.append(" ");
    }
    sb.append("F={");
    boolean first = true;
    for (int i = 0; i < finalControl.length; i++) {
      if (first) {
        first = false;
      } else {
        sb.append(",");
      }
      sb.append(controls[finalControl[i]].toFAString());
    }
    sb.append("}");
    if (!inOneRow) {
      sb.append("\n");
    }

    return sb.toString();
  }

  protected String deviceToString(boolean inOneRow) {
    return DFAToStringOrToStringNFA(inOneRow, false);
  }
  private String getNameControl(int i) {
    if (controlCount > 26) {
      return String.valueOf(i);
    }
    return String.valueOf((char) ('A' + i));
  }

  private void renameControls() {
    controls = new Control[controlCount];
    for (int i = 0, l = controlCount; i < l; i++) {
      controls[i] = new Control(getNameControl(i));
    }
  }

  int[][] getTableRule() {
    return tableRule;
  }

  int[] getFinals() {
    return finalControl;
  }

  Alphabet[] getAlphabets() {
    return alphabets;
  }

  // int blackHole = -1; // if is BlackHole -1 - DFA is total

}

class OldRuleToNew {
  int oldClass;
  int newClass;
  int[] rule;

  OldRuleToNew(int oldClass, int newClass, int[] r) {
    this.oldClass = oldClass;
    this.newClass = newClass;
    rule = r;
  }

  int getOldClass() {
    return oldClass;
  }

  int getNewClass() {
    return newClass;
  }

  int[] getRule() {
    return rule;
  }

  public boolean equals(Object o) {
    if (!(o instanceof OldRuleToNew))
      return false;

    OldRuleToNew r = (OldRuleToNew) o;
    if (r.oldClass != oldClass)
      return false;

    return Arrays.equals(rule, r.rule);
  }

  public int hashCode() {
    return Arrays.hashCode(rule) + 37 * oldClass;
  }

  public String toString() {
    String s = oldClass + "->" + newClass + " : ";
    for (int i = 0; i < rule.length; i++)
      s += rule[i] + " ";
    return s;
  }
}
