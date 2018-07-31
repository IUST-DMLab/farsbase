package ir.ac.iust.dml.kg.knowledge.runner.services.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.Run;
import ir.ac.iust.dml.kg.knowledge.runner.access.entities.RunState;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 */
@WebService
@Path("/v1/run")
@Api("/v1/run")
public interface IRunServices {
    @GET
    @Path("/run")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Run a definition by its title")
    Run run(@WebParam(name = "title") @QueryParam("title") String title);

    @GET
    @Path("/all/running")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "return list of all running")
    List<Run> allRunning();

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Run a definition by its title")
    PagingList<Run> search(
            @WebParam(name = "title") @QueryParam("title") String title,
            @WebParam(name = "state") @QueryParam("state") RunState state,
            @WebParam(name = "page") @QueryParam("page") int page,
            @WebParam(name = "pageSize") @QueryParam("pageSize") int pageSize);
}
