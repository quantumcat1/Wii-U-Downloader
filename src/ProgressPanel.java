import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressPanel
{
    private JPanel panel;
    private JLabel label;
    private JProgressBar progress;

    public ProgressPanel()
    {
        initialise();
    }

    public ProgressPanel(String label)
    {
        initialise();
        this.label.setText(label);
    }

    private void initialise()
    {
        panel = new JPanel();
        label = new JLabel();
        progress = new JProgressBar(0, 100);
        panel.add(label);
        panel.add(progress);
    }

    public JPanel getPanel()
    {
        return panel;
    }

    public String getName()
    {
        return label.getText();
    }

    public void setName(String name)
    {
        label.setText(name);
    }

    public void addProgress(int progress)
    {
        this.progress.setValue(progress);
    }
}
