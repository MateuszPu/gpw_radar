#!/bin/sh

# Create Rabbitmq user
( sleep 10 ; \

rabbitmqctl delete_user guest ; \

#create admin user and add permissions
rabbitmqctl add_user admin admin ; \

#create gpw_consumer user and add permissions
rabbitmqctl add_user gpw_consumer consumer ; \

#create rss_producer user and add permissions
rabbitmqctl add_user rss_producer producer ; \

#add admin tags to admin user
rabbitmqctl set_user_tags admin administrator

#create vhost for app
rabbitmqctl add_vhost gpw_radar ; \

#add permissions
rabbitmqctl set_permissions -p gpw_radar gpw_consumer  "" "" ".*" ; \
rabbitmqctl set_permissions -p gpw_radar rss_producer  "" ".*" "" ; \
rabbitmqctl set_permissions -p gpw_radar admin  ".*" ".*" ".*" ; \
rabbitmqctl set_permissions -p / admin  ".*" "" "" ; \

curl -i -u admin:admin -H "Content-Type: application/json" -d '{"auto_delete": false, "durable": true}' -XPUT http://192.168.99.100:15672/api/queues/gpw_radar/rss_mail
curl -i -u admin:admin -H "Content-Type: application/json" -d '{"auto_delete": false, "durable": true}' -XPUT http://192.168.99.100:15672/api/queues/gpw_radar/rss_chat
curl -i -u admin:admin -H "Content-Type: application/json" -d '{"auto_delete": false, "durable": true}' -XPUT http://192.168.99.100:15672/api/queues/gpw_radar/rss_database

curl -i -u admin:admin -H "Content-Type: application/json" -d '{"type":"fanout","auto_delete":false,"durable":true,"internal":false}' -XPUT http://192.168.99.100:15672/api/exchanges/gpw_radar/rss_fanout

curl -i -u admin:admin -H "Content-Type: application/json" -d '{"routing_key":""}' -XPOST http://192.168.99.100:15672/api/bindings/gpw_radar/e/rss_fanout/q/rss_mail
curl -i -u admin:admin -H "Content-Type: application/json" -d '{"routing_key":""}' -XPOST http://192.168.99.100:15672/api/bindings/gpw_radar/e/rss_fanout/q/rss_chat
curl -i -u admin:admin -H "Content-Type: application/json" -d '{"routing_key":""}' -XPOST http://192.168.99.100:15672/api/bindings/gpw_radar/e/rss_fanout/q/rss_database

echo "*** User '$RABBITMQ_USER' with password '$RABBITMQ_PASSWORD' completed. ***" ; \
echo "*** Log in the WebUI at port 15672 ***") &

rabbitmq-server $@