/*
 * Copyright 2021 Maxar Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SBIR DATA RIGHTS
 * Contract No. HM0476-16-C-0022
 * Contractor Name: Maxar Technologies
 * Contractor Address: 2325 Dulles Corner Blvd. STE 1000, Herndon VA 20171
 * Expiration of SBIR Data Rights Period: 2/13/2029
 *
 * The Government's rights to use, modify, reproduce, release, perform, display,
 * or disclose technical data or computer software marked with this legend are
 * restricted during the period shown as provided in paragraph (b)(4) of the
 * Rights in Noncommercial Technical Data and Computer Software-Small Business
 * Innovation Research (SBIR) Program clause contained in the above identified
 * contract. No restrictions apply after the expiration date shown above. Any
 * reproduction of technical data, computer software, or portions thereof marked
 * with this legend must also reproduce the markings.
 */

import java.lang.reflect.Field;
import java.util.List;

public class Feature {

    //private static final Logger log = LoggerFactory.getLogger(Feature.class);

    int id;
    FeatureGroups group;
    String feature;
    String value;
    String type;
    String units;
    String description;
    String ossimNonOrthoCmd;
    String ossimOrthoCmd;
    boolean canOrder;
    boolean canGenerate;

    public Feature(String featureName) throws Exception {
        this.feature = featureName;
    }

    public Feature(String[] csvLine, List<String> keys) {
        for (int i = 0; i < keys.size(); i++) {
            try {
                if (csvLine.length > i) {
                    setField(keys.get(i), csvLine[i]);
                }
            } catch (Exception e) {
                //log.error("Error setting feature field", e);
            }
        }
    }

    public Feature() {}

    @Override
    public String toString() {
        return String.format("id: %s | group: '%s' | feature: '%s' | description: '%s'", id, group, feature, description);
    }

    // --- PRIVATE METHODS ------------------------------------
    /**
     * Sets the field with passed in value
     *
     * @param fieldName
     * @param value
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws Exception
     */
    private void setField(String fieldName, String value)
            throws NoSuchFieldException, IllegalAccessException, Exception {
        if (fieldName.contentEquals("id")) {
            id = Integer.parseInt(value);
            return;
        }
        if (fieldName.contentEquals("group")) {
            try {
                group = FeatureGroups.valueOf(value.toUpperCase().trim().replace(" ", "_"));
            } catch (IllegalArgumentException e) {
                throw new Exception(String
                        .format("Group '%s' is not allowed, add to 'FeatureGroups' enum to allow", value));
            }
            return;
        }
        Field field = getClass().getDeclaredField(fieldName);
        if (field.getType() == boolean.class) {
            if (!value.isEmpty()) {
                field.set(this, Boolean.parseBoolean(value));
            }
        } else {
            field.set(this, value);
        }
    }

    // ----- GETTERS and SETTERS ---------------------------------------------
    public int getId() {
        return id;
    }

    public FeatureGroups getGroup() {
        return group;
    }

    public String getFeature() {
        return feature;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public String getUnits() {
        return units;
    }

    public String getDescription() {
        return description;
    }
}
