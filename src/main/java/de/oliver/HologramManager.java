package de.oliver;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.Display;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HologramManager {

    private final Map<String, Hologram> holograms; // hologram name, hologram object

    public HologramManager() {
        this.holograms = new HashMap<>();
    }

    public void saveHolograms(boolean force){
        FileConfiguration config = FancyHolograms.getInstance().getConfig();

        for (Hologram hologram : holograms.values()) {
            boolean shouldSave = force || hologram.isDirty();
            if(!shouldSave){
                continue;
            }

            config.set("holograms." + hologram.getName() + ".location", hologram.getLocation());
            config.set("holograms." + hologram.getName() + ".billboard", hologram.getBillboard().getSerializedName());
            config.set("holograms." + hologram.getName() + ".scale", hologram.getScale());
            config.set("holograms." + hologram.getName() + ".text", hologram.getLines());
            config.set("holograms." + hologram.getName() + ".shadow_radius", hologram.getShadowRadius());
            config.set("holograms." + hologram.getName() + ".shadow_strength", hologram.getShadowStrength());
            config.set("holograms." + hologram.getName() + ".update_text_interval", hologram.getUpdateTextInterval());
            if(hologram.getBackground() != null){
                config.set("holograms." + hologram.getName() + ".background", hologram.getBackground().getSerializedName());
            }
            if(hologram.getLinkedNpc() != null){
                config.set("holograms." + hologram.getName() + ".linkedNpc", hologram.getLinkedNpc().getName());
            }

            hologram.setDirty(false);
        }

        FancyHolograms.getInstance().saveConfig();
    }

    public void loadHolograms(){
        holograms.clear();

        FileConfiguration config = FancyHolograms.getInstance().getConfig();

        if(!config.isConfigurationSection("holograms")){
            return;
        }

        for (String name : config.getConfigurationSection("holograms").getKeys(false)) {
            Location location = config.getLocation("holograms." + name + ".location");
            ChatFormatting background = config.isString("holograms." + name + ".background") ? ChatFormatting.getByName(config.getString("holograms." + name + ".background")) : null;
            float scale = (float) config.getDouble("holograms." + name + ".scale", 1f);
            List<String> text = config.getStringList("holograms." + name + ".text");
            int updateTextInterval = config.getInt("holograms." + name + ".update_text_interval");
            float shadowRadius = (float) config.getDouble("holograms." + name + ".shadow_radius");
            float shadowStrength = (float) config.getDouble("holograms." + name + ".shadow_strength", 1);
            String linkedNpcName = config.getString("holograms." + name + ".linkedNpc");

            String billboardName = config.getString("holograms." + name + ".billboard");
            Display.BillboardConstraints billboard = Display.BillboardConstraints.CENTER;
            for (Display.BillboardConstraints b : Display.BillboardConstraints.values()) {
                if(b.getSerializedName().equalsIgnoreCase(billboardName)){
                    billboard = b;
                }
            }

            Hologram hologram = new Hologram(name, location, text, billboard, scale, background, shadowRadius, shadowStrength, updateTextInterval, null);

            if(FancyHolograms.getInstance().isUsingFancyNpcs() && linkedNpcName != null && linkedNpcName.length() > 0){
                hologram.setLinkedNpc(de.oliver.FancyNpcs.getInstance().getNpcManager().getNpc(linkedNpcName));
            }

            hologram.create();
        }

    }

    public Collection<Hologram> getAllHolograms(){
        return holograms.values();
    }

    public void addHologram(Hologram hologram){
        holograms.put(hologram.getName(), hologram);
    }

    public void removeHologram(Hologram hologram){
        holograms.remove(hologram.getName());

        FileConfiguration config = FancyHolograms.getInstance().getConfig();
        config.set("holograms." + hologram.getName(), null);
        FancyHolograms.getInstance().saveConfig();
    }

    public Hologram getHologram(String name){
        return holograms.getOrDefault(name, null);
    }

    public Map<String, Hologram> getHolograms() {
        return holograms;
    }
}
