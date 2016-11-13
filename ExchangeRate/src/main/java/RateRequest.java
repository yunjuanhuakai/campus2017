import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.script.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class RateRequest {
  public static final String Address = "http://www.pbc.gov.cn";
  private final CompletableFuture<Document> document;

  public RateRequest(String url) {
    Request request = new Request(url);
    this.document = request(request)
        .thenApply(this::fetch)
        .thenCompose(this::runFristJs)
        .thenCompose(js -> runSecondJs(js, request))
        .thenCompose(this::request);
  }

  public CompletableFuture<Document> document() {
    return document;
  }

  private String fetch(Document document) {
    Elements elements = document.getElementsByTag("script");
    return elements.get(0).html();
  }

  private CompletableFuture<String> runFristJs(String js) {
    js = js.trim();
    js = js.substring(5, js.length() - 1); // remove 'eval(' and ')'

    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    StringBuilder buffer = new StringBuilder("d = "); // 不知为何直接运行无法获取返回值
    buffer.append(js);
    return CompletableFuture.supplyAsync(() -> {
      try {
        return (String) engine.eval(buffer.toString());
      } catch (ScriptException e) {
        System.err.println(e.getMessage());
        e.printStackTrace();
        throw new RuntimeException();
      }
    });
  }

  private CompletableFuture<Request> runSecondJs(String js, Request request) {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    return CompletableFuture.supplyAsync(() -> {
      String wzwstemplate, wzwschallenge, dynamicurl;
      try {
        engine.eval(js.substring(0, js.length() - 16));
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
        throw new RuntimeException();
      }
      Map<String, String> cookis = new HashMap<>();
      cookis.put("wzwstemplate", wzwstemplate);
      cookis.put("wzwschallenge", wzwschallenge);
      request.cookies.putAll(cookis);
      request.url = Address + dynamicurl;
      return request;
    });
  }

  private CompletableFuture<Document> request(Request request) {
    Map<String, String> header = new HashMap<>();
    header.put("Host", "http://info.bet007.com");
    header.put("User-Agent", "  Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0");
    header.put("Accept", "  text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    header.put("Accept-Language", "zh-cn,zh;q=0.5");
    header.put("Accept-Charset", "  GB2312,utf-8;q=0.7,*;q=0.7");
    header.put("Connection", "keep-alive");
    Connection connect = Jsoup.connect(request.url).timeout(5000).data(header);

    return CompletableFuture.supplyAsync(() -> {
      delay(); // 每次访问加一个随机延时
      try {
        if (request.cookies != null) connect.cookies(request.cookies);
        System.out.println("url is " + request.url + " cookies is " + request.cookies);
        Connection.Response response = connect.execute();
        request.cookies = response.cookies();
        connect.cookies(response.cookies());
        return response.parse();
      } catch (IOException e) {
        System.err.println(e.getMessage());
        e.printStackTrace();
        throw new RuntimeException();
      }
    });
  }

  private static void delay() {
    int delay = 100 + random.nextInt(500);
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private static final Random random = new Random();
  private class Request {
    private String url;
    private Map<String, String> cookies;

    public Request(String url) {
      this.url = url;
      this.cookies = new HashMap<>();
    }
  }
}
