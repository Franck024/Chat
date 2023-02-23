FROM postgres:15.1
COPY create_tables.sql /docker-entrypoint-initdb.d/