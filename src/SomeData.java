import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class SomeData
{
	private ArrayList<Game> x;
	private ArrayList<Game> y;


	public SomeData () throws IOException
	{
		x = new ArrayList<Game>();
		y = new ArrayList<Game>();



		setList();
	}

	private void setList() throws IOException
	{
		x.clear();
		File[] files = new File("./tickets").listFiles();

		for(File file: files)
		{
			if(file.isDirectory())
			{
				File[] innerFiles = file.listFiles();
				int numDirectories = 0;
				int size = 0;
				String id = "";
				for(File innerFile : innerFiles)
				{
					if(innerFile.isDirectory())
					{
						numDirectories += 1;
						id = innerFile.getName();
					}
					else if (innerFile.getName().equals("size.txt"))
					{
						BufferedReader br = new BufferedReader(new FileReader(innerFile));
						String data = br.readLine();
						try
						{
							size = Integer.parseInt(data);
						}
						catch (NumberFormatException e)
						{
							System.out.println("game = " + innerFile.getName() + " size = " + data);
							e.printStackTrace();
						}
						br.close();
					}
				}
				if(numDirectories > 0)
				{
					x.add(new Game(file.getName(), size, id));
				}
			}
		}
	}

	public void setSelection(int[] rows)
	{
		y.clear();
		for(int i = 0; i < rows.length; i ++)
		{
			y.add(x.get(rows[i]));
		}
	}

	public ArrayList<Game> getX ()
	{
		return x;
	}

	public ArrayList<Game> getY ()
	{
		return y;
	}
}
