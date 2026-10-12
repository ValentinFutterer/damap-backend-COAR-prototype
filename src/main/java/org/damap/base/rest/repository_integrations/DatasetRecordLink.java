package org.damap.base.rest.repository_integrations;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.*;
import org.damap.base.domain.Dataset;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "dataset_record_link")
@Table(name = "dataset_record_link")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetRecordLink extends PanacheEntity {
    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "dataset_id", nullable = false, updatable = false, unique = true)
    private Dataset dataset;

    @Column(name = "external_record", nullable = false)
    private String externalRecord;


}
