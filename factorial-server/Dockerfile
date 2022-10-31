FROM maven:3.8.6

RUN mkdir /app
WORKDIR /app
COPY . /app
RUN mvn clean package
CMD "java" "-jar" "target/factorial-server-1.0-SNAPSHOT.jar"
