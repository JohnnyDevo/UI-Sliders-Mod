package uislidersmod;

import basemod.ModLabel;
import basemod.ModPanel;
import com.megacrit.cardcrawl.core.Settings;

import java.util.function.Consumer;

public class JobbyModLabel extends ModLabel {
    public JobbyModLabel(String labelText, float xPos, float yPos, ModPanel p, Consumer<ModLabel> updateFunc) {
        super(labelText, xPos / Settings.scale, yPos / Settings.scale, p, updateFunc);
    }
}
