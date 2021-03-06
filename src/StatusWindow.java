
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Scrollable;

public class StatusWindow extends JPanel //simplements Scrollable
{
    private static final long serialVersionUID = 3136350037469268319L;
    List<ProgressPanel> progressPanels;
    //JLabel label;
    public void initialise()
    {
        //label = new JLabel("0%");
        //add(label);
        progressPanels = new ArrayList<ProgressPanel>();
    }

    public StatusWindow()
    {
        initialise();
    }

    public ProgressPanel addNew(String name)
    {
        if(name.trim().equals("")) return null;
        ProgressPanel pp = new ProgressPanel(name);
        progressPanels.add(pp);
        add(pp.getPanel());
        revalidate();
        repaint();
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
