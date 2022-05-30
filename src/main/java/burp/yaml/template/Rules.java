package burp.yaml.template;


import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author LinChen
 */

public class Rules {
    private String type;
    public List<Rule> rule;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Rule> getRule() {
        return rule;
    }

    public void setRule(List<Rule> rule) {
        this.rule = rule;
    }

    public void setRuleObj(){

    }
    public  Object[][] getRulesObject(){
        int length = this.rule.size();
        Object[][]result = new Object[length][];
        for (int i=0;i<length;i++){
            result[i] = this.rule.get(i).getRuleObject();
        }
        return result;
    }
    public String toString(){
        return "{ type: "+type+"\n config: "+ rule +"}\n";
    }

    public Rules() {
        this.rule = new ArrayList<>();
    }
}