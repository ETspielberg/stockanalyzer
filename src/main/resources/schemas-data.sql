-- Table: journal_counter

-- DROP TABLE journal_counter;

CREATE TABLE NOT EXISTS journal_counter
(
  id character varying(255) NOT NULL,
  abbreviation character varying(255),
  category character varying(255),
  doi character varying(255),
  full_name character varying(255),
  html_requests bigint NOT NULL,
  html_requests_mobile bigint NOT NULL,
  month integer NOT NULL,
  online_issn character varying(255),
  pdf_requests bigint NOT NULL,
  pdf_requests_mobile bigint NOT NULL,
  platform character varying(255),
  print_issn character varying(255),
  proprietary character varying(255),
  ps_requests bigint NOT NULL,
  ps_requests_mobile bigint NOT NULL,
  publisher character varying(255),
  total_requests bigint NOT NULL,
  type character varying(255),
  year integer NOT NULL,
  profile character varying(255),
  CONSTRAINT journal_counter_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE journal_counter
  OWNER TO "Eike";

CREATE TABLE NOT EXISTS database_counter
(
  id character varying(255) NOT NULL,
  federated_and_automated_searches bigint NOT NULL,
  month integer NOT NULL,
  platform character varying(255),
  publisher character varying(255),
  record_views bigint NOT NULL,
  regular_searches bigint NOT NULL,
  result_clicks bigint NOT NULL,
  year integer NOT NULL,
  longDescription character varying(255),
  profile character varying(255),
  CONSTRAINT database_counter_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE database_counter
  OWNER TO "Eike";


CREATE TABLE NOT EXISTS ebook_counter
(
  id character varying(255) NOT NULL,
  doi character varying(255),
  epub_request bigint NOT NULL,
  html_requests bigint NOT NULL,
  html_requests_mobile bigint NOT NULL,
  isni character varying(255),
  month integer NOT NULL,
  online_isbn character varying(255),
  pdf_requests bigint NOT NULL,
  pdf_requests_mobile bigint NOT NULL,
  platform character varying(255),
  print_isbn character varying(255),
  profile character varying(255),
  proprietary character varying(255),
  ps_requests bigint NOT NULL,
  ps_requests_mobile bigint NOT NULL,
  publisher character varying(255),
  section_requests character varying(255),
  longDescription character varying(255),
  total_requests bigint NOT NULL,
  year integer NOT NULL,
  proprietary_identifier character varying(255),
  CONSTRAINT ebook_counter_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE ebook_counter
  OWNER TO "Eike";
