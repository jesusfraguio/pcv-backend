DROP TABLE IF EXISTS "VolunteerRecord";
DROP TABLE IF EXISTS "User";

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