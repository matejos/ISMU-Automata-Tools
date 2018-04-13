/*
 * CFGExampleGeneratorApp.java
 */

package generator.modules.cfgexamplegenerator;

import generator.communication.dto.StringAndAreaDTO;
import generator.modules.cfgexamplegenerator.generator.GeneratorManager;
import generator.modules.cfgexamplegenerator.generator.GrammarOrderAndAlg;
import generator.modules.cfgexamplegenerator.generator.SimpleCriteria;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.Timer;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

/**
 * @author Rastislav Mirek, Matus Abaffy at Masaryk University, Brno, Czech Republic
 * @mail rmirek@mail.muni.cz\
 * @version Expression version i s undefined on line 14, column 15 in Templates/Classes/Class.java.
 * @copyright Rastislav Mirek, Matus Abaffy all rights reserved
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CFGExampleGeneratorApp extends SingleFrameApplication
{

	private static GenerationActionTask generateTask;
	public static final CFGExampleGeneratorView MAIN_VIEW = new CFGExampleGeneratorView(
		CFGExampleGeneratorApp.getApplication());
	private static AtomicBoolean wasCanceled = new AtomicBoolean(false);
	private static final int MINIMAL_LONG_OF_GENERATION_TASK = 250;

	public static CFGExampleGeneratorView getMAIN_VIEW()
	{
		return MAIN_VIEW;
	}

	public static GenerationActionTask getGenerateTask()
	{
		return generateTask;
	}

	/**
	 * At startup create and show the main frame of the application.
	 */
	@Override
	protected void startup()
	{
		show(MAIN_VIEW);
	}

	public synchronized static boolean isCanceled()
	{
		return wasCanceled.get();
	}

	/**
	 * This method is to initialize the specified window by injecting resources. Windows shown in our application come
	 * fully initialized from the GUI builder, so this additional configuration is not needed.
	 */
	@Override
	protected void configureWindow(java.awt.Window root)
	{
	}

	/**
	 * A convenient static getter for the application instance.
	 * 
	 * @return the instance of CFGExampleGeneratorApp
	 */
	public static CFGExampleGeneratorApp getApplication()
	{
		return Application.getInstance(CFGExampleGeneratorApp.class);
	}

	private void setUp()
	{
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
	}

	/**
	 * Main method launching the application.
	 */
	public static void main(String[] args)
	{
		getApplication().setUp();
		launch(CFGExampleGeneratorApp.class, args);
	}

	@org.jdesktop.application.Action
	public synchronized void cancelGenerationAction()
	{
		if (!generateTask.getUserCanCancel())
		{
			MAIN_VIEW
				.showErrorDialog("Generation could not be canceled! \nIt seems that canceling of generation task by user is not allowed");
		}
		else
		{
			wasCanceled.set(true);
			if (generateTask.cancel(true))
			{
				MAIN_VIEW.showInformationDialog("Generation was canceled");
			}
			else
			{
				MAIN_VIEW
					.showInformationDialog("Generation could not be canceled. Probably generation was completed normally before cancel was "
						+ "commited");
			}
		}
	}

	private static class MyUncaughtExceptionHandler implements UncaughtExceptionHandler
	{

		public MyUncaughtExceptionHandler()
		{
		}

		public void uncaughtException(Thread t, Throwable e)
		{
			try
			{
				System.err.print("Exception in thread \"" + t.getName() + "\" ");
				e.printStackTrace(System.err);
				MAIN_VIEW.showErrorDialog(e, "This error occured in thread " + t + " : ");
			}
			catch (Throwable trow)
			{
				// nothing - this is to prevent infinite loop
			}
		}
	}

	@Action
	public Task generationAction()
	{
		wasCanceled.set(false);
		generateTask = new GenerationActionTask(Application.getInstance(CFGExampleGeneratorApp.class));

		generateTask.addPublisher(new Publisher(MAIN_VIEW.getPlain_skArea(), "language1.", "plain.", MAIN_VIEW
			.getInputGrammar(), MAIN_VIEW.getOutputGrammar()));
		generateTask.addPublisher(new Publisher(MAIN_VIEW.getPlain_enArea(), "language2.", "plain.", MAIN_VIEW
			.getInputGrammar(), MAIN_VIEW.getOutputGrammar()));
		generateTask.addPublisher(new Publisher(MAIN_VIEW.getLatex_skArea(), "language1.", "latex.", MAIN_VIEW
			.getInputGrammar(), MAIN_VIEW.getOutputGrammar()));
		generateTask.addPublisher(new Publisher(MAIN_VIEW.getLatex_enArea(), "language2.", "latex.", MAIN_VIEW
			.getInputGrammar(), MAIN_VIEW.getOutputGrammar()));

		// tato podmienka tu bola preto, lebo vyhodnocovacia IS-sluzba nepozna hentake vystupne formy
		// Set<OutputGrammarForm> ISNotEquivalentGrammars = new HashSet<OutputGrammarForm>();
		// ISNotEquivalentGrammars.add(OutputGrammarForm.RED_CNF);
		// ISNotEquivalentGrammars.add(OutputGrammarForm.RED_GNF);
		// ISNotEquivalentGrammars.add(OutputGrammarForm.RED_NO_LEFT_RECURSION);
		// ISNotEquivalentGrammars.add(OutputGrammarForm.NO_EPSILON_NO_USELESS);
		// if(!ISNotEquivalentGrammars.contains(MAIN_VIEW.getOutputGrammar())){
		generateTask.addPublisher(new Publisher(MAIN_VIEW.getIs_skArea(), "language1.", "is.", MAIN_VIEW
			.getInputGrammar(), MAIN_VIEW.getOutputGrammar()));
		generateTask.addPublisher(new Publisher(MAIN_VIEW.getIs_enArea(), "language2.", "is.", MAIN_VIEW
			.getInputGrammar(), MAIN_VIEW.getOutputGrammar()));
		// }

		MAIN_VIEW.setFunctionsEnabled(true);
		return generateTask;
	}

	public class GenerationActionTask extends Task<Void, List<StringAndAreaDTO>>
	{

		private Set<Publisher> registeredPublishers = new HashSet<Publisher>();
		private GeneratorManager manager;
		private int examplesCount;
		private int generatedNumber = 0;
		private Set<SimpleCriteria> criteria;
		private boolean publishSubresults;
		private boolean printInput;

		GenerationActionTask(org.jdesktop.application.Application app)
		{
			super(app);
			criteria = Collections.unmodifiableSet(MAIN_VIEW.fillInCriteria());
			manager = new GeneratorManager(criteria, MAIN_VIEW.getAlgToRunThrough(), MAIN_VIEW.getInputGrammar(),
				MAIN_VIEW.getOutputGrammar(), MAIN_VIEW.getMaxRuleLength(), MAIN_VIEW.getMaxNonTermNumber(),
				MAIN_VIEW.getMinNonTermNumber(), MAIN_VIEW.getMinRulesNumber(), MAIN_VIEW.getMaxTeminalsCount(), null,
				null);
			examplesCount = MAIN_VIEW.getNumberOfExamples();
			publishSubresults = MAIN_VIEW.isShowSubresults();
			printInput = MAIN_VIEW.getPrintInput();
		}

		public Set<SimpleCriteria> getCriteria()
		{
			return criteria;
		}

		public int getExamplesCount()
		{
			return examplesCount;
		}

		public int getGeneratedNumber()
		{
			return generatedNumber;
		}

		void addPublisher(Publisher newPublisher)
		{
			if (newPublisher == null)
			{
				throw new NullPointerException("newPublisher");
			}
			registeredPublishers.add(newPublisher);
		}

		@Override
		protected void process(List<List<StringAndAreaDTO>> chunks)
		{
			if (isCancelled() || wasCanceled.get())
			{
				return;
			}
			for (List<StringAndAreaDTO> gen : chunks)
			{
				for (StringAndAreaDTO g : gen)
				{
					g.getArea().append(g.getText());
				}
			}
			generatedNumber = generatedNumber + chunks.size();
		}

		@Override
		protected void failed(Throwable cause)
		{
			super.failed(cause);
			if (cause instanceof ParseException)
			{
				MAIN_VIEW
					.showErrorDialog(
						cause,
						"During parsing script which sets how to display generated examples ParseExcetion "
							+ "occured.\n If you changed any of these scripts you should correct it.\n If you think the interpreter of "
							+ "these scripts does not work correctly please contact the author.\n The exception was: ");
			}
			else
			{
				MAIN_VIEW.showErrorDialog(cause, "This error occured during example generation: ");
			}
		}

		private void closePrintouts()
		{
			try
			{
				for (Publisher p : registeredPublishers)
				{
					p.getTextArea().append(p.closeSet());
				}
			}
			catch (ParseException ex)
			{
				MAIN_VIEW
					.showErrorDialog(
						ex,
						"During parsing script which sets how to display generated examples ParseExcetion "
							+ "occured.\n If you changed any of these scripts you should correct it.\n If you think the interpreter of "
							+ "these scripts does not work correctly please contact the author.\n The exception was: ");
			}
			catch (Throwable t)
			{
				MAIN_VIEW.showErrorDialog(t,
					"This error occured at the end of example generation, during last printout: ");
			}
		}

		@Override
		protected Void doInBackground() throws InterruptedException, ParseException
		{
			try
			{
				List<StringAndAreaDTO> publishResult = new ArrayList<StringAndAreaDTO>();
				for (int i = 1; i <= examplesCount; i++)
				{
					if (isCancelled())
					{
						Thread.currentThread().interrupt();
						break;
					}
					List<GrammarOrderAndAlg> justGenerated = manager.generateGrammar();
					publishResult = new ArrayList<StringAndAreaDTO>();
					for (Publisher pub : registeredPublishers)
					{
						publishResult.add(new StringAndAreaDTO(pub
							.publish(justGenerated, publishSubresults, printInput), pub.getTextArea()));
					}
					publish(publishResult);

					setProgress(i, 0, examplesCount);
				}
			}
			finally
			{
				List<StringAndAreaDTO> endPrintouts = new ArrayList<StringAndAreaDTO>();
				for (Publisher p : registeredPublishers)
				{
					endPrintouts.add(new StringAndAreaDTO(p.closeSet(), p.getTextArea()));
				}
				publish(endPrintouts);
			}

			return null;
		}

		@Override
		protected void cancelled()
		{
			super.cancelled();
			closePrintouts();
		}

		@Override
		public synchronized boolean isProgressPropertyValid()
		{
			return true;
		}

		@Override
		protected void finished()
		{

			Timer tim = new Timer((int) Math.max(0, MINIMAL_LONG_OF_GENERATION_TASK
				- getExecutionDuration(TimeUnit.MILLISECONDS)), new ActionListener()
			{

				public void actionPerformed(ActionEvent e)
				{
					MAIN_VIEW.setFunctionsEnabled(false);

				}
			});
			tim.setRepeats(false);
			tim.start();
			// closePrintouts();
		}

	}

	@Action
	public Task clearAndGenerate()
	{
		MAIN_VIEW.clearAction();
		return generationAction();
	}

}
