#!/bin/sh

# Create Rabbitmq user
( sleep 5 ; \
./create_users.sh ;\
./create_rss_part.sh ;\
./create_stock_details_part.sh ;\
) &

rabbitmqctl start_app $@
