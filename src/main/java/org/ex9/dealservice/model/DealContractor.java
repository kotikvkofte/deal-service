package org.ex9.dealservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "deal_contractor")
public class DealContractor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deal_id", nullable = false)
    private Deal deal;

    @Size(max = 12)
    @NotNull
    @Column(name = "contractor_id", nullable = false, length = 12)
    private String contractorId;

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "inn", length = Integer.MAX_VALUE)
    private String inn;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "main", nullable = false)
    private Boolean main = false;

    @NotNull
    @ColumnDefault("now")
    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    @Column(name = "modify_date")
    private LocalDate modifyDate;

    @Column(name = "create_user_id", length = Integer.MAX_VALUE)
    private String createUserId;

    @Column(name = "modify_user_id", length = Integer.MAX_VALUE)
    private String modifyUserId;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

}
