
public class Game
{
	private String title;
	private int size;
	private String id;

	public Game()
	{
		title = "";
		size = 0;
		id = "";
	}
	public Game (String title)
	{
		this.title = title;
		size = 0;
		id = "";
	}
	public Game (String title, int size)
	{
		this.title = title;
		this.size = size;
		id = "";
	}
	public Game (String title, int size, String id)
	{
		this.title = title;
		this.size = size;
		this.id = id;
	}

	public String getTitle()
	{
		return title;
	}

	public int getSize()
	{
		return size;
	}

	public String getId()
	{
		return id;
	}
}
