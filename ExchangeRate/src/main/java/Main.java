import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Main {
  public static void main(String[] args) {
    Tools tools = Tools.getTools().days(40);
    InExecl execl = new InExecl("test.xls", tools);
    Stream.iterate(Index.of(1), Index::next)
        .map(Index::document)
        .map(f -> f.thenAcceptAsync(d -> {
          Elements es = d.select("font.newslist_style a");
          Stream<CompletableFuture<Pair<LocalDate, Stream<Rate>>>>
              stream = es.stream()
              .map(e -> new RateRequest(RateRequest.Address + e.attr("href")))
              .map(RateRequest::document)
              .map(future -> future.thenApplyAsync(Tools::parse));

          List<CompletableFuture<Pair<LocalDate, Stream<Rate>>>> futures;
          long index = tools.indexOf(d);
          if (index == -1)
            futures = stream.collect(toList());
          else
            futures = stream.limit(index + 1).collect(toList());

          futures.stream()
              .map(CompletableFuture::join)
              .map(item -> Pair.of(item.first, tools.collect(item.second)))
              .forEach(execl::inRow);
        }))
        .limit(tools.days()/ 20 + 1) // 每个界面20项
        .collect(toList())
        .stream()
        .map(CompletableFuture::join)
        .findAny();

    execl.toFile();
    System.out.println("time is = "+ LocalTime.now());
  }
}
