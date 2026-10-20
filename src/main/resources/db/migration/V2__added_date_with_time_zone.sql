-- dates are mapped to java.time.Instant, which Hibernate stores as timestamp with time zone;
-- existing values were written in UTC
alter table code_snippet
    alter column added_date type timestamp(6) with time zone using added_date at time zone 'UTC';

alter table tag
    alter column added_date type timestamp(6) with time zone using added_date at time zone 'UTC';
