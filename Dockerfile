FROM jimador/docker-jdk-8-maven-node

ADD configs configs
ADD micro_services micro_services
ADD web_app  web_app
ENV pass=myPassword

RUN apt-get update && \
    rm -rf configs/app/*/*.jar && \
    rm -rf target && \
    mkdir target && \
    perl -pi -e 's/mail_password/'${pass}'/g' web_app/gpw_radar/src/main/resources/config/application.yml && \
    mvn -f web_app/technical_analysis clean install && \
    mvn -f web_app/gpw_radar -Pprod clean package && \
    cp web_app/gpw_radar/target/gpw-radar-*.jar target && \
    cp web_app/gpw_radar/target/gpw-radar-*.jar configs/app/web/app.jar && \
    mvn -f micro_services clean package && \
    cp micro_services/rabbitmq_service/target/rabbitmq*.jar configs/app/rss/app.jar