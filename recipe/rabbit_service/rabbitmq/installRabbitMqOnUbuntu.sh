#!/bin/bash
#
# Installs the RabbitMQ server

echo "###### Installing Rabbit MQ Server on Ubuntu"

userAdmin=${1}
passAdmin=${2}

echo "User e Pass: ${userAdmin} e ${passAdmin}..."

# Install erlang
sudo apt-get -y install erlang-nox

# Install rabbit mq

wget http://www.rabbitmq.com/rabbitmq-signing-key-public.asc
apt-key add rabbitmq-signing-key-public.asc
echo "deb http://www.rabbitmq.com/debian/ testing main" > /etc/apt/sources.list.d/rabbitmq.list
apt-get update
apt-get install rabbitmq-server


# Adding RabbitMQ management 
rabbitmq-plugins enable rabbitmq_management

#invoke-rc.d rabbitmq-server restart

rabbitmqctl add_user $userAdmin $passAdmin
rabbitmqctl set_user_tags $userAdmin administrator
rabbitmqctl set_permissions -p / $userAdmin ".*" ".*" ".*"


echo "##### Finished installing Rabbit MQ Server on Ubuntu"

exit 0
