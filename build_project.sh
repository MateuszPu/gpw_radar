perl -pi -e 's/192.168.99.100/127.0.0.1/g' configs/rabbitmq/properties/rabbitmq_config.properties
perl -pi -e 's/mail_password/'$1'/g' web_app/gpw_radar/src/main/resources/config/application.yml
rm -rf target
mkdir target
cd web_app/gpw_radar
mvn -Pprod clean package
cp -p $(find target/ -name 'gpw*.war') ../../target
cd ../../micro_services/rabbitmq_service
mvn clean package
cp -p $(find target/ -name 'rabbitmq*.jar') ../../target
cd ../../
perl -pi -e 's/127.0.0.1/192.168.99.100/g' configs/rabbitmq/properties/rabbitmq_config.properties
perl -pi -e 's/'$1'/mail_password/g' web_app/gpw_radar/src/main/resources/config/application.yml