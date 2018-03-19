package cz.muni.fi.fja;

import cz.muni.fi.fja.common.ModelError;

/**
 * This class is centre of this application.
 * 
 * The Centre contains two main methods - convertDevice and equalDevices.
 * For More information see their comments.
 */
public class Centre {
  
  /*****************************************************************
   *                                                               *
   *   Public                                                      *
   *                                                               *
   *****************************************************************/
  
  public final static char SEPARATOR_AFTER_DEFINITION = ':';
  public final static char SEPARATOR_MODELS = '-';
  public final static String START_QUESTION = "b:";
  
  
  public Centre(boolean p_verbose, boolean p_details) {
    this.m_verbose = p_verbose;
    this.m_details = p_details;
  }

  /**
   * Returns false if the device was not recognized or if the
   * converting was not successful.
   * 
   * @param source - type of the inserted device
   * @param device - inserted device
   * @param destination - type of the requiring device
   */
  public boolean convertDevice(String p_source, String p_device, String p_destination, boolean p_table) {
    prepareAttributes(true);
    createEvaluator(p_source);
    return m_evaluator.convertDevice(p_source, p_device, p_destination, p_table);
  }

  /**
   * Returns true/false if the student device is equals to the 
   * teacher device and if it complies "sInfo" requirement.
   *  
   * @param tInfo - type of the teacher device
   * @param tString  - tearcher's device
   * @param sInfo - type of the requiring student device
   * @param sString - student's device
   */
  public boolean equalDevices(
      String p_tInfo, String p_tString, String p_sInfo, String p_sString) {
    prepareAttributes(false);
    /**
     * If will be added new evaluator (new models) then add here 
     * code like below.
     */
//    if ("NEWTYPE".equals(p_source)) {
//      m_evaluator = new NEWTYPEEvaluator(verbose, details);
//    } else {
//      m_evaluator = new RegEvaluator(verbose, details);
//    }
    createEvaluator(p_tInfo);
    return m_evaluator.equalDevices(p_tInfo, p_tString, p_sInfo, p_sString);
  }

  /**
   * Returns true/false if the student device is equals to the 
   * teacher device and if it complies teacher's requirement.
   * 
   * @param p_tString - teacher's device with type of teacher's device and student's device. 
   * @param p_sString - student's device.
   */
  public boolean equalDevices(String p_tString, String p_sString) {
    prepareAttributes(false);
    String l_teacherDevice = null;
    String l_studentTask = null;
    if (p_tString.length() >= 8 && p_tString.charAt(3) == SEPARATOR_MODELS && 
        p_tString.charAt(7) == SEPARATOR_AFTER_DEFINITION) {
        l_teacherDevice = p_tString.substring(0, 3);
        l_studentTask = p_tString.substring(4, 7);
        p_tString = p_tString.substring(8);
    } else {   
      setError(ModelError.incorrectEnterString());
    }
    createEvaluator(l_teacherDevice);
    return m_evaluator.equalDevices(
        l_teacherDevice, p_tString, l_studentTask, p_sString);
  }

  /**
   * Returns string optimised for the IS. 
   */
  public String getQuestion() {
    if (m_evaluator.getTeacherTask() == null || m_evaluator.getStudentTask() == null) {
      return "Nemohu vygenerovat retezec pro odpovednik, nebyl zadan jazyk odpovedi nebo pozadavek na odpoved.";
    }
    return m_evaluator.getQuestion();
  }

  /**
   * This method should returns HTML answer  
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    if (!m_verbose) {
      if (getError() == null) {
        return "true";
      }
      return "false";
    }

    StringBuffer l_sb = new StringBuffer(400
        + m_evaluator.getTeacherTable().length()
        + m_evaluator.getStudentTable().length()
        + m_evaluator.getMainTable().length());
    if (!m_converting) {
      l_sb.append("Zadana uloha:</h2>").
          append(m_evaluator.getTeacherTask()).
          append("-").
          append(m_evaluator.getStudentTask());
    } else {
      l_sb.append("<h2 class=\"transformTitle\">P&#345;evod:").
          append(m_evaluator.getTeacherTask()).
          append("-&gt;").
          append(m_evaluator.getStudentTask()).
          append("</h2><br/>");
          
    }
    if (!m_converting) {
        if (getError() != null) {
        l_sb.append("FALSE: ").append(getError().toString()).append("\n");
    } else {
        l_sb.append("TRUE: bez chyb\n");
    }
        l_sb.append(m_evaluator.getMainTable()).append("<tr><td><hr></td></tr>");
    }
    
    if (m_converting) {
      l_sb.append(m_evaluator.getTeacherTable());
    } else {
      l_sb.append("<table><tr><td>");
      l_sb.append("<table class=teacher><caption>Model ucitele</caption>").
          append(m_evaluator.getTeacherTable()).
          append("</table>\n");
      l_sb.append("</td><td>");
      l_sb.append("<table class=student><caption>Model studenta</caption>").
          append(m_evaluator.getStudentTable()).
          append("</table>\n");
      l_sb.append("</td></tr></table>\n");
    }
    return l_sb.toString();
  }

  /**
   * Returns type of the teachers device.
   * In case converting returns type of the source device.
   */
  public String getTeacherTask() {
    return m_evaluator.getTeacherTask();
  }
  
  /**
   * Returns type of the requiring device.
   */
  public String getStudentTask() {
    return m_evaluator.getStudentTask();
  }
  
  /*****************************************************************
   *                                                               *
   *   Private                                                     *
   *                                                               *
   *****************************************************************/
  
  /**
   * Reset data.
   */
  private void prepareAttributes(boolean p_converting) {
    m_error = null;
    m_evaluator = null;
    m_converting = p_converting;
  }
  
  /**
   * If will be added new evaluator (new models) then add here 
   * code like below.
   */
  private void createEvaluator(String p_type) {
    /* example */
//  if ("NEWTYPE".equals(p_type)) {
//  m_evaluator = new NEWTYPEEvaluator(verbose, details);
//} else {
//  m_evaluator = new RegEvaluator(verbose, details);
//}
    m_evaluator = new RegEvaluator(m_verbose, m_details, m_error);
  }
  
  private void setError(ModelError error) {
    if (this.m_error == null) {
      this.m_error = error;
    }
  }

  private ModelError getError() {
    if (m_error == null && m_evaluator != null) {
      return m_evaluator.getError();
    } else {
      return m_error;
    }
  }
  
  private Evaluator m_evaluator;
  private ModelError m_error;
  private boolean m_verbose = false;
  private boolean m_details = false;
  /**
   * If m_converting is true then teacher's device will be converted.
   * If m_converting is false then student's device will be tested
   *  if is equal to the teacher device and if student's device
   *  complies teacher's requirments (studentTasks).
   */
  private boolean m_converting = false;
}
