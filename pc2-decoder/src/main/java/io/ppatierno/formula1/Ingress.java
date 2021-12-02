// camel-k: language=java
package io.ppatierno.formula1;

import org.apache.camel.builder.RouteBuilder;

/**
 * This classes configures the routing for receiving and decoding
 * pc2 packets.
 */
public class Ingress extends RouteBuilder {
    private final PC2CamelAppConfig config;

    public Ingress(PC2CamelAppConfig config) {
        this.config = config;
    }

    @Override
    public void configure() {

        String connString = String.format(
            "netty:udp://0.0.0.0:%d?decoders=#packet-decoder&sync=false",
            this.config.getUdpPort());

        from(connString)
            .log("Raw Packet: ${body}")
            .routeId("udp-dispatcher");
    }
}
