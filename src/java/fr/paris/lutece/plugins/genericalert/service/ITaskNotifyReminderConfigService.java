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
     * @return a list of tasks
     */
    @Transactional( NotifyReminderPlugin.BEAN_TRANSACTION_MANAGER )
    List<TaskNotifyReminderConfig> findAll(  );
    /**
     * Select items
     * @param config task management
     * @param strIdEntry idEntry
     * @param idParentEntry idParentEntry
     */
    @Transactional( NotifyReminderPlugin.BEAN_TRANSACTION_MANAGER )
    void selectUpdate( TaskNotifyReminderConfig config, String strIdEntry, int idParentEntry ) ;
    /**
     * Unselect items
     * @param config task management
     * @param strIdEntry idEntry
     * @param idParentEntry idParentEntry
     */
    @Transactional( NotifyReminderPlugin.BEAN_TRANSACTION_MANAGER )
    void unSelectUpdate( TaskNotifyReminderConfig config, String strIdEntry, int idParentEntry );
    /**
     * 
     * @param idTask task
     * @param idDirectory directory
     * @return list Selected items
     */
    @Transactional( NotifyReminderPlugin.BEAN_TRANSACTION_MANAGER )
    Map <Integer,List<Integer>> loadSelectedList( int idTask, int idDirectory );
    
    /**
     * 
     * @param config task management
     */
    @Transactional( NotifyReminderPlugin.BEAN_TRANSACTION_MANAGER )
    void selectedRecords( TaskNotifyReminderConfig config );
    
    /**
     * 
     * @param idTask id task 
     * @param idDirectory id directory
     */
    @Transactional( NotifyReminderPlugin.BEAN_TRANSACTION_MANAGER )
    void loadListEntriesTmp( int idTask, int idDirectory );
}
