package org.dspace.app.rest.dspaceevent; /**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
///**
// * The contents of this file are subject to the license and copyright
// * detailed in the LICENSE and NOTICE files at the root of the source
// * tree and available online at
// * <p>
// * http://www.dspace.org/license/
// */
//package org.dspace.app.rest.dspaceevent;
//
//import org.dspace.app.rest.dspaceevent.constant.DspaceEventAction;
//import org.dspace.app.rest.dspaceevent.models.DspaceEventInfo;
//import org.dspace.app.rest.dspaceevent.models.SuperSetTokenModel;
//import org.dspace.app.rest.dspaceevent.models.Superset;
//import org.dspace.services.ConfigurationService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.UUID;
//
//@Component
//public class SupersetServerImpl {
//    @Autowired
//    @Qualifier("superset")
//    private RestTemplate restTemplate;
//    @Autowired
//    private ConfigurationService configurationService;
//
//    public String fetchAccessToken(Superset superset) throws RuntimeException {
//        String responce = null;
//        try {
//            String baseurl = configurationService.getProperty("superset.server");
//            String username = configurationService.getProperty("superset.username");
//            String password = configurationService.getProperty("superset.password");
//            superset.setPassword(password);
//            superset.setUsername(username);
//            superset.setProvider("db");
//            superset.setRefresh(true);
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Access-Control-Allow-Origin", "*.*");
//            headers.set("Content-Type", "application/json");
//            headers.set("allow_headers", "*");
//            HttpEntity<Superset> entity = new HttpEntity<Superset>(superset, headers);
//            responce = restTemplate.exchange(baseurl + DspaceEventAction.SUPERSETLOGIN, HttpMethod.POST, entity, String.class).getBody();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return responce;
//    }
//
//    public String fetchGuestToken(SuperSetTokenModel superSetTokenModel) throws RuntimeException {
//        String responce = null;
//        try {
//            String baseurl = configurationService.getProperty("superset.server");
//            String username = configurationService.getProperty("superset.username");
//            String password = configurationService.getProperty("superset.password");
//            HttpHeaders headers = new HttpHeaders();
//            //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//            headers.set("Content-Type", "application/json");
//            headers.set("Authorization", "Bearer "+superSetTokenModel.getAccess_token());
//            GuestTokenRequest guestTokenRequest=new GuestTokenRequest();
//            guestTokenRequest.addresources(new Resources("dashboard",superSetTokenModel.getId()));
//            HttpEntity<GuestTokenRequest> entity = new HttpEntity<GuestTokenRequest>(guestTokenRequest, headers);
//            responce = restTemplate.exchange(baseurl + DspaceEventAction.SUPERSETGUESTTOKEN, HttpMethod.POST, entity, String.class).getBody();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return responce;
//    }
//
//    public DspaceEventInfo getDspaceEventInfo(int action, UUID dsapceObjectid, int dspaceObjectType) {
//        DspaceEventInfo dspaceEventInfo = new DspaceEventInfo();
//        dspaceEventInfo.setAction(action);
//        dspaceEventInfo.setAction_date(new Date());
//        dspaceEventInfo.setDspaceobjectid(dsapceObjectid);
//        dspaceEventInfo.setDspaceobjecttype(dspaceObjectType);
//        return dspaceEventInfo;
//    }
//
//    class GuestTokenRequest {
//        private List<Resources> resources= new ArrayList<>();
//        private List<Rls> rls = new ArrayList<>();
//        private User user = new User("admin","admin","admin");
//
//        public List<Resources> getResources() {
//            return resources;
//        }
//        public void addresources( Resources resourcesobj){
//            resources.add(resourcesobj);
//        }
//        public void setResources(List<Resources> resourceslist) {
//            this.resources = resourceslist;
//        }
//
//        public List<Rls> getRls() {
//            return rls;
//        }
//
//        public void setRls(List<Rls> rls) {
//            this.rls = rls;
//        }
//
//        public User getUser() {
//            return user;
//        }
//
//        public void setUser(User user) {
//            this.user = user;
//        }
//    }
//
//    class Resources {
//        private String type;
//        private String id;
//
//        public Resources(String type, String id) {
//            this.type = type;
//            this.id = id;
//        }
//
//        public String getType() {
//            return type;
//        }
//
//        public void setType(String type) {
//            this.type = type;
//        }
//
//        public String getId() {
//            return id;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }
//    }
//
//    class Rls {
//
//    }
//
//    class User {
//        private String username;
//        private String first_name;
//        private String last_name;
//
//        public User(String username, String first_name, String last_name) {
//            this.username = username;
//            this.first_name = first_name;
//            this.last_name = last_name;
//        }
//
//        public String getUsername() {
//            return username;
//        }
//
//        public void setUsername(String username) {
//            this.username = username;
//        }
//
//        public String getFirst_name() {
//            return first_name;
//        }
//
//        public void setFirst_name(String first_name) {
//            this.first_name = first_name;
//        }
//
//        public String getLast_name() {
//            return last_name;
//        }
//
//        public void setLast_name(String last_name) {
//            this.last_name = last_name;
//        }
//    }
//}
//
