package uislidersmod;

import basemod.ModButton;
import basemod.ModPanel;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.Settings;

import java.util.function.Consumer;

public class JobbyModButton extends ModButton {
    public JobbyModButton(float xPos, float yPos, Texture tex, ModPanel p, Consumer<ModButton> c) {
        super(xPos / Settings.scale, yPos / Settings.scale, tex, p, c);
    }
}
