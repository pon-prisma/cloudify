#!/bin/sh
#Script di avvio di CKAN su Ubuntu 12

#echo "Accedo all'ambiente Python"
echo "Riavvio apache2...."
sudo a2enmod wsgi
sudo service apache2 restart

#. /usr/lib/ckan/default/bin/activate

#echo "Creo la configurazione di Ckan"
#cd /usr/lib/ckan/default/src/ckan
#paster db init -c /etc/ckan/default/production.ini
#paster serve /etc/ckan/default/production.ini