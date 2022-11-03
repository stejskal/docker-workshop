#A good base image to start from
FROM maven:3.8.6

#moving all files in the current directory into the filesystem of the image
COPY . /

#executing the maven package command inside the image
RUN mvn package

# setting up the default command to run when the container is started
CMD "java" "-jar" "target/factorial-server-1.0-SNAPSHOT.jar"
