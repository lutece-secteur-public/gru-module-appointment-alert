/*
 * Copyright (c) 2002-2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *	 and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *	 and the following disclaimer in the documentation and/or other materials
 *	 provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *	 contributors may be used to endorse or promote products derived from
 *	 this software without specific prior written permission.
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


package fr.paris.lutece.plugins.genericalert.business;

import fr.paris.lutece.plugins.genericalert.service.NotifyReminderPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class provides Data Access methods for TaskReminderConfig objects
 */

public final class TaskNotifyReminderConfigDAO implements ITaskNotifyReminderConfigDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_task, id_form, nb_alerts FROM workflow_task_notify_reminder_cf ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_task_notify_reminder_cf ( id_task, id_form, nb_alerts ) VALUES ( ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_notify_reminder_cf WHERE id_task = ?";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_notify_reminder_cf SET nb_alerts = ? WHERE id_task = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_task, id_form, nb_alerts FROM workflow_task_notify_reminder_cf";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_task FROM workflow_task_notify_reminder_cf";
    private static final String SQL_QUERY_SELECT_ID_FORM_BY_TASK = "SELECT id_form FROM workflow_task_notify_reminder_cf where id_task = ? ";
    private static final String SQL_QUERY_WHERE = " WHERE " ;
    private static final String SQL_QUERY_AND = " AND " ;
    private static final String SQL_ID_TASK = " id_task = ?" ; 
    private static final String SQL_ID_FORM = " id_form = ?" ; 
    
    private static final String SQL_QUERY_FIND_REMINDER_APPOINTMENT_BY_PRIMARY_KEY = "SELECT id_task, id_form, rank, time_to_alert, email_notify, sms_notify, email_alert_message, sms_alert_message, alert_subject, email_cc, phone_number, id_state_after FROM workflow_appointment_reminder WHERE id_form = ? AND id_task = ? ";
    private static final String SQL_QUERY_INSERT_REMINDER_APPOINTMENT_FORM_MESSAGE = "INSERT INTO workflow_appointment_reminder(id_task,id_form, rank, time_to_alert, email_notify, sms_notify, email_alert_message, sms_alert_message, alert_subject, email_cc, phone_number, id_state_after) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE_REMINDER_APPOINTMENT_FORM_MESSAGE = "UPDATE workflow_appointment_reminder SET time_to_alert = ?, email_notify = ?, sms_notify = ?, email_alert_message = ?, sms_alert_message = ?, alert_subject = ?, email_cc = ?, phone_number = ?, id_state_after = ? WHERE id_form = ?  AND id_task = ?";
    private static final String SQL_QUERY_DELETE_REMINDER_APPOINTMENT_FORM_MESSAGE = "DELETE FROM workflow_appointment_reminder WHERE id_form = ? AND id_task= ? ";
    private static final String SQL_QUERY_RANK = " AND rank = ?";
    private static final String SQL_QUERY_ORDER_BY_RANK = " ORDER BY rank";
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( TaskNotifyReminderConfig taskReminderConfig )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, NotifyReminderPlugin.getPlugin( ) );

        daoUtil.setInt( 1, taskReminderConfig.getIdTask( ) );
        daoUtil.setInt( 2, taskReminderConfig.getIdForm( ) );
        daoUtil.setInt( 3, taskReminderConfig.getNbAlerts( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
        if ( taskReminderConfig != null )
        {
		    if ( taskReminderConfig.getListReminderAppointment( ).size( ) > 0 )
		    {
		    	insertListReminderAppointment( taskReminderConfig.getListReminderAppointment( ), NotifyReminderPlugin.getPlugin( ) ) ;
		    }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TaskNotifyReminderConfig load( int nKey )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT + SQL_QUERY_WHERE + SQL_ID_TASK , NotifyReminderPlugin.getPlugin( ) );
        daoUtil.setInt( 1 , nKey );
        daoUtil.executeQuery( );

        TaskNotifyReminderConfig taskReminderConfig = null;
        
        if ( daoUtil.next( ) )
        {
            taskReminderConfig = new TaskNotifyReminderConfig();
            taskReminderConfig.setIdTask( daoUtil.getInt( 1 ) );
            taskReminderConfig.setIdForm( daoUtil.getInt( 2 ) );
            taskReminderConfig.setNbAlerts( daoUtil.getInt( 3 ) );
        }
        daoUtil.free( );
        if ( taskReminderConfig != null )
        {
	        List <ReminderAppointment> listReminder = loadListReminderAppointment( taskReminderConfig.getIdTask( ) , taskReminderConfig.getIdForm( ) , NotifyReminderPlugin.getPlugin( ) ) ;
	        
	        if ( listReminder != null )
	        {
	        	taskReminderConfig.setListReminderAppointment( listReminder );
	        }
        }
        return taskReminderConfig;
    }
    /**
     * {@inheritDoc }
     */
    @Override
    public TaskNotifyReminderConfig loadConfigByIdForm( int idForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT +  SQL_QUERY_WHERE +  SQL_ID_FORM , plugin );
        daoUtil.setInt( 1 , idForm );
        daoUtil.executeQuery( );

        TaskNotifyReminderConfig taskReminderConfig = null;
        
        if ( daoUtil.next( ) )
        {
            taskReminderConfig = new TaskNotifyReminderConfig();
            taskReminderConfig.setIdTask( daoUtil.getInt( 1 ) );
            taskReminderConfig.setIdForm( daoUtil.getInt( 2 ) );
            taskReminderConfig.setNbAlerts( daoUtil.getInt( 3 ) );
        }
        daoUtil.free( );
        if ( taskReminderConfig != null )
        {
	        List <ReminderAppointment> listReminder = loadListReminderAppointment( taskReminderConfig.getIdTask( ) , taskReminderConfig.getIdForm( ) , NotifyReminderPlugin.getPlugin( ) ) ;
	        
	        if ( listReminder != null )
	        {
	        	taskReminderConfig.setListReminderAppointment( listReminder );
	        }
        }
        return taskReminderConfig;
    }
    /**
     * {@inheritDoc }
     */
    @Override
    public void store( TaskNotifyReminderConfig taskReminderConfig )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE + SQL_QUERY_AND +  SQL_ID_FORM, NotifyReminderPlugin.getPlugin( ) );
        
        daoUtil.setInt( 1, taskReminderConfig.getNbAlerts( ) ) ;
        daoUtil.setInt( 2, taskReminderConfig.getIdTask( ) ) ;
        daoUtil.setInt( 3, taskReminderConfig.getIdForm( ) ) ;

        daoUtil.executeUpdate( );
        daoUtil.free( );
        
        if ( taskReminderConfig.getListReminderAppointment( ).size( ) > 0 )
        {
        	storeReminderAppointment( taskReminderConfig.getIdTask( ) , taskReminderConfig.getListReminderAppointment( ), NotifyReminderPlugin.getPlugin( ) );
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
   	public void delete( int idTask ) 
   	{
   		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE , NotifyReminderPlugin.getPlugin( ) );
   		
   		Collection <Integer> listIdFormList = selectIdFormByTask( idTask , NotifyReminderPlugin.getPlugin( ) ) ;
   		for ( Integer e : listIdFormList )
   		{
   			deleteListReminderAppointment( idTask ,e , NotifyReminderPlugin.getPlugin( ) ) ;
   		}

           daoUtil.setInt( 1 , idTask );
           daoUtil.executeUpdate( );
           daoUtil.free( );
   	}
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<TaskNotifyReminderConfig> selectTaskReminderConfigsList( Plugin plugin )
    {
        Collection<TaskNotifyReminderConfig> taskReminderConfigList = new ArrayList<TaskNotifyReminderConfig>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            TaskNotifyReminderConfig taskReminderConfig = new TaskNotifyReminderConfig(  );
            
            taskReminderConfig.setIdTask( daoUtil.getInt( 1 ) );
            taskReminderConfig.setIdForm( daoUtil.getInt( 2 ) );
            taskReminderConfig.setNbAlerts( daoUtil.getInt( 3 ) );

            taskReminderConfigList.add( taskReminderConfig );
        }

        daoUtil.free( );
        return taskReminderConfigList;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteReminderAppointment( int nIdTask  ,int nIdForm ,  int rank, boolean b,  Plugin plugin )
    {
    	DAOUtil daoUtil = null ;
    	if ( b )
    	{
    		daoUtil = new DAOUtil( SQL_QUERY_DELETE_REMINDER_APPOINTMENT_FORM_MESSAGE , plugin );
    		daoUtil.setInt( 1, nIdForm );
    		daoUtil.setInt( 2, nIdTask );
    	}
    	else
    	{
    		daoUtil = new DAOUtil( SQL_QUERY_DELETE_REMINDER_APPOINTMENT_FORM_MESSAGE + SQL_QUERY_RANK, plugin );
    		daoUtil.setInt( 1, nIdForm );
    		daoUtil.setInt( 2, nIdTask );
    	    daoUtil.setInt( 3, rank );
    	}

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Integer> selectIdTaskReminderConfigsList( Plugin plugin )
    {
            Collection<Integer> taskReminderConfigList = new ArrayList<Integer>( );
            DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
            daoUtil.executeQuery(  );

            while ( daoUtil.next(  ) )
            {
                taskReminderConfigList.add( daoUtil.getInt( 1 ) );
            }

            daoUtil.free( );
            return taskReminderConfigList;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List <ReminderAppointment> loadListReminderAppointment( int idTask,  int nAppointmentFormId, Plugin plugin )
    {

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_REMINDER_APPOINTMENT_BY_PRIMARY_KEY + SQL_QUERY_ORDER_BY_RANK, plugin );
        daoUtil.setInt( 1, nAppointmentFormId );
        daoUtil.setInt( 2, idTask );
        daoUtil.executeQuery(  );

        ReminderAppointment reminderAppointment;
        List <ReminderAppointment> list = new ArrayList <ReminderAppointment>( );
        while ( daoUtil.next(  ) )
        {
        	reminderAppointment = new ReminderAppointment(  );

            int nIndex = 1;
            
            reminderAppointment.setIdTask( daoUtil.getInt( nIndex++ ) );
            reminderAppointment.setIdForm( daoUtil.getInt( nIndex++ ) );
            reminderAppointment.setRank( daoUtil.getInt( nIndex++ ) );
            reminderAppointment.setTimeToAlert( daoUtil.getInt( nIndex++ ) );
            reminderAppointment.setEmailNotify( daoUtil.getBoolean( nIndex++ ) );
            reminderAppointment.setSmsNotify( daoUtil.getBoolean( nIndex++ ) );
            reminderAppointment.setEmailAlertMessage( daoUtil.getString( nIndex++ ) );
            reminderAppointment.setSmsAlertMessage( daoUtil.getString( nIndex++ ) );
            reminderAppointment.setAlertSubject( daoUtil.getString( nIndex ++ ) );
            reminderAppointment.setEmailCc( daoUtil.getString( nIndex++ ) );
            reminderAppointment.setNumberPhone( daoUtil.getString( nIndex++ ) );
            reminderAppointment.setIdStateAfter( daoUtil.getInt( nIndex ) );
            list.add( reminderAppointment );
        }
        daoUtil.free(  );

        return list;
    }
    
    /**
     * {@inheritDoc}
     */
    public List <TaskNotifyReminderConfig> loadListTaskNotifyConfig( int idTask, Plugin plugin )
    {

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT + SQL_QUERY_WHERE + SQL_ID_TASK , plugin );
        daoUtil.setInt( 1 , idTask );
        daoUtil.executeQuery( );
        
        List <TaskNotifyReminderConfig> listConfig = new ArrayList <TaskNotifyReminderConfig>( );
        while ( daoUtil.next(  ) )
        {
            TaskNotifyReminderConfig taskReminderConfig = new TaskNotifyReminderConfig(  );
            List <ReminderAppointment> listReminder = new ArrayList <ReminderAppointment>( );
            
            taskReminderConfig.setIdTask( daoUtil.getInt( 1 ) );
            taskReminderConfig.setIdForm( daoUtil.getInt( 2 ) );
            taskReminderConfig.setNbAlerts( daoUtil.getInt( 3 ) );

            listReminder = loadListReminderAppointment(  idTask, taskReminderConfig.getIdForm( ) , plugin ) ;
    	        
    	    if ( listReminder != null )
    	    {
    	    	taskReminderConfig.setListReminderAppointment( listReminder );
    	    }
            listConfig.add( taskReminderConfig ) ;
            
        }
        daoUtil.free(  );
        return listConfig;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public TaskNotifyReminderConfig loadByIdForm( int nKey , int idForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT + SQL_QUERY_WHERE + SQL_ID_TASK + SQL_QUERY_AND + SQL_ID_FORM , plugin );
        daoUtil.setInt( 1 , nKey );
        daoUtil.setInt( 2 , idForm );
        daoUtil.executeQuery( );

        TaskNotifyReminderConfig taskReminderConfig = null;
        
        if ( daoUtil.next( ) )
        {
            taskReminderConfig = new TaskNotifyReminderConfig( );
            taskReminderConfig.setIdTask( daoUtil.getInt( 1 ) );
            taskReminderConfig.setIdForm( daoUtil.getInt( 2 ) );
            taskReminderConfig.setNbAlerts( daoUtil.getInt( 3 ) );
        }
        daoUtil.free( );
        if ( taskReminderConfig != null )
        {
	        List <ReminderAppointment> listReminder = loadListReminderAppointment( nKey, idForm , plugin ) ;
	        
	        if ( listReminder != null )
	        {
	        	taskReminderConfig.setListReminderAppointment( listReminder );
	        }
        }
        return taskReminderConfig;
    }
    /**
     * Insert Reminder Appointment
     * @param reminderAppointment the reminder appointment
     * @param plugin the plugin
     */
    private void insertReminderAppointment( ReminderAppointment reminderAppointment, Plugin plugin )
    {
    	DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_REMINDER_APPOINTMENT_FORM_MESSAGE, plugin );
	        int nIndex = 1;
	       
	        daoUtil.setInt( nIndex++, reminderAppointment.getIdTask( ) );
	        daoUtil.setInt( nIndex++, reminderAppointment.getIdForm( ) );
	        daoUtil.setInt( nIndex++, reminderAppointment.getRank( ) );
	        daoUtil.setInt( nIndex++, reminderAppointment.getTimeToAlert( ) );
	        daoUtil.setBoolean( nIndex++, reminderAppointment.isEmailNotify( ) );
	        daoUtil.setBoolean( nIndex++, reminderAppointment.isSmsNotify( ) );
	        daoUtil.setString( nIndex++ , reminderAppointment.getEmailAlertMessage( ) );
	        daoUtil.setString( nIndex++ , reminderAppointment.getSmsAlertMessage( ) );
	        daoUtil.setString( nIndex++, reminderAppointment.getAlertSubject( ) );
	        daoUtil.setString( nIndex++, reminderAppointment.getEmailCc( ) );
	        daoUtil.setString( nIndex++, reminderAppointment.getNumberPhone( ) );
	        daoUtil.setInt( nIndex, reminderAppointment.getIdStateAfter( ) );
	        daoUtil.executeUpdate(  );
	        daoUtil.free(  );
    }
    
    /**
     * Insert List Reminder Appointment
     * @param listReminderAppointment List Reminder
     * @param plugin the plugin
     */
    private void insertListReminderAppointment( List <ReminderAppointment> listReminderAppointment , Plugin plugin )
    {
    	for ( ReminderAppointment reminderAppointment : listReminderAppointment )
        {
    		insertReminderAppointment ( reminderAppointment, plugin ) ;
        }
    }
    
    /**
     * Store Reminder Appointment
     * @param idTask the id task
     * @param listReminderAppointment list reminder appointment
     * @param plugin the plugin
     */
    private void storeReminderAppointment( int idTask, List <ReminderAppointment> listReminderAppointment , Plugin plugin )
    {
        
        for ( ReminderAppointment reminderAppointment : listReminderAppointment )
        {
        	if ( loadReminderAppointment ( idTask, reminderAppointment.getIdForm( ) , reminderAppointment.getRank( ) , plugin ) == null )
        	{
        		insertReminderAppointment( reminderAppointment , plugin ) ;
        	}
        	else
        	{
        		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_REMINDER_APPOINTMENT_FORM_MESSAGE + SQL_QUERY_RANK, plugin );
		        int nIndex = 1;
		        
		        daoUtil.setInt( nIndex++, reminderAppointment.getTimeToAlert( ) );
		        daoUtil.setBoolean( nIndex++, reminderAppointment.isEmailNotify( ) );
		        daoUtil.setBoolean( nIndex++, reminderAppointment.isSmsNotify( ) );
		        daoUtil.setString( nIndex++ , reminderAppointment.getEmailAlertMessage( ) );
		        daoUtil.setString( nIndex++ , reminderAppointment.getSmsAlertMessage( ) );
		        daoUtil.setString( nIndex++ , reminderAppointment.getAlertSubject( ) );
		        daoUtil.setString( nIndex++, reminderAppointment.getEmailCc( ) );
		        daoUtil.setString( nIndex++, reminderAppointment.getNumberPhone( ) );
		        daoUtil.setInt( nIndex++, reminderAppointment.getIdStateAfter( ) );
		        daoUtil.setInt( nIndex++, reminderAppointment.getIdForm(  ) );
		        daoUtil.setInt( nIndex++, reminderAppointment.getIdTask( ) );
		        daoUtil.setInt( nIndex, reminderAppointment.getRank(  ) );
		        
		
		        daoUtil.executeUpdate(  );
		        daoUtil.free(  );
        	}
        }
    }
    
    /**
     * Load Reminder Appointment
     * @param idTask the id task
     * @param nAppointmentFormId id appointment form
     * @param rank the rank reminder
     * @param plugin the plugin
     * @return Reminder Appointment
     */
    private ReminderAppointment loadReminderAppointment( int idTask, int nAppointmentFormId , int rank, Plugin plugin )
    {

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_REMINDER_APPOINTMENT_BY_PRIMARY_KEY + SQL_QUERY_RANK , plugin );
        daoUtil.setInt( 1, nAppointmentFormId );
        daoUtil.setInt( 2, idTask );
        daoUtil.setInt( 3, rank );
        daoUtil.executeQuery(  );

        ReminderAppointment reminderAppointment;
        if ( daoUtil.next(  ) )
        {
        	reminderAppointment = new ReminderAppointment(  );
            int nIndex = 1;
            reminderAppointment.setIdTask( daoUtil.getInt( nIndex++ ) );
            reminderAppointment.setIdForm( daoUtil.getInt( nIndex++ ) );
            reminderAppointment.setRank( daoUtil.getInt( nIndex++ ) );
            reminderAppointment.setTimeToAlert( daoUtil.getInt( nIndex++ ) );
            reminderAppointment.setEmailNotify( daoUtil.getBoolean( nIndex++ ) );
            reminderAppointment.setSmsNotify( daoUtil.getBoolean( nIndex++ ) );
            reminderAppointment.setEmailAlertMessage( daoUtil.getString( nIndex++ ) );
            reminderAppointment.setSmsAlertMessage( daoUtil.getString( nIndex++ ) );
            reminderAppointment.setAlertSubject( daoUtil.getString( nIndex++ ) );
            reminderAppointment.setEmailCc( daoUtil.getString( nIndex++ ) );
            reminderAppointment.setNumberPhone( daoUtil.getString( nIndex++ ) );
            reminderAppointment.setIdStateAfter( daoUtil.getInt( nIndex ) );
        }
        else
        {
        	reminderAppointment = null;
        }
        daoUtil.free(  );

        return reminderAppointment;
    }
    
    
    
    /**
     * Delete List Reminder Appointment
     * @param idTask the id task
     * @param nAppointmentFormId id appointment form
     * @param plugin the plugin
     */
    private void deleteListReminderAppointment( int idTask, int nAppointmentFormId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_REMINDER_APPOINTMENT_FORM_MESSAGE, plugin );
        daoUtil.setInt( 1, nAppointmentFormId );
        daoUtil.setInt( 2, idTask );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
    
    /**
     * 
     * @param idTask the id task
     * @param plugin the plugin
     * @return list selectIdFormByTask
     */
    private Collection<Integer> selectIdFormByTask( int idTask , Plugin plugin )
    {
            Collection<Integer> listIdFormList = new ArrayList<Integer>( );
            DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ID_FORM_BY_TASK, plugin );
            daoUtil.setInt( 1, idTask );
            daoUtil.executeQuery(  );

            while ( daoUtil.next(  ) )
            {
            	listIdFormList.add( daoUtil.getInt( 1 ) );
            }

            daoUtil.free( );
            return listIdFormList;
    }
}