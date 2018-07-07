package de.flo56958.MineTinker.Listeners;

import de.flo56958.MineTinker.Data.Modifiers;
import de.flo56958.MineTinker.Data.Strings;
import de.flo56958.MineTinker.Main;
import de.flo56958.MineTinker.Utilities.ChatWriter;
import de.flo56958.MineTinker.Utilities.ItemGenerator;
import de.flo56958.MineTinker.Utilities.LevelCalculator;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Random;

public class BlockListener implements Listener {

    @EventHandler (priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getPlayer().getGameMode().equals(GameMode.SURVIVAL) || e.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
            ItemStack tool = e.getPlayer().getInventory().getItemInMainHand();
            ItemMeta meta = tool.getItemMeta();
            if (!tool.getType().equals(Material.AIR)) {
                if (meta.hasLore()) {
                    ArrayList<String> lore = (ArrayList<String>) meta.getLore();
                    if (lore.contains(Strings.IDENTIFIER)) {
                        LevelCalculator.addExp(e.getPlayer(), tool, 1);
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.Self-Repair.allowed")) {
                            //<editor-fold desc="self-repair check">
                            searchloop:
                            for (int i = 0; i <= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.MaxLevel"); i++) {
                                if (lore.contains(Strings.SELFREPAIR + i)) {
                                    //self-repair
                                    Random rand = new Random();
                                    int n = rand.nextInt(101);
                                    if (n <= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.PercentagePerLevel") * i) {
                                        int heal = Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.HealthRepair");
                                        short dura = (short) (tool.getDurability() - heal);
                                        if (dura < 0) { dura = 0; }
                                        e.getPlayer().getInventory().getItemInMainHand().setDurability(dura);
                                        ChatWriter.log(false, e.getPlayer().getDisplayName() + " triggered Self-Repair on " + ItemGenerator.getDisplayName(tool) + " (" + tool.getType().toString() + ")!");
                                    }
                                    break searchloop;
                                }
                            }
                            //</editor-fold>
                        }
                        if (Main.getPlugin().getConfig().getBoolean("Modifiers.XP.allowed")) {
                            //<editor-fold desc="xp check">
                            searchloop:
                            for (int i = 0; i <= Main.getPlugin().getConfig().getInt("Modifiers.XP.MaxLevel"); i++) {
                                if (lore.contains(Strings.XP + i)) {
                                    //self-repair
                                    Random rand = new Random();
                                    int n = rand.nextInt(101);
                                    if (n <= Main.getPlugin().getConfig().getInt("Modifiers.XP.PercentagePerLevel") * i) {
                                        ExperienceOrb orb = e.getPlayer().getWorld().spawn(e.getBlock().getLocation(), ExperienceOrb.class);
                                        orb.setExperience(Main.getPlugin().getConfig().getInt("Modifiers.XP.XPAmount"));
                                        ChatWriter.log(false, e.getPlayer().getDisplayName() + " triggered XP on " + ItemGenerator.getDisplayName(tool) + " (" + tool.getType().toString() + ")!");
                                    }
                                    break searchloop;
                                }
                            }
                            //</editor-fold>
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            //<editor-fold desc="SELF-REPAIR">
            if (e.getClickedBlock().getType().equals(Material.BOOKSHELF)) {
                if (Main.getPlugin().getConfig().getBoolean("Modifiers.Self-Repair.allowed")) {
                    if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.MOSSY_COBBLESTONE) && !e.getPlayer().isSneaking()) {
                        if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                            e.getPlayer().getLocation().getWorld().dropItemNaturally(e.getPlayer().getLocation(), Modifiers.SELFREPAIR_MODIFIER);
                            if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                            }
                            ChatWriter.log(false, e.getPlayer().getDisplayName() + " created a Self-Repair-Modifier in Creative!");
                        } else if (e.getPlayer().getLevel() >= Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.EnchantCost")) {
                            int amount = e.getPlayer().getInventory().getItemInMainHand().getAmount();
                            int newLevel = e.getPlayer().getLevel() - Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.EnchantCost");
                            e.getPlayer().setLevel(newLevel);
                            e.getPlayer().getInventory().getItemInMainHand().setAmount(amount - 1);
                            e.getPlayer().getLocation().getWorld().dropItemNaturally(e.getPlayer().getLocation(), Modifiers.SELFREPAIR_MODIFIER);
                            if (Main.getPlugin().getConfig().getBoolean("Sound.OnEnchanting")) {
                                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 0.5F);
                            }
                            ChatWriter.log(false, e.getPlayer().getDisplayName() + " created a Self-Repair-Modifier!");
                        } else {
                            ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, "You do not have enough Levels to perform this action!");
                            ChatWriter.sendMessage(e.getPlayer(), ChatColor.RED, Main.getPlugin().getConfig().getInt("Modifiers.Self-Repair.EnchantCost") + " levels are required!");
                            ChatWriter.log(false,  e.getPlayer().getDisplayName() + " tried to create a Self-Repair-Modifier but had not enough levels!");
                        }
                        e.setCancelled(true);
                    }
                }
            }
            //</editor-fold>
        }
    }
}
