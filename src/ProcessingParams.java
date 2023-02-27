
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessingParams {

    //private static final Logger log = LoggerFactory.getLogger(ProcessingParams.class);

    private BitSet bitSet;
    private String version;
    private GsdFeature gsd;
    private SharpenFeature sharpen;
    private FeatureMap featureMap;
    private BrightnessFeature brightness;

    public ProcessingParams() throws Exception {
        this("");
    }

    public ProcessingParams(String version) throws Exception {
        setVersion(version);
        bitSet = new BitSet();
    }

    public ProcessingParams(ProcessingParams params) throws Exception {
        if (params != null) {
            this.gsd = params.getGsd();
            this.sharpen = params.getSharpen();
            this.brightness = params.getBrightness();
            setVersion(params.getVersion());
            if (params.getBitSet() != null) {
                bitSet = (BitSet) params.getBitSet().clone();
            }
        } else {
            setVersion(null);
            bitSet = new BitSet();
        }
    }

    public ProcessingParams(String featuresString, String version) throws Exception {
        setVersion(version);
        if (featuresString != null && featuresString.contains(",")) {
            bitSet = new BitSet();
            String[] features = featuresString.split(",");
            for (String feature : features) {
                this.setFeature(feature);
            }
        } else {
            try {
                bitSet = BinaryConverter.toBitSetFromBase36(featuresString);
            } catch (Exception e) {
                //log.error("Invalid featureString.  Unable to convert {} to bit set:  {}.", featuresString,
                   //     e.getMessage(), e);
                bitSet = null;
            }
        }
    }

    public List<Feature> getFeatures() {
        ArrayList<Feature> features = new ArrayList<>();
        if (bitSet != null) {
            for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
                try {
                    features.add(featureMap.getFeature(i));
                } catch (Exception e) {
                    // this should never be reached
                    //log.error("Feature at bit {} doesn't exist:  {}", i, e.getMessage(), e);
                }
                if (i == Integer.MAX_VALUE) {
                    break; // or (i+1) would overflow
                }
            }
        }

        if (gsd != null) {
            features.add(gsd);
        }

        if (sharpen != null) {
            features.add(sharpen);
        }

        if (brightness != null) {
            features.add(brightness);
        }

        return features;
    }

    public List<String> getFeatureStringList() {
        return getFeatures().stream().map(Feature::getFeature).collect(Collectors.toList());
    }

    public Feature hasFeature(String featureName) throws Exception {
        return hasFeature(featureMap.findFeature(featureName));
    }

    public Feature hasFeature(Feature feature) {
        if (feature == null) {
            return null;
        }

        if (feature == gsd) {
            return gsd;
        }
        if (feature == sharpen) {
            return sharpen;
        }

        if (feature == brightness) {
            return brightness;
        }

        if (feature.getId() < 0) {
            return null;
        }

        return (bitSet != null && bitSet.get(feature.getId())) ? feature : null;
    }

    public ProcessingParams setFeature(Feature feature) throws Exception {
        if (feature == null) {
            return this;
        }

        if (feature instanceof GsdFeature) {
            gsd = (GsdFeature) feature;
            return this;
        }

        if (feature instanceof SharpenFeature) {
            sharpen = (SharpenFeature) feature;
            return this;
        }

        if (feature instanceof BrightnessFeature) {
            brightness = (BrightnessFeature) feature;
            return this;
        }

        try {
            if (feature.getGroup().isUniqueSetting()) {
                removeGroup(feature.getGroup(), bitSet);
            }
            bitSet.set(feature.getId());
        } catch (Exception e) {
            throw new Exception(String.format("Unable to set feature: %s", feature));
        }
        return this;
    }

    public ProcessingParams setFeature(String featureName) throws Exception {
        try {
            setFeature(featureMap.findFeature(featureName));
        } catch (Exception e) {
            throw new Exception(
                    String.format("Unable to set feature: %s%nAvailable features are:%n%s", featureName,
                            featureMap.printFeatureList()));
        }
        return this;
    }

    public ProcessingParams setFeature(int id) throws Exception {
        return setFeature(featureMap.getFeature(id));
    }

    public void addFeatures(ProcessingParams newParams) throws Exception {
        if (newParams == null) {
            return;
        }
        for (Feature feature : newParams.getFeatures()) {
            setFeature(feature);
        }
    }

    public boolean isInvalid() {
        return bitSet == null;
    }

    public boolean isProcessed() {
        int[] setBits = BinaryConverter.getSetBits(bitSet);
        // check for any set features that are not SOURCE features
        // any feature that is not a SOURCE feature would indicate some processing
        for (int setBit : setBits) {
            FeatureGroups group;
            try {
                group = featureMap.getFeature(setBit).getGroup();
                if (!group.equals(FeatureGroups.SOURCE) && !group.equals(FeatureGroups.FORMAT)) {
                    return true;
                }
            } catch (Exception e) {
                // This should never be reached
                //log.error("An error occurred checking if the image is processed.", e);
            }
        }

        return gsd != null || sharpen != null || brightness != null;
    }

    @Override
    public String toString() {
        if (this.bitSet.isEmpty()) {
            return "";
        }
        return BinaryConverter.toBase36(this.bitSet);
    }

    public BitSet getBitSet() {
        return bitSet;
    }

    public void setBitSet(BitSet bitSet) {
        this.bitSet = bitSet;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) throws Exception {
        this.version = (StringUtils.isEmpty(version)) ? StaticSettings.DEFAULT_ID_VERISON : version;
        featureMap = FeatureMapFactory.getVersion(this.version);
    }

    public Feature getGroup(FeatureGroups group) {
        if (group == FeatureGroups.GSD) {
            return gsd;
        }
        for (Feature feature : featureMap.getGroupFeatures(group)) {
            if (hasFeature(feature) != null) {
                return feature;
            }
        }
        return null;
    }

    private BitSet removeGroup(FeatureGroups group, BitSet bitSet) {
        if (bitSet != null) {
            for (Feature feature : featureMap.getGroupFeatures(group)) {
                int id = feature.getId();
                if (id < 0) {
                    if (id <= GsdFeature.ID_BASE && id > SharpenFeature.ID_BASE) {
                        gsd = null;
                    } else if (id <= SharpenFeature.ID_BASE && id > BrightnessFeature.ID_BASE) {
                        sharpen = null;
                    } else if (id <= BrightnessFeature.ID_BASE) {
                        brightness = null;
                    }
                } else {
                    bitSet.set(id, false);
                }
            }
        }
        return bitSet;
    }

    public void removeFeature(int id) {
        if (bitSet == null) {
            return;
        }
        if (id >= 0) {
            bitSet.set(id, false);
        } else {
            if (id == gsd.getId()) {
                gsd = null;
            } else if (id == sharpen.getId()) {
                sharpen = null;
            } else if (id == brightness.getId()) {
                brightness = null;
            }
        }
    }

    public void removeFeature(Feature feature) {
        if (feature != null) {
            if (feature instanceof GsdFeature) {
                gsd = null;
            } else if (feature instanceof SharpenFeature) {
                sharpen = null;
            } else if (feature instanceof BrightnessFeature) {
                brightness = null;
            } else {
                removeFeature(feature.getId());
            }
        }
    }

    public GsdFeature getGsd() {
        return gsd;
    }

    public void setGsd(GsdFeature gsd) {
        this.gsd = gsd;
    }

    public void removeFeatures(ProcessingParams params) {
        for (Feature feature : params.getFeatures()) {
            removeFeature(feature);
        }
    }

    public static ProcessingParams getDifference(ProcessingParams outputParams,
                                                 ProcessingParams sourceParams) throws Exception {
        ProcessingParams deltaParams = new ProcessingParams(outputParams);
        deltaParams.removeFeatures(sourceParams);
        return deltaParams;
    }

    public SharpenFeature getSharpen() {
        return sharpen;
    }

    public BrightnessFeature getBrightness() {
        return brightness;
    }
}