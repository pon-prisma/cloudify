import org.cloudifysource.utilitydomain.context.ServiceContextFactory
import org.cloudifysource.dsl.utils.ServiceUtils;

println "jbpm_start.groovy: Starting jbpm..."



context = ServiceContextFactory.getServiceContext()

builder = new AntBuilder()


builder.sequential {
	echo(message:"jbpm_start.groovy: Chmodding +x ${context.serviceDirectory} ...")
	chmod(dir:"${context.serviceDirectory}", perm:"+x", includes: "*.sh")
	exec(executable: "${context.serviceDirectory}/start.sh",failonerror: "false")
}