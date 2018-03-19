package cz.muni.fi.fja.common;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * 
 * @author Bronek
 */
public abstract class StringStream implements InStream {
  protected char[] data;
  protected int size;
  protected int position = 0;
  protected int line = 0;
  protected int positionInLine = 0;
  private ModelError error = null;
  protected CharArrayWriter actualSymbol;
  private boolean anyActualSymbol = false;
  private boolean actualSymbolIsControl = false;

  /** Creates a new instance of StringStream */

  private StringStream() {
    actualSymbol = new CharArrayWriter();
    // prepare variables for getting tokens
  }

  public StringStream(InputStream is, String encoding) {
    this();
    load(is, encoding);
  }

  public StringStream(char[] d, int start) {
    this();
    data = d;
    position = start;
    size = data.length;
  }

  public StringStream(File f, String encoding) {
    this();
    try {
      load(new FileInputStream(f), encoding);
    } catch (FileNotFoundException e) {
      setError(new ModelError(e.getMessage()));
    }
  }

  public StringStream(File f) {
    this(f, null);
  }

  private void load(InputStream is, String encoding) {
    if (is != null) {
      try {
        BufferedReader br;
        if (encoding != null) {
          br = new BufferedReader(new InputStreamReader(is, encoding));
        } else {
          br = new BufferedReader(new InputStreamReader(is));
        }
        StringBuffer sb = new StringBuffer(64);
        while (br.ready()) {
          sb.append(br.readLine());
          sb.append('\n');
        }
        data = sb.toString().toCharArray();
        size = data.length;
        br.close();
      } catch (UnsupportedEncodingException e) {
        setError(new ModelError(e.getMessage()));
      } catch (IOException e) {
        setError(new ModelError(e.getMessage()));
      } finally {
      }
    } else {
      data = new char[0];
      size = 0;
    }
  }

  protected boolean consumePosition() {
    if (data[position] == '\n') {
      line++;
      positionInLine = 0;
    } else {
      positionInLine++;
    }
    position++;
    return true;
  }

  public boolean consume() {
    if (position < size)
      return consumePosition();

    return false;
  }

  public boolean consume(char c) {
    if (position < size && data[position] == c)
      return consumePosition();

    return false;
  }

  public boolean consume(char[] d) {
    if (position + d.length > size)
      return false;

    int oldPos = position;
    int oldLine = line;
    int oldChar = positionInLine;
    for (int i = 0, l = d.length; i < l; i++) {
      if (data[position] != d[i]) {
        position = oldPos;
        line = oldLine;
        positionInLine = oldChar;
        return false;
      }
      consumePosition();
    }
    return true;
  }

  public boolean consume(String s) {
    return consume(s.toCharArray());
  }

  public boolean writeSpecialChar() {
    setAnyActualSymbol();
    consumePosition();
    if (isEOF() || !isFollowingSpecialMark()) {
      setError(ModelError.unknownSpecialChar());
      return false;
    }
    if (getChar() == Symbol.EPSILON_CHAR) {
      return consumePosition();
    }
    return writeChar();
  }

  public boolean writeChar() {
    setAnyActualSymbol();
    actualSymbol.write(data[position]);
    return consumePosition();
  }

  public boolean getExpectedSymbol(char c) {
    if (!consume(c)) {
      setError(ModelError.expectedSymbol(c));
      return false;
    }
    skipAllWhiteSpaces();
    return true;
  }

  public boolean getExpectedSymbol(char[] d) {
    if (!consume(d)) {
      setError(ModelError.expectedSymbol(new String(d)));
      return false;
    }
    skipAllWhiteSpaces();
    return true;
  }

  public boolean getSymbol() {
    if (isError()) {
      return false;
    }
    delLastSymbol();
    return true;
  }

  public boolean getAlphabet() {
    return getSymbol();
  }

  public boolean getControl() {
    return getSymbol();
  }

  public boolean skipAllWhiteSpaces() {
    while (!isEOF()) {
      if (isWhiteSpace()) {
        consumePosition();
      } else {
        return true;
      }
    }
    return false;
  }

  public abstract boolean isSpecialMark();

  public abstract boolean isFollowingSpecialMark();

  public abstract boolean isControlChar();

  public boolean isWhiteSpace() {
    char c = data[position];
    return c == ' ' || c == '\r' || c == '\n' || c == '\t' || c == '\f';
  }

  public char getChar() {
    if (position < size) {
      return data[position];
    }
    setError(ModelError.unexpectedEnd());
    return 0;
  }

  // public int getPosition() {return position;}
  public int getLine() {
    return line;
  }

  public int getPositionInLine() {
    return positionInLine;
  }

  public String getLastSymbol() {
    return actualSymbol.toString();
  }

  public boolean anyLastSymbol() {
    return anyActualSymbol;
  }

  protected void setAnyActualSymbol() {
    anyActualSymbol = true;
  }

  protected void delLastSymbol() {
    actualSymbol.reset();
    anyActualSymbol = false;
    actualSymbolIsControl = false;
  }

  public boolean lastSymbolIsControl() {
    return actualSymbolIsControl;
  }

  protected void setActualIsControl() {
    actualSymbolIsControl = true;
  }

  public boolean isEOF() {
    return position >= size;
  }

  public boolean isError() {
    return error != null;
  }

  public ModelError getError() {
    return error;
  }

  protected ModelError setError(String s) {
    return setError(ModelError.incorrectEnterDataError(line, positionInLine, s));
  }

  protected ModelError setError(ModelError e) {
    if (error == null)
      error = e;
    return error;
  }

  public String toString() {
    return "Stream:\n" + new String(data);
  }

  public void info() {
    if (error != null) {
      System.out.print(error);
    } else {
      System.out.print("size:" + size + " position:" + position + " line:"
          + line + " charInLine:" + positionInLine + " ");
      if (position < size) {
        System.out.println("char:\"" + data[position] + "\"");
      } else {
        System.out.println("EOF");
      }
    }
  }
}
