package org.damap.base.rest.repository_integrations.invenio;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.damap.base.rest.config.domain.TenantConfigResolver;
import org.damap.base.security.SecurityService;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

@ApplicationScoped
class InvenioAPIFactory {
  @Inject TenantConfigResolver tenantConfigResolver;

  @Inject SecurityService securityService;

  private final Map<String, InvenioRemoteResource> httpClients = new ConcurrentHashMap<>();

  @Produces
  @RequestScoped
  @Priority(1)
  InvenioRemoteResource create() {
    String aff = securityService.getAffiliation();
    if (aff == null || tenantConfigResolver.isMultitenancyDisabled()) {
      aff = "no-tenant-registered";
    }
    return httpClients.computeIfAbsent(
        aff,
        missingClient ->
            RestClientBuilder.newBuilder()
                .baseUri(
                    URI.create(tenantConfigResolver.getTenantAwareConfig().invenioEndpointUrl()))
                .build(InvenioRemoteResource.class));
  }
}
