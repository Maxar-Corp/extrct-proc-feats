import java.util.Iterator;
import java.util.List;


public class Main {

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