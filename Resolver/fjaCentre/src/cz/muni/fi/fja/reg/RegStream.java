package cz.muni.fi.fja.reg;

import java.io.File;

import cz.muni.fi.fja.common.ModelError;
import cz.muni.fi.fja.common.StringStream;
import cz.muni.fi.fja.common.Symbol;

public class RegStream extends StringStream {
  private boolean lastSymbolIsNull;

  public RegStream(File f) {
    super(f);
  }

  public RegStream(File f, String encoding) {
    super(f, encoding);
  }

  public RegStream(char[] d, int start) {
    super(d, start);
  }

  public boolean getSymbol() {
    if (!super.getSymbol()) {
      return false;
    }

    if (!isEOF()) {
      if (isSpecialMark()) { // zapis specialni znak
        writeSpecialChar();
      } else if (getChar() == Symbol.REG_TERM) { // zapis klasicky znak vstupni
                                                 // abecedy - vicepismenny
                                                 // ("abc")
        getSymbolTo(Symbol.REG_TERM);
      } else {
        switch (getChar()) {
        case (Symbol.REG_EXPR_START):
        case (Symbol.REG_EXPR_END):
        case (Symbol.REG_CONCAT):
        case (Symbol.REG_ITERATOR):
        case (Symbol.REG_UNION):
          setActualIsControl();
          writeChar();
          break;
        case (Symbol.REG_INDEX):
          writeChar();
          setActualIsControl();
          if (getChar() != Symbol.REG_ITERATOR
              && getChar() != Symbol.REG_PLUS_ITERATOR) {
            setError(ModelError.wrongOperator());
          } else {
            writeChar();
          }
          break;
        default:
          writeChar();
          break;
        }
      }
      if (isError()) {
        return false;
      }
      skipAllWhiteSpaces();
    }
    return true;
  }

  private boolean getSymbolTo(char c) {
    consumePosition();
    setAnyActualSymbol();
    while (!isEOF()) {
      if (isSpecialMark()) {
        writeSpecialChar();
        if (lastSymbolIsNull) {
          setError(ModelError.wrongUsingOfEmpty());
        }
        if (isError()) {
          return false;
        }
      } else if (getChar() == c) {
        return consumePosition();
      } else {
        writeChar();
      }
    }
    setError(ModelError.expectedSymbol(c));
    return false;
  }

  public boolean isFollowingSpecialMark() {
    switch (getChar()) {
    case (' '):
    case (Symbol.EPSILON_CHAR):
    case (Symbol.SPECIAL_MARK):
    case (Symbol.REG_TERM):
    case (Symbol.REG_EMPTY):
    case (Symbol.REG_EMPTY2):
    case (Symbol.REG_EMPTY3):
    case (Symbol.REG_EXPR_START):
    case (Symbol.REG_EXPR_END):
    case (Symbol.REG_INDEX):
    case (Symbol.REG_CONCAT):
    case (Symbol.REG_ITERATOR):
    case (Symbol.REG_UNION): // stejne jako REG_PLUS_ITERATOR
      return true;
    default:
      return false;
    }
  }

  public boolean isControlChar() {
    return false;
  }

  public boolean isSpecialMark() {
    if (data[position] == Symbol.SPECIAL_MARK)
      return true;
    return false;
  }

  public boolean isWhiteSpace() {
    char c = data[position];
    return (c == ' ' || c == '\r' || c == '\t' || c == '\f' || c == '\n');
  }

  public String getLastSymbol() {
    if (lastSymbolIsNull) {
      return null;
    }
    return super.getLastSymbol();
  }

  protected void delLastSymbol() {
    super.delLastSymbol();
    lastSymbolIsNull = false;
  }

  public boolean writeSpecialChar() {
    setAnyActualSymbol();
    consumePosition();
    if (isEOF() || !isFollowingSpecialMark()) {
      setError(ModelError.unknownSpecialChar());
      return false;
    }
    switch (getChar()) {
    case (Symbol.REG_EMPTY):
    case (Symbol.REG_EMPTY2):
    case (Symbol.REG_EMPTY3):
      lastSymbolIsNull = true;// \0, \o, \O je prazdna mnozina a musi byt
                              // indikovano nastavenim ActualIsIControl
    case (Symbol.EPSILON_CHAR):
      return consumePosition();
    default:
      return writeChar();
    }
  }
}
