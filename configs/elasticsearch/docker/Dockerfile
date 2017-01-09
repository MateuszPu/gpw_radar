FROM elasticsearch:2.4.2

ADD elasticsearch.yml /usr/share/elasticsearch/config

ADD init.sh /init.sh

# Define default command
CMD ["/init.sh"]
