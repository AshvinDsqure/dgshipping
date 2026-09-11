/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.repository;

import org.dspace.app.rest.Parameter;
import org.dspace.app.rest.SearchRestMethod;
import org.dspace.app.rest.converter.EPersonConverter;
import org.dspace.app.rest.converter.EventConverte;
import org.dspace.app.rest.model.EventRest;
import org.dspace.content.Event;
import org.dspace.content.service.EventTrackService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.service.EPersonService;
import org.dspace.eperson.service.RegistrationDataService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

/**
 * This is the repository responsible to manage Item Rest object
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */

@Component(EventRest.CATEGORY + "." + EventRest.NAME)
public class EventRestRepository extends DSpaceObjectRestRepository<Event, EventRest> implements InitializingBean {
    @Autowired
    private EventTrackService EventService;
    @Autowired
    private EPersonService ePersonService;
    @Autowired
    EPersonConverter ePersonConverter;



    @Autowired
    EventConverte eventConverte;

    @Autowired
    private RegistrationDataService registrationDataService;

    public EventRestRepository(EventTrackService EventService) {
        super(EventService);
    }
    @Override
    public EventRest findOne(Context context, UUID uuid) {
        return null;
    }
    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<EventRest> findAll(Context context, Pageable pageable) {
        return  null;
    }
    @Override
    public Class<EventRest> getDomainClass() {
        return null;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
    }
    //@PreAuthorize("hasPermission(#id, 'ITEM', 'ADD')")
    @SearchRestMethod(name = "getCurrentDateEvent")
    public Page<EventRest> getCurrentDateEvent(Pageable pageable,
                                               @Parameter(value = "userID", required = false)UUID userID,
                                               @Parameter(value = "stdate", required = false)String stdateStr,
                                               @Parameter(value = "enddate", required = false)String enddateStr,
                                               @Parameter(value = "action", required = false)Integer action) {
        try {
            Context context = obtainContext();
            Date startDate=null;
            Date endDate=null;
            EPerson ePerson = null;
            if(userID != null){
                ePerson=ePersonService.find(context,userID);
            }
            if (stdateStr != null && enddateStr != null){
                DateTimeFormatter formatter_1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDate local_date_1 = LocalDate.parse(stdateStr, formatter_1);
                LocalDate local_date_2 = LocalDate.parse(enddateStr, formatter_1);
                startDate = java.sql.Date.valueOf(local_date_1);
                endDate = java.sql.Date.valueOf(local_date_2);
                System.out.println(stdateStr);
                System.out.println(enddateStr);

            }
            if(action!=null) {
                action = getAction(action);
            }

            int totalEvents= EventService.countfindAllByCurrentDate(context,pageable.getPageSize(), Math.toIntExact(pageable.getOffset()),startDate,endDate,ePerson,action);
            System.out.println("totol::::::::::"+totalEvents);
            List<Object[]> dspEvents= EventService.findAllByCurrentDate(context,pageable.getPageSize(), Math.toIntExact(pageable.getOffset()),startDate,endDate,ePerson,action);
            System.out.println("totol:::::::size:::"+dspEvents.size());
            List<EventRest> eventRests= dspEvents.stream().filter(d->d!=null).map(d -> {
                EventRest rest=null;
               try {
                   rest =eventConverte.convertbyDTO(d,context,utils.obtainProjection());
               } catch (Exception ex) {
                   ex.printStackTrace();
                    System.out.println("error "+ex.getMessage());
                }
                return rest;
            }).collect(toList());
            return new PageImpl(eventRests, pageable,totalEvents);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Integer getAction(Integer action){

        if(action==0){
            return org.dspace.event.Event.LOGIN;
        }else if(action==1){
            return org.dspace.event.Event.VIEW;
        }else if(action==2){
            return org.dspace.event.Event.SEARCH;
        }else if(action==3){
            return org.dspace.event.Event.DOWNLOAD;
        }else if(action==4){
            return org.dspace.event.Event.CREATE;
        }else if(action==5){
            return org.dspace.event.Event.MODIFY_METADATA;
        }else if(action==6){
            return org.dspace.event.Event.REMOVE;
        }else if(action==7){
            return org.dspace.event.Event.UNLOGIN;
        }else{
            return action;
        }
    }
}
