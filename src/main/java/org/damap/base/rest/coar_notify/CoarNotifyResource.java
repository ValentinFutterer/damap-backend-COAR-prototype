package org.damap.base.rest.coar_notify;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.jbosslog.JBossLog;
import oracle.jdbc.proxy.annotation.Post;
import org.damap.base.rest.coar_notify.activity_streams.ActivityStreamsMessageGenerator;
import org.damap.base.rest.coar_notify.activity_streams.activities.Activity;
import org.damap.base.rest.config.domain.TenantConfigResolver;
import org.damap.base.rest.repository_integrations.invenio.InvenioRemoteResource;

@JBossLog
@Path("/api")
@Authenticated
public class CoarNotifyResource {
    @Inject
    CoarNotifyService service;
    @Inject
    ActivityStreamsMessageGenerator generator;
    @Inject
    InvenioRemoteResource client;
    @Inject
    TenantConfigResolver tenantConfigResolver;

    @Post
    @Path("/ldn/inbox")
    @Consumes(MediaType.APPLICATION_JSON)
    @PermitAll // TODO: Remove its for development only
    public void postMessage(Activity activity) {
        if (tenantConfigResolver.getTenantAwareConfig().invenioDisabled()) {
            throw new ServiceUnavailableException("The Invenio Integration is currently disabled");
        }
        log.info(String.format("Coar Notify %s message with id %s received", activity.getType().toString(), activity.getId()));
        service.processActivity(activity);
    }

    @Post
    @Path("/announce/{dmpId}/{datasetId}")
    public void manualAnnounce(@PathParam("dmpId") Long dmpId, @PathParam("datasetId") String datasetId) {
        if (tenantConfigResolver.getTenantAwareConfig().invenioDisabled()) {
            throw new ServiceUnavailableException("The Invenio Integration is currently disabled");
        }
        log.info(String.format("Manual announce request for dataset %s in dmp %s received", datasetId, dmpId));
        // TODO: Get userId from auth token
        client.sendCOARNotification(generator.getAnnounce(dmpId, datasetId, "valentin.futterer@tuwien.ac.at"));
    }

    @Post
    @Path("/update/{dmpId}/{datasetId}")
    public void manualUpdate(@PathParam("dmpId") Long dmpId, @PathParam("datasetId") String datasetId) {
        if (tenantConfigResolver.getTenantAwareConfig().invenioDisabled()) {
            throw new ServiceUnavailableException("The Invenio Integration is currently disabled");
        }
        log.info(String.format("Manual update request for dataset %s in dmp %s received", datasetId, dmpId));
        // TODO: Get userId from auth token
        client.sendCOARNotification(generator.getUpdate(dmpId, datasetId, "valentin.futterer@tuwien.ac.at"));
    }
}
