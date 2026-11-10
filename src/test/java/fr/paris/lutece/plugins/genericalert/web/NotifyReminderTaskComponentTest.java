/*
 * Copyright (c) 2002-2021, City of Paris
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
package fr.paris.lutece.plugins.genericalert.web;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.genericalert.business.TaskNotifyReminderConfig;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Configuration screen of the reminder task
 */
public class NotifyReminderTaskComponentTest extends LuteceTestCase
{
    private static final int ID_TASK = 999999;
    private static final int ID_FORM_WITHOUT_CONFIGURATION = 999998;

    /**
     * A form chosen in the list that has no configuration yet stays the form of the screen: the list keeps it selected and the
     * reminders are saved on it
     */
    @Test
    public void testFormWithoutConfigurationStaysTheFormOfTheScreen( )
    {
        TaskNotifyReminderConfig config = NotifyReminderTaskComponent.getConfigToDisplay( ID_TASK,
                String.valueOf( ID_FORM_WITHOUT_CONFIGURATION ) );

        assertEquals( ID_FORM_WITHOUT_CONFIGURATION, config.getIdForm( ) );
        assertEquals( ID_TASK, config.getIdTask( ) );
        assertTrue( config.getListReminderAppointment( ).isEmpty( ) );
    }

    /**
     * No form chosen and no configuration: a new configuration without form
     */
    @Test
    public void testNoFormAndNoConfiguration( )
    {
        TaskNotifyReminderConfig config = NotifyReminderTaskComponent.getConfigToDisplay( ID_TASK, null );

        assertEquals( 0, config.getIdForm( ) );
    }
}
