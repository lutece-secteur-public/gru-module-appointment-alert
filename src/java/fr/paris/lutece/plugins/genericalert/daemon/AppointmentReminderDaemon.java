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
package fr.paris.lutece.plugins.genericalert.daemon;

import java.util.List;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.genericalert.service.TaskNotifyReminder;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.business.workflow.WorkflowFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * 
 * Class Appointment Daemon Reminder To send alert reminder
 */
public class AppointmentReminderDaemon extends Daemon
{

    private TaskNotifyReminder _taskReminder = SpringContextService.getBean( "genericalert.taskNotifyReminder" );

    @Override
    public void run( )
    {

        IWorkflowService workflowService = SpringContextService.getBean( fr.paris.lutece.plugins.workflowcore.service.workflow.WorkflowService.BEAN_SERVICE );
        WorkflowFilter workflowFilter = new WorkflowFilter( );

        workflowFilter.setIsEnabled( 1 );

        List<Workflow> listWorkflows = workflowService.getListWorkflowsByFilter( workflowFilter );
        IResourceWorkflowService resourceWorkflowService = SpringContextService.getBean( ResourceWorkflowService.BEAN_SERVICE );

        for ( Workflow workflow : listWorkflows )
        {
            IActionService actionService = SpringContextService.getBean( ActionService.BEAN_SERVICE );
            ActionFilter filter = new ActionFilter( );

            filter.setAutomaticReflexiveAction( true );
            filter.setIdWorkflow( workflow.getId( ) );

            List<Action> listAutomaticActions = actionService.getListActionByFilter( filter );

            for ( Action action : listAutomaticActions )
            {
                ResourceWorkflowFilter filt = new ResourceWorkflowFilter( );
                filt.setIdState( action.getStateBefore( ).getId( ) );
                filt.setIdWorkflow( workflow.getId( ) );
                filt.setResourceType( Appointment.APPOINTMENT_RESOURCE_TYPE );

                List<ResourceWorkflow> listResource = resourceWorkflowService.getListResourceWorkflowByFilter( filt );

                for ( ResourceWorkflow resource : listResource )
                {
                    try
                    {
                        _taskReminder.sendReminder( resource.getIdResource( ), resource.getResourceType( ), action.getId( ), workflow.getId( ) );

                    }
                    catch( Exception e )
                    {

                        AppLogService.error( "notify reminder appointment: " + e.getMessage( ) + ": " + e, e );
                    }

                }
            }
        }
    }

}
