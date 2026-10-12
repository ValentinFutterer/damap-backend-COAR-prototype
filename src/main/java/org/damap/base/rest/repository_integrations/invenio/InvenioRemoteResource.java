package org.damap.base.rest.repository_integrations.invenio;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.damap.base.enums.EErrorCode;
import org.damap.base.exception.DamapApiException;
import org.damap.base.exception.ErrorDto;
import org.damap.base.rest.invenio.dto.DataciteV45;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/records")
@Produces(MediaType.APPLICATION_JSON)
@Timeout(10000)
public interface InvenioRemoteResource {

  Logger log = Logger.getLogger(InvenioRemoteResource.class);

  /**
   * Retrieve a draft record with a specific Id.
   *
   * @param recordId the id of the record to fetch from invenio
   * @return the response containing the record in DataCite-Json format
   */
  @GET
  @Fallback(fallbackMethod = "getDataCiteDraftRecordFallback", skipOn = DamapApiException.class)
  @Path("/{recordId}/export/datacite-json")
  DataciteV45 getDataCiteDraftRecord(@PathParam("recordId") String recordId);

  /**
   * Retrieve all records for user with specific Id.
   *
   * @param userId the id of the user whose records to fetch from invenio
   * @return the response containing the records in DataCite-Json format
   */
  @GET
  @Fallback(fallbackMethod = "getDataCiteDraftRecordsForUserFallback", skipOn = DamapApiException.class)
  @Path("/export/datacite-json/{userId}")
  List<DataciteV45> getDataCiteDraftRecordsForUser(@PathParam("userId") String userId);

  default DataciteV45 getDataCiteDraftRecordFallback(String recordId) {
    throw fallback();
  }

  default List<DataciteV45> getDataCiteDraftRecordsForUserFallback(String userId) {
    throw fallback();
  }

  @ClientExceptionMapper
  static DamapApiException toException(jakarta.ws.rs.core.Response response) {
    // If the methods that caused the error are important for the error messages, you need a
    // ResteasyReactiveResponseExceptionMapper and register it as a provider
    return switch (response.getStatus()) {
      case 404 ->
          new DamapApiException(
              new ErrorDto(
                  EErrorCode.INVENIO_NOT_FOUND, "A draft record couldn't be found with Invenio"),
              jakarta.ws.rs.core.Response.Status.NOT_FOUND);
      case 500, 502, 503, 504 ->
          new DamapApiException(
              new ErrorDto(
                  EErrorCode.INVENIO_NOT_AVAILABLE,
                  "Invenio is currently not available, caused by " + response.getStatus()),
              jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR);
      default ->
          new DamapApiException(
              new ErrorDto(
                  EErrorCode.INVENIO_UNEXPECTED_ERROR,
                  "Something unexpected happened while calling Invenio, caused by "
                      + response.getStatus()),
              jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR);
    };
  }

  default DamapApiException fallback() {
    log.info("The Invenio API did not respond and timed out");
    return new DamapApiException(
        new ErrorDto(
            EErrorCode.INVENIO_NOT_AVAILABLE,
            "Invenio API is currently not available, connection timed out"),
        jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR);
  }
}
