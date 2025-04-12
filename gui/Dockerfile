FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1

RUN apt-get update && apt-get install -y \
    libxrender1 libxtst6 libgl1-mesa-glx libgtk-3-0 \
    libcanberra-gtk-module libcanberra-gtk3-module

WORKDIR /app

COPY build.sbt ./
COPY project ./project

COPY common ./common
COPY gui/src ./gui/src

EXPOSE 8085

CMD ["sbt", "-Dconfig.resource=application.docker.conf", "gui/run"]
