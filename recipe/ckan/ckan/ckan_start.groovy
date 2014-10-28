import org.cloudifysource.utilitydomain.context.ServiceContextFactory
import org.cloudifysource.dsl.utils.ServiceUtils;

println "ckan_start.groovy: Starting ckan..."



context = ServiceContextFactory.getServiceContext()

builder = new AntBuilder()


builder.sequential {
	echo(message:"ckan_start.groovy: Chmodding +x ${context.serviceDirectory} ...")
	chmod(dir:"${context.serviceDirectory}", perm:"+x", includes: "*.sh")
	exec(executable: "${context.serviceDirectory}/start.sh",failonerror: "false")
}