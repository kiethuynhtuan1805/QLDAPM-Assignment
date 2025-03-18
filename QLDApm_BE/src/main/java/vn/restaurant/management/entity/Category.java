package vn.restaurant.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="Category")
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "category_id", length = 10)
    private String categoryId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "date_created", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date dateCreated;

    @Column(name = "date_updated", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date dateUpdated;


}
