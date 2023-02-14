package org.example.services;

import org.example.entities.layers.*;
import org.example.repo.LayerGroupRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LayerGroupService {
    private final LayerGroupRepo layerGroupRepo;

    @Autowired
    public LayerGroupService(LayerGroupRepo layerGroupRepo) {
        this.layerGroupRepo = layerGroupRepo;
    }

    public ArrayList<String> getGroupsNames() {
        ArrayList<String> groupsNames = new ArrayList<>();
        for (LayerGroup layerGroup : layerGroupRepo.findAll()) {
            groupsNames.add(layerGroup.getName());
        }
        return groupsNames;
    }

    public String saveLayerGroup(LayerGroup layerGroup) {
        if (layerGroupRepo.existsByName(layerGroup.getName()))
            layerGroupRepo.delete(layerGroupRepo.findByName(layerGroup.getName()));
        if (layerGroup.getMarkers() != null) {
            List<Marker> markers = layerGroup.getMarkers();
            markers.forEach(marker -> {
                marker.setLayerGroup(layerGroup);
                marker.getIcon().setMarker(marker);
            });
        }
        if (layerGroup.getPolygons() != null) {
            List<Polygon> polygons = layerGroup.getPolygons();
            polygons.forEach(polygon -> {
                polygon.setLayerGroup(layerGroup);
                List<Point> points = polygon.getPoints();
                for (Point point : points) {
                    point.setPolygonId(polygon);
                }
            });
        }
        if (layerGroup.getCircles() != null) {
            List<Circle> circles = layerGroup.getCircles();
            circles.forEach(circle -> circle.setLayerGroup(layerGroup));
        }
        layerGroupRepo.save(layerGroup);
        return "L.Group (" + layerGroup.getName() + ") SAVED.";
    }

    public LayerGroup findLayerGroup(String groupName) {
        LayerGroup layerGroupDTO = layerGroupRepo.findByName(groupName);
        layerGroupDTO.getMarkers().forEach(marker -> {
            marker.setLayerGroup(null);
            marker.getIcon().setMarker(null);
        });
        layerGroupDTO.getPolygons().forEach(polygon -> {
            polygon.setLayerGroup(null);
            polygon.getPoints().forEach(point -> point.setPolygonId(null));
        });
        layerGroupDTO.getCircles().forEach(circle -> circle.setLayerGroup(null));
        return layerGroupDTO;
    }

    public String deleteLayerGroup(String groupName) {
        layerGroupRepo.delete(layerGroupRepo.findByName(groupName));
        return "L.Group (" + groupName + ") DELETED.";
    }
}
