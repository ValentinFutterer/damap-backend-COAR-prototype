package org.damap.base.rest.coar_notify;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.damap.base.domain.Dataset;
import org.damap.base.rest.coar_notify.activity_streams.EActivityStreamsType;
import org.damap.base.rest.coar_notify.activity_streams.activities.Activity;
import org.damap.base.rest.coar_notify.activity_streams.objects.items.Item;
import org.damap.base.rest.coar_notify.activity_streams.relationship.Relationship;
import org.damap.base.rest.dmp.domain.DatasetDO;
import org.damap.base.rest.repository_integrations.DatasetRecordLink;
import org.damap.base.rest.repository_integrations.DatasetRecordLinkService;
import org.damap.base.rest.repository_integrations.invenio.InvenioService;

import java.util.List;

@ApplicationScoped
public class CoarNotifyService {

    // TODO: Check ID in COAR message against resources

    @Inject
    DatasetRecordLinkService linkService;
    @Inject
    InvenioService invenioService;
    @Inject
    DatasetRecordLinkService datasetRecordLinkService;

    public void processActivity(Activity activity) {
        // TODO: add default with defined error
        List<EActivityStreamsType> types = activity.getType();

        if (types.contains(EActivityStreamsType.REMOVE)) {
            processRemove(activity);
        } else if (types.contains(EActivityStreamsType.RELATIONSHIP_ACTION)) {
            processRelationship(activity);
        } else if (types.contains(EActivityStreamsType.UPDATE)) {
            processUpdate(activity);
        } else if (types.contains(EActivityStreamsType.ANNOUNCE)) { // do this last because Announce is contained in other types
            processAnnounce(activity);
        }
    }

    private void processRemove(Activity activity) {
        Relationship relationship = (Relationship) activity.getObject();
        String subject = relationship.getSubject();
        this.linkService.removeLink(subject);
    }

    private void processRelationship(Activity activity) {
        Relationship relationship = (Relationship) activity.getObject();
        String object = relationship.getObject();
        String subject = relationship.getSubject();
        this.linkService.createLink(Long.valueOf(getLastPartOfUrl(object)),getLastPartOfUrl(subject));
        // Todo: Send Accept Maybe?
        // TODO: Make sure that one dataset can only have one relationship
    }

    private void processUpdate(Activity activity) {
        Item context = (Item) activity.getObject();
        DatasetRecordLink link = this.linkService.getLinkForRecord(getRecordIdFromInvenioContext(context));
        Dataset dataset = link.getDataset();
        invenioService.updateDatasetInDMP(dataset.getDmp().id, dataset.getId(), link.getExternalRecord());
    }

    private void processAnnounce(Activity activity) {
        Item context = (Item) activity.getObject();
        long dmpId = Long.parseLong(context.getContext().getValue());
        String recordId = getRecordIdFromInvenioContext(context);
        DatasetDO newDataset = invenioService.addDatasetToDMP(dmpId, recordId);
        this.linkService.createLink(newDataset.getId(), recordId);
        // TODO: Send COAR message to create link in partner tool
    }

    private String getRecordIdFromInvenioContext(Item context) {
        return getLastPartOfUrl(context.getUrl()); // only works when id is last in URL
    }

    private String getLastPartOfUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }
}
