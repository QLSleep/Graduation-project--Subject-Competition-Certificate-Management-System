package cn.guet.competition_cert_system.config;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import cn.guet.competition_cert_system.interceptor.JWTInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${file.upload-path}")
  private String uploadPath;

  @Autowired
  private JWTInterceptor jwtInterceptor;

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

    FastJsonConfig config = new FastJsonConfig();
    config.setDateFormat("yyyy-MM-dd HH:mm:ss");
    config.setWriterFeatures(
        JSONWriter.Feature.WriteMapNullValue,
        JSONWriter.Feature.WriteNullListAsEmpty,
        JSONWriter.Feature.WriteNullStringAsEmpty,
        JSONWriter.Feature.PrettyFormat
    );
    converter.setFastJsonConfig(config);

    converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
    converter.setDefaultCharset(StandardCharsets.UTF_8);

    converters.add(0, converter);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String path = uploadPath;
    if (!path.endsWith("/") && !path.endsWith("\\")) {
      path += "/";
    }
    registry.addResourceHandler("/files/**")
        .addResourceLocations("file:" + path);
    WebMvcConfigurer.super.addResourceHandlers(registry);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(jwtInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns("/auth/login", "/auth/refresh", "/auth/logout",
            "/auth/send-register-code", "/auth/send-reset-code", "/auth/reset-password",
            "/auth/register", "/files/**");
  }

}
