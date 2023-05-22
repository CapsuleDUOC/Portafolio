-- ======================================================================
-- ===   Sql Script for Database : FERIA_VIRTUAL
-- ===
-- === Build : 76
-- ======================================================================

CREATE TABLE archivo
  (
    id                 bigint         unique not null auto_increment,
    nombre             varchar(255)   not null,
    path               varchar(255),
    bytes              longblob,
    estado             varchar(25)    not null,
    registro_instante  datetime       not null,

    primary key(id)
  )
 ENGINE = InnoDB;

CREATE INDEX archivoIDX1 ON archivo(id);

-- ======================================================================

CREATE TABLE usuario
  (
    id                   bigint        unique not null auto_increment,
    tipo_identificacion  varchar(25)   not null,
    identificacion       varchar(25)   unique not null,
    nombre               varchar(25)   not null,
    estado               varchar(25)   not null,
    registro_instante    datetime      not null,
    telefono             varchar(25)   not null,

    primary key(id)
  )
 ENGINE = InnoDB;

CREATE INDEX usuarioIDX1 ON usuario(id);

-- ======================================================================

CREATE TABLE usuario_rol
  (
    id          bigint        unique not null auto_increment,
    usuario_id  bigint        not null,
    rol         varchar(25)   not null,

    primary key(id),

    foreign key(usuario_id) references usuario(id)
  )
 ENGINE = InnoDB;

CREATE INDEX usuario_rolIDX1 ON usuario_rol(id);

-- ======================================================================

CREATE TABLE usuario_auth
  (
    id             bigint         unique not null auto_increment,
    usuario_id     bigint,
    email          varchar(50)    unique not null,
    password       varchar(255)   not null,
    ultimo_acceso  datetime,
    token          text,

    primary key(id),

    foreign key(usuario_id) references usuario(id)
  )
 ENGINE = InnoDB;

CREATE INDEX usuario_authIDX1 ON usuario_auth(id);

-- ======================================================================

CREATE TABLE usuario_bitacora
  (
    id                 bigint         unique not null auto_increment,
    usuario_id         bigint         not null,
    registro           varchar(255)   not null,
    registro_instante  datetime       not null,

    primary key(id),

    foreign key(usuario_id) references usuario(id)
  )
 ENGINE = InnoDB;

CREATE INDEX usuario_bitacoraIDX1 ON usuario_bitacora(id);

-- ======================================================================

CREATE TABLE usuario_archivo
  (
    usuario_id  bigint,
    archivo_id  bigint,

    unique(usuario_id,archivo_id),

    foreign key(usuario_id) references usuario(id),
    foreign key(archivo_id) references archivo(id)
  )
 ENGINE = InnoDB;

-- ======================================================================

CREATE TABLE usuario_propiedad
  (
    id          bigint        not null auto_increment,
    usuario_id  bigint        not null,
    llave       varchar(25)   not null,
    valor       text,

    primary key(id),

    foreign key(usuario_id) references usuario(id)
  )
 ENGINE = InnoDB;

CREATE INDEX usuario_propiedadIDX1 ON usuario_propiedad(id);

-- ======================================================================

CREATE TABLE vehiculo
  (
    id                 bigint        unique not null auto_increment,
    usuario_id         bigint        not null,
    tipo               varchar(25)   not null,
    patente            varchar(10)   not null,
    marca              varchar(25)   not null,
    modelo             varchar(25)   not null,
    agno               varchar(4)    not null,
    registro_instante  datetime      not null,

    primary key(id),
    unique(usuario_id,patente),

    foreign key(usuario_id) references usuario(id)
  )
 ENGINE = InnoDB;

CREATE INDEX vehiculoIDX1 ON vehiculo(id);

-- ======================================================================

CREATE TABLE vehiculo_archivo
  (
    vehiculo_id  bigint   not null,
    archivo_id   bigint   not null,

    foreign key(vehiculo_id) references vehiculo(id),
    foreign key(archivo_id) references archivo(id)
  )
 ENGINE = InnoDB;

-- ======================================================================

CREATE TABLE producto
  (
    id                 bigint        unique not null auto_increment,
    usuario_id         bigint        not null,
    codigo             varchar(50)   not null,
    nombre             varchar(50)   not null,
    tipo               varchar(25)   not null,
    unidad_medida      varchar(25)   not null,
    precio             bigint        not null,
    estado             varchar(25)   not null,
    registro_instante  datetime      not null,
    archivo_imagen     bigint,

    primary key(id),
    unique(usuario_id,codigo),

    foreign key(usuario_id) references usuario(id),
    foreign key(archivo_imagen) references archivo(id)
  )
 ENGINE = InnoDB;

CREATE INDEX productoIDX1 ON producto(id);

-- ======================================================================

CREATE TABLE cosecha
  (
    id             bigint        unique not null auto_increment,
    agricultor_id  bigint        not null,
    producto_id    bigint        not null,
    cantidad       double        not null,
    unidad_medida  varchar(25),
    costo          bigint,

    primary key(id),

    foreign key(agricultor_id) references usuario(id),
    foreign key(producto_id) references producto(id)
  )
 ENGINE = InnoDB;

CREATE INDEX cosechaIDX1 ON cosecha(id);

-- ======================================================================

CREATE TABLE direccion
  (
    id           bigint        unique not null auto_increment,
    usuario_id   bigint        not null,
    direccion    varchar(50)   not null,
    comuna       varchar(50)   not null,
    ciudad       varchar(50)   not null,
    ubigeo_lat   double,
    ubigeo_long  double,

    primary key(id),

    foreign key(usuario_id) references usuario(id)
  )
 ENGINE = InnoDB;

CREATE INDEX direccionIDX1 ON direccion(id);

-- ======================================================================

CREATE TABLE comision
  (
    id              bigint        unique not null auto_increment,
    tipo_operacion  varchar(25)   not null,
    factor          double        not null,

    primary key(id)
  )
 ENGINE = InnoDB;

CREATE INDEX comisionIDX1 ON comision(id);

-- ======================================================================

CREATE TABLE comision_operacion
  (
    id              bigint        unique not null auto_increment,
    usuario_id      bigint        not null,
    comision_id     bigint        not null,
    tipo_operacion  varchar(25)   not null,
    operacion_id    bigint        not null,
    estado          varchar(25)   not null,
    valor           bigint        not null,

    primary key(id),

    foreign key(usuario_id) references usuario(id),
    foreign key(comision_id) references comision(id)
  )
 ENGINE = InnoDB;

CREATE INDEX comision_operacionIDX1 ON comision_operacion(id);

-- ======================================================================

CREATE TABLE dte
  (
    id             bigint        unique not null auto_increment,
    tipo_dte       int           not null,
    folio          bigint        not null,
    emisor         bigint        not null,
    receptor       bigint        not null,
    forma_pago     varchar(50),
    fecha_emision  date,
    total_neto     bigint        not null,
    total_bruto    bigint        not null,
    xml            bigint        not null,

    primary key(id),

    foreign key(emisor) references usuario(id),
    foreign key(receptor) references usuario(id),
    foreign key(xml) references archivo(id)
  )
 ENGINE = InnoDB;

CREATE INDEX dteIDX1 ON dte(id);

-- ======================================================================

CREATE TABLE pedido
  (
    id                 bigint   unique not null auto_increment,
    despachador_id     bigint   not null,
    vehiculo_id        bigint,
    direccion_origen   bigint   not null,
    direccion_destino  bigint   not null,
    monto_despacho     bigint   not null,
    fecha              date     not null,
    hora               time     not null,

    primary key(id),

    foreign key(despachador_id) references usuario(id),
    foreign key(vehiculo_id) references vehiculo(id),
    foreign key(direccion_origen) references direccion(id),
    foreign key(direccion_destino) references direccion(id)
  )
 ENGINE = InnoDB;

CREATE INDEX pedidoIDX1 ON pedido(id);

-- ======================================================================

CREATE TABLE pedido_producto
  (
    id           bigint   unique not null auto_increment,
    pedido_id    bigint   not null,
    producto_id  bigint   not null,
    cantidad     double   not null,

    primary key(id),

    foreign key(pedido_id) references pedido(id),
    foreign key(producto_id) references producto(id)
  )
 ENGINE = InnoDB;

CREATE INDEX pedido_productoIDX1 ON pedido_producto(id);

-- ======================================================================

CREATE TABLE carrito
  (
    id                 bigint     unique not null auto_increment,
    cliente_id         bigint     not null,
    registro_instante  datetime   not null,

    primary key(id),

    foreign key(cliente_id) references usuario(id)
  )
 ENGINE = InnoDB;

CREATE INDEX carritoIDX1 ON carrito(id);

-- ======================================================================

CREATE TABLE carrito_producto
  (
    carrito_id   bigint   not null,
    producto_id  bigint   not null,

    foreign key(carrito_id) references carrito(id),
    foreign key(producto_id) references producto(id)
  )
 ENGINE = InnoDB;

-- ======================================================================

CREATE TABLE transporte
  (
    id                 bigint   unique not null auto_increment,
    agricultor_id      bigint   not null,
    transportista_id   bigint,
    locatario_id       bigint   not null,
    direccion_origen   bigint   not null,
    direccion_destino  bigint   not null,
    fecha_salida       date,
    fecha_llegada      date,
    costo              bigint,
    dte                bigint,

    primary key(id),

    foreign key(agricultor_id) references usuario(id),
    foreign key(transportista_id) references usuario(id),
    foreign key(locatario_id) references usuario(id),
    foreign key(direccion_origen) references direccion(id),
    foreign key(direccion_destino) references direccion(id),
    foreign key(dte) references dte(id)
  )
 ENGINE = InnoDB;

CREATE INDEX transporteIDX1 ON transporte(id);

-- ======================================================================

CREATE TABLE dte_producto
  (
    id               bigint   unique not null auto_increment,
    dte_id           bigint   not null,
    producto_id      bigint   not null,
    volumen          double,
    peso_kilogramos  double   not null,

    primary key(id),

    foreign key(dte_id) references dte(id),
    foreign key(producto_id) references producto(id)
  )
 ENGINE = InnoDB;

CREATE INDEX dte_productoIDX1 ON dte_producto(id);

-- ======================================================================

CREATE TABLE transporte_cosecha
  (
    transporte_id  bigint   not null,
    cosecha_id     bigint   not null,

    foreign key(transporte_id) references transporte(id),
    foreign key(cosecha_id) references cosecha(id)
  )
 ENGINE = InnoDB;

-- ======================================================================

CREATE TABLE venta
  (
    id            bigint   unique not null auto_increment,
    locatario_id  bigint   not null,
    cliente_id    bigint   not null,
    pedido_id     bigint,
    dte_id        bigint,
    monto_venta   bigint   not null,
    fecha         date     not null,
    hora          time     not null,

    primary key(id),

    foreign key(locatario_id) references usuario(id),
    foreign key(cliente_id) references usuario(id),
    foreign key(pedido_id) references pedido(id),
    foreign key(dte_id) references dte(id)
  )
 ENGINE = InnoDB;

CREATE INDEX ventaIDX1 ON venta(id);

-- ======================================================================

