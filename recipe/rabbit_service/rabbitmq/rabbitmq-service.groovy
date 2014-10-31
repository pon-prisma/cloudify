service {
	
	name "rabbitmq"
	icon "rabbitmq.png"
	type "MESSAGE_BUS"
	
	elastic true
	numInstances = "$numInstances"
	minAllowedInstances 1
	maxAllowedInstances = "$instanceNumber"
	
	compute 
	{
		template = "$template"
	}
	
	lifecycle
	{

		details 
		{
			def config = new ConfigSlurper().parse(new File("${context.serviceDirectory}/rabbitmq-service.properties").toURL())

			def instanceID=context.instanceId
			println "rabbitmq-service.groovy: instanceID is ${instanceID}"

			
			def currPublicIP
			
			if (context.isLocalCloud()) 
			{
				currPublicIP =InetAddress.getLocalHost().getHostAddress()
			}
				else 
				{
					if (!System.getenv()["CLOUDIFY_AGENT_ENV_PUBLIC_IP"]) 
					{
						// Se CLOUDIFY_AGENT_ENV_PUBLIC_IP è vuoto prendo allora l'indirizzo IP privato.
						currPublicIP = System.getenv()["CLOUDIFY_AGENT_ENV_PRIVATE_IP"]
					} 
					else 
						{

							currPublicIP = System.getenv()["CLOUDIFY_AGENT_ENV_PUBLIC_IP"]					
						}
				}
			

			def vmUuid = System.getenv()["CLOUDIFY_CLOUD_MACHINE_ID"]
			def vmName =  InetAddress.getLocalHost().getHostName()
			def serviceType = "[" + "rabbitmq" + "," + "rabbitmq2" + "]"		
		
			def portIncrement =  context.isLocalCloud() ? context.instanceId-1 : 0
			
			def mgmtPort = config.mgmtPort + portIncrement
			def port = config.port

			
			def mgmtURL = "http://${currPublicIP}:${mgmtPort}"
			def appURL = "${currPublicIP}:${port}"

			println "rabbitmq-service.groovy: Management URL is ${mgmtURL}"


			context.attributes.thisInstance["vmUuid"] = "${vmUuid}"
			context.attributes.thisInstance["vmIp"] = "${currPublicIP}"
			context.attributes.thisInstance["vmName"] = "${vmName}"
			context.attributes.thisInstance["serviceType"] = "${serviceType}"
			context.attributes.thisInstance["managementURL"] = "${mgmtURL}"
			context.attributes.thisInstance["applicationURL"] = "${appURL}"		
	
			return ["Management URL": "<a href=\"${mgmtURL}\" target=\"_blank\">${mgmtURL}</a>",
					"Application URL":"${appURL}",					
					"mgmtPort":"${mgmtPort}",
					"port":"${port}",
					"vmUuid":"${vmUuid}",
					"vmIp":"${currPublicIP}",
					"vmName":"${vmName}",
					"serviceType":"${serviceType}"]
		}
		
		install "rabbitmq_install.groovy"
		
		postInstall "rabbitmq_postInstall.groovy"
		
		preStart "rabbitmq_preStart.groovy"
		
		start "rabbitmq_start.groovy"

	
		postStart "rabbitmq_postStart.groovy"
		
		startDetectionTimeoutSecs 10000
		startDetection 
		{
			if(context.attributes.thisInstance["port"] != null)
			{
				return ServiceUtils.isPortOccupied(context.attributes.thisInstance["port"])
			} 
			else 
			{
				println "Port is not assigned yet (i.e., null)."
				return false
			}
		}
		
		stopDetection 
		{
			boolean hasStoped = !ServiceUtils.isPortOccupied(context.attributes.thisInstance["port"])
			return hasStoped
		}
		
		locator 
		{
			def rabbitNodeName = "rabbit"
			if (context.isLocalCloud()) 
			{
				rabbitNodeName = context.attributes.thisInstance["rabbitNodeName"]
			}
			def fullRabbitNodeName = rabbitNodeName + "@" + context.attributes.thisInstance["hostname"]
			def rabbitmqPids = ServiceUtils.ProcessUtils.getPidsWithQuery("State.Name.eq=beam,Args.*.eq=${fullRabbitNodeName},Args.*.eq=-sname")
			println "rabbitmq-service.groovy: current PIDs: ${rabbitmqPids}"
			return rabbitmqPids
		}
		
		preStop "rabbitmq_preStop.groovy"
		
	}


	/*prova porte ascolto - giuseppe*/
	network 
	{
		port = mgmtPort
		protocolDescription = "AMQP"
		accessRules {
			incoming ([
				accessRule {
					type "PUBLIC"
					portRange mgmtPort
					target "0.0.0.0/0"
				}
			])
		}
	}
	
	customCommands ([
		/*
			This custom command adds a host file entry to the hosts file of the machine running this rabbitmq node. 
			It enables a new rabbitmq instance to inform its hostname and ip address to all other existing instances 
			and add an host file entry to their hosts file, so that the new node's hostname can be resovled on all 
			instances in the cluster.
			Usage :  invoke rabbitmq addHostFileEntry host_file_entry
			Example: invoke rabbitmq addHostFileEntry "172.18.48.156 rabbit10"
		*/
	
		"addHostFileEntry" : "add_hostfile_entry.groovy"
	])


/* ---DECOMMENTARE	
	userInterface 
	{
		
		metricGroups = ([
			metricGroup
			{
		
				name "process"
		
				metrics([
					"Total Process Cpu Time",
					"Process Cpu Kernel Time",
					"Total Process Residential Memory",
					"Total Num of Page Faults"
				])
			}
		]
		)
		
		widgetGroups = ([
			widgetGroup 
			{
				name "Total Process Cpu Time"
				widgets([
					balanceGauge{metric = "Total Process Cpu Time"},
					barLineChart {
						metric "Total Process Cpu Time"
						axisYUnit Unit.REGULAR
					}
				])
			},
			widgetGroup 
			{
				name "Process Cpu Kernel Time"
				widgets([
					balanceGauge{metric = "Process Cpu Kernel Time"},
					barLineChart {
						metric "Process Cpu Kernel Time"
						axisYUnit Unit.REGULAR
					}
				])
			},
			widgetGroup 
			{
				name "Total Process Residential Memory"
				widgets([
					balanceGauge{metric = "Total Process Residential Memory"},
					barLineChart {
						metric "Total Process Residential Memory"
						axisYUnit Unit.REGULAR
					}
				])
			},
			widgetGroup 
			{
				name "Total Num of Page Faults"
				widgets([
					balanceGauge{metric = "Total Num of Page Faults"},
					barLineChart {
						metric "Total Num of Page Faults"
						axisYUnit Unit.REGULAR
					}
				])
			}
		]
		)
	}
	
	scaleCooldownInSeconds 20
	samplingPeriodInSeconds 1
		
	scalingRules ([
		scalingRule {

			serviceStatistics {
				metric "Total Process Cpu Time"
				timeStatistics Statistics.averageCpuPercentage
				instancesStatistics Statistics.maximum
				movingTimeRangeInSeconds 20
			}

			highThreshold {
				value 40
				instancesIncrease 1
			}

			lowThreshold {
				value 25
				instancesDecrease 1
			}
		}
	])

*/
	
}