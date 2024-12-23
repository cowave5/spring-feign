package org.springframework.feign;

import org.reflections.Reflections;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.feign.annotation.FeignClient;
import org.springframework.lang.NonNull;

import java.util.*;

/**
 *
 * @author shanhuiming
 *
 */
public class FeignBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata meta, @NonNull BeanDefinitionRegistry registry) {
		TreeSet<String> packageSet = new TreeSet<>();
		String[] beanNames = registry.getBeanDefinitionNames();
		try {
			for (String beanName : beanNames) {
				BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
				String className = beanDefinition.getBeanClassName();
				if (className != null) {
					Class<?> clazz = Class.forName(className);
					// 启动类路径
					SpringBootApplication springBoot = clazz.getAnnotation(SpringBootApplication.class);
					if (springBoot != null) {
						packageSet.add(clazz.getPackage().getName());
					}
					// 扫描类路径
					ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
					if (componentScan != null) {
						packageSet.addAll(List.of(componentScan.basePackages()));
					}
				}
			}
		} catch (ClassNotFoundException e) {
			throw new ApplicationContextException("", e);
		}
		List<String> packageList = packageSet.stream().toList();

		// 去下重
		List<String> packages = new ArrayList<>();
        for(int right = packageList.size() - 1; right >= 0; right--){
            boolean hasPrefix = false;
            for(int i = right - 1; i >= 0; i--){
                if(packageList.get(right).startsWith(packageList.get(i) + ".")){
                    hasPrefix = true;
                    break;
                }
            }
            if(hasPrefix){
                continue;
            }
            packages.add(packageList.get(right));
        }

		// 扫描注册
		for(String pack : packages){
			Reflections reflections = new Reflections(pack);
			for(Class<?> clazz : reflections.getTypesAnnotatedWith(FeignClient.class)){
				FeignClient feign = AnnotationUtils.getAnnotation(clazz, FeignClient.class);
				if(feign == null){
					continue;
				}
				BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
				GenericBeanDefinition beanDefinition = (GenericBeanDefinition)builder.getBeanDefinition();
				beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
				beanDefinition.getPropertyValues().add("feignClass", clazz);
				beanDefinition.setBeanClass(FeignFactory.class);
				registry.registerBeanDefinition(clazz.getSimpleName(), beanDefinition);
			}
		}
	}
}
