import java.util.*;

public class CountMostImport {
  public static CountMostImport of(Import anImport, int num) {
    return new CountMostImport(anImport, num);
  }

  private CountMostImport(Import anImport, int count) {
    this.anImport = anImport;
    this.count = count;
    this.map = new TreeSet<>();
    init();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Item item : map)
      builder = builder.append(item).append("\n");
    return builder.toString();
  }

  private void init() {
    dfs(anImport, new LinkedList<>());
  }

  private void dfs(Import anImport, LinkedList<String> path) {
    path.addLast(anImport.name());

    if (anImport.isClass())
      addInMap(((Class) anImport).num(), path);

    for (int i = 0; i != anImport.childs().size(); ++i) {
      Import cImport = anImport.get(i);
      dfs(cImport, path);
    }
    path.pollLast();
  }

  private void addInMap(int num, List<String> path) {
    if (map.size() == count) {
      if (map.first().num < num) {
        map.remove(map.first());
        map.add(Item.of(num, new ArrayList<>(path)));
      }
    } else
      map.add(Item.of(num, new ArrayList<>(path)));
  }

  private static class Item implements Comparable<Item> {
    private Integer num;
    private List<String> path;

    private static Item of(Integer num, List<String> path) {
      Item res = new Item();
      res.num = num;
      res.path = path.subList(1, path.size()); // remove first "root"
      return res;
    }

    @Override
    public int compareTo(Item o) {
      int comTo = num.compareTo(o.num);
      if (comTo == 0 && path.equals(o.path))
        return 0;
      if (comTo == 0)
        return -1;
      return num.compareTo(o.num);
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      for (String s : path)
        builder = builder.append(s).append(".");
      builder = builder.deleteCharAt(builder.length() - 1)
          .append(": ").append(num);
      return builder.toString();
    }
  }

  private final Import anImport;
  private final int count;
  private final SortedSet<Item> map;
}
