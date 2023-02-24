/*-
 * Copyright 2022 Maxar Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and

 * limitations under the License.
 *
 * SBIR DATA RIGHTS
 * Contract No. HM0476-16-C-0022
 * Contractor Name: Maxar Technologies
 * Contractor Address: 2325 Dulles Corner Blvd. STE 1000, Herndon VA 20171
 * Expiration of SBIR Data Rights Period: 2/13/2029
 *
 * The Government's rights to use, modify, reproduce, release, perform, display,
 * or disclose technical data or computer software marked with this legend are
 * restricted during the period shown as provided in paragraph (b)(4) of the
 * Rights in Noncommercial Technical Data and Computer Software-Small Business
 * Innovation Research (SBIR) Program clause contained in the above identified
 * contract. No restrictions apply after the expiration date shown above. Any
 * reproduction of technical data, computer software, or portions thereof marked
 * with this legend must also reproduce the markings.
 */

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Utility class for parsing file names.
 */
public class ParseFilename {

    /**
     * The length of the DC image ID identifier.
     */
    public static final int ID_LENGTH = StaticSettings.DC_BIT_ID.length();

    /**
     * Prevents the creation of {@link ParseFilename}.
     */
    private ParseFilename() {
        // Prevents the creation of the class.
    }

    public static ImageFileRef parse(String filename) throws Exception {
        return FileNamerFactory.getByFilename(filename).parseFilename(filename);
    }

    /**
     * Removes the file extension from the filename and returns the stripped filename.
     *
     * @param filename the file name
     * @return the file name without the file extension
     */
    public static String stripExt(String filename) {
        return filename.replaceAll("\\.([^.]*)$", "");
    }

    /**
     * Returns the file extension from the filename.
     *
     * @param filename the file name
     * @return the file extension
     */
    public static String getExt(String filename) {
        String[] tokens = filename.split("\\.(?=[^\\.]+$)");
        if (tokens.length > 1) {
            return tokens[1];
        }
        return null;
    }

    /**
     * Returns true if the file extension of the supplied filename is considered an image extension.
     *
     * @param filename the file name
     * @return true if the file is an image, false if not
     */
    public static boolean isImage(String filename) {

        String ext = String.valueOf(getExt(filename)).toUpperCase();

        return isImageExt(ext);
    }

    public static boolean isImage(File item) {
        return isImage(item.getName());
    }

    /**
     * Returns true if the extension is considered an image extension.
     *
     * @param ext the file extension
     * @return true if the extension is an image extension, false if not
     */
    public static boolean isImageExt(String ext) {
        if (ext == null) {
            return false;
        }
        try {
            // If the extension is able to be converted to an image extension enum, the extension is
            // consider an image.
            //ImageExtensions.valueOf(ext.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Renames a file with the extension in lower case.
     *
     * @param file the name of the file
     * @return the new file
     */
    public static File lowerCaseExt(File file) {
        String ext = FilenameUtils.getExtension(file.toString());
        ext = (ext == null) ? null : ext.toLowerCase();
        if (!StringUtils.isEmpty(ext)) {
            String newName = String.format("%s.%s", FilenameUtils.removeExtension(file.getName()), ext);
            return new File(file.getParent(), newName);
        }

        return file;
    }

    /**
     * Checks the file's extension for compatibility with other components such as Deepcore server and
     * OSSIM. If the file extension is an r0 file, the file extension will be changed. If the file is
     * compatible, the same file will be returned.
     *
     * @param file the file to check
     * @return a file with the new extension if it needed to be changed
     */
    public static File determineCompatibilityFilename(File file) {
        var extension = FilenameUtils.getExtension(file.toString());

        if (StringUtils.isNotBlank(extension) && extension.equals("r0")) {
            String newName =
                    String.format("%s.%s", FilenameUtils.removeExtension(file.getName()), "nitf");
            return new File(file.getParent(), newName);
        }

        return file;
    }

    /**
     * Checks to see if the file is a sidecar file for the image file.
     *
     * @param filename the file name
     * @param imageFilename the image file name
     * @return true if the file is a sidecar file, false if not
     */
    public static boolean isSidecar(String filename, String imageFilename) {
        filename = new File(filename).getName();
        return !isImage(filename)
                && (filename.startsWith(stripExt(imageFilename)) || filename.startsWith(imageFilename));
    }

    /**
     * Takes a filename and tests to see if it is a related variant.
     *
     * @param filename the file name
     * @param catId the catalog ID
     * @return true if the file is an image variant, false if not
     */
    public static boolean isImageVariant(String filename, String catId) {
        return (filename != null && catId != null) && new File(filename).getName().startsWith(catId);
    }

    /**
     * Tests to see if the supplied filename is an ossim overview file to the supplied imageFilename.
     *
     * @param filename the file name
     * @param imageFilename the image file name
     * @return true if the file is an ossim overview, false if not
     */
    public static boolean isOverView(String filename, String imageFilename) {
        return isSidecar(filename, imageFilename) && filename.endsWith(".ovr");
    }

    /**
     * Tests to see if the supplied filename is an ossim histogram (.his) file to the supplied imageFilename.
     *
     * @param filename the file name
     * @param imageFilename the image file name
     * @return true if the file is an ossim histogram, false if not
     */
    public static boolean isHistogram(String filename, String imageFilename) {
        return isSidecar(filename, imageFilename) && filename.endsWith(".his");
    }

    /**
     * Tests to see if the supplied filename is a metadata file to the supplied imageFilename.
     *
     * @param filename the name of the file
     * @param imageFilename the image file name
     * @return true if the file is a metadata file, false it not
     */
    public static boolean isMetadata(String filename, String imageFilename) {
        return isSidecar(filename, imageFilename) && filename.endsWith(".metadata.json");
    }

}