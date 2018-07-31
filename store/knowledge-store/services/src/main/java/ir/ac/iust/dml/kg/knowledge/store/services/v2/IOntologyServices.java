package ir.ac.iust.dml.kg.knowledge.store.services.v2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.Ontology;
import ir.ac.iust.dml.kg.knowledge.store.services.v2.data.OntologyData;

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
 * <p>
 * Service to define and use ontology
 */
@WebService
@Path("/v2/ontology")
@Api("/v2/ontology")
public interface IOntologyServices {
    @POST
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Insert or update ontology")
    OntologyData insert(@Valid OntologyData data);

    @POST
    @Path("/batch/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Insert or update ontology")
    List<OntologyData> batchInsert(@Valid List<OntologyData> data);


    @POST
    @Path("/remove")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Remove ontology")
    Ontology remove(@ApiParam(example = "http://kg.dml.iust.ac.ir")
                    @WebParam(name = "context") @QueryParam("context") String context,
                    @ApiParam(required = true, example = "http://url.com/subject")
                    @WebParam(name = "subject") @QueryParam("subject") String subject,
                    @ApiParam(required = true, example = "http://url.com/predicate")
                    @WebParam(name = "predicate") @QueryParam("predicate") String predicate,
                    @ApiParam(required = true, example = "http://url.com/object")
                    @WebParam(name = "object") @QueryParam("object") String object);

    @GET
    @Path("/ontology")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Return ontology by (context, subject, predicate, object)")
    Ontology ontology(@ApiParam(example = "http://kg.dml.iust.ac.ir")
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
    @ApiOperation(value = "Search ontology by (context, subject, predicate, object)")
    PagingList<Ontology> search(@WebParam(name = "context") @QueryParam("context") String context,
                                @WebParam(name = "contextLike") @QueryParam("contextLike") Boolean contextLike,
                                @WebParam(name = "subject") @QueryParam("subject") String subject,
                                @WebParam(name = "subjectLike") @QueryParam("subjectLike") Boolean subjectLike,
                                @WebParam(name = "predicate") @QueryParam("predicate") String predicate,
                                @WebParam(name = "predicateLike") @QueryParam("predicateLike") Boolean predicateLike,
                                @WebParam(name = "object") @QueryParam("object") String object,
                                @WebParam(name = "objectLike") @QueryParam("objectLike") Boolean objectLike,
                                @WebParam(name = "approved") @QueryParam("approved") boolean approved,
                                @WebParam(name = "page") @QueryParam("page") int page,
                                @WebParam(name = "pageSize") @QueryParam("pageSize") int pageSize);
}
