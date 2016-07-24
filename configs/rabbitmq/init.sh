#!/bin/sh

# Create Rabbitmq user
( sleep 10 ; \

#create admin user and add permissions
rabbitmqctl add_user admin admin ; \

#create gpw_consumer user and add permissions
rabbitmqctl add_user gpw_consumer consumer ; \

#create rss_producer user and add permissions
rabbitmqctl add_user rss_producer producer ; \

#create vhost for app
rabbitmqctl add_vhost gpw_radar ; \

#add permissions
rabbitmqctl set_permissions -p gpw_radar gpw_consumer  "" ".*" ".*" ; \
rabbitmqctl set_permissions -p gpw_radar rss_producer  "" ".*" ".*" ; \
rabbitmqctl set_permissions -p gpw_radar admin  ".*" ".*" ".*" ; \
rabbitmqctl set_permissions -p / admin  ".*" ".*" ".*" ; \
rabbitmqctl set_permissions -p gpw_radar guest  ".*" ".*" ".*" ; \

curl -i -u guest:guest -H "content-type:application/json" -XPUT http://192.168.99.100:15672/api/queues/gpw_radar/dummy_name

echo "*** User '$RABBITMQ_USER' with password '$RABBITMQ_PASSWORD' completed. ***" ; \
echo "*** Log in the WebUI at port 15672 ***") &

rabbitmq-server $@