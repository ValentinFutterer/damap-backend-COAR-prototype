package org.damap.base.rest.invenio;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.damap.base.rest.coar_notify.activity_streams.ActivityStreamsMessageGenerator;
import org.damap.base.rest.coar_notify.activity_streams.ActivityStreamsObject;
import org.damap.base.rest.dmp.domain.DatasetDO;
import org.damap.base.rest.dmp.domain.DmpDO;
import org.damap.base.rest.dmp.service.DmpService;
import org.damap.base.rest.invenio.dto.DataciteV45;
import org.damap.base.rest.repository_integrations.invenio.InvenioRemoteResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

@QuarkusTest
public class InvenioApiTest {

  @Inject
  InvenioRemoteResource invenioRemoteResource;

  @Inject
  ActivityStreamsMessageGenerator generator;

  @InjectMock DmpService dmpService;

  @BeforeEach
  public void setup() {
    //super.setup();
    DmpDO testDmp = new DmpDO();
    testDmp.setTitle("Test DMP Title");
    DatasetDO testDataset = new DatasetDO();
    testDataset.setTitle("testDataset");
    testDataset.setId(1L);
    testDmp.setDatasets(List.of(testDataset));
    when(dmpService.getDmpById(1)).thenReturn(testDmp);
  }

  @Test
  @Disabled
  void test() {
    DataciteV45 test = invenioRemoteResource.getDataCiteDraftRecord("vc0z7-8zt24");
    int x = 3;
    System.out.println(test);
  }

  @Test
  @Disabled
  void test2() throws JsonProcessingException {
    ActivityStreamsObject test = generator.getRelationshipOffer(1L, "1", "vc0z7-8zt24");
    ObjectMapper mapper = new ObjectMapper();
    System.out.println("Relationship offer --------------------------------");
    System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(test));
    System.out.println("Update --------------------------------------------");
    ActivityStreamsObject test2 = generator.getUpdate(1L, "1");
    System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(test2));
    System.out.println("Announce --------------------------------------------");
    ActivityStreamsObject test3 = generator.getAnnounce(1L, "1");
    System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(test3));
    System.out.println("Remove --------------------------------------------");
    ActivityStreamsObject test4 = generator.getRemove(1L, "1", "vc0z7-8zt24");
    System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(test4));
    fail();
  }
}
