
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JTextArea;

public class ThreadManager
{
    private ExecutorService executor;
    private Map <Game, DownloadThread> futures;

    public ThreadManager(int numThreads)
    {
        futures = new HashMap <Game, DownloadThread>();
        executor = Executors.newFixedThreadPool(numThreads);
    }

    public boolean add(Game game, JTextArea originalStatusLabel)
    {
        DownloadThread dt = new DownloadThread(game, originalStatusLabel);
        executor.execute(dt);
        futures.put(game, dt);
        return true;
    }

    public Map<Game, DownloadThread> getFutures()
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
