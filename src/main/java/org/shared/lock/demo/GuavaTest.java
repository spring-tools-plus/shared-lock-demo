package org.shared.lock.demo;

import com.google.common.util.concurrent.RateLimiter;

/**
 * Guava 测试
 * @author Fenghu.Shi
 * @version 1.0
 */
public class GuavaTest {

  public static void main(String[] args) {
    RateLimiter.create(5).acquire();
  }
}
