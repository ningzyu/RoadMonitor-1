package com.sxhxjy.defensemonitor.entity;

import java.util.List;

/**
 * 2016/9/18
 *
 * @author Michael Zhao
 */
public class GroupTree {
    /**
     * id : 4028816456d59b480156d59dc2d40001
     * type : null
     * level : null
     * uid :
     * num : sxsjtt
     * name : 山西省交通运输厅
     * description : 山西省交通运输厅
     * saveTime : 1472463480000
     * deleteState : 0
     * parentid : 0
     * provinceid : 140000
     * cityid : 140100
     * adCity : {"id":140100,"city":"太原市","father":"140000"}
     * adProvince : {"id":140000,"province":"山西省"}
     * priRoleList : null
     * roleIds : null
     * childrenGroup : [{"id":"4028816456d5a2400156d5aaa38a0000","type":null,"level":null,"uid":"","num":"sxsgsglglj","name":"山西省高速公路管理局","description":"山西省高速公路管理局","saveTime":1472463545000,"deleteState":"0","parentid":"4028816456d59b480156d59dc2d40001","provinceid":"140000","cityid":"140100","adCity":{"id":140100,"city":"太原市","father":"140000"},"adProvince":{"id":140000,"province":"山西省"},"priRoleList":null,"roleIds":null,"childrenGroup":[{"id":"4028816456da23dd0156da29ee1c0000","type":null,"level":null,"uid":"","num":"TJGS","name":"太旧公司","description":"太旧公司","saveTime":1472538996000,"deleteState":"0","parentid":"4028816456d5a2400156d5aaa38a0000","provinceid":"140000","cityid":"140100","adCity":{"id":140100,"city":"太原市","father":"140000"},"adProvince":{"id":140000,"province":"山西省"},"priRoleList":null,"roleIds":null,"childrenGroup":[{"id":"4028816456da23dd0156da2d05570003","type":null,"level":null,"uid":null,"num":"TJGSYQ","name":"太旧高速阳泉路段","description":"太旧高速阳泉路段","saveTime":1472539198000,"deleteState":"0","parentid":"4028816456da23dd0156da29ee1c0000","provinceid":"140000","cityid":"140100","adCity":{"id":140100,"city":"太原市","father":"140000"},"adProvince":{"id":140000,"province":"山西省"},"priRoleList":null,"roleIds":null,"childrenGroup":null}]},{"id":"4028816456da23dd0156da2b59400001","type":null,"level":null,"uid":"","num":"DTGS","name":"大同公司","description":"大同公司","saveTime":1472539089000,"deleteState":"0","parentid":"4028816456d5a2400156d5aaa38a0000","provinceid":"140000","cityid":"140100","adCity":{"id":140100,"city":"太原市","father":"140000"},"adProvince":{"id":140000,"province":"山西省"},"priRoleList":null,"roleIds":null,"childrenGroup":null},{"id":"4028816456da23dd0156da2c64120002","type":null,"level":null,"uid":"","num":"QLGS","name":"祁临公司","description":"祁临公司","saveTime":1472539157000,"deleteState":"0","parentid":"4028816456d5a2400156d5aaa38a0000","provinceid":"140000","cityid":"140100","adCity":{"id":140100,"city":"太原市","father":"140000"},"adProvince":{"id":140000,"province":"山西省"},"priRoleList":null,"roleIds":null,"childrenGroup":null},{"id":"4028816456da23dd0156da9394820009","type":null,"level":null,"uid":"","num":"scsjtt","name":"陕西省交通厅","description":"陕西省交通厅","saveTime":1472545920000,"deleteState":"0","parentid":"4028816456d5a2400156d5aaa38a0000","provinceid":"140000","cityid":"140100","adCity":{"id":140100,"city":"太原市","father":"140000"},"adProvince":{"id":140000,"province":"山西省"},"priRoleList":null,"roleIds":null,"childrenGroup":null}]}]
     */

    public String id;
    public Object type;
    public Object level;
    public String uid;
    public String num;
    public String name;
    public String description;
    public long saveTime;
    public String deleteState;
    public String parentid;
    public String provinceid;
    public String cityid;
    /**
     * id : 140100
     * city : 太原市
     * father : 140000
     */

    public AdCity adCity;
    /**
     * id : 140000
     * province : 山西省
     */

    public AdProvince adProvince;
    public Object priRoleList;
    public Object roleIds;
    /**
     * id : 4028816456d5a2400156d5aaa38a0000
     * type : null
     * level : null
     * uid :
     * num : sxsgsglglj
     * name : 山西省高速公路管理局
     * description : 山西省高速公路管理局
     * saveTime : 1472463545000
     * deleteState : 0
     * parentid : 4028816456d59b480156d59dc2d40001
     * provinceid : 140000
     * cityid : 140100
     * adCity : {"id":140100,"city":"太原市","father":"140000"}
     * adProvince : {"id":140000,"province":"山西省"}
     * priRoleList : null
     * roleIds : null
     * childrenGroup : [{"id":"4028816456da23dd0156da29ee1c0000","type":null,"level":null,"uid":"","num":"TJGS","name":"太旧公司","description":"太旧公司","saveTime":1472538996000,"deleteState":"0","parentid":"4028816456d5a2400156d5aaa38a0000","provinceid":"140000","cityid":"140100","adCity":{"id":140100,"city":"太原市","father":"140000"},"adProvince":{"id":140000,"province":"山西省"},"priRoleList":null,"roleIds":null,"childrenGroup":[{"id":"4028816456da23dd0156da2d05570003","type":null,"level":null,"uid":null,"num":"TJGSYQ","name":"太旧高速阳泉路段","description":"太旧高速阳泉路段","saveTime":1472539198000,"deleteState":"0","parentid":"4028816456da23dd0156da29ee1c0000","provinceid":"140000","cityid":"140100","adCity":{"id":140100,"city":"太原市","father":"140000"},"adProvince":{"id":140000,"province":"山西省"},"priRoleList":null,"roleIds":null,"childrenGroup":null}]},{"id":"4028816456da23dd0156da2b59400001","type":null,"level":null,"uid":"","num":"DTGS","name":"大同公司","description":"大同公司","saveTime":1472539089000,"deleteState":"0","parentid":"4028816456d5a2400156d5aaa38a0000","provinceid":"140000","cityid":"140100","adCity":{"id":140100,"city":"太原市","father":"140000"},"adProvince":{"id":140000,"province":"山西省"},"priRoleList":null,"roleIds":null,"childrenGroup":null},{"id":"4028816456da23dd0156da2c64120002","type":null,"level":null,"uid":"","num":"QLGS","name":"祁临公司","description":"祁临公司","saveTime":1472539157000,"deleteState":"0","parentid":"4028816456d5a2400156d5aaa38a0000","provinceid":"140000","cityid":"140100","adCity":{"id":140100,"city":"太原市","father":"140000"},"adProvince":{"id":140000,"province":"山西省"},"priRoleList":null,"roleIds":null,"childrenGroup":null},{"id":"4028816456da23dd0156da9394820009","type":null,"level":null,"uid":"","num":"scsjtt","name":"陕西省交通厅","description":"陕西省交通厅","saveTime":1472545920000,"deleteState":"0","parentid":"4028816456d5a2400156d5aaa38a0000","provinceid":"140000","cityid":"140100","adCity":{"id":140100,"city":"太原市","father":"140000"},"adProvince":{"id":140000,"province":"山西省"},"priRoleList":null,"roleIds":null,"childrenGroup":null}]
     */

    public List<GroupTree> childrenGroup;

    public static class AdCity {
        public int id;
        public String city;
        public String father;
    }

    public static class AdProvince {
        public int id;
        public String province;
    }
}
