
public class ProcMon implements Runnable
{
    public static enum ExitCode
    {
        NULL, RUNNING, SUCCESS, CONNECTION_ERROR, ERROR
    };
    public ExitCode test = ExitCode.SUCCESS;
    private final Process _proc;
    private volatile boolean _complete;


    public ProcMon(Process proc)
    {
        _proc = proc;
        _complete = false;

    }


    public boolean isComplete()
    {
        return _complete;
    }

    public void run()
    {
        try
        {
            _proc.waitFor();
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        _complete = true;
    }

    public static ProcMon create(Process proc)
    {
        ProcMon procMon = new ProcMon(proc);
        Thread t = new Thread(procMon);
        t.start();
        return procMon;
    }

    public ExitCode getExitCode()
    {
        if (_proc == null)
        {
            return ExitCode.NULL;
        }
        try
        {
            int exit = _proc.exitValue();
            if(exit == -3)
            {
                return ExitCode.CONNECTION_ERROR;
            }
            else if(exit < 0)
            {
                return ExitCode.ERROR;
            }
            else
            {
                return ExitCode.SUCCESS;
            }
        }
        catch (Exception e)
        {
            return ExitCode.RUNNING;
        }
    }

    public Process getProcess()//this method shouldn't exist, but it ill have to until we work out what all the exit codes mean
    {
        return _proc;
    }

    public boolean isExitError()
    {
        ExitCode exit = getExitCode();
        if(exit != ExitCode.SUCCESS)
            return true;
        return false;
    }

    public void destroy()
    {
        _proc.destroy();
    }
}