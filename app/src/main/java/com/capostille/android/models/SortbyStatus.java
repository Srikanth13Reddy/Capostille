package com.capostille.android.models;

import java.util.ArrayList;
import java.util.Collections;

public class SortbyStatus 
{
    ArrayList<RequestModel> jobCandidate = new ArrayList<>();
    public SortbyStatus(ArrayList<RequestModel> jobCandidate) {
        this.jobCandidate = jobCandidate;
    }
    public ArrayList<RequestModel> getSortedByName() {
        Collections.sort(jobCandidate, RequestModel.nameComparator);
        return jobCandidate;
    }
    public ArrayList<RequestModel> getSortedByStatus() {
        Collections.sort(jobCandidate, RequestModel.statusComparator);
        return jobCandidate;
    }
}
