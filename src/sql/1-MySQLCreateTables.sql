DROP TABLE IF EXISTS "Participation";
DROP TABLE IF EXISTS "VolunteerRecord";
DROP TABLE IF EXISTS "Representative";
DROP TABLE IF EXISTS "Project_Ods";
DROP TABLE IF EXISTS "Task";
DROP TABLE IF EXISTS "Project";
DROP TABLE IF EXISTS "CollaborationArea";
DROP TABLE IF EXISTS "Entity";
DROP TABLE IF EXISTS "User";
DROP TABLE IF EXISTS "Ods";
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
CREATE TABLE "CollaborationArea" (
    "id" BIGSERIAL PRIMARY KEY,
    "name" VARCHAR(63) NOT NULL
);
CREATE TABLE "Project" (
    "id" BIGSERIAL PRIMARY KEY,
    "name" VARCHAR(63) NOT NULL,
    "shortDescription" VARCHAR(127) NOT NULL,
    "longDescription" VARCHAR(500),
    "locality" VARCHAR(60) NOT NULL,
    "schedule" VARCHAR(60) NOT NULL,
    "capacity" INTEGER NOT NULL,
    "preferableVolunteer" VARCHAR(63) NOT NULL,
    "completenessDate" DATE,
    "areChildren" BOOLEAN NOT NULL,
    "isPaused" BOOLEAN NOT NULL,
    "isVisible" BOOLEAN NOT NULL,
    "isDeleted" BOOLEAN NOT NULL,
    "entityId" BIGINT NOT NULL,
    "collaborationAreaId" BIGINT NOT NULL,
    CONSTRAINT "ProjectEntityForeignKey" FOREIGN KEY ("entityId") REFERENCES "Entity" ("id"),
    CONSTRAINT "ProjectAreaForeignKey" FOREIGN KEY ("collaborationAreaId") REFERENCES "CollaborationArea" ("id")
);

CREATE TABLE "Ods" (
    "id" BIGSERIAL PRIMARY KEY,
    "name" VARCHAR(63) NOT NULL,
    "number" INTEGER NOT NULL,
    "description" VARCHAR(500) NOT NULL,
    "url" VARCHAR(127) NOT NULL
);
CREATE TABLE "Project_Ods" (
    "projectId" BIGINT NOT NULL,
    "odsId" BIGINT NOT NULL,
    PRIMARY KEY ("projectId", "odsId"),
    CONSTRAINT "Project_OdsProjectForeignKey" FOREIGN KEY ("projectId")
       REFERENCES "Project" ("id") ON DELETE CASCADE,
    CONSTRAINT "Project_OdsOdsForeignKey" FOREIGN KEY ("odsId")
       REFERENCES "Ods" ("id") ON DELETE CASCADE
);
CREATE TABLE "Task"(
    "id" BIGSERIAL PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    "projectId" BIGINT NOT NULL,
    CONSTRAINT "TaskProjectForeignKey" FOREIGN KEY ("projectId") REFERENCES "Project" ("id")
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
    "isVerified" BOOLEAN,
    CONSTRAINT "VolunteerRecordDniUniqueKey" UNIQUE ("dni")
);

CREATE INDEX "VolunteerRecordIndexByDni" ON "VolunteerRecord" ("dni") WHERE "dni" IS NOT NULL;
CREATE INDEX "VolunteerRecordIndexByUserId" ON "VolunteerRecord" ("userId") WHERE "dni" IS NULL;

CREATE TABLE "Representative" (
    "phone" VARCHAR(20),
    "name" VARCHAR(60),
    "surname" VARCHAR(60),
    "id" BIGINT NOT NULL PRIMARY KEY,
    "entityId" BIGINT NOT NULL,
    CONSTRAINT "RepresentativeEntityForeignKey" FOREIGN KEY("entityId") REFERENCES "Entity" (id),
    CONSTRAINT "RepresentativeUserForeignKey" FOREIGN KEY ("id") REFERENCES "User" ("id")
);

CREATE TABLE "Participation" (
    "id" BIGSERIAL PRIMARY KEY,
    "totalHours" INTEGER,
    "state" VARCHAR(15) CHECK (state IN ('PENDING', 'SCHEDULED', 'REJECTED', 'APPROVED', 'ACCEPTED', 'DELETED')),
    "isRecommended" BOOLEAN NOT NULL,
    "registerDate" DATE NOT NULL,
    "projectId" BIGINT NOT NULL,
    "volunteerRecordId" BIGINT NOT NULL,
    CONSTRAINT "ParticipationProjectForeignKey" FOREIGN KEY("projectId") REFERENCES "Project" (id),
    CONSTRAINT "ParticipationVolunteerRecordForeignKey" FOREIGN KEY("volunteerRecordId") REFERENCES "VolunteerRecord" (id)
);
