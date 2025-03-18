package vn.restaurant.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="Dish")
public class Dish implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "dish_id", length = 10)
    private String dishId;

    @Column(name = "category_id", length = 10)
    private String categoryId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image", length = 255)
    private String image;

    @Column(name = "available")
    private boolean available;

    @Column(name = "rating")
    private float rating;

    @Column(name = "classify", length = 20)
    private String classify;

    @Column(name = "date_created", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date dateCreated;

    @Column(name = "date_updated", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date dateUpdated;
}
