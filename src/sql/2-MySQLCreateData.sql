-- ----------------------------------------------------------------------------
-- Put here INSERT statements for inserting data required by the application
-- in the "pcv" database.
-------------------------------------------------------------------------------

INSERT INTO "User" (password,email,role) VALUES('$2a$04$UQ7I9PEstZF99Y2NjePjYuoE00g9s9mQmKOriS5vXya0yfAcfcLEG','usuario1@gmail.com',0);
INSERT INTO "User" (password,email,role) VALUES('$2a$04$UQ7I9PEstZF99Y2NjePjYuoE00g9s9mQmKOriS5vXya0yfAcfcLEG','usuario2@gmail.com',0);
INSERT INTO "User" (password,email,role) VALUES('$2a$04$UQ7I9PEstZF99Y2NjePjYuoE00g9s9mQmKOriS5vXya0yfAcfcLEG','usuario3@gmail.com',0);
INSERT INTO "User" (password,email,role) VALUES('$2a$04$UQ7I9PEstZF99Y2NjePjYuoE00g9s9mQmKOriS5vXya0yfAcfcLEG','admin@pcv.es',1);


INSERT INTO "VolunteerRecord" (name,surname,locality,phone,birth,"userId","isDeleted") VALUES('Pedro','Sánchez Sánchez','A Coruña','600111222','2000-04-08',1,false);
INSERT INTO "VolunteerRecord" (name,surname,locality,phone,birth,"userId","isDeleted") VALUES('Pepe','López Sánchez','A Coruña','600111223','1990-04-08',2,false);
INSERT INTO "VolunteerRecord" (name,surname,locality,phone,birth,"userId","isDeleted") VALUES('María','López Sánchez','A Coruña','600111223','1970-04-05',3,false);