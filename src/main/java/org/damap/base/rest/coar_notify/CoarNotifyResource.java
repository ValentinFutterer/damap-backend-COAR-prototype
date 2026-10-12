package org.damap.base.rest.coar_notify;

import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.jbosslog.JBossLog;
import oracle.jdbc.proxy.annotation.Post;
import org.damap.base.rest.coar_notify.activity_streams.activities.Activity;

@JBossLog
@Path("/api")
public class CoarNotifyResource {
    @Inject
    CoarNotifyService service;

    @Post
    @Path("/coar-inbox")
    @Consumes(MediaType.APPLICATION_JSON)
    public void postMessage(Activity activity) {
        log.info(String.format("Coar Notify message with id %s received", activity.getId()));
        service.processActivity(activity);
    }
}
