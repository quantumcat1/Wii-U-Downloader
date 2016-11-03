
import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class DownloadThread extends SwingWorker<Void, String> //Thread
{
	private ProcMon procMon;
	//BufferedReader br;
	private InputStream is;
	private JTextArea statusLabel = null;
	private List<String> textContents;
	public int exitCode = -1;
	private String game;

	DownloadThread(Process proc, String game)//, JTextArea statusLabel)
	{
		this.game = game;
		textContents = new ArrayList<String>();
		//this.statusLabel = statusLabel;
		procMon = ProcMon.create(proc);
		//is = proc.getInputStream();
		try {
			is = new FileInputStream("./" + game + "_log.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Void doInBackground()
	{
		while(!procMon.isComplete())
		{
			try
			{
				int i = 0;
				final StringBuilder s = new StringBuilder();
				i = is.read();
				while(i != -1)
				{
					s.append((char)i);
					if(i == '\n')
					{
						if(s.toString().contains(".app") || s.toString().contains(".h3"))
						{
							publish(s.toString());
						}
						else if(s.toString().contains("10%") || s.toString().contains("20%") || s.toString().contains("30%") || s.toString().contains("40%") || s.toString().contains("50%") || s.toString().contains("60%") || s.toString().contains("70%") || s.toString().contains("80%") || s.toString().contains("90%"))
						{
							publish(s.toString());
						}
						s.setLength(0);
					}
					i = is.read();
				}
				//get last line case it doesn't end with a newline character
				publish(s.toString());
			}
			catch (IOException ioe) //| InterruptedException ioe)
			{
				ioe.printStackTrace();
			}
		}
		exitCode = procMon.getProcess().exitValue();
		return null;
	}
	protected void process(List<String> chunks)
	{
		if(statusLabel == null)
		{
			JFrame frame = new JFrame(game + " Status");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setPreferredSize(new Dimension(400, 600));

	        StatusWindow newContentPane = new StatusWindow(game);
	        newContentPane.setLayout(new BoxLayout(newContentPane, BoxLayout.PAGE_AXIS));
	        newContentPane.setOpaque(true);
	        frame.setContentPane(newContentPane);

	        frame.setLocationRelativeTo(null);
	        frame.pack();
	        frame.setVisible(true);

			statusLabel = newContentPane.statusLabel;
		}
		for(String s : chunks)
		{
			if(s.contains(".app") || s.contains(".h3"))
			{
				textContents.add(s);
			}
			else if(s.contains("%"))
			{
				if(textContents.get(textContents.size()-1).contains("%"))
				{
					textContents.set(textContents.size()-1, s);
				}
				else
				{
					textContents.add(s);
				}
			}
		}
		String contents = "";
		for(int i = 0; i < textContents.size(); i++)
		{
			contents += textContents.get(i) + "\n";
		}
		statusLabel.setText(contents);
	}
	@Override
	public void done()
	{
		if(exitCode > -1)
		{
			statusLabel.append("~~~~~~~Finished~~~~~~~\n");
		}
		else
		{
			statusLabel.append("~~~~~~~Error~~~~~~~\n");
		}
	}
}


