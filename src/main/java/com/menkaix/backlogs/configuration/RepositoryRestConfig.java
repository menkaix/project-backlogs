package com.menkaix.backlogs.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.menkaix.backlogs.models.entities.AbstractEntity;

@Configuration
public class RepositoryRestConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(AbstractEntity.class));

        Set<BeanDefinition> components = scanner.findCandidateComponents(
                "com.menkaix.backlogs.models.entities");

        List<Class<?>> entityClasses = new ArrayList<>();
        for (BeanDefinition bd : components) {
            try {
                entityClasses.add(Class.forName(bd.getBeanClassName()));
            } catch (ClassNotFoundException ignored) {
            }
        }

        config.exposeIdsFor(entityClasses.toArray(new Class[0]));
    }
}
