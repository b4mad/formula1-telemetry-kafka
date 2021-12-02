This module provides an implementation of a decoder for
PC2 UDP messages (`PC2PacketParser.java`). This module 
includes a `PC2CamelApp` that allows you to send 
UDP messages directly to the netty listener. 

There is also a unit test that shows how a pcap file can be played back
using the `io.pkts.Pcap` library. This allows testing without
having to use `tcpdump`, `tcpreplay`, and `tcprewrite`.