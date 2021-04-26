package uislidersmod;

import basemod.*;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

@SpireInitializer
public class UISlidersMod implements PostInitializeSubscriber, EditStringsSubscriber {
    public static final String DRAW_PILE_HORIZONTAL = "DRAW_PILE_HORIZONTAL";
    public static final String DRAW_PILE_VERTICAL = "DRAW_PILE_VERTICAL";
    public static final String DISCARD_PILE_HORIZONTAL = "DISCARD_PILE_HORIZONTAL";
    public static final String DISCARD_PILE_VERTICAL = "DISCARD_PILE_VERTICAL";
    public static final String EXHAUST_PILE_HORIZONTAL = "EXHAUST_PILE_HORIZONTAL";
    public static final String EXHAUST_PILE_VERTICAL = "EXHAUST_PILE_VERTICAL";
    public static final String ENERGY_HORIZONTAL = "ENERGY_HORIZONTAL";
    public static final String ENERGY_VERTICAL = "ENERGY_VERTICAL";
    public static final String PROFILE = "PROFILE";
    public static final String SELECTED_PROFILE = "SELECTED_PROFILE";
    public static final String ID = "uislidersmod:UISlidersMod";
    public static final Logger logger = LogManager.getLogger(UISlidersMod.class.getName());
    public static Properties UIDefaults;
    public static SpireConfig UISlidersConfig;
    public static UIStrings uiStrings;

    public UISlidersMod(){
        BaseMod.subscribe(this);
    }

    //Used by @SpireInitializer
    @SuppressWarnings("unused")
    public static void initialize(){
        new UISlidersMod();
        logger.info("UI Sliders mod initialized");
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(UIStrings.class, "uislidersmod/localization/eng/ui.json");
        String lang = Settings.language.toString().toLowerCase();
        if (!lang.equals("eng")) {
            try {
                BaseMod.loadCustomStringsFile(UIStrings.class, "uislidersmod/localization/" + lang + "/ui.json");
            } catch (Exception e) {
                System.out.println("Error loading localized strings for: " + lang);
                e.printStackTrace();
            }
        }
    }

    private static ModMinMaxSlider drH;
    private static ModMinMaxSlider drV;
    private static ModMinMaxSlider diH;
    private static ModMinMaxSlider diV;
    private static ModMinMaxSlider exH;
    private static ModMinMaxSlider exV;
    private static ModMinMaxSlider enH;
    private static ModMinMaxSlider enV;

    @Override
    public void receivePostInitialize() {
        initializeValues();
        uiStrings = CardCrawlGame.languagePack.getUIString(ID);
        String[] TEXT = uiStrings.TEXT;
        String[] EXTRA_TEXT = uiStrings.EXTRA_TEXT;

        UIDefaults = new Properties();

        //default values are pulled from the base game's respective constructors,
        //and then converted to a 0 to 1 scale based on the game's screen dimensions
        String drawPileX = String.valueOf(0.0f);
        String drawPileY = String.valueOf(0.0f);
        UIDefaults.setProperty(DRAW_PILE_HORIZONTAL, drawPileX);
        UIDefaults.setProperty(DRAW_PILE_VERTICAL, drawPileY);

        String discardPileX = String.valueOf((Settings.WIDTH - (256 * Settings.scale)) / Settings.WIDTH);
        String discardPileY = String.valueOf(0.0f);
        UIDefaults.setProperty(DISCARD_PILE_HORIZONTAL, discardPileX);
        UIDefaults.setProperty(DISCARD_PILE_VERTICAL, discardPileY);

        String exhaustPileX = String.valueOf((Settings.WIDTH - 70f * Settings.scale) / Settings.WIDTH);
        String exhaustPileY = String.valueOf((184f * Settings.scale) / Settings.HEIGHT);
        UIDefaults.setProperty(EXHAUST_PILE_HORIZONTAL, exhaustPileX);
        UIDefaults.setProperty(EXHAUST_PILE_VERTICAL, exhaustPileY);

        String energyX = String.valueOf((198f * Settings.scale) / Settings.WIDTH);
        String energyY = String.valueOf((190f * Settings.scale) / Settings.HEIGHT);
        UIDefaults.setProperty(ENERGY_HORIZONTAL, energyX);
        UIDefaults.setProperty(ENERGY_VERTICAL, energyY);

        UIDefaults.setProperty(SELECTED_PROFILE, "0");
        try {
            UISlidersConfig = new SpireConfig("UI Sliders Mod", "uislidersmod", UIDefaults);
            logger.info("UISLIDER CONFIG OPTIONS LOADED:");
            logger.info("draw pile position: " + (UISlidersConfig.getFloat(DRAW_PILE_HORIZONTAL) * 100) + "% by " + (UISlidersConfig.getFloat(DRAW_PILE_VERTICAL) * 100) + "%.");
            logger.info("discard pile position: " + (UISlidersConfig.getFloat(DISCARD_PILE_HORIZONTAL) * 100) + "% by " + (UISlidersConfig.getFloat(DISCARD_PILE_VERTICAL) * 100) + "%.");
            logger.info("exhaust pile position: " + (UISlidersConfig.getFloat(EXHAUST_PILE_HORIZONTAL) * 100) + "% by " + (UISlidersConfig.getFloat(EXHAUST_PILE_VERTICAL) * 100) + "%.");
            logger.info("energy position: " + (UISlidersConfig.getFloat(ENERGY_HORIZONTAL) * 100) + "% by " + (UISlidersConfig.getFloat(ENERGY_VERTICAL) * 100) + "%.");
        } catch (IOException e) {
            logger.error("UISlidersMod SpireConfig initialization failed");
            logger.error("UI positioning will revert to base game defaults");
            e.printStackTrace();
        }

        Texture badgeImg = new Texture("uislidersmod/images/badge.png");
        ModPanel settingsPanel = new ModPanel();
        BaseMod.registerModBadge(badgeImg, EXTRA_TEXT[0], "JohnnyDevo", EXTRA_TEXT[1], settingsPanel);

        //Draw pile sliders
        settingsPanel.addUIElement(new ModLabel(TEXT[0], (Settings.WIDTH / 2.0f) - (230.0f * Settings.scale), (Settings.HEIGHT / 2.0f) + (45.0f * Settings.scale), settingsPanel, me -> {}));
        drH = new ModMinMaxSlider(TEXT[4], (Settings.WIDTH / 2.0f) - (230.0f * Settings.scale), (Settings.HEIGHT / 2.0f) - (5.0f * Settings.scale), 0.0f, 1.0f, drawToSliderH(UISlidersConfig.getFloat(DRAW_PILE_HORIZONTAL)), null, settingsPanel, slider -> {
            UISlidersConfig.setFloat(DRAW_PILE_HORIZONTAL, sliderToDrawH(slider.getValue()));
            try { UISlidersConfig.save(); } catch (IOException e) { e.printStackTrace(); }
        });
        settingsPanel.addUIElement(drH);
        drV = new ModMinMaxSlider(TEXT[5], (Settings.WIDTH / 2.0f) - (230.0f * Settings.scale), (Settings.HEIGHT / 2.0f) - (55.0f * Settings.scale), 0.0f, 1.0f, drawToSliderV(UISlidersConfig.getFloat(DRAW_PILE_VERTICAL)), null, settingsPanel, slider -> {
            UISlidersConfig.setFloat(DRAW_PILE_VERTICAL, sliderToDiscardV(slider.getValue()));
            try { UISlidersConfig.save(); } catch (IOException e) { e.printStackTrace(); }
        });
        settingsPanel.addUIElement(drV);

        //Discard pile sliders
        settingsPanel.addUIElement(new ModLabel(TEXT[1], (Settings.WIDTH / 2.0f) + (330.0f * Settings.scale), (Settings.HEIGHT / 2.0f) + (45.0f * Settings.scale), settingsPanel, me -> {}));
        diH = new ModMinMaxSlider(TEXT[4], (Settings.WIDTH / 2.0f) + (330.0f * Settings.scale), (Settings.HEIGHT / 2.0f) - (5.0f * Settings.scale), 0.0f, 1.0f, discardToSliderH(UISlidersConfig.getFloat(DISCARD_PILE_HORIZONTAL)), null, settingsPanel, slider -> {
            UISlidersConfig.setFloat(DISCARD_PILE_HORIZONTAL, sliderToDiscardH(slider.getValue()));
            try { UISlidersConfig.save(); } catch (IOException e) { e.printStackTrace(); }
        });
        settingsPanel.addUIElement(diH);
        diV = new ModMinMaxSlider(TEXT[5], (Settings.WIDTH / 2.0f) + (330.0f * Settings.scale), (Settings.HEIGHT / 2.0f) - (55.0f * Settings.scale), 0.0f, 1.0f, discardToSliderV(UISlidersConfig.getFloat(DISCARD_PILE_VERTICAL)), null, settingsPanel, slider -> {
            UISlidersConfig.setFloat(DISCARD_PILE_VERTICAL, sliderToDiscardV(slider.getValue()));
            try { UISlidersConfig.save(); } catch (IOException e) { e.printStackTrace(); }
        });
        settingsPanel.addUIElement(diV);

        //exhaust pile sliders
        settingsPanel.addUIElement(new ModLabel(TEXT[2], (Settings.WIDTH / 2.0f) + (330.0f * Settings.scale), (Settings.HEIGHT / 2.0f) + (245.0f * Settings.scale), settingsPanel, me -> {}));
        exH = new ModMinMaxSlider(TEXT[4], (Settings.WIDTH / 2.0f) + (330.0f * Settings.scale), (Settings.HEIGHT / 2.0f) + (195.0f * Settings.scale), 0.0f, 1.0f, UISlidersConfig.getFloat(EXHAUST_PILE_HORIZONTAL), null, settingsPanel, slider -> {
            UISlidersConfig.setFloat(EXHAUST_PILE_HORIZONTAL, slider.getValue());
            try { UISlidersConfig.save(); } catch (IOException e) { e.printStackTrace(); }
        });
        settingsPanel.addUIElement(exH);
        exV = new ModMinMaxSlider(TEXT[5], (Settings.WIDTH / 2.0f) + (330.0f * Settings.scale), (Settings.HEIGHT / 2.0f) + (145.0f * Settings.scale), 0.0f, 1.0f, UISlidersConfig.getFloat(EXHAUST_PILE_VERTICAL), null, settingsPanel, slider -> {
            UISlidersConfig.setFloat(EXHAUST_PILE_VERTICAL, slider.getValue());
            try { UISlidersConfig.save(); } catch (IOException e) { e.printStackTrace(); }
        });
        settingsPanel.addUIElement(exV);

        //energy orb sliders
        settingsPanel.addUIElement(new ModLabel(TEXT[3], (Settings.WIDTH / 2.0f) - (230.0f * Settings.scale), (Settings.HEIGHT / 2.0f) + (245.0f * Settings.scale), settingsPanel, me -> {}));
        enH = new ModMinMaxSlider(TEXT[4], (Settings.WIDTH / 2.0f) - (230.0f * Settings.scale), (Settings.HEIGHT / 2.0f) + (195.0f * Settings.scale), 0.0f, 1.0f, UISlidersConfig.getFloat(ENERGY_HORIZONTAL), null, settingsPanel, slider -> {
            UISlidersConfig.setFloat(ENERGY_HORIZONTAL, slider.getValue());
            try { UISlidersConfig.save(); } catch (IOException e) { e.printStackTrace(); }
        });
        settingsPanel.addUIElement(enH);
        enV = new ModMinMaxSlider(TEXT[5], (Settings.WIDTH / 2.0f) - (230.0f * Settings.scale), (Settings.HEIGHT / 2.0f) + (145.0f * Settings.scale), 0.0f, 1.0f, UISlidersConfig.getFloat(ENERGY_VERTICAL), null, settingsPanel, slider -> {
            UISlidersConfig.setFloat(ENERGY_VERTICAL, slider.getValue());
            try { UISlidersConfig.save(); } catch (IOException e) { e.printStackTrace();}
        });
        settingsPanel.addUIElement(enV);

        //reset to defaults element
        settingsPanel.addUIElement(new ModLabeledButton(TEXT[6], (Settings.WIDTH / 2.0f) - (20.0f * Settings.scale), (Settings.HEIGHT / 2.0f) + (300.0f * Settings.scale), settingsPanel, button -> {
            setSliderValues(getDefaultValues());
        }));

        //profile management
        ModLabel profile = new ModLabel(TEXT[7], (Settings.WIDTH / 2.0f) + (95.0f * Settings.scale), (Settings.HEIGHT / 2.0f) - (150.0f * Settings.scale), settingsPanel, me -> {});
        settingsPanel.addUIElement(profile);
        ModLabel currentProfile = new ModLabel(UISlidersConfig.getString(SELECTED_PROFILE), (Settings.WIDTH / 2.0f) + (155.0f * Settings.scale), (Settings.HEIGHT / 2.0f) - (190.0f * Settings.scale), settingsPanel, me -> {});
        settingsPanel.addUIElement(currentProfile);
        ModButton leftArrow = new ModButton((Settings.WIDTH / 2.0f) + (85.0f * Settings.scale), (Settings.HEIGHT / 2.0f) - (220.0f * Settings.scale), ImageMaster.loadImage("uislidersmod/images/leftArrow.png"), settingsPanel, me -> {
            int i = UISlidersConfig.getInt(SELECTED_PROFILE);
            --i;
            if (i < 0) {
                i = 9;
            }
            UISlidersConfig.setInt(SELECTED_PROFILE, i);
            currentProfile.text = String.valueOf(i);
            try { UISlidersConfig.save(); } catch (IOException e) { e.printStackTrace();}
        });
        settingsPanel.addUIElement(leftArrow);
        ModButton rightArrow = new ModButton((Settings.WIDTH / 2.0f) + (170.0f * Settings.scale), (Settings.HEIGHT / 2.0f) - (220.0f * Settings.scale), ImageMaster.loadImage("uislidersmod/images/rightArrow.png"), settingsPanel, me -> {
            int i = UISlidersConfig.getInt(SELECTED_PROFILE);
            ++i;
            if (i > 9) {
                i = 0;
            }
            UISlidersConfig.setInt(SELECTED_PROFILE, i);
            currentProfile.text = String.valueOf(i);
            try { UISlidersConfig.save(); } catch (IOException e) { e.printStackTrace();}
        });
        settingsPanel.addUIElement(rightArrow);
        ModLabeledButton saveProfile = new ModLabeledButton(TEXT[8], (Settings.WIDTH / 2.0f) - (010.0f * Settings.scale), (Settings.HEIGHT / 2.0f) - (300.0f * Settings.scale), settingsPanel, button -> {
            saveToProfile(UISlidersConfig.getInt(SELECTED_PROFILE));
        });
        settingsPanel.addUIElement(saveProfile);
        ModLabeledButton loadProfile = new ModLabeledButton(TEXT[9], (Settings.WIDTH / 2.0f) + (190.0f * Settings.scale), (Settings.HEIGHT / 2.0f) - (300.0f * Settings.scale), settingsPanel, button -> {
            loadFromProfile(UISlidersConfig.getInt(SELECTED_PROFILE));
        });
        settingsPanel.addUIElement(loadProfile);
    }

    public static float DECK_X;
    public static float DECK_Y;
    public static float DISCARD_X;
    public static float DISCARD_Y;

    private static void initializeValues() {
        DECK_X = 76f * Settings.scale;
        DECK_Y = 74f * Settings.scale;
        DISCARD_X = 180f * Settings.scale;
        DISCARD_Y = 70f * Settings.scale;
    }

    public static float drawToSliderH(float pos) {
        return pos + (DECK_X / Settings.WIDTH);
    }

    public static float drawToSliderV(float pos) {
        return pos + (DECK_Y / Settings.HEIGHT);
    }

    public static float discardToSliderH(float pos) {
        return pos + (DISCARD_X / Settings.WIDTH);
    }

    public static float discardToSliderV(float pos) {
        return pos + (DISCARD_Y / Settings.HEIGHT);
    }

    public static float sliderToDrawH(float pos) {
        return pos - (DECK_X / Settings.WIDTH);
    }

    public static float sliderToDrawV(float pos) {
        return pos - (DECK_Y / Settings.HEIGHT);
    }

    public static float sliderToDiscardH(float pos) {
        return pos - (DISCARD_X / Settings.WIDTH);
    }

    public static float sliderToDiscardV(float pos) {
        return pos - (DISCARD_Y / Settings.HEIGHT);
    }

    private static float[] getDefaultValues() {
        return new float[]{
                Float.parseFloat(UIDefaults.getProperty(DRAW_PILE_HORIZONTAL)),
                Float.parseFloat(UIDefaults.getProperty(DRAW_PILE_VERTICAL)),
                Float.parseFloat(UIDefaults.getProperty(DISCARD_PILE_HORIZONTAL)),
                Float.parseFloat(UIDefaults.getProperty(DISCARD_PILE_VERTICAL)),
                Float.parseFloat(UIDefaults.getProperty(EXHAUST_PILE_HORIZONTAL)),
                Float.parseFloat(UIDefaults.getProperty(EXHAUST_PILE_VERTICAL)),
                Float.parseFloat(UIDefaults.getProperty(ENERGY_HORIZONTAL)),
                Float.parseFloat(UIDefaults.getProperty(ENERGY_VERTICAL))
        };
    }

    private static String convertToProfile(float[] values) {
        if (values.length != 8) {
            logger.error("Attempted to save a profile with an incorrect length of " + values.length + ".");
            logger.info("falling back to game defaults...");
            values = getDefaultValues();
        }
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < values.length; ++i) {
            b.append(values[i]);
            if (i != values.length - 1) {
                b.append(" ");
            }
        }
        return b.toString();
    }

    private static float[] convertFromProfile(String profile) {
        String[] strings = profile.split(" ");
        if (strings.length != 8) {
            logger.error("Attempted to load a profile with an incorrect length of " + strings.length + ".");
            logger.info("falling back to game defaults...");
            return getDefaultValues();
        }
        float[] retVal = new float[strings.length];
        for (int i = 0; i < strings.length; ++i) {
            retVal[i] = Float.parseFloat(strings[i]);
        }
        return retVal;
    }

    public static float[] getCurrentValues() {
        return new float[]{
                UISlidersConfig.getFloat(DRAW_PILE_HORIZONTAL),
                UISlidersConfig.getFloat(DRAW_PILE_VERTICAL),
                UISlidersConfig.getFloat(DISCARD_PILE_HORIZONTAL),
                UISlidersConfig.getFloat(DISCARD_PILE_VERTICAL),
                UISlidersConfig.getFloat(EXHAUST_PILE_HORIZONTAL),
                UISlidersConfig.getFloat(EXHAUST_PILE_VERTICAL),
                UISlidersConfig.getFloat(ENERGY_HORIZONTAL),
                UISlidersConfig.getFloat(ENERGY_VERTICAL)
        };
    }

    public static void saveToProfile(int profile) {
        String s = convertToProfile(getCurrentValues());
        UISlidersConfig.setString(PROFILE + profile, s);
        try {
            UISlidersConfig.save();
        } catch(IOException e) {
            logger.error("Failed to save new values to profile " + profile + ".");
            e.printStackTrace();
        }
    }

    public static void loadFromProfile(int profile) {
        float[] values;
        if (UISlidersConfig.has(PROFILE + profile)) {
            String p;
            p = UISlidersConfig.getString(PROFILE + profile);
            values = convertFromProfile(p);
        } else {
            values = getDefaultValues();
        }
        setSliderValues(values);
    }

    public static void setSliderValues(float[] values) {
        if (values.length != 8) {
            logger.error("Attempted to set values with an incorrect length of " + values.length + ".");
            logger.info("falling back to game defaults...");
            values = getDefaultValues();
        }
        drH.setValue(drawToSliderH(values[0]));
        drV.setValue(drawToSliderV(values[1]));
        diH.setValue(discardToSliderH(values[2]));
        diV.setValue(discardToSliderV(values[3]));
        exH.setValue(values[4]);
        exV.setValue(values[5]);
        enH.setValue(values[6]);
        enV.setValue(values[7]);
        saveAll();
    }

    public static void saveAll() {
        UISlidersConfig.setFloat(DRAW_PILE_HORIZONTAL, sliderToDrawH(drH.getValue()));
        UISlidersConfig.setFloat(DRAW_PILE_VERTICAL, sliderToDrawV(drV.getValue()));
        UISlidersConfig.setFloat(DISCARD_PILE_HORIZONTAL, sliderToDiscardH(diH.getValue()));
        UISlidersConfig.setFloat(DISCARD_PILE_VERTICAL, sliderToDiscardV(diV.getValue()));
        UISlidersConfig.setFloat(EXHAUST_PILE_HORIZONTAL, exH.getValue());
        UISlidersConfig.setFloat(EXHAUST_PILE_VERTICAL, exV.getValue());
        UISlidersConfig.setFloat(ENERGY_HORIZONTAL, enH.getValue());
        UISlidersConfig.setFloat(ENERGY_VERTICAL, enV.getValue());
        try {
            UISlidersConfig.save();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
