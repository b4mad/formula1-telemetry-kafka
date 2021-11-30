package io.ppatierno.formula1.pc2;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

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
    void parse() {
        var dataFile = this.getClass().getResource("data1.pcap");

        Assertions.assertNotNull(dataFile);

        try(var dataStream = dataFile.openStream()) {
            var data = dataStream.readAllBytes();
            parser.parse(Unpooled.wrappedBuffer(data));
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}