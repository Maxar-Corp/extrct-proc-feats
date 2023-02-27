import java.util.Locale;

public class GsdFeature extends Feature {
    public static final String GSD_KEY = "gsd";
    public static final String FILENAME_KEY = "gsd";
    public static final int ID_BASE = -10000;

    int gsdValueInCm;
    double gsdValueInMeters;
    static String rootOssimNonOrthoCmd = "-g %s";
    static String rootOssimOrthoCmd = "--meters %s";
    // Even though these fields exist in the superclass when looking up the field
    // via reflection there are issues finding the field via fieldname. It was
    // simpler to just add the field in this class and ensure that the same
    // value is set in the super class as well.
    String ossimNonOrthoCmd;
    String ossimOrthoCmd;

    public GsdFeature() {
        super();
        String placeHolder = "<?>";
        id = -1;
        units = "cm";
        group = FeatureGroups.GSD;
        feature = String.format("%s %s", GSD_KEY, placeHolder);
        value = placeHolder;
        type = "double";
        description = "Ground sample distance in cm";
        ossimNonOrthoCmd = String.format(rootOssimNonOrthoCmd, placeHolder);
        ossimOrthoCmd = String.format(rootOssimOrthoCmd, placeHolder);
        super.ossimNonOrthoCmd = ossimNonOrthoCmd;
        super.ossimOrthoCmd = ossimOrthoCmd;
        super.canGenerate = true;
        super.canOrder = true;
    }

    public GsdFeature(String featureName) throws Exception {
        super(featureName);

        units = "cm";
        group = FeatureGroups.GSD;
        gsdValueInMeters = valueFromFeatureName(featureName);
        gsdValueInCm = (int) (gsdValueInMeters * 100);
        id = ID_BASE - gsdValueInCm;
        feature = createFeatureName(gsdValueInMeters);
        value = String.valueOf(gsdValueInMeters * 100);
        type = "double";
        description = "Ground sample disitance in cm";
        ossimNonOrthoCmd = String.format(rootOssimNonOrthoCmd, gsdValueInMeters);
        ossimOrthoCmd = String.format(rootOssimOrthoCmd, gsdValueInMeters);
        super.ossimNonOrthoCmd = ossimNonOrthoCmd;
        super.ossimOrthoCmd = ossimOrthoCmd;
        super.canGenerate = true;
        super.canOrder = true;
    }

    public static double valueFromFeatureName(String featureName) throws Exception {
        if (featureName == null) {
            return Double.NaN;
        }
        featureName = featureName.trim().toLowerCase(Locale.ENGLISH);
        if (featureName.startsWith(GSD_KEY)) {
            return Double.parseDouble(featureName.replace(GSD_KEY, "").trim()) / 100;
        }
        if (featureName.startsWith("g")) {
            return Double.parseDouble(featureName.replace("g", "").trim()) / 100;

        }
        throw new Exception("Invalid gsd feature name: " + featureName);
    }

    public static String createFeatureName(double value) {
        return String.format("%s %s", GSD_KEY, (int) (value * 100));

    }

    public String getFilenameValue() {
        return String.format("%s%s", FILENAME_KEY, gsdValueInCm);
    }

    public static String createFeatureNameFromId(int id) {
        return String.format("%s %s", GSD_KEY, -1 * (id - ID_BASE));
    }
}