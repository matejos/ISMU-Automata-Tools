
package generator.communication.dto;

public class ExampleDTO
{

	private int generatedExamples = 0;
	private String plainCZOutput = "";
	private String plainENOutput = "";
	private String ISCZOutput = "";
	private String ISENOutput = "";
	private String lateXCZOutput = "";
	private String lateXENOutput = "";
	private int totalExamplesCount;

	public int getTotalExamplesCount()
	{
		return totalExamplesCount;
	}

	public ExampleDTO(int totalExamplesCount)
	{
		this.totalExamplesCount = totalExamplesCount;
	}

	public int getGeneratedExamples()
	{
		return generatedExamples;
	}

	public String getPlainCZOutput()
	{
		return plainCZOutput;
	}

	public String getPlainENOutput()
	{
		return plainENOutput;
	}

	public String getISCZOutput()
	{
		return ISCZOutput;
	}

	public String getISENOutput()
	{
		return ISENOutput;
	}

	public String getLateXCZOutput()
	{
		return lateXCZOutput;
	}

	public String getLateXENOutput()
	{
		return lateXENOutput;
	}

	public void setPlainCZOutput(String plainCZOutput)
	{
		this.plainCZOutput = plainCZOutput;
	}

	public void setPlainENOutput(String plainENOutput)
	{
		this.plainENOutput = plainENOutput;
	}

	public void setISCZOutput(String iSCZOutput)
	{
		ISCZOutput = iSCZOutput;
	}

	public void setISENOutput(String iSENOutput)
	{
		ISENOutput = iSENOutput;
	}

	public void setLateXCZOutput(String lateXCZOutput)
	{
		this.lateXCZOutput = lateXCZOutput;
	}

	public void setLateXENOutput(String lateXENOutput)
	{
		this.lateXENOutput = lateXENOutput;
	}

	@Override
	public String toString()
	{
		return String.valueOf(totalExamplesCount);
	}

	// public void continuousProgressUpdate
}
