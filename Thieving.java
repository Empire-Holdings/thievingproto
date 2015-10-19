package thievingproto;

import org.powerbot.script.*;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.GroundItem;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt4.Magic;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;
import org.powerbot.script.rt4.Npcs;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Bank.Amount;

import java.awt.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Script.Manifest(name = "thiev", description = "robbin hood")

public class Thieving extends PollingScript<ClientContext> implements MessageListener, PaintListener {
	private int[] manIds = {3078, 3080, 3087, 3257}; //245
	int silkId = 11729;
	int silk = 950;
	int lobster = 379;
	int chestId = 11736;
	GameObject natChest;
	Npc man;
	GameObject silkStall;
	private static final Tile SILK_TILE = new Tile(2663,3316, 0);
	private static final Area SILK_SAFE = new Area(new Tile(2655, 3325), new Tile(2657, 3328));
	private static final Area SILK_AREA = new Area(new Tile(2661, 3318), new Tile(2664, 3315));
	private static final Area SILK_BANK_FIRST = new Area(new Tile(2660, 3301), new Tile(2664, 3298));
	private static final Area SILK_BANK_SECOND = new Area(new Tile(2656, 3291), new Tile(2653, 3288));
	private static final Area SILK_BANK_AREA = new Area(new Tile(2652, 3286), new Tile(2654, 3282));
	private boolean silksafe = false;
	private static final int BANKBOOTHID = 11744;
	private static final Area BANK_AREA = new Area(new Tile(3092, 3245), new Tile(3094, 3242));
	private static final Area FARM_AREA = new Area(new Tile(3078, 3252), new Tile(3083, 3247));
	private static final Tile CHEST_TILE = new Tile(2672, 3301, 1);
	private GameObject booth;
	private int lobcount = -1;
	
	@Override
	public void poll() {
		
		if (!ctx.npcs.select().id(manIds).isEmpty()) {
			System.out.println("stealin");
			
			man = ctx.npcs.nearest().poll();
			if ((man.id() == 3078) || (man.id() == 3080)) {
				man.interact(false, "Pickpocket", "Man");
			} else if (man.id() == 3087) {
				man.interact(false, "Pickpocket", "Farmer");
			} else if (man.id() == 3257) {
				man.interact(false, "Pickpocket", "Master Farmer");
				Condition.sleep((int)(Math.random() * 1500));
				if ((ctx.players.local().health() < 10) && (ctx.players.local().health() > 0)) {
					System.out.println("health");
					eatfarm();
				}
			}
			man = null;
			try {
				Thread.sleep((long)(Math.random() * 1500));                //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			
		} else if (!ctx.objects.select().id(silkId).isEmpty()) {
			System.out.println("silk");
			
			silkStall = ctx.objects.nearest().poll();
			if (silkStall.inViewport()) {
				silkStall.interact("Steal");
			} else {
				if (SILK_AREA.contains(ctx.players.local().tile())) {
					System.out.println("contain");
				} else {
					ctx.movement.step(SILK_TILE);
				}
			}
			try {
				Thread.sleep((long)((Math.random() * 600)+500));                //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			
			if (ctx.inventory.select().count() == 28) {
				for (Item i : ctx.inventory.id(silk)) {
					i.interact("Drop");
				}
			}
			
			if ((ctx.players.local().health() < 10) && (ctx.players.local().health() > 0)) {
				System.out.println("health");
				eatsilk();
			}
		} else if (!ctx.objects.select().id(chestId).isEmpty()) {
			System.out.println("chest");
			
			natChest = ctx.objects.nearest().poll();
			if (natChest.inViewport()) {
				natChest.interact("Search for traps");
				try {
					Thread.sleep((long)((Math.random() * 500)+1000));                //1000 milliseconds is one second.
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
			} else {
				ctx.movement.step(CHEST_TILE);
				try {
					Thread.sleep((long)((Math.random() * 1500)+2000));                //1000 milliseconds is one second.
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
			}
			
		}
	}
	
	@Override
	public void messaged(MessageEvent e) {
		if (e.text().contains("You pick the")) {
			try {
				Thread.sleep((long)(Math.random() * 1600));                //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		} else if (e.text().contains("You've been")) {
			try {
				Thread.sleep((long)(Math.random() * 2600));                //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
	}
	
	@Override
	public void repaint(Graphics g) {
		g.setColor(Color.RED);
		if (man != null) {
			man.draw(g);
		}
	}
	
	private void safesilk() {
		ctx.movement.running(true);
		ctx.movement.step(SILK_SAFE.getRandomTile());
		Condition.sleep((int)((Math.random() * 4500)+8000));
		if (SILK_SAFE.contains(ctx.players.local().tile())) {
			ctx.movement.step(SILK_TILE);
		}
	}
	
	private void eatsilk() {
		for (Item i : ctx.inventory.id(lobster)) {
			i.interact("Eat");
			break;
		}
		if ((ctx.inventory.select().id(lobster).count() == 0) && (lobcount != 0)) {
			ctx.movement.running(true);
			ctx.movement.step(SILK_BANK_FIRST.getRandomTile());
			try {
				Thread.sleep((long)((Math.random() * 1000)+5500));              //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			ctx.movement.step(SILK_BANK_SECOND.getRandomTile());
			try {
				Thread.sleep((long)((Math.random() * 5500)+7200));              //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			ctx.movement.step(SILK_BANK_AREA.getRandomTile());
			try {
				Thread.sleep((long)((Math.random() * 4500)+9500));              //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			silksafe = true;
		} else {
			silksafe = false;
		}
		if (!silksafe) {
			safesilk();
		} else {
			bank();
		}
	}
	
	private void eatfarm() {
		for (Item i : ctx.inventory.id(lobster)) {
			i.interact("Eat");
			break;
		}
		if ((ctx.inventory.select().id(lobster).count() == 0) && (lobcount != 0)) {
			try {
				Thread.sleep((long)((Math.random() * 2500)+3500));              //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			ctx.movement.running(true);
			ctx.movement.step(BANK_AREA.getRandomTile());
			try {
				Thread.sleep((long)((Math.random() * 4500)+7500));              //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			bank();
			try {
				Thread.sleep((long)((Math.random() * 2500)+2500));              //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			ctx.movement.step(FARM_AREA.getRandomTile());
			try {
				Thread.sleep((long)((Math.random() * 1000)+1500));              //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			
		}
	}
	
	private void bank() {
		System.out.println("bank");
		if (booth == null || !booth.valid()) {
            System.out.println("Getting new banker");
            booth = ctx.objects.select().id(BANKBOOTHID).nearest().peek();
        }
        if (!ctx.bank.opened()) {
            if (!booth.inViewport())
                ctx.camera.turnTo(booth);
            booth.click();
            try {
				Thread.sleep((long)((Math.random() * 2500)+4000));              //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
        }
        if (ctx.bank.opened()) {
        	if (ctx.bank.select().id(lobster).count() == 0) {
        		ctx.bank.close();
        	}
        	else {
        		ctx.bank.withdraw(lobster, 6);
        	}
        }
        lobcount = ctx.inventory.select().id(lobster).count();
	}
}
