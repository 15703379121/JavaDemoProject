package com.baisha.javademo;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

@ComponentScan
public class ControllerApplication extends WebMvcConfigurerAdapter {
	public static void main(String[] args){
        SpringApplication.run(ControllerApplication.class,args);
    }
    //配置扫描静态资源
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/assets/");
    }
    //配置使用FastJson
    //方法一，重写方法

   @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        /*1:定义一个Convert转换消息对象
        2：添加fastjson配置信息
        3：在convert中添加配置信息
        4：将convert添加到converts中*/
        FastJsonHttpMessageConverter converter=new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig= new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        converter.setFastJsonConfig(fastJsonConfig);
        converters.add(converter);
   }
   
   public HttpMessageConverters httpMessageConverters(){
	   FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
	   return new HttpMessageConverters(converter);
   }
}
