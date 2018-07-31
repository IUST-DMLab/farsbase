package ir.ac.iust.dml.kg.knowledge.proxy.web.services.hello;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@WebService
@Path("/hello")
public interface IHelloService {
    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Say hello to current user", authorizations = {@Authorization("basic"), @Authorization("session")})
    String hello();
}
