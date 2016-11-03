

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GameList
{
	public enum Sort {ALPHA, UP, DOWN};
	private ArrayList<Game> list;
	private ArrayList<Game> selectedList;
	private boolean updates;
	public GameList() throws IOException
	{
		list = new ArrayList<Game>();
		selectedList = new ArrayList<Game>();
		updates = false;
		setList();
	}

	private void setList() throws IOException
	{
		list.clear();
		System.out.println(new File(".").getCanonicalPath());
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
					list.add(new Game(file.getName(), size, id));
				}
			}
		}
		selectedList.add(new Game ("test", 0, "1337"));
	}

	public ArrayList<Game> getList()
	{
		return list;
	}

	public ArrayList<Game> getSelectedList()
	{
		return selectedList;
	}

	public void setUpdates(boolean updates)
	{
		this.updates = updates;
	}

	public boolean isUpdates()
	{
		return updates;
	}

	public void setSelection(int[] rows)
	{
		selectedList.clear();
		for(int i = 0; i < rows.length; i ++)
		{
			selectedList.add(list.get(rows[i]));
		}
	}

	public void setSelectedList(ArrayList<Game> selectedList)
	{
		this.selectedList = selectedList;
	}

	public void removeSelection(int i)
	{
		Game game = list.get(i);
		int j = 0;
		for(Game g : selectedList)
		{
			if(g.getTitle().equals(game.getTitle()))
			{
				selectedList.remove(j);
				break;
			}
			j ++;
		}
	}

	public void sortList(Sort s)
	{
		if(s == Sort.ALPHA)
		{
			Collections.sort(list, sortAlpha());
		}
		else if(s == Sort.UP)
		{
			Collections.sort(list, sortUp());
		}
		else
		{
			Collections.sort(list, sortDown());
		}
	}

	public static Comparator<Game> sortAlpha()
	{
		Comparator<Game> comp = new Comparator<Game>()
		{
			@Override
			public int compare(Game g1, Game g2)
			{
				return g1.getTitle().compareTo(g2.getTitle());
			}
		};
		return comp;
	}

	public static Comparator<Game> sortUp()
	{
		Comparator<Game> comp = new Comparator<Game>()
		{
			@Override
			public int compare(Game g1, Game g2)
			{
				return g1.getSize() - g2.getSize();
			}
		};
		return comp;
	}

	public static Comparator<Game> sortDown()
	{
		Comparator<Game> comp = new Comparator<Game>()
		{
			@Override
			public int compare(Game g1, Game g2)
			{
				return g2.getSize() - g1.getSize();
			}
		};
		return comp;
	}
}
