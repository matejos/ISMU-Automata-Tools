package cz.muni.fi.fja.grammar;

import cz.muni.fi.fja.common.Alphabet;
import cz.muni.fi.fja.common.Control;
import cz.muni.fi.fja.common.InStream;
import cz.muni.fi.fja.common.ModelError;
import cz.muni.fi.fja.common.ModelReaderAbstract;
import cz.muni.fi.fja.common.Rule;

public class GrammarReader extends ModelReaderAbstract {
  private static char[] conversionSymbol = new char[] { '-', '>' };
  private boolean epsilonOnRight = false; // if true - epsion is accepted on the
                                          // left side
  private boolean startSymbolOnRight = false;

  private int typeGrammar = 3; // -1 incorrect grammar
  private ModelError errorNonRegular;

  private int rightSymbols = 0; // count Symbols on the right side of the rule
                                // in a row
  private int leftSymbols = 0; // count Symbols on the left side of the rule in
                               // a row

  public GrammarReader(InStream is) {
    super(is);

    if (!isError()) {
      start();
    }
    this.is = null;
    finishedAdding();
  }

  public void start() {
    getEndOfRule();
    while (!isError() && !is.isEOF()) {
      Rule r = getLeftSide();
      if (r != null && is.getExpectedSymbol(conversionSymbol)) {
        boolean first = true;
        while (!isError() && !getEndOfRule()) {
          if (first) {
            first = false;
          } else {
            if (!is.getExpectedSymbol('|')) {
              getErrorFromIs();
              break;
            }
          }
          if (getRightSide(r)) {
            if (rightSymbols == 0) {
              if (r == startSymbol.getRule()) {
                epsilonOnRight = true;
                if (startSymbolOnRight) {
                  setTypeGrammar(0);
                }
              } else {
                setTypeGrammar(0);
              }
            } else if (typeGrammar == 1 && leftSymbols > rightSymbols) {
              setTypeGrammar(0);
            }
          }
        }
      } else {
        getErrorFromIs();
      }
    }
    return;
  }

  protected Rule getLeftSide() {
    leftSymbols = 0;
    Control c = null;

    while (is.getSymbol()) {
      if (!is.anyLastSymbol()) {
        if (c == null) {
          setError(ModelError.notFoundLeftNonterminal());
          return null;
        } else {
          return c.getRule();
        }
      }
      String s = is.getLastSymbol();
      leftSymbols++;
      if (is.lastSymbolIsControl()) {
        if (c == null) {
          c = getControl(s);
          if (startSymbol == null) {
            startSymbol = c;
          }
        } else {
          setTypeGrammar(1);
        }
      } else {
        if (s.equals("")) {
          leftSymbols--;
        }
      }
      if (leftSymbols > 1) {
        setTypeGrammar(1);
      }
    }
    getErrorFromIs();
    return null;
  }

  protected boolean getRightSide(Rule r) {
    rightSymbols = 0;
    String startNonterm = startSymbol.toString();
    Alphabet term = null;
    Control nonterm = null;
    boolean anySymbol = false;
    while (is.getSymbol()) {
      if (!anySymbol) {
        anySymbol = is.anyLastSymbol();
      }
      if (!is.anyLastSymbol()) {
        if (typeGrammar == 3) {
          if (term != null) {
            if (nonterm == null) {
              r.add(term);
            } else {
              r.add(term, nonterm);
              // jestlize se objevilo nahodou nekde \eN tak je to jiz oseterno
            }
          }
        }
        return anySymbol;
      }
      String s = is.getLastSymbol();
      rightSymbols++;
      if (is.lastSymbolIsControl() && !startSymbolOnRight
          && startNonterm.equals(s)) {
        startSymbolOnRight = true;
        if (epsilonOnRight) {
          setTypeGrammar(0);
        }
      }
      if (typeGrammar == 3) {
        if (!is.lastSymbolIsControl()) {
          if (term == null || (term.isEpsilon())) {
            term = getAlphabet(s);
            if (term.isEpsilon()) {
              rightSymbols--;
            }
          } else {
            if (s.equals("")) {
              rightSymbols--;
            } else {
              setTypeGrammar(2);
            }
          }
        } else {
          if (term != null && rightSymbols == 2) {
            nonterm = getControl(s);
          } else {
            setTypeGrammar(2);
          }
        }
      } else {
        if (s.equals("")) {
          rightSymbols--;
        }
      }
    }
    getErrorFromIs();
    return false;
  }

  protected void setTypeGrammar(int n) {
    if (n < typeGrammar) {
      typeGrammar = n;
      errorNonRegular = ModelError.nonregularGrammar(is.getLine(), is
          .getPositionInLine(), typeGrammar);
    }
  }

  protected boolean getEndOfRule() {
    boolean endOfRule = false;
    while (is.skipAllWhiteSpaces()) {
      if (is.getChar() == '\n' || is.getChar() == ',') {
        is.consume();
        endOfRule = true;
      } else {
        return endOfRule;
      }
    }
    return true;
  }

  protected Control createControl(String n) {
    return new Control(n);
  }

  protected Alphabet createAlphabet(String n) {
    return new Alphabet(n);
  }

  protected Rule createRule(Control c) {
    return new RuleGrammar(c);
  }

  public ModelError finishedAdding() {
    setError(super.finishedAdding());
    if (!isError()) {
      setError(errorNonRegular);
    } else {
      typeGrammar = -1;
    }
    return getError();
  }

  public int typeGrammar() {
    return typeGrammar;
  }

}
