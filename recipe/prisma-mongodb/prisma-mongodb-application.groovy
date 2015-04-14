application {
	name="prisma-mongodb"
	
	service {
		name = "mongod"
		dependsOn = ["mongos",]		
	}
	
	service {
		name = "mongoConfig"		
	}
			
	service {
		name = "mongos"
		dependsOn = ["mongoConfig",]
	}
}