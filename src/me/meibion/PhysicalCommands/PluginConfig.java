package me.meibion.PhysicalCommands;

import org.bukkit.Material;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PluginConfig extends Configuration {

    private static List<String> permittedMaterials = new ArrayList<String>();

    public PluginConfig(File file) {
        super(file);

        // Check if the file exists
        if(!file.exists()) {

            // Attempt to create file path
            final File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + file.getAbsolutePath());
            }

            // Create default data
            generateConfig();
        }

        // Load data from file
        try {
            load();
            permittedMaterials = this.getStringList("permittedMaterials", new ArrayList<>());
        }
        catch(Exception e) { e.printStackTrace(); }
    }

    private void generateConfig() {
        // File header
        final String header = String.join("\n",
                "# Physical Commands Config: -",
                          "# 'permittedMaterials' - Enter the material name of the blocks that can be assigned a physical command",
                          "# - 'ALL' can be set as a material to allow all blocks."
        );
        this.setHeader(header);

        // Set default allows blocks
        final String[] permittedMats = {"LEVER", "STONE_BUTTON", "STONE_PLATE", "WOOD_PLATE", "SIGN_POST", "WALL_SIGN"};
        this.setProperty("permittedMaterials", permittedMats);
        save();
    }

    public boolean isMaterialPermitted(Material material) {
        if(material == null) { return false;}

        for(String permittedMaterial : permittedMaterials) {
            if(permittedMaterial.equalsIgnoreCase(material.name())) {
                return true;
            }
            else if(permittedMaterial.equalsIgnoreCase("ALL")) {
                return true;
            }
        }

        return false;
    }
}
