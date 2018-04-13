
package generator.common;

import generator.common.tools.Constants;
import generator.core.FormalLanguagesExampleGenerator;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Locale;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Jiri Uhlir
 */
public abstract class GenericModulePane extends JScrollPane
{

	private static final long serialVersionUID = 1L;
	private Map<String, JTextArea> output;
	protected JLabel messagesLabel;

	public GenericModulePane()
	{

		this.setMinimumSize(new java.awt.Dimension(203, 200));
		this.setName("settingsScrollPane"); // NOI18N
		this.setPreferredSize(new java.awt.Dimension(Constants.WIDTH_LEFT_PANEL_MODULE_CONTENT,
			Constants.HEIGHT_LEFT_PANEL_MODULE_CONTENT));
		setMaximumSize(new Dimension(Constants.WIDTH_LEFT_PANEL_MODULE_CONTENT, Short.MAX_VALUE));
		messagesLabel = FormalLanguagesExampleGenerator.getStatusLabel();
	}

	public Map<String, JTextArea> getOutput()
	{
		return output;
	}

	public void setOutput(Map<String, JTextArea> output)
	{
		this.output = output;
	}

	/**
	 * Top level pane of module should be set as viewPortView of this object in this method.
	 */
	public abstract void afterInit();

	/**
	 * In this method, resourceBundle should be reinitialized to use new specified Locale and all Strings should be
	 * reset according to current bundle.
	 * 
	 * @param newLocale
	 *            Locale which will be used to translate localized strings
	 */
	public abstract void onLocaleChanged(Locale newLocale);
	// scrollableContentContainer.setViewportView(this);

	/**
	 * This method is called from main GUI container, it should execute example generation and also take current locale
	 * into account (should be passed as part of the call to the background generating logic. It is programmers
	 * responsibility to take care that multithread generating is ensured in correct number of examples (for getting
	 * number of examples per thread CommonUtils.getThreadsForGivenExampleNumber method can be used). Each generating
	 * thread should also implement verification against FormalLanguagesGenerator.IS_GENERATING_ACTIVE and if it's set
	 * to false, generating should be stopped.
	 */
	public abstract void generate(int numberOfExamplesToGenerate);

	public abstract Map<String, Integer> saveSettings();

	public abstract void loadSettings(Map<String, Integer> settings);

	public void updateAllFonts()
	{
		updateComponent(this);
	}

	private void updateComponent(JComponent c)
	{
		c.setFont(Constants.DEFAULT_WIN_FONT);
		for (Component comp : c.getComponents())
		{
			if (comp instanceof JComponent)
				updateComponent((JComponent) comp);
		}

	}
}
