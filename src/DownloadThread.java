
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

public class DownloadThread extends SwingWorker<ProcMon.ExitCode, String> //Thread
{
    private ProcMon procMon = null;
    private InputStream is;
    private JTextArea statusLabel = null;
    private JTextArea originalStatusLabel = null;
    private List<String> textContents;
    private GameVO game;

    DownloadThread(GameVO game, JTextArea originalStatusLabel)
    {
        this.game = game;
        textContents = new ArrayList<String>();
        this.originalStatusLabel = originalStatusLabel;
    }

    public ProcMon.ExitCode doInBackground()
    {
        ProcMon.ExitCode exit = checkCancel();
        if(!(exit == ProcMon.ExitCode.RUNNING || exit == ProcMon.ExitCode.NULL))
        {
            return exit;
        }
        if(procMon == null)
        {
            ProcessBuilder pb = new ProcessBuilder("NUSgrabber.exe", game.getId());
            pb.redirectErrorStream(true);
            File f = new File("./" + game.getTitle() + "_log.txt");

            Process process = null;
            try
            {
                f.createNewFile();
                pb.redirectOutput(f);
                process = pb.start();
                is = new FileInputStream(f);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                cancel(true);
            }
            procMon = ProcMon.create(process);
            originalStatusLabel.append(game.getTitle() + " starting\n");
        }
        while((!procMon.isComplete() && exit == ProcMon.ExitCode.RUNNING) || exit == ProcMon.ExitCode.NULL)
        {
            try
            {
                int i = 0;
                final StringBuilder s = new StringBuilder();
                i = is.read();
                while(i != -1 && exit == ProcMon.ExitCode.RUNNING)
                {
                    s.append((char)i);
                    if(i == '\n')
                    {
                        if(s.toString().contains(".app") || s.toString().contains(".h3"))
                        {
                            publish(s.toString());
                        }
                        //else if(s.toString().contains("10%") || s.toString().contains("20%") || s.toString().contains("30%") || s.toString().contains("40%") || s.toString().contains("50%") || s.toString().contains("60%") || s.toString().contains("70%") || s.toString().contains("80%") || s.toString().contains("90%"))
                        if(s.toString().contains("%"))
                        {
                            publish(s.toString());
                        }
                        s.setLength(0);
                    }
                    i = is.read();
                    exit = checkCancel();
                }
                //get last line case it doesn't end with a newline character
                publish(s.toString());
            }
            catch (IOException ioe) //| InterruptedException ioe)
            {
                ioe.printStackTrace();
            }
            exit = checkCancel();
        }
        return procMon.getExitCode();
    }
    protected void process(List<String> chunks)
    {
        if(statusLabel == null)
        {
            JFrame frame = new JFrame(game.getTitle() + " Status");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(400, 600));

            StatusWindow newContentPane = new StatusWindow();
            newContentPane.setLayout(new BoxLayout(newContentPane, BoxLayout.PAGE_AXIS));
            newContentPane.setOpaque(true);
            frame.setContentPane(newContentPane);

            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setVisible(true);

            statusLabel = newContentPane.statusLabel;
        }
        for(String s : chunks)
        {
            if(s.contains(".app") || s.contains(".h3"))
            {
                textContents.add(s);
            }
            else if(s.contains("%"))
            {
                if(textContents.get(textContents.size()-1).contains("%"))
                {
                    textContents.set(textContents.size()-1, s);
                }
                else
                {
                    textContents.add(s);
                }
            }
        }
        String contents = "";
        for(int i = 0; i < textContents.size(); i++)
        {
            contents += textContents.get(i) + "\n";
        }
        statusLabel.setText(contents);
    }
    private ProcMon.ExitCode checkCancel()
    {
        if(procMon != null)
        {
            if(isCancelled() || Thread.interrupted() || Thread.currentThread().isInterrupted())
            {
                procMon.destroy();
                try
                {
                    if (is != null)is.close();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return ProcMon.ExitCode.ERROR;
            }
            return procMon.getExitCode();
        }
        return ProcMon.ExitCode.NULL;
    }

    @Override
    public void done()
    {
        //need to think of a better thing to do here
        try
        {
            if(is != null)is.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(procMon.getExitCode() == ProcMon.ExitCode.RUNNING)
        {
            procMon.destroy();
        }
        sendGame();
        if (statusLabel != null)statusLabel.append("~~~~~~~Finished~~~~~~~ Exit code: " + procMon.getProcess().exitValue());//shouldn't be allowed to directly access the process - but how else to get the real exit code?
    }

    public void sendGame()
    {
        JSONObject obj = getJson(game);

        if(obj != null)
        {
            sendJson(obj);
        }
    }
    public void sendJson(JSONObject json)
    {
        try
        {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost("http://quantumc.at/update.php");
            HttpResponse response = null;
            post.addHeader("Content-type", "application/json");

            ArrayList<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
            list.add(new BasicNameValuePair("value", json.toString()));
            post.setEntity(new UrlEncodedFormEntity(list));

            response = httpClient.execute(post);
            httpClient.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private static JSONObject getJson(GameVO game)
    {
        File f = new File ("./" + game.getId() + "/");
        if(f.exists())
        {
            JSONObject obj = new JSONObject();
            obj.put("titleid", game.getId());
            obj.put("name", game.getTitle());

            JSONObject files = new JSONObject();

            File[] innerFiles = f.listFiles();
            int i = 1;
            for(File innerFile : innerFiles)
            {
                if(!innerFile.isDirectory())
                {
                    JSONObject file = new JSONObject();
                    file.put("name", innerFile.getName());
                    file.put("size", String.valueOf(innerFile.length()));
                    files.put("file" + String.valueOf(i), file);
                    i++;
                }
            }
            obj.put("files", files);
            return obj;
        }
        return null;
    }
}


