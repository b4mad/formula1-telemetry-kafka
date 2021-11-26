# Camel K implementation

### Prerequisites

* Download the `kamel` cli from https://github.com/apache/camel-k/releases

### Install integration

The integration opens a UDP port at 10666

To open the port to external clients a service listening on port 30666 is installed

```
oc apply -f service.yaml
kamel run --name ingress Ingress.java Decoder.java --dev --pod-template template.yaml
```

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

