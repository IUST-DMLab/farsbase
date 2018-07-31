package ir.ac.iust.dml.kg.knowledge.proxy.web.services.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import ir.ac.iust.dml.kg.knowledge.proxy.web.services.v1.data.ProfileData;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Rest: /rs/v1/profile
 * SOA: /ws/v1/users
 */
@WebService
@Path("/v1/profile")
@Api("/v1/profile")
public interface IProfileServices {
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
    @ApiOperation(value = "Edit profile data of logged user",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    ProfileData edit(@Valid ProfileData data);


}
