package org.shared.lock.demo;

import net.madtiger.lock.EnabledSharedLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DisLockApplication {

    public static void main(String[] args) {
        SpringApplication.run(DisLockApplication.class, args);
    }

}
