package ir.ac.iust.dml.kg.knowledge.expert.web.services.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.Ticket;
import ir.ac.iust.dml.kg.knowledge.expert.access.stats.KeyCount;
import ir.ac.iust.dml.kg.knowledge.store.client.Vote;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Rest: /rs/v1/experts
 * SOA: /ws/v1/experts
 */
@WebService
@Path("/v1/experts")
@Api("/v1/experts")
public interface IExpertServices {
    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Return list of users role",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    List<String> login();

    @GET
    @Path("/triples/new/random")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Get a list of new triple and return as ticket",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    List<Ticket> triplesNewByRandom(@WebParam(name = "count") @QueryParam("count") int count);

    @GET
    @Path("/triples/new/subject")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Get a list of new triple for a subject and return as ticket",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    List<Ticket> triplesNewBySubject(@WebParam(name = "sourceModule") @QueryParam("sourceModule") String sourceModule,
                                     @WebParam(name = "subjectQuery") @QueryParam("subjectQuery") String subjectQuery,
                                     @WebParam(name = "subjectMatch") @QueryParam("subjectMatch") String subjectMatch,
                                     @WebParam(name = "size") @QueryParam("size") Integer size);

    @GET
    @Path("/triples/current")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Return a list of current assigned ticket",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    PagingList<Ticket> triplesCurrent(
            @WebParam(name = "subject") @QueryParam("subject") String subject,
            @WebParam(name = "page") @QueryParam("page") int page,
            @WebParam(name = "pageSize") @QueryParam("pageSize") int pageSize
    );

    @GET
    @Path("/subjects/current")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Return a list of current assigned subjects",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    PagingList<KeyCount> subjectsCurrent(
            @WebParam(name = "page") @QueryParam("page") int page,
            @WebParam(name = "pageSize") @QueryParam("pageSize") int pageSize
    );


    @GET
    @Path("/vote")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Vote on ticket",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    Boolean vote(
            @WebParam(name = "identifier") @QueryParam("identifier") String identifier,
            @WebParam(name = "vote") @QueryParam("vote") Vote vote
    );

    @POST
    @Path("/vote/batch")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Vote on ticket",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    Boolean batchVote(@Valid HashMap<String, Vote> votes);

}
