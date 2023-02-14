CREATE TABLE IF NOT EXISTS tiles
(
    z             integer,
    x             integer,
    y             integer,
    tilebytearray bytea NOT NULL,
    source        character varying(50),
    addingtime    character varying(50),
    PRIMARY KEY (x, y, z)
);
CREATE TABLE IF NOT EXISTS tifftiles
(
    z             integer,
    x             integer,
    y             integer,
    tilebytearray bytea NOT NULL,
    source        character varying(50),
    addingtime    character varying(50),
    PRIMARY KEY (x, y, z)
);

-- drop table if exists circles;
-- drop table if exists points;
-- drop table if exists polygons;
-- drop table if exists icons;
-- drop table if exists markers;
-- drop table if exists layer_groups;

CREATE TABLE IF NOT EXISTS layer_groups
(
    id          bigint generated always as identity primary key,
    name        varchar(50) NOT NULL UNIQUE,
    adding_time varchar(50)
);

CREATE TABLE IF NOT EXISTS markers
(
    id                    bigint generated always as identity primary key,
    layer_group_id        bigint NOT NULL,
    center                float8[],
    interactive           boolean,
    keyboard              boolean,
    title                 varchar(50),
    alt                   varchar(50),
    z_index_offset        integer,
    opacity               float4,
    rise_on_hover         boolean,
    rise_offset           integer,
    pane                  varchar(20),
    shadow_pane           varchar(20),
    bubbling_mouse_events boolean,
    auto_pan_on_focus     boolean,
    draggable             boolean,
    auto_pan              boolean,
    auto_pan_padding      integer[],
    auto_pan_speed        integer,
    attribution           varchar(100),
    CONSTRAINT fk_layer_group FOREIGN KEY (layer_group_id) REFERENCES layer_groups (id)
);

CREATE TABLE IF NOT EXISTS icons
(
    id                bigint generated always as identity primary key,
    marker_id         bigint NOT NULL,
    icon_url          varchar(100),
    icon_retina_url   varchar(100),
    icon_size         integer[],
    icon_anchor       integer[],
    popup_anchor      integer[],
    tooltip_anchor    integer[],
    shadow_url        varchar(100),
    shadow_retina_url varchar(100),
    shadow_size       integer[],
    shadow_anchor     integer[],
    class_name        varchar(50),
    cross_origin      varchar(100),
    CONSTRAINT fk_marker FOREIGN KEY (marker_id) REFERENCES markers (id)
);

CREATE TABLE IF NOT EXISTS polygons
(
    id                    bigint generated always as identity primary key,
    layer_group_id        bigint NOT NULL,
    layer_type            varchar(20),
    weight                integer,
    opacity               float4,
    color                 varchar(10),
    fill_opacity          float4,
    fill_color            varchar(10),
    fill                  boolean,
    smooth_factor         float4,
    no_clip               boolean,
    stroke                boolean,
    line_cap              varchar(20),
    line_join             varchar(20),
    dash_array            varchar(50),
    dash_offset           varchar(50),
    fill_rule             varchar(20),
    interactive           boolean,
    bubbling_mouse_events boolean,
    pane                  varchar(20),
    attribution           varchar(100),
    CONSTRAINT fk_layer_group FOREIGN KEY (layer_group_id) REFERENCES layer_groups (id)
);

CREATE TABLE IF NOT EXISTS points
(
    id           bigint generated always as identity primary key,
    polygon_id   bigint NOT NULL,
    coordinate   float8[],
    CONSTRAINT fk_polygon FOREIGN KEY (polygon_id) REFERENCES polygons (id)
);

CREATE TABLE IF NOT EXISTS circles
(
    id                    bigint generated always as identity primary key,
    layer_group_id        bigint NOT NULL,
    center                float8[],
    radius                integer,
    weight                integer,
    opacity               float4,
    color                 varchar(10),
    fill_opacity          float4,
    fill_color            varchar(10),
    fill                  boolean,
    stroke                boolean,
    line_cap              varchar(20),
    line_join             varchar(20),
    dash_array            varchar(50),
    dash_offset           varchar(50),
    fill_rule             varchar(20),
    interactive           boolean,
    bubbling_mouse_events boolean,
    pane                  varchar(20),
    attribution           varchar(100),
    CONSTRAINT fk_layer_group FOREIGN KEY (layer_group_id) REFERENCES layer_groups (id)
);




