#!/bin/sh
#Script di installazione di CKAN su Ubuntu 12

echo "Setto le variabili"
JettyStatus="NO_START=0"
JavaPath="JAVA_HOME=\/usr\/lib\/jvm\/java-6-openjdk-amd64\/"
JettyHost="JETTY_HOST=127.0.0.1"
JettyPort="JETTY_PORT=8983"

#dbPassW="mccmar"
#dbUser=test
#dbName=ckanmario
dbName="$1"
dbUser="$1"
dbPassW="$3"

echo "Aggiorno il sistema"
sudo apt-get update

echo "Scarico i pacchetti di Ubuntu utili per CKAN"
sudo apt-get install -y nginx apache2 libapache2-mod-wsgi libpq5

echo "Scarico i pacchetti di CKAN"
wget http://appstore.sielte.it/repo/python-ckan_2.2_amd64.deb

echo "Installo CKAN"
sudo dpkg -i python-ckan_2.2_amd64.deb

echo "Installo Postgresql"
sudo apt-get install -y postgresql solr-jetty

echo "Modifico il file di configurazione di Jetty"
cd /etc/default
sed -i 's/NO_START=1/'$JettyStatus'/g' jetty
sed -i 's/#JETTY_PORT=8080/'$JettyPort'/g' jetty
sed -i 's/#JETTY_HOST=$(uname -n)/'$JettyHost'/g' jetty
sed -i 's/#JAVA_HOME=/'$JavaPath'/g' jetty

sudo mv /etc/solr/conf/schema.xml /etc/solr/conf/schema.xml.bak
sudo ln -s /usr/lib/ckan/default/src/ckan/ckan/config/solr/schema.xml /etc/solr/conf/schema.xml

sudo service jetty restart

echo "Modifico il file di configurazione Ckan"
echo "sqlalchemy.url = postgresql://username:password@host:port/database"

cd /etc/ckan/default
sed -i 's/pass/'$dbPassW'/g' production.ini
#sed -i 's/port = 5000/port = 5001/g' production.ini
sed -i 's/ckan_default:/'$dbUser':/g' production.ini
sed -i 's/ckan_default/'$dbName'/g' production.ini

echo "Installo Paster"
sudo apt-get install -y python-pastescript

echo "Creo l'utente Postgres"
sudo -u postgres createuser -S -D -R $dbUser -w
sudo -u postgres psql -c "ALTER USER $dbUser WITH PASSWORD '$dbPassW';"

echo "Creo il DB ed associo l'utente"
sudo -u postgres createdb -O $dbUser $dbName -E utf-8

echo "Accedo all'ambiente Python"
. /usr/lib/ckan/default/bin/activate

echo "Creo la configurazione di Ckan"
cd /usr/lib/ckan/default/src/ckan
paster db init -c /etc/ckan/default/production.ini
