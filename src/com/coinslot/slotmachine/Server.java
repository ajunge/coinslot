package com.coinslot.slotmachine;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;


public class Server extends Verticle {

	public void start() throws Exception {
		JsonObject config = container.getConfig();
		container.deployVerticle(SlotMachine.class.getName(), config, 1);

		JsonArray inboundPermitted = new JsonArray();
		JsonObject inboundPermitted1 = new JsonObject().putString("address", "pull.lever");
		inboundPermitted.add(inboundPermitted1);
		
		JsonObject webServerConf = new JsonObject()
					.putNumber("port", 8080)
					.putBoolean("bridge", true)
					.putArray("inbound_permitted", inboundPermitted);
				
		
		container.deployModule("vertx.web-server-v1.0", webServerConf);
	}
	
	
}
