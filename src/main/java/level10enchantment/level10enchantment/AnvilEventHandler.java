package level10enchantment.level10enchantment;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.Map;

import static level10enchantment.level10enchantment.AnvilEnchantingUtil.combine;

public class AnvilEventHandler implements Listener {

    public AnvilEventHandler(Level10Enchantment plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPrepareAnvilEvent(PrepareAnvilEvent event){

        AnvilInventory inventory = event.getInventory();

        inventory.setMaximumRepairCost(Integer.MAX_VALUE);

        ItemStack leftItem = inventory.getItem(0);
        ItemStack rightItem = inventory.getItem(1);
        ItemStack resultItem;

        //check if both item slots are filled
        if (leftItem == null || rightItem == null) {
            return;
        }

        if(leftItem.getType() == rightItem.getType() ||
                rightItem.getItemMeta() instanceof EnchantmentStorageMeta){

            Map<Enchantment, Integer> base = getEnchantments(leftItem);
            Map<Enchantment, Integer> addition = getEnchantments(rightItem);

            Map<Enchantment, Integer> enchantments = combine(leftItem, base, addition);
            resultItem = replaceEnchantments(leftItem, enchantments);

            int repairCost = getRepairCost(enchantments);

            if(leftItem.getItemMeta() instanceof Damageable &&
                    rightItem.getItemMeta() instanceof Damageable &&
                    resultItem.getItemMeta() instanceof Damageable){
                int leftDamage = ((Damageable) leftItem.getItemMeta()).getDamage();
                if(leftDamage > 0){
                    int healthLeft = leftItem.getType().getMaxDurability() - leftDamage;
                    int healthRight = rightItem.getType().getMaxDurability() -
                            ((Damageable) rightItem.getItemMeta()).getDamage();
                    int finalDamage = Integer.max(0, rightItem.getType().getMaxDurability() - healthLeft - healthRight);
                    Damageable meta = (Damageable) resultItem.getItemMeta();
                    meta.setDamage(finalDamage);
                    resultItem.setItemMeta(meta);
                    repairCost++;
                }
            }

            event.setResult(resultItem);
            inventory.setRepairCost(repairCost);
            event.getView().setProperty(InventoryView.Property.REPAIR_COST, repairCost);
        }
    }


    private ItemStack replaceEnchantments(ItemStack item, Map<Enchantment, Integer> enchantments){
        ItemStack result = item.clone();
        if(item.getItemMeta() instanceof EnchantmentStorageMeta){
            EnchantmentStorageMeta resultItemMeta = (EnchantmentStorageMeta) result.getItemMeta();
            if(resultItemMeta != null){
                for (Map.Entry<Enchantment, Integer> entry : resultItemMeta.getStoredEnchants().entrySet()) {
                    resultItemMeta.removeStoredEnchant(entry.getKey());
                }
                for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    resultItemMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
                }
            }
            result.setItemMeta(resultItemMeta);
            return result;
        }
        if(result.getItemMeta() != null){
            for (Map.Entry<Enchantment, Integer> entry : result.getItemMeta().getEnchants().entrySet()) {
                result.removeEnchantment(entry.getKey());
            }
        }
        result.addUnsafeEnchantments(enchantments);
        return result;
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(checkWhetherEventIsRelevant(event)){
            //Who dared to cause this? That one should have the Mark of the Clicker.
            Player player = (Player) event.getWhoClicked();

            final AnvilInventory anvilInventory = (AnvilInventory) event.getInventory();
            ItemStack[] items = anvilInventory.getContents();
            int repairCost = anvilInventory.getRepairCost();

            if (player.getLevel() >= repairCost && event.getCurrentItem() != null) {

                // clone the result item
                ItemStack itemToGive = event.getCurrentItem().clone();

                // let's make SURE that the item given is only 1!
                itemToGive.setAmount(1);

                // If the first item is a stack, we should give it back (-1)
                if (items[0].getAmount() > 1) {
                    ItemStack returnedStack = items[0].clone();
                    returnedStack.setAmount(returnedStack.getAmount() - 1);
                    if (player.getInventory().addItem(returnedStack).size() != 0) {
                        player.getWorld().dropItem(player.getLocation(), returnedStack);
                    }
                }

                // Make the Emptiness come upon the anvil. Let all three slot become barren lands!
                ItemStack temp;
                if((temp = anvilInventory.getItem(0)) != null ){
                    anvilInventory.remove(temp);
                }
                if((temp = anvilInventory.getItem(1)) != null ){
                    anvilInventory.remove(temp);
                }
                if((temp = anvilInventory.getItem(2)) != null ){
                    anvilInventory.remove(temp);
                }


                // give the player the clone of the result! (drop it on them if their inventory is full)
                if (player.getInventory().addItem(itemToGive).size() != 0) {
                    player.getWorld().dropItem(player.getLocation(), itemToGive);
                }

                // Play the anvil sound on the player.
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);

                //let's set players exp levels to what they should be after this repair
                player.giveExpLevels(-repairCost);
            }
        }
    }

    private boolean checkWhetherEventIsRelevant(InventoryClickEvent event){
        if(event.isCancelled()){
            // Another one had intervened
            return false;
        }
        if(!(event.getWhoClicked() instanceof Player)){
            // Was it a player or some kind of dark luminous power, we have yet to encounter?
            return false;
        }
        if(!(event.getInventory() instanceof AnvilInventory)){
            // Sometimes it is just not the right place
            return false;
        }
        int rawSlot = event.getRawSlot();
        if(rawSlot != event.getView().convertSlot(rawSlot)){
            // We need to use the right rawSlot at the right place. Otherwise, the anvil may not like it.
            return false;
        }
        if(rawSlot != 2){
            // It might prefer it raw, but it has to be the right slot.
            return false;
        }
        ItemStack[] items = event.getInventory().getContents();
        if(items[0] == null || items[1] == null) {
            // The first two slots of the anvil need to be filled.
            return false;
        }
        if (event.getCurrentItem()== null || event.getCurrentItem().getType() == Material.AIR){
            // We ain't interested in just some hot air
            return false;
        }
        return event.getCurrentItem() != items[0] && event.getCurrentItem() != items[1];
    }

    /**
     * The glorious and highly complex computation of the repair cost.
     * Unless you have a deep understanding of the technology called 'Addition',
     * you should just take this function as it is.
     *
     * @param enchantments is a List of Enchantments and their corresponding level
     * @return the cost for repairing or combining two items.
     */
    private int getRepairCost(Map<Enchantment, Integer> enchantments){
        int repairCost = 0;
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            repairCost += entry.getValue();
        }
        return repairCost;
    }

    private Map<Enchantment, Integer> getEnchantments(ItemStack item){
        if(item.getItemMeta() == null){
            return new HashMap<>();
        }
        if(item.getItemMeta() instanceof EnchantmentStorageMeta){
            return new HashMap<>(((EnchantmentStorageMeta)item.getItemMeta()).getStoredEnchants());
        }
        return item.getItemMeta().getEnchants();
    }

}
