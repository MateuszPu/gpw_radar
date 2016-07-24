FROM rabbitmq:3-management

RUN  apt-get update \
  && apt-get install -y wget \
  && apt-get install -y curl \
  && rm -rf /var/lib/apt/lists/*

ADD rabbitmqadmin /usr/bin/rabbitmqadmin
RUN chmod 755 /usr/bin/rabbitmqadmin

ADD init.sh /init.sh

# Define default command
CMD ["/init.sh"]