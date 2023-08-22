FROM eclipse-mosquitto:2.0.15

COPY ./config /mosquitto/config

EXPOSE 1883
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["/usr/sbin/mosquitto", "-c", "/mosquitto/config/mosquitto.conf"]

