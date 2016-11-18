import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ImportImpl implements Import {
  public ImportImpl(String name) {
    this.all = 0;
    this.name = name;
    this.childs = Collections.emptyList();
  }

  @Override
  public Import run() {
    map(i -> Tools.add(i, all));
    map(Import::run);
    return this;
  }

  @Override
  public List<Import> childs() {
    if (childs == Collections.EMPTY_LIST)
      return childs = new ArrayList<>();
    return childs;
  }

  @Override
  public String name() {
    return this.name;
  }

  public Import all() {
    ++all;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder = builder.append(name()).append("\n");
    for (Import i : childs)
      builder = builder.append(i);
    return builder.toString();
  }

  private int all;
  private String name;
  private List<Import> childs;
}
