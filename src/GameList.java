

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

    private String convertSize(int size)
    {
        String r_size = size + " B";
        float gb = size/1073741824;
        if(gb < 1)
        {
            float mb = size/1048576;
            if(mb < 1)
            {
                float kb = size/1024;
                if(kb > 1)
                {
                    r_size = String.format("%.2f", kb) + " KB";
                }
            }
            else
            {
                r_size = String.format("%.2f", mb) + " MB";
            }
        }
        else
        {
            r_size = String.format("%.2f", gb) + " GB";
        }
        return r_size;
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
                    list.add(new Game(file.getName(), 0, "?", id));
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
            HttpEntity entity = response.getEntity();
            responseString = EntityUtils.toString(entity, "UTF-8");
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
                    game.setSizeStr(obj.getString("size_str"));
                    game.setSize(obj.getInt("size"));
                }
                else
                {
                    game = new Game(obj.getString("name"), obj.getInt("size"), obj.getString("size_str"), obj.getString("titleid"));
                    list.add(game);
                }
            }
        }
    }

    public Game getById(String id)
    {
        for(Game game : list)
        {
            if(game.getId() == id)
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
