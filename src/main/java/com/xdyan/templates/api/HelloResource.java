package com.xdyan.templates.api;

import jakarta.ws.rs.core.Response;

import java.util.Map;

public class HelloResource implements DefaultResource {
  @Override
  public Response getHello() {
    var map = Map.of("message", "Hello World");
    return Response.ok(map).build();
  }
}
