package com.rotpaddon.exampleaddon.form;

import com.rotpaddon.exampleaddon.AddonMain;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Shared form descriptions used by both the client GUI and server validation. */
public final class AlcorFormRegistry {
    public static final ResourceLocation BLUE_NIGHT =
            new ResourceLocation(AddonMain.MOD_ID, "blue_night");

    // Fortunate Son will register its ACTs here later, using the same GUI/packet.
    private static final Map<ResourceLocation, List<FormOption>> FORMS = new HashMap<>();

    static {
        FORMS.put(BLUE_NIGHT, Collections.unmodifiableList(Arrays.asList(
                new FormOption(0, "Bracelet", 0, true),
                new FormOption(1, "Scythe", 0, true),
                new FormOption(2, "Katana", 1, false),
                new FormOption(3, "Bow", 2, false),
                new FormOption(4, "Hammer", 3, false),
                new FormOption(5, "Axe", 4, false)
        )));
    }

    private AlcorFormRegistry() {}

    public static List<FormOption> getForms(ResourceLocation standId) {
        return FORMS.getOrDefault(standId, Collections.emptyList());
    }

    public static boolean isAllowed(ResourceLocation standId, int formId, int resolveLevel) {
        for (FormOption form : getForms(standId)) {
            if (form.id == formId) {
                return form.implemented && resolveLevel >= form.requiredResolve;
            }
        }
        return false;
    }

    public static final class FormOption {
        public final int id;
        public final String name;
        public final int requiredResolve;
        public final boolean implemented;

        public FormOption(int id, String name, int requiredResolve, boolean implemented) {
            this.id = id;
            this.name = name;
            this.requiredResolve = requiredResolve;
            this.implemented = implemented;
        }
    }
}
