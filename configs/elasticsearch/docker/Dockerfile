FROM elasticsearch:2.4.2

ADD elasticsearch.yml /usr/share/elasticsearch/config

# Define default command
RUN plugin install delete-by-query
RUN plugin install mobz/elasticsearch-head