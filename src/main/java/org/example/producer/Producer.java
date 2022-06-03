package org.example.producer;

import org.apache.activemq.artemis.api.core.*;
import org.apache.activemq.artemis.api.core.client.*;

public class Producer {
    public static void main(String[] args) throws Exception {
        final String address = "anyq.addr", queueName = "anyq";
        ServerLocator locator = ActiveMQClient.createServerLocator("tcp://localhost:61616");
        ClientSession session = locator.createSessionFactory().createSession();
        gracefulShutdown(session, queueName);

        QueueConfiguration qc = new QueueConfiguration(queueName);
        qc.setAddress(address);
        qc.setDurable(true);
        qc.setRoutingType(RoutingType.ANYCAST);
        session.createQueue(qc);

        ClientProducer producer = session.createProducer(address);
        session.start();
        int counter = 0;
        while (true) {
            ClientMessage msg = session.createMessage(true);
            ActiveMQBuffer buffer = msg.getBodyBuffer();
            buffer.writeString("The Message - " + counter);
            producer.send(msg);
            System.out.println("Sent \"" + buffer.readString() + '"');
            counter++;
        }
    }

    public static void gracefulShutdown(ClientSession session, String queueName) {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                try {
                    session.queueQuery(SimpleString.toSimpleString(queueName));
                    session.deleteQueue(queueName);
                    System.out.println("Queue deleted");
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
