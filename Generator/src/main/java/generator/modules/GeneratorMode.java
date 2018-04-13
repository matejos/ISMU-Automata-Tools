
package generator.modules;

@SuppressWarnings("rawtypes")
public enum GeneratorMode
{
	REG_LANG("regLang", RegModule.class), CFL("cfl", CFGExampleGeneratorModule.class), CYK("cyk", CYKModule.class);

	private String generatorModeName;
	private Class generatorClass;

	GeneratorMode(String generatorModeName, Class generatorClass)
	{
		this.generatorModeName = generatorModeName;
		this.generatorClass = generatorClass;
	}

	public String getGeneratorModeName()
	{
		return generatorModeName;
	}

	public void setGeneratorModeName(String generatorModeName)
	{
		this.generatorModeName = generatorModeName;
	}

	public Class getGeneratorClass()
	{
		return generatorClass;
	}

	public void setGeneratorClass(Class generatorClass)
	{
		this.generatorClass = generatorClass;
	}

}
