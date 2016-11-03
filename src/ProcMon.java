
public class ProcMon implements Runnable
{

	public ProcMon(Process proc)
	{
		_proc = proc;
		_complete = false;
	}

  private final Process _proc;
  private volatile boolean _complete;

  public boolean isComplete() { return _complete; }

  public void run() {
    try {
		_proc.waitFor();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    _complete = true;
  }

  public static ProcMon create(Process proc) {
    ProcMon procMon = new ProcMon(proc);
    Thread t = new Thread(procMon);
    t.start();
    return procMon;
  }

  public Process getProcess()
  {
	  return _proc;
  }
}