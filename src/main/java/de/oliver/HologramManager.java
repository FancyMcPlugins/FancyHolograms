package de.oliver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HologramManager {

    private final Map<String, Hologram> holograms; // hologram name, hologram object

    public HologramManager() {
        this.holograms = new HashMap<>();
    }

    public void saveHolograms(){

    }

    public void loadHolograms(){

    }

    public Collection<Hologram> getAllHolograms(){
        return holograms.values();
    }

    public void addHologram(Hologram hologram){
        holograms.put(hologram.getName(), hologram);
    }

    public void removeHologram(Hologram hologram){
        holograms.remove(hologram.getName());
    }

    public Hologram getHologram(String name){
        return holograms.getOrDefault(name, null);
    }

    public Map<String, Hologram> getHolograms() {
        return holograms;
    }
}
