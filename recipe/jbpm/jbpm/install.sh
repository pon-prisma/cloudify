#!/bin/sh
#Questo è lo script di installazione di JBPM su Ubuntu 12

echo "Aggiorno il sistema"
sudo apt-get update

echo "Installo unzip"
sudo apt-get -y install unzip

echo "Scarico Java";
sudo apt-get -y install openjdk-7-jdk openjdk-7-jre

#echo "Setto la variabile Java";
#export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64

echo "Installo ANT";
sudo apt-get -y install ant

echo "Scarico JBPM";
cd /root
#wget http://sourceforge.net/projects/jbpm/files/jBPM%205/jbpm-5.4.0.Final/jbpm-5.4.0.Final-installer-full.zip
wget http://sourceforge.net/projects/jbpm/files/jBPM%206/jbpm-6.1.0.Final/jbpm-6.1.0.Final-installer-full.zip

echo "Scompatto JBPM";
sudo unzip jbpm-6.1.0.Final-installer-full.zip
#sudo unzip jbpm-5.4.0.Final-installer-full.zip

echo "Cambio cartella";
cd /root/jbpm-installer

echo "Installo Jbpm Ant";
sudo ant install.demo.noeclipse

echo "Modifico il file di configurazione";
echo jboss.bind.address.management=0.0.0.0 >> build.properties
echo jboss.bind.address=0.0.0.0 >> build.properties
echo jboss.bind.address.unsecure=0.0.0.0 >> build.properties
sed -i 's/value="localhost"/value="0.0.0.0"/g' build.xml


