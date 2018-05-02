package indi.noclay.cloudhealth.carddata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NoClay on 2018/5/2.
 */

public class IllnessRetData extends ShowApiRetBase implements Serializable{
    private PageBean pagebean;

    public PageBean getPagebean() {
        return pagebean;
    }

    public void setPagebean(PageBean pagebean) {
        this.pagebean = pagebean;
    }

    @Override
    public List<Object> getDataItem() {
        List<Object> result = new ArrayList<>();
        if (pagebean.getContentlist() != null && pagebean.getContentlist().size() > 0){
            result.addAll(pagebean.getContentlist());
        }
        return result;
    }

    public static class Illness implements Serializable{
        private String id; //疾病id
        private String summary; //描述
        private String typeName; //科目名称
        private String typeId; //科室id
        private String subTypeId; //子科室id
        private String subTypeName; //子科室名称
        private String name; //疾病名称
        private List<Tag> tagList; //标签
        public static class Tag implements Serializable{
            private String name; //标签名
            private String content; //标签内容

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
        private String ct; //入库时间

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getTypeId() {
            return typeId;
        }

        public void setTypeId(String typeId) {
            this.typeId = typeId;
        }

        public String getSubTypeId() {
            return subTypeId;
        }

        public void setSubTypeId(String subTypeId) {
            this.subTypeId = subTypeId;
        }

        public String getSubTypeName() {
            return subTypeName;
        }

        public void setSubTypeName(String subTypeName) {
            this.subTypeName = subTypeName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Tag> getTagList() {
            return tagList;
        }

        public void setTagList(List<Tag> tagList) {
            this.tagList = tagList;
        }

        public String getCt() {
            return ct;
        }

        public void setCt(String ct) {
            this.ct = ct;
        }
    }

    public static class PageBean implements Serializable{
        private Integer allPages;
        private List<Illness> contentlist;
        private Integer currentPage;
        private Integer allNum;
        private String maxResult;

        public Integer getAllPages() {
            return allPages;
        }

        public void setAllPages(Integer allPages) {
            this.allPages = allPages;
        }

        public List<Illness> getContentlist() {
            return contentlist;
        }

        public void setContentlist(List<Illness> contentlist) {
            this.contentlist = contentlist;
        }

        public Integer getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(Integer currentPage) {
            this.currentPage = currentPage;
        }

        public Integer getAllNum() {
            return allNum;
        }

        public void setAllNum(Integer allNum) {
            this.allNum = allNum;
        }

        public String getMaxResult() {
            return maxResult;
        }

        public void setMaxResult(String maxResult) {
            this.maxResult = maxResult;
        }
    }
}
