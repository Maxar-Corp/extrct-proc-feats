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

import java.io.FileNotFoundException;
import java.util.*;

/**
 * This a mapping of all available feature for the bit map encoding.
 */
public class VersionOneFeatureMap implements FeatureMap {

    private static final ArrayList<Feature> features = new ArrayList<>();
    private static boolean locked = false;
    private static final Map<FeatureGroups, List<Feature>> mapping =
            new EnumMap<>(FeatureGroups.class);

    private static final Map<String, GsdFeature> gsdFeatures = new HashMap<>();
    private static final Map<String, SharpenFeature> sharpenFeatures = new HashMap<>();
    private static final Map<String, BrightnessFeature> brightnessFeatures = new HashMap<>();

    private static final GsdFeature gsdPlaceHolder = new GsdFeature();
    private static final SharpenFeature sharpnessPlaceHolder = new SharpenFeature();
    private static final BrightnessFeature brightnessPlaceHolder = new BrightnessFeature();
    private static final List<Feature> gsdPlaceHolderList = Collections.singletonList(gsdPlaceHolder);

    /**
     * Creates a new instance of {@link VersionOneFeatureMap}.
     */
    public VersionOneFeatureMap() throws FileNotFoundException {
        new CsvMappingReader(this, StaticSettings.FEATURE_MAP_FILE_VERSION_1).setFeatureMap();
        // add the sharpness placeholder to the adjustments group
        mapping.get(FeatureGroups.ADJUSTMENTS).add(sharpnessPlaceHolder);

        // Add the brightness placeholder to the adjustments group.
        mapping.get(FeatureGroups.ADJUSTMENTS).add(brightnessPlaceHolder);
    }

    @Override
    public void add(String[] csvLine, List<String> keys) throws IllegalAccessError {
        if (locked) {
            throw new IllegalAccessError("FeatureMap is locked.");
        }
        int id = Integer.parseInt(csvLine[keys.indexOf("id")]);
        Feature feature = new Feature(csvLine, keys);
        // Add the feature to a mapping where the group is the key with a list of
        // associated features
        mapping.computeIfAbsent(feature.getGroup(), k -> new ArrayList<>());
        mapping.get(feature.getGroup()).add(feature);
        features.add(id, feature);
    }

    @Override
    public Feature getFeature(int id) throws Exception {
        if (id <= GsdFeature.ID_BASE && id > SharpenFeature.ID_BASE) {
            return findGsdFeature(GsdFeature.createFeatureNameFromId(id));
        }

        if (id <= SharpenFeature.ID_BASE && id > BrightnessFeature.ID_BASE) {
            return findSharpenFeature(SharpenFeature.createFeatureNameFromId(id));
        }

        if (id <= BrightnessFeature.ID_BASE) {
            return findBrightnessFeature(BrightnessFeature.createFeatureNameFromId(id));
        }

        return features.get(id);
    }

    @Override
    public Feature findFeature(String featureName) throws Exception {

        String name = (featureName == null) ? null : featureName.toLowerCase();
        Feature gsdFeature = findGsdFeature(featureName);
        if (gsdFeature != null) {
            return gsdFeature;
        }
        for (Feature feature : features) {
            if (feature.getFeature().equalsIgnoreCase(name)) {
                return feature;
            }
        }
        Feature sharpnessFeature = findSharpenFeature(featureName);
        if (sharpnessFeature != null) {
            return sharpnessFeature;
        }

        Feature brightnessFeature = findBrightnessFeature(featureName);
        if (brightnessFeature != null) {
            return brightnessFeature;
        }

        throw new Exception(featureName);
    }

    @Override
    public List<Feature> getGroupFeatures(FeatureGroups group) {
        if (FeatureGroups.GSD == group) {
            return gsdPlaceHolderList;
        }
        return mapping.get(group);
    }

    @Override
    public void lock() {
        locked = true;
    }

    @Override
    public int length() {
        return features.size();
    }

    @Override
    public String printFeatureList() {
        List<String> featureList = new ArrayList<>();
        for (Feature feature : features) {
            featureList.add(String.format(feature.toString()));
        }
        featureList.add(gsdPlaceHolder.toString());
        featureList.add(sharpnessPlaceHolder.toString());
        featureList.add(brightnessPlaceHolder.toString());
        return String.join("\n", featureList);
    }

    @Override
    public Map<FeatureGroups, List<Feature>> getMapping() {
        return mapping;
    }

    private static Feature findGsdFeature(String featureName) throws Exception {
        if (!StringUtils.startsWith(featureName, GsdFeature.GSD_KEY)) {
            return null;
        }

        GsdFeature gsdFeature =
                gsdFeatures.get(GsdFeature.createFeatureName(GsdFeature.valueFromFeatureName(featureName)));
        if (gsdFeature != null) {
            return gsdFeature;
        }
        gsdFeature = new GsdFeature(featureName);
        gsdFeatures.put(gsdFeature.getFeature(), gsdFeature);
        return gsdFeature;
    }

    private static Feature findSharpenFeature(String featureName) throws Exception {
        if (!StringUtils.startsWith(featureName, SharpenFeature.SHARPEN_KEY)) {
            return null;
        }

        SharpenFeature sharpnessFeature = sharpenFeatures
                .get(SharpenFeature.createFeatureName(SharpenFeature.valueFromFeatureName(featureName)));
        if (sharpnessFeature != null) {
            return sharpnessFeature;
        }
        sharpnessFeature = new SharpenFeature(featureName);
        sharpenFeatures.put(sharpnessFeature.getFeature(), sharpnessFeature);
        return sharpnessFeature;
    }

    private static Feature findBrightnessFeature(String featureName) throws Exception {
        if (!StringUtils.startsWith(featureName, BrightnessFeature.BRIGHTNESS_KEY)) {
            return null;
        }

        BrightnessFeature brightnessFeature = brightnessFeatures.get(
                BrightnessFeature.createFeatureName(BrightnessFeature.valueFromFeatureName(featureName)));

        if (brightnessFeature != null) {
            return brightnessFeature;
        }

        brightnessFeature = new BrightnessFeature(featureName);
        brightnessFeatures.put(brightnessFeature.getFeature(), brightnessFeature);

        return brightnessFeature;
    }
}