import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.script.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Main {
  public static void main(String[] args) {
    ParseText parseText = new ParseText(
        "中国人民银行授权中国外汇交易中心公布，2016年11月10日银行间外汇市场人民币汇率中间价为：" +
            "1美元对人民币6.7885元，1欧元对人民币7.4165元，100日元对人民币6.4245元，" +
            "1港元对人民币0.87535元，1英镑对人民币8.4368元，1澳大利亚元对人民币5.1947元，" +
            "1新西兰元对人民币4.9508元，1新加坡元对人民币4.8531元，1瑞士法郎对人民币6.8985元，" +
            "1加拿大元对人民币5.0609元，人民币1元对0.62235林吉特，人民币1元对9.3883俄罗斯卢布, " +
            "人民币1元对1.9850南非兰特，人民币1元对170.00韩元，人民币1元对0.54072阿联酋迪拉姆，" +
            "人民币1元对0.55210沙特里亚尔。");
    System.out.println(parseText);

    try {
      Document document = Jsoup.connect("http://www.pbc.gov.cn/zhengcehuobisi/125207/125217/125925/17105/index1.html").get();
      Elements element = document.getElementsByTag("script");
      String js = element.html();
      js = js.trim();
      js = js.substring(5, js.length() - 1);

      ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
      String mirror = (String) engine.eval("d = " + js);

      js = "function Wzwstemplate() { return KTKY2RBD9NHPBCIHV9ZMEQQDARSLVFDU(template.toString()) + \"; path=/\"}" +
          "function Wzwschallenge() { var confirm = QWERTASDFGXYSF(); return KTKY2RBD9NHPBCIHV9ZMEQQDARSLVFDU(confirm.toString()) + \"; path=/\"; }" +
          "function Dynamicurl() { return dynamicurl; }";

      engine = new ScriptEngineManager().getEngineByName("nashorn");
      engine.eval(mirror.substring(0, mirror.length() - 16) + js);
      Invocable invocable = (Invocable) engine;
      ScriptContext context = engine.getContext();
      System.out.println((String) invocable.invokeFunction("Dynamicurl"));
      System.out.println((String) context.getAttribute("dynamicurl"));
    } catch (IOException | ScriptException | NoSuchMethodException e) {
      e.printStackTrace();
    }
//                                                 http://www.pbc.gov.cn/zhengcehuobisi/125207/125217/125925/index.html
    Optional<Document> document = new RateRequest("http://www.pbc.gov.cn/zhengcehuobisi/125207/125217/125925/17105/index1.html").document;
    if (document.isPresent()) {
      Document d = document.get();
      Elements es = d.select("font.newslist_style a");
      System.out.println(es.size());
      for (Element e : es) {
        List<Rate> list = new RateRequest(RateRequest.Address + e.attr("href")).document
            .map(doc -> doc.getElementById("zoom").child(0))
            .map(Element::html)
            .map(ParseText::new)
            .map(ParseText::all)
            .orElse(Collections.emptyList());
        // .forEach(System.out::println);
        list.forEach(System.out::println);
        // System.out.println(list.size());
      }
    }
  }
}
