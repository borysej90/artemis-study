package producer;

import org.apache.activemq.artemis.api.core.ActiveMQBuffer;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.*;

public class Producer {
    public static void main(String[] args) throws Exception {

        ServerLocator locator = ActiveMQClient.createServerLocator("tcp://localhost:61616");
        ClientSession session = locator.createSessionFactory().createSession();
        gracefulShutdown(session);

        ClientProducer producer = session.createProducer("anyq.addr");
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
                }
            }
        });
    }
}
