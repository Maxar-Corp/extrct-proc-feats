/*
 * Copyright 2022 Maxar Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * SBIR DATA RIGHTS Contract No. HM0476-16-C-0022 Contractor Name: Maxar Technologies Contractor
 * Address: 2325 Dulles Corner Blvd. STE 1000, Herndon VA 20171 Expiration of SBIR Data Rights
 * Period: 2/13/2029
 *
 * The Government's rights to use, modify, reproduce, release, perform, display, or disclose
 * technical data or computer software marked with this legend are restricted during the period
 * shown as provided in paragraph (b)(4) of the Rights in Noncommercial Technical Data and Computer
 * Software-Small Business Innovation Research (SBIR) Program clause contained in the above
 * identified contract. No restrictions apply after the expiration date shown above. Any
 * reproduction of technical data, computer software, or portions thereof marked with this legend
 * must also reproduce the markings.
 */

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementation of the file namer for version 1.
 */
public class VersionOneFileNamer implements FileNamer {

    private static final String VERSION = "1";

    private static final Pattern gsdPattern =
            Pattern.compile(String.format("%s\\d+", GsdFeature.FILENAME_KEY));
    private static final Pattern sharpnessPattern =
            Pattern.compile(String.format("%s\\d+", SharpenFeature.FILENAME_KEY));
    private static final Pattern brightnessPattern =
            Pattern.compile(String.format("%s(-)?\\d+", BrightnessFeature.FILENAME_KEY));

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public ImageFileRef parseFilename(String filename) throws Exception {
        filename = formatFilename(filename, true);

        ImageFileRef imageFileRef = new ImageFileRef();
        int dcbitPos = filename.indexOf(String.format("_%s", StaticSettings.DC_BIT_ID));

        String catId = ParseFilename.stripExt(filename);
        imageFileRef.setOriginalFilename(filename);
        imageFileRef.setCatId(catId);

        if (dcbitPos > -1) {
            String[] parts = catId.substring(dcbitPos + 1).split("_");
            final String version = parts[0].substring(ParseFilename.ID_LENGTH);
            catId = filename.substring(0, dcbitPos);
            imageFileRef.setOriginalFilename(filename);
            imageFileRef.setCatId(catId);
            imageFileRef.setVersion(version);
            imageFileRef.setProcessingString(getProcessingString(parts));

            String[] variableValues = getVariableValue(parts);

            if (variableValues[0] != null) {
                imageFileRef.getParams().setFeature(FeatureMapFactory.getVersion(version).findFeature(
                        GsdFeature.createFeatureName(GsdFeature.valueFromFeatureName(variableValues[0]))));
            }

            if (variableValues[1] != null) {
                imageFileRef.getParams()
                        .setFeature(FeatureMapFactory.getVersion(version).findFeature(SharpenFeature
                                .createFeatureName(SharpenFeature.valueFromFeatureName(variableValues[1]))));
            }

            if (variableValues[2] != null) {
                imageFileRef.getParams()
                        .setFeature(FeatureMapFactory.getVersion(version).findFeature(BrightnessFeature
                                .createFeatureName(BrightnessFeature.valueFromFeatureName(variableValues[2]))));
            }

            imageFileRef.setCroppedHash(getCroppedHash(parts));
            return imageFileRef;
        } else {
            imageFileRef.setVersion(StaticSettings.DEFAULT_ID_VERISON);
        }

        return imageFileRef;
    }

    @Override
    public String getFilename(ImageFileRef imageFileRef) {
        return getFilename(imageFileRef, true);
    }

    @Override
    public String getFilename(ImageFileRef imageFileRef, boolean withExt) {
        // early out if the file reference is invalid
        if (imageFileRef.getType() == ImageFileRefType.INVALID) {
            return imageFileRef.getOriginalFilename();
        }

        List<String> parts = new ArrayList<>();
        parts.add(imageFileRef.calcCatId());
        ProcessingParams params = imageFileRef.getParams();
        String processing = params.toString();
        if (!StringUtils.isEmpty(processing)) {
            parts.add(processing);
        }
        GsdFeature gsd = params.getGsd();
        SharpenFeature sharpness = params.getSharpen();
        List<String> variableParts = new ArrayList<>();
        if (gsd != null) {
            variableParts.add(gsd.getFilenameValue());
        }
        if (sharpness != null) {
            variableParts.add(sharpness.getFilenameValue());
        }

        // Get the brightness feature, if present, for the file name.
        BrightnessFeature brightness = params.getBrightness();
        if (brightness != null) {
            variableParts.add(brightness.getFilenameValue());
        }

        if (!variableParts.isEmpty()) {
            parts.add(String.join("", variableParts));
        }
        String croppedHash = imageFileRef.getCroppedHash();
        if (!StringUtils.isEmpty(croppedHash)) {
            parts.add(croppedHash);
        }
        if (parts.size() > 1) {
            parts.add(1, StaticSettings.DC_BIT_ID + VERSION);
        }
        String filename = String.join("_", parts);
        String ext = imageFileRef.getFileExt();
        if (!StringUtils.isEmpty(ext) && withExt) {
            filename = filename + "." + ext;
        }
        return filename;
    }

    private static String getProcessingString(String[] parts) {

        if (parts.length > 1 && !parts[1].startsWith(GsdFeature.FILENAME_KEY)
                && !parts[1].startsWith(SharpenFeature.FILENAME_KEY)
                && !parts[1].startsWith(BrightnessFeature.FILENAME_KEY)) {
            return parts[1];
        }

        return null;
    }

    /**
     * Returns the croppedHash section of the filename.
     *
     * @param parts file name parts
     * @return the cropped hash section
     */
    private static String getCroppedHash(String[] parts) {
        if (parts.length > 2) {
            int i = parts.length - 1;
            if (parts[i] != null && !parts[i].startsWith("gsd") && !parts[i].startsWith("os")
                    && !parts[i].startsWith("ob")) {
                return parts[i];
            }
        }

        return null;
    }

    /**
     * Finds the variable section of the file name.
     *
     * @param parts the file name parts
     * @return the variable value
     */
    private static String[] getVariableValue(String[] parts) {
        String[] rtnValues = new String[3];
        for (String part : parts) {
            if (part.startsWith("gsd") || part.startsWith("os") || part.startsWith("ob")) {
                rtnValues[0] = getMatchedPatternOrNull(gsdPattern, part);
                rtnValues[1] = getMatchedPatternOrNull(sharpnessPattern, part);
                rtnValues[2] = getMatchedPatternOrNull(brightnessPattern, part);
            }
        }
        return rtnValues;
    }

    /**
     * Returns the matched patter or null if not found.
     *
     * @param pattern the pattern to match
     * @param string the string to match
     * @return the matched pattern or null if not found
     */
    private static String getMatchedPatternOrNull(Pattern pattern, String string) {
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    @Override
    public String formatCatId(String catId) {
        return formatFilename(catId, false);
    }

    /**
     * Formats the filename/catId appropriately.
     *
     * @param filename the file name
     * @param withExt should the extension be included
     * @return the formatted file name
     */
    public String formatFilename(String filename, boolean withExt) {
        String newFilename = StringUtils.isEmpty(filename) ? null
                : new File(filename).getName().trim().replace(" ", "_").replaceAll("_+", "_");
        return withExt ? newFilename : ParseFilename.stripExt(newFilename);
    }

    public String getDescriptiveName(ImageFileRef imageFileRef) {
        List<String> parts = new ArrayList<>();
        parts.add(imageFileRef.calcCatId());
        ProcessingParams params = imageFileRef.getParams();
        String processing;
        try {
            processing = String.format("[%s]",
                    params.getFeatures().stream().map(Feature::getFeature).collect(Collectors.joining(",")));
        } catch (Exception e) {
            processing = null;
        }
        if (!StringUtils.isEmpty(processing)) {
            parts.add(processing);
        }
        Feature gsd = params.getGroup(FeatureGroups.GSD);
        if (gsd != null) {
            parts.add(gsd.getFeature().replace(" ", ""));
        }
        String croppedHash = imageFileRef.getCroppedHash();
        if (!StringUtils.isEmpty(croppedHash)) {
            parts.add(croppedHash);
        }
        if (parts.size() > 1) {
            parts.add(1, StaticSettings.DC_BIT_ID + VERSION);
        }
        String filename = String.join("_", parts);
        String ext = imageFileRef.getFileExt();
        filename = filename + "." + ext;

        return filename;
    }

}
