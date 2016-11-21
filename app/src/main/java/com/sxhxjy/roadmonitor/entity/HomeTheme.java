package com.sxhxjy.roadmonitor.entity;

import java.util.List;

/**
 * Created by zm on 2016/11/16.
 */

public class HomeTheme {


    /**
     * data : [{"father_id":"0","id":"4028812c57b7e2290157b82883fb0005","name":"环境主题"},{"father_id":"0","id":"4028812c57b7e2290157b82de4280008","name":"变形主题"},{"father_id":"0","id":"4028812c57bba19c0157bbb8b94b0003","name":"应力/应变主题"}]
     * resultCode : 200
     * resultMessage : 查询成功
     */

    private String resultCode;
    private String resultMessage;
    private List<DataBean> data;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * father_id : 0
         * id : 4028812c57b7e2290157b82883fb0005
         * name : 环境主题
         */

        private String father_id;
        private String id;
        private String name;

        public String getFather_id() {
            return father_id;
        }

        public void setFather_id(String father_id) {
            this.father_id = father_id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
