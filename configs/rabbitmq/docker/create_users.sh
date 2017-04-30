#!/bin/sh

# Create Rabbitmq user
( sleep 2 ; \

rabbitmqctl delete_user guest ; \

#create admin user and add permissions
rabbitmqctl add_user admin admin ; \

#create gpw_consumer user and add permissions
rabbitmqctl add_user gpw_consumer consumer ; \

#create rss_producer user and add permissions
rabbitmqctl add_user rss_producer producer ; \

#add admin tags to admin user
rabbitmqctl set_user_tags admin administrator ;\

#create vhost for app
rabbitmqctl add_vhost gpw_radar ; \

#add permissions
rabbitmqctl set_permissions -p gpw_radar gpw_consumer  "" "" ".*" ; \
rabbitmqctl set_permissions -p gpw_radar rss_producer  "" ".*" "" ; \
rabbitmqctl set_permissions -p gpw_radar admin  ".*" ".*" ".*" ; \
rabbitmqctl set_permissions -p / admin  ".*" "" "" ; \

)