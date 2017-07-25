package top.apa7.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

public class DOMHelper {
    private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
    private static final String regEx_style  = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
    private static final String regEx_html   = "<[^>]+>"; // 定义HTML标签的正则表达式
    private static final String regEx_space  = "\\s*|\t|\r|\n";// 定义空格回车换行符

    public static final String AMOUNT  = "-?\\d+\\.\\d+";    // 定义金额的表达式(可为负)
    public static final String CARDNUM = "\\d{4}";    // 定义金额的表达式(可为负)

    public static Element getMaxContainer(Elements elements, String keyStr) {
        Elements newElements = null;
        if (StringUtils.isEmpty(keyStr)) {
            newElements = elements;
        } else {
            String[] keys = keyStr.split("&");
            newElements = new Elements();
            for (Element element : elements) {
                boolean flag = true;
                for (String key : keys) {
                    if (!element.html().contains(key)) {
                        flag = false;
                    }
                }
                if (flag) {
                    newElements.add(element);
                }
            }
        }
        int len = Integer.MIN_VALUE;
        Element e = null;
        for (Element element : newElements) {
            if (element.html().length() > len) {
                len = element.html().length();
                e = element;
            }
        }
        return e;
    }

    public static Element getMinContainer(Elements elements, String keyStr) {
        Elements newElements = filter(elements, keyStr);
        int len = Integer.MAX_VALUE;
        Element e = null;
        for (Element element : newElements) {
            if (element.html().length() < len) {
                len = element.html().length();
                e = element;
            }
        }
        return e;
    }

    public static Elements filter(Elements elements, String keyStr) {

        Elements newElements = null;
        if (StringUtils.isEmpty(keyStr)) {
            newElements = elements;
        } else {
            String[] keys = keyStr.split("&");
            newElements = new Elements();
            for (Element element : elements) {
                boolean flag = true;
                for (String key : keys) {
                    if (!element.html().contains(key)) {
                        flag = false;
                    }
                }
                if (flag) {
                    newElements.add(element);
                }
            }
        }

        return newElements;
    }

    public static String getText(Elements elements) {
        if (elements != null || elements.hasText()) {
            return elements.text().replaceAll("[　| | ]+", "");
        }
        return "";
    }

    /**
     * @param element
     * @return
     */
    public static String getText(Element element) {
        if (element != null || element.hasText()) {
            return element.text().replaceAll("[　| | ]+", "");
        }
        return "";
    }

    // 去除html
    public static String filterHtml(String str) {
        Pattern p_script = Pattern.compile(regEx_script,
                Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(str);
        str = m_script.replaceAll(""); // 过滤script标签

        Pattern p_style = Pattern
                .compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(str);
        str = m_style.replaceAll(""); // 过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(str);
        str = m_html.replaceAll(""); // 过滤html标签

        Pattern p_space = Pattern
                .compile(regEx_space, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(str);
        str = m_space.replaceAll(""); // 过滤空格回车标签
        return str.trim(); // 返回文本字符串
    }

    public static String getMatches(String content, String regex) {
        String str = "";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(content);
        if (matcher.find()) {
            str = matcher.group();
        }
        return str;
    }

    public static List<String> getMatchList(String content, String regex) {
        List<String> list = new ArrayList<>();
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(content);
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

    /**
     * 替换全部空格(全角,半角)为标记
     *
     * @param content 原文
     * @param tag     标记
     * @return
     */
    public static String replaceAllSpace(String content, String tag) {
        return content.replaceAll("[　| | ]+", tag);
    }

    /**
     * 获取纯文本,空格替换为指定标记
     *
     * @param htmlContent html文本
     * @param tag         标记
     * @return
     */
    public static String getPlainTextWithTag(String htmlContent, String tag) {
        String mailContent;
        mailContent = Jsoup.parse(htmlContent).text().trim();
        mailContent = replaceAllSpace(mailContent, tag);
        return mailContent;
    }

}