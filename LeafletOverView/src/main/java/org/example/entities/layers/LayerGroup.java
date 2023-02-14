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
@Table(name = "layer_groups")
public class LayerGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column (length = 50, nullable = false, unique = true)
    private String name;
    @Column(length = 50)
    private String addingTime;

    @OneToMany(mappedBy = "layerGroup", cascade = CascadeType.ALL)
    private List<Marker> markers;

    @OneToMany(mappedBy = "layerGroup", cascade = CascadeType.ALL)
    private List<Polygon> polygons;

    @OneToMany(mappedBy = "layerGroup", cascade = CascadeType.ALL)
    private List<Circle> circles;
}
