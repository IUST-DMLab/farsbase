package ir.ac.iust.dml.kg.knowledge.expert.web.services.hello;

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
    String hello();
}
