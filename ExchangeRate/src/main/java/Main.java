import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Main {
  private static <T> List<T> streamToList(Stream<T> stream, Document d, Tools tools) {
    List<T> futures;
    long index = tools.indexOf(d);
    if (index == -1)
      futures = stream.collect(toList());
    else
      futures = stream.limit(index + 1).collect(toList());
    return futures;
  }

  private static void run(Document d, InExecl execl, Tools tools) {
    Elements es = d.select("font.newslist_style a");
    Stream<CompletableFuture<Document>> ds = es.stream()
        .map(e -> new RateRequest(RateRequest.Address + e.attr("href")))
        .map(RateRequest::document);
    // 这样写的唯一原因就是为了一个较短的类型声明
    streamToList(ds.map(future -> future.thenApply(Tools::parse)), d, tools)
        .stream()
        .map(CompletableFuture::join)
        .map(item -> Pair.of(item.first, tools.collect(item.second)))
        .forEach(execl::inRow);
  }

  public static void main(String[] args) {
    Tools tools = Tools.getTools();// .days(40);
    InExecl execl = new InExecl("test.xls", tools);

    Stream.iterate(Index.of(1), Index::next)
        .map(Index::document)
        .map(f -> f.thenAccept(d -> run(d, execl, tools)))
        .limit(tools.days() / 20 + 1) // 每个界面20项
        .collect(toList())  // 异步访问所有索引页
        .stream()
        .map(CompletableFuture::join)
        .count();

    execl.toFile();
  }
}
