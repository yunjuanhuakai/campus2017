import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.stream.Collector.Characteristics.*;

public class ToTreeSet<T> implements Collector<T, Set<T>, Set<T>> {
  public static <T> Collector<T, Set<T>, Set<T>> toSet() {
    return new ToTreeSet<T>();
  }

  @Override
  public Supplier<Set<T>> supplier() {
    return TreeSet::new;
  }

  @Override
  public BiConsumer<Set<T>, T> accumulator() {
    return Set::add;
  }

  @Override
  public BinaryOperator<Set<T>> combiner() {
    return (set1, set2) -> {
      set1.addAll(set2);
      return set1;
    };
  }

  @Override
  public Function<Set<T>, Set<T>> finisher() {
    return Function.identity();
  }

  @Override
  public Set<Characteristics> characteristics() {
    return Collections.unmodifiableSet(EnumSet.of(
        IDENTITY_FINISH, CONCURRENT
    ));
  }
}
