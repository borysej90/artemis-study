package org.example.consumer;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.*;

public class Consumer {
    public static void main(String[] args) throws Exception {

        ServerLocator locator = ActiveMQClient.createServerLocator("tcp://localhost:61616");
        ClientSession session = locator.createSessionFactory().createSession();
        gracefulShutdown(session);

        ClientConsumer consumer = session.createConsumer("anyq");
        session.start();
        while (true) {
            ClientMessage msg = consumer.receive();
            System.out.println("Received \"" + msg.getBodyBuffer().readString() + '"');
        }
    }

    public static void gracefulShutdown(ClientSession session) {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                try {
                    session.stop();
                    System.out.println("Session closed");
                } catch (ActiveMQException e) {
                    System.out.println("Failed to stop session");
                    System.out.println(e);
                }
            }
        });
    }
}
