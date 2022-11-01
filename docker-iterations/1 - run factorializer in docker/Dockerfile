#A good base image to start from
FROM maven:3.8.6

#Creating a place for our files to live in the filesystem of the image
RUN mkdir /app

#Setting the directory in which all commands will execute
WORKDIR /app

#moving all files in the current directory into the filesystem of the image
COPY . /app

#executing the maven package command inside the image
RUN mvn package

# setting up the default command to run when the container is started
CMD "java" "-jar" "target/factorial-server-1.0-SNAPSHOT.jar"
