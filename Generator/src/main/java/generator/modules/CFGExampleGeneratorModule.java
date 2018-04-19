
package generator.modules;

import generator.common.GenericModulePane;
import generator.common.tools.Constants;
import generator.common.tools.CriteriaConstraintsChecker;
import generator.common.tools.SpinnersSupport;
import generator.core.FormalLanguagesExampleGenerator;
import generator.modules.cfgexamplegenerator.ColorController;
import generator.modules.cfgexamplegenerator.GenerationWorker;
import generator.modules.cfgexamplegenerator.Publisher;
import generator.modules.cfgexamplegenerator.generator.AlgorithmHierarchii;
import generator.modules.cfgexamplegenerator.generator.AlgorithmType;
import generator.modules.cfgexamplegenerator.generator.CriteriaType;
import generator.modules.cfgexamplegenerator.generator.InputGrammarForm;
import generator.modules.cfgexamplegenerator.generator.OutputGrammarForm;
import generator.modules.cfgexamplegenerator.generator.SimpleCriteria;
import javafx.util.Pair;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
public class CFGExampleGeneratorModule extends GenericModulePane
{

	ResourceBundle resourceBundle = ResourceBundle.getBundle("cflModule", new Locale("en"));

	public static final int MAX_RULES = 100;
	public static final String PANE_USED_CLIENT_PROPERTY = "PANE_USED_CLIENT_PROPERTY";

	public static final String SPINNER_DISABLED_BY_RADIO = "SPINNER_DISABLED_BY_RADIO";
	public static final Map<AlgorithmType, JPanel> ALGORITHMS_MAP = new HashMap<AlgorithmType, JPanel>();
	public Map<JPanel, Integer> algorithmsPanelsOrder = new HashMap<JPanel, Integer>();
	JPopupMenu popupMenuInternal = new JPopupMenu();
	// private boolean afterInit = false;
	private ImageIcon questionMarkIcon = new ImageIcon();
	private static String LINE_SEPARATOR = System.getProperty("line.separator");
	private FormalLanguagesExampleGenerator generatorCore;
	private boolean validationOK;

	public CFGExampleGeneratorModule()
	{
		// new HelpNNDifficultyDialog()..setVisible(true);;
		// JOptionPane.showMessageDialog(this, "xxx"+LINE_SEPARATOR+"zzz","xx",1);
		settingsPane.setName("settingsPane"); // NOI18N
		settingsPane.setLayout(new java.awt.GridBagLayout());

		setupInputGrammarLayout();
		setupInputOutputLayout();
		setupGrammarTabbedPaneLayout();
		setupOutputGrammarLayout();

		// CollapsiblePanel cp= new CollapsiblePanel(algorithmsAndGramarTabbedPane);

		addEndPageFiller();

		settingsPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		spinnersSetup();

		algPropertiesCheck.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setViewAlgorithms(algPropertiesCheck.isSelected());
			}
		});

		disPopUpMenu.setName("disPopUpMenu"); // NOI18N
		disMenuItem.setName("disMenuItem"); // NOI18N
		disPopUpMenu.add(disMenuItem);

		ActionListener comboListener = new ComboBoxesActionListener();
		ioInputGramarCombo.addActionListener(comboListener);
		ioOutputGramarCombo.addActionListener(comboListener);
		ioOutputGramarCombo.setSelectedItem(AlgorithmType.GNF_CREATION_ALG);

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

		updateAlgTabPane();
		setAllGrammarsVisible(true);

		// setViewAlgorithms(false);

		updateStrings();
		for (JPanel pane : algorithmsPanes)
		{
			updateComponent(pane);
		}

		for (JPanel pane : gramarPanes)
		{
			updateComponent(pane);
		}

		// afterInit = true;
	}

	private void setTabbedPaneVisible()
	{
		if (!algPropertiesCheck.isSelected())
		{
			algorithmsAndGramarTabbedPane.setVisible(true);
			algorithmsAndGramarTabbedPane.putClientProperty(PANE_USED_CLIENT_PROPERTY, true);
		}
		else
		{
			algorithmsAndGramarTabbedPane.setVisible(true);
			algorithmsAndGramarTabbedPane.putClientProperty(PANE_USED_CLIENT_PROPERTY, true);
		}
	}

	private void addEndPageFiller()
	{
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 10;
		gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.anchor = GridBagConstraints.SOUTH;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		settingsPane.add(new JPanel(), gridBagConstraints);
	}

	public void setViewAlgorithms(boolean visible)
	{
		setTabbedPaneVisible();

		for (JPanel p : algorithmsPanes)
		{
			p.setVisible(visible);
			p.putClientProperty(PANE_USED_CLIENT_PROPERTY, visible);
		}
	}

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

	public void allGramarsVisible()
	{
		setAllGrammarsVisible(true);
	}

	public void onlyOutputGramar()
	{
		setAllGrammarsVisible(false);
	}

	private void initializeQuestionIcon()
	{
		// initialize question markIcon
		questionMarkIcon = new ImageIcon(getClass().getClassLoader().getResource("questionMark.jpg"));
		Image questionImage = questionMarkIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
		questionMarkIcon = new ImageIcon(questionImage);
	}

	public JButton createQuestionMarkButton()
	{
		final JButton b = new JButton(questionMarkIcon);
		final Border raisedBevelBorder = BorderFactory.createRaisedBevelBorder();
		final Insets insets = raisedBevelBorder.getBorderInsets(b);
		final EmptyBorder emptyBorder = new EmptyBorder(insets);
		b.setBorder(emptyBorder);
		b.setFocusPainted(false);
		b.setOpaque(false);
		b.setContentAreaFilled(false);
		b.setRolloverEnabled(true);
		b.setCursor(new Cursor(Cursor.HAND_CURSOR));
		setStandardJComponentSize(b, 25, 25);
		// b.setToolTipText("Help");
		b.getModel().addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				ButtonModel model = (ButtonModel) e.getSource();
				if (model.isRollover())
				{
					b.setBorder(raisedBevelBorder);

				}
				else
				{
					b.setBorder(emptyBorder);
				}
			}
		});
		return b;
	}

	private void setupGrammarTabbedPaneLayout()
	{
		initializeQuestionIcon();

		// JButton questionMarkButton = new JButton();
		// questionMarkButton.setIcon(questionMarkIcon);
		// setStandardJComponentSize(questionMarkButton, 30, 30);

		algorithmsAndGramarTabbedPane.setName("Minimum difficulty of simple rules elimination algorithm"); // NOI18N

		setupNongeneratingTabbedLayout();
		setupUnreachableTabbedLayout();
		setupEpsilonTabbedLayout();
		setupSimpleRuleTabbedLayout();
		setupLeftRecursionTabbedLayout();

		setupCNFTabbedLayout();
		setupGNFTabbedLayout();

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
		settingsPane.add(algorithmsAndGramarTabbedPane, gridBagConstraints);

	}

	private void setupUnreachableTabbedLayout()
	{
		unTabPane.setMaximumSize(new java.awt.Dimension(315, 221));
		unTabPane.setName("Unreachable"); // NOI18N
		unTabPane.setLayout(new java.awt.GridBagLayout());

		unGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("unGramarPaneInTab.border.title"))); // NOI18N
		unGramarPaneInTab.setName("unGramarPaneInTab"); // NOI18N

		unRulesNumberLabel.setText(resourceBundle.getString("unRulesNumberLabel.text")); // NOI18N
		unRulesNumberLabel.setMaximumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel.setMinimumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel.setName("unRulesNumberLabel"); // NOI18N
		unRulesNumberLabel.setPreferredSize(new java.awt.Dimension(185, 17));

		unGrMinimumLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMinimumLabel.setText(resourceBundle.getString("unGrMinimumLabel.text")); // NOI18N
		unGrMinimumLabel.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel.setName("unGrMinimumLabel"); // NOI18N
		unGrMinimumLabel.setPreferredSize(new java.awt.Dimension(80, 17));

		unLinesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		unLinesMaximumSpinner.setName("unLinesMaximumSpinner.name"); // NOI18N
		unLinesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		unLinesMaximumSpinner.setValue(7);

		unGrMaximumLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMaximumLabel.setText(resourceBundle.getString("unGrMaximumLabel.text")); // NOI18N
		unGrMaximumLabel.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel.setName("unGrMaximumLabel"); // NOI18N
		unGrMaximumLabel.setPreferredSize(new java.awt.Dimension(80, 17));

		unRulesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		unRulesMinimumSpinner.setMaximumSize(null);
		unRulesMinimumSpinner.setName("unRulesMinimumSpinner.name"); // NOI18N
		unRulesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		unRulesMinimumSpinner.setValue(5);

		unLinesNumberLabel.setText(resourceBundle.getString("unLinesNumberLabel.text")); // NOI18N
		unLinesNumberLabel.setMaximumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel.setMinimumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel.setName("unLinesNumberLabel"); // NOI18N
		unLinesNumberLabel.setPreferredSize(new java.awt.Dimension(185, 17));

		unRulesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		unRulesMaximumSpinner.setName("unRulesMaximumSpinner.name"); // NOI18N
		unRulesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		unRulesMaximumSpinner.setValue(14);

		unLinesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		unLinesMinimumSpinner.setMaximumSize(null);
		unLinesMinimumSpinner.setName("unLinesMinimumSpinner.name"); // NOI18N
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
		unAlMinimumLabel.setText(resourceBundle.getString("unAlMinimumLabel.text")); // NOI18N
		unAlMinimumLabel.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel.setName("unAlMinimumLabel"); // NOI18N
		unAlMinimumLabel.setPreferredSize(new java.awt.Dimension(80, 17));

		unAlMaximumLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMaximumLabel.setText(resourceBundle.getString("unAlMaximumLabel.text")); // NOI18N
		unAlMaximumLabel.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel.setName("unAlMaximumLabel"); // NOI18N
		unAlMaximumLabel.setPreferredSize(new java.awt.Dimension(80, 17));

		unMaximumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		unMaximumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		unMaximumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		unMaximumDifficultySpinner.setName("unMaximumDifficultySpinner.name"); // NOI18N
		unMaximumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		unMinimumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		unMinimumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		unMinimumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		unMinimumDifficultySpinner.setName("unMinimumDifficultySpinner.name"); // NOI18N
		unMinimumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		unAlgorithmDifficultyLabel.setText(resourceBundle.getString("unAlgorithmDifficultyLabel.text")); // NOI18N
		unAlgorithmDifficultyLabel.setMaximumSize(new java.awt.Dimension(120, 17));
		unAlgorithmDifficultyLabel.setMinimumSize(new java.awt.Dimension(120, 17));
		unAlgorithmDifficultyLabel.setName("unAlgorithmDifficultyLabel"); // NOI18N
		unAlgorithmDifficultyLabel.setPreferredSize(new java.awt.Dimension(185, 17));

		JButton unQuestionMarkButton = createQuestionMarkButton();
		unQuestionMarkButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				// JOptionPane.showMessageDialog(
				// CFGExampleGeneratorModule.this,
				// resourceBundle.getString("unDifficultyHelpStep1") + LINE_SEPARATOR
				// + resourceBundle.getString("unDifficultyHelpStep2"),
				// resourceBundle.getString("unDifficultyHelpDialog"), JOptionPane.PLAIN_MESSAGE);
				// TODO do for the others as well
				JLabel label = new JLabel();
				label.setText("<html>" + resourceBundle.getString("unDifficultyHelpStep1") + "<br>"
					+ resourceBundle.getString("unDifficultyHelpStep2") + "<br> <br>"
					+ resourceBundle.getString("difficultyHelpRules") + "</html>");
				label.setFont(Constants.DEFAULT_WIN_FONT);
				JOptionPane.showMessageDialog(CFGExampleGeneratorModule.this, label,
					resourceBundle.getString("unDifficultyHelpDialog"), JOptionPane.PLAIN_MESSAGE);

			}
		});

		javax.swing.GroupLayout unAlgorithmPaneInTabLayout = new javax.swing.GroupLayout(unAlgorithmPaneInTab);
		unAlgorithmPaneInTab.setLayout(unAlgorithmPaneInTabLayout);
		unAlgorithmPaneInTabLayout.setHorizontalGroup(unAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			unAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addGap(19)
				.addComponent(unQuestionMarkButton)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(unAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				// .addGap(33, 33, 33)
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
							javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(unQuestionMarkButton))
				.addContainerGap(15, Short.MAX_VALUE)));

		unAlgorithmPaneInTabLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {
			unMaximumDifficultySpinner, unMinimumDifficultySpinner });

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		unTabPane.add(unAlgorithmPaneInTab, gridBagConstraints);

		algorithmsAndGramarTabbedPane
			.addTab(resourceBundle.getString("Unreachable.TabConstraints.tabTitle"), unTabPane); // NOI18N

	}

	private void setupEpsilonTabbedLayout()
	{

		epTabPane.setMaximumSize(new java.awt.Dimension(315, 221));
		epTabPane.setName("epTabPane"); // NOI18N
		epTabPane.setLayout(new java.awt.GridBagLayout());

		epGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("epGramarPaneInTab.border.title"))); // NOI18N
		epGramarPaneInTab.setName("epGramarPaneInTab"); // NOI18N

		unRulesNumberLabel2.setText(resourceBundle.getString("unRulesNumberLabel2.text")); // NOI18N
		unRulesNumberLabel2.setMaximumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel2.setMinimumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel2.setName("unRulesNumberLabel2"); // NOI18N
		unRulesNumberLabel2.setPreferredSize(new java.awt.Dimension(185, 17));

		unGrMinimumLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMinimumLabel2.setText(resourceBundle.getString("unGrMinimumLabel2.text")); // NOI18N
		unGrMinimumLabel2.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel2.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel2.setName("unGrMinimumLabel2"); // NOI18N
		unGrMinimumLabel2.setPreferredSize(new java.awt.Dimension(80, 17));

		epLinesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		epLinesMaximumSpinner.setName("epLinesMaximumSpinner.name"); // NOI18N
		epLinesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		epLinesMaximumSpinner.setValue(8);

		unGrMaximumLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMaximumLabel2.setText(resourceBundle.getString("unGrMaximumLabel2.text")); // NOI18N
		unGrMaximumLabel2.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel2.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel2.setName("unGrMaximumLabel2"); // NOI18N
		unGrMaximumLabel2.setPreferredSize(new java.awt.Dimension(80, 17));

		epRulesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		epRulesMinimumSpinner.setMaximumSize(null);
		epRulesMinimumSpinner.setName("epRulesMinimumSpinner.name"); // NOI18N
		epRulesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		epRulesMinimumSpinner.setValue(5);

		unLinesNumberLabel2.setText(resourceBundle.getString("unLinesNumberLabel2.text")); // NOI18N
		unLinesNumberLabel2.setMaximumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel2.setMinimumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel2.setName("unLinesNumberLabel2"); // NOI18N
		unLinesNumberLabel2.setPreferredSize(new java.awt.Dimension(185, 17));

		epRulesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		epRulesMaximumSpinner.setName("epRulesMaximumSpinner.name"); // NOI18N
		epRulesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		epRulesMaximumSpinner.setValue(16);

		epLinesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		epLinesMinimumSpinner.setMaximumSize(null);
		epLinesMinimumSpinner.setName("epLinesMinimumSpinner.name"); // NOI18N
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
		unAlMinimumLabel2.setText(resourceBundle.getString("unAlMinimumLabel2.text")); // NOI18N
		unAlMinimumLabel2.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel2.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel2.setName("unAlMinimumLabel2"); // NOI18N
		unAlMinimumLabel2.setPreferredSize(new java.awt.Dimension(80, 17));

		unAlMaximumLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMaximumLabel2.setText(resourceBundle.getString("unAlMaximumLabel2.text")); // NOI18N
		unAlMaximumLabel2.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel2.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel2.setName("unAlMaximumLabel2"); // NOI18N
		unAlMaximumLabel2.setPreferredSize(new java.awt.Dimension(80, 17));

		epMaximumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		epMaximumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		epMaximumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		epMaximumDifficultySpinner.setName("epMaximumDifficultySpinner.name"); // NOI18N
		epMaximumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		epMinimumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		epMinimumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		epMinimumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		epMinimumDifficultySpinner.setName("epMinimumDifficultySpinner.name"); // NOI18N
		epMinimumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		epAlgorithmDifficultyLabel.setText(resourceBundle.getString("epAlgorithmDifficultyLabel.text")); // NOI18N
		epAlgorithmDifficultyLabel.setMaximumSize(new java.awt.Dimension(120, 17));
		epAlgorithmDifficultyLabel.setMinimumSize(new java.awt.Dimension(120, 17));
		epAlgorithmDifficultyLabel.setName("epAlgorithmDifficultyLabel"); // NOI18N
		epAlgorithmDifficultyLabel.setPreferredSize(new java.awt.Dimension(185, 17));

		JButton epQuestionMarkButton = createQuestionMarkButton();
		epQuestionMarkButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				JLabel label = new JLabel();
				label.setText("<html>" + resourceBundle.getString("epsDifficultyHelpBase") + "<br> <br>  "
					+ resourceBundle.getString("epsDifficultyHelpStep1") + ",<br>   "
					+ resourceBundle.getString("epsDifficultyHelpStep2") + ",<br>   "
					+ resourceBundle.getString("epsDifficultyHelpStep3") + ",<br>   "
					+ resourceBundle.getString("epsDifficultyHelpStep4") + ",<br>   "
					+ resourceBundle.getString("epsDifficultyHelpStep5") + ". <br> <br>"
					+ resourceBundle.getString("difficultyHelpRules") + "</html>");
				label.setFont(Constants.DEFAULT_WIN_FONT);
				JOptionPane.showMessageDialog(CFGExampleGeneratorModule.this, label,
					resourceBundle.getString("epsDifficultyHelpDialog"), JOptionPane.PLAIN_MESSAGE);

				// JOptionPane.showMessageDialog(
				// CFGExampleGeneratorModule.this,
				// resourceBundle.getString("epsDifficultyHelpBase") + LINE_SEPARATOR + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("epsDifficultyHelpStep1") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("epsDifficultyHelpStep2") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("epsDifficultyHelpStep3") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("epsDifficultyHelpStep4") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("epsDifficultyHelpStep5"),
				// resourceBundle.getString("epsDifficultyHelpDialog"), JOptionPane.PLAIN_MESSAGE);

			}
		});

		javax.swing.GroupLayout epAlgorithmPaneInTabLayout = new javax.swing.GroupLayout(epAlgorithmPaneInTab);
		epAlgorithmPaneInTab.setLayout(epAlgorithmPaneInTabLayout);
		epAlgorithmPaneInTabLayout.setHorizontalGroup(epAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			epAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addGap(19)
				.addComponent(epQuestionMarkButton)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(epAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				// .addGap(33, 33, 33)
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
							javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(epQuestionMarkButton))
				.addContainerGap(15, Short.MAX_VALUE)));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		epTabPane.add(epAlgorithmPaneInTab, gridBagConstraints);

		algorithmsAndGramarTabbedPane.addTab(resourceBundle.getString("epTabPane.TabConstraints.tabTitle"), epTabPane); // NOI18N

	}

	private void setupSimpleRuleTabbedLayout()
	{

		srTabPane.setMaximumSize(new java.awt.Dimension(315, 221));
		srTabPane.setName("srTabPane"); // NOI18N
		srTabPane.setLayout(new java.awt.GridBagLayout());

		srGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("srGramarPaneInTab.border.title"))); // NOI18N
		srGramarPaneInTab.setName("srGramarPaneInTab"); // NOI18N

		unRulesNumberLabel7.setText(resourceBundle.getString("unRulesNumberLabel7.text")); // NOI18N
		unRulesNumberLabel7.setMaximumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel7.setMinimumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel7.setName("unRulesNumberLabel7"); // NOI18N
		unRulesNumberLabel7.setPreferredSize(new java.awt.Dimension(185, 17));

		unGrMinimumLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMinimumLabel7.setText(resourceBundle.getString("unGrMinimumLabel7.text")); // NOI18N
		unGrMinimumLabel7.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel7.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel7.setName("unGrMinimumLabel7"); // NOI18N
		unGrMinimumLabel7.setPreferredSize(new java.awt.Dimension(80, 17));

		srLinesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		srLinesMaximumSpinner.setName("srLinesMaximumSpinner.name"); // NOI18N
		srLinesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		srLinesMaximumSpinner.setValue(8);

		unGrMaximumLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMaximumLabel7.setText(resourceBundle.getString("unGrMaximumLabel7.text")); // NOI18N
		unGrMaximumLabel7.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel7.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel7.setName("unGrMaximumLabel7"); // NOI18N
		unGrMaximumLabel7.setPreferredSize(new java.awt.Dimension(80, 17));

		srRulesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		srRulesMinimumSpinner.setMaximumSize(null);
		srRulesMinimumSpinner.setName("srRulesMinimumSpinner.name"); // NOI18N
		srRulesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		srRulesMinimumSpinner.setValue(9);

		unLinesNumberLabel7.setText(resourceBundle.getString("unLinesNumberLabel7.text")); // NOI18N
		unLinesNumberLabel7.setMaximumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel7.setMinimumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel7.setName("unLinesNumberLabel7"); // NOI18N
		unLinesNumberLabel7.setPreferredSize(new java.awt.Dimension(185, 17));

		srRulesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		srRulesMaximumSpinner.setName("srRulesMaximumSpinner.name"); // NOI18N
		srRulesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		srRulesMaximumSpinner.setValue(22);

		srLinesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		srLinesMinimumSpinner.setMaximumSize(null);
		srLinesMinimumSpinner.setName("srLinesMinimumSpinner.name"); // NOI18N
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
		unAlMinimumLabel7.setText(resourceBundle.getString("unAlMinimumLabel7.text")); // NOI18N
		unAlMinimumLabel7.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel7.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel7.setName("unAlMinimumLabel7"); // NOI18N
		unAlMinimumLabel7.setPreferredSize(new java.awt.Dimension(80, 17));

		unAlMaximumLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMaximumLabel7.setText(resourceBundle.getString("unAlMaximumLabel7.text")); // NOI18N
		unAlMaximumLabel7.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel7.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel7.setName("unAlMaximumLabel7"); // NOI18N
		unAlMaximumLabel7.setPreferredSize(new java.awt.Dimension(80, 17));

		srMaximumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		srMaximumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		srMaximumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		srMaximumDifficultySpinner.setName("srMaximumDifficultySpinner.name"); // NOI18N
		srMaximumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		srMinimumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		srMinimumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		srMinimumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		srMinimumDifficultySpinner.setName("srMinimumDifficultySpinner.name"); // NOI18N
		srMinimumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		srAlgorithmDifficultyLabel.setText(resourceBundle.getString("srAlgorithmDifficultyLabel.text")); // NOI18N
		srAlgorithmDifficultyLabel.setMaximumSize(new java.awt.Dimension(120, 17));
		srAlgorithmDifficultyLabel.setMinimumSize(new java.awt.Dimension(120, 17));
		srAlgorithmDifficultyLabel.setName("srAlgorithmDifficultyLabel"); // NOI18N
		srAlgorithmDifficultyLabel.setPreferredSize(new java.awt.Dimension(185, 17));

		JButton srQuestionMarkButton = createQuestionMarkButton();
		srQuestionMarkButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				JLabel label = new JLabel();
				label.setText("<html>" + resourceBundle.getString("srDifficultyHelpBase") + "<br> <br>   "
					+ resourceBundle.getString("srDifficultyHelpStep1") + "<br>   "
					+ resourceBundle.getString("srDifficultyHelpStep2") + "<br>   "
					+ resourceBundle.getString("srDifficultyHelpStep3") + "<br>   "
					+ resourceBundle.getString("srDifficultyHelpStep4") + "<br>   "
					+ resourceBundle.getString("srDifficultyHelpStep5") + "<br> <br>"
					+ resourceBundle.getString("srDifficultyHelpStepAppendix") + "<br> <br> "
					+ resourceBundle.getString("difficultyHelpRules") + "</html>");
				label.setFont(Constants.DEFAULT_WIN_FONT);
				JOptionPane.showMessageDialog(CFGExampleGeneratorModule.this, label,
					resourceBundle.getString("srDifficultyHelpDialog"), JOptionPane.PLAIN_MESSAGE);

				// JOptionPane.showMessageDialog(
				// CFGExampleGeneratorModule.this,
				// resourceBundle.getString("srDifficultyHelpBase") + LINE_SEPARATOR + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("srDifficultyHelpStep1") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("srDifficultyHelpStep2") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("srDifficultyHelpStep3") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("srDifficultyHelpStep4") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("srDifficultyHelpStep5") + LINE_SEPARATOR + LINE_SEPARATOR
				// + resourceBundle.getString("srDifficultyHelpStepAppendix"),
				// resourceBundle.getString("srDifficultyHelpDialog"), JOptionPane.PLAIN_MESSAGE);

			}
		});

		javax.swing.GroupLayout srAlgorithmPaneInTabLayout = new javax.swing.GroupLayout(srAlgorithmPaneInTab);
		srAlgorithmPaneInTab.setLayout(srAlgorithmPaneInTabLayout);
		srAlgorithmPaneInTabLayout.setHorizontalGroup(srAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			srAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addGap(19)
				.addComponent(srQuestionMarkButton)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(srAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				// .addGap(33, 33, 33)
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
							javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(srQuestionMarkButton))
				.addContainerGap(15, Short.MAX_VALUE)));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		srTabPane.add(srAlgorithmPaneInTab, gridBagConstraints);

		algorithmsAndGramarTabbedPane.addTab(resourceBundle.getString("srTabPane.TabConstraints.tabTitle"), srTabPane); // NOI18N

	}

	private void setupLeftRecursionTabbedLayout()
	{

		lrTabPane.setMaximumSize(new java.awt.Dimension(315, 221));
		lrTabPane.setName("lrTabPane"); // NOI18N
		lrTabPane.setLayout(new java.awt.GridBagLayout());

		lrGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("lrGramarPaneInTab.border.title"))); // NOI18N
		lrGramarPaneInTab.setName("lrGramarPaneInTab"); // NOI18N

		unRulesNumberLabel4.setText(resourceBundle.getString("unRulesNumberLabel4.text")); // NOI18N
		unRulesNumberLabel4.setMaximumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel4.setMinimumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel4.setName("unRulesNumberLabel4"); // NOI18N
		unRulesNumberLabel4.setPreferredSize(new java.awt.Dimension(185, 17));

		unGrMinimumLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMinimumLabel4.setText(resourceBundle.getString("unGrMinimumLabel4.text")); // NOI18N
		unGrMinimumLabel4.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel4.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel4.setName("unGrMinimumLabel4"); // NOI18N
		unGrMinimumLabel4.setPreferredSize(new java.awt.Dimension(80, 17));

		lrLinesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		lrLinesMaximumSpinner.setName("lrLinesMaximumSpinner.name"); // NOI18N
		lrLinesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		lrLinesMaximumSpinner.setValue(11);

		unGrMaximumLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMaximumLabel4.setText(resourceBundle.getString("unGrMaximumLabel4.text")); // NOI18N
		unGrMaximumLabel4.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel4.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel4.setName("unGrMaximumLabel4"); // NOI18N
		unGrMaximumLabel4.setPreferredSize(new java.awt.Dimension(80, 17));

		lrRulesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		lrRulesMinimumSpinner.setMaximumSize(null);
		lrRulesMinimumSpinner.setName("lrRulesMinimumSpinner.name"); // NOI18N
		lrRulesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		lrRulesMinimumSpinner.setValue(10);

		unLinesNumberLabel4.setText(resourceBundle.getString("unLinesNumberLabel4.text")); // NOI18N
		unLinesNumberLabel4.setMaximumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel4.setMinimumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel4.setName("unLinesNumberLabel4"); // NOI18N
		unLinesNumberLabel4.setPreferredSize(new java.awt.Dimension(185, 17));

		lrRulesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		lrRulesMaximumSpinner.setName("lrRulesMaximumSpinner.name"); // NOI18N
		lrRulesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		lrRulesMaximumSpinner.setValue(30);

		lrLinesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		lrLinesMinimumSpinner.setMaximumSize(null);
		lrLinesMinimumSpinner.setName("lrLinesMinimumSpinner.name"); // NOI18N
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
		unAlMinimumLabel4.setText(resourceBundle.getString("unAlMinimumLabel4.text")); // NOI18N
		unAlMinimumLabel4.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel4.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel4.setName("unAlMinimumLabel4"); // NOI18N
		unAlMinimumLabel4.setPreferredSize(new java.awt.Dimension(80, 17));

		unAlMaximumLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMaximumLabel4.setText(resourceBundle.getString("unAlMaximumLabel4.text")); // NOI18N
		unAlMaximumLabel4.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel4.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel4.setName("unAlMaximumLabel4"); // NOI18N
		unAlMaximumLabel4.setPreferredSize(new java.awt.Dimension(80, 17));

		lrMaximumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		lrMaximumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		lrMaximumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		lrMaximumDifficultySpinner.setName("lrMaximumDifficultySpinner.name"); // NOI18N
		lrMaximumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		lrMinimumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		lrMinimumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		lrMinimumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		lrMinimumDifficultySpinner.setName("lrMinimumDifficultySpinner.name"); // NOI18N
		lrMinimumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		lrAlgorithmDifficultyLabel.setText(resourceBundle.getString("lrAlgorithmDifficultyLabel.text")); // NOI18N
		lrAlgorithmDifficultyLabel.setMaximumSize(new java.awt.Dimension(120, 17));
		lrAlgorithmDifficultyLabel.setMinimumSize(new java.awt.Dimension(120, 17));
		lrAlgorithmDifficultyLabel.setName("lrAlgorithmDifficultyLabel"); // NOI18N
		lrAlgorithmDifficultyLabel.setPreferredSize(new java.awt.Dimension(185, 17));

		JButton lrQuestionMarkButton = createQuestionMarkButton();
		lrQuestionMarkButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{

				JLabel label = new JLabel();
				label.setText("<html>" + resourceBundle.getString("lrDifficultyHelpStep3Part1") + "<br>"
					+ resourceBundle.getString("lrDifficultyHelpStep4Part1") + "<br> <br>" + "   "
					+ resourceBundle.getString("lrDifficultyHelpStep1") + "<br>   "
					+ resourceBundle.getString("lrDifficultyHelpStep2") + "<br>   "
					+ resourceBundle.getString("lrDifficultyHelpStep3Part2") + "<br>   "
					+ resourceBundle.getString("lrDifficultyHelpStep4Part2") + "<br>   "
					+ resourceBundle.getString("lrDifficultyHelpStep5") + "<br> <br>"
					+ resourceBundle.getString("lrDifficultyHelpStepAppendix") + "<br> <br>"
					+ resourceBundle.getString("difficultyHelpRules") + "</html>");
				label.setFont(Constants.DEFAULT_WIN_FONT);
				JOptionPane.showMessageDialog(CFGExampleGeneratorModule.this, label,
					resourceBundle.getString("lrDifficultyHelpDialog"), JOptionPane.PLAIN_MESSAGE);

				// JOptionPane.showMessageDialog(
				// CFGExampleGeneratorModule.this,
				// resourceBundle.getString("lrDifficultyHelpStep3Part1") + LINE_SEPARATOR
				// + resourceBundle.getString("lrDifficultyHelpStep4Part1") + LINE_SEPARATOR + LINE_SEPARATOR
				// + "   " + resourceBundle.getString("lrDifficultyHelpStep1") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("lrDifficultyHelpStep2") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("lrDifficultyHelpStep3Part2") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("lrDifficultyHelpStep4Part2") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("lrDifficultyHelpStep5") + LINE_SEPARATOR + LINE_SEPARATOR
				// + resourceBundle.getString("lrDifficultyHelpStepAppendix"),
				// resourceBundle.getString("lrDifficultyHelpDialog"), JOptionPane.PLAIN_MESSAGE);

			}
		});

		javax.swing.GroupLayout lrAlgorithmPaneInTabLayout = new javax.swing.GroupLayout(lrAlgorithmPaneInTab);
		lrAlgorithmPaneInTab.setLayout(lrAlgorithmPaneInTabLayout);
		lrAlgorithmPaneInTabLayout.setHorizontalGroup(lrAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			lrAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addGap(19)
				.addComponent(lrQuestionMarkButton)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(lrAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				// .addGap(33, 33, 33)
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
							javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(lrQuestionMarkButton))
				.addContainerGap(15, Short.MAX_VALUE)));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		lrTabPane.add(lrAlgorithmPaneInTab, gridBagConstraints);

		algorithmsAndGramarTabbedPane.addTab(resourceBundle.getString("lrTabPane.TabConstraints.tabTitle"), lrTabPane); // NOI18N

	}

	private void setupCNFTabbedLayout()
	{
		cfTabPane.setMaximumSize(new java.awt.Dimension(315, 221));
		cfTabPane.setName("cfTabPane"); // NOI18N
		cfTabPane.setLayout(new java.awt.GridBagLayout());

		cfGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("cfGramarPaneInTab.border.title"))); // NOI18N
		cfGramarPaneInTab.setName("cfGramarPaneInTab"); // NOI18N

		unRulesNumberLabel5.setText(resourceBundle.getString("unRulesNumberLabel5.text")); // NOI18N
		unRulesNumberLabel5.setMaximumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel5.setMinimumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel5.setName("unRulesNumberLabel5"); // NOI18N
		unRulesNumberLabel5.setPreferredSize(new java.awt.Dimension(185, 17));

		unGrMinimumLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMinimumLabel5.setText(resourceBundle.getString("unGrMinimumLabel5.text")); // NOI18N
		unGrMinimumLabel5.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel5.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel5.setName("unGrMinimumLabel5"); // NOI18N
		unGrMinimumLabel5.setPreferredSize(new java.awt.Dimension(80, 17));

		cfLinesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		cfLinesMaximumSpinner.setName("cfLinesMaximumSpinner.name"); // NOI18N
		cfLinesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		cfLinesMaximumSpinner.setValue(18);

		unGrMaximumLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMaximumLabel5.setText(resourceBundle.getString("unGrMaximumLabel5.text")); // NOI18N
		unGrMaximumLabel5.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel5.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel5.setName("unGrMaximumLabel5"); // NOI18N
		unGrMaximumLabel5.setPreferredSize(new java.awt.Dimension(80, 17));

		cfRulesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		cfRulesMinimumSpinner.setMaximumSize(null);
		cfRulesMinimumSpinner.setName("cfRulesMinimumSpinner.name"); // NOI18N
		cfRulesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		cfRulesMinimumSpinner.setValue(10);

		unLinesNumberLabel5.setText(resourceBundle.getString("unLinesNumberLabel5.text")); // NOI18N
		unLinesNumberLabel5.setMaximumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel5.setMinimumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel5.setName("unLinesNumberLabel5"); // NOI18N
		unLinesNumberLabel5.setPreferredSize(new java.awt.Dimension(185, 17));

		cfRulesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		cfRulesMaximumSpinner.setName("cfRulesMaximumSpinner.name"); // NOI18N
		cfRulesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		cfRulesMaximumSpinner.setValue(26);

		cfLinesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		cfLinesMinimumSpinner.setMaximumSize(null);
		cfLinesMinimumSpinner.setName("cfLinesMinimumSpinner.name"); // NOI18N
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
		unAlMinimumLabel5.setText(resourceBundle.getString("unAlMinimumLabel5.text")); // NOI18N
		unAlMinimumLabel5.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel5.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel5.setName("unAlMinimumLabel5"); // NOI18N
		unAlMinimumLabel5.setPreferredSize(new java.awt.Dimension(80, 17));

		unAlMaximumLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMaximumLabel5.setText(resourceBundle.getString("unAlMaximumLabel5.text")); // NOI18N
		unAlMaximumLabel5.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel5.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel5.setName("unAlMaximumLabel5"); // NOI18N
		unAlMaximumLabel5.setPreferredSize(new java.awt.Dimension(80, 17));

		cfMaximumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		cfMaximumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		cfMaximumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		cfMaximumDifficultySpinner.setName("cfMaximumDifficultySpinner.name"); // NOI18N
		cfMaximumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		cfMinimumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		cfMinimumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		cfMinimumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		cfMinimumDifficultySpinner.setName("cfMinimumDifficultySpinner.name"); // NOI18N
		cfMinimumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		cfAlgorithmDifficultyLabel.setText(resourceBundle.getString("cfAlgorithmDifficultyLabel.text")); // NOI18N
		cfAlgorithmDifficultyLabel.setMaximumSize(new java.awt.Dimension(120, 17));
		cfAlgorithmDifficultyLabel.setMinimumSize(new java.awt.Dimension(120, 17));
		cfAlgorithmDifficultyLabel.setName("cfAlgorithmDifficultyLabel"); // NOI18N
		cfAlgorithmDifficultyLabel.setPreferredSize(new java.awt.Dimension(185, 17));

		JButton cfQuestionMarkButton = createQuestionMarkButton();
		cfQuestionMarkButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				JLabel label = new JLabel();
				label.setText("<html>" + resourceBundle.getString("cfDifficultyHelpBase") + "<br> <br>  "
					+ resourceBundle.getString("cfDifficultyHelpStep1") + "<br>   "
					+ resourceBundle.getString("cfDifficultyHelpStep2") + "<br>   "
					+ resourceBundle.getString("cfDifficultyHelpStep3") + "<br>   "
					+ resourceBundle.getString("cfDifficultyHelpStep4") + "<br>   "
					+ resourceBundle.getString("cfDifficultyHelpStep5") + "<br> <br>"
					+ resourceBundle.getString("cfDifficultyHelpStepAppendix") + "<br> <br>"
					+ resourceBundle.getString("difficultyHelpRules") + "</html>");
				label.setFont(Constants.DEFAULT_WIN_FONT);
				JOptionPane.showMessageDialog(CFGExampleGeneratorModule.this, label,
					resourceBundle.getString("cfDifficultyHelpDialog"), JOptionPane.PLAIN_MESSAGE);

				// JOptionPane.showMessageDialog(
				// CFGExampleGeneratorModule.this,
				// resourceBundle.getString("cfDifficultyHelpBase") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("cfDifficultyHelpStep1") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("cfDifficultyHelpStep2") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("cfDifficultyHelpStep3") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("cfDifficultyHelpStep4") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("cfDifficultyHelpStep5") + LINE_SEPARATOR
				// + resourceBundle.getString("cfDifficultyHelpStepAppendix"),
				// resourceBundle.getString("cfDifficultyHelpDialog"), JOptionPane.PLAIN_MESSAGE);

			}
		});

		javax.swing.GroupLayout cfAlgorithmPaneInTabLayout = new javax.swing.GroupLayout(cfAlgorithmPaneInTab);
		cfAlgorithmPaneInTab.setLayout(cfAlgorithmPaneInTabLayout);
		cfAlgorithmPaneInTabLayout.setHorizontalGroup(cfAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			cfAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addGap(19)
				.addComponent(cfQuestionMarkButton)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(cfAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				// .addGap(33, 33, 33)
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
							javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(cfQuestionMarkButton))
				.addContainerGap(15, Short.MAX_VALUE)));// 31,
		// Short.MAX_VALUE

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		cfTabPane.add(cfAlgorithmPaneInTab, gridBagConstraints);

		algorithmsAndGramarTabbedPane.addTab(resourceBundle.getString("cfTabPane.TabConstraints.tabTitle"), cfTabPane); // NOI18N

	}

	private void setupGNFTabbedLayout()
	{

		gfTabPane.setMaximumSize(new java.awt.Dimension(315, 221));
		gfTabPane.setName("gfTabPane"); // NOI18N
		gfTabPane.setLayout(new java.awt.GridBagLayout());

		gfGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("gfGramarPaneInTab.border.title"))); // NOI18N
		gfGramarPaneInTab.setName("gfGramarPaneInTab"); // NOI18N

		unRulesNumberLabel6.setText(resourceBundle.getString("unRulesNumberLabel6.text")); // NOI18N
		unRulesNumberLabel6.setMaximumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel6.setMinimumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel6.setName("unRulesNumberLabel6"); // NOI18N
		unRulesNumberLabel6.setPreferredSize(new java.awt.Dimension(185, 17));

		unGrMinimumLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMinimumLabel6.setText(resourceBundle.getString("unGrMinimumLabel6.text")); // NOI18N
		unGrMinimumLabel6.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel6.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel6.setName("unGrMinimumLabel6"); // NOI18N
		unGrMinimumLabel6.setPreferredSize(new java.awt.Dimension(80, 17));

		gfLinesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		gfLinesMaximumSpinner.setName("gfLinesMaximumSpinner.name"); // NOI18N
		gfLinesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		gfLinesMaximumSpinner.setValue(13);

		unGrMaximumLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMaximumLabel6.setText(resourceBundle.getString("unGrMaximumLabel6.text")); // NOI18N
		unGrMaximumLabel6.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel6.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel6.setName("unGrMaximumLabel6"); // NOI18N
		unGrMaximumLabel6.setPreferredSize(new java.awt.Dimension(80, 17));

		gfRulesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		gfRulesMinimumSpinner.setMaximumSize(null);
		gfRulesMinimumSpinner.setName("gfRulesMinimumSpinner.name"); // NOI18N
		gfRulesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		gfRulesMinimumSpinner.setValue(13);

		unLinesNumberLabel6.setText(resourceBundle.getString("unLinesNumberLabel6.text")); // NOI18N
		unLinesNumberLabel6.setMaximumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel6.setMinimumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel6.setName("unLinesNumberLabel6"); // NOI18N
		unLinesNumberLabel6.setPreferredSize(new java.awt.Dimension(185, 17));

		gfRulesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		gfRulesMaximumSpinner.setName("gfRulesMaximumSpinner.name"); // NOI18N
		gfRulesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		gfRulesMaximumSpinner.setValue(60);

		gfLinesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		gfLinesMinimumSpinner.setMaximumSize(null);
		gfLinesMinimumSpinner.setName("gfLinesMinimumSpinner.name"); // NOI18N
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
		unAlMinimumLabel6.setText(resourceBundle.getString("unAlMinimumLabel6.text")); // NOI18N
		unAlMinimumLabel6.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel6.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel6.setName("unAlMinimumLabel6"); // NOI18N
		unAlMinimumLabel6.setPreferredSize(new java.awt.Dimension(80, 17));

		unAlMaximumLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMaximumLabel6.setText(resourceBundle.getString("unAlMaximumLabel6.text")); // NOI18N
		unAlMaximumLabel6.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel6.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel6.setName("unAlMaximumLabel6"); // NOI18N
		unAlMaximumLabel6.setPreferredSize(new java.awt.Dimension(80, 17));

		gfMaximumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		gfMaximumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		gfMaximumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		gfMaximumDifficultySpinner.setName("gfMaximumDifficultySpinner.name"); // NOI18N
		gfMaximumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		gfMinimumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		gfMinimumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		gfMinimumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		gfMinimumDifficultySpinner.setName("gfMinimumDifficultySpinner.name"); // NOI18N
		gfMinimumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		gfAlgorithmDifficultyLabel.setText(resourceBundle.getString("gfAlgorithmDifficultyLabel.text")); // NOI18N
		gfAlgorithmDifficultyLabel.setMaximumSize(new java.awt.Dimension(120, 17));
		gfAlgorithmDifficultyLabel.setMinimumSize(new java.awt.Dimension(120, 17));
		gfAlgorithmDifficultyLabel.setName("gfAlgorithmDifficultyLabel"); // NOI18N
		gfAlgorithmDifficultyLabel.setPreferredSize(new java.awt.Dimension(185, 17));

		JButton gfQuestionMarkButton = createQuestionMarkButton();
		gfQuestionMarkButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				JLabel label = new JLabel();
				label.setText("<html>" + resourceBundle.getString("gfDifficultyHelpBase") + "<br> <br>   "
					+ resourceBundle.getString("gfDifficultyHelpStep1") + "<br>   "
					+ resourceBundle.getString("gfDifficultyHelpStep2") + "<br>   "
					+ resourceBundle.getString("gfDifficultyHelpStep3") + "<br>   "
					+ resourceBundle.getString("gfDifficultyHelpStep4") + "<br>   "
					+ resourceBundle.getString("gfDifficultyHelpStep5") + "<br> <br>"
					+ resourceBundle.getString("gfDifficultyHelpStepAppendix") + "<br> <br>"
					+ resourceBundle.getString("difficultyHelpRules") + "</html>");
				label.setFont(Constants.DEFAULT_WIN_FONT);
				JOptionPane.showMessageDialog(CFGExampleGeneratorModule.this, label,
					resourceBundle.getString("gfDifficultyHelpDialog"), JOptionPane.PLAIN_MESSAGE);

				// JOptionPane.showMessageDialog(
				// CFGExampleGeneratorModule.this,
				// resourceBundle.getString("gfDifficultyHelpBase") + LINE_SEPARATOR + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("gfDifficultyHelpStep1") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("gfDifficultyHelpStep2") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("gfDifficultyHelpStep3") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("gfDifficultyHelpStep4") + LINE_SEPARATOR + "   "
				// + resourceBundle.getString("gfDifficultyHelpStep5") + LINE_SEPARATOR + LINE_SEPARATOR
				// + resourceBundle.getString("gfDifficultyHelpStepAppendix"),
				// resourceBundle.getString("gfDifficultyHelpDialog"), JOptionPane.PLAIN_MESSAGE);

			}
		});

		javax.swing.GroupLayout gfAlgorithmPaneInTabLayout = new javax.swing.GroupLayout(gfAlgorithmPaneInTab);
		gfAlgorithmPaneInTab.setLayout(gfAlgorithmPaneInTabLayout);
		gfAlgorithmPaneInTabLayout.setHorizontalGroup(gfAlgorithmPaneInTabLayout.createParallelGroup(
			javax.swing.GroupLayout.Alignment.LEADING).addGroup(
			gfAlgorithmPaneInTabLayout
				.createSequentialGroup()
				.addGap(19)
				.addComponent(gfQuestionMarkButton)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(gfAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				// .addGap(33, 33, 33)
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
							javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(gfQuestionMarkButton))
				.addContainerGap(15, Short.MAX_VALUE)));

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gfTabPane.add(gfAlgorithmPaneInTab, gridBagConstraints);

		algorithmsAndGramarTabbedPane.addTab(resourceBundle.getString("gfTabPane.TabConstraints.tabTitle"), gfTabPane); // NOI18N

	}

	private void setupNongeneratingTabbedLayout()
	{
		nnTabPane.setMaximumSize(new java.awt.Dimension(315, 221));
		nnTabPane.setName("nnTabPane"); // NOI18N
		nnTabPane.setLayout(new java.awt.GridBagLayout());

		nnGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("nnGramarPaneInTab.border.title"))); // NOI18N
		nnGramarPaneInTab.setName("nnGramarPaneInTab"); // NOI18N

		unRulesNumberLabel1.setText(resourceBundle.getString("unRulesNumberLabel1.text")); // NOI18N
		unRulesNumberLabel1.setMaximumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel1.setMinimumSize(new java.awt.Dimension(120, 17));
		unRulesNumberLabel1.setName("unRulesNumberLabel1"); // NOI18N
		unRulesNumberLabel1.setPreferredSize(new java.awt.Dimension(185, 17));

		unGrMinimumLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMinimumLabel1.setText(resourceBundle.getString("unGrMinimumLabel1.text")); // NOI18N
		unGrMinimumLabel1.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel1.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMinimumLabel1.setName("unGrMinimumLabel1"); // NOI18N
		unGrMinimumLabel1.setPreferredSize(new java.awt.Dimension(80, 17));

		nnLinesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		nnLinesMaximumSpinner.setName("nnLinesMaximumSpinner.name"); // NOI18N
		nnLinesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		nnLinesMaximumSpinner.setValue(8);

		unGrMaximumLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unGrMaximumLabel1.setText(resourceBundle.getString("unGrMaximumLabel1.text")); // NOI18N
		unGrMaximumLabel1.setMaximumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel1.setMinimumSize(new java.awt.Dimension(80, 17));
		unGrMaximumLabel1.setName("unGrMaximumLabel1"); // NOI18N
		unGrMaximumLabel1.setPreferredSize(new java.awt.Dimension(80, 17));

		nnRulesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		nnRulesMinimumSpinner.setMaximumSize(null);
		nnRulesMinimumSpinner.setName("nnRulesMinimumSpinner.name"); // NOI18N
		nnRulesMinimumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		nnRulesMinimumSpinner.setValue(4);

		unLinesNumberLabel1.setText(resourceBundle.getString("unLinesNumberLabel1.text")); // NOI18N
		unLinesNumberLabel1.setMaximumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel1.setMinimumSize(new java.awt.Dimension(120, 17));
		unLinesNumberLabel1.setName("unLinesNumberLabel1"); // NOI18N
		unLinesNumberLabel1.setPreferredSize(new java.awt.Dimension(185, 17));

		nnRulesMaximumSpinner.setComponentPopupMenu(disPopUpMenu);
		nnRulesMaximumSpinner.setName("nnRulesMaximumSpinner.name"); // NOI18N
		nnRulesMaximumSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		nnRulesMaximumSpinner.setValue(16);

		nnLinesMinimumSpinner.setComponentPopupMenu(disPopUpMenu);
		nnLinesMinimumSpinner.setMaximumSize(null);
		nnLinesMinimumSpinner.setName("nnLinesMinimumSpinner.name"); // NOI18N
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
		unAlMinimumLabel1.setText(resourceBundle.getString("unAlMinimumLabel1.text")); // NOI18N
		unAlMinimumLabel1.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel1.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMinimumLabel1.setName("unAlMinimumLabel1"); // NOI18N
		unAlMinimumLabel1.setPreferredSize(new java.awt.Dimension(80, 17));

		unAlMaximumLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		unAlMaximumLabel1.setText(resourceBundle.getString("unAlMaximumLabel1.text")); // NOI18N
		unAlMaximumLabel1.setMaximumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel1.setMinimumSize(new java.awt.Dimension(80, 17));
		unAlMaximumLabel1.setName("unAlMaximumLabel1"); // NOI18N
		unAlMaximumLabel1.setPreferredSize(new java.awt.Dimension(80, 17));

		nnMaximumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		nnMaximumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		nnMaximumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		nnMaximumDifficultySpinner.setName("nnMaximumDifficultySpinner.name"); // NOI18N
		nnMaximumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		nnMinimumDifficultySpinner.setComponentPopupMenu(disPopUpMenu);
		nnMinimumDifficultySpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		nnMinimumDifficultySpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		nnMinimumDifficultySpinner.setName("nnMinimumDifficultySpinner.name"); // NOI18N
		nnMinimumDifficultySpinner.setPreferredSize(new java.awt.Dimension(80, 25));

		JButton nnQuestionMarkButton = createQuestionMarkButton();
		nnQuestionMarkButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				JLabel label = new JLabel();
				label.setText("<html>" + resourceBundle.getString("nnDifficultyHelpStep1") + "<br>"
					+ resourceBundle.getString("nnDifficultyHelpStep2") + "<br> <br> "
					+ resourceBundle.getString("difficultyHelpRules") + "</html>");
				label.setFont(Constants.DEFAULT_WIN_FONT);
				JOptionPane.showMessageDialog(CFGExampleGeneratorModule.this, label,
					resourceBundle.getString("nnDifficultyHelpDialog"), JOptionPane.PLAIN_MESSAGE);

				// JOptionPane.showMessageDialog(
				// CFGExampleGeneratorModule.this,
				// resourceBundle.getString("nnDifficultyHelpStep1") + LINE_SEPARATOR
				// + resourceBundle.getString("nnDifficultyHelpStep2"),
				// resourceBundle.getString("nnDifficultyHelpDialog"), JOptionPane.PLAIN_MESSAGE);

			}
		});

		nnAlgorithmDifficultyLabel.setText(resourceBundle.getString("nnAlgorithmDifficultyLabel.text")); // NOI18N
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
				.addGap(19)
				.addComponent(nnQuestionMarkButton)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(nnAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				// .addGap(20, 20, 20)
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
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(nnMinimumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(nnMaximumDifficultySpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
							javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(nnAlgorithmDifficultyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
							javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(nnQuestionMarkButton))
				.addContainerGap(15, Short.MAX_VALUE)));//

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;

		nnTabPane.add(nnAlgorithmPaneInTab, gridBagConstraints);

		algorithmsAndGramarTabbedPane.addTab(resourceBundle.getString("nnTabPane.TabConstraints.tabTitle"), nnTabPane); // NOI18N

	}

	private void setupOutputGrammarLayout()
	{

		outputGramarPane.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("outputGramarPane.border.title"))); // NOI18N
		outputGramarPane.setName("outputGramarPane"); // NOI18N

		ogRulesNumberLabel.setText(resourceBundle.getString("ogRulesNumberLabel.text")); // NOI18N
		ogRulesNumberLabel.setMaximumSize(new java.awt.Dimension(78, 14));
		ogRulesNumberLabel.setMinimumSize(new java.awt.Dimension(78, 14));
		ogRulesNumberLabel.setName("ogRulesNumberLabel"); // NOI18N
		ogRulesNumberLabel.setPreferredSize(new java.awt.Dimension(78, 14));

		ogMinLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		ogMinLabel.setText(resourceBundle.getString("ogMinLabel.text")); // NOI18N
		ogMinLabel.setName("ogMinLabel"); // NOI18N

		ogMinRulesSpinner.setComponentPopupMenu(disPopUpMenu);
		ogMinRulesSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		ogMinRulesSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		ogMinRulesSpinner.setName("ogMinRulesSpinner.name"); // NOI18N
		ogMinRulesSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		ogMinRulesSpinner.setValue(5);

		ogMaxLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		ogMaxLabel.setText(resourceBundle.getString("ogMaxLabel.text")); // NOI18N
		ogMaxLabel.setMaximumSize(new java.awt.Dimension(40, 14));
		ogMaxLabel.setMinimumSize(new java.awt.Dimension(40, 14));
		ogMaxLabel.setName("ogMaxLabel"); // NOI18N
		ogMaxLabel.setPreferredSize(new java.awt.Dimension(40, 14));

		ogMinLinesSpinner.setComponentPopupMenu(disPopUpMenu);
		ogMinLinesSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		ogMinLinesSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		ogMinLinesSpinner.setName("ogMinLinesSpinner.name"); // NOI18N
		ogMinLinesSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		ogMinLinesSpinner.setValue(3);

		ogLinesNumberLabel.setText(resourceBundle.getString("ogLinesNumberLabel.text")); // NOI18N
		ogLinesNumberLabel.setName("ogLinesNumberLabel"); // NOI18N

		ogMaxLinesSpinner.setComponentPopupMenu(disPopUpMenu);
		ogMaxLinesSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		ogMaxLinesSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		ogMaxLinesSpinner.setName("ogMaxLinesSpinner.name"); // NOI18N
		ogMaxLinesSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		ogMaxLinesSpinner.setValue(12);

		ogMaxRulesSpinner.setComponentPopupMenu(disPopUpMenu);
		ogMaxRulesSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		ogMaxRulesSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		ogMaxRulesSpinner.setName("ogMaxRulesSpinner.name"); // NOI18N
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

		// gridBagConstraints = new java.awt.GridBagConstraints();
		// gridBagConstraints.gridx = 0;
		// gridBagConstraints.gridy = 3;
		// gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		// gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
		// settingsPane.add(outputGramarPane, gridBagConstraints);

	}

	private void setupInputOutputLayout()
	{

		InputAndOutputPane.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("InputAndOutputPane.border.title"))); // NOI18N
		InputAndOutputPane.setName("InputAndOutputPane"); // NOI18N

		// InputAndOutputPane.setPreferredSize(new java.awt.Dimension(430, 132));

		ioInputGramarLabel.setText(resourceBundle.getString("ioInputGramarLabel.text")); // NOI18N
		ioInputGramarLabel.setName("ioInputGramarLabel"); // NOI18N

		ioOutputGramarLabel.setText(resourceBundle.getString("ioOutputGramarLabel.text")); // NOI18N
		ioOutputGramarLabel.setName("ioOutputGramarLabel"); // NOI18N

		ioExampleNumberLabel.setText(resourceBundle.getString("ioExampleNumberLabel.text")); // NOI18N
		ioExampleNumberLabel.setName("ioExampleNumberLabel"); // NOI18N

		ioInputGramarCombo.setModel(inputModel);
		ioInputGramarCombo.setMaximumSize(new java.awt.Dimension(114, 18));
		ioInputGramarCombo.setName("ioInputGramarCombo"); // NOI18N

		setStandardJComponentSize(ioInputGramarCombo, 230, 25);
		setStandardJComponentSize(ioOutputGramarCombo, 230, 25);

		ioOutputGramarCombo.setModel(outputModel);
		ioOutputGramarCombo.setName("ioOutputGramarCombo"); // NOI18N

		ioDisplayLabel.setText(resourceBundle.getString("ioDisplayLabel.text")); // NOI18N
		ioDisplayLabel.setName("ioDisplayLabel"); // NOI18N

		ioDifficultyParamsLabel.setText(resourceBundle.getString("viewAlgorithmsSettingsCheck.text")); // NOI18N
		ioDifficultyParamsLabel.setName("ioDifficultyParamsLabel"); // NOI18N

		algPropertiesCheck.setHorizontalAlignment(SwingConstants.LEADING);
		JPanel algPropWrapper = new JPanel();
		algPropWrapper.setLayout(new GridLayout());
		setStandardJComponentSize(algPropWrapper, 230, 25);
		algPropWrapper.add(algPropertiesCheck);

		javax.swing.GroupLayout InputAndOutputPaneLayout = new javax.swing.GroupLayout(InputAndOutputPane);
		InputAndOutputPane.setLayout(InputAndOutputPaneLayout);

		InputAndOutputPaneLayout
			.setHorizontalGroup(InputAndOutputPaneLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(
					Alignment.TRAILING,
					InputAndOutputPaneLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
							InputAndOutputPaneLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(ioInputGramarLabel, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
								.addComponent(ioOutputGramarLabel, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
							InputAndOutputPaneLayout
								.createParallelGroup(Alignment.LEADING, false)
								.addGroup(
									InputAndOutputPaneLayout.createSequentialGroup().addComponent(ioInputGramarCombo,
										GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE))
								.addGroup(
									InputAndOutputPaneLayout.createSequentialGroup().addComponent(ioOutputGramarCombo,
										GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))).addContainerGap()));
		InputAndOutputPaneLayout
			.setVerticalGroup(InputAndOutputPaneLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(
					InputAndOutputPaneLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
							InputAndOutputPaneLayout
								.createParallelGroup(Alignment.CENTER)
								.addComponent(ioInputGramarLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
									GroupLayout.PREFERRED_SIZE)
								.addComponent(ioInputGramarCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
									GroupLayout.PREFERRED_SIZE))
						.addGap(6, 6, 6)
						.addGroup(
							InputAndOutputPaneLayout
								.createParallelGroup(Alignment.CENTER)
								.addComponent(ioOutputGramarLabel, GroupLayout.PREFERRED_SIZE,
									GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(ioOutputGramarCombo, GroupLayout.PREFERRED_SIZE,
									GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		// ioExampleNumberLabel, ioExampleNumberSpinner
		// InputAndOutputPaneLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {
		// ioInputGramarCombo, ioInputGramarLabel, ioOutputGramarCombo, ioOutputGramarLabel });

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.ipady = 2;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
		settingsPane.add(InputAndOutputPane, gridBagConstraints);

	}

	private void setupInputGrammarLayout()
	{
		InputGramarPane.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("InputGramarPane.border.title"))); // NOI18N
		InputGramarPane.setName("InputGramarPane"); // NOI18N

		igMaxLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		igMaxLabel.setText(resourceBundle.getString("igMaxLabel.text")); // NOI18N
		igMaxLabel.setMaximumSize(new java.awt.Dimension(40, 14));
		igMaxLabel.setMinimumSize(new java.awt.Dimension(40, 14));
		igMaxLabel.setName("igMaxLabel"); // NOI18N
		igMaxLabel.setPreferredSize(new java.awt.Dimension(40, 14));

		igMinLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		igMinLabel.setText(resourceBundle.getString("igMinLabel.text")); // NOI18N
		igMinLabel.setName("igMinLabel"); // NOI18N

		igRulesNumberLabel.setText(resourceBundle.getString("igRulesNumberLabel.text")); // NOI18N
		igRulesNumberLabel.setMaximumSize(new java.awt.Dimension(78, 14));
		igRulesNumberLabel.setMinimumSize(new java.awt.Dimension(78, 14));
		igRulesNumberLabel.setName("igRulesNumberLabel"); // NOI18N
		igRulesNumberLabel.setPreferredSize(new java.awt.Dimension(78, 14));

		igMinRulesNumberSpinner.setComponentPopupMenu(disPopUpMenu);
		igMinRulesNumberSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		igMinRulesNumberSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		igMinRulesNumberSpinner.setName("igMinRulesNumberSpinner.name"); // NOI18N
		igMinRulesNumberSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		igMinRulesNumberSpinner.setValue(8);

		igMaxRulesNumberSpinner.setComponentPopupMenu(disPopUpMenu);
		igMaxRulesNumberSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		igMaxRulesNumberSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		igMaxRulesNumberSpinner.setName("igMaxRulesNumberSpinner.name"); // NOI18N
		igMaxRulesNumberSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		igMaxRulesNumberSpinner.setValue(11);

		igLinesNumberLabel.setText(resourceBundle.getString("igLinesNumberLabel.text")); // NOI18N
		igLinesNumberLabel.setName("igLinesNumberLabel"); // NOI18N

		igTerminalsNumberLabel.setText(resourceBundle.getString("igTerminalsNumberLabel.text")); // NOI18N
		igTerminalsNumberLabel.setName("igTerminalsNumberLabel"); // NOI18N

		igMaxRuleLengthLabel.setText(resourceBundle.getString("igMaxRuleLengthLabel.text")); // NOI18N
		igMaxRuleLengthLabel.setMaximumSize(new java.awt.Dimension(78, 14));
		igMaxRuleLengthLabel.setMinimumSize(new java.awt.Dimension(78, 14));
		igMaxRuleLengthLabel.setName("igMaxRuleLengthLabel"); // NOI18N
		igMaxRuleLengthLabel.setPreferredSize(new java.awt.Dimension(78, 14));

		igMinLinesNumberSpinner.setComponentPopupMenu(disPopUpMenu);
		igMinLinesNumberSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		igMinLinesNumberSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		igMinLinesNumberSpinner.setName("igMinLinesNumberSpinner.name"); // NOI18N
		igMinLinesNumberSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		igMinLinesNumberSpinner.setValue(4);

		igMaxLinesNumberSpinner.setComponentPopupMenu(disPopUpMenu);
		igMaxLinesNumberSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		igMaxLinesNumberSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		igMaxLinesNumberSpinner.setName("igMaxLinesNumberSpinner.name"); // NOI18N
		igMaxLinesNumberSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		igMaxLinesNumberSpinner.setValue(6);

		igMinTerminalNumberSpinner.setComponentPopupMenu(disPopUpMenu);
		igMinTerminalNumberSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		igMinTerminalNumberSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		igMinTerminalNumberSpinner.setName("igMinTerminalNumberSpinner.name"); // NOI18N
		igMinTerminalNumberSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		igMinTerminalNumberSpinner.setValue(2);

		igMaxTerminalNumberSpinner.setComponentPopupMenu(disPopUpMenu);
		igMaxTerminalNumberSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		igMaxTerminalNumberSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		igMaxTerminalNumberSpinner.setName("igMaxTerminalNumberSpinner.name"); // NOI18N
		igMaxTerminalNumberSpinner.setPreferredSize(new java.awt.Dimension(80, 25));
		igMaxTerminalNumberSpinner.setValue(4);

		igMaxRuleLengthSpinner.setMaximumSize(new java.awt.Dimension(80, 25));
		igMaxRuleLengthSpinner.setMinimumSize(new java.awt.Dimension(80, 25));
		igMaxRuleLengthSpinner.setName("igMaxRuleLengthSpinner.name"); // NOI18N
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
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
		settingsPane.add(InputGramarPane, gridBagConstraints);

	}

	private void updateComponent(JComponent c)
	{

		c.setFont(Constants.DEFAULT_WIN_FONT);
		for (Component comp : c.getComponents())
		{

			if (comp instanceof JComponent)
			{
				updateComponent((JComponent) comp);
			}
		}

	}

	private void spinnersSetup()
	{
		colorContr = new ColorController(ioExampleNumberSpinner);

		spinnersSup = new SpinnersSupport(disPopUpMenu, disMenuItem, colorContr);
		for (JSpinner s : allSpinners)
		{
			spinnersSup.registerSpinner(s);
			s.setToolTipText(s.getName());
			// set alignment
			NumberEditor editor = (NumberEditor) s.getEditor();
			editor.getTextField().setHorizontalAlignment(JTextField.CENTER);
			s.putClientProperty(SPINNER_DISABLED_BY_RADIO, !s.isEnabled());
		}

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

		criteriaChecker = new CriteriaConstraintsChecker(messagesLabel, new javax.swing.Action[] {}, spinnersSup,
			colorContr);
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

	}

	// private javax.swing.JLabel messagesLabel = new JLabel();

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

			algorithmsAndGramarTabbedPane.addTab(
				resourceBundle.getString(toAdd.getName() + ".TabConstraints.tabTitle"), toAdd);
		}
		int tabCount = algorithmsAndGramarTabbedPane.getTabCount();
		algorithmsAndGramarTabbedPane.setSelectedIndex(tabCount - 1);
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
				ResourceBundle.getBundle("CFGExampleGeneratorView").getString(
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

	private GridBagConstraints gridBagConstraints;
	private JPanel settingsPane = new JPanel();
	private JPanel InputGramarPane = new JPanel();
	private JLabel igMaxLabel = new JLabel();
	private JLabel igMinLabel = new JLabel();
	private JLabel igRulesNumberLabel = new JLabel();
	private JSpinner igMinRulesNumberSpinner = new JSpinner();
	private JSpinner igMaxRulesNumberSpinner = new JSpinner();
	private JLabel igLinesNumberLabel = new JLabel();
	private JLabel igTerminalsNumberLabel = new JLabel();
	private JLabel igMaxRuleLengthLabel = new JLabel();
	private JSpinner igMinLinesNumberSpinner = new JSpinner();
	private JSpinner igMaxLinesNumberSpinner = new JSpinner();
	private JSpinner igMinTerminalNumberSpinner = new JSpinner();
	private JSpinner igMaxTerminalNumberSpinner = new JSpinner();
	private JSpinner igMaxRuleLengthSpinner = new JSpinner();
	private JPanel InputAndOutputPane = new JPanel();
	private JLabel ioInputGramarLabel = new JLabel();
	private JLabel ioOutputGramarLabel = new JLabel();
	private JLabel ioExampleNumberLabel = new JLabel();
	private JLabel ioDisplayLabel = new JLabel();
	private JLabel ioDifficultyParamsLabel = new JLabel();
	private SpinnersSupport spinnersSup;
	private JMenuItem disMenuItem = new javax.swing.JMenuItem();

	private JComboBox ioInputGramarCombo = new JComboBox();
	private JComboBox ioOutputGramarCombo = new JComboBox();
	private JSpinner ioExampleNumberSpinner = new JSpinner();
	private JTabbedPane algorithmsAndGramarTabbedPane = new JTabbedPane();
	private JPanel nnTabPane = new JPanel();
	private JPanel nnGramarPaneInTab = new JPanel();
	private JLabel unRulesNumberLabel1 = new JLabel();
	private JLabel unGrMinimumLabel1 = new JLabel();
	private JSpinner nnLinesMaximumSpinner = new JSpinner();
	private JLabel unGrMaximumLabel1 = new JLabel();
	private JSpinner nnRulesMinimumSpinner = new JSpinner();
	private JLabel unLinesNumberLabel1 = new JLabel();
	private JSpinner nnRulesMaximumSpinner = new JSpinner();
	private JSpinner nnLinesMinimumSpinner = new JSpinner();
	private JPanel nnAlgorithmPaneInTab = new JPanel();
	private JLabel unAlMinimumLabel1 = new JLabel();
	private JLabel unAlMaximumLabel1 = new JLabel();
	private JSpinner nnMaximumDifficultySpinner = new JSpinner(new SpinnerNumberModel(2, 0, 2, 1));
	private JSpinner nnMinimumDifficultySpinner = new JSpinner(new SpinnerNumberModel(1, 0, 2, 1));
	private ColorController colorContr;
	private JLabel nnAlgorithmDifficultyLabel = new JLabel();
	private JPanel unTabPane = new JPanel();
	private JPanel unGramarPaneInTab = new JPanel();
	private JLabel unRulesNumberLabel = new JLabel();
	private JLabel unGrMinimumLabel = new JLabel();
	private JSpinner unLinesMaximumSpinner = new JSpinner();
	private JLabel unGrMaximumLabel = new JLabel();
	private JSpinner unRulesMinimumSpinner = new JSpinner();
	private JLabel unLinesNumberLabel = new JLabel();
	private JSpinner unRulesMaximumSpinner = new JSpinner();
	private JSpinner unLinesMinimumSpinner = new JSpinner();
	private JPanel unAlgorithmPaneInTab = new JPanel();
	private JLabel unAlMinimumLabel = new JLabel();
	private JLabel unAlMaximumLabel = new JLabel();
	private JSpinner unMaximumDifficultySpinner = new JSpinner(new SpinnerNumberModel(2, 0, 2, 1));
	private JSpinner unMinimumDifficultySpinner = new JSpinner(new SpinnerNumberModel(1, 0, 2, 1));
	private JLabel unAlgorithmDifficultyLabel = new JLabel();
	private JPanel epTabPane = new JPanel();
	private JPanel epGramarPaneInTab = new JPanel();
	private JLabel unRulesNumberLabel2 = new JLabel();
	private JLabel unGrMinimumLabel2 = new JLabel();
	private JSpinner epLinesMaximumSpinner = new JSpinner();
	private JLabel unGrMaximumLabel2 = new JLabel();
	private JSpinner epRulesMinimumSpinner = new JSpinner();
	private JLabel unLinesNumberLabel2 = new JLabel();
	private JSpinner epRulesMaximumSpinner = new JSpinner();
	private JSpinner epLinesMinimumSpinner = new JSpinner();
	private JPanel epAlgorithmPaneInTab = new JPanel();
	private JLabel unAlMinimumLabel2 = new JLabel();
	private JLabel unAlMaximumLabel2 = new JLabel();
	private JSpinner epMaximumDifficultySpinner = new JSpinner(new SpinnerNumberModel(5, 0, 5, 1));
	private JSpinner epMinimumDifficultySpinner = new JSpinner(new SpinnerNumberModel(1, 0, 5, 1));
	private JLabel epAlgorithmDifficultyLabel = new JLabel();
	private JPanel srTabPane = new JPanel();
	private JPanel srGramarPaneInTab = new JPanel();
	private JLabel unRulesNumberLabel7 = new JLabel();
	private JLabel unGrMinimumLabel7 = new JLabel();
	private JSpinner srLinesMaximumSpinner = new JSpinner();
	private JLabel unGrMaximumLabel7 = new JLabel();
	private JSpinner srRulesMinimumSpinner = new JSpinner();
	private JLabel unLinesNumberLabel7 = new JLabel();
	private JSpinner srRulesMaximumSpinner = new JSpinner();
	private JSpinner srLinesMinimumSpinner = new JSpinner();
	private JPanel srAlgorithmPaneInTab = new JPanel();
	private JLabel unAlMinimumLabel7 = new JLabel();
	private JLabel unAlMaximumLabel7 = new JLabel();
	private JSpinner srMaximumDifficultySpinner = new JSpinner(new SpinnerNumberModel(5, 0, 5, 1));
	private JSpinner srMinimumDifficultySpinner = new JSpinner(new SpinnerNumberModel(1, 0, 5, 1));
	private JLabel srAlgorithmDifficultyLabel = new JLabel();
	private JPanel lrTabPane = new JPanel();
	private JPanel lrGramarPaneInTab = new JPanel();
	private JLabel unRulesNumberLabel4 = new JLabel();
	private JLabel unGrMinimumLabel4 = new JLabel();
	private JSpinner lrLinesMaximumSpinner = new JSpinner();
	private JLabel unGrMaximumLabel4 = new JLabel();
	private JSpinner lrRulesMinimumSpinner = new JSpinner();
	private JLabel unLinesNumberLabel4 = new JLabel();
	private JSpinner lrRulesMaximumSpinner = new JSpinner();
	private JSpinner lrLinesMinimumSpinner = new JSpinner();
	private JPanel lrAlgorithmPaneInTab = new JPanel();
	private JLabel unAlMinimumLabel4 = new JLabel();
	private JLabel unAlMaximumLabel4 = new JLabel();
	private JSpinner lrMaximumDifficultySpinner = new JSpinner(new SpinnerNumberModel(4, 0, 5, 1));
	private JSpinner lrMinimumDifficultySpinner = new JSpinner(new SpinnerNumberModel(1, 0, 5, 1));
	private JLabel lrAlgorithmDifficultyLabel = new JLabel();
	private JPanel cfTabPane = new JPanel();
	private JPanel cfGramarPaneInTab = new JPanel();
	private JLabel unRulesNumberLabel5 = new JLabel();
	private JLabel unGrMinimumLabel5 = new JLabel();
	private JSpinner cfLinesMaximumSpinner = new JSpinner();
	private JLabel unGrMaximumLabel5 = new JLabel();
	private JSpinner cfRulesMinimumSpinner = new JSpinner();
	private JLabel unLinesNumberLabel5 = new JLabel();
	private JSpinner cfRulesMaximumSpinner = new JSpinner();
	private JSpinner cfLinesMinimumSpinner = new JSpinner();
	private JPanel cfAlgorithmPaneInTab = new JPanel();
	private JLabel unAlMinimumLabel5 = new JLabel();
	private JLabel unAlMaximumLabel5 = new JLabel();
	private JSpinner cfMaximumDifficultySpinner = new JSpinner(new SpinnerNumberModel(5, 0, 5, 1));
	private JSpinner cfMinimumDifficultySpinner = new JSpinner(new SpinnerNumberModel(1, 0, 5, 1));
	private JLabel cfAlgorithmDifficultyLabel = new JLabel();
	private JPanel gfTabPane = new JPanel();
	private JPanel gfGramarPaneInTab = new JPanel();
	private JLabel unRulesNumberLabel6 = new JLabel();
	private JLabel unGrMinimumLabel6 = new JLabel();
	private JSpinner gfLinesMaximumSpinner = new JSpinner();
	private JLabel unGrMaximumLabel6 = new JLabel();
	private JSpinner gfRulesMinimumSpinner = new JSpinner();
	private JLabel unLinesNumberLabel6 = new JLabel();
	private JSpinner gfRulesMaximumSpinner = new JSpinner();
	private JSpinner gfLinesMinimumSpinner = new JSpinner();
	private JPanel gfAlgorithmPaneInTab = new JPanel();
	private JLabel unAlMinimumLabel6 = new JLabel();
	private JLabel unAlMaximumLabel6 = new JLabel();
	private JSpinner gfMaximumDifficultySpinner = new JSpinner(new SpinnerNumberModel(4, 0, 5, 1));
	private JSpinner gfMinimumDifficultySpinner = new JSpinner(new SpinnerNumberModel(1, 0, 5, 1));
	private JLabel gfAlgorithmDifficultyLabel = new JLabel();
	private JPanel outputGramarPane = new JPanel();
	private JLabel ogRulesNumberLabel = new JLabel();
	private JLabel ogMinLabel = new JLabel();
	private JSpinner ogMinRulesSpinner = new JSpinner();
	private JLabel ogMaxLabel = new JLabel();
	private JSpinner ogMinLinesSpinner = new JSpinner();
	private JLabel ogLinesNumberLabel = new JLabel();
	private JSpinner ogMaxLinesSpinner = new JSpinner();
	private JSpinner ogMaxRulesSpinner = new JSpinner();
	private JPopupMenu disPopUpMenu = new JPopupMenu();

	private DefaultComboBoxModel inputModel = new DefaultComboBoxModel(InputGrammarForm.values());
	// private DefaultComboBoxModel outputModel = new
	// DefaultComboBoxModel(OutputGrammarForm.values());

	private DefaultComboBoxModel outputModel = new DefaultComboBoxModel(
		AlgorithmHierarchii.after(InputGrammarForm.GENERAL));
	JComboBox ioDisplayCombo = new JComboBox();
	private CriteriaConstraintsChecker criteriaChecker;

	JRadioButton outputParamOnly = new JRadioButton();
	JRadioButton allParam = new JRadioButton();
	JCheckBox algPropertiesCheck = new JCheckBox();

	private JPanel[] gramarPanes = new JPanel[] { unGramarPaneInTab, nnGramarPaneInTab, epGramarPaneInTab,
		srGramarPaneInTab, lrGramarPaneInTab, gfGramarPaneInTab, cfGramarPaneInTab };

	private JPanel[] algorithmsPanes = new JPanel[] { unAlgorithmPaneInTab, nnAlgorithmPaneInTab, epAlgorithmPaneInTab,
		srAlgorithmPaneInTab, lrAlgorithmPaneInTab, gfAlgorithmPaneInTab, cfAlgorithmPaneInTab };

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
		gfRulesMinimumSpinner, gfMaximumDifficultySpinner, gfMinimumDifficultySpinner, gfRulesMaximumSpinner,
		igMaxRuleLengthSpinner };

	// opravit - pre input GENERAL nepovolit REDUCED grammars

	@Override
	public void afterInit()
	{
		this.setViewportView(settingsPane);

	}

	@Override
	public void onLocaleChanged(Locale newLocale)
	{
		resourceBundle = ResourceBundle.getBundle("cflModule", newLocale);
		updateStrings();
	}

	@Override
	public void generate(int numberOfExamplesToGenerate)
	{
		validationOK = true;
		generatorCore = FormalLanguagesExampleGenerator.getCoreInstance();

		checkSpinnerErrors();
		if (!validationOK)
		{
			return;
		}

		Map<String, JTextArea> textAreas = generatorCore.getTextAreas();
		GenerationWorker worker = new GenerationWorker(this, numberOfExamplesToGenerate);
		worker.addPublisher(new Publisher(textAreas.get("plainCZ"), "language1.", "plain.", getInputGrammar(),
			getOutputGrammar()));
		worker.addPublisher(new Publisher(textAreas.get("plainEN"), "language2.", "plain.", getInputGrammar(),
			getOutputGrammar()));
		worker.addPublisher(new Publisher(textAreas.get("latexCZ"), "language1.", "latex.", getInputGrammar(),
			getOutputGrammar()));
		worker.addPublisher(new Publisher(textAreas.get("latexEN"), "language2.", "latex.", getInputGrammar(),
			getOutputGrammar()));
		worker.addPublisher(new Publisher(textAreas.get("isCZ"), "language1.", "is.", getInputGrammar(),
			getOutputGrammar()));
		worker.addPublisher(new Publisher(textAreas.get("isEN"), "language2.", "is.", getInputGrammar(),
			getOutputGrammar()));
		worker.execute();
	}

	private void setStandardJComponentSize(JComponent component, int width, int height)
	{
		component.setMaximumSize(new Dimension(width, height));
		component.setMinimumSize(new Dimension(width, height));
		component.setPreferredSize(new Dimension(width, height));
	}

	private void checkSpinnerErrors()
	{
		for (Pair<String, String> p : criteriaChecker.getErrorMessages()){
			wrongParamsInterruptGenerating(p.getValue());
		}
	}

	private void wrongParamsInterruptGenerating(String warningMessage)
	{
		validationOK = false;
		generatorCore.generatingStopped();
		this.showWarningDialog(warningMessage);
	}

	/**
	 * shows the the error dialog pane, if the error occures
	 * 
	 * @param message
	 *            the cause of the error
	 */
	public void showErrorDialog(String message)
	{
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * shows the warning dialog pane and resets the stop and generate button
	 * 
	 * @param message
	 *            the cause of the proposition
	 */
	public void showWarningDialog(String message)
	{
		JOptionPane.showMessageDialog(this, message, resourceBundle.getString("Error"), JOptionPane.WARNING_MESSAGE);
	}

	private void updateStrings()
	{

		for (JSpinner s : allSpinners)
		{
			s.setToolTipText("<html><p style=\"font:10px Tahoma\">" + resourceBundle.getString(s.getName())
				+ " </html>");
		}

		unRulesNumberLabel1.setText(resourceBundle.getString("unRulesNumberLabel1.text")); // NOI18N
		unGrMinimumLabel1.setText(resourceBundle.getString("unGrMinimumLabel1.text")); // NOI18N
		unGrMaximumLabel1.setText(resourceBundle.getString("unGrMaximumLabel1.text")); // NOI18N
		unLinesNumberLabel1.setText(resourceBundle.getString("unLinesNumberLabel1.text")); // NOI18N
		unAlMinimumLabel1.setText(resourceBundle.getString("unAlMinimumLabel1.text")); // NOI18N
		unAlMaximumLabel1.setText(resourceBundle.getString("unAlMaximumLabel1.text")); // NOI18N
		nnAlgorithmDifficultyLabel.setText(resourceBundle.getString("nnAlgorithmDifficultyLabel.text")); // NOI18N
		unRulesNumberLabel.setText(resourceBundle.getString("unRulesNumberLabel.text")); // NOI18N
		unGrMinimumLabel.setText(resourceBundle.getString("unGrMinimumLabel.text")); // NOI18N
		unGrMaximumLabel.setText(resourceBundle.getString("unGrMaximumLabel.text")); // NOI18N
		unLinesNumberLabel.setText(resourceBundle.getString("unLinesNumberLabel.text")); // NOI18N
		unAlMinimumLabel.setText(resourceBundle.getString("unAlMinimumLabel.text")); // NOI18N
		unAlMaximumLabel.setText(resourceBundle.getString("unAlMaximumLabel.text")); // NOI18N
		unAlgorithmDifficultyLabel.setText(resourceBundle.getString("unAlgorithmDifficultyLabel.text")); // NOI18N
		unRulesNumberLabel2.setText(resourceBundle.getString("unRulesNumberLabel2.text")); // NOI18N
		unGrMinimumLabel2.setText(resourceBundle.getString("unGrMinimumLabel2.text")); // NOI18N
		unGrMaximumLabel2.setText(resourceBundle.getString("unGrMaximumLabel2.text")); // NOI18N
		unLinesNumberLabel2.setText(resourceBundle.getString("unLinesNumberLabel2.text")); // NOI18N
		unAlMinimumLabel2.setText(resourceBundle.getString("unAlMinimumLabel2.text")); // NOI18N
		unAlMaximumLabel2.setText(resourceBundle.getString("unAlMaximumLabel2.text")); // NOI18N
		epAlgorithmDifficultyLabel.setText(resourceBundle.getString("epAlgorithmDifficultyLabel.text")); // NOI18N
		unRulesNumberLabel7.setText(resourceBundle.getString("unRulesNumberLabel7.text")); // NOI18N
		unGrMinimumLabel7.setText(resourceBundle.getString("unGrMinimumLabel7.text")); // NOI18N
		unGrMaximumLabel7.setText(resourceBundle.getString("unGrMaximumLabel7.text")); // NOI18N
		unLinesNumberLabel7.setText(resourceBundle.getString("unLinesNumberLabel7.text")); // NOI18N
		unAlMinimumLabel7.setText(resourceBundle.getString("unAlMinimumLabel7.text")); // NOI18N
		unAlMaximumLabel7.setText(resourceBundle.getString("unAlMaximumLabel7.text")); // NOI18N
		srAlgorithmDifficultyLabel.setText(resourceBundle.getString("srAlgorithmDifficultyLabel.text")); // NOI18N
		unRulesNumberLabel4.setText(resourceBundle.getString("unRulesNumberLabel4.text")); // NOI18N
		unGrMinimumLabel4.setText(resourceBundle.getString("unGrMinimumLabel4.text")); // NOI18N
		unGrMaximumLabel4.setText(resourceBundle.getString("unGrMaximumLabel4.text")); // NOI18N
		unLinesNumberLabel4.setText(resourceBundle.getString("unLinesNumberLabel4.text")); // NOI18N
		unAlMinimumLabel4.setText(resourceBundle.getString("unAlMinimumLabel4.text")); // NOI18N
		unAlMaximumLabel4.setText(resourceBundle.getString("unAlMaximumLabel4.text")); // NOI18N
		lrAlgorithmDifficultyLabel.setText(resourceBundle.getString("lrAlgorithmDifficultyLabel.text")); // NOI18N
		unRulesNumberLabel5.setText(resourceBundle.getString("unRulesNumberLabel5.text")); // NOI18N
		unGrMinimumLabel5.setText(resourceBundle.getString("unGrMinimumLabel5.text")); // NOI18N
		unGrMaximumLabel5.setText(resourceBundle.getString("unGrMaximumLabel5.text")); // NOI18N
		unLinesNumberLabel5.setText(resourceBundle.getString("unLinesNumberLabel5.text")); // NOI18N
		unAlMinimumLabel5.setText(resourceBundle.getString("unAlMinimumLabel5.text")); // NOI18N
		unAlMaximumLabel5.setText(resourceBundle.getString("unAlMaximumLabel5.text")); // NOI18N
		cfAlgorithmDifficultyLabel.setText(resourceBundle.getString("cfAlgorithmDifficultyLabel.text")); // NOI18N
		unRulesNumberLabel6.setText(resourceBundle.getString("unRulesNumberLabel6.text")); // NOI18N
		unGrMinimumLabel6.setText(resourceBundle.getString("unGrMinimumLabel6.text")); // NOI18N
		unGrMaximumLabel6.setText(resourceBundle.getString("unGrMaximumLabel6.text")); // NOI18N
		unLinesNumberLabel6.setText(resourceBundle.getString("unLinesNumberLabel6.text")); // NOI18N
		unAlMinimumLabel6.setText(resourceBundle.getString("unAlMinimumLabel6.text")); // NOI18N
		unAlMaximumLabel6.setText(resourceBundle.getString("unAlMaximumLabel6.text")); // NOI18N
		gfAlgorithmDifficultyLabel.setText(resourceBundle.getString("gfAlgorithmDifficultyLabel.text")); // NOI18N
		ogRulesNumberLabel.setText(resourceBundle.getString("ogRulesNumberLabel.text")); // NOI18N
		ogMinLabel.setText(resourceBundle.getString("ogMinLabel.text")); // NOI18N
		ogMaxLabel.setText(resourceBundle.getString("ogMaxLabel.text")); // NOI18N
		ogLinesNumberLabel.setText(resourceBundle.getString("ogLinesNumberLabel.text")); // NOI18N
		ioInputGramarLabel.setText(resourceBundle.getString("ioInputGramarLabel.text")); // NOI18N
		ioOutputGramarLabel.setText(resourceBundle.getString("ioOutputGramarLabel.text")); // NOI18N
		ioExampleNumberLabel.setText(resourceBundle.getString("ioExampleNumberLabel.text")); // NOI18N
		igMaxLabel.setText(resourceBundle.getString("igMaxLabel.text")); // NOI18N
		igMinLabel.setText(resourceBundle.getString("igMinLabel.text")); // NOI18N
		igRulesNumberLabel.setText(resourceBundle.getString("igRulesNumberLabel.text")); // NOI18N
		igLinesNumberLabel.setText(resourceBundle.getString("igLinesNumberLabel.text")); // NOI18N
		igTerminalsNumberLabel.setText(resourceBundle.getString("igTerminalsNumberLabel.text")); // NOI18N
		igMaxRuleLengthLabel.setText(resourceBundle.getString("igMaxRuleLengthLabel.text")); // NOI18N

		// TitledBorders
		unGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("unGramarPaneInTab.border.title"))); // NOI18N
		unGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("unGramarPaneInTab.border.title"))); // NOI18N
		epGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("epGramarPaneInTab.border.title"))); // NOI18N
		srGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("srGramarPaneInTab.border.title"))); // NOI18N
		lrGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("lrGramarPaneInTab.border.title"))); // NOI18N
		cfGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("cfGramarPaneInTab.border.title"))); // NOI18N
		gfGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("gfGramarPaneInTab.border.title"))); // NOI18N
		outputGramarPane.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("outputGramarPane.border.title"))); // NOI18N
		InputAndOutputPane.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("InputAndOutputPane.border.title"))); // NOI18N
		InputGramarPane.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("InputGramarPane.border.title"))); // NOI18N
		nnGramarPaneInTab.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle
			.getString("nnGramarPaneInTab.border.title"))); // NOI18N

		int selectedDisplay = ioDisplayCombo.getSelectedIndex();
		ioDisplayCombo.setModel(new DefaultComboBoxModel(new String[] {
			" " + resourceBundle.getString("viewAllGramarsRadio.text"),
			" " + resourceBundle.getString("viewOnlyOutputRadio.text") }));
		if (selectedDisplay != -1)
			ioDisplayCombo.setSelectedIndex(selectedDisplay);

		int tabCount = algorithmsAndGramarTabbedPane.getTabCount();

		for (int i = 0; i < tabCount; i++)
		{
			String tabTitle = algorithmsAndGramarTabbedPane.getComponentAt(i).getName();
			algorithmsAndGramarTabbedPane
				.setTitleAt(i, resourceBundle.getString(tabTitle + ".TabConstraints.tabTitle"));
		}

		ResourceBundle outputGrammarBundle = ResourceBundle.getBundle("OutputGrammarForm", resourceBundle.getLocale());
		for (int i = 0; i < ioOutputGramarCombo.getItemCount(); i++)
		{
			OutputGrammarForm outputGrammarForm = (OutputGrammarForm) ioOutputGramarCombo.getItemAt(i);
			outputGrammarForm.setVisibleName(outputGrammarBundle.getString(outputGrammarForm.name()));
		}

		ResourceBundle inputGrammarBundle = ResourceBundle.getBundle("InputGrammarForm", resourceBundle.getLocale());
		for (int i = 0; i < ioInputGramarCombo.getItemCount(); i++)
		{
			InputGrammarForm inputGrammarForm = (InputGrammarForm) ioInputGramarCombo.getItemAt(i);
			inputGrammarForm.setVisibleName(inputGrammarBundle.getString(inputGrammarForm.name()));
		}

	}

	@Override
	public Map<String, Integer> saveSettings()
	{
		Map<String, Integer> settings = new HashMap<String, Integer>();
		// -1 is disabled
		for (JSpinner spinner : allSpinners)
		{
			settings.put(spinner.getName().replace(".name", ""), !spinnersSup.isEnabled(spinner) ? -1
				: (Integer) spinner.getValue());
		}

		return settings;
	}
	@Override
	public void loadSettings(Map<String, Integer> settings)
	{
		for (JSpinner spinner : allSpinners)
		{
			String spinnerDeclarationName = spinner.getName().replace(".name", "");
			if (settings.get(spinnerDeclarationName) == -1)
			{
				spinnersSup.disable(spinner);
			}
			else
			{
				spinner.setValue(settings.get(spinnerDeclarationName));
			}
		}
	}
}
