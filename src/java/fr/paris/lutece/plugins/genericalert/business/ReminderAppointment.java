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
package fr.paris.lutece.plugins.genericalert.business;

/**
 * 
 * Class to describe the reminder appointment
 *
 */
public class ReminderAppointment

{
    private int _nIdTask;
    private int _nIdForm;
    private int _nRank;
    private int _nTimeToAlert;
    private boolean _bEmailNotify;
    private boolean _bSmsNotify;
    private String _strEmailAlertMessage;
    private String _strSmsAlertMessage;
    private String _strAlertSubject;
    private String _strEmailCc;
    private String _strNumberPhone;
    private int _nIdStateAfter;

    /**
     * GET id form
     * 
     * @return id form
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * SET th id form
     * 
     * @param nIdForm
     *            the id form
     */
    public void setIdForm( int nIdForm )
    {
        this._nIdForm = nIdForm;
    }

    /**
     * GET the rank
     * 
     * @return the rank
     */
    public int getRank( )
    {
        return _nRank;
    }

    /**
     * SET the rank
     * 
     * @param nRank
     *            the rank
     */
    public void setRank( int nRank )
    {
        this._nRank = nRank;
    }

    /**
     * GET the TimeToAlert
     * 
     * @return TimeToAlert time to alert
     */
    public int getTimeToAlert( )
    {
        return _nTimeToAlert;
    }

    /**
     * SET the time to alert
     * 
     * @param nTimeToAlert
     *            the TimeToAlert
     */
    public void setTimeToAlert( int nTimeToAlert )
    {
        this._nTimeToAlert = nTimeToAlert;
    }

    /**
     * GET the email notify
     * 
     * @return if email notify
     */
    public boolean isEmailNotify( )
    {
        return _bEmailNotify;
    }

    /**
     * SET the email notify
     * 
     * @param bEmailNotify
     *            email notify
     */
    public void setEmailNotify( boolean bEmailNotify )
    {
        this._bEmailNotify = bEmailNotify;
    }

    /**
     * GET sms notify
     * 
     * @return sms notify
     */
    public boolean isSmsNotify( )
    {
        return _bSmsNotify;
    }

    /**
     * SET SmsNotify
     * 
     * @param bSmsNotify
     *            the sms notify
     */
    public void setSmsNotify( boolean bSmsNotify )
    {
        this._bSmsNotify = bSmsNotify;
    }

    /**
     * Get alert email message
     * 
     * @return email alert message
     */
    public String getEmailAlertMessage( )
    {
        return _strEmailAlertMessage;
    }

    /**
     * Sets the email alert message
     * 
     * @param strAlertEmailMessage
     *            email alert message
     */
    public void setEmailAlertMessage( String strAlertEmailMessage )
    {
        this._strEmailAlertMessage = strAlertEmailMessage;
    }

    /**
     * Get alert sms message
     * 
     * @return sms message
     */
    public String getSmsAlertMessage( )
    {
        return _strSmsAlertMessage;
    }

    /**
     * Sets the sms alert message
     * 
     * @param strSmsAlertMessage
     *            alert message
     */
    public void setSmsAlertMessage( String strSmsAlertMessage )
    {
        this._strSmsAlertMessage = strSmsAlertMessage;
    }

    /**
     * Get the subject
     * 
     * @return the subject
     */
    public String getAlertSubject( )
    {
        return _strAlertSubject;
    }

    /**
     * Set the subject
     * 
     * @param strAlertSubject
     *            the subject
     */
    public void setAlertSubject( String strAlertSubject )
    {
        this._strAlertSubject = strAlertSubject;
    }

    /**
     * Get emailCc
     * 
     * @return emailCc
     */
    public String getEmailCc( )
    {
        return _strEmailCc;
    }

    /**
     * Set emailCc
     * 
     * @param strEmailCc
     *            the email cc
     */

    public void setEmailCc( String strEmailCc )
    {
        this._strEmailCc = strEmailCc;
    }

    /**
     * Get the number phone
     * 
     * @return NumberPhone
     */
    public String getNumberPhone( )
    {
        return _strNumberPhone;
    }

    /**
     * Set the numberPhone
     * 
     * @param strNumberPhone
     *            the number phone
     */
    public void setNumberPhone( String strNumberPhone )
    {
        this._strNumberPhone = strNumberPhone;
    }

    /**
     * Get the state after
     * 
     * @return stateAfter the state after
     */
    public int getIdStateAfter( )
    {
        return _nIdStateAfter;
    }

    /**
     * Set the state after
     * 
     * @param nIdStateAfter
     *            id state after
     */
    public void setIdStateAfter( int nIdStateAfter )
    {
        this._nIdStateAfter = nIdStateAfter;
    }

    /**
     * Get id task
     * 
     * @return id task
     */
    public int getIdTask( )
    {
        return _nIdTask;
    }

    /**
     * Set the id task
     * 
     * @param nIdTask
     *            the id task
     */
    public void setIdTask( int nIdTask )
    {
        this._nIdTask = nIdTask;
    }

}
