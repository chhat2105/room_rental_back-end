package com.chhatkh.dev.models;

import com.chhatkh.dev.constants.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = -8892838641805537110L;
    @Column(name = "CREATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt = new Date();
    @Column(name = "CREATED_BY")
    private String createBy = Constants.SYSTEM;
    @Column(name = "UPDATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateAt;
    @Column(name = "UPDATED_BY")
    private String updateBy;

}
