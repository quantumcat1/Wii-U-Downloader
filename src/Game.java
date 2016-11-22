
public class Game
{
    private String title;
    private int size;
    private String size_str;
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
    public Game (String title, int size, String size_str, String id)
    {
        this.title = title;
        this.size = size;
        this.size_str = size_str;
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

    public void setSize(int size)
    {
        this.size = size;
    }

    public void setSizeStr(String size_str)
    {
        this.size_str = size_str;
    }

    public String getSizeStr()
    {
        return size_str;
    }

    public String getId()
    {
        return id;
    }
    public Game update()
    {
        return new Game(title + " Update", this.size, id.replaceAll("00050000", "0005000E"));
    }
}
