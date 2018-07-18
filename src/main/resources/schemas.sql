-- Table: profile

-- DROP TABLE profile;

CREATE TABLE IF NOT EXISTS profile
(
  dtype character varying(31) NOT NULL,
  identifier character varying(255) NOT NULL,
  created timestamp without time zone,
  lastrun timestamp without time zone,
  status character varying(255),
  blacklist_expire double precision,
  collections character varying(255),
  deletion_mail_bcc character varying(255),
  description character varying(255),
  grouped_analysis boolean,
  materials character varying(255),
  minimum_days_of_request integer,
  minimum_years integer,
  persist_empty_analysis boolean,
  static_buffer double precision,
  subjectid character varying(255),
  system_code character varying(255),
  variable_buffer double precision,
  years_of_requests integer,
  years_to_average integer,
  notationgroup character varying(255),
  threshold_duration integer,
  threshold_ratio double precision,
  threshold_requests integer,
  timeperiod bigint,
  name character varying(255),
  sushi_customer_referenceid character varying(255),
  sushi_customer_reference_name character varying(255),
  sushi_release integer,
  sushi_requestor_email character varying(255),
  sushi_requestorid character varying(255),
  sushi_requestor_name character varying(255),
  sushiurl character varying(255),
  report_types character varying(255),
  CONSTRAINT profile_pkey PRIMARY KEY (identifier)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE profile
  OWNER TO "postgres";


-- Table: counter_log

-- DROP TABLE counter_log;

CREATE TABLE IF NOT EXISTS counter_log
(
  id character varying NOT NULL,
  sushiprovider character varying,
  report_type character varying,
  status character varying,
  month integer,
  year integer,
  comment text, --
  error text,
  "timestamp" date
)
WITH (
  OIDS=FALSE
);
ALTER TABLE counter_log
  OWNER TO postgres;
COMMENT ON COLUMN counter_log.comment IS '
';



