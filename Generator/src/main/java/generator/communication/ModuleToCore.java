
package generator.communication;

import generator.core.FormalLanguagesExampleGenerator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.Timer;

/**
 * Singleton serving as communication API for communication between module, its logic and core of the application.
 * 
 * @author JUH
 */
public class ModuleToCore
{
	// TODO add javadocs to methods!
	private static ModuleToCore instance;
	private FormalLanguagesExampleGenerator generatorCore;
	private ResourceBundle resourceBundle;
	private JProgressBar progressBar;
	private Map<String, JTextArea> textAreas;
	private int lastGeneratedExampleNumber;
	private int runLengthInSeconds = 0;
	private Timer t;
	private String generatorRunning;
	private String generated;
	private String examples;

	public static ModuleToCore getInstance()
	{
		if (instance == null)
		{
			instance = new ModuleToCore();
		}
		return instance;
	}

	public ModuleToCore()
	{
		generatorCore = FormalLanguagesExampleGenerator.getCoreInstance();
		textAreas = generatorCore.getTextAreas();
		progressBar = generatorCore.getProgressBar();
	}

	public void publishResult(String plainCZ, String plainEN, String latexCZ, String latexEN, String isCZ, String isEN)
	{
		textAreas.get("plainCZ").append(plainCZ);
		textAreas.get("plainEN").append(plainEN);
		textAreas.get("latexCZ").append(latexCZ);
		textAreas.get("latexEN").append(latexEN);
		textAreas.get("isCZ").append(isCZ);
		textAreas.get("isEN").append(isEN);
	}

	public void publishSingleResultAndAppend(JTextArea area, String singleResult)
	{
		area.append(singleResult);
	}

	@SuppressWarnings("static-access")
	public void updateProgress(int exampleOrderNumber, int totalExamples)
	{
		lastGeneratedExampleNumber = exampleOrderNumber;
		int progressPercent = (int) Math.round(((double) exampleOrderNumber / totalExamples) * 100);
		progressBar.setValue(progressPercent);
		generatorCore.getStatusLabel().setText(
			generatorRunning + " " + runLengthInSeconds + "s" + ". " + generated + " " + exampleOrderNumber + "/"
				+ totalExamples + " " + examples + ".");
		if (exampleOrderNumber == totalExamples)
		{
			generatorCore.getStatusLabel().setText("");
			generatorCore.generatingStopped();
		}
	}

	public int getLastGeneratedExampleNumber()
	{
		return lastGeneratedExampleNumber;
	}

	public void generationStarted()
	{
		t = new Timer(1000, new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				runLengthInSeconds++;
			}
		});

		t.start();
	}

	public void stopAndResetTimer()
	{
		if (t != null)
		{
			t.stop();
		}
		runLengthInSeconds = 0;
	}

	public void updateLocale(Locale newLocale)
	{
		resourceBundle = ResourceBundle.getBundle("mainPanel", newLocale);
		generatorRunning = resourceBundle.getString("generatorRunning");
		generated = resourceBundle.getString("generated");
		examples = resourceBundle.getString("examples");

	}

}
