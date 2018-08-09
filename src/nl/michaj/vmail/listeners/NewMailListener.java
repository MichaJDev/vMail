package nl.michaj.vmail.listeners;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import nl.michaj.vmail.Main;

public class NewMailListener implements Listener {

	Main main = Main.getInstance();

	public NewMailListener(Main main) {
		this.main = main;
	}

	@EventHandler
	public void onPlayerCreateSpamFolder(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (main.getSpamFile(p).exists()) {
			try {
				main.createSpamList(p);
			} catch (NullPointerException | IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		this.main.getPlayerFolder(p);
		File folder = this.main.getMailFolder(p);
		File[] listOfFiles = folder.listFiles();
		int id = 0;
		for (int i = 0; i < listOfFiles.length; i++) {
			FileConfiguration mails = this.main.getPlayerMailCfg(p,
					Integer.parseInt(listOfFiles[i].getName().replaceAll(".yml", "")));
			if (mails.getBoolean("Mail.New") == true) {
				id++;
			}
		}
		if (id > 0) {
			this.main.msg(p, "&6 -[vMails]---------------------------");
			this.main.msg(p, "&6 you have &a" + id + "&6 new emails");
			this.main.msg(p, "&6 use &a/mail inbox &6to check out from whome!");
			this.main.msg(p, "&6 ----------------------------------");
			this.main.getLogger().log(Level.INFO, p.getName() + " has " + id + " new Mails");
		} else {
			this.main.msg(p, "&6 -[vMails]---------------------------");
			this.main.msg(p, "&6 you have &a" + id + "&6 new emails");
			this.main.msg(p, "&6 ----------------------------------");
			this.main.getLogger().log(Level.INFO, p.getName() + " has " + id + " new Mails");
		}
	}

}
