#!/bin/sh

APP_VER=${VERSION:-latest}

docker build -t k8sled/rpi-led:$APP_VER .
