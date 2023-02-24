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

/**
 * <p>
 * Factory Class for returning the correct FileNamer class for parsing and generating filenames for
 * the specified version
 * </p>
 *
 * @author mstabile
 *
 */
public class FileNamerFactory {
    private static final FileNamer versionOne = new VersionOneFileNamer();
    private static int id_length = StaticSettings.DC_BIT_ID.length();

    public static FileNamer getByVersion(String version) throws Exception {
        if (StringUtils.isEmpty(version)) {
            version = StaticSettings.DEFAULT_ID_VERISON;
        }
        if (StringUtils.equals(version, "1")) {
            return versionOne;
        }
        throw new Exception(
                String.format("There is no FileNamer implemeted for specified version"));
    }

    public static FileNamer getByFilename(String filename) throws Exception {
        return getByVersion(findVersionFromFilename(filename));
    }

    static String findVersionFromFilename(String filename) {
        if (StringUtils.contains(filename, StaticSettings.DC_BIT_ID)) {
            int start = filename.indexOf(StaticSettings.DC_BIT_ID) + id_length;
            return filename.substring(start, start + 1);
        }
        return null;
    }
}