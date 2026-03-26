package com.menkaix.backlogs.configuration;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.ai.vertexai.embedding.VertexAiEmbeddingConnectionDetails;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingModel;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "qdrant.enabled", havingValue = "true")
public class QdrantConfiguration {

    @Value("${qdrant.host:localhost}")
    private String host;

    @Value("${qdrant.port:6334}")
    private int port;

    @Value("${qdrant.use-tls:false}")
    private boolean useTls;

    @Value("${qdrant.api-key:}")
    private String apiKey;

    @Value("${gcp.config.project}")
    private String gcpProject;

    @Value("${gcp.config.location}")
    private String gcpLocation;

    @Bean
    public EmbeddingModel embeddingModel() {
        VertexAiEmbeddingConnectionDetails connectionDetails = VertexAiEmbeddingConnectionDetails.builder()
                .projectId(gcpProject)
                .location(gcpLocation)
                .build();
        VertexAiTextEmbeddingOptions options = VertexAiTextEmbeddingOptions.builder()
                .model("text-multilingual-embedding-002")
                .build();
        // Override dimensions() to avoid a startup API call to Vertex AI
        return new VertexAiTextEmbeddingModel(connectionDetails, options) {
            @Override
            public int dimensions() {
                return 768;
            }
        };
    }

    @Bean
    public QdrantClient qdrantClient() {
        QdrantGrpcClient.Builder builder = QdrantGrpcClient.newBuilder(host, port, useTls);
        if (apiKey != null && !apiKey.isBlank()) {
            builder.withApiKey(apiKey);
        }
        return new QdrantClient(builder.build());
    }

    @Bean(name = "taskContextVectorStore")
    public VectorStore taskContextVectorStore(QdrantClient qdrantClient, EmbeddingModel embeddingModel) {
        return QdrantVectorStore.builder(qdrantClient, embeddingModel)
                .collectionName("task-contexts")
                .initializeSchema(true)
                .build();
    }

    @Bean(name = "issueContextVectorStore")
    public VectorStore issueContextVectorStore(QdrantClient qdrantClient, EmbeddingModel embeddingModel) {
        return QdrantVectorStore.builder(qdrantClient, embeddingModel)
                .collectionName("issue-contexts")
                .initializeSchema(true)
                .build();
    }

    @Bean(name = "projectTreeVectorStore")
    public VectorStore projectTreeVectorStore(QdrantClient qdrantClient, EmbeddingModel embeddingModel) {
        return QdrantVectorStore.builder(qdrantClient, embeddingModel)
                .collectionName("project-trees")
                .initializeSchema(true)
                .build();
    }
}
