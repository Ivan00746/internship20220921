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
@Table(name = "icons")
public class Icon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn (name = "marker_id", nullable = false)
    private Marker marker;

    @Column(name = "icon_url", length = 100)
    private String iconUrl;
    @Column(name = "icon_retina_url", length = 100)
    private String iconRetinaUrl;
    @Column(name = "icon_size", columnDefinition = "integer[]")
    @Type(type = "org.example.config.IntArrayDataType")
    private Integer[] iconSize;
    @Column(name  = "icon_anchor", columnDefinition = "integer[]")
    @Type(type = "org.example.config.IntArrayDataType")
    private Integer[] iconAnchor;
    @Column(name = "popup_anchor", columnDefinition = "integer[]")
    @Type(type = "org.example.config.IntArrayDataType")
    private Integer[] popupAnchor;
    @Column(name = "tooltip_anchor", columnDefinition = "integer[]")
    @Type(type = "org.example.config.IntArrayDataType")
    private Integer[] tooltipAnchor;
    @Column(name = "shadow_url", length = 100)
    private String shadowUrl;
    @Column(name = "shadow_retina_url", length = 100)
    private String shadowRetinaUrl;
    @Column(name = "shadow_size", columnDefinition = "integer[]")
    @Type(type = "org.example.config.IntArrayDataType")
    private Integer[] shadowSize;
    @Column(name = "shadow_anchor", columnDefinition = "integer[]")
    @Type(type = "org.example.config.IntArrayDataType")
    private Integer[] shadowAnchor;
    @Column(name = "class_name", length = 50)
    private String className;
    @Column(name = "cross_origin", length = 100)
    private String crossOrigin;
}
