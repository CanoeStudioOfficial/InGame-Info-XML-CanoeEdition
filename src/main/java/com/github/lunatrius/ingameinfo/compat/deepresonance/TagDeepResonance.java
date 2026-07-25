package com.github.lunatrius.ingameinfo.compat.deepresonance;

import com.github.lunatrius.ingameinfo.reference.Reference;
import com.github.lunatrius.ingameinfo.tag.Tag;
import com.github.lunatrius.ingameinfo.tag.registry.TagRegistry;
import mcjty.deepresonance.items.RadiationMonitorItem;

public abstract class TagDeepResonance extends Tag {
    @Override
    public String getCategory() {
        return "deepresonance";
    }

    public static class DeepResonanceRadiation extends TagDeepResonance {
        @Override
        public String getValue() {
            if (player == null) {
                return "0";
            }

            try {
                RadiationMonitorItem.fetchRadiation(player);
                return Long.toString((long) RadiationMonitorItem.radiationStrength);
            } catch (final NullPointerException e) {
                return "0";
            } catch (final Throwable e) {
                log(this, e);
            }

            return "NaN";
        }
    }

    public static void register() {
        TagRegistry.INSTANCE.register(new DeepResonanceRadiation().setName("drradiation"));
    }

    private static void log(final Tag tag, final Throwable ex) {
        Reference.logger.warn(Reference.MODID + ":" + tag.getName(), ex);
    }
}
