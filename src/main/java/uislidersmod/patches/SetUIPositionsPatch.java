package uislidersmod.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.ui.panels.*;
import uislidersmod.UISlidersMod;

import java.io.IOException;

public class SetUIPositionsPatch {
    @SpirePatch(
            clz = DrawPilePanel.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class SetDrawPilePositionPatch {
        public static void Postfix(DrawPilePanel __instance) {
            if (UISlidersMod.UISlidersConfig != null) {
                __instance.show_x = UISlidersMod.UISlidersConfig.getFloat(UISlidersMod.DRAW_PILE_HORIZONTAL) * Settings.WIDTH;
                __instance.show_y = UISlidersMod.UISlidersConfig.getFloat(UISlidersMod.DRAW_PILE_VERTICAL) * Settings.HEIGHT;
            }
        }
    }

    @SpirePatch(
            clz = DiscardPilePanel.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class SetDiscardPilePositionPatch {
        public static void Postfix(DiscardPilePanel __instance) {
            if (UISlidersMod.UISlidersConfig != null) {
                __instance.show_x = UISlidersMod.UISlidersConfig.getFloat(UISlidersMod.DISCARD_PILE_HORIZONTAL) * Settings.WIDTH;
                __instance.show_y = UISlidersMod.UISlidersConfig.getFloat(UISlidersMod.DISCARD_PILE_VERTICAL) * Settings.HEIGHT;
            }
        }
    }

    @SpirePatch(
            clz = ExhaustPanel.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class SetExhaustPilePositionPatch {
        public static void Postfix(ExhaustPanel __instance) {
            if (UISlidersMod.UISlidersConfig != null) {
                __instance.target_x = __instance.current_x = __instance.show_x = UISlidersMod.UISlidersConfig.getFloat(UISlidersMod.EXHAUST_PILE_HORIZONTAL) * Settings.WIDTH;
                __instance.target_y = __instance.current_y = __instance.show_y = UISlidersMod.UISlidersConfig.getFloat(UISlidersMod.EXHAUST_PILE_VERTICAL) * Settings.HEIGHT;
            }
        }
    }

    @SpirePatch(
            clz = EnergyPanel.class,
            method = SpirePatch.CONSTRUCTOR
    )
    public static class SetEnergyPanelPositionPatch {
        public static void Postfix(EnergyPanel __instance) {
            if (UISlidersMod.UISlidersConfig != null) {
                __instance.show_x = UISlidersMod.UISlidersConfig.getFloat(UISlidersMod.ENERGY_HORIZONTAL) * Settings.WIDTH;
                __instance.show_y = UISlidersMod.UISlidersConfig.getFloat(UISlidersMod.ENERGY_VERTICAL) * Settings.HEIGHT;
            }
        }
    }

    @SpirePatch(
            clz = DrawPilePanel.class,
            method = "updatePositions"
    )
    public static class DragDrawPilePatch {

        public static void Prefix(DrawPilePanel __instance) {
            Hitbox hb = ReflectionHacks.getPrivate(__instance, DrawPilePanel.class, "hb");
            hb.move(__instance.current_x + UISlidersMod.DECK_X, __instance.current_y + UISlidersMod.DECK_Y);
        }
    }

    @SpirePatch(
            clz = DiscardPilePanel.class,
            method = "updatePositions"
    )
    public static class DragDiscardPilePatch {

        public static void Prefix(DiscardPilePanel __instance) {
            Hitbox hb = ReflectionHacks.getPrivate(__instance, DiscardPilePanel.class, "hb");
            hb.move(__instance.current_x + UISlidersMod.DISCARD_X, __instance.current_y + UISlidersMod.DISCARD_Y);
        }
    }

    private static AbstractPanel draggedItem = null;
    private static Vector2 relativePosition = null;

    @SpirePatch(
            clz = AbstractPanel.class,
            method = "updatePositions"
    )
    public static class DragPanelsPatch {

        public static void Prefix(AbstractPanel __instance) {
            if (__instance instanceof EnergyPanel
                    || __instance instanceof ExhaustPanel
                    || __instance instanceof DiscardPilePanel
                    || __instance instanceof DrawPilePanel) {
                if (InputHelper.justClickedLeft && InputHelper.isShortcutModifierKeyPressed()) {
                    if (isHovered(__instance)) {
                        draggedItem = __instance;
                        relativePosition = new Vector2(InputHelper.mX - __instance.show_x, InputHelper.mY - __instance.show_y);
                        InputHelper.justClickedLeft = false;
                    }
                } else if (draggedItem == __instance) {
                    if (InputHelper.justReleasedClickLeft || !InputHelper.isShortcutModifierKeyPressed()) {
                        UISlidersMod.UISlidersConfig.setFloat(getH(draggedItem), __instance.show_x / Settings.WIDTH);
                        UISlidersMod.UISlidersConfig.setFloat(getV(draggedItem), __instance.show_y / Settings.HEIGHT);
                        try {UISlidersMod.UISlidersConfig.save();} catch(IOException e) {e.printStackTrace();}
                        UISlidersMod.setSliderValues(UISlidersMod.getCurrentValues());
                        draggedItem = null;
                        InputHelper.justReleasedClickLeft = false;
                    } else {
                        setRenderTransformH(draggedItem, InputHelper.mX - relativePosition.x);
                        setRenderTransformV(draggedItem, InputHelper.mY - relativePosition.y);
                        draggedItem.current_x = draggedItem.target_x = draggedItem.show_x;
                        draggedItem.current_y = draggedItem.target_y = draggedItem.show_y;
                    }
                }
            }
        }
    }

    private static boolean isHovered(AbstractPanel item) {
        Hitbox hb = null;
        if (item instanceof EnergyPanel) {
            hb = ReflectionHacks.getPrivate(item, EnergyPanel.class, "tipHitbox");
        }
        if (item instanceof ExhaustPanel) {
            hb = ReflectionHacks.getPrivate(item, ExhaustPanel.class, "hb");
        }
        if (item instanceof DiscardPilePanel) {
            hb = ReflectionHacks.getPrivate(item, DiscardPilePanel.class, "hb");
        }
        if (item instanceof DrawPilePanel) {
            hb = ReflectionHacks.getPrivate(item, DrawPilePanel.class, "hb");
        }
        if (hb != null) {
            return hb.hovered;
        }
        return false;
    }

    private static String getH(AbstractPanel item) {
        if (item instanceof EnergyPanel) {
            return UISlidersMod.ENERGY_HORIZONTAL;
        } else if (item instanceof ExhaustPanel) {
            return UISlidersMod.EXHAUST_PILE_HORIZONTAL;
        } else if (item instanceof DiscardPilePanel) {
            return UISlidersMod.DISCARD_PILE_HORIZONTAL;
        } else if (item instanceof DrawPilePanel) {
            return UISlidersMod.DRAW_PILE_HORIZONTAL;
        } else {
            return "";
        }
    }

    private static String getV(AbstractPanel item) {
        if (item instanceof EnergyPanel) {
            return UISlidersMod.ENERGY_VERTICAL;
        } else if (item instanceof ExhaustPanel) {
            return UISlidersMod.EXHAUST_PILE_VERTICAL;
        } else if (item instanceof DiscardPilePanel) {
            return UISlidersMod.DISCARD_PILE_VERTICAL;
        } else if (item instanceof DrawPilePanel) {
            return UISlidersMod.DRAW_PILE_VERTICAL;
        } else {
            return "";
        }
    }

    private static void setRenderTransformH(AbstractPanel item, float val) {
        if (item instanceof DiscardPilePanel) {
            item.show_x = UISlidersMod.sliderToDiscardH(val / Settings.WIDTH) * Settings.WIDTH;
        } else if (item instanceof DrawPilePanel) {
            item.show_x = UISlidersMod.sliderToDrawH(val / Settings.WIDTH) * Settings.WIDTH;
        }
        item.show_x = val;
    }

    private static void setRenderTransformV(AbstractPanel item, float val) {
        if (item instanceof DiscardPilePanel) {
            item.show_y = UISlidersMod.sliderToDiscardV(val / Settings.HEIGHT) * Settings.HEIGHT;
        } else if (item instanceof DrawPilePanel) {
            item.show_y = UISlidersMod.sliderToDrawV(val / Settings.HEIGHT) * Settings.HEIGHT;
        }
        item.show_y = val;
    }
}
