package osu.grading;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import osu.grading.ZipTools.ZipToolsException;

/**
 * ExpanderTools - methods to deal with ZIP archives of student work produced by
 * Canvas.
 *
 * @author Jeremy Morris
 */
public class ExpanderTools {

    /**
     * list of files to expand along with any .java files.
     */
    private static Set<String> files = new HashSet<>();
    static {
        files.add(".checkstyle");
        files.add(".project");
        files.add(".classpath");
        files.add("org.eclipse.core.resources.prefs");

    }

    /**
     * List of project directories that must be created.
     */
    private static Set<String> dirs = new HashSet<>();
    static {
        dirs.add(".settings/");
        dirs.add("bin/");
        dirs.add("data/");
        dirs.add("doc/");
        dirs.add("lib/");
        dirs.add("src/");
        dirs.add("test/");
    }

    /**
     * Creates the project space based on the directories in dirs. This ensures
     * that all required Eclipse directories are created.
     *
     * @param p
     *            - root path to add project space to
     * @throws IOException
     */
    private static void createProjectSpace(Path p) throws IOException {
        for (String dir : dirs) {
            Path p1 = p.resolve(dir);
            if (!Files.exists(p1)) {
                Files.createDirectories(p1);
            }
        }
    }

    /**
     * Unzips a Zip InputStream to a folder in the directory outDir. Prepends a
     * prefix to the front of the output directory stored in the file. This
     * function presumes that we are using Canvas created Zip files of Zip
     * files.
     *
     * @param prefix
     *            prefix to put at the front of the output directory
     * @param outDir
     *            directory to output our unzipped directory to
     * @param is
     *            zip input stream to unzip
     */
    private static void decodeZipStream(String prefix, Path outDir, InputStream is) {
        try (ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry ent = zis.getNextEntry();
            while (ent != null) {
                Path p1 = Paths.get(ent.getName()).getName(0);
                Path projectPath = outDir.resolve(prefix + "_" + p1.toString());
                createProjectSpace(projectPath);
                Path p = outDir.resolve(prefix + "_" + ent.getName());
                if (Files.exists(p.getParent())) {
                    String name = p.getFileName().toString();
                    if (name.endsWith(".java") || files.contains(name)) {
                        System.out.println("Expanding --> " + p);
                        ZipTools.writeZstreamToFile(p, zis);
                    }
                }
                ent = zis.getNextEntry();
            }

        } catch (IOException e) {
            System.err.println("Error reading Zip stream ");
            e.printStackTrace();
            System.exit(-1);
        }

    }

    /**
     * Unzips a zipFile of Eclipse projects in Canvas assignment export format
     * to an output directory. Updates the Eclipse project names to have the
     * team name at the front of the folder.
     *
     * @param zipFile
     *            zipfile in Canvas format to unzip
     * @param outputDir
     *            directory to write project folders to
     */
    public static void unzipCanvasFile(Path zipFile, Path outputDir)
            throws ZipToolsException {
        try (ZipFile zFile = new ZipFile(zipFile.toFile())) {
            @SuppressWarnings("unchecked")
            Iterator<ZipEntry> it = (Iterator<ZipEntry>) zFile.stream().iterator();
            while (it.hasNext()) {
                ZipEntry e = it.next();
                if (e.getName().endsWith(".zip")) {
                    System.out.println("Expanding ZIP file " + e);
                    decodeZipStream(e.getName().split("_")[0], outputDir,
                            zFile.getInputStream(e));
                } else if (e.getName().endsWith(".pdf")) {
                    System.out.println("Skipping PDF file " + e);
                } else {
                    System.out.println("UNKNOWN FILE " + e);
                }
            }
        } catch (IOException e) {
            throw new ZipToolsException(
                    "Error reading from zip file in archive " + zipFile);
        }
    }

    /**
     * Fixes the project files to include the name of the team or individual on
     * the project folder so the name in the .project file matches the name on
     * the project folder.
     *
     * @param zipOutputDir
     *            directory where projects were expanded to
     */
    public static void updateProjectFiles(Path zipOutputDir) {
        // open output dir
        for (File fileEntry : zipOutputDir.toFile().listFiles()) {
            // cycle through all dirs in the output dir
            // get the prefix off the last element of the directory name
            String prefix = fileEntry.toPath().getFileName().toString().split("_")[0];

            if (fileEntry.isDirectory()) {
                for (File project : fileEntry
                        .listFiles((dir, name) -> name.equals(".project"))) {
                    System.out.println("Updating .project file: " + project);
                    // send each .project file to the update function
                    XMLTools.updateProjectFile(prefix, project.toPath());
                }

            }

        }
    }

    /**
     * Test main program
     *
     * @param args
     *            - ignored
     */
    public static void main(String[] args) {
        String zipFname = "data/submissions.zip";
        String zipOutputDir = "data/output/";

        Path zipFile = Paths.get(zipFname);
        Path outPath = Paths.get(zipOutputDir);
        System.out.println(outPath);

        try {
            unzipCanvasFile(zipFile, outPath);
            updateProjectFiles(outPath);
        } catch (ZipToolsException e) {
            e.printStackTrace();
        }

    }

}
