#!/bin/bash

set -e
set -u

# Criação das filas SQS
awslocal sqs create-queue --queue-name generic-queue
awslocal sqs create-queue --queue-name generic-queue-fifo.fifo --attributes FifoQueue=true
awslocal sqs create-queue --queue-name generic-queue-real-time
awslocal sqs create-queue --queue-name generic-queue-lazy-time