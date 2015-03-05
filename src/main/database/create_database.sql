-- Database: "CurrencyFair"

-- DROP DATABASE "CurrencyFair";

CREATE DATABASE "CurrencyFair"
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'English_United States.1252'
       LC_CTYPE = 'English_United States.1252'
       CONNECTION LIMIT = -1;



-- Table: "MessagesStore"

-- DROP TABLE "MessagesStore";

CREATE TABLE "MessagesStore"
(
  "Index" bigserial NOT NULL,
  "UserId" text NOT NULL,
  "CurrencyFrom" text,
  "CurrencyTo" text,
  "AmountSell" double precision,
  "AmountBuy" double precision,
  "Rate" double precision,
  "TimePlaced" timestamp without time zone,
  "OriginatingCountry" text,
  CONSTRAINT "Index" PRIMARY KEY ("Index")
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "MessagesStore"
  OWNER TO postgres;
