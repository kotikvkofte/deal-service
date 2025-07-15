package org.ex9.dealservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class ContractorToRoleId implements java.io.Serializable {

    private static final long serialVersionUID = 3662835081628321270L;
    @NotNull
    @Column(name = "contractor_id", nullable = false)
    private UUID contractorId;

    @Size(max = 30)
    @NotNull
    @Column(name = "role_id", nullable = false, length = 30)
    private String roleId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        ContractorToRoleId entity = (ContractorToRoleId) o;
        return Objects.equals(this.contractorId, entity.contractorId) &&
                Objects.equals(this.roleId, entity.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contractorId, roleId);
    }

}
