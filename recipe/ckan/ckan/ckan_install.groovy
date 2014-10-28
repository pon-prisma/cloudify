import org.cloudifysource.utilitydomain.context.ServiceContextFactory
import org.cloudifysource.dsl.utils.ServiceUtils;
import org.cloudifysource.domain.context.ServiceInstance;

println "ckan_install.groovy: Installing ckan..."

context = ServiceContextFactory.getServiceContext()
config = new ConfigSlurper().parse(new File("ckan-service.properties").toURL())

builder = new AntBuilder()

context.attributes.thisInstance["dbName"] = config.dbName
context.attributes.thisInstance["dbUser"] = config.dbUser
context.attributes.thisInstance["dbPassW"] = config.dbPassW

builder.sequential {
	echo(message:"ckan_install.groovy: Chmodding +x ${context.serviceDirectory} ...")
	chmod(dir:"${context.serviceDirectory}", perm:"+x", includes: "*.sh")
	exec(executable: "${context.serviceDirectory}/install.sh",failonerror: "false")
	{
			arg(value:"${config.dbName}")
			arg(value:"${config.dbUser}")
			arg(value:"${config.dbPassW}")
			
	}
}

