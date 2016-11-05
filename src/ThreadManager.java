import java.io.File;
import javax.swing.SwingWorker;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadManager
{
    private ExecutorService executor;
    private Map <Game, DownloadThread> futures;

    public ThreadManager(int numThreads)
    {
        futures = new HashMap <Game, DownloadThread>();
        executor = Executors.newFixedThreadPool(numThreads);
    }

    public boolean add(Game game)
    {
        ProcessBuilder pb = new ProcessBuilder("NUSgrabber.exe", game.getId());
        pb.redirectErrorStream(true);
        pb.redirectOutput(new File("./" + game.getTitle() + "_log.txt"));
        Process process;
        try
        {
            process = pb.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        DownloadThread dt = new DownloadThread(process, game.getTitle());
        executor.execute(dt);
        futures.put(game, dt);
        return true;
    }

    public Map<Game, DownloadThread> getFutures()
    {
        return futures;
    }
}
