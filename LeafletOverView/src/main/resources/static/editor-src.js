//--TODO: ICON MENU functions:
function onClickMarkerIcon() {
    iconMenuShifter();
    document.getElementById("i1").setAttribute("style", "background-color: #f5bd83;");
    document.getElementById("map").style.cursor = "crosshair";
    document.getElementById("editMsgText").innerHTML =
        "<br>Click on map to draw a <strong>Marker</strong>.<br><br>";
    selectSettingsMode("markerSettings");
    map.on('click', function (ev) {
        layersPointsCleaner();
        editableLayers[0] = adjustedMarker().setLatLng(ev.latlng).addTo(map);
        layersPoints[0] = [];
        layersPoints[0][0] = L.marker(ev.latlng, {icon: drawPointIcon}).addTo(map);
        setGarbageRemovers();
    });
}

function onClickPLine() {
    iconMenuShifter();
    document.getElementById("i2").setAttribute("style", "background-color: #f5bd83;");
    document.getElementById("map").style.cursor = "crosshair";
    document.getElementById("editMsgText").innerHTML =
        "Repeatedly click, drag and click to draw a <strong>POLYLINE</strong>. " +
        "Right button click to stop drawing.";
    selectSettingsMode("vectorSettings");
    polyCreator(adjustedVector("polyline"));
}

function onClickPolygonIcon() {
    iconMenuShifter();
    document.getElementById("i3").setAttribute("style", "background-color: #f5bd83;");
    document.getElementById("map").style.cursor = "crosshair";
    document.getElementById("editMsgText").innerHTML =
        "Repeatedly click, drag and click to draw a <strong>POLYGON</strong>. " +
        "Right button click to stop drawing.";
    selectSettingsMode("vectorSettings");
    polyCreator(adjustedVector("polygon"));
}

function onClickRectIcon() {
    iconMenuShifter();
    document.getElementById("i4").setAttribute("style", "background-color: #f5bd83;");
    document.getElementById("map").style.cursor = "crosshair";
    document.getElementById("editMsgText").innerHTML =
        "Click, drag and click to draw a <strong>RECTANGLE</strong>. " +
        "Right button click to cancel drawing.";
    selectSettingsMode("vectorSettings");
    rectangleCreator(adjustedVector("rectangle"));
}

function onClickCircleIcon() {
    iconMenuShifter();
    document.getElementById("i5").setAttribute("style", "background-color: #f5bd83;");
    document.getElementById("map").style.cursor = "crosshair";
    document.getElementById("editMsgText").innerHTML =
        "Click, drag and click to draw a <strong>CIRCLE</strong>. " +
        "Right button click to cancel drawing.<br><br>";
    selectSettingsMode("vectorSettings");
    circleCreator(adjustedVector("circle"));
}

function onClickBrowsIcon() {
    iconMenuShifter();
    document.getElementById("i6").setAttribute("style", "background-color: #bbffa9;");
    document.getElementById("map").style.cursor = "grab";
    document.getElementById("editMsgText").innerHTML = "To place a <strong>LAYER</strong>, select " +
        "it from the menu on the left. To browse current <strong>LAYER GROUPS</strong> " +
        "use icon - <img src='/images/layers.png' width=16px>";
    selectSettingsMode("none");
}

function onClickArrowIcon() {
    iconMenuShifter();
    editMode = true;
    document.getElementById("i7").setAttribute("style", "background-color: #f5bd83;");
    document.getElementById("map").style.cursor = "context-menu";
    document.getElementById("editMsgText").innerHTML =
        "<br>Click layer on the map to <strong>SELECT</strong>.<br><br>";
    selectSettingsMode("none");
    map.eachLayer(function (layer) {
        switch (true) {
            case layer instanceof L.Marker:
                layer.on('click', function () {
                    document.getElementById("debugMsg").innerText =
                        JSON.stringify(layer.toGeoJSON()) + "; Options {" + temporary(layer.options) + "}" +
                        "; Icon {" + temporary(layer.getIcon().options) + "}";
                    layersPointsCleaner();
                    markerEditor(layer);
                    selectSettingsMode("markerSettings");
                });
                break;
            case layer instanceof L.Rectangle:
                layer.on('click', function () {
                    document.getElementById("debugMsg").innerText =
                        JSON.stringify(layer.toGeoJSON()) + "; Options {" + temporary(layer) + "}";
                    layersPointsCleaner();
                    rectangleEditor(layer);
                    selectSettingsMode("vectorSettings");
                });
                break;
            case layer instanceof L.Circle:
                layer.on('click', function () {
                    document.getElementById("debugMsg").innerText =
                        JSON.stringify(layer.toGeoJSON()) + "; Options {"
                        + temporary(layer) + "} Radius:" + layer.getRadius();
                    layersPointsCleaner();
                    circleEditor(layer);
                    selectSettingsMode("vectorSettings");
                });
                break;
            case layer instanceof L.Polyline:
                layer.on('click', function () {
                    document.getElementById("debugMsg").innerText =
                        JSON.stringify(layer.toGeoJSON()) + "; Options {" + temporary(layer) + "}";
                    layersPointsCleaner();
                    polyEditor(layer);
                    selectSettingsMode("vectorSettings");
                });
        }
    });
}

function temporary(object) {
    let text = "";
    for (let option in object) {
        text += option + ":"
            + object[option] + "; ";
    }
    return text;
}

function onClickRectSelIcon() {
    iconMenuShifter();
    document.getElementById("i8").setAttribute("style", "background-color: #f5bd83;");
    document.getElementById("map").style.cursor = "context-menu";
    document.getElementById("editMsgText").innerHTML =
        "<strong>SELECT</strong> group of a layers. " +
        "Selection of congeneric (" +
        "<strong>MARKERS</strong> or <strong>VECTOR</strong>) layers allows change group settings.";
    selectSettingsMode("groupSelection");
    rectangleSelector(newSelectionRectangle());
}

//--TODO: Layer CREAT(E)ors:
function polyCreator(polyLayer) {
    map.off('click mousemove contextmenu');
    map.on('click', function (ev1) {
        layersPointsCleaner();
        editableLayers[0] = polyLayer;
        layersPoints[0] = [];
        layersPoints[0][0] = L.marker(ev1.latlng, {icon: drawPointIcon}).addTo(map);
        let polygonsArray = editableLayers[0].getLatLngs();
        polygonsArray[0][0] = ev1.latlng;
        editableLayers[0].setLatLngs(polygonsArray).addTo(map);
        let cornerIndex = 1;
        map.on('mousemove', function (ev) {
            polygonsArray[0][cornerIndex] = ev.latlng;
            editableLayers[0].setLatLngs(polygonsArray);
        });
        map.off('click');
        map.on('click', function (ev2) {
            layersPoints[0][cornerIndex] = L.marker(ev2.latlng, {icon: drawPointIcon}).addTo(map);
            polygonsArray[0][cornerIndex++] = ev2.latlng;
            editableLayers[0].setLatLngs(polygonsArray);
        });
        map.on('contextmenu', function (evx) {
            if (cornerIndex === 1) {
                editableLayers[0].remove();
            } else {
                if (!polygonsArray[0][cornerIndex - 1].equals(evx.latlng)) polygonsArray[0].pop();
                editableLayers[0].setLatLngs(polygonsArray);
            }
            if (editableLayers[0] instanceof L.Polygon) {
                if (cornerIndex === 1) layersPointsCleaner();
                setGarbageRemovers();
                polyCreator(adjustedVector("polygon"));
            } else {
                if (cornerIndex === 1) layersPointsCleaner();
                setGarbageRemovers();
                polyCreator(adjustedVector("polyline"));
            }
        });
    });
}

function rectangleCreator(rect) {
    map.off('click mousemove contextmenu');
    map.on('click', function (ev1) {
        layersPointsCleaner();
        editableLayers[0] = rect.addTo(map);
        editableLayers[0] = rect.setBounds([ev1.latlng, ev1.latlng]);
        layersPoints[0] = [];
        layersPoints[0][0] = L.marker(ev1.latlng, {icon: drawPointIcon}).addTo(map);
        map.on('mousemove', function (ev) {
            editableLayers[0].setBounds([layersPoints[0][0].getLatLng(), ev.latlng]);
        });
        map.off('click');
        map.on('click', function (ev2) {
            layersPoints[0][1] = L.marker(ev2.latlng, {icon: drawPointIcon}).addTo(map);
            editableLayers[0].setBounds([layersPoints[0][0].getLatLng(), layersPoints[0][1].getLatLng()]);
            setGarbageRemovers();
            rectangleCreator(adjustedVector("rectangle"));
        });
        map.on('contextmenu', function () {
            editableLayers[0].remove();
            layersPointsCleaner();
            rectangleCreator(adjustedVector("rectangle"));
        });
    });
}

function circleCreator(circle) {
    map.off('click mousemove contextmenu');
    map.on('click', function (ev1) {
        layersPointsCleaner();
        editableLayers[0] = circle.addTo(map);
        editableLayers[0].setLatLng(ev1.latlng);
        layersPoints[0] = [];
        layersPoints[0][0] = L.marker(ev1.latlng, {icon: drawPointIcon}).addTo(map);
        map.on('mousemove', function (ev) {
            editableLayers[0].setRadius(layersPoints[0][0].getLatLng().distanceTo(ev.latlng));
        });
        map.off('click');
        map.on('click', function (ev2) {
            layersPoints[0][1] = L.marker(ev2.latlng, {icon: drawPointIcon}).addTo(map);
            editableLayers[0].setRadius(layersPoints[0][0].getLatLng()
                .distanceTo(layersPoints[0][1].getLatLng()));
            setGarbageRemovers();
            circleCreator(adjustedVector("circle"));
        });
        map.on('contextmenu', function () {
            editableLayers[0].remove();
            layersPointsCleaner();
            circleCreator(adjustedVector("circle"));
        });
    });
}

function rectangleSelector(rectSel) {
    map.off('click mousemove contextmenu');
    map.on('click', function (ev1) {
        layersPointsCleaner();
        rectSel.setBounds([ev1.latlng, ev1.latlng]);
        rectSel.addTo(map);
        let tempLayerIndex = temporaryLayers.length;
        temporaryLayers[tempLayerIndex] = rectSel;
        tempLayersPoints[tempLayerIndex] = [];
        map.on('mousemove', function (ev) {
            rectSel.setBounds([ev1.latlng, ev.latlng]);
        });
        map.off('click');
        map.on('click', function (ev2) {
            let rectSelBounds = L.latLngBounds(ev1.latlng, ev2.latlng);
            rectSel.remove();
            map.eachLayer(function (layer) {
                switch (true) {
                    case layer instanceof L.Marker:
                        if (rectSelBounds.contains(layer.getLatLng())) markerEditor(layer);
                        break;
                    case layer instanceof L.Rectangle:
                        if (rectSelBounds.intersects(layer.getBounds())) rectangleEditor(layer);
                        break;
                    case layer instanceof L.Circle:
                        if (rectSelBounds.intersects(layer.getBounds())) circleEditor(layer);
                        break;
                    case layer instanceof L.Polyline:
                        let linesArray = layer.getLatLngs();
                        for (let i = 0; i < linesArray[0].length; i++) {
                            if (rectSelBounds.contains(linesArray[0][i])) {
                                polyEditor(layer);
                                break;
                            }
                        }
                }
            });
            let isMarkerGroup = true,
                isVectorGroup = true;
            setGarbageRemovers();
            for (let i = 0; i < editableLayers.length; i++) {
                if (editableLayers[i] instanceof L.Marker) isVectorGroup = false;
                else isMarkerGroup = false;
            }
            if (isMarkerGroup) selectSettingsMode("markerSettings");
            else if (isVectorGroup) selectSettingsMode("vectorSettings");
            else selectSettingsMode("groupSelection");

            //--adds Selection Rectangle and central cross to the map after layers selection:
            rectSel.setBounds(rectSelBounds).addTo(map);
            tempLayersPoints[tempLayerIndex][0] = newSelectionCross(rectSel.getCenter()).addTo(map);
            tempLayersPoints[tempLayerIndex][0].getElement().style.cursor = "all-scroll";

            //--adds listener to moving of the selection by the central cross:
            let linesArray = temporaryLayers[tempLayerIndex].getLatLngs();
            let centerLat = tempLayersPoints[tempLayerIndex][0].getLatLng().lat;
            let centerLng = tempLayersPoints[tempLayerIndex][0].getLatLng().lng;
            tempLayersPoints[tempLayerIndex][0].dragging.enable();
            tempLayersPoints[tempLayerIndex][0].on('drag', function (ev) {
                let deltaLat = ev.latlng.lat - centerLat;
                let deltaLng = ev.latlng.lng - centerLng;
                for (let i = 0; i < editableLayers.length; i++) {
                    switch (true) {
                        case editableLayers[i] instanceof L.Marker:
                            markerMove(i, deltaLat, deltaLng);
                            break;
                        case editableLayers[i] instanceof L.Rectangle:
                            rectangleMove(i, deltaLat, deltaLng);
                            break;
                        case editableLayers[i] instanceof L.Circle:
                            circleMove(i, deltaLat, deltaLng);
                            break;
                        case editableLayers[i] instanceof L.Polyline:
                            polyMove(i, deltaLat, deltaLng);
                    }
                }
                for (let i = 0; i < linesArray[0].length; i++)
                    linesArray[0][i] = L.latLng(linesArray[0][i].lat + deltaLat, linesArray[0][i].lng + deltaLng);
                temporaryLayers[tempLayerIndex].setLatLngs(linesArray);
                tempLayersPoints[tempLayerIndex][0].setLatLng(temporaryLayers[tempLayerIndex].getCenter());
                centerLat = tempLayersPoints[tempLayerIndex][0].getLatLng().lat;
                centerLng = tempLayersPoints[tempLayerIndex][0].getLatLng().lng;
            });
            rectangleSelector(newSelectionRectangle());
        });
        map.on('contextmenu', function () {
            layersPointsCleaner();
            rectangleSelector(newSelectionRectangle());
        });
    });
}

//TODO: Shapes EDITors:
function markerEditor(marker) {
    let layerIndex = editableLayers.length;
    editableLayers[layerIndex] = marker;
    layersPoints[layerIndex] = [];
    layersPoints[layerIndex][0] = newEditPoint(marker.getLatLng()).addTo(map);
    layersPoints[layerIndex][0].getElement().style.cursor = "all-scroll";
    let centerLat = layersPoints[layerIndex][0].getLatLng().lat;
    let centerLng = layersPoints[layerIndex][0].getLatLng().lng;
    layersPoints[layerIndex][0].dragging.enable();
    layersPoints[layerIndex][0].on('drag', function (ev) {
        let deltaLat = ev.latlng.lat - centerLat;
        let deltaLng = ev.latlng.lng - centerLng;
        markerMove(layerIndex, deltaLat, deltaLng);
        centerLat = layersPoints[layerIndex][0].getLatLng().lat;
        centerLng = layersPoints[layerIndex][0].getLatLng().lng;
    });
    setGarbageRemovers();
}

function polyEditor(polyLayer) {
    let layerIndex = editableLayers.length;
    editableLayers[layerIndex] = polyLayer;
    layersPoints[layerIndex] = [];
    layersPoints[layerIndex][0] = newEditCross(getLayerCenter(editableLayers[layerIndex])).addTo(map);
    layersPoints[layerIndex][0].getElement().style.cursor = "all-scroll";
    let centerLat = layersPoints[layerIndex][0].getLatLng().lat;
    let centerLng = layersPoints[layerIndex][0].getLatLng().lng;
    let linesArray = polyLayer.getLatLngs();
    linesArray[0].forEach(function (pointLatLng) {
        let point = newEditPoint(pointLatLng).addTo(map);
        let pointIndex = layersPoints[layerIndex].length;
        layersPoints[layerIndex].push(point);
        layersPoints[layerIndex][pointIndex].getElement().style.cursor = "all-scroll";
        layersPoints[layerIndex][pointIndex].dragging.enable();
        layersPoints[layerIndex][pointIndex].on('drag', function (ev) {
            linesArray = editableLayers[layerIndex].getLatLngs();
            linesArray[0][pointIndex - 1] = ev.latlng;
            editableLayers[layerIndex].setLatLngs(linesArray);
            layersPoints[layerIndex][0].setLatLng(getLayerCenter(editableLayers[layerIndex]));
            centerLat = layersPoints[layerIndex][0].getLatLng().lat;
            centerLng = layersPoints[layerIndex][0].getLatLng().lng;
        });
    });
    layersPoints[layerIndex][0].dragging.enable();
    layersPoints[layerIndex][0].on('drag', function (ev) {
        let deltaLat = ev.latlng.lat - centerLat;
        let deltaLng = ev.latlng.lng - centerLng;
        polyMove(layerIndex, deltaLat, deltaLng);
        centerLat = layersPoints[layerIndex][0].getLatLng().lat;
        centerLng = layersPoints[layerIndex][0].getLatLng().lng;
    });
    setGarbageRemovers();
}

function rectangleEditor(rect) {
    let layerIndex = editableLayers.length;
    editableLayers[layerIndex] = rect;
    layersPoints[layerIndex] = [];
    layersPoints[layerIndex][0] = newEditCross(rect.getCenter()).addTo(map);
    layersPoints[layerIndex][0].getElement().style.cursor = "all-scroll";
    let linesArray = rect.getLatLngs();
    setCornerPoint(newEditPoint(linesArray[0][0]).addTo(map));
    setCornerPoint(newEditPoint(linesArray[0][2]).addTo(map));
    let centerLat = layersPoints[layerIndex][0].getLatLng().lat;
    let centerLng = layersPoints[layerIndex][0].getLatLng().lng;
    layersPoints[layerIndex][0].dragging.enable();
    layersPoints[layerIndex][0].on('drag', function (ev) {
        let deltaLat = ev.latlng.lat - centerLat;
        let deltaLng = ev.latlng.lng - centerLng;
        rectangleMove(layerIndex, deltaLat, deltaLng);
        centerLat = layersPoints[layerIndex][0].getLatLng().lat;
        centerLng = layersPoints[layerIndex][0].getLatLng().lng;
    });

    function setCornerPoint(point) {
        let pointIndex = layersPoints[layerIndex].length;
        layersPoints[layerIndex][pointIndex] = point;
        layersPoints[layerIndex][pointIndex].getElement().style.cursor = "ne-resize";
        layersPoints[layerIndex][pointIndex].dragging.enable();
        layersPoints[layerIndex][pointIndex].on('drag', function () {
            editableLayers[layerIndex].setBounds([layersPoints[layerIndex][1].getLatLng(),
                layersPoints[layerIndex][2].getLatLng()]);
            layersPoints[layerIndex][0].setLatLng(editableLayers[layerIndex].getCenter());
            centerLat = layersPoints[layerIndex][0].getLatLng().lat;
            centerLng = layersPoints[layerIndex][0].getLatLng().lng;
        });
    }

    setGarbageRemovers();
}

function circleEditor(circle) {
    let layerIndex = editableLayers.length;
    editableLayers[layerIndex] = circle;
    layersPoints[layerIndex] = [];
    layersPoints[layerIndex][0] = newEditCross(circle.getLatLng()).addTo(map);
    layersPoints[layerIndex][0].getElement().style.cursor = "all-scroll";
    layersPoints[layerIndex][1] = newEditPoint(L.latLng(circle.getBounds().getSouth(), circle.getLatLng().lng)).addTo(map);
    layersPoints[layerIndex][1].getElement().style.cursor = "ns-resize";
    layersPoints[layerIndex][1].dragging.enable();
    layersPoints[layerIndex][1].on('drag', function () {
        editableLayers[layerIndex].setRadius(layersPoints[layerIndex][0].getLatLng()
            .distanceTo(layersPoints[layerIndex][1].getLatLng()));
    });
    let centerLat = layersPoints[layerIndex][0].getLatLng().lat;
    let centerLng = layersPoints[layerIndex][0].getLatLng().lng;
    layersPoints[layerIndex][0].dragging.enable();
    layersPoints[layerIndex][0].on('drag', function (ev) {
        let deltaLat = ev.latlng.lat - centerLat;
        let deltaLng = ev.latlng.lng - centerLng;
        circleMove(layerIndex, deltaLat, deltaLng);
        centerLat = layersPoints[layerIndex][0].getLatLng().lat;
        centerLng = layersPoints[layerIndex][0].getLatLng().lng;
    });
    setGarbageRemovers();
}

//TODO: Layers MOVE methods:
function markerMove(layerIndex, deltaLat, deltaLng) {
    let newPoint = L.latLng(editableLayers[layerIndex].getLatLng().lat + deltaLat,
        editableLayers[layerIndex].getLatLng().lng + deltaLng);
    editableLayers[layerIndex].setLatLng(newPoint);
    layersPoints[layerIndex][0].setLatLng(newPoint);
}

function polyMove(layerIndex, deltaLat, deltaLng) {
    let linesArray = editableLayers[layerIndex].getLatLngs();
    for (let i = 0; i < linesArray[0].length; i++) {
        linesArray[0][i] = L.latLng(linesArray[0][i].lat + deltaLat, linesArray[0][i].lng + deltaLng);
        layersPoints[layerIndex][i + 1].setLatLng(linesArray[0][i]);
    }
    editableLayers[layerIndex].setLatLngs(linesArray);
    layersPoints[layerIndex][0].setLatLng(getLayerCenter(editableLayers[layerIndex]));
}

function rectangleMove(layerIndex, deltaLat, deltaLng) {
    let linesArray = editableLayers[layerIndex].getLatLngs();
    for (let i = 0; i < linesArray[0].length; i++) {
        linesArray[0][i] = L.latLng(linesArray[0][i].lat + deltaLat,
            linesArray[0][i].lng + deltaLng);
        if (i === 0) layersPoints[layerIndex][1].setLatLng(linesArray[0][i]);
        if (i === 2) layersPoints[layerIndex][2].setLatLng(linesArray[0][i]);
    }
    editableLayers[layerIndex].setLatLngs(linesArray);
    layersPoints[layerIndex][0].setLatLng(editableLayers[layerIndex].getCenter());
}

function circleMove(layerIndex, deltaLat, deltaLng) {
    let newPoint = L.latLng(editableLayers[layerIndex].getLatLng().lat + deltaLat,
        editableLayers[layerIndex].getLatLng().lng + deltaLng);
    editableLayers[layerIndex].setLatLng(newPoint);
    layersPoints[layerIndex][0].setLatLng(newPoint);
    layersPoints[layerIndex][1].setLatLng(L.latLng(editableLayers[layerIndex].getBounds().getSouth(),
        editableLayers[layerIndex].getLatLng().lng));
}

//TODO: methods to get adjusted layers(tools):
//--layers:
function adjustedMarker() {
    let marker = L.marker(map.getCenter(), {icon: adjustedIcon(markerIconUrl, true)});
    if (document.getElementById("mToolTipInput").value !== "")
        marker.bindTooltip(document.getElementById("mToolTipInput").value);
    return marker;
}

function adjustedIcon(iconUrl, doSize) {
    let markerIcon = L.icon({
        shadowUrl: '/images/marker-shadow.png',
    });
    let kSize = 1.0;
    if (doSize) kSize = document.getElementById("mSizeInput").value / 100;
    if (iconUrl === "/images/marker-icon-2x.png") {
        markerIcon.options.iconSize = [25 * kSize, 41 * kSize];
        markerIcon.options.iconAnchor = [13 * kSize - 1, 41 * kSize];
    } else {
        markerIcon.options.iconSize = [31 * kSize, 41 * kSize];
        markerIcon.options.iconAnchor = [16 * kSize - 1, 41 * kSize];
    }
    markerIcon.options.iconUrl = iconUrl;
    markerIcon.options.shadowSize = [47 * kSize + 1, 66 * kSize];
    markerIcon.options.shadowAnchor = [15 * kSize + 1, 66 * kSize];
    return markerIcon;
}

function adjustedVector(layerType) {
    let layer;
    let points = [];
    points[0] = []
    points[0][0] = map.getCenter();
    let weight = document.getElementById("borderWeight").value;
    let opacity = document.getElementById("borderOpacity").value / 100;
    let color = document.getElementById("borderColor").value;
    let fillOpacity = document.getElementById("fillOpacity").value / 100;
    let fillColor = document.getElementById("fillColor").value;
    switch (layerType) {
        case "polyline":
            layer = L.polyline(points, {weight: weight, opacity: opacity, color: color});
            break;
        case "polygon":
            layer = L.polygon(points, {
                weight: weight, opacity: opacity, color: color,
                fillOpacity: fillOpacity, fillColor: fillColor
            });
            break;
        case "rectangle":
            layer = L.rectangle(L.latLngBounds(map.getCenter(), map.getCenter()),
                {
                    weight: weight, opacity: opacity, color: color, fillOpacity: fillOpacity,
                    fillColor: fillColor
                });
            break;
        case "circle":
            layer = L.circle(map.getCenter(), {
                radius: 0, weight: weight, opacity: opacity,
                color: color, fillOpacity: fillOpacity, fillColor: fillColor
            });
    }
    return layer;
}

//--map service tools:
function newSelectionRectangle() {
    return L.rectangle(L.latLngBounds(map.getCenter(), map.getCenter()),
        {
            color: "#56add5", opacity: 1, weight: 3, fill: false,
            dashArray: "8, 8"
        });
}

function newSelectionCross(point) {
    return L.marker(point, {icon: selectionCrossIcon, zIndexOffset: 600});
}

function newEditCross(point) {
    return L.marker(point, {icon: editCrossIcon, zIndexOffset: 500, opacity: 1});
}

function newEditPoint(point) {
    return L.marker(point, {icon: editPointIcon, zIndexOffset: 500, opacity: 1});
}

//TODO: --MENU functions:
//-----ADD/DELETE layer group functions:
function onClickAddDel() {
    document.getElementById("addMsgColor").setAttribute("style", "background-color: #5b5be0;");
    document.getElementById("addMsgText").innerHTML = "Enter a name for the new layer group " +
        "and click <strong>CREATE</strong> or select a layer group and click <strong>REMOVE</strong>";
    document.getElementById("divSet_1").setAttribute("style", "display: block; height: "
        + (mapHeight - 139) + "px;");
    document.getElementById("divSet_1_scroll").setAttribute("style", "overflow: auto; height: " +
        +(mapHeight - 134 - document.getElementById("addMsgText").clientHeight) + "px");
    document.getElementById("divSet_3").setAttribute("style", "display: none;");
    document.getElementById("divSet_4").setAttribute("style", "display: none;");
    layerGrDivRefresh();
    layerGrSelRefresh();
}

function onClickLayerGrAdd() {
    const newLGroupName = document.getElementById("addField").value;
    let duplicate = false;
    for (let layerName in userLayerGroups) {
        if (layerName === newLGroupName) duplicate = true;
    }
    if (!duplicate && newLGroupName !== "") {
        document.getElementById("addField").value = "";
        userLayerGroups[newLGroupName] = L.layerGroup();
        controlPaneRefresh();
        document.getElementById("addMsgColor").setAttribute("style", "background-color: #5b5be0;");
        document.getElementById("addMsgText").innerHTML = "Enter a name for the new layer group " +
            "and click <strong>ADD</strong> or select a layer group and click <strong>REMOVE</strong>";
        layerGrDivRefresh();
        layerGrSelRefresh();
    } else {
        document.getElementById("addMsgColor").setAttribute("style", "background-color: red;");
        document.getElementById("addMsgText").innerText = "Layer group name is NULL or already EXISTS";
    }
}

function onClickLayerGrDel() {
    const forDelLGroupName = document.getElementById("existedLGroupsSelector0").value;
    delete userLayerGroups[forDelLGroupName];
    controlPaneRefresh();
    layerGrDivRefresh();
    layerGrSelRefresh();
    layerGrOwnerSelRefresh();
}

//-----LOAD/SAVE layer group functions:
function onClickLoadSave() {
    document.getElementById("divSet_1").setAttribute("style", "display: none;");
    document.getElementById("divSet_3").setAttribute("style", "display: block; height: "
        + (mapHeight - 139) + "px;");
    document.getElementById("divSet_3_scroll").setAttribute("style", "overflow: auto; height: " +
        +(mapHeight - 134 - document.getElementById("bdMsgText").clientHeight) + "px");
    document.getElementById("divSet_4").setAttribute("style", "display: none;");
    layerGrSelRefresh();
    layerGrDbSelRefresh();
}

function layerGrDbSelRefresh() {
    const xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/getLayerGroups");
    xhttp.send();
    xhttp.onload = function () {
        let groupsNames = JSON.parse(this.response);
        let selector = document.getElementById("savedGroups");
        selector.getElementsByTagName("select")[0].remove();
        const newSelection = document.createElement("select");
        newSelection.setAttribute("id", "savedGroupSelection");
        newSelection.setAttribute("style", "height: 30px; width: 95%;");
        selector.appendChild(newSelection);
        for (let i = 0; i < groupsNames.length; i++) {
            let layerOption = document.createElement("option");
            layerOption.setAttribute("value", groupsNames[i]);
            layerOption.innerText = groupsNames[i];
            document.getElementById("savedGroupSelection").appendChild(layerOption);
        }
    }
}

function saveGroupToDB() {
    const forSaveGroupName = document.getElementById("existedLGroupsSelector1").value;
    let layers = userLayerGroups[forSaveGroupName].getLayers();
    let markers = [];
    let polygons = [];
    let circles = [];
    let layerGroup = {name: forSaveGroupName};
    for (let i = 0; i < layers.length; i++) {
        switch (true) {
            case layers[i] instanceof L.Marker:
                let marker = {};
                let sourceIcon = layers[i].getIcon();
                let icon = {};
                for (let option in sourceIcon.options) {
                    icon[option] = sourceIcon.options[option];
                }
                marker.center = [];
                marker.center[0] = layers[i].getLatLng().lat;
                marker.center[1] = layers[i].getLatLng().lng;
                for (let option in layers[i].options) {
                    marker[option] = layers[i].options[option];
                }
                marker.icon = icon;
                markers.push(marker);
                break;
            case layers[i] instanceof L.Polyline:
                let polygon = {};
                let points = [];
                let latLngs = layers[i].getLatLngs();
                for (let i = 0; i < latLngs[0].length; i++) {
                    let point = {};
                    let coordinate = [];
                    coordinate[0] = latLngs[0][i].lat;
                    coordinate[1] = latLngs[0][i].lng;
                    point.coordinate = coordinate;
                    points.push(point);
                }
                polygon.points = points;
                polygon.layerType = "polyline";
                if (layers[i] instanceof L.Polygon) polygon.layerType = "polygon";
                if (layers[i] instanceof L.Rectangle) polygon.layerType = "rectangle";
                for (let option in layers[i].options) {
                    polygon[option] = layers[i].options[option];
                }
                polygons.push(polygon);
                break;
            case layers[i] instanceof L.Circle:
                let circle = {};
                circle.center = [];
                circle.center[0] = layers[i].getLatLng().lat;
                circle.center[1] = layers[i].getLatLng().lng;
                for (let option in layers[i].options) {
                    circle[option] = layers[i].options[option];
                }
                circle.radius = layers[i].getRadius();
                circles.push(circle);
                break;
        }
    }
    layerGroup.markers = markers;
    layerGroup.polygons = polygons;
    layerGroup.circles = circles;
    const xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/saveLayerGroup");
    xhttp.setRequestHeader("Content-Type", "application/json");
    xhttp.send(JSON.stringify(layerGroup));
    xhttp.onload = function () {
        layerGrDbSelRefresh();
        document.getElementById("debugMsg").innerHTML = "<strong>" + this.response + "</strong>"
            + " To browse current <strong>LAYER GROUPS</strong> use icon - " +
            "<img src='/images/layers.png' width=16px>";
    }
}

function loadGroupFromDB() {
    const xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/loadLayerGroup");
    xhttp.setRequestHeader("Content-Type", "text/html;charset=UTF-8");
    xhttp.send(document.getElementById("savedGroupSelection").value);
    xhttp.onload = function () {
        let layerGroupData = JSON.parse(this.response);
        let layerGroupName = layerGroupData.name;
        let layerGroup = L.layerGroup();
        let markers = layerGroupData.markers;
        let polygons = layerGroupData.polygons;
        let circles = layerGroupData.circles;
        for (let i = 0; i < markers.length; i++) {
            let markerOptions = {};
            let markerCenter = L.latLng(markers[i].center[0], markers[i].center[1]);
            let sourceIcon = markers[i].icon;
            let iconOptions = {};
            for (let option in sourceIcon) {
                if (option === "id" || option === "marker") continue;
                iconOptions[option] = sourceIcon[option];
            }
            markerOptions.icon = L.icon(iconOptions);
            for (let option in markers[i]) {
                if (option === "id" || option === "layerGroup" ||
                    option === "center" || option === "icon") continue;
                markerOptions[option] = markers[i][option];
            }
            L.marker(markerCenter, markerOptions).addTo(map).addTo(layerGroup);
        }
        for (let i = 0; i < polygons.length; i++) {
            let polygonOptions = {};
            let polygon;
            let points = [];
            points[0] = [];
            for (let p = 0; p < polygons[i].points.length; p++) {
                points[0][p] = L.latLng(polygons[i].points[p].coordinate[0],
                    polygons[i].points[p].coordinate[1]);
            }
            for (let option in polygons[i]) {
                if (option === "id" || option === "layerGroup" ||
                    option === "points" || option === "layerType") continue;
                polygonOptions[option] = polygons[i][option];
            }
            switch (polygons[i].layerType) {
                case "rectangle":
                    let rectBounds = [points[0], points[2]];
                    polygon = L.rectangle(rectBounds, polygonOptions);
                    break;
                case "polygon":
                    polygon = L.polygon(points, polygonOptions);
                    break;
                case "polyline":
                    polygon = L.polyline(points, polygonOptions);
            }
            polygon.addTo(map).addTo(layerGroup);
        }
        for (let i = 0; i < circles.length; i++) {
            let circleOptions = {};
            let circleCenter = L.latLng(circles[i].center[0], circles[i].center[1]);
            for (let option in circles[i]) {
                if (option === "id" || option === "layerGroup" ||
                    option === "center") continue;
                circleOptions[option] = circles[i][option];
            }
            let circle = L.circle(circleCenter, circleOptions);
            circle.setRadius(circles[i].radius);
            circle.addTo(map).addTo(layerGroup);
        }
        for (let layerName in userLayerGroups) {
            if (layerName === layerGroupName) delete userLayerGroups[layerGroupName];
        }
        userLayerGroups[layerGroupName] = layerGroup;
        controlPaneRefresh();
        document.getElementById("debugMsg").innerHTML =
            "Layer group:<strong>'" + layerGroupName + "'</strong> loaded." +
            " To browse current <strong>LAYER GROUPS</strong> use icon - " +
            "<img src='/images/layers.png' width=16px>";
    }
}

function deleteGroupFromDB() {
    const xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/deleteLayerGroup");
    xhttp.setRequestHeader("Content-Type", "text/html;charset=UTF-8");
    xhttp.send(document.getElementById("savedGroupSelection").value);
    xhttp.onload = function () {
        layerGrDbSelRefresh();
        document.getElementById("debugMsg").innerHTML = "<strong>" + this.response + "</strong>"
            + " To browse current <strong>LAYER GROUPS</strong> use icon - " +
            "<img src='/images/layers.png' width=16px>";
    }
}

//-----EDIT layer group functions:
function onClickDivEdit() {
    document.getElementById("divSet_1").setAttribute("style", "display: none;");
    document.getElementById("divSet_3").setAttribute("style", "display: none;");
    document.getElementById("divSet_4").setAttribute("style", "display: block; height: "
        + (mapHeight - 139) + "px;");
    document.getElementById("divSet_4_scroll").setAttribute("style", "overflow: auto; height: "
        + (mapHeight - 134 - document.getElementById("editMsgText").clientHeight) + "px");
    layerGrSelRefresh();
}

function selectSettingsMode(mode) {
    layerGrSelRefresh();
    document.getElementById("markerSettings").setAttribute("style", "display: none;");

    document.getElementById("vectorSettings").setAttribute("style", "display: none;");
    document.getElementById("groupSelection").setAttribute("style", "display: none;");
    if (mode !== "none") {
        document.getElementById(mode).setAttribute("style", "display: block;");
        layerGrOwnerSelRefresh();
    }
}

function mIconShifter(mIndex) {
    for (let i = 0; i < 8; i++)
        document.getElementById("marker" + i).removeAttribute("style");
    document.getElementById("marker" + mIndex).setAttribute("style", "background-color: #dddddd;");
    if (mIndex === 0) {
        markerIconUrl = "/images/marker-icon-2x.png";
    } else {
        markerIconUrl = "/images/layers/markers/" + mIndex + "Tr.png";
    }
    changeSelectedMarkers("changeIcon");
}

function changeSelectedMarkers(mode) {
    let changeSize = false;
    for (let i = 0; i < editableLayers.length; i++) {
        let iconSource = editableLayers[i].getIcon().options.iconUrl;
        if (editableLayers[i] instanceof L.Marker) {
            switch (mode) {
                case "changeSize":
                    changeSize = true;
                    break;
                case "changeIcon":
                    iconSource = markerIconUrl;
                    break;
                case "changeTooltip":
                    editableLayers[i].bindTooltip(document.getElementById("mToolTipInput").value);
                    if (document.getElementById("mToolTipInput").value === "") editableLayers[i].unbindTooltip();
            }
            editableLayers[i].setIcon(adjustedIcon(iconSource, changeSize));
        }
    }
}

function changeSelectedVectors(mode) {
    for (let i = 0; i < editableLayers.length; i++) {
        if (editableLayers[i] instanceof L.Polyline || editableLayers[i] instanceof L.Circle) {
            switch (mode) {
                case "changeWeight":
                    let weight = document.getElementById("borderWeight").value;
                    editableLayers[i].setStyle({weight: weight});
                    break;
                case "changeOpacity":
                    let opacity = document.getElementById("borderOpacity").value / 100;
                    editableLayers[i].setStyle({opacity: opacity});
                    break;
                case "changeColor":
                    let color = document.getElementById("borderColor").value;
                    editableLayers[i].setStyle({color: color});
                    break;
                case "changeFillOpacity":
                    let fillOpacity = document.getElementById("fillOpacity").value / 100;
                    editableLayers[i].setStyle({fillOpacity: fillOpacity});
                    break;
                case "changeFillColor":
                    let fillColor = document.getElementById("fillColor").value;
                    editableLayers[i].setStyle({fillColor: fillColor});
                    break;
                case "changeTooltip":
                    editableLayers[i].bindTooltip(document.getElementById("vToolTip").value);
                    if (document.getElementById("vToolTip").value === "") editableLayers[i].unbindTooltip();
            }
            editableLayers[i].redraw();
        }
    }
}

function onClickToLGroupAdd(selectorNumber) {
    let layerName = document.getElementById("existedLGroupsSelector" + selectorNumber).value;
    for (let i = 0; i < editableLayers.length; i++)
        userLayerGroups[layerName].addLayer(editableLayers[i]);
    layerGrOwnerSelRefresh();
}

function onClickFromOwnerDel(selectorNumber) {
    let layerName = document.getElementById("ownerSelector" + selectorNumber).value;
    for (let i = 0; i < editableLayers.length; i++)
        userLayerGroups[layerName].removeLayer(editableLayers[i]);
    layersPointsCleaner()
    layerGrOwnerSelRefresh();
}

//TODO: --Auxiliary functions:
function getLayerCenter(polyLayer) {
    let centerLatLng = polyLayer.getCenter();
    let pLayerBounds = polyLayer.getBounds();
    if (!pLayerBounds.contains(centerLatLng)) {
        centerLatLng = L.latLng((pLayerBounds.getNorth() + pLayerBounds.getSouth()) / 2,
            (pLayerBounds.getEast() + pLayerBounds.getWest()) / 2);
    }
    return centerLatLng;
}

function layerGrDivRefresh() {
    document.getElementById("existingList").getElementsByTagName("div")[0].remove();
    const newDivList = document.createElement("div");
    newDivList.setAttribute("style", "width: 95%; height: 105px; background-color: white; " +
        "border: 1px solid gray; padding: 0 0 0 3px; overflow: auto");
    let str = document.createElement("strong");
    str.innerText = "User layer groups:";
    newDivList.appendChild(str);
    newDivList.appendChild(document.createElement("br"));
    for (let layerName in userLayerGroups) {
        let a = document.createElement("a");
        a.innerText = layerName;
        newDivList.appendChild(a);
        newDivList.appendChild(document.createElement("br"));
    }
    newDivList.appendChild(document.createElement("br"));
    str = document.createElement("strong");
    str.innerText = "Base map title layers:";
    newDivList.appendChild(str);
    newDivList.appendChild(document.createElement("br"));
    for (let layerName in baseLayers) {
        let a = document.createElement("a");
        a.innerText = layerName;
        newDivList.appendChild(a);
        newDivList.appendChild(document.createElement("br"));
    }
    document.getElementById("existingList").appendChild(newDivList);
}

function layerGrSelRefresh() {
    let selectors = document.getElementsByClassName("layerSelection");
    for (let i = 0; i < selectors.length; i++) {
        selectors[i].getElementsByTagName("select")[0].remove();
        const newSelection = document.createElement("select");
        newSelection.setAttribute("id", "existedLGroupsSelector" + i);
        newSelection.setAttribute("style", "height: 30px; width: 95%;");
        selectors[i].appendChild(newSelection);
        for (let layerName in userLayerGroups) {
            let layerOption = document.createElement("option");
            layerOption.setAttribute("value", layerName);
            layerOption.innerText = layerName;
            document.getElementById("existedLGroupsSelector" + i).appendChild(layerOption);
        }
    }
}

function layerGrOwnerSelRefresh() {
    let selectors = document.getElementsByClassName("ownerSelection");
    for (let i = 0; i < selectors.length; i++) {
        selectors[i].getElementsByTagName("select")[0].remove();
        const newSelection = document.createElement("select");
        newSelection.setAttribute("id", "ownerSelector" + i);
        newSelection.setAttribute("style", "height: 30px; width: 95%;");
        selectors[i].appendChild(newSelection);
        for (let layerName in userLayerGroups) {
            let layerGroup = userLayerGroups[layerName];
            if (editableLayers[0] != null && layerGroup.hasLayer(editableLayers[0])) {
                let layerOption = document.createElement("option");
                layerOption.setAttribute("value", layerName);
                layerOption.innerText = layerName;
                document.getElementById("ownerSelector" + i).appendChild(layerOption);
            }
        }
    }
}

function removeFromMap() {
    for (let i = 0; i < editableLayers.length; i++) {
        editableLayers[i].remove();
        for (let j = 0; j < layersPoints[i].length; j++) {
            layersPoints[i][j].remove();
        }
    }
    layersPointsCleaner();
}

function layersPointsCleaner() {
    editableLayers = [];
    let layerPoints = [];
    while (layersPoints.length !== 0) {
        layerPoints = layersPoints.shift();
        while (layerPoints.length !== 0) {
            let point = layerPoints.shift();
            point.remove();
        }
    }
    while (temporaryLayers.length !== 0) {
        let layer = temporaryLayers.shift();
        layer.remove();
    }
    let tempLayerPoints = [];
    while (tempLayersPoints.length !== 0) {
        tempLayerPoints = tempLayersPoints.shift();
        while (tempLayerPoints.length !== 0) {
            let point = tempLayerPoints.shift();
            point.remove();
        }
    }
    setGarbageRemovers();
}

function setGarbageRemovers() {
    let removers = document.getElementsByClassName("garbageCounter");
    for (let i = 0; i < removers.length; i++) {
        removers[i].innerText = editableLayers.length;
    }
}

function iconMenuShifter() {
    for (let i = 1; i < 9; i++) document.getElementById("i" + i).removeAttribute("style");
    map.off('click mousemove contextmenu');
    layersPointsCleaner();
    setGarbageRemovers();
    editMode = false;
    map.eachLayer(function (layer) {
        if (layer instanceof L.Marker) layer.off('click');
        if (layer instanceof L.Polyline) layer.off('click');
        if (layer instanceof L.Circle) layer.off('click');
    });
}

function controlPaneRefresh() {
    layerControl.remove();
    layerControl = L.control.layers(baseLayers, userLayerGroups);
    layerControl.addTo(map);
    layerControl.getContainer().addEventListener('mousedown', function () {
        layersPointsCleaner();
    }, true);
}
