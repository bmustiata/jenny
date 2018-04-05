FROM ubuntu:16.04

RUN apt update -y && \
    apt upgrade -y && \
    apt install -y groovy curl && \
    curl https://get.docker.com | bash

COPY . /jenny/
RUN chmod +x /jenny/bin/test-jenny.sh && \
    chmod +x /jenny/jenny && \
    mkdir -p ${HOME}/.jenny && \
    echo "noLogo: true" > ${HOME}/.jenny/config

CMD /jenny/bin/test-jenny.sh

