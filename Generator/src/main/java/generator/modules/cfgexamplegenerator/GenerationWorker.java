
package generator.modules.cfgexamplegenerator;

import generator.common.GeneratingLogic;
import generator.communication.ModuleToCore;
import generator.communication.dto.StringAndAreaDTO;
import generator.core.FormalLanguagesExampleGenerator;
import generator.modules.CFGExampleGeneratorModule;
import generator.modules.cfgexamplegenerator.generator.GeneratorManager;
import generator.modules.cfgexamplegenerator.generator.GrammarOrderAndAlg;
import generator.modules.cfgexamplegenerator.generator.SimpleCriteria;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.SwingWorker;

public class GenerationWorker extends SwingWorker<Void, List<StringAndAreaDTO>> implements GeneratingLogic
{

	private Set<Publisher> registeredPublishers = new HashSet<Publisher>();
	private GeneratorManager manager;
	private int examplesCount;
	private Set<SimpleCriteria> criteria;
	// private boolean publishSubresults;
	// private boolean printInput;
	// private CFGExampleGeneratorModule cfgModule;
	private ModuleToCore moduleToCoreCommunication;
	private int exampleOrdNumber = 1;
	boolean publishSubresults = false;
	boolean printInput = false;

	public GenerationWorker(CFGExampleGeneratorModule cfgModule, int numberOfExamples)
	{
		// this.cfgModule = cfgModule;
		criteria = Collections.unmodifiableSet(cfgModule.fillInCriteria());

		manager = new GeneratorManager(criteria, cfgModule.getAlgToRunThrough(), cfgModule.getInputGrammar(),
			cfgModule.getOutputGrammar(), cfgModule.getMaxRuleLength(), cfgModule.getMaxNonTermNumber(),
			cfgModule.getMinNonTermNumber(), cfgModule.getMinRulesNumber(), cfgModule.getMaxTeminalsCount(), null, null);
		examplesCount = numberOfExamples;
		moduleToCoreCommunication = ModuleToCore.getInstance();
		publishSubresults = FormalLanguagesExampleGenerator.getCoreInstance().isShowSubresults();
		printInput = FormalLanguagesExampleGenerator.getCoreInstance().getPrintInput();
	}

	public Set<SimpleCriteria> getCriteria()
	{
		return criteria;
	}

	public int getExamplesCount()
	{
		return examplesCount;
	}

	public void addPublisher(Publisher newPublisher)
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
		if (!FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE)
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
		// if (!FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE)
		// {
		moduleToCoreCommunication.updateProgress(exampleOrdNumber, examplesCount);
		// }
	}

	@Override
	protected Void doInBackground() throws InterruptedException, ParseException
	{

		executeGeneration(examplesCount, ModuleToCore.getInstance());
		return null;
	}

	public void executeGeneration(int numberOfExamples, ModuleToCore moduleToCoreCommunication)
	{
		try
		{
			List<StringAndAreaDTO> publishResult = new ArrayList<StringAndAreaDTO>();
			for (int i = 1; i <= numberOfExamples; i++)
			{
				if (!FormalLanguagesExampleGenerator.IS_GENERATING_ACTIVE)
				{
					Thread.currentThread().interrupt();
					break;
				}

				List<GrammarOrderAndAlg> justGenerated = manager.generateGrammar();

				publishResult = new ArrayList<StringAndAreaDTO>();
				try
				{
					for (Publisher pub : registeredPublishers)
					{
						publishResult.add(new StringAndAreaDTO(pub
							.publish(justGenerated, publishSubresults, printInput), pub.getTextArea()));
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				exampleOrdNumber = i;

				publish(publishResult);
			}
		}
		finally
		{
			List<StringAndAreaDTO> endPrintouts = new ArrayList<StringAndAreaDTO>();
			for (Publisher p : registeredPublishers)
			{
				try
				{
					endPrintouts.add(new StringAndAreaDTO(p.closeSet(), p.getTextArea()));
				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}
			}
			publish(endPrintouts);
		}

	}

	public void executeGeneration()
	{
	}

}
