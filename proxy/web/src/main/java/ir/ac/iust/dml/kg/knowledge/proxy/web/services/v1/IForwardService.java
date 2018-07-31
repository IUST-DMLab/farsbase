package ir.ac.iust.dml.kg.knowledge.proxy.web.services.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.Forward;
import ir.ac.iust.dml.kg.knowledge.proxy.access.entities.Permission;
import ir.ac.iust.dml.kg.knowledge.proxy.web.services.v1.data.ForwardData;
import ir.ac.iust.dml.kg.knowledge.proxy.web.services.v1.data.PermissionData;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Rest: /rs/v1/forwards
 * SOA: /ws/v1/forwards
 */
@WebService
@Path("/v1/forwards")
@Api("/v1/forwards")
public interface IForwardService {
    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Return list of users role",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    List<String> login();

    @POST
    @Path("/permission/edit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Insert or update permission",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    Permission permission(@Valid PermissionData data);

    @GET
    @Path("/permission/list")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Return list of available permissions",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    List<Permission> permissions();

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Return list of available forward",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    List<Forward> forwards();

    @POST
    @Path("/forward")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Insert or update forwards",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    ForwardData edit(@Valid ForwardData data);

}
