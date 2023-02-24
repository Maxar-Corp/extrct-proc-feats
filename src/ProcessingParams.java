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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data model for processing parameters.
 */
public class ProcessingParams {

    //private static final Logger log = LoggerFactory.getLogger(ProcessingParams.class);

    private BitSet bitSet;
    private String version;
    private GsdFeature gsd;
    private SharpenFeature sharpen;
    private FeatureMap featureMap;
    private BrightnessFeature brightness;

    /**
     * Creates a new instance of {@link ProcessingParams}.
     *
     * @throws Exception unable to set the version
     */
    public ProcessingParams() throws Exception {
        this("");
    }

    /**
     * Creates a new instance of {@link ProcessingParams}.
     *
     * @param version the feature map version
     * @throws Exception unable to set the version
     */
    public ProcessingParams(String version) throws Exception {
        setVersion(version);
        bitSet = new BitSet();
    }

    /**
     * Creates a new instance of {@link ProcessingParams}.
     *
     * @param initialBits the initial bits to set
     * @param version the feature map version to use
     * @throws Exception unable to set the version
     */
    public ProcessingParams(BitSet initialBits, String version) throws Exception {
        setVersion(version);
        bitSet = (initialBits == null) ? new BitSet() : initialBits;
    }

    /**
     * Creates a new instance of {@link ProcessingParams}.
     *
     * @param params another instance of processing params
     * @throws Exception unable to set the version
     */
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

    /**
     * Creates an instance of {@link ProcessingParams}.
     *
     * @param featuresString a Json encoded string or a base36 encoded string of features
     * @throws Exception unable to set the version
     */
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

    /**
     * Returns a list of all the set features.
     *
     * @return a list of features
     */
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

    /**
     * Returns the string values for all the set features.
     *
     * @return a list of string features
     */
    public List<String> getFeatureStringList() {
        return getFeatures().stream().map(Feature::getFeature).collect(Collectors.toList());
    }

    /**
     * Returns the Feature by searching for its feature name or null if it does not have the feature.
     *
     * @param featureName the name of the feature
     * @return the feature if present
     * @throws Exception the feature is not present
     */
    public Feature hasFeature(String featureName) throws Exception {
        return hasFeature(featureMap.findFeature(featureName));
    }

    /**
     * Returns the Feature by searching for a group and value or null if it is not set.
     *
     * @param group the feature group
     * @param value the string value of the feature
     * @return the feature if present
     * @throws Exception the feature is not present
     */
    public Feature hasFeature(FeatureGroups group, String value) throws Exception {
        return hasFeature(featureMap.findFeature(group, value));
    }

    /**
     * Returns the feature given.
     *
     * @param feature the feature to find
     * @return the feature
     */
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

    /**
     * Returns true if there is any ortho processing in this set of params
     * regardless of ortho type or elevation model.
     *
     * @return
     */
    public boolean hasAnyOrtho() {
        for (Feature f: getFeatures()) {
            if(f.getGroup() == FeatureGroups.ORTHO) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set the given feature as a processing parameter.
     *
     * @param feature the feature to set
     * @return the new processing parameters
     * @throws Exception unable to set the feature
     */
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

    /**
     * Sets the processing parameter.
     *
     * @param featureName the name of the feature
     * @return the new processing parameters
     * @throws Exception unable to set the feature
     */
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

    public ProcessingParams setFeature(FeatureGroups group, String value)
            throws Exception {
        return setFeature(featureMap.findFeature(group, value));
    }

    public ProcessingParams setFeature(int id) throws Exception {
        return setFeature(featureMap.getFeature(id));
    }

    /**
     * Adds the list of processing parameters to the processing parameters.
     *
     * @param newParams the processing parameters to add
     * @throws Exception unable to set the features
     */
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

    /**
     * Check to see if the processing parameters include any features that are not source parameters.
     *
     * @return true if the image has been processed, false if not
     */
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

    /**
     * Checks to see if too sets of processing parameters are similar. Does not check to if the image
     * came from the same source.
     *
     * @param compareParams the processing parameters to compare
     * @return true if the processing parameters are similar, false if not
     */
    public boolean isSimilar(ProcessingParams compareParams) {
        // clone and remove the source from the set bits as we do not care if the image
        // came from a different source

        // early out for invalid params
        if (bitSet == null || compareParams.getBitSet() == null) {
            return false;
        }

        BitSet thisBitSet = removeGroup(FeatureGroups.SOURCE, (BitSet) bitSet.clone());
        BitSet compareBitSet =
                removeGroup(FeatureGroups.SOURCE, (BitSet) compareParams.getBitSet().clone());

        return thisBitSet != null && thisBitSet.equals(compareBitSet) && hasSameGsd(compareParams)
                && hasSameSharpen(compareParams) && hasSameBrightness(compareParams);
    }

    /**
     * Checks to see if the processing parameters have the same brightness values.
     *
     * @param compareParams the processing parameters to compare to
     * @return true if they have the same brightness, false if not
     */
    public boolean hasSameBrightness(ProcessingParams compareParams) {
        BrightnessFeature compareBrightness = compareParams.getBrightness();
        double compareBrightnessValue =
                (compareBrightness != null) ? compareBrightness.getBrightnessValue() : -1.0;
        double thisBrightnessValue = (brightness != null) ? brightness.getBrightnessValue() : -1.0;
        return compareBrightnessValue == thisBrightnessValue;
    }

    /**
     * Checks to see if the processing parameters contain the same GSD values.
     *
     * @param compareParams the processing parameters to compare
     * @return true if the GSD values are the same, false if not
     */
    public boolean hasSameGsd(ProcessingParams compareParams) {
        GsdFeature compareGsd = compareParams.getGsd();
        int compareGsdValue = (compareGsd != null) ? compareGsd.getGsdValueInCm() : -1;
        int thisGsdValue = (this.gsd != null) ? (this.gsd).getGsdValueInCm() : -1;
        return compareGsdValue == thisGsdValue;
    }

    /**
     * True if the passed in ProcessingParams has the same Sharpen value.
     *
     * @param compareParams the processing parameters to compare
     * @return true if the same sharpen value is present
     */
    public boolean hasSameSharpen(ProcessingParams compareParams) {
        SharpenFeature compareSharpen = compareParams.getSharpen();
        int compareSharpenValue = compareSharpen != null ? compareSharpen.getPercentage() : -1;
        int thisSharpenValue = sharpen != null ? sharpen.getPercentage() : -1;
        return compareSharpenValue == thisSharpenValue;
    }

    @Override
    public String toString() {
        if (this.bitSet.isEmpty()) {
            return "";
        }
        return BinaryConverter.toBase36(this.bitSet);
    }

    // ----- GETTERS and SETTERS
    // -----------------------------------------------------------------
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

    public boolean hasGroup(FeatureGroups group) {
        return getGroup(group) != null;

    }

    /**
     * Gets the feature group.
     *
     * @param group the feature group to get
     * @return the feature
     */
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

    /**
     * Gets the features that are part of the given feature group.
     *
     * @param group the feature group
     * @return a list of features in the group
     */
    public List<Feature> getGroupFeatures(FeatureGroups group) {
        List<Feature> featureList = new ArrayList<>();
        if (group == FeatureGroups.GSD) {
            featureList.add(gsd);
            return featureList;
        }
        for (Feature feature : featureMap.getGroupFeatures(group)) {
            if (hasFeature(feature) != null) {
                featureList.add(feature);
            }
        }

        if (group == FeatureGroups.ADJUSTMENTS) {
            if (sharpen != null) {
                featureList.add(sharpen);
            }

            if (brightness != null) {
                featureList.add(brightness);
            }
        }

        return featureList;
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

    /**
     * Checks to see if the instance has all the features from the passed in params argument.
     *
     * @param params the values to compare against
     * @return true if all the features are present
     */
    public boolean hasParams(ProcessingParams params) {
        // if either params have invalid bitSets we cannot compare and assume they are not equal
        if (bitSet == null || params.getBitSet() == null) {
            return false;
        }

        BitSet testBitSet = (BitSet) bitSet.clone();
        testBitSet.and(params.getBitSet());
        return testBitSet.equals(params.getBitSet());
    }

    /**
     * Removes a feature based on ID value.
     *
     * @param id the ID of the feature to remove
     */
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

    /**
     * Removes a feature from the processing parameters based on the feature.
     *
     * @param feature the feature to remove
     */
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

    /**
     * Prints out the processing params as a formatted string.
     *
     * @return the features as a string
     */
    public String toPrettyString() {
        List<Feature> features = getFeatures();
        if (features.isEmpty()) {
            return "None";
        }
        return features.stream().map(Feature::getFeature).collect(Collectors.joining(", "));
    }

    public GsdFeature getGsd() {
        return gsd;
    }

    public void setGsd(GsdFeature gsd) {
        this.gsd = gsd;
    }

    /**
     * Removes the feature group from the processing parameters.
     *
     * @param group the feature group to remove
     * @return the feature that was removed
     */
    public Feature popGroup(FeatureGroups group) {
        Feature feature = getGroup(group);
        if (feature == null) {
            return null;
        }
        removeFeature(feature);
        return feature;
    }

    /**
     * Returns a list of all the set features.
     *
     * @return a list of feature IDs
     */
    public int[] getFeatureIds() {
        int[] ids = new int[0];

        if (bitSet != null) {
            ids = bitSet.stream().toArray();
        }

        if (brightness != null) {
            ids = ArrayUtils.add(ids, brightness.getId());
        }

        if (sharpen != null) {
            ids = ArrayUtils.add(ids, sharpen.getId());
        }

        if (gsd != null) {
            ids = ArrayUtils.add(ids, gsd.getId());
        }

        return ids;
    }

    /**
     * Removes all the features of the feature group from the processing params.
     *
     * @param group the feature group to remove
     * @return a list of features that were removed
     */
    public List<Feature> popGroupFeatures(FeatureGroups group) {
        List<Feature> features = getGroupFeatures(group);
        features.forEach(this::removeFeature);
        return features;
    }

    /**
     * Counts the number of set features.
     *
     * @return the number of set features
     */
    public int getFeatureCount() {
        int count = gsd != null ? 1 : 0;
        count += sharpen != null ? 1 : 0;
        count += brightness != null ? 1 : 0;

        return bitSet == null ? count : count + bitSet.cardinality();
    }

    /**
     * Removes all the params specified in params.
     *
     * @param params the processing parameters to remove
     */
    public void removeFeatures(ProcessingParams params) {
        for (Feature feature : params.getFeatures()) {
            removeFeature(feature);
        }
    }

    /**
     * Returns the params that do not intersect with the sourceParams.
     *
     * @param outputParams the output processing parameters
     * @param sourceParams the source processing parameters
     * @return the processing parameters that do not intersect
     * @throws Exception unable to create a new processing params object
     */
    public static ProcessingParams getDifference(ProcessingParams outputParams,
                                                 ProcessingParams sourceParams) throws Exception {
        ProcessingParams deltaParams = new ProcessingParams(outputParams);
        deltaParams.removeFeatures(sourceParams);
        return deltaParams;
    }

    /**
     * Gets the sharpen feature.
     */
    public SharpenFeature getSharpen() {
        return sharpen;
    }

    /**
     * Gets the brightness feature from the processing parameters.
     */
    public BrightnessFeature getBrightness() {
        return brightness;
    }
}
