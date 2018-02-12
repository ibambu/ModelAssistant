/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.snal.test;

/**
 *
 * @author Luo Tao
 */
public class Sence {

    private String senceCode;
    private String senceSeq;
    private String senceId;
    private String senceName;
    private String senceTypeId;
    private String senceTypeName;
    private String cityId;
    private String branchCode;

    public Sence() {

    }

    public Sence(String senceCode, String senceSeq, String senceId, String senceName,
            String senceTypeId, String senceTypeName, String cityId, String branchCode) {
        this.senceCode = senceCode;
        this.senceSeq = senceSeq;
        this.senceId = senceId;
        this.senceName = senceName;
        this.senceTypeId = senceTypeId;
        this.senceTypeName = senceTypeName;
        this.cityId = cityId;
        this.branchCode = branchCode;
    }

    public String getSenceCode() {
        return senceCode;
    }

    public void setSenceCode(String senceCode) {
        this.senceCode = senceCode;
    }

    public String getSenceSeq() {
        return senceSeq;
    }

    public void setSenceSeq(String senceSeq) {
        this.senceSeq = senceSeq;
    }

    public String getSenceId() {
        return senceId;
    }

    public void setSenceId(String senceId) {
        this.senceId = senceId;
    }

    public String getSenceName() {
        return senceName;
    }

    public void setSenceName(String senceName) {
        this.senceName = senceName;
    }

    public String getSenceTypeId() {
        return senceTypeId;
    }

    public void setSenceTypeId(String senceTypeId) {
        this.senceTypeId = senceTypeId;
    }

    public String getSenceTypeName() {
        return senceTypeName;
    }

    public void setSenceTypeName(String senceTypeName) {
        this.senceTypeName = senceTypeName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    @Override
    public String toString() {
        return senceCode + "," + senceId + "," + senceName + "," + senceTypeId + "," + senceTypeName + "," + cityId + "," + branchCode;
    }

}
