package ir.ac.iust.dml.kg.knowledge.store.services.hello;

import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@WebService
@Path("/hello")
public interface IHelloService {
    @GET
    @Path("/{a}")
    @Produces(MediaType.TEXT_PLAIN)
    String hello(@PathParam("a") String text);
}
