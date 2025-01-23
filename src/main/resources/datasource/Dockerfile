FROM postgres:16.1

ENV POSTGRES_DB bank-challenge-db
ENV POSTGRES_USER root
ENV POSTGRES_PASSWORD root

COPY init.sql /docker-entrypoint-initdb.d/

EXPOSE 5432

CMD ["postgres"]