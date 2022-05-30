package burp;

import burp.action.*;
import burp.ui.MainUI;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author EvilChen & 0chencc
 */

public class BurpExtender implements IBurpExtender, IHttpListener, IMessageEditorTabFactory, ITab {
    private  MainUI main;
    private static PrintWriter stdout;
    private IBurpExtenderCallbacks callbacks;
    private static IExtensionHelpers helpers;
    GetColorKey gck = new GetColorKey();
    UpgradeColor uc = new UpgradeColor();
    ProcessMessage pm = new ProcessMessage();

    @Override
    public void registerExtenderCallbacks(final IBurpExtenderCallbacks callbacks)
    {
        this.callbacks = callbacks;
        BurpExtender.helpers = callbacks.getHelpers();
        callbacks.setExtensionName("HaE");
        // 定义输出
        stdout = new PrintWriter(callbacks.getStdout(), true);
        stdout.println("@Core Author: EvilChen");
        stdout.println("@Architecture Author: 0chencc");
        stdout.println("@Github: https://github.com/gh0stkey/HaE");

        this.main = new MainUI(stdout);

        callbacks.addSuiteTab(BurpExtender.this);
        callbacks.customizeUiComponent(main);
        callbacks.registerHttpListener(BurpExtender.this);
        callbacks.registerMessageEditorTabFactory(BurpExtender.this);
    }


    @Override
    public String getTabCaption(){
        return "HaE";
    }

    @Override
    public Component getUiComponent() {
        return main;
    }

    /**
     * 使用processHttpMessage用来做Highlighter
     */
    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
        // 判断是否是响应，且该代码作用域为：REPEATER、INTRUDER、PROXY（分别对应toolFlag 64、32、4）
        if (toolFlag == 64 || toolFlag == 32 || toolFlag == 4) {
            byte[] content;
            if(messageIsRequest){
                content = messageInfo.getRequest();
            }else{
                content = messageInfo.getResponse();
            }
            Map<String,Map<String,List<String>>> result = pm.processMessageByContent(helpers, content, messageIsRequest, true/*,stdout*/);
            if (result != null && !result.isEmpty() && result.size() > 0) {
                String originalColor = messageInfo.getHighlight();
                String originalComment = messageInfo.getComment();
                List<String> colorList = new ArrayList<>();
                if (originalColor != null) {
                    colorList.add(originalColor);
                }
                colorList.add(result.get(ProcessMessage.HighLight_Comment_key).get("color").get(0));
                String color = uc.getEndColor(gck.getColorKeys(colorList));

                messageInfo.setHighlight(color);
                String addComment = result.get(ProcessMessage.HighLight_Comment_key).get("comment").get(0).replaceAll(":,",":").replaceAll(", ;",";");
                String resComment = originalComment != null ? String.format("%s, %s", originalComment, addComment) : addComment;

                messageInfo.setComment(resComment);
            }
        }
    }

    @Override
    public IMessageEditorTab createNewInstance(IMessageEditorController controller, boolean editable) {
        return new MarkInfoTab(controller, editable,stdout);
    }

    class MarkInfoTab implements IMessageEditorTab {
        private JTabbedPane jTabbedPane;
        private final IMessageEditorController controller;
        Map<String,Map<String,List<String>>> extractdata;
        private PrintWriter stdout;
        private byte[] default_content;

        public MarkInfoTab(IMessageEditorController controller, boolean editable,PrintWriter stdout) {
            this.controller = controller;
            this.stdout = stdout;
            this.jTabbedPane = new JTabbedPane();
            this.extractdata = new HashMap<>();
        }

        @Override
        public String getTabCaption() {
            return "HaE";
        }

        @Override
        public Component getUiComponent() {
            callbacks.customizeUiComponent(this.jTabbedPane);
            return this.jTabbedPane;
        }

        @Override
        public boolean isEnabled(byte[] content, boolean isRequest) {
            if(content==null || content.length == 0 ){
                return false;
            }
            this.default_content = content;
           return true;
        }

        @Override
        public byte[] getMessage() {
            return this.default_content;
        }

        @Override
        public boolean isModified() {
            return false;
        }

        /**
         * 快捷键复制功能
         */
        @Override
        public byte[] getSelectedData() {
            if(this.jTabbedPane.getTabCount()>0){
                JTable jTable = (JTable) ((JScrollPane)jTabbedPane.getSelectedComponent()).getViewport().getView();
                int[] selectRows = jTable.getSelectedRows();
                StringBuilder selectData = new StringBuilder();
                for (int row : selectRows) {
                    selectData.append(jTable.getValueAt(row, 0).toString()).append("\n");
                }
                byte[] content = helpers.stringToBytes(selectData.toString());
                return Arrays.copyOfRange(content,0,content.length-1);
            }
            return  null;
        }

        /**
         * 使用setMessage用来做Extractor
         */
        @Override
        public void setMessage(byte[] content, boolean isRequest) {
            this.default_content = content;
            this.jTabbedPane.removeAll();
            if(content !=null && content.length != 0 ){
                extractdata = pm.processMessageByContent(helpers, content, isRequest, false);
                if (extractdata.size()>0){
                    makeTable();
                }
            }
        }

        /**
         * 创建MarkInfo表单
         */
        public void makeTable() {
            extractdata.keySet().forEach(rules_type->{
                Map<String,List<String>>rulesdata = extractdata.get(rules_type);
                ArrayList<String>tmpList = new ArrayList<>();
                for (String rule_name : rulesdata.keySet()){
                    List<String> data = rulesdata.get(rule_name);
                    tmpList.addAll(data);
                }
                Object[][] data = new Object[tmpList.size()][1];
                for(int i=0;i<tmpList.size();i++){
                    data[i][0] = tmpList.get(i);
                }
                JScrollPane jScrollPane = new JScrollPane(new JTable(data, new Object[] {"Information"}));
//                int indexOfTab = this.jTabbedPane.indexOfTab(rules_type);
                this.jTabbedPane.addTab(rules_type, jScrollPane);
//                 使用removeAll会导致UI出现空白的情况，为了改善用户侧体验，采用remove的方式进行删除
//                if (indexOfTab != -1) {
//                    this.jTabbedPane.remove(indexOfTab);
//                }
            });
        }
    }

}