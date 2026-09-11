/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.dspaceevent;

import org.dspace.app.rest.dspaceevent.constant.DspaceEventAction;
import org.dspace.app.rest.dspaceevent.models.DspaceEventInfo;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

@Component
public class AnalyticsServerImpl {
    @Autowired
    @Qualifier(value="analytics")
    public RestTemplate restTemplate;
    @Autowired
    private ConfigurationService configurationService;
    public String storeEvent(DspaceEventInfo dspaceEventInfo) throws  RuntimeException{
        String responce =null;
        try {
            String baseurl = configurationService.getProperty("dspaceevent.server");
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<DspaceEventInfo> entity = new HttpEntity<DspaceEventInfo>(dspaceEventInfo, headers);
            System.out.println("Event url:::"+baseurl + DspaceEventAction.DSPACEEVNT);
            responce= restTemplate.exchange(baseurl + DspaceEventAction.DSPACEEVNT, HttpMethod.POST, entity, String.class).getBody();
        }catch (Exception e){
            System.out.println("error::"+e.getMessage());
            //e.printStackTrace();
        }
        return  responce;
    }
    public  DspaceEventInfo getDspaceEventInfo(int action, UUID dsapceObjectid,int dspaceObjectType){
        DspaceEventInfo dspaceEventInfo=new DspaceEventInfo();
        dspaceEventInfo.setAction(action);
        dspaceEventInfo.setAction_date(new Date());
        dspaceEventInfo.setDspaceobjectid(dsapceObjectid);
        dspaceEventInfo.setDspaceobjecttype(dspaceObjectType);
        return dspaceEventInfo;
    }
}

