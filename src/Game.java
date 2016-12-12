import java.util.ArrayList;

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

    public Game(String title, String id, ArrayList<FileVO> files)
    {
        title.replaceAll(" Update", "");
        if(id.contains("0005000E"))
        {
            game = new GameVO(title, id.replaceAll("0005000E", "00050000"), files);
            update = new GameVO(title + " Update", id, files);
        }
        else
        {
            game = new GameVO(title, id, files);
            update = new GameVO(title + " Update", id.replaceAll("00050000", "0005000E"), files);
        }
    }

    public void setGameFiles(ArrayList<FileVO> files)
    {
        game.setFiles(files);
    }

    public void setUpdateSize(ArrayList<FileVO> files)
    {
        update.setFiles(files);
    }

    public void setSize(String id, ArrayList<FileVO> files)
    {
        if(id.contains("0005000E"))
        {
            update.setFiles(files);
        }
        else
        {
            game.setFiles(files);
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
