package nl.michaj.vmail;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.coloredcarrot.jsonapi.impl.JsonClickEvent;
import com.coloredcarrot.jsonapi.impl.JsonMsg;

import nl.michaj.vmail.cmds.MailCmd;
import nl.michaj.vmail.cmds.SpamCmd;
import nl.michaj.vmail.listeners.NewMailListener;

public class Main extends JavaPlugin {

	static Main main;
	File file = new File(getDataFolder(), "/Players/");

	public static Main getInstance() {
		return main;
	}

	@Override
	public void onEnable() {
		if (!file.exists()) {
			createPlayersFolder();
		}
		try {
			getLogger().log(Level.INFO, "creating Players File.");
			createPlayersFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		createSpamFoldersOnFirstStart();
		getListeners();
		getCommands();
	}

	@Override
	public void onDisable() {
		saveConfig();
		countFiles();
	}

	public void getCommands() {
		getCommand("mail").setExecutor(new MailCmd(this));
		getCommand("spam").setExecutor(new SpamCmd(this));
		// getCommand("sent").setExecutor(new SentCmd(this));
	}

	public void getListeners() {
		getServer().getPluginManager().registerEvents(new NewMailListener(this), this);
	}

	public void countFiles() {
		getLogger().log(Level.INFO, "Files in Plugin directory: " + getFilesCount(getDataFolder()));
	}

	public static int getFilesCount(File file) {
		File[] files = file.listFiles();
		int count = 0;
		for (File f : files)
			if (f.isDirectory())
				count += getFilesCount(f);
			else
				count++;

		return count;
	}

	public void createSpamFoldersOnFirstStart() {
		getLogger().log(Level.INFO, "Adding SpamFolders to every player");
		File[] directories = new File(getDataFolder(), "/Players/").listFiles(File::isDirectory);
		for (int i = 0; i < directories.length; i++) {
			boolean spamFolderExists = new File(directories[i], "/Spam/").exists();
			if (!spamFolderExists) {
				File newFile = new File(directories[i], "spamList.yml");
				try {
					newFile.createNewFile();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				File file = new File(directories[i], "/Spam/");
				file.mkdirs();
			}

		}

	}

	public void createSentFoldersOnFirstStart() {
		File[] directories = new File(getDataFolder(), "/Players/").listFiles(File::isDirectory);
		for (int i = 0; i < directories.length; i++) {
			boolean sentFolderExists = new File(directories[i], "/Sent/").exists();
			if (!sentFolderExists) {
				File file = new File(directories[i], "/Sent/");
				file.mkdirs();
			}
		}
	}

	public void createSentFolder(Player p) {
		File SentFolder = new File(getPlayerFolder(p), "/Sent/");
		SentFolder.mkdirs();
	}

	public File getSentFolder(Player p) {
		File SentFolder = new File(getPlayerFolder(p), "/Sent/");
		if (!SentFolder.exists()) {
			createSentFolder(p);
			return SentFolder;
		}
		return SentFolder;
	}

	public void createSentMail(Player p, int id) throws IOException {
		File file = new File(getSentFolder(p), id + ".yml");
		if (!file.exists()) {
			file.createNewFile();
		}
	}

	public File getSentMail(Player p, int id) {
		File file = new File(getSentFolder(p), id + ".yml");
		if (!file.exists()) {
			msg(p, getPrefix() + "&c Couldn't find that mail!");
			return null;
		}
		return file;
	}

	public FileConfiguration getSentCfg(Player p, int id) {
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(getSentMail(p, id));
		if (cfg == null) {
			msg(p, getPrefix() + "&c Something went wrong!");
		}
		return cfg;
	}

	public void createSpamList(Player p) throws NullPointerException, IOException {
		File spamFile = new File(getPlayerFolder(p), "spamList.yml");
		if (!spamFile.exists()) {
			createSpamFolder(p);
			spamFile.createNewFile();
		}
	}

	public File getSpamFile(Player p) throws NullPointerException {
		File spamFile = new File(getPlayerFolder(p), "spamList.yml");
		if (!spamFile.exists()) {
			main.getLogger().log(Level.INFO, "Couldn't get players spam list.");
			return null;
		}
		return spamFile;
	}

	public FileConfiguration getSpamCfg(Player p) {
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(getSpamFile(p));
		if (cfg == null) {
			getLogger().log(Level.INFO, "Couldn't find cfg file");
			return null;
		}
		return cfg;
	}

	public void createSpamFolder(Player p) {
		getLogger().log(Level.INFO, "Creating Spam Folder");
		File folder = new File(getPlayerFolder(p), "/Spam/");
		folder.mkdirs();
	}

	public File getSpamFolder(Player p) {
		File folder = new File(getPlayerFolder(p), "/Spam/");
		if (!folder.exists()) {
			getLogger().log(Level.INFO, "Couldn't receive spamfolder");
			return null;
		}
		return folder;
	}

	public File getSpamMailFile(Player p, int id) {

		File file = new File(getSpamFolder(p), id + ".yml");
		if (!file.exists()) {
			getLogger().warning("Specific file doesnt exist");
		}
		return file;

	}

	public void delSpamPlayer(Player p, OfflinePlayer tp) throws IOException {
		FileConfiguration cfg = getSpamCfg(p);
		if (cfg.contains("Players." + tp.getUniqueId().toString())) {
			msg(p, getPrefix() + "&c Player succesfully deleted from spamList");
			cfg.set("Players." + tp.getUniqueId().toString(), false);
			cfg.options().copyDefaults(true);
			cfg.save(getSpamFile(p));
		} else {
			msg(p, getPrefix() + "&c Player not found in spamList");
			cfg.set("Players." + tp.getUniqueId().toString(), false);
			cfg.options().copyDefaults(true);
			cfg.save(getSpamFile(p));
		}
	}

	public void addSpamPlayer(Player p, OfflinePlayer tp) throws IOException {
		FileConfiguration cfg = getSpamCfg(p);
		if (cfg.contains("Players." + tp.getUniqueId().toString())) {
			msg(p, getPrefix() + "&a Player succefully added to spamList!");
			cfg.set("Players." + tp.getUniqueId().toString(), true);
			cfg.options().copyDefaults(true);
			cfg.save(getSpamFile(p));
		} else {
			msg(p, getPrefix() + "&a Player succefully added to spamList!");
			cfg.addDefault("Players." + tp.getUniqueId().toString(), true);
			cfg.options().copyDefaults(true);
			cfg.save(getSpamFile(p));
		}
	}

	public Boolean getPlayerFromSpamFile(Player p, OfflinePlayer target) {
		FileConfiguration cfg = getSpamCfg(p);
		if (!cfg.contains(target.getUniqueId().toString())) {
			msg(p, getPrefix() + "&c Couldn't find player");
			return null;
		}
		return cfg.getBoolean(target.getUniqueId().toString());
	}

	public FileConfiguration getSpamMailCfg(Player p, int id) throws NullPointerException {
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(getSpamMailFile(p, id));
		return cfg;
	}

	public void createPlayersFile() throws IOException, NullPointerException {
		File playersFile = new File(getDataFolder(), "players.yml");
		if (!playersFile.exists()) {
			playersFile.createNewFile();
		}
	}

	public void createPlayersFolder() {
		File playersFolder = new File(getDataFolder(), "/Players/");
		playersFolder.mkdirs();
	}

	public void createPlayerFolder(Player p) {
		File playerFolder = new File(getPlayersFolder(), p.getUniqueId() + "/");

		playerFolder.mkdirs();
		File mailFolder = new File(playerFolder, "Mails/");
		mailFolder.mkdirs();
	}

	public File getPlayerFolder(Player p) throws NullPointerException {

		File playerFolder = new File(getPlayersFolder(), p.getUniqueId() + "/");
		if (!playerFolder.exists()) {
			getLogger().log(Level.WARNING,
					"Couldnt find player folder for: " + p.getName() + " creating necessary Folders.");
			createPlayerFolder(p);
			createMailFolder(p);
		}
		return playerFolder;
	}

	public File getPlayersFolder() {
		File file = new File(getDataFolder(), "/Players/");
		return file;

	}

	public File getMailFolder(Player p) {
		File mailFolder = new File(getPlayerFolder(p), "Mails/");
		return mailFolder;
	}

	public void createMailFolder(Player p) {
		File mailFolder = new File(getPlayerFolder(p), "Mails/");
		mailFolder.mkdirs();

	}

	public void createPlayerMailFile(Player p, int id) {
		try {
			File file = new File(getMailFolder(p), id + ".yml");
			file.createNewFile();
		} catch (IOException e) {
			getLogger().warning("Couldnt create Mail file");
			e.printStackTrace();
		}
	}

	public File getPlayerMailFile(Player p, int id) {
		File file = new File(getMailFolder(p), id + ".yml");
		if (!file.exists()) {
			getLogger().warning("Specific file doesnt exist");
			msg(p, "&8[&6vMail&8]" + "&c Couldnt find mail with that ID");
			return null;
		}
		return file;
	}

	public FileConfiguration getPlayerMailCfg(Player p, int id) {
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(getPlayerMailFile(p, id));
		return cfg;
	}

	public void msg(Player p, String msg) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}

	public String getPrefix() {
		return "&8[&6vMail&8]&r";
	}

	public void createSpamMail(Player tp, int id) {
		try {
			File file = new File(getSpamFolder(tp), id + ".yml");
			file.createNewFile();
		} catch (IOException e) {
			getLogger().warning("Couldnt create SpamMail file");
			e.printStackTrace();
		}
	}

	public void createMail(Player p, OfflinePlayer to, int id, boolean New, boolean forward, boolean reply,
			String forwarded, String forwardedFrom, String replied, String repliedFrom, String Subject, String[] args)
			throws IOException {
		if (to != null) {
			if (forward == false && reply == false) {
				createPlayerMailFile((Player) to, id);
				File mailFile = getPlayerMailFile((Player) to, id);
				if (mailFile.exists()) {
					FileConfiguration cfg = getPlayerMailCfg((Player) to, id);
					StringBuilder message = new StringBuilder();
					for (int i = 3; i < args.length; i++) {
						message.append(args[i]);
						message.append(" ");
					}
					String msg = message.toString();
					cfg.addDefault("Mail.Reply", reply);
					cfg.addDefault("Mail.New", New);
					cfg.addDefault("Mail.Forward", forwarded);
					cfg.addDefault("Mail.From", p.getName());
					cfg.addDefault("Mail.Subject", Subject);
					cfg.addDefault("Mail.Replied.From", repliedFrom);
					cfg.addDefault("Mail.Replied.Message", replied);
					cfg.addDefault("Mail.Forwarded.From", forwardedFrom);
					cfg.addDefault("Mail.Forwarded.Message", forwarded);
					cfg.addDefault("Mail.Message", msg);
					cfg.options().copyDefaults(true);
					cfg.save(mailFile);
					if (mailFile.exists() && mailFile.length() != 0) {
						JsonMsg read = new JsonMsg("[READ]", ChatColor.GREEN, ChatColor.BOLD);
						read.clickEvent(JsonClickEvent.runCommand("/mail read " + id));
						msg(p, main.getPrefix() + "&a Mail succesfully sent!");
						read.send(p);
						((Player) to).playSound(((Player) to).getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0F,
								2.14748365E9F);
						p.playSound(p.getLocation(), Sound.ENTITY_ENDEREYE_LAUNCH, 1.0F, 2.14748365E9F);
						getLogger().log(Level.INFO, p.getName() + " sent mail to: " + to.getName());
					}
				}
			}
		}
	}

	public void createSentMail(Player p, OfflinePlayer to, int idNumber, boolean New, boolean forward, boolean reply,
			String forwarded, String forwardedFrom, String replied, String repliedFrom, String Subject, String[] args)
			throws IOException {
		if (to != null) {
			File mailFolder = getMailFolder(p);
			File[] mailList = mailFolder.listFiles();
			int id = mailList.length + 1;
			createSentMail(p, id);
			File mailFile = getSentMail(p, id);
			if (mailFile.exists()) {
				FileConfiguration cfg = getSentCfg(p, id);
				StringBuilder message = new StringBuilder();
				for (int i = 3; i < args.length; i++) {
					message.append(args[i]);
					message.append(" ");
				}
				String msg = message.toString();
				cfg.addDefault("Mail.Reply", reply);
				cfg.addDefault("Mail.New", New);
				cfg.addDefault("Mail.Forward", forwarded);
				cfg.addDefault("Mail.From", p.getName());
				cfg.addDefault("Mail.Subject", Subject);
				cfg.addDefault("Mail.Replied.From", repliedFrom);
				cfg.addDefault("Mail.Replied.Message", replied);
				cfg.addDefault("Mail.Forwarded.From", forwardedFrom);
				cfg.addDefault("Mail.Forwarded.Message", forwarded);
				cfg.addDefault("Mail.Message", msg);
				cfg.options().copyDefaults(true);
				cfg.save(mailFile);
				if (mailFile.exists() && mailFile.length() != 0) {
					msg(p, main.getPrefix() + "&a Mail added to SENT inbox!");
					p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 2.14748365E9F);
					p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 2.2F);
					p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 2.14748365E9F);
					p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 2.2F);
				}
			}
		}
	}

	public void createSpamMail(Player p, OfflinePlayer to, int idNumber, boolean New, boolean forward, boolean reply,
			String forwarded, String forwardedFrom, String replied, String repliedFrom, String Subject, String[] args)
			throws IOException {
		if (to != null) {
			File mailFolder = getSpamFolder((Player) to);
			File[] mailList = mailFolder.listFiles();
			int id = mailList.length + 1;
			createSpamMail((Player) to, id);
			File spamFile = getSpamFile((Player) to);
			if (spamFile.exists()) {
				FileConfiguration cfg = getSpamMailCfg((Player) to, id);
				StringBuilder message = new StringBuilder();
				for (int i = 3; i < args.length; i++) {
					message.append(args[i]);
					message.append(" ");
				}
				String msg = message.toString();
				cfg.addDefault("Mail.Reply", reply);
				cfg.addDefault("Mail.New", New);
				cfg.addDefault("Mail.Forward", forwarded);
				cfg.addDefault("Mail.From", p.getName());
				cfg.addDefault("Mail.Subject", Subject);
				cfg.addDefault("Mail.Replied.From", repliedFrom);
				cfg.addDefault("Mail.Replied.Message", replied);
				cfg.addDefault("Mail.Forwarded.From", forwardedFrom);
				cfg.addDefault("Mail.Forwarded.Message", forwarded);
				cfg.addDefault("Mail.Message", msg);
				cfg.options().copyDefaults(true);
				cfg.save(spamFile);
				if (spamFile.exists() && spamFile.length() != 0) {
					((Player) to).playSound(((Player) to).getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0F, 1.0F);
				}
			}
		}
	}

}
