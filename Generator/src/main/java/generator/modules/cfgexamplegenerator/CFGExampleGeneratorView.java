/*
 * CFGExampleGeneratorView.java
 */

package generator.modules.cfgexamplegenerator;

import generator.common.tools.CriteriaConstraintsChecker;
import generator.common.tools.SpinnersSupport;
import generator.modules.cfgexamplegenerator.generator.AlgorithmHierarchii;
import generator.modules.cfgexamplegenerator.generator.AlgorithmType;
import generator.modules.cfgexamplegenerator.generator.CriteriaType;
import generator.modules.cfgexamplegenerator.generator.InputGrammarForm;
import generator.modules.cfgexamplegenerator.generator.OutputGrammarForm;
import generator.modules.cfgexamplegenerator.generator.SimpleCriteria;
import generator.modules.cfgexamplegenerator.generator.SymbolManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.TaskMonitor;

/**
 * @author Rastislav Mirek, Matus Abaffy at Masaryk University, Brno, Czech Republic
 * @mail rmirek@mail.muni.cz\
 * @version Expression version i s undefined on line 14, column 15 in Templates/Classes/Class.java.
 * @copyright Rastislav Mirek, Matus Abaffy all rights reserved
 */
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class CFGExampleGeneratorView extends FrameView
{

	public static final String ALL_GRAM_PREF_KEY = "all_grammars_visible";
	public static final String ALG_SHOWN_PREF_KEY = "algorithms_visible";
	public static final Map<AlgorithmType, JPanel> ALGORITHMS_MAP = new HashMap<AlgorithmType, JPanel>();
	public static final double COLOR_MULTIPLIER = 3.5;
	public Map<JPanel, Integer> algorithmsPanelsOrder = new HashMap<JPanel, Integer>();
	private boolean detailsCentredPosition = true;
	private int ignoreMove = 0;
	private int timersCalls = 0;
	private Timer informationTimer;
	private Component focusedBefore;
	private File lastSaveLocation = null;
	private Map<String, JComponent> nameToComponent = new HashMap<String, JComponent>();
	private boolean generationInProgress = false;
	private static final SpinnerModel model2max = new SpinnerNumberModel(5, 0, 5, 1);// Difficulty from 0 to 5
	public static final String PANE_USED_CLIENT_PROPERTY = "PANE_USED_CLIENT_PROPERTY";
	public static final String SPINNER_DISABLED_BY_RADIO = "SPINNER_DISABLED_BY_RADIO";
	public static final int MAX_RULES = 100;
	private static final String[] ACTIONS_TO_DISABLE_WHEN_GENERATING = { "saveAction", "clearAction",
		"clearAndGenerate", "applySettings", "setDefaultAction", "okAction", "generationAction", "quickSaveAction" };
	private static final String[] ACTIONS_TO_ENABLE_WHEN_GENERATING = { "cancelGenerationAction" };

	public CFGExampleGeneratorView(SingleFrameApplication app)
	{
		super(app);

		initComponents();

		setUp();

		ResourceMap resourceMap = getResourceMap();
		// status bar initialization - message timeout, idle icon and busy
		// animation, etc

		informationTimer = new Timer(200, new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				timersCalls++;
				if (timersCalls >= 5)
				{
					updateTaskState(true, false);
					timersCalls = 0;
				}
				else
				{
					updateTaskState(false, false);
				}

			}
		});

		int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
		for (int i = 0; i < busyIcons.length; i++)
		{
			busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
		}
		busyIconTimer = new Timer(busyAnimationRate, new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{

				busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
				statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
			}
		});
		idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
		statusAnimationLabel.setIcon(idleIcon);
		progressBar.setVisible(false);

		// connecting action tasks to status bar via TaskMonitor
		TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
		taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener()
		{

			public void propertyChange(java.beans.PropertyChangeEvent evt)
			{
				String propertyName = evt.getPropertyName();
				if ("started".equals(propertyName))
				{
					if (!busyIconTimer.isRunning())
					{
						statusAnimationLabel.setIcon(busyIcons[0]);
						busyIconIndex = 0;
						busyIconTimer.start();
					}
					informationTimer.start();
					progressBar.setVisible(true);
					progressBar.setIndeterminate(true);
					clearCriteriaTable();
					updateTaskState(false, false);
				}
				else if ("done".equals(propertyName))
				{
					busyIconTimer.stop();
					statusAnimationLabel.setIcon(idleIcon);
					progressBar.setVisible(false);
					progressBar.setValue(0);
					updateTaskState(false, true);
				}
				else if ("progress".equals(propertyName))
				{
					int value = (Integer) (evt.getNewValue());
					progressBar.setVisible(true);
					progressBar.setIndeterminate(false);
					progressBar.setValue(value);
					progressBar.setString(updateTaskState(false, false) + "%");
				}
			}
		});
	}

	private void clearCriteriaTable()
	{
		Map<JSpinner, Integer> rows = getCriteriaTableRows();
		for (JSpinner s : rows.keySet())
		{
			colorContr.removeSpinnerProp(s, ColorPriority.HARD_TO_FULLFILL);
		}
		DefaultTableModel model = (DefaultTableModel) criteriumTable.getModel();
		model.setRowCount(0);
	}

	public synchronized int updateTaskState(boolean afterSecond, boolean done)
	{
		CFGExampleGeneratorApp.GenerationActionTask generator = CFGExampleGeneratorApp.getGenerateTask();

		long duration = generator.getExecutionDuration(TimeUnit.SECONDS);
		int generated = generator.getGeneratedNumber();
		int count = generator.getExamplesCount();
		int progress = generator.getProgress();

		if (progress >= 100)
		{
			generated = count;
		}

		ResourceBundle bundle = ResourceBundle
			.getBundle("cz/muni/fi/cfgexamplegenerator/resources/CFGExampleGeneratorView");
		messagesLabel.setText(bundle.getString("GEN_IS_RUNNING") + " " + duration + " s. "
			+ bundle.getString("IT_HAS_GENERATED") + " " + generated + "/" + count + bundle.getString("EXAMPLES"));

		runningLabel.setText(bundle.getString("GEN_IS_RUNNING") + ": " + duration + " s");
		generatedExamplesLabel.setText(bundle.getString("EXAMPLES_GEN") + " " + generated + "/" + count);
		percentageLabel.setText(bundle.getString("DONE") + progress + "%");

		if ((afterSecond && !generator.isDone()) || done)
		{
			Set<SimpleCriteria> criter = generator.getCriteria();
			List<Object[]> data = new ArrayList<Object[]>();

			long allFailures = 0;

			Map<SimpleCriteria, Long> values = new HashMap<SimpleCriteria, Long>();
			for (SimpleCriteria c : criter)
			{
				values.put(c, c.getNotFulfilledCount());
			}

			for (SimpleCriteria c : values.keySet())
			{
				allFailures = allFailures + values.get(c);
			}

			int percentage;
			if (allFailures > 0)
			{
				for (SimpleCriteria c : criter)
				{
					percentage = (int) (((double) values.get(c) / (double) allFailures) * 100);
					if (percentage >= 1)
					{
						data.add(new Object[] { c.toString(), percentage });
					}
				}
			}

			DefaultTableModel model = (DefaultTableModel) criteriumTable.getModel();
			model.setRowCount(data.size());
			for (int i = 0; i < data.size(); i++)
			{
				model.setValueAt(data.get(i)[0], i, 0);
				model.setValueAt(data.get(i)[1], i, 1);
			}
		}

		if (done)
		{
			showCriteriaComponentsAction();
			informationTimer.stop();
		}

		return progress;
	}

	public void showFileWriteErrorDialog(Throwable error)
	{
		JFrame mainFrame = CFGExampleGeneratorApp.getApplication().getMainFrame();
		saveScreen();
		JOptionPane.showMessageDialog(mainFrame, "This error occured during writing to file :\n\n" + error
			+ "\n\nCheck if the file or disk are not "
			+ " used by another application, corrupted or write-protected and try again", "Error occured",
			JOptionPane.ERROR_MESSAGE);
		restoreScreen();
	}

	public void showErrorDialog(Throwable error, String beforeMessage)
	{
		JFrame mainFrame = CFGExampleGeneratorApp.getApplication().getMainFrame();
		saveScreen();
		while (error.getCause() != null && (error.getMessage() == null) || error.getMessage().equals(""))
		{
			error = error.getCause();
		}
		JOptionPane.showMessageDialog(mainFrame, beforeMessage + " \n\n" + error
			+ "\n\nIf it occcures repeatedly please contact the author.", "Error", JOptionPane.ERROR_MESSAGE);
		restoreScreen();
	}

	public void showErrorDialog(String message)
	{
		JFrame mainFrame = CFGExampleGeneratorApp.getApplication().getMainFrame();
		saveScreen();
		JOptionPane.showMessageDialog(mainFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
		restoreScreen();
	}

	public void showInformationDialog(String information)
	{
		JFrame mainFrame = CFGExampleGeneratorApp.getApplication().getMainFrame();
		saveScreen();
		JOptionPane.showMessageDialog(mainFrame, information);
		restoreScreen();
	}

	@Action
	public void showAboutBox()
	{
		if (aboutBox == null)
		{
			JFrame mainFrame = CFGExampleGeneratorApp.getApplication().getMainFrame();
			aboutBox = new ExamExampleGeneneratorForCFGAboutBox(mainFrame);
			aboutBox.setLocationRelativeTo(mainFrame);
		}
		saveScreen();
		CFGExampleGeneratorApp.getApplication().show(aboutBox);
		restoreScreen();
	}

	private void saveScreen()
	{
		focusedBefore = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		detailsDialog.setAlwaysOnTop(false);
	}

	private void restoreScreen()
	{
		detailsDialog.setAlwaysOnTop(true);
		if (focusedBefore != null)
		{
			focusedBefore.requestFocus();
		}
	}

	// saved preferences - add language pref.
	private void applyViewPreferences()
	{
		Preferences pref = Preferences.userNodeForPackage(this.getClass());
		if (pref.getBoolean(ALL_GRAM_PREF_KEY, false))
		{
			viewAllGramarsRadio.setSelected(true);
			setAllGrammarsVisible(true);
		}
		else
		{
			viewOnlyOutputRadio.setSelected(true);
			setAllGrammarsVisible(false);
		}
		viewAlgorithmsSettingsCheck.setSelected(pref.getBoolean(ALG_SHOWN_PREF_KEY, false));
		setViewAlgorithms(pref.getBoolean(ALG_SHOWN_PREF_KEY, false));
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
	 * content of this method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings({ "serial" })
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents()
	{
		java.awt.GridBagConstraints gridBagConstraints;

		mainPanel = new javax.swing.JPanel();
		leftMainPanel = new javax.swing.JPanel();
		settingsScrollPane = new javax.swing.JScrollPane();
		settingsPane = new javax.swing.JPanel();
		InputGramarPane = new javax.swing.JPanel();
		igMaxLabel = new javax.swing.JLabel();
		igMinLabel = new javax.swing.JLabel();
		igRulesNumberLabel = new javax.swing.JLabel();
		igMinRulesNumberSpinner = new javax.swing.JSpinner();
		igMaxRulesNumberSpinner = new javax.swing.JSpinner();
		igLinesNumberLabel = new javax.swing.JLabel();
		igTerminalsNumberLabel = new javax.swing.JLabel();
		igMaxRuleLengthLabel = new javax.swing.JLabel();
		igMinLinesNumberSpinner = new javax.swing.JSpinner();
		igMaxLinesNumberSpinner = new javax.swing.JSpinner();
		igMinTerminalNumberSpinner = new javax.swing.JSpinner();
		igMaxTerminalNumberSpinner = new javax.swing.JSpinner();
		igMaxRuleLengthSpinner = new javax.swing.JSpinner();
		InputAndOutputPane = new javax.swing.JPanel();
		ioInputGramarLabel = new javax.swing.JLabel();
		ioOutputGramarLabel = new javax.swing.JLabel();
		ioExampleNumberLabel = new javax.swing.JLabel();
		ioInputGramarCombo = new javax.swing.JComboBox();
		ioOutputGramarCombo = new javax.swing.JComboBox();
		ioExampleNumberSpinner = new javax.swing.JSpinner();
		algorithmsAndGramarTabbedPane = new javax.swing.JTabbedPane();
		nnTabPane = new javax.swing.JPanel();
		nnGramarPaneInTab = new javax.swing.JPanel();
		unRulesNumberLabel1 = new javax.swing.JLabel();
		unGrMinimumLabel1 = new javax.swing.JLabel();
		nnLinesMaximumSpinner = new javax.swing.JSpinner();
		unGrMaximumLabel1 = new javax.swing.JLabel();
		nnRulesMinimumSpinner = new javax.swing.JSpinner();
		unLinesNumberLabel1 = new javax.swing.JLabel();
		nnRulesMaximumSpinner = new javax.swing.JSpinner();
		nnLinesMinimumSpinner = new javax.swing.JSpinner();
		nnAlgorithmPaneInTab = new javax.swing.JPanel();
		unAlMinimumLabel1 = new javax.swing.JLabel();
		unAlMaximumLabel1 = new javax.swing.JLabel();
		nnMaximumDifficultySpinner = new javax.swing.JSpinner(new SpinnerNumberModel(2, 0, 2, 1));
		nnMinimumDifficultySpinner = new javax.swing.JSpinner(new SpinnerNumberModel(1, 0, 2, 1));
		nnAlgorithmDifficultyLabel = new javax.swing.JLabel();
		unTabPane = new javax.swing.JPanel();
		unGramarPaneInTab = new javax.swing.JPanel();
		unRulesNumberLabel = new javax.swing.JLabel();
		unGrMinimumLabel = new javax.swing.JLabel();
		unLinesMaximumSpinner = new javax.swing.JSpinner();
		unGrMaximumLabel = new javax.swing.JLabel();
		unRulesMinimumSpinner = new javax.swing.JSpinner();
		unLinesNumberLabel = new javax.swing.JLabel();
		unRulesMaximumSpinner = new javax.swing.JSpinner();
		unLinesMinimumSpinner = new javax.swing.JSpinner();
		unAlgorithmPaneInTab = new javax.swing.JPanel();
		unAlMinimumLabel = new javax.swing.JLabel();
		unAlMaximumLabel = new javax.swing.JLabel();
		unMaximumDifficultySpinner = new javax.swing.JSpinner(new SpinnerNumberModel(2, 0, 2, 1));
		unMinimumDifficultySpinner = new javax.swing.JSpinner(new SpinnerNumberModel(1, 0, 2, 1));
		unAlgorithmDifficultyLabel = new javax.swing.JLabel();
		epTabPane = new javax.swing.JPanel();
		epGramarPaneInTab = new javax.swing.JPanel();
		unRulesNumberLabel2 = new javax.swing.JLabel();
		unGrMinimumLabel2 = new javax.swing.JLabel();
		epLinesMaximumSpinner = new javax.swing.JSpinner();
		unGrMaximumLabel2 = new javax.swing.JLabel();
		epRulesMinimumSpinner = new javax.swing.JSpinner();
		unLinesNumberLabel2 = new javax.swing.JLabel();
		epRulesMaximumSpinner = new javax.swing.JSpinner();
		epLinesMinimumSpinner = new javax.swing.JSpinner();
		epAlgorithmPaneInTab = new javax.swing.JPanel();
		unAlMinimumLabel2 = new javax.swing.JLabel();
		unAlMaximumLabel2 = new javax.swing.JLabel();
		epMaximumDifficultySpinner = new javax.swing.JSpinner(new SpinnerNumberModel(5, 0, 5, 1));
		epMinimumDifficultySpinner = new javax.swing.JSpinner(new SpinnerNumberModel(1, 0, 5, 1));
		epAlgorithmDifficultyLabel = new javax.swing.JLabel();
		srTabPane = new javax.swing.JPanel();
		srGramarPaneInTab = new javax.swing.JPanel();
		unRulesNumberLabel7 = new javax.swing.JLabel();
		unGrMinimumLabel7 = new javax.swing.JLabel();
		srLinesMaximumSpinner = new javax.swing.JSpinner();
		unGrMaximumLabel7 = new javax.swing.JLabel();
		srRulesMinimumSpinner = new javax.swing.JSpinner();
		unLinesNumberLabel7 = new javax.swing.JLabel();
		srRulesMaximumSpinner = new javax.swing.JSpinner();
		srLinesMinimumSpinner = new javax.swing.JSpinner();
		srAlgorithmPaneInTab = new javax.swing.JPanel();
		unAlMinimumLabel7 = new javax.swing.JLabel();
		unAlMaximumLabel7 = new javax.swing.JLabel();
		srMaximumDifficultySpinner = new javax.swing.JSpinner(new SpinnerNumberModel(5, 0, 5, 1));
		srMinimumDifficultySpinner = new javax.swing.JSpinner(new SpinnerNumberModel(1, 0, 5, 1));
		srAlgorithmDifficultyLabel = new javax.swing.JLabel();
		lrTabPane = new javax.swing.JPanel();
		lrGramarPaneInTab = new javax.swing.JPanel();
		unRulesNumberLabel4 = new javax.swing.JLabel();
		unGrMinimumLabel4 = new javax.swing.JLabel();
		lrLinesMaximumSpinner = new javax.swing.JSpinner();
		unGrMaximumLabel4 = new javax.swing.JLabel();
		lrRulesMinimumSpinner = new javax.swing.JSpinner();
		unLinesNumberLabel4 = new javax.swing.JLabel();
		lrRulesMaximumSpinner = new javax.swing.JSpinner();
		lrLinesMinimumSpinner = new javax.swing.JSpinner();
		lrAlgorithmPaneInTab = new javax.swing.JPanel();
		unAlMinimumLabel4 = new javax.swing.JLabel();
		unAlMaximumLabel4 = new javax.swing.JLabel();
		lrMaximumDifficultySpinner = new javax.swing.JSpinner(new SpinnerNumberModel(4, 0, 5, 1));
		lrMinimumDifficultySpinner = new javax.swing.JSpinner(new SpinnerNumberModel(1, 0, 5, 1));
		lrAlgorithmDifficultyLabel = new javax.swing.JLabel();
		cfTabPane = new javax.swing.JPanel();
		cfGramarPaneInTab = new javax.swing.JPanel();
		unRulesNumberLabel5 = new javax.swing.JLabel();
		unGrMinimumLabel5 = new javax.swing.JLabel();
		cfLinesMaximumSpinner = new javax.swing.JSpinner();
		unGrMaximumLabel5 = new javax.swing.JLabel();
		cfRulesMinimumSpinner = new javax.swing.JSpinner();
		unLinesNumberLabel5 = new javax.swing.JLabel();
		cfRulesMaximumSpinner = new javax.swing.JSpinner();
		cfLinesMinimumSpinner = new javax.swing.JSpinner();
		cfAlgorithmPaneInTab = new javax.swing.JPanel();
		unAlMinimumLabel5 = new javax.swing.JLabel();
		unAlMaximumLabel5 = new javax.swing.JLabel();
		cfMaximumDifficultySpinner = new javax.swing.JSpinner(new SpinnerNumberModel(5, 0, 5, 1));
		cfMinimumDifficultySpinner = new javax.swing.JSpinner(new SpinnerNumberModel(1, 0, 5, 1));
		cfAlgorithmDifficultyLabel = new javax.swing.JLabel();
		gfTabPane = new javax.swing.JPanel();
		gfGramarPaneInTab = new javax.swing.JPanel();
		unRulesNumberLabel6 = new javax.swing.JLabel();
		unGrMinimumLabel6 = new javax.swing.JLabel();
		gfLinesMaximumSpinner = new javax.swing.JSpinner();
		unGrMaximumLabel6 = new javax.swing.JLabel();
		gfRulesMinimumSpinner = new javax.swing.JSpinner();
		unLinesNumberLabel6 = new javax.swing.JLabel();
		gfRulesMaximumSpinner = new javax.swing.JSpinner();
		gfLinesMinimumSpinner = new javax.swing.JSpinner();
		gfAlgorithmPaneInTab = new javax.swing.JPanel();
		unAlMinimumLabel6 = new javax.swing.JLabel();
		unAlMaximumLabel6 = new javax.swing.JLabel();
		gfMaximumDifficultySpinner = new javax.swing.JSpinner(new SpinnerNumberModel(4, 0, 5, 1));
		gfMinimumDifficultySpinner = new javax.swing.JSpinner(new SpinnerNumberModel(1, 0, 5, 1));
		gfAlgorithmDifficultyLabel = new javax.swing.JLabel();
		outputGramarPane = new javax.swing.JPanel();
		ogRulesNumberLabel = new javax.swing.JLabel();
		ogMinLabel = new javax.swing.JLabel();
		ogMinRulesSpinner = new javax.swing.JSpinner();
		ogMaxLabel = new javax.swing.JLabel();
		ogMinLinesSpinner = new javax.swing.JSpinner();
		ogLinesNumberLabel = new javax.swing.JLabel();
		ogMaxLinesSpinner = new javax.swing.JSpinner();
		ogMaxRulesSpinner = new javax.swing.JSpinner();
		rightMainPanel = new javax.swing.JPanel();
		resultTabbledPane = new javax.swing.JTabbedPane();
		jScrollPane3 = new javax.swing.JScrollPane();
		plain_skArea = new javax.swing.JTextArea();
		jScrollPane2 = new javax.swing.JScrollPane();
		latex_skArea = new javax.swing.JTextArea();
		validationScroll = new javax.swing.JScrollPane();
		is_skArea = new javax.swing.JTextArea();
		jScrollPane7 = new javax.swing.JScrollPane();
		plain_enArea = new javax.swing.JTextArea();
		jScrollPane4 = new javax.swing.JScrollPane();
		latex_enArea = new javax.swing.JTextArea();
		jScrollPane5 = new javax.swing.JScrollPane();
		is_enArea = new javax.swing.JTextArea();
		cancelButton = new javax.swing.JButton();
		generateButton = new javax.swing.JButton();
		clearButton = new javax.swing.JButton();
		clearAndGenerateButton = new javax.swing.JButton();
		subresultsCheck = new javax.swing.JCheckBox();
		detailedInformationCheck = new javax.swing.JCheckBox();
		printInputCheck = new javax.swing.JCheckBox();
		menuBar = new javax.swing.JMenuBar();
		javax.swing.JMenu fileMenu = new javax.swing.JMenu();
		jMenuItem1 = new javax.swing.JMenuItem();
		saveMenuItem = new javax.swing.JMenuItem();
		jSeparator1 = new javax.swing.JSeparator();
		javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
		editMenu = new javax.swing.JMenu();
		copyMenuItem = new javax.swing.JMenuItem();
		clearMenuItem = new javax.swing.JMenuItem();
		viewMenu = new javax.swing.JMenu();
		viewAllGramarsRadio = new javax.swing.JRadioButtonMenuItem();
		viewOnlyOutputRadio = new javax.swing.JRadioButtonMenuItem();
		viewAlgorithmsSettingsCheck = new javax.swing.JCheckBoxMenuItem();
		javax.swing.JMenu helpMenu = new javax.swing.JMenu();
		javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
		statusPanel = new javax.swing.JPanel();
		javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
		statusMessageLabel = new javax.swing.JLabel();
		statusAnimationLabel = new javax.swing.JLabel();
		progressBar = new javax.swing.JProgressBar();
		messagesLabel = new javax.swing.JLabel();
		unAlgorithmMethodGroup = new javax.swing.ButtonGroup();
		nnAlgorithmMethodGroup = new javax.swing.ButtonGroup();
		epAlgorithmMethodGroup = new javax.swing.ButtonGroup();
		srAlgorithmMethodGroup = new javax.swing.ButtonGroup();
		lrAlgorithmMethodGroup = new javax.swing.ButtonGroup();
		cfAlgorithmMethodGroup = new javax.swing.ButtonGroup();
		gfAlgorithmMethodGroup = new javax.swing.ButtonGroup();
		disPopUpMenu = new javax.swing.JPopupMenu();
		disMenuItem = new javax.swing.JMenuItem();
		viewButtonGroup = new javax.swing.ButtonGroup();
		jScrollPane1 = new javax.swing.JScrollPane();
		standardResultTextPane = new javax.swing.JTextPane();
		detailsDialog = new javax.swing.JDialog();
		jScrollPane6 = new javax.swing.JScrollPane();
		criteriumTable = new javax.swing.JTable();
		hideDetailsDialog = new javax.swing.JButton();
		cancelButtonInDialog = new javax.swing.JButton();
		runningLabel = new javax.swing.JLabel();
		generatedExamplesLabel = new javax.swing.JLabel();
		percentageLabel = new javax.swing.JLabel();
		showCriteriaCheck = new javax.swing.JCheckBox();
		advancedSettingsDialog = new javax.swing.JDialog();
		jPanel1 = new javax.swing.JPanel();
		jPanel2 = new javax.swing.JPanel();
		forceSEpsylonRadio = new javax.swing.JRadioButton();
		forbidSEpsylonRadio = new javax.swing.JRadioButton();
		decideSEpsylonRadio = new javax.swing.JRadioButton();
		noUnreachableChecker = new javax.swing.JCheckBox();
		noUnnormalizedChecker = new javax.swing.JCheckBox();
		noEpsilonChecker = new javax.swing.JCheckBox();
		allowRightSRadio = new javax.swing.JRadioButton();
		forbidRightSRadio = new javax.swing.JRadioButton();
		decideRightSRadio = new javax.swing.JRadioButton();
		noSimpleChecker = new javax.swing.JCheckBox();
		noLeftRekChecker = new javax.swing.JCheckBox();
		defaultAdvancedButton = new javax.swing.JButton();
		cancelButton1 = new javax.swing.JButton();
		okButton = new javax.swing.JButton();
		applyButton = new javax.swing.JButton();

		mainPanel.setName("mainPanel"); // NOI18N
		mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.LINE_AXIS));

		leftMainPanel.setMaximumSize(new java.awt.Dimension(540, 7070));
		leftMainPanel.setName("leftMainPanel"); // NOI18N
		leftMainPanel.setPreferredSize(new java.awt.Dimension(495, 652));

		settingsScrollPane.setMaximumSize(new java.awt.Dimension(332, 331));
		settingsScrollPane.setMinimumSize(new java.awt.Dimension(23, 60));
		settingsScrollPane.setName("settingsScrollPane"); // NOI18N
		settingsScrollPane.setPreferredSize(new java.awt.Dimension(332, 331));

		settingsPane.setName("settingsPane"); // NOI18N
		settingsPane.setLayout(new java.awt.GridBagLayout());

		org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application
			.getInstance(generator.modules.cfgexamplegenerator.CFGExampleGeneratorApp.class).getContext()
			.getResourceMap(CFGExampleGeneratorView.class);
		InputGramarPane.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap
			.getString("InputGramarPane.border.title"))); // NOI18N
		InputGramarPane.setName("InputGramarPane"); // NOI18N

		igMaxLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		igMaxLabel.setText(resourceMap.getString("igMaxLabel.text")); // NOI18N
		igMaxLabel.setMaximumSize(new java.awt.Dimension(40, 14));
		igMaxLabel.setMinimumSize(new java.awt.Dimension(40, 14));
		igMaxLabel.setName("igMaxLabel"); // NOI18N
		igMaxLabel.setPreferredSize(new java.awt.Dimension(40, 14));

		igMinLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		igMinLabel.setText(resourceMap.getString("igMinLabel.text")); // NOI18N
		igMinLabel.setName("igMinLabel"); // NOI18N

		igRulesNumberLabel.setText(resourceMap.getString("igRulesNumberLabel.text")); // NOI18N
		igRulesNumberLabel.setMaximumSize(new java.awt.Dimension(78, 14));
		igRulesNumberLabel.setMinimumSize(new java.awt.Dimension(78, 14));
		igRulesNumberLabel.setName("igRulesNumberLabel"); // NOI18N
		igRulesNumberLabel.setPreferredSize(new java.awt.Dimension(78, 14));

		igMinRulesNumberSpinner.setComponentPopupMenu(disPopUpMenu);
		igMinRulesNumberSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		igMinRulesNumberSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		igMinRulesNumberSpinner.setName(resourceMap.getString("igMinRulesNumberSpinner.name")); // NOI18N
		igMinRulesNumberSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		igMinRulesNumberSpinner.setValue(8);

		igMaxRulesNumberSpinner.setComponentPopupMenu(disPopUpMenu);
		igMaxRulesNumberSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		igMaxRulesNumberSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		igMaxRulesNumberSpinner.setName(resourceMap.getString("igMaxRulesNumberSpinner.name")); // NOI18N
		igMaxRulesNumberSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		igMaxRulesNumberSpinner.setValue(11);

		igLinesNumberLabel.setText(resourceMap.getString("igLinesNumberLabel.text")); // NOI18N
		igLinesNumberLabel.setName("igLinesNumberLabel"); // NOI18N

		igTerminalsNumberLabel.setText(resourceMap.getString("igTerminalsNumberLabel.text")); // NOI18N
		igTerminalsNumberLabel.setName("igTerminalsNumberLabel"); // NOI18N

		igMaxRuleLengthLabel.setText(resourceMap.getString("igMaxRuleLengthLabel.text")); // NOI18N
		igMaxRuleLengthLabel.setMaximumSize(new java.awt.Dimension(78, 14));
		igMaxRuleLengthLabel.setMinimumSize(new java.awt.Dimension(78, 14));
		igMaxRuleLengthLabel.setName("igMaxRuleLengthLabel"); // NOI18N
		igMaxRuleLengthLabel.setPreferredSize(new java.awt.Dimension(78, 14));

		igMinLinesNumberSpinner.setComponentPopupMenu(disPopUpMenu);
		igMinLinesNumberSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		igMinLinesNumberSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		igMinLinesNumberSpinner.setName(resourceMap.getString("igMinLinesNumberSpinner.name")); // NOI18N
		igMinLinesNumberSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		igMinLinesNumberSpinner.setValue(4);

		igMaxLinesNumberSpinner.setComponentPopupMenu(disPopUpMenu);
		igMaxLinesNumberSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		igMaxLinesNumberSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		igMaxLinesNumberSpinner.setName(resourceMap.getString("igMaxLinesNumberSpinner.name")); // NOI18N
		igMaxLinesNumberSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		igMaxLinesNumberSpinner.setValue(6);

		igMinTerminalNumberSpinner.setComponentPopupMenu(disPopUpMenu);
		igMinTerminalNumberSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		igMinTerminalNumberSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		igMinTerminalNumberSpinner.setName(resourceMap.getString("igMinTerminalNumberSpinner.name")); // NOI18N
		igMinTerminalNumberSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		igMinTerminalNumberSpinner.setValue(2);

		igMaxTerminalNumberSpinner.setComponentPopupMenu(disPopUpMenu);
		igMaxTerminalNumberSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		igMaxTerminalNumberSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		igMaxTerminalNumberSpinner.setName(resourceMap.getString("igMaxTerminalNumberSpinner.name")); // NOI18N
		igMaxTerminalNumberSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		igMaxTerminalNumberSpinner.setValue(4);

		igMaxRuleLengthSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		igMaxRuleLengthSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		igMaxRuleLengthSpinner.setName(resourceMap.getString("igMaxRuleLengthSpinner.name")); // NOI18N
		igMaxRuleLengthSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		igMaxRuleLengthSpinner.setValue(4);

		javax.swing.GroupLayout InputGramarPaneLayout = new javax.swing.GroupLayout(InputGramarPane);
		InputGramarPane.setLayout(InputGramarPaneLayout);
		InputGramarPaneLayout.setHorizontalGroup(InputGramarPaneLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				InputGramarPaneLayout
					.createSequentialGroup()
					.addContainerGap()
					.addGroup(
						InputGramarPaneLayout
							.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
							.addComponent(igRulesNumberLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 225,
								Short.MAX_VALUE)
							.addComponent(igLinesNumberLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 225,
								Short.MAX_VALUE)
							.addComponent(igTerminalsNumberLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 225,
								Short.MAX_VALUE)
							.addComponent(igMaxRuleLengthLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 225,
								Short.MAX_VALUE))
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(
						InputGramarPaneLayout
							.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
							.addGroup(
								InputGramarPaneLayout
									.createSequentialGroup()
									.addComponent(igMinLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 81,
										javax.swing.GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(igMaxLabel, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addGroup(
								InputGramarPaneLayout
									.createSequentialGroup()
									.addComponent(igMinRulesNumberSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 81,
										javax.swing.GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(igMaxRulesNumberSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
							.addGroup(
								InputGramarPaneLayout
									.createSequentialGroup()
									.addComponent(igMinLinesNumberSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 81,
										javax.swing.GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(igMaxLinesNumberSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
							.addGroup(
								InputGramarPaneLayout
									.createSequentialGroup()
									.addComponent(igMinTerminalNumberSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
										81, javax.swing.GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(igMaxTerminalNumberSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
							.addGroup(
								InputGramarPaneLayout
									.createSequentialGroup()
									.addGap(87, 87, 87)
									.addComponent(igMaxRuleLengthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
					.addContainerGap()));
		InputGramarPaneLayout.setVerticalGroup(InputGramarPaneLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			InputGramarPaneLayout
				.createSequentialGroup()
				.addGroup(
					InputGramarPaneLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(igMinLabel)
						.addGroup(
							InputGramarPaneLayout
								.createSequentialGroup()
								.addGap(1, 1, 1)
								.addComponent(igMaxLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
				.addGap(6, 6, 6)
				.addGroup(
					InputGramarPaneLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(igRulesNumberLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(igMinRulesNumberSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(igMaxRulesNumberSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					InputGramarPaneLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(igLinesNumberLabel)
						.addComponent(igMinLinesNumberSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(igMaxLinesNumberSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					InputGramarPaneLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(igTerminalsNumberLabel)
						.addComponent(igMinTerminalNumberSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(igMaxTerminalNumberSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					InputGramarPaneLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(igMaxRuleLengthLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(igMaxRuleLengthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(6, 11, 0, 11);
		settingsPane.add(InputGramarPane, gridBagConstraints);

		InputAndOutputPane.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap
			.getString("InputAndOutputPane.border.title"))); // NOI18N
		InputAndOutputPane.setName("InputAndOutputPane"); // NOI18N
		InputAndOutputPane.setPreferredSize(new java.awt.Dimension(430, 132));

		ioInputGramarLabel.setText(resourceMap.getString("ioInputGramarLabel.text")); // NOI18N
		ioInputGramarLabel.setName("ioInputGramarLabel"); // NOI18N

		ioOutputGramarLabel.setText(resourceMap.getString("ioOutputGramarLabel.text")); // NOI18N
		ioOutputGramarLabel.setName("ioOutputGramarLabel"); // NOI18N

		ioExampleNumberLabel.setText(resourceMap.getString("ioExampleNumberLabel.text")); // NOI18N
		ioExampleNumberLabel.setName("ioExampleNumberLabel"); // NOI18N

		ioInputGramarCombo.setModel(inputModel);
		ioInputGramarCombo.setMaximumSize(new java.awt.Dimension(114, 18));
		ioInputGramarCombo.setName("ioInputGramarCombo"); // NOI18N

		ioOutputGramarCombo.setModel(outputModel);
		ioOutputGramarCombo.setName("ioOutputGramarCombo"); // NOI18N

		ioExampleNumberSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		ioExampleNumberSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		ioExampleNumberSpinner.setName(resourceMap.getString("ioExampleNumberSpinner.name")); // NOI18N
		ioExampleNumberSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		ioExampleNumberSpinner.setValue(1);

		javax.swing.GroupLayout InputAndOutputPaneLayout = new javax.swing.GroupLayout(InputAndOutputPane);
		InputAndOutputPane.setLayout(InputAndOutputPaneLayout);
		InputAndOutputPaneLayout.setHorizontalGroup(InputAndOutputPaneLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(
				InputAndOutputPaneLayout
					.createSequentialGroup()
					.addContainerGap()
					.addGroup(
						InputAndOutputPaneLayout
							.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(
								InputAndOutputPaneLayout
									.createSequentialGroup()
									.addComponent(ioExampleNumberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 246,
										javax.swing.GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72,
										Short.MAX_VALUE)
									.addComponent(ioExampleNumberSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
							.addGroup(
								InputAndOutputPaneLayout
									.createSequentialGroup()
									.addGroup(
										InputAndOutputPaneLayout
											.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
											.addComponent(ioInputGramarLabel,
												javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addComponent(ioOutputGramarLabel,
												javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
									.addGroup(
										InputAndOutputPaneLayout
											.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
											.addComponent(ioOutputGramarCombo,
												javax.swing.GroupLayout.Alignment.TRAILING, 0, 299, Short.MAX_VALUE)
											.addComponent(ioInputGramarCombo, 0, 299, Short.MAX_VALUE))))
					.addContainerGap()));
		InputAndOutputPaneLayout.setVerticalGroup(InputAndOutputPaneLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			InputAndOutputPaneLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					InputAndOutputPaneLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
							InputAndOutputPaneLayout.createSequentialGroup().addComponent(ioInputGramarLabel)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(ioOutputGramarLabel))
						.addGroup(
							InputAndOutputPaneLayout
								.createSequentialGroup()
								.addComponent(ioInputGramarCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 18,
									javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(ioOutputGramarCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 18,
									javax.swing.GroupLayout.PREFERRED_SIZE)))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					InputAndOutputPaneLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(ioExampleNumberLabel)
						.addComponent(ioExampleNumberSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		InputAndOutputPaneLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {
			ioExampleNumberLabel, ioExampleNumberSpinner, ioInputGramarCombo, ioInputGramarLabel, ioOutputGramarCombo,
			ioOutputGramarLabel });

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipady = 2;
		gridBagConstraints.insets = new java.awt.Insets(11, 11, 0, 11);
		settingsPane.add(InputAndOutputPane, gridBagConstraints);

		algorithmsAndGramarTabbedPane.setName("Minimum difficulty of simple rules elimination algorithm"); // NOI18N

		nnTabPane.setMaximumSize(new java.awt.Dimension(315, 221));
		nnTabPane.setName("nnTabPane"); // NOI18N
		nnTabPane.setLayout(new java.awt.GridBagLayout());

		nnGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap
			.getString("nnGramarPaneInTab.border.title"))); // NOI18N
		nnGramarPaneInTab.setName("nnGramarPaneInTab"); // NOI18N

		unRulesNumberLabel1.setText(resourceMap.getString("unRulesNumberLabel1.text")); // NOI18N
		unRulesNumberLabel1.setMaximumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel1.setMinimumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel1.setName("unRulesNumberLabel1"); // NOI18N
		unRulesNumberLabel1.setPreferredSize(new java.awt.Dimension(185, 17));

		unGrMinimumLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMinimumLabel1.setText(resourceMap.getString("unGrMinimumLabel1.text")); // NOI18N
		unGrMinimumLabel1.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel1.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel1.setName("unGrMinimumLabel1"); // NOI18N
		unGrMinimumLabel1.setPreferredSize(new java.awt.Dimension(80, 17));

		nnLinesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		nnLinesMaximumSpinner.setName(resourceMap.getString("nnLinesMaximumSpinner.name")); // NOI18N
		nnLinesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		nnLinesMaximumSpinner.setValue(8);

		unGrMaximumLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMaximumLabel1.setText(resourceMap.getString("unGrMaximumLabel1.text")); // NOI18N
		unGrMaximumLabel1.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel1.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel1.setName("unGrMaximumLabel1"); // NOI18N
		unGrMaximumLabel1.setPreferredSize(new java.awt.Dimension(80, 17));

		nnRulesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		nnRulesMinimumSpinner.setMaximumSize(null);
		nnRulesMinimumSpinner.setName(resourceMap.getString("nnRulesMinimumSpinner.name")); // NOI18N
		nnRulesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		nnRulesMinimumSpinner.setValue(4);

		unLinesNumberLabel1.setText(resourceMap.getString("unLinesNumberLabel1.text")); // NOI18N
		unLinesNumberLabel1.setMaximumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel1.setMinimumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel1.setName("unLinesNumberLabel1"); // NOI18N
		unLinesNumberLabel1.setPreferredSize(new java.awt.Dimension(185, 17));

		nnRulesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		nnRulesMaximumSpinner.setName(resourceMap.getString("nnRulesMaximumSpinner.name")); // NOI18N
		nnRulesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		nnRulesMaximumSpinner.setValue(16);

		nnLinesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		nnLinesMinimumSpinner.setMaximumSize(null);
		nnLinesMinimumSpinner.setName(resourceMap.getString("nnLinesMinimumSpinner.name")); // NOI18N
		nnLinesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		nnLinesMinimumSpinner.setValue(3);

		javax.swing.GroupLayout nnGramarPaneInTabLayout = new javax.swing.GroupLayout(nnGramarPaneInTab);
		nnGramarPaneInTab.setLayout(nnGramarPaneInTabLayout);
		nnGramarPaneInTabLayout.setHorizontalGroup(nnGramarPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			nnGramarPaneInTabLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					nnGramarPaneInTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(unLinesNumberLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
						.addComponent(unRulesNumberLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					nnGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unGrMinimumLabel1, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(nnRulesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(nnLinesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					nnGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unGrMaximumLabel1, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(nnRulesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(nnLinesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap()));
		nnGramarPaneInTabLayout.setVerticalGroup(nnGramarPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			nnGramarPaneInTabLayout
				.createSequentialGroup()
				.addGroup(
					nnGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
							nnGramarPaneInTabLayout
								.createSequentialGroup()
								.addGap(23, 23, 23)
								.addComponent(unRulesNumberLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
									javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(unLinesNumberLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
									javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(
							nnGramarPaneInTabLayout
								.createSequentialGroup()
								.addComponent(unGrMaximumLabel1, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(nnRulesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(nnLinesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(
							nnGramarPaneInTabLayout
								.createSequentialGroup()
								.addComponent(unGrMinimumLabel1, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(nnRulesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(nnLinesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 7;
		gridBagConstraints.insets = new java.awt.Insets(6, 11, 11, 11);
		nnTabPane.add(nnGramarPaneInTab, gridBagConstraints);

		nnAlgorithmPaneInTab.setMaximumSize(new java.awt.Dimension(301, 118));
		nnAlgorithmPaneInTab.setName("nnAlgorithmPaneInTab"); // NOI18N

		unAlMinimumLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMinimumLabel1.setText(resourceMap.getString("unAlMinimumLabel1.text")); // NOI18N
		unAlMinimumLabel1.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel1.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel1.setName("unAlMinimumLabel1"); // NOI18N
		unAlMinimumLabel1.setPreferredSize(new java.awt.Dimension(80, 17));

		unAlMaximumLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMaximumLabel1.setText(resourceMap.getString("unAlMaximumLabel1.text")); // NOI18N
		unAlMaximumLabel1.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel1.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel1.setName("unAlMaximumLabel1"); // NOI18N
		unAlMaximumLabel1.setPreferredSize(new java.awt.Dimension(80, 17));

		nnMaximumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		nnMaximumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		nnMaximumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		nnMaximumDifficultySpinner.setName(resourceMap.getString("nnMaximumDifficultySpinner.name")); // NOI18N
		nnMaximumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		nnMinimumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		nnMinimumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		nnMinimumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		nnMinimumDifficultySpinner.setName(resourceMap.getString("nnMinimumDifficultySpinner.name")); // NOI18N
		nnMinimumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		nnAlgorithmDifficultyLabel.setText(resourceMap.getString("nnAlgorithmDifficultyLabel.text")); // NOI18N
		nnAlgorithmDifficultyLabel.setMaximumSize(new java.awt.Dimension(120, 17));
		nnAlgorithmDifficultyLabel.setMinimumSize(new java.awt.Dimension(120, 17));
		nnAlgorithmDifficultyLabel.setName("nnAlgorithmDifficultyLabel"); // NOI18N
		nnAlgorithmDifficultyLabel.setPreferredSize(new java.awt.Dimension(185, 17));

		javax.swing.GroupLayout nnAlgorithmPaneInTabLayout = new javax.swing.GroupLayout(nnAlgorithmPaneInTab);
		nnAlgorithmPaneInTab.setLayout(nnAlgorithmPaneInTabLayout);
		nnAlgorithmPaneInTabLayout.setHorizontalGroup(nnAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			nnAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addGap(26, 26, 26)
				.addComponent(nnAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(33, 33, 33)
				.addGroup(
					nnAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(nnMinimumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMinimumLabel1, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					nnAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(nnMaximumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMaximumLabel1, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap()));
		nnAlgorithmPaneInTabLayout.setVerticalGroup(nnAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			nnAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					nnAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(unAlMinimumLabel1, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMaximumLabel1, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					nnAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(nnMinimumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(nnMaximumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(nnAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
							javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(31, Short.MAX_VALUE)));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		nnTabPane.add(nnAlgorithmPaneInTab, gridBagConstraints);

		algorithmsAndGramarTabbedPane.addTab(resourceMap.getString("nnTabPane.TabConstraints.tabTitle"), nnTabPane); // NOI18N

		unTabPane.setMaximumSize(new java.awt.Dimension(315, 221));
		unTabPane.setName("Unreachable"); // NOI18N
		unTabPane.setLayout(new java.awt.GridBagLayout());

		unGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap
			.getString("unGramarPaneInTab.border.title"))); // NOI18N
		unGramarPaneInTab.setName("unGramarPaneInTab"); // NOI18N

		unRulesNumberLabel.setText(resourceMap.getString("unRulesNumberLabel.text")); // NOI18N
		unRulesNumberLabel.setMaximumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel.setMinimumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel.setName("unRulesNumberLabel"); // NOI18N
		unRulesNumberLabel.setPreferredSize(new java.awt.Dimension(185, 17));

		unGrMinimumLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMinimumLabel.setText(resourceMap.getString("unGrMinimumLabel.text")); // NOI18N
		unGrMinimumLabel.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel.setName("unGrMinimumLabel"); // NOI18N
		unGrMinimumLabel.setPreferredSize(new java.awt.Dimension(80, 17));

		unLinesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		unLinesMaximumSpinner.setName(resourceMap.getString("unLinesMaximumSpinner.name")); // NOI18N
		unLinesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		unLinesMaximumSpinner.setValue(7);

		unGrMaximumLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMaximumLabel.setText(resourceMap.getString("unGrMaximumLabel.text")); // NOI18N
		unGrMaximumLabel.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel.setName("unGrMaximumLabel"); // NOI18N
		unGrMaximumLabel.setPreferredSize(new java.awt.Dimension(80, 17));

		unRulesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		unRulesMinimumSpinner.setMaximumSize(null);
		unRulesMinimumSpinner.setName(resourceMap.getString("unRulesMinimumSpinner.name")); // NOI18N
		unRulesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		unRulesMinimumSpinner.setValue(5);

		unLinesNumberLabel.setText(resourceMap.getString("unLinesNumberLabel.text")); // NOI18N
		unLinesNumberLabel.setMaximumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel.setMinimumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel.setName("unLinesNumberLabel"); // NOI18N
		unLinesNumberLabel.setPreferredSize(new java.awt.Dimension(185, 17));

		unRulesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		unRulesMaximumSpinner.setName(resourceMap.getString("unRulesMaximumSpinner.name")); // NOI18N
		unRulesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		unRulesMaximumSpinner.setValue(14);

		unLinesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		unLinesMinimumSpinner.setMaximumSize(null);
		unLinesMinimumSpinner.setName(resourceMap.getString("unLinesMinimumSpinner.name")); // NOI18N
		unLinesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		unLinesMinimumSpinner.setValue(3);

		javax.swing.GroupLayout unGramarPaneInTabLayout = new javax.swing.GroupLayout(unGramarPaneInTab);
		unGramarPaneInTab.setLayout(unGramarPaneInTabLayout);
		unGramarPaneInTabLayout.setHorizontalGroup(unGramarPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			unGramarPaneInTabLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					unGramarPaneInTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(unLinesNumberLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
						.addComponent(unRulesNumberLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					unGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unGrMinimumLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unRulesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unLinesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					unGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unGrMaximumLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unRulesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unLinesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap()));

		unGramarPaneInTabLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {
			unGrMaximumLabel, unGrMinimumLabel, unLinesMaximumSpinner, unLinesMinimumSpinner, unRulesMaximumSpinner,
			unRulesMinimumSpinner });

		unGramarPaneInTabLayout.setVerticalGroup(unGramarPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			unGramarPaneInTabLayout
				.createSequentialGroup()
				.addGroup(
					unGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
							unGramarPaneInTabLayout
								.createSequentialGroup()
								.addGap(23, 23, 23)
								.addComponent(unRulesNumberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
									javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(unLinesNumberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
									javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(
							unGramarPaneInTabLayout
								.createSequentialGroup()
								.addComponent(unGrMaximumLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(unRulesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(unLinesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(
							unGramarPaneInTabLayout
								.createSequentialGroup()
								.addComponent(unGrMinimumLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(unRulesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(unLinesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		unGramarPaneInTabLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {
			unLinesMaximumSpinner, unLinesMinimumSpinner, unLinesNumberLabel, unRulesMaximumSpinner,
			unRulesMinimumSpinner, unRulesNumberLabel });

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 7;
		gridBagConstraints.insets = new java.awt.Insets(6, 11, 11, 11);
		unTabPane.add(unGramarPaneInTab, gridBagConstraints);

		unAlgorithmPaneInTab.setMaximumSize(new java.awt.Dimension(301, 118));
		unAlgorithmPaneInTab.setName("unAlgorithmPaneInTab"); // NOI18N

		unAlMinimumLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMinimumLabel.setText(resourceMap.getString("unAlMinimumLabel.text")); // NOI18N
		unAlMinimumLabel.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel.setName("unAlMinimumLabel"); // NOI18N
		unAlMinimumLabel.setPreferredSize(new java.awt.Dimension(80, 17));

		unAlMaximumLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMaximumLabel.setText(resourceMap.getString("unAlMaximumLabel.text")); // NOI18N
		unAlMaximumLabel.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel.setName("unAlMaximumLabel"); // NOI18N
		unAlMaximumLabel.setPreferredSize(new java.awt.Dimension(80, 17));

		unMaximumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		unMaximumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		unMaximumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		unMaximumDifficultySpinner.setName(resourceMap.getString("unMaximumDifficultySpinner.name")); // NOI18N
		unMaximumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		unMinimumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		unMinimumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		unMinimumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		unMinimumDifficultySpinner.setName(resourceMap.getString("unMinimumDifficultySpinner.name")); // NOI18N
		unMinimumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		unAlgorithmDifficultyLabel.setText(resourceMap.getString("unAlgorithmDifficultyLabel.text")); // NOI18N
		unAlgorithmDifficultyLabel.setMaximumSize(new java.awt.Dimension(120, 17));
		unAlgorithmDifficultyLabel.setMinimumSize(new java.awt.Dimension(120, 17));
		unAlgorithmDifficultyLabel.setName("unAlgorithmDifficultyLabel"); // NOI18N
		unAlgorithmDifficultyLabel.setPreferredSize(new java.awt.Dimension(185, 17));

		javax.swing.GroupLayout unAlgorithmPaneInTabLayout = new javax.swing.GroupLayout(unAlgorithmPaneInTab);
		unAlgorithmPaneInTab.setLayout(unAlgorithmPaneInTabLayout);
		unAlgorithmPaneInTabLayout.setHorizontalGroup(unAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			unAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addGap(26, 26, 26)
				.addComponent(unAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(33, 33, 33)
				.addGroup(
					unAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(unMinimumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMinimumLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					unAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(unMaximumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMaximumLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap()));

		unAlgorithmPaneInTabLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {
			unAlMaximumLabel, unAlMinimumLabel, unMaximumDifficultySpinner, unMinimumDifficultySpinner });

		unAlgorithmPaneInTabLayout.setVerticalGroup(unAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			unAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					unAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(unAlMinimumLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMaximumLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					unAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(unMinimumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unMaximumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
							javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(31, Short.MAX_VALUE)));

		unAlgorithmPaneInTabLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {
			unMaximumDifficultySpinner, unMinimumDifficultySpinner });

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		unTabPane.add(unAlgorithmPaneInTab, gridBagConstraints);

		algorithmsAndGramarTabbedPane.addTab(resourceMap.getString("Unreachable.TabConstraints.tabTitle"), unTabPane); // NOI18N

		epTabPane.setMaximumSize(new java.awt.Dimension(315, 221));
		epTabPane.setName("epTabPane"); // NOI18N
		epTabPane.setLayout(new java.awt.GridBagLayout());

		epGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap
			.getString("epGramarPaneInTab.border.title"))); // NOI18N
		epGramarPaneInTab.setName("epGramarPaneInTab"); // NOI18N

		unRulesNumberLabel2.setText(resourceMap.getString("unRulesNumberLabel2.text")); // NOI18N
		unRulesNumberLabel2.setMaximumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel2.setMinimumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel2.setName("unRulesNumberLabel2"); // NOI18N
		unRulesNumberLabel2.setPreferredSize(new java.awt.Dimension(185, 17));

		unGrMinimumLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMinimumLabel2.setText(resourceMap.getString("unGrMinimumLabel2.text")); // NOI18N
		unGrMinimumLabel2.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel2.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel2.setName("unGrMinimumLabel2"); // NOI18N
		unGrMinimumLabel2.setPreferredSize(new java.awt.Dimension(80, 17));

		epLinesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		epLinesMaximumSpinner.setName(resourceMap.getString("epLinesMaximumSpinner.name")); // NOI18N
		epLinesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		epLinesMaximumSpinner.setValue(8);

		unGrMaximumLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMaximumLabel2.setText(resourceMap.getString("unGrMaximumLabel2.text")); // NOI18N
		unGrMaximumLabel2.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel2.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel2.setName("unGrMaximumLabel2"); // NOI18N
		unGrMaximumLabel2.setPreferredSize(new java.awt.Dimension(80, 17));

		epRulesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		epRulesMinimumSpinner.setMaximumSize(null);
		epRulesMinimumSpinner.setName(resourceMap.getString("epRulesMinimumSpinner.name")); // NOI18N
		epRulesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		epRulesMinimumSpinner.setValue(5);

		unLinesNumberLabel2.setText(resourceMap.getString("unLinesNumberLabel2.text")); // NOI18N
		unLinesNumberLabel2.setMaximumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel2.setMinimumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel2.setName("unLinesNumberLabel2"); // NOI18N
		unLinesNumberLabel2.setPreferredSize(new java.awt.Dimension(185, 17));

		epRulesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		epRulesMaximumSpinner.setName(resourceMap.getString("epRulesMaximumSpinner.name")); // NOI18N
		epRulesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		epRulesMaximumSpinner.setValue(16);

		epLinesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		epLinesMinimumSpinner.setMaximumSize(null);
		epLinesMinimumSpinner.setName(resourceMap.getString("epLinesMinimumSpinner.name")); // NOI18N
		epLinesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		epLinesMinimumSpinner.setValue(4);

		javax.swing.GroupLayout epGramarPaneInTabLayout = new javax.swing.GroupLayout(epGramarPaneInTab);
		epGramarPaneInTab.setLayout(epGramarPaneInTabLayout);
		epGramarPaneInTabLayout.setHorizontalGroup(epGramarPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			epGramarPaneInTabLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					epGramarPaneInTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(unLinesNumberLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
						.addComponent(unRulesNumberLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					epGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unGrMinimumLabel2, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(epRulesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(epLinesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					epGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unGrMaximumLabel2, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(epRulesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(epLinesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap()));
		epGramarPaneInTabLayout.setVerticalGroup(epGramarPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			epGramarPaneInTabLayout
				.createSequentialGroup()
				.addGroup(
					epGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
							epGramarPaneInTabLayout
								.createSequentialGroup()
								.addGap(23, 23, 23)
								.addComponent(unRulesNumberLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
									javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(unLinesNumberLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
									javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(
							epGramarPaneInTabLayout
								.createSequentialGroup()
								.addComponent(unGrMaximumLabel2, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(epRulesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(epLinesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(
							epGramarPaneInTabLayout
								.createSequentialGroup()
								.addComponent(unGrMinimumLabel2, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(epRulesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(epLinesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 7;
		gridBagConstraints.insets = new java.awt.Insets(6, 11, 11, 11);
		epTabPane.add(epGramarPaneInTab, gridBagConstraints);

		epAlgorithmPaneInTab.setMaximumSize(new java.awt.Dimension(301, 118));
		epAlgorithmPaneInTab.setName("epAlgorithmPaneInTab"); // NOI18N

		unAlMinimumLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMinimumLabel2.setText(resourceMap.getString("unAlMinimumLabel2.text")); // NOI18N
		unAlMinimumLabel2.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel2.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel2.setName("unAlMinimumLabel2"); // NOI18N
		unAlMinimumLabel2.setPreferredSize(new java.awt.Dimension(80, 17));

		unAlMaximumLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMaximumLabel2.setText(resourceMap.getString("unAlMaximumLabel2.text")); // NOI18N
		unAlMaximumLabel2.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel2.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel2.setName("unAlMaximumLabel2"); // NOI18N
		unAlMaximumLabel2.setPreferredSize(new java.awt.Dimension(80, 17));

		epMaximumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		epMaximumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		epMaximumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		epMaximumDifficultySpinner.setName(resourceMap.getString("epMaximumDifficultySpinner.name")); // NOI18N
		epMaximumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		epMinimumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		epMinimumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		epMinimumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		epMinimumDifficultySpinner.setName(resourceMap.getString("epMinimumDifficultySpinner.name")); // NOI18N
		epMinimumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		epAlgorithmDifficultyLabel.setText(resourceMap.getString("epAlgorithmDifficultyLabel.text")); // NOI18N
		epAlgorithmDifficultyLabel.setMaximumSize(new java.awt.Dimension(120, 17));
		epAlgorithmDifficultyLabel.setMinimumSize(new java.awt.Dimension(120, 17));
		epAlgorithmDifficultyLabel.setName("epAlgorithmDifficultyLabel"); // NOI18N
		epAlgorithmDifficultyLabel.setPreferredSize(new java.awt.Dimension(185, 17));

		javax.swing.GroupLayout epAlgorithmPaneInTabLayout = new javax.swing.GroupLayout(epAlgorithmPaneInTab);
		epAlgorithmPaneInTab.setLayout(epAlgorithmPaneInTabLayout);
		epAlgorithmPaneInTabLayout.setHorizontalGroup(epAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			epAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addGap(26, 26, 26)
				.addComponent(epAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(33, 33, 33)
				.addGroup(
					epAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(epMinimumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMinimumLabel2, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					epAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(epMaximumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMaximumLabel2, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap()));
		epAlgorithmPaneInTabLayout.setVerticalGroup(epAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			epAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					epAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(unAlMinimumLabel2, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMaximumLabel2, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					epAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(epMinimumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(epMaximumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(epAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
							javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(31, Short.MAX_VALUE)));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		epTabPane.add(epAlgorithmPaneInTab, gridBagConstraints);

		algorithmsAndGramarTabbedPane.addTab(resourceMap.getString("epTabPane.TabConstraints.tabTitle"), epTabPane); // NOI18N

		srTabPane.setMaximumSize(new java.awt.Dimension(315, 221));
		srTabPane.setName("srTabPane"); // NOI18N
		srTabPane.setLayout(new java.awt.GridBagLayout());

		srGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap
			.getString("srGramarPaneInTab.border.title"))); // NOI18N
		srGramarPaneInTab.setName("srGramarPaneInTab"); // NOI18N

		unRulesNumberLabel7.setText(resourceMap.getString("unRulesNumberLabel7.text")); // NOI18N
		unRulesNumberLabel7.setMaximumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel7.setMinimumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel7.setName("unRulesNumberLabel7"); // NOI18N
		unRulesNumberLabel7.setPreferredSize(new java.awt.Dimension(185, 17));

		unGrMinimumLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMinimumLabel7.setText(resourceMap.getString("unGrMinimumLabel7.text")); // NOI18N
		unGrMinimumLabel7.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel7.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel7.setName("unGrMinimumLabel7"); // NOI18N
		unGrMinimumLabel7.setPreferredSize(new java.awt.Dimension(80, 17));

		srLinesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		srLinesMaximumSpinner.setName(resourceMap.getString("srLinesMaximumSpinner.name")); // NOI18N
		srLinesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		srLinesMaximumSpinner.setValue(8);

		unGrMaximumLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMaximumLabel7.setText(resourceMap.getString("unGrMaximumLabel7.text")); // NOI18N
		unGrMaximumLabel7.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel7.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel7.setName("unGrMaximumLabel7"); // NOI18N
		unGrMaximumLabel7.setPreferredSize(new java.awt.Dimension(80, 17));

		srRulesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		srRulesMinimumSpinner.setMaximumSize(null);
		srRulesMinimumSpinner.setName(resourceMap.getString("srRulesMinimumSpinner.name")); // NOI18N
		srRulesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		srRulesMinimumSpinner.setValue(9);

		unLinesNumberLabel7.setText(resourceMap.getString("unLinesNumberLabel7.text")); // NOI18N
		unLinesNumberLabel7.setMaximumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel7.setMinimumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel7.setName("unLinesNumberLabel7"); // NOI18N
		unLinesNumberLabel7.setPreferredSize(new java.awt.Dimension(185, 17));

		srRulesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		srRulesMaximumSpinner.setName(resourceMap.getString("srRulesMaximumSpinner.name")); // NOI18N
		srRulesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		srRulesMaximumSpinner.setValue(22);

		srLinesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		srLinesMinimumSpinner.setMaximumSize(null);
		srLinesMinimumSpinner.setName(resourceMap.getString("srLinesMinimumSpinner.name")); // NOI18N
		srLinesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		srLinesMinimumSpinner.setValue(4);

		javax.swing.GroupLayout srGramarPaneInTabLayout = new javax.swing.GroupLayout(srGramarPaneInTab);
		srGramarPaneInTab.setLayout(srGramarPaneInTabLayout);
		srGramarPaneInTabLayout.setHorizontalGroup(srGramarPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			srGramarPaneInTabLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					srGramarPaneInTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(unRulesNumberLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
						.addComponent(unLinesNumberLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					srGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unGrMinimumLabel7, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(srRulesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(srLinesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					srGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unGrMaximumLabel7, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(srRulesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(srLinesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap()));
		srGramarPaneInTabLayout.setVerticalGroup(srGramarPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			srGramarPaneInTabLayout
				.createSequentialGroup()
				.addGroup(
					srGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unGrMinimumLabel7, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unGrMaximumLabel7, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					srGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unRulesNumberLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
							javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(srRulesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(srRulesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					srGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unLinesNumberLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
							javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(srLinesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(srLinesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 7;
		gridBagConstraints.insets = new java.awt.Insets(6, 11, 11, 11);
		srTabPane.add(srGramarPaneInTab, gridBagConstraints);

		srAlgorithmPaneInTab.setMaximumSize(new java.awt.Dimension(301, 118));
		srAlgorithmPaneInTab.setName("srAlgorithmPaneInTab"); // NOI18N

		unAlMinimumLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMinimumLabel7.setText(resourceMap.getString("unAlMinimumLabel7.text")); // NOI18N
		unAlMinimumLabel7.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel7.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel7.setName("unAlMinimumLabel7"); // NOI18N
		unAlMinimumLabel7.setPreferredSize(new java.awt.Dimension(80, 17));

		unAlMaximumLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMaximumLabel7.setText(resourceMap.getString("unAlMaximumLabel7.text")); // NOI18N
		unAlMaximumLabel7.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel7.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel7.setName("unAlMaximumLabel7"); // NOI18N
		unAlMaximumLabel7.setPreferredSize(new java.awt.Dimension(80, 17));

		srMaximumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		srMaximumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		srMaximumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		srMaximumDifficultySpinner.setName(resourceMap.getString("srMaximumDifficultySpinner.name")); // NOI18N
		srMaximumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		srMinimumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		srMinimumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		srMinimumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		srMinimumDifficultySpinner.setName(resourceMap.getString("srMinimumDifficultySpinner.name")); // NOI18N
		srMinimumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		srAlgorithmDifficultyLabel.setText(resourceMap.getString("srAlgorithmDifficultyLabel.text")); // NOI18N
		srAlgorithmDifficultyLabel.setMaximumSize(new java.awt.Dimension(120, 17));
		srAlgorithmDifficultyLabel.setMinimumSize(new java.awt.Dimension(120, 17));
		srAlgorithmDifficultyLabel.setName("srAlgorithmDifficultyLabel"); // NOI18N
		srAlgorithmDifficultyLabel.setPreferredSize(new java.awt.Dimension(185, 17));

		javax.swing.GroupLayout srAlgorithmPaneInTabLayout = new javax.swing.GroupLayout(srAlgorithmPaneInTab);
		srAlgorithmPaneInTab.setLayout(srAlgorithmPaneInTabLayout);
		srAlgorithmPaneInTabLayout.setHorizontalGroup(srAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			srAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addGap(26, 26, 26)
				.addComponent(srAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(33, 33, 33)
				.addGroup(
					srAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(srMinimumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMinimumLabel7, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					srAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(srMaximumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMaximumLabel7, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap()));
		srAlgorithmPaneInTabLayout.setVerticalGroup(srAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			srAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					srAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(unAlMinimumLabel7, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMaximumLabel7, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					srAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(srMinimumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(srMaximumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(srAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
							javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(31, Short.MAX_VALUE)));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		srTabPane.add(srAlgorithmPaneInTab, gridBagConstraints);

		algorithmsAndGramarTabbedPane.addTab(resourceMap.getString("srTabPane.TabConstraints.tabTitle"), srTabPane); // NOI18N

		lrTabPane.setMaximumSize(new java.awt.Dimension(315, 221));
		lrTabPane.setName("lrTabPane"); // NOI18N
		lrTabPane.setLayout(new java.awt.GridBagLayout());

		lrGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap
			.getString("lrGramarPaneInTab.border.title"))); // NOI18N
		lrGramarPaneInTab.setName("lrGramarPaneInTab"); // NOI18N

		unRulesNumberLabel4.setText(resourceMap.getString("unRulesNumberLabel4.text")); // NOI18N
		unRulesNumberLabel4.setMaximumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel4.setMinimumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel4.setName("unRulesNumberLabel4"); // NOI18N
		unRulesNumberLabel4.setPreferredSize(new java.awt.Dimension(185, 17));

		unGrMinimumLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMinimumLabel4.setText(resourceMap.getString("unGrMinimumLabel4.text")); // NOI18N
		unGrMinimumLabel4.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel4.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel4.setName("unGrMinimumLabel4"); // NOI18N
		unGrMinimumLabel4.setPreferredSize(new java.awt.Dimension(80, 17));

		lrLinesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		lrLinesMaximumSpinner.setName(resourceMap.getString("lrLinesMaximumSpinner.name")); // NOI18N
		lrLinesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		lrLinesMaximumSpinner.setValue(11);

		unGrMaximumLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMaximumLabel4.setText(resourceMap.getString("unGrMaximumLabel4.text")); // NOI18N
		unGrMaximumLabel4.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel4.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel4.setName("unGrMaximumLabel4"); // NOI18N
		unGrMaximumLabel4.setPreferredSize(new java.awt.Dimension(80, 17));

		lrRulesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		lrRulesMinimumSpinner.setMaximumSize(null);
		lrRulesMinimumSpinner.setName(resourceMap.getString("lrRulesMinimumSpinner.name")); // NOI18N
		lrRulesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		lrRulesMinimumSpinner.setValue(10);

		unLinesNumberLabel4.setText(resourceMap.getString("unLinesNumberLabel4.text")); // NOI18N
		unLinesNumberLabel4.setMaximumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel4.setMinimumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel4.setName("unLinesNumberLabel4"); // NOI18N
		unLinesNumberLabel4.setPreferredSize(new java.awt.Dimension(185, 17));

		lrRulesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		lrRulesMaximumSpinner.setName(resourceMap.getString("lrRulesMaximumSpinner.name")); // NOI18N
		lrRulesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		lrRulesMaximumSpinner.setValue(30);

		lrLinesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		lrLinesMinimumSpinner.setMaximumSize(null);
		lrLinesMinimumSpinner.setName(resourceMap.getString("lrLinesMinimumSpinner.name")); // NOI18N
		lrLinesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		lrLinesMinimumSpinner.setValue(4);

		javax.swing.GroupLayout lrGramarPaneInTabLayout = new javax.swing.GroupLayout(lrGramarPaneInTab);
		lrGramarPaneInTab.setLayout(lrGramarPaneInTabLayout);
		lrGramarPaneInTabLayout.setHorizontalGroup(lrGramarPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			lrGramarPaneInTabLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					lrGramarPaneInTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(unRulesNumberLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
						.addComponent(unLinesNumberLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					lrGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unGrMinimumLabel4, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(lrRulesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(lrLinesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					lrGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unGrMaximumLabel4, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(lrRulesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(lrLinesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap()));
		lrGramarPaneInTabLayout.setVerticalGroup(lrGramarPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			lrGramarPaneInTabLayout
				.createSequentialGroup()
				.addGroup(
					lrGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unGrMinimumLabel4, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unGrMaximumLabel4, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					lrGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unRulesNumberLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
							javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(lrRulesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(lrRulesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					lrGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unLinesNumberLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
							javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(lrLinesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(lrLinesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 7;
		gridBagConstraints.insets = new java.awt.Insets(6, 11, 11, 11);
		lrTabPane.add(lrGramarPaneInTab, gridBagConstraints);

		lrAlgorithmPaneInTab.setMaximumSize(new java.awt.Dimension(301, 118));
		lrAlgorithmPaneInTab.setName("lrAlgorithmPaneInTab"); // NOI18N

		unAlMinimumLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMinimumLabel4.setText(resourceMap.getString("unAlMinimumLabel4.text")); // NOI18N
		unAlMinimumLabel4.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel4.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel4.setName("unAlMinimumLabel4"); // NOI18N
		unAlMinimumLabel4.setPreferredSize(new java.awt.Dimension(80, 17));

		unAlMaximumLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMaximumLabel4.setText(resourceMap.getString("unAlMaximumLabel4.text")); // NOI18N
		unAlMaximumLabel4.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel4.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel4.setName("unAlMaximumLabel4"); // NOI18N
		unAlMaximumLabel4.setPreferredSize(new java.awt.Dimension(80, 17));

		lrMaximumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		lrMaximumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		lrMaximumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		lrMaximumDifficultySpinner.setName(resourceMap.getString("lrMaximumDifficultySpinner.name")); // NOI18N
		lrMaximumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		lrMinimumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		lrMinimumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		lrMinimumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		lrMinimumDifficultySpinner.setName(resourceMap.getString("lrMinimumDifficultySpinner.name")); // NOI18N
		lrMinimumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		lrAlgorithmDifficultyLabel.setText(resourceMap.getString("lrAlgorithmDifficultyLabel.text")); // NOI18N
		lrAlgorithmDifficultyLabel.setMaximumSize(new java.awt.Dimension(120, 17));
		lrAlgorithmDifficultyLabel.setMinimumSize(new java.awt.Dimension(120, 17));
		lrAlgorithmDifficultyLabel.setName("lrAlgorithmDifficultyLabel"); // NOI18N
		lrAlgorithmDifficultyLabel.setPreferredSize(new java.awt.Dimension(185, 17));

		javax.swing.GroupLayout lrAlgorithmPaneInTabLayout = new javax.swing.GroupLayout(lrAlgorithmPaneInTab);
		lrAlgorithmPaneInTab.setLayout(lrAlgorithmPaneInTabLayout);
		lrAlgorithmPaneInTabLayout.setHorizontalGroup(lrAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			lrAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addGap(26, 26, 26)
				.addComponent(lrAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(33, 33, 33)
				.addGroup(
					lrAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(lrMinimumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMinimumLabel4, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					lrAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(lrMaximumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMaximumLabel4, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap()));
		lrAlgorithmPaneInTabLayout.setVerticalGroup(lrAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			lrAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					lrAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(unAlMinimumLabel4, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMaximumLabel4, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					lrAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(lrMinimumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(lrMaximumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(lrAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
							javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(31, Short.MAX_VALUE)));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		lrTabPane.add(lrAlgorithmPaneInTab, gridBagConstraints);

		algorithmsAndGramarTabbedPane.addTab(resourceMap.getString("lrTabPane.TabConstraints.tabTitle"), lrTabPane); // NOI18N

		cfTabPane.setMaximumSize(new java.awt.Dimension(315, 221));
		cfTabPane.setName("cfTabPane"); // NOI18N
		cfTabPane.setLayout(new java.awt.GridBagLayout());

		cfGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap
			.getString("cfGramarPaneInTab.border.title"))); // NOI18N
		cfGramarPaneInTab.setName("cfGramarPaneInTab"); // NOI18N

		unRulesNumberLabel5.setText(resourceMap.getString("unRulesNumberLabel5.text")); // NOI18N
		unRulesNumberLabel5.setMaximumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel5.setMinimumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel5.setName("unRulesNumberLabel5"); // NOI18N
		unRulesNumberLabel5.setPreferredSize(new java.awt.Dimension(185, 17));

		unGrMinimumLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMinimumLabel5.setText(resourceMap.getString("unGrMinimumLabel5.text")); // NOI18N
		unGrMinimumLabel5.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel5.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel5.setName("unGrMinimumLabel5"); // NOI18N
		unGrMinimumLabel5.setPreferredSize(new java.awt.Dimension(80, 17));

		cfLinesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		cfLinesMaximumSpinner.setName(resourceMap.getString("cfLinesMaximumSpinner.name")); // NOI18N
		cfLinesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		cfLinesMaximumSpinner.setValue(18);

		unGrMaximumLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMaximumLabel5.setText(resourceMap.getString("unGrMaximumLabel5.text")); // NOI18N
		unGrMaximumLabel5.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel5.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel5.setName("unGrMaximumLabel5"); // NOI18N
		unGrMaximumLabel5.setPreferredSize(new java.awt.Dimension(80, 17));

		cfRulesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		cfRulesMinimumSpinner.setMaximumSize(null);
		cfRulesMinimumSpinner.setName(resourceMap.getString("cfRulesMinimumSpinner.name")); // NOI18N
		cfRulesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		cfRulesMinimumSpinner.setValue(10);

		unLinesNumberLabel5.setText(resourceMap.getString("unLinesNumberLabel5.text")); // NOI18N
		unLinesNumberLabel5.setMaximumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel5.setMinimumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel5.setName("unLinesNumberLabel5"); // NOI18N
		unLinesNumberLabel5.setPreferredSize(new java.awt.Dimension(185, 17));

		cfRulesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		cfRulesMaximumSpinner.setName(resourceMap.getString("cfRulesMaximumSpinner.name")); // NOI18N
		cfRulesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		cfRulesMaximumSpinner.setValue(26);

		cfLinesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		cfLinesMinimumSpinner.setMaximumSize(null);
		cfLinesMinimumSpinner.setName(resourceMap.getString("cfLinesMinimumSpinner.name")); // NOI18N
		cfLinesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		cfLinesMinimumSpinner.setValue(7);

		javax.swing.GroupLayout cfGramarPaneInTabLayout = new javax.swing.GroupLayout(cfGramarPaneInTab);
		cfGramarPaneInTab.setLayout(cfGramarPaneInTabLayout);
		cfGramarPaneInTabLayout.setHorizontalGroup(cfGramarPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			cfGramarPaneInTabLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					cfGramarPaneInTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(unLinesNumberLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
						.addComponent(unRulesNumberLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					cfGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unGrMinimumLabel5, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(cfRulesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(cfLinesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					cfGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unGrMaximumLabel5, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(cfRulesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(cfLinesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap()));
		cfGramarPaneInTabLayout.setVerticalGroup(cfGramarPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			cfGramarPaneInTabLayout
				.createSequentialGroup()
				.addGroup(
					cfGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
							cfGramarPaneInTabLayout
								.createSequentialGroup()
								.addGap(23, 23, 23)
								.addComponent(unRulesNumberLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
									javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(unLinesNumberLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
									javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(
							cfGramarPaneInTabLayout
								.createSequentialGroup()
								.addComponent(unGrMaximumLabel5, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(cfRulesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(cfLinesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(
							cfGramarPaneInTabLayout
								.createSequentialGroup()
								.addComponent(unGrMinimumLabel5, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(cfRulesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(cfLinesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 7;
		gridBagConstraints.insets = new java.awt.Insets(6, 11, 11, 11);
		cfTabPane.add(cfGramarPaneInTab, gridBagConstraints);

		cfAlgorithmPaneInTab.setMaximumSize(new java.awt.Dimension(301, 118));
		cfAlgorithmPaneInTab.setName("cfAlgorithmPaneInTab"); // NOI18N

		unAlMinimumLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMinimumLabel5.setText(resourceMap.getString("unAlMinimumLabel5.text")); // NOI18N
		unAlMinimumLabel5.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel5.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel5.setName("unAlMinimumLabel5"); // NOI18N
		unAlMinimumLabel5.setPreferredSize(new java.awt.Dimension(80, 17));

		unAlMaximumLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMaximumLabel5.setText(resourceMap.getString("unAlMaximumLabel5.text")); // NOI18N
		unAlMaximumLabel5.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel5.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel5.setName("unAlMaximumLabel5"); // NOI18N
		unAlMaximumLabel5.setPreferredSize(new java.awt.Dimension(80, 17));

		cfMaximumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		cfMaximumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		cfMaximumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		cfMaximumDifficultySpinner.setName(resourceMap.getString("cfMaximumDifficultySpinner.name")); // NOI18N
		cfMaximumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		cfMinimumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		cfMinimumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		cfMinimumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		cfMinimumDifficultySpinner.setName(resourceMap.getString("cfMinimumDifficultySpinner.name")); // NOI18N
		cfMinimumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		cfAlgorithmDifficultyLabel.setText(resourceMap.getString("cfAlgorithmDifficultyLabel.text")); // NOI18N
		cfAlgorithmDifficultyLabel.setMaximumSize(new java.awt.Dimension(120, 17));
		cfAlgorithmDifficultyLabel.setMinimumSize(new java.awt.Dimension(120, 17));
		cfAlgorithmDifficultyLabel.setName("cfAlgorithmDifficultyLabel"); // NOI18N
		cfAlgorithmDifficultyLabel.setPreferredSize(new java.awt.Dimension(185, 17));

		javax.swing.GroupLayout cfAlgorithmPaneInTabLayout = new javax.swing.GroupLayout(cfAlgorithmPaneInTab);
		cfAlgorithmPaneInTab.setLayout(cfAlgorithmPaneInTabLayout);
		cfAlgorithmPaneInTabLayout.setHorizontalGroup(cfAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			cfAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addGap(26, 26, 26)
				.addComponent(cfAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(33, 33, 33)
				.addGroup(
					cfAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(cfMinimumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMinimumLabel5, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					cfAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(cfMaximumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMaximumLabel5, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap()));
		cfAlgorithmPaneInTabLayout.setVerticalGroup(cfAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			cfAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					cfAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(unAlMinimumLabel5, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMaximumLabel5, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					cfAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(cfMinimumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(cfMaximumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(cfAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
							javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(31, Short.MAX_VALUE)));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		cfTabPane.add(cfAlgorithmPaneInTab, gridBagConstraints);

		algorithmsAndGramarTabbedPane.addTab(resourceMap.getString("cfTabPane.TabConstraints.tabTitle"), cfTabPane); // NOI18N

		gfTabPane.setMaximumSize(new java.awt.Dimension(315, 221));
		gfTabPane.setName("gfTabPane"); // NOI18N
		gfTabPane.setLayout(new java.awt.GridBagLayout());

		gfGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap
			.getString("gfGramarPaneInTab.border.title"))); // NOI18N
		gfGramarPaneInTab.setName("gfGramarPaneInTab"); // NOI18N

		unRulesNumberLabel6.setText(resourceMap.getString("unRulesNumberLabel6.text")); // NOI18N
		unRulesNumberLabel6.setMaximumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel6.setMinimumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel6.setName("unRulesNumberLabel6"); // NOI18N
		unRulesNumberLabel6.setPreferredSize(new java.awt.Dimension(185, 17));

		unGrMinimumLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMinimumLabel6.setText(resourceMap.getString("unGrMinimumLabel6.text")); // NOI18N
		unGrMinimumLabel6.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel6.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel6.setName("unGrMinimumLabel6"); // NOI18N
		unGrMinimumLabel6.setPreferredSize(new java.awt.Dimension(80, 17));

		gfLinesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		gfLinesMaximumSpinner.setName(resourceMap.getString("gfLinesMaximumSpinner.name")); // NOI18N
		gfLinesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		gfLinesMaximumSpinner.setValue(13);

		unGrMaximumLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMaximumLabel6.setText(resourceMap.getString("unGrMaximumLabel6.text")); // NOI18N
		unGrMaximumLabel6.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel6.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel6.setName("unGrMaximumLabel6"); // NOI18N
		unGrMaximumLabel6.setPreferredSize(new java.awt.Dimension(80, 17));

		gfRulesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		gfRulesMinimumSpinner.setMaximumSize(null);
		gfRulesMinimumSpinner.setName(resourceMap.getString("gfRulesMinimumSpinner.name")); // NOI18N
		gfRulesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		gfRulesMinimumSpinner.setValue(13);

		unLinesNumberLabel6.setText(resourceMap.getString("unLinesNumberLabel6.text")); // NOI18N
		unLinesNumberLabel6.setMaximumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel6.setMinimumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel6.setName("unLinesNumberLabel6"); // NOI18N
		unLinesNumberLabel6.setPreferredSize(new java.awt.Dimension(185, 17));

		gfRulesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		gfRulesMaximumSpinner.setName(resourceMap.getString("gfRulesMaximumSpinner.name")); // NOI18N
		gfRulesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		gfRulesMaximumSpinner.setValue(60);

		gfLinesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		gfLinesMinimumSpinner.setMaximumSize(null);
		gfLinesMinimumSpinner.setName(resourceMap.getString("gfLinesMinimumSpinner.name")); // NOI18N
		gfLinesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		gfLinesMinimumSpinner.setValue(7);

		javax.swing.GroupLayout gfGramarPaneInTabLayout = new javax.swing.GroupLayout(gfGramarPaneInTab);
		gfGramarPaneInTab.setLayout(gfGramarPaneInTabLayout);
		gfGramarPaneInTabLayout.setHorizontalGroup(gfGramarPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			gfGramarPaneInTabLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					gfGramarPaneInTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(unLinesNumberLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
						.addComponent(unRulesNumberLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					gfGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unGrMinimumLabel6, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(gfRulesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(gfLinesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					gfGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
						.addComponent(unGrMaximumLabel6, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(gfRulesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(gfLinesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap()));
		gfGramarPaneInTabLayout.setVerticalGroup(gfGramarPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			gfGramarPaneInTabLayout
				.createSequentialGroup()
				.addGroup(
					gfGramarPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
							gfGramarPaneInTabLayout
								.createSequentialGroup()
								.addGap(23, 23, 23)
								.addComponent(unRulesNumberLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
									javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(unLinesNumberLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
									javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(
							gfGramarPaneInTabLayout
								.createSequentialGroup()
								.addComponent(unGrMaximumLabel6, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(gfRulesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(gfLinesMaximumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(
							gfGramarPaneInTabLayout
								.createSequentialGroup()
								.addComponent(unGrMinimumLabel6, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6)
								.addComponent(gfRulesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(gfLinesMinimumSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 7;
		gridBagConstraints.insets = new java.awt.Insets(6, 11, 11, 11);
		gfTabPane.add(gfGramarPaneInTab, gridBagConstraints);

		gfAlgorithmPaneInTab.setMaximumSize(new java.awt.Dimension(301, 118));
		gfAlgorithmPaneInTab.setName("gfAlgorithmPaneInTab"); // NOI18N

		unAlMinimumLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMinimumLabel6.setText(resourceMap.getString("unAlMinimumLabel6.text")); // NOI18N
		unAlMinimumLabel6.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel6.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel6.setName("unAlMinimumLabel6"); // NOI18N
		unAlMinimumLabel6.setPreferredSize(new java.awt.Dimension(80, 17));

		unAlMaximumLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMaximumLabel6.setText(resourceMap.getString("unAlMaximumLabel6.text")); // NOI18N
		unAlMaximumLabel6.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel6.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel6.setName("unAlMaximumLabel6"); // NOI18N
		unAlMaximumLabel6.setPreferredSize(new java.awt.Dimension(80, 17));

		gfMaximumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		gfMaximumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		gfMaximumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		gfMaximumDifficultySpinner.setName(resourceMap.getString("gfMaximumDifficultySpinner.name")); // NOI18N
		gfMaximumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		gfMinimumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		gfMinimumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		gfMinimumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		gfMinimumDifficultySpinner.setName(resourceMap.getString("gfMinimumDifficultySpinner.name")); // NOI18N
		gfMinimumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		gfAlgorithmDifficultyLabel.setText(resourceMap.getString("gfAlgorithmDifficultyLabel.text")); // NOI18N
		gfAlgorithmDifficultyLabel.setMaximumSize(new java.awt.Dimension(120, 17));
		gfAlgorithmDifficultyLabel.setMinimumSize(new java.awt.Dimension(120, 17));
		gfAlgorithmDifficultyLabel.setName("gfAlgorithmDifficultyLabel"); // NOI18N
		gfAlgorithmDifficultyLabel.setPreferredSize(new java.awt.Dimension(185, 17));

		javax.swing.GroupLayout gfAlgorithmPaneInTabLayout = new javax.swing.GroupLayout(gfAlgorithmPaneInTab);
		gfAlgorithmPaneInTab.setLayout(gfAlgorithmPaneInTabLayout);
		gfAlgorithmPaneInTabLayout.setHorizontalGroup(gfAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			gfAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addGap(26, 26, 26)
				.addComponent(gfAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(33, 33, 33)
				.addGroup(
					gfAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(gfMinimumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMinimumLabel6, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					gfAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(gfMaximumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMaximumLabel6, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap()));
		gfAlgorithmPaneInTabLayout.setVerticalGroup(gfAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			gfAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					gfAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(unAlMinimumLabel6, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(unAlMaximumLabel6, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					gfAlgorithmPaneInTabLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(gfMinimumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(gfMaximumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(gfAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
							javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(31, Short.MAX_VALUE)));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gfTabPane.add(gfAlgorithmPaneInTab, gridBagConstraints);

		algorithmsAndGramarTabbedPane.addTab(resourceMap.getString("gfTabPane.TabConstraints.tabTitle"), gfTabPane); // NOI18N

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(6, 11, 11, 11);
		settingsPane.add(algorithmsAndGramarTabbedPane, gridBagConstraints);

		outputGramarPane.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap
			.getString("outputGramarPane.border.title"))); // NOI18N
		outputGramarPane.setName("outputGramarPane"); // NOI18N

		ogRulesNumberLabel.setText(resourceMap.getString("ogRulesNumberLabel.text")); // NOI18N
		ogRulesNumberLabel.setMaximumSize(new java.awt.Dimension(78, 14));
		ogRulesNumberLabel.setMinimumSize(new java.awt.Dimension(78, 14));
		ogRulesNumberLabel.setName("ogRulesNumberLabel"); // NOI18N
		ogRulesNumberLabel.setPreferredSize(new java.awt.Dimension(78, 14));

		ogMinLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		ogMinLabel.setText(resourceMap.getString("ogMinLabel.text")); // NOI18N
		ogMinLabel.setName("ogMinLabel"); // NOI18N

		ogMinRulesSpinner.setComponentPopupMenu(disPopUpMenu);
		ogMinRulesSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		ogMinRulesSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		ogMinRulesSpinner.setName(resourceMap.getString("ogMinRulesSpinner.name")); // NOI18N
		ogMinRulesSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		ogMinRulesSpinner.setValue(5);

		ogMaxLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		ogMaxLabel.setText(resourceMap.getString("ogMaxLabel.text")); // NOI18N
		ogMaxLabel.setMaximumSize(new java.awt.Dimension(40, 14));
		ogMaxLabel.setMinimumSize(new java.awt.Dimension(40, 14));
		ogMaxLabel.setName("ogMaxLabel"); // NOI18N
		ogMaxLabel.setPreferredSize(new java.awt.Dimension(40, 14));

		ogMinLinesSpinner.setComponentPopupMenu(disPopUpMenu);
		ogMinLinesSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		ogMinLinesSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		ogMinLinesSpinner.setName(resourceMap.getString("ogMinLinesSpinner.name")); // NOI18N
		ogMinLinesSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		ogMinLinesSpinner.setValue(3);

		ogLinesNumberLabel.setText(resourceMap.getString("ogLinesNumberLabel.text")); // NOI18N
		ogLinesNumberLabel.setName("ogLinesNumberLabel"); // NOI18N

		ogMaxLinesSpinner.setComponentPopupMenu(disPopUpMenu);
		ogMaxLinesSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		ogMaxLinesSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		ogMaxLinesSpinner.setName(resourceMap.getString("ogMaxLinesSpinner.name")); // NOI18N
		ogMaxLinesSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		ogMaxLinesSpinner.setValue(12);

		ogMaxRulesSpinner.setComponentPopupMenu(disPopUpMenu);
		ogMaxRulesSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		ogMaxRulesSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		ogMaxRulesSpinner.setName(resourceMap.getString("ogMaxRulesSpinner.name")); // NOI18N
		ogMaxRulesSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		ogMaxRulesSpinner.setValue(20);

		javax.swing.GroupLayout outputGramarPaneLayout = new javax.swing.GroupLayout(outputGramarPane);
		outputGramarPane.setLayout(outputGramarPaneLayout);
		outputGramarPaneLayout.setHorizontalGroup(outputGramarPaneLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(
				outputGramarPaneLayout
					.createSequentialGroup()
					.addContainerGap()
					.addGroup(
						outputGramarPaneLayout
							.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(
								outputGramarPaneLayout
									.createSequentialGroup()
									.addComponent(ogLinesNumberLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 227,
										Short.MAX_VALUE)
									.addGap(4, 4, 4)
									.addComponent(ogMinLinesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 81,
										javax.swing.GroupLayout.PREFERRED_SIZE)
									.addGap(6, 6, 6)
									.addComponent(ogMaxLinesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
							.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								outputGramarPaneLayout
									.createSequentialGroup()
									.addGroup(
										outputGramarPaneLayout
											.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
											.addGroup(
												outputGramarPaneLayout
													.createSequentialGroup()
													.addComponent(ogRulesNumberLabel,
														javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
													.addGap(4, 4, 4)
													.addComponent(ogMinRulesSpinner,
														javax.swing.GroupLayout.PREFERRED_SIZE, 81,
														javax.swing.GroupLayout.PREFERRED_SIZE))
											.addComponent(ogMinLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 81,
												javax.swing.GroupLayout.PREFERRED_SIZE))
									.addGap(6, 6, 6)
									.addGroup(
										outputGramarPaneLayout
											.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
											.addComponent(ogMaxLabel, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addComponent(ogMaxRulesSpinner, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
					.addContainerGap()));
		outputGramarPaneLayout.setVerticalGroup(outputGramarPaneLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			outputGramarPaneLayout
				.createSequentialGroup()
				.addGroup(
					outputGramarPaneLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(ogMaxLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(ogMinLabel))
				.addGap(6, 6, 6)
				.addGroup(
					outputGramarPaneLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
							outputGramarPaneLayout
								.createSequentialGroup()
								.addGap(5, 5, 5)
								.addComponent(ogRulesNumberLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
									javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addComponent(ogMinRulesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(ogMaxRulesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGap(6, 6, 6)
				.addGroup(
					outputGramarPaneLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
							outputGramarPaneLayout.createSequentialGroup().addGap(4, 4, 4)
								.addComponent(ogLinesNumberLabel))
						.addComponent(ogMinLinesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(ogMaxLinesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(6, 11, 0, 11);
		settingsPane.add(outputGramarPane, gridBagConstraints);

		settingsScrollPane.setViewportView(settingsPane);

		javax.swing.GroupLayout leftMainPanelLayout = new javax.swing.GroupLayout(leftMainPanel);
		leftMainPanel.setLayout(leftMainPanelLayout);
		leftMainPanelLayout.setHorizontalGroup(leftMainPanelLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			javax.swing.GroupLayout.Alignment.TRAILING,
			leftMainPanelLayout.createSequentialGroup().addContainerGap()
				.addComponent(settingsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
				.addContainerGap()));
		leftMainPanelLayout.setVerticalGroup(leftMainPanelLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			leftMainPanelLayout.createSequentialGroup().addContainerGap()
				.addComponent(settingsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 582, Short.MAX_VALUE)
				.addContainerGap()));

		mainPanel.add(leftMainPanel);

		rightMainPanel.setName("rightMainPanel"); // NOI18N

		resultTabbledPane.setMinimumSize(new java.awt.Dimension(23, 80));
		resultTabbledPane.setName("resultTabbledPane"); // NOI18N

		jScrollPane3.setName("jScrollPane3"); // NOI18N

		plain_skArea.setColumns(20);
		plain_skArea.setEditable(false);
		plain_skArea.setFont(resourceMap.getFont("plain_skArea.font")); // NOI18N
		plain_skArea.setRows(5);
		plain_skArea.setName("plain_skArea"); // NOI18N
		jScrollPane3.setViewportView(plain_skArea);

		resultTabbledPane.addTab(resourceMap.getString("jScrollPane3.TabConstraints.tabTitle"), jScrollPane3); // NOI18N

		jScrollPane2.setName("jScrollPane2"); // NOI18N

		latex_skArea.setColumns(20);
		latex_skArea.setEditable(false);
		latex_skArea.setFont(resourceMap.getFont("latex_skArea.font")); // NOI18N
		latex_skArea.setRows(5);
		latex_skArea.setName("latex_skArea"); // NOI18N
		jScrollPane2.setViewportView(latex_skArea);

		resultTabbledPane.addTab(resourceMap.getString("jScrollPane2.TabConstraints.tabTitle"), jScrollPane2); // NOI18N

		validationScroll.setName("Validation version"); // NOI18N

		is_skArea.setColumns(20);
		is_skArea.setEditable(false);
		is_skArea.setFont(resourceMap.getFont("is_skArea.font")); // NOI18N
		is_skArea.setRows(5);
		is_skArea.setName("is_skArea"); // NOI18N
		validationScroll.setViewportView(is_skArea);

		resultTabbledPane.addTab("IS CZ", validationScroll);

		jScrollPane7.setName("jScrollPane7"); // NOI18N

		plain_enArea.setColumns(20);
		plain_enArea.setEditable(false);
		plain_enArea.setFont(resourceMap.getFont("plain_enArea.font")); // NOI18N
		plain_enArea.setRows(5);
		plain_enArea.setName("plain_enArea"); // NOI18N
		jScrollPane7.setViewportView(plain_enArea);

		resultTabbledPane.addTab(resourceMap.getString("jScrollPane7.TabConstraints.tabTitle"), jScrollPane7); // NOI18N

		jScrollPane4.setName("jScrollPane4"); // NOI18N

		latex_enArea.setColumns(20);
		latex_enArea.setEditable(false);
		latex_enArea.setFont(resourceMap.getFont("latex_enArea.font")); // NOI18N
		latex_enArea.setRows(5);
		latex_enArea.setName("latex_enArea"); // NOI18N
		jScrollPane4.setViewportView(latex_enArea);

		resultTabbledPane.addTab(resourceMap.getString("jScrollPane4.TabConstraints.tabTitle"), jScrollPane4); // NOI18N

		jScrollPane5.setName("jScrollPane5"); // NOI18N

		is_enArea.setColumns(20);
		is_enArea.setEditable(false);
		is_enArea.setFont(resourceMap.getFont("is_enArea.font")); // NOI18N
		is_enArea.setRows(5);
		is_enArea.setName("is_enArea"); // NOI18N
		jScrollPane5.setViewportView(is_enArea);

		resultTabbledPane.addTab(resourceMap.getString("jScrollPane5.TabConstraints.tabTitle"), jScrollPane5); // NOI18N

		javax.swing.ActionMap actionMap = org.jdesktop.application.Application
			.getInstance(generator.modules.cfgexamplegenerator.CFGExampleGeneratorApp.class).getContext()
			.getActionMap(CFGExampleGeneratorView.class, this);
		cancelButton.setAction(actionMap.get("cancelGenerationAction")); // NOI18N
		cancelButton.setText(resourceMap.getString("cancelButton.text")); // NOI18N
		cancelButton.setName("cancelButton"); // NOI18N

		generateButton.setAction(actionMap.get("generationAction")); // NOI18N
		generateButton.setText(resourceMap.getString("generateButton.text")); // NOI18N
		generateButton.setName("generateButton"); // NOI18N
		generateButton.addFocusListener(new java.awt.event.FocusAdapter()
		{
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				CFGExampleGeneratorView.this.focusLost(evt);
			}
		});

		clearButton.setAction(actionMap.get("clearAction")); // NOI18N
		clearButton.setText(resourceMap.getString("clearButton.text")); // NOI18N
		clearButton.setName("clearButton"); // NOI18N

		clearAndGenerateButton.setAction(actionMap.get("clearAndGenerate")); // NOI18N
		clearAndGenerateButton.setText(resourceMap.getString("clearAndGenerateButton.text")); // NOI18N
		clearAndGenerateButton.setName("clearAndGenerateButton"); // NOI18N

		subresultsCheck.setSelected(true);
		subresultsCheck.setText(resourceMap.getString("subresultsCheck.text")); // NOI18N
		subresultsCheck.setName("subresultsCheck"); // NOI18N

		detailedInformationCheck.setAction(actionMap.get("detailsDialogAction")); // NOI18N
		detailedInformationCheck.setText(resourceMap.getString("detailedInformationCheck.text")); // NOI18N
		detailedInformationCheck.setName("detailedInformationCheck"); // NOI18N

		printInputCheck.setSelected(true);
		printInputCheck.setText(resourceMap.getString("printInputCheck.text")); // NOI18N
		printInputCheck.setName("printInputCheck"); // NOI18N

		javax.swing.GroupLayout rightMainPanelLayout = new javax.swing.GroupLayout(rightMainPanel);
		rightMainPanel.setLayout(rightMainPanelLayout);
		rightMainPanelLayout.setHorizontalGroup(rightMainPanelLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			rightMainPanelLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					rightMainPanelLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(resultTabbledPane, javax.swing.GroupLayout.Alignment.TRAILING,
							javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
						.addGroup(
							rightMainPanelLayout.createSequentialGroup().addComponent(subresultsCheck)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(printInputCheck)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(detailedInformationCheck))
						.addGroup(
							rightMainPanelLayout
								.createSequentialGroup()
								.addComponent(clearButton)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(generateButton)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(clearAndGenerateButton)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85,
									Short.MAX_VALUE).addComponent(cancelButton))).addContainerGap()));
		rightMainPanelLayout.setVerticalGroup(rightMainPanelLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			javax.swing.GroupLayout.Alignment.TRAILING,
			rightMainPanelLayout
				.createSequentialGroup()
				.addContainerGap()
				.addComponent(resultTabbledPane, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					rightMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(subresultsCheck).addComponent(printInputCheck)
						.addComponent(detailedInformationCheck))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(
					rightMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(cancelButton).addComponent(clearButton).addComponent(generateButton)
						.addComponent(clearAndGenerateButton)).addContainerGap()));

		mainPanel.add(rightMainPanel);

		menuBar.setName("menuBar"); // NOI18N

		fileMenu.setMnemonic('f');
		fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
		fileMenu.setName("fileMenu"); // NOI18N

		jMenuItem1.setAction(actionMap.get("quickSaveAction")); // NOI18N
		jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
		jMenuItem1.setName("jMenuItem1"); // NOI18N
		fileMenu.add(jMenuItem1);

		saveMenuItem.setAction(actionMap.get("saveAction")); // NOI18N
		saveMenuItem.setText(resourceMap.getString("saveMenuItem.text")); // NOI18N
		saveMenuItem.setName("saveMenuItem"); // NOI18N
		fileMenu.add(saveMenuItem);

		jSeparator1.setName("jSeparator1"); // NOI18N
		fileMenu.add(jSeparator1);

		exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
		exitMenuItem.setName("exitMenuItem"); // NOI18N
		fileMenu.add(exitMenuItem);

		menuBar.add(fileMenu);

		editMenu.setMnemonic('e');
		editMenu.setText(resourceMap.getString("editMenu.text")); // NOI18N
		editMenu.setName("editMenu"); // NOI18N

		copyMenuItem.setAction(actionMap.get("copyToClipboardAction")); // NOI18N
		copyMenuItem.setText(resourceMap.getString("copyMenuItem.text")); // NOI18N
		copyMenuItem.setName("copyMenuItem"); // NOI18N
		editMenu.add(copyMenuItem);

		clearMenuItem.setAction(actionMap.get("clearAction")); // NOI18N
		clearMenuItem.setText(resourceMap.getString("clearMenuItem.text")); // NOI18N
		clearMenuItem.setName("clearMenuItem"); // NOI18N
		editMenu.add(clearMenuItem);

		menuBar.add(editMenu);

		viewMenu.setMnemonic('u');
		viewMenu.setText(resourceMap.getString("viewMenu.text")); // NOI18N
		viewMenu.setName("viewMenu"); // NOI18N

		viewAllGramarsRadio.setAction(actionMap.get("allGramarsVisible")); // NOI18N
		viewButtonGroup.add(viewAllGramarsRadio);
		viewAllGramarsRadio.setSelected(true);
		viewAllGramarsRadio.setText(resourceMap.getString("viewAllGramarsRadio.text")); // NOI18N
		viewAllGramarsRadio.setName("viewAllGramarsRadio"); // NOI18N
		viewMenu.add(viewAllGramarsRadio);

		viewOnlyOutputRadio.setAction(actionMap.get("onlyOutputGramar")); // NOI18N
		viewButtonGroup.add(viewOnlyOutputRadio);
		viewOnlyOutputRadio.setText(resourceMap.getString("viewOnlyOutputRadio.text")); // NOI18N
		viewOnlyOutputRadio.setName("viewOnlyOutputRadio"); // NOI18N
		viewMenu.add(viewOnlyOutputRadio);

		viewAlgorithmsSettingsCheck.setAction(actionMap.get("viewAlgorithms")); // NOI18N
		viewAlgorithmsSettingsCheck.setSelected(true);
		viewAlgorithmsSettingsCheck.setText(resourceMap.getString("viewAlgorithmsSettingsCheck.text")); // NOI18N
		viewAlgorithmsSettingsCheck.setName("viewAlgorithmsSettingsCheck"); // NOI18N
		viewMenu.add(viewAlgorithmsSettingsCheck);

		menuBar.add(viewMenu);

		helpMenu.setMnemonic('h');
		helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
		helpMenu.setName("helpMenu"); // NOI18N

		aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
		aboutMenuItem.setName("aboutMenuItem"); // NOI18N
		helpMenu.add(aboutMenuItem);

		menuBar.add(helpMenu);

		statusPanel.setName("statusPanel"); // NOI18N

		statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

		statusMessageLabel.setText(resourceMap.getString("statusMessageLabel.text")); // NOI18N
		statusMessageLabel.setName("statusMessageLabel"); // NOI18N

		statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

		progressBar.setName("progressBar"); // NOI18N
		progressBar.setStringPainted(true);

		messagesLabel.setText(resourceMap.getString("messagesLabel.text")); // NOI18N
		messagesLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
		messagesLabel.setName("messagesLabel"); // NOI18N

		javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
		statusPanel.setLayout(statusPanelLayout);
		statusPanelLayout.setHorizontalGroup(statusPanelLayout
			.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(
				statusPanelLayout
					.createSequentialGroup()
					.addContainerGap()
					.addGroup(
						statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
							.addComponent(statusMessageLabel)
							.addComponent(messagesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 741, Short.MAX_VALUE))
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(statusAnimationLabel).addContainerGap())
			.addComponent(statusPanelSeparator, javax.swing.GroupLayout.Alignment.TRAILING,
				javax.swing.GroupLayout.DEFAULT_SIZE, 915, Short.MAX_VALUE));
		statusPanelLayout.setVerticalGroup(statusPanelLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			javax.swing.GroupLayout.Alignment.TRAILING,
			statusPanelLayout
				.createSequentialGroup()
				.addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(2, 2, 2)
				.addGroup(
					statusPanelLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
						.addGroup(
							statusPanelLayout
								.createSequentialGroup()
								.addGroup(
									statusPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(statusMessageLabel)
										.addComponent(statusAnimationLabel)
										.addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE,
											javax.swing.GroupLayout.DEFAULT_SIZE,
											javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(3, 3, 3))
						.addComponent(messagesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))));

		disPopUpMenu.setName("disPopUpMenu"); // NOI18N

		disMenuItem.setText(resourceMap.getString("disMenuItem.text")); // NOI18N
		disMenuItem.setName("disMenuItem"); // NOI18N
		disPopUpMenu.add(disMenuItem);

		jScrollPane1.setName("jScrollPane1"); // NOI18N

		standardResultTextPane.setEditable(false);
		standardResultTextPane.setName("standardResultTextPane"); // NOI18N
		jScrollPane1.setViewportView(standardResultTextPane);

		detailsDialog.setTitle(resourceMap.getString("detailsDialog.title")); // NOI18N
		detailsDialog.setAlwaysOnTop(true);
		detailsDialog.setMinimumSize(new java.awt.Dimension(400, 330));
		detailsDialog.setName("detailsDialog"); // NOI18N
		detailsDialog.addComponentListener(new java.awt.event.ComponentAdapter()
		{
			public void componentHidden(java.awt.event.ComponentEvent evt)
			{
				detailsHidden(evt);
			}

			public void componentMoved(java.awt.event.ComponentEvent evt)
			{
				detailsDialogMoved(evt);
			}
		});

		jScrollPane6.setBorder(null);
		jScrollPane6.setName("jScrollPane6"); // NOI18N

		criteriumTable.setAutoCreateRowSorter(true);
		criteriumTable.setBackground(resourceMap.getColor("criteriumTable.background")); // NOI18N
		criteriumTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][] {

		}, new String[] { "Criterium name", "Overall failure %" })
		{
			boolean[] canEdit = new boolean[] { false, false };

			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				return canEdit[columnIndex];
			}
		});
		criteriumTable.setName("criteriumTable"); // NOI18N
		criteriumTable.setOpaque(false);
		criteriumTable.setRowSelectionAllowed(false);
		criteriumTable.setShowHorizontalLines(false);
		criteriumTable.setShowVerticalLines(false);
		jScrollPane6.setViewportView(criteriumTable);

		hideDetailsDialog.setAction(actionMap.get("detailsDialogAction")); // NOI18N
		hideDetailsDialog.setText(resourceMap.getString("hideDetailsDialog.text")); // NOI18N
		hideDetailsDialog.setName("hideDetailsDialog"); // NOI18N

		cancelButtonInDialog.setAction(actionMap.get("cancelGenerationAction")); // NOI18N
		cancelButtonInDialog.setText(resourceMap.getString("cancelButtonInDialog.text")); // NOI18N
		cancelButtonInDialog.setName("cancelButtonInDialog"); // NOI18N

		runningLabel.setName("runningLabel"); // NOI18N

		generatedExamplesLabel.setText(resourceMap.getString("generatedExamplesLabel.text")); // NOI18N
		generatedExamplesLabel.setName("generatedExamplesLabel"); // NOI18N

		percentageLabel.setText(resourceMap.getString("percentageLabel.text")); // NOI18N
		percentageLabel.setName("percentageLabel"); // NOI18N

		showCriteriaCheck.setAction(actionMap.get("showCriteriaComponentsAction")); // NOI18N
		showCriteriaCheck.setText(resourceMap.getString("showCriteriaCheck.text")); // NOI18N
		showCriteriaCheck.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		showCriteriaCheck.setName("showCriteriaCheck"); // NOI18N

		javax.swing.GroupLayout detailsDialogLayout = new javax.swing.GroupLayout(detailsDialog.getContentPane());
		detailsDialog.getContentPane().setLayout(detailsDialogLayout);
		detailsDialogLayout.setHorizontalGroup(detailsDialogLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			javax.swing.GroupLayout.Alignment.TRAILING,
			detailsDialogLayout
				.createSequentialGroup()
				.addContainerGap()
				.addGroup(
					detailsDialogLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
						.addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.LEADING,
							javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
						.addGroup(
							javax.swing.GroupLayout.Alignment.LEADING,
							detailsDialogLayout
								.createSequentialGroup()
								.addComponent(cancelButtonInDialog)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 248,
									Short.MAX_VALUE).addComponent(hideDetailsDialog))
						.addComponent(runningLabel, javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(showCriteriaCheck, javax.swing.GroupLayout.Alignment.LEADING,
							javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
						.addComponent(generatedExamplesLabel, javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(percentageLabel, javax.swing.GroupLayout.Alignment.LEADING)).addContainerGap()));
		detailsDialogLayout.setVerticalGroup(detailsDialogLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			javax.swing.GroupLayout.Alignment.TRAILING,
			detailsDialogLayout
				.createSequentialGroup()
				.addContainerGap()
				.addComponent(runningLabel)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(generatedExamplesLabel)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(percentageLabel)
				.addGap(18, 18, 18)
				.addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(showCriteriaCheck)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(
					detailsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(hideDetailsDialog).addComponent(cancelButtonInDialog)).addContainerGap()));

		detailsDialog.getAccessibleContext().setAccessibleParent(null);

		advancedSettingsDialog.setTitle("Advanced settings"); // NOI18N
		advancedSettingsDialog.setMinimumSize(new java.awt.Dimension(423, 246));
		advancedSettingsDialog.setModal(true);
		advancedSettingsDialog.setName("advancedSettingsDialog"); // NOI18N
		advancedSettingsDialog.setResizable(false);
		advancedSettingsDialog.addComponentListener(new java.awt.event.ComponentAdapter()
		{
			public void componentHidden(java.awt.event.ComponentEvent evt)
			{
				advancedSettingsHidden(evt);
			}
		});

		jPanel1.setName("jPanel1"); // NOI18N

		jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
		jPanel2.setName("jPanel2"); // NOI18N

		forceSEpsylonRadio.setText(resourceMap.getString("forceSEpsylonRadio.text")); // NOI18N
		forceSEpsylonRadio.setName("forceSEpsylonRadio"); // NOI18N

		forbidSEpsylonRadio.setText(resourceMap.getString("forbidSEpsylonRadio.text")); // NOI18N
		forbidSEpsylonRadio.setName("forbidSEpsylonRadio"); // NOI18N

		decideSEpsylonRadio.setSelected(true);
		decideSEpsylonRadio.setText(resourceMap.getString("decideSEpsylonRadio.text")); // NOI18N
		decideSEpsylonRadio.setName("decideSEpsylonRadio"); // NOI18N

		noUnreachableChecker.setText(resourceMap.getString("noUnreachableChecker.text")); // NOI18N
		noUnreachableChecker.setName("noUnreachableChecker"); // NOI18N

		noUnnormalizedChecker.setText(resourceMap.getString("noUnnormalizedChecker.text")); // NOI18N
		noUnnormalizedChecker.setName("noUnnormalizedChecker"); // NOI18N

		noEpsilonChecker.setText(resourceMap.getString("noEpsilonChecker.text")); // NOI18N
		noEpsilonChecker.setName("noEpsilonChecker"); // NOI18N

		allowRightSRadio.setText(resourceMap.getString("allowRightSRadio.text")); // NOI18N
		allowRightSRadio.setName("allowRightSRadio"); // NOI18N

		forbidRightSRadio.setText(resourceMap.getString("forbidRightSRadio.text")); // NOI18N
		forbidRightSRadio.setName("forbidRightSRadio"); // NOI18N

		decideRightSRadio.setSelected(true);
		decideRightSRadio.setText(resourceMap.getString("decideRightSRadio.text")); // NOI18N
		decideRightSRadio.setName("decideRightSRadio"); // NOI18N

		noSimpleChecker.setText(resourceMap.getString("noSimpleChecker.text")); // NOI18N
		noSimpleChecker.setName("noSimpleChecker"); // NOI18N

		noLeftRekChecker.setText(resourceMap.getString("noLeftRekChecker.text")); // NOI18N
		noLeftRekChecker.setName("noLeftRekChecker"); // NOI18N

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(
				jPanel2Layout
					.createSequentialGroup()
					.addContainerGap()
					.addGroup(
						jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
							.addComponent(forceSEpsylonRadio).addComponent(allowRightSRadio)
							.addComponent(forbidRightSRadio).addComponent(decideRightSRadio)
							.addComponent(forbidSEpsylonRadio).addComponent(decideSEpsylonRadio))
					.addGap(35, 35, 35)
					.addGroup(
						jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
							.addComponent(noLeftRekChecker).addComponent(noSimpleChecker)
							.addComponent(noUnreachableChecker).addComponent(noUnnormalizedChecker)
							.addComponent(noEpsilonChecker))
					.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(
				jPanel2Layout
					.createSequentialGroup()
					.addGroup(
						jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
							.addComponent(forceSEpsylonRadio).addComponent(noUnreachableChecker))
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(
						jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
							.addComponent(forbidSEpsylonRadio).addComponent(noUnnormalizedChecker))
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(
						jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
							.addComponent(decideSEpsylonRadio).addComponent(noEpsilonChecker))
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(noSimpleChecker)
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(
						jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
							.addComponent(noLeftRekChecker).addComponent(allowRightSRadio))
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(forbidRightSRadio)
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(decideRightSRadio).addContainerGap()));

		defaultAdvancedButton.setAction(actionMap.get("setDefaultAction")); // NOI18N
		defaultAdvancedButton.setText(resourceMap.getString("defaultAdvancedButton.text")); // NOI18N
		defaultAdvancedButton.setName("defaultAdvancedButton"); // NOI18N

		cancelButton1.setAction(actionMap.get("cancelAction")); // NOI18N
		cancelButton1.setText(resourceMap.getString("cancelButton1.text")); // NOI18N
		cancelButton1.setName("cancelButton1"); // NOI18N

		okButton.setAction(actionMap.get("okAction")); // NOI18N
		okButton.setText(resourceMap.getString("okButton.text")); // NOI18N
		okButton.setName("okButton"); // NOI18N

		applyButton.setAction(actionMap.get("applySettings")); // NOI18N
		applyButton.setText(resourceMap.getString("applyButton.text")); // NOI18N
		applyButton.setName("applyButton"); // NOI18N

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(
				jPanel1Layout
					.createSequentialGroup()
					.addContainerGap()
					.addGroup(
						jPanel1Layout
							.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
							.addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGroup(
								jPanel1Layout
									.createSequentialGroup()
									.addComponent(defaultAdvancedButton)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 153,
										Short.MAX_VALUE).addComponent(okButton)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(applyButton)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(cancelButton1))).addContainerGap()));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(
				jPanel1Layout
					.createSequentialGroup()
					.addContainerGap()
					.addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(
						jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
							.addComponent(defaultAdvancedButton).addComponent(cancelButton1).addComponent(applyButton)
							.addComponent(okButton)).addGap(11, 11, 11)));

		javax.swing.GroupLayout advancedSettingsDialogLayout = new javax.swing.GroupLayout(
			advancedSettingsDialog.getContentPane());
		advancedSettingsDialog.getContentPane().setLayout(advancedSettingsDialogLayout);
		advancedSettingsDialogLayout.setHorizontalGroup(advancedSettingsDialogLayout
			.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGap(0, 423, Short.MAX_VALUE)
			.addGroup(
				advancedSettingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
					advancedSettingsDialogLayout
						.createSequentialGroup()
						.addGap(0, 0, Short.MAX_VALUE)
						.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(0, 0, Short.MAX_VALUE))));
		advancedSettingsDialogLayout.setVerticalGroup(advancedSettingsDialogLayout
			.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGap(0, 246, Short.MAX_VALUE)
			.addGroup(
				advancedSettingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
					advancedSettingsDialogLayout
						.createSequentialGroup()
						.addGap(0, 0, Short.MAX_VALUE)
						.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(0, 0, Short.MAX_VALUE))));

		setComponent(mainPanel);
		setMenuBar(menuBar);
		setStatusBar(statusPanel);
	}// </editor-fold>//GEN-END:initComponents

	private void setUp()
	{

		actionMap = Application.getInstance(CFGExampleGeneratorApp.class).getContext()
			.getActionMap(CFGExampleGeneratorView.class, this);

		gramarPanes = new JPanel[] { unGramarPaneInTab, nnGramarPaneInTab, epGramarPaneInTab, srGramarPaneInTab,
			lrGramarPaneInTab, gfGramarPaneInTab, cfGramarPaneInTab };
		algorithmsPanes = new JPanel[] { unAlgorithmPaneInTab, nnAlgorithmPaneInTab, epAlgorithmPaneInTab,
			srAlgorithmPaneInTab, lrAlgorithmPaneInTab, gfAlgorithmPaneInTab, cfAlgorithmPaneInTab };

		for (JPanel alPan : algorithmsPanes)
		{

			alPan.getParent().setName(
				getResourceMap().getString(alPan.getParent().getName() + ".TabConstraints.tabTitle"));
		}

		DefaultTableCellRenderer ren = new DefaultTableCellRenderer();
		ren.setHorizontalAlignment(JTextField.CENTER);
		// table with info about criteria failure
		criteriumTable.getColumnModel().getColumn(1).setCellRenderer(ren);
		criteriumTable.getColumnModel().getColumn(1).setPreferredWidth(125);
		criteriumTable.getColumnModel().getColumn(1).setMaxWidth(180);
		criteriumTable.getColumnModel().getColumn(1).setMinWidth(30);
		criteriumTable.getModel().addTableModelListener(new TableModelListener()
		{

			public void tableChanged(TableModelEvent e)
			{
				if (criteriumTable.getRowCount() > 0)
				{
					actionMap.get("showCriteriaComponentsAction").setEnabled(true);
				}
				else
				{
					actionMap.get("showCriteriaComponentsAction").setEnabled(false);
				}
			}
		});

		actionMap.get("showCriteriaComponentsAction").setEnabled(false);

		ALGORITHMS_MAP.put(AlgorithmType.EPSILON_ELIM_ALG, epTabPane);
		ALGORITHMS_MAP.put(AlgorithmType.SIMPLE_RULES_ELIM_ALG, srTabPane);
		ALGORITHMS_MAP.put(AlgorithmType.NONGENERATING_ELIM_ALG, nnTabPane);
		ALGORITHMS_MAP.put(AlgorithmType.UNREACHABLE_ELIM_ALG, unTabPane);
		ALGORITHMS_MAP.put(AlgorithmType.CNF_CREATION_ALG, cfTabPane);
		ALGORITHMS_MAP.put(AlgorithmType.LRECURSION_ELIM_ALG, lrTabPane);
		ALGORITHMS_MAP.put(AlgorithmType.GNF_CREATION_ALG, gfTabPane);

		for (AlgorithmType a : ALGORITHMS_MAP.keySet())
		{
			algorithmsPanelsOrder.put(ALGORITHMS_MAP.get(a), a.ordinal() - 1);
		}

		ActionListener comboListener = new ComboBoxesActionListener();
		ioInputGramarCombo.addActionListener(comboListener);
		ioOutputGramarCombo.addActionListener(comboListener);
		ioOutputGramarCombo.setSelectedItem(AlgorithmType.GNF_CREATION_ALG);

		fileChooser = new JFileChooser();
		colorContr = new ColorController(ioExampleNumberSpinner);

		getFrame().pack();
		getFrame().setMinimumSize(new Dimension(750, 500));

		JSpinner[] allSpinners = { igMaxLinesNumberSpinner, igMaxRulesNumberSpinner, igMaxTerminalNumberSpinner,
			igMinLinesNumberSpinner, igMinRulesNumberSpinner, igMinTerminalNumberSpinner, ogMaxLinesSpinner,
			ogMaxRulesSpinner, ogMinLinesSpinner, ogMinRulesSpinner, unLinesMaximumSpinner, unLinesMinimumSpinner,
			unMaximumDifficultySpinner, unMinimumDifficultySpinner, unRulesMaximumSpinner, unRulesMinimumSpinner,
			nnLinesMaximumSpinner, nnLinesMinimumSpinner, nnMaximumDifficultySpinner, nnMinimumDifficultySpinner,
			nnRulesMaximumSpinner, nnRulesMinimumSpinner, epLinesMaximumSpinner, epLinesMinimumSpinner,
			epMaximumDifficultySpinner, epMinimumDifficultySpinner, epRulesMaximumSpinner, epRulesMinimumSpinner,
			srLinesMaximumSpinner, srLinesMinimumSpinner, srMaximumDifficultySpinner, srMinimumDifficultySpinner,
			srRulesMaximumSpinner, srRulesMinimumSpinner, lrLinesMaximumSpinner, lrLinesMinimumSpinner,
			lrMaximumDifficultySpinner, lrMinimumDifficultySpinner, lrRulesMaximumSpinner, lrRulesMinimumSpinner,
			cfLinesMaximumSpinner, cfLinesMinimumSpinner, cfMaximumDifficultySpinner, cfMinimumDifficultySpinner,
			cfRulesMaximumSpinner, cfRulesMinimumSpinner, gfLinesMaximumSpinner, gfLinesMinimumSpinner,
			gfRulesMinimumSpinner, gfMaximumDifficultySpinner, gfMinimumDifficultySpinner, gfRulesMaximumSpinner };
		spinnersSup = new SpinnersSupport(disPopUpMenu, disMenuItem, colorContr);
		for (JSpinner s : allSpinners)
		{
			spinnersSup.registerSpinner(s);
			s.setToolTipText(s.getName());
			s.putClientProperty(SPINNER_DISABLED_BY_RADIO, !s.isEnabled());
		}

		ioExampleNumberSpinner.setToolTipText(ioExampleNumberSpinner.getName());
		igMaxRuleLengthSpinner.setToolTipText(igMaxRuleLengthSpinner.getName());

		JSpinner[][] spinersErrorPaars = { { igMinRulesNumberSpinner, igMaxRulesNumberSpinner },
			{ igMinLinesNumberSpinner, igMaxLinesNumberSpinner },
			{ igMinTerminalNumberSpinner, igMaxTerminalNumberSpinner }, { ogMinRulesSpinner, ogMaxRulesSpinner },
			{ ogMinLinesSpinner, ogMaxLinesSpinner }, { unMinimumDifficultySpinner, unMaximumDifficultySpinner },
			{ unRulesMinimumSpinner, unRulesMaximumSpinner }, { unLinesMinimumSpinner, unLinesMaximumSpinner },
			{ nnMinimumDifficultySpinner, nnMaximumDifficultySpinner },
			{ nnRulesMinimumSpinner, nnRulesMaximumSpinner }, { nnLinesMinimumSpinner, nnLinesMaximumSpinner },
			{ epMinimumDifficultySpinner, epMaximumDifficultySpinner },
			{ epRulesMinimumSpinner, epRulesMaximumSpinner }, { epLinesMinimumSpinner, epLinesMaximumSpinner },
			{ srMinimumDifficultySpinner, srMaximumDifficultySpinner },
			{ srRulesMinimumSpinner, srRulesMaximumSpinner }, { srLinesMinimumSpinner, srLinesMaximumSpinner },
			{ lrMinimumDifficultySpinner, lrMaximumDifficultySpinner },
			{ lrRulesMinimumSpinner, lrRulesMaximumSpinner }, { lrLinesMinimumSpinner, lrRulesMaximumSpinner },
			{ cfMinimumDifficultySpinner, cfMaximumDifficultySpinner },
			{ cfRulesMinimumSpinner, cfRulesMaximumSpinner }, { cfLinesMinimumSpinner, cfLinesMaximumSpinner },
			{ gfMinimumDifficultySpinner, gfMaximumDifficultySpinner },
			{ gfRulesMinimumSpinner, gfRulesMaximumSpinner }, { gfLinesMinimumSpinner, gfLinesMaximumSpinner }, };

		criteriaChecker = new CriteriaConstraintsChecker(messagesLabel, new javax.swing.Action[] {
			actionMap.get("generationAction"), actionMap.get("clearAndGenerate") }, spinnersSup, colorContr);
		for (JSpinner[] s : spinersErrorPaars)
		{
			criteriaChecker.addErrConstraint(s[0], s[1]);
			if (s[0].getName().contains("rules") || s[1].getName().contains("rules"))
			{
				criteriaChecker.addErrorConstraint(s[0], MAX_RULES, false, s[0].getName()
					+ " value too high. Maximal rules number in grammar can be " + MAX_RULES);
				criteriaChecker.addErrorConstraint(s[1], MAX_RULES, false, s[1].getName()
					+ " value too high. Maximal rules number in grammar can be " + MAX_RULES);
			}
		}

		if (!((OutputGrammarForm) ioOutputGramarCombo.getSelectedItem()).equals(OutputGrammarForm.PROPER))
		{
			criteriaChecker.addErrConstraint(nnLinesMinimumSpinner, igMaxLinesNumberSpinner);
			criteriaChecker.addErrConstraint(unLinesMinimumSpinner, igMaxLinesNumberSpinner);
			criteriaChecker.addErrConstraint(unLinesMinimumSpinner, epLinesMaximumSpinner);
			criteriaChecker.addErrConstraint(nnRulesMinimumSpinner, igMaxRulesNumberSpinner);
			criteriaChecker.addErrConstraint(unRulesMinimumSpinner, igMaxRulesNumberSpinner);
		}
		criteriaChecker.addErrConstraint(unRulesMinimumSpinner, nnRulesMaximumSpinner);
		criteriaChecker.addErrConstraint(unLinesMinimumSpinner, nnLinesMaximumSpinner);
		criteriaChecker.addErrConstraint(srLinesMinimumSpinner, epLinesMaximumSpinner);
		criteriaChecker.addErrConstraint(epLinesMinimumSpinner, srLinesMaximumSpinner);
		criteriaChecker.addErrConstraint(srLinesMinimumSpinner, lrLinesMaximumSpinner);
		criteriaChecker.addErrConstraint(srLinesMinimumSpinner, cfLinesMaximumSpinner);
		criteriaChecker.addErrConstraint(lrLinesMinimumSpinner, gfLinesMaximumSpinner);
		criteriaChecker.addErrConstraint(srLinesMinimumSpinner, gfLinesMaximumSpinner);
		criteriaChecker.addErrConstraint(srRulesMinimumSpinner, lrRulesMaximumSpinner);
		criteriaChecker.addErrConstraint(srRulesMinimumSpinner, cfRulesMaximumSpinner);
		criteriaChecker.addErrConstraint(lrRulesMinimumSpinner, gfRulesMaximumSpinner);
		criteriaChecker.addErrConstraint(srRulesMinimumSpinner, gfRulesMaximumSpinner);

		criteriaChecker.addErrConstraint(lrLinesMinimumSpinner, lrRulesMaximumSpinner);
		criteriaChecker.addErrConstraint(cfLinesMinimumSpinner, cfRulesMaximumSpinner);
		criteriaChecker.addErrConstraint(gfLinesMinimumSpinner, gfRulesMaximumSpinner);

		// criteriaChecker.addErrConstraint(igMinLinesNumberSpinner,
		// igMinRulesNumberSpinner);
		// criteriaChecker.addErrConstraint(igMinLinesNumberSpinner,
		// igMaxRulesNumberSpinner);
		// criteriaChecker.addErrConstraint(ogMinLinesSpinner,
		// ogMinRulesSpinner);
		// criteriaChecker.addErrConstraint(ogMinLinesNumberSpinner,
		// igMaxRulesNumberSpinner);

		criteriaChecker.addErrorConstraint(igMinTerminalNumberSpinner, SymbolManager.ALPHABETH_LENGTH, false, null);
		criteriaChecker.addErrorConstraint(igMaxTerminalNumberSpinner, SymbolManager.ALPHABETH_LENGTH, false, null);

		JComponent[] otherComponents = { ioInputGramarCombo, ioOutputGramarCombo, ioExampleNumberSpinner,
			igMaxRuleLengthSpinner, subresultsCheck };

		for (JComponent comp : allSpinners)
		{
			nameToComponent.put(comp.getName(), comp);
		}

		for (JComponent comp : otherComponents)
		{
			nameToComponent.put(comp.getName(), comp);
		}

		criteriaChecker.addErrorConstraint(ioExampleNumberSpinner, 1, true, ioExampleNumberSpinner.getName()
			+ " must be at least 1");
		criteriaChecker.addErrorConstraint(igMaxRuleLengthSpinner, 1, true, igMaxRuleLengthSpinner.getName()
			+ " must be at least 1");
		criteriaChecker.addErrorConstraint(igMinLinesNumberSpinner, 1, true, igMinRulesNumberSpinner.getName()
			+ " must be at least 1");
		criteriaChecker.addErrorConstraint(igMaxLinesNumberSpinner, 1, true, igMaxRulesNumberSpinner.getName()
			+ " must be at least 1");
		// criteriaChecker.addErrorConstraint(ioExampleNumberSpinner, 1500,
		// false,
		// "Maximum of 1500 examples can be generated at once. This is to" +
		// " prevent allocating too much memory.");

		applyViewPreferences();

		super.getApplication().addExitListener(new Application.ExitListener()
		{

			public boolean canExit(EventObject arg0)
			{
				return true;
			}

			public void willExit(EventObject arg0)
			{
				storeViewPreferences();
			}
		});

		this.getFrame().addWindowListener(new WindowAdapter()
		{

			@Override
			public void windowClosed(WindowEvent e)
			{
				storeViewPreferences();
				super.windowClosed(e);
			}

			@Override
			public void windowClosing(WindowEvent e)
			{
				storeViewPreferences();
				super.windowClosing(e);
			}
		});

		booleans.add(new BooleanPropertyWithDefault(false, noUnreachableChecker, getUnTabPane()));
		booleans.add(new BooleanPropertyWithDefault(false, noUnnormalizedChecker, getNnTabPane()));
		booleans.add(new BooleanPropertyWithDefault(false, noEpsilonChecker, getEpTabPane()));
		booleans.add(new BooleanPropertyWithDefault(false, noSimpleChecker, getSrTabPane()));
		booleans.add(new BooleanPropertyWithDefault(false, noLeftRekChecker, getLrTabPane()));

		for (BooleanPropertyWithDefault b : booleans)
		{
			b.addChangedPropertyListener(enabler);
		}
		advancedSettingsDialog.pack();

		/*
		 * setRadio(unAlgorithmDifficultyRadio, unCycleIterrationsRadio, unMinimumDifficultySpinner,
		 * unMaximumDifficultySpinner, unMinimumIterationsSpinner, unMaximumIterationsSpinner, unAlgorithmMethodGroup);
		 * setRadio(nnAlgorithmDifficultyRadio, nnCycleIterrationsRadio, nnMinimumDifficultySpinner,
		 * nnMaximumDifficultySpinner, nnMinimumIterationsSpinner, nnMaximumIterationsSpinner, nnAlgorithmMethodGroup);
		 * setRadio(epAlgorithmDifficultyRadio, epCycleIterrationsRadio, epMinimumDifficultySpinner,
		 * epMaximumDifficultySpinner, epMinimumIterationsSpinner, epMaximumIterationsSpinner, epAlgorithmMethodGroup);
		 * setRadio(srAlgorithmDifficultyRadio, srCycleIterrationsRadio, srMinimumDifficultySpinner,
		 * srMaximumDifficultySpinner, srMinimumIterationsSpinner, srMaximumIterationsSpinner, srAlgorithmMethodGroup);
		 * setRadio(lrAlgorithmDifficultyRadio, lrCycleIterrationsRadio, lrMinimumDifficultySpinner,
		 * lrMaximumDifficultySpinner, lrMinimumIterationsSpinner, lrMaximumIterationsSpinner, lrAlgorithmMethodGroup);
		 * setRadio(cfAlgorithmDifficultyRadio, cfCycleIterrationsRadio, cfMinimumDifficultySpinner,
		 * cfMaximumDifficultySpinner, cfMinimumIterationsSpinner, cfMaximumIterationsSpinner, cfAlgorithmMethodGroup);
		 * setRadio(gfAlgorithmDifficultyRadio, gfCycleIterrationsRadio, gfMinimumDifficultySpinner,
		 * gfMaximumDifficultySpinner, gfMinimumIterationsSpinner, gfMaximumIterationsSpinner, gfAlgorithmMethodGroup);
		 */

		setFunctionsEnabled(false);
		updateAlgTabPane();

		// temporaly measure to disable not completed features. Delete to make
		// them useable
		actionMap.get("showAdvancedAction").setEnabled(false);
		actionMap.get("showAdvancedAction").addPropertyChangeListener(new PropertyChangeListener()
		{

			public void propertyChange(PropertyChangeEvent evt)
			{
				actionMap.get("showAdvancedAction").setEnabled(false);
			}
		});
		// end temporaly section
	}

	private void setSpinnerEnabled(JSpinner spin, boolean enabled)
	{
		spin.putClientProperty(SPINNER_DISABLED_BY_RADIO, !enabled);
		spin.setEnabled(enabled);
	}

	private void detailsHidden(java.awt.event.ComponentEvent evt)
	{// GEN-FIRST:event_detailsHidden
		detailedInformationCheck.setSelected(false);
	}// GEN-LAST:event_detailsHidden

	private void detailsDialogMoved(java.awt.event.ComponentEvent evt)
	{// GEN-FIRST:event_detailsDialogMoved
		System.out.println(detailsDialog.getParent());
		if (ignoreMove <= 0)
		{
			detailsCentredPosition = false;
		}
		else
		{
			ignoreMove--;
		}
	}// GEN-LAST:event_detailsDialogMoved

	private void advancedSettingsHidden(java.awt.event.ComponentEvent evt)
	{// GEN-FIRST:event_advancedSettingsHidden
		restoreScreen();
	}// GEN-LAST:event_advancedSettingsHidden

	private void focusLost(java.awt.event.FocusEvent evt)
	{// GEN-FIRST:event_focusLost

	}// GEN-LAST:event_focusLost

	@Action
	public void setAllGrammarsVisible(boolean all)
	{

		setTabbedPaneVisible();

		outputGramarPane.setVisible(!all);
		outputGramarPane.putClientProperty(PANE_USED_CLIENT_PROPERTY, !all);

		for (JPanel p : gramarPanes)
		{
			p.setVisible(all);
			p.putClientProperty(PANE_USED_CLIENT_PROPERTY, all);
		}

		settingsPane.validate();
	}

	@Action
	public void setViewAlgorithms(boolean visible)
	{
		setTabbedPaneVisible();

		for (JPanel p : algorithmsPanes)
		{
			p.setVisible(visible);
			p.putClientProperty(PANE_USED_CLIENT_PROPERTY, visible);
		}

		settingsPane.validate();
	}

	private void setTabbedPaneVisible()
	{
		if (!viewAlgorithmsSettingsCheck.isSelected() && !viewAllGramarsRadio.isSelected())
		{
			algorithmsAndGramarTabbedPane.setVisible(false);
			algorithmsAndGramarTabbedPane.putClientProperty(PANE_USED_CLIENT_PROPERTY, false);
		}
		else
		{
			algorithmsAndGramarTabbedPane.setVisible(true);
			algorithmsAndGramarTabbedPane.putClientProperty(PANE_USED_CLIENT_PROPERTY, true);
		}
		settingsPane.revalidate();
		settingsPane.repaint();
	}

	@Action
	public void saveAction()
	{
		JFrame mainFrame = CFGExampleGeneratorApp.getApplication().getMainFrame();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		saveScreen();
		int returnVal = fileChooser.showSaveDialog(mainFrame);
		restoreScreen();

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			lastSaveLocation = fileChooser.getSelectedFile();
			if (lastSaveLocation != null)
			{
				quickSaveAction();
			}
		}
	}

	@Action
	public void copyToClipboardAction()
	{
		JScrollPane selected = (JScrollPane) resultTabbledPane.getSelectedComponent();
		JTextArea text = (JTextArea) selected.getViewport().getView();

		Clipboard cl = Toolkit.getDefaultToolkit().getSystemClipboard();
		if (text.getText() == null)
		{
			return;
		}

		cl.setContents(new StringSelection(text.getText()), new ClipboardOwner()
		{

			public void lostOwnership(Clipboard clipboard, Transferable contents)
			{
			}
		});

	}

	@Action
	public void clearAction()
	{
		plain_skArea.setText("");
		is_skArea.setText("");
		latex_skArea.setText("");
		plain_enArea.setText("");
		is_enArea.setText("");
		latex_enArea.setText("");
	}

	@Action
	public void showAdvancedAction()
	{
		advancedSettingsDialog.setLocationRelativeTo(getFrame());

		dialogShown();
		saveScreen();
		advancedSettingsDialog.setVisible(true);
	}

	public boolean getPrintInput()
	{
		return printInputCheck.isSelected();
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JPanel InputAndOutputPane;
	private javax.swing.JPanel InputGramarPane;
	private javax.swing.JDialog advancedSettingsDialog;
	private javax.swing.JTabbedPane algorithmsAndGramarTabbedPane;
	private javax.swing.JRadioButton allowRightSRadio;
	private javax.swing.JButton applyButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JButton cancelButton1;
	private javax.swing.JButton cancelButtonInDialog;
	private javax.swing.JLabel cfAlgorithmDifficultyLabel;
	private javax.swing.ButtonGroup cfAlgorithmMethodGroup;
	private javax.swing.JPanel cfAlgorithmPaneInTab;
	private javax.swing.JPanel cfGramarPaneInTab;
	private javax.swing.JSpinner cfLinesMaximumSpinner;
	private javax.swing.JSpinner cfLinesMinimumSpinner;
	private javax.swing.JSpinner cfMaximumDifficultySpinner;
	private javax.swing.JSpinner cfMinimumDifficultySpinner;
	private javax.swing.JSpinner cfRulesMaximumSpinner;
	private javax.swing.JSpinner cfRulesMinimumSpinner;
	private javax.swing.JPanel cfTabPane;
	private javax.swing.JButton clearAndGenerateButton;
	private javax.swing.JButton clearButton;
	private javax.swing.JMenuItem clearMenuItem;
	private javax.swing.JMenuItem copyMenuItem;
	private javax.swing.JTable criteriumTable;
	private javax.swing.JRadioButton decideRightSRadio;
	private javax.swing.JRadioButton decideSEpsylonRadio;
	private javax.swing.JButton defaultAdvancedButton;
	private javax.swing.JCheckBox detailedInformationCheck;
	private javax.swing.JDialog detailsDialog;
	private javax.swing.JMenuItem disMenuItem;
	private javax.swing.JPopupMenu disPopUpMenu;
	private javax.swing.JMenu editMenu;
	private javax.swing.JLabel epAlgorithmDifficultyLabel;
	private javax.swing.ButtonGroup epAlgorithmMethodGroup;
	private javax.swing.JPanel epAlgorithmPaneInTab;
	private javax.swing.JPanel epGramarPaneInTab;
	private javax.swing.JSpinner epLinesMaximumSpinner;
	private javax.swing.JSpinner epLinesMinimumSpinner;
	private javax.swing.JSpinner epMaximumDifficultySpinner;
	private javax.swing.JSpinner epMinimumDifficultySpinner;
	private javax.swing.JSpinner epRulesMaximumSpinner;
	private javax.swing.JSpinner epRulesMinimumSpinner;
	private javax.swing.JPanel epTabPane;
	private javax.swing.JRadioButton forbidRightSRadio;
	private javax.swing.JRadioButton forbidSEpsylonRadio;
	private javax.swing.JRadioButton forceSEpsylonRadio;
	private javax.swing.JButton generateButton;
	private javax.swing.JLabel generatedExamplesLabel;
	private javax.swing.JLabel gfAlgorithmDifficultyLabel;
	private javax.swing.ButtonGroup gfAlgorithmMethodGroup;
	private javax.swing.JPanel gfAlgorithmPaneInTab;
	private javax.swing.JPanel gfGramarPaneInTab;
	private javax.swing.JSpinner gfLinesMaximumSpinner;
	private javax.swing.JSpinner gfLinesMinimumSpinner;
	private javax.swing.JSpinner gfMaximumDifficultySpinner;
	private javax.swing.JSpinner gfMinimumDifficultySpinner;
	private javax.swing.JSpinner gfRulesMaximumSpinner;
	private javax.swing.JSpinner gfRulesMinimumSpinner;
	private javax.swing.JPanel gfTabPane;
	private javax.swing.JButton hideDetailsDialog;
	private javax.swing.JLabel igLinesNumberLabel;
	private javax.swing.JLabel igMaxLabel;
	private javax.swing.JSpinner igMaxLinesNumberSpinner;
	private javax.swing.JLabel igMaxRuleLengthLabel;
	private javax.swing.JSpinner igMaxRuleLengthSpinner;
	private javax.swing.JSpinner igMaxRulesNumberSpinner;
	private javax.swing.JSpinner igMaxTerminalNumberSpinner;
	private javax.swing.JLabel igMinLabel;
	private javax.swing.JSpinner igMinLinesNumberSpinner;
	private javax.swing.JSpinner igMinRulesNumberSpinner;
	private javax.swing.JSpinner igMinTerminalNumberSpinner;
	private javax.swing.JLabel igRulesNumberLabel;
	private javax.swing.JLabel igTerminalsNumberLabel;
	private javax.swing.JLabel ioExampleNumberLabel;
	private javax.swing.JSpinner ioExampleNumberSpinner;
	private javax.swing.JComboBox ioInputGramarCombo;
	private javax.swing.JLabel ioInputGramarLabel;
	private javax.swing.JComboBox ioOutputGramarCombo;
	private javax.swing.JLabel ioOutputGramarLabel;
	private javax.swing.JTextArea is_enArea;
	private javax.swing.JTextArea is_skArea;
	private javax.swing.JMenuItem jMenuItem1;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JScrollPane jScrollPane4;
	private javax.swing.JScrollPane jScrollPane5;
	private javax.swing.JScrollPane jScrollPane6;
	private javax.swing.JScrollPane jScrollPane7;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JTextArea latex_enArea;
	private javax.swing.JTextArea latex_skArea;
	private javax.swing.JPanel leftMainPanel;
	private javax.swing.JLabel lrAlgorithmDifficultyLabel;
	private javax.swing.ButtonGroup lrAlgorithmMethodGroup;
	private javax.swing.JPanel lrAlgorithmPaneInTab;
	private javax.swing.JPanel lrGramarPaneInTab;
	private javax.swing.JSpinner lrLinesMaximumSpinner;
	private javax.swing.JSpinner lrLinesMinimumSpinner;
	private javax.swing.JSpinner lrMaximumDifficultySpinner;
	private javax.swing.JSpinner lrMinimumDifficultySpinner;
	private javax.swing.JSpinner lrRulesMaximumSpinner;
	private javax.swing.JSpinner lrRulesMinimumSpinner;
	private javax.swing.JPanel lrTabPane;
	private javax.swing.JPanel mainPanel;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JLabel messagesLabel;
	private javax.swing.JLabel nnAlgorithmDifficultyLabel;
	private javax.swing.ButtonGroup nnAlgorithmMethodGroup;
	private javax.swing.JPanel nnAlgorithmPaneInTab;
	private javax.swing.JPanel nnGramarPaneInTab;
	private javax.swing.JSpinner nnLinesMaximumSpinner;
	private javax.swing.JSpinner nnLinesMinimumSpinner;
	private javax.swing.JSpinner nnMaximumDifficultySpinner;
	private javax.swing.JSpinner nnMinimumDifficultySpinner;
	private javax.swing.JSpinner nnRulesMaximumSpinner;
	private javax.swing.JSpinner nnRulesMinimumSpinner;
	private javax.swing.JPanel nnTabPane;
	private javax.swing.JCheckBox noEpsilonChecker;
	private javax.swing.JCheckBox noLeftRekChecker;
	private javax.swing.JCheckBox noSimpleChecker;
	private javax.swing.JCheckBox noUnnormalizedChecker;
	private javax.swing.JCheckBox noUnreachableChecker;
	private javax.swing.JLabel ogLinesNumberLabel;
	private javax.swing.JLabel ogMaxLabel;
	private javax.swing.JSpinner ogMaxLinesSpinner;
	private javax.swing.JSpinner ogMaxRulesSpinner;
	private javax.swing.JLabel ogMinLabel;
	private javax.swing.JSpinner ogMinLinesSpinner;
	private javax.swing.JSpinner ogMinRulesSpinner;
	private javax.swing.JLabel ogRulesNumberLabel;
	private javax.swing.JButton okButton;
	private javax.swing.JPanel outputGramarPane;
	private javax.swing.JLabel percentageLabel;
	private javax.swing.JTextArea plain_enArea;
	private javax.swing.JTextArea plain_skArea;
	private javax.swing.JCheckBox printInputCheck;
	private javax.swing.JProgressBar progressBar;
	private javax.swing.JTabbedPane resultTabbledPane;
	private javax.swing.JPanel rightMainPanel;
	private javax.swing.JLabel runningLabel;
	private javax.swing.JMenuItem saveMenuItem;
	private javax.swing.JPanel settingsPane;
	private javax.swing.JScrollPane settingsScrollPane;
	private javax.swing.JCheckBox showCriteriaCheck;
	private javax.swing.JLabel srAlgorithmDifficultyLabel;
	private javax.swing.ButtonGroup srAlgorithmMethodGroup;
	private javax.swing.JPanel srAlgorithmPaneInTab;
	private javax.swing.JPanel srGramarPaneInTab;
	private javax.swing.JSpinner srLinesMaximumSpinner;
	private javax.swing.JSpinner srLinesMinimumSpinner;
	private javax.swing.JSpinner srMaximumDifficultySpinner;
	private javax.swing.JSpinner srMinimumDifficultySpinner;
	private javax.swing.JSpinner srRulesMaximumSpinner;
	private javax.swing.JSpinner srRulesMinimumSpinner;
	private javax.swing.JPanel srTabPane;
	private javax.swing.JTextPane standardResultTextPane;
	private javax.swing.JLabel statusAnimationLabel;
	private javax.swing.JLabel statusMessageLabel;
	private javax.swing.JPanel statusPanel;
	private javax.swing.JCheckBox subresultsCheck;
	private javax.swing.JLabel unAlMaximumLabel;
	private javax.swing.JLabel unAlMaximumLabel1;
	private javax.swing.JLabel unAlMaximumLabel2;
	private javax.swing.JLabel unAlMaximumLabel4;
	private javax.swing.JLabel unAlMaximumLabel5;
	private javax.swing.JLabel unAlMaximumLabel6;
	private javax.swing.JLabel unAlMaximumLabel7;
	private javax.swing.JLabel unAlMinimumLabel;
	private javax.swing.JLabel unAlMinimumLabel1;
	private javax.swing.JLabel unAlMinimumLabel2;
	private javax.swing.JLabel unAlMinimumLabel4;
	private javax.swing.JLabel unAlMinimumLabel5;
	private javax.swing.JLabel unAlMinimumLabel6;
	private javax.swing.JLabel unAlMinimumLabel7;
	private javax.swing.JLabel unAlgorithmDifficultyLabel;
	private javax.swing.ButtonGroup unAlgorithmMethodGroup;
	private javax.swing.JPanel unAlgorithmPaneInTab;
	private javax.swing.JLabel unGrMaximumLabel;
	private javax.swing.JLabel unGrMaximumLabel1;
	private javax.swing.JLabel unGrMaximumLabel2;
	private javax.swing.JLabel unGrMaximumLabel4;
	private javax.swing.JLabel unGrMaximumLabel5;
	private javax.swing.JLabel unGrMaximumLabel6;
	private javax.swing.JLabel unGrMaximumLabel7;
	private javax.swing.JLabel unGrMinimumLabel;
	private javax.swing.JLabel unGrMinimumLabel1;
	private javax.swing.JLabel unGrMinimumLabel2;
	private javax.swing.JLabel unGrMinimumLabel4;
	private javax.swing.JLabel unGrMinimumLabel5;
	private javax.swing.JLabel unGrMinimumLabel6;
	private javax.swing.JLabel unGrMinimumLabel7;
	private javax.swing.JPanel unGramarPaneInTab;
	private javax.swing.JSpinner unLinesMaximumSpinner;
	private javax.swing.JSpinner unLinesMinimumSpinner;
	private javax.swing.JLabel unLinesNumberLabel;
	private javax.swing.JLabel unLinesNumberLabel1;
	private javax.swing.JLabel unLinesNumberLabel2;
	private javax.swing.JLabel unLinesNumberLabel4;
	private javax.swing.JLabel unLinesNumberLabel5;
	private javax.swing.JLabel unLinesNumberLabel6;
	private javax.swing.JLabel unLinesNumberLabel7;
	private javax.swing.JSpinner unMaximumDifficultySpinner;
	private javax.swing.JSpinner unMinimumDifficultySpinner;
	private javax.swing.JSpinner unRulesMaximumSpinner;
	private javax.swing.JSpinner unRulesMinimumSpinner;
	private javax.swing.JLabel unRulesNumberLabel;
	private javax.swing.JLabel unRulesNumberLabel1;
	private javax.swing.JLabel unRulesNumberLabel2;
	private javax.swing.JLabel unRulesNumberLabel4;
	private javax.swing.JLabel unRulesNumberLabel5;
	private javax.swing.JLabel unRulesNumberLabel6;
	private javax.swing.JLabel unRulesNumberLabel7;
	private javax.swing.JPanel unTabPane;
	private javax.swing.JScrollPane validationScroll;
	private javax.swing.JCheckBoxMenuItem viewAlgorithmsSettingsCheck;
	private javax.swing.JRadioButtonMenuItem viewAllGramarsRadio;
	private javax.swing.ButtonGroup viewButtonGroup;
	private javax.swing.JMenu viewMenu;
	private javax.swing.JRadioButtonMenuItem viewOnlyOutputRadio;
	// End of variables declaration//GEN-END:variables
	private final Timer busyIconTimer;
	private final Icon idleIcon;
	private final Icon[] busyIcons = new Icon[15];
	private int busyIconIndex = 0;
	private JDialog aboutBox;
	private SpinnersSupport spinnersSup;
	private ColorController colorContr;
	private CriteriaConstraintsChecker criteriaChecker;
	private JFileChooser fileChooser;
	private JPanel advancedPanel;
	private ActionMap actionMap;
	private DefaultComboBoxModel inputModel = new DefaultComboBoxModel(InputGrammarForm.values());
	// private DefaultComboBoxModel outputModel = new
	// DefaultComboBoxModel(OutputGrammarForm.values());
	private DefaultComboBoxModel outputModel = new DefaultComboBoxModel(
		AlgorithmHierarchii.after(InputGrammarForm.GENERAL));

	// opravit - pre input GENERAL nepovolit REDUCED grammars

	public JPanel getUnTabPane()
	{
		return unTabPane;
	}

	public JPanel getNnTabPane()
	{
		return nnTabPane;
	}

	public JPanel getEpTabPane()
	{
		return epTabPane;
	}

	public JPanel getSrTabPane()
	{
		return srTabPane;
	}

	public JPanel getLrTabPane()
	{
		return lrTabPane;
	}

	private class ComboBoxesActionListener implements ActionListener
	{

		private boolean ignoreOutput = false;
		private boolean ignoreInput = false;

		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == ioInputGramarCombo)
			{
				if (!ignoreInput)
				{
					ignoreOutput = true;
					boolean remains = false;
					OutputGrammarForm outputSelection = (OutputGrammarForm) ioOutputGramarCombo.getSelectedItem();
					OutputGrammarForm[] after = AlgorithmHierarchii.after((InputGrammarForm) ioInputGramarCombo
						.getSelectedItem());
					outputModel.removeAllElements();
					for (OutputGrammarForm al : after)
					{
						outputModel.addElement(al);
						if (outputSelection.equals(al))
						{
							remains = true;
						}
					}
					if (remains)
					{
						ioOutputGramarCombo.setSelectedItem(outputSelection);
					}
					updateAlgTabPane();
					ignoreOutput = false;
				}
			}
			else if (e.getSource() == ioOutputGramarCombo)
			{
				if (!ignoreOutput)
				{
					ignoreInput = true;

					updateAlgTabPane();
					ignoreInput = false;
				}
			}
		}
	}

	protected void updateAlgTabPane()
	{
		AlgorithmType[] allowed = AlgorithmHierarchii.between((InputGrammarForm) ioInputGramarCombo.getSelectedItem(),
			(OutputGrammarForm) ioOutputGramarCombo.getSelectedItem());
		algorithmsAndGramarTabbedPane.removeAll();
		for (Object o : ALGORITHMS_MAP.values())
		{
			JPanel p = (JPanel) o;
			p.putClientProperty(PANE_USED_CLIENT_PROPERTY, false);
		}

		for (AlgorithmType a : allowed)
		{
			JPanel toAdd = ALGORITHMS_MAP.get(a);
			toAdd.putClientProperty(PANE_USED_CLIENT_PROPERTY, true);
			algorithmsAndGramarTabbedPane.add(toAdd, algorithmsPanelsOrder.get(toAdd));
		}
		algorithmsAndGramarTabbedPane.validate();
		algorithmsAndGramarTabbedPane.repaint();
	}

	public Set<SimpleCriteria> fillInCriteria()
	{
		Set<SimpleCriteria> result = new HashSet<SimpleCriteria>();
		fillCrit(igMinLinesNumberSpinner, AlgorithmType.INPUT, CriteriaType.NONTERMINALS_COUNT, true, result);
		fillCrit(igMaxLinesNumberSpinner, AlgorithmType.INPUT, CriteriaType.NONTERMINALS_COUNT, false, result);
		fillCrit(igMinRulesNumberSpinner, AlgorithmType.INPUT, CriteriaType.RULES_COUNT, true, result);
		fillCrit(igMaxRulesNumberSpinner, AlgorithmType.INPUT, CriteriaType.RULES_COUNT, false, result);
		fillCrit(igMinTerminalNumberSpinner, AlgorithmType.INPUT, CriteriaType.TERMINALS_COUNT, true, result);
		fillCrit(igMaxTerminalNumberSpinner, AlgorithmType.INPUT, CriteriaType.TERMINALS_COUNT, false, result);
		AlgorithmType[] help = AlgorithmHierarchii.between((InputGrammarForm) ioInputGramarCombo.getSelectedItem(),
			(OutputGrammarForm) ioOutputGramarCombo.getSelectedItem());
		AlgorithmType last = help[help.length - 1];
		fillCrit(ogMinLinesSpinner, last, CriteriaType.NONTERMINALS_COUNT, true, result);
		fillCrit(ogMaxLinesSpinner, last, CriteriaType.NONTERMINALS_COUNT, false, result);
		fillCrit(ogMinRulesSpinner, last, CriteriaType.RULES_COUNT, true, result);
		fillCrit(ogMaxRulesSpinner, last, CriteriaType.RULES_COUNT, false, result);
		fillCrit(nnMinimumDifficultySpinner, AlgorithmType.NONGENERATING_ELIM_ALG, CriteriaType.COST_OF_ALGORITHM,
			true, result);
		fillCrit(nnMaximumDifficultySpinner, AlgorithmType.NONGENERATING_ELIM_ALG, CriteriaType.COST_OF_ALGORITHM,
			false, result, true);
		fillCrit(nnLinesMinimumSpinner, AlgorithmType.NONGENERATING_ELIM_ALG, CriteriaType.NONTERMINALS_COUNT, true,
			result);
		fillCrit(nnLinesMaximumSpinner, AlgorithmType.NONGENERATING_ELIM_ALG, CriteriaType.NONTERMINALS_COUNT, false,
			result);
		fillCrit(nnRulesMinimumSpinner, AlgorithmType.NONGENERATING_ELIM_ALG, CriteriaType.RULES_COUNT, true, result);
		fillCrit(nnRulesMaximumSpinner, AlgorithmType.NONGENERATING_ELIM_ALG, CriteriaType.RULES_COUNT, false, result);
		fillCrit(unMinimumDifficultySpinner, AlgorithmType.UNREACHABLE_ELIM_ALG, CriteriaType.COST_OF_ALGORITHM, true,
			result);
		fillCrit(unMaximumDifficultySpinner, AlgorithmType.UNREACHABLE_ELIM_ALG, CriteriaType.COST_OF_ALGORITHM, false,
			result, true);
		fillCrit(unLinesMinimumSpinner, AlgorithmType.UNREACHABLE_ELIM_ALG, CriteriaType.NONTERMINALS_COUNT, true,
			result);
		fillCrit(unLinesMaximumSpinner, AlgorithmType.UNREACHABLE_ELIM_ALG, CriteriaType.NONTERMINALS_COUNT, false,
			result);
		fillCrit(unRulesMinimumSpinner, AlgorithmType.UNREACHABLE_ELIM_ALG, CriteriaType.RULES_COUNT, true, result);
		fillCrit(unRulesMaximumSpinner, AlgorithmType.UNREACHABLE_ELIM_ALG, CriteriaType.RULES_COUNT, false, result);
		fillCrit(epMinimumDifficultySpinner, AlgorithmType.EPSILON_ELIM_ALG, CriteriaType.COST_OF_ALGORITHM, true,
			result);
		fillCrit(epMaximumDifficultySpinner, AlgorithmType.EPSILON_ELIM_ALG, CriteriaType.COST_OF_ALGORITHM, false,
			result);
		fillCrit(epLinesMinimumSpinner, AlgorithmType.EPSILON_ELIM_ALG, CriteriaType.NONTERMINALS_COUNT, true, result);
		fillCrit(epLinesMaximumSpinner, AlgorithmType.EPSILON_ELIM_ALG, CriteriaType.NONTERMINALS_COUNT, false, result);
		fillCrit(epRulesMinimumSpinner, AlgorithmType.EPSILON_ELIM_ALG, CriteriaType.RULES_COUNT, true, result);
		fillCrit(epRulesMaximumSpinner, AlgorithmType.EPSILON_ELIM_ALG, CriteriaType.RULES_COUNT, false, result);
		fillCrit(srMinimumDifficultySpinner, AlgorithmType.SIMPLE_RULES_ELIM_ALG, CriteriaType.COST_OF_ALGORITHM, true,
			result);
		fillCrit(srMaximumDifficultySpinner, AlgorithmType.SIMPLE_RULES_ELIM_ALG, CriteriaType.COST_OF_ALGORITHM,
			false, result);
		fillCrit(srLinesMinimumSpinner, AlgorithmType.SIMPLE_RULES_ELIM_ALG, CriteriaType.NONTERMINALS_COUNT, true,
			result);
		fillCrit(srLinesMaximumSpinner, AlgorithmType.SIMPLE_RULES_ELIM_ALG, CriteriaType.NONTERMINALS_COUNT, false,
			result);
		fillCrit(srRulesMinimumSpinner, AlgorithmType.SIMPLE_RULES_ELIM_ALG, CriteriaType.RULES_COUNT, true, result);
		fillCrit(srRulesMaximumSpinner, AlgorithmType.SIMPLE_RULES_ELIM_ALG, CriteriaType.RULES_COUNT, false, result);
		fillCrit(lrMinimumDifficultySpinner, AlgorithmType.LRECURSION_ELIM_ALG, CriteriaType.COST_OF_ALGORITHM, true,
			result);
		fillCrit(lrMaximumDifficultySpinner, AlgorithmType.LRECURSION_ELIM_ALG, CriteriaType.COST_OF_ALGORITHM, false,
			result);
		fillCrit(lrLinesMinimumSpinner, AlgorithmType.LRECURSION_ELIM_ALG, CriteriaType.NONTERMINALS_COUNT, true,
			result);
		fillCrit(lrLinesMaximumSpinner, AlgorithmType.LRECURSION_ELIM_ALG, CriteriaType.NONTERMINALS_COUNT, false,
			result);
		fillCrit(lrRulesMinimumSpinner, AlgorithmType.LRECURSION_ELIM_ALG, CriteriaType.RULES_COUNT, true, result);
		fillCrit(lrRulesMaximumSpinner, AlgorithmType.LRECURSION_ELIM_ALG, CriteriaType.RULES_COUNT, false, result);
		fillCrit(cfMinimumDifficultySpinner, AlgorithmType.CNF_CREATION_ALG, CriteriaType.COST_OF_ALGORITHM, true,
			result);
		fillCrit(cfMaximumDifficultySpinner, AlgorithmType.CNF_CREATION_ALG, CriteriaType.COST_OF_ALGORITHM, false,
			result);
		fillCrit(cfLinesMinimumSpinner, AlgorithmType.CNF_CREATION_ALG, CriteriaType.NONTERMINALS_COUNT, true, result);
		fillCrit(cfLinesMaximumSpinner, AlgorithmType.CNF_CREATION_ALG, CriteriaType.NONTERMINALS_COUNT, false, result);
		fillCrit(cfRulesMinimumSpinner, AlgorithmType.CNF_CREATION_ALG, CriteriaType.RULES_COUNT, true, result);
		fillCrit(cfRulesMaximumSpinner, AlgorithmType.CNF_CREATION_ALG, CriteriaType.RULES_COUNT, false, result);
		fillCrit(gfMinimumDifficultySpinner, AlgorithmType.GNF_CREATION_ALG, CriteriaType.COST_OF_ALGORITHM, true,
			result);
		fillCrit(gfMaximumDifficultySpinner, AlgorithmType.GNF_CREATION_ALG, CriteriaType.COST_OF_ALGORITHM, false,
			result);
		fillCrit(gfLinesMinimumSpinner, AlgorithmType.GNF_CREATION_ALG, CriteriaType.NONTERMINALS_COUNT, true, result);
		fillCrit(gfLinesMaximumSpinner, AlgorithmType.GNF_CREATION_ALG, CriteriaType.NONTERMINALS_COUNT, false, result);
		fillCrit(gfRulesMinimumSpinner, AlgorithmType.GNF_CREATION_ALG, CriteriaType.RULES_COUNT, true, result);
		fillCrit(gfRulesMaximumSpinner, AlgorithmType.GNF_CREATION_ALG, CriteriaType.RULES_COUNT, false, result);

		if (ioOutputGramarCombo.getSelectedItem().equals(OutputGrammarForm.NO_USELESS))
		{
			result.add(new SimpleCriteria(AlgorithmType.UNREACHABLE_ELIM_ALG, CriteriaType.USELESS_NO_PUNISH, false, 0,
				ResourceBundle.getBundle("cz/muni/fi/cfgexamplegenerator/resources/CFGExampleGeneratorView").getString(
					"PENALTY"), 0));
		}

		result.add(new SimpleCriteria(AlgorithmType.INPUT, CriteriaType.NOT_SAME, true, 1,
			"Grammar was identical to some of previously generated", 1));
		// lokalizovat tieto stringy
		for (AlgorithmType al : help)
		{
			result.add(new SimpleCriteria(al, CriteriaType.MORE_RULES_THEN_ALLOWED, false, MAX_RULES + 1,
				"Maximal number of " + MAX_RULES + " was exceeded during generation", 0));
		}

		return result;
	}

	private boolean contains(Object[] arr, Object elem)
	{
		if (arr == null)
		{
			throw new NullPointerException("arr");
		}

		boolean result = false;
		for (Object o : arr)
		{
			result = result || elem.equals(o);
		}

		return result;
	}

	public AlgorithmType[] getAlgToRunThrough()
	{
		return AlgorithmHierarchii.between((InputGrammarForm) ioInputGramarCombo.getSelectedItem(),
			(OutputGrammarForm) ioOutputGramarCombo.getSelectedItem());
	}

	private void fillCrit(JSpinner spin, AlgorithmType form, CriteriaType type, boolean lower, Set<SimpleCriteria> toAdd)
	{
		fillCrit(spin, form, type, lower, toAdd, false);
	}

	private void fillCrit(JSpinner spin, AlgorithmType form, CriteriaType type, boolean lower,
		Set<SimpleCriteria> toAdd, boolean notCritical)
	{
		if (spinnersSup.isCriteriaUsed(spin))
		{
			toAdd.add(new SimpleCriteria(form, type, lower, (Integer) spin.getValue(), spin, notCritical));
		}
	}

	public InputGrammarForm getInputGrammar()
	{
		return (InputGrammarForm) ioInputGramarCombo.getSelectedItem();
	}

	public OutputGrammarForm getOutputGrammar()
	{
		return (OutputGrammarForm) ioOutputGramarCombo.getSelectedItem();
	}

	public int getNumberOfExamples()
	{
		return (Integer) ioExampleNumberSpinner.getValue();
	}

	public int getMaxRuleLength()
	{
		return (Integer) igMaxRuleLengthSpinner.getValue();
	}

	public Integer getMaxNonTermNumber()
	{
		return spinnersSup.isCriteriaUsed(igMaxLinesNumberSpinner) ? (Integer) igMaxLinesNumberSpinner.getValue()
			: null;
	}

	public Integer getMinNonTermNumber()
	{
		return spinnersSup.isCriteriaUsed(igMinLinesNumberSpinner) ? (Integer) igMinLinesNumberSpinner.getValue()
			: null;
	}

	public Integer getMinRulesNumber()
	{
		return spinnersSup.isCriteriaUsed(igMinRulesNumberSpinner) ? (Integer) igMinRulesNumberSpinner.getValue()
			: null;
	}

	public Integer getMaxTeminalsCount()
	{
		return spinnersSup.isCriteriaUsed(igMaxTerminalNumberSpinner) ? (Integer) igMaxTerminalNumberSpinner.getValue()
			: null;
	}

	public JTextArea getPlain_skArea()
	{
		return plain_skArea;
	}

	public JTextArea getLatex_skArea()
	{
		return latex_skArea;
	}

	public JTextArea getIs_skArea()
	{
		return is_skArea;
	}

	public JTextArea getPlain_enArea()
	{
		return plain_enArea;
	}

	public JTextArea getLatex_enArea()
	{
		return latex_enArea;
	}

	public JTextArea getIs_enArea()
	{
		return is_enArea;
	}

	private void storeViewPreferences()
	{
		Preferences pref = Preferences.userNodeForPackage(this.getClass());
		pref.putBoolean(ALL_GRAM_PREF_KEY, viewAllGramarsRadio.isSelected());
		pref.putBoolean(ALG_SHOWN_PREF_KEY, viewAlgorithmsSettingsCheck.isSelected());
		try
		{
			pref.flush();
		}
		catch (BackingStoreException ex)
		{
			Logger.getLogger(CFGExampleGeneratorView.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Action
	public void detailsDialogAction()
	{
		if (detailsDialog.isVisible())
		{
			detailsDialog.setVisible(false);
			detailedInformationCheck.setSelected(false);
		}
		else
		{
			if (detailsCentredPosition)
			{
				ignoreMove++;
				ignoreMove++;
				detailsDialog.setLocationRelativeTo(CFGExampleGeneratorApp.getApplication().getMainFrame());
			}

			detailsDialog.setVisible(true);
			detailedInformationCheck.setSelected(true);
		}
	}

	@Action
	public void showCriteriaComponentsAction()
	{
		Map<JSpinner, Integer> rows = getCriteriaTableRows();
		if (showCriteriaCheck.isSelected())
		{
			for (JSpinner s : rows.keySet())
			{
				colorContr.addNewSpinnerProp(
					s,
					ColorPriority.HARD_TO_FULLFILL,
					new Color(255, Math.max(0, 255 - (int) (rows.get(s) * COLOR_MULTIPLIER)), Math.max(0,
						255 - (int) (rows.get(s) * COLOR_MULTIPLIER))));
			}
		}
		else
		{
			for (JSpinner s : rows.keySet())
			{
				colorContr.removeSpinnerProp(s, ColorPriority.HARD_TO_FULLFILL);
			}
		}
	}

	public Map<JSpinner, Integer> getCriteriaTableRows()
	{
		String key;
		Map<JSpinner, Integer> rows = new HashMap<JSpinner, Integer>();
		for (int i = 0; i < criteriumTable.getRowCount(); i++)
		{
			key = criteriumTable.getModel().getValueAt(i, 0).toString();
			if (!nameToComponent.containsKey(key))
			{
				continue;
			}
			if (nameToComponent.get(key) instanceof JSpinner)
			{
				rows.put((JSpinner) nameToComponent.get(key),
					Integer.parseInt(criteriumTable.getModel().getValueAt(i, 1).toString().replaceAll("\\%", "")));
			}
		}
		return rows;
	}

	void setFunctionsEnabled(boolean generanInProgres)
	{
		generationInProgress = generanInProgres;
		printInputCheck.setEnabled(!generanInProgres);
		JComponent comp;
		for (String key : nameToComponent.keySet())
		{
			comp = nameToComponent.get(key);
			if (comp.getClientProperty(SPINNER_DISABLED_BY_RADIO) == null
				|| comp.getClientProperty(SPINNER_DISABLED_BY_RADIO).equals(false))
			{
				comp.setEnabled(!generanInProgres);
			}
		}
		for (String s : ACTIONS_TO_DISABLE_WHEN_GENERATING)
		{
			actionMap.get(s).setEnabled(!generanInProgres);
		}
		for (String s : ACTIONS_TO_ENABLE_WHEN_GENERATING)
		{
			actionMap.get(s).setEnabled(generanInProgres);
		}
	}

	public boolean isGenerationInProgress()
	{
		return generationInProgress;
	}

	@Action
	public void applySettings()
	{
		for (BooleanPropertyWithDefault b : booleans)
		{
			b.apply();
		}
	}

	@Action
	public void cancelAction()
	{
		for (BooleanPropertyWithDefault b : booleans)
		{
			b.cancel();
		}
		advancedSettingsDialog.setVisible(false);
	}

	@Action
	public void okAction()
	{
		applySettings();
		advancedSettingsDialog.setVisible(false);
	}

	@Action
	public void setDefaultAction()
	{
		for (BooleanPropertyWithDefault b : booleans)
		{
			b.setDefault();
		}
	}

	private class Enabler implements PropertyChangeListener
	{

		public void propertyChange(PropertyChangeEvent evt)
		{
			applyButton.setEnabled(true);
		}
	}

	void dialogShown()
	{
		applyButton.setEnabled(false);
	}

	@SuppressWarnings("resource")
	@Action
	public void quickSaveAction()
	{
		if (lastSaveLocation == null)
		{
			saveAction();
			return;
		}
		try
		{
			FileOutputStream outToFile = new FileOutputStream(lastSaveLocation);
			Writer writer = new OutputStreamWriter(outToFile, "UTF8");
			BufferedWriter buf = new BufferedWriter(writer);
			Component comp = resultTabbledPane.getSelectedComponent();
			if (comp == null)
			{
				return;
			}
			JScrollPane pane = (JScrollPane) comp;
			JTextArea area = null;
			for (Component c : pane.getViewport().getComponents())
			{
				if (c instanceof JTextArea)
				{
					area = (JTextArea) c;
				}
			}
			if (area == null)
			{
				throw new InternalError("JScroll pane in tab does not contain JTextArea");
			}

			area.write(buf);
			buf.flush();
			buf.close();
		}
		catch (IOException ex)
		{
			showFileWriteErrorDialog(ex);
		}
	}

	@Action
	public void allGramarsVisible()
	{
		setAllGrammarsVisible(true);
	}

	@Action
	public void onlyOutputGramar()
	{
		setAllGrammarsVisible(false);
	}

	@Action
	public void viewAlgorithms()
	{
		setViewAlgorithms(viewAlgorithmsSettingsCheck.isSelected());
	}

	public boolean isShowSubresults()
	{
		return subresultsCheck.isSelected();
	}

	private CFGExampleGeneratorView parent;
	private List<BooleanPropertyWithDefault> booleans = new ArrayList<BooleanPropertyWithDefault>();
	private PropertyChangeListener enabler = new Enabler();
	private JPanel[] gramarPanes;
	private JPanel[] algorithmsPanes;
}
