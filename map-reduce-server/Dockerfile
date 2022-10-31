FROM maven:3.8.6

ENV workers=""
RUN mkdir /app
WORKDIR /app
COPY . /app
RUN mvn clean package
CMD "java" "-jar" "target/map-reduce-server-1.0-SNAPSHOT.jar" $workers
