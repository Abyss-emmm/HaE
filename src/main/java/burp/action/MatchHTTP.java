package burp.action;

import burp.yaml.Config;
import jregex.Matcher;
import jregex.Pattern;
import jregex.REFlags;

/**
 * @author EvilChen
 */

public class MatchHTTP {
    // 匹配后缀
    Config config = new Config();
    public boolean matchSuffix(String str) {
        Pattern pattern = new Pattern(String.format("[\\w]+[\\.](%s)",config.getExcludeSuffix()), REFlags.IGNORE_CASE);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }
}
