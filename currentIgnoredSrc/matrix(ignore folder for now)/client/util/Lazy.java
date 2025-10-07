package com.cosium.matrix.client.util;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {
  private static final String CACHE_KEY = String.valueOf(0);
  private final Map<String, T> cache = new ConcurrentHashMap<>();

  private final Supplier<T> factory;

  private Lazy(Supplier<T> factory) {
    this.factory = requireNonNull(factory);
  }

  public static <T> Lazy<T> of(Supplier<T> factory) {
    return new Lazy<>(factory);
  }

  @Override
  public T get() {
    return cache.computeIfAbsent(CACHE_KEY, k -> factory.get());
  }
}
