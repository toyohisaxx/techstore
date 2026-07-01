package cl.techstore.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import java.time.Instant;

@Service
public class AuditService {

    private final SqsClient sqsClient;

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    public AuditService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @Async
    public void publicarAuditoria(String accion, Long productoId, String nombre, String usuario) {
        String mensaje = String.format(
                "{\"accion\":\"%s\",\"productoId\":%d,\"nombre\":\"%s\",\"usuario\":\"%s\",\"fecha\":\"%s\"}",
                accion, productoId, nombre, usuario, Instant.now().toString()
        );
        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(mensaje)
                .build());
        System.out.println("[Audit] Publicado en SQS: " + mensaje);
    }
}