/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alberta.model;

/**
 *
 * @author farazahmad
 */
public class PrescriptionVO {

    private String prescriptionMasterId;
    private String patientId;
    private String remarks;
    private String doctorId;
    private String clinicId;
    private String prescriptionNo;
    private String[] medicineId;
    private String[] days;
    private String[] qty;
    private String[] frequencyId;
    private String[] usageId;
    private String[] labId;
    private String[] labTestId;
    private String[] labCenterId;
    private String[] occurrence;
    private String[] questions;
    private String[] answers;
    private String[] vaccinationMasterId;
    private String[] questionCategories;
    private String[] questionRemarks;
    private String[] diagnosticsId;
    private String[] diagnosticVal;
    private String questionCategory;
    private String userName;

    /**
     * @return the prescriptionMasterId
     */
    public String getPrescriptionMasterId() {
        return prescriptionMasterId;
    }

    /**
     * @param prescriptionMasterId the prescriptionMasterId to set
     */
    public void setPrescriptionMasterId(String prescriptionMasterId) {
        this.prescriptionMasterId = prescriptionMasterId;
    }

    public String[] getLabCenterId() {
        return labCenterId;
    }

    public void setLabCenterId(String[] labCenterId) {
        this.labCenterId = labCenterId;
    }

    public String[] getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(String[] occurrence) {
        this.occurrence = occurrence;
    }

    /**
     * @return the patientId
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * @param patientId the patientId to set
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * @return the doctorId
     */
    public String getDoctorId() {
        return doctorId;
    }

    /**
     * @param doctorId the doctorId to set
     */
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    /**
     * @return the clinicId
     */
    public String getClinicId() {
        return clinicId;
    }

    /**
     * @param clinicId the clinicId to set
     */
    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    /**
     * @return the medicineId
     */
    public String[] getMedicineId() {
        return medicineId;
    }

    /**
     * @param medicineId the medicineId to set
     */
    public void setMedicineId(String[] medicineId) {
        this.medicineId = medicineId;
    }

    /**
     * @return the days
     */
    public String[] getDays() {
        return days;
    }

    /**
     * @param days the days to set
     */
    public void setDays(String[] days) {
        this.days = days;
    }

    /**
     * @return the qty
     */
    public String[] getQty() {
        return qty;
    }

    /**
     * @param qty the qty to set
     */
    public void setQty(String[] qty) {
        this.qty = qty;
    }

    /**
     * @return the frequencyId
     */
    public String[] getFrequencyId() {
        return frequencyId;
    }

    /**
     * @param frequencyId the frequencyId to set
     */
    public void setFrequencyId(String[] frequencyId) {
        this.frequencyId = frequencyId;
    }

    /**
     * @return the usageId
     */
    public String[] getUsageId() {
        return usageId;
    }

    /**
     * @param usageId the usageId to set
     */
    public void setUsageId(String[] usageId) {
        this.usageId = usageId;
    }

    /**
     * @return the labId
     */
    public String[] getLabId() {
        return labId;
    }

    /**
     * @param labId the labId to set
     */
    public void setLabId(String[] labId) {
        this.labId = labId;
    }

    /**
     * @return the labTestId
     */
    public String[] getLabTestId() {
        return labTestId;
    }

    /**
     * @param labTestId the labTestId to set
     */
    public void setLabTestId(String[] labTestId) {
        this.labTestId = labTestId;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the questions
     */
    public String[] getQuestions() {
        return questions;
    }

    /**
     * @param questions the questions to set
     */
    public void setQuestions(String[] questions) {
        this.questions = questions;
    }

    /**
     * @return the answers
     */
    public String[] getAnswers() {
        return answers;
    }

    /**
     * @param answers the answers to set
     */
    public void setAnswers(String[] answers) {
        this.answers = answers;
    }

    /**
     * @return the questionCategory
     */
    public String getQuestionCategory() {
        return questionCategory;
    }

    /**
     * @param questionCategory the questionCategory to set
     */
    public void setQuestionCategory(String questionCategory) {
        this.questionCategory = questionCategory;
    }

    /**
     * @return the vaccinationMasterId
     */
    public String[] getVaccinationMasterId() {
        return vaccinationMasterId;
    }

    /**
     * @param vaccinationMasterId the vaccinationMasterId to set
     */
    public void setVaccinationMasterId(String[] vaccinationMasterId) {
        this.vaccinationMasterId = vaccinationMasterId;
    }

    /**
     * @return the prescriptionNo
     */
    public String getPrescriptionNo() {
        return prescriptionNo;
    }

    /**
     * @param prescriptionNo the prescriptionNo to set
     */
    public void setPrescriptionNo(String prescriptionNo) {
        this.prescriptionNo = prescriptionNo;
    }

    /**
     * @return the questionCategories
     */
    public String[] getQuestionCategories() {
        return questionCategories;
    }

    /**
     * @param questionCategories the questionCategories to set
     */
    public void setQuestionCategories(String[] questionCategories) {
        this.questionCategories = questionCategories;
    }

    /**
     * @return the questionRemarks
     */
    public String[] getQuestionRemarks() {
        return questionRemarks;
    }

    /**
     * @param questionRemarks the questionRemarks to set
     */
    public void setQuestionRemarks(String[] questionRemarks) {
        this.questionRemarks = questionRemarks;
    }

    /**
     * @return the diagnosticsId
     */
    public String[] getDiagnosticsId() {
        return diagnosticsId;
    }

    /**
     * @param diagnosticsId the diagnosticsId to set
     */
    public void setDiagnosticsId(String[] diagnosticsId) {
        this.diagnosticsId = diagnosticsId;
    }

    /**
     * @return the diagnosticVal
     */
    public String[] getDiagnosticVal() {
        return diagnosticVal;
    }

    /**
     * @param diagnosticVal the diagnosticVal to set
     */
    public void setDiagnosticVal(String[] diagnosticVal) {
        this.diagnosticVal = diagnosticVal;
    }

}
