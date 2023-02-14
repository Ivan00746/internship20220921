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
@Table(name = "markers")
public class Marker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn (name = "layer_group_id", nullable = false)
    private LayerGroup layerGroup;

    @Column(columnDefinition = "float8[]")
    @Type(type = "org.example.config.DoubleArrayDataType")
    private Double[] center;

    @OneToOne(mappedBy = "marker", cascade = CascadeType.ALL)
    private Icon icon;

    @Column
    private boolean interactive;
    @Column
    private boolean keyboard;
    @Column(length = 50)
    private String title;
    @Column(length = 50)
    private String alt;
    @Column
    private int zIndexOffset;
    @Column
    private float opacity;
    @Column
    private boolean riseOnHover;
    @Column
    private int riseOffset;
    @Column(length = 20)
    private String pane;
    @Column(length = 20)
    private String shadowPane;
    @Column
    private boolean bubblingMouseEvents;
    @Column
    private boolean autoPanOnFocus;
    @Column
    private boolean draggable;
    @Column
    private boolean autoPan;
    @Column(columnDefinition = "integer[]")
    @Type(type = "org.example.config.IntArrayDataType")
    private Integer[] autoPanPadding;
    @Column
    private int autoPanSpeed;
    @Column(length = 100)
    private String attribution;
}
