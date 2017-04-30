#!/bin/sh

# Create Rabbitmq user
( sleep 2 ; \
#stock details updater part
curl -i -u admin:admin -H "Content-Type: application/json" -d '{"auto_delete": false, "durable": true, "arguments":{"x-dead-letter-exchange": "error_exchange"}}' -XPUT http://localhost:15672/api/queues/gpw_radar/stock_details_updater ;\

curl -i -u admin:admin -H "Content-Type: application/json" -d '{"type":"direct","auto_delete":false,"durable":true,"internal":false}' -XPUT http://localhost:15672/api/exchanges/gpw_radar/stock_details_direct ;\

curl -i -u admin:admin -H "Content-Type: application/json" -d '{"routing_key":"stock_details"}' -XPOST http://localhost:15672/api/bindings/gpw_radar/e/stock_details_direct/q/stock_details_updater ;\

)