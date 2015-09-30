package fr.paris.lutece.plugins.genericalert.business;

/**
 * 
 * Class to describe the reminder appointment
 *
 */
public class ReminderAppointment 

{
	private int _nIdTask ;
	private int _nIdForm ;
	private int _nRank;
    private int _nTimeToAlert ;
    private boolean _bEmailNotify ;
    private boolean _bSmsNotify ;
    private String _strAlertMessage ;
    private String _strAlertSubject ;
    private String _strEmailCc;
    private String _strNumberPhone;
    private int _nIdStateAfter ;
    
    /**
     * GET id form 
     * @return id form
     */
    public int getIdForm( ) 
    {
		return _nIdForm;
	}
    /**
     * SET th id form
     * @param nIdForm the id form
     */
	public void setIdForm( int nIdForm ) 
	{
		this._nIdForm = nIdForm;
	}
	/**
	 * GET the rank
	 * @return the rank
	 */
	public int getRank() 
	{
		return _nRank;
	}
	/**
	 * SET the rank
	 * @param nRank the rank
	 */
	public void setRank( int nRank ) 
	{
		this._nRank = nRank;
	}
	/**
     * GET the TimeToAlert
     * @return TimeToAlert time to alert
     */
	public int getTimeToAlert( ) 
	{
		return _nTimeToAlert;
	}
	/**
	 * SET the time to alert
	 * @param nTimeToAlert the TimeToAlert
	 */
	public void setTimeToAlert( int nTimeToAlert ) 
	{
		this._nTimeToAlert = nTimeToAlert;
	}
	/**
	 * GET the email notify
	 * @return if email notify
	 */
	public boolean isEmailNotify( ) 
	{
		return _bEmailNotify;
	}
	/**
	 * SET the email notify
	 * @param bEmailNotify email notify
	 */
	public void setEmailNotify( boolean bEmailNotify ) 
	{
		this._bEmailNotify = bEmailNotify;
	}
	/**
	 * GET sms notify
	 * @return sms notify
	 */
	public boolean isSmsNotify( ) 
	{
		return _bSmsNotify;
	}
	/**
	 * SET SmsNotify
	 * @param bSmsNotify the sms notify
	 */
	public void setSmsNotify( boolean bSmsNotify ) 
	{
		this._bSmsNotify = bSmsNotify;
	}
	
	 /**
     * Get alert message
     * @return alert message
     */
	public String getAlertMessage( ) 
	{
		return _strAlertMessage;
	}
	
	/**
	 * Sets the alert message
	 * @param strAlertMessage alert message
	 */
	public void setAlertMessage( String strAlertMessage ) 
	{
		this._strAlertMessage = strAlertMessage;
	}
	
	/**
	 * Get the subject
	 * @return the subject
	 */
	public String getAlertSubject( ) 
	{
		return _strAlertSubject;
	}
	
	/**
	 * Set the subject 
	 * @param strAlertSubject the subject
	 */
	public void setAlertSubject( String strAlertSubject ) 
	{
		this._strAlertSubject = strAlertSubject;
	}
	
	/**
	 * Get emailCc
	 * @return emailCc
	 */
	public String getEmailCc( ) 
	{
		return _strEmailCc;
	}
	
	/**
	 * Set emailCc
	 * @param strEmailCc the email cc
	 */
	
	public void setEmailCc( String strEmailCc ) 
	{
		this._strEmailCc = strEmailCc;
	}
	
	/**
	 * Get the number phone
	 * @return NumberPhone
	 */
	public String getNumberPhone( ) 
	{
		return _strNumberPhone;
	}
	
	/**
	 * Set the numberPhone
	 * @param strNumberPhone the number phone
	 */
	public void setNumberPhone( String strNumberPhone ) 
	{
		this._strNumberPhone = strNumberPhone;
	}
	
	/**
	 * Get the state after
	 * @return stateAfter the state after
	 */
	public int getIdStateAfter(  ) 
	{
		return _nIdStateAfter;
	}
	
	/**
	 * Set the state after
	 * @param nIdStateAfter id state after
	 */
	public void setIdStateAfter( int nIdStateAfter )  
	{
		this._nIdStateAfter = nIdStateAfter;
	}
	
	/**
	 * Get id task
	 * @return id task
	 */
	public int getIdTask(  ) 
	{
		return _nIdTask;
	}
	/**
	 * Set the id task
	 * @param nIdTask the id task
	 */
	public void setIdTask( int nIdTask ) 
	{
		this._nIdTask = nIdTask;
	}
	
    
}
