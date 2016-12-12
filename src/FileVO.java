
public class FileVO
{
    private String id;
    private int size;

    public FileVO(String id, int size)
    {
        this.id = id;
        this.size = size;
    }

    public FileVO()
    {
        id = "";
        size = 0;
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

    public void setSize(int size)
    {
        this.size = size;
    }


}
