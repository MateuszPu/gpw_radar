FROM rabbitmq:3-management

RUN  apt-get update \
  && apt-get install -y curl \
  && rm -rf /var/lib/apt/lists/*

ADD create_rss_part.sh /create_rss_part.sh
ADD create_stock_details_part.sh /create_stock_details_part.sh
ADD create_users.sh /create_users.sh
ADD init.sh /init.sh

# Define default command
CMD ["/init.sh"]