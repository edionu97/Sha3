package utils.constants.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Constants {

    private String rejectedFileName;
    private String rejectedFileType;
    private String mainDirectory;

    public Constants() {
    }

    public String getRejectedFileName() {
        return rejectedFileName;
    }

    @XmlElement(
            name = "rejected-file-name"
    )
    public void setRejectedFileName(String rejectedFileName) {
        this.rejectedFileName = rejectedFileName;
    }

    public String getRejectedFileType() {
        return rejectedFileType;
    }

    @XmlElement(
            name = "rejected-file-type"
    )
    public void setRejectedFileType(String rejectedFileType) {
        this.rejectedFileType = rejectedFileType;
    }

    public String getMainDirectory() {
        return mainDirectory;
    }

    @XmlElement(
            name = "main-directory-absolute-location"
    )
    public void setMainDirectory(String mainDirectory) {
        this.mainDirectory = mainDirectory;
    }
}