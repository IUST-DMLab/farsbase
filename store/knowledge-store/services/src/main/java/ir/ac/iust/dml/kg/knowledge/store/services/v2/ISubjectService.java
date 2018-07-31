package ir.ac.iust.dml.kg.knowledge.store.services.v2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.Subject;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * <p>
 * Service to define triples
 */
@WebService
@Path("/v2/subjects")
@Api("/v2/subjects")
public interface ISubjectService {
    @GET
    @Path("/get")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Get subject in database")
    Subject get(
            @WebParam(name = "context") @QueryParam("context") String context,
            @WebParam(name = "subject") @QueryParam("subject") String subject);

    @GET
    @Path("/has/predicate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Get subject in database")
    PagingList<Subject> hasPredicate(
            @WebParam(name = "predicate") @QueryParam("predicate") String predicate,
            @WebParam(name = "page") @QueryParam("page") int page,
            @WebParam(name = "pageSize") @QueryParam("pageSize") int pageSize);

    @GET
    @Path("/has/value")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Get subject in database")
    PagingList<Subject> hasValue(
            @WebParam(name = "predicate") @QueryParam("predicate") String predicate,
            @WebParam(name = "object") @QueryParam("object") String object,
            @WebParam(name = "page") @QueryParam("page") int page,
            @WebParam(name = "pageSize") @QueryParam("pageSize") int pageSize);

    @GET
    @Path("/all")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Get subjects of database")
    PagingList<Subject> all(
            @WebParam(name = "page") @QueryParam("page") int page,
            @WebParam(name = "pageSize") @QueryParam("pageSize") int pageSize);
}
