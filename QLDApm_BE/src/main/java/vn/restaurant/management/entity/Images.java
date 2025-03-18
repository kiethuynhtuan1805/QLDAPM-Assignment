package vn.restaurant.management.entity;

import lombok.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="Images")
public class Images implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="id", length = 20)
    private String id;

    @Column(name="link_url", length = 255)
    private String linkImg;

    @Column(name="dish_id", length = 20)
    private String dishId;
}
