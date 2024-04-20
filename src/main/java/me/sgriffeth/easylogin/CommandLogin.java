package me.sgriffeth.easylogin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;

public class CommandLogin implements CommandExecutor {

    private File dataFolder;
    private File whitelist;

    private Main mainClass;

    public CommandLogin(Main mainClass) {
        this.mainClass = mainClass;
        dataFolder = new File(mainClass.getDataFolder().getName());
        dataFolder.mkdir();
        
        whitelist = new File(mainClass.getDataFolder().getName() + "/" + "Whitelist");
        //LOGGER.info("Creating file at: " + getDataFolder().getName() + "/" + "Whitelist");
        try {
            if(!whitelist.exists())
            whitelist.createNewFile();
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    boolean isWhitelisted(String name) throws FileNotFoundException, IOException {
        FileInputStream fstream = null;
        fstream = new FileInputStream(whitelist);    
        
        BufferedReader infile = new BufferedReader(new InputStreamReader(
                fstream));
        String data;
        while((data = infile.readLine()) != null) {
            if(data.equals(name)) {
                return true;
            }
        }
        return false;
    }

    boolean hasRegistered(String name) throws FileNotFoundException, IOException {
        File file = new File(mainClass.getDataFolder().getName() + "/" + name + "_digest");
        if(file.exists()) return true;
        return false;
    }

    void registerUser(String name, String passwd) throws IOException, NoSuchAlgorithmException {
        File digestFile = new File(mainClass.getDataFolder().getName() + "/" + name + "_digest");
        digestFile.createNewFile();
        File saltFile = new File(mainClass.getDataFolder().getName() + "/" + name + "_salt");
        saltFile.createNewFile();

        MessageDigest md = null;
        md = MessageDigest.getInstance("SHA-512");    
        
        md.update(passwd.getBytes());
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        Main.LOGGER.info("passwd bytes: " + Arrays.toString(passwd.getBytes()));
        Main.LOGGER.info("salt: " + Arrays.toString(salt));
        md.update(salt);
        byte[] digest = md.digest();
        new FileOutputStream(saltFile).write(salt);
        new FileOutputStream(digestFile).write(digest);
    }

    boolean passwdIsCorrect(String name, String passwd) throws NoSuchAlgorithmException, FileNotFoundException, IOException {
        FileInputStream digestFile = new FileInputStream(mainClass.getDataFolder().getName() + "/" + name + "_digest");
        FileInputStream saltFile = new FileInputStream(mainClass.getDataFolder().getName() + "/" + name + "_salt");
        byte[] digest = digestFile.readAllBytes();
        byte[] salt = saltFile.readAllBytes();
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(passwd.getBytes());
        if(Arrays.equals(digest, md.digest(salt))) {
            return true;
        }
        return false;
    }

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player)sender;
        if(args.length > 1) {
            try {
                if(!isWhitelisted(args[0])) {
                    player.sendMessage("you are not whitelisted");
                    Main.LOGGER.info(args[0] + " is not whitelisted");
                    return false;
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            try {
                if(!hasRegistered(args[0])) {
                    try {
                        registerUser(args[0], args[1]);
                        player.sendMessage("You have registered. Use login again to enter.");
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                    Main.LOGGER.info(args[0] + " has registered");
                    return false;
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            
            try {
                if(passwdIsCorrect(args[0], args[1])) {
                    player.sendMessage("Logged in");
                    player.setGameMode(GameMode.SURVIVAL);
                    Main.loggedIn.put(player.getUniqueId().toString(), true);
                    Main.LOGGER.info(args[0] + " has logged in");
                    return false;
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            
        } else {
            System.out.println("Enter your name and then your password");
        }
        return false;
    }
}