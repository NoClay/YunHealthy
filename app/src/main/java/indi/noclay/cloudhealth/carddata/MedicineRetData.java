package indi.noclay.cloudhealth.carddata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NoClay on 2018/5/2.
 */

public class MedicineRetData extends ShowApiRetBase implements Serializable {
    private List<Drug> drugList;

    public Number getPage() {
        return page;
    }

    public void setPage(Number page) {
        this.page = page;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getAllResults() {
        return allResults;
    }

    public void setAllResults(String allResults) {
        this.allResults = allResults;
    }

    @Override
    public List<Object> getDataItem() {
        List<Object> result = new ArrayList<>();
        if (drugList != null && drugList.size() > 0) {
            result.addAll(drugList);
        }
        return result;
    }

    public List<Drug> getDrugList() {
        return drugList;
    }

    public void setDrugList(List<Drug> drugList) {
        this.drugList = drugList;
    }

    public static class Drug implements Serializable {
        private String blfy; //不良反应
        private String drugName; //药品名称
        private String ggxh; //规格型号
        private String img; //图片地址
        private String jj; //禁忌
        private String manu; //生产企业
        private String price; //参考价格
        private String pzwh; //批准文号
        private String syz; //适应症
        private String type; //药品类别
        private String xz; //性状
        private String yfyl; //用法用量
        private String ywxhzy; //药物相互作用
        private String yxq; //有效期
        private String zc; //储藏
        private String zxbz; //执行标准
        private String zysx; //注意事项
        private String zzjb; //主治疾病
        private String zycf; //主要成分

        public String getBlfy() {
            return blfy;
        }

        public void setBlfy(String blfy) {
            this.blfy = blfy;
        }

        public String getDrugName() {
            return drugName;
        }

        public void setDrugName(String drugName) {
            this.drugName = drugName;
        }

        public String getGgxh() {
            return ggxh;
        }

        public void setGgxh(String ggxh) {
            this.ggxh = ggxh;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getJj() {
            return jj;
        }

        public void setJj(String jj) {
            this.jj = jj;
        }

        public String getManu() {
            return manu;
        }

        public void setManu(String manu) {
            this.manu = manu;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getPzwh() {
            return pzwh;
        }

        public void setPzwh(String pzwh) {
            this.pzwh = pzwh;
        }

        public String getSyz() {
            return syz;
        }

        public void setSyz(String syz) {
            this.syz = syz;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getXz() {
            return xz;
        }

        public void setXz(String xz) {
            this.xz = xz;
        }

        public String getYfyl() {
            return yfyl;
        }

        public void setYfyl(String yfyl) {
            this.yfyl = yfyl;
        }

        public String getYwxhzy() {
            return ywxhzy;
        }

        public void setYwxhzy(String ywxhzy) {
            this.ywxhzy = ywxhzy;
        }

        public String getYxq() {
            return yxq;
        }

        public void setYxq(String yxq) {
            this.yxq = yxq;
        }

        public String getZc() {
            return zc;
        }

        public void setZc(String zc) {
            this.zc = zc;
        }

        public String getZxbz() {
            return zxbz;
        }

        public void setZxbz(String zxbz) {
            this.zxbz = zxbz;
        }

        public String getZysx() {
            return zysx;
        }

        public void setZysx(String zysx) {
            this.zysx = zysx;
        }

        public String getZzjb() {
            return zzjb;
        }

        public void setZzjb(String zzjb) {
            this.zzjb = zzjb;
        }

        public String getZycf() {
            return zycf;
        }

        public void setZycf(String zycf) {
            this.zycf = zycf;
        }
    }
}
