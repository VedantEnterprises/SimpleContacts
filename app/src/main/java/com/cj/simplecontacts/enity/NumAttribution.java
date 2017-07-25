package com.cj.simplecontacts.enity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by chenjun on 17-7-25.
 * 映射到数据库
 */
@Entity
public class NumAttribution {
    @Id
    private Long id;
    @Property(nameInDb = "code")
    private String code;//1300000  前7位  或 固定电话区号  021
    @Property(nameInDb = "type")
    private String type;//cellPhone or fixedPhone
    @Property(nameInDb = "attribution")
    private String attribution;//山东-济南 联通
    @Generated(hash = 399951444)
    public NumAttribution(Long id, String code, String type, String attribution) {
        this.id = id;
        this.code = code;
        this.type = type;
        this.attribution = attribution;
    }
    @Generated(hash = 745184698)
    public NumAttribution() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCode() {
        return this.code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getAttribution() {
        return this.attribution;
    }
    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }


}
