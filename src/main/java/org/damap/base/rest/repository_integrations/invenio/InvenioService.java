package org.damap.base.rest.repository_integrations.invenio;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.damap.base.domain.Dmp;
import org.damap.base.enums.EDataKind;
import org.damap.base.enums.EDataSource;
import org.damap.base.rda.dmpcommonstandard.DMPMapper;
import org.damap.base.rda.dmpcommonstandard.DMPWithID;
import org.damap.base.rest.coar_notify.DataCiteCommonStandardMapper;
import org.damap.base.rest.config.domain.TenantConfigResolver;
import org.damap.base.rest.dmp.domain.DatasetDO;
import org.damap.base.rest.dmp.domain.DmpDO;
import org.damap.base.rest.dmp.domain.RepositoryDO;
import org.damap.base.rest.dmp.mapper.DmpDOMapper;
import org.damap.base.rest.dmp.mapper.MapperService;
import org.damap.base.rest.dmp.service.DmpService;

@ApplicationScoped
public class InvenioService {

  @Inject InvenioRemoteResource invenioRemoteResource;
  @Inject DmpService dmpService;

  @Inject MapperService mapperService;

  @Inject TenantConfigResolver tenantConfigResolver;

  @Inject
  DataCiteCommonStandardMapper dataciteCsMapper;

  private final DMPMapper dmpMapper = new DMPMapper(false);

  // TODO: If we want to support multiple repositories, create a parent service with shared code
  //  and write a factory to generate the correct service

  @Transactional
  public DatasetDO addDatasetToDMP(long dmpId, String recordId) {
    // TODO: Also fetch contributors maybe?
    // TODO: Check that dataset doesnt already exists
    DMPWithID csDmp = dataciteCsMapper.mapDataciteToCommonStandard(this.invenioRemoteResource.getDataCiteDraftRecord(recordId));
    DatasetDO newDataset = dmpMapper.convert(csDmp).getDatasets().get(0);

    DmpDO dmp = this.dmpService.getDmpById(dmpId);
    List<Long> oldDatasetIds = dmp.getDatasets().stream()
            .map(DatasetDO::getId)
            .toList();
    dmp.getDatasets().add(newDataset);
    dmp.setDataKind(EDataKind.SPECIFY); // otherwise the dataset is deleted
    DmpDO updatedDmp = dmpService.update(dmp);

    return updatedDmp.getDatasets().stream()
            .filter(d -> !oldDatasetIds.contains(d.getId()))
            .findFirst()
            .get();

    // TODO: Add workflow connecting new DAMAP dataset with Invenio Record
  }

  @Transactional
  public void updateDatasetInDMP(long dmpId, long datasetId, String recordId) {
    DMPWithID csDmp = dataciteCsMapper.mapDataciteToCommonStandard(this.invenioRemoteResource.getDataCiteDraftRecord(recordId));
    DmpDO externalDmpDO = dmpMapper.convert(csDmp);

    DmpDO dmp = this.dmpService.getDmpById(dmpId);

    updateDatasetFromExternalDataCite(dmp, externalDmpDO, datasetId);
    dmpService.update(dmp);
  }

  private void updateDatasetFromExternalDataCite(DmpDO dmpDO, DmpDO externalDmpDO, long datasetId) {
    // If dataset doesnt already have an Invenio distribution, add it
    Optional<RepositoryDO> tuwrd =
        dmpDO.getRepositories().stream()
            .filter(r -> r.getRepositoryId().equals("r3d100013557"))
            .findFirst();
    if (tuwrd.isPresent()) {
      if (tuwrd.get().getDatasets().stream().noneMatch(d -> d.equals(Long.toString(datasetId)))) {
        tuwrd.get().getDatasets().add(Long.toString(datasetId));
      }
    } else {
      // TODO: Should be in externalDmpDO, no need to generate
      RepositoryDO newTuwrd =
          RepositoryDO.builder()
              .repositoryId(tenantConfigResolver.getTenantAwareConfig().invenioRe3dataId())
              .datasets(List.of(Long.toString(datasetId)))
              .title(tenantConfigResolver.getTenantAwareConfig().invenioTitle())
              .build();
      dmpDO.getRepositories().add(newTuwrd);
    }

    DatasetDO dataset =
        dmpDO.getDatasets().stream().filter(d -> d.getId() == datasetId).findFirst().orElseThrow();

    // set reference hash so the update can go through
    dataset.setReferenceHash(String.valueOf(datasetId));

    // Only one dataset should be imported
    DatasetDO externalDataset =
        externalDmpDO.getDatasets().stream()
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No dataset was parsed from Invenio"));

    // TODO: Also import contributors into DMP?

    dataset.setTitle(externalDataset.getTitle());
    dataset.setType(externalDataset.getType());
    dataset.setFileFormat(externalDataset.getFileFormat());
    dataset.setSize(externalDataset.getSize());
    dataset.setDescription(externalDataset.getDescription());
    //dataset.setPersonalData(
        //externalDataset.getPersonalData()); // no way to get it from CS
    //dataset.setSensitiveData(
        //externalDataset.getSensitiveData()); // no way to get it from CSo
    dataset.setLicense(externalDataset.getLicense());
    dataset.setStartDate(externalDataset.getStartDate());
    //dataset.setDataAccess(
        //externalDataset.getDataAccess()); // No way to get from CS
    //dataset.setRetentionPeriod(
        //externalDataset.getRetentionPeriod()); // no way to get it from CS
    dataset.setDatasetId(externalDataset.getDatasetId());
    dataset.setSource(EDataSource.NEW); // Only new datasets are drafts in Invenio
    dataset.setTechnicalResources(externalDataset.getTechnicalResources());
  }
}
