#!/bin/sh
#Questo è lo script di start di JBPM su Ubuntu 12

echo "Cambio cartella";
cd /root/jbpm-installer

echo "Start Jbpm";
sudo ant start.demo.noeclipse

