
package generator.core;

import generator.common.GenericModulePane;
import generator.common.ModuleFactory;
import generator.common.tools.CommonUtils;
import generator.common.tools.Constants;
import generator.common.tools.CriteriaConstraintsChecker;
import generator.communication.ExampleConsumer;
import generator.communication.ModuleToCore;
import generator.communication.dto.ExampleDTO;
import generator.modules.GeneratorMode;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FontUIResource;

/**
 * Copyright 2014 Jiří Uhlíř Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 * 
 * @author Jiri Uhlir
 */
public class FormalLanguagesExampleGenerator extends JFrame
{

	private static final long serialVersionUID = 1L;

	private ResourceBundle resourceBundle;
	private static Locale currentLocale;
	private GenericModulePane activeModule;
	private Map<String, GenericModulePane> modulesMap = new HashMap<String, GenericModulePane>();
	private Map<String, JTextArea> outputTextAreasMap = new HashMap<String, JTextArea>();
	private BlockingQueue<ExampleDTO> exampleQueue = new LinkedBlockingQueue<ExampleDTO>(Integer.MAX_VALUE);
	private ExampleConsumer exampleConsumer;
	public static boolean IS_GENERATING_ACTIVE = false;
	private JCheckBoxMenuItem enOutput = new JCheckBoxMenuItem();
	private JCheckBoxMenuItem czOutput = new JCheckBoxMenuItem();

	/**
	 * Indicates whether application should be used as singlethread or multithread in terms of generation, defaults as
	 * singlethread
	 */
	public static boolean MULTI_THREAD_GENERATING = false;
	private static Font TEXT_AREA_FONT = new Font("Monospaced", Font.PLAIN, 12);

	private static FormalLanguagesExampleGenerator instance;

	public static JLabel getStatusLabel()
	{
		return statusMessageLabel;
	}

	public static Locale getCurrentLocale()
	{
		return currentLocale;
	}

	public static FormalLanguagesExampleGenerator getCoreInstance()
	{
		return instance;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				// laters to set lookand feel
				// try {
				// MetalLookAndFeel.setCurrentTheme(new
				// customLookAndFeelTheme());
				// UIManager.setLookAndFeel(new MetalLookAndFeel());
				// } catch (UnsupportedLookAndFeelException e) {
				// e.printStackTrace();
				// }

				String name = UIManager.getSystemLookAndFeelClassName();
				try
				{
					UIManager.setLookAndFeel(name);
					// UIManager.getDefaults().remove("Label.font");

					// UIManager.get
					// set default fonts to make app consistent throughout different OS
					// UIManager.put("Label.font", new FontUIResource(Constants.DEFAULT_WIN_FONT));
					// UIManager.put("CheckBox.font", new FontUIResource(Constants.DEFAULT_WIN_FONT));
					// UIManager.put("Menu.font", new FontUIResource(Constants.DEFAULT_WIN_FONT));
					// UIManager.put("MenuItem.font", new FontUIResource(Constants.DEFAULT_WIN_FONT));
					// UIManager.put("RadioButton.font", new FontUIResource(Constants.DEFAULT_WIN_FONT));
					// UIManager.put("CheckBoxMenuItem.font", new FontUIResource(Constants.DEFAULT_WIN_FONT));
					// UIManager.put("RadioButtonMenuItem.font", new FontUIResource(Constants.DEFAULT_WIN_FONT));
					// UIManager.put("Button.font", new FontUIResource(Constants.DEFAULT_WIN_FONT));
					// UIManager.put("TabbedPane.font", new FontUIResource(Constants.DEFAULT_WIN_FONT));
					// UIManager.put("TabbedPane.font", new FontUIResource(Constants.DEFAULT_WIN_FONT));
					// UIManager.put("ComboBox.font", new FontUIResource(Constants.DEFAULT_WIN_FONT));
					UIManager.put("TitledBorder.font", new FontUIResource(Constants.DEFAULT_WIN_FONT));
					UIManager.put("ToolTip.font", new FontUIResource(Constants.DEFAULT_WIN_FONT));

					// for (Entry<Object,Object> entry : UIManager.getDefaults().entrySet()){
					// if (entry.getKey().toString().contains("font")) System.out.println(entry);
					// }

				}
				catch (ClassNotFoundException e)
				{
					e.printStackTrace();
				}
				catch (InstantiationException e)
				{
					e.printStackTrace();
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
				catch (UnsupportedLookAndFeelException e)
				{
					e.printStackTrace();
				}
				FormalLanguagesExampleGenerator window = new FormalLanguagesExampleGenerator();
				window.setVisible(true);

			}
		});

	}

	/**
	 * Create the application.
	 */
	public FormalLanguagesExampleGenerator()
	{
		initialize();
		instance = this;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		// JFrame.setDefaultLookAndFeelDecorated(true);

		// open in lefttop corner
		this.setBounds(0, 0, 450, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		// set borderlayout, so the statusbar is always displayed
		coreMainPanel.setLayout(new BorderLayout());

		// initialize right panel components

		setupResultTabbedPane();

		setupLeftPanel();
		setupRightPanel();
		setupStatusBar();
		setupMenu();

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));

		mainPanel.add(leftMainPanel);
		mainPanel.add(rightMainPanel);

		// ensures that everything holds to topleft borders and status panel is
		// always displayed
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		getContentPane().add(statusPanel, BorderLayout.SOUTH);

		// limit height resizing of progressBar
		statusPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, progressBar.getHeight()));

		this.pack();

		int processorsAvailable = Runtime.getRuntime().availableProcessors();

		if (processorsAvailable >= 4) // If there is enough cores available for generating
		{
			MULTI_THREAD_GENERATING = true;
			exampleConsumer = new ExampleConsumer(exampleQueue, outputTextAreasMap, this);
			exampleConsumer.execute();
			CommonUtils.setNumberOfThreads(2);
			// or more if we have more than 6 cores, etc...
		}// else use singlethread with publish

		// temporary before remove
		// detailedInformationCheck.setVisible(false);
		activeModule = modulesMap.get(GeneratorMode.values()[0].getGeneratorModeName());

		this.addWindowListener(new WindowListener()
		{

			@Override
			public void windowOpened(WindowEvent e)
			{
			}

			@Override
			public void windowIconified(WindowEvent e)
			{
			}

			@Override
			public void windowDeiconified(WindowEvent e)
			{
			}

			@Override
			public void windowDeactivated(WindowEvent e)
			{
			}

			@Override
			public void windowClosing(WindowEvent e)
			{
				storeViewPreferences();

			}

			@Override
			public void windowClosed(WindowEvent e)
			{
				storeViewPreferences();

			}

			@Override
			public void windowActivated(WindowEvent e)
			{
			}
		});
		changeLocalization(new Locale("en"));
		loadViewPreferences();

		// setMinimumSize(new Dimension(leftMainPanel.getWidth() + rightMainPanel.getWidth() + 40, 400));
		setMinimumSize(new Dimension(leftMainPanel.getWidth() + rightMainPanel.getWidth() - 200, 400));

		loadDefaultSettings();
	}

	private void setupResultTabbedPane()
	{
		resultTabbedPaneEn.setMinimumSize(new java.awt.Dimension(23, 80));
		plainAreaCz.setFont(TEXT_AREA_FONT);

		plainAreaCz.setColumns(80);
		plainAreaCz.setEditable(false);
		plainAreaCz.setRows(5);
		plainAreaCz.setName("PlainCZ"); // NOI18N

		plainAreaEn.setFont(TEXT_AREA_FONT);
		plainAreaEn.setColumns(20);
		plainAreaEn.setEditable(false);
		plainAreaEn.setRows(5);
		plainAreaEn.setName("PlainEN"); // NOI18N

		latexAreaEn.setFont(TEXT_AREA_FONT);
		latexAreaEn.setColumns(20);
		latexAreaEn.setEditable(false);
		latexAreaEn.setRows(5);
		latexAreaEn.setName("LaTeXEN"); // NOI18N

		latexAreaCz.setFont(TEXT_AREA_FONT);
		latexAreaCz.setColumns(20);
		latexAreaCz.setEditable(false);
		latexAreaCz.setRows(5);
		latexAreaCz.setName("LaTeXCZ"); // NOI18N

		isAreaEn.setFont(TEXT_AREA_FONT);
		isAreaEn.setColumns(20);
		isAreaEn.setEditable(false);
		isAreaEn.setRows(5);
		isAreaEn.setName("ISEN"); // NOI18N

		isAreaCz.setFont(TEXT_AREA_FONT);
		isAreaCz.setColumns(20);
		isAreaCz.setEditable(false);
		isAreaCz.setRows(5);
		isAreaCz.setName("ISCZ"); // NOI18N

		scrollPaneIsCz.setName("ISCZ");
		scrollPaneIsEn.setName("ISEN");
		scrollPaneLatexCz.setName("LaTeXCZ");
		scrollPaneLatexEn.setName("LaTeXEN");
		scrollPanePlainCz.setName("PlainCZ");
		scrollPanePlainEn.setName("PlainEN");

		scrollPanePlainCz.setViewportView(plainAreaCz);
		scrollPanePlainEn.setViewportView(plainAreaEn);
		scrollPaneLatexCz.setViewportView(latexAreaCz);
		scrollPaneLatexEn.setViewportView(latexAreaEn);
		scrollPaneIsCz.setViewportView(isAreaCz);
		scrollPaneIsEn.setViewportView(isAreaEn);

		resultTabbedPane.setFont(Constants.DEFAULT_WIN_FONT);
		resultTabbedPane.addTab("PlainCZ", scrollPanePlainCz);
		resultTabbedPane.addTab("LaTeXCZ", scrollPaneLatexCz);
		resultTabbedPane.addTab("ISCZ", scrollPaneIsCz);
		resultTabbedPane.addTab("PlainEN", scrollPanePlainEn);
		resultTabbedPane.addTab("LaTeXEN", scrollPaneLatexEn);
		resultTabbedPane.addTab("ISEN", scrollPaneIsEn);

		outputTextAreasMap.put("plainCZ", plainAreaCz);
		outputTextAreasMap.put("plainEN", plainAreaEn);
		outputTextAreasMap.put("latexCZ", latexAreaCz);
		outputTextAreasMap.put("latexEN", latexAreaEn);
		outputTextAreasMap.put("isCZ", isAreaCz);
		outputTextAreasMap.put("isEN", isAreaEn);

	}
	private void setupMenu()
	{
		this.setJMenuBar(menuBar);

		saveOutputActive.setText("Save");
		saveOutputActive.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				File outputFile = null;
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileFilter()
				{

					@Override
					public String getDescription()
					{
						return "*.txt";
					}

					@Override
					public boolean accept(File f)
					{
						if (!f.getName().endsWith(".txt") && !f.isDirectory())
							return false;
						return true;
					}
				});
				int returnVal = fileChooser.showSaveDialog(FormalLanguagesExampleGenerator.getCoreInstance());
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					outputFile = fileChooser.getSelectedFile();
					if (!outputFile.getAbsolutePath().endsWith(".txt"))
					{
						outputFile = new File(outputFile.getAbsolutePath() + ".txt");
					}
				}
				if (outputFile != null)
					saveOutput(outputFile);

			}
		});
		// saveOutputAll.setText("Save All");
		saveProfile.setText("Save Profile");
		saveProfile.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				File settingsFile = null;
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileFilter()
				{

					@Override
					public String getDescription()
					{
						return "*.txt";
					}

					@Override
					public boolean accept(File f)
					{
						if (!f.getName().endsWith(".txt") && !f.isDirectory())
							return false;
						return true;
					}
				});
				int returnVal = fileChooser.showSaveDialog(FormalLanguagesExampleGenerator.getCoreInstance());
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					settingsFile = fileChooser.getSelectedFile();
					if (!settingsFile.getAbsolutePath().endsWith(".txt"))
					{
						settingsFile = new File(settingsFile.getAbsolutePath() + ".txt");
					}
				}
				if (settingsFile != null)
					saveSettings(settingsFile);

			}
		});
		loadProfile.setText("Load Profile");
		loadProfile.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				File settingsFile = null;
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileFilter()
				{

					@Override
					public String getDescription()
					{
						return "*.txt";
					}

					@Override
					public boolean accept(File f)
					{
						if (!f.getName().endsWith(".txt") && !f.isDirectory())
							return false;
						return true;
					}
				});
				int returnVal = fileChooser.showOpenDialog(FormalLanguagesExampleGenerator.getCoreInstance());
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					settingsFile = fileChooser.getSelectedFile();
				}
				if (settingsFile != null)
					loadSettings(settingsFile);

			}
		});
		loadDefaultProfile.setText("Load Default Profile");
		loadDefaultProfile.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				loadDefaultSettings();
			}
		});
		exit.setText("Exit");

		exit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});

		moduleChoiceMenu.setText("mode selection");

		ButtonGroup modeGroup = new ButtonGroup();
		boolean modeSelected = false;

		for (final GeneratorMode mode : GeneratorMode.values())
		{
			final JRadioButtonMenuItem modeItem = new JRadioButtonMenuItem();
			if (!modeSelected) {
				modeSelected = true;
				modeItem.setSelected(true);
			}
			modeItem.setText(mode.getGeneratorModeName());
			modeItem.addActionListener(new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					CardLayout layout = (CardLayout) inputModePane.getLayout();
					activeModule = modulesMap.get(mode.getGeneratorModeName());
					instance.setTitle(resourceBundle.getString("ApplicationTitle") + " - " + resourceBundle.getString(mode.getGeneratorModeName()));
					layout.show(inputModePane, mode.getGeneratorModeName());
				}
			});
			moduleChoiceMenu.add(modeItem);
			modeGroup.add(modeItem);
		}

		languageSelectionMenu.setText("SelectLanguage");
		// languageSelectionMenu.setsetOpaque(false);

		ButtonGroup languageButtonGroup = new ButtonGroup();

		czLangItem.setText("cz");
		languageButtonGroup.add(czLangItem);
		czLangItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				changeLocalization(new Locale("cz"));
			}
		});

		enLangItem.setText("en");
		languageButtonGroup.add(enLangItem);

		enLangItem.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				changeLocalization(new Locale("en"));
			}
		});

		languageSelectionMenu.add(czLangItem);
		languageSelectionMenu.add(enLangItem);

		enLangItem.setSelected(true);

		enOutput.setText("onlyEn");
		enOutput.setSelected(true);
		enOutput.addActionListener(new OutputSelectionActionListener());

		czOutput.setText("onlyCz");
		czOutput.setSelected(true);
		czOutput.addActionListener(new OutputSelectionActionListener());

		subresultsCheck.setSelected(true);
		subresultsCheck.setText("subresultsCheck.text"); // NOI18N
		subresultsCheck.setName("subresultsCheck"); // NOI18N

		printInputCheck.setSelected(true);
		printInputCheck.setText("printInputCheck.text"); // NOI18N
		printInputCheck.setName("printInputCheck"); // NOI18N

		viewSelectionMenu.add(enOutput);
		viewSelectionMenu.add(czOutput);
		viewSelectionMenu.add(new JSeparator());
		viewSelectionMenu.add(subresultsCheck);
		// viewSelectionMenu.add(printInputCheck);

		fileMenu.add(saveOutputActive);
		// fileMenu.add(saveOutputAll);
		fileMenu.add(new JSeparator());
		fileMenu.add(saveProfile);
		fileMenu.add(loadProfile);
		fileMenu.add(loadDefaultProfile);
		fileMenu.add(new JSeparator());
		fileMenu.add(languageSelectionMenu);
		fileMenu.add(new JSeparator());
		fileMenu.add(exit);

		menuBar.add(fileMenu);
		fileMenu.setMargin(new Insets(0, 20, 0, 20));

		menuBar.add(moduleChoiceMenu);
		moduleChoiceMenu.setMargin(new Insets(0, 20, 0, 20));

		menuBar.add(viewSelectionMenu);
		viewSelectionMenu.setMargin(new Insets(0, 20, 0, 20));

		about.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				JDialog aboutDialog = new AboutJDialog(instance, true, resourceBundle);
				aboutDialog.setVisible(true);
			}
		});
		helpMenu.add(about);

		menuBar.add(helpMenu);
		helpMenu.setMargin(new Insets(0, 20, 0, 20));

		updateComponent(menuBar);

	}
	private void updateComponent(JComponent c)
	{
		c.setFont(Constants.DEFAULT_WIN_FONT);
		for (Component comp : c.getComponents())
		{
			if (comp instanceof JComponent)
				updateComponent((JComponent) comp);
		}
		if (c instanceof JMenu)
		{
			for (Component comp : ((JMenu) c).getMenuComponents())
			{
				updateComponent((JComponent) comp);
			}
		}

	}

	private class OutputSelectionActionListener implements ActionListener
	{

		public void actionPerformed(ActionEvent e)
		{
			resultTabbedPane.removeAll();
			if (enOutput.isSelected() && czOutput.isSelected())
			{
				resultTabbedPane.addTab(resourceBundle.getString("PlainCZ"), scrollPanePlainCz);
				resultTabbedPane.addTab(resourceBundle.getString("LaTeXCZ"), scrollPaneLatexCz);
				resultTabbedPane.addTab(resourceBundle.getString("ISCZ"), scrollPaneIsCz);
				resultTabbedPane.addTab(resourceBundle.getString("PlainEN"), scrollPanePlainEn);
				resultTabbedPane.addTab(resourceBundle.getString("LaTeXEN"), scrollPaneLatexEn);
				resultTabbedPane.addTab(resourceBundle.getString("ISEN"), scrollPaneIsEn);
			}
			else if (enOutput.isSelected() && !czOutput.isSelected())
			{
				resultTabbedPane.addTab(resourceBundle.getString("PlainEN"), scrollPanePlainEn);
				resultTabbedPane.addTab(resourceBundle.getString("LaTeXEN"), scrollPaneLatexEn);
				resultTabbedPane.addTab(resourceBundle.getString("ISEN"), scrollPaneIsEn);
			}
			else if (!enOutput.isSelected() && czOutput.isSelected())
			{
				resultTabbedPane.addTab(resourceBundle.getString("PlainCZ"), scrollPanePlainCz);
				resultTabbedPane.addTab(resourceBundle.getString("LaTeXCZ"), scrollPaneLatexCz);
				resultTabbedPane.addTab(resourceBundle.getString("ISCZ"), scrollPaneIsCz);
			}
		}

	}

	private void setupLeftPanel()
	{
		leftMainPanel.setName("leftPanel");
		leftMainPanel.setMinimumSize(new java.awt.Dimension(Constants.WIDTH_LEFT_PANEL_PREFFERED,
			Constants.HEIGHT_LEFT_PANEL_PREFFERED));

		leftMainPanel.setMaximumSize(new java.awt.Dimension(540, 7070));
		leftMainPanel.setPreferredSize(new java.awt.Dimension(Constants.WIDTH_LEFT_PANEL_PREFFERED,
			Constants.HEIGHT_LEFT_PANEL_PREFFERED));

		fillContentOfLeftPanel();

		GroupLayout leftMainPanelLayout = new GroupLayout(leftMainPanel);

		leftMainPanel.setLayout(leftMainPanelLayout);
		leftMainPanelLayout.setHorizontalGroup(leftMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(
				GroupLayout.Alignment.TRAILING,
				leftMainPanelLayout
					.createSequentialGroup()
					.addContainerGap()
					.addComponent(inputModePane, GroupLayout.DEFAULT_SIZE, Constants.WIDTH_LEFT_PANEL_PREFFERED,
						Short.MAX_VALUE).addContainerGap()));
		leftMainPanelLayout.setVerticalGroup(leftMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(
				leftMainPanelLayout
					.createSequentialGroup()
					.addContainerGap()
					.addComponent(inputModePane, GroupLayout.DEFAULT_SIZE, Constants.HEIGHT_LEFT_PANEL_PREFFERED,
						Short.MAX_VALUE).addContainerGap()));

	}

	private void fillContentOfLeftPanel()
	{

		CardLayout inputModePaneLayout = new CardLayout();

		inputModePane.setLayout(inputModePaneLayout);
		try
		{
			for (GeneratorMode mode : GeneratorMode.values())
			{
				GenericModulePane module = ModuleFactory.getModule(mode, this);
				module.setName(mode.getGeneratorModeName());
				modulesMap.put(mode.getGeneratorModeName(), module);
				inputModePane.add(module, mode.getGeneratorModeName());
				module.updateAllFonts();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		inputModePane.setName("inputModePane"); // NOI18N

		inputModePane.setPreferredSize(new Dimension(400, 400));
	}

	private void changeLocalization(Locale newLocale)
	{
		currentLocale = newLocale;
		for (GenericModulePane module : modulesMap.values())
		{
			module.setLocale(newLocale);
		}
		resourceBundle = ResourceBundle.getBundle("mainPanel", newLocale);
		if (exampleConsumer != null)
		{
			exampleConsumer.updateLocale(newLocale);
		}
		updateStrings();
		CriteriaConstraintsChecker.changeLocale(newLocale);
	}

	private void setupRightPanel()
	{
		initButtonAndCheckBoxesForRightPanel();

		GroupLayout rightMainPanelLayout = new GroupLayout(rightMainPanel);

		rightMainPanel.setLayout(rightMainPanelLayout);
		rightMainPanelLayout.setHorizontalGroup(rightMainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(
				rightMainPanelLayout
					.createSequentialGroup()
					.addContainerGap()
					.addGroup(
						rightMainPanelLayout
							.createParallelGroup(GroupLayout.Alignment.LEADING)

							.addGroup(
								rightMainPanelLayout.createSequentialGroup().addGap(6)
									.addComponent(numberOfExamplesJLabel).addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(numberOfExamplesJSpinner).addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(clearAndGenerateButton).addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(cancelButton))// .addGap(200))
							.addComponent(resultTabbedPane, GroupLayout.Alignment.TRAILING, 240, 649, Short.MAX_VALUE))
					.addContainerGap()));
		rightMainPanelLayout.setVerticalGroup(rightMainPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addGroup(
				GroupLayout.Alignment.LEADING,
				rightMainPanelLayout
					.createSequentialGroup()
					.addContainerGap()
					.addGroup(
						rightMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
							.addComponent(numberOfExamplesJLabel).addComponent(numberOfExamplesJSpinner)
							.addComponent(cancelButton).addComponent(clearAndGenerateButton))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(resultTabbedPane, GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE).addContainerGap()));

		numberOfExamplesJLabel.setFont(Constants.DEFAULT_WIN_FONT);

	}

	private void initButtonAndCheckBoxesForRightPanel()
	{
		// cancelButton.setAction("cancelGenerationAction"); // NOI18N
		cancelButton.setText("cancelButton.text"); // NOI18N
		cancelButton.setName("cancelButton"); // NOI18N
		cancelButton.setEnabled(false);
		cancelButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				generatingStopped();
				try
				{
					Thread.sleep(10);
				}
				catch (InterruptedException e1)
				{
					e1.printStackTrace();
				}
				String status = resourceBundle.getString("generatorForcedToStop");
				// Generated "
				// + exampleConsumer.getLastGeneratedExampleNumber() + "/" + numberOfExamplesJSpinner.getValue()
				// + " examples.";
				statusMessageLabel.setText(status);
			}
		});

		cancelButton.setRolloverEnabled(true);
		cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

		clearAndGenerateButton.setFont(Constants.DEFAULT_WIN_FONT);
		Font oldButtonFont = clearAndGenerateButton.getFont();
		Font newButtonBoldFont = new Font(oldButtonFont.getName(), Font.BOLD, oldButtonFont.getSize());
		cancelButton.setFont(newButtonBoldFont);

		clearAndGenerateButton.setFont(newButtonBoldFont);
		clearAndGenerateButton.setText("clearAndGenerateButton.text"); // NOI18N
		clearAndGenerateButton.setName("clearAndGenerateButton"); // NOI18N
		clearAndGenerateButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				// ModuleToCore.getInstance().generationStarted();
				if (MULTI_THREAD_GENERATING)
				{
					exampleConsumer.reset();
				}
				else
				{
					for (JTextArea textArea : outputTextAreasMap.values())
					{
						textArea.setText("");
					}
				}
				generatingStarted();
			}
		});

		clearAndGenerateButton.setRolloverEnabled(true);
		clearAndGenerateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

		numberOfExamplesJSpinner.setValue(1);
		setStandardJComponentSize(numberOfExamplesJSpinner, 65, 25);
		NumberEditor e = (NumberEditor) numberOfExamplesJSpinner.getEditor();
		e.getTextField().setHorizontalAlignment(JTextField.CENTER);
		e.getModel().setMinimum(1);

	}

	private void setupStatusBar()
	{
		statusMessageLabel.setText("textResource"); // NOI18N
		statusMessageLabel.setName("statusMessageLabel"); // NOI18N

		statusAnimationLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

		progressBar.setName("progressBar"); // NOI18N
		progressBar.setStringPainted(true);
		progressBar.setVisible(false);

		messagesLabelPlaceholder.setText(""); // messageLabelRsc
		messagesLabelPlaceholder.setVerticalAlignment(SwingConstants.TOP);
		messagesLabelPlaceholder.setName("messagesLabel"); // NOI18N

		setStandardJComponentSize(statusMessageLabel, 1000, 25);

		statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

		GroupLayout statusPanelLayout = new GroupLayout(statusPanel);

		statusPanel.setLayout(statusPanelLayout);

		statusPanel.setLayout(statusPanelLayout);
		statusPanelLayout.setHorizontalGroup(statusPanelLayout
			.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(
				statusPanelLayout
					.createSequentialGroup()
					.addContainerGap()
					.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(
						statusPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(statusMessageLabel)
							.addComponent(messagesLabelPlaceholder, GroupLayout.DEFAULT_SIZE, 741, Short.MAX_VALUE))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)

					.addComponent(statusAnimationLabel).addContainerGap())
			.addComponent(statusPanelSeparator, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 915,
				Short.MAX_VALUE));
		statusPanelLayout.setVerticalGroup(statusPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(
				GroupLayout.Alignment.TRAILING,
				statusPanelLayout
					.createSequentialGroup()
					.addComponent(statusPanelSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE)
					.addGap(3, 3, 3)
					.addGroup(
						statusPanelLayout
							.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addGroup(
								statusPanelLayout
									.createSequentialGroup()
									.addGroup(
										statusPanelLayout
											.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(statusMessageLabel)
											.addComponent(statusAnimationLabel)
											.addComponent(progressBar, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addGap(3, 3, 3))
							.addComponent(messagesLabelPlaceholder, GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))));
	}

	private void setStandardJComponentSize(JComponent component, int width, int height)
	{
		component.setMaximumSize(new Dimension(width, height));
		component.setMinimumSize(new Dimension(width, height));
		component.setPreferredSize(new Dimension(width, height));
	}
	// components init

	private JPanel coreMainPanel = new JPanel();
	private JPanel mainPanel = new JPanel();

	private JPanel leftMainPanel = new JPanel();
	private JPanel rightMainPanel = new JPanel();
	private JPanel statusPanel = new JPanel();

	private JMenuItem saveOutputActive = new JMenuItem();
	private JMenuItem saveOutputAll = new JMenuItem();
	private JMenuItem saveProfile = new JMenuItem();
	private JMenuItem loadProfile = new JMenuItem();
	private JMenuItem loadDefaultProfile = new JMenuItem();
	private JMenuItem exit = new JMenuItem();
	private JTabbedPane resultTabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private JTabbedPane resultTabbedPaneEn = new JTabbedPane(JTabbedPane.TOP);
	private JTextArea plainAreaCz = new JTextArea();
	private JTextArea plainAreaEn = new JTextArea();
	private JTextArea latexAreaEn = new JTextArea();
	private JTextArea latexAreaCz = new JTextArea();
	private JTextArea isAreaEn = new JTextArea();
	private JTextArea isAreaCz = new JTextArea();
	private JScrollPane scrollPanePlainCz = new JScrollPane();
	private JScrollPane scrollPanePlainEn = new JScrollPane();
	private JScrollPane scrollPaneLatexCz = new JScrollPane();
	private JScrollPane scrollPaneLatexEn = new JScrollPane();
	private JScrollPane scrollPaneIsCz = new JScrollPane();
	private JScrollPane scrollPaneIsEn = new JScrollPane();
	private JProgressBar progressBar = new JProgressBar();
	private static JLabel statusMessageLabel = new JLabel();
	private JMenu helpMenu = new JMenu("About");
	private JMenu languageSelectionMenu = new JMenu("Language selection");
	private JMenu moduleChoiceMenu = new JMenu("Module selection");
	private JMenu fileMenu = new JMenu("File menu");
	private JMenu viewSelectionMenu = new JMenu("View selection");
	private JMenuBar menuBar = new JMenuBar();
	private JLabel messagesLabelPlaceholder = new JLabel();
	private JSeparator statusPanelSeparator = new JSeparator();
	private JLabel statusAnimationLabel = new JLabel();
	private JPanel inputModePane = new JPanel();
	private JSpinner numberOfExamplesJSpinner = new JSpinner();
	private JLabel numberOfExamplesJLabel = new JLabel();

	private JRadioButtonMenuItem czLangItem = new JRadioButtonMenuItem();
	private JRadioButtonMenuItem enLangItem = new JRadioButtonMenuItem();
	private JMenuItem about = new JMenuItem();
	private JMenuItem usageInstructions = new JMenuItem();

	private javax.swing.JButton cancelButton = new JButton();
	private javax.swing.JButton generateButton = new JButton();
	private javax.swing.JButton clearAndGenerateButton = new JButton();
	private javax.swing.JButton clearButton = new JButton();
	private javax.swing.JCheckBoxMenuItem subresultsCheck = new JCheckBoxMenuItem();
	private javax.swing.JCheckBoxMenuItem printInputCheck = new JCheckBoxMenuItem();

	public JProgressBar getProgressBar()
	{
		return progressBar;
	}

	public JButton getCancelButton()
	{
		return cancelButton;
	}

	public JButton getGenerateButton()
	{
		return generateButton;
	}

	public JButton getClearAndGenerateButton()
	{
		return clearAndGenerateButton;
	}

	public JButton getClearButton()
	{
		return clearButton;
	}

	private void updateStrings()
	{
		// plainAreaCz.setText(b.toString());
		moduleChoiceMenu.setText(" " + resourceBundle.getString("ModeSelection") + " ");
		languageSelectionMenu.setText(resourceBundle.getString("LanguageSelection"));
		czLangItem.setText("cz");
		enLangItem.setText("en");
		cancelButton.setText(resourceBundle.getString("cancelButton.text"));
		generateButton.setText(resourceBundle.getString("generateButton.text"));
		clearButton.setText(resourceBundle.getString("clearButton.text"));
		clearAndGenerateButton.setText(resourceBundle.getString("clearAndGenerateButton.text"));
		subresultsCheck.setText(resourceBundle.getString("subresultsCheck.text"));
		printInputCheck.setText(resourceBundle.getString("printInputCheck.text"));
		messagesLabelPlaceholder.setText(""); // messageLabelRsc
		helpMenu.setText(" " + resourceBundle.getString("help") + " ");
		about.setText(resourceBundle.getString("about"));
		usageInstructions.setText(resourceBundle.getString("UsageInstructions"));
		this.setTitle(resourceBundle.getString("ApplicationTitle") + " - " + resourceBundle.getString(activeModule.getName()));
		numberOfExamplesJLabel.setText(resourceBundle.getString("exampleNumber.text"));

		int i = 0;
		for (GeneratorMode mode : GeneratorMode.values())
		{
			moduleChoiceMenu.getItem(i).setText(resourceBundle.getString(mode.getGeneratorModeName()));
			moduleChoiceMenu.getItem(i).setToolTipText(
				resourceBundle.getString(mode.getGeneratorModeName() + ".tooltip"));
			i++;
		}

		viewSelectionMenu.setText(" " + resourceBundle.getString("viewSelection") + " ");
		czOutput.setText(resourceBundle.getString("viewCzOutput"));
		enOutput.setText(resourceBundle.getString("viewEnOutput"));

		czLangItem.setText(resourceBundle.getString("czLang"));
		enLangItem.setText(resourceBundle.getString("enLang"));

		int tabCount = resultTabbedPane.getTabCount();

		for (int j = 0; j < tabCount; j++)
		{
			String tabTitle = resultTabbedPane.getComponentAt(j).getName();
			resultTabbedPane.setTitleAt(j, resourceBundle.getString(tabTitle));
		}
		subresultsCheck.setToolTipText(resourceBundle.getString("subresultsCheckTooltip"));
		printInputCheck.setToolTipText(resourceBundle.getString("printInputCheckTooltip"));
		enOutput.setToolTipText(resourceBundle.getString("onlyEnOutputTooltip"));
		czOutput.setToolTipText(resourceBundle.getString("onlyCzOutputTooltip"));

		fileMenu.setText(resourceBundle.getString("file") + " ");

		saveOutputActive.setText(resourceBundle.getString("saveOutput"));
		saveOutputAll.setText(resourceBundle.getString("saveOutputAll"));

		saveProfile.setText(resourceBundle.getString("saveProfile"));
		saveProfile.setToolTipText(resourceBundle.getString("saveProfile.tooltip"));
		loadProfile.setText(resourceBundle.getString("loadProfile"));
		loadProfile.setToolTipText(resourceBundle.getString("loadProfile.tooltip"));
		loadDefaultProfile.setText(resourceBundle.getString("loadDefaultProfile"));
		loadDefaultProfile.setToolTipText(resourceBundle.getString("loadDefaultProfile.tooltip"));
		exit.setText(resourceBundle.getString("exit"));

	}

	private void loadViewPreferences()
	{
		Preferences pref = Preferences.userNodeForPackage(this.getClass());
		if (pref.get("lang", "cz").equals("cz"))
		{
			czLangItem.setSelected(true);
			changeLocalization(new Locale("cz"));
		}
		else
		{
			enLangItem.setSelected(true);
			changeLocalization(new Locale("en"));
		}
		subresultsCheck.setSelected(pref.getBoolean("showSubresults", true));

	}

	private void storeViewPreferences()
	{
		Preferences pref = Preferences.userNodeForPackage(this.getClass());
		pref.put("lang", resourceBundle.getLocale().getLanguage());
		pref.putBoolean("showSubresults", subresultsCheck.isSelected());
		try
		{
			pref.flush();
		}
		catch (BackingStoreException ex)
		{
			Logger.getLogger(FormalLanguagesExampleGenerator.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	private void saveSettings(File propertiesFile)
	{
		Map<String, Integer> settings = new HashMap<String, Integer>();
		for (GenericModulePane module : modulesMap.values())
		{
			settings.putAll(module.saveSettings());
		}

		Properties applicationProps = new Properties();
		for (String property : settings.keySet())
		{
			applicationProps.put(property, String.valueOf(settings.get(property)));
		}
		try (FileOutputStream out = new FileOutputStream(propertiesFile))
		{
			applicationProps.store(out, "---Stored properties for formal languages example generator---");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private void loadSettings(File propertiesFile)
	{
		Properties configurationProperties = new Properties();
		Map<String, Integer> settings = new HashMap<String, Integer>();

		try (FileInputStream in = new FileInputStream(propertiesFile))
		{

			configurationProperties.load(in);

			for (Object property : configurationProperties.keySet())
			{
				settings.put((String) property, Integer.valueOf((String) configurationProperties.get(property)));
			}
			;

			for (GenericModulePane module : modulesMap.values())
			{
				module.loadSettings(settings);
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private void loadDefaultSettings()
	{
		Properties configurationProperties = new Properties();
		Map<String, Integer> settings = new HashMap<String, Integer>();

		try (InputStream in = getClass().getClassLoader().getResourceAsStream("default.properties"))
		{
			configurationProperties.load(in);

			for (Object property : configurationProperties.keySet())
			{
				settings.put((String) property, Integer.valueOf((String) configurationProperties.get(property)));
			}

			for (GenericModulePane module : modulesMap.values())
			{
				module.loadSettings(settings);
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private void saveOutput(File file)
	{
		try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw);)
		{

			JScrollPane selectedOutputWindow = (JScrollPane) resultTabbedPane.getSelectedComponent();
			JTextArea selectedTextArea = (JTextArea) selectedOutputWindow.getViewport().getView();

			String[] outputText = selectedTextArea.getText().split("\\r?\\n");
			for (String string : outputText)
			{
				bw.append(string);
				bw.newLine();
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * shows the the error dialog pane, if the error occures
	 * 
	 * @param message
	 *            the cause of the error
	 */
	public void showErrorDialog(String message)
	{
		javax.swing.JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * shows the warning dialog pane and resets the stop and generate button
	 * 
	 * @param message
	 *            the cause of the proposition
	 */
	public void showWarningDialog(String message)
	{
		javax.swing.JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
	}
	private long start;

	public void generatingStopped()
	{
		cancelButton.setEnabled(false);
		generateButton.setEnabled(true);
		clearAndGenerateButton.setEnabled(true);
		clearButton.setEnabled(true);
		IS_GENERATING_ACTIVE = false;
		progressBar.setVisible(false);
		progressBar.setValue(0);
//		System.out.println("Runtime: " + (System.currentTimeMillis() - start) + "ms");
		if (MULTI_THREAD_GENERATING)
		{
			exampleConsumer.stopAndResetTimer();
		}
		ModuleToCore.getInstance().stopAndResetTimer();

	}

	public void generatingStarted()
	{
		cancelButton.setEnabled(true);
		generateButton.setEnabled(false);
		clearAndGenerateButton.setEnabled(false);
		clearButton.setEnabled(false);
		progressBar.setVisible(true);
		IS_GENERATING_ACTIVE = true;
		progressBar.setValue(0);
		int numberOfExamplesToGenerate = (Integer) numberOfExamplesJSpinner.getValue();
		if (MULTI_THREAD_GENERATING)
		{
			exampleConsumer.generationStarted();
			exampleConsumer.setTotalExampleCount(numberOfExamplesToGenerate);
		}
		ModuleToCore.getInstance().updateLocale(resourceBundle.getLocale());
		ModuleToCore.getInstance().generationStarted();
		start = System.currentTimeMillis();
		activeModule.generate(numberOfExamplesToGenerate);
	}

	public BlockingQueue<ExampleDTO> getExampleQueue()
	{
		return exampleQueue;
	}

	public Map<String, JTextArea> getTextAreas()
	{
		return outputTextAreasMap;
	}

	public boolean getPrintInput()
	{
		return printInputCheck.isSelected();
	}

	public boolean isShowSubresults()
	{
		return subresultsCheck.isSelected();
	}
}
