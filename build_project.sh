if [ $# -eq 0 ]
  then
    echo "No mail_password provided"
    exit 1
fi
perl -pi -e 's/mail_password/'$1'/g' web_app/gpw_radar/src/main/resources/config/application.yml
rm -rf configs/app/*/*.jar
rm -rf target
mkdir target
mvn -f web_app/technical_analysis clean install
mvn -Dmaven.test.skip=true -f web_app/gpw_radar -Pprod clean package
cp web_app/gpw_radar/target/gpw-radar-*.jar target
cp web_app/gpw_radar/target/gpw-radar-*.jar configs/app/web/app.jar
mvn -f micro_services clean package
cp micro_services/rabbitmq_service/target/rabbitmq*.jar configs/app/rss/app.jar
perl -pi -e 's/'$1'/mail_password/g' web_app/gpw_radar/src/main/resources/config/application.yml
