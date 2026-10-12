package org.damap.base.rest.coar_notify;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.damap.base.enums.EDataKind;
import org.damap.base.rda.dmpcommonstandard.*;
import org.damap.base.rda.dmpcommonstandard.AlternateIdentifier;
import org.damap.base.rda.dmpcommonstandard.Contributor;
import org.damap.base.rda.dmpcommonstandard.Creator;
import org.damap.base.rda.dmpcommonstandard.RelatedIdentifier;
import org.damap.base.rest.config.domain.DamapTenantAwareConfig;
import org.damap.base.rest.config.domain.TenantConfigResolver;
import org.damap.base.rest.invenio.dto.*;
import org.damap.base.rest.invenio.dto.Date;

import java.time.LocalDate;
import java.util.*;

@ApplicationScoped
public class DataCiteCommonStandardMapper {

    @Inject
    TenantConfigResolver tenantConfigResolver;

    public DMPWithID mapDataciteToCommonStandard(DataciteV45 dataciteV45) {
        Dataset dataset = new Dataset();

        // ---------- Alternate Identifier ----------
        dataset.setAlternateIdentifier(extractAlternateIdentifiers(dataciteV45));

        // ---------- Creator ----------
        dataset.setCreator(extractCreators(dataciteV45));

        // ---------- Dataset Id ----------
        dataset.setDatasetId(extractDatasetId(dataciteV45));

        // ---------- Description ----------
        dataset.setDescription(extractDescription(dataciteV45));

        // ---------- Distribution ----------
        dataset.setDistribution(extractDistributions(dataciteV45));

        // ---------- Is Reused ----------
        dataset.setIsReused(false); // false is assumed, as the feature is meant to be used for concurrent work

        // ---------- Issued ----------
        dataset.setIssued(extractIssued(dataciteV45));

        // ---------- Language ----------
        try {
            dataset.setLanguage(LanguageCode.fromValue(dataciteV45.getLanguage()));
        } catch (IllegalArgumentException e) {
            // empty catch - in case mapping languages fails do nothing
        }

        // ---------- Language ----------
        dataset.setRelatedIdentifier(extractRelatedIdentifier(dataciteV45));

        // ---------- Sensitive Data ----------
        // Not supported by datacite, set to unknown as a default
        dataset.setSensitiveData(Dataset.SensitiveDataEnum.UNKNOWN);

        // ---------- Title ----------
        dataset.setTitle(extractTitle(dataciteV45));

        // ---------- Resource Type ----------
        dataset.setType(dataciteV45.getTypes().getResourceTypeGeneral().value());

        DMPWithID dmp = new DMPWithID();
        DMPData dmpData = new DMPData();
        dmpData.setDataset(List.of(dataset));
        dmp.setDmp(dmpData);

        // Set some required values so the common standard mapper doesnt fail
        dmp.setId("1");
        dmpData.setEthicalIssuesExist(Booleanish.UNKNOWN);

        return dmp;
    }

    private List<AlternateIdentifier> extractAlternateIdentifiers(DataciteV45 dataciteV45) {
        List<AlternateIdentifier> alternateIdentifiers = new ArrayList<>();
        if (dataciteV45.getAlternateIdentifiers() != null) {
            for (var dataciteAltId : dataciteV45.getAlternateIdentifiers()) {
                AlternateIdentifier alternateIdentifier = new AlternateIdentifier();
                alternateIdentifier.setIdentifier(dataciteAltId.getAlternateIdentifier());
                alternateIdentifier.setType(dataciteAltId.getAlternateIdentifierType());
                alternateIdentifiers.add(alternateIdentifier);
            }
        }
        return alternateIdentifiers;
    }

    private DatasetID extractDatasetId(DataciteV45 dataciteV45) {
        DatasetID datasetID = new DatasetID();
        // v4.5 doesnt have Identifier, it only has DOI which equals an identifier of type doi
        if (dataciteV45.getDoi() != null) {
            datasetID.setIdentifier(dataciteV45.getDoi());
            datasetID.setType("doi");
        } else {
            datasetID.setIdentifier(extractTitle(dataciteV45));
            datasetID.setType("other");
        }
        return datasetID;
    }

    private String extractDescription(DataciteV45 dataciteV45) {
        // Maybe filter and try to get the description with type Abstract as a preferred option?
        if (dataciteV45.getDescriptions() != null) {
            return dataciteV45.getDescriptions()
                    .stream()
                    .findFirst()
                    .map(Description::getDescription)
                    .orElse(null);
        } else {
            return null;
        }
    }

    private LocalDate extractIssued(DataciteV45 dataciteV45) {
        if (dataciteV45.getDates() != null) {
            for (Date date : dataciteV45.getDates()) {
                if (date.getDateType().equals(Date.DateType.ISSUED)) {
                    return LocalDate.parse(date.getDate());
                }
            }
        }
        if (dataciteV45.getPublicationYear() != null) {
            return LocalDate.ofYearDay(Integer.parseInt(dataciteV45.getPublicationYear()), 1);
        }
        return null;
    }

    private List<RelatedIdentifier> extractRelatedIdentifier(DataciteV45 dataciteV45) {
        List<RelatedIdentifier> relatedIdentifiers = new ArrayList<>();
        if (dataciteV45.getRelatedIdentifiers() != null) {
            for (var dataciteRelId : dataciteV45.getRelatedIdentifiers()) {
                RelatedIdentifier relatedIdentifier = new RelatedIdentifier();
                relatedIdentifier.setIdentifier(dataciteRelId.getRelatedIdentifier());
                relatedIdentifier.setType(dataciteRelId.getRelatedIdentifierType().value());
                // v4.5 is missing metadataScheme, relationType, resourceTypeGeneral, schemeType and schemeURI,
                relatedIdentifiers.add(relatedIdentifier);
            }
        }
        return relatedIdentifiers;
    }

    private String extractTitle(DataciteV45 dataciteV45) {
        if (dataciteV45.getTitles() != null) {
            // A title with no type is the main title
            for (Title title : dataciteV45.getTitles()) {
                if (title.getTitleType() == null) {
                    return title.getTitle();
                }
            }
            // Otherwise return the first title
            return dataciteV45.getTitles()
                    .stream()
                    .findFirst()
                    .map(Title::getTitle)
                    .orElse(null);
        }
        return null;
    }

    private List<Creator> extractCreators(DataciteV45 dataciteV45) {
        List<Creator> retCreators = new ArrayList<>();
        for (org.damap.base.rest.invenio.dto.Creator creator : dataciteV45.getCreators()) {
            // Creator objects are not generated correctly and their properties are in a map
            Map<String, Object> properties = creator.getAdditionalProperties();
            retCreators.add(extractCreator(properties));
        }
        return retCreators;
    }

    private Creator extractCreator(Map<String,Object> properties) {
        Creator retCreator = new Creator();
        retCreator.setName((String) properties.get("name")); // might be contributorName in later versions

        // map name identifiers
        if (properties.get("nameIdentifiers") != null) {
            List<ContributorID> creatorIds = new ArrayList<>();
            for (Map<String, Object> nameIdentifier : (List<Map<String, Object>>) properties.get("nameIdentifiers")) {
                ContributorID creatorId = new ContributorID();
                creatorId.setIdentifier((String) nameIdentifier.get("nameIdentifier"));
                creatorId.setType((String) nameIdentifier.get("nameIdentifierScheme"));
                creatorIds.add(creatorId);
            }
            // RDA schema has an optional list of IDs, but currently its one required object
            retCreator.setCreatorId(creatorIds.stream()
                    .findFirst()
                    .orElse(new ContributorID()));
        }

        // map affiliations
        if (properties.get("affiliation") != null) {
            List<Affiliation> affiliations = new ArrayList<>();
            for (Map<String, Object> affiliation : (List<Map<String, Object>>) properties.get("affiliation")) {
                Affiliation retAffiliation = new Affiliation();
                AffiliationID affiliationId = new AffiliationID();
                // id is mandatory in the CS
                if (affiliation.get("affiliationIdentifier") == null) {
                    continue;
                }
                affiliationId.setIdentifier((String) affiliation.get("affiliationIdentifier"));
                affiliationId.setType((String) affiliation.get("affiliationIdentifierScheme"));
                retAffiliation.setAffiliationId(affiliationId);
                retAffiliation.setName((String) affiliation.get("name"));
                affiliations.add(retAffiliation);
            }
            retCreator.setAffiliation(affiliations);
        }
        return retCreator;
    }

    private List<Contributor> extractContributors(DataciteV45 dataciteV45) {
        List<Contributor> contributors = new ArrayList<>();
        for (org.damap.base.rest.invenio.dto.Contributor contributor : dataciteV45.getContributors()) {

            // Contributor objects are not generated correctly and their properties are in a map
            Map<String, Object> properties = contributor.getAdditionalProperties();
            // The objects are very similar, so the code can be reused here
            Creator creator = extractCreator(properties);
            Contributor retContributor = new Contributor();
            retContributor.setContributorId(creator.getCreatorId());
            retContributor.setAffiliation(creator.getAffiliation());
            retContributor.setName(creator.getName());

            retContributor.setRole(Set.of(properties.get("contributorType").toString()));
            contributors.add(retContributor);
        }
        return contributors;
    }

    private List<Distribution> extractDistributions(DataciteV45 dataciteV45) {
        Distribution retDistribution = new Distribution();
        // TODO: Complete the URL
        retDistribution.setAccessUrl(tenantConfigResolver.getTenantAwareConfig().invenioEndpointUrl());
        retDistribution.setByteSize(dataciteV45.getSizes().stream().mapToLong(this::toBytes).sum());
        // TODO: find solution for data access
        //retDistribution.setDataAccess(); no equivalent in datacite, needs a solution
        retDistribution.setDescription(extractDescription(dataciteV45));
        // TODO: Complete the URL
        retDistribution.setDownloadUrl(tenantConfigResolver.getTenantAwareConfig().invenioEndpointUrl());
        retDistribution.setFormat(dataciteV45.getFormats().stream().toList());
        retDistribution.setHost(getHostFromConfig());
        retDistribution.setIssued(extractIssued(dataciteV45));
        retDistribution.setLicense(extractLicenses(dataciteV45));
        retDistribution.setTitle(extractTitle(dataciteV45));
        return List.of(retDistribution);
    }

    private long toBytes(String value) {
        String[] parts = value.trim().split("\\s+");

        double number = Double.parseDouble(parts[0]);
        String unit = parts[1].toLowerCase();

        return switch (unit) {
            case "bit", "bits", "b" ->
                    (long) Math.ceil(number / 8);

            case "byte", "bytes" ->
                    (long) number;

            case "kb" ->
                    (long) (number * 1_000L);

            case "mb" ->
                    (long) (number * 1_000_000L);

            case "gb" ->
                    (long) (number * 1_000_000_000L);

            case "tb" ->
                    (long) (number * 1_000_000_000_000L);

            default ->
                    0L; // do nothing
        };
    }

    private List<License> extractLicenses(DataciteV45 dataciteV45) {
        List<License> licenses = new ArrayList<>();
        if (dataciteV45.getRightsList() != null) {
            for (Rights rights : dataciteV45.getRightsList()) {
                if (rights.getRightsUri() == null) {
                    continue; // could also try to reconstruct with rightsIdentifier and a lookup table
                }
                License license = new License();
                license.setLicenseRef(rights.getRightsUri().toString());
                Optional<Date> available = dataciteV45.getDates().stream().filter(x -> x.getDateType().equals(Date.DateType.AVAILABLE)).findFirst();
                if (available.isPresent()) {
                    license.setStartDate(LocalDate.parse(available.get().getDate()));
                } else if (extractIssued(dataciteV45) != null) {
                    license.setStartDate(extractIssued(dataciteV45));
                } else {
                    license.setStartDate(LocalDate.now());
                }
                licenses.add(license);
            }
        }
        return licenses;
    }

    // always uses Invenio as default - can be expanded later by differentiating connected repos
    private Host getHostFromConfig() {
        DamapTenantAwareConfig config = tenantConfigResolver.getTenantAwareConfig();;
        Host host = new Host();

        HostID hostID = new HostID();
        hostID.setIdentifier(config.invenioRe3dataId());
        hostID.setType("Re3Data");
        host.setHostId(List.of(hostID));

        host.setAvailability(config.invenioAvailability());
        host.setBackupFrequency(config.invenioBackupFrequency());
        host.setBackupType(config.invenioBackupType());
        host.setCertifiedWith(Certification.fromValue(config.invenioCertifiedWith()));
        host.setDescription(config.invenioDescription());
        host.setGeoLocation(CountryCode.fromValue(config.invenioGeoLocation()));
        host.setPidSystem(List.of(PIDSystemType.valueOf(config.invenioPidSystem())));
        host.setStorageType(config.invenioStorageType());
        host.setSupportVersioning(Booleanish.fromValue(config.invenioSupportVersioning()));
        host.setUrl(config.invenioEndpointUrl());

        return host;
    }
}
