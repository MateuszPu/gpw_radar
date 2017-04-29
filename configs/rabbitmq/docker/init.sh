#!/bin/sh

# Create Rabbitmq user
( sleep 2 ; \
./create_users.sh ;\
./create_rss_part.sh ;\
./create_stock_details_part.sh ;\
) &

rabbitmq-server $@