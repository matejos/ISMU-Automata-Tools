/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 *
 * @author Daniel Pelisek
 */
public class LogsController {
  
  private static final String LOGS_PATH = ".." + File.separator + "logs" + File.separator + "fja.log";
  
  public static String getLogsJson() {
    
    StringBuilder sb = new StringBuilder();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(LOGS_PATH));
      String line = br.readLine();
      while (line != null) {
        sb.append(line.replaceAll("\\s", " "));
        line = br.readLine();
      }
    } catch (Exception e) {
    }
    finally {
      Utils.closeQuite(br);
    }
    String result = sb.toString();
    return "[" + result.substring(0, result.length() - 1) + "]";
  }
}
