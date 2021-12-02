// camel-k: language=java

import org.apache.camel.builder.RouteBuilder;
import io.ppatierno.formula1;
import io.ppatierno.formula1.pc2;

/**
 * This classes configures the routing for receiving and decoding
 * pc2 packets.
 */
public class Ingress extends RouteBuilder {
    private PC2PacketParser packetParser = new PC2PacketParser();

    @BindToRegistry("decoder")
    public ChannelHandler getDecoder() {
        return new Decoder<>(packetParser);
    }

    @Override
    public void configure() {

        from("netty:udp://0.0.0.0:10666?decoders=#decoder&sync=false")
            .log("Raw Packet: ${body}")
            .routeId("udp-dispatcher");
    }
}