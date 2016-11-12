
public class Rate {
  // public enum Type {LEFT, RIGHT}

  public Rate(Pair<Double, String> left, Pair<Double, String> right) {
    this.left = left;
    this.right = right;
  }

  public Double unitTo() {
    return right.first/ left.first;
  }

  public String getName() {
    return right.second;
  }

  @Override
  public String toString() {
    return left + " -> " + right;
  }

  private Pair<Double, String> left;
  private Pair<Double, String> right;
}
