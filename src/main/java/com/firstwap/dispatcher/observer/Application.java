package com.firstwap.dispatcher.observer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

import com.firstwap.dispatcher.observer.configurer.MvcConfigurer;
import com.firstwap.dispatcher.observer.service.runner.bean.ObserverDaemon;

@SpringBootApplication
@Import(value = {MvcConfigurer.class})
public class Application {

  public static void main(String[] args) throws Exception {
    ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

    ObserverDaemon checker = context.getBean(ObserverDaemon.class);
    Thread checkerThread = new Thread(checker);
    checkerThread.start();
  }
}
