package nl.michaj.vmail.cmds;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.coloredcarrot.jsonapi.impl.JsonClickEvent;
import com.coloredcarrot.jsonapi.impl.JsonColor;
import com.coloredcarrot.jsonapi.impl.JsonMsg;

import nl.michaj.vmail.Main;

public class SpamCmd implements CommandExecutor {

	Main main = Main.getInstance();

	public SpamCmd(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
		if (s instanceof Player) {
			if (s.hasPermission("vmail.spam")) {
				Player p = (Player) s;
				if (args.length == 0) {
					msg(p, "&6-[vMail]-----------------------------------");
					msg(p, "&a/spam add <player>");
					msg(p, "&a/spam delete <player>");
					msg(p, "&a/spam read <id>");
					msg(p, "&a/spam del <id>");
					msg(p, "&a/spam reply <id>");
					msg(p, "&a/spam inbox");
					msg(p, "&6-[vMail]-----------------------------------");
				}
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("add"))
						msg(p, main.getPrefix() + "&c Usage: /spam add <player>");
					if (args[0].equalsIgnoreCase("del"))
						msg(p, main.getPrefix() + "&c Usage: /spam del <player>");
					if (args[0].equalsIgnoreCase("read"))
						msg(p, main.getPrefix() + "&c Usage: /spam read <id>");
					if (args[0].equalsIgnoreCase("inbox")) {
						File folder = new File(this.main.getSpamFolder(p).toString());
						File[] listOfFiles = folder.listFiles();
						msg(p, "&6-[Spam]-----------------------------------");
						if (listOfFiles.length > 0) {
							for (int i = 0; i < listOfFiles.length; i++) {
								p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 2.14748365E9F);
								JsonMsg read = new JsonMsg("[Read]", ChatColor.GREEN, ChatColor.BOLD);
								JsonMsg delete = new JsonMsg("[Delete]", ChatColor.RED, ChatColor.BOLD);
								JsonMsg id = new JsonMsg(listOfFiles[i].getName().replaceAll(".yml", ""),
										ChatColor.GREEN);
								JsonMsg from = new JsonMsg(" From: ", JsonColor.WHITE);
								JsonMsg spacer = new JsonMsg("     ");
								JsonMsg re = new JsonMsg("[RE] ", JsonColor.GRAY);
								JsonMsg fw = new JsonMsg("[FW] ", JsonColor.YELLOW);
								JsonMsg NEW = new JsonMsg(" NEW ", JsonColor.DARK_GREEN);
								JsonMsg text;
								read.clickEvent(JsonClickEvent
										.runCommand("/spam read " + listOfFiles[i].getName().replaceAll(".yml", "")));
								delete.clickEvent(JsonClickEvent
										.runCommand("/spam del " + listOfFiles[i].getName().replaceAll(".yml", "")));
								FileConfiguration mails = YamlConfiguration.loadConfiguration(listOfFiles[i]);
								if (mails.getBoolean("Mail.New") && mails.getBoolean("Mail.Reply") == false
										&& mails.getBoolean("Mail.Forward") == false) {
									text = new JsonMsg("ID: ").append(id).append(NEW).append(from)
											.append(" " + mails.getString("Mail.From") + " ").append(spacer)
											.append(read).append(" ").append(delete);
									text.send(p);
								}
								if (mails.getBoolean("Mail.New") && mails.getBoolean("Mail.Reply")
										&& mails.getBoolean("Mail.Forward") == false) {
									text = new JsonMsg("ID: ").append(id).append(NEW).append(from)
											.append(" " + mails.getString("Mail.From") + " ").append(re).append(read)
											.append(" ").append(delete);
									text.send(p);
								}
								if (mails.getBoolean("Mail.New") && mails.getBoolean("Mail.Reply") == false
										&& mails.getBoolean("Mail.Forward")) {
									text = new JsonMsg("ID: ").append(id).append(NEW).append(from)
											.append(" " + mails.getString("Mail.From") + " ").append(fw).append(read)
											.append(" ").append(delete);
									text.send(p);
								}
								if (mails.getBoolean("Mail.New") == false && mails.getBoolean("Mail.Reply") == false
										&& mails.getBoolean("Mails.Forward") == false) {
									text = new JsonMsg("ID: ").append(id).append(from)
											.append(" " + mails.getString("Mail.From") + " ").append(spacer)
											.append(read).append(" ").append(delete);
									text.send(p);
								}
								if (mails.getBoolean("Mail.New") == false && mails.getBoolean("Mail.Reply")
										&& mails.getBoolean("Mail.Forward") == false) {
									text = new JsonMsg("ID: ").append(id).append(from)
											.append(" " + mails.getString("Mail.From") + " ").append(re).append(read)
											.append(" ").append(delete);
									text.send(p);
								}
								if (mails.getBoolean("Mail.New") == false && mails.getBoolean("Mail.Reply") == false
										&& mails.getBoolean("Mail.Forward")) {
									text = new JsonMsg("ID: ").append(id).append(from)
											.append(" " + mails.getString("Mail.From") + " ").append(fw).append(read)
											.append(" ").append(delete);
									text.send(p);
								}
							}

						} else {
							msg(p, "&cYour Spambox is empty.");
						}
						msg(p, "&6-[Spam]-----------------------------------");
					}
				}
				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("add")) {
						OfflinePlayer tp = Bukkit.getPlayer(args[1]);
						try {
							main.addSpamPlayer(p, tp);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (args[0].equalsIgnoreCase("delete")) {
						OfflinePlayer tp = Bukkit.getPlayer(args[1]);
						try {
							main.delSpamPlayer(p, tp);
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
					if (args[0].equalsIgnoreCase("read")) {
						try {
							readMail(p, Integer.parseInt(args[1]));
						} catch (NumberFormatException | IOException e) {
							e.printStackTrace();
						}
					}
					if (args[0].equalsIgnoreCase("del")) {
						try {
							delMail(p, Integer.parseInt(args[1]));
						} catch (NumberFormatException | IOException e) {
							e.printStackTrace();
						}
					}

				}
				if (args.length > 2) {
					if (args[0].equalsIgnoreCase("reply")) {
						try {
							replyMail(p, Integer.parseInt(args[1]), args);
						} catch (NumberFormatException | IOException e) {
							e.printStackTrace();
						}
					}

				}
			}
		}
		return false;
	}

	public void delMail(Player p, int id) throws IOException {
		File mail = this.main.getSpamMailFile(p, id);
		if (mail.exists()) {
			mail.delete();
			if (!mail.exists()) {
				msg(p, this.main.getPrefix() + "&a Deleted spam mail succesfully");
			}
		}
	}

	public void readMail(Player p, int id) throws IOException {
		File mailFile = this.main.getSpamMailFile(p, id);
		if (mailFile.exists()) {
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(mailFile);

			cfg.set("Mail.New", false);
			cfg.options().copyDefaults(true);
			cfg.save(mailFile);

			JsonMsg text = new JsonMsg("[Reply]", ChatColor.GREEN, ChatColor.BOLD);
			JsonMsg text1 = new JsonMsg("[Delete]", ChatColor.RED, ChatColor.BOLD);
			JsonMsg text2 = new JsonMsg("[Forward]", ChatColor.YELLOW, ChatColor.BOLD);
			JsonMsg fullMsg;
			text.clickEvent(JsonClickEvent.suggestCommand("/spam reply " + id + " "));
			text1.clickEvent(JsonClickEvent.runCommand("/spam del " + id + " "));
			text2.clickEvent(JsonClickEvent.suggestCommand("/spam forward " + id + " "));
			msg(p, "&7========================================&r");
			msg(p, "&6From: &r" + cfg.getString("Mail.From"));
			msg(p, "&6Subject: &r" + cfg.getString("Mail.Subject"));
			msg(p, "&7========================================&r");
			if (cfg.getBoolean("Mail.Reply") == true) {
				msg(p, "&7From: " + cfg.getString("Mail.Replied.From") + "\n");
				msg(p, "&7----------------------------------------");
				msg(p, "&7" + cfg.getString("Mail.Replied.Message"));
				msg(p, "&7========================================&r");
			}
			if (cfg.getBoolean("Mail.Forward") == true) {
				msg(p, "&7" + cfg.getString("Mail.Forwarded.From"));
				msg(p, "&7" + cfg.getString("Mail.Forward.Message"));
				msg(p, "&7========================================&r");
			}
			msg(p, cfg.getString("Mail.Message"));
			msg(p, "&7========================================&r");
			fullMsg = new JsonMsg("").append(text).append(" ").append(text2).append(" ").append(text1);
			fullMsg.send(p);
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BELL, 1.0F, 2.14748365E9F);
		}
	}

	public void replyMail(Player p, int id, String[] args) throws IOException {
		FileConfiguration cfg = main.getPlayerMailCfg(p, id);
		OfflinePlayer tp = Bukkit.getPlayer(cfg.getString("Mail.From"));
		if (main.getPlayerFromSpamFile((Player) tp, p) == null || !(main.getPlayerFromSpamFile((Player) tp, p))) {
			main.createMail(p, tp, id, false, true, true, cfg.getString("Mail.Replied.Message"),
					cfg.getString("Mail.Replied.From"), null, null, cfg.getString("Mail.Subject"), args);
			main.createSentMail(p, tp, id, false, true, true, cfg.getString("Mail.Replied.Message"),
					cfg.getString("Mail.Replied.From"), null, null, cfg.getString("Mail.Subject"), args);
		} else if (main.getPlayerFromSpamFile((Player) tp, p) != null && main.getPlayerFromSpamFile((Player) tp, p)) {
			main.createSpamMail(p, tp, id, false, true, true, cfg.getString("Mail.Replied.Message"),
					cfg.getString("Mail.Replied.From"), null, null, cfg.getString("Mail.Subject"), args);
			p.playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0F, 1.0F);
			main.createSentMail(p, tp, id, false, true, true, cfg.getString("Mail.Replied.Message"),
					cfg.getString("Mail.Replied.From"), null, null, cfg.getString("Mail.Subject"), args);
		}
	}

	public void msg(Player p, String msg) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}

}
