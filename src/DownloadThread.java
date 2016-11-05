
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

public class DownloadThread extends SwingWorker<ProcMon.ExitCode, String> //Thread
{
    private ProcMon procMon = null;
    private InputStream is;
    private JTextArea statusLabel = null;
    private List<String> textContents;
    private Game game;

    DownloadThread(Game game)//, JTextArea statusLabel)
    {
        this.game = game;
        textContents = new ArrayList<String>();
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
        }
        while(!procMon.isComplete())
        {
            exit = checkCancel();
            if(exit != ProcMon.ExitCode.RUNNING)
            {
                return exit;
            }
            try
            {
                int i = 0;
                final StringBuilder s = new StringBuilder();
                i = is.read();
                while(i != -1)
                {
                    s.append((char)i);
                    if(i == '\n')
                    {
                        if(s.toString().contains(".app") || s.toString().contains(".h3"))
                        {
                            publish(s.toString());
                        }
                        else if(s.toString().contains("10%") || s.toString().contains("20%") || s.toString().contains("30%") || s.toString().contains("40%") || s.toString().contains("50%") || s.toString().contains("60%") || s.toString().contains("70%") || s.toString().contains("80%") || s.toString().contains("90%"))
                        {
                            publish(s.toString());
                        }
                        s.setLength(0);
                    }
                    i = is.read();
                }
                //get last line case it doesn't end with a newline character
                publish(s.toString());
            }
            catch (IOException ioe) //| InterruptedException ioe)
            {
                ioe.printStackTrace();
            }
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
                try {
                    if (is != null)is.close();
                } catch (IOException e) {
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
        try {
            if(is != null)is.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(procMon.getExitCode() == ProcMon.ExitCode.RUNNING)
        {
            procMon.destroy();
        }
        if (procMon.isExitError())
        {
            if (statusLabel != null)statusLabel.append("~~~~~~~Error~~~~~~~\n");
        }
        else
        {
            if (statusLabel != null)statusLabel.append("~~~~~~~Finished~~~~~~~\n");
        }
    }
}


