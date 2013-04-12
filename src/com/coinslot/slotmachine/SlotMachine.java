package com.coinslot.slotmachine;
import java.util.Random;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;


public class SlotMachine extends Verticle {
	Random random;
	
	public void start() throws Exception {
		random = new Random();
		vertx.eventBus().registerHandler("pull.lever", createPullLeverHandler());
		
	}

	private Handler<Message<JsonObject>> createPullLeverHandler() {		// TODO Auto-generated method stub
		return new Handler<Message<JsonObject>>(){
			public void handle(Message<JsonObject> request) {
				container.getLogger().info("request:"+request.body);
				
				//Write some slotmachine magic
				int probability=Integer.parseInt(request.body.getString("probability","10"));
				int randomNumber=random.nextInt(99);
				boolean win=(randomNumber>(100-probability));
				
				JsonObject response=new JsonObject()
					.putNumber("probability", probability)
					.putNumber("randomNumber", randomNumber)
					.putBoolean("win",win);
				
				request.reply(response);
			}
			
		};
	}

}
