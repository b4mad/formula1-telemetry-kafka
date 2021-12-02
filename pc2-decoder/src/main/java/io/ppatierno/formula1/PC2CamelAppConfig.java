/*
 * Copyright Paolo Patierno.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.ppatierno.formula1;

public class PC2CamelAppConfig {

    private static final String UDP_PORT_ENV = "UDP_PORT";

    private static final int DEFAULT_UDP_PORT = 10666;

    private final int udpPort;

    private PC2CamelAppConfig(int udpPort) {
        this.udpPort = udpPort;
    }

    public static PC2CamelAppConfig fromEnv() {
        int udpPort = System.getenv(UDP_PORT_ENV) == null
            ? DEFAULT_UDP_PORT
            : Integer.parseInt(System.getenv(UDP_PORT_ENV));

        return new PC2CamelAppConfig(udpPort);
    }

    public int getUdpPort() {
        return udpPort;
    }

    @Override
    public String toString() {
        return "F1CamelKAppConfig[" +
            "udpReceivePort=" + this.udpPort +
            "]";
    }
}
