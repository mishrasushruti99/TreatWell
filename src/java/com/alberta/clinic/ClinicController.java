/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alberta.clinic;

import com.alberta.model.Brick;
import com.alberta.model.Company;
import com.alberta.model.DoctorVO;
import com.alberta.model.Product;
import com.alberta.model.User;
import com.alberta.service.ServiceFactory;
import com.alberta.utility.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 *
 * @author farazahmad
 */
public class ClinicController extends MultiActionController {

    private ServiceFactory serviceFactory;

    /**
     * @return the serviceFactory
     */
    public ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    /**
     * @param serviceFactory the serviceFactory to set
     */
    public void setServiceFactory(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    public ModelAndView prescriptionListing(HttpServletRequest request, HttpServletResponse response) {
        Map map = new HashMap();
        map.put("rightName", "Prescription History");
        User user = (User) request.getSession().getAttribute("user");
        String doctorId = user.getDoctorId();
        //map.put("doctors", this.serviceFactory.getSetupService().getDoctors(null, null, null));
        map.put("doctors", this.serviceFactory.getClinicService().getPrescriptionPatientsForDoctor(doctorId));
        return new ModelAndView("clinic/prescriptionListing", "refData", map);
    }

    public void getPrescriptionListing(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fromDate = request.getParameter("dateFrom");
        String toDate = request.getParameter("dateTo");
        String patientId = request.getParameter("patientId");
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userType = request.getSession().getAttribute("userType").toString();
        List<JSONObject> objList = new ArrayList();
        if (userType.equalsIgnoreCase("DOCTOR")) {
            Map clinic = (Map) request.getSession().getAttribute("selectedClinic");
            if (clinic != null) {
                String clinicId = clinic.get("TW_CLINIC_ID").toString();
                String doctorId = user.getDoctorId();
                List<Map> list = this.serviceFactory.getClinicService().getPrescriptionListing(fromDate, toDate, doctorId, clinicId, patientId);
                JSONObject obj = null;
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        Map map = (Map) list.get(i);
                        obj = new JSONObject();
                        Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                        while (itr.hasNext()) {
                            String key = itr.next().getKey();
                            obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                        }
                        objList.add(obj);
                    }
                }
            }
        }

        response.getWriter().write(objList.toString());
    }

    public ModelAndView addDoctorProfile(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = null;
        map = this.serviceFactory.getUmsService().getUserRights(userName, "Doctor Profile");
        map.put("rightName", "Doctor Profile");
//        map.put("patients", this.serviceFactory.getSetupService().getPatient(null, null,null,null));
        String userType = request.getSession().getAttribute("userType").toString();
        if (userType.equalsIgnoreCase("ADMIN")) {
            map.put("clinics", this.serviceFactory.getSetupService().getClinic("", "Y"));
        } else if (userType.equalsIgnoreCase("DOCTOR")) {
            map.put("doctorId", user.getDoctorId());
        }
        map.put("userType", userType);

        //map.put("doctors", this.serviceFactory.getSetupService().getDoctors(null, null, null));
        map.put("categories", this.serviceFactory.getSetupService().getDoctorCagetories(""));
        //map.put("lastDegree", this.serviceFactory.getSetupService().getDoctorDegrees(""));
        map.put("degree", this.serviceFactory.getSetupService().getDoctorDegrees(""));
        map.put("hospitals", this.serviceFactory.getClinicService().getHospitals(""));
        map.put("associations", this.serviceFactory.getClinicService().getAssociations(""));
        map.put("medicalColleges", this.serviceFactory.getClinicService().getMedicalColleges(""));
        map.put("countries", this.serviceFactory.getSetupService().getCountry(com.getCompanyId()));
        map.put("types", this.serviceFactory.getSetupService().getDoctorSpeciality(""));
        map.put("diseases", this.serviceFactory.getSetupService().getDiseases(""));
        map.put("services", this.serviceFactory.getSetupService().getMedicalServices(""));
        return new ModelAndView("clinic/addDoctorProfile", "refData", map);

//      User user = (User) request.getSession().getAttribute("user");
//        String userName = "";
//        if (user != null) {
//            userName = user.getUsername();
//        }
//        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Clinics");
//        map.put("countries", this.serviceFactory.getClinicService().getCountries(""));
//        map.put("rightName", "Clinics");
//   
//         return new ModelAndView("clinic/addCity", "refData", map);
    }

    public void saveDoctorEducation(HttpServletRequest request, HttpServletResponse response, DoctorVO vo) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        String companyId = com.getCompanyId();
        String userName = request.getSession().getAttribute("userName") != null ? request.getSession().getAttribute("userName").toString() : "";
        vo.setUserName(userName);
        vo.setCompanyId(companyId);

        String userType = request.getSession().getAttribute("userType").toString();

        boolean flag = this.serviceFactory.getClinicService().saveDoctorEducation(vo);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void deleteDoctorEducation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteDoctorEducation(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void deleteDoctorExperience(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteDoctorExperience(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void deleteDoctorAssociation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteDoctorAssociation(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getDoctorEducation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String doctorId = request.getParameter("doctorId");
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getDoctorEducation(doctorId);

        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void saveDoctorExperience(HttpServletRequest request, HttpServletResponse response, DoctorVO vo) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        String companyId = com.getCompanyId();
        String userName = request.getSession().getAttribute("userName") != null ? request.getSession().getAttribute("userName").toString() : "";
        vo.setUserName(userName);
        vo.setCompanyId(companyId);

        String userType = request.getSession().getAttribute("userType").toString();

        boolean flag = this.serviceFactory.getClinicService().saveDoctorExperience(vo);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getDoctorExperience(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String doctorId = request.getParameter("doctorId");
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getDoctorExperience(doctorId);

        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void getAssociations(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getAssociations(null);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void saveDoctorAssociation(HttpServletRequest request, HttpServletResponse response, DoctorVO vo) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        String companyId = com.getCompanyId();
        String userName = request.getSession().getAttribute("userName") != null ? request.getSession().getAttribute("userName").toString() : "";
        vo.setUserName(userName);
        vo.setCompanyId(companyId);

        String userType = request.getSession().getAttribute("userType").toString();

        boolean flag = this.serviceFactory.getClinicService().saveDoctorAssociation(vo);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getDoctorAssociation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String doctorId = request.getParameter("doctorId");
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getDoctorAssociation(doctorId);

        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public ModelAndView addDiseases(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Diseases");
        map.put("rightName", "Diseases");
        return new ModelAndView("clinic/addDiseases", "refData", map);
    }

    public void saveDisease(HttpServletRequest request, HttpServletResponse response, DoctorVO c) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String companyId = com.getCompanyId();
        c.setCompanyId(companyId);
        c.setUserName(userName);

        boolean flag = this.serviceFactory.getClinicService().saveDisease(c);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getDiseases(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String diseaseNameSearch = request.getParameter("diseaseNameSearch");
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getDiseases(diseaseNameSearch);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void deleteDisease(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteDisease(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getDiseasesById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String diseaseId = request.getParameter("diseaseId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getDiseasesById(diseaseId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public ModelAndView addHospital(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Clinics");
        map.put("countries", this.serviceFactory.getClinicService().getCountries(""));
        map.put("cities", this.serviceFactory.getClinicService().getCities(""));
        map.put("rightName", "Add Hospital");
        return new ModelAndView("clinic/addHospital", "refData", map);
    }

    public void saveHospital(HttpServletRequest request, HttpServletResponse response, DoctorVO c) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String companyId = com.getCompanyId();
        c.setCompanyId(companyId);
        c.setUserName(userName);
        boolean flag = this.serviceFactory.getClinicService().saveHospital(c);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getHospitalById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String hospitalId = request.getParameter("hospitalId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getHospitalById(hospitalId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void getHospitals(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String hospitalName = request.getParameter("hospitalName");
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getHospital(hospitalName);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void deleteHospital(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteHospital(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public ModelAndView addMedicalDegrees(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Degrees");

        map.put("rightName", "Degree");

        return new ModelAndView("clinic/addMedicalDegrees", "refData", map);
    }

    public void saveDegree(HttpServletRequest request, HttpServletResponse response, DoctorVO c) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String companyId = com.getCompanyId();
        c.setCompanyId(companyId);
        c.setUserName(userName);

        boolean flag = this.serviceFactory.getClinicService().saveDegree(c);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getDegrees(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String degreeName = request.getParameter("degreeName");
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getDegrees(degreeName);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void getDegreeById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String degreeId = request.getParameter("degreeId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getDegreeById(degreeId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void deleteDegree(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteDegree(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public ModelAndView addCountry(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Countries");

        map.put("rightName", "Countries");

        return new ModelAndView("clinic/addCountry", "refData", map);
    }

    public void saveCountry(HttpServletRequest request, HttpServletResponse response, DoctorVO c) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String companyId = com.getCompanyId();
        c.setCompanyId(companyId);
        c.setUserName(userName);

        boolean flag = this.serviceFactory.getClinicService().saveCountry(c);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getCountry(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String countryName = request.getParameter("countryName");
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getCountry(countryName);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void getCountryById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String countryId = request.getParameter("countryId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getCountryById(countryId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void deleteCountry(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteCountry(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void saveMedicineType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String medicineTypeId = request.getParameter("medicineTypeId");
        String mdeicineTypeName = request.getParameter("mdeicineTypeName");

        boolean flag = this.serviceFactory.getClinicService().saveMedicineType(medicineTypeId, mdeicineTypeName, userName);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getMedicineType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String mdeicineTypeName = request.getParameter("mdeicineTypeName");
        List<Map> list = this.serviceFactory.getClinicService().getMedicineType(mdeicineTypeName);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void getMedicineTypeById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String medicineTypeId = request.getParameter("medicineTypeId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getMedicineTypeById(medicineTypeId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void deleteMedicineType(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteMedicineType(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public ModelAndView addMedicineType(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Medicine Type");
        map.put("rightName", "Medicine Type");

        return new ModelAndView("clinic/addMedicineType", "refData", map);
    }

    public ModelAndView addCity(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Cities");
        map.put("countries", this.serviceFactory.getClinicService().getCountries(""));
        map.put("province", this.serviceFactory.getClinicService().getProvince());
        map.put("rightName", "Cities");

        return new ModelAndView("clinic/addCity", "refData", map);
    }

    public ModelAndView addState(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "States");
        map.put("countries", this.serviceFactory.getClinicService().getCountries(""));

        return new ModelAndView("clinic/addState", "refData", map);
    }

    public ModelAndView addProcedure(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Procedures");
        map.put("rightName", "Procedures");
        return new ModelAndView("clinic/addProcedure", "refData", map);
    }

    public void deleteCity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteCity(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void deleteState(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteState(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void deleteProcedure(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteProcedure(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getCityById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cityId = request.getParameter("cityId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getCityById(cityId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void getStateById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String stateId = request.getParameter("stateId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getStateById(stateId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void getProcedureById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String procedureId = request.getParameter("procedureId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getProcedureById(procedureId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void getCity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cityName = request.getParameter("cityName");
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getCity(cityName);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void getState(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getState(com.getCompanyId());
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void getProcedure(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getProcedure(com.getCompanyId());
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void saveCity(HttpServletRequest request, HttpServletResponse response, DoctorVO c) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String companyId = com.getCompanyId();
        c.setCompanyId(companyId);
        c.setUserName(userName);

        boolean flag = this.serviceFactory.getClinicService().saveCity(c);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void saveState(HttpServletRequest request, HttpServletResponse response, DoctorVO c) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String companyId = com.getCompanyId();
        c.setCompanyId(companyId);
        c.setUserName(userName);

        boolean flag = this.serviceFactory.getClinicService().saveState(c);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void saveProcedure(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String companyId = com.getCompanyId();
        String procedureId = request.getParameter("procedureId");
        String procedureName = request.getParameter("procedureName");

        boolean flag = this.serviceFactory.getClinicService().saveProcedure(procedureId, procedureName, companyId);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public ModelAndView addMedicalServices(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Medical Services");

        map.put("rightName", "Medical Services");

        return new ModelAndView("clinic/addMedicalServices", "refData", map);
    }

    public void saveMedicalServices(HttpServletRequest request, HttpServletResponse response, DoctorVO c) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String companyId = com.getCompanyId();
        c.setCompanyId(companyId);
        c.setUserName(userName);

        boolean flag = this.serviceFactory.getClinicService().saveMedicalServices(c);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getMedicalServices(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String servicesNameSearch = request.getParameter("servicesNameSearch");
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getMedicalServices(servicesNameSearch);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void getMedicalServiceById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String serviceId = request.getParameter("serviceId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getMedicalServiceById(serviceId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void deleteMedicalService(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteMedicalService(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public ModelAndView addEducationInstitution(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Colleges");
        map.put("countries", this.serviceFactory.getClinicService().getCountries(""));
        map.put("cities", this.serviceFactory.getClinicService().getCities(""));
        map.put("rightName", "Colleges");
        return new ModelAndView("clinic/addEducationInstitution", "refData", map);
    }

    public void saveEducationInstitution(HttpServletRequest request, HttpServletResponse response, DoctorVO c) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String companyId = com.getCompanyId();
        c.setCompanyId(companyId);
        c.setUserName(userName);
        boolean flag = this.serviceFactory.getClinicService().saveEducationInstitution(c);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getEducationInstitutions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String medicalCollegeName = request.getParameter("medicalCollegeName");
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getEducationInstitutions(medicalCollegeName);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void getEducationInstitutionById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String medicalCollegeId = request.getParameter("medicalCollegeId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getEducationInstitutionById(medicalCollegeId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void deleteEducationInstitution(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteEducationInstitution(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public ModelAndView addLabTest(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Lab Tests");
        map.put("testGroups", this.serviceFactory.getSetupService().getTestGroups());
        map.put("rightName", "Lab Tests");
        return new ModelAndView("clinic/addLabTest", "refData", map);
    }
    
    public ModelAndView addLabTestRate(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Lab Test Rate");
        
        String userType = request.getSession().getAttribute("userType").toString();
        if (userType.equalsIgnoreCase("PHARMA")) {
            String pharmaId = user.getMedicalPharmacyId();
            map.put("lab", this.serviceFactory.getPerformaService().getMedicalLab(pharmaId));
        } else {
            map.put("lab", this.serviceFactory.getPerformaService().getMedicalLab());
        }
        map.put("testGroups", this.serviceFactory.getSetupService().getTestGroups());
        map.put("rightName", "Lab Test Rate");
        return new ModelAndView("clinic/addLabTestRate", "refData", map);
    }

    public void saveLabTest(HttpServletRequest request, HttpServletResponse response, DoctorVO c) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String companyId = com.getCompanyId();
        c.setCompanyId(companyId);
        c.setUserName(userName);

        boolean flag = this.serviceFactory.getClinicService().saveLabTest(c);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }
    
    public void saveLabTestRate(HttpServletRequest request, HttpServletResponse response, DoctorVO c) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String companyId = com.getCompanyId();
        c.setCompanyId(companyId);
        c.setUserName(userName);

        boolean flag = this.serviceFactory.getClinicService().saveLabTestRate(c);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getLabTests(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String labTestNameSearch = request.getParameter("labTestNameSearch");
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getLabTests(labTestNameSearch);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }
    
    public void getLabTestRate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getLabTestRate(id);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void getLabtestById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String labTestId = request.getParameter("labTestId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getLabtestById(labTestId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void getDoctorByAreaId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String areaId = request.getParameter("areaId");
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getDoctorByAreaId(areaId);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void getAreasByCityId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String cityId = request.getParameter("cityId");
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getAreaByCityId(cityId);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void deleteLabTest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteLabTest(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public ModelAndView addMedicine(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Medicines");
        map.put("companies", this.serviceFactory.getSetupService().getPharmaCompanies());
        map.put("diseases", this.serviceFactory.getSetupService().getDiseases(""));
        map.put("medicineType", this.serviceFactory.getClinicService().getMedicineType(""));
        map.put("rightName", "Medicines");
        return new ModelAndView("clinic/addMedicine", "refData", map);
    }

    public ModelAndView addMedicineRep(HttpServletRequest request, HttpServletResponse response) {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Medical Rep");
        String userType = request.getSession().getAttribute("userType").toString();
        if (userType.equalsIgnoreCase("PHARMA")) {
            String pharmaId = user.getPharmaId();
            map.put("companies", this.serviceFactory.getSetupService().getPharmaCompanies(pharmaId));
        } else {
            map.put("companies", this.serviceFactory.getSetupService().getPharmaCompanies());
        }

        map.put("rightName", "Medical Rep");
        return new ModelAndView("clinic/addMedicineRep", "refData", map);
    }

    public ModelAndView addMedicalRepAppointment(HttpServletRequest request, HttpServletResponse response) {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String userType = request.getSession().getAttribute("userType").toString();
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Rep Appointment");
        if (userType.equalsIgnoreCase("PHARMA")) {
            String pharmaId = user.getPharmaId();
            map.put("companies", this.serviceFactory.getSetupService().getPharmaCompanies(pharmaId));
        } else {
            map.put("companies", this.serviceFactory.getSetupService().getPharmaCompanies());
        }
        map.put("rightName", "Rep Appointment");
        //addMedicalRepAppointment
        return new ModelAndView("clinic/addMedicalRepAppointment", "refData", map);
    }

    public void saveMedicine(HttpServletRequest request, HttpServletResponse response, Product vo) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String companyId = com.getCompanyId();
        vo.setCompanyId(companyId);
        vo.setUserName(userName);
        vo.setMultiSelectDiseases(request.getParameterValues("selectDiseasesArr[]"));
        boolean flag = this.serviceFactory.getClinicService().saveMedicine(vo);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void saveMedicineAttachments(HttpServletRequest request, HttpServletResponse response, Product vo) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        vo.setUserName(userName);
        String attachmentPath = request.getServletContext().getRealPath("/upload/medicine/");
        boolean flag = this.serviceFactory.getClinicService().saveMedicineAttachments(vo, attachmentPath);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void saveMedicineRep(HttpServletRequest request, HttpServletResponse response, Product vo) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String companyId = com.getCompanyId();
        vo.setCompanyId(companyId);
        vo.setUserName(userName);
        boolean flag = this.serviceFactory.getClinicService().saveMedicineRep(vo);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void saveMedicalRepAppointment(HttpServletRequest request, HttpServletResponse response, Product vo) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String companyId = com.getCompanyId();
        vo.setBrickId(request.getParameterValues("brickIdArr[]"));
        vo.setCompanyId(companyId);
        vo.setUserName(userName);
        boolean flag = this.serviceFactory.getClinicService().saveMedicalRepAppointment(vo);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getMedicines(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pharmaCompanyId = request.getParameter("pharmaCompanyId");
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getMedicines(pharmaCompanyId);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void getMedicinesRep(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pharmaCompanyId = request.getParameter("pharmaCompanyId");
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getMedicinesRep(pharmaCompanyId);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void getMedicineById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String medicineId = request.getParameter("medicineId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getMedicineById(medicineId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void getMedicalRepAppointmentById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pharmaRepId = request.getParameter("pharmaRepId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getMedicalRepAppointmentById(pharmaRepId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void getMedicineRepById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String medicineRepId = request.getParameter("medicineRepId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getMedicineRepById(medicineRepId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void deleteMedicine(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteMedicine(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void deleteMedicineRep(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteMedicineRep(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public ModelAndView addMessages(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = new HashMap();
        map = this.serviceFactory.getUmsService().getUserRights(userName, "SMS Templates");
        map.put("rightName", "SMS Templates");
//        map.put("patients", this.serviceFactory.getSetupService().getPatient(null, null,null,null));
        String userType = request.getSession().getAttribute("userType").toString();
        if (userType.equalsIgnoreCase("ADMIN")) {
            map.put("clinics", this.serviceFactory.getSetupService().getClinic("", "Y"));
        } else if (userType.equalsIgnoreCase("DOCTOR")) {
            map.put("doctorId", user.getDoctorId());
        }
        map.put("userType", userType);
//        map = this.serviceFactory.getUmsService().getUserRights(userName, "SMS Templates");
//        map.put("rightName", "SMS Templates");
        return new ModelAndView("clinic/sendMessage", "refData", map);
    }

    public void saveMessage(HttpServletRequest request, HttpServletResponse response, DoctorVO c) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
            c.setDoctorId(user.getDoctorId());
        }
        String companyId = com.getCompanyId();
        c.setCompanyId(companyId);
        c.setUserName(userName);
        boolean flag = this.serviceFactory.getClinicService().saveMessage(c);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getMessage(HttpServletRequest request, HttpServletResponse response, DoctorVO c) throws IOException {
        String medicineNameSearch = request.getParameter("medicineNameSearch");
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
            c.setDoctorId(user.getDoctorId());
        }
        c.setUserName(userName);
        List<Map> list = this.serviceFactory.getClinicService().getMessage(c);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public ModelAndView addPrintLayout(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = new HashMap();
        map = this.serviceFactory.getUmsService().getUserRights(userName, "Print Layout");
        map.put("rightName", "Print Layout");
//        map.put("patients", this.serviceFactory.getSetupService().getPatient(null, null,null,null));
        String userType = request.getSession().getAttribute("userType").toString();
        if (userType.equalsIgnoreCase("ADMIN")) {
            map.put("clinics", this.serviceFactory.getSetupService().getClinic("", "Y"));
        } else if (userType.equalsIgnoreCase("DOCTOR")) {
            map.put("doctorId", user.getDoctorId());
        }
        map.put("userType", userType);
        map.put("doctorId", user.getDoctorId());
        return new ModelAndView("clinic/setPrintLayout", "refData", map);
    }

    public void savePrintLayout(HttpServletRequest request, HttpServletResponse response, DoctorVO vo) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        String userType = request.getSession().getAttribute("userType").toString();
        String attachmentPath = request.getServletContext().getRealPath("/upload/doctor/latterPad/");
        if (userType.equalsIgnoreCase("DOCTOR")) {
            Map clinic = (Map) request.getSession().getAttribute("selectedClinic");
            if (clinic != null) {
                String clinicId = clinic.get("TW_CLINIC_ID").toString();
                String doctorId = user.getDoctorId();
                vo.setDoctorId(doctorId);
                vo.setClinicId(clinicId);
                boolean flag = this.serviceFactory.getClinicService().savePrintLayout(vo, attachmentPath);
                JSONObject obj = new JSONObject();
                if (flag) {
                    obj.put("result", "save_success");
                } else {
                    obj.put("result", "save_error");
                }
                response.getWriter().write(obj.toString());
            }
        }
    }

    public void getPrintLayouts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String doctorId = request.getParameter("doctorId");
        User user = (User) request.getSession().getAttribute("user");
        String userType = request.getSession().getAttribute("userType").toString();
        Company com = (Company) request.getSession().getAttribute("company");
        if (userType.equalsIgnoreCase("DOCTOR")) {
            Map clinic = (Map) request.getSession().getAttribute("selectedClinic");
            if (clinic != null) {
                String clinicId = clinic.get("TW_CLINIC_ID").toString();
                Map map = this.serviceFactory.getClinicService().getPrintLayouts(user.getDoctorId(), clinicId);
                JSONObject obj = null;
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                response.getWriter().write(obj.toString());
            }
        }
    }

    public void getAreaByCitys(HttpServletRequest request, HttpServletResponse response, Brick b) throws IOException {
        b.setCityId(request.getParameterValues("cityIdArr[]"));
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getAreaByCitys(b);

        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void getPrintLayoutById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String layoutId = request.getParameter("layoutId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getPrintLayoutById(layoutId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void saveDoctorAttachment(HttpServletRequest request, HttpServletResponse response, DoctorVO vo) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        vo.setUserName(userName);
        String attachmentPath = request.getServletContext().getRealPath("/upload/doctor/doctorAttachments/");
        boolean flag = this.serviceFactory.getClinicService().saveDoctorAttachment(vo, attachmentPath);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void saveDoctorProfile(HttpServletRequest request, HttpServletResponse response, DoctorVO vo) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        vo.setUserName(userName);
        String attachmentPath = request.getServletContext().getRealPath("/upload/doctor/profilePic/");
        boolean flag = this.serviceFactory.getClinicService().updateProfileImage(vo, attachmentPath);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void uploadDoctorVistingCard(HttpServletRequest request, HttpServletResponse response, DoctorVO vo) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        vo.setUserName(userName);
        String attachmentPath = request.getServletContext().getRealPath("/upload/doctor/visitingCard/");
        boolean flag = this.serviceFactory.getClinicService().updateVisitingCard(vo, attachmentPath);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getDoctorAppointedPatients(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String doctorId = request.getParameter("doctorId");
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getDoctorAppointedPatients(doctorId);

        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public ModelAndView addAssociation(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Medical Association");
        map.put("rightName", "Medical Association");
        return new ModelAndView("clinic/addAssociation", "refData", map);
    }

    public ModelAndView addBrick(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Add Brick");

        String userType = request.getSession().getAttribute("userType").toString();
        if (userType.equalsIgnoreCase("PHARMA")) {
            String pharmaId = user.getPharmaId();
            map.put("companies", this.serviceFactory.getSetupService().getPharmaCompanies(pharmaId));
        } else {
            map.put("companies", this.serviceFactory.getSetupService().getPharmaCompanies());
        }
        map.put("cities", this.serviceFactory.getClinicService().getCitysOfPakistan());
        map.put("rightName", "Add Brick");
        return new ModelAndView("clinic/addBrick", "refData", map);
    }

    public void getBricks(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pharmaCompanyId = request.getParameter("pharmaCompanyId");
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getBricks(pharmaCompanyId);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void saveBrick(HttpServletRequest request, HttpServletResponse response, Brick b) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        b.setUsername(userName);
        b.setAreaId(request.getParameterValues("areaIdArr[]"));
        boolean flag = this.serviceFactory.getClinicService().saveBrick(b);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getBrickById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String brickId = request.getParameter("brickId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getBrickById(brickId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void deleteBrick(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("brickId");
        boolean flag = this.serviceFactory.getClinicService().deleteBrick(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void saveAssociation(HttpServletRequest request, HttpServletResponse response, DoctorVO c) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String companyId = com.getCompanyId();
        c.setCompanyId(companyId);
        c.setUserName(userName);

        boolean flag = this.serviceFactory.getClinicService().saveMedicalAssociation(c);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getAssociationById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String labTestId = request.getParameter("labTestId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getAssociationById(labTestId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void deleteAssociation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteAssociation(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public ModelAndView addMedicalLab(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Laboratories");
        map.put("rightName", "Laboratories");
        return new ModelAndView("clinic/addMedicalLab", "refData", map);
    }

    public void saveMedicalLab(HttpServletRequest request, HttpServletResponse response, DoctorVO c) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String companyId = com.getCompanyId();
        c.setCompanyId(companyId);
        c.setUserName(userName);

        boolean flag = this.serviceFactory.getClinicService().saveMedicalLab(c);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getMedicalLabs(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        List<Map> list = this.serviceFactory.getClinicService().getMedicalLabs(null);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void deleteMedicalLab(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteMedicalLab(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getMedicalLabById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String diseaseId = request.getParameter("diseaseId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getMedicalLabById(diseaseId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public ModelAndView addPharmaArea(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Area");
        map.put("cityList", this.serviceFactory.getClinicService().getCitysOfPakistan());
        map.put("companies", this.serviceFactory.getSetupService().getPharmaCompanies());
        return new ModelAndView("clinic/addPharmaArea", "refData", map);
    }

    public void getPharmaArea(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        String pharmaCompanyId = request.getParameter("pharmaCompanyId");
        List<Map> list = this.serviceFactory.getClinicService().getPharmaArea(pharmaCompanyId);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void savePharmaArea(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String areaName = request.getParameter("areaName");
        String areaId = request.getParameter("areaId");
        String cityId = request.getParameter("cityId");
        String pharmaCompanyId = request.getParameter("pharmaCompanyId");
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }

        boolean flag = this.serviceFactory.getClinicService().savePharmaArea(areaName, areaId, cityId, pharmaCompanyId);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void deletePharmaArea(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deletePharmaArea(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getPharmaAreaById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String areaId = request.getParameter("areaId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getPharmaAreaById(areaId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public ModelAndView addCityArea(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Area");
        map.put("cityList", this.serviceFactory.getClinicService().getCitysOfPakistan());
        map.put("rightName", "Area");
        return new ModelAndView("clinic/addCityArea", "refData", map);
    }

    public void getCityArea(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        String cityId = request.getParameter("cityId");
        List<Map> list = this.serviceFactory.getClinicService().getCityArea(cityId);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void saveCityArea(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String areaName = request.getParameter("areaName");
        String areaId = request.getParameter("areaId");
        String cityId = request.getParameter("cityId");
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }

        boolean flag = this.serviceFactory.getClinicService().saveCityArea(areaName, areaId, cityId, userName);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void deleteCityArea(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteCityArea(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getCityAreaById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String areaId = request.getParameter("areaId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getCityAreaById(areaId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void getBrickByPharmaceuticalId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pharmaCompanyId = request.getParameter("pharmaCompanyId");
        List<Map> list = this.serviceFactory.getClinicService().getBrickByPharmaceuticalId(pharmaCompanyId);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    // Add Speciality
    public ModelAndView addMedicalSpeciality(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Medical Speciality");
        map.put("rightName", "Medical Speciality");
        return new ModelAndView("clinic/addMedicalSpeciality", "refData", map);
    }

    public void saveMedicalSpeciality(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String specialityId = request.getParameter("specialityId");
        String specialityName = request.getParameter("specialityName");
        String showWebInd = request.getParameter("showWebInd");
        boolean flag = this.serviceFactory.getClinicService().saveMedicalSpeciality(specialityId, specialityName, showWebInd);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getMedicalSpeciality(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String specialityNameSearch = request.getParameter("specialityNameSearch");
        List<Map> list = this.serviceFactory.getClinicService().getMedicalSpeciality(specialityNameSearch);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void getMedicalSpecialityById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String specialityId = request.getParameter("specialityId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getMedicalSpecialityById(specialityId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void deleteMedicalSpeciality(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteMedicalSpeciality(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    // hosptail ward
    public ModelAndView addHospitalWard(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Hospital Ward");
        String userType = request.getSession().getAttribute("userType").toString();
        if (userType.equalsIgnoreCase("CLINIC")) {
            String clinicId = user.getClinicId();
            map.put("clinic", this.serviceFactory.getSetupService().getClinicForStaff(clinicId));
        } else {
            map.put("clinic", this.serviceFactory.getSetupService().getClinic(null, "Y"));
        }
        map.put("rightName", "Hospital Ward");
        return new ModelAndView("clinic/addHospitalWard", "refData", map);
    }

    public void saveHospitalWard(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String wardId = request.getParameter("wardId");
        String clinicId = request.getParameter("clinicId");
        String wardName = request.getParameter("wardName");
        String beds = request.getParameter("beds");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        boolean flag = this.serviceFactory.getClinicService().saveHospitalWard(wardId, clinicId, wardName, beds, userName);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getHospitalWard(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String clinicId = request.getParameter("clinicId");
        List<Map> list = this.serviceFactory.getClinicService().getHospitalWard(clinicId);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void getHospitalWardById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String wardId = request.getParameter("wardId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getHospitalWardById(wardId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void deleteHospitalWard(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteHospitalWard(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    // hosptail room
    public ModelAndView addHospitalRoom(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Hospital Room");
        String userType = request.getSession().getAttribute("userType").toString();
        if (userType.equalsIgnoreCase("CLINIC")) {
            String clinicId = user.getClinicId();
            map.put("clinic", this.serviceFactory.getSetupService().getClinicForStaff(clinicId));
        } else {
            map.put("clinic", this.serviceFactory.getSetupService().getClinic(null, "Y"));
        }
        map.put("rightName", "Hospital Room");
        return new ModelAndView("clinic/addHospitalRoom", "refData", map);
    }

    public void saveHospitalRoom(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String roomId = request.getParameter("roomId");
        String clinicId = request.getParameter("clinicId");
        String roomName = request.getParameter("roomName");
        String beds = request.getParameter("beds");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        boolean flag = this.serviceFactory.getClinicService().saveHospitalRoom(roomId, clinicId, roomName, beds, userName);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getHospitalRoom(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String clinicId = request.getParameter("clinicId");
        List<Map> list = this.serviceFactory.getClinicService().getHospitalRoom(clinicId);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void getHospitalRoomById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String roomId = request.getParameter("roomId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getHospitalRoomById(roomId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void deleteHospitalRoom(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteHospitalRoom(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public ModelAndView addAdmitPatient(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Admit Patient");
        //  map.put("patients", this.serviceFactory.getSetupService().getPatient("", "", "", "", ""));
        String userType = request.getSession().getAttribute("userType").toString();
        if (userType.equalsIgnoreCase("CLINIC")) {
            String clinicId = user.getClinicId();
            map.put("clinic", this.serviceFactory.getSetupService().getClinicForStaff(clinicId));
        } else {
            map.put("clinic", this.serviceFactory.getSetupService().getClinic(null, "Y"));
        }
        map.put("rightName", "Admit Patient");
        return new ModelAndView("clinic/addAdmitPatient", "refData", map);
    }

    public void saveAdmitPatient(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String roomId = request.getParameter("roomId");
        String wardId = request.getParameter("wardId");
        String clinicId = request.getParameter("clinicId");
        String patientId = request.getParameter("patientId");
        String bedNo = request.getParameter("bedNo");
        String hospitalPatientId = request.getParameter("hospitalPatientId");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        boolean flag = this.serviceFactory.getClinicService().saveAdmitPatient(hospitalPatientId, roomId, clinicId, wardId, patientId, bedNo, userName);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getAdmitPatient(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String clinicId = request.getParameter("clinicId");
        String statusInd = request.getParameter("statusInd");
        List<Map> list = this.serviceFactory.getClinicService().getAdmitPatient(clinicId, statusInd);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void getAdmitPatientById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String hospitalPatientId = request.getParameter("hospitalPatientId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getAdmitPatientById(hospitalPatientId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

//    public void deleteAdmitPatient(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String id = request.getParameter("id");
//        boolean flag = this.serviceFactory.getClinicService().deleteAdmitPatient(id);
//        JSONObject obj = new JSONObject();
//        if (flag) {
//            obj.put("result", "save_success");
//        } else {
//            obj.put("result", "save_error");
//        }
//        response.getWriter().write(obj.toString());
//    }
    public void saveDischargeData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String remarks = request.getParameter("remarks");
        String dischargeDate = request.getParameter("dischargeDate");
        String hospitalPatientId = request.getParameter("hospitalPatientId");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        boolean flag = this.serviceFactory.getClinicService().saveDischargeData(hospitalPatientId, dischargeDate, remarks, userName);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    // hospital Employee
    public ModelAndView addHospitalStaff(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Hospital Staff");
        String userType = request.getSession().getAttribute("userType").toString();
        if (userType.equalsIgnoreCase("CLINIC")) {
            String clinicId = user.getClinicId();
            map.put("clinic", this.serviceFactory.getSetupService().getClinicForStaff(clinicId));
        } else {
            map.put("clinic", this.serviceFactory.getSetupService().getClinic(null, "Y"));
        }
        map.put("rightName", "Hospital Staff");
        return new ModelAndView("clinic/addHospitalStaff", "refData", map);
    }

    public void saveHospitalEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String loginId = request.getParameter("loginId");
        String clinicId = request.getParameter("clinicId");
        String contactNo = request.getParameter("contactNo");
        String employeeId = request.getParameter("employeeId");
        boolean flag = this.serviceFactory.getClinicService().saveHospitalEmployee(employeeId, clinicId, fullName, email, loginId, contactNo);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getHospitalEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String clinicId = request.getParameter("clinicId");
        List<Map> list = this.serviceFactory.getClinicService().getHospitalEmployee(clinicId);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }

    public void getHospitalEmployeeById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String employeeId = request.getParameter("employeeId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getHospitalEmployeeById(employeeId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void deleteMessageTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String id = request.getParameter("id");
        boolean flag = this.serviceFactory.getClinicService().deleteMessageTemplate(id);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getSmsTemplateById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String templateId = request.getParameter("templateId");
        Company com = (Company) request.getSession().getAttribute("company");
        Map map = this.serviceFactory.getClinicService().getSmsTemplateById(templateId);
        JSONObject obj = new JSONObject();
        if (map != null) {
            Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
            }
        }
        response.getWriter().write(obj.toString());
    }

    public void sendMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");
        String numbers = request.getParameter("numbers");
        boolean flag = Util.generateMultipleSms(numbers, message);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public ModelAndView intakeDiseases(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        Map map = this.serviceFactory.getUmsService().getUserRights(userName, "Intake Diseases");
        map.put("speciality", this.serviceFactory.getPerformaService().getMedicalSpeciality());
        map.put("diseases", this.serviceFactory.getSetupService().getDiseases(""));
        map.put("rightName", "Intake Diseases");
        return new ModelAndView("clinic/intakeDiseases", "refData", map);
    }

    public void saveIntakeDisease(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String specialityId = request.getParameter("specialityId");
        String diseasesId[] = request.getParameterValues("diseasesId[]");
        boolean flag = this.serviceFactory.getClinicService().saveIntakeDisease(specialityId, diseasesId);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("result", "save_success");
        } else {
            obj.put("result", "save_error");
        }
        response.getWriter().write(obj.toString());
    }

    public void getIntakeDiseases(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String specialityId = request.getParameter("specialityId");
        List<Map> list = this.serviceFactory.getClinicService().getIntakeDiseases(specialityId);
        List<JSONObject> objList = new ArrayList();
        JSONObject obj = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                obj = new JSONObject();
                Iterator<Map.Entry<String, Object>> itr = map.entrySet().iterator();
                while (itr.hasNext()) {
                    String key = itr.next().getKey();
                    obj.put(key, map.get(key) != null ? map.get(key).toString() : "");
                }
                objList.add(obj);
            }
        }
        response.getWriter().write(objList.toString());
    }
}
