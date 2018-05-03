package cz.muni.fi.admin;

import cz.muni.fi.cfg.conversions.TransformationTypes;
import cz.muni.fi.fja.conversions.InputFormalisms;

import java.io.*;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author Daniel Pelisek, Adrian Elgyutt
 */
public class ServicesController {
  
  public interface OperationType {
    
    public String getDescription();
    
    public TransformationTypes getTransformationTypes();
  }

  public interface InType {
    
    public String getDescription();
    
    public InputFormalisms getInputTypes();
  }
  
  public enum TransformationType implements OperationType {
    NE1("Odstranit nenormované symboly", TransformationTypes.NE1),
    NE2("Odstranit nedosažitelné symboly", TransformationTypes.NE2),
    RED("Převést na redukovanou CFG", TransformationTypes.RED),
    EPS("Odstranit epsilon kroky", TransformationTypes.EPS),
    SRF("Odstranit jednoduchá pravidla", TransformationTypes.SRF),
    PRO("Převést na vlastní CFG", TransformationTypes.PRO),
    CNF("Převést do CNF", TransformationTypes.CNF),
    RLR("Odstranit levou rekurzi", TransformationTypes.RLR),
    GNF("Převést do GNF", TransformationTypes.GNF),
    ESA("Převést na PDA akceptující prázdným zásobníkem", TransformationTypes.ESA),
    FSA("Převést na rozšířený PDA akceptující koncovým stavem", TransformationTypes.FSA),
    ANA("Analyzovat gramatiku", TransformationTypes.ANA);
    
    private String description;
    private TransformationTypes type;
    
    TransformationType(String desc, TransformationTypes tt) {
      description = desc;
      type = tt;
    }
    
    @Override
    public String getDescription() {
      return description;
    }
    
    @Override
    public TransformationTypes getTransformationTypes() {
      return type;
    }
    
    public static OperationType fromTransformationTypes(TransformationTypes tt) {
      for (OperationType ot : TransformationType.values()) {
        if (ot.getTransformationTypes() == tt) {
          return ot;
        }
      }
      return null;
    }
  }
  
  public enum ConversionType implements OperationType {
    CYK("Algoritmus Cocke-Younger-Kasami", null),
    NE12("<samp><b>NE1</b></samp> - Odstranit nenormované symboly", TransformationTypes.NE1),
    NE22("<samp><b>NE2</b></samp> - Odstranit nedosažitelné symboly", TransformationTypes.NE2),
    RED2("<samp><b>RED</b></samp> - Převést na redukovanou CFG", TransformationTypes.RED),
    EPS2("<samp><b>EPS</b></samp> - Odstranit epsilon kroky", TransformationTypes.EPS),
    SRF2("<samp><b>SRF</b></samp> - Odstranit jednoduchá pravidla", TransformationTypes.SRF),
    PRO2("<samp><b>PRO</b></samp> - Převést na vlastní CFG", TransformationTypes.PRO),
    CNF2("<samp><b>CNF</b></samp> - Převést do CNF", TransformationTypes.CNF),
    RLR2("<samp><b>RLR</b></samp> - Odstranit levou rekurzi", TransformationTypes.RLR),
    GNF2("<samp><b>GNF</b></samp> - Převést do GNF", TransformationTypes.GNF);
    
    private String description;
    private TransformationTypes type;
    
    ConversionType(String desc, TransformationTypes tt) {
      description = desc;
      type = tt;
    }
    
    @Override
    public String getDescription() {
      return description;
    }
    
    @Override
    public TransformationTypes getTransformationTypes() {
      return type;
    }
    
    public static OperationType fromTransformationTypes(TransformationTypes tt) {
      for (OperationType ot : ConversionType.values()) {
        if (ot.getTransformationTypes() == tt) {
          return ot;
        }
      }
      
      for (OperationType ot : ConversionType2.values()) {
        if (ot.getTransformationTypes() == tt) {
          return ot;
        }
      }
      
      return null;
    }
  }
  
  public enum ConversionType2 implements OperationType {
    TOT("<b>TOT</b> - totálny DFA", TransformationTypes.TOT),
    MIC("<b>MIC</b> - minimálny kanonický DFA", TransformationTypes.MIC),
    MIN("<b>MIN</b> - minimalny DFA", TransformationTypes.MIN),
    DFA("<b>DFA</b> - DFA", TransformationTypes.DFA),
    NFA("<b>NFA</b> - NFA", TransformationTypes.NFA),
    EFA("<b>EFA</b> - NFA s epsilon krokmi", TransformationTypes.EFA),
    GRA("<b>GRA</b> - Regulárna gramatika", TransformationTypes.GRA),
    REG("<b>REG</b> - Regulárny výraz", TransformationTypes.REG),
    ALL("<b>ALL</b> - regulárny formalizmus", TransformationTypes.ALL);
    
    private String description;
    private TransformationTypes type;
    
    ConversionType2(String desc, TransformationTypes tt) {
      description = desc;
      type = tt;
    }
    
    @Override
    public String getDescription() {
      return description;
    }
    
    @Override
    public TransformationTypes getTransformationTypes() {
      return type;
    }
    
    public static OperationType fromTransformationTypes(TransformationTypes tt) {
      for (OperationType ot : ConversionType2.values()) {
        if (ot.getTransformationTypes() == tt) {
          return ot;
        }
      }
      return null;
    }
  }
  
  public enum ConversionType3 implements OperationType {
    CAN("<b>CAN</b> - kanonický DFA", TransformationTypes.TOT),
    MIC("<b>MIC</b> - minimálny kanonický DFA", TransformationTypes.MIC),
    MIN("<b>MIN</b> - minimalny DFA", TransformationTypes.MIN),
    DFA("<b>DFA</b> - DFA", TransformationTypes.DFA),
    NFA("<b>NFA</b> - NFA", TransformationTypes.NFA),
    EFA("<b>EFA</b> - NFA s epsilon krokmi", TransformationTypes.EFA);
    
    private String description;
    private TransformationTypes type;
    
    ConversionType3(String desc, TransformationTypes tt) {
      description = desc;
      type = tt;
    }
    
    @Override
    public String getDescription() {
      return description;
    }
    
    @Override
    public TransformationTypes getTransformationTypes() {
      return type;
    }
    
    public static OperationType fromTransformationTypes(TransformationTypes tt) {
      for (OperationType ot : ConversionType3.values()) {
        if (ot.getTransformationTypes() == tt) {
          return ot;
        }
      }
      return null;
    }
  }
  
   public enum InputType implements InType {
    DFA("<b>DFA</b> - det. konečný automat", InputFormalisms.DFA),
    EFA("<b>EFA</b> - NFA s epsilon krokmi", InputFormalisms.EFA),
    GRA("<b>GRA</b> - regulárna gramatika", InputFormalisms.GRA),
    REG("<b>REG</b> - regulárny výraz", InputFormalisms.REG),
    CFG("<b>CFG</b> - bezkontextová gram.", InputFormalisms.CFG);

    
    private String description;
    private InputFormalisms type;
    
    InputType(String desc, InputFormalisms in) {
      description = desc;
      type = in;
    }
    
    @Override
    public String getDescription() {
      return description;
    }
    
    @Override
    public InputFormalisms getInputTypes() {
      return type;
    }
  }
  private static final Logger log = Logger.getLogger(ServicesController.class);

  private static final String CONF_DIR = ".." + File.separator + "conf" + File.separator;
  private static final String DEFAULT_PROP_PATH = CONF_DIR + "default.properties";
  private static final String APP_PROP_PATH = CONF_DIR + "app.properties";

  private static ServicesController instance;

  private Properties settings;

  /**
   * Private constructor unavailable from other classes.
   * Loads log4j settings from properties file
   * Loads default application settings
   * Loads settings set by last run of application
   */
  private  ServicesController() {
    load();
  }

  /**
   * Gets instance of this class.
   * In the first call of this method creates new instance which is stored
   * in static reference. All other calls returns reference to the same object.
   * @return singleton instance of this class
   */
  public static synchronized ServicesController instance() {
    if (instance == null) {
      instance = new ServicesController();
    }
    return instance;
  }
  
  /**
   * Loads settings from .properties file.
   * First loads default settings which are used when no application settings
   * is available.
   * Afterwards loads application settings from last application run which are
   * used primary.
   * If no settings have been loaded properly, settings map is empty.
   */
    private synchronized void load() {
      FileInputStream in = null;
        try {
            log.info("Loading default settings from " + DEFAULT_PROP_PATH);
            Properties defaults = new Properties();
            try {
              in = new FileInputStream(DEFAULT_PROP_PATH);
              defaults.load(in);
              Utils.closeQuite(in);
            }
            catch (FileNotFoundException e) {
              log.error(e.toString());
            }

            log.info("Loading app settings from " + APP_PROP_PATH);
            settings = new Properties(defaults);
            try {
              in = new FileInputStream(APP_PROP_PATH);
              settings.load(in);
              Utils.closeQuite(in);
            }
            catch (FileNotFoundException e) {
              log.error(e.toString());
            }
        }
        catch (IOException e) {
            log.error("Failed to load settings: " + e, e);
            settings = new Properties();
        }
        finally {
            Utils.closeQuite(in);
        }
    }

    private synchronized void save() {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(APP_PROP_PATH);
            settings.store(out, "Last change from: " + Thread.currentThread().getStackTrace()[3]);
        }
        catch (IOException e) {
            log.warn("Failed to store settings: " + e, e);
        }
        finally {
            Utils.closeQuite(out);
        }
    }
    
    public boolean getIsAllowed(){
        return true;
    }
    public boolean isAllowed(OperationType type) {
        String strVal = settings.getProperty(type.toString(), "true");
        return Boolean.parseBoolean(strVal);
    }

    public void setAllowed(OperationType type, boolean isAllowed) {
        settings.setProperty(type.toString(), isAllowed + "");
        log.info(type + " set to " + (isAllowed ? "allowed" : "not allowed"));
        save();
    }
    
  public static String getTransformationsJson() {
    StringBuilder sb = new StringBuilder();
    for (TransformationType tt : TransformationType.values()) {
      sb.append("{\"name\":\"").append(tt).append("\",");
      sb.append("\"description\":\"").append(tt.getDescription()).append("\",");
      sb.append("\"allowed\":\"").append(instance().isAllowed(tt)).append("\"},");
    }
    String str = sb.toString();
    return "[" + str.substring(0, str.length() - 1) + "]";
  }
    
  public static String getConversionsJson() {
    StringBuilder sb = new StringBuilder();
    for (ConversionType ct : ConversionType.values()) {
      sb.append("{\"name\":\"").append(ct).append("\",");
      sb.append("\"description\":\"").append(ct.getDescription()).append("\",");
      sb.append("\"allowed\":\"").append(instance().isAllowed(ct)).append("\"},");
    }
    String str = sb.toString();
    return "[" + str.substring(0, str.length() - 1) + "]";
  }
}