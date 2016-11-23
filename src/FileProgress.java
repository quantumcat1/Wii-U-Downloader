
public class FileProgress
{
    private String name;
    private int progress;

    public FileProgress()
    {
        name = "";
        progress = 0;
    }

    public FileProgress(String name)
    {
        this.name = name;
    }

    public FileProgress(String name, int progress)
    {
        this.name = name;
        this.progress = progress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

}
