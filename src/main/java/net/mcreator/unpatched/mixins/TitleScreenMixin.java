package net.mcreator.unpatched.mixins;

import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.OptionsScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin() {
        super(Component.translatable("narrator.screen.title"));
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        // 1. Clear out ALL default components and widgets
        this.clearWidgets();

        // 2. Set up layout positions (centered layout)
        int buttonWidth = 200;
        int buttonHeight = 20;
        int centerX = this.width / 2 - (buttonWidth / 2);
        int startY = this.height / 4 + 48; // Position under the main title logo
        int spacing = 24;                 // Pixel space between buttons

        // 3. Add Singleplayer Button
        this.addRenderableWidget(Button.builder(Component.translatable("menu.singleplayer"), (button) -> {
            this.minecraft.setScreen(new SelectWorldScreen(this));
        }).bounds(centerX, startY, buttonWidth, buttonHeight).build());

        // 4. Add Multiplayer Button
        this.addRenderableWidget(Button.builder(Component.translatable("menu.multiplayer"), (button) -> {
            this.minecraft.setScreen(new JoinMultiplayerScreen(this));
        }).bounds(centerX, startY + spacing, buttonWidth, buttonHeight).build());

        // 5. Add Options Button
        this.addRenderableWidget(Button.builder(Component.translatable("menu.options"), (button) -> {
            this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
        }).bounds(centerX, startY + (spacing * 2), buttonWidth, buttonHeight).build());

        // 6. Add Quit Game Button (Created, Disabled, then Darkened out)
        Button quitButton = Button.builder(Component.translatable("menu.quit"), (button) -> {
            this.triggerHorrorPopup();
            this.minecraft.close();
        }).bounds(centerX, startY + (spacing * 3), buttonWidth, buttonHeight).build();
        
        quitButton.active = false;
        this.addRenderableWidget(quitButton);
    }

    // Intercepts the Escape key closure pathway for this screen specifically
    @Override
    public void onClose() {
        this.triggerHorrorPopup();
        super.onClose();
    }

    // Custom helper method that handles launching the authentic Windows pop-up alert
    private void triggerHorrorPopup() {
        try {
            // Forces the window to use the operating system's native look and feel layout 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback if styling fails
        }

        // Creates a standard system modal warning dialog box
        JOptionPane.showMessageDialog(
            null, 
            "err.sys.0", // The main message inside the box
            "SYSTEM ERROR",                     // The title bar text of the pop-up windows frame
            JOptionPane.ERROR_MESSAGE           // Adds the classic red 'X' warning icon
        );
    }
}
