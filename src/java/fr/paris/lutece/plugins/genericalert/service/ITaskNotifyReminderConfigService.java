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

import fr.paris.lutece.plugins.genericalert.business.TaskNotifyReminderConfig;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 * ITaskFillingDirectoryConfigService
 *
 */
public interface ITaskNotifyReminderConfigService
{
    /**
     * Select all tasks
     * 
     * @return a list of tasks
     */
    @Transactional( NotifyReminderPlugin.BEAN_TRANSACTION_MANAGER )
    List<TaskNotifyReminderConfig> findAll( );

    /**
     * Select items
     * 
     * @param config
     *            task management
     * @param strIdEntry
     *            idEntry
     * @param idParentEntry
     *            idParentEntry
     */
    @Transactional( NotifyReminderPlugin.BEAN_TRANSACTION_MANAGER )
    void selectUpdate( TaskNotifyReminderConfig config, String strIdEntry, int idParentEntry );

    /**
     * Unselect items
     * 
     * @param config
     *            task management
     * @param strIdEntry
     *            idEntry
     * @param idParentEntry
     *            idParentEntry
     */
    @Transactional( NotifyReminderPlugin.BEAN_TRANSACTION_MANAGER )
    void unSelectUpdate( TaskNotifyReminderConfig config, String strIdEntry, int idParentEntry );

    /**
     * 
     * @param idTask
     *            task
     * @param idDirectory
     *            directory
     * @return list Selected items
     */
    @Transactional( NotifyReminderPlugin.BEAN_TRANSACTION_MANAGER )
    Map<Integer, List<Integer>> loadSelectedList( int idTask, int idDirectory );

    /**
     * 
     * @param config
     *            task management
     */
    @Transactional( NotifyReminderPlugin.BEAN_TRANSACTION_MANAGER )
    void selectedRecords( TaskNotifyReminderConfig config );

    /**
     * 
     * @param idTask
     *            id task
     * @param idDirectory
     *            id directory
     */
    @Transactional( NotifyReminderPlugin.BEAN_TRANSACTION_MANAGER )
    void loadListEntriesTmp( int idTask, int idDirectory );
}
