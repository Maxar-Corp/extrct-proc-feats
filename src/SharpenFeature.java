import java.util.Locale;

/**
 * This Feature allows for variability in the sharpen processing options
 *
 * @author mstabile
 *
 */
public class SharpenFeature extends Feature {
    public static final String SHARPEN_KEY = "ossim-sharpen";
    public static final String FILENAME_KEY = "os";
    public static final int ID_BASE = -20000;
    private Double percentageInDecimal;
    private int percentage;
    private static final String rootOssimNonOrthoCmd = "";
    private static final String rootOssimOrthoCmd = "--sharpen-percent %s";
    private static final String sharpenDescription = "Ossim sharpen percentage from 0 to 100";
    // Even though these fields exist in the superclass when looking up the field
    // via reflection there are issues finding the field via fieldname. It was
    // simpler to just add the field in this class and ensure that the same
    // value is set in the super class as well.
    protected String ossimNonOrthoCmd;
    protected String ossimOrthoCmd;

    public SharpenFeature() {
        super();
        String placeHolder = "<?>";
        id = -2;
        units = "percentage";
        group = FeatureGroups.ADJUSTMENTS;
        feature = String.format("%s %s", SHARPEN_KEY, placeHolder);
        value = placeHolder;
        type = "int";
        description = sharpenDescription;
        ossimNonOrthoCmd = String.format(rootOssimNonOrthoCmd, placeHolder);
        ossimOrthoCmd = String.format(rootOssimOrthoCmd, placeHolder);
        super.ossimNonOrthoCmd = ossimNonOrthoCmd;
        super.ossimOrthoCmd = ossimOrthoCmd;
        super.canGenerate = true;
        super.canOrder = false;
    }

    public SharpenFeature(String featureName) throws Exception {
        super(featureName);

        units = "percentage";
        group = FeatureGroups.ADJUSTMENTS;
        percentage = valueFromFeatureName(featureName);
        // ensure that the value is valid
        if (percentage > 100 || percentage < 0) {
            throw new Exception(String
                    .format("sharpen value must be between 0 and 100, supplied value was: %s", percentage));
        }
        percentageInDecimal = percentage / 100d;
        id = ID_BASE - percentage;
        feature = createFeatureName(percentage);
        value = String.valueOf(percentage);
        type = "int";
        description = sharpenDescription;
        ossimNonOrthoCmd = null;
        ossimOrthoCmd = String.format(rootOssimOrthoCmd, percentageInDecimal);
        super.ossimNonOrthoCmd = ossimNonOrthoCmd;
        super.ossimOrthoCmd = ossimOrthoCmd;
        super.canGenerate = true;
        super.canOrder = false;
    }

    /**
     * Takes the Feature name returns the integer value of the sharpen percentage
     *
     * @param  featureName
     * @return
     * @throws Exception
     */
    public static int valueFromFeatureName(String featureName) throws Exception {
        if (featureName == null) {
            return -1;
        }
        featureName = featureName.trim().toLowerCase(Locale.ENGLISH);
        if (featureName.startsWith(SHARPEN_KEY)) {
            return Integer.parseInt(featureName.replace(SHARPEN_KEY, "").trim());
        }
        if (featureName.startsWith(FILENAME_KEY)) {
            return Integer.parseInt(featureName.replace(FILENAME_KEY, "").trim());

        }
        throw new Exception("Invalid ossim-sharpen feature name: " + featureName);
    }

    /**
     * create a consistent name for the feature
     *
     * @param  value
     * @return
     */
    public static String createFeatureName(int value) {
        return String.format("%s %s", SHARPEN_KEY, value);

    }

    public int getPercentage() {
        return percentage;
    }

    /**
     * Returns the value to be displayed in the filename
     *
     * @return
     */
    public String getFilenameValue() {
        return String.format("%s%s", FILENAME_KEY, value);
    }

    /**
     * return the feature name for the supplied id
     *
     * @param  id
     * @return
     */
    public static String createFeatureNameFromId(int id) {
        return String.format("%s %s", SHARPEN_KEY, -1 * (id - ID_BASE));
    }

}