
public class Game
{
    private GameVO game;
    private GameVO update;

    public Game(String title, String id)
    {
        if(id.contains("0005000E"))
        {
            game = new GameVO(title, id.replaceAll("0005000E", "00050000"));
            update = new GameVO(title + " Update", id);
        }
        else
        {
            game = new GameVO(title, id);
            update = new GameVO(title + " Update", id.replaceAll("00050000", "0005000E"));
        }
    }

    public Game(String title, String id, int size)
    {
        title.replaceAll(" Update", "");
        if(id.contains("0005000E"))
        {
            game = new GameVO(title, id.replaceAll("0005000E", "00050000"), size);
            update = new GameVO(title + " Update", id, size);
        }
        else
        {
            game = new GameVO(title, id, size);
            update = new GameVO(title + " Update", id.replaceAll("00050000", "0005000E"), size);
        }
    }

    public void setGameSize(int size)
    {
        game.setSize(size);
    }

    public void setUpdateSize(int size)
    {
        update.setSize(size);
    }

    public void setSize(String id, int size)
    {
        if(id.contains("0005000E"))
        {
            update.setSize(size);
        }
        else
        {
            game.setSize(size);
        }
    }

    public int getGameSize()
    {
        return game.getSize();
    }

    public int getUpdateSize()
    {
        return update.getSize();
    }

    public String getGameSizeStr()
    {
        return game.getSizeStr();
    }

    public String getUpdateSizeStr()
    {
        return update.getSizeStr();
    }

    public String getGameId()
    {
        return game.getId();
    }

    public String getUpdateId()
    {
        return update.getId();
    }

    public void setTitle(String id, String title)
    {
        title = title.replaceAll(" Update", "");
        if(id.contains("0005000E"))
        {
            update.setTitle(title + " Update");
        }
        else
        {
            game.setTitle(title);
        }
    }

    public String getGameTitle()
    {
        return game.getTitle();
    }

    public String getUpdateTitle()
    {
        return update.getTitle();
    }

    public GameVO getGame()
    {
        return game;
    }

    public GameVO getUpdate()
    {
        return update;
    }

    public boolean isMatchId(String id)
    {
        if(game.getId().equals(id) || update.getId().equals(id))
        {
            return true;
        }
        return false;
    }
}
