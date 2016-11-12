import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateRates {
  public CreateRates() {
    this("美元", "欧元", "港元");
  }

  private CreateRates(String... ss) {
    set = new HashSet<>();
    Collections.addAll(set, ss);
    this.start = LocalDate.now();
  }

  // private List<Rate> allGet()

  private int indexOf(Elements elements) {
    LocalDate end = start.minusDays(30);
    LocalDate the = LocalDate.parse(elements.last().html(),
        DateTimeFormatter.ISO_LOCAL_DATE);
    if (the.isAfter(end))
      return -1;
    int res = 18;
    while (the.isBefore(end))
      the = LocalDate.parse(elements.get(res--).html(),
          DateTimeFormatter.ISO_LOCAL_DATE);
    return res;
  }

  private final Set<String> set;
  private final LocalDate start;
}
