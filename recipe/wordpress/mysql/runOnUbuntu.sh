#!/bin/bash -x

# args:
# $1 the error code of the last command (should be explicitly passed)
# $2 the message to print in case of an error
# 
# an error message is printed and the script exists with the provided error code
function error_exit {
	echo "$2 : error code: $1"
	exit ${1}
}

export PATH=$PATH:/usr/sbin:/sbin:/usr/bin || error_exit $? "Failed on: export PATH=$PATH:/usr/sbin:/sbin"

if [[ ! "$(service mysql status)" =~ "start/running" ]]
then
    sudo service mysql start || error_exit $? "Failed on: sudo service mysql start"
fi


ps -ef | grep -i mysql | grep -ivE "gigaspaces|GSC|GSA|grep"

sleep 5