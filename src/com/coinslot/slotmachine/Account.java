package com.coinslot.slotmachine;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

public class Account extends Verticle {

	public void start() throws Exception {
		vertx.eventBus()
				.registerHandler("get.credit", createGetCreditHandler());
		vertx.eventBus()
				.registerHandler("pull.lever", createPullLeverHandler());

	}

	private Handler<Message<JsonObject>> createGetCreditHandler() {
		return new Handler<Message<JsonObject>>() {
			public void handle(Message<JsonObject> request) {
				container.getLogger()
						.info("get.credit.request:" + request.body);

				String uuid = request.body.getString("uuid");

				// Read from somewhere else
				Integer credit = new Integer(10);
				vertx.sharedData().getMap("credits").put(uuid, credit);

				JsonObject response = new JsonObject().putNumber("credit",
						credit);
				request.reply(response);
			}

		};
	}

	private Handler<Message<JsonObject>> createPullLeverHandler() {
		return new Handler<Message<JsonObject>>() {
			public void handle(final Message<JsonObject> request) {
				//container.getLogger().info("request:" + request.body);
				final String uuid = request.body.getString("uuid");
				final int coins = (Integer) request.body.getNumber("coins");
				final int credit = (Integer) vertx.sharedData().getMap("credits")
						.get(uuid);
				
				//Check if there is credit
				if (credit < coins) { //No credit left
					JsonObject response = new JsonObject().putNumber("credit",
							credit).putString("error", "not enough credit!");
					request.reply(response);

				} else { //Credit left, lets spin the reels					
					vertx.eventBus().send("spin.reels", request.body,
							new Handler<Message<JsonObject>>() {
								public void handle(Message<JsonObject> resp) {
									//Adjust credit
									int finalCredit=credit-coins+(Integer)resp.body.getNumber("prize");
									vertx.sharedData().getMap("credits").put(uuid, finalCredit);
									
									JsonObject response=resp.body;
									response.putNumber("credit", finalCredit);
									request.reply(response);
								}
							});

				}
			}

		};
	}

}
