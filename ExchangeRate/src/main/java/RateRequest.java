import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.script.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RateRequest {
  public static final String Address = "http://www.pbc.gov.cn";
  public final Optional<Document> document;

  public RateRequest(String url) {
    this.document = request(url)
        .flatMap(d -> fetch(d, "script")
            .map(es -> es.get(0).html()))
        .flatMap(this::runFristJs)
        .flatMap(this::runSecondJs);
  }

  private Optional<Elements> fetch(Document document, String select) {
    Elements elements = document.select(select);
    if (elements.isEmpty())
      return Optional.empty();
    return Optional.of(elements);
  }

  private Optional<String> runFristJs(String js) {
    js = js.trim();
    js = js.substring(5, js.length() - 1); // remove 'eval(' and ')'

    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    StringBuilder buffer = new StringBuilder("d = "); // 不知为何直接运行无法获取返回值
    buffer.append(js);
    try {
      String res = (String) engine.eval(buffer.toString());
      return Optional.of(res);
    } catch (ScriptException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      return Optional.empty();
    }
  }

  private Optional<Document> runSecondJs(String js) {
    js = js.substring(0, js.length() - 16); // remove 'HXXTTKKLLPPP5();'
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    String wzwstemplate, wzwschallenge, dynamicurl;
    try {
      engine.eval(js);
      ScriptContext context = engine.getContext();
      Integer template = (Integer) context.getAttribute("template");
      dynamicurl = (String) context.getAttribute("dynamicurl");
      Invocable invocable = (Invocable) engine;
      wzwstemplate = (String) invocable.invokeFunction("KTKY2RBD9NHPBCIHV9ZMEQQDARSLVFDU", template.toString());
      String confirm = (String) invocable.invokeFunction("QWERTASDFGXYSF");
      wzwschallenge = (String) invocable.invokeFunction("KTKY2RBD9NHPBCIHV9ZMEQQDARSLVFDU", confirm);
    } catch (ScriptException | NoSuchMethodException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      return Optional.empty();
    }
    cookis.put("wzwstemplate", wzwstemplate);
    cookis.put("wzwschallenge", wzwschallenge);
    return request(Address + dynamicurl, true, 3);
  }

  private Optional<Document> request(String url) {
    return request(url, false, 5);
  }

  private Optional<Document> request(String url, boolean hasCookis, int n) {
    Connection connect = Jsoup.connect(url).timeout(3000);
    Document document;
    try {
      if (hasCookis) connect.cookies(cookis);
      Connection.Response response = connect.execute();
      cookis.putAll(response.cookies());
      document = response.parse();
    } catch (IOException e) {
      if (n != 0)
        return request(url, hasCookis, n - 1);
      System.err.println(e.getMessage());
      e.printStackTrace();
      return Optional.empty();
    }
    return Optional.of(document);
  }

  private Map<String, String> cookis = new HashMap<>();
}
