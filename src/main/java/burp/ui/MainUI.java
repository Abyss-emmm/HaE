package burp.ui;

import burp.yaml.Config;
import burp.yaml.template.Rules;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.util.Map;
import java.util.List;

/**
 * @author LinChen
 */

public class MainUI extends JPanel{
//    private final LoadConfig loadConn = new LoadConfig();
    private final Config config = Config.getInstance();
    private Map<String,Rules> ruleconfig = config.getRules();
    private PrintWriter stdout;

    public MainUI(PrintWriter stdout) {
        this.stdout = stdout;
        initComponents();
    }

    public void closeTabActionPerformed(ActionEvent e){
        if (tabbedPane1.getTabCount()>2){
            if (tabbedPane1.getSelectedIndex()!=0){
                ruleconfig.remove(tabbedPane1.getTitleAt(tabbedPane1.getSelectedIndex()));
                tabbedPane1.remove(tabbedPane1.getSelectedIndex());
                tabbedPane1.setSelectedIndex(tabbedPane1.getSelectedIndex()-1);
            }else{
                ruleconfig.remove(tabbedPane1.getTitleAt(tabbedPane1.getSelectedIndex()));
                tabbedPane1.remove(tabbedPane1.getSelectedIndex());
                tabbedPane1.setSelectedIndex(tabbedPane1.getSelectedIndex());
            }
            config.saveConfig();
        }
    }

    private void SelectFileMouseClicked(MouseEvent e) {
        JFileChooser selectFile = new JFileChooser();
        selectFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Yaml File (.yml/.yaml)","yaml", "yml");
        selectFile.setFileFilter(filter);
        int selectFrame = selectFile.showDialog(new JLabel(),"Select");
        if (selectFrame == JFileChooser.APPROVE_OPTION){
            String configPath = selectFile.getSelectedFile().toString();
            reloadRule();
            config.setConfigPath(configPath);
//            loadConn.setConfigPath(configPath);
            configFilepathtext.setText(configPath);
        }
    }

    private void reloadRule(){
        tabbedPane1.removeAll();
        ruleSwitch.setListen(false);
        Map<String,Rules> ruleConfig = Config.getInstance().getRules();
//        Map<String,Object[][]> rules = LoadConfig.getRules();
        for(Rules rules:ruleConfig.values()){
            tabbedPane1.addTab(rules.getType(),new RulePane(rules.getRulesObject(),tabbedPane1));
        }
        tabbedPane1.addTab("...", new JLabel());
        ruleSwitch.setListen(true);
    }

    private void reloadMouseClicked(MouseEvent e) {
        reloadRule();
    }

    private void ESSaveMouseClicked(MouseEvent e) {
        Config config = Config.getInstance();
        config.setExcludeSuffix(EStext.getText());
    }
    private void initComponents() {
        tabbedPane2 = new JTabbedPane();
        tabbedPane1 = new JTabbedPane();
        panel3 = new JPanel();
        configFilepathtext = new JTextField();
        label1 = new JLabel();
        SelectFile = new JButton();
        reload = new JButton();
        label2 = new JLabel();
        EStext = new JTextField();
        ESSave = new JButton();


        //======== this ========
        setLayout(new GridBagLayout());
        ((GridBagLayout)getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout)getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout)getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout)getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

        //======== tabbedPane2 ========
        {
            tabbedPane2.addTab("Rules", tabbedPane1);

            //======== panel3 ========
            {
                panel3.setLayout(new GridBagLayout());
                ((GridBagLayout)panel3.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                ((GridBagLayout)panel3.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout)panel3.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 1.0E-4};
                ((GridBagLayout)panel3.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

                //---- configFilepathtext ----
                configFilepathtext.setEditable(false);
                panel3.add(configFilepathtext, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(5, 0, 5, 5), 0, 0));

                //---- label1 ----
                label1.setText("Config File Path:");
                panel3.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                        new Insets(5, 5, 5, 5), 0, 0));

                //---- SelectFile ----
                SelectFile.setText("Select File ...");
                SelectFile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        SelectFileMouseClicked(e);
                    }
                });
                panel3.add(SelectFile, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(5, 0, 5, 5), 0, 0));

                //---- reload ----
                reload.setText("Reload");
                reload.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        reloadMouseClicked(e);
                    }
                });
                panel3.add(reload, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,

                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(5, 0, 5, 5), 0, 0));
//---- label2 ----
                label2.setText("ExcludeSuffix:");
                panel3.add(label2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
                        new Insets(0, 5, 5, 5), 0, 0));
                panel3.add(EStext, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                //---- ESSave ----
                ESSave.setText("Save");
                ESSave.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        ESSaveMouseClicked(e);
                    }
                });
                panel3.add(ESSave, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));
            }
            tabbedPane2.addTab("Config", panel3);
        }
        add(tabbedPane2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents

        Config config = Config.getInstance();
        Map<String,Rules> ruleConfig = config.getRules();
//        stdout.println(ruleConfig);
//        Rules rules = ruleConfig.get("Basic Information");
//        tabbedPane1.addTab(rules.getType(),new RulePane(rules.getRulesObject(),tabbedPane1));
        for(Map.Entry<String, Rules> entry:ruleConfig.entrySet()){
            Rules rules = entry.getValue();
            tabbedPane1.addTab(rules.getType(),new RulePane(rules.getRulesObject(),tabbedPane1));
        }
//        for (Rules rules:ruleConfig.values()){
//            tabbedPane1.addTab(rules.getType(),new RulePane(rules.getRulesObject(),tabbedPane1));
//        }

//        ruleConfig.forEach(rules->tabbedPane1.addTab(rules.getType(),new RulePane(rules.getRulesObject(),tabbedPane1)));

        tabbedPane1.addTab("...",new JLabel());

        //TabTitleEditListener ruleSwitch = new TabTitleEditListener(tabbedPane1);
        configFilepathtext.setText(config.getConfigPath());
        EStext.setText(config.getExcludeSuffix());
        ruleSwitch = new TabTitleEditListener(tabbedPane1);
        tabbedPane1.addChangeListener(ruleSwitch);
        tabbedPane1.addMouseListener(ruleSwitch);
        closeTab.addActionListener(e -> closeTabActionPerformed(e));
        tabMenu.add(closeTab);
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTabbedPane tabbedPane2;
    private JTabbedPane tabbedPane1;
    private JPanel panel3;
    private JTextField configFilepathtext;
    private JLabel label1;
    private JButton SelectFile;
    private JButton reload;
    private JLabel label2;
    private JTextField EStext;
    private JButton ESSave;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
    protected static JPopupMenu tabMenu = new JPopupMenu();
    private JMenuItem closeTab = new JMenuItem("Delete");
    private TabTitleEditListener ruleSwitch;
}

class TabTitleEditListener extends MouseAdapter implements ChangeListener, DocumentListener {
    protected final JTextField editor = new JTextField();
    protected final JTabbedPane tabbedPane;
    protected int editingIdx = -1;
    protected int len = -1;
    protected Boolean listen = true;
    protected Dimension dim;
    protected Component tabComponent;
    protected Boolean isRenameOk = false;
    private Config config = Config.getInstance();
    private Map<String,Rules> ruleConfig = config.getRules();
//    protected SetConfig setConfig = new SetConfig();
    protected final Action startEditing = new AbstractAction() {
        @Override public void actionPerformed(ActionEvent e) {
            editingIdx = tabbedPane.getSelectedIndex();
            tabComponent = tabbedPane.getTabComponentAt(editingIdx);
            tabbedPane.setTabComponentAt(editingIdx, editor);
            isRenameOk = true;
            editor.setVisible(true);
            editor.setText(tabbedPane.getTitleAt(editingIdx));
            editor.selectAll();
            editor.requestFocusInWindow();
            len = editor.getText().length();
            dim = editor.getPreferredSize();
            editor.setMinimumSize(dim);
        }
    };
    protected final Action renameTabTitle = new AbstractAction() {
        @Override public void actionPerformed(ActionEvent e) {
            String title = editor.getText().trim();
            if (editingIdx >= 0 && !title.isEmpty()) {
                String oldName = tabbedPane.getTitleAt(editingIdx);
                tabbedPane.setTitleAt(editingIdx, title);
                Rules rules = ruleConfig.get(oldName);
                rules.setType(title);
                ruleConfig.put(title,rules);
                ruleConfig.remove(oldName);
//                setConfig.rename(oldName,title);
            }
            cancelEditing.actionPerformed(null);
        }
    };
    protected final Action cancelEditing = new AbstractAction() {
        @Override public void actionPerformed(ActionEvent e) {
            if (editingIdx >= 0) {
                tabbedPane.setTabComponentAt(editingIdx, tabComponent);
                editor.setVisible(false);
                editingIdx = -1;
                len = -1;
                tabComponent = null;
                editor.setPreferredSize(null);
                tabbedPane.requestFocusInWindow();
            }
        }
    };

    protected TabTitleEditListener(JTabbedPane tabbedPane) {
        super();
        this.tabbedPane = tabbedPane;
        editor.setBorder(BorderFactory.createEmptyBorder());
        editor.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                renameTabTitle.actionPerformed(null);
            }
        });
        InputMap im = editor.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = editor.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel-editing");
        am.put("cancel-editing", cancelEditing);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "rename-tab-title");
        am.put("rename-tab-title", renameTabTitle);
        editor.getDocument().addDocumentListener(this);
        tabbedPane.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "start-editing");
        tabbedPane.getActionMap().put("start-editing", startEditing);
    }
    @Override public void stateChanged(ChangeEvent e) {
        if (e.getSource() instanceof JTabbedPane && listen) {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            if (!isRenameOk){
                if (pane.getSelectedIndex() == pane.getComponentCount()-1){
                    newTab();
                }
            }else{
                if (pane.getSelectedIndex() == pane.getComponentCount()-2){
                    newTab();
                }
            }
        }
        renameTabTitle.actionPerformed(null);
    }
    public void newTab(){
        Object[][] data = new Object[][]{{false, "New Name", "(New Regex)", "gray", "any", "nfa"}};
        insertTab(tabbedPane, config.getNewTitle(), data);
    }
    public void insertTab(JTabbedPane pane,String title,Object[][] data){
        pane.addTab(title,new RulePane(data,pane));
        pane.remove(pane.getSelectedIndex());
        pane.addTab("...",new JLabel());
    }
    public void setListen(Boolean listen){
        this.listen = listen;
    }
    @Override public void insertUpdate(DocumentEvent e) {
        updateTabSize();
    }

    @Override public void removeUpdate(DocumentEvent e) {
        updateTabSize();
    }

    @Override public void changedUpdate(DocumentEvent e) {}

    @Override public void mouseClicked(MouseEvent e) {
        switch (e.getButton()){
            case MouseEvent.BUTTON1://左键 2是滚轮，3是右键
            {
                Rectangle r = tabbedPane.getBoundsAt(tabbedPane.getSelectedIndex());
                boolean isDoubleClick = e.getClickCount() >= 2;
                if (isDoubleClick && r.contains(e.getPoint())) {
                    startEditing.actionPerformed(null);
                } else {
                    renameTabTitle.actionPerformed(null);
                }
                break;
            }
            case MouseEvent.BUTTON3:{
                MainUI.tabMenu.show(e.getComponent(),e.getX(),e.getY());
                break;
            }
            default:
                break;
        }
    }

    protected void updateTabSize() {
        editor.setPreferredSize(editor.getText().length() > len ? null : dim);
        tabbedPane.revalidate();
    }
}