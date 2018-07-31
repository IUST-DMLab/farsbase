package ir.ac.iust.dml.kg.knowledge.commons;

import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 * Map exception to response used in apache cxf JaxRS
 */
public class JAXRSExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOG = LogManager.getLogger(JAXRSExceptionMapper.class);

    @Override
    public Response toResponse(Throwable throwable) {
        throwable.printStackTrace();
        final Response.Status errorStatus;
        final StringBuilder responseBody = new StringBuilder();
        if (throwable instanceof ConstraintViolationException) {
            errorStatus = Response.Status.BAD_REQUEST;
            final ConstraintViolationException constraint = (ConstraintViolationException) throwable;
            for (final ConstraintViolation<?> violation : constraint.getConstraintViolations()) {
                final String msg = buildErrorMessage(violation);
                responseBody.append(msg).append("\n");
                LOG.warn(msg);
            }
        } else {
            errorStatus = Response.Status.INTERNAL_SERVER_ERROR;
            responseBody.append(throwable.getMessage()).append("\n");
        }
        Response.ResponseBuilder rb = JAXRSUtils.toResponseBuilder(errorStatus);
        rb.type(MediaType.TEXT_PLAIN).entity(responseBody.toString());
        return rb.build();
    }

    private String buildErrorMessage(ConstraintViolation<?> violation) {
        return "Value "
                + (violation.getInvalidValue() != null ? "'" + violation.getInvalidValue().toString() + "'" : "(null)")
                + " of " + violation.getRootBeanClass().getSimpleName()
                + "." + violation.getPropertyPath()
                + ": " + violation.getMessage();
    }
}
