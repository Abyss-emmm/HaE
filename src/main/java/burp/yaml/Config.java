package burp.yaml;

import burp.yaml.template.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.nodes.Tag;

public class Config {
    private Yaml yaml;
    private String configdir = "";
    private String SettingPath = "Setting.yml";
    private String ConfigPath = "Config.yml";
    private String excludeSuffix = "";
    public static String[] scopeArray = new String[] {
            "any",
            "response",
            "response header",
            "response body",
            "request",
            "request header",
            "request body"
    };;

    public static String[] engineArray= new String[] {
            "nfa",
            "dfa"
    };;

    public static String[] colorArray= new String[] {
            "red",
            "orange",
            "yellow",
            "green",
            "cyan",
            "blue",
            "pink",
            "magenta",
            "gray"
    };
    private Map<String,Rules> rulesConfig;
    private volatile static Config instance;

    public static Config getInstance(){
        if (instance == null){
            synchronized (Config.class){
                if (instance == null){
                    instance = new Config();
                }
            }
        }
        return instance;
    }

    public Config() {
        // 构造函数，初始化配置
        this.rulesConfig = new HashMap<String,Rules>();
        DumperOptions dop = new DumperOptions();
        dop.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);//修改位层级表示
        this.yaml = new Yaml(dop);
        String path = System.getProperty("java.class.path");
        int firstIndex = path.lastIndexOf(System.getProperty("path.separator"))+1;
        int lastIndex = path.lastIndexOf(File.separator) +1;
        this.configdir = path.substring(firstIndex, lastIndex)+"PluginConfig"+File.separator+"HaE"+File.separator;
        File config_dir = new File(this.configdir);
        if (!config_dir.exists()){
            config_dir.mkdirs();
        }
        this.SettingPath = this.configdir+this.SettingPath;
        this.ConfigPath = this.configdir+this.ConfigPath;
//        this.scopeArray
//        this.engineArray
//        this.colorArray
        File yamlSetting = new File(SettingPath);
        if (!(yamlSetting.exists() && yamlSetting.isFile())) {
            initSetting();
            initRules();
        }
        else{
            try {
                InputStream config = new FileInputStream(this.ConfigPath);
                this.rulesConfig = yaml.loadAs(config, this.rulesConfig.getClass());
                config.close();
                InputStream setting = new FileInputStream(this.SettingPath);
                Map<String,Object> r = yaml.load(setting);
                this.excludeSuffix = r.get("excludeSuffix").toString();
                setting.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 初始化设置信息
    public void initSetting() {
        this.excludeSuffix = "3g2|3gp|7z|aac|abw|aif|aifc|aiff|arc|au|avi|azw|bin|bmp|bz|bz2|cmx|cod|csh|css|csv|doc|docx|eot|epub|gif|gz|ico|ics|ief|jar|jfif|jpe|jpeg|jpg|m3u|mid|midi|mjs|mp2|mp3|mpa|mpe|mpeg|mpg|mpkg|mpp|mpv2|odp|ods|odt|oga|ogv|ogx|otf|pbm|pdf|pgm|png|pnm|ppm|ppt|pptx|ra|ram|rar|ras|rgb|rmi|rtf|snd|svg|swf|tar|tif|tiff|ttf|vsd|wav|weba|webm|webp|woff|woff2|xbm|xls|xlsx|xpm|xul|xwd|zip|zip";
        Map<String, Object> r = new HashMap<>();
        r.put("configPath", ConfigPath);
        r.put("excludeSuffix", this.excludeSuffix);
        try {
            Writer ws = new OutputStreamWriter(new FileOutputStream(SettingPath), StandardCharsets.UTF_8);
            yaml.dump(r, ws);
            ws.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 初始化规则配置
    public void initRules() {
        Rule rule = new Rule();
        rule.setLoaded(true);
        rule.setName("Email");
        rule.setColor("yellow");
        rule.setEngine("nfa");
        rule.setScope("response");
        rule.setRegex("(([a-zA-Z0-9][_|\\.])*[a-zA-Z0-9]+@([a-zA-Z0-9][-|_|\\.])*[a-zA-Z0-9]+\\.((?!js|css|jpg|jpeg|png|ico)[a-zA-Z]{2,}))");
        Rules rules = new Rules();
        rules.setType("Basic Information");
        rules.rule.add(rule);
        this.rulesConfig.put(rules.getType(),rules);
        File f = new File(ConfigPath);
        try{
            Writer ws = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8);
            yaml.dump(this.rulesConfig,ws);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    // 获取配置路径
    public String getConfigPath(){
        return this.ConfigPath;
    }

    // 获取不包含的后缀名
    public String getExcludeSuffix(){
        return this.excludeSuffix;
    }

    // 获取规则配置
    public Map<String,Rules> getRules(){
        return this.rulesConfig;
    }

    // 设置配置路径
    public void setConfigPath(String filePath){
        Map<String,Object> r = new HashMap<>();
        r.put("configPath", filePath);
        r.put("excludeSuffix", getExcludeSuffix());
        this.ConfigPath = filePath;
        try{
            Writer ws = new OutputStreamWriter(new FileOutputStream(SettingPath), StandardCharsets.UTF_8);
            yaml.dump(r, ws);
            ws.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    // 设置不包含的后缀名
    public void setExcludeSuffix(String excludeSuffix){
        this.excludeSuffix = excludeSuffix;
        Map<String,Object> r = new HashMap<>();
        r.put("configPath", getConfigPath());
        r.put("excludeSuffix", excludeSuffix);
        try{
            Writer ws = new OutputStreamWriter(new FileOutputStream(SettingPath), StandardCharsets.UTF_8);
            yaml.dump(r, ws);
            ws.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void saveConfig(){
        File f = new File(ConfigPath);
        try{
            Writer ws = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8);
            yaml.dump(this.rulesConfig,ws);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public String getNewTitle(){
        int i = 0;
        String name = "New ";
        Rule rule = new Rule(false,"New Name", "(New Regex)", "gray", "any", "nfa");
        while (rulesConfig.containsKey(name + i)) {
            i++;
        }
        Rules rules = new Rules();
        String type = name+i;
        rules.rule.add(rule);
        rules.setType(type);
        rulesConfig.put(type,rules);
        saveConfig();
        return type;
    }


}