
package generator.common;

import generator.core.FormalLanguagesExampleGenerator;
import generator.modules.GeneratorMode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

/**
 * @author Jiri Uhlir
 */
public class ModuleFactory
{
	private static GenericModulePane selectedModule = null;

	public static GenericModulePane getModule(GeneratorMode mode, FormalLanguagesExampleGenerator core)
		throws Exception
	{
		selectedModule = (GenericModulePane) mode.getGeneratorClass().newInstance();

		selectedModule.afterInit();
		selectedModule.addPropertyChangeListener("locale", new PropertyChangeListener()
		{

			GenericModulePane moduleCopy = selectedModule;
			public void propertyChange(PropertyChangeEvent evt)
			{
				moduleCopy.onLocaleChanged((Locale) evt.getNewValue());
			}
		});
		return selectedModule;
	}

}
