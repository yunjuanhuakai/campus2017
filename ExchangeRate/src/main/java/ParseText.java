import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParseText {
  private List<Rate> list;

  public ParseText(String text) {
    text = text.split("：")[1];
    text = text.substring(0, text.length() - 1);
    this.list = Arrays.stream(text.split("(，|, )"))
        .map(this::fetch)
        .collect(Collectors.toList());
  }

  public List<Rate> all() {
    return list;
  }

  @Override
  public String toString() {
    return list.toString();
  }

  private Rate fetch(String str) {
    String[] strs = str.split("对");
    if (strs[0].startsWith("人民币"))
      return fetch(strs[0], strs[1]);
    else
      return fetch(strs[1], strs[0]);
  }

  private Rate fetch(String left, String right) {
    Pair<Double, String> pleft = Pair.of(fetchNumOfEnd(left), "人民币");

    Pair<Double, Integer> pair = fetchNumOfHead(right);
    Pair<Double, String> pright = Pair.of(pair.first, right.substring(pair.second));
    return new Rate(pleft, pright);
  }

  private Pair<Double, Integer> fetchNumOfHead(String str) {
    int i = 0;
    while (Character.isDigit(str.charAt(i)) || str.charAt(i) == '.')
      i++;
    return Pair.of(Double.valueOf(str.substring(0, i)), i);
  }

  private double fetchNumOfEnd(String str) {
    return Double.valueOf(str.substring(3, str.length() - 1));
  }
}
