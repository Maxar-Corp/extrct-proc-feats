
public enum FeatureGroups {
    OPERATION(true), SOURCE(true), HISTOGRAM(true), RADIOMETRY(true), GSD(true), ORTHO(
            true), ELEVATION_DATA(true), RESOLUTION(
            true), ADJUSTMENTS(false), SOFTWARE(false), THUMBNAIL(true), BANDS(true), FORMAT(true);
    // uniqueSetting: if set true only one feature in that group can be set at one
    // time in a ProcessingParams instance
    boolean uniqueSetting;

    FeatureGroups(boolean uniqueSetting) {
        this.uniqueSetting = uniqueSetting;
    }

    public boolean isUniqueSetting() {
        return uniqueSetting;
    }

}