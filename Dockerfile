FROM jimador/docker-jdk-8-maven-node

ADD configs configs
ADD micro_services micro_services
ADD web_app  web_app
ADD build_project.sh build_project.sh
RUN apt-get update
#RUN curl -sL https://deb.nodesource.com/setup_8.x
#RUN apt-get install -y nodejs
#RUN ls /usr/local/bin/docker-java-home
#RUN whereis npm
#RUN /usr/local/bin/npm -v \
 # && /usr/local/bin/npm install -g bower \
  #&& /usr/local/bin/npm install -g grunt \
 # && /usr/local/bin/npm install -g grunt-cli

RUN ./build_project.sh pass

