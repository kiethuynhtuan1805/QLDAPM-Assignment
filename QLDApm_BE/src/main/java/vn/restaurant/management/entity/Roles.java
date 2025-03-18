package vn.restaurant.management.entity;

import lombok.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="Roles")
public class Roles implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "role_id", length = 10)
    private String roleId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;
}
