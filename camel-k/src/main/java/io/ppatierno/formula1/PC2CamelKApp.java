/*
 * Copyright Paolo Patierno.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.ppatierno.formula1;

import io.ppatierno.formula1.pc2.PC2PacketParser;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class PC2CamelKApp {

    private static final Logger log = LoggerFactory.getLogger(PC2CamelKApp.class);

    public static void main(String[] args) throws Exception {
        PC2CamelKAppConfig config = PC2CamelKAppConfig.fromEnv();
        CamelContext camelContext = new DefaultCamelContext();

        log.info("Config: {}", config);

        PC2PacketParser packetParser = new PC2PacketParser();
        camelContext.getRegistry().bind("packet-decoder", new Decoder<>(packetParser));

        camelContext.addRoutes(new Ingress(config));

        CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    camelContext.close();
                } catch (Exception e) {
                    log.error("Error closing CamelContext", e);
                } finally {
                    latch.countDown();
                }
            }
        });

        try {
            camelContext.start();
            latch.await();
        } catch (Throwable e) {
            log.error("Error starting CamelContext", e);
            System.exit(1);
        }
        System.exit(0);
    }
}