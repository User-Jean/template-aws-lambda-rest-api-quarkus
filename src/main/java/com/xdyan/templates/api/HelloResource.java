package com.xdyan.templates.api;

import com.xdyan.templates.api.model.HelloResponse;
import jakarta.ws.rs.core.Response;

public final class HelloResource implements DefaultResource {

  @Override
  public Response getHello() {
    var helloResponse = new HelloResponse()
        .message("Hello World");
    return Response.ok(helloResponse).build();
  }
}
