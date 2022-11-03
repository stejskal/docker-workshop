FROM maven:3.8.6

ENV workers=""
COPY . /
RUN mvn clean package
CMD "java" "-jar" "target/map-reduce-server-1.0-SNAPSHOT.jar" $workers
