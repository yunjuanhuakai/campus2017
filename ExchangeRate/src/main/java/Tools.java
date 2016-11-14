import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

public class Tools {
  private LocalDate start;
  private Set<String> set;
  private long days;
  private static final Tools tools = new Tools();

  public static Tools getTools() {
    return tools;
  }

  private Tools() {
    set = new TreeSet<>();
    Collections.addAll(set, "美元", "欧元", "港元");
    start = LocalDate.now();
    days = 30;
  }

  public Tools set(String... set) {
    this.set.clear();
    Collections.addAll(this.set, set);
    return this;
  }

  public Tools days(int days) {
    this.days = days;
    return this;
  }

  public long days() {
    return days;
  }

  public Set<String> set() {
    return set;
  }

  public LocalDate end() {
    return start.minusDays(days);
  }

  public Set<Rate> collect(Stream<Rate> stream) {
    Set<Rate> res = stream
        .filter(r -> set.contains(r.getName()))
        .collect(ToTreeSet.toSet());
    if (res.size() == set.size())
      return res;

    for (String s : set)
      if (!hasTheMoney(s, res))
        res.add(new Rate(Pair.of(1., "人民币"), Pair.of(-1., s)));
    return res;
  }

  // 索引页
  public Optional<Integer> indexOf(Document document) {
    Elements elements = document.select("span.hui12");
    LocalDate end = end();
    LocalDate the = LocalDate.parse(elements.last().html(),
        DateTimeFormatter.ISO_LOCAL_DATE);

    if (end.isBefore(the))
      return Optional.of(20);

    the = LocalDate.parse(elements.first().html(),
        DateTimeFormatter.ISO_LOCAL_DATE);

    if (the.isBefore(end))
      return Optional.empty(); // first is before end; no read the document

    int res = 1;
    while (the.isAfter(end))
      the = LocalDate.parse(elements.get(res++).html(),
          DateTimeFormatter.ISO_LOCAL_DATE);

    return Optional.of(res);
  }

  // 公告页
  public static CompletableFuture<Pair<LocalDate, Stream<Rate>>>
  parse(Document document) {
    return CompletableFuture.supplyAsync(() -> {
      String text = document.getElementById("zoom").child(0).html();
      text = text.split("：")[1];
      text = text.substring(0, text.length() - 1);
      Stream<Rate> rl = Arrays.stream(text.split("(，|, )"))
          .map(Tools::fetch);

      String dateStr = document.getElementById("shijian").html().split(" ")[0];
      LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
      System.out.println("正在解析：" + date + " 的汇率信息");
      return Pair.of(date, rl);
    });
  }

  private static Rate fetch(String str) {
    String[] strs = str.split("对");
    if (strs[0].startsWith("人民币"))
      return fetch(strs[0], strs[1]);
    else
      return fetch(strs[1], strs[0]);
  }

  private static Rate fetch(String left, String right) {
    Pair<Double, String> pleft = Pair.of(fetchNumOfEnd(left), "人民币");

    Pair<Double, Integer> pair = fetchNumOfHead(right);
    Pair<Double, String> pright = Pair.of(pair.first, right.substring(pair.second));
    return new Rate(pleft, pright);
  }

  private static Pair<Double, Integer> fetchNumOfHead(String str) {
    int i = 0;
    while (Character.isDigit(str.charAt(i)) || str.charAt(i) == '.')
      i++;
    return Pair.of(Double.valueOf(str.substring(0, i)), i);
  }

  private static double fetchNumOfEnd(String str) {
    return Double.valueOf(str.substring(3, str.length() - 1));
  }

  private boolean hasTheMoney(String s, Set<Rate> rset) {
    boolean res = false;
    for (Rate r : rset)
      res |= r.getName().equals(s);
    return res;
  }
}
