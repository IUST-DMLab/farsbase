package ir.ac.iust.dml.kg.knowledge.expert.web.services.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.expert.access.entities.Ticket;
import ir.ac.iust.dml.kg.knowledge.expert.access.stats.KeyCount;
import ir.ac.iust.dml.kg.knowledge.expert.access.stats.UserStats;
import ir.ac.iust.dml.kg.knowledge.expert.access.stats.UserVoteStats;
import ir.ac.iust.dml.kg.knowledge.store.client.Vote;

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
 *
 * Rest: /rs/v1/experts
 * SOA: /ws/v1/experts
 */
@WebService
@Path("/v1/reports")
@Api("/v1/reports")
public interface IReportService {
    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Return list of users role",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    List<String> login();


    @GET
    @Path("/triples")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "List triples by vote state",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    PagingList<Ticket> triples(
            @WebParam(name = "username") @QueryParam("username") String username,
            @WebParam(name = "subject") @QueryParam("subject") String subject,
            @WebParam(name = "hasVote") @QueryParam("hasVote") Boolean hasVote,
            @WebParam(name = "vote") @QueryParam("vote") Vote vote,
            @WebParam(name = "page") @QueryParam("page") int page,
            @WebParam(name = "pageSize") @QueryParam("pageSize") int pageSize);

    @GET
    @Path("/count/subject")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "List subjects by vote state",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    PagingList<KeyCount> countBySubject(
            @WebParam(name = "username") @QueryParam("username") String username,
            @WebParam(name = "hasVote") @QueryParam("hasVote") Boolean hasVote,
            @WebParam(name = "vote") @QueryParam("vote") Vote vote,
            @WebParam(name = "page") @QueryParam("page") int page,
            @WebParam(name = "pageSize") @QueryParam("pageSize") int pageSize);

    @GET
    @Path("/count/user")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "List users by vote state",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    PagingList<UserStats> countByUser(
            @WebParam(name = "username") @QueryParam("username") String username,
            @WebParam(name = "hasVote") @QueryParam("hasVote") Boolean hasVote,
            @WebParam(name = "vote") @QueryParam("vote") Vote vote,
            @WebParam(name = "page") @QueryParam("page") int page,
            @WebParam(name = "pageSize") @QueryParam("pageSize") int pageSize);

    @GET
    @Path("/count/users_votes")
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "List users by vote state",
            authorizations = {@Authorization("basic"), @Authorization("session")})
    PagingList<UserVoteStats> countByUserVote(
            @WebParam(name = "username") @QueryParam("username") String username,
            @WebParam(name = "hasVote") @QueryParam("hasVote") Boolean hasVote,
            @WebParam(name = "vote") @QueryParam("vote") Vote vote,
            @WebParam(name = "page") @QueryParam("page") int page,
            @WebParam(name = "pageSize") @QueryParam("pageSize") int pageSize);

}
