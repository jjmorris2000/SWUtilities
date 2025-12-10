package osu.grading.gui;

import java.nio.file.Path;
import java.nio.file.Paths;

import osu.grading.ExpanderTools;

public class CanvasExpandController {

    private String modelInputArchive;
    private String modelOutputDir;

    private CanvasExpandGUI gui;

    public CanvasExpandController(CanvasExpandGUI gui) {
        this.modelInputArchive = "";
        this.modelOutputDir = "";

    }

    public void updateModel(String input, String output) {
        this.modelInputArchive = input;
        this.modelOutputDir = output;
    }

    public void expand() {
        System.out.println("Running the expander...");
        System.out.print("Expanding ");
        System.out.print(this.modelInputArchive);
        System.out.print(" to ");
        System.out.print(this.modelOutputDir);
        Path zipfile = Paths.get(this.modelInputArchive);
        if (!zipfile.toFile().exists() || zipfile.toFile().isDirectory()) {
            System.out.println("Error - zipfile must be an existing archive file.");
            return;
        }
        Path outDir = Paths.get(this.modelOutputDir);
        if (!outDir.toFile().exists() || !outDir.toFile().isDirectory()) {
            System.out.println("Error - output_directory must be an existing directory.");
            return;
        }
        ExpanderTools.unzipCanvasFile(zipfile, outDir);
        ExpanderTools.updateProjectFiles(outDir);
    }

}
