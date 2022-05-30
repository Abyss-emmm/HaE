package burp.action;

import java.nio.charset.StandardCharsets;
import java.util.*;

import burp.yaml.Config;
import burp.yaml.template.Rule;
import burp.yaml.template.Rules;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.AutomatonMatcher;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;
import jregex.Matcher;
import jregex.Pattern;

/**
 * @author EvilChen
 */

public class ExtractContent {
    /*
    返回的result，第一层字典的key是Rules.type，对应的value仍然是个字典
    第二层的字典的key对应的是每个rule的name，value对应的是依然是个字典，包含color和data.
    color就是rule对应的color，data则是匹配到的数据，通过换行分割。
     */

    public Map<String, Map<String,Map<String,Object>>>  matchRegex(byte[] content, String headers, byte[] body, String scopeString) {
        Map<String, Map<String,Map<String,Object>>> result_map = new HashMap<>(); // 最终返回的结果
        Config config = Config.getInstance();
        Map<String,Rules> ruleConfig = config.getRules();
        for(Rules rules:ruleConfig.values()){
            Map<String,Map<String,Object>> rulesMap = new HashMap<>();//key是rule的name，value是检测到的结果，包含color和data
            for(Rule rule:rules.rule) {
                List<String> result = new ArrayList<>();
                String matchContent = "";
                Map<String, Object> ruleMap;
                String name = rule.getName();
                boolean loaded = rule.getLoaded();
                String regex = rule.getRegex();
                String color = rule.getColor();
                String scope = rule.getScope();
                String engine = rule.getEngine();
                if (loaded && (scope.contains(scopeString) || "any".equals(scope))) {
                    switch (scope) {
                        case "any":
                        case "request":
                        case "response":
                            matchContent = new String(content, StandardCharsets.UTF_8).intern();
                            break;
                        case "request header":
                        case "response header":
                            matchContent = headers;
                            break;
                        case "request body":
                        case "response body":
                            matchContent = new String(body, StandardCharsets.UTF_8).intern();
                            break;
                        default:
                            break;
                    }

                    if ("nfa".equals(engine)) {
                        Pattern pattern = new Pattern(regex);
                        Matcher matcher = pattern.matcher(matchContent);
                        while (matcher.find()) {
                            // 添加匹配数据至list
                            // 强制用户使用()包裹正则
                            result.add(matcher.group(0));
                        }
                    } else {
                        RegExp regexp = new RegExp(regex);
                        Automaton auto = regexp.toAutomaton();
                        RunAutomaton runAuto = new RunAutomaton(auto, true);
                        AutomatonMatcher autoMatcher = runAuto.newMatcher(matchContent);
                        while (autoMatcher.find()) {
                            // 添加匹配数据至list
                            // 强制用户使用()包裹正则
                            result.add(autoMatcher.group());
                        }
                    }
                    // 去除重复内容
                    HashSet tmpList = new HashSet(result);
                    result.clear();
                    result.addAll(tmpList);

                    if (!result.isEmpty()) {
                       ruleMap = new HashMap<>();
                        ruleMap.put("color", color);
                        ruleMap.put("data", String.join("\n", result));
                        rulesMap.put(name,ruleMap);
                    }
                }
            }
            result_map.put(rules.getType(),rulesMap);
        }

        return result_map;
    }
}
