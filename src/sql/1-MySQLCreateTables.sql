DROP TABLE IF EXISTS "User";

CREATE TABLE "User" (
    "id" BIGSERIAL PRIMARY KEY,
    "userName" VARCHAR(60) COLLATE "C" NOT NULL,
    "password" VARCHAR(60) NOT NULL,
    "firstName" VARCHAR(60) NOT NULL,
    "lastName" VARCHAR(60) NOT NULL,
    "email" VARCHAR(60) NOT NULL,
    "role" SMALLINT NOT NULL,
    CONSTRAINT "UserEmailUniqueKey" UNIQUE ("email"),
    CONSTRAINT "UserUserNameUniqueKey" UNIQUE ("userName")
);
CREATE INDEX "UserIndexByUserName" ON "User" ("userName");