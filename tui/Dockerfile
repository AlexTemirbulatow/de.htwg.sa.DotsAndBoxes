FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1

WORKDIR /app

COPY build.sbt ./
COPY project ./project

COPY common ./common
COPY tui/src ./tui/src

EXPOSE 8084

CMD ["sbt", "-Dconfig.resource=application.docker.conf", "tui/run"]
