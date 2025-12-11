package osu.grading;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipInputStream;

/**
 * ZipTools - library to hold convenience methods dealing with ZIP archives.
 *
 * @author Jeremy Morris
 */
public class ZipTools {

    private ZipTools() {
    }

    /**
     * Wrapper method to write an arbitrary zip input stream to a file.
     *
     * @param p
     *            path to output file
     * @param zis
     *            input stream to write to the file
     * @throws IOException
     *             if the output file cannot be written
     */
    public static void writeZstreamToFile(Path p, ZipInputStream zis) throws IOException {
        byte[] buffer = new byte[2048];
        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(p.toFile()))) {
            int length = zis.read(buffer);
            while (length > 0) {
                bos.write(buffer, 0, length);
                length = zis.read(buffer);
            }
        }
    }
}
