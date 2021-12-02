# Camel K implementation

### Prerequisites

* Download the `kamel` cli from https://github.com/apache/camel-k/releases

### Install integration

The integration opens a UDP port at 10666

To open the port to external clients a service listening on port 30666 is installed

```
oc apply -f service.yaml
```

Note: This folder contains two versions of the PC2 packet decoder. 
1. `PC2DecoderStandAlone.java` contains all of the classes for decoding in a single file. In order to run this file, just do `kamel run --name ingress PC2DecoderStandAlone.java --dev --pod-template template.yaml`
2. `Ingress.java` provides a much more maintainable version of the PC2 decoder in that it utilizes the `pc2-decoder` module. The `Ingress.java` file simply defines the route and delegates decoding to the `PC2PacketParser` defined in the `pcs-decoder` module. In order to deploy this version of the decoder, you need to include the `pc2-decoder` module as a dependency for `kamel`. Unfortunately, at the moment, camel-k doesn't work with local jars. Therefore, you either have to push the pcs-converter jar to maven central or configure the maven settings according to https://camel.apache.org/camel-k/1.7.x/configuration/maven.html. Once the configuration is completed, run `kamel run -d mvn:io.ppatierno:f1-telemetry-pc2-decoder:1.0 --name ingress Ingress.java --dev --pod-template template.yaml`

Send a UDP packet to the integration

```
date | nc -u 128.52.60.32 30666
```

## Telemetry

### Capture telemetry stream locally

Listen locally via `nc` and write the stream to a pcap file. 
Install https://www.telemetrytool.com/ and configure UDP replay to the IP and port.

```
nc -u -k -l 192.168.1.111 10666
sudo tcpdump -i en7 -n -w ams2.pcap udp port 10666
tcpdump -r ams2.pcap
```

A sample stream is here in `ams2.pcap.gz`

### Replaying the UDP stream

You need to rewrite the ethernet and udp layer of the packets

```
export DST=128.52.60.32
export DSTMAC=b4:fb:e4:24:0f:4d
export SRC=192.168.1.111
export SRCMAC=00:50:b6:a1:77:87
export DUMP=ams2.pcap
tcprewrite --infile=$DUMP --outfile=temp1.pcap --dstipmap=0.0.0.0/0:$DST --enet-dmac=$DSTMAC
tcprewrite --infile=temp1.pcap --outfile=temp2.pcap --srcipmap=0.0.0.0/0:$SRC --enet-smac=$SRCMAC
tcprewrite --infile=temp2.pcap --outfile=temp3.pcap --portmap=10666:30666
tcprewrite --infile=temp3.pcap --outfile=final.pcap --fixcsum
```

Now we can replay the stream

```
sudo tcpreplay --intf1=en7 -L 1 final.pcap
```

