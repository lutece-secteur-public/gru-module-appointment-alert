/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.genericalert.service;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.localization.Localization;
import fr.paris.lutece.plugins.appointment.business.slot.Slot;
import fr.paris.lutece.plugins.appointment.business.user.User;
import fr.paris.lutece.plugins.appointment.service.AppointmentResponseService;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.LocalizationService;
import fr.paris.lutece.plugins.appointment.service.SlotService;
import fr.paris.lutece.plugins.appointment.service.UserService;
import fr.paris.lutece.plugins.appointment.service.entrytype.EntryTypePhone;
import fr.paris.lutece.plugins.appointment.web.AppointmentApp;
import fr.paris.lutece.plugins.genericalert.business.ReminderAppointment;
import fr.paris.lutece.plugins.genericalert.business.TaskNotifyReminderConfig;
import fr.paris.lutece.plugins.genericalert.business.TaskNotifyReminderConfigHome;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.l10n.LocaleService;

/**
 * Task notify appointment
 * 
 * @author Mairie de Paris
 *
 */
public class TaskNotifyReminder extends SimpleTask
{

    // mark
    private static final String MARK_FIRST_NAME = "${firstName}";
    private static final String MARK_LAST_NAME = "${lastName}";
    private static final String MARK_DATE_APP = "${date_appointment}";
    private static final String MARK_TIME_APP = "${time_appointment}";
    private static final String MARK_LOCALIZATION = "${localisation}";
    private static final String MARK_CANCEL_APP = "${url_cancel}";
    private static final String MARK_PREFIX_SENDER = "@contact-everyone.fr";
    private static final String MARK_SENDER_SMS = "magali.lemaire@paris.fr";
    private static final String MARK_REGEX_SMS = "^(06|07)[0-9]{8}$";
    private static final String USER_AUTO = "auto";
    private static final String MARK_DURATION_LIMIT = "daemon.reminder.interval";

    // properties
    private static final String PROPERTY_MAIL_SENDER_NAME = "genericalert.task_notify_reminder.mailSenderName";
    private static final String MESSAGE_MARK_DESCRIPTION = "genericalert.task_notify_reminder.description";

    public static final String FORMAT_DATE = "dd/MM/yyyy";
    public static final String FORMAT_TIME = "HH:mm";
    
    // service
    private final StateService _stateService = SpringContextService.getBean( StateService.BEAN_SERVICE );
    @Inject
    @Named( TaskNotifyReminderConfigService.BEAN_SERVICE )
    private ITaskConfigService _taskNotifyReminderConfigService;

    @Inject
    private IActionService _actionService;

    @Inject
    private ITaskService _taskService;

    @Inject
    private IResourceHistoryService _resourceHistoryService;

    @Inject
    private IResourceWorkflowService _resourceWorkflowService;

    IWorkflowService _workflowService = SpringContextService.getBean( fr.paris.lutece.plugins.workflowcore.service.workflow.WorkflowService.BEAN_SERVICE );

    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        // elle est déclanché juste pour garder une trace dans l'historique du
        // workflow
    }

    public void sendReminder( int nIdResource, String strResourceType, int nIdAction, int nIdWorkflow )
    {
        ITask task = null;
        List<ITask> listActionTasks = _taskService.getListTaskByIdAction( nIdAction, Locale.getDefault( ) );
        for ( ITask tsk : listActionTasks )
        {
            if ( tsk.getTaskType( ) != null && tsk.getTaskType( ).getBeanName( ) != null
                    && tsk.getTaskType( ).getBeanName( ).equals( "genericalert.taskNotifyReminder" ) )
            {
                task = tsk;
            }
        }
        if ( task != null )
        {
            Action action = _actionService.findByPrimaryKey( nIdAction );
            State stateBefore = action.getStateBefore( );
            TaskNotifyReminderConfig config = null;
            Date date = new Date( );
            Calendar calendar = new GregorianCalendar( );
            calendar.setTime( date );
            Timestamp timestampDay = new Timestamp( calendar.getTimeInMillis( ) );
            Appointment appointment = AppointmentService.findAppointmentById( nIdResource );
            Slot slot = null;
            if ( appointment != null )
            {
                slot = SlotService.findSlotById( appointment.getIdSlot( ) );
            }
            if ( slot != null )
            {
                config = TaskNotifyReminderConfigHome.findByIdForm( task.getId( ), slot.getIdForm( ) );
            }
            if ( config != null )
            {
                List<ReminderAppointment> listReminders = null;
                if ( appointment != null && FormService.findFormLightByPrimaryKey( slot.getIdForm( ) ).getIsActive( ) )
                {
                    Timestamp timeStartDate = slot.getStartingTimestampDate( );
                    State stateAppointment = _stateService.findByResource( appointment.getIdAppointment( ), Appointment.APPOINTMENT_RESOURCE_TYPE, nIdWorkflow );
                    if ( timeStartDate.getTime( ) > timestampDay.getTime( ) && stateAppointment != null && stateAppointment.getId( ) == stateBefore.getId( ) )
                    {
                        long minutes = Math.abs( TimeUnit.MILLISECONDS.toMinutes( timestampDay.getTime( ) - timeStartDate.getTime( ) ) );
                        /*
                         * long lDiffTimeStamp = Math.abs( timestampDay.getTime( ) - timeStartDate.getTime( ) ); int nDays = (int) lDiffTimeStamp / ( 1000 * 60
                         * * 60 * 24 ); int nDiffHours = ( (int) lDiffTimeStamp / ( 60 * 60 * 1000 ) % 24 ) + ( nDays * 24 ); int nDiffMin = ( nDiffHours * 60 )
                         * + (int) ( lDiffTimeStamp / ( 60 * 1000 ) % 60 );
                         */
                        if ( config.getNbAlerts( ) > 0 )
                        {
                            listReminders = config.getListReminderAppointment( );
                        }

                        for ( ReminderAppointment reminder : listReminders )
                        {
                            sendReminder( appointment, reminder, timeStartDate, minutes, nIdWorkflow, config, strResourceType, nIdAction );
                        }
                    }
                }
            }
        }
    }

    /**
     * 
     * @param appointment
     *            the appointment
     * @param reminder
     *            the reminder
     * @param startAppointment
     *            startAppointment date of start appointment
     * @param nDiffMin
     *            diffrence time in minutes
     * @param form
     *            the appointment form
     * @param config
     *            the task config
     */
    private void sendReminder( Appointment appointment, ReminderAppointment reminder, Date startAppointment, long nDiffMin, int nIdWorkflow,
            TaskNotifyReminderConfig config, String strResourceType, int nIdAction )
    {
        int nInterval = Integer.parseInt( AppPropertiesService.getProperty( MARK_DURATION_LIMIT ) );

        long nMinTime = ( reminder.getTimeToAlert( ) * 60 * 24 ) - nInterval;
        long nMaxTime = ( reminder.getTimeToAlert( ) * 60 * 24 ) + nInterval;

        if ( nDiffMin <= nMaxTime && nDiffMin >= nMinTime
                && ( ( appointment.getNotification( ) == 0 ) || ( appointment.getNotification( ) != ( reminder.getRank( ) ) ) ) )
        {
            boolean bNotified = false;
            Locale locale = LocaleService.getDefault( );
            String strSenderMail = MailService.getNoReplyEmail( );
            String strSenderName = I18nService.getLocalizedString( PROPERTY_MAIL_SENDER_NAME, locale );
            String strEmailCc = reminder.getEmailCc( );

            String strEmailText = reminder.getEmailAlertMessage( );
            String strSmsText = reminder.getSmsAlertMessage( );

            if ( strEmailText != null && !strEmailText.isEmpty( ) )
            {
                strEmailText = getMessageAppointment( strEmailText, appointment );
            }
            if ( strSmsText != null && !strSmsText.isEmpty( ) )
            {
                strSmsText = getMessageAppointment( strSmsText, appointment );
            }
            User user = UserService.findUserById( appointment.getIdUser( ) );
            if ( reminder.isEmailNotify( ) && !StringUtils.isEmpty( user.getEmail( ) ) )
            {
                try
                {
                    MailService.sendMailHtml( user.getEmail( ), strEmailCc, StringUtils.EMPTY, strSenderName, strSenderMail, reminder.getAlertSubject( ),
                            strEmailText );
                    bNotified = true;
                }
                catch( Exception e )
                {
                    AppLogService.info( "CATCH sending MAIL" );
                    AppLogService.error( "AppointmentReminderDaemon - Error sending reminder alert MAIL to : " + e.getMessage( ), e );
                }
            }
            // AppLogService.info( "SMS : " + reminder.isSmsNotify( ) );
            if ( reminder.isSmsNotify( ) && reminder.getNumberPhone( ) != null )
            {
                String strRecipient = getSmsFromAppointment( appointment, reminder );

                if ( !strRecipient.isEmpty( ) && strRecipient.matches( MARK_REGEX_SMS ) )
                {
                    try
                    {
                        strRecipient += MARK_PREFIX_SENDER;

                        MailService.sendMailHtml( strRecipient, strSenderName, MARK_SENDER_SMS, reminder.getAlertSubject( ), strSmsText );
                        bNotified = true;
                    }
                    catch( Exception e )
                    {
                        AppLogService.info( "CATCH sending reminder alert SMS: " );
                        AppLogService.error( "AppointmentReminderDaemon - Error sending reminder alert SMS to : " + strRecipient + e.getMessage( ), e );
                    }
                }
            }
            if ( bNotified )
            {
                appointment.setNotification( reminder.getRank( ) );
                try
                {
                    AppointmentService.updateAppointment( appointment );
                    doChangeState( config, reminder, nIdWorkflow, appointment );
                }
                catch( Exception e )
                {
                    AppLogService.info( "CATCH CHANGING STATE : " );
                    throw new AppException( e.getMessage( ), e );

                }
            }
        }
    }

    /**
     * Get sms number
     * 
     * @param appointment
     *            the appointment
     * @param reminder
     *            the reminder task
     * @return the sms number
     */
    private String getSmsFromAppointment( Appointment appointment, ReminderAppointment reminder )
    {
        String strPhoneNumber = StringUtils.EMPTY;
        Slot slot = SlotService.findSlotById( appointment.getIdSlot( ) );
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdResource( slot.getIdForm( ) );

        List<Integer> listIdResponse = AppointmentResponseService.findListIdResponse( appointment.getIdAppointment( ) );
        List<Response> listResponses = new ArrayList<Response>( listIdResponse.size( ) );
        for ( int nIdResponse : listIdResponse )
        {
            Response response = ResponseHome.findByPrimaryKey( nIdResponse );
            if ( response != null )
            {
                listResponses.add( response );
            }
        }

        List<Entry> listEntries = EntryHome.getEntryList( entryFilter );
        for ( Entry entry : listEntries )
        {
            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( entry );

            if ( entryTypeService instanceof EntryTypePhone )
            {
                for ( Response response : listResponses )
                {
                    if ( ( response.getEntry( ).getIdEntry( ) == entry.getIdEntry( ) ) && StringUtils.isNotBlank( response.getResponseValue( ) )
                            && entry.getTitle( ).equals( reminder.getNumberPhone( ) ) )
                    {
                        strPhoneNumber = response.getResponseValue( );

                        break;
                    }
                }

                if ( StringUtils.isNotEmpty( strPhoneNumber ) )
                {
                    break;
                }
            }
        }
        return strPhoneNumber;
    }

    /**
     * Get message text
     * 
     * @param msg
     *            the text message
     * @param appointment
     *            the appointment
     * @return the text message
     */
    private String getMessageAppointment( String msg, Appointment appointment )
    {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern( FORMAT_DATE );
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern( FORMAT_TIME );
        String strText = StringUtils.EMPTY;
        User user = UserService.findUserById( appointment.getIdUser( ) );
        Slot slot = SlotService.findSlotById( appointment.getIdSlot( ) );
        Localization localization = LocalizationService.findLocalizationWithFormId( slot.getIdForm( ) );
        String strLocation = StringUtils.EMPTY;
        if ( localization != null && localization.getAddress( ) != null )
        {
            strLocation = localization.getAddress( );
        }
        strText = msg.replace( MARK_FIRST_NAME, user.getFirstName( ) );
        strText = strText.replace( MARK_LAST_NAME, user.getLastName( ) );
        strText = strText.replace( MARK_DATE_APP, slot.getDate( ).format( dateFormatter ) );
        strText = strText.replace( MARK_TIME_APP, slot.getStartingDateTime( ).format( timeFormatter ) );
        strText = strText.replace( MARK_LOCALIZATION, strLocation );
        strText = strText.replace( MARK_CANCEL_APP, AppointmentApp.getCancelAppointmentUrl( appointment ) );

        return strText;
    }

    /**
     * 
     * @param config
     *            the config task
     * @param reminder
     *            the reminder task
     * @param form
     *            the appointment form
     * @param appointment
     *            the appointment
     */
    private void doChangeState( TaskNotifyReminderConfig config, ReminderAppointment reminder, int nIdWorkflow, Appointment appointment )
    {
        Locale locale = I18nService.getDefaultLocale( );
        ITask task = _taskService.findByPrimaryKey( config.getIdTask( ), locale );

        if ( task != null )
        {

            State state = _stateService.findByPrimaryKey( reminder.getIdStateAfter( ) );
            Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );

            if ( ( state != null ) && ( action != null ) )
            {

                // Create Resource History
                ResourceHistory resourceHistory = new ResourceHistory( );
                resourceHistory.setIdResource( appointment.getIdAppointment( ) );
                resourceHistory.setResourceType( Appointment.APPOINTMENT_RESOURCE_TYPE );
                resourceHistory.setAction( action );
                resourceHistory.setWorkFlow( action.getWorkflow( ) );
                resourceHistory.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
                resourceHistory.setUserAccessCode( USER_AUTO );
                _resourceHistoryService.create( resourceHistory );

                // Update Resource
                ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( appointment.getIdAppointment( ),
                        Appointment.APPOINTMENT_RESOURCE_TYPE, nIdWorkflow );

                resourceWorkflow.setState( state );
                _resourceWorkflowService.update( resourceWorkflow );
                // Execute the relative tasks of the state in the workflow
                // We use AutomaticReflexiveActions because we don't want to change the state of the resource by executing actions.
                _workflowService.doProcessAutomaticReflexiveActions( appointment.getIdAppointment( ), Appointment.APPOINTMENT_RESOURCE_TYPE, state.getId( ),
                        null, locale );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveConfig( )
    {
        _taskNotifyReminderConfigService.remove( this.getId( ) );
    }

    @Override
    public String getTitle( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_MARK_DESCRIPTION, locale );
    }

}
