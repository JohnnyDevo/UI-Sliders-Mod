package uislidersmod;

import basemod.ModMinMaxSlider;
import basemod.ModPanel;
import com.megacrit.cardcrawl.core.Settings;

import java.util.function.Consumer;

public class JobbyModMinMaxSlider extends ModMinMaxSlider {
    public JobbyModMinMaxSlider(String lbl, float posX, float posY, float min, float max, float val, String format, ModPanel p, Consumer<ModMinMaxSlider> changeAction) {
        super(lbl, posX / Settings.scale, posY / Settings.scale, min, max, val, format, p, changeAction);
    }
}
