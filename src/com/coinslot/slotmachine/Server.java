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
		
		int port=8080;
		if(System.getenv("PORT")!=null && !System.getenv("PORT").equals("")){
			try{
				port=Integer.parseInt(System.getenv("PORT"));
			}catch (Exception e){
				port=8080;
			}
		}
		
		JsonObject webServerConf = new JsonObject()
					.putNumber("port", port )
					.putString("host", "0.0.0.0")
					.putBoolean("bridge", true)
					.putArray("inbound_permitted", inboundPermitted);
				
		
		container.deployModule("vertx.web-server-v1.0", webServerConf);
	}
	
	//.listen( System.getenv('PORT') as int, '0.0.0.0' )
}
