package osu.grading;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import osu.grading.gui.CanvasExpandController;
import osu.grading.gui.CanvasExpandGUI;

/**
 * CanvasExpand - a program to automatically expand Canvas zip submission
 * archives into separate folders.
 *
 * @author Jeremy Morris
 */
public class CanvasExpand {

    public static void main(String[] args) {
        if (args.length < 2) {
            CanvasExpandGUI gui = new CanvasExpandGUI();
            CanvasExpandController controller = new CanvasExpandController(gui);
            gui.registerController(controller);
            return;
        }
        System.out.println(Arrays.toString(args));
        Path zipfile = Paths.get(args[0]);
        if (!zipfile.toFile().exists() || zipfile.toFile().isDirectory()) {
            System.out.println("Error - zipfile must be an existing archive file.");
            return;
        }
        Path outDir = Paths.get(args[1]);
        if (!outDir.toFile().exists() || !outDir.toFile().isDirectory()) {
            System.out.println("Error - output_directory must be an existing directory.");
            return;
        }
        ExpanderTools.unzipCanvasFile(zipfile, outDir);
        ExpanderTools.updateProjectFiles(outDir);
    }

}
