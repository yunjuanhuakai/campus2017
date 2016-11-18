public class Class extends ImportImpl {
  public static Class of(String name) {
    return new Class(name);
  }

  private Class(String name) {
    super(name);
    this.num = 1;
  }

  public void inc(int n) {
    num += n;
  }

  public int num() {
    return num;
  }

  private int num;
}
