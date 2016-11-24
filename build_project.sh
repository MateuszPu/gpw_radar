rm -rf target
mkdir target
cd web_app/gpw_radar
mvn -Pprod clean package
cp -p $(find target/ -name 'gpw*.war') ../../target
cd ../../micro_services/rabbitmq_service
mvn clean package
cp -p $(find target/ -name 'rabbitmq*.jar') ../../target