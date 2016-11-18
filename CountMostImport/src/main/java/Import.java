import java.util.List;
import java.util.function.Function;

public interface Import {
  Import run(); // 对所有文件的import读取完毕之后进行最后一步的整理
  List<Import> childs();
  String name(); // get the (package|class) name
  Import all(); // *++

  // 默认首字母大写的为class
  default boolean isClass() {
    return Character.isUpperCase(this.name().charAt(0));
  }

  default Import get(int index) {
    return childs().get(index);
  }

  default void set(int index, Import anImport) {
    childs().set(index, anImport);
  }

  default void add(Import anImport) {
    childs().add(anImport);
  }

  default void map(Function<Import, Import> func) {
    int length = childs().size();
    for (int i = 0; i != length; ++i)
      set(i, func.apply(get(i)));
  }

  default int indexOf(String name) {
    int res = 0;
    for (int i = 0; i != childs().size(); ++i) {
      Import aI = get(i);
      if (aI.name().equals(name))
        return res;
      else ++res;
    }
    return -1;
  }
}
