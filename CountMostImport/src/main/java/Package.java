public class Package extends ImportImpl {
  private static Package root = of("root");

  public static Package root() {
    return root;
  }

  public static Package of(String name) {
    return new Package(name);
  }

  private Package(String name) {
    super(name);
  }
}
