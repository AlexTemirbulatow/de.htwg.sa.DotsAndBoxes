FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1

WORKDIR /app

COPY build.sbt ./
COPY project ./project

COPY common ./common
COPY metric/src ./metric/src

EXPOSE 8086

CMD ["sbt", "-Dconfig.resource=application.docker.conf", "metric/run"]

