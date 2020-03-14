package org.jazzteam;

import org.jazzteam.gui.MainForm;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.awt.EventQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class Application {
    @Bean
    public ExecutorService executorService() {
        // Cached thread pool is acceptable because of one GUI client
        return Executors.newCachedThreadPool();
    }

    public static void main(String... args) {
        ApplicationContext applicationContext
                = new SpringApplicationBuilder(Application.class).headless(false).run(args);
        EventQueue.invokeLater(() -> {
            MainForm mainForm = applicationContext.getBean(MainForm.class);
            mainForm.setVisible(true);
        });
    }
}
