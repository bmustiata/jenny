FROM ubuntu:16.04

RUN apt update -y && \
    apt upgrade -y && \
    apt install -y groovy

COPY . /jenny/
RUN chmod +x /jenny/bin/test-jenny.sh && \
    chmod +x /jenny/jenny

CMD /jenny/bin/test-jenny.sh

