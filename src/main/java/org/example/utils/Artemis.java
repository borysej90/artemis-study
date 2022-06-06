package org.example.utils;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.QueueConfiguration;
import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ServerLocator;

import java.util.Random;

public class Artemis {
    private QueueConfiguration queueConfig;
    private String queueName;
    private ClientSession session;

    public Artemis(String url) throws Exception {
        ServerLocator locator = ActiveMQClient.createServerLocator(url);
        session = locator.createSessionFactory().createSession();
    }

    public Artemis(String url, String queueName) throws Exception {
        this(url);
        this.queueName = queueName;
    }

    public void startSession() throws ActiveMQException {
        session.start();
    }

    public void stop() throws ActiveMQException {
        if (queueConfig != null) {
            deleteQueue();
        }
        session.stop();
    }

    public void createAnycastQueue(String address, String queueName) throws ActiveMQException {
        this.queueName = queueName; // FIXME: replaces queueName from second constructor
        queueConfig = new QueueConfiguration(queueName);
        queueConfig.setAddress(address);
        queueConfig.setDurable(true);
        queueConfig.setRoutingType(RoutingType.ANYCAST);
        session.createQueue(this.queueConfig);
    }

    public void deleteQueue() throws ActiveMQException {
        this.session.deleteQueue(queueConfig.getName());
    }

    public ClientSession getSession() {
        return session;
    }

    public String getQueueName() {
        return queueName;
    }

    public void registerGracefulShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                try {
                    if (queueConfig != null) {
                        deleteQueue();
                        System.out.println("Queue deleted");
                    }
                    session.stop();
                    System.out.println("Session closed");
                } catch (ActiveMQException e) {
                    System.out.println("Failed to stop session");
                    System.out.println(e.getMessage());
                }
            }
        });
    }
}
