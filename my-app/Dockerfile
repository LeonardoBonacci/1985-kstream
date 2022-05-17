FROM ubuntu:16.04

RUN apt-get update && \
  apt-get install -y  curl && \
  curl -fsSL https://deb.nodesource.com/setup_14.x | bash - && \
  apt-get install -y nodejs

COPY . /home/
WORKDIR /home

RUN npm install

EXPOSE 3000
CMD [ "npm", "start" ]
