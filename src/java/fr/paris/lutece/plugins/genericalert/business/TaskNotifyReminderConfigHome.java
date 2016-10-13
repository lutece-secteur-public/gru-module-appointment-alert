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
package fr.paris.lutece.plugins.genericalert.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Collection;
import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for TaskReminderConfig objects
 */

public final class TaskNotifyReminderConfigHome
{
    // Static variable pointed at the DAO instance

    private static ITaskNotifyReminderConfigDAO _dao = SpringContextService.getBean( "genericalert.taskNotifyReminderConfigDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "genericalert" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TaskNotifyReminderConfigHome( )
    {
    }

    /**
     * Create an instance of the taskReminderConfig class
     * 
     * @param taskReminderConfig
     *            The instance of the TaskReminderConfig which contains the informations to store
     * @return The instance of taskReminderConfig which has been created with its primary key.
     */
    public static TaskNotifyReminderConfig create( TaskNotifyReminderConfig taskReminderConfig )
    {
        _dao.insert( taskReminderConfig );

        return taskReminderConfig;
    }

    /**
     * Update of the taskReminderConfig which is specified in parameter
     * 
     * @param taskReminderConfig
     *            The instance of the TaskReminderConfig which contains the data to store
     * @return The instance of the taskReminderConfig which has been updated
     */
    public static TaskNotifyReminderConfig update( TaskNotifyReminderConfig taskReminderConfig )
    {
        _dao.store( taskReminderConfig );

        return taskReminderConfig;
    }

    /**
     * Remove the taskReminderConfig whose identifier is specified in parameter
     * 
     * @param nKey
     *            The taskReminderConfig Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a taskReminderConfig whose identifier is specified in parameter
     * 
     * @param nKey
     *            The taskReminderConfig primary key
     * @return an instance of TaskReminderConfig
     */
    public static TaskNotifyReminderConfig findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey );
    }

    /**
     * Load the data of all the taskReminderConfig objects and returns them in form of a collection
     * 
     * @return the collection which contains the data of all the taskReminderConfig objects
     */
    public static Collection<TaskNotifyReminderConfig> getTaskReminderConfigsList( )
    {
        return _dao.selectTaskReminderConfigsList( _plugin );
    }

    /**
     * Load the id of all the taskReminderConfig objects and returns them in form of a collection
     * 
     * @return the collection which contains the id of all the taskReminderConfig objects
     */
    public static Collection<Integer> getIdTaskReminderConfigsList( )
    {
        return _dao.selectIdTaskReminderConfigsList( _plugin );
    }

    /**
     * Remove a Appointment Reminder
     * 
     * @param idTask
     *            the id task
     * @param nAppointmentFormId
     *            id form appointment
     * @param rank
     *            the rank
     * @param bool
     *            to delete all reminders
     */
    public static void removeAppointmentReminder( int idTask, int nAppointmentFormId, int rank, boolean bool )
    {
        _dao.deleteReminderAppointment( idTask, nAppointmentFormId, rank, bool, _plugin );
    }

    /**
     * 
     * @param idTask
     *            the id task
     * @param nAppointmentFormId
     *            id form
     * @return list ReminderAppointment
     */
    public static List<ReminderAppointment> loadListReminderAppointment( int idTask, int nAppointmentFormId )
    {
        return _dao.loadListReminderAppointment( idTask, nAppointmentFormId, _plugin );
    }

    /**
     * 
     * @param nKey
     *            id task
     * @param idForm
     *            id form
     * @return TaskNotifyReminderConfig the config
     */
    public static TaskNotifyReminderConfig findByIdForm( int nKey, int idForm )
    {
        return _dao.loadByIdForm( nKey, idForm, _plugin );
    }

    /**
     * 
     * @param idTask
     *            the id task
     * @return list TaskNotifyReminderConfig
     */
    public static List<TaskNotifyReminderConfig> loadListTaskNotifyConfig( int idTask )
    {
        return _dao.loadListTaskNotifyConfig( idTask, _plugin );
    }

    /**
     * 
     * @param idForm
     *            the id form
     * @return TaskNotifyReminderConfig the config
     */
    public static TaskNotifyReminderConfig loadConfigByIdForm( int idForm )
    {
        return _dao.loadConfigByIdForm( idForm, _plugin );
    }
}
