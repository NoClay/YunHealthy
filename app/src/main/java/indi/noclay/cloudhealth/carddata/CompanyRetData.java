package indi.noclay.cloudhealth.carddata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NoClay on 2018/5/2.
 */

public class CompanyRetData extends ShowApiRetBase implements Serializable{

    private List<DrugFactory> drugFactoryList;

    public List<DrugFactory> getDrugFactoryList() {
        return drugFactoryList;
    }

    public void setDrugFactoryList(List<DrugFactory> drugFactoryList) {
        this.drugFactoryList = drugFactoryList;
    }

    @Override
    public List<Object> getDataItem() {
        List<Object> result = new ArrayList<>();
        if (drugFactoryList != null && drugFactoryList.size() > 0){
            result.addAll(drugFactoryList);
        }
        return result;
    }

    public static class DrugFactory implements Serializable{
        private String factoryName;
        private String linkPhone;
        private String addr;

        public String getFactoryName() {
            return factoryName;
        }

        public void setFactoryName(String factoryName) {
            this.factoryName = factoryName;
        }

        public String getLinkPhone() {
            return linkPhone;
        }

        public void setLinkPhone(String linkPhone) {
            this.linkPhone = linkPhone;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }
    }
}
