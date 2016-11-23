
public class GameVO
{
    private String id;
    private int size;
    private String sizeStr;
    private String title;


    public GameVO()
    {
        id = "";
        size = 0;
        sizeStr = "";
    }

    public GameVO(String title, String id, int size)
    {
        this.title = title;
        this.id = id;
        this.size = size;
        this.sizeStr = convertSizeStr(size);
    }

    public GameVO(String title, String id)
    {
        this.title = title;
        this.id = id;
        size = 0;
    }

    private String convertSizeStr(int size)
    {
        String r_size = size + " B";
        float gb = (float)size/1073741824;
        if(gb < 1)
        {
            float mb = (float)size/1048576;
            if(mb < 1)
            {
                float kb = (float)size/1024;
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


    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    public int getSize()
    {
        return size;
    }
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public void setSize(int size)
    {
        this.size = size;
        sizeStr = convertSizeStr(size);
    }
    public String getSizeStr()
    {
        return sizeStr;
    }

}

