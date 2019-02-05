package com.acero;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SimpleQueue {

    public static void main(String[] args) {
        System.out.println("Hi");
        SpringApplication.run(SimpleQueue.class, args);
        /*try {
            String queueUrl = getAWSQueue();
            sendQueueMsj(queueUrl, "Test Messager" + (new Random()).nextInt(50));
            receiveQueueMessages(queueUrl);
        } catch (AmazonServiceException e) {
        }*/
    }

}
