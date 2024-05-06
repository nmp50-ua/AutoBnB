-- Seeder para la tabla Usuario
INSERT INTO Usuario (id, nombre, apellidos, email, password, telefono, direccion, ciudad, codigoPostal, dni, fechaCaducidadDni, fechaCarnetConducir, administrador, esArrendador, esArrendatario, imagen) VALUES
(1, 'Noel', 'Martínez', 'noel@gmail.com', 'noel2002', 123456789, 'Calle Falsa 123', 'Madrid', 28080, '12345678Z', '2025-12-31', '2025-12-31', FALSE, FALSE, TRUE, 'cara.jpg'),
(2, 'Ana', 'García', 'ana.garcia@example.com', 'password123', 987654321, 'Avenida de la Paz 456', 'Barcelona', 08080, '87654321X', '2026-05-30', '2026-05-30', FALSE, FALSE, TRUE, 'cara.jpg'),
(3, 'admin', 'admin', 'admin@gmail.com', 'admin00', 112233445, 'Paseo de la Victoria 789', 'Sevilla', 41010, '98765432Q', '2027-09-15', '2027-09-15', TRUE, FALSE, TRUE, 'cara.jpg'),
(4, 'Carlos', 'Ruiz', 'carlos.ruiz@example.com', 'pass7890', 123450001, 'Calle Real 1', 'Zaragoza', 50001, 'A1234567A', '2028-01-01', '2023-01-01', FALSE, TRUE, FALSE, 'cara.jpg'),
(5, 'Diana', 'Gomez', 'diana.gomez@example.com', 'pass7891', 123450002, 'Avenida Gran Via 2', 'Bilbao', 48001, 'B2345678B', '2028-02-02', '2023-02-02', FALSE, FALSE, TRUE, 'cara.jpg'),
(6, 'Elena', 'Nito', 'elena.nito@example.com', 'pass7892', 123450003, 'Plaza Mayor 3', 'Granada', 18001, 'C3456789C', '2028-03-03', '2023-03-03', FALSE, TRUE, TRUE, 'cara.jpg'),
(7, 'Fernando', 'Sanz', 'fernando.sanz@example.com', 'pass7893', 123450004, 'Calle Nueva 4', 'Salamanca', 37001, 'D4567890D', '2028-04-04', '2023-04-04', FALSE, FALSE, TRUE, 'cara.jpg'),
(8, 'Gloria', 'Perez', 'gloria.perez@example.com', 'pass7894', 123450005, 'Ronda Sur 5', 'Alicante', 03001, 'E5678901E', '2028-05-05', '2023-05-05', FALSE, FALSE, FALSE, 'cara.jpg'),
(9, 'Hector', 'Lopez', 'hector.lopez@example.com', 'pass7895', 123450006, 'Camino Largo 6', 'Valladolid', 47001, 'F6789012F', '2028-06-06', '2023-06-06', FALSE, TRUE, FALSE, 'cara.jpg'),
(10, 'Irene', 'Martinez', 'irene.martinez@example.com', 'pass7896', 123450007, 'Paseo de la Castellana 7', 'Santander', 39001, 'G7890123G', '2028-07-07', '2023-07-07', FALSE, FALSE, TRUE, 'cara.jpg'),
(11, 'Jorge', 'Navarro', 'jorge.navarro@example.com', 'pass7897', 123450008, 'Callejón de los Milagros 8', 'Oviedo', 33001, 'H8901234H', '2028-08-08', '2023-08-08', FALSE, TRUE, TRUE, 'cara.jpg'),
(12, 'Karen', 'Jimenez', 'karen.jimenez@example.com', 'pass7898', 123450009, 'Avenida de América 9', 'Gijón', 33201, 'I9012345I', '2028-09-09', '2023-09-09', FALSE, FALSE, FALSE, 'cara.jpg'),
(13, 'Luis', 'Ortega', 'luis.ortega@example.com', 'pass7899', 123450010, 'Plaza del Sol 10', 'Tarragona', 43001, 'J0123456J', '2028-10-10', '2023-10-10', FALSE, FALSE, TRUE, 'cara.jpg');

-- Seeder para la tabla Cuenta
INSERT INTO Cuenta (id, idUsuario, numeroCuenta, saldo) VALUES
(1, 1, 'ES9801234567890123456789', 1000.00),
(2, 2, 'ES9809876543210987654321', 1500.00),
(3, 3, 'ES9812345678901234567890', 2000.00),
(4, 4, 'ES4444444444444444444444', 2000.00),
(5, 5, 'ES5555555555555555555555', 2500.00),
(6, 6, 'ES6666666666666666666666', 3000.00),
(7, 7, 'ES7777777777777777777777', 3500.00),
(8, 8, 'ES8888888888888888888888', 4000.00),
(9, 9, 'ES9999999999999999999999', 4500.00),
(10, 10, 'ES1010101010101010101010', 5000.00),
(11, 11, 'ES1111111111111111111111', 5500.00),
(12, 12, 'ES1212121212121212121212', 6000.00),
(13, 13, 'ES1313131313131313131313', 6500.00);

-- Seeder para la tabla Pago
INSERT INTO Pago (id, titular, numeroTarjeta, idUsuario) VALUES
(1, 'Noel Martínez Pomares', '1111222233334444', 1),
(2, 'Ana García', '4444333322221111', 2),
(3, 'Carlos Ruiz', '5000500050005000', 4),
(4, 'Diana Gomez', '5100510051005100', 5),
(5, 'Elena Nito', '5200520052005200', 6),
(6, 'Fernando Sanz', '5300530053005300', 7),
(7, 'Gloria Perez', '5400540054005400', 8),
(8, 'Hector Lopez', '5500550055005500', 9),
(9, 'Irene Martinez', '5600560056005600', 10),
(10, 'Jorge Navarro', '5700570057005700', 11),
(11, 'Karen Jimenez', '5800580058005800', 12),
(12, 'Luis Ortega', '5900590059005900', 13);

-- Seeder para la tabla Marca
INSERT INTO Marca (id, nombre) VALUES
                                   (1, 'Toyota'), (2, 'Ford'), (3, 'Nissan'), (4, 'Chevrolet'), (5, 'Volkswagen'), (6, 'Honda'), (7, 'BMW'), (8, 'Audi'), (9, 'Hyundai'), (10, 'Kia'),
                                   (11, 'Fiat'), (12, 'Peugeot'), (13, 'Tesla'), (14, 'Abarth'), (15, 'Alfa Romeo'), (16, 'Alpine'), (17, 'Aston Martin'), (18, 'Bentley'), (19, 'Bugatti'), (20, 'BYD'),
                                   (21, 'Buick'), (22, 'Cadillac'), (23, 'Caterham'), (24, 'Chrysler'), (25, 'Citroen'), (26, 'Cupra'), (27, 'Dacia'), (28, 'Dodge'), (29, 'DS'), (30, 'Ferrari'),
                                   (31, 'Fisker'), (32, 'Genesis'), (33, 'GMC'), (34, 'Hummer'), (35, 'Infiniti'), (36, 'IONIQ'), (37, 'Jaguar'), (38, 'Jeep'), (39, 'Koenigsegg'), (40, 'Lamborghini'),
                                   (41, 'Lancia'), (42, 'Land Rover'), (43, 'Lexus'), (44, 'Lincoln'), (45, 'Lotus'), (46, 'Lucid'), (47, 'Lynk&Co'), (48, 'Mahindra'), (49, 'Maserati'), (50, 'Maybach'),
                                   (51, 'Mazda'), (52, 'McLaren'), (53, 'Mercedes-Benz'), (54, 'MG'), (55, 'Mercury'), (56, 'Mini'), (57, 'Mitsubishi'), (58, 'Morgan'), (59, 'Mustang'), (60, 'Omoda'),
                                   (61, 'Oldsmobile'), (62, 'Opel'), (63, 'Pagani'), (64, 'Plymouth'), (65, 'Pontiac'), (66, 'Porsche'), (67, 'Ram'), (68, 'Renault'), (69, 'Rolls-Royce'), (70, 'Saab'),
                                   (71, 'Saturn'), (72, 'Scion'), (73, 'Seat'), (74, 'Skoda'), (75, 'Smart'), (76, 'Ssangyong'), (77, 'Spyker'), (78, 'Subaru'), (79, 'Suzuki'), (80, 'Tata'), (81, 'Volvo');

-- Seeder para la tabla Modelo
INSERT INTO Modelo (id, nombre, idMarca) VALUES
(1, 'Corolla', 1),
(2, 'Focus', 2),
(3, 'Altima', 3),
(4, 'Camaro', 4),
(5, 'Golf', 5),
(6, 'Civic', 6),
(7, 'Serie 3', 7),
(8, 'A4', 8),
(9, 'Tucson', 9),
(10, 'Sorento', 10),
(11, '500', 11),
(12, '208', 12),
(13, 'RAV4', 1),
(14, 'Prius', 1),
(15, 'Camry', 1),
(16, 'Tacoma', 1),
(17, 'Highlander', 1),
(18, 'Mustang', 2),
(19, 'Explorer', 2),
(20, 'Fiesta', 2),
(21, 'F-150', 2),
(22, 'Escape', 2),
(23, 'Sentra', 3),
(24, 'Maxima', 3),
(25, 'Rogue', 3),
(26, 'Murano', 3),
(27, 'Leaf', 3),
(28, 'Impala', 4),
(29, 'Malibu', 4),
(30, 'Equinox', 4),
(31, 'Tahoe', 4),
(32, 'Silverado', 4),
(33, 'Polo', 5),
(34, 'Passat', 5),
(35, 'Tiguan', 5),
(36, 'Jetta', 5),
(37, 'Beetle', 5),
(38, 'Accord', 6),
(39, 'Fit', 6),
(40, 'CR-V', 6),
(41, 'Pilot', 6),
(42, 'Odyssey', 6),
(43, 'Serie 1', 7),
(44, 'Serie 5', 7),
(45, 'X3', 7),
(46, 'X5', 7),
(47, 'M4', 7),
(48, 'Q5', 8),
(49, 'Q7', 8),
(50, 'A6', 8),
(51, 'TT', 8),
(52, 'R8', 8),
(53, 'Elantra', 9),
(54, 'Sonata', 9),
(55, 'Santa Fe', 9),
(56, 'Veloster', 9),
(57, 'Kona', 9),
(58, 'Optima', 10),
(59, 'Rio', 10),
(60, 'Sportage', 10),
(61, 'Telluride', 10),
(62, 'Forte', 10),
(63, 'Punto', 11),
(64, 'Panda', 11),
(65, 'Tipo', 11),
(66, 'Ducato', 11),
(67, 'Freemont', 11),
(68, '306', 12),
(69, '406', 12),
(70, '508', 12),
(71, '2008', 12),
(72, '3008', 12);

-- Seeder para la tabla Transmision
INSERT INTO Transmision (id, nombre) VALUES
(1, 'Manual'),
(2, 'Automática'),
(3, 'Semi-Automática'),
(4, 'CVT'),
(5, 'Doble Embrague'),
(6, 'Manual Secuencial'),
(7, 'Automática 8 velocidades'),
(8, 'Automática 9 velocidades'),
(9, 'Automática 10 velocidades'),
(10, 'Híbrida'),
(11, 'Eléctrica'),
(12, 'Manual 7 velocidades');

-- Seeder para la tabla Categoria
INSERT INTO Categoria (id, nombre, descripcion) VALUES
(1, 'Económico', 'Vehículos económicos y eficientes en combustible'),
(2, 'SUV', 'Vehículos deportivos utilitarios para mayor espacio y comodidad'),
(3, 'Deportivo', 'Vehículos de alto rendimiento y diseño deportivo'),
(4, 'Lujo', 'Vehículos de alta gama con acabados premium'),
(5, 'Compacto', 'Vehículos pequeños ideales para la ciudad'),
(6, 'Familiar', 'Vehículos espaciosos orientados a la comodidad familiar'),
(7, 'Pickup', 'Vehículos utilitarios con espacio de carga'),
(8, 'Minivan', 'Vehículos con capacidad extendida de pasajeros'),
(9, 'Coupé', 'Vehículos con dos puertas y diseño elegante'),
(10, 'Convertible', 'Vehículos con techo plegable o desmontable'),
(11, 'Eléctrico', 'Vehículos alimentados exclusivamente por baterías eléctricas'),
(12, 'Híbrido', 'Vehículos que combinan motor eléctrico con motor de combustión');

-- Seeder para la tabla Color
INSERT INTO Color (id, nombre) VALUES
(1, 'Rojo'),
(2, 'Azul'),
(3, 'Verde'),
(4, 'Negro'),
(5, 'Blanco'),
(6, 'Gris'),
(7, 'Amarillo'),
(8, 'Naranja'),
(9, 'Violeta'),
(10, 'Cian'),
(11, 'Magenta'),
(12, 'Dorado');

-- Seeder para la tabla Vehiculo
INSERT INTO Vehiculo (id, descripcion, imagen, matricula, kilometraje, anyoFabricacion, capacidadPasajeros, capacidadMaletero, numeroPuertas, numeroMarchas, aireAcondicionado, enMantenimiento, oferta, precioPorDia, precioPorMedioDia, precioCombustible, idCategoria, idMarca, idModelo, idTransmision, idColor, idUsuario) VALUES
(1, 'Un vehículo compacto y eficiente', 'coche.jpg', '1234ABC', 50000, 2018, 5, 300, 5, 6, TRUE, FALSE, NULL, 30.00, 20.00, 1.50, 1, 1, 1, 1, 1, 2),
(2, 'Un SUV espacioso y cómodo', 'coche.jpg', '5678DEF', 75000, 2017, 7, 500, 5, 5, TRUE, FALSE, 10, 60.00, 40.00, 2.00, 2, 2, 2, 2, 2, 1),
(3, 'Deportivo eléctrico con autonomía superior', 'coche.jpg', 'M1234XY', 10000, 2021, 4, 150, 2, 1, TRUE, FALSE, NULL, 100.00, 70.00, 0.00, 9, 7, 7, 9, 9, 4),
(4, 'SUV híbrido para toda la familia', 'coche.jpg', 'M5678YZ', 30000, 2019, 7, 550, 5, 1, TRUE, FALSE, 5, 80.00, 55.00, 1.20, 10, 9, 9, 10, 10, 5),
(5, 'Pickup robusta para trabajo pesado', 'coche.jpg', 'M9012AB', 50000, 2018, 5, 1000, 4, 6, TRUE, FALSE, NULL, 90.00, 60.00, 1.80, 5, 10, 10, 7, 1, 6),
(6, 'Compacto económico para la ciudad', 'coche.jpg', 'M3456BC', 15000, 2020, 5, 200, 4, 5, TRUE, FALSE, 10, 50.00, 35.00, 1.40, 3, 3, 3, 4, 2, 7),
(7, 'Familiar espacioso y confortable', 'coche.jpg', 'M7890CD', 40000, 2017, 6, 600, 4, 5, TRUE, TRUE, NULL, 70.00, 50.00, 1.60, 4, 4, 4, 5, 3, 8),
(8, 'Coupé elegante y rápido', 'coche.jpg', 'M2345DE', 20000, 2019, 4, 300, 2, 7, TRUE, FALSE, 15, 95.00, 65.00, 1.70, 1, 5, 5, 6, 4, 9),
(9, 'Convertible ideal para el verano', 'coche.jpg', 'M6789EF', 25000, 2018, 4, 250, 2, 6, TRUE, FALSE, 20, 85.00, 60.00, 1.50, 8, 6, 6, 8, 5, 10),
(10, 'Minivan perfecta para viajes largos', 'coche.jpg', 'M1122FG', 60000, 2016, 8, 700, 5, 4, TRUE, TRUE, NULL, 75.00, 55.00, 1.90, 6, 8, 8, 3, 6, 4),
(11, 'Lujo y rendimiento en un solo vehículo', 'coche.jpg', 'M3344GH', 12000, 2022, 5, 400, 4, 8, TRUE, FALSE, 25, 110.00, 80.00, 1.30, 2, 11, 11, 2, 7, 5),
(12, 'Eléctrico compacto y eficiente', 'coche.jpg', 'M5566HI', 9000, 2023, 4, 150, 4, 1, TRUE, FALSE, 10, 60.00, 40.00, 0.00, 9, 12, 12, 9, 8, 6);

-- Seeder para la tabla Comentario
INSERT INTO Comentario (id, descripcion, fechaCreacion, idVehiculo, idUsuario) VALUES
(1, 'Gran vehículo, cómodo para la familia', '2023-03-20', 2, 2),
(2, 'Excelente para viajes largos, muy económico en combustible', '2023-03-21', 1, 1),
(3, 'Increíble rendimiento y muy silencioso', '2024-01-02', 3, 1),
(4, 'Perfecto para la familia, muy espacioso', '2024-01-03', 4, 2),
(5, 'Gran capacidad de carga y muy resistente', '2024-01-04', 5, 3),
(6, 'Ideal para moverse por la ciudad y aparcar en cualquier lado', '2024-01-05', 6, 4),
(7, 'Cómodo para viajes largos, los niños tienen espacio de sobra', '2024-01-06', 7, 5),
(8, 'Diseño espectacular y aceleración impresionante', '2024-01-07', 8, 6),
(9, 'La sensación de conducirlo con el techo bajado es inigualable', '2024-01-08', 9, 7),
(10, 'Nos encantó en nuestro viaje por carretera, muy fiable', '2024-01-09', 10, 8),
(11, 'Un coche que gira cabezas, y es un placer conducir', '2024-01-10', 11, 9),
(12, 'Muy económico en términos de consumo eléctrico, y cómodo', '2024-01-11', 12, 10);

-- Seeder para la tabla Alquiler
INSERT INTO Alquiler (id, fechaCreacion, fechaEntrega, fechaDevolucion, precioFinal, litrosCombustible, idVehiculo, idPago) VALUES
(1, '2023-03-25', '2023-03-26', '2023-03-27', 60.00, 5, 1, 1),
(2, '2023-04-01', '2023-04-02', '2023-04-03', 120.00, 10, 2, 2),
(3, '2024-02-01', '2024-02-03', '2024-02-04', 200.00, NULL, 3, 3),
(4, '2024-02-05', '2024-02-07', '2024-02-08', 160.00, NULL, 4, 4),
(5, '2024-02-09', '2024-02-11', '2024-02-12', 180.00, 10, 5, 5),
(6, '2024-02-13', '2024-02-15', '2024-02-16', 100.00, 5, 6, 6),
(7, '2024-02-17', '2024-02-19', '2024-02-20', 140.00, 8, 7, 7),
(8, '2024-02-21', '2024-02-23', '2024-02-24', 190.00, 6, 8, 8),
(9, '2024-02-25', '2024-02-27', '2024-02-28', 170.00, 4, 9, 9),
(10, '2024-03-01', '2024-03-03', '2024-03-04', 150.00, 7, 10, 10),
(11, '2024-03-05', '2024-03-07', '2024-03-08', 220.00, NULL, 11, 11),
(12, '2024-03-09', '2024-03-11', '2024-03-12', 120.00, NULL, 12, 12);

-- Ajuste de secuencias después de insertar datos iniciales
SELECT setval('usuario_id_seq', COALESCE((SELECT MAX(id)+1 FROM usuario), 1), false);
SELECT setval('cuenta_id_seq', COALESCE((SELECT MAX(id)+1 FROM cuenta), 1), false);
SELECT setval('pago_id_seq', COALESCE((SELECT MAX(id)+1 FROM pago), 1), false);
SELECT setval('marca_id_seq', COALESCE((SELECT MAX(id)+1 FROM marca), 1), false);
SELECT setval('modelo_id_seq', COALESCE((SELECT MAX(id)+1 FROM modelo), 1), false);
SELECT setval('transmision_id_seq', COALESCE((SELECT MAX(id)+1 FROM transmision), 1), false);
SELECT setval('categoria_id_seq', COALESCE((SELECT MAX(id)+1 FROM categoria), 1), false);
SELECT setval('color_id_seq', COALESCE((SELECT MAX(id)+1 FROM color), 1), false);
SELECT setval('vehiculo_id_seq', COALESCE((SELECT MAX(id)+1 FROM vehiculo), 1), false);
SELECT setval('comentario_id_seq', COALESCE((SELECT MAX(id)+1 FROM comentario), 1), false);
SELECT setval('alquiler_id_seq', COALESCE((SELECT MAX(id)+1 FROM alquiler), 1), false);
SELECT setval('mensaje_id_seq', COALESCE((SELECT MAX(id)+1 FROM mensaje), 1), false);
