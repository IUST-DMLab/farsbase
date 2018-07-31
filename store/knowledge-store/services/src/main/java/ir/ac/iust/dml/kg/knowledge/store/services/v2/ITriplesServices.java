package ir.ac.iust.dml.kg.knowledge.store.services.v2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ir.ac.iust.dml.kg.knowledge.store.services.v2.data.TripleData;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 *
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Rest: /rs/v2/triples
 * SOA: /ws/v2/triples
 *
 * Service to define versions
 */
@WebService
@Path("/v2/triples")
@Api("/v2/triples")
public interface ITriplesServices {
    @GET
    @Path("/version/new")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Insert or update triple")
    Integer newVersion(
            @ApiParam(required = true, example = "wiki")
            @WebParam(name = "module") @QueryParam("module") String module);

    @GET
    @Path("/version/activate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Insert or update triple")
    Boolean activateVersion(
            @ApiParam(required = true, example = "wiki")
            @WebParam(name = "module") @QueryParam("module") String module,
            @ApiParam(required = false, value = "if be null set it to next version")
            @WebParam(name = "version") @QueryParam("version") Integer version);

    @POST
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Insert or update triple")
    Boolean insert(@Valid TripleData data);

    @POST
    @Path("/batch/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Insert or update triple and return affected subject")
    Boolean batchInsert(@Valid List<TripleData> data);

  @GET
  @Path("/remove")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @WebMethod
  @ApiOperation(value = "Remove triple")
  List<TripleData> remove(@ApiParam(required = false, example = "http://kg.dml.iust.ac.ir")
                          @WebParam(name = "context") @QueryParam("context") String context,
                          @ApiParam(required = true, example = "http://url.com/subject")
                          @WebParam(name = "subject") @QueryParam("subject") String subject,
                          @ApiParam(required = true, example = "http://url.com/predicate")
                          @WebParam(name = "predicate") @QueryParam("predicate") String predicate,
                          @ApiParam(required = true, example = "http://url.com/object")
                          @WebParam(name = "object") @QueryParam("object") String object);

    @GET
    @Path("/triple")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Return triples by (context, subject, predicate, object)")
    List<TripleData> triple(@ApiParam(required = false, example = "http://kg.dml.iust.ac.ir")
                            @WebParam(name = "context") @QueryParam("context") String context,
                            @ApiParam(required = true, example = "http://url.com/subject")
                            @WebParam(name = "subject") @QueryParam("subject") String subject,
                            @ApiParam(required = true, example = "http://url.com/predicate")
                            @WebParam(name = "predicate") @QueryParam("predicate") String predicate,
                            @ApiParam(required = true, example = "http://url.com/object")
                            @WebParam(name = "object") @QueryParam("object") String object);


}
