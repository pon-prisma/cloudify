service {
	name "jbpm"
	icon "jbpm.png"
	type "WEB_SERVER"
	
	elastic = "$elastic"
	numInstances = "$numInstances"
	minAllowedInstances = "$minAllowedInstances"
	maxAllowedInstances = "$maxAllowedInstances"
	
	compute {
		template = "$template"
	}
	
	def currentPort = 8080
	
	lifecycle {
		install "jbpm_install.groovy"
		start "jbpm_start.groovy"
		
		startDetectionTimeoutSecs 800
		startDetection {
			ServiceUtils.isPortOccupied(currentPort)
		}
		
		details {
			def publicIP

			if (  context.isLocalCloud()  ) {
				publicIP =InetAddress.getLocalHost().getHostAddress()
			}
			else {
				if (!System.getenv()["CLOUDIFY_AGENT_ENV_PUBLIC_IP"]) {
					// Se CLOUDIFY_AGENT_ENV_PUBLIC_IP è vuoto prendo allora l'indirizzo IP privato.
					publicIP = System.getenv()["CLOUDIFY_AGENT_ENV_PRIVATE_IP"]
				} else {
					publicIP = System.getenv()["CLOUDIFY_AGENT_ENV_PUBLIC_IP"]
				}
			}

			def hostAndPort="http://${publicIP}:8080/jbpm-console"

			return [
				"ZZ01URL":"<a href=\"${hostAndPort}\" target=\"_blank\">${hostAndPort}</a>",
				"ZZ20User":"krisv",
				"ZZ21Password":"krisv"
			]
		}
	}
	
	network {
		port currentPort
		protocolDescription "HTTP"
		accessRules {
			incoming ([
				accessRule {
					type "PUBLIC"
					portRange currentPort
					target "0.0.0.0/0"
				}
			])
		}
	}
}

