package org.shared.lock.demo;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import net.madtiger.lock.ISharedLock;
import net.madtiger.lock.SharedLockBuilder;
import net.madtiger.lock.zk.ZookeeperConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * demo
 * @author Fenghu.Shi
 * @version 1.0
 */
@RestController
@RequestMapping("/demo")
@Slf4j
public class DemoController {

  @Autowired
  private DemoService demoService;

  private static final String LOCK_KEY = "lock-test-key";


  /**
   * 可重入锁测试
   * @return
   * @throws Exception
   */
  @GetMapping("/do-reentrant")
  public Flux<String> doReentrant() throws Throwable{
    ISharedLock lock = SharedLockBuilder.builder(LOCK_KEY).build();
    return lock.execute(() -> {
      // 支持降级的处理
      return SharedLockBuilder.builder(LOCK_KEY).build().execute(() -> {
        System.out.println("执行成功");
        return Flux.just("OK");
      }, () -> Flux.just("失败了"), 1, TimeUnit.SECONDS);
    }, () -> {
      // 这里可以执行回退或者异常检查
      return Flux.error(new Throwable("失败了"));
    }, 1, TimeUnit.SECONDS);
  }


  /**
   * try-basic 模式
   * @return
   * @throws IOException
   */
  @GetMapping("/try-basic")
  public Flux<String> doTry() throws Throwable {
    // 来一个 try-with-resource 模式
    try(ISharedLock lock = SharedLockBuilder.builder(LOCK_KEY).build()) {
      if (lock.tryLock(5, TimeUnit.SECONDS)) {
        System.out.println("执行成功");
        return Flux.just("OK");
      } else {
        return Flux.error(new Throwable("失败了"));
      }
    }
  }

  /**
   * try 模式
   * @return
   * @throws IOException
   */
  @GetMapping("/try-rollback")
  public Flux<String> doRollback() throws Throwable {
    // 来一个基本模式
    ISharedLock lock = SharedLockBuilder.builder(LOCK_KEY).providerConfigurer(ZookeeperConfigurer.builder().namespace("/shared").build()).build();
    Flux<String> result;
    try{
      // 尝试获取所并判断是否锁定成功
      if (lock.tryLock(10, TimeUnit.SECONDS)){
        result = Flux.just("获取锁成功");
      }else {
        result = Flux.just("获取锁失败");
      }
    } finally {
      // 3. 释放锁
      lock.unlock();
      if (lock.isRollback()) {
        result = Flux.just("回滚吧");
      }
    }
    return result;
  }


  /**
   * 使用 try-with-resource 模式
   * @return
   * @throws IOException
   */
  @GetMapping("/try-with-resource")
  public Flux<String> doTryWithResource() throws Throwable {
    try(ISharedLock lock = SharedLockBuilder.builder(LOCK_KEY).build()){
      if (lock.tryLock(10, TimeUnit.SECONDS)){
        return Flux.just("获取所成功");
      }else {
        return Flux.just("获取所失败");
      }
    }
  }


  /**
   * execute 模式
   * @return
   * @throws TimeoutException
   */
  @GetMapping("/do-execute")
  public Flux<String> doExecute() throws Throwable {
    return SharedLockBuilder.builder(LOCK_KEY).build().execute(() -> {
      return Flux.just("获取到锁了");
    }, () -> {
      return Flux.error(new Throwable("获取所失败"));
    });
  }

  /**
   * aop 测试
   * @param param
   * @return
   */
  @GetMapping("/do-aop")
  public Flux<String> doAop(@RequestParam("a") String param){
    return Flux.just(demoService.testAopLock(param));
  }

}
