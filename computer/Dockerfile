FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1

WORKDIR /app

COPY build.sbt ./
COPY project ./project

COPY common ./common
COPY computer/src ./computer/src

EXPOSE 8082

CMD ["sbt", "-Dconfig.resource=application.docker.conf", "computer/run"]

