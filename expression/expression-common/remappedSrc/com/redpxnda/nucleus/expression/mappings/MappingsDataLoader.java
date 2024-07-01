package com.redpxnda.nucleus.expression.mappings;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.registry.NucleusNamespaces;
import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.format.MappingFormat;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class MappingsDataLoader extends SimplePreparableReloadListener<Map<ResourceLocation, MappingTree>> {
    private static final Logger LOGGER = Nucleus.getLogger();
    public static MappingsDataLoader INSTANCE = new MappingsDataLoader();

    protected final Map<String, MappingFormat> extensionToFormat = new HashMap<>();
    protected Map<ResourceLocation, MappingTree> loadedMappings;

    public MappingsDataLoader() {
        for (MappingFormat format : MappingFormat.values()) {
            extensionToFormat.put(format.fileExt, format);
        }
    }

    public MappingTree getMappings(ResourceLocation id) {
        return loadedMappings.get(id);
    }

    public Map<ResourceLocation, MappingTree> getAllLoadedMappings() {
        return loadedMappings;
    }

    public static String getFileExtension(String file) {
        String[] parts = file.split("\\.");
        if (parts.length == 0) return "";
        return parts[parts.length-1];
    }

    @Override
    protected Map<ResourceLocation, MappingTree> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, MappingTree> result = new HashMap<>();
        for (Map.Entry<ResourceLocation, Resource> entry : manager.listResources("mappings", id -> NucleusNamespaces.isNamespaceValid(id.getNamespace()) && extensionToFormat.containsKey(getFileExtension(id.getPath()))).entrySet()) {
            ResourceLocation location = entry.getKey();

            String string = location.getPath();
            String fileExtension = getFileExtension(location.getPath());
            ResourceLocation id = location.withPath(string.substring("mappings".length() + 1, string.length() - fileExtension.length() - 1));

            try {
                BufferedReader reader = entry.getValue().openAsReader();
                try {
                    MemoryMappingTree tree = new MemoryMappingTree();
                    MappingReader.read(reader, tree);

                    result.put(id, tree);
                } finally {
                    if (reader == null) continue;
                    reader.close();
                }
            } catch (Exception exception) {
                LOGGER.error("Couldn't parse mapping data from '" + location + "'", exception);
            }
        }
        return result;
    }

    @Override
    protected void apply(Map<ResourceLocation, MappingTree> prepared, ResourceManager manager, ProfilerFiller profiler) {
        loadedMappings = prepared;
        System.out.println("Mappings done .. .. " + loadedMappings);

        TwoStepTreeRemapper remapper = new TwoStepTreeRemapper(
                loadedMappings.get(new ResourceLocation("nucleus:mojmap")),
                loadedMappings.get(new ResourceLocation("nucleus:yarn")),
                "source",
                "target",
                "official",
                "named"
        );

        String result = remapper.mapFieldName("net/minecraft/client/Minecraft", "mouseHandler", "Lnet/minecraft/client/MouseHandler;");
        System.out.println(result);
    }
}
