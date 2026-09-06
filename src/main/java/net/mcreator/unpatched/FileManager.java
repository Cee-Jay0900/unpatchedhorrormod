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
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@SuppressWarnings("removal")
public class FileManager {

    /**
     * Creates a new file onto the user's desktop, or wherever {@link FileSystemView#getHomeDirectory()} may be.
     * <p>
     * Fails if the file already exists, if an I/O error occurred, or if a security manager exists and its {@link SecurityManager#checkWrite(String)} method denies write access to the file
     * @param fileName The file name of the new file, including the extension. I.e. <code>my_file.txt</code>
     * @param content The content within the new file.
     */
    public static void createFileOnDesktop(String fileName, String content) {
        File desktop = FileSystemView.getFileSystemView().getHomeDirectory();
        File file = new File(desktop, fileName);
        try {
            if (!file.exists() || !file.createNewFile()) System.out.println("File already exists or failed to create: " + file.getAbsolutePath());
            try (FileWriter writer = new FileWriter(file, false)) {
                writer.write(content);
                System.out.println("File written successfully: " + file.getAbsolutePath());
            }
        } catch (IOException e) { System.out.println("Failed to create or write to file: " + file.getAbsolutePath()); }
    }


    /**
     * Opens a file from the user's desktop, or wherever {@link FileSystemView#getHomeDirectory()} may be.
     * @param fileName The file name, including extension, to open.
     */
    public static void openFileOnDesktop(String fileName) {
        File file = FileSystemView.getFileSystemView().getHomeDirectory().toPath().resolve(fileName).toFile();
        try { Desktop.getDesktop().open(file); } catch (Exception e) { System.out.println("Could not open desktop file: " + e); }
    }

    /**
     * Opens the camera app (if found)
     */
    @SuppressWarnings("deprecation")
    public static void openCamera() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) Runtime.getRuntime().exec("explorer.exe shell:AppsFolder\\Microsoft.WindowsCamera_8wekyb3d8bbwe!App");
            else if (os.contains("mac")) Runtime.getRuntime().exec("open /Applications/Photo Booth.app");
            else if (os.contains("nix") || os.contains("nux")) Runtime.getRuntime().exec("cheese");
        } catch (Exception e) { System.out.println("Failed to open camera app: " + e); }
    }

    /**
     * Opens a website via a link.
     * @param link The link to open, as a String.
     */
    public static void openWebsite(String link) {
        try {
            URI uri = new URI(link);
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) Desktop.getDesktop().browse(uri);
            else System.out.println("Desktop browsing not supported on this platform.");
        } catch (Exception e) { System.out.println("Could not open website: " + e); }
    }

}