package com.capostille.android.models;

public class DocumentsDetailsModel
{
    String documentDetailsId;
    String serviceName;
    String documentName;
    String stateName;
    String status;
    String shortName;
    String otherDocumentName;
    String documentTypeID;

    public String getDocumentTypeID() {
        return documentTypeID;
    }

    public void setDocumentTypeID(String documentTypeID) {
        this.documentTypeID = documentTypeID;
    }

    public String getOtherDocumentName() {
        return otherDocumentName;
    }

    public void setOtherDocumentName(String otherDocumentName) {
        this.otherDocumentName = otherDocumentName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getDocumentDetailsId() {
        return documentDetailsId;
    }

    public void setDocumentDetailsId(String documentDetailsId) {
        this.documentDetailsId = documentDetailsId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
}
