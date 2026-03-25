package com.menkaix.backlogs.configuration;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.menkaix.backlogs.mcptools.PersonToolsRegistry;
import com.menkaix.backlogs.mcptools.ProjectToolsRegistry;
import com.menkaix.backlogs.mcptools.SkillToolsRegistry;
import com.menkaix.backlogs.mcptools.TaskToolsRegistry;

@Configuration
public class MCPConfiguration {

    @Bean
    ToolCallbackProvider backlogsMcpTools(
            ProjectToolsRegistry projectTools,
            TaskToolsRegistry taskTools,
            PersonToolsRegistry personTools,
            SkillToolsRegistry skillTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(projectTools, taskTools, personTools, skillTools)
                .build();
    }
}
