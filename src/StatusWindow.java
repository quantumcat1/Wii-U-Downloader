
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class StatusWindow extends JPanel
{
    /**
     *
     */
    private static final long serialVersionUID = 3136350037469268319L;
    public JTextArea statusLabel;
    public void initialise()
    {
        statusLabel = new JTextArea();
        add(new JScrollPane(statusLabel));
    }

    public StatusWindow()
    {
        initialise();
    }

}
