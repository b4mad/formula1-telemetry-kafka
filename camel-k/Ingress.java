// camel-k: language=java


import org.apache.camel.builder.RouteBuilder;


public class Ingress extends RouteBuilder {
  @Override
  public void configure() throws Exception {

      // from("timer:java?period=1000")
      //   .setHeader("example")
      //     .constant("Java")
      //   .setBody()
      //     .simple("Hello Camel K route written in ${header.example}.")
      // from("netty:udp://0.0.0.0:10666?sync=false&decoders=#packet-decoder")
      from("netty:udp://0.0.0.0:10666?sync=false")
          // .multicast()
          // .parallelProcessing()
          // .to("direct:raw-packets", "direct:events", "direct:drivers", "log:info")
          .log("Raw Packet: ${body}")
          .to("direct:raw-packets")
          .routeId("udp-dispatcher");
  }
}
