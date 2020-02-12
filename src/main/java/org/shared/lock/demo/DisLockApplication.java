package org.shared.lock.demo;

import net.madtiger.shared.lock.EnabledSharedLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnabledSharedLock
public class DisLockApplication {

    public static void main(String[] args) {
        SpringApplication.run(DisLockApplication.class, args);
    }

}
