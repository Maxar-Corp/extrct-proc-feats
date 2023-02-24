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


import java.util.Locale;

/**
 * This feature allows for variability in the brightness processing options.
 */
public class BrightnessFeature extends Feature {
    public static final String BRIGHTNESS_KEY = "ossim-brightness";
    public static final String FILENAME_KEY = "ob";
    public static final int ID_BASE = -30000;

    private static final String BRIGHTNESS_DESCRIPTION =
            "ossim brightness manipulation set to -1.00 - 1.00";
    private static final String OSSIM_COMMAND = "--brightness %s";
    private static final String COMMAND_FORMAT = "%s %s";
    private static final String PLACEHOLDER = "<?>";
    private static final String FEATURE_TYPE = "double";

    private int brightnessPercent;
    private double brightnessValue;

    // Even though these fields exist in the superclass, when looking up the field via reflection,
    // there are issues finding the field via fieldname. It was simpler to just add the field in
    // this class and ensure that the same value is set in the super class as well.
    protected final String ossimNonOrthoCmd;
    protected final String ossimOrthoCmd;

    /**
     * Creates a new instance of {@link BrightnessFeature}.
     */
    public BrightnessFeature() {
        super();

        id = -3;
        group = FeatureGroups.ADJUSTMENTS;
        feature = String.format(COMMAND_FORMAT, BRIGHTNESS_KEY, PLACEHOLDER);
        value = PLACEHOLDER;
        type = FEATURE_TYPE;
        description = BRIGHTNESS_DESCRIPTION;
        units = FEATURE_TYPE;

        ossimNonOrthoCmd = String.format(COMMAND_FORMAT, OSSIM_COMMAND, PLACEHOLDER);
        ossimOrthoCmd = String.format(COMMAND_FORMAT, OSSIM_COMMAND, PLACEHOLDER);

        super.ossimNonOrthoCmd = ossimNonOrthoCmd;
        super.ossimOrthoCmd = ossimOrthoCmd;
        super.canGenerate = true;
        super.canOrder = false;
    }

    /**
     * Creates a new instance of {@link BrightnessFeature}.
     *
     * @param featureName the name of the feature
     *
     * @throws Exception invalid brightness value
     */
    public BrightnessFeature(String featureName) throws Exception {
        super(featureName);

        group = FeatureGroups.ADJUSTMENTS;
        brightnessValue = valueFromFeatureName(featureName);
        brightnessPercent = (int) (brightnessValue * 100);
        units = FEATURE_TYPE;

        // Ensure the value is valid. Ossim supports any double that fits in a 64 bit float, however,
        // to make sure the value is able to fit the filenaming and DC image ID schema, it is limited
        // to values with only two decimal places, ie. -.75 or .54.
        if (brightnessPercent > 100 || brightnessPercent < -100) {
            throw new Exception(
                    String.format("Brightness value must be between -1.00 and 1.00, supplied value was %s",
                            brightnessValue));
        }

        id = ID_BASE - (brightnessPercent + 100);
        type = FEATURE_TYPE;
        description = BRIGHTNESS_DESCRIPTION;
        feature = createFeatureName(brightnessValue);
        value = String.valueOf(brightnessValue);

        ossimNonOrthoCmd = String.format(OSSIM_COMMAND, brightnessValue);
        ossimOrthoCmd = String.format(OSSIM_COMMAND, brightnessValue);

        super.ossimNonOrthoCmd = ossimNonOrthoCmd;
        super.ossimOrthoCmd = ossimOrthoCmd;
        super.canGenerate = true;
        super.canOrder = false;
    }

    /**
     * Takes the feature name and returns the double value of the brightness adjustment.
     *
     * @param featureName the feature name
     * @return the double value
     * @throws Exception invalid brightness adjustment
     */
    public static double valueFromFeatureName(String featureName) throws Exception {
        if (featureName == null) {
            return 0.0;
        }

        featureName = featureName.trim().toLowerCase(Locale.ENGLISH);

        if (featureName.startsWith(BRIGHTNESS_KEY)) {
            return Double.parseDouble(featureName.replace(BRIGHTNESS_KEY, "").trim());
        }

        if (featureName.startsWith(FILENAME_KEY)) {
            return Double.parseDouble(featureName.replace(FILENAME_KEY, "").trim()) / 100.0;
        }

        throw new Exception(
                String.format("Invalid ossim brightness feature name:  %s", featureName));
    }

    /**
     * Creates a consistent name for the feature.
     *
     * @param value the brightness value
     * @return the feature name
     */
    public static String createFeatureName(double value) {
        return String.format(COMMAND_FORMAT, BRIGHTNESS_KEY, value);
    }

    /**
     * Gets the brightness value.
     */
    public double getBrightnessValue() {
        return brightnessValue;
    }

    /**
     * Gets the value to be displayed in the file name.
     */
    public String getFilenameValue() {
        return String.format("%s%s", FILENAME_KEY, brightnessPercent);
    }

    /**
     * Returns the feature name for the supplied ID.
     *
     * @param id the ID of the feature
     * @return the feature name
     */
    public static String createFeatureNameFromId(int id) {
        return String.format(COMMAND_FORMAT, BRIGHTNESS_KEY, (id - ID_BASE + 100.0) / -100.0);
    }
}
