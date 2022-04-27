package com.pandoaspen.common.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Range {


    public static void run(int range, Runnable runnable) {
        IntStream.range(0, range).forEach(i -> runnable.run());
    }

    public static <T> Stream<T> stream(int range, Supplier<T> supplier) {
        return IntStream.range(0, range).mapToObj(i -> supplier.get());
    }

    public static <C extends Collection<T>, T> C collect(int range, Supplier<T> supplier,
                                                         Collector<T, ?, C> collector) {
        return stream(range, supplier).collect(collector);
    }

    public static <T> List<T> list(int range, Supplier<T> supplier) {
        return collect(range, supplier, Collectors.toList());
    }

    public static <K, V> Map<K, V> map(int range, Supplier<K> keySupplier, Supplier<V> valueSupplier) {
        Map<K, V> map = new HashMap<>(range);
        run(range, () -> map.put(keySupplier.get(), valueSupplier.get()));
        return map;
    }
}
