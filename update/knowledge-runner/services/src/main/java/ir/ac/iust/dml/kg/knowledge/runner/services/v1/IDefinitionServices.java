package ir.ac.iust.dml.kg.knowledge.runner.services.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ir.ac.iust.dml.kg.knowledge.runner.services.v1.data.DefinitionData;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 */
@WebService
@Path("/v1/definitions")
@Api("/v1/definitions")
public interface IDefinitionServices {
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Get all definitions")
    List<DefinitionData> all();

    @POST
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Insert or update triple")
    DefinitionData insert(@Valid DefinitionData data);

}
