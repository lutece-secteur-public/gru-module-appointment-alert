--
-- Drop table  workflow_task_notify_reminder_cf if exists
--
DROP TABLE IF EXISTS workflow_task_notify_reminder_cf;

--
-- Drop table  workflow_appointment_reminder if exists
--
DROP TABLE IF EXISTS workflow_appointment_reminder;

--
-- Table structure for table workflow_task_notify_reminder_cf
--
CREATE TABLE  workflow_task_notify_reminder_cf
(
	id_task INT NOT NULL,
	id_form int DEFAULT 0,
	nb_alerts INT DEFAULT 0,
  	PRIMARY KEY  (id_task,id_form )
);

--
-- Table structure for table workflow_appointment_reminder
--
CREATE TABLE workflow_appointment_reminder
(
	id_task int NOT NULL,
	id_form int NOT NULL,
	rank int NOT NULL,
	time_to_alert INT NOT NULL,     
    email_notify SMALLINT NOT NULL, 
	sms_notify SMALLINT NOT NULL,
	email_alert_message long varchar,
	sms_alert_message long varchar,
	alert_subject VARCHAR ( 255 ) NOT NULL,
	email_cc VARCHAR ( 255 ) DEFAULT NULL,
	phone_number VARCHAR ( 255 ) DEFAULT NULL,
	id_state_after int NOT NULL,
	PRIMARY KEY (id_task,id_form,rank)
);




