import java.util.ArrayList;

public class GameVO
{
    private String id;
    //private int size;
    private String sizeStr;
    private String title;
    private ArrayList<FileVO> files;


    public GameVO()
    {
        id = "";
        sizeStr = "";
        files = new ArrayList<FileVO>();
    }

    public GameVO(String title, String id, ArrayList<FileVO> files)
    {
        this.title = title;
        this.id = id;
        this.files = files;
        this.sizeStr = convertSizeStr(getSize());
    }

    public GameVO(String title, String id)
    {
        this.title = title;
        this.id = id;
        files = new ArrayList<FileVO>();
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
        int size = 0;
        for(FileVO file : files)
        {
            size += file.getSize();
        }
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

    public String getSizeStr()
    {
        return sizeStr;
    }

    public void setFiles(ArrayList<FileVO> files)
    {
        this.files = files;
    }

    public ArrayList<FileVO> getFiles()
    {
        return files;
    }

}

