import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import com.google.common.base.Ascii;

public class Tools {
  public static boolean isJavaFile(Path path, BasicFileAttributes attr) {
    return path.toString().endsWith(".java");
  }

  public static Import toImport(Path file) {
    try {
      return Files.readAllLines(file).stream()
          .filter(Tools::isImportLine)
          .map(Tools::toSplitImport)
          .reduce(Package.root(), Tools::reduceOneLine, (i, n) -> i);
    } catch (IOException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private static boolean isImportLine(String line) {
    return line.trim().startsWith("import ");
  }

  private static List<String> toSplitImport(String importStr) {
    String d = importStr;
    importStr = importStr.trim().substring(7);
    if (importStr.startsWith("static "))
      importStr = importStr.substring(7);
    int endPos = importStr.indexOf(';');
    importStr = importStr.substring(0, endPos);

    List<String> res = new ArrayList<>();
    while (true) {
      endPos = importStr.indexOf('.');
      if (endPos == -1) {
        res.add(importStr);
        break;
      }
      res.add(importStr.substring(0, endPos));
      importStr = importStr.substring(endPos + 1);
    }

    if (res.get(res.size() - 1).isEmpty())
      System.out.println(d);
    char end = res.get(res.size() - 1).charAt(0);
    if (end == '*' || Character.isUpperCase(end))
      return res;
    return res.subList(0, res.size() - 1);
  }

  private static Import reduceOneLine(Import anImport, List<String> ps) {
    boolean isAll = ps.get(ps.size() - 1).equals("*");
    int length = isAll ? ps.size() - 2 : ps.size();
    Import res = anImport;

    for (int i = 0; i != length; ++i) {
      int index = anImport.indexOf(ps.get(i));
      if (index == -1) {
        index = anImport.childs().size();
        anImport.add(createImpl(ps.get(i)));
      } else {
        anImport.set(index, add(anImport.get(index)));
      }
      anImport = anImport.get(index);
    }

    if (isAll) {
      int index = anImport.indexOf(ps.get(length));
      if (index == -1)
        anImport.add(createImpl(ps.get(length)).all());
      else
        anImport.set(index, anImport.get(index).all());
    }

    return res;
  }

  private static Import createImpl(String name) {
    return Character.isUpperCase(name.charAt(0))? Class.of(name) : Package.of(name);
  }

  public static Import add(Import anImport) {
    return add(anImport, 1);
  }

  public static Import add(Import anImport, int n) {
    if (anImport.isClass()) {
      Class newImport = (Class) anImport;
      newImport.inc(n);
      return newImport;
    }
    return anImport;
  }
}
