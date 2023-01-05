# Level10Enchantment

Level10Enchantment is a Minecraft server plugin for Bukkit/Spigot with the purpose to increase the level cap of enchantments by combining two items on an anvil.
For example if you combine two diamond pickaxes, each with Efficiency V, you get a pickaxe with Efficiency VI.

Level10Enchantment has been tested on the following Minecraft versions using Spigot or Paper:
- `Minecraft 1.19`, `Minecraft 1.19.1`, `Minecraft 1.19.2`, `Minecraft 1.19.3`

You can download those version from the folder `Level10Enchantment/out/versions`.

This mod is loosely based on `https://github.com/koenrad/VanillaEnchants` by koenrad

## Why does this exist?

I couldn't find a plugin that would just increase the max levels of some enchantments.
No chat commands, no permissions, no config files, no useless log messages.
The only plugin which came near to what I wanted was the mentioned one by koenrad.
But since he stopped maintaining it, I decided to make my own plugin with Blackjack and Intellij.

## Mechanics

The mechanics should stay mostly the same:
- `Enchantment level L` + `Enchantment level L` => `Enchantment level L+1`
- Max Enchantment level is capped by either 10 or some other useful value (<10). 
The Wikipage https://minecraft.fandom.com/wiki/Enchanting#Maximum_effective_values_for_enchantments should give some hints regarding this topic. 
- Enchantment Compatibility gets verified (No Efficiency on a helmet or Luck on an earth block)
- Mutual exclusivity gets verified (No infinity + mending)
- The item in the left slot will always be the resulting item. This allows players to put enchants into a book.
- The cost of combining enchants is equal to the sum total of all levels on the resulting item.
    - Ex: Book 1 has Looting IV, Book 2 has Power II, the cost to combine them would be (4 + 2) = 6
    - If the item gets repaired the cost is increased by 1
    - I took that idea from koenrad (see `https://github.com/koenrad/VanillaEnchants`)

## Level Caps

Here is the part of the source code `AnvilEnchantingUtil.setEnchantmentLimits()` which changes the max levels:

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
      result.put(Enchantment.DAMAGE_ALL, 10);             //sharpness
      result.put(Enchantment.DAMAGE_UNDEAD, 10);          //smite
      result.put(Enchantment.QUICK_CHARGE, 5);            //quick_charge
      result.put(Enchantment.OXYGEN, 10);                 //respiration
      result.put(Enchantment.SWEEPING_EDGE, 10);          //sweeping (sweeping edge)
      result.put(Enchantment.THORNS, 7);                  //thorns
      result.put(Enchantment.DURABILITY, 10);             //unbreaking


## Installation

Copy the built jar file to

```bash
<your_server_directory>/plugins/
```

## Configuration

No configuration needed! I'm lazy and so shall be you!

## Known Issues/Idiosyncrasies
- The repair costs cap of 40 levels is ignored (I like it that way)
- If you shift click the resulting item, sometimes it will appear to have been duplicated (duplicate item disappears when interacting with it)
- If you do something inappropriate, this will have consequences (item might disappear etc)
- You can combine two stacks of stone to get one stack of stones! Yay!

## Questions

### Can I contribute through pull request?

Yes you can.

### I found a bug!

Then kill it! Or eat it to get the protein!