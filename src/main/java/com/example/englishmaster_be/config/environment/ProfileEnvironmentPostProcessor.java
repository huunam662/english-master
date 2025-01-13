//package com.example.englishmaster_be.config.environment;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.env.EnvironmentPostProcessor;
//import org.springframework.core.env.ConfigurableEnvironment;
//
//public class ProfileEnvironmentPostProcessor implements EnvironmentPostProcessor {
//
//    @Override
//    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
//
//        String local = "local";
//
//        String staging = "staging";
//
//        String hostName = System.getenv("HOSTNAME");
//
//        System.out.println(hostName);
//    }
//}
