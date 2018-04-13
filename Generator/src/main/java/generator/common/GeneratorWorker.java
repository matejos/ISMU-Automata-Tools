
package generator.common;

import generator.communication.ModuleToCore;
import generator.communication.dto.StringAndAreaDTO;
import generator.core.FormalLanguagesExampleGenerator;
import java.util.List;
import java.util.Map;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public abstract class GeneratorWorker extends SwingWorker<Void, List<StringAndAreaDTO>> implements GeneratingLogic
{
	protected JTextArea plainAreaCz;
	protected JTextArea plainAreaEn;
	protected JTextArea latexAreaEn;
	protected JTextArea latexAreaCz;
	protected JTextArea isAreaEn;
	protected JTextArea isAreaCz;
	protected ModuleToCore moduleToCoreCommunication;

	public GeneratorWorker()
	{
		FormalLanguagesExampleGenerator generatorCore = FormalLanguagesExampleGenerator.getCoreInstance();
		Map<String, JTextArea> textAreasMap = generatorCore.getTextAreas();
		plainAreaCz = textAreasMap.get("plainCZ");
		plainAreaEn = textAreasMap.get("plainEN");
		latexAreaCz = textAreasMap.get("latexCZ");
		latexAreaEn = textAreasMap.get("latexEN");
		isAreaEn = textAreasMap.get("isCZ");
		isAreaCz = textAreasMap.get("isEN");
		moduleToCoreCommunication = ModuleToCore.getInstance();
	}

	protected Void doInBackground()
	{
		executeGeneration();
		return null;
	}

	public abstract void executeGeneration();

	public void executeGeneration(int numberOfExamples, ModuleToCore moduleToCoreCommunication)
	{
	}

	protected void process(List<List<StringAndAreaDTO>> chunks)
	{
		for (List<StringAndAreaDTO> gen : chunks)
		{
			for (StringAndAreaDTO g : gen)
			{
				g.getArea().append(g.getText());
			}
		}
	}

	public JTextArea getPlainAreaCz()
	{
		return plainAreaCz;
	}

	public void setPlainAreaCz(JTextArea plainAreaCz)
	{
		this.plainAreaCz = plainAreaCz;
	}

	public JTextArea getPlainAreaEn()
	{
		return plainAreaEn;
	}

	public void setPlainAreaEn(JTextArea plainAreaEn)
	{
		this.plainAreaEn = plainAreaEn;
	}

	public JTextArea getLatexAreaEn()
	{
		return latexAreaEn;
	}

	public void setLatexAreaEn(JTextArea latexAreaEn)
	{
		this.latexAreaEn = latexAreaEn;
	}

	public JTextArea getLatexAreaCz()
	{
		return latexAreaCz;
	}

	public void setLatexAreaCz(JTextArea latexAreaCz)
	{
		this.latexAreaCz = latexAreaCz;
	}

	public JTextArea getIsAreaEn()
	{
		return isAreaEn;
	}

	public void setIsAreaEn(JTextArea isAreaEn)
	{
		this.isAreaEn = isAreaEn;
	}

	public JTextArea getIsAreaCz()
	{
		return isAreaCz;
	}

	public void setIsAreaCz(JTextArea isAreaCz)
	{
		this.isAreaCz = isAreaCz;
	}

	public ModuleToCore getModuleToCoreCommunication()
	{
		return moduleToCoreCommunication;
	}

	public void setModuleToCoreCommunication(ModuleToCore moduleToCoreCommunication)
	{
		this.moduleToCoreCommunication = moduleToCoreCommunication;
	}

}
