package com.example.englishmaster_be.util;

import org.jsoup.nodes.Element;

import java.util.List;

public class DomUtil {

    public static String toHtmlQuestionContentSpeaking(String title, List<String> questionContents){
        if(questionContents.isEmpty()) return null;
        Element container = new Element("div");
        Element p = new Element("p").attr("style", "margin: 6px 2px;");
        Element strong = new Element("strong").text(title);
        container.prependChild(p.appendChild(strong));
        Element ul = new Element("ul");
        for(String questionContent : questionContents){
            Element li = new Element("li").text(questionContent);
            ul.appendChild(li);
        }
        container.appendChild(ul);
        return container.toString();
    }

}
