package com.app.tuantuan;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

@Slf4j
@SpringBootApplication
public class CrawlerServiceApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext application =
        SpringApplication.run(CrawlerServiceApplication.class, args);
    consolePrint(application.getEnvironment());
  }

  public static void consolePrint(Environment environment) {
    String ip = "";
    try {
      ip = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      log.error("获取服务器IP失败", e);
    }
    String port = environment.getProperty("server.port");
    String contextPath = environment.getProperty("server.servlet.context-path");
    contextPath = Objects.isNull(contextPath) ? "" : contextPath;
    log.info(
        """

                    \t-------------------------------------------------------------
                    \t\
                    本 地 地 址: \thttp://localhost:{}{}/
                    \t\
                    外 部 地 址: \thttp://{}:{}{}/
                    \t\
                    Swagger-ui地址: http://{}:{}{}/swagger-ui/
                    \t\
                    --------------------------------------------------------------""",
        port,
        contextPath,
        ip,
        port,
        contextPath,
        ip,
        port,
        contextPath);
  }
}
