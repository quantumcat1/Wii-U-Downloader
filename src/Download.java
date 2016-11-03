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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JTextArea;

public class Download
{
	public JTextArea statusLabel;
	public Download(JTextArea statusLabel)
	{
		this.statusLabel = statusLabel;
	}

	private static void copyFile(String pathIn, String pathOut) throws IOException
    {
        File infile = new File(pathIn);
        File outfile = new File(pathOut);

        if(infile.isDirectory())
        {
        	File[] files = infile.listFiles();
        	for(File f : files)
        	{
        		copyFile(f.getPath().toString(), pathOut);
        	}
        }

        if(outfile.isDirectory())
        {
        	File[] files = outfile.listFiles();
        	for(File f : files)
        	{
        		copyFile(pathIn, f.getPath().toString());
        	}
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

	public void download(GameList gameList) throws IOException //throws IOException, InterruptedException
, InterruptedException
	{
		//move nusgrabber into temp folder
		extractZip("Nusgrabber.zip");

		copyFile("./temp/Nusgrabber/NUSgrabber.exe", "./NUSgrabber.exe");
		copyFile("./temp/Nusgrabber/vcruntime140.dll", "./vcruntime140.dll");
		copyFile("./temp/Nusgrabber/wget.exe", "./wget.exe");

		//make install folder
		File f = new File("./install/");
		if(!f.exists())
		{
			f.mkdir();
		}
		//now to download the games
		for(Game game : gameList.getSelectedList())
		{
			String name = game.getTitle() + " Update";
			String updateId = game.getId().replaceAll("00050000", "0005000E");

			ProcessBuilder pb2 = new ProcessBuilder("NUSgrabber.exe", updateId);
			ProcessBuilder pb1 = new ProcessBuilder("NUSgrabber.exe", game.getId());

			pb1.redirectErrorStream(true);
			pb2.redirectErrorStream(true);

			pb1.redirectOutput(new File("./" + game.getTitle() + "_log.txt"));


			Process process_game = pb1.start();
			DownloadThread dt_game = new DownloadThread(process_game, game.getTitle());
			DownloadThread dt_update = null;
			dt_game.execute();

			if(gameList.isUpdates())
			{
				pb2.redirectOutput(new File("./" + name + "_log.txt"));
				Process process_update = pb2.start();
				dt_update = new DownloadThread(process_update, name);
				dt_update.execute();
			}

			while(!dt_game.isDone() || (!dt_update.isDone() && dt_update != null)){
				//let them finish before moving on
			}

			int exitVal = dt_game.exitCode;

			if(exitVal > -1)
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

				copyFile("./tickets/" + game.getTitle() + "/" + game.getId() + "/title.tik", "./" + game.getId() + "/title.tik");
				copyFile("./" + game.getId() + "/", "./install/" + game.getTitle() + "/");
				deleteDirectory(new File("./" + game.getId() + "/"));
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
