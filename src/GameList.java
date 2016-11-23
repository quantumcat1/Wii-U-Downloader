

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class GameList
{
    public enum Sort {ALPHA, UP, DOWN};
    private ArrayList<Game> list;
    private ArrayList<Game> selectedList;
    private boolean updates;
    private boolean game;
    public GameList() throws IOException
    {
        list = new ArrayList<Game>();
        selectedList = new ArrayList<Game>();
        updates = false;
        game = false;
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
                String id = "";
                for(File innerFile : innerFiles)
                {
                    if(innerFile.isDirectory())
                    {
                        numDirectories += 1;
                        id = innerFile.getName();
                    }
                }
                if(numDirectories > 0)
                {
                    list.add(new Game(file.getName(), id));
                }
            }
        }
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("http://quantumc.at/getGameSize.php");
        post.addHeader("Content-type", "application/json");
        HttpResponse response = null;
        String responseString = "";
        boolean connected = true;
        try
        {
            response = httpClient.execute(post);
            responseString = EntityUtils.toString(response.getEntity());
        }
        catch (Exception e)
        {
            connected = false;
            e.printStackTrace();
        }
        if(connected)
        {
            JSONArray arr = new JSONArray(responseString);
            httpClient.close();

            for(int i = 0; i < arr.length(); i ++)
            {
                JSONObject obj = arr.getJSONObject(i);
                Game game = getById(obj.getString("titleid"));
                if(game != null)
                {
                    game.setSize(obj.getString("titleid"), obj.getInt("size"));
                    game.setTitle(obj.getString("titleid"), obj.getString("name"));
                }
                else
                {
                    list.add(new Game(obj.getString("name"), obj.getString("titleid"), obj.getInt("size")));
                }
            }
        }
    }

    public Game getById(String id)
    {
        for(Game game : list)
        {
            if(game.isMatchId(id))
            {
                return game;
            }
        }
        return null;
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
    public void setGame(boolean game)
    {
        this.game = game;
    }

    public boolean isGame()
    {
        return game;
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
            if(game.isMatchId(g.getGameId()))
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
                return g1.getGameTitle().compareTo(g2.getGameTitle());
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
                return g1.getGameSize() - g2.getGameSize();
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
                return g2.getGameSize() - g1.getGameSize();
            }
        };
        return comp;
    }
}
