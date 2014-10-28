import org.cloudifysource.dsl.utils.ServiceUtils;
import org.cloudifysource.utilitydomain.context.ServiceContextFactory;

context = ServiceContextFactory.getServiceContext()


new GroovyShell().evaluate(new File("apache_postInstall.groovy"))

config = new ConfigSlurper().parse(new File("wordpress-service.properties").toURL())

osConfig = ServiceUtils.isWindows() ? config.win32 : config.linux



builder = new AntBuilder()

drRoot = context.attributes.thisInstance["docRoot"]


def mysqlInstance=context.attributes.mysql.instances[1]
dbHost=mysqlInstance["dbHost"]
dbName=mysqlInstance["dbName"]
dbUser=mysqlInstance["dbUser"]
dbPassW=mysqlInstance["dbPassW"]

installFolder="${context.serviceDirectory}"

/*configFileName= "${installFolder}/tmpZipFolder/appFolder/wp-config-sample.php"*/

configFile = new File("/var/www/wp-config-sample.php")
configFileText = configFile.text

modifiedConfig = configFileText.replace("database_name_here","${dbName}")
modifiedConfig = modifiedConfig.replace("username_here","${dbUser}")
modifiedConfig = modifiedConfig.replace("password_here","${dbPassW}")
modifiedConfig = modifiedConfig.replace("localhost","${dbHost}")

configFile.text = modifiedConfig

configFileNameDest = "${installFolder}/tmpZipFolder/appFolder/wp-config.php"

builder.sequential {

	echo(message:"wordpress_postInstall.groovy: Starting wordpress_postInstall.groovy ...")
	
	builder.copy(file:'/var/www/wp-config-sample.php', tofile:'/var/www/wp-config.php')
	
	builder.delete(file:'/var/www/wp-config-sample.php')

}