package cz.muni.fi.fja;

import cz.muni.fi.fja.common.Messages;
import cz.muni.fi.fja.common.ModelError;
import cz.muni.fi.fja.fa.DFA;
import cz.muni.fi.fja.fa.EFA;
import cz.muni.fi.fja.fa.FAStream;
import cz.muni.fi.fja.fa.NFAFromDFA;
import cz.muni.fi.fja.fa.QuickEqual;
import cz.muni.fi.fja.grammar.Grammar;
import cz.muni.fi.fja.grammar.GrammarStream;
import cz.muni.fi.fja.reg.Reg;
import cz.muni.fi.fja.reg.RegStream;

/**
 * RegEvaluator converting and equaling devices.
 * 
 * There are two main methods - convertDevice and equalDevices.
 * For More information see their comments.
 * 
 * @author Bronek
 */
public class RegEvaluator implements Evaluator {
  
  /*****************************************************************
   *                                                               *
   *   Public                                                      *
   *                                                               *
   *****************************************************************/
  
  public final static String DFA_MINIMAL_CANONIC = "MIC";
  public final static String DFA_MINIMAL = "MIN";
  public final static String DFA_TOTAL_CANONIC = "TOC";
  public final static String DFA_CANONIC = "CAN";
  public final static String DFA_TOTAL = "TOT";
  public final static String DFA = "DFA";
  public final static String NFA_EPSILON_FREE = "NFA";
  public final static String NFA_EPSILON = "EFA";
  public final static String GRAMMAR = "GRA";
  public final static String REGULAR_EXPRESSION = "REG";
  public final static String ALL = "ALL";
  
  /**
   * Prepare RegEvaluator for evaluating.
   */
  public RegEvaluator(boolean p_verbose, boolean p_details, ModelError p_error) {
    m_verbose = p_verbose;
    m_details = p_details;
    m_error = p_error;
  }
  
  /**
   * @see cz.muni.fi.xhoudek.fja.Evaluator#equalDevices(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  public boolean equalDevices(
      String p_tInfo, String p_tString, String p_sInfo, String p_sString) {
    prepareAttributes();
    m_teacherTask = p_tInfo;
    m_studentTask = p_sInfo;
    m_teacher = createDevice(m_teacherTask, p_tString);
    m_student = createDevice(m_studentTask, p_sString);
    return equalDevices();
  }

  /**
   * @see cz.muni.fi.xhoudek.fja.Evaluator#convertDevice(java.lang.String, java.lang.String, java.lang.String)
   */
  public boolean convertDevice(
      String p_source, String p_device, String p_destination, boolean p_intable) {
      intable=p_intable;
    prepareAttributes();
    m_teacherTask = p_source;
    if (m_teacherTask == null) {
      m_teacher = createDevice(ALL, p_device);
      switch (m_teacher.getTypeOfDevice()) {
        case (0):
        case (1):
        case (2):
          m_teacherTask = DFA;
          break;
        case (3):
        case (4):
          m_teacherTask = NFA_EPSILON;
          break;
        case (5):
          m_teacherTask = GRAMMAR;
          break;
        case (6):
          m_teacherTask = REGULAR_EXPRESSION;
          break;
        default:
          m_teacherTask = REGULAR_EXPRESSION;
      }
    } else {
      //!td Bronek if will be there any problems - look HERE!!
      m_teacher = createDevice(m_teacherTask, p_device);
    }
    m_studentTask = p_destination == null ? 
        DFA_MINIMAL_CANONIC : p_destination;
    return convertDevice();
  }

  /**
   * @see cz.muni.fi.xhoudek.fja.Evaluator#getQuestion()
   */
  public String getQuestion() {
    if (m_teacher.isError()) {
      return "Nemohu vygenerovat &#345;et&#283;zec pro odpov&#283;dník, nebyl zadán korektní model zadání.";
    }
    String s = m_teacher.makeMinimalCanonicDFA().toStringInOneRow();
    return Centre.START_QUESTION + DFA + Centre.SEPARATOR_MODELS + m_studentTask
        + Centre.SEPARATOR_AFTER_DEFINITION + s;
  }

  /**
   * @see cz.muni.fi.xhoudek.fja.Evaluator#getTeacherTask()
   */
  public String getTeacherTask() {
    return m_teacherTask;
  }

  /**
   * @see cz.muni.fi.xhoudek.fja.Evaluator#getStudentTask()
   */
  public String getStudentTask() {
    return m_studentTask;
  }

  /**
   * @see cz.muni.fi.xhoudek.fja.Evaluator#getError()
   */
  public ModelError getError() {
    return m_error;
  }
  
  /**
   * @see cz.muni.fi.xhoudek.fja.Evaluator#getMainTable()
   */
  public StringBuffer getMainTable() {
    return m_mainTable;
  }
  
  /**
   * @see cz.muni.fi.xhoudek.fja.Evaluator#getTeacherTable()
   */
  public StringBuffer getTeacherTable() {
    return m_teacherTable;
  }
  
  /**
   * @see cz.muni.fi.xhoudek.fja.Evaluator#getStudentTable()
   */
  public StringBuffer getStudentTable() {
    return m_studentTable;
  }
  
  /*****************************************************************
   *                                                               *
   *   Protected                                                   *
   *                                                               *
   *****************************************************************/
  
  /*****************************************************************
   *                                                               *
   *   Private                                                     *
   *                                                               *
   *****************************************************************/
  
  /**
   * All required parameters are prepared. Equal devices.
   * 
   * BE AWATE before some changes in equalsDevices!
   * If you think that here should be some changes then contact me.
   * (xsysel@seznam.cz)
   * A lot of things there could be shorter and more intuitive but
   * the first priority is performance.
   * 
   */
  private boolean equalDevices() {
    printTable(m_teacherTable, Messages.enterModel(), m_teacher, false);
    printTable(m_studentTable, Messages.enterModel(), m_student, false);

    if (m_teacher.isError()) {
      setError(ModelError.device(false));
    } else if (m_student.isError()) {
      setError(ModelError.device(true));
    }

    m_student = checkTask(m_studentTask, m_student, m_studentTable);

    if (!isError() || m_verbose) {
      m_teacher = convertDeviceToDFA(m_teacherTask, m_teacher, m_teacherTable);

      if (m_verbose) {
        if (!m_teacher.isError()) {
          m_teacher = m_teacher.makeMinimalCanonicDFA();
          printTable(m_teacherTable, "Minimalizovaný DFA", m_teacher, false);
        }
        if (!m_student.isError()) {
          m_student = m_student.makeMinimalCanonicDFA();
          printTable(m_studentTable, "Minimalizovaný DFA", m_student, false);
        }
      }
      QuickEqual l_qe = new QuickEqual(m_teacher, m_student);
      if (DFA_TOTAL.equals(m_studentTask) || DFA_MINIMAL.equals(m_studentTask)
          || DFA_TOTAL_CANONIC.equals(m_studentTask)
          || DFA_MINIMAL_CANONIC.equals(m_studentTask)) {
        if (l_qe.equalAlphabets()) {
          this.printTableCorrect(Messages.equalAlphabets());
        } else {
          setError(ModelError.nonEqualAlphabets());
          printTableError(Messages.nonequalAlphabets());
        }
      }
      if (!isError() || m_verbose) {
        if (l_qe.result()) {
          printTableCorrect(Messages.equalModel());
        } else {
          setError(ModelError.nonEqual());
          printTableError(Messages.nonequalModel());
        }
      }
    }
    return !isError();
  }

  /**
   * All required parameters are prepared. Convert device.
   */
  private boolean convertDevice() {
      m_teacherTable.append("<div class=\"leftTable\">");

    printTable(m_teacherTable, Messages.enterModel(), m_teacher, intable);
    m_teacherTable.append("</div>");
    if (m_teacher.isError()) {
      setError(ModelError.device(false));
      return false;
    }
    m_teacherTable.append("<div class=\"rightTable\">");
    if (NFA_EPSILON.equals(m_studentTask)) {
      if (DFA.equals(m_teacherTask)) {
        m_teacher = new NFAFromDFA(m_teacher);
        printTable(m_teacherTable, "DFA p&#345;evedeno na EFA.", m_teacher, intable);
      } else {
        m_teacher = convertDeviceToEFA(m_teacherTask, m_teacher, m_teacherTable);
      }
    } else if (NFA_EPSILON_FREE.equals(m_studentTask)) {
      if (DFA.equals(m_teacherTask)) {
        m_teacher = new NFAFromDFA(m_teacher);
        printTable(m_teacherTable, "DFA p&#345;evedeno na NFA.", m_teacher, intable);
      } else {
        m_teacher = convertDeviceToEFA(m_teacherTask, m_teacher, m_teacherTable);
        if (!GRAMMAR.equals(m_teacherTask)) {
          m_teacher = m_teacher.makeNFA();
          printTable(m_teacherTable, "Výsledný NFA bez epsilon krok&#367;.", m_teacher, intable);
        }
      }
    } else {
      if (DFA.equals(m_studentTask)) {
        m_teacher = convertDeviceToNFA(m_teacherTask, m_teacher, m_teacherTable);
        m_teacher = m_teacher.makeDFA();
        printTable(m_teacherTable, "Výsledný DFA", m_teacher, intable);
      } else {
        m_teacher = convertDeviceToDFA(m_teacherTask, m_teacher, m_teacherTable);
        if (DFA_MINIMAL.equals(m_studentTask)) {
          m_teacher = m_teacher.makeMinimalDFA();
          printTable(m_teacherTable, "Výsledný minimalizovaný DFA", m_teacher, intable);
        } else if (DFA_MINIMAL_CANONIC.equals(m_studentTask)) {
          m_teacher = m_teacher.makeMinimalDFA();
          if (m_details) {
            printTable(m_teacherTable, "Minimalizovaný DFA", m_teacher, intable);
          }
          m_teacher = m_teacher.makeMinimalCanonicDFA();
          printTable(m_teacherTable, "Výsledný minimalizovaný kanonický DFA",
              m_teacher, intable);
        } else if (DFA_TOTAL.equals(m_studentTask)) {
          m_teacher = m_teacher.makeCanonicDFA();
          printTable(m_teacherTable, "Výsledný totální DFA", m_teacher, intable);
        }
      }
    }
    m_teacherTable.append("</div>");
    return true;
  }
  
  private void prepareAttributes() {
    m_error = null;
    m_teacherTable = new StringBuffer();
    m_studentTable = new StringBuffer();
    m_mainTable = new StringBuffer();
    m_teacher = null;
    m_student = null;
  }

  /**
   * This method create device according to the device's type.
   */
  private static RegularDevice createDevice(String p_type, String p_deviceString) {
    // System.out.println("create: " + name + " model:\n" + s);
    if (p_type == null) {
      return ErrorDevice.getInstance();
    }
    char[] l_deviceChars = p_deviceString.toCharArray();
    if (DFA.equals(p_type) || DFA_TOTAL.equals(p_type) || DFA_MINIMAL.equals(p_type)
        || DFA_CANONIC.equals(p_type) || DFA_MINIMAL_CANONIC.equals(p_type)
        || DFA_TOTAL_CANONIC.equals(p_type)) {
      return new DFA(new FAStream(l_deviceChars, 0));
    }
    if (NFA_EPSILON_FREE.equals(p_type) || NFA_EPSILON.equals(p_type)) {
      return new EFA(new FAStream(l_deviceChars, 0));
    }
    if (GRAMMAR.equals(p_type)) {
      return new Grammar(new GrammarStream(l_deviceChars, 0));
    }
    if (REGULAR_EXPRESSION.equals(p_type)) {
      return new Reg(new RegStream(l_deviceChars, 0));
    }
    if (ALL.equals(p_type)) {
      RegularDevice l_device = new DFA(new FAStream(l_deviceChars, 0));
      if (l_device.isError()) {
        l_device = new EFA(new FAStream(l_deviceChars, 0));
        if (l_device.isError()) {
          l_device = new Grammar(new GrammarStream(l_deviceChars, 0));
          if (l_device.isError()) {
            l_device = new Reg(new RegStream(l_deviceChars, 0));
          }
        }
      }
      return l_device;
    }
    return ErrorDevice.getInstance();
  }

  /**
   * Check entered device if corresponds to the task.
   */
  private RegularDevice checkTask(
      String p_task, RegularDevice p_device, StringBuffer p_sb) {
    if (p_device.isError()) {
      return p_device;
    }
    boolean l_isStudentTaskChecking = p_sb == m_studentTable;

    if (DFA_TOTAL.equals(p_task)) {
      setError(checkTotalDFA(p_device, l_isStudentTaskChecking));
    } else if (DFA_MINIMAL.equals(p_task)) {
      setError(checkMinimalDFA(p_device, l_isStudentTaskChecking));
    } else if (DFA_CANONIC.equals(p_task)) {
      setError(checkCanonicDFA(p_device, l_isStudentTaskChecking));
    } else if (DFA_MINIMAL_CANONIC.equals(p_task)) {
      setError(checkCanonicDFA(p_device, l_isStudentTaskChecking));
      if (!isError() || m_verbose) {
        setError(checkMinimalDFA(p_device, l_isStudentTaskChecking));
      }
    } else if (DFA_TOTAL_CANONIC.equals(p_task)) {
      setError(checkTotalDFA(p_device, l_isStudentTaskChecking));
      if (m_error == null || m_verbose) {
        setError(checkCanonicDFA(p_device, l_isStudentTaskChecking));
      }
    } else if (!DFA.equals(p_task)) {
      if (NFA_EPSILON_FREE.equals(p_task)) {
        if (p_device.containsEpsilon()) {
          printTableError(Messages.nonepsilonFreeNFA(l_isStudentTaskChecking));
          setError(ModelError.containsEpsilon(l_isStudentTaskChecking));
        } else {
          printTableCorrect(Messages.epsilonFreeNFA(l_isStudentTaskChecking));
        }
      } else if (ALL.equals(p_task)) {
        printTableCorrect(Messages.recognizedModel(l_isStudentTaskChecking, p_device));
      }
      if (m_error == null || m_verbose) {
        p_device = convertDeviceToDFA(p_task, p_device, p_sb);
      }
    }

    return p_device;
  }

  /**
   * Check if device is total DFA.
   */
  private ModelError checkTotalDFA(
      RegularDevice p_device, boolean p_isStudent) {
    if (p_device.isTotal()) {
      printTableCorrect(Messages.totalDFA(p_isStudent));
      return null;
    } else {
      printTableError(Messages.nontotalDFA(p_isStudent));
      return ModelError.nonTotal(p_isStudent);
    }
  }

  /**
   * Check if device is minimal DFA.
   */
  private ModelError checkMinimalDFA(
      RegularDevice p_device, boolean p_isStudent) {
    ModelError l_error = checkTotalDFA(p_device, p_isStudent);
    if (l_error == null) {
      int l_controlsCount = p_device.controlCount();
      p_device = p_device.makeMinimalDFA();
      if (l_controlsCount == p_device.controlCount()) {
        printTableCorrect(Messages.minimalDFA(p_isStudent));
      } else {
        printTableError(Messages.nonminimalDFA(p_isStudent));
        l_error = ModelError.nonMinimal(p_isStudent);
      }
    } else {
      printTableError(Messages.minimalMustBeTotal(p_isStudent));
    }
    return l_error;
  }

  /**
   * Check if device is canonic DFA. 
   */
  private ModelError checkCanonicDFA(
      RegularDevice p_device, boolean p_isStudent) {
    if (p_device.isCanonic()) {
      printTableCorrect(Messages.canonicDFA(p_isStudent));
      return null;
    } else {
      printTableError(Messages.noncanonicDFA(p_isStudent));
      return ModelError.nonCanonic(p_isStudent);
    }
  }

  /**
   * Convert device to DFA.
   */
  private RegularDevice convertDeviceToDFA(
      String p_type, RegularDevice p_device, StringBuffer p_sb) {
    if (p_device.isError()) {
      return p_device;
    }
    if (DFA.equals(p_type)) {
      return p_device;
    }

    p_device = convertDeviceToNFA(p_type, p_device, p_sb);

    p_device = p_device.makeDFA();
    if (m_details) {
      printTable(p_sb, "NFA p&#345;eveden na DFA", p_device, intable);
    }
    return p_device;
  }

  /**
   * Convert device to NFA.
   */
  private RegularDevice convertDeviceToNFA(
      String p_task, RegularDevice p_device, StringBuffer p_sb) {
    p_device = convertDeviceToEFA(p_task, p_device, p_sb);
    if (!(GRAMMAR.equals(p_task) || NFA_EPSILON_FREE.equals(p_task))
        || p_device.containsEpsilon()) {
      p_device = p_device.makeNFA();
      if (m_details) {
        printTable(p_sb, "NFA bez epsilon krok&#367;", p_device, intable);
      }
    }
    return p_device;
  }

  /**
   * Convert device to EFA.
   */
  private RegularDevice convertDeviceToEFA(
      String p_task, RegularDevice p_device, StringBuffer p_sb) {
    if (GRAMMAR.equals(p_task)) {
      p_device = p_device.makeNFA();
      printTable(p_sb, "Gramatika p&#345;evedena na NFA", p_device, intable);
    } else if (REGULAR_EXPRESSION.equals(p_task)) {
      p_device = p_device.makeEFA();
      printTable(p_sb, "RE p&#345;evedeno na EFA.", p_device, intable);
    }
    return p_device;
  }

  private void setError(ModelError p_error) {
    if (!isError()) {
      m_error = p_error;
    }
  }

  private boolean isError() {
    return m_error != null;
  }
  
  /**
   * Append to the p_sb table rows with Title and Device.  
   */
  private void printTable(
      StringBuffer p_sb, String p_title, RegularDevice p_device, boolean p_intable) {
    if (m_verbose) {
      p_sb.append("<center><h2 class=\"transformTitle\">" + p_title + "</h2></center><br/>");
      p_sb.append("<tr><td><pre class=model>");
      assert p_device != null;
      if(p_intable) {p_sb.append(p_device.toString2());}
              else {p_sb.append(p_device.toString());}
      p_sb.append("</pre></td></tr>");
    }
  }

  private void printTableCorrect(String p_message) {
    printTable(p_message, true);
  }

  private void printTableError(String p_message) {
    printTable(p_message, false);
  }

  private void printTable(String p_message, boolean p_isCorrectMessage) {
    if (m_verbose) {
      if (p_isCorrectMessage) {
        m_mainTable.append("<tr><td class=systemyes>");
      } else {
        m_mainTable.append("<tr><td class=systemno>");
      }
      m_mainTable.append(p_message);
      m_mainTable.append("</td></tr>");
    }
  }

  private ModelError m_error;
  private boolean m_verbose = false;
  private boolean m_details = false;
  private RegularDevice m_teacher;
  private RegularDevice m_student;
  private String m_studentTask;
  private String m_teacherTask;
  private StringBuffer m_mainTable;
  private StringBuffer m_teacherTable;
  private StringBuffer m_studentTable;
  private boolean intable = false;

}
