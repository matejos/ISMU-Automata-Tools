
package generator.communication;

import generator.communication.dto.ExampleDTO;
import generator.core.FormalLanguagesExampleGenerator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.Timer;

public class ExampleConsumer extends SwingWorker<Void, Void> implements Runnable
{
	private BlockingQueue<ExampleDTO> queue;
	private Map<String, JTextArea> textAreas;
	private int exampleNumber = 1;
	private JProgressBar progressBar;
	private int totalExampleCount = 0;
	private FormalLanguagesExampleGenerator generatorCore;
	private ResourceBundle resourceBundle;
	private int lastExampleNumber;
	private StringBuilder plainCZ = new StringBuilder();
	private StringBuilder plainEn = new StringBuilder();
	private StringBuilder latexCz = new StringBuilder();
	private StringBuilder latexEn = new StringBuilder();
	private StringBuilder isCz = new StringBuilder();
	private StringBuilder isEn = new StringBuilder();
	private String generatorRunning;
	private String generated;
	private String examples;
	private int runLengthInSeconds = 0;
	private Timer t;

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String EXAMPLE_LINE = "********************";

	private static final String latexIntroString = "\\documentclass[czech]{article}" + LINE_SEPARATOR
		+ "\\usepackage[T1]{fontenc}" + LINE_SEPARATOR + "\\usepackage[utf8]{inputenc}" + LINE_SEPARATOR
		+ "\\usepackage{babel}" + LINE_SEPARATOR + "\\begin{document}" + LINE_SEPARATOR;

	public ExampleConsumer(BlockingQueue<ExampleDTO> q, Map<String, JTextArea> textAreas,
		FormalLanguagesExampleGenerator generatorCore)
	{
		this.queue = q;
		this.textAreas = textAreas;
		this.generatorCore = generatorCore;
		progressBar = generatorCore.getProgressBar();
	}

	public void updateLocale(Locale newLocale)
	{
		resourceBundle = ResourceBundle.getBundle("mainPanel", newLocale);
		generatorRunning = resourceBundle.getString("generatorRunning");
		generated = resourceBundle.getString("generated");
		examples = resourceBundle.getString("examples");
	}

	@Override
	protected Void doInBackground() throws Exception
	{

		while (true)
		{
			try
			{
				ExampleDTO exampleForms = queue.take();
				// give memory allocation some time before first output call
				if (exampleNumber == 1)
				{
					if (totalExampleCount > 300)
					{
						Thread.sleep(500);
					}
					else if (totalExampleCount > 100)
					{
						Thread.sleep(100);
					}
					else if (totalExampleCount > 10)
					{
						Thread.sleep(5);
					}
				}
				if (FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE)
				{
					updateTextAreas(exampleForms);
					updateProgressMainPanel();
				}
				if (totalExampleCount == exampleNumber)
					generatingComplete();
				exampleNumber++;

			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

	}

	public void setTotalExampleCount(int totalExampleCount)
	{
		this.totalExampleCount = totalExampleCount;
	}

	public void reset()
	{
		for (JTextArea textArea : textAreas.values())
		{
			textArea.setText("");
		}

		textAreas.get("latexCZ").append(latexIntroString);
		textAreas.get("latexEN").append(latexIntroString);

		resetCounter();

	}

	public void resetCounter()
	{
		exampleNumber = 1;
	}

	@SuppressWarnings("static-access")
	private void generatingComplete()
	{
		isEn = new StringBuilder();
		plainEn = new StringBuilder();
		latexCz = new StringBuilder();
		latexEn = new StringBuilder();
		plainCZ = new StringBuilder();
		isCz = new StringBuilder();
		for (JTextArea textArea : textAreas.values())
		{
			textArea.selectAll();
		}
		generatorCore.getStatusLabel().setText("");
		stopAndResetTimer();
		generatorCore.generatingStopped();

	}

	private void updateTextAreas(final ExampleDTO exampleForms)
	{
		plainEn = new StringBuilder();
		latexCz = new StringBuilder();
		latexEn = new StringBuilder();
		plainCZ = new StringBuilder();

		plainCZ.append(LINE_SEPARATOR + EXAMPLE_LINE + LINE_SEPARATOR + "  PlainCZ Example " + exampleNumber
			+ LINE_SEPARATOR + EXAMPLE_LINE + LINE_SEPARATOR + exampleForms.getPlainCZOutput());
		plainEn.append(LINE_SEPARATOR + EXAMPLE_LINE + LINE_SEPARATOR + "  PlainEn Example " + exampleNumber
			+ LINE_SEPARATOR + EXAMPLE_LINE + LINE_SEPARATOR + exampleForms.getPlainENOutput());
		latexCz.append(LINE_SEPARATOR + "\\hrule \\bigskip \\noindent \\textbf{Příklad " + exampleNumber + ". }" + LINE_SEPARATOR
			+ exampleForms.getLateXCZOutput());
		latexEn.append(LINE_SEPARATOR + "\\hrule \\bigskip \\noindent \\textbf{Example " + exampleNumber + ". }" + LINE_SEPARATOR
			+ exampleForms.getLateXENOutput());
		if (!"".equals(isCz.toString())) {
			isCz = new StringBuilder();
			isCz.append("--" + LINE_SEPARATOR);
		}
		else {
			isCz = new StringBuilder();
		}
		isCz.append(LINE_SEPARATOR
				//+ EXAMPLE_LINE + LINE_SEPARATOR + "  iscz Example " + exampleNumber + LINE_SEPARATOR + EXAMPLE_LINE + LINE_SEPARATOR
				+ exampleForms.getISCZOutput());
		if (!"".equals(isEn.toString())) {
			isEn = new StringBuilder();
			isEn.append("--" + LINE_SEPARATOR);
		}
		else {
			isEn = new StringBuilder();
		}
		isEn.append(LINE_SEPARATOR
				//+ EXAMPLE_LINE + LINE_SEPARATOR + "  isen Example " + exampleNumber + LINE_SEPARATOR + EXAMPLE_LINE + LINE_SEPARATOR
				+ exampleForms.getISENOutput());

		if (exampleNumber % 1 == 0)
		{
			textAreas.get("plainCZ").append(plainCZ.toString());
			textAreas.get("plainEN").append(plainEn.toString());
			textAreas.get("latexCZ").append(latexCz.toString());
			textAreas.get("latexEN").append(latexEn.toString());
			textAreas.get("isCZ").append(isCz.toString());
			textAreas.get("isEN").append(isEn.toString());

			for (JTextArea textArea : textAreas.values())
			{
				textArea.selectAll();
			}
		}
		lastExampleNumber = exampleNumber;

	}

	@SuppressWarnings("static-access")
	private void updateProgressMainPanel()
	{

		int a = (int) Math.round(((double) exampleNumber / totalExampleCount) * 100);
		progressBar.setValue(a);
		generatorCore.getStatusLabel().setText(
			generatorRunning + " " + runLengthInSeconds + "s. " + generated + " " + exampleNumber + "/"
				+ totalExampleCount + " " + examples + ".");

	}

	public int getLastGeneratedExampleNumber()
	{
		return lastExampleNumber;
	}

	public void stopAndResetTimer()
	{
		if (t != null)
		{
			t.stop();
		}
		runLengthInSeconds = 0;
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

}
