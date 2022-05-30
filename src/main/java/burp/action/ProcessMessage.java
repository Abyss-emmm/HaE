package burp.action;

import burp.*;

import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessMessage {
    MatchHTTP mh = new MatchHTTP();
    ExtractContent ec = new ExtractContent();
    DoAction da = new DoAction();
    GetColorKey gck = new GetColorKey();
    UpgradeColor uc = new UpgradeColor();
    public static final String HighLight_Comment_key = "highlight_comment";

    public Map<String,Map<String,List<String>>> processMessageByContent(IExtensionHelpers helpers, byte[] content, boolean isRequest, boolean ishighlight_comment/*, PrintWriter stdout*/) {
        /*
        如果是做高亮和注释的，result仅包含key为highlight_comment,对应的值是个字典，分别包含key为color和comment。
            color和comment虽然是List，但都仅包含一项.
        如果是返回匹配到的数据，第一个key包含的是rules_type，对应的value依然是个字典
            第二个字典的Key是rule_name,value是匹配到的数据。
        */
//        List<Map<String, String>> result = new ArrayList<>();
        Map<String,Map<String,List<String>>> result = new HashMap<>();
        Map<String, Map<String,Map<String,Object>>> result_map;
//        byte[] content;
//        if (isRequest) {
//            content = httpRequestResponse.getRequest();
//        } else {
//            content = httpRequestResponse.getResponse();
//        }

        if (isRequest) {

            // 获取报文头
            IRequestInfo requestInfo = helpers.analyzeRequest(content);
            List<String> requestTmpHeaders = requestInfo.getHeaders();
            String requestHeaders = String.join("\n", requestTmpHeaders);
//            URL url = requestInfo.getUrl();
            String urlString = requestTmpHeaders.get(0).split(" ")[1];
//            stdout.println(urlString);


            try {
                // 流量清洗
                urlString = urlString.indexOf("?") > 0 ? urlString.substring(0, urlString.indexOf("?")) : urlString;
//                stdout.println(urlString);
                // 正则判断
                if (mh.matchSuffix(urlString)) {
                    return result;
                }
            } catch (Exception e) {
                return result;
            }


            // 获取报文主体
            int requestBodyOffset = requestInfo.getBodyOffset();
            byte[] requestBody = Arrays.copyOfRange(content, requestBodyOffset, content.length);

            result_map = ec.matchRegex(content, requestHeaders, requestBody, "request");
        } else {
            IResponseInfo responseInfo = helpers.analyzeResponse(content);
            try {
                // 流量清洗
                String inferredMimeType = responseInfo.getInferredMimeType().toLowerCase();
                String statedMimeType = responseInfo.getStatedMimeType().toLowerCase();
                // 正则判断
                if (mh.matchSuffix(statedMimeType) || mh.matchSuffix(inferredMimeType)) {
                    return result;
                }
            } catch (Exception e) {
                return result;
            }
            // 获取报文头
            List<String> responseTmpHeaders = responseInfo.getHeaders();
            String responseHeaders = String.join("\n", responseTmpHeaders);

            // 获取报文主体
            int responseBodyOffset = responseInfo.getBodyOffset();
            byte[] responseBody = Arrays.copyOfRange(content, responseBodyOffset, content.length);

            result_map = ec.matchRegex(content, responseHeaders, responseBody, "response");
        }

        if (ishighlight_comment) {
            List<List<String>> resultList = da.highlightAndComment(result_map);
            List<String> colorList = resultList.get(0);
            List<String> commentList = resultList.get(1);

            List<String> result_colorlist = new ArrayList<>();
            List<String> result_commentlist = new ArrayList<>();
            if (colorList.size() != 0 && commentList.size() != 0) {
                String color = uc.getEndColor(gck.getColorKeys(colorList));
                result_colorlist.add(color);
                result_commentlist.add(String.join(", ", commentList));
                result.put(HighLight_Comment_key,new HashMap<>());
                result.get(HighLight_Comment_key).put("color",result_colorlist);
                result.get(HighLight_Comment_key).put("comment",result_commentlist);
            }
        } else {
            if (result_map.size() > 0) {
                result = da.extractString(result_map);
            }
        }
        return result;

    }
}