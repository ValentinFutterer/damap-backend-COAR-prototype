package org.damap.base.rest.config.domain;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import org.damap.base.enums.EContributorRole;

/** Represents all config options that need to be uniquely configured per tenant */
public interface DamapTenantAwareConfig {
  String title();

  Fields fields();

  String projectService();

  List<ServiceConfig> personServices();

  String elsevierPureDescriptionClassification();

  List<PureRoleClassification> elsevierPureContributorRoleClassifications();

  String elsevierPureProjectLeadRoleClassification();

  String elsevierPureBackend();

  String elsevierPureEndpointUrl();

  String elsevierPureApiKey();

  URL elsevierPureProjectsFile();

  URL elsevierPurePersonsFile();

  Optional<String> evaluationServiceUrl();

  Optional<String> genericCrisUrl();

  Optional<String> genericCrisApiKey();

  String invenioEndpointUrl();

  String invenioRe3dataId();

  String invenioLdnInbox();


  String invenioAvailability();
  String invenioBackupFrequency();
  String invenioBackupType();
  String invenioCertifiedWith();
  String invenioDescription();
  String invenioGeoLocation();
  String invenioTitle();
  String invenioPidSystem();
  String invenioStorageType();
  String invenioSupportVersioning();

  interface Fields {
    boolean ethicalReportEnabled();
  }

  interface ServiceConfig {
    String className();

    String displayText();

    String queryValue();
  }

  interface PureRoleClassification {
    String pureRoleUri();

    EContributorRole contributorRole();
  }
}
