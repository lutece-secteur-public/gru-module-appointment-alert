/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
package fr.paris.lutece.plugins.genericalert.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.appointment.service.entrytype.EntryTypePhone;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentFormDTO;
import fr.paris.lutece.plugins.genericalert.business.ReminderAppointment;
import fr.paris.lutece.plugins.genericalert.business.TaskNotifyReminderConfig;
import fr.paris.lutece.plugins.genericalert.business.TaskNotifyReminderConfigHome;
import fr.paris.lutece.plugins.genericalert.service.TaskNotifyReminderConfigService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 *
 * NotificationTaskComponent
 *
 */
public class NotifyReminderTaskComponent extends NoFormTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_NOTIFY_REMINDER_CONFIG = "admin/plugins/workflow/modules/notifyreminder/task_notifyreminder_config.html";
    // Parameters
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_NB_ALERT = "nbAlerts";
    private static final String PARAMETER_ID_TASK = "id_task";
    private static final String PARAMETER_APPLY_NB_ALERTS = "apply_nbAlerts";
    private static final String PARAMETER_APPLY_ID_FORM = "apply_idForm";
    // MARKS
    private static final String MARK_CONFIG = "config";
    private static final String MARK_WEBAPP_URL = "webapp_url";

    private static final String MARK_LOCALE = "language";
    private static final String MARK_LOCALE_TINY = "locale";
    private static final String MARK_FALSE = "false";
    private static final String MARK_LIST_FORM = "listForms";
    private static final String MARK_LIST_PHONE_NUMBERS = "listTel";
    private static final String MARK_LIST_STATUS_WORKFLOW = "listStates";
    private static final String MARK_TIME_ALERT = "timeToAlert_";
    private static final String MARK_EMAIL_NOTIFY = "emailNotify_";
    private static final String MARK_SMS_NOTIFY = "smsNotify_";
    private static final String MARK_EMAIL_ALERT_MESSAGE = "email_textMessage_";
    private static final String MARK_SMS_ALERT_MESSAGE = "sms_textMessage_";
    private static final String MARK_ALERT_SUBJECT = "alert_subject_";
    private static final String MARK_NUMBER_PHONE = "tel_";
    private static final String MARK_EMAIL_CC = "emailCc_";
    private static final String MARK_STATUS_WORKFLOW = "state_";
    private static final String MARK_SMS_TEXT_LENGTH = "sms_maxlength";
    // JSP
    private static final String JSP_MODIFY_TASK = "jsp/admin/plugins/workflow/ModifyTask.jsp";
    // Errors
    private static final String MESSAGE_ERROR_SUBJECT_EMPTY = "genericalert.message.error.subjectIsEmpty";
    private static final String MESSAGE_ERROR_STATUS_EMPTY = "genericalert.message.error.statusIsEmpty";
    private static final String MESSAGE_ERROR_ALERT_TIME_NO_VALID = "genericalert.message.error.alerttimeNoValid";
    private static final String MESSAGE_ERROR_NB_ALERT_NO_VALID = "genericalert.message.error.nbAlertsNoValid";
    private static final String MESSAGE_ERROR_NOTIFY_TYPE_EMPTY = "genericalert.message.error.notifyTypeEmpty";
    private static final String MESSAGE_ERROR_EMAIL_TEXT_EMPTY = "genericalert.message.error.notifyEmailTextEmpty";
    private static final String MESSAGE_ERROR_SMS_TEXT_EMPTY = "genericalert.message.error.notifySmsTextEmpty";
    private static final String MESSAGE_ERROR_NUMBER_PHONE_EMPTY = "genericalert.message.error.numberPhoneEmpty";

    // properties
    private static final String PROPERTY_MAX_LENGTH_SMS_TEXT = "genericalert.maxLength.textSms";
    // private IStateService _stateService = SpringContextService.getBean(
    // StateService.BEAN_SERVICE );

    @Inject
    @Named( StateService.BEAN_SERVICE )
    private IStateService _stateService;

    @Inject
    @Named( TaskNotifyReminderConfigService.BEAN_SERVICE )
    private ITaskConfigService _taskNotifyReminderConfigService;

    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {

        String strIdForm = request.getParameter( PARAMETER_ID_FORM ) == null ? StringUtils.EMPTY : request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = 0;
        TaskNotifyReminderConfig config = null;
        if ( StringUtils.isNotEmpty( strIdForm ) && StringUtils.isNumeric( strIdForm ) )
        {
            nIdForm = Integer.parseInt( strIdForm );
        }
        if ( StringUtils.isNotEmpty( strIdForm ) )
        {
            config = TaskNotifyReminderConfigHome.findByIdForm( task.getId( ), nIdForm );
        }

        List<AppointmentFormDTO> listForms = FormService.buildAllActiveAppointmentForm( );
        List<String> listTel = getListPhoneEntries( nIdForm );
        List<State> listStates = null;

        AppointmentFormDTO tmpForm = FormService.buildAppointmentFormLight( nIdForm );
        if ( tmpForm != null )
        {
            StateFilter stateFilter = new StateFilter( );
            stateFilter.setIdWorkflow( tmpForm.getIdWorkflow( ) );
            listStates = _stateService.getListStateByFilter( stateFilter );
        }
        // initialiser la configuration
        if ( config == null )
        {
            config = new TaskNotifyReminderConfig( );
            config.setIdTask( task.getId( ) );
            config.setListReminderAppointment( new ArrayList<ReminderAppointment>( ) );
        }

        String strMaxLenght = DatastoreService.getDataValue( PROPERTY_MAX_LENGTH_SMS_TEXT, StringUtils.EMPTY );

        if ( !DatastoreService.existsKey( PROPERTY_MAX_LENGTH_SMS_TEXT ) )
        {
            DatastoreService.setDataValue( PROPERTY_MAX_LENGTH_SMS_TEXT, AppPropertiesService.getProperty( PROPERTY_MAX_LENGTH_SMS_TEXT ) );
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_CONFIG, config );
        model.put( MARK_LIST_FORM, listForms );
        model.put( MARK_LIST_PHONE_NUMBERS, listTel );
        model.put( MARK_LIST_STATUS_WORKFLOW, listStates );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, locale );
        model.put( MARK_LOCALE_TINY, locale );
        model.put( MARK_SMS_TEXT_LENGTH, strMaxLenght );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_NOTIFY_REMINDER_CONFIG, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        String strNbAlerts = request.getParameter( PARAMETER_NB_ALERT );
        String strApplyNbAlerts = request.getParameter( PARAMETER_APPLY_NB_ALERTS );
        String strForm = request.getParameter( PARAMETER_APPLY_ID_FORM );

        List<ReminderAppointment> listAppointment = new ArrayList<ReminderAppointment>( );

        Boolean bCreate = false;

        int nIdForm = Integer.parseInt( strIdForm );
        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MODIFY_TASK );
        url.addParameter( PARAMETER_ID_TASK, task.getId( ) );
        url.addParameter( PARAMETER_ID_FORM, nIdForm );
        int nbAlerts = 0;

        if ( strNbAlerts != null )
        {
            if ( StringUtils.isNotEmpty( strNbAlerts ) && StringUtils.isNumeric( strNbAlerts ) )
            {
                nbAlerts = Integer.parseInt( strNbAlerts );
            }
            else
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_NB_ALERT_NO_VALID, AdminMessage.TYPE_STOP );
            }
        }

        TaskNotifyReminderConfig config = TaskNotifyReminderConfigHome.findByIdForm( task.getId( ), nIdForm );

        if ( config == null )
        {
            config = new TaskNotifyReminderConfig( );
            config.setIdTask( task.getId( ) );
            config.setIdForm( nIdForm );
            config.setListReminderAppointment( new ArrayList<ReminderAppointment>( ) );
            bCreate = true;
        }
        else
        {

            List<ReminderAppointment> listReminder = config.getListReminderAppointment( );

            if ( nbAlerts == 0 && strForm == null )
            {
                TaskNotifyReminderConfigHome.removeAppointmentReminder( config.getIdTask( ), nIdForm, nbAlerts, true );
                listReminder.clear( );
                config.setListReminderAppointment( listReminder );
            }

            if ( nbAlerts < listReminder.size( ) && nbAlerts != 0 && strForm == null )
            {
                for ( int i = nbAlerts + 1; i <= listReminder.size( ); i++ )
                {

                    TaskNotifyReminderConfigHome.removeAppointmentReminder( config.getIdTask( ), nIdForm, i, false );
                    listReminder.remove( i - 1 );
                }
                config.setListReminderAppointment( listReminder );
            }
            if ( nbAlerts != 0 && strApplyNbAlerts == null && strForm == null )
            {
                for ( int i = 1; i <= nbAlerts; i++ )
                {
                    ReminderAppointment reminderAppointment = new ReminderAppointment( );
                    String strTimeToAlert = request.getParameter( MARK_TIME_ALERT + i );
                    String strEmailNotify = request.getParameter( MARK_EMAIL_NOTIFY + i ) == null ? MARK_FALSE : request.getParameter( MARK_EMAIL_NOTIFY + i );
                    String strSmsNotify = request.getParameter( MARK_SMS_NOTIFY + i ) == null ? MARK_FALSE : request.getParameter( MARK_SMS_NOTIFY + i );
                    String strEmailAlertMessage = request.getParameter( MARK_EMAIL_ALERT_MESSAGE + i ) == null ? StringUtils.EMPTY : request
                            .getParameter( MARK_EMAIL_ALERT_MESSAGE + i );
                    String strSmsAlertMessage = request.getParameter( MARK_SMS_ALERT_MESSAGE + i ) == null ? StringUtils.EMPTY : request
                            .getParameter( MARK_SMS_ALERT_MESSAGE + i );
                    String strAlertSubject = request.getParameter( MARK_ALERT_SUBJECT + i ) == null ? StringUtils.EMPTY : request
                            .getParameter( MARK_ALERT_SUBJECT + i );
                    String strPhoneNumber = request.getParameter( MARK_NUMBER_PHONE + i ) == null ? StringUtils.EMPTY : request.getParameter( MARK_NUMBER_PHONE
                            + i );
                    String strEmailCc = request.getParameter( MARK_EMAIL_CC + i ) == null ? StringUtils.EMPTY : request.getParameter( MARK_EMAIL_CC + i );
                    String strIdSateAfter = request.getParameter( MARK_STATUS_WORKFLOW + i ) == null ? StringUtils.EMPTY : request
                            .getParameter( MARK_STATUS_WORKFLOW + i );

                    if ( StringUtils.isEmpty( strTimeToAlert ) || !StringUtils.isNumeric( strTimeToAlert ) )
                    {
                        if ( strApplyNbAlerts == null && strForm == null )
                        {
                            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_ALERT_TIME_NO_VALID, AdminMessage.TYPE_STOP );
                        }
                    }
                    if ( strSmsNotify.equals( MARK_FALSE ) && strEmailNotify.equals( MARK_FALSE ) )
                    {
                        if ( strApplyNbAlerts == null && strForm == null )
                        {
                            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_NOTIFY_TYPE_EMPTY, AdminMessage.TYPE_STOP );
                        }
                    }
                    if ( !strSmsNotify.equals( MARK_FALSE ) && StringUtils.isEmpty( strPhoneNumber ) )
                    {
                        if ( strApplyNbAlerts == null && strForm == null )
                        {
                            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_NUMBER_PHONE_EMPTY, AdminMessage.TYPE_STOP );
                        }
                    }
                    if ( StringUtils.isEmpty( strIdSateAfter ) )
                    {
                        if ( strApplyNbAlerts == null && strForm == null )
                        {
                            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_STATUS_EMPTY, AdminMessage.TYPE_STOP );
                        }
                    }

                    if ( StringUtils.isEmpty( strAlertSubject ) )
                    {
                        if ( strApplyNbAlerts == null && strForm == null )
                        {
                            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_SUBJECT_EMPTY, AdminMessage.TYPE_STOP );
                        }
                    }

                    if ( StringUtils.isEmpty( strEmailAlertMessage ) && Boolean.parseBoolean( strEmailNotify ) )
                    {
                        if ( strApplyNbAlerts == null && strForm == null )
                        {
                            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_EMAIL_TEXT_EMPTY, AdminMessage.TYPE_STOP );
                        }
                    }
                    if ( StringUtils.isEmpty( strSmsAlertMessage ) && Boolean.parseBoolean( strSmsNotify ) )
                    {
                        if ( strApplyNbAlerts == null && strForm == null )
                        {
                            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_SMS_TEXT_EMPTY, AdminMessage.TYPE_STOP );
                        }
                    }

                    else
                    {
                        reminderAppointment.setIdTask( config.getIdTask( ) );
                        reminderAppointment.setIdForm( nIdForm );
                        reminderAppointment.setRank( i );
                        reminderAppointment.setTimeToAlert( Integer.parseInt( strTimeToAlert ) );
                        reminderAppointment.setEmailNotify( Boolean.parseBoolean( strEmailNotify ) );
                        reminderAppointment.setSmsNotify( Boolean.parseBoolean( strSmsNotify ) );
                        reminderAppointment.setEmailAlertMessage( strEmailAlertMessage );
                        reminderAppointment.setSmsAlertMessage( strSmsAlertMessage );
                        reminderAppointment.setAlertSubject( strAlertSubject );
                        reminderAppointment.setNumberPhone( strPhoneNumber );
                        reminderAppointment.setEmailCc( strEmailCc );
                        reminderAppointment.setIdStateAfter( Integer.parseInt( strIdSateAfter ) );
                        listAppointment.add( reminderAppointment );
                    }
                }
            }

            config.setIdForm( nIdForm );
        }

        if ( strForm == null )
        {
            if ( listAppointment.size( ) > 0 )
            {
                config.setListReminderAppointment( listAppointment );
            }
            config.setNbAlerts( nbAlerts );

            if ( bCreate )
            {
                _taskNotifyReminderConfigService.create( config );
            }
            else
            {
                _taskNotifyReminderConfigService.update( config );
            }
        }

        if ( strApplyNbAlerts != null || strForm != null )
        {
            return AppPathService.getBaseUrl( request ) + JSP_MODIFY_TASK + "?" + PARAMETER_ID_TASK + "=" + task.getId( ) + "&" + PARAMETER_ID_FORM + "="
                    + nIdForm;
        }
        return null;
    }

    /**
     * Get list Phone Entrie
     * 
     * @param idForm
     *            the id form
     * @return list getListPhoneEntries
     */
    private List<String> getListPhoneEntries( int idForm )
    {
        List<String> listPhoneNumber = new ArrayList<String>( );

        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdResource( idForm );
        entryFilter.setResourceType( AppointmentFormDTO.RESOURCE_TYPE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );
        List<Entry> listEntries = EntryHome.getEntryList( entryFilter );
        for ( Entry entry : listEntries )
        {
            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( entry );

            if ( entryTypeService instanceof EntryTypePhone )
            {
                if ( entry.getTitle( ) != null )
                {
                    listPhoneNumber.add( entry.getTitle( ) );
                }
            }
        }
        return listPhoneNumber;
    }

    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

}
