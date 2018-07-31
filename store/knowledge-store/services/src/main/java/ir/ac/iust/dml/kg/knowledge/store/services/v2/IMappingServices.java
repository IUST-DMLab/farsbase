package ir.ac.iust.dml.kg.knowledge.store.services.v2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ir.ac.iust.dml.kg.knowledge.commons.PagingList;
import ir.ac.iust.dml.kg.knowledge.store.access2.entities.TemplateMapping;
import ir.ac.iust.dml.kg.knowledge.store.services.v2.data.TemplateData;

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
 * Rest: /rs/v2/mappings
 * SOA: /ws/v2/mappings
 *
 * Service to use mappings
 */
@WebService
@Path("/v2/mappings")
@Api("/v2/mappings")
public interface IMappingServices {
    @POST
    @Path("/template/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Insert or update template definition")
    TemplateData insert(@Valid TemplateData data);

    @POST
    @Path("/template/batch/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Insert or update template definition")
    List<TemplateData> batchInsert(@Valid List<TemplateData> data);


    @POST
    @Path("/all")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @WebMethod
    @ApiOperation(value = "Read all mapping")
    PagingList<TemplateMapping> readAll(
            @WebParam(name = "page") @QueryParam("page") int page,
            @WebParam(name = "pageSize") @QueryParam("pageSize") int pageSize
    );
}
