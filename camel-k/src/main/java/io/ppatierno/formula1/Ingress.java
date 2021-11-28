// camel-k: language=java
package io.ppatierno.formula1;

import org.apache.camel.builder.RouteBuilder;

/**
 * This classes configures the routing for receiving and decoding
 * pc2 packets.
 */
public class Ingress extends RouteBuilder {
    private final PC2CamelKAppConfig config;

    public Ingress(PC2CamelKAppConfig config) {
        this.config = config;
    }

    @Override
    public void configure() throws Exception {

        String connString = String.format(
            "netty:udp://0.0.0.0:%d?decoders=#packet-decoder&disconnectOnNoReply=false&sync=false",
            this.config.getUdpPort());

        from(connString)
            .log("Raw Packet: ${body}")
            .to("direct:raw-packets")
            .routeId("udp-dispatcher");
    }
}
