import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;


public class Main {
    public class StaticSettings {
        public static final String NAME = "mirage-utils";
        public static final String DESCRIPTION = "utility class for working with images on a filesystem";
        // public static final String VERSION = "0.0.1";
        public static final String DC_BIT_ID = "dcId";
        public static final String DEFAULT_ID_VERISON = "1";
        public static final String FEATURE_MAP_FILE_VERSION_1 = "featuremap_v1.csv";
        //public static final ImageExtensions defaultOutputFileType = ImageExtensions.NITF;
        public static final String METADATA_EXT = "metadata.json";
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

    public static final int ID_LENGTH = StaticSettings.DC_BIT_ID.length();

    private static final Pattern gsdPattern =
            Pattern.compile(String.format("%s\\d+", "gsd"));
    private static final Pattern sharpnessPattern =
            Pattern.compile(String.format("%s\\d+", "os"));
    private static final Pattern brightnessPattern =
            Pattern.compile(String.format("%s(-)?\\d+", "ob"));

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

    public static boolean parseFilename(String filename) throws Exception {
        //filename = formatFilename(filename, true);

        //ImageFileRef imageFileRef = new ImageFileRef();
        int dcbitPos = filename.indexOf(String.format("_%s", StaticSettings.DC_BIT_ID));

        String catId = stripExt(filename);
        //imageFileRef.setOriginalFilename(filename);
        //imageFileRef.setCatId(catId);

        if (dcbitPos > -1) {
            String[] parts = catId.substring(dcbitPos + 1).split("_");
            final String version = parts[0].substring(ID_LENGTH);
            catId = filename.substring(0, dcbitPos);
//            imageFileRef.setOriginalFilename(filename);
//            imageFileRef.setCatId(catId);
//            imageFileRef.setVersion(version);
//            imageFileRef.setProcessingString(getProcessingString(parts));

            String[] variableValues = getVariableValue(parts);

            for (int i = 0; i < variableValues.length; i++)
                System.out.println(variableValues[i]);

            if (variableValues[0] != null) {
//            imageFileRef.getParams().setFeature(FeatureMapFactory.getVersion(version).findFeature(
//                    GsdFeature.createFeatureName(GsdFeature.valueFromFeatureName(variableValues[0]))));
        }

        if (variableValues[1] != null) {
//            imageFileRef.getParams()
//                    .setFeature(FeatureMapFactory.getVersion(version).findFeature(SharpenFeature
//                            .createFeatureName(SharpenFeature.valueFromFeatureName(variableValues[1]))));
        }

        if (variableValues[2] != null) {
//            imageFileRef.getParams()
//                    .setFeature(FeatureMapFactory.getVersion(version).findFeature(BrightnessFeature
//                            .createFeatureName(BrightnessFeature.valueFromFeatureName(variableValues[2]))));
        }

//        imageFileRef.setCroppedHash(getCroppedHash(parts));
//        return imageFileRef;
    } else {
//        imageFileRef.setVersion(StaticSettings.DEFAULT_ID_VERISON);
    }

//        return imageFileRef;
        return false;
    }

    public static void main(String[] args) {

        String processedImageId = "104001004E9B7800_dcId1_y1u6j5s";

        try {
            ImageFileRef imageRef = ParseFilename.parse(processedImageId);
            List<String> features = imageRef.getFeatureStringList();

            Iterator iterator = features.iterator();
            while(iterator.hasNext()) {
                System.out.println(iterator.next());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}