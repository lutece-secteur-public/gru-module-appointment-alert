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
package fr.paris.lutece.plugins.genericalert.service;

import fr.paris.lutece.plugins.genericalert.business.TaskNotifyReminderConfig;
import fr.paris.lutece.plugins.workflowcore.service.config.TaskConfigService;

import java.util.List;
import java.util.Map;

/**
 *
 * TaskFillingDirectoryConfigService
 *
 */

public class TaskNotifyReminderConfigService extends TaskConfigService implements ITaskNotifyReminderConfigService
{

    public static final String BEAN_SERVICE = "genericalert.taskNotifyReminderConfigService";

    @Override
    public List<TaskNotifyReminderConfig> findAll( )
    {
        return null;
    }

    @Override
    public void selectUpdate( TaskNotifyReminderConfig config, String strIdEntry, int idParentEntry )
    {

    }

    @Override
    public void unSelectUpdate( TaskNotifyReminderConfig config, String strIdEntry, int idParentEntry )
    {

    }

    @Override
    public Map<Integer, List<Integer>> loadSelectedList( int idTask, int idDirectory )
    {
        return null;
    }

    @Override
    public void selectedRecords( TaskNotifyReminderConfig config )
    {

    }

    @Override
    public void loadListEntriesTmp( int idTask, int idDirectory )
    {

    }

}
