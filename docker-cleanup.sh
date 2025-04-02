#!/bin/bash

docker stop $(docker ps -q) && docker rm $(docker ps -aq) && docker rmi -f $(docker images -q) && docker volume rm $(docker volume ls -q)
