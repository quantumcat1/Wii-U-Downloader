import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;

public class Download
{
    public JTextArea statusLabel;
    ThreadManager tm;
    public Download(JTextArea statusLabel, ThreadManager tm)
    {
        this.statusLabel = statusLabel;
        this.tm = tm;
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

    public void download(GameList gameList) throws IOException, InterruptedException
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
            if(gameList.isGame())
            {
                tm.add(game);
                //hopefully mark the id folder as delete on exit
                f = new File("./" + game.getId() + "/");
                f.deleteOnExit();
            }
            if(gameList.isUpdates())
            {
                tm.add(game.update());
              //hopefully mark the id folder as delete on exit
                f = new File("./" + game.update().getId() + "/");
                f.deleteOnExit();
            }
        }

        Map<Game, DownloadThread> completed = new HashMap<Game, DownloadThread>();

        while(completed.size() < tm.getFutures().size())
        {
            for (Map.Entry<Game,DownloadThread> entry : tm.getFutures().entrySet())
            {
                Game game = entry.getKey();
                DownloadThread dt = entry.getValue();

                if(dt.isDone() && (completed.get(game) == null))
                {
                    completed.put(game, dt);
                    ProcMon.ExitCode exit = ProcMon.ExitCode.ERROR;
                    try {
                        exit = dt.get();
                    } catch (ExecutionException e) {
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
                        deleteDirectory(new File("./" + game.getId() + "/"));
                    }
                }
            }
        }
        deleteDirectory(new File("./temp/"));
    }

    private boolean deleteDirectory(File directory)
    {
        if (directory.exists())
        {
            File[] files = directory.listFiles();
            if (null != files)
            {
                for (int i = 0; i < files.length; i++)
                {
                    if (files[i].isDirectory())
                    {
                        deleteDirectory(files[i]);
                        System.out.println("deleting " + files[i].getName());
                    }
                    else
                    {
                        files[i].delete();
                        System.out.println("deleting " + files[i].getName());
                    }
                }
            }
        }
        return (directory.delete());
    }
}
