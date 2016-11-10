import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Main {
  private static boolean filterStr(String str) {
    String res = str.trim();
    return !(res.equals("") || res.startsWith("//"));
  }

  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("请输入要处理的java文件路径");
      System.exit(-1);
    }

    Path path = Paths.get(args[0]);
    if (!path.isAbsolute())
      path = path.toAbsolutePath();

    if (!Files.exists(path) || !path.toString().endsWith(".java")) {
      System.err.println("请输入正确的java文件路径");
      System.exit(-1);
    }

    try (Stream<String> lines = Files.lines(path)) {
      System.out.println("文件 " + path.getFileName() + " 的有效行数为: " +
          lines.filter(Main::filterStr).count());
    } catch (IOException e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
}
