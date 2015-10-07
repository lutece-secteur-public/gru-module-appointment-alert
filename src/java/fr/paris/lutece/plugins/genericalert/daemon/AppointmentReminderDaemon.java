package fr.paris.lutece.plugins.genericalert.daemon;

import java.util.List;
import java.util.Locale;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
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
	* Class Appointment Daemon Reminder
	* To send alert reminder 
	*/
	public class AppointmentReminderDaemon extends Daemon 
	
	{
	
		@Override
		public void run( ) 
		{
				AppLogService.info( "START DEBUG : \n" );
            
			  	IWorkflowService workflowService = SpringContextService.getBean( fr.paris.lutece.plugins.workflowcore.service.workflow.WorkflowService.BEAN_SERVICE );
		        WorkflowFilter workflowFilter = new WorkflowFilter(  );
		        
		        workflowFilter.setIsEnabled( 1 );
		        
		        List<Workflow> listWorkflows = workflowService.getListWorkflowsByFilter( workflowFilter );
		        IResourceWorkflowService resourceWorkflowService = SpringContextService.getBean( ResourceWorkflowService.BEAN_SERVICE );

		        for ( Workflow workflow : listWorkflows )
		        {
		            IActionService actionService = SpringContextService.getBean( ActionService.BEAN_SERVICE );
		            ActionFilter filter = new ActionFilter(  );

		            filter.setAutomaticReflexiveAction( true );
		            filter.setIdWorkflow( workflow.getId(  ) );

		            List<Action> listAutomaticActions = actionService.getListActionByFilter( filter );

		            for ( Action action : listAutomaticActions )
		            {
		                List<ResourceWorkflow> listResource = resourceWorkflowService.getAllResourceWorkflowByState( action.getStateBefore(  )
		                                                                                                                   .getId(  ) );
             
		                for ( ResourceWorkflow resource : listResource )
		                {
		                    workflowService.doProcessAction( resource.getIdResource(  ), resource.getResourceType(  ),
		                        action.getId(  ), resource.getExternalParentId(  ), null, Locale.getDefault(  ), true, null );
		                    
		                    break;
		                }
		            }
		        }
		    }
		}