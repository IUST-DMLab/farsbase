package ir.ac.iust.dml.kg.knowledge.store.services.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.Triple;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.TripleState;
import ir.ac.iust.dml.kg.knowledge.store.services.v1.data.TripleData;

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
 * Rest: /rs/v1/triples
 * SOA: /ws/v1/triples
 */
@Deprecated
@WebService
@Path("/v1/triples")
@Api("/v1/triples")
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
    @ApiOperation(value = "Insert or update triple")
    Boolean batchInsert(@Valid List<TripleData> data);


    @POST
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Remove triple")
    Triple remove(@ApiParam(required = false, example = "http://kg.dml.iust.ac.ir")
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
    Triple triple(@ApiParam(required = false, example = "http://kg.dml.iust.ac.ir")
                  @WebParam(name = "context") @QueryParam("context") String context,
                  @ApiParam(required = true, example = "http://url.com/subject")
                  @WebParam(name = "subject") @QueryParam("subject") String subject,
                  @ApiParam(required = true, example = "http://url.com/predicate")
                  @WebParam(name = "predicate") @QueryParam("predicate") String predicate,
                  @ApiParam(required = true, example = "http://url.com/object")
                  @WebParam(name = "object") @QueryParam("object") String object);

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Search triples by (context, subject, predicate, object)")
    PagingList<Triple> search(@WebParam(name = "context") @QueryParam("context") String context,
                              @WebParam(name = "useRegexForContext") @QueryParam("useRegexForContext") Boolean useRegexForContext,
                              @WebParam(name = "subject") @QueryParam("subject") String subject,
                              @WebParam(name = "useRegexForSubject") @QueryParam("useRegexForSubject") Boolean useRegexForSubject,
                              @WebParam(name = "predicate") @QueryParam("predicate") String predicate,
                              @WebParam(name = "useRegexForPredicate") @QueryParam("useRegexForPredicate") Boolean useRegexForPredicate,
                              @WebParam(name = "object") @QueryParam("object") String object,
                              @WebParam(name = "useRegexForObject") @QueryParam("useRegexForObject") Boolean useRegexForObject,
                              @WebParam(name = "page") @QueryParam("page") int page,
                              @WebParam(name = "pageSize") @QueryParam("pageSize") int pageSize);

    @GET
    @Path("/export/{state}/{format}")
    @Produces(MediaType.TEXT_PLAIN)
    @WebMethod
    @ApiOperation(value = "Return a list of rdf that must inserted in specified format")
    String export(
            @WebParam(name = "state") @PathParam("state") TripleState state,
            @WebParam(name = "format") @PathParam("format") ExportFormat format,
            @WebParam(name = "epoch") @QueryParam("epoch") Long epoch,
            @WebParam(name = "page") @QueryParam("page") int page,
            @WebParam(name = "pageSize") @QueryParam("pageSize") int pageSize);


}
