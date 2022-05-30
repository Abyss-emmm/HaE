package burp.action;

import java.util.*;

import burp.yaml.Config;

/**
 * @author EvilChen
 */

public class DoAction {
    public Map<String,Map<String,List<String>>> extractString(Map<String, Map<String,Map<String,Object>>> data_map) {
        Map<String,Map<String,List<String>>> resultMap = new HashMap<>();
        data_map.keySet().forEach(rules_type->{
            Map<String,Map<String,Object>> rulesMap = data_map.get(rules_type);
            Map<String,List<String>>rules_result = new HashMap<>();
            rulesMap.keySet().forEach(rule_name->{
                String data = (String) rulesMap.get(rule_name).get("data");
                rules_result.put(rule_name, Arrays.asList(data.split("\n")));
            });
            if(rules_result.size() > 0) {
                resultMap.put(rules_type, rules_result);
            }
        });
        return resultMap;
    }

    public List<List<String>> highlightAndComment(Map<String, Map<String,Map<String,Object>>> result_map) {
        List<String> colorList = new ArrayList<>();
        List<String> commentList = new ArrayList<>();
        List<List<String>> result = new ArrayList<>();
        result_map.keySet().forEach(rules_type->{
            Map<String,Map<String,Object>> rulesmap = result_map.get(rules_type);
            commentList.add(rules_type+":");
            rulesmap.keySet().forEach(rulename->{
                String color = rulesmap.get(rulename).get("color").toString();
                colorList.add(color);
                commentList.add(rulename);
            });
            commentList.add(";");
        });
        result.add(colorList);
        result.add(commentList);
        return result;
    }
}