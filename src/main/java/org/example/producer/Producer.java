package org.example.producer;

import org.apache.activemq.artemis.api.core.*;
import org.apache.activemq.artemis.api.core.client.*;
import org.example.utils.Artemis;

public class Producer {
    public static void main(String[] args) throws Exception {
        final String address = "client-facing";
        Artemis artemis = new Artemis("tcp://192.168.56.11:61616");
        artemis.registerGracefulShutdown();

        ClientProducer producer = artemis.getSession().createProducer(address);
        artemis.startSession();
        try (ClientSession session = artemis.getSession()) {
            int counter = 0;
            while (!session.isClosed()) {
                ClientMessage msg = session.createMessage(true);
                ActiveMQBuffer buffer = msg.getBodyBuffer();
                buffer.writeString("The Message - " + counter);
                producer.send(msg);
                System.out.println("Sent \"" + buffer.readString() + '"');
                counter++;
            }
        }
    }
}
