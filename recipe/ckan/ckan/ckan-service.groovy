service {
	name = "ckan"
	icon "ckan.png"
	type "WEB_SERVER"
	
	elastic = "$elastic"
	numInstances = "$numInstances"
	minAllowedInstances = "$minAllowedInstances"
	maxAllowedInstances = "$maxAllowedInstances"
	
	compute {
		template = "$template"
	}
	lifecycle {
	
		install "ckan_install.groovy"
		start "ckan_start.groovy"				  
		startDetection {
			return ServiceUtils.isPortOccupied(8080)
		}
		details {
			def publicIP
			
			if (  context.isLocalCloud()  ) {
				publicIP =InetAddress.getLocalHost().getHostAddress()
			}
			else {
				publicIP =System.getenv()["CLOUDIFY_AGENT_ENV_PUBLIC_IP"]
			}
				
			def hostAndPort="http://${publicIP}:8080"

			return [
				"URL":"<a href=\"${hostAndPort}\" target=\"_blank\">${hostAndPort}</a>"
				]
			}	
      }
      
      userInterface {
	metricGroups = ([
		metricGroup {

			name "PRISMA"

			metrics([
				"Total Process Cpu Time"					
			])
		} 
	])

	widgetGroups = ([								
		widgetGroup {
			name "Total Process Cpu Time"
			widgets([
				balanceGauge{metric = "Total Process Cpu Time"},
				barLineChart {
					metric "Total Process Cpu Time"
					axisYUnit Unit.REGULAR
				}
			])
		}
	])
	}
	scaleCooldownInSeconds 30
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


	network {
		port 8080
		protocolDescription "HTTP"
		accessRules {
			incoming ([
				accessRule {
					type "PUBLIC"
					portRange 8080
					target "0.0.0.0/0"
				}
			])
		}
	}
}