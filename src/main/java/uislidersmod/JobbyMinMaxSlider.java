package uislidersmod;

import basemod.ModMinMaxSlider;
import basemod.ModPanel;
import com.megacrit.cardcrawl.core.Settings;

import java.util.function.Consumer;

public class JobbyMinMaxSlider extends ModMinMaxSlider {
    public JobbyMinMaxSlider(String lbl, float posX, float posY, float min, float max, float val, String format, ModPanel p, Consumer<ModMinMaxSlider> changeAction) {
        super(lbl, posX / Settings.scale, posY / Settings.scale, min, max, val, format, p, changeAction);
    }
}
