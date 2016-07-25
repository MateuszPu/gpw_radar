FROM rabbitmq:3-management

RUN  apt-get update \
  && apt-get install -y curl \
  && rm -rf /var/lib/apt/lists/*

ADD init.sh /init.sh

# Define default command
CMD ["/init.sh"]