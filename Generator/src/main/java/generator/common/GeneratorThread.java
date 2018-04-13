
package generator.common;

public abstract class GeneratorThread implements Runnable
{

	private int numberOfExamplesPerThread;

	public GeneratorThread(int numberOfExamplesPerThread)
	{
		this.setNumberOfExamplesPerThread(numberOfExamplesPerThread);
	}
	public int getNumberOfExamplesPerThread()
	{
		return numberOfExamplesPerThread;
	}
	public void setNumberOfExamplesPerThread(int numberOfExamplesPerThread)
	{
		this.numberOfExamplesPerThread = numberOfExamplesPerThread;
	}

}
