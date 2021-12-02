package io.ppatierno.formula1.pc2;

import io.netty.buffer.Unpooled;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.packet.UDPPacket;
import io.pkts.protocol.Protocol;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PC2PacketParserTest {
    private PC2PacketParser parser;

    @BeforeEach
    void setUp() {
        parser = new PC2PacketParser();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testParse() {
        // This test loads a pcap file (contained in the resources directory)
        // and plays it back using the io.pkts.Pcap library. This allows debugging
        // of the parsing logic.
        var dataFile = this.getClass().getResource("pc2_v2_1.pcap");

        assertNotNull(dataFile);

        try (var dataStream = dataFile.openStream()) {
            var pcap = Pcap.openStream(dataStream);

            pcap.loop(packet -> {
                if (packet.hasProtocol(Protocol.UDP)) {
                    UDPPacket udpPacket = (UDPPacket) packet.getPacket(Protocol.UDP);
                    Buffer buffer = udpPacket.getPayload();

                    if (buffer != null) {
                        var data = buffer.getArray();

                        var result = parser.parse(Unpooled.wrappedBuffer(data));

                        assertEquals(PacketTypes.GameState, result.getPacketType());
                    }
                }

                return true;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}