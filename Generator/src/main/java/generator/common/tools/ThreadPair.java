
package generator.common.tools;

/**
 * POJO for pair threadId/numberOfExamplesPerThread
 * 
 * @author JUH
 */
public class ThreadPair
{
	private int threadId;
	private int numberOfExamplesPerThread;

	public ThreadPair(int threadId, int numberOfExamplesPerThread)
	{
		super();
		this.threadId = threadId;
		this.numberOfExamplesPerThread = numberOfExamplesPerThread;
	}

	public int getThreadId()
	{
		return threadId;
	}

	public void setThreadId(int threadId)
	{
		this.threadId = threadId;
	}

	public int getNumberOfExamplesPerThread()
	{
		return numberOfExamplesPerThread;
	}

	public void setNumberOfExamplesPerThread(int numberOfExamplesPerThread)
	{
		this.numberOfExamplesPerThread = numberOfExamplesPerThread;
	}

	@Override
	public String toString()
	{
		return "ThreadPair [threadId=" + threadId + ", numberOfExamplesPerThread=" + numberOfExamplesPerThread + "]";
	}

}
