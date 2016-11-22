
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

public class MainWindow extends JPanel implements ActionListener, ItemListener
{
    /**
     *
     */
    private static final long serialVersionUID = 1340313595349448384L;

    private JTable gameTable;
    private JButton btnGo;
    private JTextArea descLabel;
    private static GameList gameList;
    private JRadioButton alphabeticalRadio;
    private JRadioButton sizeDownRadio;
    private JRadioButton sizeUpRadio;
    private JCheckBox updatesCheck;
    private JCheckBox gameCheck;
    public JTextArea statusLabel;
    private JPanel radioPane;
    private JPanel checkPane;
    private JPanel downloadPane;
    private JComboBox<String> threadCombo;
    private int threads;
    private static Download download;

    public void initialise() throws IOException
    {
        threads = 2;
        gameList = new GameList();
        radioPane = new JPanel();
        checkPane = new JPanel();
        downloadPane = new JPanel();

        radioPane.setLayout(new BoxLayout(radioPane, BoxLayout.LINE_AXIS));
        checkPane.setLayout(new BoxLayout(checkPane, BoxLayout.LINE_AXIS));
        downloadPane.setLayout(new BoxLayout(downloadPane, BoxLayout.LINE_AXIS));

        statusLabel = new JTextArea();
        statusLabel.setMaximumSize(new Dimension(900, 100));
        //statusLabel.setMaximumSize(new Dimension(500, 100));
        statusLabel.setMinimumSize(new Dimension(500,100));
        statusLabel.setLineWrap(true);
        statusLabel.setEditable(false);
        statusLabel.setWrapStyleWord(true);


        descLabel = new JTextArea("Select the games you want to download (hold Ctrl while clicking to select multiple):");
        descLabel.setLineWrap(true);
        descLabel.setEditable(false);
        descLabel.setWrapStyleWord(true);
        Font labelFont = descLabel.getFont();
        descLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 24));
        descLabel.setMaximumSize(new Dimension(900, 100));
        descLabel.setMinimumSize(new Dimension(500,100));
        add(descLabel);

        JLabel blankLabel1 = new JLabel("   ");
        blankLabel1.setMinimumSize(new Dimension(30, 30));
        add(blankLabel1);

        gameTable = new JTable();

        setTableRows();

        gameTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                gameList.setSelection(gameTable.getSelectedRows());
                update();
                Download.cleanUp(gameList);
            }
        });


        gameTable.setFont(new Font(labelFont.getName(), Font.PLAIN, 16));
        gameTable.setMaximumSize(new Dimension(500, 30));
        gameTable.setMinimumSize(new Dimension(500, 30));
        gameTable.setRowSelectionAllowed(true);
        gameTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        gameTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        //add(gameTable);

        JScrollPane scrollPane = new JScrollPane(gameTable);
        add(scrollPane);

        JLabel blankLabel3 = new JLabel("   ");
        blankLabel3.setMinimumSize(new Dimension(30, 30));
        add(blankLabel3);

        alphabeticalRadio = new JRadioButton("alpha");
        sizeDownRadio = new JRadioButton("down");
        sizeUpRadio = new JRadioButton("up");

        alphabeticalRadio.setActionCommand("alpha");
        sizeDownRadio.setActionCommand("down");
        sizeUpRadio.setActionCommand("up");

        alphabeticalRadio.setText("Sort alphabetically");
        sizeDownRadio.setText("Sort by size (descending)");
        sizeUpRadio.setText("Sort by size (ascending)");

        ButtonGroup bg = new ButtonGroup();
        bg.add(alphabeticalRadio);
        bg.add(sizeDownRadio);
        bg.add(sizeUpRadio);

        alphabeticalRadio.addActionListener(this);
        sizeDownRadio.addActionListener(this);
        sizeUpRadio.addActionListener(this);

        alphabeticalRadio.setSelected(true);

        radioPane.add(alphabeticalRadio);
        radioPane.add(sizeDownRadio);
        radioPane.add(sizeUpRadio);

        add(radioPane);

        updatesCheck = new JCheckBox("updates");
        updatesCheck.setActionCommand("updates");
        updatesCheck.setText("Get update?");
        updatesCheck.addActionListener(this);

        checkPane.add(updatesCheck);

        gameCheck = new JCheckBox("game");
        gameCheck.setActionCommand("game");
        gameCheck.setText("Get game?");
        gameCheck.addActionListener(this);
        gameList.setGame(true);

        checkPane.add(gameCheck);

        gameCheck.setSelected(true);

        add(checkPane);

        String[] threads = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        threadCombo = new JComboBox<String>(threads);
        threadCombo.setSelectedIndex(1);
        threadCombo.setMaximumSize(new Dimension(40, 40));
        threadCombo.setMinimumSize(new Dimension(40, 40));
        threadCombo.addItemListener(this);

        JLabel text = new JLabel("Use ", SwingConstants.RIGHT);
        text.setMaximumSize(new Dimension(40, 40));
        text.setMinimumSize(new Dimension(40, 40));
        JLabel text2 = new JLabel(" threads");
        text2.setMaximumSize(new Dimension(80, 40));
        text2.setMinimumSize(new Dimension(80, 40));

        downloadPane.add(text);
        downloadPane.add(threadCombo);
        downloadPane.add(text2);

        btnGo = new JButton("Download");
        btnGo.setActionCommand("go");
        btnGo.addActionListener(this);
        btnGo.setMaximumSize(new Dimension(100, 40));
        btnGo.setMinimumSize(new Dimension(100, 40));
        downloadPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        downloadPane.add(btnGo);

        add(downloadPane);

        add(new JScrollPane(statusLabel));

        update();
    }

    private void setTableRows()
    {
        DefaultTableModel dtm = new DefaultTableModel(0, 0);
        String[] header = new String[]{"Name", "Size"};
        dtm.setColumnIdentifiers(header);
        for(Game game : gameList.getList())
        {
            String sizeColumn = game.getSizeStr();
            dtm.addRow(new Object[]{game.getTitle(), sizeColumn});
        }
        gameTable.setModel(dtm);
    }

    private void update() {

        adjustJTableRowSizes(gameTable);
        for (int i = 0; i < gameTable.getColumnCount(); i++) {
            adjustColumnSizes(gameTable, i, 2);
        }
    }
    private void adjustJTableRowSizes(JTable jTable) {
        for (int row = 0; row < jTable.getRowCount(); row++) {
            int maxHeight = 0;
            for (int column = 0; column < jTable.getColumnCount(); column++) {
                TableCellRenderer cellRenderer = jTable.getCellRenderer(row, column);
                Object valueAt = jTable.getValueAt(row, column);
                Component tableCellRendererComponent = cellRenderer.getTableCellRendererComponent(jTable, valueAt, false, false, row, column);
                int heightPreferable = tableCellRendererComponent.getPreferredSize().height;
                maxHeight = Math.max(heightPreferable, maxHeight);
            }
            jTable.setRowHeight(row, maxHeight);
        }

    }
    public void adjustColumnSizes(JTable table, int column, int margin) {
        DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
        TableColumn col = colModel.getColumn(column);
        int width;

        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }
        Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
        width = comp.getPreferredSize().width;

        for (int r = 0; r < table.getRowCount(); r++) {
            renderer = table.getCellRenderer(r, column);
            comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, column), false, false, r, column);
            int currentWidth = comp.getPreferredSize().width;
            width = Math.max(width, currentWidth);
        }

        width += 2 * margin;

        col.setPreferredWidth(width);
        col.setWidth(width);
    }

    public MainWindow() throws IOException
    {
        initialise();
    }

    private static void createWindow() throws IOException
    {
        JFrame frame = new JFrame("Download games");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(500, 800));

        MainWindow newContentPane = new MainWindow();
        newContentPane.setLayout(new BoxLayout(newContentPane, BoxLayout.PAGE_AXIS));
        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);

        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to quit downloading?", "Quit?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
                {
                    if(download != null)
                    {
                        download.cancel();
                        download.cleanUp(gameList);
                    }
                    System.exit(0);
                }
            }
        });
    }

    public static void main(String[] args) throws IOException
    {
        createWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        switch(e.getActionCommand())
        {
            case "alpha":
                gameList.sortList(GameList.Sort.ALPHA);
                setTableRows();
                update();
                break;
            case "up":
                gameList.sortList(GameList.Sort.UP);
                setTableRows();
                update();
                break;
            case "down":
                gameList.sortList(GameList.Sort.DOWN);
                setTableRows();
                update();
                break;
            case "updates":
                gameList.setUpdates(updatesCheck.isSelected());
                break;
            case "game":
                gameList.setGame(gameCheck.isSelected());
                break;
            case "go":
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        download = new Download(statusLabel, threads, gameList);

                        try {
                            download.download();
                        } catch (IOException | InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }.start();
            break;
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e)
    {
        threads = threadCombo.getSelectedIndex() + 1;
    }
}
