
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class StatusWindow extends JPanel
{
    private static final long serialVersionUID = 3136350037469268319L;
    List<ProgressPanel> progressPanels;
    public void initialise()
    {
        progressPanels = new ArrayList<ProgressPanel>();
    }

    public StatusWindow()
    {
        initialise();
    }

    public ProgressPanel addNew(String name)
    {
        ProgressPanel pp = new ProgressPanel(name);
        progressPanels.add(pp);
        add(pp.getPanel());
        return pp;
    }

    public ProgressPanel getPanel(String name)
    {
        for(ProgressPanel pp : progressPanels)
        {
            if(pp.getName().equals(name))
            {
                return pp;
            }
        }
        return null;
    }

    public void addProgress(String name, int progress)
    {
        ProgressPanel pp = getPanel(name);
        pp.addProgress(progress);
    }
}
