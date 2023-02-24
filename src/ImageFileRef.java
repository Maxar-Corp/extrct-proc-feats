/*
 * Copyright 2021 Maxar Technologies
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
import org.joda.time.DateTime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ImageFileRef {
    FileNamer fileNamer;
    FeatureMap featureMap;
    String originalFilename;
    String fileExt;
    String catId;
    String croppedHash;
    String version;
    Feature source = null;
    String sourceImageId;
    String fullPath;
    //DirectoriesDto directories;
    ProcessingParams params;

    // ---- CONSTRUCTORS ------------------------
    public ImageFileRef() throws Exception {
        setVersion(null);
        params = new ProcessingParams();
    }

    /**
     * Returns the new filename based on the class attributes
     *
     * @return
     * @throws Exception
     */
    public String getFilename() throws Exception {
        return getFilename(true);
    }

    public String getFilename(boolean withExt) throws Exception {
        return fileNamer.getFilename(this, withExt);
    }

    public void setVersion(String version) throws Exception {
        this.version = (StringUtils.isEmpty(version)) ? StaticSettings.DEFAULT_ID_VERISON : version;
        fileNamer = FileNamerFactory.getByVersion(this.version);
        featureMap = FeatureMapFactory.getVersion(this.version);
    }

    public void setOriginalFilename(String filename) {
        if (filename.contains(File.separator)) {
            filename = new File(filename).getName();
        }
        this.originalFilename = filename;
        if (this.fileExt == null || this.fileExt.isEmpty()) {
            this.fileExt = ParseFilename.getExt(filename);
        }
    }

    public void setCatId(String catId) {
        this.catId = catId == null ? catId : fileNamer.formatCatId(catId);
    }

    public void setProcessingString(String processing) throws Exception {
        setProcessing(new ProcessingParams(processing, version));
    }

    public void setProcessing(ProcessingParams processing) throws Exception {
        ProcessingParams newParams;
        if (processing == null) {
            newParams = new ProcessingParams("");
        } else {
            newParams = new ProcessingParams(processing);
        }
        if (processing != null && processing.getGsd() != null) {
            newParams.setGsd(processing.getGsd());
        }
        if (params != null && (!params.getBitSet().isEmpty() ||
                params.getGsd() != null ||
                params.getBrightness() != null ||
                params.getSharpen() != null)) {
            setVersion(newParams.getVersion());
            params.addFeatures(newParams);
        } else {
            params = newParams;
        }
        Feature source = params.getGroup(FeatureGroups.SOURCE);
        if (source != null) {
            this.source = source;
        }
    }

    public String getCroppedHash() {

        return croppedHash;
    }

    public boolean isThumbnail() {
        try {
            return params.hasFeature("thumbnail") != null;
        } catch (Exception e) {
            return false;
        }
    }

    public void setCroppedHash(String croppedHash) {
        if (croppedHash == null) {
            this.params.removeFeature(6);
        } else {
            try {
                if (!this.isThumbnail()) {
                    this.params.setFeature(6);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        this.croppedHash = croppedHash;
    }

    public String getFileExt() {
        return fileExt;
    }

    public Feature getSource() {
        return source;
    }

    public ProcessingParams getParams() {
        return params;
    }

    public boolean isInvalid() {
        return params.isInvalid();
    }

    public String getFullPath() {
        return fullPath;
    }

    public boolean isCropped() {
        try {
            return params.hasFeature("cropped") != null;
        } catch (Exception e) {
            return false;
        }
    }

    public DateTime getModified() {
        File fileRef = new File(getFullPath());
        if (!fileRef.exists()) {
            return null;
        }
        return new DateTime(fileRef.lastModified());
    }

    public String calcCatId() {
        return (hasCatId()) ? fileNamer.formatCatId(catId)
                : (hasSourceImageId()) ? fileNamer.formatCatId(sourceImageId) : null;
    }

    public boolean hasCatId() {
        return catId != null && !catId.isEmpty();
    }

    public boolean hasSourceImageId() {
        return sourceImageId != null && !sourceImageId.isEmpty();
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public ImageFileRefType getType() {
        ImageFileRefType type = null;
        type = isProcessed() ? ImageFileRefType.PROCESSED : null;
        type = isCropped() ? ImageFileRefType.CROPPED : type;
        type = isThumbnail() ? ImageFileRefType.THUMBNAIL : type;
        type = type == null ? ImageFileRefType.ORIGINAL : type;

        // regardless of the above if there is an illegal bitset mark it as invalid
        type = isInvalid() ? ImageFileRefType.INVALID : type;
        return type;
    }

    public List<String> getFeatureStringList() {
        if (params == null) {
            return new ArrayList<String>();
        }
        return params.getFeatureStringList();
    }

    public boolean isProcessed() {
        return params.isProcessed();
    }

}
