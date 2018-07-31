package ir.ac.iust.dml.kg.knowledge.expert.web.services.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.User;
import ir.ac.iust.dml.kg.knowledge.expert.web.services.v1.data.UserData;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Rest: /rs/v1/users
 * SOA: /ws/v1/users
 */
@WebService
@Path("/v1/users")
@Api("/v1/users")
public interface IUserServices {
    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Return list of users role",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    List<String> login();

    @POST
    @Path("/edit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Insert or update users",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    User edit(@Valid UserData data);

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Search user by (name and username)",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    PagingList<User> search(@WebParam(name = "name") @QueryParam("name") String name,
                            @WebParam(name = "username") @QueryParam("username") String username,
                            @WebParam(name = "page") @QueryParam("page") int page,
                            @WebParam(name = "pageSize") @QueryParam("pageSize") int pageSize);
}
