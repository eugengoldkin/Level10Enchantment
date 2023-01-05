package level10enchantment.level10enchantment;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Based on <a href="https://github.com/koenrad/VanillaEnchants">VanillaEnchants</a>
 */
public final class Level10Enchantment extends JavaPlugin {

    @Override
    public void onEnable() {
        AnvilEnchantingUtil.initialize();
        AnvilEventHandler eventHandler = new AnvilEventHandler(this);
    }

    @Override
    public void onDisable() {
    }

    public String chatPrepend(){
        return ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "Level10Enchantment" + ChatColor.DARK_GRAY + "]" +
                ChatColor.RESET + " ";
    }

}
