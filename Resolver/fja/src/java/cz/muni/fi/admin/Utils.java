/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.admin;

import java.io.Closeable;

/**
 *
 * @author Daniel Pelisek
 */
public class Utils {
  
  /**
   * Methods for closing any closeable object without throwing exception.
   * Exception is always muted.
   * @param c Closeable object to close
   */
  public static void closeQuite(Closeable c) {
    try {
      c.close();
    }
    catch (Throwable ignore) {
    }
  }
}
