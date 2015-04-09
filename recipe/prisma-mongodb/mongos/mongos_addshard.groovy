@Grab(group='com.gmongo', module='gmongo', version='0.8')
import com.gmongo.GMongo
import org.cloudifysource.utilitydomain.context.ServiceContextFactory
import java.util.concurrent.TimeUnit

def mongodHost = args[0]
def mongodPort= args[1]

println "mongos_addshard.groovy: mongod host and port = ${mongodHost}:${mongodPort}"


serviceContext = ServiceContextFactory.getServiceContext()

println "mongos_addshard.groovy: serviceContext object is " + serviceContext

currPort = serviceContext.attributes.thisInstance["port"] as int
println "mongos_addshard.groovy: Connecting to mongos on port ${currPort} ..."

mongo = new GMongo("127.0.0.1", currPort)
	
println "mongos_addshard.groovy: After new GMongo port ${currPort} ..."
	
db = mongo.getDB("admin")
assert db != null
println "Connection succeeded"
	
	

println "mongos_addshard.groovy: mongod host and port = ${mongodHost}:${mongodPort}"
result = db.command(["addshard":"${mongodHost}:${mongodPort}"])
println "mongos_addshard.groovy: db result: ${result}"

	
	
result = db.command(["enablesharding":"petclinic"])
println "mongos_addshard.groovy: db result: ${result}"
	
result = db.command(["shardcollection":"petclinic.Person", "key":["_id":1]])
println "mongos_addshard.groovy: db result: ${result}"
	

 


