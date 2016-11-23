
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JTextArea;

public class ThreadManager
{
    private ExecutorService executor;
    private Map <GameVO, DownloadThread> futures;

    public ThreadManager(int numThreads)
    {
        futures = new HashMap <GameVO, DownloadThread>();
        executor = Executors.newFixedThreadPool(numThreads);
    }

    public boolean add(GameVO game, JTextArea originalStatusLabel)
    {
        DownloadThread dt = new DownloadThread(game, originalStatusLabel);
        executor.execute(dt);
        futures.put(game, dt);
        return true;
    }

    public Map<GameVO, DownloadThread> getFutures()
    {
        return futures;
    }

    public void cancel()
    {
        //send cancel message to all threads (since shutdown() doesn't seem to do this)
        for (DownloadThread value : futures.values())
        {
            value.cancel(true);
        }
        executor.shutdown();
    }
}
