FROM ubuntu:16.04

RUN apt update -y && \
    apt upgrade -y && \
    apt install -y groovy curl && \
    curl https://get.docker.com | bash

RUN useradd -m germanium && \
    usermod -G docker germanium && \
    echo 'DOCKER_OPTS="-H tcp://0.0.0.0:4243 -H unix:///var/run/docker.sock"' >> /etc/default/docker

USER germanium
RUN mkdir /home/germanium/.jenny && \
    echo "noLogo: true" > /home/germanium/.jenny/config

ARG JENNY_WORKSPACE_FOLDER=/tmp/test
RUN mkdir -p ${JENNY_WORKSPACE_FOLDER}

