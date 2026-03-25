package cn.guet.competition_cert_system.config;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer{

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

    FastJsonConfig config = new FastJsonConfig();
    config.setDateFormat("yyyy-MM-dd HH:mm:ss");
    config.setWriterFeatures(
      JSONWriter.Feature.WriteMapNullValue,        // 输出 Map 的 null 值
      JSONWriter.Feature.WriteNullListAsEmpty,     // List 为 null 输出 []
      JSONWriter.Feature.WriteNullStringAsEmpty,   // String 为 null 输出 ""
      JSONWriter.Feature.PrettyFormat               // 美化输出（生产环境建议关闭）      
    );
    converter.setFastJsonConfig(config);

    converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
    converter.setDefaultCharset(StandardCharsets.UTF_8);
    
    converters.add(0, converter);
  }
  
}
