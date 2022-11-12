package org.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebViewConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("testShop");
        registry.addViewController("/bankid").setViewName("testShop");
        registry.addViewController("/bankid/authMethodSelect").setViewName("toolsMenuPage");
        registry.addViewController("/bankid/qr_page").setViewName("logInPage");
        registry.addViewController("/bankid/paysettings_page").setViewName("paySettings");
        registry.addViewController("/bankid/payment_cancelled").setViewName("testShop");
        registry.addViewController("/bankid/test").setViewName("paymentHandle");
    }
}
