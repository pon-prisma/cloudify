import org.cloudifysource.utilitydomain.context.ServiceContextFactory

import java.util.concurrent.TimeUnit
println "mongod_poststart.groovy: inizio il postStart..."


serviceContext = ServiceContextFactory.getServiceContext()

mongosService = serviceContext.waitForService("mongos", 20, TimeUnit.SECONDS)

if (mongosService == null) {
	throw new IllegalStateException("mongos service not found. mongos must be installed before mongos.");
}

mongodService = serviceContext.waitForService("mongod", 20, TimeUnit.SECONDS)
if (mongodService == null) {
		throw new IllegalStateException("mongod service not found. mongod must be installed before mongos.");
}
println "mongod_poststart.groovy: Attendo 120 secondi fino a quando non ho le istanze pianificate pronte."
mongodHostInstances = mongodService.waitForInstances(mongodService.numberOfPlannedInstances, 120, TimeUnit.SECONDS)
println "mongod_poststart.groovy: Numero di istanze trovate"

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
