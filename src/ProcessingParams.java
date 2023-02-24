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

    public GsdFeature getGsd() {
        return gsd;
    }

    public void setGsd(GsdFeature gsd) {
        this.gsd = gsd;
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
