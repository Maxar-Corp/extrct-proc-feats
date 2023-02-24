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


public interface FileNamer {

    /**
     * Returns the version of the implemented FileNamer
     *
     * @return
     */
    public String getVersion();

    /**
     * Parses the filename into an ImageFileRef
     *
     * @param filename
     * @return
     * @throws Exception
     */
    public ImageFileRef parseFilename(String filename) throws Exception;

    /**
     * Returns the string filename from the imageFileRef
     *
     * @param imageFileRef
     * @return
     */
    public String getFilename(ImageFileRef imageFileRef);

    /**
     * Returns the string filename from the imageFileRef
     *
     * @param imageFileRef
     * @param withExt
     * @return
     */
    public String getFilename(ImageFileRef imageFileRef, boolean withExt);

    /**
     * Formats the CatId appropriately so that it conforms to the standard used for the implemented
     * FileNamer
     *
     * @param catId
     * @return
     */
    public String formatCatId(String catId);

    /**
     * Generates a human readable version of the file reference
     *
     * @param imageFileRef
     * @return
     */
    public String getDescriptiveName(ImageFileRef imageFileRef);
}
