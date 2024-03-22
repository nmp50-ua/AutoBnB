-- Seeder para la tabla Usuario
INSERT INTO Usuario (idUsuario, nombre, apellidos, email, password, telefono, direccion, ciudad, codigoPostal, dni, fechaCaducidadDni, fechaCarnetConducir, administrador, esArrendador, esArrendatario, imagen) VALUES
(1, 'Noel', 'Martínez', 'noel@gmail.com', 'noel2002', 123456789, 'Calle Falsa 123', 'Madrid', 28080, '12345678Z', '2025-12-31', '2025-12-31', FALSE, FALSE, FALSE, NULL),
(2, 'Ana', 'García', 'ana.garcia@example.com', 'password123', 987654321, 'Avenida de la Paz 456', 'Barcelona', 08080, '87654321X', '2026-05-30', '2026-05-30', FALSE, FALSE, FALSE, NULL),
(3, 'admin', 'admin', 'admin@gmail.com', 'admin00', 112233445, 'Paseo de la Victoria 789', 'Sevilla', 41010, '98765432Q', '2027-09-15', '2027-09-15', TRUE, FALSE, FALSE, NULL);

-- Seeder para la tabla Cuenta
INSERT INTO Cuenta (idCuenta, idUsuario, numeroCuenta, saldo) VALUES
(1, 1, 'ES9801234567890123456789', 1000.00),
(2, 2, 'ES9809876543210987654321', 1500.00),
(3, 3, 'ES9812345678901234567890', 2000.00);

-- Seeder para la tabla Pago
INSERT INTO Pago (idPago, titular, numeroTarjeta, idUsuario) VALUES
(1, 'Noel Martínez Pomares', '1111222233334444', 1),
(2, 'Ana García', '4444333322221111', 2);

-- Seeder para la tabla Marca
INSERT INTO Marca (idMarca, nombre) VALUES
(1, 'Toyota'),
(2, 'Ford');

-- Seeder para la tabla Modelo
INSERT INTO Modelo (idModelo, nombre, idMarca) VALUES
(1, 'Corolla', 1),
(2, 'Focus', 2);

-- Seeder para la tabla Transmision
INSERT INTO Transmision (idTransmision, nombre) VALUES
(1, 'Manual'),
(2, 'Automática');

-- Seeder para la tabla Categoria
INSERT INTO Categoria (idCategoria, nombre, descripcion) VALUES
(1, 'Económico', 'Vehículos económicos y eficientes en combustible'),
(2, 'SUV', 'Vehículos deportivos utilitarios para mayor espacio y comodidad');

-- Seeder para la tabla Color
INSERT INTO Color (idColor, nombre) VALUES
(1, 'Rojo'),
(2, 'Azul');

-- Seeder para la tabla Vehiculo
INSERT INTO Vehiculo (idVehiculo, descripcion, imagen, matricula, kilometraje, anyoFabricacion, capacidadPasajeros, capacidadMaletero, numeroPuertas, numeroMarchas, aireAcondicionado, enMantenimiento, oferta, precioPorDia, precioPorMedioDia, precioCombustible, idCategoria, idMarca, idModelo, idTransmision, idColor, idUsuario) VALUES
(1, 'Un vehículo compacto y eficiente', 'imagen1.jpg', '1234ABC', 50000, 2018, 5, 300, 5, 6, TRUE, FALSE, NULL, 30.00, 20.00, 1.50, 1, 1, 1, 1, 1, 2),
(2, 'Un SUV espacioso y cómodo', 'imagen2.jpg', '5678DEF', 75000, 2017, 7, 500, 5, 5, TRUE, FALSE, 10, 60.00, 40.00, 2.00, 2, 2, 2, 2, 2, 1);

-- Seeder para la tabla Comentario
INSERT INTO Comentario (idComentario, descripcion, fechaCreacion, idVehiculo, idUsuario) VALUES
(1, 'Gran vehículo, cómodo para la familia', '2023-03-20', 2, 2),
(2, 'Excelente para viajes largos, muy económico en combustible', '2023-03-21', 1, 1);

-- Seeder para la tabla Alquiler
INSERT INTO Alquiler (idAlquiler, fechaCreacion, fechaEntrega, fechaDevolucion, precioFinal, litrosCombustible, idVehiculo, idPago) VALUES
(1, '2023-03-25', '2023-03-26', '2023-03-27', 60.00, 5, 1, 1),
(2, '2023-04-01', '2023-04-02', '2023-04-03', 120.00, 10, 2, 2);
