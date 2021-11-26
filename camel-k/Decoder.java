// camel-k: language=java

import org.apache.camel.builder.RouteBuilder;

public class Decoder extends RouteBuilder {
  @Override
  public void configure() throws Exception {

      from("direct:raw-packets")
          .log("In Decoder ${body}");
  }
}
