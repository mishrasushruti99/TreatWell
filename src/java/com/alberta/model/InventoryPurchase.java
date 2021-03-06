/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alberta.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author Faraz
 */
public class InventoryPurchase implements Serializable, RowMapper {

    private BigDecimal id;
    private String coaCode;
    private Date purchaseDate;
    private String week;
    private BigDecimal quantity;
    private BigDecimal total;
    private BigDecimal companyId;
    private BigDecimal siteId;
    private String title;
    private String unit;
    private BigDecimal packing;

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {

        InventoryPurchase inv = new InventoryPurchase();
        inv.setId(rs.getBigDecimal("INVENTORY_PURCHASE_ID"));
        inv.setCoaCode(rs.getString("COA_CDE"));
        //inv.setPurchaseDate(rs.getDate("PUR_DATE"));
        inv.setQuantity(rs.getBigDecimal("QTY"));
        inv.setTotal(rs.getBigDecimal("TOTAL"));
        inv.setSiteId(rs.getBigDecimal("SITE_ID"));
        inv.setUnit(rs.getString("UNIT"));
        inv.setTitle(rs.getString("TITLE"));
         inv.setPacking(rs.getBigDecimal("PACKING"));
        //   inv.setCompanyId(rs.getBigDecimal(""));
        return inv;
    }

    /**
     * @return the id
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * @return the coaCode
     */
    public String getCoaCode() {
        return coaCode;
    }

    /**
     * @param coaCode the coaCode to set
     */
    public void setCoaCode(String coaCode) {
        this.coaCode = coaCode;
    }

    /**
     * @return the purchaseDate
     */
    public Date getPurchaseDate() {
        return purchaseDate;
    }

    /**
     * @param purchaseDate the purchaseDate to set
     */
    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    /**
     * @return the quantity
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the total
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    /**
     * @return the companyId
     */
    public BigDecimal getCompanyId() {
        return companyId;
    }

    /**
     * @param companyId the companyId to set
     */
    public void setCompanyId(BigDecimal companyId) {
        this.companyId = companyId;
    }

    /**
     * @return the siteId
     */
    public BigDecimal getSiteId() {
        return siteId;
    }

    /**
     * @param siteId the siteId to set
     */
    public void setSiteId(BigDecimal siteId) {
        this.siteId = siteId;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @return the packing
     */
    public BigDecimal getPacking() {
        return packing;
    }

    /**
     * @param packing the packing to set
     */
    public void setPacking(BigDecimal packing) {
        this.packing = packing;
    }
}
