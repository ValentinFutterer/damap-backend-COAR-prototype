package org.damap.base.rest.coar_notify.activity_streams;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.damap.base.rest.coar_notify.activity_streams.activities.Activity;
import org.damap.base.rest.coar_notify.activity_streams.activities.RelationshipAction;
import org.damap.base.rest.coar_notify.activity_streams.activities.Remove;
import org.damap.base.rest.coar_notify.activity_streams.activities.Update;
import org.damap.base.rest.coar_notify.activity_streams.actors.Person;
import org.damap.base.rest.coar_notify.activity_streams.actors.Service;
import org.damap.base.rest.coar_notify.activity_streams.objects.About;
import org.damap.base.rest.coar_notify.activity_streams.objects.items.Document;
import org.damap.base.rest.coar_notify.activity_streams.relationship.Relationship;
import org.damap.base.rest.config.domain.TenantConfigResolver;
import org.damap.base.rest.dmp.domain.DmpDO;
import org.damap.base.rest.dmp.service.DmpService;
import org.damap.base.security.SecurityService;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ActivityStreamsMessageGenerator {

  @ConfigProperty(name = "damap.ldn-id")
  String ldnId;

  @ConfigProperty(name = "damap.ldn-inbox")
  String ldnInbox;

  @ConfigProperty(name = "damap.ldn-name")
  String ldnName;

  @Inject TenantConfigResolver tenantConfigResolver;
  @Inject SecurityService securityService;
  @Inject DmpService dmpService;

  /*
  public Activity getDelete(Long dmpId, String datasetId) {
    return Delete.builder()
        .id(UUID.randomUUID().toString())
        .origin(getOrigin())
        .target(getTarget()) // Currently only Invenio connection
        .actor(getActor())
        .object(getDmpDatasetObject(dmpId, datasetId))
        .build();
  }

  public Activity getDelete(Long dmpId) {
    return Delete.builder()
        .id(UUID.randomUUID().toString())
        .origin(getOrigin())
        .target(getTarget()) // Currently only Invenio connection
        .actor(getActor())
        .object(getDmpObject(dmpId))
        .build();
  }
  */

  public Activity getUpdate(Long dmpId, String datasetId) {
    return Update.builder()
        .id(UUID.randomUUID().toString())
        .origin(getOrigin())
        .target(getTarget()) // Currently only Invenio connection
        .actor(getActor())
        .object(getDmpDatasetObject(dmpId, datasetId))
        .build();
  }

  public Activity getAnnounce(Long dmpId, String datasetId) {
    return Update.builder()
            .id(UUID.randomUUID().toString())
            .origin(getOrigin())
            .target(getTarget()) // Currently only Invenio connection
            .actor(getActor())
            .object(getDmpDatasetObject(dmpId, datasetId))
            .build();
  }

  public Activity getRelationshipOffer(Long dmpId, String datasetId, String recordId) {
    return RelationshipAction.builder()
        .id(UUID.randomUUID().toString())
        .origin(getOrigin())
        .target(getTarget()) // Currently only Invenio connection
        .actor(getActor())
        .object(getRelationshipObject(dmpId, datasetId, recordId))
        .context(getRecordAboutPage(recordId))
        .build();
  }

  public Activity getRemove(Long dmpId, String datasetId, String recordId) {
    return Remove.builder()
    .id(UUID.randomUUID().toString())
            .origin(getOrigin())
            .target(getTarget()) // Currently only Invenio connection
            .actor(getActor())
            .object(getRelationshipObject(dmpId, datasetId, recordId))
            .build();
  }

  private Service getOrigin() {
    return Service.builder().id(ldnId).inbox(ldnInbox).name(ldnName).build();
  }

  private Service getTarget() {
    return Service.builder()
        .id(tenantConfigResolver.getTenantAwareConfig().invenioEndpointUrl())
        .inbox(tenantConfigResolver.getTenantAwareConfig().invenioLdnInbox())
        .name(tenantConfigResolver.getTenantAwareConfig().invenioTitle())
        .build();
  }

  private Person getActor() {
    return Person.builder()
        .id(securityService.getUserId()) // TODO: This should be a shared ID between Invenio and DAMAP
        .name(securityService.getDisplayName())
        .build();
  }

  private About getDmpDatasetAboutPage(Long dmpId, String datasetId) {
    return About.builder()
            .id(getDmpUrl(dmpId))
            .citeAs(getDmpUrl(dmpId))
            .item(getDmpDatasetObject(dmpId, datasetId))
            .build();
  }

  private About getRecordAboutPage(String recordId) {
    return About.builder()
            .id(getInvenioUrl(recordId))
            .citeAs(getInvenioUrl(recordId))
            .item(getRecordObject(recordId))
            .build();
  }

  private Document getDmpDatasetObject(Long dmpId, String datasetId) {
    DmpDO dmp = dmpService.getDmpById(dmpId);

    return Document.builder()
            .id(getDmpUrl(dmpId)) // TODO: Change to download URL
            .context(StringStreamsObject.builder().value(datasetId).build())
            .name(dmp.getDatasets().stream().filter(d -> d.getId().toString().equals(datasetId)).findFirst().get().getTitle())
            .mediaType("application/json")
            .build();
  }

  private Document getRecordObject(String recordId) {
    return Document.builder()
            .id(getInvenioUrl(recordId) + "/export/json")
            .mediaType("application/json")
            .build();
  }

  private Relationship getRelationshipObject(Long dmpId, String datasetId, String recordId) {
    return Relationship.builder()
        .id(UUID.randomUUID().toString())
        .summary("Dataset " + datasetId + " is linked to record " + recordId)
        .subject(getDmpDatasetUrl(dmpId, datasetId))
            // TODO: Hardcoded for Invenio
        .object(getInvenioUrl(recordId))
            .build();
  }

  private String getInvenioUrl(String recordId) {
    return tenantConfigResolver.getTenantAwareConfig().invenioEndpointUrl() + "/records/" + recordId;
  }
  private String getDmpUrl(Long dmpId) {
    return ldnId + "dmp/" + dmpId;
  }

  private String getDmpDatasetUrl(Long dmpId, String datasetId) {
    return this.getDmpUrl(dmpId) + "/dataset/" + datasetId;
  }
}
