package fr.paris.lutece.plugins.genericalert.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.Appointment;
import fr.paris.lutece.plugins.appointment.business.AppointmentForm;
import fr.paris.lutece.plugins.appointment.business.AppointmentFormHome;
import fr.paris.lutece.plugins.appointment.business.AppointmentHome;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlot;
import fr.paris.lutece.plugins.appointment.business.calendar.AppointmentSlotHome;
import fr.paris.lutece.plugins.appointment.service.AppointmentPlugin;
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
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.l10n.LocaleService;
import fr.paris.lutece.util.sql.TransactionManager;

/**
 * Task notify appointment
 * @author Mairie de Paris
 *
 */
public class TaskNotifyReminder extends SimpleTask
{
	
	//mark    
	private static final String MARK_FIRST_NAME = "${firstName}";
	private static final String MARK_LAST_NAME = "${lastName}";
	private static final String MARK_DATE_APP = "${date_appointment}";
	private static final String MARK_TIME_APP = "${time_appointment}";
	private static final String MARK_LOCALIZATION = "${localisation}";
	private static final String MARK_CANCEL_APP = "${url_cancel}" ;
	private static final String MARK_PREFIX_SENDER = "@contact-everyone.fr" ;
    private static final String	MARK_SENDER_SMS = "magali.lemaire@paris.fr" ;
    private static final String	MARK_REGEX_SMS = "^(06|07)[0-9]{8}$" ;
    private static final String USER_AUTO = "auto";
    private static final int 	MARK_DURATION_LIMIT = 5;
    
	//properties
    private static final String PROPERTY_MAIL_SENDER_NAME = "genericalert.task_notify_reminder.mailSenderName";
    private static final String MESSAGE_MARK_DESCRIPTION = "genericalert.task_notify_reminder.description" ;
    
    //service 
    private final StateService _stateService  = SpringContextService.getBean( StateService.BEAN_SERVICE );
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
	
	@Override
	public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
	{
		ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
		
        Action action = _actionService.findByPrimaryKey( resourceHistory.getAction( ).getId( ) );
         
        State stateBefore = action.getStateBefore( ) ;
		
		DateFormat dateFormat = DateFormat.getDateInstance( DateFormat.DEFAULT );
		AppLogService.info( "START DEBUG : \n" );
		Date date = new Date();
        Calendar calendar = new GregorianCalendar(  );
        calendar.setTime( date );
        Timestamp timestampDay = new Timestamp( calendar.getTimeInMillis(  ) );
		
		List<AppointmentForm> listForms =  AppointmentFormHome.getActiveAppointmentFormsList( );
		
		for ( AppointmentForm  form : listForms )
    	{
			int nIdForm = form.getIdForm( ) ;
			
			TaskNotifyReminderConfig config = TaskNotifyReminderConfigHome.findByIdForm( this.getId(  ) , nIdForm );
			
			if ( config != null )
			{
				List <ReminderAppointment> listReminders = null ;
				List< Appointment > listAppointments = getListAppointment( form ) ;
	    	
		        for ( Appointment appointment : listAppointments )
		        {
		        	Calendar cal2 = new GregorianCalendar(  );
		        	Date startAppointment = appointment.getStartAppointment( ) ;
		        	cal2.setTime( startAppointment );
		        	Timestamp timeStartDate = new Timestamp( cal2.getTimeInMillis(  ) );
		        	AppLogService.info( "Current Date   : " + dateFormat.format( date ) );
		        	AppLogService.info( "Date appointment   : " + dateFormat.format( startAppointment ) ); 
		        	
		        	State stateAppointment = _stateService.findByResource( appointment.getIdAppointment( ), Appointment.APPOINTMENT_RESOURCE_TYPE, form.getIdWorkflow( ) );
		        	
		        	if ( timeStartDate.getTime( ) > timestampDay.getTime( ) && stateAppointment.getId( ) == stateBefore.getId( ) )
		        	{
			        	long lDiffTimeStamp = Math.abs ( timestampDay.getTime( ) - timeStartDate.getTime( ) ) ;
			        	int nDays = ( int ) lDiffTimeStamp / ( 1000*60*60*24 ) ;
			        	int nDiffHours = ( ( int ) lDiffTimeStamp /( 60 * 60 * 1000 ) % 24 ) + ( nDays * 24 ) ;
			        	int nDiffMin =  ( nDiffHours * 60 ) + ( int ) ( lDiffTimeStamp / ( 60 * 1000 ) % 60 ) ;
	
						if ( config.getNbAlerts( ) > 0 )
						{
							listReminders = config.getListReminderAppointment( );
						}
		    		
		    			for ( ReminderAppointment reminder : listReminders )
		    			{
		    				sendReminder ( appointment , reminder, startAppointment, nDiffMin, form, config ) ;
		    			}
			        }
		        }
			}
    	}
	}
	/**
	 * Get list appointment
	 * @param form the appointmentForm
	 * @return list appointment
	 */
	private List<Appointment> getListAppointment ( AppointmentForm form )
	{
		List <Appointment> listAllAppointments = AppointmentHome.getAppointmentsListByIdForm ( form.getIdForm( ) ) ; 
		List <Integer> list = new ArrayList<Integer> ( ) ;
		for ( Appointment appointment : listAllAppointments )
		{
			list.add( appointment.getIdAppointment( ) ) ;

		}
		List<Appointment> listAppointments = AppointmentHome.getAppointmentListById( list );
		return listAppointments ;
	}

	/**
	 * 
	 * @param appointment the appointment
	 * @param reminder the reminder
	 * @param startAppointment startAppointment date of start appointment
	 * @param nDiffMin diffrence time in minutes
	 * @param form the appointment form
	 * @param config the task config
	 */
	private void sendReminder ( Appointment appointment , ReminderAppointment reminder, Date startAppointment, int nDiffMin , AppointmentForm form, TaskNotifyReminderConfig config )
	{
		int nMinTime = ( reminder.getTimeToAlert( ) * 60 * 24 ) - MARK_DURATION_LIMIT  ;
		int nMaxTime = ( reminder.getTimeToAlert( ) * 60 * 24 ) + MARK_DURATION_LIMIT  ;
		
		AppLogService.info( "Alert time :" + reminder.getTimeToAlert( ) );
		AppLogService.info( "nDiffMin  : " + nDiffMin );
		
		
    	if ( nDiffMin <= nMaxTime  &&  nDiffMin >= nMinTime && ( ( appointment.getHasNotify ( ) == 0 ) || ( appointment.getHasNotify ( ) != ( reminder.getRank( ) ) ) ) )
    	{
    		boolean bNotified = false ;
    		Locale locale = LocaleService.getDefault(  );
    		String strSenderMail = MailService.getNoReplyEmail(  );
    		String strSenderName = I18nService.getLocalizedString( PROPERTY_MAIL_SENDER_NAME, locale );
    		String strEmailCc = reminder.getEmailCc( ) ;
    		
    		AppLogService.info( "IN :" );
    		AppLogService.info( "strSenderMail :" + strSenderMail );
    		AppLogService.info( "strSenderName :" + strSenderName );
    		AppLogService.info( "Dest :" + appointment.getEmail( ) );
    		AppLogService.info( "Objet :" + reminder.getAlertSubject( ) );
    		
    		String strText = reminder.getAlertMessage( ) ;
    		
    		if ( strText!=null && !strText.isEmpty( ) )
    		{
    			strText = getMessageAppointment( strText, appointment ) ;
    		}
    		if ( reminder.isEmailNotify( ) && !appointment.getEmail( ).isEmpty( ) )
    		{
    			 try
                 {
    				 AppLogService.info( "try to send MAIL : \n " );
    				 MailService.sendMailHtml( appointment.getEmail( ) ,strEmailCc,  StringUtils.EMPTY,  strSenderName, strSenderMail ,reminder.getAlertSubject( ) , strText  );
    				 bNotified = true ;
    				 AppLogService.info( "AppointmentReminderDaemon - Info sending reminder alert mail to : " + appointment.getEmail( ) );
                 }
    			 catch ( Exception e )
                 {
                     AppLogService.error( "AppointmentReminderDaemon - Error sending reminder alert MAIL to : " +
                         e.getMessage(  ), e );
                 }
    		}
    		AppLogService.info( "SMS :  " + reminder.isSmsNotify( ) );
    		if ( reminder.isSmsNotify( ) && reminder.getNumberPhone( ) != null )
    		{
    			
    			String strRecipient = getSmsFromAppointment ( appointment, reminder );
    			AppLogService.info( "PHONE : " + strRecipient );
    			
    			if ( !strRecipient.isEmpty( ) && strRecipient.matches( MARK_REGEX_SMS ) )
    			{
	        		 try
	                 {
	        			strRecipient += MARK_PREFIX_SENDER ;
	        			AppLogService.info( "try to send SMS : \n " );
	        			AppLogService.info( "strRecipient" + strRecipient );
		        		AppLogService.info( "strSenderName" + strSenderName );
		        		AppLogService.info( "MARK_SENDER_SMS" + MARK_SENDER_SMS  );
		        		AppLogService.info( "strText" + strText );
		        		
	 	    			MailService.sendMailText( strRecipient  ,strEmailCc,  StringUtils.EMPTY,  strSenderName ,  MARK_SENDER_SMS ,reminder.getAlertSubject( ) , strText  );
	 	        		bNotified = true ;
	 	        		AppLogService.info( "AppointmentReminderDaemon - Info sending reminder alert SMS to : " + strRecipient );
	                 }
	    			 catch ( Exception e )
	                 {
	                     AppLogService.error( "AppointmentReminderDaemon - Error sending reminder alert SMS to : " +
	                         e.getMessage(  ), e );
	                 }
    			}
    		}
    		if ( bNotified )
    		{
    			 Plugin appointmentPlugin = PluginService.getPlugin( AppointmentPlugin.PLUGIN_NAME );

                 TransactionManager.beginTransaction( appointmentPlugin );
                 appointment.setHasNotify( reminder.getRank( ) );
    			 try
                 {
	    			AppointmentHome.update( appointment );
	    			doChangeState( config , reminder , form, appointment ) ; 
	    			AppLogService.info( "FLAG ON : " );
                 }
                 catch ( Exception e )
                 {
                     TransactionManager.rollBack( appointmentPlugin );
                     throw new AppException( e.getMessage(  ), e );
                 }
    			 TransactionManager.commitTransaction( appointmentPlugin );
    		}
    	}
	}
	
	/**
	 *  Get sms number
	 * @param appointment the appointment
	 * @param reminder the reminder task
	 * @return the sms number
	 */
	private String getSmsFromAppointment( Appointment appointment, ReminderAppointment reminder )
    {
		AppLogService.info( " getSmsFromAppointment GET NUMBER : " );
        String strPhoneNumber = StringUtils.EMPTY ;
        AppointmentSlot slot = AppointmentSlotHome.findByPrimaryKey( appointment.getIdSlot(  ) );
        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdResource( slot.getIdForm(  ) );

        List<Integer> listIdResponse = AppointmentHome.findListIdResponse( appointment.getIdAppointment(  ) );
        AppLogService.info( "listIdResponse.SIZE  : " + listIdResponse.size( ) );
        List<Response> listResponses = new ArrayList<Response>( listIdResponse.size(  ) );
        AppLogService.info( "listResponses.SIZE  : " + listResponses.size( ) );
        for ( int nIdResponse : listIdResponse )
        {
        	Response response = ResponseHome.findByPrimaryKey( nIdResponse ) ;
        	if ( response != null )
        	{
        		listResponses.add( response );
        	}
        }
        
        List<Entry> listEntries = EntryHome.getEntryList( entryFilter );
        AppLogService.info( "listEntries.SIZE  : " + listEntries.size( ) );
        for ( Entry entry : listEntries )
        {
            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( entry );

            if ( entryTypeService instanceof EntryTypePhone )
            {
                for ( Response response : listResponses )
                {
                    if ( ( response.getEntry(  ).getIdEntry(  ) == entry.getIdEntry(  ) ) &&
                            StringUtils.isNotBlank( response.getResponseValue(  ) ) && entry.getTitle( ).equals( reminder.getNumberPhone( ) ) )
                    {
                        strPhoneNumber = response.getResponseValue(  );

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
	 * @param msg the text message
	 * @param appointment the appointment
	 * @return the text message
	 */
	private String getMessageAppointment( String msg, Appointment appointment )
	{
		String strLocation = appointment.getLocation( ) == null ? StringUtils.EMPTY : appointment.getLocation( ) ;
		String strText = StringUtils.EMPTY;
		strText = msg.replace( MARK_FIRST_NAME, appointment.getFirstName( ) );
		strText = strText.replace( MARK_LAST_NAME, appointment.getLastName( ) );
		strText = strText.replace( MARK_DATE_APP,  appointment.getDateAppointment( ).toString( ) );
		strText = strText.replace( MARK_TIME_APP,  appointment.getStartAppointment().toString( ) );
		strText = strText.replace( MARK_LOCALIZATION, strLocation  );
		strText = strText.replace( MARK_CANCEL_APP , AppointmentApp.getCancelAppointmentUrl( appointment ) ) ;
		
		return strText ;
	}
	/**
	 * 
	 * @param config the config task
	 * @param reminder the reminder task
	 * @param form the appointment form
	 * @param appointment the appointment
	 */
    private void doChangeState( TaskNotifyReminderConfig config , ReminderAppointment reminder, AppointmentForm form , Appointment appointment )
    {
         //The locale is not important. It is just used to fetch the task action id
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
                ResourceWorkflow resourceWorkflow =  _resourceWorkflowService.findByPrimaryKey( appointment.getIdAppointment( ), Appointment.APPOINTMENT_RESOURCE_TYPE, form.getIdWorkflow( ) );
                resourceWorkflow.setState( state );
                _resourceWorkflowService.update( resourceWorkflow );
               
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveConfig(  )
    {
    	_taskNotifyReminderConfigService.remove( this.getId(  ) );
    }
	@Override
	public String getTitle( Locale locale ) 
	{
		return I18nService.getLocalizedString( MESSAGE_MARK_DESCRIPTION , locale ); 
	}

}
