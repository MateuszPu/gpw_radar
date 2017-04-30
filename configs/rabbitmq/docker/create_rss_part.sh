#!/bin/sh

# Create Rabbitmq user
( sleep 2 ; \

#rss part
curl -i -u admin:admin -H "Content-Type: application/json" -d '{"auto_delete": false, "durable": true, "arguments":{"x-dead-letter-exchange": "error_exchange"}}' -XPUT http://localhost:15672/api/queues/gpw_radar/rss_mail ;\
curl -i -u admin:admin -H "Content-Type: application/json" -d '{"auto_delete": false, "durable": true, "arguments":{"x-dead-letter-exchange": "error_exchange"}}' -XPUT http://localhost:15672/api/queues/gpw_radar/rss_chat ;\
curl -i -u admin:admin -H "Content-Type: application/json" -d '{"auto_delete": false, "durable": true, "arguments":{"x-dead-letter-exchange": "error_exchange"}}' -XPUT http://localhost:15672/api/queues/gpw_radar/rss_database ;\

curl -i -u admin:admin -H "Content-Type: application/json" -d '{"type":"fanout","auto_delete":false,"durable":true,"internal":false}' -XPUT http://localhost:15672/api/exchanges/gpw_radar/rss_fanout ;\

curl -i -u admin:admin -H "Content-Type: application/json" -d '{"routing_key":""}' -XPOST http://localhost:15672/api/bindings/gpw_radar/e/rss_fanout/q/rss_mail ;\
curl -i -u admin:admin -H "Content-Type: application/json" -d '{"routing_key":""}' -XPOST http://localhost:15672/api/bindings/gpw_radar/e/rss_fanout/q/rss_chat ;\
curl -i -u admin:admin -H "Content-Type: application/json" -d '{"routing_key":""}' -XPOST http://localhost:15672/api/bindings/gpw_radar/e/rss_fanout/q/rss_database ;\

curl -i -u admin:admin -H "Content-Type: application/json" -d '{"type":"topic","auto_delete":false,"durable":true,"internal":false}' -XPUT http://localhost:15672/api/exchanges/gpw_radar/error_exchange ;\
curl -i -u admin:admin -H "Content-Type: application/json" -d '{"auto_delete": false, "durable": true}' -XPUT http://localhost:15672/api/queues/gpw_radar/error ;\
curl -i -u admin:admin -H "Content-Type: application/json" -d '{"routing_key":"#"}' -XPOST http://localhost:15672/api/bindings/gpw_radar/e/error_exchange/q/error ;\

)