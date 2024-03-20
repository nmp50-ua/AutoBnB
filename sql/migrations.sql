-- Usuario
CREATE TABLE IF NOT EXISTS Usuario (
    idUsuario INT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(50) NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    telefono INT NOT NULL,
    direccion VARCHAR(100) NOT NULL,
    ciudad VARCHAR(50) NOT NULL,
    codigoPostal INT NOT NULL,
    dni VARCHAR(9) UNIQUE NOT NULL,
    fechaCaducidadDni DATE NOT NULL,
    fechaCarnetConducir DATE NOT NULL,
    administrador BOOLEAN DEFAULT FALSE NOT NULL,
    esArrendador BOOLEAN DEFAULT FALSE NOT NULL,
    esArrendatario BOOLEAN DEFAULT FALSE NOT NULL,
    imagen VARCHAR(255) NULL
);

-- Cuenta
CREATE TABLE IF NOT EXISTS Cuenta (
    idCuenta INT PRIMARY KEY,
    idUsuario INT REFERENCES Usuario(idUsuario) NOT NULL,
    numeroCuenta VARCHAR(50) UNIQUE NOT NULL,
    saldo DECIMAL(10,2) NOT NULL
);

-- Pago
CREATE TABLE IF NOT EXISTS Pago (
    idPago INT PRIMARY KEY,
    titular VARCHAR(50) NOT NULL,
    numeroTarjeta VARCHAR(16) NOT NULL,
    idUsuario INT REFERENCES Usuario(idUsuario) NOT NULL
);

-- Marca
CREATE TABLE IF NOT EXISTS Marca (
    idMarca INT PRIMARY KEY,
    nombre VARCHAR(255) UNIQUE NOT NULL
);

-- Modelo
CREATE TABLE IF NOT EXISTS Modelo (
    idModelo INT PRIMARY KEY,
    nombre VARCHAR(255) UNIQUE NOT NULL,
    idMarca INT REFERENCES Marca(idMarca) NOT NULL
);

-- Transmision
CREATE TABLE IF NOT EXISTS Transmision (
    idTransmision INT PRIMARY KEY,
    nombre VARCHAR(255) UNIQUE NOT NULL
);

-- Categoria
CREATE TABLE IF NOT EXISTS Categoria (
    idCategoria INT PRIMARY KEY,
    nombre VARCHAR(255) UNIQUE NOT NULL,
    descripcion VARCHAR(255) NULL
);

-- Color
CREATE TABLE IF NOT EXISTS Color (
    idColor INT PRIMARY KEY,
    nombre VARCHAR(255) UNIQUE NOT NULL
);

-- Vehiculo
CREATE TABLE IF NOT EXISTS Vehiculo (
    idVehiculo INT PRIMARY KEY,
    descripcion VARCHAR(255) NOT NULL,
    imagen VARCHAR(255) NOT NULL,
    matricula VARCHAR(7) UNIQUE NOT NULL,
    kilometraje INT NOT NULL,
    anyoFabricacion INT NOT NULL,
    capacidadPasajeros INT NOT NULL,
    capacidadMaletero INT NOT NULL,
    numeroPuertas INT NOT NULL,
    numeroMarchas INT NOT NULL,
    aireAcondicionado BOOLEAN DEFAULT FALSE NOT NULL,
    enMantenimiento BOOLEAN DEFAULT FALSE NOT NULL,
    oferta INT NULL,
    precioPorDia DECIMAL(10,2) NOT NULL,
    precioPorMedioDia DECIMAL(10,2) NOT NULL,
    precioCombustible DECIMAL(10,2) NOT NULL,
    idCategoria INT REFERENCES Categoria(idCategoria),
    idMarca INT REFERENCES Marca(idMarca),
    idModelo INT REFERENCES Modelo(idModelo),
    idTransmision INT REFERENCES Transmision(idTransmision),
    idColor INT REFERENCES Color(idColor),
    idUsuario INT REFERENCES Usuario(idUsuario)
);

-- Comentario
CREATE TABLE IF NOT EXISTS Comentario (
    idComentario INT PRIMARY KEY,
    descripcion VARCHAR(255) NOT NULL,
    fechaCreacion DATE NOT NULL,
    idVehiculo INT REFERENCES Vehiculo(idVehiculo) NOT NULL,
    idUsuario INT REFERENCES Usuario(idUsuario) NOT NULL
);

-- Alquiler
CREATE TABLE IF NOT EXISTS Alquiler (
    idAlquiler INT PRIMARY KEY,
    fechaCreacion DATE NOT NULL,
    fechaEntrega DATE NOT NULL,
    fechaDevolucion DATE NOT NULL,
    precioFinal DECIMAL(10,2) NOT NULL,
    litrosCombustible DECIMAL(10,2) NULL,
    idVehiculo INT REFERENCES Vehiculo(idVehiculo) NOT NULL,
    idPago INT REFERENCES Pago(idPago) NOT NULL
);
