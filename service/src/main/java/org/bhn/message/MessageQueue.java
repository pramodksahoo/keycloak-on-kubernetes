package org.bhn.message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.Optional;

@Slf4j
public class MessageQueue {
    private final SqsClient sqsClient;
    private final String sqlPersonManagemenQueque;
    private final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    public MessageQueue() {
        this.sqsClient = getSqsClient();
        this.sqlPersonManagemenQueque = Optional.ofNullable(System.getenv().get("SQS_PERSON_MANAGEMENT_SYNCH"))
                .orElseThrow(()->new RuntimeException("environment variable sql-person-management-synch not found"));
    }

    public void sendMessage(Object message) {
        try {
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(sqlPersonManagemenQueque).messageBody(ow.writeValueAsString(message))
                    .delaySeconds(5) .build();
            SendMessageResponse response = sqsClient.sendMessage(sendMsgRequest);
            log.info("Keycloak listener: message was sent with id: {}", response.messageId());
        } catch (Exception e) {
            log.error("Keycloak listener: error during sending a message: {}", e.getMessage());
        }
    }

    private SqsClient getSqsClient() {
        return SqsClient.builder().region(Region.US_WEST_2)
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();
    }
}
