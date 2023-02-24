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
    List<ImageFileRef> relatedFiles;
    //List<PathResponse> messages;
    private String directory;
    private String geoDcImageId;

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
//
//    /**
//     * Returns the new filename based on the class attributes
//     *
//     * @return
//     * @throws MirageUtilsException
//     */
        public String getFilename(boolean withExt) throws Exception {
            return fileNamer.getFilename(this, withExt);
        }
//
//    /**
//     * Returns a string describing that image
//     *
//     * @return
//     * @throws MirageUtilsException
//     */
//    public String info() throws MirageUtilsException {
//
//        ArrayList<String> parts = new ArrayList<>();
//        parts.add(String.format("filename: %s", getFilename()));
//        parts.add(String.format("path: %s", fullPath));
//        parts.add(String.format("fileExists: %s", new File(fullPath).exists()));
//        parts.add(String.format("catID: %s", calcCatId()));
//        parts.add(String.format("sourceImageId: %s", sourceImageId));
//        parts.add(String.format("Original/Processed: %s", (isProcessed()) ? "Processed" : "Original"));
//
//        if (params != null) {
//            for (Feature feature : params.getFeatures()) {
//                parts.add(String.format("%s: %s", feature.getGroup(), feature.getFeature()));
//            }
//        }
//
//        if (relatedFiles != null) {
//
//            parts.add("Related Files:\n    Original Files:");
//
//            for (ImageFileRef ref : relatedFiles) {
//                if (!ref.isProcessed()) {
//                    parts.add(String.format("        %s", ref.toString()));
//                }
//            }
//
//            parts.add("    Processed Files:");
//
//            for (ImageFileRef ref : relatedFiles) {
//                if (ref.isProcessed()) {
//                    parts.add(String.format("        %s", ref.toString()));
//                }
//            }
//        }
//
//        if (messages != null && messages.size() > 0) {
//            parts.add("Messages:");
//            for (PathResponse msg : messages) {
//                parts.add(String.format("        %s", msg.toString()));
//            }
//        }
//
//        return String.join("\n", parts);
//
//    }
//
//    public boolean hasCatId() {
//        return catId != null && !catId.isEmpty();
//    }
//
//    public boolean hasSource() {
//        return source != null;
//    }
//
//    public boolean hasSourceImageId() {
//        return sourceImageId != null && !sourceImageId.isEmpty();
//    }
//
//    public boolean hasGeom() {
//        return croppedHash != null && !croppedHash.isEmpty();
//    }
//
//    public boolean hasParams() {
//        return params != null && !params.getBitSet().isEmpty();
//    }
//
//    /**
//     * Calculates the new catId based up the existence of the catId or the sourceImageId
//     *
//     * @return
//     */
//    public String calcCatId() {
//        return (hasCatId()) ? fileNamer.formatCatId(catId)
//                : (hasSourceImageId()) ? fileNamer.formatCatId(sourceImageId) : null;
//    }
//
//    // --- PRIVATE METHODS ------------------------------------------
//
//    // ----- GETTERS and SETTERS -------------------
//    public String getVersion() {
//        return version;
//    }
//
//    public String getOriginalFilename() {
//        return originalFilename;
//    }
//
    public void setVersion(String version) throws Exception {
        this.version = (StringUtils.isEmpty(version)) ? StaticSettings.DEFAULT_ID_VERISON : version;
        fileNamer = FileNamerFactory.getByVersion(this.version);
        featureMap = FeatureMapFactory.getVersion(this.version);
    }
//
    public void setOriginalFilename(String filename) {
        if (filename.contains(File.separator)) {
            filename = new File(filename).getName();
        }
        this.originalFilename = filename;
        if (this.fileExt == null || this.fileExt.isEmpty()) {
            this.fileExt = ParseFilename.getExt(filename);
        }
    }
//
//    public String getCatId() {
//        return catId;
//    }
//
    public void setCatId(String catId) {
        this.catId = catId == null ? catId : fileNamer.formatCatId(catId);
    }
//
//    public String getProcessingString() {
//        return params.toString();
//    }
//
    public void setProcessingString(String processing) throws Exception {
        setProcessing(new ProcessingParams(processing, version));
    }
//
//    /**
//     * This add the passed in processing params to the current set of features
//     * <strong>setParams</strong> will replace the current features
//     *
//     * @param  processing
//     * @throws MirageUtilsException
//     */
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
//
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
//
    public String getFileExt() {
        return fileExt;
    }
//
//    public void setFileExt(String fileExt) {
//        this.fileExt = fileExt;
//    }
//
    public Feature getSource() {
        return source;
    }
//
//    public void setSource(String source) throws MirageUtilsException {
//        if (source == null || source.isEmpty()) {
//            return;
//        }
//        try {
//            setSource(featureMap.findFeature(FeatureGroups.SOURCE, source, true));
//        } catch (MirageUtilsException e) {
//            throw new MirageUtilsException(String.format("Invalid source: %s\nAvailable sources are:\n%s",
//                    source, featureMap.printGroupFeatureList(FeatureGroups.SOURCE)));
//        }
//    }
//
//    public void setSource(Feature source) throws MirageUtilsException {
//        this.source = source;
//        try {
//            this.params.setFeature(this.source);
//        } catch (MirageUtilsException e) {
//            throw new MirageUtilsException(String.format("Invalid source: %s\nAvailable sources are:\n%s",
//                    source, featureMap.printGroupFeatureList(FeatureGroups.SOURCE)));
//        }
//    }
//
//    public String getSourceImageId() {
//        return sourceImageId;
//    }
//
//    public void setSourceImageId(String sourceImageId) {
//        this.sourceImageId = sourceImageId;
//    }
//
    public ProcessingParams getParams() {
        return params;
    }
//
//    public void setParams(ProcessingParams params) {
//        this.params = params;
//    }
//
//    public boolean isProcessed() {
//        return params.isProcessed();
//    }
//
    public boolean isInvalid() {
        return params.isInvalid();
    }
//
//    public void setFullPath(String fullPath) {
//        this.fullPath = fullPath;
//    }
//
//    public void setDirectories(DirectoriesDto directories) {
//        this.directories = directories;
//    }
//
//    public void setDirectories() throws MirageUtilsException {
//        this.directories = ImageFileRefGenerator.getStaticPathGenerator().getDirectories(this);
//    }
//
    public String getFullPath() {
        return fullPath;
    }
//
//    public DirectoriesDto getDirectories() {
//        return directories;
//    }
//
//    public List<ImageFileRef> getRelatedFiles() {
//        return relatedFiles;
//    }
//
//    public void setRelatedFiles(List<ImageFileRef> relatedFiles) {
//        this.relatedFiles = relatedFiles;
//    }
//
//    public void setMessages(List<PathResponse> messages) {
//        this.messages = messages;
//    }
//
//    public List<PathResponse> getMessages() {
//        return messages;
//    }
//
//    public PathResponse findMessageType(PathResponseTypes type) {
//        if (messages == null) {
//            return null;
//        }
//        for (PathResponse response : messages) {
//            if (response.getType() == type) {
//                return response;
//            }
//        }
//        return null;
//    }
//
//    public String getDirectory() {
//        return directory;
//    }
//
//    public void setDirectory(String directory) {
//        this.directory = directory;
//
//    }
//
//    @Override
//    public String toString() {
//        try {
//            return getFilename();
//        } catch (MirageUtilsException e) {
//            return "Error creating filename";
//        }
//    }
//
    public boolean isCropped() {
        try {
            return params.hasFeature("cropped") != null;
        } catch (Exception e) {
            return false;
        }
    }
//
//    public boolean isThumbnail() {
//        try {
//            return params.hasFeature("thumbnail") != null;
//        } catch (NoFeatureException e) {
//            return false;
//        }
//    }
//
//    public String getTempPath() throws MirageUtilsException {
//        return getTempPath(true);
//    }
//
//    public String getTempPath(boolean withExt) throws MirageUtilsException {
//        return String.format("%s/%s",
//                ImageFileRefGenerator.getStaticPathGenerator().getTempDirectory(this),
//                getFilename(withExt));
//    }
//
//    public String getTempDirectory() throws MirageUtilsException {
//        return ImageFileRefGenerator.getStaticPathGenerator().getTempDirectory(this);
//    }
//
//    public boolean exists() {
//        return new File(getFullPath()).exists();
//    }
//
//    public void setFileExt(ImageExtensions fileExt) {
//        this.fileExt = (fileExt == null) ? null : fileExt.toString().toLowerCase();
//    }
//
//    public ImageFileRefType getType() {
//        ImageFileRefType type = null;
//        type = isProcessed() ? ImageFileRefType.PROCESSED : null;
//        type = isCropped() ? ImageFileRefType.CROPPED : type;
//        type = isThumbnail() ? ImageFileRefType.THUMBNAIL : type;
//        type = type == null ? ImageFileRefType.ORIGINAL : type;
//
//        // regardless of the above if there is an illegal bitset mark it as invalid
//        type = isInvalid() ? ImageFileRefType.INVALID : type;
//        return type;
//    }
//
//    public DateTime getCreated() throws IOException {
//        File fileRef = new File(getFullPath());
//        if (!fileRef.exists()) {
//            return null;
//        }
//        BasicFileAttributes attr =
//                Files.readAttributes(Paths.get(fileRef.toURI()), BasicFileAttributes.class);
//        FileTime fileTime = attr.creationTime();
//        return new DateTime(fileTime.toMillis());
//    }
//
    public DateTime getModified() {
        File fileRef = new File(getFullPath());
        if (!fileRef.exists()) {
            return null;
        }
        return new DateTime(fileRef.lastModified());
    }
//
//    public long getSize() {
//        File fileRef = new File(getFullPath());
//        if (!fileRef.exists()) {
//            return 0;
//        }
//        if (fileRef.isDirectory()) {
//            return FileUtils.sizeOfDirectory(fileRef);
//        }
//        return fileRef.length();
//    }
//
//    public String getFullPath(boolean withExt) throws MirageUtilsException {
//        return String.format("%s%s%s",
//                ImageFileRefGenerator.getStaticPathGenerator().getDirectory(this), File.separator,
//                getFilename(withExt));
//    }
//
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

    public String getDescriptiveName() {
        return fileNamer.getDescriptiveName(this);
    }

    public String getGeoDcImageId() {
        return geoDcImageId;
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

    public void setGeoDcImageId(String geoDcImageId) {

        this.geoDcImageId = geoDcImageId;
    }
//
//    /**
//     * Convenience method that returns the features set in the processing params
//     *
//     * @return
//     */
    public List<Feature> getFeatureList() {
        if (params == null) {
            return new ArrayList<Feature>();
        }
        return params.getFeatures();
    }
//
//    /**
//     * Convenience method that returns the feature string names set in the processing params
//     *
//     * @return
//     * @throws NoFeatureException
//     */
    public List<String> getFeatureStringList() {
        if (params == null) {
            return new ArrayList<String>();
        }
        return params.getFeatureStringList();
    }

//    /**
//     * Convenience method that returns the feature ids set in the processing params
//     *
//     * @return
//     * @throws NoFeatureException
//     */
    public int[] getFeatureIds() {
        if (params == null) {
            return new int[0];
        }
        return params.getFeatureIds();
    }
    public boolean isProcessed() {
        return params.isProcessed();
    }

}
