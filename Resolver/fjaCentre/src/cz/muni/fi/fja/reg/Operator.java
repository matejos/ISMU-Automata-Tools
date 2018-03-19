package cz.muni.fi.fja.reg;

import cz.muni.fi.fja.common.Symbol;

public class Operator extends Symbol {
  public static final Operator SUB_EXPRESSION = new Operator(REG_EXPR_START, 0);
  public static final Operator SUB_EXPRESSION_END = new Operator(REG_EXPR_END,
      0);
  public static final Operator UNION = new Operator(REG_UNION, 1);
  public static final Operator CONCAT = new Operator(REG_CONCAT, 2);
  public static final Operator PLUS_ITERATOR = new Operator("" + REG_INDEX
      + REG_PLUS_ITERATOR, 3);
  public static final Operator ITERATOR = new Operator("" + REG_INDEX
      + REG_ITERATOR, 3);
  public static final Operator CLOSE_CONCAT = new Operator(REG_CLOSE_CONCAT, 4);

  private Operator(String s, int i) {
    super(s, false);
    super.setInt(i);
  }

  private Operator(char c, int i) {
    super(String.valueOf(c), false);
    super.setInt(i);
  }

  public void setInt(int i) {
  }

  public int compareTo(Operator o) {
    return getInt() - o.getInt();
  }

  public boolean isHigherThan(Operator o) {
    return compareTo(o) > 0;
  }

  public boolean isSubExpression() {
    return this == SUB_EXPRESSION;
  }

  public boolean isSubExpressionEnd() {
    return this == SUB_EXPRESSION_END;
  }

  public boolean isBinary() {
    return this == UNION || this == CONCAT || this == CLOSE_CONCAT;
  }

  public boolean isUnary() {
    return this == ITERATOR || this == PLUS_ITERATOR;
  }

  public int numberOfArgs() {
    if (isBinary()) {
      return 2;
    } else if (isUnary()) {
      return 1;
    } else {
      return 0;
    }
  }

  public static Operator createOperator(String s) {
    if (s.length() == 0) {
      return CLOSE_CONCAT;
    } else if (s.length() == 1) {
      char c = s.charAt(0);
      switch (c) {
      case (REG_EXPR_START):
        return SUB_EXPRESSION;
      case (REG_EXPR_END):
        return SUB_EXPRESSION_END;
      case (REG_UNION):
        return UNION;
      case (REG_CONCAT):
        return CONCAT;
      case (REG_ITERATOR):
        return ITERATOR;
      }
    } else if (s.length() == 2 || s.charAt(0) == REG_INDEX) {
      char c = s.charAt(1);
      if (c == REG_ITERATOR) {
        return ITERATOR;
      } else if (c == REG_PLUS_ITERATOR) {
        return PLUS_ITERATOR;
      }
    }
    return null;
  }

}
