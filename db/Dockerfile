FROM mysql/mysql-server:latest

# Set working directory
WORKDIR /tmp

# Copy all SQL and dump files into /tmp
COPY test_db/*.sql /tmp/
COPY test_db/*.dump /tmp/

# Copy the main employees SQL file into MySQL init folder
COPY test_db/employees.sql /docker-entrypoint-initdb.d

# Set root password
ENV MYSQL_ROOT_PASSWORD=example
