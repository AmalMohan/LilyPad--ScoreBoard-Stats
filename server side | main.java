package me.shawshark.totalonlinescoreboard;

import java.io.UnsupportedEncodingException;
import java.util.Collections;

import lilypad.client.connect.api.Connect;
import lilypad.client.connect.api.request.RequestException;
import lilypad.client.connect.api.request.impl.MessageRequest;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class main extends JavaPlugin implements Listener {
	
	Connect connect;
	String channel = "LilyTotalOnline";
	BukkitTask task;
	String servername;
	boolean status;
	
	public void onEnable() {
	    loadPlugin();
	}
	
	public void loadPlugin() {
		try {
			/* LilyPad */
			connect = Bukkit.getServer().getServicesManager().getRegistration(Connect.class).getProvider();
			servername = connect.getSettings().getUsername();
			
			startTask();
			status = true;
			
		} catch(Exception ex) {
			ex.printStackTrace();
			status = false;
		}
	}
	
	public void onDisable() {
		disablePlugin();
	}
	
	public void disablePlugin() {
		try {
			
			if(status) {
				
				/* Cancel the repeating task. */
				task.cancel();
				
				/* send 0 status to hub. */
				send(0);
				
				/* Set everything to null. */
				connect = null;
				task = null;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void startTask() {
		task = new BukkitRunnable() {
			@Override
			public void run() {
				send(Bukkit.getOnlinePlayers().length);
			}
		}.runTaskTimer(this, 40, 40);
	}
	
	public void send(int amount) {
		try {
			MessageRequest request = new MessageRequest(Collections.<String> emptyList(), channel, servername + "0/" + amount);
			connect.request(request); 
	    } catch (UnsupportedEncodingException | RequestException e1) { 
	    	e1.printStackTrace();
	    }
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("refresh")) {
			send(Bukkit.getOnlinePlayers().length);
			return true;
		}
		return false;
	}
}
