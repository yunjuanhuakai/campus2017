import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class Main {

  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("请输入要处理的java文件路径");
      System.exit(-1);
    }

    Path path = Paths.get(args[0]);
    if (!path.isAbsolute())
      path = path.toAbsolutePath();

    Import root;
    try {
      root = Files.find(path, 100, Tools::isJavaFile)
          .map(Tools::toImport)
          .reduce(Package.root(), (i, n) -> i);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }

    root.run();
    System.out.println(CountMostImport.of(root, 10));
  }
}
