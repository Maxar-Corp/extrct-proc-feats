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


import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;


public class FeatureMapFactory {
    private static VersionOneFeatureMap versionOne;

    static {
        try {
            versionOne = new VersionOneFeatureMap();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * returns the default FeatureMap
     *
     * @return
     * @throws Exception
     */
    public static FeatureMap get() throws Exception {
        return getVersion(null);
    }

    /**
     * Gets the FeatureMap by specified version
     *
     * @param version
     * @return
     * @throws Exception
     */
    public static FeatureMap getVersion(String version) throws Exception {
        if (StringUtils.isEmpty(version)) {
            version = StaticSettings.DEFAULT_ID_VERISON;
        }
        if (StringUtils.equals(version, "1")) {
            return versionOne;
        }
        throw new Exception(
                String.format("There is no FileNamer implemeted for specified version '%s'", version));

    }
}
