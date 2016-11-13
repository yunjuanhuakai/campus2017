import org.jsoup.nodes.Document;

import java.util.concurrent.CompletableFuture;

public class Index {
  //                                    http://www.pbc.gov.cn/zhengcehuobisi/125207/125217/125925/17105/index1.html
  private final static String format = "http://www.pbc.gov.cn/zhengcehuobisi/125207/125217/125925/17105/index%d.html";
  private int index;

  public static Index of(int index) {
    return new Index(index);
  }

  private Index(int index) {
    this.index = index;
  }

  public CompletableFuture<Document> document() {
    return new RateRequest(String.format(format, index)).document();
  }

  public Index next() {
    return new Index(index + 1);
  }
}
