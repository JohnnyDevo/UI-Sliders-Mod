package uislidersmod;

import basemod.ModLabel;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.ModToggleButton;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.core.Settings;

import java.util.function.Consumer;

public class JobbyModLabeledToggleButton extends ModLabeledToggleButton {
    public JobbyModLabeledToggleButton(String labelText, float xPos, float yPos, Color color, BitmapFont font, boolean enabled, ModPanel p, Consumer<ModLabel> labelUpdate, Consumer<ModToggleButton> c) {
        super(labelText, xPos / Settings.scale, yPos / Settings.scale, color, font, enabled, p, labelUpdate, c);
    }
}
