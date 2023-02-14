package org.example.entities.layers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "points")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn (name = "polygon_id")
    private Polygon polygonId;

    @Column(columnDefinition = "float8[]")
    @Type(type = "org.example.config.DoubleArrayDataType")
    private Double[] coordinate;
}
