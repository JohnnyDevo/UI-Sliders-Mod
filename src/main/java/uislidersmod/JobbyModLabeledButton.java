package uislidersmod;

import basemod.ModLabeledButton;
import basemod.ModPanel;
import com.megacrit.cardcrawl.core.Settings;

import java.util.function.Consumer;

public class JobbyModLabeledButton extends ModLabeledButton {
    public JobbyModLabeledButton(String label, float xPos, float yPos, ModPanel p, Consumer<ModLabeledButton> c) {
        super(label, xPos / Settings.scale, yPos / Settings.scale, p, c);
    }
}
