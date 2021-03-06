/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alberta.login;

import com.alberta.model.Company;
import com.alberta.model.Encryption;
import com.alberta.model.User;
import com.alberta.service.ServiceFactory;
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
 * @author Faraz
 */
public class LoginController extends MultiActionController {

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

    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) {
        Map map = new HashMap();
        User user = (User) request.getSession().getAttribute("user");
        if (user != null && request.getSession().getAttribute("parentList") != null && request.getSession().getAttribute("childList") != null) {
            return new ModelAndView("home", "refData", map);
        } else {
            return new ModelAndView("index", "refData", map);
        }
    }

    public ModelAndView processLogin(HttpServletRequest request, HttpServletResponse response) {
        Map map = new HashMap();
        String userName = request.getParameter("username");
        String password = request.getParameter("password");
        String ipAddress = "";
        ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        Encryption pswdSec = new Encryption();
        if (userName != null && userName.length() > 0 && password != null && password.length() > 0) {
            password = pswdSec.encrypt(password);
            User user = this.serviceFactory.getLoginService().verifyLogin(userName, password);
            if (user != null) {
                String sessionId = this.serviceFactory.getLoginService().insertUserSession(userName, ipAddress, "LogIn", "");
                request.getSession().setAttribute("userName", user.getUsername());
                request.getSession().setAttribute("moduleId", "1");
                user.setSessionId(sessionId);
                user.setUserName(user.getUsername());
                request.getSession().setAttribute("user", user);
                Company comp = this.serviceFactory.getSetupService().getCompanyById("1");
                request.getSession().setAttribute("company", comp);
                request.getSession().setMaxInactiveInterval(-1);
                String returnPage = "";
                if (user.getDoctorId() != null && !user.getDoctorId().isEmpty()) {
                    request.getSession().setAttribute("userType", "DOCTOR");
                    List<Map> clinics = this.serviceFactory.getSetupService().getClinicForDoctors(user.getDoctorId());
                    if (clinics != null && clinics.size() == 1) {
                        Map clin = clinics.get(0);
                        String clinicId = clin.get("TW_CLINIC_ID").toString();
                        request.getSession().setAttribute("selectedClinic", this.serviceFactory.getSetupService().getClinicById(clinicId));
                        returnPage = "home";
                    } else {
                        map.put("clinics", clinics);
                        returnPage = "clinic/selectClinic";
                    }
                    request.getSession().setAttribute("parentMenu", this.serviceFactory.getUmsService().getParentRightsForNonAdmin(userName));
                    request.getSession().setAttribute("childMenu", this.serviceFactory.getUmsService().getRightsForNonAdminUsers(userName));
                } else if (user.getPatientId() != null && !user.getPatientId().isEmpty()) {
                    request.getSession().setAttribute("userType", "PATIENT");
                    returnPage = "home";
                } else if (user.getPharmaId() != null && !user.getPharmaId().isEmpty()) {
                    request.getSession().setAttribute("userType", "PHARMA");
                    returnPage = "home";
                    request.getSession().setAttribute("parentMenu", this.serviceFactory.getUmsService().getParentRightsForNonAdmin(userName));
                    request.getSession().setAttribute("childMenu", this.serviceFactory.getUmsService().getRightsForNonAdminUsers(userName));
                } else if (user.getMedicalPharmacyId() != null && !user.getMedicalPharmacyId().isEmpty()) {
                    request.getSession().setAttribute("userType", "MEDICAL_STORE");
                    returnPage = "home";
                    request.getSession().setAttribute("parentMenu", this.serviceFactory.getUmsService().getParentRightsForNonAdmin(userName));
                    request.getSession().setAttribute("childMenu", this.serviceFactory.getUmsService().getRightsForNonAdminUsers(userName));
                } else if (user.getLabMasterId() != null && !user.getLabMasterId().isEmpty()) {
                    request.getSession().setAttribute("userType", "LAB");
                    returnPage = "home";
                    request.getSession().setAttribute("parentMenu", this.serviceFactory.getUmsService().getParentRightsForNonAdmin(userName));
                    request.getSession().setAttribute("childMenu", this.serviceFactory.getUmsService().getRightsForNonAdminUsers(userName));
                } else if (user.getClinicId() != null && !user.getClinicId().isEmpty()) {
                    request.getSession().setAttribute("userType", "CLINIC");
                    returnPage = "home";
                    request.getSession().setAttribute("parentMenu", this.serviceFactory.getUmsService().getParentRightsForNonAdmin(userName));
                    request.getSession().setAttribute("childMenu", this.serviceFactory.getUmsService().getRightsForNonAdminUsers(userName));
                } else {
                    request.getSession().setAttribute("userType", "ADMIN");
                    returnPage = "home";
                    request.getSession().setAttribute("parentMenu", this.serviceFactory.getUmsService().getParentRightsForNonAdmin(userName));
                    request.getSession().setAttribute("childMenu", this.serviceFactory.getUmsService().getRightsForNonAdminUsers(userName));
//                    request.getSession().setAttribute("parentMenu", this.serviceFactory.getUmsService().getParentRightsForAdmin());
//                    request.getSession().setAttribute("childMenu", this.serviceFactory.getUmsService().getRightsForAdmin());
                }

                return new ModelAndView(returnPage, "refData", map);
            } else {
                map.put("msg", "invalid");
                return new ModelAndView("index", "refData", map);
            }
        } else {
            map.put("msg", "empty");
            return new ModelAndView("index", "refData", map);
        }
    }

    public ModelAndView selectClinic(HttpServletRequest request, HttpServletResponse response) {
        Map map = new HashMap();
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            String id = request.getParameter("clinicId");
            if (id != null && !id.isEmpty()) {
                request.getSession().setAttribute("selectedClinic", this.serviceFactory.getSetupService().getClinicById(id));
                return new ModelAndView("home", "refData", map);
            } else {
                Company com = (Company) request.getSession().getAttribute("company");
                map.put("clinics", this.serviceFactory.getSetupService().getClinicForDoctors(user.getDoctorId()));
                return new ModelAndView("clinic/selectClinic", "refData", map);
            }
        } else {
            return new ModelAndView("index", "refData", map);
        }
    }

    public ModelAndView processSignOut(HttpServletRequest request, HttpServletResponse response) {
        Map map = new HashMap();
        String ipAddress = "";
        ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        User user = (User) request.getSession().getAttribute("user");
        String userName = user.getUsername();
        this.serviceFactory.getLoginService().insertUserSession(userName, ipAddress, "LogOut", user.getSessionId());
        request.getSession().setAttribute("parentList", null);
        request.getSession().setAttribute("childList", null);
        request.getSession().setAttribute("user", null);
        request.getSession().setAttribute("moduleId", null);
        request.getSession().setAttribute("selectedClinic", null);
        request.getSession().setAttribute("userType", null);
        request.getSession().invalidate();
        map.put("msg", "logged Out");
        return new ModelAndView("index", "refData", map);
    }

    public ModelAndView accessDenied(HttpServletRequest request, HttpServletResponse response) {
        Map map = new HashMap();
        map.put("msg", "session");
        return new ModelAndView("index", "refData", map);
    }

    public ModelAndView changePassword(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("ums/changePassword");
    }

    public void processChangePassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        String oldPassword = request.getParameter("oldPassword");
//        Encryption pswdSec = new Encryption();
//        password = pswdSec.encrypt(password);
//        oldPassword = pswdSec.encrypt(oldPassword);
        boolean flag = this.serviceFactory.getLoginService().changePassword(userName, oldPassword, password);
        JSONObject obj = new JSONObject();
        if (flag) {
            obj.put("msg", "saved");
        } else {
            obj.put("msg", "error");
        }
        response.getWriter().write(obj.toString());
    }

//    public ModelAndView processChangePassword(HttpServletRequest request, HttpServletResponse response) {
//        Map map = new HashMap();
//        boolean flag = true;
//        Encryption pswdSec = new Encryption();
//        String userName = request.getParameter("userName");
//        String oldPassword = request.getParameter("oldPassword");
//        String newPassword = request.getParameter("newPassword");
//        String retypePassword = request.getParameter("retypePassword");
//        if (userName != null && oldPassword != null && newPassword != null && retypePassword != null) {
//            newPassword = pswdSec.encrypt(newPassword);
//            retypePassword = pswdSec.encrypt(retypePassword);
//            if (!newPassword.equals(retypePassword)) {
//                flag = false;
//                map.put("msg", "");
//            }
//        } else {
//            flag = false;
//            map.put("msg", "");
//        }
//
//        if (flag) {
//            oldPassword = pswdSec.encrypt(oldPassword);
//            if (this.serviceFactory.getLoginService().changePassword(userName, oldPassword, newPassword)) {
//                map.put("msg", "<div class='ui-state-highlight ui-corner-all'><span class='ui-icon ui-icon-info' style='float: left; margin-right: .3em;'></span>Password changed Successfully.</div>");
//            } else {
//                map.put("msg", "<div class='ui-state-error ui-corner-all'><span class='ui-icon ui-icon-alert' style='float: left; margin-right: .3em;'></span>Error in Changing Password.</div>");
//            }
//        }
//        return new ModelAndView("ums/changePassword", "refData", map);
//    }
    public ModelAndView expireSession(HttpServletRequest request, HttpServletResponse response) {
        Map map = new HashMap();
        map.put("msg", "session");
        return new ModelAndView("index", "refData", map);
    }

    public ModelAndView viewDashBoard(HttpServletRequest request, HttpServletResponse response) {
        //Map map = new HashMap();
        Company com = (Company) request.getSession().getAttribute("company");
        User user = (User) request.getSession().getAttribute("user");
        String userName = "";
        if (user != null) {
            userName = user.getUsername();
        }
        String moduleId = request.getSession().getAttribute("moduleId") != null ? (String) request.getSession().getAttribute("moduleId") : "";

        Map map = new HashMap();
        map.put("rightName", "Dashboard");
        return new ModelAndView("home", "refData", map);
    }

    public void getDoctorDashBoard(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        Company com = (Company) request.getSession().getAttribute("company");
        String doctorId = user.getDoctorId();
        Map clinic = (Map) request.getSession().getAttribute("selectedClinic");
        String clinicId = clinic.get("TW_CLINIC_ID").toString();
        List<Map> list = this.serviceFactory.getLoginService().getDashBoardDataForDoctors(doctorId, clinicId);
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

    public void getDashBoardDataForPatient(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        Company com = (Company) request.getSession().getAttribute("company");
        String patientId = user.getPatientId();
        List<Map> list = this.serviceFactory.getLoginService().getDashBoardDataForPatient(patientId);
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

    public void getCollectedFeeForDoctorsByMonth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        Company com = (Company) request.getSession().getAttribute("company");
        String doctorId = user.getDoctorId();
        Map clinic = (Map) request.getSession().getAttribute("selectedClinic");
        String clinicId = clinic.get("TW_CLINIC_ID").toString();
        List<Map> list = this.serviceFactory.getLoginService().getCollectedFeeForDoctorsByMonth(doctorId, clinicId);
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

    public ModelAndView resetPassword(HttpServletRequest request, HttpServletResponse response) {
        Map map = new HashMap();
        return new ModelAndView("resetPassword", "refData", map);
    }

    public ModelAndView processResetPassword(HttpServletRequest request, HttpServletResponse response) {
        Map map = new HashMap();
        String mobileNo = request.getParameter("mobileNo");
        String email = request.getParameter("email");
        String userType = request.getParameter("userType");
        boolean flag = this.serviceFactory.getLoginService().resetPassword(mobileNo, email, userType);
        if (flag) {
            map.put("msg", "password_reset");
        } else {
            map.put("result", "error_reset");
        }
        return new ModelAndView("index", "refData", map);
    }
}
