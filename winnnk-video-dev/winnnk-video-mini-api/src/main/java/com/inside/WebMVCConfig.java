package com.inside;

import com.inside.controller.interceptor.MiniInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMVCConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").
                addResourceLocations("classpath:/META-INF/resources/").
                addResourceLocations("file:/usr/projects/winnnkvideo/");
    }

    @Bean
    public MiniInterceptor miniInterceptor() {
        return new MiniInterceptor();
    }

    @Bean(initMethod = "init")
    public ZKCuratorClient zkCuratorClient() {
        return new ZKCuratorClient();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(miniInterceptor())
                .addPathPatterns("/user/**")
                .excludePathPatterns("/user/queryPublisher")
                .addPathPatterns("/bgm/**")
                .addPathPatterns("/video/upload",
                        "/video/userLike",
                        "/video/userUnlike",
                        "/video/saveComment");
        super.addInterceptors(registry);
    }
}
