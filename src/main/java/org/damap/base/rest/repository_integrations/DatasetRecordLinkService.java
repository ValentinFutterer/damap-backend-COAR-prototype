package org.damap.base.rest.repository_integrations;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.damap.base.domain.Dataset;

@ApplicationScoped
public class DatasetRecordLinkService {

    public DatasetRecordLink getLinkForDataset(Long datasetId) {
        return DatasetRecordLink.find("datasetId=?1", datasetId).firstResult();
    }
    public DatasetRecordLink getLinkForRecord(String recordId) {
        return DatasetRecordLink.find("externalRecord=?1", recordId).firstResult();
    }


    @Transactional
    public void createLink(Long datasetId, String recordId) {
        // TODO: Make sure dataset actually exists
        DatasetRecordLink.builder()
                .dataset(Dataset.findById(datasetId))
                .externalRecord(recordId)
                .build().persistAndFlush();
    }

    @Transactional
    public void removeLink(Long datasetId) {
        getLinkForDataset(datasetId).delete();
    }

    @Transactional
    public void removeLink(String recordId) {
        getLinkForRecord(recordId).delete();
    }
}
