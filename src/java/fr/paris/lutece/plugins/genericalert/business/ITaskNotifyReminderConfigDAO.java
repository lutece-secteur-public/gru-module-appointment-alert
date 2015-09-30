
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

import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.Collection;
import java.util.List;



/**
 * ITaskReminderConfigDAO Interface
 */
public interface ITaskNotifyReminderConfigDAO extends ITaskConfigDAO<TaskNotifyReminderConfig>
{

    /**
     * Insert a new record in the table.
     * @param taskReminderConfig instance of the TaskReminderConfig object to insert
     */
    void insert( TaskNotifyReminderConfig taskReminderConfig );

    /**
     * Update the record in the table
     * @param taskReminderConfig the reference of the TaskReminderConfig
     */
    void store( TaskNotifyReminderConfig taskReminderConfig );

    /**
     * Delete a record from the table
     * @param nKey The identifier of the TaskReminderConfig to delete
     */
    void delete( int nKey );

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data from the table
     * @param nKey The identifier of the taskReminderConfig
     * @return The instance of the taskReminderConfig
     */
    TaskNotifyReminderConfig load( int nKey );

    /**
     * Load the data of all the taskReminderConfig objects and returns them as a collection
     * @param plugin the Plugin
     * @return The collection which contains the data of all the taskReminderConfig objects
     */
    Collection<TaskNotifyReminderConfig> selectTaskReminderConfigsList( Plugin plugin );
    
    /**
     * Load the id of all the taskReminderConfig objects and returns them as a collection
     * @param plugin the Plugin
     * @return The collection which contains the id of all the taskReminderConfig objects
     */
    Collection<Integer> selectIdTaskReminderConfigsList( Plugin plugin );
    
    /**
     * 
     * @param nIdForm the idform
     * @param nIdTask the id task
     * @param nRank the rank reminder
     * @param b boolean delete
     * @param plugin the plugin
     */
	void deleteReminderAppointment( int nIdForm,int nIdTask , int nRank, boolean b, Plugin plugin );
	
	/**
	 * @param nIdTask the id task
	 * @param nAppointmentFormId the id form 
	 * @param plugin the plugin
	 * @return list ReminderAppointment
	 */
	List<ReminderAppointment> loadListReminderAppointment( int nIdTask,  int nAppointmentFormId, Plugin plugin );
	
	/**
	 * 
	 * @param nKey the id task
	 * @param idForm the id form
	 * @param plugin the plugin
	 * @return TaskNotifyReminderConfig
	 */
	TaskNotifyReminderConfig loadByIdForm( int nKey, int idForm, Plugin plugin );
	
	/**
	 * 
	 * @param idTask the id task
	 * @param plugin the plugin
	 * @return list TaskNotifyReminderConfig 
	 */
	List<TaskNotifyReminderConfig> loadListTaskNotifyConfig( int idTask, Plugin plugin );

	/**
	 * 
	 * @param idForm the id form
	 * @param plugin the plugin
	 * @return TaskNotifyReminderConfig
	 */
	TaskNotifyReminderConfig loadConfigByIdForm( int idForm, Plugin plugin );


}

