/*******************************************************************************
 * Copyright (c) 2012 GigaSpaces Technologies Ltd. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * __          ______  _____  _____  _____  _____  ______  _____ _____ 
 * \ \        / / __ \|  __ \|  __ \|  __ \|  __ \|  ____|/ ____/ ____|
 *  \ \  /\  / / |  | | |__) | |  | | |__) | |__) | |__  | (___| (___  
 *   \ \/  \/ /| |  | |  _  /| |  | |  ___/|  _  /|  __|  \___ \\___ \ 
 *    \  /\  / | |__| | | \ \| |__| | |    | | \ \| |____ ____) |___) |
 *     \/  \/   \____/|_|  \_\_____/|_|    |_|  \_\______|_____/_____/ 
 *    
 *******************************************************************************/
service {
	extend "apache"
	name "wordpress"
	icon "wordpress.png"

	elastic = "$elastic"
	numInstances = "$numInstances"
	minAllowedInstances = "$minAllowedInstances"
	maxAllowedInstances = "$maxAllowedInstances"
	
	compute {
		template = "$template"
	}

	lifecycle {
		postInstall "wordpress_postInstall.groovy"

		details {
			def publicIP

			if (  context.isLocalCloud()  ) {
				publicIP = InetAddress.getLocalHost().getHostAddress()
			}
			else {
				if (!System.getenv()["CLOUDIFY_AGENT_ENV_PUBLIC_IP"]) {
					// Se CLOUDIFY_AGENT_ENV_PUBLIC_IP è vuoto prendo allora l'indirizzo IP privato.
					publicIP = System.getenv()["CLOUDIFY_AGENT_ENV_PRIVATE_IP"]
				} else {
					publicIP = System.getenv()["CLOUDIFY_AGENT_ENV_PUBLIC_IP"]					
				}
				
			}

			def hostAndPort="http://${publicIP}/wp-admin/install.php"


			return [
				"ZZ10URL": "<a href=\"${hostAndPort}\" target=\"_blank\">${hostAndPort}</a>"				
			]
		}
	}
}