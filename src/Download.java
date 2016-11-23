import java.awt.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import org.json.*;

public class Download
{
    public JTextArea statusLabel;
    ThreadManager tm;
    GameList gameList;

    public Download(JTextArea statusLabel, int threads, GameList gameList)
    {
        this.statusLabel = statusLabel;
        this.tm = new ThreadManager(threads);
        this.gameList = gameList;
    }

    public void cancel()
    {
        if(tm != null)
        {
            tm.cancel();
        }
    }

    private static void copyFile(String pathIn, String pathOut, boolean bDeleteOnExit) throws IOException
    {
        File infile = new File(pathIn);
        File outfile = new File(pathOut);

        if(infile.isDirectory() && !outfile.isDirectory())
        {
            File[] files = infile.listFiles();
            for(File f : files)
            {
                copyFile(f.getPath().toString(), pathOut, bDeleteOnExit);
            }
        }
        else if(!infile.isDirectory() && outfile.isDirectory())
        {
            copyFile(pathIn, pathOut + "/" + infile.getName(), bDeleteOnExit);
        }
        else if(infile.isDirectory() && outfile.isDirectory())
        {
            FileUtils.copyDirectory(infile, outfile);
        }
        else if(!infile.isDirectory() && !outfile.isDirectory())
        {
            if(bDeleteOnExit)
            {
                outfile.deleteOnExit();
            }

            FileInputStream instream = new FileInputStream(infile);
            FileOutputStream outstream = new FileOutputStream(outfile);

            byte[] buffer = new byte[1024];

            int length;

            while ((length = instream.read(buffer)) > 0)
            {
                outstream.write(buffer, 0, length);
            }

            instream.close();
            outstream.close();
        }
    }

    private void extractZip(String zip) throws IOException
    {
        InputStream is = this.getClass().getResourceAsStream(zip);
        ZipInputStream zis = new ZipInputStream(is);
        ZipEntry entry = zis.getNextEntry();
        //entry = zis.getNextEntry();

        String destination = "./temp/";
        byte[] buf = new byte[1024];
        while (entry != null)
        {
            String name = entry.getName();
            int n;
            FileOutputStream fileoutputstream;

            String path = destination + name;
            File newFile = new File(path);
            newFile.getParentFile().mkdirs();
            if (name.charAt(name.length() - 1) == '/')
            {
                zis.closeEntry();
                entry = zis.getNextEntry();
                continue;
            }

            if (!newFile.exists())
            {
                newFile.createNewFile();
            }

            fileoutputstream = new FileOutputStream(path);

            while ((n = zis.read(buf, 0, 1024)) > -1)
                fileoutputstream.write(buf, 0, n);

            fileoutputstream.close();
            zis.closeEntry();
            entry = zis.getNextEntry();
        }
    }

    public void download() throws IOException, InterruptedException
    {
        //move nusgrabber into temp folder
        extractZip("Nusgrabber.zip");

        copyFile("./temp/Nusgrabber/NUSgrabber.exe", "./NUSgrabber.exe", true);
        copyFile("./temp/Nusgrabber/vcruntime140.dll", "./vcruntime140.dll", true);
        copyFile("./temp/Nusgrabber/wget.exe", "./wget.exe", true);

        //make install folder
        File f = new File("./install/");
        if(!f.exists())
        {
            f.mkdir();
        }

        //ThreadManager tm = new ThreadManager(3);
        //now to download the games
        for(Game game : gameList.getSelectedList())
        {
            String thing = "";
            if(gameList.isGame())
            {
                tm.add(game.getGame(), statusLabel);
                thing = game.getGameTitle();
            }
            if(gameList.isUpdates())
            {
                tm.add(game.getUpdate(), statusLabel);
                if(gameList.isGame())
                {
                    thing += " and ";
                }
                thing += game.getUpdateTitle();
            }
            if(!thing.equals(""))
            {
                thing += " added to queue\n";
            }
            final String final_thing = thing;
            if(!thing.equals(""))
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        statusLabel.append(final_thing);
                    }
                });
            }
        }

        Map<GameVO, DownloadThread> completed = new HashMap<GameVO, DownloadThread>();

        while(completed.size() < tm.getFutures().size())
        {
            for (Map.Entry<GameVO,DownloadThread> entry : tm.getFutures().entrySet())
            {
                GameVO game = entry.getKey();
                DownloadThread dt = entry.getValue();

                if(dt.isDone() && (completed.get(game) == null))
                {
                    completed.put(game, dt);
                    ProcMon.ExitCode exit = ProcMon.ExitCode.ERROR;
                    try
                    {
                        exit = dt.get();
                    }
                    catch (ExecutionException e)
                    {
                        e.printStackTrace();
                    }
                    if(exit == ProcMon.ExitCode.SUCCESS)
                    {
                        f = new File("./" + game.getId());
                        if(!f.exists())
                        {
                            f.mkdir();
                        }

                        f = new File("./install/" + game.getTitle() + "/");
                        if(!f.exists())
                        {
                            f.mkdir();
                        }
                        f = new File("./tickets/" + game.getTitle() + "/" + game.getId() + "/");
                        if(f.exists())
                        {
                            copyFile("./tickets/" + game.getTitle() + "/" + game.getId() + "/title.tik", "./" + game.getId() + "/title.tik", false);
                        }
                        copyFile("./" + game.getId() + "/", "./install/" + game.getTitle() + "/", false);
                        SwingUtilities.invokeLater(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                statusLabel.append(game.getTitle() + " finished\n");
                            }
                        });
                    }
                    else
                    {
                        SwingUtilities.invokeLater(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                statusLabel.append(game.getTitle() + " finished with errors (please check separate log window for exit code)\n");
                            }
                        });
                    }
                }
            }
        }
        cleanUp(gameList);

    }



    public void cleanUp(GameList gameList)
    {
        for(Game game : gameList.getSelectedList())
        {
            File f = new File ("./" + game.getGameId() + "/");
            File f2 = new File ("./" + game.getUpdateId() + "/");

            deleteDirectory(f);
            deleteDirectory(f2);
        }
        deleteDirectory(new File("./temp/"));
        deleteDirectory(new File("./NUSgrabber.exe"));
        deleteDirectory(new File("./vcruntime140.dll"));
        deleteDirectory(new File("./wget.exe"));
    }

    private static boolean deleteDirectory(File directory)
    {
        if (directory.exists())
        {
            File[] files = directory.listFiles();
            if (null != files)
            {
                for (File file : files)
                {
                    if (file.isDirectory())
                    {
                        deleteDirectory(file);
                        System.out.println("deleting " + file.getName());
                    }
                    else
                    {
                        file.delete();
                        System.out.println("deleting " + file.getName());
                    }
                }
            }
        }
        return (directory.delete());
    }
}
