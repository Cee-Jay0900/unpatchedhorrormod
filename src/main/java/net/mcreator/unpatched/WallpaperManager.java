/**
 * The code of this mod element is always locked.
 *
 * You can register new events in this class too.
 *
 * If you want to make a plain independent class, create it using
 * Project Browser -> New... and make sure to make the class
 * outside net.mcreator.unpatched as this package is managed by MCreator.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
 *
 * This class will be added in the mod root package.
*/
package net.mcreator.unpatched;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class WallpaperManager {

    private static final String userBackground = getCurrentBackground();
    private static String overriddenBackground = null;

    private static final String SET_WALLPAPER_SCRIPT =
        """
        Add-Type -AssemblyName System.Drawing
        Add-Type @"
        using System;
        using System.Runtime.InteropServices;
        public class Wallpaper {
            [DllImport("user32.dll", CharSet = CharSet.Auto)]
            public static extern int SystemParametersInfo(int uAction, int uParam, string lpvParam, int fuWinIni);
        }
        "@
        $bmpPath = "$env:TEMP\\wallpaper.bmp"
        $img = [System.Drawing.Image]::FromFile('%s')
        $img.Save($bmpPath, [System.Drawing.Imaging.ImageFormat]::Bmp)
        $img.Dispose()
        [Wallpaper]::SystemParametersInfo(20, 0, $bmpPath, 3)
        """;

    /**
     * Gets the user's initial background, before any changes were made.
     */
    public static String getUserBackground()  { return userBackground; }
    /**
     * Gets the overridden background.
     */
    public static String getOverriddenBackground()   { return overriddenBackground;  }

    private static boolean isNotWindows() { return !System.getProperty("os.name").toLowerCase().startsWith("win"); }

    private static void runPowerShellScript(String script) throws Exception {
        File ps1 = new File(System.getProperty("java.io.tmpdir"), "set_wallpaper.ps1");
        Files.writeString(ps1.toPath(), script);
        new ProcessBuilder("powershell", "-ExecutionPolicy", "Bypass", "-WindowStyle", "Hidden", "-File", ps1.getAbsolutePath()).redirectErrorStream(true).start().waitFor();
    }

    private static void setWallpaperFromFile(String filePath) {
        try {runPowerShellScript(SET_WALLPAPER_SCRIPT.formatted(filePath.replace("\\", "\\\\"))); }
        catch (Exception e) { System.out.println("Failed to set wallpaper from file " + filePath + ": " + e.getMessage()); }
    }

    /**
     * Gets the current background as a Path in the form of a String.
     */
    public static String getCurrentBackground() {
        if (isNotWindows()) return null;
        try {
            File ps1 = new File(System.getProperty("java.io.tmpdir"), "get_wallpaper.ps1");
            Files.writeString(ps1.toPath(), "Get-ItemPropertyValue -Path \"Registry::HKEY_CURRENT_USER\\Control Panel\\Desktop\" -Name Wallpaper");
            Process process = new ProcessBuilder("powershell", "-ExecutionPolicy", "Bypass", "-WindowStyle", "Hidden", "-File", ps1.getAbsolutePath())
                .redirectErrorStream(true)
                .start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String output = reader.readLine();
                process.waitFor();
                if (output != null && !output.isBlank()) {
                    File f = new File(output.trim());
                    return (f.exists() && !f.isDirectory()) ? f.getPath() : null;
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to get current background: " + e.getMessage());
        }
        return null;
    }

    /**
     * Restores the user's background to its initial state, before any changes were made.
     */
    public static void restoreUserBackground() {
        if (isNotWindows() || userBackground == null || userBackground.isEmpty()) return;
        try {
            if (userBackground.equals(getCurrentBackground())) System.out.println("User changed wallpaper during play — skipping restore.");
            else {
                setWallpaperFromFile(userBackground);
                System.out.println("Restored original wallpaper: " + userBackground);
            }
        } catch (Exception e) { System.out.println("Failed to restore original wallpaper: " + e); }
    }

    /**
     * Sets the background from a resource path. If used in a minecraft mod, this is usually found in the assets' folder.
     * @param resourcePath The resource path, i.e. <code>/assets/my_mod/textures/wallpaper/my_wallpaper.png</code>
     */
    public static void setBackground(String resourcePath, String outputName) {
        if (isNotWindows()) return;
        if (userBackground == null || userBackground.isEmpty()) {
            System.out.println("[WallpaperManager] Cannot set background: original wallpaper not saved.");
            return;
        }
        try {
            File image = exportResource(resourcePath);
            setWallpaperFromFile(image.getAbsolutePath());
            overriddenBackground = getCurrentBackground();
        } catch (Exception e) { System.out.println("[WallpaperManager] Error setting background: " + e); }
    }

    /**
     * Returns a File from a resource location.
     * @param resourcePath The path to the resource as a String
     * @return A file from the resource Path.
     * @throws IOException if the file can't be found or if an I/O error occurs when reading or writing
     */
    public static File exportResource(String resourcePath) throws IOException {
        InputStream stream = WallpaperManager.class.getResourceAsStream(resourcePath);
        if (stream == null) throw new FileNotFoundException("Resource not found: " + resourcePath);
        String fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
        File tempFile = new File(System.getProperty("java.io.tmpdir"), fileName);
        Files.copy(stream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return tempFile;
    }

}