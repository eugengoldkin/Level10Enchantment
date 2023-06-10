package level10enchantment.level10enchantment;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.Map;

public class AnvilEnchantingUtil {

    private static HashMap<Enchantment, Integer> limits = new HashMap<>();

    public static void initialize(){
        setEnchantmentLimits();
    }

    public static int getMaxLevel(Enchantment enchantment){
        if(limits.containsKey(enchantment)){
            return limits.get(enchantment);
        }
        return enchantment.getMaxLevel();
    }

    private static void setEnchantmentLimits(){
        HashMap<Enchantment, Integer> result = new HashMap<>();

        result.put(Enchantment.DAMAGE_ARTHROPODS,10);       //bane_of_arthropods
        result.put(Enchantment.DIG_SPEED, 10);              //efficiency
        result.put(Enchantment.PROTECTION_FALL, 7);         //feather_falling
        result.put(Enchantment.FIRE_ASPECT, 5);             //fire_aspect
        result.put(Enchantment.PROTECTION_FIRE, 10);        //fire_protection
        result.put(Enchantment.LOOT_BONUS_BLOCKS, 10);      //fortune
        result.put(Enchantment.IMPALING, 10);               //impaling
        result.put(Enchantment.LOOT_BONUS_MOBS, 10);        //looting
        result.put(Enchantment.LUCK, 5);                    //luck_of_the_sea
        result.put(Enchantment.LURE, 5);                    //lure
        result.put(Enchantment.ARROW_DAMAGE, 10);           //power
        result.put(Enchantment.PROTECTION_PROJECTILE, 10);  //projectile_protection
        result.put(Enchantment.PROTECTION_ENVIRONMENTAL, 10);//protection
        result.put(Enchantment.PROTECTION_EXPLOSIONS, 10);  //explosion_protection
        result.put(Enchantment.DAMAGE_ALL, 10);             //sharpness
        result.put(Enchantment.DAMAGE_UNDEAD, 10);          //smite
        result.put(Enchantment.QUICK_CHARGE, 5);            //quick_charge
        result.put(Enchantment.OXYGEN, 10);                 //respiration
        result.put(Enchantment.SWEEPING_EDGE, 10);          //sweeping (sweeping edge)
        result.put(Enchantment.THORNS, 7);                  //thorns
        result.put(Enchantment.DURABILITY, 10);             //unbreaking

        limits = result;
    }

    public static HashMap<Enchantment, Integer> combine(
            ItemStack itemStack,
            Map<Enchantment, Integer> left,
            Map<Enchantment, Integer> right){

        HashMap<Enchantment, Integer> result = new HashMap<>(left);

        //iterate over all enchants on the right item
        for (Map.Entry<Enchantment, Integer> entry : right.entrySet()) {
            Enchantment rightEnchantment = entry.getKey();
            int rightEnchantmentLevel = entry.getValue();
            //make sure only appropriate enchantments can be put upon the item
            if(!(itemStack.getItemMeta() instanceof EnchantmentStorageMeta ||
                    rightEnchantment.canEnchantItem(itemStack))){
                continue;
            }
            //prevent conflicting enchantments on the same item
            if(hasConflict(left, rightEnchantment)){
                continue;
            }
            if (!left.containsKey(rightEnchantment)) {
                //if the left item has not what the right item has to offer, we shall give it to the result
                result.put(rightEnchantment, rightEnchantmentLevel);
            } else {
                int leftEnchantmentLevel = left.get(rightEnchantment);
                if (leftEnchantmentLevel != rightEnchantmentLevel) {
                    result.put(rightEnchantment, Integer.max(leftEnchantmentLevel,rightEnchantmentLevel));
                } else {
                    if(rightEnchantmentLevel + 1 <= getMaxLevel(rightEnchantment)){
                        result.put(rightEnchantment, rightEnchantmentLevel + 1);
                    }else{
                        result.put(rightEnchantment, rightEnchantmentLevel);
                    }
                }
            }
        }
        return result;
    }

    private static boolean hasConflict(Map<Enchantment, Integer> enchants, Enchantment enchantment){
        for(Enchantment e : enchants.keySet()){
            if(enchantment.conflictsWith(e) && !(enchantment.equals(e))){
                return true;
            }
        }
        return false;
    }

}
