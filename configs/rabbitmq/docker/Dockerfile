FROM rabbitmq:3-management

RUN  apt-get update \
  && apt-get install -y curl \
  && rm -rf /var/lib/apt/lists/*

ADD rabbitmq.config /etc/rabbitmq/
ADD def.json /etc/rabbitmq/
