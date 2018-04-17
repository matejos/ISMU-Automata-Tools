package cz.muni.fi.fja;

import cz.muni.fi.fja.common.ModelError;

/**
 * This interface define mandatory methods necessary to evaluating
 * some devices.
 * 
 * @author Bronek
 */
public interface Evaluator {

  /**
   * Returns true/false if the student device is equals to the 
   * teacher device and if it complies "sInfo" requirement.
   *  
   * @param tInfo - type of the teacher device
   * @param tString  - tearcher's device
   * @param sInfo - type of the requiring student device
   * @param sString - student's device
   */
  boolean equalDevices(String tInfo, String tString, String sInfo, String sString);
  
  /**
   * Returns false if the device was not recognized or if the
   * converting was not succesful.
   * 
   * @param source - type of the inserted device
   * @param device - inserted device
   * @param destination - type of the requiring device
   */
  boolean convertDevice(String source, String device, String destination, boolean intable);
    
  /**
   * Returns string optimised for the IS. 
   */
  String getQuestion();

  /**
   * Returns type of the teachers device.
   * In case converting returns type of the source device.
   */
  String getTeacherTask();

  /**
   * Returns type of the requiring device.
   */
  String getStudentTask();
  
  /**
   * Return error or null if there is not error.
   */
  ModelError getError();
  
  /**
   * Returns content of the table of the teacher's models in HTML.
   * 
   * <tr><td class=title>Minimalizovany DFA</td></tr>
   * <tr><td><pre class=model>A
   * init=A
   * (A,a)=A (A,b)=B (A,c)=B 
   * (B,a)=B (B,b)=A (B,c)=A 
   * final={B}
   * </pre></td></tr>
   */
  StringBuffer getTeacherTable();
  
  /**
   * Returns content of the table of the student's models in HTML.
   * 
   * <tr><td class=title>Minimalizovany DFA</td></tr>
   * <tr><td><pre class=model>A
   * init=A
   * (A,a)=A (A,b)=B (A,c)=B 
   * (B,a)=B (B,b)=A (B,c)=A 
   * final={B}
   * </pre></td></tr>
   */
  StringBuffer getStudentTable();
  
  /**
   * Returns content of the table with reports about student task. 
   * 
   * <tr><td class=systemyes>
   * DFA studenta je totalni.
   * </td></tr>
   * <tr><td class=systemno>
   * DFA studenta neni minimalni.
   * </td></tr>
   */
  StringBuffer getMainTable();
}
