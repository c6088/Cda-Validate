package com.bayc.xsdvalidatecmd.entity;

import java.io.Serializable;

/**
 * 手术知情告知书(Cda26)实体类
 *
 * @author makejava
 * @since 2022-07-04 11:03:14
 */
public class Cda26 implements Serializable {
    private static final long serialVersionUID = -92627446600132212L;
    /**
     * 病人类型
     */
    private String patientType;
    /**
     * 病人ID
     */
    private String patientId;
    /**
     * 住院次数或者门诊号
     */
    private Integer visitId;
    /**
     * 文件号
     */
    private Integer fileNo;
    /**
     * 文档状态
     */
    private String docStatus;
    /**
     * 知情同意书编号
     */
    private String informedConsentNo;
    /**
     * 患者身份证件号码
     */
    private String patIdNumber;
    /**
     * 患者姓名
     */
    private String patName;
    /**
     * 出生日期
     */
    private String dateBirth;
    /**
     * 年龄（岁）
     */
    private Integer ageYear;
    /**
     * 年龄单位
     */
    private String age;
    /**
     * 医疗机构意见
     */
    private String organizationView;
    /**
     * 患者/代理人意见
     */
    private String patLegalRepView;
    /**
     * CDA代码
     */
    private String cdaCode;
    /**
     * 性别代码
     */
    private String sexCode;
    /**
     * 性别名称
     */
    private String sexName;
    /**
     * 经治医师ID
     */
    private String attendingDoctorId;
    /**
     * 经治医师姓名
     */
    private String attendingDoctorName;
    /**
     * 经治医师签名时间
     */
    private String attendingDoctorTime;
    /**
     * 手术者ID
     */
    private String surgeryDrId;
    /**
     * 手术者名称
     */
    private String surgeryDrName;
    /**
     * 手术者签名时间
     */
    private String surgeryDrTime;
    /**
     * 患者ID=PATIENT_ID
     */
    private String patSignId;
    /**
     * 患者姓名=PAT_NAME
     */
    private String patSignName;
    /**
     * 患者签名时间
     */
    private String patSignTime;
    /**
     * 代理人姓名
     */
    private String legalRepName;
    /**
     * 代理人签名时间
     */
    private String legalRepSignTime;
    /**
     * 代理人与患者的关系代码
     */
    private String legalRepRelationCode;
    /**
     * 代理人与患者的关系名称
     */
    private String legalRepRelationName;
    /**
     * 病床ID
     */
    private String bedId;
    /**
     * 病床号
     */
    private String bedName;
    /**
     * 病房ID
     */
    private String roomId;
    /**
     * 病房号
     */
    private String roomName;
    /**
     * 科室ID
     */
    private String deptId;
    /**
     * 科室名称
     */
    private String deptName;
    /**
     * 病区ID
     */
    private String wardId;
    /**
     * 病区名称
     */
    private String wardName;
    /**
     * 医院ID
     */
    private String hospitalId;
    /**
     * 医院名称
     */
    private String hospitalName;
    /**
     * 术前诊断编码
     */
    private String diagnosisCode;
    /**
     * 术前诊断名称
     */
    private String diagnosisName;
    /**
     * 实施手术编码
     */
    private String surgeryCode;
    /**
     * 实施手术名称
     */
    private String surgeryName;
    /**
     * 实施手术时间
     */
    private String surgeryTime;
    /**
     * 手术方式
     */
    private String surgeryMethod;
    /**
     * 术前准备
     */
    private String surgeryReady;
    /**
     * 手术禁忌症
     */
    private String surgeryTaboo;
    /**
     * 手术指征
     */
    private String surgeryIndication;
    /**
     * 实施麻醉方法代码
     */
    private String anaesMethodCode;
    /**
     * 实施麻醉方法名称
     */
    private String anaesMethodName;
    /**
     * 替代方案
     */
    private String replaceOptions;
    /**
     * 手术中可能出现的意外及风险
     */
    private String surgeryRisk;
    /**
     * 手术后可能出现的意外及并发症
     */
    private String poc;


    public String getPatientType() {
        return patientType;
    }

    public void setPatientType(String patientType) {
        this.patientType = patientType;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Integer getVisitId() {
        return visitId;
    }

    public void setVisitId(Integer visitId) {
        this.visitId = visitId;
    }

    public Integer getFileNo() {
        return fileNo;
    }

    public void setFileNo(Integer fileNo) {
        this.fileNo = fileNo;
    }

    public String getDocStatus() {
        return docStatus;
    }

    public void setDocStatus(String docStatus) {
        this.docStatus = docStatus;
    }

    public String getInformedConsentNo() {
        return informedConsentNo;
    }

    public void setInformedConsentNo(String informedConsentNo) {
        this.informedConsentNo = informedConsentNo;
    }

    public String getPatIdNumber() {
        return patIdNumber;
    }

    public void setPatIdNumber(String patIdNumber) {
        this.patIdNumber = patIdNumber;
    }

    public String getPatName() {
        return patName;
    }

    public void setPatName(String patName) {
        this.patName = patName;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(String dateBirth) {
        this.dateBirth = dateBirth;
    }

    public Integer getAgeYear() {
        return ageYear;
    }

    public void setAgeYear(Integer ageYear) {
        this.ageYear = ageYear;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getOrganizationView() {
        return organizationView;
    }

    public void setOrganizationView(String organizationView) {
        this.organizationView = organizationView;
    }

    public String getPatLegalRepView() {
        return patLegalRepView;
    }

    public void setPatLegalRepView(String patLegalRepView) {
        this.patLegalRepView = patLegalRepView;
    }

    public String getCdaCode() {
        return cdaCode;
    }

    public void setCdaCode(String cdaCode) {
        this.cdaCode = cdaCode;
    }

    public String getSexCode() {
        return sexCode;
    }

    public void setSexCode(String sexCode) {
        this.sexCode = sexCode;
    }

    public String getSexName() {
        return sexName;
    }

    public void setSexName(String sexName) {
        this.sexName = sexName;
    }

    public String getAttendingDoctorId() {
        return attendingDoctorId;
    }

    public void setAttendingDoctorId(String attendingDoctorId) {
        this.attendingDoctorId = attendingDoctorId;
    }

    public String getAttendingDoctorName() {
        return attendingDoctorName;
    }

    public void setAttendingDoctorName(String attendingDoctorName) {
        this.attendingDoctorName = attendingDoctorName;
    }

    public String getAttendingDoctorTime() {
        return attendingDoctorTime;
    }

    public void setAttendingDoctorTime(String attendingDoctorTime) {
        this.attendingDoctorTime = attendingDoctorTime;
    }

    public String getSurgeryDrId() {
        return surgeryDrId;
    }

    public void setSurgeryDrId(String surgeryDrId) {
        this.surgeryDrId = surgeryDrId;
    }

    public String getSurgeryDrName() {
        return surgeryDrName;
    }

    public void setSurgeryDrName(String surgeryDrName) {
        this.surgeryDrName = surgeryDrName;
    }

    public String getSurgeryDrTime() {
        return surgeryDrTime;
    }

    public void setSurgeryDrTime(String surgeryDrTime) {
        this.surgeryDrTime = surgeryDrTime;
    }

    public String getPatSignId() {
        return patSignId;
    }

    public void setPatSignId(String patSignId) {
        this.patSignId = patSignId;
    }

    public String getPatSignName() {
        return patSignName;
    }

    public void setPatSignName(String patSignName) {
        this.patSignName = patSignName;
    }

    public String getPatSignTime() {
        return patSignTime;
    }

    public void setPatSignTime(String patSignTime) {
        this.patSignTime = patSignTime;
    }

    public String getLegalRepName() {
        return legalRepName;
    }

    public void setLegalRepName(String legalRepName) {
        this.legalRepName = legalRepName;
    }

    public String getLegalRepSignTime() {
        return legalRepSignTime;
    }

    public void setLegalRepSignTime(String legalRepSignTime) {
        this.legalRepSignTime = legalRepSignTime;
    }

    public String getLegalRepRelationCode() {
        return legalRepRelationCode;
    }

    public void setLegalRepRelationCode(String legalRepRelationCode) {
        this.legalRepRelationCode = legalRepRelationCode;
    }

    public String getLegalRepRelationName() {
        return legalRepRelationName;
    }

    public void setLegalRepRelationName(String legalRepRelationName) {
        this.legalRepRelationName = legalRepRelationName;
    }

    public String getBedId() {
        return bedId;
    }

    public void setBedId(String bedId) {
        this.bedId = bedId;
    }

    public String getBedName() {
        return bedName;
    }

    public void setBedName(String bedName) {
        this.bedName = bedName;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getWardId() {
        return wardId;
    }

    public void setWardId(String wardId) {
        this.wardId = wardId;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getDiagnosisCode() {
        return diagnosisCode;
    }

    public void setDiagnosisCode(String diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
    }

    public String getDiagnosisName() {
        return diagnosisName;
    }

    public void setDiagnosisName(String diagnosisName) {
        this.diagnosisName = diagnosisName;
    }

    public String getSurgeryCode() {
        return surgeryCode;
    }

    public void setSurgeryCode(String surgeryCode) {
        this.surgeryCode = surgeryCode;
    }

    public String getSurgeryName() {
        return surgeryName;
    }

    public void setSurgeryName(String surgeryName) {
        this.surgeryName = surgeryName;
    }

    public String getSurgeryTime() {
        return surgeryTime;
    }

    public void setSurgeryTime(String surgeryTime) {
        this.surgeryTime = surgeryTime;
    }

    public String getSurgeryMethod() {
        return surgeryMethod;
    }

    public void setSurgeryMethod(String surgeryMethod) {
        this.surgeryMethod = surgeryMethod;
    }

    public String getSurgeryReady() {
        return surgeryReady;
    }

    public void setSurgeryReady(String surgeryReady) {
        this.surgeryReady = surgeryReady;
    }

    public String getSurgeryTaboo() {
        return surgeryTaboo;
    }

    public void setSurgeryTaboo(String surgeryTaboo) {
        this.surgeryTaboo = surgeryTaboo;
    }

    public String getSurgeryIndication() {
        return surgeryIndication;
    }

    public void setSurgeryIndication(String surgeryIndication) {
        this.surgeryIndication = surgeryIndication;
    }

    public String getAnaesMethodCode() {
        return anaesMethodCode;
    }

    public void setAnaesMethodCode(String anaesMethodCode) {
        this.anaesMethodCode = anaesMethodCode;
    }

    public String getAnaesMethodName() {
        return anaesMethodName;
    }

    public void setAnaesMethodName(String anaesMethodName) {
        this.anaesMethodName = anaesMethodName;
    }

    public String getReplaceOptions() {
        return replaceOptions;
    }

    public void setReplaceOptions(String replaceOptions) {
        this.replaceOptions = replaceOptions;
    }

    public String getSurgeryRisk() {
        return surgeryRisk;
    }

    public void setSurgeryRisk(String surgeryRisk) {
        this.surgeryRisk = surgeryRisk;
    }

    public String getPoc() {
        return poc;
    }

    public void setPoc(String poc) {
        this.poc = poc;
    }

}

