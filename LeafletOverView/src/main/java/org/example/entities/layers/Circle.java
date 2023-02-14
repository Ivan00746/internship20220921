package org.example.entities.layers;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Data
@Entity
@Table(name = "circles")
@NoArgsConstructor
public class Circle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn (name = "layer_group_id")
    private LayerGroup layerGroup;

    @Column(columnDefinition = "float8[]")
    @Type(type = "org.example.config.DoubleArrayDataType")
    private Double[] center;
    @Column
    private int radius;
    @Column
    private int weight;
    @Column
    private float opacity;
    @Column (length = 10)
    private String color;
    @Column
    private float fillOpacity;
    @Column (length = 10)
    private String fillColor;
    @Column
    private boolean fill;
    @Column
    private boolean stroke;
    @Column (length = 20)
    private String lineCap;
    @Column (length = 20)
    private String lineJoin;
    @Column (length = 50)
    private String dashArray;
    @Column (length = 50)
    private String dashOffset;
    @Column (length = 20)
    private String fillRule;
    @Column
    private boolean interactive;
    @Column
    private boolean bubblingMouseEvents;
    @Column (length = 20)
    private String pane;
    @Column (length = 100)
    private String attribution;
}
