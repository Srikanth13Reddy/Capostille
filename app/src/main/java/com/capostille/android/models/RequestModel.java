package com.capostille.android.models;

import java.util.Comparator;

public class RequestModel
{
    private String userRequestDetailsId;
    private String requestedDate;
    private String customerId;
    private String status;
    private String assignedTo;
    private String documentsCount;
    private String name;
    private String fullAddress;
    private String assignedToName;
    private String requestCode;

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public String getUserRequestDetailsId() {
        return userRequestDetailsId;
    }

    public void setUserRequestDetailsId(String userRequestDetailsId) {
        this.userRequestDetailsId = userRequestDetailsId;
    }

    public String getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(String requestedDate) {
        this.requestedDate = requestedDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getDocumentsCount() {
        return documentsCount;
    }

    public void setDocumentsCount(String documentsCount) {
        this.documentsCount = documentsCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public void setAssignedToName(String assignedToName) {
        this.assignedToName = assignedToName;
    }

    public static Comparator<RequestModel> nameComparator = new Comparator<RequestModel>() {
        @Override
        public int compare(RequestModel jc1, RequestModel jc2) {
            return (int) (jc1.getName().compareTo(jc2.getName()));
        }
    };

    public static Comparator<RequestModel> statusComparator = new Comparator<RequestModel>() {
        @Override
        public int compare(RequestModel jc1, RequestModel jc2) {
            return (int) (jc1.getStatus().compareTo(jc2.getStatus()));
        }
    };

}
