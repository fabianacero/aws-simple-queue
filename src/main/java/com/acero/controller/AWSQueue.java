package com.acero.controller;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.util.List;

@RestController
public class AWSQueue {

    private static AmazonSQS sqs;
    private static Jedis redisDb;
    private static String queueUrl;

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.pwd}")
    private String redisPwd;

    public AWSQueue() {
        sqs = AmazonSQSClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    /**
     * Crea una cola desde AWS
     */
    @RequestMapping("/create/{queueName}")
    private String createAWSQueue(@PathVariable String queueName) {

        String queueUrl = "";
        StringBuilder responseQueueCreation = new StringBuilder("");
        CreateQueueRequest create_request = new CreateQueueRequest(queueName)
                .addAttributesEntry("DelaySeconds", "60")
                .addAttributesEntry("MessageRetentionPeriod", "86400");
        try {
            GetQueueUrlResult remoteQueueUrl = sqs.getQueueUrl(queueName);
            queueUrl = remoteQueueUrl.getQueueUrl();
            if (!queueUrl.equals("")) {
                throw new QueueNameExistsException("The queue already exist");
            }
        } catch (final QueueDoesNotExistException qdne) {
            // Creating Queue
            responseQueueCreation.append("Creating Queue:\n");
            queueUrl = sqs.createQueue(create_request).getQueueUrl();
            responseQueueCreation.append(queueUrl);
            System.out.println(responseQueueCreation);
        } catch (final QueueNameExistsException qne) {
            // Printing Queues
            responseQueueCreation.append("Your Queue already exists:\n");
            ListQueuesResult queueList = sqs.listQueues();
            for (String url : queueList.getQueueUrls()) {
                responseQueueCreation.append(url + "<br />");
            }
            System.out.println(responseQueueCreation);
        }

        saveQueueUrl(queueUrl);

        return responseQueueCreation.toString();
    }

    /**
     * Envia un mensaje a la cola seleccionada aws
     *
     * @param message Mensaje enviado a la cola
     * @return boolean
     */
    @RequestMapping("/send/{message}")
    private String sendQueueMsj(@PathVariable String message) {

        if (getRecentQueueUrl()) return "No queue defined";
        return sqs.sendMessage(new SendMessageRequest(queueUrl, message)).toString();
    }

    /**
     * Receive all queue Messages
     *
     * @param queueUrl
     */
    @RequestMapping("/list/{queueName}")
    private String receiveQueueMessages(@PathVariable String queueUrl) {
        if (getRecentQueueUrl()) return "No queue defined";
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        StringBuilder messageListResponde = new StringBuilder("");

        for (Message message : messages) {
            messageListResponde.append(message.getBody() + "<br />");
        }
        System.out.println(messageListResponde);
        return messageListResponde.toString();
    }

    /**
     * Guarda la url de la cola recientemente creada
     *
     * @param queueUrl
     */
    private void saveQueueUrl(String queueUrl) {
        redisDb = new Jedis(redisHost);
        redisDb.auth(redisPwd);
        redisDb.set("queue-url", queueUrl);
    }

    /**
     * Obtiene la url de la ultima cola creada
     *
     * @return
     */
    private boolean getRecentQueueUrl() {
        redisDb = new Jedis(redisHost);
        redisDb.auth(redisPwd);
        queueUrl = redisDb.get("queue-url");
        return queueUrl.isEmpty();
    }
}
