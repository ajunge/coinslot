package com.coinslot.slotmachine;

import java.util.Random;
import java.util.Vector;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

public class SlotMachine extends Verticle {
	Random random;

	public void start() throws Exception {
		random = new Random();
		vertx.setTimer(0, createRandomHandler());
		vertx.eventBus()
				.registerHandler("spin.reels", createSpinReelsHandler());

	}

	private Handler<Message<JsonObject>> createSpinReelsHandler() {
		return new Handler<Message<JsonObject>>() {
			public void handle(Message<JsonObject> request) {
				//container.getLogger().info("request:" + request.body);
				int coins=(Integer) request.body.getNumber("coins");
				
				JsonObject response = new JsonObject();

				int reels=3; //TODO: Configurable maybe?
				int iconsOnReel=10;
				//Spin every reel
				Vector<Integer> r=new Vector<Integer>(reels);
				for(int i=0; i<reels;i++){
					int randomNumber = random.nextInt(iconsOnReel-1);
					r.add(i, new Integer(randomNumber));
					response.putNumber("REEL"+i, randomNumber);
				}
				
				int prize=getPrize(r,coins);
				response.putNumber("prize", prize);
				
				boolean win=(prize>0);
				response.putBoolean("win", win);
				
				request.reply(response);
			}

		};
	}
	
	private int getPrize(Vector<Integer> r,int coins){
		if(r.get(0).equals(r.get(1)) && r.get(0).equals(r.get(2))){
			//All equals. Probability 10*(1/10*1/10*1/10)=1/100 
			//Prize 90
			return 90;
		}
		if(r.get(0).equals(r.get(1)) || r.get(1).equals(r.get(2))){
			//XXY or YXX. Probability 10*(1/10*1/10*9/10)=9/100
			//Prize
			return 9;
		}
		return 0;
	}

	/*
	 * Just to add more enthropy to the randomizer
	 */
	private Handler<Long> createRandomHandler() {
		return new Handler<Long>() {
			public void handle(Long arg0) {
				int randomSec=random.nextInt(1000);
				//container.getLogger().info("randomSec:" + randomSec);
				vertx.setTimer(randomSec, createRandomHandler());
			}
		};

	}
}
