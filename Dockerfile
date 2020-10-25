FROM openjdk:11.0.3-jre-slim
MAINTAINER Shisei Hanai<ruimo.uno@gmail.com>

RUN apt-get update
RUN apt-get install unzip
RUN mkdir -p /opt/led
ADD rpi-led-*.zip /opt/led
RUN cd /opt/led && \
  cmd=$(basename rpi-led-*.zip .zip) && \
  unzip -q $cmd.zip && \
  echo export ONLINE_BLINK_PERIOD=500 > /opt/led/launch.sh && \
  echo /opt/led/$cmd/bin/rpi-led >> /opt/led/launch.sh && \
  chmod +x /opt/led/launch.sh

EXPOSE 8080

ENTRYPOINT ["/bin/bash", "-c", "opt/led/launch.sh"]
