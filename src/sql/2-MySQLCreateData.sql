-- ----------------------------------------------------------------------------
-- Put here INSERT statements for inserting data required by the application
-- in the "pcv" database.
-------------------------------------------------------------------------------
INSERT INTO "Entity" (name,"shortDescription",url,address,email,phone) VALUES ('Plataforma Coruñesa de Voluntariado', 'Somos una organización que aglutina a distintas asociaciones y llevamos x años realizando labores comunitarias en la provincia de A Coruña','pcv.es','Bajo Mirador los Puentes, A Coruña','support@pcv.es','999999999');

INSERT INTO "User" (password,email,role) VALUES('$2a$04$UQ7I9PEstZF99Y2NjePjYuoE00g9s9mQmKOriS5vXya0yfAcfcLEG','usuario1@gmail.com',0);
INSERT INTO "User" (password,email,role) VALUES('$2a$04$UQ7I9PEstZF99Y2NjePjYuoE00g9s9mQmKOriS5vXya0yfAcfcLEG','usuario2@gmail.com',0);
INSERT INTO "User" (password,email,role) VALUES('$2a$04$UQ7I9PEstZF99Y2NjePjYuoE00g9s9mQmKOriS5vXya0yfAcfcLEG','usuario3@gmail.com',0);
INSERT INTO "User" (password,email,role) VALUES('$2a$04$UQ7I9PEstZF99Y2NjePjYuoE00g9s9mQmKOriS5vXya0yfAcfcLEG','admin@pcv.es',1);
INSERT INTO "Representative" (id,phone,surname,"entityId") VALUES(4,null,'admin',1);


INSERT INTO "VolunteerRecord" (name,surname,locality,phone,birth,"userId","isDeleted") VALUES('Pedro','Sánchez Sánchez','A Coruña','600111222','2000-04-08',1,false);
INSERT INTO "VolunteerRecord" (name,surname,locality,phone,birth,"userId","isDeleted") VALUES('Pepe','López Sánchez','A Coruña','600111223','1990-04-08',2,false);
INSERT INTO "VolunteerRecord" (name,surname,locality,phone,birth,"userId","isDeleted") VALUES('María','López Sánchez','A Coruña','600111223','1970-04-05',3,false);



INSERT INTO "Ods" (name, number, description, url)
VALUES ('Fin de la pobreza', 1, 'Erradicar la pobreza en todas sus formas y dimensiones es un imperativo ético, y constituye la base de la igualdad de derechos, oportunidades y de justicia social. La pobreza tiene numerosas dimensiones, y la lucha contra ella debe abarcar medidas que permitan a las personas satisfacer sus necesidades básicas, como el acceso a servicios de salud y educación, empleo digno y protección social.', 'https://www.un.org/sustainabledevelopment/es/poverty/');
INSERT INTO "Ods" (name, number, description, url)
VALUES ('Hambre cero', 2, 'El hambre es la peor forma de malnutrición y una grave violación del derecho al alimento. Los esfuerzos para combatir el hambre se han centrado en aumentar la producción agrícola, pero la malnutrición es causada por más factores que la falta de acceso a alimentos. La atención debe centrarse también en la mejora de la calidad de la alimentación y en garantizar el acceso a agua potable, saneamiento y servicios de atención médica adecuados.', 'https://www.un.org/sustainabledevelopment/es/zero-hunger/');
INSERT INTO "Ods" (name, number, description, url)
VALUES ('Salud y bienestar', 3, 'La salud es un derecho humano fundamental y una condición necesaria para el desarrollo sostenible. La atención médica debe estar disponible y ser asequible para todos, independientemente de su género, edad, origen étnico o condición social. La inversión en la prevención y el tratamiento de enfermedades es crucial para el bienestar de las personas y las sociedades.', 'https://www.un.org/sustainabledevelopment/es/good-health/');
INSERT INTO "Ods" (name, number, description, url)
VALUES ('Educación de calidad', 4, 'La educación es un derecho humano fundamental y esencial para el desarrollo sostenible. La educación de calidad es un requisito previo para el aprendizaje permanente y la capacitación, y es un medio para reducir la desigualdad y la pobreza. La educación debe ser inclusiva y equitativa, y debe promover la adquisición de conocimientos y habilidades que permitan a las personas participar plenamente en la vida social y económica.', 'https://www.un.org/sustainabledevelopment/es/quality-education/');
INSERT INTO "Ods" (name, number, description, url)
VALUES ('Igualdad de género', 5, 'La igualdad de género es un derecho humano fundamental y un imperativo para el desarrollo sostenible. La discriminación de género afecta a millones de mujeres y niñas en todo el mundo, limitando sus oportunidades en la educación, el empleo y la participación política. La promoción de la igualdad de género y el empoderamiento de las mujeres son esenciales para la construcción de sociedades más justas y equitativas.', 'https://www.un.org/sustainabledevelopment/es/gender-equality/');
INSERT INTO "Ods" (name, number,description, url) VALUES ('Agua limpia y saneamiento', 6, 'Garantizar la disponibilidad y la gestión sostenible del agua y el saneamiento para todos', 'https://www.un.org/sustainabledevelopment/es/water-sanitation/');
INSERT INTO "Ods" (name, number, description, url) VALUES ('Energía asequible y no contaminante', 7, 'Garantizar el acceso a una energía asequible, segura, sostenible y moderna para todos', 'https://www.un.org/sustainabledevelopment/es/energy/');
INSERT INTO "Ods" (name, number, description, url) VALUES ('Trabajo decente y crecimiento económico', 8, 'Fomentar el crecimiento económico sostenido, inclusivo y sostenible, el empleo pleno y productivo y el trabajo decente para todos', 'https://www.un.org/sustainabledevelopment/es/economic-growth/');
INSERT INTO "Ods" (name, number, description, url) VALUES ('Industria, innovación e infraestructura', 9, 'Construir infraestructuras resilientes, promover la industrialización sostenible y fomentar la innovación', 'https://www.un.org/sustainabledevelopment/es/infrastructure-industrialization/');
INSERT INTO "Ods" (name, number, description, url) VALUES ('Reducción de las desigualdades', 10, 'Reducir la desigualdad económica, social y territorial', 'https://www.un.org/sustainabledevelopment/es/inequality/');
INSERT INTO "Ods" (name, number, description, url) VALUES ('Ciudades y comunidades sostenibles', 11, 'Lograr que las ciudades y los asentamientos humanos sean inclusivos, seguros, resilientes y sostenibles', 'https://www.un.org/sustainabledevelopment/es/cities/');
INSERT INTO "Ods" (name, number, description, url) VALUES ('Producción y consumo responsables', 12, 'Garantizar modalidades de consumo y producción sostenibles', 'https://www.un.org/sustainabledevelopment/es/sustainable-consumption-production/');
INSERT INTO "Ods" (name, number, description, url) VALUES ('Acción por el clima', 13, 'Adoptar medidas urgentes para combatir el cambio climático y sus efectos', 'https://www.un.org/sustainabledevelopment/es/climate-change/');
INSERT INTO "Ods" (name, number, description, url) VALUES ('Vida submarina', 14, 'Conservar y utilizar en forma sostenible los océanos, los mares y los recursos marinos para el desarrollo sostenible', 'https://www.un.org/sustainabledevelopment/es/oceans/');
INSERT INTO "Ods" (name,number, description,url) VALUES ('Vida de ecosistemas terrestres', 15, 'Conservar y restaurar los ecosistemas terrestres y promover su uso sostenible es esencial para combatir el cambio climático, asegurar la supervivencia de muchas especies y mantener la calidad del aire y del agua.', 'https://www.un.org/sustainabledevelopment/es/life-on-land/');
INSERT INTO "Ods" (name,number, description,url) VALUES ('Paz, justicia e instituciones sólidas', 16, 'Fortalecer las instituciones para promover sociedades pacíficas e inclusivas y proporcionar acceso a la justicia para todos.', 'https://www.un.org/sustainabledevelopment/es/peace-justice-and-strong-institutions/');
INSERT INTO "Ods" (name,number, description,url) VALUES ('Alianzas para lograr los objetivos', 17, 'Fortalecer las alianzas entre gobiernos, sociedad civil, sector privado y organizaciones internacionales para lograr los objetivos de desarrollo sostenible.', 'https://www.un.org/sustainabledevelopment/es/global-partnerships/');

INSERT INTO "CollaborationArea" (name) VALUES ('Educación');
INSERT INTO "CollaborationArea" (name) VALUES ('Salud');
INSERT INTO "CollaborationArea" (name) VALUES ('Medio ambiente');
INSERT INTO "CollaborationArea" (name) VALUES ('Derechos humanos');
INSERT INTO "CollaborationArea" (name) VALUES ('Desarrollo económico');
INSERT INTO "CollaborationArea" (name) VALUES ('Emprendimiento');
INSERT INTO "CollaborationArea" (name) VALUES ('Tecnología');
INSERT INTO "CollaborationArea" (name) VALUES ('Arte y cultura');
INSERT INTO "CollaborationArea" (name) VALUES ('Deporte');
INSERT INTO "CollaborationArea" (name) VALUES ('Alimentación y nutrición');
INSERT INTO "CollaborationArea" (name) VALUES ('Agricultura sostenible');
INSERT INTO "CollaborationArea" (name) VALUES ('Energía y recursos naturales');
INSERT INTO "CollaborationArea" (name) VALUES ('Igualdad de género');
INSERT INTO "CollaborationArea" (name) VALUES ('Comunicación y medios de información');
INSERT INTO "CollaborationArea" (name) VALUES ('Idiomas y traducción');
INSERT INTO "CollaborationArea" (name) VALUES ('Desastres naturales y emergencias');
INSERT INTO "CollaborationArea" (name) VALUES ('Migración y refugio');
INSERT INTO "CollaborationArea" (name) VALUES ('Infancia y juventud');
INSERT INTO "CollaborationArea" (name) VALUES ('Apoyo a personas mayores');
INSERT INTO "CollaborationArea" (name) VALUES ('Apoyo a personas con discapacidad');