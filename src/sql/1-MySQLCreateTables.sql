DROP TABLE IF EXISTS "VolunteerRecord";
DROP TABLE IF EXISTS "Representative";
DROP TABLE IF EXISTS "Entity";
DROP TABLE IF EXISTS "User";
DROP TABLE IF EXISTS "File";
DROP TYPE IF EXISTS file_type;

CREATE TYPE file_type AS ENUM (
  'dni',
  'harassementCert',
  'photo',
  'formationCompleted'
  'agreementFileSignedByEntity',
  'agreementFileSignedByBoth',
  'agreementFile',
  'logo'
);

CREATE TABLE "File"(
   "id" uuid NOT NULL PRIMARY KEY,
   "date" timestamp without time zone NOT NULL,
   "originalName" varchar(255) NOT NULL,
   "filetype" varchar(31) NOT NULL,
   "extension" varchar(7) NOT NULL
);
CREATE TABLE "Entity"(
    "id" BIGSERIAL PRIMARY KEY,
    "name" VARCHAR(123) NOT NULL,
    "shortDescription" VARCHAR(255) NOT NULL,
    "url" VARCHAR(63),
    "address" VARCHAR(60),
    "email" VARCHAR(60),
    "phone" VARCHAR(60),
    "certFileId" uuid,
    "logoId" uuid,
    CONSTRAINT "EntityFileCertForeignKey" FOREIGN KEY ("certFileId") REFERENCES "File" ("id"),
    CONSTRAINT "EntityFileLogoForeignKey" FOREIGN KEY ("logoId") REFERENCES "File" ("id")
);

CREATE TABLE "User" (
    "id" BIGSERIAL PRIMARY KEY,
    "password" VARCHAR(60) NOT NULL,
    "email" VARCHAR(60) NOT NULL,
    "role" SMALLINT NOT NULL,
    CONSTRAINT "UserEmailUniqueKey" UNIQUE ("email")
);
CREATE INDEX "UserIndexByEmail" ON "User" ("email");

CREATE TABLE "VolunteerRecord" (
    "id" BIGSERIAL PRIMARY KEY,
    "name" VARCHAR(60) NOT NULL,
    "surname" VARCHAR(60) NOT NULL,
    "dni" VARCHAR(15) COLLATE "C",
    "dniExpiration" DATE,
    "locality" VARCHAR(60) NOT NULL,
    "phone" VARCHAR(15) NOT NULL,
    "birth" DATE NOT NULL,
    "userId" BIGINT REFERENCES "User" (id),
    "isDeleted" BOOLEAN NOT NULL,
    CONSTRAINT "VolunteerRecordDniUniqueKey" UNIQUE ("dni")
);

CREATE INDEX "VolunteerRecordIndexByDni" ON "VolunteerRecord" ("dni") WHERE "dni" IS NOT NULL;
CREATE INDEX "VolunteerRecordIndexByUserId" ON "VolunteerRecord" ("userId") WHERE "dni" IS NULL;

CREATE TABLE "Representative" (
    "phone" VARCHAR(20) NOT NULL,
    "name" VARCHAR(60),
    "surname" VARCHAR(60),
    "id" BIGINT NOT NULL PRIMARY KEY,
    "entityId" BIGINT NOT NULL,
    CONSTRAINT "RepresentativeEntityForeignKey" FOREIGN KEY("entityId") REFERENCES "Entity" (id),
    CONSTRAINT "RepresentativeUserForeignKey" FOREIGN KEY ("id") REFERENCES "User" ("id")
);
