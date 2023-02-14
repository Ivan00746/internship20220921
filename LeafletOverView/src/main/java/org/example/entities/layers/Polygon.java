package org.example.entities.layers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "polygons")
public class Polygon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn (name = "layer_group_id", nullable = false)
    private LayerGroup layerGroup;

    @OneToMany(mappedBy = "polygonId", cascade = CascadeType.ALL)
    private List<Point> points;

    @Column (length = 20)
    private String layerType;
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
    private float smoothFactor;
    @Column
    private boolean noClip;
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
