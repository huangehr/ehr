package com.yihu.ehr.ha;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.geogrephy.GeographyModel;
import com.yihu.ehr.agModel.patient.CardModel;
import com.yihu.ehr.agModel.patient.PatientDetailModel;
import com.yihu.ehr.ha.patient.controller.CardController;
import com.yihu.ehr.ha.patient.controller.PatientController;
import com.yihu.ehr.util.Envelop;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by AndyCai on 2016/2/26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AgAdminApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PatientControllerTests {

    @Autowired
    private PatientController patientController;

    @Autowired
    private CardController cardController;

    @Autowired
    private ObjectMapper objectMapper;

    ApplicationContext applicationContext;

    @Test
    public void atestCard()throws Exception{
        applicationContext = new SpringApplicationBuilder()
                .web(false).sources(AgAdminApplication.class).run();

        PatientDetailModel detailModel = new PatientDetailModel();
        detailModel.setName("test_cms");
        detailModel.setIdCardNo("350625199010211030");
        detailModel.setGender("Male");
        detailModel.setNation("1");
        detailModel.setNativePlace("福建漳州");
        detailModel.setMartialStatus("10");
        detailModel.setBirthday(new Date());
        detailModel.setTelephoneNo("15959208182");
        detailModel.setEmail("584264571@qq.com");
        detailModel.setResidenceType("temp");

        GeographyModel geographyModel = new GeographyModel();
        geographyModel.setProvince("福建省");
        geographyModel.setCity("漳州市");
        geographyModel.setDistrict("长泰县");
        geographyModel.setStreet("岩溪镇");
        geographyModel.setExtra("上蔡村35号");

        detailModel.setHomeAddressInfo(geographyModel);

        String dataJson = objectMapper.writeValueAsString(detailModel);
        Envelop envelop = patientController.createPatient(dataJson);
        assertTrue("新增失败", envelop.isSuccessFlg());

        detailModel = (PatientDetailModel)envelop.getObj();
        boolean boolResult = patientController.resetPass(detailModel.getIdCardNo());
        assertTrue("密码初始化失败", boolResult);

        envelop = patientController.getPatient(detailModel.getIdCardNo());
        assertTrue("数据获取失败", envelop.isSuccessFlg() && envelop.getObj()!=null);

        detailModel = (PatientDetailModel)envelop.getObj();
        detailModel.setName("test_cms_1");
        geographyModel.setExtra("上蔡村大学35号");
        detailModel.setHomeAddressInfo(geographyModel);
        envelop = patientController.updatePatient(objectMapper.writeValueAsString(detailModel));
        assertTrue("修改失败",envelop.isSuccessFlg() && envelop.getObj()!=null && ((PatientDetailModel) envelop.getObj()).getName().equals("test_cms_1"));

        envelop = cardController.searchCardUnBinding("","MediCard",1,15);
        assertTrue("卡信息获取失败",envelop.isSuccessFlg());
        List<CardModel> cardModels = envelop.getDetailModelList()==null?null: (List<CardModel>)envelop.getDetailModelList();

        CardModel cardModel = null;
        if(cardModels!=null && cardModels.size()>0)
        {
            cardModel = cardModels.get(0);
        }
        boolResult = cardController.attachCard(cardModel.getId(),detailModel.getIdCardNo(),cardModel.getCardType());
        assertTrue("卡关联失败",boolResult);

        envelop = cardController.searchCardBinding(detailModel.getIdCardNo(), "", "MediCard", 1, 15);
        assertTrue("已关联卡信息获取失败",envelop.isSuccessFlg()&&envelop.getDetailModelList().size()>0);

        boolResult = cardController.detachCard(cardModel.getId(),cardModel.getCardType());
        assertTrue("解绑失败",boolResult);

        envelop = patientController.searchPatient(detailModel.getName(),"","","","",15,1);
        assertTrue("病人列表获取失败",envelop.isSuccessFlg() && envelop.getDetailModelList().size()>0);

        envelop = patientController.deletePatient(detailModel.getIdCardNo());
        assertTrue("病人信息删除失败",envelop.isSuccessFlg());
    }
}
