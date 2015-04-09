import org.cloudifysource.utilitydomain.context.ServiceContextFactory

import java.util.concurrent.TimeUnit
println "mongod_poststart.groovy: inizio il postStart..."


serviceContext = ServiceContextFactory.getServiceContext()

mongosService = serviceContext.waitForService("mongos", 20, TimeUnit.SECONDS)

if (mongosService == null) {
	throw new IllegalStateException("mongos service not found. mongos must be installed before mongos.");
}
def port = serviceContext.attributes.thisInstance["port"]

def privateIP
if (  serviceContext.isLocalCloud()  ) {
	privateIP = InetAddress.getLocalHost().getHostAddress()
}
else {
	privateIP = System.getenv()["CLOUDIFY_AGENT_ENV_PRIVATE_IP"]
}
println "mongod_poststart.groovy: privateIP is ${privateIP} ..."

println "mongod_poststart.groovy: Chiamo il comand addShard specificando ${privateIP}:${port}"
def params = new Object[2]
params[0] = privateIP
params[1] = port.toString()

mongosService.invoke("addShard",params)


//mongosService.
//mongosService.
//mongosService.getInstances()[0].
/*
currPort = serviceContext.attributes.thisInstance["port"] as int
println "mongos_poststart.groovy: Connecting to mongos on port ${currPort} ..."

mongo = new GMongo("127.0.0.1", currPort)
	
println "mongos_poststart.groovy: After new GMongo port ${currPort} ..."
*/
