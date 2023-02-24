
public enum ImageFileRefType {
    ORIGINAL("An original image acquired from an imagery provider"), PROCESSED(
            "An image that has been processed"), CROPPED(
            "An image that has been cropped from original image"), THUMBNAIL_OVERLAY(
            "A thumbnail of an object with an imbedded overlay"), THUMBNAIL(
            "A thumbnail of an image that does not have an overlay in it"), INVALID(
            "An image whose processing parameters cannot be determined");

    private String description;

    ImageFileRefType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
