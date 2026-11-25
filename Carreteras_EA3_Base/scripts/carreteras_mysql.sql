-- =====================================================================
-- Red Vial - Base de datos (MySQL 8)
-- Crea la base de datos carreteras, sus tablas y los datos iniciales.
-- Ejecuta el script completo. No lo modifiques.
-- =====================================================================

DROP DATABASE IF EXISTS carreteras;
CREATE DATABASE carreteras CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE carreteras;

-- Catálogo de tipos
CREATE TABLE tipo_carretera (
    id_tipo      INT          NOT NULL,
    descripcion  VARCHAR(30)  NOT NULL,
    CONSTRAINT pk_tipo_carretera PRIMARY KEY (id_tipo),
    CONSTRAINT uq_tipo_carretera_descripcion UNIQUE (descripcion)
);

-- Registros.
-- tarifa_peaje, iluminada, tag_habilitado se usan solo para Concesionada;
-- altitud_maxima se usa solo para Estatal.
CREATE TABLE carretera (
    id               INT           NOT NULL AUTO_INCREMENT,
    id_tipo          INT           NOT NULL,
    codigo           VARCHAR(80)   NOT NULL,
    numero_pistas    INT           NOT NULL,
    longitud_km      DECIMAL(10,2) NOT NULL,
    tarifa_peaje     DECIMAL(10,2) NULL,
    iluminada        BOOLEAN       NULL,
    tag_habilitado   BOOLEAN       NULL,
    altitud_maxima   DECIMAL(10,2) NULL,
    CONSTRAINT pk_carretera PRIMARY KEY (id),
    CONSTRAINT fk_carretera_tipo FOREIGN KEY (id_tipo) REFERENCES tipo_carretera (id_tipo),
    CONSTRAINT ck_carretera_numero_pistas CHECK (numero_pistas BETWEEN 1 AND 8),
    CONSTRAINT ck_carretera_longitud_km CHECK (longitud_km > 0),
    CONSTRAINT ck_carretera_tarifa_peaje CHECK (tarifa_peaje > 0),
    CONSTRAINT ck_carretera_altitud_maxima CHECK (altitud_maxima > 0)
);

-- Datos iniciales
INSERT INTO tipo_carretera (id_tipo, descripcion) VALUES (1, 'Concesionada');
INSERT INTO tipo_carretera (id_tipo, descripcion) VALUES (2, 'Estatal');
