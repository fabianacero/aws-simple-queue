package com.acero;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class SimpleQueue {

    private static final String CONFIG_FILE = "src/main/resources/application.properties";
    private static String QUEUE_NAME;

    public static void main(String[] args) {


        try {
            File file = new File(CONFIG_FILE);
            FileReader reader = new FileReader(file);
            Properties properties = new Properties();
            properties.load(reader);
            QUEUE_NAME = properties.getProperty("queue.name");
            reader.close();
        } catch (IOException e) {
            System.exit(0);
        }

        AmazonSQS sqs;
        sqs = AmazonSQSClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();
        CreateQueueRequest create_request = new CreateQueueRequest(QUEUE_NAME)
                .addAttributesEntry("DelaySeconds", "60")
                .addAttributesEntry("MessageRetentionPeriod", "86400");

        try {
            GetQueueUrlResult queueUrl = sqs.getQueueUrl(QUEUE_NAME);
            String queueUrlStr = queueUrl.getQueueUrl();
            if(!queueUrlStr.equals("")){
                throw new QueueNameExistsException("The queue already exist");
            }
        } catch (final QueueDoesNotExistException qdne) {
            // Creating Queue
            System.out.println("Creating Queue:\n");
            System.out.println(sqs.createQueue(create_request));
        } catch (final QueueNameExistsException qne) {
            // Printing Queues
            System.out.println("Your Queue already exists:\n");
            ListQueuesResult queueList = sqs.listQueues();
            for (String url : queueList.getQueueUrls()) {
                System.out.println(url);
                System.out.println("\n");
            }
        }
    }
}
